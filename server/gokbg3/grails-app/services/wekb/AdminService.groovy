package wekb

import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.acl.AclEntry
import grails.plugin.springsecurity.acl.AclObjectIdentity
import grails.plugin.springsecurity.acl.AclSid
import org.gokb.cred.KBDomainInfo
import org.hibernate.SessionFactory
import org.springframework.security.acls.domain.BasePermission
import org.springframework.security.acls.model.MutableAcl
import org.springframework.security.acls.model.ObjectIdentity
import org.springframework.security.acls.model.Sid
import org.springframework.security.core.session.SessionRegistry

import java.security.Permission

@Transactional
class AdminService {

    def aclUtilService
    def grailsApplication
    def sessionRegistry
    SpringSecurityService springSecurityService


    def setupDefaultAcl() {

        def basicDomains = [
                            "Org",
                            "Package",
                            "Platform",
                            "Source",
                            "TitleInstancePackagePlatform",]

        basicDomains.each { dcd ->

            def kb_domain = KBDomainInfo.findByDcName("org.gokb.cred.${dcd}")

            if(kb_domain) {
                checkAndAdd(kb_domain, 'ROLE_USER', BasePermission.READ)

                checkAndAdd(kb_domain, 'ROLE_CONTRIBUTOR', BasePermission.READ)
                checkAndAdd(kb_domain, 'ROLE_CONTRIBUTOR', BasePermission.WRITE)
                checkAndAdd(kb_domain, 'ROLE_CONTRIBUTOR', BasePermission.CREATE)

                checkAndAdd(kb_domain, 'ROLE_EDITOR', BasePermission.READ)
                checkAndAdd(kb_domain, 'ROLE_EDITOR', BasePermission.WRITE)
                checkAndAdd(kb_domain, 'ROLE_EDITOR', BasePermission.CREATE)
                checkAndAdd(kb_domain, 'ROLE_EDITOR', BasePermission.DELETE)

                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.READ)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.WRITE)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.CREATE)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.DELETE)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.ADMINISTRATION)
            }
        }

        //Set all other Permissions only to ROLE_ADMIN
        grailsApplication.domainClasses.each { dc ->

            def kb_domain = KBDomainInfo.findByDcName(dc.clazz.name)

            if(!(dc.clazz.name in basicDomains) && kb_domain){
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.READ)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.WRITE)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.CREATE)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.DELETE)
                checkAndAdd(kb_domain, 'ROLE_ADMIN', BasePermission.ADMINISTRATION)
            }

        }

        //set specialPermission
        setSpecialAcl()

    }

    private def setSpecialAcl(){

        def dc_cmb = KBDomainInfo.findByDcName("org.gokb.cred.Combo")

        checkAndAdd(dc_cmb, 'ROLE_CONTRIBUTOR', BasePermission.CREATE)
        checkAndAdd(dc_cmb, 'ROLE_CONTRIBUTOR', BasePermission.DELETE)

        checkAndAdd(dc_cmb, 'ROLE_EDITOR', BasePermission.CREATE)
        checkAndAdd(dc_cmb, 'ROLE_EDITOR', BasePermission.DELETE)

        def dc_cg = KBDomainInfo.findByDcName('org.gokb.cred.CuratoryGroup')

        checkAndAdd(dc_cg, 'ROLE_CONTRIBUTOR', BasePermission.READ)
        checkAndAdd(dc_cg, 'ROLE_CONTRIBUTOR', BasePermission.WRITE)
        checkAndAdd(dc_cg, 'ROLE_CONTRIBUTOR', BasePermission.CREATE)

        checkAndAdd(dc_cg, 'ROLE_EDITOR', BasePermission.READ)
        checkAndAdd(dc_cg, 'ROLE_EDITOR', BasePermission.WRITE)
        checkAndAdd(dc_cg, 'ROLE_EDITOR', BasePermission.CREATE)

        // DecisionSupport

        def dc_dsc = KBDomainInfo.findByDcName('org.gokb.cred.DSCriterion')

        checkAndAdd(dc_dsc, 'ROLE_EDITOR', BasePermission.READ)

        def dc_dscat = KBDomainInfo.findByDcName('org.gokb.cred.DSCategory')

        checkAndAdd(dc_dscat, 'ROLE_EDITOR', BasePermission.READ)

        def dc_kbc = KBDomainInfo.findByDcName('org.gokb.cred.KBComponent')

        checkAndAdd(dc_kbc, 'ROLE_USER', BasePermission.READ)

        checkAndAdd(dc_kbc, 'ROLE_CONTRIBUTOR', BasePermission.READ)

        checkAndAdd(dc_kbc, 'ROLE_EDITOR', BasePermission.READ)


        def dc_id = KBDomainInfo.findByDcName('org.gokb.cred.Identifier')

        checkAndAdd(dc_id, 'ROLE_USER', BasePermission.READ)

        checkAndAdd(dc_id, 'ROLE_CONTRIBUTOR', BasePermission.READ)
        checkAndAdd(dc_id, 'ROLE_CONTRIBUTOR', BasePermission.CREATE)
        checkAndAdd(dc_id, 'ROLE_CONTRIBUTOR', BasePermission.DELETE)

        checkAndAdd(dc_id, 'ROLE_EDITOR', BasePermission.READ)
        checkAndAdd(dc_id, 'ROLE_EDITOR', BasePermission.CREATE)
        checkAndAdd(dc_id, 'ROLE_EDITOR', BasePermission.DELETE)

        def dc_ns = KBDomainInfo.findByDcName('org.gokb.cred.IdentifierNamespace')

        checkAndAdd(dc_ns, 'ROLE_USER', BasePermission.READ)

        checkAndAdd(dc_ns, 'ROLE_CONTRIBUTOR', BasePermission.READ)

        checkAndAdd(dc_ns, 'ROLE_EDITOR', BasePermission.READ)


        def dc_rdc = KBDomainInfo.findByDcName('org.gokb.cred.RefdataCategory')

        checkAndAdd(dc_rdc, 'ROLE_CONTRIBUTOR', BasePermission.READ)

        checkAndAdd(dc_rdc, 'ROLE_EDITOR', BasePermission.READ)


        def dc_rdv = KBDomainInfo.findByDcName('org.gokb.cred.RefdataValue')

        checkAndAdd(dc_rdv, 'ROLE_USER', BasePermission.READ)

        checkAndAdd(dc_rdv, 'ROLE_CONTRIBUTOR', BasePermission.READ)

        checkAndAdd(dc_rdv, 'ROLE_EDITOR', BasePermission.READ)

        def dc_uo = KBDomainInfo.findByDcName('org.gokb.cred.UserOrganisation')

        checkAndAdd(dc_uo, 'ROLE_CONTRIBUTOR', BasePermission.READ)
        checkAndAdd(dc_uo, 'ROLE_CONTRIBUTOR', BasePermission.WRITE)
        checkAndAdd(dc_uo, 'ROLE_CONTRIBUTOR', BasePermission.CREATE)

        checkAndAdd(dc_uo, 'ROLE_EDITOR', BasePermission.READ)
        checkAndAdd(dc_uo, 'ROLE_EDITOR', BasePermission.WRITE)
        checkAndAdd(dc_uo, 'ROLE_EDITOR', BasePermission.CREATE)








    }

    private def checkAndAdd(KBDomainInfo domain, String role, BasePermission permission){

        if (domain.dcName.startsWith('org.gokb.cred') || domain.dcName.startsWith('wekb')) {
            def sid = AclSid.findBySid(role)
            AclObjectIdentity aclObjectIdentity = AclObjectIdentity.findByObjectId(domain.id)

            if (!AclEntry.findBySidAndAclObjectIdentityAndMask(sid, aclObjectIdentity, permission.mask)) {
                aclUtilService.addPermission(domain, role, permission)
            }
        }
    }

    int getNumberOfActiveUsers() {
        getActiveUsers( (1000 * 60 * 10) ).size() // 10 minutes
    }

    List getActiveUsers(long ms) {
        List result = []

        sessionRegistry.getAllPrincipals().each { user ->
            List lastAccessTimes = []

            sessionRegistry.getAllSessions(user, false).each { userSession ->
                if (user.username == springSecurityService.getCurrentUser()?.username) {
                    userSession.refreshLastRequest()
                }
                lastAccessTimes << userSession.getLastRequest().getTime()
            }
            if (lastAccessTimes.max() > System.currentTimeMillis() - ms) {
                result.add(user)
            }
        }
        result
    }
}
