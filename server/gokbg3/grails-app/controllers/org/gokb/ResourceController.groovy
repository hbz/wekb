package org.gokb

import org.springframework.security.access.annotation.Secured;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.IOUtils
import org.apache.tika.Tika
import org.apache.tika.metadata.Metadata
import org.gokb.cred.*
import grails.converters.JSON
import wekb.AccessService

class ResourceController {

  def genericOIDService
  def classExaminationService
  def springSecurityService
  def displayTemplateService
  AccessService accessService

  def index() {
  }

  def show() {

    User user = springSecurityService.currentUser

    log.debug("ResourceController::show ${params}");
    def result = ['params':params]
    def oid = params.id
    def displayobj = null
    def read_perm = false

    if (params.type && params.id) {
      oid = "org.gokb.cred." + params.type + ":" + params.id
    }
    else if (params.int('id')) {
      displayobj = KBComponent.get(params.int('id'))
      oid = (displayobj ? (displayobj.class.name + ":" + params.id) : null)
    }

    if ( oid ) {
      displayobj = KBComponent.findByUuid(oid)

      if (!displayobj) {
        displayobj = genericOIDService.resolveOID(oid)
      }
      else {
        oid = "${displayobj?.class?.name}:${displayobj?.id}"
      }

      if ( displayobj ) {

        List allowedPublicShow = ['CuratoryGroup',
                                  'Identifier',
                                  'Org',
                                  'Package',
                                  'Platform',
                                  'Source',
                                  'TitleInstancePackagePlatform']

        if ((displayobj.class.simpleName in allowedPublicShow) || (springSecurityService.isLoggedIn() && springSecurityService.ifAnyGranted("ROLE_ADMIN"))) {

          result.displayobjclassname = displayobj.class.name
          result.__oid = "${result.displayobjclassname}:${displayobj.id}"

          log.debug("Looking up display template for ${result.displayobjclassname}")

          result.displaytemplate = displayTemplateService.getTemplateInfo(result.displayobjclassname)

          log.debug("Using displaytemplate: ${result.displaytemplate}")

          result.displayobjclassname_short = displayobj.class.simpleName

          result.isComponent = (displayobj instanceof KBComponent)

          result.displayobj = displayobj

          if(springSecurityService.isLoggedIn()) {
            read_perm = accessService.checkReadable(displayobj.class.name)

            if (read_perm) {

              if (displayobj instanceof Package) {
                displayobj.createCoreIdentifiersIfNotExist()
              }

              // Need to figure out whether the current user has curatorial rights (or is an admin).
              // Defaults to true as not all components have curatorial groups defined.

              def curatedObj = displayobj.respondsTo("getCuratoryGroups") ? displayobj : (displayobj.hasProperty('pkg') ? displayobj.pkg : false)

              if (curatedObj && curatedObj.curatoryGroups && curatedObj.niceName != 'User') {

                def cur = user.curatoryGroups?.id.intersect(curatedObj.curatoryGroups?.id) ?: []
                request.curator = cur
              } else {
                request.curator = null
              }

              result.editable = accessService.checkEditableObject(displayobj, params)

              // Add any refdata property names for this class to the result.
              //result.refdata_properties = classExaminationService.getRefdataPropertyNames(result.displayobjclassname)

              //result.acl = gokbAclService.readAclSilently(displayobj)

              def oid_components = oid.split(':');
              def qry_params = [result.displayobjclassname, Long.parseLong(oid_components[1])];
              result.ownerClass = oid_components[0]
              result.ownerId = oid_components[1]
              result.num_notes = KBComponent.executeQuery("select count(n.id) from Note as n where ownerClass=? and ownerId=?", qry_params)[0];
              // How many people are watching this object
              result.num_watch = KBComponent.executeQuery("select count(n.id) from ComponentWatch as n where n.component=?", displayobj)[0];
              result.user_watching = KBComponent.executeQuery("select count(n.id) from ComponentWatch as n where n.component=? and n.user=?", [displayobj, user])[0] == 1 ? true : false;
            } else {
              flash.error = "You have no permission to view this resource."
            }
          }
        }else {
          flash.error = "You have no permission to view this resource."
        }
      }
      else {
        log.debug("unable to resolve object")
        flash.error = "Unable to find the requested resource."
      }
    }
    println(result.editable)
        result
    }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def showLogin() {

    redirect(action: 'show', params: params)
  }
}
