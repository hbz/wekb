package org.gokb

import com.k_int.apis.SecurityApi
import de.wekb.helper.RCConstants
import grails.converters.JSON
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.annotation.Secured;
import org.gokb.cred.*
import org.grails.datastore.mapping.model.*
import org.grails.datastore.mapping.model.types.*
import grails.core.GrailsClass
import org.springframework.transaction.TransactionStatus
import org.springframework.web.multipart.MultipartFile
import wekb.AccessService
import wekb.ExportService

import java.time.Instant
import java.time.ZoneId
import java.time.LocalDateTime

import org.mozilla.universalchardet.UniversalDetector

@Secured(['IS_AUTHENTICATED_FULLY'])
class CreateController {

  def genericOIDService
  def classExaminationService
  def springSecurityService
  def displayTemplateService
  def messageSource
  AccessService accessService
  ComponentLookupService componentLookupService
  ExportService exportService

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def index() {
    log.debug("CreateControler::index... ${params}");
    def result=[:]
    User user = springSecurityService.currentUser

    // Create a new empty instance of the object to create
    result.newclassname=params.tmpl
    if ( params.tmpl ) {
      def newclass = grailsApplication.getArtefact("Domain",result.newclassname)
      if ( newclass ) {
        log.debug("Got new class")
        try {
          result.displayobj = newclass.newInstance()
          log.debug("Got new instance");
          result.editable = SecurityApi.isTypeCreatable(result.displayobj.getClass())

          if ( params.tmpl ) {
            result.displaytemplate = displayTemplateService.getTemplateInfo(params.tmpl)

            /* Extras needed for the refdata */
            result.refdata_properties = classExaminationService.getRefdataPropertyNames(result.newclassname)
            result.displayobjclassname_short = result.displayobj.class.simpleName
            result.isComponent = (result.displayobj instanceof KBComponent)
          }
        }
        catch ( Exception e ) {
          log.error("Problem",e);
        }
      }else {
        log.info("No Permission for ${result.newclassname} in CreateControler::index... ${params}");
        response.sendError(401)
        return
      }
    }

    log.debug("index:: return");
    result
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def process() {
    log.debug("CreateController::process... ${params}");

    def result=['responseText':'OK']


    // II: Defaulting this to true - don't like it much, but we need to be able to create a title without any
    // props being set... not ideal, but issue closing.
    boolean propertyWasSet = true

    User user = springSecurityService.currentUser

    if ( params.cls ) {

      GrailsClass newclass = grailsApplication.getArtefact("Domain",params.cls)
      PersistentEntity pent = grailsApplication.mappingContext.getPersistentEntity(params.cls)
      // def refdata_properties = classExaminationService.getRefdataPropertyNames(params.cls)
      log.debug("Got entity ${pent} for ${newclass.name}")

      if ( newclass ) {
        try {
          result.newobj = newclass.newInstance()
          log.debug("got newInstance...");

          params.each { p ->
            log.debug("Consider ${p.key} -> ${p.value}");
            if ( pent.getPropertyByName(p.key) ) {
              // THis deffo didn't work :( if ( newclass.metaClass.hasProperty(p.key) ) {

              // Ensure that blank values actually null the value instead of trying to use an empty string.
              if (p.value == "") p.value = null

              PersistentProperty pprop = pent.getPropertyByName(p.key)

              if ( pprop instanceof Association ) {
                if ( pprop instanceof OneToOne) {
                  log.debug("one-to-one");
                  def related_item = null

                  related_item = genericOIDService.resolveOID(p.value);

                  if (!related_item && pprop.getType().name == 'org.gokb.cred.RefdataValue') {
                    def rdc = classExaminationService.deriveCategoryForProperty(params.cls, p.key)
                    related_item = RefdataCategory.lookup(rdc, p.value)
                  }

                  result.newobj[p.key] = related_item
                  propertyWasSet = propertyWasSet || (related_item != null)
                }
                else if ( pprop instanceof ManyToOne ) {
                  log.debug("many-to-one");
                  def related_item = genericOIDService.resolveOID(p.value);
                  result.newobj[p.key] = related_item
                  propertyWasSet = propertyWasSet || (related_item != null)
                }
                else {
                  log.debug("unhandled association type");
                }
              }
              else {
                log.debug("Scalar property");
                if ( pprop.getType().name == 'java.lang.String' ) {
                  result.newobj[p.key] = p.value?.trim() ?: null
                }
                else if ( pprop.getType().name == 'java.util.Date' ) {
                  def sdf = new java.text.SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss z", Locale.ENGLISH);
                  def incoming = p.value.substring(0,31) + ":" + p.value.substring(31, 33)
                  Instant instant = sdf.parse(incoming).toInstant()
                  LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.of("GMT"))

                  result.newobj[p.key] = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
                }
                propertyWasSet = propertyWasSet || (p.value != null)
              }
            }
            else {
              log.debug("Persistent class has no property named ${p.key}");
            }
          }
          log.debug("Completed setting properties");

         /* if ( result.newobj.hasProperty('postCreateClosure') ) {
            log.debug("Created object has a post create closure.. call it");
            result.newobj.postCreateClosure.call([user:user])
          }*/

          if (result.newobj instanceof TitleInstancePackagePlatform && (params.pkg == null || params.hostPlatform == null || params.url == null || params.name == null)) {
            flash.error="Please fill Package, Platform and Host Platform URL to create the component."
            result.uri = g.createLink([controller: 'create', action:'index', params:[tmpl:params.cls]])
          }
          else if (!propertyWasSet) {
            // Add an error message here if no property was set via data sent through from the form.
            log.debug("No properties set");
            flash.error="Please fill in at least one piece of information to create the component."
            result.uri = g.createLink([controller: 'create', action:'index', params:[tmpl:params.cls]])
          } else {

            log.debug("Saving..");
            if ( !result.newobj.validate() ) {
              flash.error = []

              result.newobj.errors.allErrors.each { eo ->

                String[] messageArgs = eo.getArguments()
                def errorMessage = null

                log.debug("Found error with args: ${messageArgs}")

                eo.getCodes().each { ec ->

                  if (!errorMessage) {
                    // log.debug("testing code -> ${ec}")

                    def msg = messageSource.resolveCode(ec, request.locale)?.format(messageArgs)

                    if(msg && msg != ec) {
                      errorMessage = msg
                    }

                    if(!errorMessage) {
                      // log.debug("Could not resolve message")
                    }else{
                      log.debug("found message: ${msg}")
                    }
                  }
                }

                if (errorMessage) {
                  flash.error.add(errorMessage)
                }else{
                  log.debug("No message found for ${eo.getCodes()}")
                }
              }

              if ( flash.error.size() == 0 ) {
                flash.error.add("There has been an error creating the component. Please try again.")
              }

              result.errors = flash.error

              result.uri = createLink([controller: 'create', action:'index', params:[tmpl:params.cls]])
            } else {
              result.newobj.save(flush:true)

              log.debug("Setting combos..");

              if (result.newobj instanceof KBComponent) {
                // The save completed OK.. if we want to be really cool, we can now loop through the properties
                // and set any combos on the object
                boolean changed=false
                params.each { p ->
                  def combo_properties = result.newobj.getComboTypeValue(p.key)

                  if ( combo_properties != null ) {
                    log.debug("Deal with a combo doodah ${p.key}:${p.value}");
                    if ( ( p.value != "") && ( p.value != null ) ) {
                      def related_item = genericOIDService.resolveOID(p.value);
                      result.newobj[p.key] = related_item
                      changed = true
                    }
                  }
                  result.newobj.save(flush:true)
                }
              }

              if (result.newobj.respondsTo("getCuratoryGroups")) {
                log.debug("Set CuratoryGroups..");
                if(user.isAdmin() || user.getSuperUserStatus()) {
                  flash.message = "Object was not assigned to a curator group because you are admin or superuser!!!!"

                }else {
                  user.curatoryGroups.each { CuratoryGroup cg ->
                    result.newobj.curatoryGroups.add(cg)
                  }
                }

                  result.newobj.save(flush:true)
              }

              result.uri = createLink([controller: 'resource', action:'show', id:"${params.cls}:${result.newobj.id}"])
            }
          }
        }
        catch ( Exception e ) {
          log.error("Problem",e);
          flash.error = "Could not create component!"
          result.uri = createLink([controller: 'resource', action:'show', id:"${params.cls}:${result.newobj.id}"])
        }
      }
    }
    log.debug("CreateController::process return ${result}");

    render result as JSON
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def exportPackageBatchImportTemplate() {

    String filename = "Moe.xlsx"

    try {

      response.setHeader("Content-disposition","attachment; filename=\"${filename}\"")
      response.contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

      def out = response.outputStream

      exportService.exportPackageBatchImportTemplate(out)

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }


  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def packageBatch() {
    log.debug("CreateControler::packageBatch... ${params}");
    def result=[:]
    User user = springSecurityService.currentUser

    result.mappingCols = ["package_name", "provider_uuid", "nominal_platform_uuid", "description", "url", "breakable", "consistent", "content_type",
            "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "anbieter_produkt_id", "ddc", "source_url", "frequency", "title_id_namespace"]

    result
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def processPackageBatch() {
    log.debug("CreateControler::processPackageBatch... ${params}");
        User user = springSecurityService.currentUser
        MultipartFile csvFile = request.getFile("csvFile")
        if(csvFile && csvFile.size > 0) {
          String encoding = UniversalDetector.detectCharset(csvFile.getInputStream())
          if(encoding == "UTF-8") {
            Map packagesData = packageBatchImport(csvFile, user)

            render view: 'packageBatchCompleted', model: packagesData
          }
          else {
            flash.error = "The file you have uploaded has a wrong character encoding! Please ensure that your file is encoded in UTF-8. Guessed encoding has been: ${encoding}"
            redirect(url: request.getHeader('referer'))
          }
        }
        else {
          flash.error = "You have not uploaded a valid file!"
          redirect(url: request.getHeader('referer'))
        }
  }

  private Map packageBatchImport(MultipartFile csvFile, User user) {
    Map colMap = [:]
    Set<String> globalErrors = []
    List<Package> packageList = []
    RefdataValue combo_active = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)
    RefdataValue combo_type_id = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'KBComponent.Ids')

    InputStream fileContent = csvFile.getInputStream()
    List<String> rows = fileContent.text.split('\n')

    rows[0].split(';').eachWithIndex { String s, int c ->
      String headerCol = s.trim()
      if(headerCol.startsWith("\uFEFF"))
        headerCol = headerCol.substring(1)
      //important: when processing column headers, grab those which are reserved; default case: check if it is a name of a property definition; if there is no result as well, reject.
      switch(headerCol.toLowerCase()) {
        case "package_name": colMap.name = c
          break
        case "provider_uuid": colMap.provider_uuid = c
          break
        case "nominal_platform_uuid": colMap.nominal_platform_uuid = c
          break
        case "description": colMap.description = c
          break
        case "url": colMap.url = c
          break
        case "breakable": colMap.breakable = c
          break
        case "consistent": colMap.consistent = c
          break
        case "content_type": colMap.content_type = c
          break
        case "file": colMap.file = c
          break
        case "open_access": colMap.open_access = c
          break
        case "payment_type": colMap.payment_type = c
          break
        case "scope": colMap.scope = c
          break
        case "national_range": colMap.national_ranges = c
          break
        case "regional_range": colMap.regional_ranges = c
          break
        case "anbieter_produkt_id": colMap.anbieter_produkt_id = c
          break
        case "ddc": colMap.ddcs = c
          break
        case "source_url": colMap.source_url = c
          break
        case "frequency": colMap.frequency = c
          break
        case "title_id_namespace": colMap.title_id_namespace = c
          break
      }
    }

    rows.remove(0)

    RefdataValue status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

    rows.each { row ->
        List<String> cols = row.split(';')
        if (colMap.name != null) {
          String name = cols[colMap.name].trim()


          def dupes = Package.findAllByNameIlikeAndStatusNotEqual(name, status_deleted)

          if (dupes && dupes.size() > 0) {
            globalErrors << "The we:kb already has a package with the name '${name}'. Therefore a package with the name could not be created!"
            name = null
          }

          if (name) {
            String pkg_normname = Package.generateNormname(name)
            Package pkg = new Package(name: name, normname: pkg_normname)

            if (colMap.provider_uuid != null) {
              Org provider = Org.findByUuid(cols[colMap.provider_uuid].trim())
              if (provider)
                pkg.provider = provider
            }

            if (colMap.nominal_platform_uuid != null) {
              Platform platform = Platform.findByUuid(cols[colMap.nominal_platform_uuid].trim())
              if (platform)
                pkg.nominalPlatform = platform
            }

            if (colMap.description != null) {
              pkg.description = cols[colMap.description].trim()
            }

            if (colMap.url != null) {
              pkg.descriptionURL = cols[colMap.url].trim()
            }

            if (colMap.breakable != null) {
              String value = cols[colMap.breakable].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_BREAKABLE, value)
                if (refdataValue)
                  pkg.breakable = refdataValue
              }
            }

            if (colMap.consistent != null) {
              String value = cols[colMap.consistent].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_CONSISTENT, value)
                if (refdataValue)
                  pkg.consistent = refdataValue
              }
            }

            if (colMap.content_type != null) {
              String value = cols[colMap.content_type].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_CONTENT_TYPE, value)
                if (refdataValue)
                  pkg.contentType = refdataValue
              }
            }

