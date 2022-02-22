package org.gokb

import com.k_int.ESSearchService

import grails.converters.JSON
import grails.gorm.transactions.Transactional
import org.elasticsearch.action.bulk.BulkItemResponse
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.bulk.BulkResponse
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.common.xcontent.XContentType
import org.gokb.cred.KBComponent
import org.gokb.cred.KBComponentAdditionalProperty
import org.gokb.cred.TIPPCoverageStatement
import org.gokb.cred.RefdataValue
import wekb.Contact
import wekb.KBComponentLanguage

import java.text.Normalizer

@Transactional
class FTUpdateService {

  def ESWrapperService
  def sessionFactory
  def dateFormatService

  public static boolean running = false


  /**
   * Update ES.
   * The caller is responsible for running this function in a task if needed. This method
   * is responsible for ensuring only 1 FT index task runs at a time. It's a simple mutex.
   * see https://async.grails.org/latest/guide/index.html
   */
  def synchronized updateFTIndexes() {
    log.info("updateFTIndexes")
    if (running == false) {
      running = true
      doFTUpdate()
      log.info("FTUpdate done.")
      return new Date()
    }
    else {
      log.error("FTUpdate already running")
      return "Job cancelled – FTUpdate was already running!"
    }
  }

  def doFTUpdate() {
    log.info("Execute IndexUpdateJob starting at ${new Date()}")
    def esclient = ESWrapperService.getClient()
    try {
      updateES(esclient, org.gokb.cred.Package.class) { org.gokb.cred.Package kbc ->
        def result = null
        result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name

        result.description = kbc.description
        result.descriptionURL = kbc.descriptionURL
        result.sortname = generateSortName(kbc.name)
        result.altname = []

        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.updater = 'pkg'
        result.titleCount = kbc.currentTippCount
        result.cpname = kbc.provider?.name
        result.provider = kbc.provider ? kbc.provider.getLogEntityId() : ""
        result.providerName = kbc.provider?.name ?: ""
        result.providerUuid = kbc.provider?.uuid ?: ""
        result.nominalPlatform = kbc.nominalPlatform ? kbc.nominalPlatform.getLogEntityId() : ""
        result.nominalPlatformName = kbc.nominalPlatform?.name ?: ""
        result.nominalPlatformUuid = kbc.nominalPlatform?.uuid ?: ""
        result.scope = kbc.scope ? kbc.scope.value : ""
        result.breakable = kbc.breakable ? kbc.breakable.value : ""
        result.paymentType = kbc.paymentType ? kbc.paymentType.value : ""
        result.openAccess = kbc.openAccess?.value
        result.file = kbc.file?.value
        result.contentType = kbc.contentType?.value

        if (kbc.source) {
          result.source = [
            id              : kbc.source.id,
            name            : kbc.source.name,
            automaticUpdates: kbc.source.automaticUpdates,
            url             : kbc.source.url,
            frequency       : kbc.source.frequency?.value,
          ]
          if (kbc.source.lastRun){
            result.source.lastRun = dateFormatService.formatIsoTimestamp(kbc.source.lastRun)
          }
        }

        if(kbc.respondsTo('getCuratoryGroups')) {
          result.curatoryGroups = []
          kbc.curatoryGroups?.each { KBComponent cg ->
            result.curatoryGroups.add([name: cg.name,
                                       type: cg.type?.value,
                                       curatoryGroup: cg.getLogEntityId()])
          }
        }

        result.status = kbc.status?.value
        result.editingStatus = kbc.editingStatus?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName

        result.nationalRanges = []
        kbc.nationalRanges.each { nationalRange ->
          result.nationalRanges.add([value     : nationalRange.value,
                                    value_de  : nationalRange.value_de,
                                    value_en  : nationalRange.value_en])
        }

        result.regionalRanges = []
        kbc.regionalRanges.each { regionalRange ->
          result.regionalRanges.add([value     : regionalRange.value,
                                    value_de  : regionalRange.value_de,
                                    value_en  : regionalRange.value_en])
        }

        result.ddcs = []
        kbc.ddcs.each { ddc ->
          result.ddcs.add([value     : ddc.value,
                           value_de  : ddc.value_de,
                           value_en  : ddc.value_en])
        }

        result.languages = []
        kbc.languages.each { KBComponentLanguage kbl ->
          result.languages.add([value     : kbl.language.value,
                                value_de  : kbl.language.value_de,
                                value_en  : kbl.language.value_en])
        }


        result
      }

      updateES(esclient, org.gokb.cred.Org.class) { org.gokb.cred.Org kbc ->
        def result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
        result.altname = []
        result.updater = 'org'
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.roles = []
        kbc.roles.each { role ->
          result.roles.add(role.value)
        }

        if(kbc.respondsTo('getCuratoryGroups')) {
          result.curatoryGroups = []
          kbc.curatoryGroups?.each { KBComponent cg ->
            result.curatoryGroups.add([name: cg.name,
                                       type: cg.type?.value,
                                       curatoryGroup: cg.getLogEntityId()])
          }
        }

        result.status = kbc.status?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName
        result.platforms = []
        kbc.providedPlatforms?.each { plt ->
          def platform = [:]
          platform.uuid = plt.uuid ?: ""
          platform.url = plt.primaryUrl ?: ""
          platform.name = plt.name ?: ""
          result.platforms.add(platform)
        }

        result.additionalProperties = []

        kbc.additionalProperties.each { KBComponentAdditionalProperty kbComponentAdditionalProperty ->
          result.additionalProperties.add([value    : kbComponentAdditionalProperty.apValue,
                                           name      : kbComponentAdditionalProperty.propertyDefn.propertyName])
        }

        result.contacts = []
        kbc.contacts.each { Contact contact ->
          result.contacts.add([  content: contact.content,
                                 contentType: contact.contentType?.value,
                                 type: contact.type?.value,
                                 language: contact.language?.value])
        }

        result.kbartDownloaderURL = kbc.kbartDownloaderURL
        result.metadataDownloaderURL = kbc.metadataDownloaderURL

        result
      }

      updateES(esclient, org.gokb.cred.Platform.class) { org.gokb.cred.Platform kbc ->
        def result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
        result.updater = 'platform'
        result.cpname = kbc.provider?.name
        result.provider = kbc.provider ? kbc.provider.getLogEntityId() : ""
        result.providerName = kbc.provider ? kbc.provider.name : ""
        result.providerUuid = kbc.provider ? kbc.provider.uuid : ""
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)

        if(kbc.respondsTo('getCuratoryGroups')) {
          result.curatoryGroups = []
          kbc.curatoryGroups?.each { KBComponent cg ->
            result.curatoryGroups.add([name: cg.name,
                                       type: cg.type?.value,
                                       curatoryGroup: cg.getLogEntityId()])
          }
        }

        result.altname = []
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.updater = 'platform'
        result.primaryUrl = kbc.primaryUrl
        result.status = kbc.status?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName

        result.titleNamespace = kbc.titleNamespace?.value

        result.lastAuditDate = kbc.lastAuditDate ? dateFormatService.formatIsoTimestamp(kbc.lastAuditDate) : null

        result.ipAuthentication = kbc.ipAuthentication?.value

        result.shibbolethAuthentication = kbc.shibbolethAuthentication?.value

        result.passwordAuthentication = kbc.passwordAuthentication?.value

        result.statisticsFormat = kbc.statisticsFormat?.value
        result.counterR3Supported = kbc.counterR3Supported?.value
        result.counterR4Supported = kbc.counterR4Supported?.value
        result.counterR5Supported = kbc.counterR5Supported?.value
        result.counterR4SushiApiSupported = kbc.counterR4SushiApiSupported?.value
        result.counterR5SushiApiSupported = kbc.counterR5SushiApiSupported?.value
        result.counterR4SushiServerUrl = kbc.counterR4SushiServerUrl
        result.counterR5SushiServerUrl = kbc.counterR5SushiServerUrl
        result.counterRegistryUrl = kbc.counterRegistryUrl
        result.counterCertified = kbc.counterCertified?.value
        result.statisticsAdminPortalUrl = kbc.statisticsAdminPortalUrl
        result.statisticsUpdate = kbc.statisticsUpdate?.value
        result.proxySupported = kbc.proxySupported?.value
        
        result
      }

/*      updateES(esclient, org.gokb.cred.JournalInstance.class) { org.gokb.cred.JournalInstance kbc ->
        def result = null
        def current_pub = kbc.currentPublisher
        result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
        result.updater = 'journal'
        // result.publisher = kbc.currentPublisher?.name
        result.publisher = current_pub ? current_pub.getLogEntityId() : ""
        result.publisherName = current_pub?.name
        result.publisherUuid = current_pub?.uuid ?: ""
        result.altname = []
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.status = kbc.status?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName
        // log.debug("process ${result}")
        result
      }

      updateES(esclient, org.gokb.cred.DatabaseInstance.class) { org.gokb.cred.DatabaseInstance kbc ->
        def result = null
        def current_pub = kbc.currentPublisher
        result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
        // result.publisher = kbc.currentPublisher?.name
        result.publisher = current_pub ? current_pub.getLogEntityId() : ""
        result.publisherName = current_pub?.name
        result.publisherUuid = current_pub?.uuid ?: ""
        result.altname = []
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.status = kbc.status?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName
        // log.debug("process ${result}")
        result
      }

      updateES(esclient, org.gokb.cred.OtherInstance.class) { org.gokb.cred.OtherInstance kbc ->

        def result = null
        def current_pub = kbc.currentPublisher

        result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
        // result.publisher = kbc.currentPublisher?.name
        result.publisher = current_pub ? current_pub.getLogEntityId() : ""
        result.publisherName = current_pub?.name
        result.publisherUuid = current_pub?.uuid ?: ""
        result.altname = []
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.status = kbc.status?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName
        // log.debug("process ${result}")
        result
      }

      updateES(esclient, org.gokb.cred.BookInstance.class) { org.gokb.cred.BookInstance kbc ->
        def result = null
        def current_pub = kbc.currentPublisher

        result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.sortname = generateSortName(kbc.name)
//         result.publisher = kbc.currentPublisher?.name
        result.publisher = current_pub ? current_pub.getLogEntityId() : ""
        result.publisherName = current_pub?.name
        result.publisherUuid = current_pub?.uuid ?: ""
        result.altname = []
        result.updater = 'book'
        kbc.variantNames.each { vn ->
          result.altname.add(vn.variantName)
        }
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.status = kbc.status?.value
        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        result.componentType = kbc.class.simpleName
        // log.debug("process ${result}")
        result
      }*/

      updateES(esclient, org.gokb.cred.TitleInstancePackagePlatform.class) { org.gokb.cred.TitleInstancePackagePlatform kbc ->

        def result = [:]
        result._id = "${kbc.class.name}:${kbc.id}"
        result.uuid = kbc.uuid
        result.name = kbc.name
        result.componentType = kbc.class.simpleName

        result.curatoryGroups = []
        kbc.pkg?.curatoryGroups?.each { KBComponent cg ->
          result.curatoryGroups.add([name: cg.name,
                                     type: cg.type?.value,
                                       curatoryGroup: cg.getLogEntityId()])
        }

        result.titleType = kbc.niceName ?: 'Unknown'
        result.lastUpdatedDisplay = dateFormatService.formatIsoTimestamp(kbc.lastUpdated)
        result.url = kbc.url
        if (kbc.niceName == 'Journal') {
          result.coverage = []
          ArrayList<TIPPCoverageStatement> coverage_src = kbc.coverageStatements
          coverage_src.each { TIPPCoverageStatement tcs ->
            def cst = [:]
            if (tcs.startDate) cst.startDate = dateFormatService.formatIsoTimestamp(tcs.startDate)
            cst.startVolume = tcs.startVolume ?: ""
            cst.startIssue = tcs.startIssue ?: ""
            if (tcs.endDate) cst.endDate = dateFormatService.formatIsoTimestamp(tcs.endDate)
            cst.endVolume = tcs.endVolume ?: ""
            cst.endIssue = tcs.endIssue ?: ""
            cst.embargo = tcs.embargo ?: ""
            cst.coverageNote = tcs.coverageNote ?: ""
            cst.coverageDepth = tcs.coverageDepth ? tcs.coverageDepth.value : ""
            result.coverage.add(cst)
          }
        }
/*        if (kbc.niceName == 'Book') {
          // edition for eBooks
          def edition = [:]
          if (kbc.title?.editionNumber) {
            edition.number = kbc.title.editionNumber
          }
          if (kbc.title?.editionDifferentiator) {
            edition.differentiator = kbc.title.editionDifferentiator
          }
          if (kbc.title?.editionStatement) {
            edition.statement = kbc.title.editionStatement
          }
          if (!edition.isEmpty()) {
            result.titleEdition = edition
          }
          // simple eBook fields
          result.titleVolumeNumber = kbc.title?.volumeNumber ?: ""
          if (kbc.title?.dateFirstInPrint) result.titleDateFirstInPrint = dateFormatService.formatIsoTimestamp(kbc.title.dateFirstInPrint)
          if (kbc.title?.dateFirstOnline) result.titleDateFirstOnline = dateFormatService.formatIsoTimestamp(kbc.title.dateFirstOnline)
          result.titleFirstEditor = kbc.title?.firstEditor ?: ""
          result.titleFirstAuthor = kbc.title?.firstAuthor ?: ""
          result.titleImprint = kbc.title?.imprint?.name ?: ""
        }*/

        if (kbc.pkg) {
          result.tippPackage = kbc.pkg.getLogEntityId()
          result.tippPackageName = kbc.pkg.name
          result.tippPackageUuid = kbc.pkg.uuid
        }

        if (kbc.hostPlatform) {
          result.hostPlatform = kbc.hostPlatform.getLogEntityId()
          result.hostPlatformName = kbc.hostPlatform.name
          result.hostPlatformUuid = kbc.hostPlatform.uuid
        }

        // title history
        result.titleHistory = []
        // publishers
        result.titlePublishers = []
        // variant names
        result.altname = []
/*        if (kbc.title) {
          result.tippTitle = kbc.title.getLogEntityId()
          result.tippTitleName = kbc.title.name
          result.tippTitleUuid = kbc.title.uuid
          result.tippTitleMedium = kbc.title.medium.value
          kbc.title.titleHistory?.each { he ->
            if (he.date) {
              def event = [:]
              event.date = dateFormatService.formatIsoTimestamp(he.date)
              event.from = []
              if (he.from) {
                event.from.addAll(he.from.collect { fe -> [id: fe?.id, uuid: fe?.uuid, name: fe?.name] })
              }
              event.to = []
              if (he.to) {
                event.to.addAll(he.to.collect { te -> [id: te?.id, uuid: te?.uuid, name: te?.name] })
              }
              event.id = he.id ?: ""
              result.titleHistory.add(event)
            }
          }
          kbc.title.publisher?.each { pub ->
            def publisher = [:]
            publisher.name = pub.name ?: ""
            publisher.id = pub.id ?: ""
            publisher.uuid = pub.uuid ?: ""
            result.titlePublishers.add(publisher)
          }
          kbc.title.variantNames.each { vn ->
            result.altname.add(vn.variantName)
          }
        }*/
        if (kbc.medium) result.medium = kbc.medium.value
        if (kbc.status) result.status = kbc.status.value
        if (kbc.publicationType) result.publicationType = kbc.publicationType.value
        if (kbc.openAccess) result.openAccess = kbc.openAccess.value
        if (kbc.accessType) result.accessType = kbc.accessType.value

        result.identifiers = []
        kbc.getCombosByPropertyNameAndStatus('ids', 'Active').each { idc ->
          result.identifiers.add([namespace    : idc.toComponent.namespace.value,
                                  value        : idc.toComponent.value,
                                  namespaceName: idc.toComponent.namespace.name])
        }
        if (kbc.dateFirstOnline) result.dateFirstOnline = dateFormatService.formatIsoTimestamp(kbc.dateFirstOnline)
        if (kbc.dateFirstInPrint) result.dateFristInPrint = dateFormatService.formatIsoTimestamp(kbc.dateFirstInPrint)
        if (kbc.accessStartDate) result.accessStartDate = dateFormatService.formatIsoTimestamp(kbc.accessStartDate)
        if (kbc.accessEndDate) result.accessEndDate = dateFormatService.formatIsoTimestamp(kbc.accessEndDate)
        if (kbc.lastChangedExternal) result.lastChangedExternal = dateFormatService.formatIsoTimestamp(kbc.lastChangedExternal)

        if (kbc.publisherName) result.publisherName = kbc.publisherName
        if (kbc.subjectArea) result.subjectArea = kbc.subjectArea
        if (kbc.series) result.series = kbc.series
        if (kbc.volumeNumber) result.volumeNumber = kbc.volumeNumber
        if (kbc.editionStatement) result.editionStatement = kbc.editionStatement
        if (kbc.firstAuthor) result.firstAuthor = kbc.firstAuthor
        if (kbc.firstEditor) result.firstEditor = kbc.firstEditor
        if (kbc.parentPublicationTitleId) result.parentPublicationTitleId = kbc.parentPublicationTitleId
        if (kbc.precedingPublicationTitleId) result.precedingPublicationTitleId = kbc.precedingPublicationTitleId
        if (kbc.supersedingPublicationTitleId) result.supersedingPublicationTitleId = kbc.supersedingPublicationTitleId
        if (kbc.note) result.note = kbc.note

        // prices
        result.prices = []
        kbc.prices?.each { p ->
          def price = [:]
          price.type = p.priceType?.value ?: ""
          price.amount = String.valueOf(p.price) ?: ""
          price.currency = p.currency?.value ?: ""
          if (p.startDate){
            price.startDate = dateFormatService.formatIsoTimestamp(p.startDate)
          }
          if (p.endDate){
            price.endDate = dateFormatService.formatIsoTimestamp(p.endDate)
          }
          result.prices.add(price)
        }

        result.ddcs = []
        kbc.ddcs.each { ddc ->
          result.ddcs.add([value     : ddc.value,
                           value_de  : ddc.value_de,
                           value_en  : ddc.value_en])
        }

        result.languages = []
        kbc.languages.each { KBComponentLanguage kbl ->
          result.languages.add([value     : kbl.language.value,
                                value_de  : kbl.language.value_de,
                                value_en  : kbl.language.value_en])
        }

        result
      }

      updateES(esclient, wekb.DeletedKBComponent.class) { wekb.DeletedKBComponent deletedKBComponent ->

        def result = [:]
        result.recid = "${deletedKBComponent.class.name}:${deletedKBComponent.id}"
        result.uuid = deletedKBComponent.uuid
        result.name = deletedKBComponent.name
        result.componentType = deletedKBComponent.componentType
        result.status = deletedKBComponent.status.value
        result.dateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.dateCreated)
        result.lastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.lastUpdated)
        result.oldDateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldDateCreated)
        result.oldLastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldLastUpdated)
        result.oldId = deletedKBComponent.oldId

        result
      }
    }
    catch (Exception e) {
      log.error("Problem", e)
    }

    finally {
      try {
        esclient.close()
      }
      catch (Exception e) {
        log.error("Problem by Close ES Client", e)
      }
    }

    running = false
  }


  def updateES(esclient, domain, recgen_closure) {
    log.debug("updateES(${domain}...)")
    cleanUpGorm()
    def count = 0
    try {
      log.debug("updateES - ${domain.name}")
      def latest_ft_record = null
      def highest_timestamp = 0
      def highest_id = 0
      FTControl.withNewTransaction {
        latest_ft_record = FTControl.findByDomainClassNameAndActivity(domain.name, 'ESIndex')
        log.debug("result of findByDomain: ${domain} ${latest_ft_record}")
        if (!latest_ft_record) {
          latest_ft_record =
              new FTControl(domainClassName: domain.name, activity: 'ESIndex', lastTimestamp: 0, lastId: 0)
                  .save(flush: true, failOnError: true)
          log.debug("Create new FT control record, as none available for ${domain.name}")
        }
        else {
          highest_timestamp = latest_ft_record.lastTimestamp
          log.debug("Got existing ftcontrol record for ${domain.name} max timestamp is ${highest_timestamp} which is " +
              "${new Date(highest_timestamp)}")
        }
      }
      log.debug("updateES ${domain.name} since ${latest_ft_record.lastTimestamp}")

      def total = 0
      Date from = new Date(latest_ft_record.lastTimestamp)
      def countq = domain.executeQuery("select count(o.id) from " + domain.name +
          " as o where (( o.lastUpdated > :ts ) OR ( o.dateCreated > :ts )) ", [ts: from], [readonly: true])[0]
      log.debug("Will process ${countq} records")
      def q = domain.executeQuery("select o.id from " + domain.name +
          " as o where ((o.lastUpdated > :ts ) OR ( o.dateCreated > :ts )) order by o.lastUpdated, o.id", [ts: from],
          [readonly: true])
      log.debug("Query completed.. processing rows...")

      BulkRequest bulkRequest = new BulkRequest()
      // while (results.next()) {
      for (r_id in q) {
        if (Thread.currentThread().isInterrupted()) {
          log.debug("Job cancelling ..")
          running = false
          break
        }
        Object r = domain.get(r_id)
        log.debug("${r.id} ${domain.name} -- (rects)${r.lastUpdated} > (from)${from}")
        def idx_record = recgen_closure(r)
        def es_index = ESSearchService.indicesPerType.get(idx_record['componentType'])
        if (idx_record != null) {
          def recid = idx_record['_id'].toString()
          idx_record.remove('_id')

          IndexRequest request = new IndexRequest(es_index);
          request.id(recid);
          String jsonString = idx_record as JSON
          //String jsonString = JsonOutput.toJson(idx_record)
          //println(jsonString)
          request.source(jsonString, XContentType.JSON)

          bulkRequest.add(request)
        }
        if (r.lastUpdated?.getTime() > highest_timestamp) {
          highest_timestamp = r.lastUpdated?.getTime()
        }
        highest_id = r.id
        count++
        total++
        if (count > 250) {
          count = 0
          log.debug("interim:: processed ${total} out of ${countq} records (${domain.name}) - updating highest timestamp to ${highest_timestamp} interim flush")
          BulkResponse bulkResponse = esclient.bulk(bulkRequest, RequestOptions.DEFAULT)

          if (bulkResponse.hasFailures()) {
            for (BulkItemResponse bulkItemResponse : bulkResponse) {
              if (bulkItemResponse.isFailed()) {
                BulkItemResponse.Failure failure = bulkItemResponse.getFailure()
                log.debug("updateES ${domain.name}: ES Bulk operation has failure -> ${failure}")
              }
            }
          }
          log.debug("BulkResponse: ${bulkResponse}")
          FTControl.withNewTransaction {
            Long id = latest_ft_record.id
            latest_ft_record = FTControl.get(id)
            if (latest_ft_record) {
              latest_ft_record.lastTimestamp = highest_timestamp
              latest_ft_record.lastId = highest_id
              latest_ft_record.save(flush: true, failOnError: true)
            }
            else {
              log.error("Unable to locate free text control record with ID ${id}. Possibe parallel FT update")
            }
          }
          cleanUpGorm()
          synchronized (this) {
            Thread.yield()
          }
        }
      }
      if (count > 0) {
        BulkResponse bulkResponse = esclient.bulk(bulkRequest, RequestOptions.DEFAULT)

        if (bulkResponse.hasFailures()) {
          for (BulkItemResponse bulkItemResponse : bulkResponse) {
            if (bulkItemResponse.isFailed()) {
              BulkItemResponse.Failure failure = bulkItemResponse.getFailure()
              log.debug("updateES ${domain.name}: ES Bulk operation has failure -> ${failure}")
            }
          }
        }
        log.debug("Final BulkResponse: ${bulkResponse}")
      }
      // update timestamp
      FTControl.withNewTransaction {
        latest_ft_record = FTControl.get(latest_ft_record.id)
        latest_ft_record.lastTimestamp = highest_timestamp
        latest_ft_record.lastId = highest_id
        latest_ft_record.save(flush: true, failOnError: true)
      }
      cleanUpGorm()
      log.debug("final:: Processed ${total} out of ${countq} records for ${domain.name}. Max TS seen ${highest_timestamp} highest id with that TS: ${highest_id}")
    }
    catch (Exception e) {
      log.error("Problem with FT index", e)
    }
    finally {
      log.debug("Completed processing on ${domain.name} - saved ${count} records")
    }
  }


  def cleanUpGorm() {
    log.debug("Clean up GORM")
    def session = sessionFactory.currentSession
    session.flush()
    session.clear()
  }


  def clearDownAndInitES() {
    if (running == false) {
      log.debug("Remove existing FTControl ..")
      FTControl.withTransaction {
        def res = FTControl.executeUpdate("delete FTControl c")
        log.debug("Result: ${res}")
      }
      updateFTIndexes()
    }
    else {
      log.error("FTUpdate already running")
      return "Job cancelled – FTUpdate was already running!"
    }
  }


  @javax.annotation.PreDestroy
  def destroy() {
    log.debug("Destroy")
  }

  private String generateSortName(String input_title) {
    if (!input_title) return null
    String s1 = Normalizer.normalize(input_title, Normalizer.Form.NFKD).trim().toLowerCase()
   

    return s1.trim()

  }
}
