package org.gokb


import de.wekb.helper.RCConstants
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.annotation.Secured
import groovy.json.JsonSlurper
import org.springframework.web.servlet.support.RequestContextUtils
import org.gokb.cred.*
import au.com.bytecode.opencsv.CSVReader
import com.k_int.ClassUtils
import com.k_int.ConcurrencyManagerService.Job
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.concurrent.CancellationException

import groovy.util.logging.*

@Slf4j
class IntegrationController {

  def springSecurityService
  def concurrencyManagerService
  def sessionFactory
  def crossReferenceService

  @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def index() {
  }

  /*@Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def assertJsonldPlatform() {
    def result = [result: 'OK']
    def name = request.JSON.'skos:prefLabel'
    def normname = GOKbTextUtils.norm2(name)
    def located_entries = KBComponent.findAllByNormname(normname)
    log.debug("assertJsonldPlatform ${name}/${normname}");
    if (located_entries.size() == 0) {
      log.debug("No platform with normname ${normname} - create");
      def new_platform = new org.gokb.cred.Platform(name: name, normname: normname).save()
      result.message = "Added new platform"
    }
    else {
      result.message = "Entity with that name already exists.."
    }
    render result as JSON
  }

  @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def assertGroup() {
    def result = [result: 'OK']
    def name = request.JSON.name
    def normname = CuratoryGroup.generateNormname(name)
    def user = springSecurityService.currentUser
    def uuid = request.JSON.uuid
    def group = uuid ? CuratoryGroup.findByUuid(uuid) : null
    def status = request.JSON.status

    if (!group) {
      group = CuratoryGroup.findByNormname(normname)
    }
    else {
      result.message = "Group ${group.name} matched by uuid!"
    }

    if (!group) {
      group = new CuratoryGroup(name: name, uuid: uuid)

      if (group.validate()) {
        group.save(flush: true)
        result.message = "Created new group ${name}!"
      }
      else {
        result.message = "Could not reference group ${name}"
        result.errors = group.errors
        result.result = 'ERROR'

        render result as JSON
      }
    }
    else {
      result.message = "Group ${group.name} matched by name!"
    }

    // Defaults first.
    componentUpdateService.ensureCoreData(group, request.JSON, false, user)

    // Find by username but do not create missing entries.
    def owner = request.JSON.owner
    if (owner) {
      group.owner = User.findByUsername(owner)
    }

    // Need to add all users to the group.
    def memberNames = request.JSON.users
    if (memberNames) {
      def members = User.createCriteria().list {
        'in'('username', memberNames)
      }

      members.each {
        group.addToUsers(it)
      }
    }

    if (group) {
      group.save(flush: true)
      result.groupId = group.id
    }
    else {
      result.message = "Could not reference group ${name}"
      result.result = 'ERROR'
    }

    render result as JSON
  }*/

/*  @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def assertJsonldOrg() {
    // log.debug("assertOrg, request.json = ${request.JSON}");
    def result = [:]
    def user = springSecurityService.currentUser
    result.status = true;

    try {

      def name = request.JSON.'skos:prefLabel'

      if ((name != null) && (name.trim().length() > 0)) {

        log.debug("Trying to locate component with ID ${request.JSON.'@id'} name is \"${name}\"");

        // Try and match on primary ID
        def located_entries = KBComponent.lookupByIdentifierValue([request.JSON.'@id'.toString()] as String[]);

        if (located_entries?.size() == 1) {
          log.debug("Identified record..");
          enrichJsonLDOrg(located_entries[0], request.JSON)
        }
        else if (located_entries?.size() == 0) {

          log.debug("Not identified - try sameAs relations");

          if (request.JSON.'owl:sameAs' != null) {
            log.debug("Attempt lookup by sameAs : ${request.JSON.'owl:sameAs' as String[]} ");
            located_entries = KBComponent.lookupByIdentifierValue((request.JSON.'owl:sameAs') as String[])
          }
          else {
            log.debug("No owl:sameAs entries found");
          }

          if (located_entries?.size() == 0) {
            log.debug("Failed to match on same-as. Attempting primary name match");
            def normname = GOKbTextUtils.norm2(name)
            located_entries = KBComponent.findAllByNormname(normname)
            if (located_entries?.size() == 0) {
              log.debug("No match on normalised name ${normname}.. Trying variant names");
              def variant_normname = GOKbTextUtils.normaliseString(name)
              def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
              located_entries = Org.executeQuery("select distinct o from Org as o join o.variantNames as v where v.normVariantName = ? and o.status <> ?", [variant_normname, status_deleted]);

              if (located_entries?.size() == 0) {

                createJsonLDOrg(request.JSON);
              }
              else if (located_entries?.size() == 1) {
                log.debug("Exact match on normalised variantname ${variant_normname} - good enough");
                enrichJsonLDOrg(located_entries[0], request.JSON)
              }
              else {
                log.error("Multiple matches on normalised variant name... abandon all hope");
              }
            }
            else if (located_entries?.size() == 1) {
              log.debug("Exact match on normalised name ${normname} - good enough");
              enrichJsonLDOrg(located_entries[0], request.JSON)
            }
            else {
              log.error("Multiple matches on normalised name... abandon all hope");
            }

          }
          else if (located_entries?.size() == 1) {
            log.debug("Located identifier");
          }
          else {
            log.error("set of SameAs identifiers locate more that one component");
          }
        }
        else {
          log.error("Unique identifier finds multiple components.");
        }

        result.status = 'OK'
      }
      else {
        log.error("skipping org [ ${request.JSON.'@id'}] due to null name");
      }
    }
    catch (Exception e) {
      log.error("Problem", e)
      result.status = 'ERROR'
    }
    finally {
    }

    render result as JSON
  }

  @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def createJsonLDOrg(ldjsonorg) {
    log.debug("createJsonLDOrg");
    //             "@id": "http://www.lib.ncsu.edu/ld/onld/00000134" ,
    //         "skos:prefLabel": "A.B. Lundequistska Bokhandeln" ,
    //         "owl:sameAs": [
    //             "http://viaf.org/viaf/152447102" ,
    //             "http://isni-url.oclc.nl/isni/0000000102215732" ,
    //             "http://id.loc.gov/authorities/names/n80148304"
    //         ] ,
    def name = request.JSON.'skos:prefLabel'
    def id = request.JSON.'@id'
    def new_org = new Org(name: name)


    new_org.ids.add(primary_identifier)

    request.JSON.'owl:sameAs'?.each { said ->

      // Double check that this identifier is NOT already used
      def existing_usage = KBComponent.lookupByIO('global', said)
      if (existing_usage == null) {

        new_org.ids.add(identifier)
      }
      else {
        log.error("Not adding identifer to a second item...");
      }
    }

    new_org.save();

    request.JSON.'skos:altLabel'?.each { al ->
      println("checking alt label ${al}");
      new_org.ensureVariantName(al);
    }

    new_org.save();

    if (request.JSON.'foaf:homepage' != null) {
      new_org.homepage = request.JSON.'foaf:homepage'
    }

    if (new_org.save(flush: true, failOnError: true)) {
      log.debug("Saved ok");
    }
    else {
      log.error("Problem saving new org. ${new_org.errors}");
    }
  }

  @Secured(value = ["hasRole('ROLE_USER')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def enrichJsonLDOrg(org, jsonld) {
    log.debug("Enrich existing..");
    request.JSON.'skos:altLabel'?.each { al ->
      println("checking alt label ${al}");
      org.ensureVariantName(al);
    }

  }*/