            if (colMap.file != null) {
              String value = cols[colMap.file].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_FILE, value)
                if (refdataValue)
                  pkg.file = refdataValue
              }
            }

            if (colMap.open_access != null) {
              String value = cols[colMap.open_access].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_OPEN_ACCESS, value)
                if (refdataValue)
                  pkg.openAccess = refdataValue
              }
            }

            if (colMap.payment_type != null) {
              String value = cols[colMap.payment_type].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_PAYMENT_TYPE, value)
                if (refdataValue)
                  pkg.paymentType = refdataValue
              }
            }

            if (colMap.scope != null) {
              String value = cols[colMap.scope].trim()
              if (value) {
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_SCOPE, value)
                if (refdataValue)
                  pkg.scope = refdataValue
              }
            }


            if (colMap.national_ranges) {
              List<String> national_ranges = cols[colMap.national_ranges].split(',')
              national_ranges.each { String value ->
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.COUNTRY, value)
                if (refdataValue) {
                  pkg.addToNationalRanges(refdataValue)
                }
              }
            }

            if (colMap.regional_ranges) {
              List<String> regional_ranges = cols[colMap.regional_ranges].split(',')
              regional_ranges.each { String value ->
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_REGIONAL_RANGE, value)
                if (refdataValue) {
                  pkg.addToRegionalRanges(refdataValue)
                }
              }
            }

            if (colMap.anbieter_produkt_id != null) {
              String value = cols[colMap.anbieter_produkt_id].trim()
              if (value) {
                Identifier canonical_identifier = componentLookupService.lookupOrCreateCanonicalIdentifier("Anbieter_Produkt_ID", value)
                if (canonical_identifier) {
                  if(pkg.save()) {
                    def new_id = new Combo(fromComponent: pkg, toComponent: canonical_identifier, status: combo_active, type: combo_type_id).save()
                  }
                }
              }
            }

            if (colMap.ddcs) {
              List<String> ddcs = cols[colMap.ddcs].split(',')
              ddcs.each { String value ->
                if(value.toInteger() < 10){
                  value = "00"+value
                }

                if(value.toInteger() >= 10 && value.toInteger() < 100){
                  value = "0"+value
                }
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, value)
                if (refdataValue) {
                  pkg.addToDdcs(refdataValue)
                }
              }
            }

            if (colMap.source_url != null) {
              String source_url = cols[colMap.source_url].trim()
              if (source_url) {

                Source source = new Source(name: pkg.name)
                source.url = source_url

                if (colMap.frequency != null) {
                  String value = cols[colMap.frequency].trim()
                  if (value) {
                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.SOURCE_FREQUENCY, value)
                    if (refdataValue)
                      source.frequency = refdataValue
                  }
                }

                if (colMap.title_id_namespace != null) {
                  String value = cols[colMap.title_id_namespace].trim()
                  if (value) {
                    IdentifierNamespace identifierNamespace = IdentifierNamespace.findByValue(value)
                    if (identifierNamespace)
                      source.targetNamespace = identifierNamespace
                  }
                }
                Package.withTransaction { TransactionStatus ts ->
                  if (source.save()) {
                    user.curatoryGroups.each { CuratoryGroup cg ->
                      source.curatoryGroups.add(cg)
                    }

                    pkg.source = source
                  }
                }

              }
            }
            Package.withTransaction { TransactionStatus ts ->
              if (pkg.save()) {

                user.curatoryGroups.each { CuratoryGroup cg ->
                  pkg.curatoryGroups.add(cg)
                }

                packageList << pkg

              }
            }
          }
        }
      }


    [packages: packageList, rowsCount: rows.size(), errors: globalErrors]
  }
}
