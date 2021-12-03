package org.gokb.rest

import de.wekb.helper.RCConstants
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import org.gokb.ReviewRequestService
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.gokb.cred.ReviewRequest
import org.gokb.cred.User

import java.time.Duration
import java.time.LocalDateTime

@Transactional(readOnly = true)
class ReviewsController {

  static namespace = 'rest'

  def genericOIDService
  def springSecurityService
  def ESSearchService
  def messageService
  def restMappingService
  def componentLookupService
  ReviewRequestService reviewRequestService

  @Secured(['ROLE_CONTRIBUTOR', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    def result = []
    def base = grailsApplication.config.serverUrl + "/rest"
    User user = User.get(springSecurityService.principal.id)
    result = componentLookupService.restLookup(user, ReviewRequest, params)

    render result as JSON
  }

  @Secured(['ROLE_CONTRIBUTOR', 'IS_AUTHENTICATED_FULLY'])
  def show() {
    def result = [:]
    def obj = ReviewRequest.get(genericOIDService.oidToId(params.id))
    def base = grailsApplication.config.serverUrl + "/rest"
    def includes = params['_include'] ? params['_include'].split(',') : []
    def embeds = params['_embed'] ? params['_embed'].split(',') : []
    User user = User.get(springSecurityService.principal.id)

    if (obj?.isReadable()) {
      result = restMappingService.mapObjectToJson(obj, params, user)
      result._links = generateLinks(obj, user)
      result.additionalInfo = obj.additional
    }
    else if (!obj) {
      result.message = "Object ID could not be resolved!"
      response.setStatus(404)
      result.code = 404
      result.result = 'ERROR'
    }
    else {
      result.message = "Access to object was denied!"
      response.setStatus(403)
      result.code = 403
      result.result = 'ERROR'
    }

    render result as JSON
  }

  @Secured(value=["hasRole('ROLE_EDITOR')", 'IS_AUTHENTICATED_FULLY'])
  @Transactional
  def update() {
    def result = ['result':'OK', 'params': params]
    def reqBody = request.JSON
    def errors = [:]
    def immutable = ['componentToReview', 'dateCreated', 'lastUpdated', 'id']
    def user = User.get(springSecurityService.principal.id)
    def obj = ReviewRequest.get(genericOIDService.oidToId(params.id))

    if (obj && reqBody) {
      obj.lock()

      def curator = isUserCurator(obj, user)

      if (curator || user.isAdmin()) {

        if (reqBody.status) {
          def new_status = null

          if (reqBody.status instanceof Integer) {
            def rdc = RefdataCategory.findByDesc(RCConstants.REVIEW_REQUEST_STATUS)
            def rdv = RefdataValue.get(reqBody.status)

            if (rdv?.owner == rdc) {
              new_status = rdv
            }
          } else {
            new_status = RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, reqBody.status)
          }

          if (new_status) {
            obj.status = new_status
          }
          else {
            errors.status = [[message: "Illegal status value provided.", code: 404, baddata:reqBody.status]]
          }
        }

        if (reqBody.stdDesc) {
          def rdv_desc = null

          if (reqBody.stdDesc instanceof Integer) {
            def rdc = RefdataCategory.findByDesc(RCConstants.REVIEW_REQUEST_STD_DESC)
            def rdv = RefdataValue.get(reqBody.stdDesc)

            if (rdv?.owner == rdc) {
              rdv_desc = rdv
            }
          } else {
            rdv_desc = RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STD_DESC, reqBody.stdDesc)
          }

          if (rdv_desc) {
            obj.status = rdv_desc
          }
          else {
            errors.status = [[message: "Illegal standard description provided.", code: 404, baddata:reqBody.stdDesc]]
          }
        }

        if (reqBody.additionalInfo) {
          obj.additionalInfo = JsonOutput.toJson(reqBody.additionalInfo)
        }

        if (reqBody.needsNotify) {
          Boolean nn = params.boolean(reqBody.needsNotify)

          if (nn) {
            obj.needsNotify = nn
          }
          else {
            errors.needsNotify = [[message:"Expected boolean value for needsNotify!", baddata: reqBody.needsNotify]]
          }
        }

        if (reqBody.reviewRequest?.trim()) {
          obj.reviewRequest = reqBody.reviewRequest.trim()
        }

        if (reqBody.descriptionOfCause?.trim()) {
          obj.descriptionOfCause = reqBody.descriptionOfCause.trim()
        }

        if (reqBody.componentToReview && reqBody.componentToReview != obj.componentToReview.id) {
          errors.componentToReview = [[message: "Changing the connected component of an existing review is not allowed!", baddata: reqBody.componentToReview]]
        }

        if( obj.validate() ) {
          if(errors.size() == 0) {
            log.debug("No errors.. saving")
            obj = obj.merge(flush:true)
            result = restMappingService.mapObjectToJson(obj, params, user)

            result._links = generateLinks(obj, user)
          }
          else {
            response.setStatus(400)
            result.message = message(code:"default.update.errors.message")
          }
        }
        else {
          result.result = 'ERROR'
          response.setStatus(400)
          errors << messageService.processValidationErrors(obj.errors, request.locale)
        }
      }
      else {
        result.result = 'ERROR'
        response.setStatus(403)
        result.message = "User must belong to at least one curatory group of an existing package to make changes!"
      }
    }
    else {
      result.result = 'ERROR'
      response.setStatus(404)
      result.message = "Package not found or empty request body!"
    }

