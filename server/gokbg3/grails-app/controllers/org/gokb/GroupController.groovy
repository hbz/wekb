package org.gokb

import de.wekb.helper.RCConstants
import gokbg3.DateFormatService
import org.springframework.security.access.annotation.Secured;

import org.gokb.cred.*
import wekb.ExportService
import wekb.SearchService

@Secured(['IS_AUTHENTICATED_FULLY'])
class GroupController {

    def springSecurityService
    SearchService searchService
    DateFormatService dateFormatService
    ExportService exportService

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def index() {
        def result = [:]
        if (params.id) {
            User user = springSecurityService.currentUser

            log.debug("Entering GroupController:index ${params}");

            result.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSizeAsInteger
            result.rr_offset = params.rr_offset ? Integer.parseInt(params.rr_offset) : 0;

            if (params.rr_jumpToPage) {
                result.rr_offset = ((Integer.parseInt(params.rr_jumpToPage) - 1) * result.max)
            }
            params.rr_offset = result.rr_offset
            params.remove('rr_jumpToPage')

            result.group = CuratoryGroup.get(params.id);

            def rr_sort = params.rr_sort ?: 'dateCreated'
            def rr_sort_order = params.rr_sort_order ?: 'asc'

            def closedStat = RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Closed')
            def delStat = RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Deleted')
            def cg_components = KBComponent.executeQuery("select c.id from KBComponent as c where exists ( select oc from c.outgoingCombos as oc where oc.toComponent.id = :group )", [group: result.group.id])

            log.debug("Got ${cg_components.size()} connected components")

            def cg_review_tasks_hql = ''' from ReviewRequest as rr where ((
        rr.componentToReview.id in (:cgcomponents)
      ) or exists (select arc from AllocatedReviewGroup as arc where arc.review = rr and arc.group = :group))
      and rr.status != :closed and rr.status != :deleted
      '''

            result.rr_count = Package.executeQuery('select count(rr) ' + cg_review_tasks_hql, [group: result.group, cgcomponents: cg_components, closed: closedStat, deleted: delStat])[0];
            result.rrs = Package.executeQuery('select rr ' + cg_review_tasks_hql + " order by ${rr_sort} ${rr_sort_order}", [group: result.group, cgcomponents: cg_components, closed: closedStat, deleted: delStat], [max: result.max, offset: result.rr_offset]);


            result.rr_page_max = (result.rr_count / result.max).toInteger() + (result.rr_count % result.max > 0 ? 1 : 0)
            result.rr_page = (result.rr_offset / result.max) + 1


            result.withoutJump = params.clone()
            result.remove('rr_jumpToPage');
            result.withoutJump.remove('rr_jumpToPage');
        }
        return result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myOrgs() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:orgs'
            params.hide = ['qp_curgroup']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
        }else {
            response.sendError(404)
            return
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPackages() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:packages'
            params.hide = ['qp_curgroup']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
        }else {
            response.sendError(404)
            return
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPlatforms() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:platforms'
            params.hide = ['qp_curgroup']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
        }else {
            response.sendError(404)
            return
        }
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myReviewRequests() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:allocatedReviewGroups'
            params.hide = ['qp_curgroup']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
        }else {
            response.sendError(404)
            return
        }
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def mySources() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:sources'
            params.hide = ['qp_curgroup']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
        }else {
            response.sendError(404)
            return
        }
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myTitles() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:tipps'
            params.hide = ['qp_curgroup']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
        }else {
            response.sendError(404)
            return
        }
    }


    private Map getResultGenerics(){
        Map result = [:]
        result.user = springSecurityService.currentUser

        result.group = CuratoryGroup.get(params.id)

        boolean cur = (result.group.id in result.user.curatoryGroups?.id)
        if (!cur) {
            log.debug("No curator!")
            response.sendError(403)
            return
        }

        //result.s_action = actionName
        //result.s_controller = controllerName

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def exportMyPackages() {
        def searchResult = [:]
        if (params.id) {
            searchResult = getResultGenerics()

            params.qp_curgroup = searchResult.group.class.name+':'+searchResult.group.id
            params.qbe = 'g:packages'
            params.hide = ['qp_curgroup']
            params.max = '10000'

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result

            println(searchResult.result)

            String export_date = dateFormatService.formatDate(new Date());

            String filename = "wekb_my_packages_${export_date}.tsv"

            try {
                response.setContentType('text/tab-separated-values');
                response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

                def out = response.outputStream

                exportService.exportPackages(out, searchResult.result.recset)

            }
            catch ( Exception e ) {
                log.error("Problem with export",e);
            }

        }else {
            response.sendError(404)
            return
        }
    }

}
