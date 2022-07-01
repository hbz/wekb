package org.gokb

import grails.converters.JSON
import grails.converters.XML
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.aggregations.AggregationBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.FieldSortBuilder
import org.elasticsearch.search.sort.SortOrder
import org.springframework.security.access.annotation.Secured;

import org.gokb.cred.*
import wekb.SearchService

class SearchController {

    def genericOIDService
    SearchService searchService

    static def reversemap = ['subject':'subjectKw','componentType':'componentType','status':'status','name':'name']
    static def non_analyzed_fields = ['componentType','status']

    def ESWrapperService
    SpringSecurityService springSecurityService

    def index() {
        log.debug("SearchController::index ${params}")
        def result = [:]
        def apiresponse = null

        def esclient = ESWrapperService.getClient()

        Map searchParams = [:]
        try {

            if ( params.q && params.q.length() > 0) {

                searchParams.name = params.q.replace('[',"(")
                searchParams.name = searchParams.name.replace(']',")")
                searchParams.name = searchParams.name.replace(':',"")
                if(params.allProperties){
                    searchParams.q = searchParams.name
                    searchParams.remove('name')
                }

                User user = springSecurityService.getCurrentUser()
                result.max = params.max ? Integer.parseInt(params.max) : (user ? user.defaultPageSizeAsInteger : 50)
                result.offset = params.offset ? Integer.parseInt(params.offset) : 0

                def query_str = buildQuery(searchParams);

                log.debug("Searching for ${query_str}");

                def typing_field = grailsApplication.config.globalSearch.typingField ?: 'componentType'

                //QueryBuilder esQuery = QueryBuilders.queryStringQuery(query_str)

                log.debug("Using indices ${grailsApplication.config.globalSearch.indices.join(", ")}")

                SearchResponse searchResponse
                SearchRequest searchRequest = new SearchRequest()
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()

                params.sort = params.sort ?: "sortname"

                if (params.sort) {
                    SortOrder order = SortOrder.ASC
                    if (params.order) {
                        order = SortOrder.valueOf(params.order?.toUpperCase())
                    }
                    searchSourceBuilder.sort(new FieldSortBuilder("${params.sort}").order(order))
                }

                searchSourceBuilder.query(QueryBuilders.queryStringQuery(query_str))
                searchSourceBuilder.aggregation(AggregationBuilders.terms('ComponentType').size(25).field(typing_field))

                searchSourceBuilder.from(result.offset)
                searchSourceBuilder.size(result.max)
                searchRequest.source(searchSourceBuilder)


                searchResponse = esclient.search(searchRequest, RequestOptions.DEFAULT)

                result.hits = searchResponse.getHits()

                /*if(searchResponse.getHits().maxScore == Float.NaN) { //we cannot parse NaN to json so set to zero...
                  searchResponse.hits.maxScore = 0;
                }*/

                result.resultsTotal = searchResponse.getHits().getTotalHits().value ?: 0
                // We pre-process the facet response to work around some translation issues in ES

                if (searchResponse.getAggregations()) {
                    result.facets = [:]
                    searchResponse.getAggregations().each { entry ->
                        def facet_values = []
                        //log.debug("Entry: ${entry.type}")

                        if(entry.type == 'nested'){
                            entry.getAggregations().each { subEntry ->
                                //log.debug("metaData: ${subEntry.name}")
                                subEntry.buckets.each { bucket ->
                                    //log.debug("Bucket: ${bucket}")
                                    bucket.each { bi ->
                                        def displayTerm = bi.getKey()
                                        if (bi.getKey() == 'TitleInstancePackagePlatform') {
                                            displayTerm = 'Titles'
                                        }
                                        if (bi.getKey() == 'Org') {
                                            displayTerm = 'Provider'
                                        }
                                        log.debug("Bucket item: ${bi} ${bi.getKey()} ${bi.getDocCount()}");
                                        facet_values.add([term:bi.getKey(),display:displayTerm,count:bi.getDocCount()])
                                    }
                                }
                            }
                        }else {
                            entry.buckets.each { bucket ->
                                //log.debug("Bucket: ${bucket}")
                                bucket.each { bi ->
                                    def displayTerm = bi.getKey()
                                    if (bi.getKey() == 'TitleInstancePackagePlatform') {
                                        displayTerm = 'Titles'
                                    }
                                    if (bi.getKey() == 'Org') {
                                        displayTerm = 'Provider'
                                    }
                                    log.debug("Bucket item: ${bi} ${bi.getKey()} ${bi.getDocCount()}");
                                    facet_values.add([term:bi.getKey(),display:displayTerm,count:bi.getDocCount()])
                                }
                            }
                        }
                        result.facets[entry.getName()] = facet_values

                    }
                }
                /*if ( ( response.format == 'json' ) || ( response.format == 'xml' ) ) {
                    apiresponse = [:]
                    apiresponse.count = result.resultsTotal
                    apiresponse.max = result.max
                    apiresponse.offset = result.offset
                    apiresponse.records = []
                    result.hits.each { r ->
                        def response_record = [:]
                        response_record.id = r.id
                        response_record.score = r.score
                        response_record.name = r.getSourceAsMap().name
                        response_record.identifiers = r.getSourceAsMap().identifiers
                        response_record.altNames = r.getSourceAsMap().altname

                        apiresponse.records.add(response_record);
                    }
                }*/
            }
        }
        finally {
            try {
                esclient.close()
            }
            catch (Exception e) {
                log.error("Problem by Close ES Client", e)
            }
        }

        withFormat {
            html result
            /*json { render apiresponse as JSON }
            xml { render apiresponse as XML }*/
        }
    }

