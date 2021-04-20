package gokbg3


import de.wekb.helper.RCConstants;
import grails.util.Environment
import grails.core.GrailsClass
import grails.core.GrailsApplication
import grails.converters.JSON
import liquibase.util.csv.opencsv.CSVReader
import org.gokb.LanguagesService

import javax.servlet.http.HttpServletRequest
import grails.plugin.springsecurity.acl.*

import org.gokb.DomainClassExtender
import org.gokb.ComponentStatisticService
import org.gokb.cred.*

import com.k_int.apis.A_Api;
import com.k_int.ConcurrencyManagerService.Job
import org.elasticsearch.client.IndicesAdminClient
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse

class BootStrap {

    GrailsApplication grailsApplication
    def cleanupService
    def ComponentStatisticService
    def concurrencyManagerService
    def ESWrapperService
    //def titleLookupService

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


        if (grailsApplication.config.gokb.decisionSupport) {
            log.info("Configuring default decision support parameters");
            DSConfig();
        }


//    log.debug("Theme:: ${grailsApplication.config.gokb.theme}");
//


        refdataCats()

        registerDomainClasses()

        migrateDiskFilesToDatabase()

        CuratoryGroup.withTransaction() {
            if (grailsApplication.config.gokb.defaultCuratoryGroup != null) {

                log.info("Ensure curatory group: ${grailsApplication.config.gokb?.defaultCuratoryGroup}");

                def local_cg = CuratoryGroup.findByName(grailsApplication.config.gokb?.defaultCuratoryGroup) ?:
                        new CuratoryGroup(name: grailsApplication.config.gokb?.defaultCuratoryGroup).save(flush: true, failOnError: true);
            }
        }


        log.info("GoKB missing normalised component names");

        def ctr = 0;
        KBComponent.executeQuery("select kbc.id from KBComponent as kbc where kbc.normname is null and kbc.name is not null").each { kbc_id ->
            KBComponent kbc = KBComponent.get(kbc_id)
            log.debug("Repair component with no normalised name.. ${kbc.class.name} ${kbc.id} ${kbc.name}");
            kbc.generateNormname()
            kbc.save(flush: true, failOnError: true);
            ctr++
        }
        log.debug("${ctr} components updated");

