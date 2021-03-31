package wekb

import com.k_int.apis.SecurityApi
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.servlet.mvc.GrailsParameterMap
import org.gokb.cred.User

@Transactional
class AccessService {

    SpringSecurityService springSecurityService

    def checkEditableObject(Object o, GrailsParameterMap grailsParameterMap) {
        boolean editable = false

        if (!(o.respondsTo('isSystemComponent') && o.isSystemComponent())) {

            def curatedObj = o.respondsTo("getCuratoryGroups") ? o : ( o.hasProperty('pkg') ? o.pkg : false )

            if (curatedObj && curatedObj.curatoryGroups && curatedObj.niceName != 'User') {
                User user = springSecurityService.currentUser

                if(user.curatoryGroups?.id.intersect(curatedObj.curatoryGroups?.id).size() > 0)
                {
                    editable = SecurityApi.isTypeEditable(o.getClass(), true) ?: (grailsParameterMap.curationOverride == 'true' && grailsParameterMap.isAdmin())
                }else {
                    editable = SpringSecurityUtils.ifAnyGranted('ROLE_SUPERUSER') ?: false
                }
            }else {
                editable = SecurityApi.isTypeEditable(o.getClass(), true)
            }
        }

        editable

    }
}