  /**
   *  assertOrg()
   *  allow an authorized external component to send in a JSON structure following this template:
   *      [
   *         name:National Association of Corrosion Engineers,
   *         description:National Association of Corrosion Engineers,
   *         parent:
   *         customIdentifiers:[[identifierType:"idtype", identifierValue:"value"]],
   *         combos:[[linkTo:[identifierType:"ncsu-internal", identifierValue:"ncsu:61929"], linkType:"HasParent"]],
   *         flags:[[flagType:"Org Role", flagValue:"Content Provider"],
   *                [flagType:"Org Role", flagValue:"Publisher"],
   *                [flagType:"Authorized", flagValue:"N"]]
   *      ]
   *
   */
/*  @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def assertOrg() {
    log.debug("assertOrg, request.json = ${request.JSON}");
    def result = [result: 'OK']
    def user = springSecurityService.currentUser
    def assert_errors = false;
    def jsonOrg = request.JSON

    try {
      def located_or_new_org = resolveOrgUsingPrivateIdentifiers(jsonOrg.identifiers)

      if (located_or_new_org == null) {
        if (jsonOrg.name) {
          String orgName = jsonOrg.name
          String orgNormName = Org.generateNormname(orgName)

          // No match. One more attempt to match on norm_name only.
          def org_by_name = Org.findAllByNormname(orgNormName)
          def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

          if (org_by_name.size() == 1) {
            located_or_new_org = org_by_name[0]
          }

          if (located_or_new_org == null && org_by_name.size() == 0) {

            def variant_normname = GOKbTextUtils.normaliseString(orgName)
            def candidate_orgs = Org.executeQuery("select distinct o from Org as o join o.variantNames as v where v.normVariantName = ? and o.status <> ?", [variant_normname, status_deleted]);

            if (candidate_orgs.size() == 1) {
              located_or_new_org = candidate_orgs[0]

              log.debug("Matched Org on variant name!");
            }
            else if (candidate_orgs.size() == 0) {

              log.debug("Create new org name will be \"${jsonOrg.name}\" (${jsonOrg.name?.length()})");

              located_or_new_org = new Org(name: jsonOrg.name, normname: orgNormName, uuid: jsonOrg.uuid?.trim()?.size() > 0 ? jsonOrg.uuid : null)

              log.debug("Attempt to save - validate: ${located_or_new_org}");

              if (located_or_new_org.save(flush: true, failOnError: true)) {
                log.debug("Saved ok");
              }
              else {
                assert_errors = true;
              }
            }
            else {
              log.debug("Multiple matches via variant name, skipping Org!");
              assert_errors = true;
            }
          }
          else if (org_by_name.size == 1) {
            log.debug("Matched Org by normname!")
          }
          else {
            log.debug("Multiple matches for org via normname!")
            assert_errors = true;
          }
        }
        else {
          log.warn("Provided Org has no name!");
          assert_errors = true;
        }
      }
      else {
        log.debug("Located existing record.. Still update...");
      }

      if (assert_errors) {
        log.debug("Save failed ${located_or_new_org}");
        result.errors = []
        located_or_new_org.errors.each { e ->
          log.error("Problem saving new org record", e);
          result.errors.add("${e}".toString());
        }
        result.result = 'ERROR'
        response.setStatus(400)
        result.message = "There was a problem saving the new Org!"
        result.baddata = jsonOrg
        return
      }

      componentUpdateService.setAllRefdata([
          'software', 'service'
      ], jsonOrg, located_or_new_org)

      if (jsonOrg.mission) {
        log.debug("Mission ${jsonOrg.mission}");
        located_or_new_org.mission = RefdataCategory.lookup(RCConstants.ORG_MISSION, jsonOrg.mission);
      }

      if (jsonOrg.homepage) {
        located_or_new_org.homepage = jsonOrg.homepage
      }

      // Add parent.
      if (jsonOrg.parent) {
        def parentDef = jsonOrg.parent;
        log.debug("Adding parent using ${parentDef.identifierType}:${parentDef.identifierValue}");
        def located_component = KBComponent.lookupByIO(parentDef.identifierType, parentDef.identifierValue)
        if (located_component) {
          located_or_new_org.parent = located_component
        }
      }

      log.debug("Combo processing: ${jsonOrg.combos}")

      // combos
      jsonOrg.combos.each { c ->
        log.debug("lookup to item using ${c.linkTo.identifierType}:${c.linkTo.identifierValue}");
        def located_component = KBComponent.lookupByIO(c.linkTo.identifierType, c.linkTo.identifierValue)

        // Located a component.
        if ((located_component != null)) {
          def combo = new Combo(
              type: RefdataCategory.lookup(RCConstants.COMBO_TYPE, c.linkType),
              fromComponent: located_or_new_org,
              toComponent: located_component,
              startDate: new Date()).save(flush: true, failOnError: true);
        }
        else {
          log.error("Problem resolving from(${located_or_new_org}) or to(${located_component}) org for combo");
        }
      }

      // roles
      log.debug("Role Processing: ${jsonOrg.roles}");
      jsonOrg.roles.each { r ->
        log.debug("Adding role ${r}");
        def role = RefdataCategory.lookup(RCConstants.ORG_ROLE, r)

        if (role) {
          located_or_new_org.addToRoles(role)
        }
      }

      // Core data...
      componentUpdateService.ensureCoreData(located_or_new_org, jsonOrg, false, user)

      log.debug("Attempt to save - validate: ${located_or_new_org}");

      if (located_or_new_org.save(flush: true, failOnError: true)) {
        log.debug("Saved ok");
        result.message = "Added/Updated org: ${located_or_new_org.id} ${located_or_new_org.name}";
        result.orgId = located_or_new_org.id
      }
      else {
        log.debug("Save failed ${located_or_new_org}");
        result.errors = []
        located_or_new_org.errors.each { e ->
          log.error("Problem saving new org record", e);
          result.errors.add("${e}".toString());
        }
        response.setStatus(400)
        result.baddata = jsonOrg
        result.result = 'ERROR'
        result.message = "There was a problem saving the Org!"
      }

    }
    catch (Exception e) {
      log.error("Unexpected error importing org", e)
      result.result = 'ERROR'
      result.message = "Unexpected error importing the Org!"
      result.baddata = jsonOrg
      response.setStatus(500)
    }
    render result as JSON
  }*/

