package wekb

import com.k_int.apis.SecurityApi
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.User

@Transactional
class AccessService {

    SpringSecurityService springSecurityService

    boolean checkEditableObject(Object o, GrailsParameterMap grailsParameterMap) {
        boolean editable = false

        if (!(o.respondsTo('isSystemComponent') && o.isSystemComponent())) {

            def curatedObj = o.respondsTo("getCuratoryGroups") ? o : ( o.hasProperty('pkg') ? o.pkg : false )
            User user = springSecurityService.currentUser
            if (curatedObj && curatedObj.curatoryGroups && curatedObj.niceName != 'User') {

                if(user.curatoryGroups?.id.intersect(curatedObj.curatoryGroups?.id).size() > 0)
                {
                    editable = SecurityApi.isTypeEditable(o.getClass(), true) ?: (grailsParameterMap.curationOverride == 'true' && grailsParameterMap.isAdmin())
                }else {
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER') ?: false
                }
            }else {
                if(o instanceof CuratoryGroup && o.id in user.curatoryGroups?.id){
                    editable = SecurityApi.isTypeEditable(o.getClass(), true)
                }
                else{
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER') ?: false
                }
            }
        }

        editable

    }
    boolean checkReadable(String baseclassName) {

        List allowedBaseClasses = ['org.gokb.cred.CuratoryGroup',
                                   'org.gokb.cred.JobResult',
                                   'org.gokb.cred.IdentifierNamespace',
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
            Class target_class = Class.forName(baseclassName)
            if(target_class.isTypeReadable()){
                return true
            }else {
                return false
            }
        }


    }

}
