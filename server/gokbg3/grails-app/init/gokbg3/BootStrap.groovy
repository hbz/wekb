package gokbg3


import de.wekb.helper.RCConstants
import grails.core.GrailsClass
import grails.core.GrailsApplication
import liquibase.util.csv.opencsv.CSVReader
import wekb.LanguagesService

import javax.servlet.http.HttpServletRequest
import grails.plugin.springsecurity.acl.*

import org.gokb.DomainClassExtender
import org.gokb.ComponentStatisticService
import org.gokb.cred.*

import com.k_int.apis.A_Api

class BootStrap {

    GrailsApplication grailsApplication
    def cleanupService
    def ComponentStatisticService
    def concurrencyManagerService
    def ESWrapperService

    def init = { servletContext ->

        log.info("------------------------------------Init Begin--------------------------------------------")


        log.info("Database: ${grailsApplication.config.dataSource.url}")
        log.info("Database datasource dbCreate: ${grailsApplication.config.dataSource.dbCreate}")
        log.info("Database migration plugin updateOnStart: ${grailsApplication.config.grails.plugin.databasemigration.updateOnStart}")

        log.info("\n\n\n **WARNING** \n\n\n - Automatic create of component identifiers index is no longer part of the domain model");
        log.info("Create manually with create index norm_id_value_idx on kbcomponent(kbc_normname(64),id_namespace_fk,class)");


        KBComponent.withTransaction() {
            cleanUpMissingDomains()
        }

        // Add our custom metaclass methods for all KBComponents.
        alterDefaultMetaclass()

        // Add Custom APIs.
        addCustomApis()

        // Add a custom check to see if this is an ajax request.
        HttpServletRequest.metaClass.isAjax = {
            'XMLHttpRequest' == delegate.getHeader('X-Requested-With')
        }

        // Global System Roles
        log.info("Set global system roles")
        KBComponent.withTransaction() {
            def contributorRole = Role.findByAuthority('ROLE_CONTRIBUTOR') ?: new Role(authority: 'ROLE_CONTRIBUTOR', roleType: 'global').save(failOnError: true)
            def userRole = Role.findByAuthority('ROLE_USER') ?: new Role(authority: 'ROLE_USER', roleType: 'global').save(failOnError: true)
            def editorRole = Role.findByAuthority('ROLE_EDITOR') ?: new Role(authority: 'ROLE_EDITOR', roleType: 'global').save(failOnError: true)
            def adminRole = Role.findByAuthority('ROLE_ADMIN') ?: new Role(authority: 'ROLE_ADMIN', roleType: 'global').save(failOnError: true)
            def apiRole = Role.findByAuthority('ROLE_API') ?: new Role(authority: 'ROLE_API', roleType: 'global').save(failOnError: true)
            def suRole = Role.findByAuthority('ROLE_SUPERUSER') ?: new Role(authority: 'ROLE_SUPERUSER', roleType: 'global').save(failOnError: true)

            /*log.debug("Create admin user...");
            def adminUser = User.findByUsername('admin')
            if (!adminUser) {
                log.error("No admin user found, create")
                adminUser = new User(
                        username: 'admin',
                        password: 'admin',
                        display: 'Admin',
                        email: 'admin@localhost',
                        enabled: true).save(failOnError: true)
            }

            def ingestAgent = User.findByUsername('ingestAgent')
            if (!ingestAgent) {
                log.error("No ingestAgent user found, create")
                ingestAgent = new User(
                        username: 'ingestAgent',
                        password: 'ingestAgent',
                        display: 'Ingest Agent',
                        email: '',
                        enabled: false).save(failOnError: true)
            }
            def deletedUser = User.findByUsername('deleted')
            if (!deletedUser) {
                log.error("No deleted user found, create")
                deletedUser = new User(
                        username: 'deleted',
                        password: 'deleted',
                        display: 'Deleted User',
                        email: '',
                        enabled: false).save(failOnError: true)
            }

            if (Environment.current != Environment.PRODUCTION) {
                def tempUser = User.findByUsername('tempUser')
                if (!tempUser) {
                    log.error("No tempUser found, create")
                    tempUser = new User(
                            username: 'tempUser',
                            password: 'tempUser',
                            display: 'Temp User',
                            email: '',
                            enabled: true).save(failOnError: true)
                }

                if (!tempUser.authorities.contains(userRole)) {
                    UserRole.create tempUser, userRole
                }
            }*/


            // Make sure admin user has all the system roles.
            /*[contributorRole, userRole, editorRole, adminRole, apiRole, suRole].each { role ->
                log.debug("Ensure admin user has ${role} role");
                if (!adminUser.authorities.contains(role)) {
                    UserRole.create adminUser, role
                }
            }*/
        }



        refdataCats()

        registerDomainClasses()

        //migrateDiskFilesToDatabase()

        CuratoryGroup.withTransaction() {
            if (grailsApplication.config.gokb.defaultCuratoryGroup != null && grailsApplication.config.gokb.defaultCuratoryGroup != "") {

                log.info("Ensure curatory group: ${grailsApplication.config.gokb?.defaultCuratoryGroup}");

                def local_cg = CuratoryGroup.findByName(grailsApplication.config.gokb?.defaultCuratoryGroup) ?:
                        new CuratoryGroup(name: grailsApplication.config.gokb?.defaultCuratoryGroup).save(flush: true, failOnError: true);
            }
        }


        /*log.info("GoKB missing normalised component names");

        def ctr = 0;
        KBComponent.executeQuery("select kbc.id from KBComponent as kbc where kbc.normname is null and kbc.name is not null").each { kbc_id ->
            KBComponent kbc = KBComponent.get(kbc_id)
            log.debug("Repair component with no normalised name.. ${kbc.class.name} ${kbc.id} ${kbc.name}");
            kbc.generateNormname()
            kbc.save(flush: true, failOnError: true);
            ctr++
        }
        log.debug("${ctr} components updated");*/

        log.info("GoKB remove usused refdata");
        def rr_std = RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STD_DESC, 'RR Standard Desc 1')

