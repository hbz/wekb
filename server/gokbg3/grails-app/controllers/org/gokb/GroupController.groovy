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
        result = getResultGenerics()
        return result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myProviders() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        params.qbe = 'g:orgs'
        params.qp_curgroups = searchResult.user.curatoryGroups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

        searchResult.result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPackages() {
        def searchResult = [:]

        searchResult = getResultGenerics()

        params.qbe = 'g:packages'
        params.qp_curgroups = searchResult.user.curatoryGroups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

        searchResult.result

    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myPlatforms() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        params.qbe = 'g:platforms'
        params.qp_curgroups = searchResult.user.curatoryGroups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

        searchResult.result

    }

/*    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myReviewRequests() {
        def searchResult = [:]
            searchResult = getResultGenerics()

            params.qbe = 'g:allocatedReviewGroups'
            params.qp_curgroups = searchResult.user.curatoryGroups.id
            params.hide = ['qp_curgroup', 'qp_curgroups']

            searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

            searchResult.result
    }*/


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def mySources() {
        def searchResult = [:]

        searchResult = getResultGenerics()

        params.qbe = 'g:sources'
        params.qp_curgroups = searchResult.user.curatoryGroups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

        searchResult.result
    }


    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def myTitles() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        params.qbe = 'g:tipps'
        params.qp_curgroups = searchResult.user.curatoryGroups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']

        searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

        searchResult.result
    }


    private Map getResultGenerics() {
        Map result = [:]
        result.user = springSecurityService.currentUser

        boolean cur = result.user.curatoryGroups?.size() > 0
        if (!cur) {
            log.debug("No curator!")
            response.sendError(403)
            return
        }

        //result.s_action = actionName
        //result.s_controller = controllerName

        result.groups = result.user.curatoryGroups

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def exportMyPackages() {
        def searchResult = [:]
        searchResult = getResultGenerics()

        params.qbe = 'g:packages'
        params.qp_curgroups = searchResult.user.curatoryGroups.id
        params.hide = ['qp_curgroup', 'qp_curgroups']
        params.max = '10000'

        searchResult = searchService.search(searchResult.user, searchResult, params, response.format)

        searchResult.result

        String export_date = dateFormatService.formatDate(new Date());

        String filename = "wekb_my_packages_${export_date}.tsv"

        try {
            response.setContentType('text/tab-separated-values');
            response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

            def out = response.outputStream

            exportService.exportPackages(out, searchResult.result.recset)

        }
        catch (Exception e) {
            log.error("Problem with export", e);
        }
    }

}
