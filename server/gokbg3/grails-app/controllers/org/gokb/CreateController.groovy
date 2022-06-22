package org.gokb

import com.k_int.apis.SecurityApi
import de.wekb.helper.RCConstants
import grails.converters.JSON
import org.apache.commons.lang.RandomStringUtils
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.access.annotation.Secured;
import org.gokb.cred.*
import org.grails.datastore.mapping.model.*
import org.grails.datastore.mapping.model.types.*
import grails.core.GrailsClass
import org.springframework.transaction.TransactionStatus
import org.springframework.web.multipart.MultipartFile
import wekb.AccessService
import wekb.CreateComponentService
import wekb.ExportService
import wekb.PackageArchivingAgency

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
  CreateComponentService createComponentService

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

    def result=[:]

    result = createComponentService.process(result, params)

    if(result.error)
      flash.error = result.error

    if(result.message)
      flash.message = result.message

    log.debug("CreateController::process return ${result}");

    if(result.urlMap) {
      redirect(result.urlMap)
    }else {
      redirect(url: request.getHeader('referer'))
    }
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def exportPackageBatchImportTemplate() {

    String filename = "template_package_import.xlsx"

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

    result.mappingCols = ["package_uuid", "package_name", "provider_uuid", "nominal_platform_uuid", "description", "url", "breakable", "content_type",
            "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "anbieter_produkt_id", "ddc", "source_url", "frequency", "title_id_namespace", "automated_updates", "archiving_agency", "open_access_of_archiving_agency", "post_cancellation_access_of_archiving_agency"]

    result
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def processPackageBatch() {
    log.debug("CreateControler::processPackageBatch... ${params}");
        User user = springSecurityService.currentUser
        MultipartFile tsvFile = request.getFile("tsvFile")
        if(tsvFile && tsvFile.size > 0) {
          String encoding = UniversalDetector.detectCharset(tsvFile.getInputStream())
          if(encoding in ["UTF-8", "US-ASCII"]) {
            Map packagesData = packageBatchImport(tsvFile, user)

            render view: 'packageBatchCompleted', model: packagesData
          }
          else {
            String errorText = "The file you have uploaded has a wrong character encoding! Please ensure that your file is encoded in UTF-8. Guessed encoding has been: ${encoding}"
            flash.error = errorText
            redirect(url: request.getHeader('referer'))
          }
        }
        else {
          String errorText = "You have not uploaded a valid file!"
          flash.error = errorText

          redirect(url: request.getHeader('referer'))
        }
  }

  private Map packageBatchImport(MultipartFile tsvFile, User user) {
    Map colMap = [:]
    Set<String> globalErrors = []
    List<Package> packageList = []
    RefdataValue combo_type_id = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'KBComponent.Ids')
    RefdataValue combo_type_status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)

    InputStream fileContent = tsvFile.getInputStream()
    List<String> rows = fileContent.text.split('\n')

    rows[0].split('\t').eachWithIndex { String s, int c ->
      String headerCol = s.trim()
      if(headerCol.startsWith("\uFEFF"))
        headerCol = headerCol.substring(1)
      //important: when processing column headers, grab those which are reserved; default case: check if it is a name of a property definition; if there is no result as well, reject.
      switch(headerCol.toLowerCase()) {
        case "package_name": colMap.name = c
          break
        case "package_uuid": colMap.package_uuid = c
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
        case "editing_status": colMap.editing_status = c
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
        case "automated_updates": colMap.automated_updates = c
          break
        case 'archiving_agency': colMap.archiving_agency = c
          break
        case 'open_access_of_archiving_agency': colMap.open_access_of_archiving_agency = c
          break
        case 'post_cancellation_access_of_archiving_agency': colMap.post_cancellation_access_of_archiving_agency = c
          break
      }
    }

    rows.remove(0)

    RefdataValue status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

    rows.each { row ->
        List<String> cols = row.split('\t')

      Package pkg
      boolean editAllowed = true
      if (colMap.package_uuid != null) {
        String package_uuid = cols[colMap.package_uuid].trim()
        pkg = package_uuid ? Package.findByUuid(package_uuid) : null

        if (pkg != null && !accessService.checkEditableObject(pkg, null)){
          globalErrors << "You have no authorization to edit the package with the uuid '${package_uuid}'.!"
          editAllowed = false
        }

      }

      if ((colMap.name != null || pkg != null) && editAllowed) {

          String name = cols[colMap.name].trim()

          if(pkg == null) {
            def dupes = Package.findAllByNameIlikeAndStatusNotEqual(name, status_deleted)

            if (dupes && dupes.size() > 0) {
              globalErrors << "The we:kb already has a package with the name '${name}'. Therefore a package with the name could not be created!"
              name = null
            }
          }
          try {

              if (name && pkg == null) {
                String pkg_normname = Package.generateNormname(name)
                pkg = new Package(name: name, normname: pkg_normname)
              }

              if(pkg != null) {
                pkg.name = name ?: pkg.name

                if (colMap.provider_uuid != null) {
                  Org provider = Org.findByUuid(cols[colMap.provider_uuid].trim())
                  if (provider){
                    if(!(pkg.provider && pkg.provider == provider)){
                      def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Provider')
                      def current_combo = Combo.findByFromComponentAndType(pkg, combo_type)

                      if (current_combo) {
                        current_combo.delete(flush: true)
                      }

                      def new_combo = new Combo(fromComponent: pkg, toComponent: provider, type: combo_type).save(flush: true)

                    }
                  }

                }

                if (colMap.nominal_platform_uuid != null) {
                  Platform platform = Platform.findByUuid(cols[colMap.nominal_platform_uuid].trim())
                  if (platform){
                    if(!(pkg.nominalPlatform && pkg.nominalPlatform == platform)){
                      def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.NominalPlatform')
                      def current_combo = Combo.findByFromComponentAndType(pkg, combo_type)

                      if (current_combo) {
                        current_combo.delete(flush: true)
                      }

                      def new_combo = new Combo(fromComponent: pkg, toComponent: platform, type: combo_type).save(flush: true)

                    }
                  }

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

                /* if (colMap.consistent != null) {
                    String value = cols[colMap.consistent].trim()
                    if (value) {
                      RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_CONSISTENT, value)
                      if (refdataValue)
                        pkg.consistent = refdataValue
                    }
                  }*/

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

                /* if (colMap.editing_status != null) {
                    String value = cols[colMap.editing_status].trim()
                    if (value) {
                      RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_EDITING_STATUS, value)
                      if (refdataValue)
                        pkg.editingStatus = refdataValue
                    }
                  }*/


                if (colMap.national_ranges) {
                  List<String> national_ranges = cols[colMap.national_ranges].split(',')
                  national_ranges.each { String value ->
                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.COUNTRY, value.trim())
                    if (refdataValue && !(refdataValue in pkg.nationalRanges)) {
                      pkg.addToNationalRanges(refdataValue)
                    }
                  }
                }

                if (colMap.regional_ranges) {
                  List<String> regional_ranges = cols[colMap.regional_ranges].split(',')
                  regional_ranges.each { String value ->
                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PACKAGE_REGIONAL_RANGE, value.trim())
                    if (refdataValue && !(refdataValue in pkg.regionalRanges)) {
                      pkg.addToRegionalRanges(refdataValue)
                    }
                  }
                }

                if (colMap.anbieter_produkt_id != null) {
                  String value = cols[colMap.anbieter_produkt_id].trim()
                  if (value) {

                    pkg.addOnlySpecialIdentifiers("Anbieter_Produkt_ID", value)
                  }
                }

                if (colMap.ddcs) {
                  List<String> ddcs = cols[colMap.ddcs].split(',')
                  ddcs.each { String value ->
                    value = value.trim()
                    if (value != "") {
                      if (value.toInteger() < 10) {
                        value = "00" + value
                      }

                      if (value.toInteger() >= 10 && value.toInteger() < 100) {
                        value = "0" + value
                      }
                      RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, value)
                      if (refdataValue && !(refdataValue in pkg.ddcs)) {
                        pkg.addToDdcs(refdataValue)
                      }
                    }
                  }
                }

                  if (pkg.save(flush: true)) {

                    if (colMap.archiving_agency != null) {
                      String value = cols[colMap.archiving_agency].trim()
                      if (value) {
                        RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.PAA_ARCHIVING_AGENCY, value)
                        if (refdataValue){
                          PackageArchivingAgency packageArchivingAgency = new PackageArchivingAgency(archivingAgency: refdataValue, pkg: pkg)
                          if (packageArchivingAgency.save(flush: true)) {
                            if (colMap.open_access_of_archiving_agency != null) {
                              String paaOp = cols[colMap.open_access_of_archiving_agency].trim()
                              if (paaOp) {
                                RefdataValue refdataValuePaaOP = RefdataCategory.lookup(RCConstants.PAA_OPEN_ACCESS, paaOp)
                                if (refdataValuePaaOP)
                                  packageArchivingAgency.openAccess = refdataValuePaaOP
                              }
                            }

                            if (colMap.post_cancellation_access_of_archiving_agency != null) {
                              String paaPCA = cols[colMap.post_cancellation_access_of_archiving_agency].trim()
                              if (paaPCA) {
                                RefdataValue refdataValuePaaPCA = RefdataCategory.lookup(RCConstants.PAA_POST_CANCELLATION_ACCESS, paaPCA)
                                if (refdataValuePaaPCA)
                                  packageArchivingAgency.postCancellationAccess = refdataValuePaaPCA
                              }
                            }
                            packageArchivingAgency.save(flush: true)
                          }

                        }
                      }
                    }

                    user.curatoryGroups.each { CuratoryGroup cg ->
                      if (!(cg in pkg.curatoryGroups)) {
                        def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.CuratoryGroups')

                        def new_combo = new Combo(fromComponent: pkg, toComponent: cg, type: combo_type).save(flush: true)

                      }
                    }

                    if (colMap.source_url != null) {
                      String source_url = cols[colMap.source_url].trim()
                      if (source_url) {
                        Source source
                        if (pkg.source == null) {
                          def dupes = Source.findAllByNameIlikeAndStatusNotEqual(pkg.name, status_deleted)
                          String sourceName = pkg.name
                          if (dupes && dupes.size() > 0) {
                            sourceName = "${sourceName} ${dupes.size()}"
                          }

                          source = new Source(name: sourceName)
                        } else {
                          source = pkg.source
                        }

                        source.url = source_url

                        if (colMap.frequency != null) {
                          String value = cols[colMap.frequency].trim()
                          if (value) {
                            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.SOURCE_FREQUENCY, value)
                            if (refdataValue)
                              source.frequency = refdataValue
                          }
                        }

                        if (colMap.automated_updates != null) {
                          String value = cols[colMap.automated_updates].trim()
                          if (value) {
                            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.YN, value)
                            if (refdataValue) {
                              source.automaticUpdates = (refdataValue.value == "Yes") ? true : false
                            }

                            if (refdataValue && refdataValue.value == "Yes") {
                              String charset = (('a'..'z') + ('0'..'9')).join()
                              String tokenValue = RandomStringUtils.random(255, charset.toCharArray())
                              if (!UpdateToken.findByPkg(pkg)) {
                                UpdateToken newToken = new UpdateToken(pkg: pkg, updateUser: user, value: tokenValue).save(flush: true)
                              }
                            }

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

                        if (source.save(flush: true)) {
                          user.curatoryGroups.each { CuratoryGroup cg ->
                            if (!(cg in source.curatoryGroups)) {
                              def combo_type = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Source.CuratoryGroups')

                              def new_combo = new Combo(fromComponent: source, toComponent: cg, type: combo_type).save(flush: true)
                            }
                          }

                          if (source.automaticUpdates) {
                            String charset = (('a'..'z') + ('0'..'9')).join()
                            String tokenValue = RandomStringUtils.random(255, charset.toCharArray())
                            if (!UpdateToken.findByPkg(pkg)) {
                              UpdateToken newToken = new UpdateToken(pkg: pkg, updateUser: user, value: tokenValue).save(flush: true)
                            }
                          }
                          if (source != pkg.source) {
                            pkg.source = source
                            pkg.save(flush: true)
                          }
                        }
                      }
                    }
                  }

                packageList << pkg

              }

            }catch ( Exception e ) {

              if(pkg){
                pkg.delete(flush: true)
              }
              log.error(e.printStackTrace())
              globalErrors << "Error on package with the name '${name}'. Please try agian!"
            }
          }
      }


    [packages: packageList, rowsCount: rows.size(), errors: globalErrors]
  }
}