        if (rr_std) {
            rr_std.delete()
        }

        log.info("Fix missing Combo status");

        def status_active = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)
        int num_c = Combo.executeUpdate("update Combo set status = ? where status is null", [status_active])
        log.debug("${num_c} combos updated");

        /*log.info("GoKB defaultSortKeys()");
        defaultSortKeys()*/

       /* log.info("GoKB sourceObjects()");
        sourceObjects()*/

        log.info("Ensure default Identifier namespaces")
        def namespaces = [
                [value: 'cup', name: 'cup', targetType: 'TitleInstancePackagePlatform'],
                [value: 'dnb', name: 'dnb', targetType: 'TitleInstancePackagePlatform'],
                [value: 'doi', name: 'DOI', targetType: 'TitleInstancePackagePlatform'],
                [value: 'eissn', name: 'e-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'ezb', name: 'EZB-ID', targetType: 'TitleInstancePackagePlatform'],
                [value: 'gnd-id', name: 'gnd-id', targetType: 'TitleInstancePackagePlatform'],
                [value: 'isbn', name: 'ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'issn', name: 'p-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'issnl', name: 'ISSN-L', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'TitleInstancePackagePlatform'],
                [value: 'pisbn', name: 'Print-ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                [value: 'oclc', name: 'oclc', targetType: 'TitleInstancePackagePlatform'],
                [value: 'preselect', name: 'preselect', targetType: 'TitleInstancePackagePlatform'],
                [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'TitleInstancePackagePlatform'],

                //Kbart Import
                [value: 'ill_indicator', name: 'Ill Indicator',  targetType: 'TitleInstancePackagePlatform'],
                [value: 'package_isci', name: 'Package ISCI',  targetType: 'TitleInstancePackagePlatform'],
                [value: 'package_isil', name: 'Package ISIL',  targetType: 'TitleInstancePackagePlatform'],
                [value: 'package_ezb_anchor', name: 'EZB Anchor',  targetType: 'TitleInstancePackagePlatform'],


                [value: 'Anbieter_Produkt_ID', name: 'Anbieter_Produkt_ID', targetType: 'Package'],
                [value: 'dnb', name: 'dnb', targetType: 'Package'],
                [value: 'doi', name: 'DOI', targetType: 'Package'],
                [value: 'ezb', name: 'EZB-ID', targetType: 'Package'],
                [value: 'gvk_ppn', name: 'gvk_ppn', targetType: 'Package'],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'Package'],
                [value: 'package_isci', name: 'Package ISCI',  targetType: 'Package'],
                [value: 'package_ezb_anchor', name: 'EZB Anchor',  targetType: 'Package'],
                [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'Package'],
                [value: 'zdb_ppn', name: 'EZB Anchor',  targetType: 'Package'],


                [value: 'gnd-id', name: 'gnd-id', targetType: 'Org'],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$",  targetType: 'Org'],
                [value: 'zdb_ppn', name: 'EZB Anchor',  targetType: 'Org'],
        ]

        namespaces.each { ns ->
            RefdataValue targetType = RefdataValue.findByValueAndOwner(ns.targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
            def ns_obj = IdentifierNamespace.findByValueAndTargetType(ns.value, targetType)

            if (ns_obj) {
                if (ns.pattern && !ns_obj.pattern) {
                    ns_obj.pattern = ns.pattern
                }

                if (ns.name && !ns_obj.name) {
                    ns_obj.name = ns.name
                }

                if (ns.family && !ns_obj.family) {
                    ns_obj.family = ns.family
                }

                if (ns.targetType) {
                    ns_obj.targetType = targetType
                }

                ns_obj.save(flush: true)
            } else {
                ns.targetType = targetType
                ns_obj = new IdentifierNamespace(ns).save(flush: true, failOnError: true)
            }

            log.info("Ensured ${ns_obj}!")
        }


        /*log.info("Register users and override default admin password");
        registerUsers()*/

        anonymizeUsers()

        log.info("Ensuring ElasticSearch index")
        ensureEsIndices()

        log.info("Checking for missing component statistics")
        ComponentStatisticService.updateCompStats()

        log.info("------------------------------------Init End--------------------------------------------")
    }

    def cleanUpMissingDomains() {

        log.info("cleanUpMissingDomains()")
        def domains = KBDomainInfo.createCriteria().list { ilike('dcName', 'org.gokb%') }.each { d ->
            try {

                // Just try reading the class.
                Class c = Class.forName(d.dcName)
                // log.debug ("Looking for ${d.dcName} found class ${c}.")

            } catch (ClassNotFoundException e) {
                d.delete(flush: true)
                log.info("Deleted domain object for ${d.dcName} as the Class could not be found.")
            }
        }
    }


    private void addCustomApis() {

        log.info("addCustomApis()")
        (grailsApplication.getArtefacts("Domain")*.clazz).each { Class<?> c ->

            // SO: Changed this to use the APIs 'applicableFor' method that is used to check whether,
            // to add to the class or not. This defaults to "true". Have overriden on the GrailsDomainHelperApi utils
            // and moved the selective code there. This means that *ALL* domain classes will still receive the methods in the
            // apiClasses.

            // log.debug("Considering ${c}")
            grailsApplication.config.apiClasses.each { String className ->
                // log.debug("Adding methods to ${c.name} from ${className}");
                // Add the api methods.
                A_Api.addMethods(c, Class.forName(className))
            }
        }
    }

    def registerDomainClasses() {

        log.info("registerDomainClasses()")

        AclClass aclClass = AclClass.findByClassName('org.gokb.cred.KBDomainInfo') ?: new AclClass(className: 'org.gokb.cred.KBDomainInfo').save(flush: true)

        AclSid sidAdmin = AclSid.findBySid('ROLE_ADMIN') ?: new AclSid(sid: 'ROLE_ADMIN', principal: false).save(flush: true)
        AclSid sidSuperUser = AclSid.findBySid('ROLE_SUPERUSER') ?: new AclSid(sid: 'ROLE_SUPERUSER', principal: false).save(flush: true)
        AclSid sidUser = AclSid.findBySid('ROLE_USER') ?: new AclSid(sid: 'ROLE_USER', principal: false).save(flush: true)
        AclSid sidContributor = AclSid.findBySid('ROLE_CONTRIBUTOR') ?: new AclSid(sid: 'ROLE_CONTRIBUTOR', principal: false).save(flush: true)
        AclSid sidEditor = AclSid.findBySid('ROLE_EDITOR') ?: new AclSid(sid: 'ROLE_EDITOR', principal: false).save(flush: true)
        AclSid sidApi = AclSid.findBySid('ROLE_API') ?: new AclSid(sid: 'ROLE_API', principal: false).save(flush: true)

        RefdataValue std_domain_type = RefdataCategory.lookupOrCreate(RCConstants.DC_TYPE, 'Standard').save(flush: true, failOnError: true)
        grailsApplication.domainClasses.each { dc ->
            // log.debug("Ensure ${dc.name} has entry in KBDomainInfo table");
            KBDomainInfo dcinfo = KBDomainInfo.findByDcName(dc.clazz.name)
            if (dcinfo == null) {
                dcinfo = new KBDomainInfo(dcName: dc.clazz.name, displayName: dc.name, type: std_domain_type);
                dcinfo.save(flush: true);
            }

            if (dcinfo.dcName.startsWith('org.gokb.cred') || dcinfo.dcName.startsWith('wekb')) {
                AclObjectIdentity oid

                if (!AclObjectIdentity.findByObjectId(dcinfo.id)) {
                    oid = new AclObjectIdentity(objectId: dcinfo.id, aclClass: aclClass, owner: sidAdmin, entriesInheriting: false).save(flush: true)
                }
            }
        }
    }

    def alterDefaultMetaclass = {

        // Inject helpers to Domain classes.
        log.info("alterDefaultMetaclass()")
        grailsApplication.domainClasses.each { GrailsClass domainClass ->

            // Extend the domain class.
            DomainClassExtender.extend(domainClass)

        }
    }

    def assertPublisher(name) {
        def p = Org.findByName(name)
        if (!p) {
            def content_provider_role = RefdataCategory.lookupOrCreate('Org Role', 'Content Provider');
            p = new Org(name: name)
            p.save(flush: true);
        }

    }

    /*def defaultSortKeys() {
        def vals = RefdataValue.executeQuery("select o from RefdataValue o where o.sortKey is null or trim(o.sortKey) = ''")

        // Default the sort key to 0.
        vals.each {
            it.sortKey = "0"
            it.save(flush: true, failOnError: true)
        }

        // Now we should also do the same for the Domain objects.
        vals = KBDomainInfo.executeQuery("select o from KBDomainInfo o where o.dcSortOrder is null or trim(o.dcSortOrder) = ''")

        // Default the sort key to 0.
        vals.each {
            it.dcSortOrder = "0"
            it.save(flush: true, failOnError: true)
        }
    }*/

    def destroy = {
    }


    def refdataCats() {

        log.info("refdataCats")
        RefdataValue.executeUpdate('UPDATE RefdataValue rdv SET rdv.isHardData =:reset', [reset: false])
        RefdataCategory.executeUpdate('UPDATE RefdataCategory rdc SET rdc.isHardData =:reset', [reset: false])

        List rdcList = getParsedCsvData('setup/RefdataCategory.csv', 'RefdataCategory')

        rdcList.each { map ->
            RefdataCategory.construct(map)
        }

        List rdvList = getParsedCsvData('setup/RefdataValue.csv', 'RefdataValue')

        rdvList.each { map ->
            RefdataValue.construct(map)
        }

        List ddcList = getParsedCsvData('setup/DDC.csv', 'RefdataValue')

        ddcList.each { map ->
            if(map.get('token').toInteger() < 10)
            {
                map.token = "00"+map.get('token')
            }

            if(map.get('token').toInteger() < 100 && map.get('token').toInteger() >= 10)
            {
                map.token = "0"+map.get('token')
            }

            RefdataValue.construct(map)
        }

        log.debug("Deleting any null refdata values")
        RefdataValue.executeUpdate('delete from RefdataValue where value is null')

        log.debug("Languages Service initialize")
        LanguagesService.initialize()
    }

   /* def sourceObjects() {
        log.debug("Lookup or create source objects")
        def ybp_source = Source.findByName('YBP') ?: new Source(name: 'YBP').save(flush: true, failOnError: true)
        def cup_source = Source.findByName('CUP') ?: new Source(name: 'CUP').save(flush: true, failOnError: true)
        def wiley_source = Source.findByName('WILEY') ?: new Source(name: 'WILEY').save(flush: true, failOnError: true)
        def cufts_source = Source.findByName('CUFTS') ?: new Source(name: 'CUFTS').save(flush: true, failOnError: true)
        def askews_source = Source.findByName('ASKEWS') ?: new Source(name: 'ASKEWS').save(flush: true, failOnError: true)
        def ebsco_source = Source.findByName('EBSCO') ?: new Source(name: 'EBSCO').save(flush: true, failOnError: true)
    }*/


    def registerUsers() {
        grailsApplication.config.sysusers.each { su ->
            log.debug("test ${su.name} ${su.pass} ${su.display} ${su.roles}")
            def user = User.findByUsername(su.name)
            if (user) {
                if (user.password != su.pass) {
                    log.debug("Hard change of user password from config ${user.password} -> ${su.pass}")
                    user.password = su.pass
                    user.save(failOnError: true)
                } else {
                    log.debug("${su.name} present and correct");
                }
            } else {
                log.debug("Create user...")
                user = new User(
                        username: su.name,
                        password: su.pass,
                        display: su.display,
                        email: su.email,
                        enabled: true).save(failOnError: true)
            }

            log.debug("Add roles for ${su.name}");
            su.roles.each { r ->
                def role = Role.findByAuthority(r)
                if (!(user.authorities.contains(role))) {
                    log.debug("  -> adding role ${role}")
                    UserRole.create user, role
                } else {
                    log.debug("  -> ${role} already present")
                }
            }
        }
    }

    def anonymizeUsers() {
        if(grailsApplication.config.gokb.anonymizeUsers) {
            log.info("anonymizeUsers")
            User.findAll().each { User user ->

                log.debug("anonymizeUsers ${user.displayName} ${user.username}")
                if(user.curatoryGroups.find{CuratoryGroup curatoryGroup -> curatoryGroup.name == "hbz" || curatoryGroup.name == "LAS:eR"}){
                    user.email = 'local@localhost.local'
                }else {
                    user.username = "User ${user.id}"
                    user.displayName = "User ${user.id}"
                    user.email = 'local@localhost.local'
                    user.password = "${user.lastUpdated}+${user.id}"
                    user.enabled = false
                    user.accountLocked = true
                    user.save()
                }

            }
        }
    }


    def ensureEsIndices() {
        def esIndices = grailsApplication.config.gokb.es.indices?.values()
        for (String indexName in esIndices) {
            try {
                ESWrapperService.createIndex(indexName)
            }
            catch (Exception e) {
                log.error("Problem by ensureEsIndices -> Exception: ${e}")
            }
        }
    }


    List getParsedCsvData(String filePath, String objType) {

        List result = []
        File csvFile = grailsApplication.mainContext.getResource(filePath).file

        if (! ['RefdataCategory', 'RefdataValue'].contains(objType)) {
            println "WARNING: invalid object type ${objType}!"
        }
        else if (! csvFile.exists()) {
            println "WARNING: ${filePath} not found!"
        }
        else {
            csvFile.withReader { reader ->
                CSVReader csvr = new CSVReader(reader, (char) ';', (char) '"', (char) '\\', (int) 1)
                String[] line

                while (line = csvr.readNext()) {
                    if (line[0]) {
                        if (objType == 'RefdataCategory') {
                            // CSV: [token, value_de, value_en]
                            Map<String, Object> map = [
                                    token   : line[0].trim(),
                                    desc_de: line[1].trim(),
                                    desc_en: line[2].trim(),
                                    hardData: true
                            ]
                            result.add(map)
                        }
                        if (objType == 'RefdataValue') {
                            // CSV: [rdc, token, value_de, value_en]
                            Map<String, Object> map = [
                                    token   : line[1].trim(),
                                    rdc     : line[0].trim(),
                                    value_de: line[2].trim(),
                                    value_en: line[3].trim(),
                                    hardData: true

                            ]
                            result.add(map)
                        }
                    }
                }
            }
        }

        result
    }

}
