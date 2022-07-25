package wekb


import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.Identifier
import org.gokb.cred.User

@Transactional
class AccessService {

    SpringSecurityService springSecurityService

    boolean checkEditableObject(Object o, GrailsParameterMap grailsParameterMap) {
        checkEditableObject(o, (grailsParameterMap && grailsParameterMap.curationOverride == 'true'))
    }

    boolean checkEditableObject(Object o, boolean curationOverride = false) {
        boolean editable = false

        List allowedToEditable = ['Identifier',
                                  'Org',
                                  'Package',
                                  'Platform',
                                  'Source',
                                  'TitleInstancePackagePlatform']

        if (!(o.respondsTo('isSystemComponent') && o.isSystemComponent())) {
            def curatedObj = null
            if(o instanceof Identifier){
                curatedObj = o.reference.respondsTo("getCuratoryGroups") ? o.reference : ( o.reference.hasProperty('pkg') ? o.reference.pkg : null )
            }else if(o instanceof Contact){
                curatedObj = o.org
            }
            else {
                curatedObj = o.respondsTo("getCuratoryGroups") ? o : ( o.hasProperty('pkg') ? o.pkg : null )
            }

            User user = springSecurityService.currentUser
            if (curatedObj && curatedObj.curatoryGroups && !(curatedObj instanceof User)) {

                if(user && user.curatoryGroups?.id.intersect(curatedObj.curatoryGroups?.id).size() > 0)
                {
                    editable = true //SecurityApi.isTypeEditable(o.getClass(), true) ?: (grailsParameterMap.curationOverride == 'true' && user.isAdmin())
                }else {
                    editable = (curationOverride && user.isAdmin()) //SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER') ?: (grailsParameterMap.curationOverride == 'true' && user.isAdmin())
                }
            }else {
                if(o instanceof CuratoryGroup && user && o.id in user.curatoryGroups?.id){
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN')
                }
                else{
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
                }
            }
        }

        editable

    }
    boolean checkReadable(String baseclassName) {

        List allowedBaseClasses = ['org.gokb.cred.CuratoryGroup',
                                   'org.gokb.cred.JobResult',
                                   'org.gokb.cred.IdentifierNamespace',
                                   'org.gokb.cred.Identifier',
                                   'org.gokb.cred.Org',
                                   'org.gokb.cred.Package',
                                   'org.gokb.cred.Platform',
                                   'org.gokb.cred.ReviewRequest',
                                   'org.gokb.cred.Source',
                                   'org.gokb.cred.TitleInstancePackagePlatform',
                                   'org.gokb.cred.ComponentWatch']


        if(baseclassName in allowedBaseClasses){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

    boolean checkDeletable(String baseclassName) {

        List allowedBaseClasses = ['org.gokb.cred.CuratoryGroup',
                                   'org.gokb.cred.JobResult',
                                   'org.gokb.cred.IdentifierNamespace',
                                   'org.gokb.cred.Identifier',
                                   'org.gokb.cred.Org',
                                   'org.gokb.cred.Package',
                                   'org.gokb.cred.Platform',
                                   'org.gokb.cred.ReviewRequest',
                                   'org.gokb.cred.Source',
                                   'org.gokb.cred.TitleInstancePackagePlatform',
                                   'org.gokb.cred.ComponentWatch']


        if(baseclassName in allowedBaseClasses){
            return true
        }else {
            return SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER')
        }


    }

}