  /**
   *
   *  assertSource()
   *  allow an authorized external component to send in a JSON structure following this template:
   *      [
   *         name:'',
   *         shortcode:'',
   *         url:'',
   *         defaultAccessURL:'',
   *         explanationAtSource:'',
   *         contextualNotes:'',
   *         frequency:'',
   *         ruleset:'',
   *         defaultSupplyMethod:'',
   *         defaultDataFormat:''
   *      ]
   */
  /*@Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def assertSource() {
    createOrUpdateSource(request.JSON)
  }

  private static def createOrUpdateSource(data) {
    log.debug("assertSource, data = ${data}");
    def result = [:]
    def source_data = data;
    result.status = true;

    try {
      if (data.name) {

        Source.withNewSession {
          def located_or_new_source = Source.findByNormname(Source.generateNormname(data.name)) ?: new Source(name: data.name).save(flush: true, failOnError: true)

          ClassUtils.setStringIfDifferent(located_or_new_source, 'url', data.url)
          ClassUtils.setStringIfDifferent(located_or_new_source, 'defaultAccessURL', data.defaultAccessURL)
          ClassUtils.setStringIfDifferent(located_or_new_source, 'explanationAtSource', data.explanationAtSource)
          ClassUtils.setStringIfDifferent(located_or_new_source, 'contextualNotes', data.contextualNotes)
          ClassUtils.setStringIfDifferent(located_or_new_source, 'frequency', data.frequency)
          ClassUtils.setStringIfDifferent(located_or_new_source, 'ruleset', data.ruleset)

          componentUpdateService.setAllRefdata([
              'software', 'service'
          ], source_data, located_or_new_source)

          ClassUtils.setRefdataIfPresent(data.defaultSupplyMethod, located_or_new_source, 'defaultSupplyMethod', RCConstants.SOURCE_DATA_SUPPLY_METHOD)
          ClassUtils.setRefdataIfPresent(data.defaultDataFormat, located_or_new_source, 'defaultDataFormat', RCConstants.SOURCE_DATA_FORMAT)

          log.debug("Variant names processing: ${data.variantNames}")

          // variants
          data.variantNames.each { vn ->
            addVariantNameToComponent(located_or_new_source, vn)
          }

          result['component'] = located_or_new_source
        }
      }
    }
    catch (Exception e) {
      e.printStackTrace()
    }
    result
  }*/


