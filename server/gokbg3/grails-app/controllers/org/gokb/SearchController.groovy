package org.gokb

import org.springframework.security.access.annotation.Secured;

import org.gokb.cred.*
import wekb.SearchService

class SearchController {

    def genericOIDService
    def springSecurityService
    SearchService searchService

    def index() {
        User user = springSecurityService.currentUser
        def start_time = System.currentTimeMillis();

        log.debug("Entering SearchController:index ${params}")

        def searchResult = [:]
        List allowedSearch = ["g:tipps", "g:platforms", "g:packages", "g:orgs", "g:tippsOfPkg", "g:sources", "g:curatoryGroups", "g:identifiers"]

        if ((params.qbe in allowedSearch) || (sec.ifLoggedIn() && sec.ifAnyGranted("ROLE_ADMIN"))) {
            searchResult = searchService.search(user, searchResult, params, response.format)

            log.debug("Search completed after ${System.currentTimeMillis() - start_time}");

        } else {
            searchResult.result = [:]
           flash.error = "This search is not allowed!"
        }
        searchResult.result
    }

}
