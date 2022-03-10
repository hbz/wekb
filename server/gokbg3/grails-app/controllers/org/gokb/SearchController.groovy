package org.gokb

import grails.converters.*
import org.springframework.security.access.annotation.Secured;

import org.gokb.cred.*
import wekb.SearchService

@Secured(['IS_AUTHENTICATED_FULLY'])
class SearchController {

  def genericOIDService
  def springSecurityService
  SearchService searchService


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    User user = springSecurityService.currentUser
    def start_time = System.currentTimeMillis();

    log.debug("Entering SearchController:index ${params}");

    def searchResult = [:]

    searchResult = searchService.search(user, searchResult, params, response.format)

    // log.debug("leaving SearchController::index...");
    log.debug("Search completed after ${System.currentTimeMillis() - start_time}");

    withFormat {
      html searchResult.result
      json { render searchResult.apiresponse as JSON }
      xml { render searchResult.apiresponse as XML }
    }
  }



}