 /* @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  @Transactional(readOnly = true)
  private resolveOrgUsingPrivateIdentifiers(idlist) {
    def located_or_new_org = null;

    if (idlist?.size() ?: 0 > 0) {
      // Rewritten to perform this as a singel query.
      def crit = Org.createCriteria()
      def matched_orgs = crit.list {

        createAlias('outgoingCombos', 'ogc')
        createAlias('ogc.type', 'ogcType')
        createAlias('ogcType.owner', 'ogcOwner')

        createAlias('ogc.toComponent', 'tc')
        createAlias('tc.namespace', 'tcNamespace')

        and {
          and {
            eq 'ogcOwner.desc', RCConstants.COMBO_TYPE
            eq 'ogcType.value', 'KBComponent.Ids'
          }
          or {
            for (def ci : idlist) {
              and {
                eq 'tc.value', ci.identifierValue
                eq 'tcNamespace.value', ci.identifierType
              }
            }
          }
        }

        projections {
          distinct 'id'
        }
      }

      switch (matched_orgs.size()) {
        case 0:
          log.debug("No match for ${idlist}.")
          break
        case 1:
          log.debug("Found single component ID: ${matched_orgs}")
          // Matched one only! This is correct.
          located_or_new_org = Org.read(matched_orgs[0])
          break
        case { it > 1 }:
          log.error("**CONFLICT**")
          log.error("Identifiers ${idlist} matched multiple component IDs ${matched_orgs}!")
          break

      }
    }

    // See if we can locate the item using any of the custom identifiers

    located_or_new_org
  }*/

//  @Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
//  private resolveOrgUsingPrivateIdentifiers(idlist) {
//    def located_or_new_org = null;
//
//    // See if we can locate the item using any of the custom identifiers
//    idlist.each { ci ->
//
//      log.debug("Attempt lookup of ${ci.identifierType}:${ci.identifierValue}");
//      if ( located_or_new_org ) {
//        // We've already located an org for this identifier, the new identifier should be new (And therefore added to this org) or
//        // resolve to this org. If it resolves to some other org, then there is a conflict and we fail!
//        def located_component = KBComponent.lookupByIO(ci.identifierType,ci.identifierValue)
//        if ( located_component ) {
//          log.debug("Matched something...");
//          if ( !located_or_new_org ) {
//            located_or_new_org = located_component
//          }
//          else {
//            if ( located_component.id == located_or_new_org.id ) {
//              log.debug("Matched an identifier");
//            }
//            else {
//              log.error("**CONFLICT**");
//            }
//          }
//        }
//        else {
//          // No match.. candidate identifier
//          log.debug("No match for ${ci.identifierType}:${ci.identifierValue}");
//        }
//      }
//      else {
//        located_or_new_org = KBComponent.lookupByIO(ci.identifierType,ci.identifierValue)
//      }
//    }
//    located_or_new_org
//  }

/*  @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def registerVariantName() {
    log.debug("registerVariantName ${params} ${request.JSON}")

    // See if we can locate the variant name as a first class component

    Org variant_org = null;
    if (request.JSON.variantidns != null && request.JSON.variantidvalue != null) {
      variant_org = Org.lookupByIO(request.JSON.variantidns, request.JSON.variantidvalue)
      log.debug("Existing variant org[${request.JSON.variantidns}:${request.JSON.variantidvalue}]: ${variant_org}")
    }

    Org org_to_update = Org.lookupByIO(request.JSON.idns, request.JSON.idvalue)
    log.debug("Org to update[${request.JSON.idns}:${request.JSON.idvalue}]: ${org_to_update}")

    // Update any combos that point to the variant so that they now point to the authorized entry

    // Delete any remaining variant org combox
    // Delete the variant org

    render addVariantNameToComponent(org_to_update, request.JSON.name)
  }*/

  private static addVariantNameToComponent(KBComponent component, variant_name) {
    component.ensureVariantName(variant_name)
  }