    def spotlightSearch() {
        log.debug("SearchController::spotlightSearch ${params}")
        Map result = [:]
        result.offset = 0
        result.max = 10000

        RestHighLevelClient esclient = ESWrapperService.getClient()
        Map searchParams = [:]
        try {

            if ( params.q && params.q.length() > 0) {

                searchParams.name = params.q.replace('[',"(")
                searchParams.name = searchParams.name.replace(']',")")
                searchParams.name = searchParams.name.replace(':',"")

                //User user = springSecurityService.getCurrentUser()
                //result.max = params.max ? Integer.parseInt(params.max) : (user ? user.defaultPageSizeAsInteger : 50)
                //result.offset = params.offset ? Integer.parseInt(params.offset) : 0

                def query_str = buildQuery(searchParams)

                log.debug("Searching for ${query_str}")

                //def typing_field = grailsApplication.config.globalSearch.typingField ?: 'componentType'

                log.debug("Using indices ${grailsApplication.config.globalSearch.indices.join(", ")}")

                SearchResponse searchResponse
                SearchRequest searchRequest = new SearchRequest()
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()

                params.sort = params.sort ?: "sortname"

                if (params.sort) {
                    SortOrder order = SortOrder.ASC
                    if (params.order) {
                        order = SortOrder.valueOf(params.order?.toUpperCase())
                    }
                    searchSourceBuilder.sort(new FieldSortBuilder("${params.sort}").order(order))
                }

                searchSourceBuilder.query(QueryBuilders.queryStringQuery(query_str))
                //searchSourceBuilder.aggregation(AggregationBuilders.terms('ComponentType').size(25).field(typing_field))

                searchSourceBuilder.from(result.offset)
                searchSourceBuilder.size(result.max)
                searchRequest.source(searchSourceBuilder)

                searchResponse = esclient.search(searchRequest, RequestOptions.DEFAULT)
                result.hits = searchResponse.getHits()

                result.resultsTotal = searchResponse.getHits().getTotalHits().value ?: 0
                result.query = searchParams.name
            }
        }
        finally {
            try {
                esclient.close()
            }
            catch (Exception e) {
                log.error("Problem by Close ES Client", e)
            }
        }
        result
    }

    private def buildQuery(Map params) {

        StringWriter sw = new StringWriter()

        if ( ( params != null ) && ( params.q != null ) )
            if(params.q.equals("*")){
                sw.write(params.q)
            }
            else{
                sw.write("(${params.q})")
            }
        else
            sw.write("*:*")

        // For each reverse mapping
        reversemap.each { mapping ->

            // log.debug("testing ${mapping.key}");

            // If the query string supplies a value for that mapped parameter
            if ( params[mapping.key] != null ) {

                // If we have a list of values, rather than a scalar
                if ( params[mapping.key].class == java.util.ArrayList) {
                    params[mapping.key].each { p ->
                        sw.write(" AND ")
                        sw.write(mapping.value)
                        sw.write(":")

                        if(non_analyzed_fields.contains(mapping.value)) {
                            sw.write("${p}")
                        }
                        else {
                            sw.write("\"${p}\"")
                        }
                    }
                }
                else {
                    // We are dealing with a single value, this is "a good thing" (TM)
                    // Only add the param if it's length is > 0 or we end up with really ugly URLs
                    // II : Changed to only do this if the value is NOT an *
                    if ( params[mapping.key].length() > 0 && ! ( params[mapping.key].equalsIgnoreCase('*') ) ) {
                        sw.write(" AND ")
                        // Write out the mapped field name, not the name from the source
                        sw.write(mapping.value)
                        sw.write(":")

                        if(non_analyzed_fields.contains(mapping.value)) {
                            sw.write("${params[mapping.key]}")
                        }
                        else {
                            sw.write("\"${params[mapping.key]}\"")
                        }
                    }
                    else if (mapping.key == 'status') {
                        sw.write(" AND ")
                        sw.write("status:Current")
                    }
                }
            }
            else if (mapping.key == 'status') {
                sw.write(" AND (NOT status:Removed) ")
            }
        }

        def result = sw.toString();
        result;
    }


    def componentSearch() {
        User user = springSecurityService.currentUser
        def start_time = System.currentTimeMillis();

        log.debug("SearchController:componentSearch ${params}")

        def searchResult = [:]
        List allowedSearch = ["g:tipps", "g:platforms", "g:packages", "g:orgs", "g:tippsOfPkg", "g:sources", "g:curatoryGroups", "g:identifiers"]

        if ((params.qbe in allowedSearch) || (springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN"))) {
            searchResult = searchService.search(user, searchResult, params, response.format)

            log.debug("Search completed after ${System.currentTimeMillis() - start_time}");

        } else {
            searchResult.result = [:]
           flash.error = "This search is not allowed!"
        }
        searchResult.result
    }

    def inlineSearch() {
        User user = springSecurityService.currentUser
        def start_time = System.currentTimeMillis();

        log.debug("inlineSearch:componentSearch ${params}")

        def searchResult = [:]
        List allowedSearch = ["g:tipps", "g:platforms", "g:packages", "g:orgs", "g:tippsOfPkg", "g:sources", "g:curatoryGroups", "g:identifiers"]

        if ((params.qbe in allowedSearch) || (springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN"))) {
            searchResult = searchService.search(user, searchResult, params, response.format)

            log.debug("Search completed after ${System.currentTimeMillis() - start_time}");

        } else {
            searchResult.result = [:]
            flash.error = "This search is not allowed!"
        }
        searchResult.result
    }

}