        log.info("GoKB remove usused refdata");
        def rr_std = RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STD_DESC, 'RR Standard Desc 1')

        if (rr_std) {
            rr_std.delete()
        }

        log.info("GoKB missing normalised identifiers");

        def id_ctr = 0;
        Identifier.executeQuery("select id.id from Identifier as id where id.normname is null and id.value is not null").each { id_id ->
            Identifier i = Identifier.get(id_id)
            i.generateNormname()
            i.save(flush: true, failOnError: true)
            id_ctr++
        }
        log.debug("${id_ctr} identifiers updated");

        log.info("Fix missing Combo status");

        def status_active = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)
        int num_c = Combo.executeUpdate("update Combo set status = ? where status is null", [status_active])
        log.debug("${num_c} combos updated");

        log.info("GoKB defaultSortKeys()");
        defaultSortKeys()

        log.info("GoKB sourceObjects()");
        sourceObjects()

        log.info("Ensure default Identifier namespaces")
        def namespaces = [
                [value: 'isbn', name: 'ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$"],
                [value: 'pisbn', name: 'Print-ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$"],
                [value: 'issn', name: 'p-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$"],
                [value: 'eissn', name: 'e-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$"],
                [value: 'issnl', name: 'ISSN-L', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$"],
                [value: 'doi', name: 'DOI'],
                [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$"],
                [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$"],
                [value: 'ezb_anchor', name: 'EZB Anchor'],
                [value: 'ezb', name: 'EZB-ID'],
                [value: 'package_isci', name: 'Package ISCI'],
        ]

        namespaces.each { ns ->
            def ns_obj = IdentifierNamespace.findByValue(ns.value)

            if (ns_obj) {
                if (ns.pattern && !ns_obj.pattern) {
                    ns_obj.pattern = ns.pattern
                    ns_obj.save(flush: true)
                }

                if (ns.name && !ns_obj.name) {
                    ns_obj.name = ns.name
                    ns_obj.save(flush: true)
                }
            } else {
                ns_obj = new IdentifierNamespace(ns).save(flush: true, failOnError: true)
            }

            log.info("Ensured ${ns_obj}!")
        }


        // log.info("Default batch loader config");
        // defaultBulkLoaderConfig();

        /*log.info("Register users and override default admin password");
        registerUsers()*/

        anonymizeUsers()

        log.info("Ensuring ElasticSearch index")
        ensureEsIndices()


        log.info("Bootstrap Identifier Cleanup")
        Job hk_job = concurrencyManagerService.createJob {
            cleanupService.housekeeping()
        }.startOrQueue()

        hk_job.description = "Bootstrap Identifier Cleanup"
        hk_job.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'BootstrapIdentifierCleanup')

        hk_job.startTime = new Date()

        log.info("Checking for missing component statistics")
        ComponentStatisticService.updateCompStats()

        log.info("------------------------------------Init End--------------------------------------------")
    }

    def defaultBulkLoaderConfig() {
        // BulkLoaderConfig
        grailsApplication.config.kbart2.mappings.each { k, v ->
            log.debug("Process ${k}");
            def existing_cfg = BulkLoaderConfig.findByCode(k)
            if (existing_cfg) {
                log.debug("Got existing config");
            } else {
                def cfg = v as JSON
                existing_cfg = new BulkLoaderConfig(code: k, cfg: cfg?.toString()).save(flush: true, failOnError: true)
            }
        }
    }

    def migrateDiskFilesToDatabase() {
        log.info("Migrate Disk Files");
        def baseUploadDir = grailsApplication.config.baseUploadDir ?: '.'

        DataFile.findAll("from DataFile as df where df.fileData is null").each { df ->
            log.debug("Migrating files for ${df.uploadName}::${df.guid}")
            def sub1 = df.guid.substring(0, 2);
            def sub2 = df.guid.substring(2, 4);
            def temp_file_name = "${baseUploadDir}/${sub1}/${sub2}/${df.guid}";
            try {
                def source_file = new File(temp_file_name);
                df.fileData = source_file.getBytes()
                if (df.save(flush: true)) {
                    //success
                    source_file.delete()
                } else {
                    log.debug("Errors while trying to save DataFile fileData:")
                    log.debug(df.errors)
                }
            } catch (Exception e) {
                log.error("Exception while migrating files to database. File ${temp_file_name}", e)
            }
        }
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
            // SecurityApi.
            // II: has this caused projects under org.gokb.refine to no longer be visible? Not sure how to fix it.

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

            if (dcinfo.dcName.startsWith('org.gokb.cred') || dcinfo.dcName == 'org.gokb.Annotation') {
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
            p.tags.add(content_provider_role);
            p.save(flush: true);
        }

    }

    def defaultSortKeys() {
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
    }

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
            RefdataValue.construct(map)
        }

        /*RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS,
            [(KBComponent.STATUS_CURRENT)  : '0',
             (KBComponent.STATUS_EXPECTED) : '1',
             (KBComponent.STATUS_RETIRED)  : '2',
             (KBComponent.STATUS_DELETED)  : '3'
            ]
        )

        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_EDIT_STATUS, KBComponent.EDIT_STATUS_APPROVED).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_EDIT_STATUS, KBComponent.EDIT_STATUS_IN_PROGRESS).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_EDIT_STATUS, KBComponent.EDIT_STATUS_REJECTED).save(flush: true, failOnError: true)


        RefdataCategory.lookupOrCreate(RCConstants.TIPP_ACCESS_TYPE, "Free").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_ACCESS_TYPE, "Paid").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_FORMAT, "Digitised").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_FORMAT, "Electronic").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_FORMAT, "Print").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_DELAYED_OA, "No").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_DELAYED_OA, "Unknown").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_DELAYED_OA, "Yes").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_HYBRIDA_OA, "No").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_HYBRIDA_OA, "Unknown").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_HYBRIDA_OA, "Yes").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PRIMARY, "Yes").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PRIMARY, "No").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "Complimentary").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "Limited Promotion").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "Paid").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "Opt Out Promotion").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "Uncharged").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_PAYMENT_TYPE, "Unknown").save(flush: true, failOnError: true)

        ['Database', 'Monograph', 'Other', 'Serial'].each { pubType ->
            RefdataCategory.lookupOrCreate(RCConstants.TIPP_PUBLICATION_TYPE, pubType).save(flush: true, failOnError: true)
        }

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_COVERAGE_DEPTH, "Fulltext").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_COVERAGE_DEPTH, "Selected Articles").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_COVERAGE_DEPTH, "Abstracts").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Selected Articles").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Abstracts").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_SCOPE, "Global").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_SCOPE, "Consortium").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_SCOPE, "National").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_SCOPE, "Individual").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_LIST_STATUS, "Checked").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_LIST_STATUS, "In Progress").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_BREAKABLE, "No").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_BREAKABLE, "Yes").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_BREAKABLE, "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONSISTENT, "No").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONSISTENT, "Yes").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONSISTENT, "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_FILE, "Back File").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_FILE, "Front File").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_FILE, "Master File").save(flush: true, failOnError: true)


        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_PAYMENT_TYPE, "Paid").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_PAYMENT_TYPE, "Mixed").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_PAYMENT_TYPE, "Uncharged").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_PAYMENT_TYPE, "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_LINK_TYPE, "Parent").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_LINK_TYPE, "Previous").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_GLOBAL, "Consortium").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_GLOBAL, "Regional").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_GLOBAL, "Global").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_GLOBAL, "Other").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONTENT_TYPE, "Mixed").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONTENT_TYPE, "Journal").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONTENT_TYPE, "Book").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONTENT_TYPE, "Database").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_CONTENT_TYPE, "Other").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "Blue OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "Gold OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "Green OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "Hybrid").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "Empty").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "White OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PACKAGE_OPEN_ACCESS, "Yellow OA").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_AUTH_METHOD, "IP").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_AUTH_METHOD, "Shibboleth").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_AUTH_METHOD, "User Password").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_AUTH_METHOD, "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_ROLE, "Admin").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_ROLE, "Host").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_SOFTWARE, "Atupon").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_SERVICE, "Highwire").save(flush: true, failOnError: true)

        ["A & I Database", "Audio", "Book", "Database", "Dataset", "Film", "Image", "Journal",
         "Other", "Published Score", "Article", "Software", "Statistics", "Market Data", "Standards",
         "Biography", "Legal Text", "Cartography", "Miscellaneous"].each { med ->
            RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_MEDIUM, med).save(flush: true, failOnError: true)
            RefdataCategory.lookupOrCreate(RCConstants.TIPP_MEDIUM, med).save(flush: true, failOnError: true)
        }

        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_OA_STATUS, "Unknown").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_OA_STATUS, "Full OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_OA_STATUS, "Hybrid OA").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_OA_STATUS, "No OA").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_PURE_OA, "Yes").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_PURE_OA, "No").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_PURE_OA, "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate("TitleInstance.ContinuingSeries", "Yes").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate("TitleInstance.ContinuingSeries", "No").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate("TitleInstance.ContinuingSeries", "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REASON_RETIRED, "Ceased").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REASON_RETIRED, "Paused").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_STATUS_REASON, "Xfer Out").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_STATUS_REASON, "Xfer In").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TIPP_LINK_TYPE, "Comes With").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_LINK_TYPE, "Parent").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TIPP_LINK_TYPE, "Previous").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Translated").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Absorbed").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "In Series").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Merged").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Renamed").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Split").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Supplement").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Transferred").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.TITLEINSTANCE_REL, "Unknown").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.ORG_MISSION, 'Academic').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_MISSION, 'Commercial').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_MISSION, 'Community Agency').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_MISSION, 'Consortium').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.USER_ORGANISATION_MISSION, 'Academic').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.USER_ORGANISATION_MISSION, 'Commercial').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.USER_ORGANISATION_MISSION, 'Community Agency').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.USER_ORGANISATION_MISSION, 'Consortium').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Org').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Package').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Title').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Book').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Journal').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Database').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE, 'Other').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Licensor').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Licensee').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Broker').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Vendor').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Content Provider').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Platform Provider').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Issuing Body').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Publisher').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ORG_ROLE, 'Imprint').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Afghanistan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Albania').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Algeria').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'American Samoa').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Andorra').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Angola').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Anguilla').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Antigua and Barbuda').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Argentina').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Armenia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Aruba').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Australia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Austria').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Azerbaijan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bahamas, The').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bahrain').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bangladesh').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Barbados').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Belarus').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Belgium').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Belize').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Benin').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bermuda').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bhutan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bolivia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bosnia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Botswana').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bougainville').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Brazil').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'British Indian Ocean').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'British Virgin Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Brunei').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Bulgaria').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Burkina Faso').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Burundi').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cambodia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cameroon').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Canada').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cape Verde Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cayman Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Central African Republic').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Chad').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Chile').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'China, Hong Kong').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'China, Macau').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'China, People’s Republic').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'China, Taiwan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Colombia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Comoros').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Congo, Democratic Republic of').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Congo, Republic of').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cook Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Costa Rica').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cote d’Ivoire').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Croatia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cuba').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Cyprus').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Czech Republic').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Denmark').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Djibouti').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Dominica').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Dominican Republic').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Ecuador').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Egypt').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'El Salvador').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Equatorial Guinea').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Eritrea').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Estonia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Ethiopia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Faeroe Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Falkland Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Federated States of Micronesia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Fiji').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Finland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'France').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'French Guiana').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'French Polynesia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Gabon').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Gambia, The').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Georgia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Germany').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Ghana').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Gibraltar').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Greece').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Greenland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Grenada').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Guadeloupe').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Guam').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Guatemala').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Guinea').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Guinea-Bissau').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Guyana').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Haiti').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Holy See (Vatican City State)').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Honduras').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Hungary').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Iceland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'India').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Indonesia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Iran').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Iraq').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Ireland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Israel').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Italy').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Jamaica').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Japan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Jordan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Kazakhstan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Kenya').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Kiribati').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Korea, Democratic People’s Rep').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Korea, Republic of').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Kosovo').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Kuwait').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Kyrgyzstan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Laos').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Latvia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Lebanon').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Lesotho').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Liberia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Libya').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Liechtenstein').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Lithuania').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Luxembourg').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Macedonia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Madagascar').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Malawi').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Malaysia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Maldives').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Mali').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Malta').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Martinique').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Mauritania').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Mauritius').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Mayotte').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Mexico').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Moldova').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Monaco').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Mongolia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Montenegro').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Montserrat').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Morocco Mozambique').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Myanmar').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Namibia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Nauru').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Nepal').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Netherlands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Netherlands Antilles').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'New Caledonia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'New Zealand').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Nicaragua').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Niger').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Nigeria').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Norway').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Oman').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Pakistan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Palestine').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Panama').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Papua New Guinea').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Paraguay').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Peru').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Philippines').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Poland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Portugal').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Puerto Rico').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Qatar').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Réunion').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Romania').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Russia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Rwanda').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Barthelemy').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Helena').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Kitts & Nevis').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Lucia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Martin').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Pierre & Miquelon').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saint Vincent').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Samoa').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'San Marino').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Sao Tomé & Principe').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Saudi Arabia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Senegal').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Serbia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Seychelles').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Sierra Leone').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Singapore').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Slovakia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Slovenia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Solomon Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Somalia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'South Africa').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Spain').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Sri Lanka').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Sudan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Suriname').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Swaziland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Sweden').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Switzerland').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Syria').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Tajikistan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Tanzania').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Thailand').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Timor Leste').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Togo').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Tokelau Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Tonga').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Trinidad & Tobago').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Tunisia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Turkey').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Turkmenistan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Turks & Caicos Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Tuvalu').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Uganda').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Ukraine').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'United Arab Emirates').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'United Kingdom of GB & NI').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'United States of America').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Uruguay').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'US Virgin Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Uzbekistan').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Vanuatu').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Venezuela').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Vietnam').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Wallis & Futuna Islands').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Yemen').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Zambia').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COUNTRY, 'Zimbabwe').save(flush: true, failOnError: true)
        //    RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, "Content Provider").save()
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, Combo.STATUS_DELETED).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, Combo.STATUS_SUPERSEDED).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, Combo.STATUS_EXPIRED).save(flush: true, failOnError: true)

        //    RefdataCategory.lookupOrCreate('ComboType','Unknown').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Previous').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Model').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Parent').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Translated').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Absorbed').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Merged').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Renamed').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Split').save()
        //    RefdataCategory.lookupOrCreate('ComboType','Transferred').save()

        RefdataCategory.lookupOrCreate(RCConstants.LICENSE_TYPE, 'Template').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.LICENSE_TYPE, 'Other').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE, 'Misspelling').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE, 'Authorized').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE, 'Acronym').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE, 'Minor Change').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE, 'Nickname').save(flush: true, failOnError: true)


        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'en_US').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'en_GB').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'en').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'de').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'es').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'fr').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'it').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'ru').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'pt').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL, 'la').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_STATUS, KBComponent.STATUS_CURRENT).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_STATUS, KBComponent.STATUS_DELETED).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_STATUS, KBComponent.STATUS_EXPECTED).save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_STATUS, KBComponent.STATUS_RETIRED).save(flush: true, failOnError: true)

        // Review Request
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Open').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Closed').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Deleted').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.ALLOCATED_REVIEW_GROUP_STATUS, 'Claimed').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ALLOCATED_REVIEW_GROUP_STATUS, 'In Progress').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Minor Identifier Mismatch').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Major Identifier Mismatch').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Multiple Matches').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Type Mismatch').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Name Mismatch').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Name Similarity').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Namespace Mismatch').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Platform Noncurrent').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'New Platform').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'New Org').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Status Deleted').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Status Retired').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'TIPPs Retired').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Invalid TIPPs').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Removed Identifier').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STD_DESC, 'Ambiguous Matches').save(flush: true, failOnError: true)


        RefdataCategory.lookupOrCreate(RCConstants.ACTIVITY_STATUS, 'Active').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ACTIVITY_STATUS, 'Complete').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.ACTIVITY_STATUS, 'Abandoned').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.ACTIVITY_TYPE, 'TitleTransfer').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.YN, 'Yes').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.YN, 'No').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.DC_TYPE, 'Admin', "100").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.DC_TYPE, 'Standard', "200").save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.DC_TYPE, 'Support', "300").save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.LICENSE_CATEGORY, 'Content').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.LICENSE_CATEGORY, 'Software').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_DATA_SUPPLY_METHOD, 'eMail').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_DATA_SUPPLY_METHOD, 'HTTP Url').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_DATA_SUPPLY_METHOD, 'FTP').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_DATA_SUPPLY_METHOD, 'Other').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_DATA_FORMAT, 'KBART').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_DATA_FORMAT, 'Proprietary').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_FREQUENCY, 'Daily').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_FREQUENCY, 'Weekly').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_FREQUENCY, 'Monthly').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_FREQUENCY, 'Quarterly').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.SOURCE_FREQUENCY, 'Yearly').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.RDFDATA_TYPE, 'uri').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.RDFDATA_TYPE, 'string').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.INGEST_FILE_TYPE, 'kbart2').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.INGEST_FILE_TYPE, 'ingram').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.INGEST_FILE_TYPE, 'ybp').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.INGEST_FILE_TYPE, 'cufts').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_AUTHENTICATION, 'Unknown').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PLATFORM_ROLES, 'Host').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'KBComponent.Ids').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'KBComponent.FileAttachments').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Tipps').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Tipls').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Publisher').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Issuer').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Imprint').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.TranslatedFrom').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.AbsorbedBy').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.MergedWith').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.RenamedTo').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.SplitFrom').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstancePackagePlatform.DerivedFrom').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstancePackagePlatform.MasterTipp').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.CuratoryGroups').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.HostedTipps').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.HostedTitles').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.Provider').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Office.Org').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Office.CuratoryGroups').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Org.Imprint').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Org.Previous').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Org.Parent').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Org.OwnedImprints').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Org.CuratoryGroups').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Org.Imprint').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Provider').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Tipps').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.CuratoryGroups').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.NominalPlatform').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Previous').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Parent').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Vendor').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Broker').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Licensor').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'License.Licensee').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'IngestionProfile.Source').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Source.CuratoryGroups').save(flush: true, failOnError: true)


        RefdataCategory.lookupOrCreate(RCConstants.MEMBERSHIP_ROLE, 'Administrator').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.MEMBERSHIP_ROLE, 'Member').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.MEMBERSHIP_STATUS, 'Approved').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.MEMBERSHIP_STATUS, 'Pending').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.MEMBERSHIP_STATUS, 'Rejected/Revoked').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.PRICE_TYPE, 'list').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PRICE_TYPE, 'perpetual').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PRICE_TYPE, 'topup').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PRICE_TYPE, 'on-off').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.PRICE_TYPE, 'subscription').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.CURRENCY, 'EUR').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.CURRENCY, 'GBP').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.CURRENCY, 'USD').save(flush: true, failOnError: true)

        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'Unknown').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'Undefined').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'PackageCrossRef').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TitleCrossRef').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'BootstrapIdentifierCleanup').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'DepositDatafile').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RegenerateLicenseSummaries').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TidyOrgsData').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnsureUUIDs').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnsureTIPLs').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'GenerateTIPPCoverage').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'MarkInconsDateRanges').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'UpdateFreeTextIndexes').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'ResetFreeTextIndexes').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'MasterListUpdate').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnrichmentService').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'GeneratePackageTypes').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'Housekeeping').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'CleanupDeletedComponents').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'CleanupRejectedComponents').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TIPPCleanup').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'IdentifierCleanup').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'DeleteTIWithoutHistory').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RejectTIWithoutIdentifier').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'PlatformCleanup').save(flush: true, failOnError: true)
        RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RecalculateStatistics').save(flush: true, failOnError: true)*/

        log.debug("Deleting any null refdata values")
        RefdataValue.executeUpdate('delete from RefdataValue where value is null')

        LanguagesService.initialize()
    }

    def sourceObjects() {
        log.debug("Lookup or create source objects")
        def ybp_source = Source.findByName('YBP') ?: new Source(name: 'YBP').save(flush: true, failOnError: true)
        def cup_source = Source.findByName('CUP') ?: new Source(name: 'CUP').save(flush: true, failOnError: true)
        def wiley_source = Source.findByName('WILEY') ?: new Source(name: 'WILEY').save(flush: true, failOnError: true)
        def cufts_source = Source.findByName('CUFTS') ?: new Source(name: 'CUFTS').save(flush: true, failOnError: true)
        def askews_source = Source.findByName('ASKEWS') ?: new Source(name: 'ASKEWS').save(flush: true, failOnError: true)
        def ebsco_source = Source.findByName('EBSCO') ?: new Source(name: 'EBSCO').save(flush: true, failOnError: true)
    }


    def DSConfig() {
        log.info("DSConfig()")
        [
                'accessdl': 'Access - Download',
                'accessol': 'Access - Read Online',
                'accbildl': 'Accessibility - Download',
                'accbilol': 'Accessibility - Read Online',
                'device'  : 'Device Requirements for Download',
                'drm'     : 'DRM',
                'format'  : 'Format',
                'lic'     : 'Licensing',
                'other'   : 'Other',
                'ref'     : 'Referencing',
        ].each { k, v ->
            def dscat = DSCategory.findByCode(k) ?: new DSCategory(code: k, description: v).save(flush: true, failOnError: true)
        }

        [
                ['format', 'Downloadable PDF', '', ''],
                ['format', 'Embedded PDF', '', ''],
                ['format', 'ePub', '', ''],
                ['format', 'OeB', '', ''],
                ['accessol', 'Book Navigation', '', ''],
                ['accessol', 'Table of contents navigation', '', ''],
                ['accessol', 'Pagination', '', ''],
                ['accessol', 'Page Search', '', ''],
                ['accessol', 'Search Within Book', '', ''],
                ['accessdl', 'Download Extent', '', ''],
                ['accessdl', 'Download Time', '', ''],
                ['accessdl', 'Download Reading View Navigation', '', ''],
                ['accessdl', 'Table of Contents Navigation', '', ''],
                ['accessdl', 'Pagination', '', ''],
                ['accessdl', 'Page Search', '', ''],
                ['accessdl', 'Search Within Book', '', ''],
                ['accessdl', 'Read Aloud or Listen Option', '', ''],
                ['device', 'General', '', ''],
                ['device', 'Android', '', ''],
                ['device', 'iOS', '', ''],
                ['device', 'Kindle Fire', '', ''],
                ['device', 'PC', '', ''],
                ['drm', 'Copying', '', ''],
                ['drm', 'Printing', '', ''],
                ['accbilol', 'Dictionary', '', ''],
                ['accbilol', 'Text Resize', '', ''],
                ['accbilol', 'Change Reading Colour', '', ''],
                ['accbilol', 'Read aloud or Listen Option', '', ''],
                ['accbilol', 'Integrated Help', '', ''],
                ['accbildl', 'Copying', '', ''],
                ['accbildl', 'Printing', '', ''],
                ['accbildl', 'Add Notes', '', ''],
                ['accbildl', 'Dictionary', '', ''],
                ['accbildl', 'Text Resize', '', ''],
                ['accbildl', 'Change Reading Colour', '', ''],
                ['accbildl', 'Integrated Help', '', ''],
                ['accbildl', 'Other Accessibility features or Support', '', ''],
                ['ref', 'Export to bibliographic software', '', ''],
                ['ref', 'Sharing / Social Media', '', ''],
                ['other', 'Changes / Redevelopment in the near future', '', ''],
                ['lic', 'Number of users', '', ''],
                ['lic', 'Credit Payment Model', '', ''],
                ['lic', 'Publishers Included', '', '']
        ].each { crit ->
            def cat = DSCategory.findByCode(crit[0]);
            if (cat) {
                def c = DSCriterion.findByOwnerAndTitle(cat, crit[1]) ?: new DSCriterion(
                        owner: cat,
                        title: crit[1],
                        description: crit[2],
                        explanation: crit[3]).save(flush: true, failOnError: true)
            } else {
                log.error("Unable to locate category: ${crit[0]}")
            }
        }
        //log.debug(titleLookupService.getTitleFieldForIdentifier([[ns:'isbn',value:'9780195090017']],'publishedFrom'));
        //log.debug(titleLookupService.getTitleFieldForIdentifier([[ns:'isbn',value:'9780195090017']],'publishedTo'));
    }


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
            ensureEsIndex(indexName)
        }
    }


    def ensureEsIndex(String indexName) {
        log.info("ensureESIndex for ${indexName}");
        def esclient = ESWrapperService.getClient()
        IndicesAdminClient adminClient = esclient.admin().indices()

        if (!adminClient.prepareExists(indexName).execute().actionGet().isExists()) {
            log.debug("ES index ${indexName} did not exist, creating..")

            CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(indexName)

            log.debug("Adding index settings..")
            createIndexRequestBuilder.setSettings(ESWrapperService.getSettings().get("settings"))
            log.debug("Adding index mappings..")
            createIndexRequestBuilder.addMapping("component", ESWrapperService.getMapping())

            CreateIndexResponse indexResponse = createIndexRequestBuilder.execute().actionGet()

            if (indexResponse.isAcknowledged()) {
                log.debug("Index ${indexName} successfully created!")
            } else {
                log.debug("Index creation failed: ${indexResponse}")
            }
        } else {
            log.debug("ES index ${indexName} already exists..")
            // Validate settings & mappings
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