  def crossReferencePackage() {
    def result = ['result': 'OK']
    def async = params.async ? params.boolean('async') : false
    def addOnly = params.addOnly ? params.boolean('addOnly') : false
    def request_locale = RequestContextUtils.getLocale(request)
    def rjson = request.JSON
    def cancelled = false
    UpdateToken updateToken = null
    User request_user = null
    def fullsync = false
    def token = null

    log.debug("crossReferencePackage (${request_locale})")

    if (springSecurityService.isLoggedIn()) {
      request_user = springSecurityService.currentUser
    }
    else if (params.user && params.password) {
      request_user = springSecurityService.reauthenticate(params.user, params.password)
    }
    else if (params.updateToken?.trim() || rjson.updateToken?.trim()) {
      token = params.updateToken ?: rjson.updateToken
      updateToken = UpdateToken.findByValue(token)

      if (updateToken) {
        request_user = updateToken.updateUser

        if (rjson.packageHeader) {
          rjson.packageHeader.uuid = updateToken.pkg.uuid
        }
      }
      else {
        log.error("Unable to reference update token!")
        result.message = "Unable to reference update token!"
        response.setStatus(400)
        result.result = "ERROR"
      }
    }
    else {
      response.setStatus(401)
      response.setHeader('WWW-Authenticate', 'Basic realm="gokb"')
    }

    if (params.fullsync == "true" && request_user?.adminStatus) {
      fullsync = true
    }

    if (!async) {
      result = crossReferenceService.xRefPkg(rjson,
          addOnly as boolean, fullsync as boolean, token != null,
          request_locale, request_user)
      log.debug("xRefPkg Result:\n$result")
    }
    else {
      // start xRef Job
      Job background_job = concurrencyManagerService.createJob { Job job ->
        crossReferenceService.xRefPkg(rjson, addOnly as boolean, fullsync as boolean,
            token != null, request_locale, request_user, job)
      }
      log.debug("Starting job ${background_job}..")
      background_job.description = "Package CrossRef (${rjson.packageHeader.name})"
      background_job.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'PackageCrossRef')
      background_job.linkedItem = [name: rjson.packageHeader.name,
                                   type: "Package"]
      background_job.message("Starting upsert for Package ${rjson.packageHeader.name}")
      background_job.startOrQueue()
      background_job.startTime = new Date()
      result << [job_id: background_job.uuid,
                 // TODO: remove key 'info' as it is deprecated
                 info  : [job_id: background_job.uuid]]
    }
    render result as JSON
  }

 /* @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  @Transactional
  def crossReferencePlatform() {
    def result = ['result': 'OK']
    def created = false
    def platformJson = request.JSON
    def fullsync = false
    User user = springSecurityService.currentUser

    if (params.fullsync == "true" && user.adminStatus) {
      fullsync = true
    }

    log.debug("crossReferencePlatform - ${platformJson}")
    if (platformJson?.platformUrl?.trim() && (platformJson?.name?.trim() || platformJson?.platformName?.trim())) {
      if (!platformJson.name?.trim() && platformJson.platformName) {
        platformJson.name = platformJson.platformName
      }

      try {
        def p = Platform.upsertDTO(platformJson)

        if (p) {
          log.debug("created or looked up platform ${p}!")

          if (platformJson.provider) {
            def prov = null

            if (platformJson.provider instanceof String) {
              prov = Org.findByNormname(Org.generateNormname(platformJson.provider))
            }
            else {
              if (platformJson.provider.uuid) {
                prov = Org.findByUuid(platformJson.provider.uuid)
              }

              if (!prov && platformJson.provider.name) {
                prov = Org.findByNormname(Org.generateNormname(platformJson.provider.name))
              }
            }

            if (prov) {
              log.debug("Adding Provider ${prov} to platform ${p}!")
              p.provider = prov
            }
            else {
              log.debug("No provider found for ${platformJson.provider}!")
            }
          }
          p.save(flush: true)

          // Add the core data.
          componentUpdateService.ensureCoreData(p, platformJson, fullsync, user)

          //      if ( changed ) {
          //        p.save(flush:true, failOnError:true);
          //      }
          result.message = "Created/Updated platform ${p}"

          result.platformId = p.id;
        }
        else {
          log.debug("No platform matched for ${platformJson}")
          result.message = "Could not crossreference platform ${platformJson}"
          response.setStatus(500)
          result.result = 'ERROR'
        }
      }
      catch (Exception e) {
        log.debug("Exception while looking up platform!", e)
        result.message = "There was an error looking up platform ${platformJson}"
        response.setStatus(500)
        result.result = "ERROR"
      }
    }
    else {
      log.debug("Missing Platform info for ${platformJson}")
      result.message = "Platform ${platformJson} is missing required information ('name' or 'platformUrl')!"
      response.setStatus(400)
      result.result = "ERROR"
    }
    render result as JSON
  }*/

  private static boolean setAllRefdata(propNames, data, target, boolean createNew = false) {
    boolean changed = false
    propNames.each { String prop ->
      changed |= ClassUtils.setRefdataIfPresent(data[prop], target, prop, createNew)
    }
    changed
  }