    if(errors.size() > 0) {
      result.error = errors
    }
    render result as JSON
  }

  @Secured(value=["hasRole('ROLE_EDITOR')", 'IS_AUTHENTICATED_FULLY'])
  @Transactional
  def save() {
    def result = ['result':'OK', 'params': params]
    def reqBody = request.JSON
    def errors = [:]
    def user = User.get(springSecurityService.principal.id)

    if ( ReviewRequest.isTypeCreatable() ) {
      def obj = null
      def pars = [
        componentToReview: null,
        reviewRequest: null,
        descriptionOfCause: null,
        additionalInfo: null,
        stdDesc: null
      ]

      if (reqBody.reviewRequest?.trim())
        pars.reviewRequest = reqBody.reviewRequest.trim()

      if (reqBody.descriptionOfCause?.trim())
        pars.descriptionOfCause = reqBody.descriptionOfCause.trim()

      if (reqBody.componentToReview instanceof Integer) {
        def comp = KBComponent.get(reqBody.componentToReview)

        if (comp) {
          pars.componentToReview = comp
        }
        else {
          errors.componentToReview = [[message: "Unable to lookup component to be reviewed!", baddata: reqBody.componentToReview]]
        }
      }
      else {
        errors.componentToReview = [[message: "Missing component to be reviewed!"]]
        result.message = "Request payload must contain the component to be reviewed"
        response.setStatus(400)
      }

      if (reqBody.additionalInfo) {
        try {
          pars.additionalInfo = JsonOutput.toJson(reqBody.additionalInfo)
        }
        catch (Exception e) {
          errors.additionalInfo = [[message: "Unable to save additional Info", baddata: reqBody.additionalInfo]]
        }
      }

      if (reqBody.stdDesc || reqBody.type) {
        def desc = null
        def reqDesc = reqBody?.stdDesc ?: reqBody.type
        def cat = RefdataCategory.findByLabel(RCConstants.REVIEW_REQUEST_STD_DESC)

        if (reqDesc instanceof Integer) {
          def rdv = RefdataValue.get(reqBody.stdDesc)

          if (rdv && rdv in cat.values) {
            desc = rdv
          }
        }
        else {
          desc = RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STD_DESC, reqDesc)
        }

        if (desc) {
          pars.stdDesc = desc
        }
        else {
          errors.stdDesc = [[message: "Illegal value for standard description provided!", baddata: reqDesc]]
        }
      }

      if (errors.size() == 0) {
        try {
          List<CuratoryGroup> curatoryGroups = pars.componentToReview.respondsTo('curatoryGroups') ? pars.componentToReview.curatoryGroups : null
          obj = reviewRequestService.raise(pars.componentToReview, pars.reviewRequest, pars.descriptionOfCause, RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_TYPE, 'User Request'), pars.additionalInfo, pars.stdDesc, curatoryGroups)

          if (obj) {
            result = restMappingService.mapObjectToJson(obj, user)
            response.status = 201

            result._links = generateLinks(obj, user)
          }
          else {
            response.setStatus(500)
            result.result = 'ERROR'
            result.message = "Unable to create request for review!"
          }
        }
        catch (Exception e) {
          response.setStatus(500)
          result.result = 'ERROR'
          response.message = "There was an error creating the request."
        }
      }
    }
    else {
      result.result = 'ERROR'
      response.setStatus(403)
      result.message = "User is not allowed to delete this component!"
    }
    render result as JSON
  }

  @Secured(value=["hasRole('ROLE_EDITOR')", 'IS_AUTHENTICATED_FULLY'])
  @Transactional
  def delete() {
    def result = ['result':'OK', 'params': params]
    def user = User.get(springSecurityService.principal.id)
    def obj = ReviewRequest.get(genericOIDService.oidToId(params.id))

    if ( obj && obj.isDeletable() ) {
      def curator = isUserCurator(obj, user)

      if ( curator || user.isAdmin() ) {
        obj.status = RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS,'Deleted')
      }
      else {
        result.result = 'ERROR'
        response.setStatus(403)
        result.message = "User must belong to at least one curatory group of an existing title to make changes!"
      }
    }
    else if (!obj) {
      result.result = 'ERROR'
      response.setStatus(404)
      result.message = "TitleInstance not found or empty request body!"
    }
    else {
      result.result = 'ERROR'
      response.setStatus(403)
      result.message = "User is not allowed to delete this component!"
    }
    render result as JSON
  }

  private def isUserCurator(obj, user) {
    def curator = false

    if (obj.allocatedGroups?.id.intersect(user.curatoryGroups?.id)) {
      curator = true
    }

    curator
  }

  private def generateLinks(obj,user) {
    def base = grailsApplication.config.serverUrl + "/rest" + obj.restPath + "/${obj.id}"
    def linksObj = [self:[href:base]]
    def curator = isUserCurator(obj,user)

    if (curator || user.isAdmin()) {
      linksObj.update = [href:base]
      linksObj.delete = [href:base]
    }

    return linksObj
  }
}
