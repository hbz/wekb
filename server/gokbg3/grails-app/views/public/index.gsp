<!DOCTYPE html>
<%
    def addFacet = { params, facet, val ->
        def newparams = [:]
        newparams.putAll(params)

        newparams.remove('offset');
        newparams.remove('max');

        def current = newparams[facet]
        if (current == null) {
            newparams[facet] = val
        } else if (current instanceof String[]) {
            newparams.remove(current)
            newparams[facet] = current as List
            newparams[facet].add(val);
        } else {
            newparams[facet] = [current, val]
        }
        newparams
    }

    def removeFacet = { params, facet, val ->
        def newparams = [:]
        newparams.putAll(params)
        def current = newparams[facet]

        newparams.remove('offset');
        newparams.remove('max');

        if (current == null) {
        } else if (current instanceof String[]) {
            newparams.remove(current)
            newparams[facet] = current as List
            newparams[facet].remove(val);
        } else if (current?.equals(val.toString())) {
            newparams.remove(facet);
        }
        newparams
    }
%>
<html>
<head>
    <meta name='layout' content='public'/>
    <title>GOKb: Packages</title>
</head>

<body>

<div class="container">
    <div class="row">
        <div class="col-md-12">
                <div class="well form-horizontal">
                    <g:each in="${componentsOfStatistic}" var="component">
                            ${component}: ${countComponent."${component}"}
                    </g:each>
                </div>
        </div>
    </div>
</div>

<div class="container">
    <div class="row">
        <div class="col-md-12">
            <g:form controller="public" class="form" role="form" action="index" method="get" params="${params}">
                <div class="well form-horizontal">

                    <label for="q">Search for packages...</label>

                    <div class="input">
                        <input type="text" class="form-control" placeholder="Find package like..." value="${params.q}"
                               name="q">
                    </div>
                    Showing results ${firstrec} to ${lastrec} of ${resultsTotal}

                    <br>
                    <br>

                    <g:each in="${facets?.sort { it.key }}" var="facet">
                        <g:if test="${facet.key != 'type'}">

                            <label for="${facet.key}" class="form-label"><g:message code="facet.so.${facet.key}"
                                                                                    default="${facet.key}"/></label>

                            <select name="${facet.key}" class="form-select wekb-multiselect" multiple aria-label="Default select example">
                                <option value="">Select <g:message code="facet.so.${facet.key}"
                                                          default="${facet.key}"/></option>
                                <g:each in="${facet.value?.sort { it.display }}" var="v">
                                    <g:set var="fname" value="facet:${facet.key + ':' + v.term}"/>
                                    <g:set var="kbc"
                                           value="${v.term.startsWith('org.gokb.cred') ? org.gokb.cred.KBComponent.get(v.term.split(':')[1].toLong()) : null}"/>
                                    <g:if test="${params.list(facet.key).contains(v.term.toString())}">
                                        <option value="${v.term}"
                                                selected="selected">${kbc?.name ?: v.display} (${v.count})</option>
                                    </g:if>
                                    <g:else>
                                        <option value="${v.term}">${kbc?.name ?: v.display} (${v.count})</option>
                                    </g:else>
                                </g:each>
                            </select>

                        </g:if>
                    </g:each>


                    <br>
                    <br>

                    <button class="btn btn-primary" type="submit" value="yes" name="search"><span
                            class="fa fa-search" aria-hidden="true">Search</span></button>
                </div>
            </g:form>
        </div>
    </div>
</div>


<div class="container">
    <div class="row">
        <div class="col-md-12">
            <table class="table table-striped well">
                <thead>
                <tr>
                    <th>Package name</th>
                    <th>Provider</th>
                    <th>Curatory Groups</th>
                    <th>Content Type</th>
                    <th>Title count</th>
                    <th>Last updated</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${hits}" var="hit">
                    <tr>
                        <td>
                            <g:link controller="public" action="packageContent"
                                    id="${hit.id}">${hit.source.name}</g:link>
                        <!-- <g:link controller="public" action="kbart" id="${hit.id}">(Download Kbart File)</g:link>-->

                        </td>
                        <td>${hit.source.cpname}</td>
                        <td>
                            <g:if test="${hit.source.curatoryGroups?.size() > 0}">
                                    <g:each in="${hit.source.curatoryGroups}" var="cg" status="i">
                                        <g:if test="${i > 0}"><br></g:if>
                                        ${cg}
                                    </g:each>
                            </g:if>
                            <g:else>
                                <div>No Curators</div>
                            </g:else>
                        </td>
                        <td>${hit.source.contentType}</td>
                        <td>${hit.source.titleCount}<g:if test="${hit.source.listStatus != 'Checked'}">*</g:if></td>
                        <td>${hit.source.lastUpdatedDisplay}</td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div style="font-size:0.8em;">
                <b>*</b> The editing status of this package is marked as 'In Progress'. The number of titles in this package should therefore not be taken as final.
            </div>

            <g:if test="${resultsTotal ?: 0 > 0}">
                <div class="pagination">
                    <g:paginate controller="public" action="index" params="${params}" next="&raquo;" prev="&laquo;"
                                max="${max}" total="${resultsTotal}"/>
                </div>
            </g:if>

        </div>
    </div>

</div>

</div> <!-- /.container -->
<g:javascript>
    // When DOM is ready.
    $(document).ready(function(){

        var form_selects = $(".wekb-multiselect");

        form_selects.each(function() {

            var conf = {
                allowClear: true,
                width:'resolve',
                minimumInputLength: 0,
/*                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                    url: gokb.config.lookupURI,
                    dataType: 'json',
                    data: function (term, page) {
                        return {
                            format:'json',
                            q: term,
                            baseClass:$(this).data('domain'),
                            filter1:$(this).data('filter1'),
                            addEmpty:'Y'
                        };
                    },
                    results: function (data, page) {
                        // console.log("resultsFn");
                        return {results: data.values};
                    }
                }*/
            };

            var me = $(this);


            me.select2(conf);
        });

    });
</g:javascript>
</body>
</html>