/**
 *  Cross reference an incoming title with the database. See an example of calling this controller method
 *  in GOKB_PROJECT slash scripts slash sync_gokb_titles.groovy
 *
 *  Cross reference record::
 *
 *{*    'title':'the_title',
 *    'publisher':'the_publisher',
 *    'identifiers':[
 *{type:'namespace',value:'value'},
 *{type:'isbn', value:'1234-5678'}*    ]
 *    'type':'Serial'|'Monograph',
 *    'variantNames':[
 *      'Array Of Strings - one for each variant name'
 *    ],
 *    'imprint':'the_publisher',
 *    'publishedFrom':'yyyy-MM-dd' 'HH:mm:ss.SSS',
 *    'publishedTo':'yyyy-MM-dd' 'HH:mm:ss.SSS',
 *    'status':'status_value',
 *    'historyEvents':[
 *    ],
 *    'series':'series_name',
 *    'subjectArea':'subject_area_name',
 *    'prices':[
 *{*       'type':'list',
 *       'currency':'EUR',
 *       'amount':12.89
 *}*    ]
 *}*/
 /* @Secured(value = ["hasRole('ROLE_API')", 'IS_AUTHENTICATED_FULLY'], httpMethod = 'POST')
  def crossReferenceTitle() {
    User user = springSecurityService.currentUser
    def rjson = request.JSON
    def cancelled = false
    def async = params.async ? params.boolean('async') : false
    def request_locale = RequestContextUtils.getLocale(request)
    def fullsync = false
    def result

    if (params.fullsync == "true" && user.adminStatus) {
      fullsync = true
    }

    if (org.grails.web.json.JSONArray != rjson.getClass()) {

      result = crossReferenceSingleTitle(rjson, user.id, fullsync, request_locale)

      cleanUpGorm()
    }
    else {
      log.debug("Starting crossReferenceTitle Job")
      Job background_job = concurrencyManagerService.createJob { Job job ->
        def json = rjson
        def locale = request_locale
        def job_result = [:]
        def ctr = 0

        job_result.results = []

        for (e in json) {

          if (Thread.currentThread().isInterrupted()) {
            log.debug("Job cancelling ..")
            cancelled = true
            job.endTime = new Date()
            job_result.status = "cancelled"
            break;
          }

          job_result.results << crossReferenceSingleTitle(e, user.id, fullsync, locale)

          ctr++
          job.setProgress(ctr, json.size())
        }

        job.endTime = new Date()
        job.setProgress(100)
        job.message("Finished processing ${job_result?.results?.size()} titles.".toString())

        JobResult.withNewSession {
          JobResult result_object = JobResult.findByUuid(job.uuid)

          def job_map = [
                  uuid: (job.uuid),
                  description: (job.description),
                  resultObject: (job_result ? (job_result as JSON).toString() : null),
                  type: (job.type),
                  statusText: (job_result ? (job_result.result) : job.status),
                  ownerId: (job.ownerId),
                  groupId: (job.groupId),
                  startTime: (job.startTime),
                  endTime: (job.endTime),
                  linkedItemId: (job.linkedItem?.id)
          ]

          if (!result_object) {
            result_object = new JobResult(job_map).save()
          }else {

            result_object.save(job_map).save()

          }
        }

        return job_result
      }
      log.debug("Starting job ${background_job}..")

      background_job.startOrQueue()
      background_job.description = "Title CrossRef"
      background_job.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TitleCrossRef')
      background_job.startTime = new Date()

      if (async == false) {
        result = background_job.get()
      }
      else {
        result = [job_id: background_job.uuid]
      }
    }

    render result as JSON
  }

  private crossReferenceSingleTitle(Object titleObj, userid, fullsync, locale) {

    def result = ['result': 'OK']

    log.debug("crossReferenceTitle(${titleObj.type},${titleObj.name},${titleObj.identifiers}},...)");

    TitleInstance.withNewSession {
      User user = User.get(userid)

      def title_validation = TitleInstance.validateDTO(titleObj, locale)

      if (title_validation && !title_validation.valid) {
        log.warn("Not valid after title validation ${titleObj}")
        result.result = 'ERROR'
        result.message = messageService.resolveCode('crossRef.title.error.preValidation', [titleObj.name], locale)
        result.baddata = titleObj
        result.errors = title_validation.errors
      }
      else {
        def title_class_name = determineTitleClass(titleObj)

        if (!title_class_name) {
          log.error("Missing or unknown publication type: ${titleObj.type}")
          result.result = "ERROR"
          result.message = messageService.resolveCode('crossRef.title.error.type', [titleObj.name], locale)
          result.errors = [type: [message: messageService.resolveCode('crossRef.title.error.type.local', null, locale), baddata: titleObj.type]]

          return result
        }

        try {
          def title = titleLookupService.findOrCreate(
              titleObj.name,
              titleObj.publisher,
              titleObj.identifiers,
              user,
              null,
              title_class_name,
              titleObj.uuid,
              false,
              titleObj.language
          )

          if (title && !title.hasErrors()) {
            def title_changed = false;

            // Add the core data.
            componentUpdateService.ensureCoreData(title, titleObj, fullsync, user)

            title_changed |= componentUpdateService.setAllRefdata([
                'OAStatus', 'medium', 'pureOA', 'continuingSeries', 'reasonRetired'
            ], titleObj, title)

            def pubFrom = GOKbTextUtils.completeDateString(titleObj.publishedFrom)
            def pubTo = GOKbTextUtils.completeDateString(titleObj.publishedTo, false)

            log.debug("Completed date publishedFrom ${titleObj.publishedFrom} -> ${pubFrom}")

            title_changed |= ClassUtils.setDateIfPresent(pubFrom, title, 'publishedFrom')
            title_changed |= ClassUtils.setDateIfPresent(pubTo, title, 'publishedTo')


            if (title_class_name == 'org.gokb.cred.BookInstance') {

              log.debug("Adding Monograph fields for ${title.class.name}: ${title}")
              def mg_change = addMonographFields(title, titleObj)

              if (mg_change) {
                title_changed = true
              }
            }

            titleLookupService.addPublisherHistory(title, titleObj.publisher_history)

            title.save()

            if (!result.message) {
              result.message = messageService.resolveCode('crossRef.title.success', null, locale)
            }
            result.cls = title.class.name
            result.titleId = title.id
            result.uuid = title.uuid
          }
          else if (title) {
            result.result = "ERROR"
            result.baddata = titleObj
            log.error("Cross Reference Title failed: ${titleObj}");
            result.errors = messageService.processValidationErrors(title.errors)

            if (title?.id) {
              result.titleId = title.id
              result.uuid = title.uuid
              result.message = messageService.resolveCode('crossRef.title.error.existing', [title.name, title.id], locale)
              log.error("CrossReference Matched existing title (${title.id}) with errors: ${title.errors}")
            }
            else {
              result.message = "Cross Reference of title ${titleObj.name} failed";
            }
          }
          else {
            result.result = "ERROR"
            result.baddata = titleObj.identifiers
            result.message = messageService.resolveCode('crossRef.title.error.dupes', [title.name], locale)
          }
        }
        catch (org.gokb.exceptions.MultipleComponentsMatchedException mcme) {
          log.debug("Handling MultipleComponentsMatchedException")
          result.result = "ERROR"
          result.message = messageService.resolveCode('crossRef.title.error.multipleMatches', [titleObj.name, mcme.matched_ids], locale)
        }
        catch (grails.validation.ValidationException ve) {
          log.debug("ValidationException attempting to cross reference title", ve);
          result.result = "ERROR"
          result.message = messageService.resolveCode('crossRef.title.error.validation', [titleObj.name], locale)
          result.errors = messageService.processValidationErrors(ve.errors)
          result.baddata = titleObj
        }
        catch (Exception e) {
          log.error("Exception attempting to cross reference title", e);

          if (result.result != 'ERROR') {
            result.result = "ERROR"
            result.message = messageService.resolveCode('crossRef.title.error.unknown', [titleObj.name], locale)
            result.baddata = titleObj
            log.error("Source message causing error (ADD_TO_TEST_CASES): ${titleObj}");
          }
        }
        finally {
          log.debug("Result of cross ref title: ${result}");
        }
      }
    }
    result
  }*/

  /*private static addPublisherHistory(TitleInstance ti, publishers) {
    if (publishers && ti) {
      log.debug("Handling publisher history ..")

      def publisher_combos = []
      publisher_combos.addAll(ti.getCombosByPropertyName('publisher'))
      String propName = ti.isComboReverse('publisher') ? 'fromComponent' : 'toComponent'
      String tiPropName = ti.isComboReverse('publisher') ? 'toComponent' : 'fromComponent'

      // Go through each Org.
      for (def pub_to_add : publishers) {

        Org publisher = null
        // Lookup the publisher.
        if (pub_to_add.uuid) {
          publisher = Org.findByUuid(pub_to_add.uuid)
        }

        def norm_pub_name = KBComponent.generateNormname(pub_to_add.name)
        def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

        if (!publisher) {
          publisher = Org.findByNormname(norm_pub_name)
        }

        if (!publisher || publisher.status == status_deleted) {
          def variant_normname = GOKbTextUtils.normaliseString(pub_to_add.name)
          def candidate_orgs = Org.executeQuery("select distinct o from Org as o join o.variantNames as v where v.normVariantName = ? and o.status <> ?", [variant_normname, status_deleted]);

          if (candidate_orgs.size() == 1) {
            publisher = candidate_orgs[0]
          }
        }

        if (publisher) {

          LocalDateTime parsedStart = GOKbTextUtils.completeDateString(pub_to_add.startDate)
          LocalDateTime parsedEnd = GOKbTextUtils.completeDateString(pub_to_add.endDate, false)
          Date pub_add_sd = parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null
          Date pub_add_ed = parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null

          boolean found = false
          for (int i = 0; !found && i < publisher_combos.size(); i++) {
            Combo pc = publisher_combos[i]
            def idMatch = pc."${propName}".id == publisher.id

            if (idMatch) {
              if (pub_add_sd && pc.startDate && pub_add_sd != pc.startDate) {
              }
              else if (pub_add_ed && pc.endDate && pub_add_ed != pc.endDate) {
              }
              else {
                found = true
              }
            }


          }

          // Only add if we havn't found anything.
          if (!found) {

            log.debug("Adding new combo for publisher ${publisher} (${propName}) to title ${ti} (${tiPropName})")

            RefdataValue type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, ti.getComboTypeValue('publisher'))

            def combo = null

            if (propName == "toComponent") {
              combo = new Combo(
                  type: (type),
                  status: pub_to_add.status ? RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, pub_to_add.status) : DomainClassExtender.getComboStatusActive(),
                  startDate: pub_add_sd,
                  endDate: pub_add_ed,
                  toComponent: publisher,
                  fromComponent: ti
              )
            }
            else {
              combo = new Combo(
                  type: (type),
                  status: pub_to_add.status ? RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, pub_to_add.status) : DomainClassExtender.getComboStatusActive(),
                  startDate: pub_add_sd,
                  endDate: pub_add_ed,
                  fromComponent: publisher,
                  toComponent: ti
              )
            }

            // Depending on where the combo is defined we need to add a combo.
//            if (ti.isComboReverse('publisher')) {
//              ti.addToIncomingCombos(combo)
//            } else {
//              ti.addToOutgoingCombos(combo)
//            }
//            publisher.save()

            if (combo) {
              combo.save(flush: true, failOnError: true)

              // Add the combo to our list to avoid adding duplicates.
              publisher_combos.add(combo)

              log.debug "Added publisher ${publisher.name} for '${ti.name}'" +
                  (combo.startDate ? ' from ' + combo.startDate : '') +
                  (combo.endDate ? ' to ' + combo.endDate : '')
            }
            else {
              log.error("Could not create publisher Combo..")
            }

          }
          else {
            log.debug "Publisher ${publisher.name} already set against '${ti.name}'"
          }

        }
        else {
          log.debug "Could not find org name: ${pub_to_add.name}, with normname: ${norm_pub_name}"
        }
      }
    }
  }*/

  /*private static addMonographFields(BookInstance bi, titleObj) {

    def book_changed = false

    def bookStringAttrs = ["editionNumber", "editionDifferentiator",
                           "editionStatement", "volumeNumber",
                           "summaryOfContent", "firstAuthor", "firstEditor"]

    bookStringAttrs.each {
      if (titleObj[it] && titleObj[it].toString().trim().length() > 0) {
        book_changed |= ClassUtils.setStringIfDifferent(bi, it, titleObj[it])
      }
    }
    def dfip = GOKbTextUtils.completeDateString(titleObj.dateFirstInPrint)
    def dfo = GOKbTextUtils.completeDateString(titleObj.dateFirstOnline, false)

    book_changed |= ClassUtils.setDateIfPresent(dfip, bi, 'dateFirstInPrint')
    book_changed |= ClassUtils.setDateIfPresent(dfo, bi, 'dateFirstOnline')

    if (book_changed) {
      bi.save(flush: true, failOnError: true)
    }

    book_changed
  }*/

  def getJobInfo() {
    def result = ['result': 'OK', 'params': params]
    User user = null
    String uuid = params.id
    log.info("getJobInfo($uuid)")
    if (uuid == null) {
      result.result = "ERROR"
      response.setStatus(400)
      result.message = "Request has no id parameter."
    }
    else {
      if (springSecurityService.isLoggedIn()) {
        user = springSecurityService.currentUser
      }
      else if (params.updateToken?.trim()) {
        def updateToken = UpdateToken.findByValue(params.updateToken)

        if (updateToken) {
          user = updateToken.updateUser
        }
        else {
          log.error("Unable to reference update token!")
          result.message = "Unable to reference update token!"
          response.setStatus(400)
          result.result = "ERROR"
        }
      }
      else {
        response.setStatus(401)
        response.setHeader('WWW-Authenticate', 'Basic realm="gokb"')
      }

      if (user) {
        Job job = concurrencyManagerService.getJob(uuid)

        if (job) {
          log.debug("${job}")

          if (user.superUserStatus || (job.ownerId && job.ownerId == user.id)) {
            result.description = job.description
            result.type = job.type ? [value: job.type.value, id: job.type.id] : null
            result.linkedItem = job.linkedItem
            result.startTime = job.startTime

            if (job.endTime || job.isCancelled()) {
              result.finished = true
              result.endTime = job.endTime
              try {
                result.job_result = job.get()
              }
              catch (CancellationException ce) {
                result.cancelled = true
              }
            }
            else {
              result.finished = false
              result.progress = job.progress
            }
          }
          else {
            result.result = "ERROR"
            response.setStatus(403)
            result.message = "No permission to view job with ID ${uuid}."
          }
        }
        else {
          def persistedResult = JobResult.findByUuid(uuid)

          if (persistedResult) {
            def linkedItemMap = null

            if (persistedResult.linkedItemId) {
              def linkedItem = KBComponent.get(persistedResult.linkedItemId)

              if (linkedItem) {
                linkedItemMap = [id: linkedItem.id, name: linkedItem.name, uuid: linkedItem.uuid, type: linkedItem.niceName]
              }
            }

            result.description = persistedResult.description
            result.type = persistedResult.type ? [value: persistedResult.type.value, id: persistedResult.type.id] : null
            result.linkedItem = linkedItemMap
            result.startTime = persistedResult.startTime
            result.endTime = persistedResult.endTime
            result.job_result = new JsonSlurper().parseText(persistedResult.resultObject)
            result.finished = true

            if (result.job_result?.result == 'CANCELLED') {
              result.cancelled = true
            }
          }
          else {
            result.result = "ERROR"
            response.setStatus(404)
            result.message = "Could not find job with ID ${uuid}."
          }
        }
      }
    }
    render result as JSON
  }


  /*@Secured(['ROLE_API', 'IS_AUTHENTICATED_FULLY'])
  def loadTitleList() {
    def title_file = request.getFile("titleFile")?.inputStream
    char tab = '\t'
    char quote = '"'
    def r = new CSVReader(new InputStreamReader(title_file, java.nio.charset.Charset.forName('UTF-8')), tab, quote)

    def col_positions = ['identifier.pissn': -1, 'identifier.eissn': -1, 'title': -1]

    String[] header = r.readNext()
    int ctr = 0
    header.each {
      col_positions[it.toLowerCase()] = ctr++
    }

    if ((col_positions.'title' != -1) &&
        ((col_positions.'identifier.pissn' != -1) ||
            (col_positions.'identifier.eissn' != -1))) {

      // So long as we have at least one identifier...
      String[] nl = r.readNext()

      int rowctr = 0;

      while (nl != null) {
        try {
          KBComponent.withNewTransaction() {

            def candidate_identifiers = []

            if ((col_positions.'identifier.pissn' != -1) &&
                (nl[col_positions.'identifier.pissn']?.length() > 0) &&
                (nl[col_positions.'identifier.pissn'].toLowerCase() != 'null')) {
              candidate_identifiers.add([type: 'issn', value: nl[col_positions.'identifier.pissn']]);
            }

            if ((col_positions.'identifier.eissn' != -1) &&
                (nl[col_positions.'identifier.eissn']?.length() > 0) &&
                (nl[col_positions.'identifier.eissn'].toLowerCase() != 'null')) {
              candidate_identifiers.add([type: 'eissn', value: nl[col_positions.'identifier.eissn']]);
            }

            if (candidate_identifiers.size() > 0) {
              log.debug("Looking up ${candidate_identifiers} - ${nl[col_positions.'title']}");
              // def existing_component = titleLookupService.find (nl[col_positions.'title'], null, candidate_identifiers)
            }
            else {
              log.debug("No candidate identifiers: ${nl}");
            }
          }
        }
        catch (Exception e) {
          log.error("Unable to process..", e);
        }

        if (rowctr++ > 100) {
          log.debug("CleanUpGorm..");
          rowctr = 0;
          cleanUpGorm()
        }
        nl = r.readNext()
        log.debug("Next row: ${nl}");
      }
    }
    log.debug("Done");
    redirect(action: 'index');
  }*/

  private def cleanUpGorm() {
    log.debug("Clean up GORM");

    // Get the current session.
    def session = sessionFactory.currentSession

    // flush and clear the session.
    session.flush()
    session.clear()
  }

}
