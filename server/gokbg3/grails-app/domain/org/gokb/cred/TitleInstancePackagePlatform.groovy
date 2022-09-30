package org.gokb.cred

import de.wekb.annotations.HbzKbartAnnotation
import de.wekb.annotations.KbartAnnotation
import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.ComponentLookupService
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import wekb.AutoUpdateTippInfo
import wekb.KBComponentLanguage

import javax.persistence.Transient
import com.k_int.ClassUtils
import org.gokb.GOKbTextUtils
import groovy.util.logging.*

import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId

@Slf4j
class TitleInstancePackagePlatform extends KBComponent {

  def dateFormatService
  def cascadingUpdateService

  @Deprecated
  String hybridOAUrl

  @Deprecated
  String coverageNote

  @Deprecated
  @RefdataAnnotation(cat = RCConstants.TIPP_PRIMARY)
  RefdataValue primary

  @Deprecated
  @RefdataAnnotation(cat = RCConstants.TIPP_HYBRIDA_OA)
  RefdataValue hybridOA

  @Deprecated
  @RefdataAnnotation(cat = RCConstants.TIPP_DELAYED_OA)
  RefdataValue delayedOA

  @HbzKbartAnnotation(kbartField = 'oa_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_OPEN_ACCESS)
  RefdataValue openAccess

  @HbzKbartAnnotation(kbartField = 'subject_area' , type='all')
  String subjectArea

  @HbzKbartAnnotation(kbartField = 'monograph_parent_collection_title' , type='monographs')
  String series

  @KbartAnnotation(kbartField = 'publisher_name' , type='all')
  String publisherName

  @KbartAnnotation(kbartField = 'title_url' , type='all')
  String url

  @HbzKbartAnnotation(kbartField = 'access_start_date' , type='all')
  Date accessStartDate

  @HbzKbartAnnotation(kbartField = 'access_end_date' , type='all')
  Date accessEndDate

  @HbzKbartAnnotation(kbartField = 'last_changed' , type='all')
  Date lastChangedExternal

  @KbartAnnotation(kbartField = 'first_author' , type='monographs')
  String firstAuthor

  @KbartAnnotation(kbartField = 'first_editor' , type='monographs')
  String firstEditor

  @KbartAnnotation(kbartField = 'parent_publication_title_id' , type='monographs')
  String parentPublicationTitleId

  @KbartAnnotation(kbartField = 'preceding_publication_title_id' , type='serials')
  String precedingPublicationTitleId

  @HbzKbartAnnotation(kbartField = 'superseding_publication_title_id' , type='all')
  String supersedingPublicationTitleId

  @KbartAnnotation(kbartField = 'notes' , type='all')
  String note

  @KbartAnnotation(kbartField = 'monograph_volume' , type='monographs')
  String volumeNumber

  @KbartAnnotation(kbartField = 'monograph_edition' , type='monographs')
  String editionStatement

  @KbartAnnotation(kbartField = 'date_monograph_published_print' , type='monographs')
  Date dateFirstInPrint

  @KbartAnnotation(kbartField = 'date_monograph_published_online' , type='monographs')
  Date dateFirstOnline

  @KbartAnnotation(kbartField = 'access_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_ACCESS_TYPE)
  RefdataValue accessType

  @Deprecated
  @KbartAnnotation(kbartField = 'coverage_depth' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_COVERAGE_DEPTH)
  RefdataValue coverageDepth

  @KbartAnnotation(kbartField = 'medium' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_MEDIUM)
  RefdataValue medium

  @KbartAnnotation(kbartField = 'publication_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_PUBLICATION_TYPE)
  RefdataValue publicationType

  boolean fromKbartImport = false

  static transients = [ "kbartImportRunning" ]
  boolean kbartImportRunning = false


  private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd")

/*  private static refdataDefaults = [
    "delayedOA"    : "Unknown",
    "hybridOA"     : "Unknown",
    "primary"      : "No",
    "coverageDepth": "Fulltext"
  ]*/

  static jsonMapping = [
    'ignore'       : [
      'accessType',
      'delayedOA',
      'hybridOA',
      'primary',
      'coverageDepth',
      'description',
      'hybridOAUrl'
    ],
    'es'           : [
      'hostPlatformUuid'      : "hostPlatform.uuid",
      'hostPlatformName'      : "hostPlatform.name",
      'hostPlatform'          : "hostPlatform.id",
      'tippPackageUuid'       : "pkg.uuid",
      'tippPackageName'       : "pkg.name",
      'tippPackage'           : "pkg.id",
      'titleType'             : "niceName",
      'coverage'              : "coverageStatements",
      'publisherName'         : "publisherName",
      'dateFirstInPrint'      : "dateFirstInPrint",
      'dateFirstOnline'       : "dateFirstOnline",
      'firstAuthor'           : "firstAuthor",
      'publicationType'       : "publicationType",
      'volumeNumber'          : "volumeNumber",
      'editionStatement'      : "editionStatement",
      'firstEditor'           : "firstEditor",
      'parentPublicationTitleId'   : "parentPublicationTitleId",
      'precedingPublicationTitleId': "precedingPublicationTitleId",
      'supersedingPublicationTitleId':"supersedingPublicationTitleId",
      'lastChangedExternal'   : "lastChangedExternal",
      'medium'                : "medium",
      'languages'              : "languages",
      'accessType'              : "accessType",
      'openAccess'              : "openAccess"
    ],
    'defaultLinks' : [
      'pkg',
      'title',
      'hostPlatform'
    ],
    'defaultEmbeds': [
      'coverageStatements'
    ]
  ]

  static hasByCombo = [
    pkg         : Package,
    hostPlatform: Platform,
    derivedFrom : TitleInstancePackagePlatform,
    masterTipp  : TitleInstancePackagePlatform,
  ]

  static mappedByCombo = [
    pkg                : 'tipps',
    hostPlatform       : 'hostedTipps',
    additionalPlatforms: 'linkedTipps',
    title              : 'tipps',
    derivatives        : 'derivedFrom'
  ]

  static manyByCombo = [
    derivatives        : TitleInstancePackagePlatform,
    additionalPlatforms: Platform,
  ]

  static hasMany = [
    coverageStatements: TIPPCoverageStatement,
    ddcs: RefdataValue,
    ids: Identifier,
    autoUpdateTippInfos: AutoUpdateTippInfo

  ]

  static mappedBy = [
    coverageStatements: 'owner'
  ]

  def getPersistentId() {
    "${uuid ?: 'wekb:TIPP:' + title?.id + ':' + pkg?.id + ':' + hostPlatform?.id}"
  }

  static mapping = {
    includes KBComponent.mapping
    coverageDepth column: 'tipp_coverage_depth'
    coverageNote column: 'tipp_coverage_note', type: 'text'
    note column: 'tipp_note', type: 'text'
    delayedOA column: 'tipp_delayed_oa'
    hybridOA column: 'tipp_hybrid_oa'
    hybridOAUrl column: 'tipp_hybrid_oa_url'
    primary column: 'tipp_primary'
    accessType column: 'tipp_access_type'
    accessStartDate column: 'tipp_access_start_date', index: 'tipp_access_start_date_idx'
    accessEndDate column: 'tipp_access_end_date', index: 'tipp_access_end_date_idx'
    firstAuthor column: 'tipp_first_author', type: 'text', index: 'tipp_first_author_idx'
    publicationType column: 'tipp_publication_type_rv_fk', index: 'tipp_publication_type_idx'
    volumeNumber column: 'tipp_volume_number'
    editionStatement column: 'tipp_edition_statement'
    firstEditor column: 'tipp_first_editor', type: 'text'
    parentPublicationTitleId column: 'tipp_parent_publication_id', index: 'tipp_parent_publication_type_idx'
    precedingPublicationTitleId column: 'tipp_preceding_publication_id', index: 'tipp_preceding_publication_type_idx'
    supersedingPublicationTitleId column: 'tipp_superseding_publication_title_id', index: 'tipp_superseding_publication_type_idx'
    lastChangedExternal column: 'tipp_last_change_ext', index: 'tipp_last_changed_ext_idx'
    medium column: 'tipp_medium_rv_fk', index: 'tipp_medium_idx'
    series column: 'series', type: 'text'
    url column: 'url', type: 'text', index: 'tipp_url_idx'
    subjectArea column: 'subject_area', type: 'text', index: 'tipp_subject_area_idx'
    openAccess column: 'tipp_open_access_rv_fk', index: 'tipp_open_access_idx'

    fromKbartImport column: 'tipp_from_kbart_import'

    ddcs             joinTable: [
            name:   'tipp_dewey_decimal_classification',
            key:    'tipp_fk',
            column: 'ddc_rv_fk', type:   'BIGINT'
    ], lazy: false
  }

  static constraints = {
    coverageDepth(nullable: true, blank: true)
    coverageNote(nullable: true, blank: true)
    note(nullable: true, blank: true)
    delayedOA(nullable: true, blank: true)
    hybridOA(nullable: true, blank: true)
    hybridOAUrl(nullable: true, blank: true)
    primary(nullable: true, blank: true)
    accessType (nullable: true, blank: true)
    accessStartDate(nullable: true, blank: false)
    accessEndDate(validator: { val, obj ->
      if (obj.accessStartDate && val && (obj.hasChanged('accessEndDate') || obj.hasChanged('accessStartDate')) && obj.accessStartDate > val) {
        return ['accessEndDate.endPriorToStart']
      }
    })
    url(nullable: true, blank: true)
    firstAuthor(nullable: true, blank: true)
    publicationType(nullable: true, blank: true)
    volumeNumber(nullable: true, blank: true)
    editionStatement(nullable: true, blank: true)
    firstEditor(nullable: true, blank: true)
    parentPublicationTitleId(nullable: true, blank: true)
    precedingPublicationTitleId(nullable: true, blank: true)
    supersedingPublicationTitleId (nullable: true, blank: true)
    lastChangedExternal(nullable: true, blank: true)
    medium(nullable: true, blank: true)
    ddcs(nullable: true)
  }

  public static final String restPath = "/package-titles"

  def availableActions() {
    [[code: 'setStatus::Retired', label: 'Mark Title Retire'],
     /*[code: 'tipp::retire', label: 'Retire (with Date)'],*/
     [code: 'setStatus::Deleted', label: 'Mark Title Delete', perm: 'delete'],
     [code: 'setStatus::Removed', label: 'Remove Title', perm: 'delete'],
     [code: 'setStatus::Expected', label: 'Mark Title Expected'],
     [code: 'setStatus::Current', label: 'Mark Titel Current'],
     /*[code: 'tipp::move', label: 'Move TIPP']*/
    ]
  }

  @Transient
  def getPermissableCombos() {
    [
    ]
  }

  @Override
  String getNiceName() {
    if (publicationType) {
      switch (publicationType) {
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Serial"):
          return "Journal"
          break;
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Monograph"):
          return "Book"
          break;
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Database"):
          return "Database"
          break;
        case RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Other"):
          return "Other"
          break;
        default:
          return "Title"
          break;
      }
    }
    else {
      return "Title"
    }
  }



  @Override
  @Transient
  String getDisplayName() {
    return name ?: "${pkg?.name} / ${title?.name} / ${hostPlatform?.name}"
  }






  @Transient
  static def oaiConfig = [
    id             : 'tipps',
    textDescription: 'TIPP repository for GOKb',
    pkg            : 'Package.Tipps',
    query          : " from TitleInstancePackagePlatform as o ",
    pageSize       : 10
  ]

  /**
   *  Render this tipp as OAI_dc
   */
  @Transient
  def toOaiDcXml(builder, attr) {
    builder.'dc'(attr) {
      'dc:title'(title.name)
    }
  }

  /**
   *  Render this TIPP as GoKBXML
   */
  @Transient
  def toGoKBXml(builder, attr) {
    def linked_pkg = getPkg()
    def ti = getTitle()

    builder.'gokb'(attr) {
      builder.'tipp'([id: (id), uuid: (uuid)]) {

        addCoreGOKbXmlFields(builder, attr)
        builder.'lastUpdated'(lastUpdated ? dateFormatService.formatIsoTimestamp(lastUpdated) : null)
        builder.'type'(titleClass)
        builder.'url'(url ?: "")
        builder.'subjectArea'(subjectArea?.trim())
        builder.'series'(series?.trim())
        builder.'publisherName'(publisherName?.trim())
        builder.'dateFirstInPrint'(dateFirstInPrint?.trim())
        builder.'dateFirstOnline'(dateFirstOnline?.trim())
        builder.'firstAuthor'(firstAuthor?.trim())
        builder.'publicationType'(publicationType.value.trim())
        builder.'volumeNumber'(volumeNumber?.trim())
        builder.'editionStatement'(editionStatement?.trim())
        builder.'firstEditor'(firstEditor?.trim())
        builder.'parentPublicationTitleId'(parentPublicationTitleId?.trim())
        builder.'precedingPublicationTitleId'(precedingPublicationTitleId?.trim())
        builder.'supersedingPublicationTitleId'(supersedingPublicationTitleId?.trim())
        builder.'lastChangedExternal'(lastChangedExternal?.trim())
        builder.'medium'(medium?.value.trim())
        builder.'languages' {
          languages.each {KBComponentLanguage lan ->
            builder.'language'(lan.language.value.trim())
          }
        }
        builder.'title'([id: ti.id, uuid: ti.uuid]) {
          builder.'name'(ti.name?.trim())
          builder.'type'(titleClass)
          builder.'status'(ti.status?.value)
          builder.'identifiers' {
            titleIds.each { tid ->
              builder.'identifier'([namespace: tid[0], namespaceName: tid[3], value: tid[1], type: tid[2]])
            }
          }
        }
        builder.'package'([id: linked_pkg.id, uuid: linked_pkg.uuid]) {
          linked_pkg.with {
            addCoreGOKbXmlFields(builder, attr)

            'scope'(scope?.value)
            'openAccess'(openAccess?.value)
            'file'(file?.value)
            'breakable'(breakable?.value)
            'consistent'(consistent?.value)
            'accessType'(accessType?.value)
            'globalNote'(globalNote)
            'contentType'(contentType?.value)
            'lastUpdated'(lastUpdated ? dateFormatService.formatIsoTimestamp(lastUpdated) : null)
            if (provider) {
              builder.'provider'([id: provider?.id, uuid: provider?.uuid]) {
                'name'(provider?.name)
                'mission'(provider?.mission?.value)
              }
            } else {
              builder.'provider'()
            }
            if (nominalPlatform) {
              builder.'nominalPlatform'([id: nominalPlatform?.id, uuid: nominalPlatform?.uuid]) {
                'name'(nominalPlatform.name?.trim())
                'primaryUrl'(nominalPlatform.primaryUrl?.trim())
              }
            } else {
              builder.'nominalPlatform'()
            }
            builder.'curatoryGroups' {
              pkg.curatoryGroups.each { cg ->
                builder.'group' {
                  builder.'name'(cg.name)
                }
              }
            }
          }
        }
        builder.'platform'([id: hostPlatform.id, uuid: hostPlatform.uuid]) {
          'primaryUrl'(hostPlatform.primaryUrl?.trim())
          'name'(hostPlatform.name?.trim())
        }
        'access'([start: (accessStartDate ? dateFormatService.formatIsoTimestamp(accessStartDate) : null), end: (accessEndDate ? dateFormatService.formatIsoTimestamp(accessEndDate) : null)])
        def cov_statements = getCoverageStatements()
        if (cov_statements?.size() > 0) {
          cov_statements.each { tcs ->
            'coverage'(
              startDate: (tcs.startDate ? dateFormatService.formatIsoTimestamp(tcs.startDate) : null),
              startVolume: (tcs.startVolume),
              startIssue: (tcs.startIssue),
              endDate: (tcs.endDate ? dateFormatService.formatIsoTimestamp(tcs.endDate) : null),
              endVolume: (tcs.endVolume),
              endIssue: (tcs.endIssue),
              coverageDepth: (tcs.coverageDepth?.value ?: null),
              coverageNote: (tcs.coverageNote),
              embargo: (tcs.embargo)
            )
          }
        }
        if (prices && prices.size() > 0) {
          builder.'prices'() {
            prices.each { price ->
              builder.'price' {
                builder.'type'(price.priceType.value)
                builder.'amount'(price.price)
                builder.'currency'(price.currency)
                builder.'startDate'(price.startDate)
                if (price.endDate) {
                  builder.'endDate'(price.endDate)
                }
              }
            }
          }
        }
      }
    }
  }


  @Transient
  getTitleClass() {
    def result = KBComponent.get(title.id)?.class.getSimpleName()
    result
  }




  @Transient
  public String getTitleID(){
      String result = null
      if(pkg.source && pkg.source.targetNamespace){
        result = getIdentifierValue(pkg.source.targetNamespace.value)
      }else if(hostPlatform.titleNamespace){
        result = getIdentifierValue(hostPlatform.titleNamespace.value)
      }
    return result
  }

  @Transient
  public String getPrintIdentifier(){
    ids?.findAll{ it.namespace.value.toLowerCase() in ["issn", "pisbn"]}?.value.join(';')
  }

  @Transient
  public String getOnlineIdentifier(){
    ids?.findAll{ it.namespace.value.toLowerCase() in ["eissn", "isbn"]}?.value.join(';')
  }

  @Transient
  public String getListPriceInEUR(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'EUR')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getListPriceInUSD(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'USD')

    return retrievePriceOfCategory(listType, currency)
  }


  @Transient
  public String getListPriceInGBP(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'GBP')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getOAAPCPriceInEUR(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'EUR')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String getOAAPCPriceInUSD(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'USD')

    return retrievePriceOfCategory(listType, currency)
  }


  @Transient
  public String getOAAPCPriceInGBP(){
    RefdataValue listType = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    RefdataValue currency = RefdataCategory.lookup(RCConstants.CURRENCY, 'GBP')

    return retrievePriceOfCategory(listType, currency)
  }

  @Transient
  public String retrievePriceOfCategory(RefdataValue listType, RefdataValue currency){
    String result = null

    ComponentPrice existPrice = ComponentPrice.findWhere(owner: this, priceType: listType, currency: currency)

    if(existPrice){
      result = existPrice.price.toString()
    }
    return result

  }

  @Transient
  String getIdentifierValue(idtype){
    // Null returned if no match.
    ids?.find{ it.namespace.value.toLowerCase() == idtype.toLowerCase() }?.value
  }

  def afterInsert (){
    log.debug("afterSave for ${this}")
    cascadingUpdateService.update(this, dateCreated)

  }

  def beforeDelete (){
    log.debug("beforeDelete for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  def afterUpdate(){
    log.debug("afterUpdate for ${this}")
    cascadingUpdateService.update(this, lastUpdated)

  }

  @Transient
  public String getDomainName() {
    return "Title"
  }

}
