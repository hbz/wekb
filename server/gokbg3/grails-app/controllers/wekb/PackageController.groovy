package wekb

import grails.converters.JSON
import grails.core.GrailsApplication
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import org.gokb.GenericOIDService
import org.gokb.cred.KBComponent
import org.gokb.cred.Package
import org.gokb.cred.User
import org.springframework.security.access.annotation.Secured

import java.nio.file.Files

class PackageController {

    SpringSecurityService springSecurityService
    GenericOIDService genericOIDService
    AccessService accessService
    GrailsApplication grailsApplication

    def index() { }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def recentActivity() {
        User user = springSecurityService.currentUser

        log.debug("PackageController::recentActivity ${params}");
        def result = ['params':params]
        def oid = params.id
        Package pkg = null
        def read_perm = false

       if (params.int('id')) {
           pkg = Package.get(params.int('id'))
            oid = (pkg ? (pkg.class.name + ":" + params.id) : null)
       }

        if ( oid ) {
            pkg = Package.findByUuid(oid)

            if (!pkg) {
                pkg = genericOIDService.resolveOID(oid)
            }

            if ( pkg ) {

                read_perm = accessService.checkReadable(pkg.class.name)

                if (read_perm) {

                    result.editable = accessService.checkEditableObject(pkg, params)

                }
                else {
                    response.setStatus(403)
                    result.code = 403
                    result.result = "ERROR"
                    result.message = "You have no permission to view this resource."
                }
            }
            else {
                log.debug("unable to resolve object")
                response.setStatus(404)
                result.status = 404
                result.result = "ERROR"
                result.message = "Unable to find the requested resource."
            }
        }

        if (pkg && read_perm) {
            result.pkg = pkg

            result.recentActivitys = pkg.getRecentActivity()
        }

        result
    }

    @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
    def showYgorStatistic() {
        if(params.id && grailsApplication.config.gokb.ygorUrl) {

            redirect(url: "${grailsApplication.config.gokb.ygorUrl}/enrichment/uploadRawFileFromWEKB?ygorStatisticResultHash=${params.id}")

        }else {

            flash.error = "We are sorry. Unfortunately an error happened. The statistics cannot be displayed"
            redirect(url: request.getHeader("referer"))
        }
    }
}
