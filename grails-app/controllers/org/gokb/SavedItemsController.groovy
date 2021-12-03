package org.gokb

import grails.converters.*
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.access.annotation.Secured;

import org.gokb.cred.*

@Secured(['IS_AUTHENTICATED_FULLY'])
class SavedItemsController {

  def genericOIDService
  def springSecurityService

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index() { 
    def result = [:]
    User user = springSecurityService.currentUser
    result.saved_items = SavedSearch.executeQuery('Select ss from SavedSearch as ss where ss.owner = :user order by name',[user: user])
    result
  }
}
