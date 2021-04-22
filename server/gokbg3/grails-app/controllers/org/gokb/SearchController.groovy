package org.gokb

import grails.converters.*
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.access.annotation.Secured;
import com.k_int.apis.SecurityApi
import org.springframework.security.acls.model.Permission

import grails.util.GrailsClassUtils
import org.gokb.cred.*
import wekb.GlobalSearchTemplatesService
import wekb.SearchService

class SearchController {

  def genericOIDService
  def springSecurityService
  def classExaminationService
  def gokbAclService
  def displayTemplateService
  GlobalSearchTemplatesService globalSearchTemplatesService
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
