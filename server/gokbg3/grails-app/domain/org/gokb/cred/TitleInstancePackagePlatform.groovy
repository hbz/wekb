package org.gokb.cred

import de.wekb.annotations.HbzKbartAnnotation
import de.wekb.annotations.KbartAnnotation
import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.ComponentLookupService
import org.grails.web.json.JSONObject

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

  String hybridOAUrl
  String coverageNote

  @RefdataAnnotation(cat = RCConstants.TIPP_PRIMARY)
  RefdataValue primary

  @RefdataAnnotation(cat = RCConstants.TIPP_HYBRIDA_OA)
  RefdataValue hybridOA

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

  @KbartAnnotation(kbartField = 'coverage_depth' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_COVERAGE_DEPTH)
  RefdataValue coverageDepth

  @KbartAnnotation(kbartField = 'medium' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_MEDIUM)
  RefdataValue medium

  @KbartAnnotation(kbartField = 'publication_type' , type='all')
  @RefdataAnnotation(cat = RCConstants.TIPP_PUBLICATION_TYPE)
  RefdataValue publicationType


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
      'coverageNote',
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
      'languages'              : "languages"
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

  static touchOnUpdate = [
    "pkg"
  ]

  static hasByCombo = [
    pkg         : Package,
    hostPlatform: Platform,
    title       : TitleInstance,
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
    ddcs: RefdataValue

  ]

  static mappedBy = [
    coverageStatements: 'owner'
  ]

  def getPersistentId() {
    "${uuid ?: 'gokb:TIPP:' + title?.id + ':' + pkg?.id + ':' + hostPlatform?.id}"
  }

  static isTypeCreatable(boolean defaultValue = false) {
    return defaultValue;
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
    accessStartDate column: 'tipp_access_start_date'
    accessEndDate column: 'tipp_access_end_date'
    firstAuthor column: 'tipp_first_author', type: 'text'
    publicationType column: 'tipp_publication_type_rv_fk'
    volumeNumber column: 'tipp_volume_number'
    editionStatement column: 'tipp_edition_statement'
    firstEditor column: 'tipp_first_editor', type: 'text'
    parentPublicationTitleId column: 'tipp_parent_publication_id'
    precedingPublicationTitleId column: 'tipp_preceding_publication_id'
    supersedingPublicationTitleId column: 'tipp_superseding_publication_title_id'
    lastChangedExternal column: 'tipp_last_change_ext'
    medium column: 'tipp_medium_rv_fk'
    series column: 'series', type: 'text'
    url column: 'url', type: 'text'
    subjectArea column: 'subject_area', type: 'text'
    openAccess column: 'tipp_open_access_rv_fk'

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
    [[code: 'setStatus::Retired', label: 'Retire'],
     [code: 'tipp::retire', label: 'Retire (with Date)'],
     [code: 'setStatus::Deleted', label: 'Delete', perm: 'delete'],
     [code: 'setStatus::Expected', label: 'Mark Expected'],
     [code: 'setStatus::Current', label: 'Set Current'],
     [code: 'tipp::move', label: 'Move TIPP']
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

  /**
   * Create a new TIPP
   */
  static TitleInstancePackagePlatform tippCreate(tipp_fields = [:]) {

    def tipp_status = tipp_fields.status ? RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, tipp_fields.status) : null
    def tipp_editstatus = tipp_fields.editStatus ? RefdataCategory.lookup(RCConstants.KBCOMPONENT_EDIT_STATUS, tipp_fields.editStatus) : null
    RefdataValue tipp_medium = null
    if (tipp_fields.medium) {
      tipp_medium = determineMediumRef(tipp_fields)
    }
    RefdataValue tipp_publicationType = null
    if (tipp_fields.type) {
      tipp_publicationType = determinePublicationType(tipp_fields)
    }

    TitleInstancePackagePlatform result = new TitleInstancePackagePlatform(
            uuid: tipp_fields.uuid,
            status: tipp_status,
            editStatus: tipp_editstatus,
            name: tipp_fields.name,
            medium: tipp_medium,
            publicationType: tipp_publicationType,
            url: tipp_fields.url).save(failOnError: true)
    if (result) {

      def pkg_combo_type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Tipps')
      new Combo(toComponent: result, fromComponent: tipp_fields.pkg, type: pkg_combo_type).save(flush: true, failOnError: true)

      def plt_combo_type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.HostedTipps')
      new Combo(toComponent: result, fromComponent: tipp_fields.hostPlatform, type: plt_combo_type).save(flush: true, failOnError: true)

    } else {
      log.error("TIPP creation failed!")
    }

    result
  }

  @Override
  @Transient
  String getDisplayName() {
    return name ?: "${pkg?.name} / ${title?.name} / ${hostPlatform?.name}"
  }

  /**
   * Please see https://github.com/openlibraryenvironment/gokb/wiki/tipp_dto
   */
  /*@Transient
  static def validateDTO(tipp_dto, locale) {
    def result = ['valid': true]
    def errors = [:]
    def pkgLink = tipp_dto.pkg ?: tipp_dto.package
    def pltLink = tipp_dto.hostPlatform ?: tipp_dto.platform
    def tiLink = tipp_dto.title

    if (!pkgLink) {
      result.valid = false
      errors.pkg = [[message: "Missing package link!", baddata: pkgLink]]
    } else {
      def pkg = null

      if (pkgLink instanceof Map) {
        pkg = Package.get(pkgLink.id ?: pkgLink.internalId)
      } else {
        pkg = Package.get(pkgLink)
      }

      if (!pkg) {
        result.valid = false
        errors.pkg = [[message: "Could not resolve package id!", baddata: pkgLink, code: 404]]
      }
    }

    if (!pltLink) {
      result.valid = false
      errors.hostPlatform = [[message: "Missing platform link!", baddata: pltLink]]
    } else {
      def plt = null

      if (pltLink instanceof Map) {
        plt = Platform.get(pltLink.id ?: pltLink.internalId)
      } else {
        plt = Platform.get(pltLink)
      }

      if (!plt) {
        result.valid = false
        errors.hostPlatform = [[message: "Could not resolve platform id!", baddata: pltLink, code: 404]]
      }
    }

    if (!tiLink) {
      result.valid = false
      errors.title = [[message: "Missing title link!", baddata: tiLink]]
    } else {
      def ti = null

      if (tiLink instanceof Map) {
        ti = TitleInstance.get(tiLink.id ?: tiLink.internalId)
      } else {
        ti = TitleInstance.get(tiLink)
      }

      if (!ti) {
        result.valid = false
        errors.title = [[message: "Could not resolve title id!", baddata: tiLink, code: 404]]
      }
    }

    String idJsonKey = 'ids'
    def ids_list = tipp_dto[idJsonKey]
    if (!ids_list) {
      idJsonKey = 'identifiers'
      ids_list = tipp_dto[idJsonKey]
    }
    if (ids_list) {
      def id_errors = Identifier.validateDTOs(ids_list, locale)
      if (id_errors.size() > 0) {
        errors.put(idJsonKey, id_errors)
      }
    }

    if (tipp_dto.coverageStatements && !tipp_dto.coverage) {
      tipp_dto.coverage = tipp_dto.coverageStatements
    }

    for (def coverage : tipp_dto.coverage) {
      LocalDateTime parsedStart = GOKbTextUtils.completeDateString(coverage.startDate)
      LocalDateTime parsedEnd = GOKbTextUtils.completeDateString(coverage.endDate, false)

      if (coverage.startDate && !parsedStart) {
        if (!errors.startDate) {
          errors.startDate = []
        }

        result.valid = false
        errors.startDate << [message: "Unable to parse coverage start date ${coverage.startDate}!", baddata: coverage.startDate]
      }

      if (coverage.endDate && !parsedEnd) {
        if (!errors.endDate) {
          errors.endDate = []
        }

        result.valid = false
        errors.endDate << [message: "Unable to parse coverage end date ${coverage.endDate}!", baddata: coverage.endDate]
      }

      if (!coverage.coverageDepth) {
        if (!errors.coverageDepth) {
          errors.coverageDepth = []
        }
        coverage.coverageDepth = "fulltext"
        errors.coverageDepth << [message: "Missing value for coverage depth: set to fulltext", baddata: coverage.coverageDepth]
      } else {
        if (coverage.coverageDepth instanceof String && !['fulltext', 'selected articles', 'abstracts'].contains(coverage.coverageDepth?.toLowerCase())) {
          if (!errors.coverageDepth) {
            errors.coverageDepth = []
          }

          result.valid = false
          errors.coverageDepth << [message: "Unrecognized value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
        } else if (coverage.coverageDepth instanceof Integer) {
          try {
            def candidate = RefdataValue.get(coverage.coverageDepth)

            if (!candidate && candidate.owner.label == RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH) {
              if (!errors.coverageDepth) {
                errors.coverageDepth = []
              }

              result.valid = false
              errors.coverageDepth << [message: "Illegal value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
            }
          } catch (Exception e) {
            log.error("Exception $e caught in TIPP.validateDTO while coverageDepth instanceof Integer")
          }
        } else if (coverage.coverageDepth instanceof Map) {
          if (coverage.coverageDepth.id) {
            try {
              def candidate = RefdataValue.get(coverage.coverageDepth.id)

              if (!candidate && candidate.owner.label == RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH) {
                if (!errors.coverageDepth) {
                  errors.coverageDepth = []
                }

                result.valid = false
                errors.coverageDepth << [message: "Illegal ID value '${coverage.coverageDepth.id}' for coverage depth", baddata: coverage.coverageDepth]
              }
            } catch (Exception e) {
              log.error("Exception $e caught in TIPP.validateDTO while coverageDepth instanceof Map")
            }
          } else if (coverage.coverageDepth.value || coverage.coverageDepth.name) {
            if (!['fulltext', 'selected articles', 'abstracts'].contains(coverage.coverageDepth?.toLowerCase())) {
              if (!errors.coverageDepth) {
                errors.coverageDepth = []
              }

              result.valid = false
              errors.coverageDepth << [message: "Unrecognized value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
            }
          }
        }
      }

      if (parsedStart && parsedEnd && (parsedEnd < parsedStart)) {
        result.valid = false
        errors.endDate = [[message: "Coverage end date must not be prior to its start date!", baddata: coverage.endDate]]
      }
    }

    if (tipp_dto.medium) {
      RefdataValue[] media = RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM)
      if (!media*.value.contains(tipp_dto.medium))
        errors.put('medium', [message: "unknown", baddata: tipp_dto.remove('medium')])
    }

    if (tipp_dto.publicationType) {
      RefdataValue[] pubTypes = RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE)
      if (!pubTypes*.value.contains(tipp_dto.publicationType))
        errors.put('publicationType', [message: "unknown", baddata: tipp_dto.remove('publicationType')])
    }

    if (tipp_dto.dateFirstInPrint) {
      LocalDateTime dfip = GOKbTextUtils.completeDateString(tipp_dto.dateFirstInPrint, false)
      if (!dfip) {
        errors.put('dateFirstInPrint', [message: "Unable to parse", baddata: tipp_dto.remove('dateFirstInPrint')])
      }
    }

    if (tipp_dto.dateFirstOnline) {
      LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.dateFirstOnline, false)
      if (!dfo) {
        errors.put('dateFirstOnline', [message: "Unable to parse", baddata: tipp_dto.remove('dateFirstOnline')])
      }
    }

    if (tipp_dto.last_changed) {
      LocalDateTime lce = GOKbTextUtils.completeDateString(tipp_dto.lastChangedExternal, false)
      if (!lce) {
        errors.put('last_changed', [message: "Unable to parse", baddata: tipp_dto.remove('lastChangedExternal')])
      }
    }

    if (!result.valid) {
      log.warn("Tipp failed validation: ${tipp_dto} - pkg:${pkgLink} plat:${pltLink} ti:${tiLink} -- Errors: ${errors}")
    }

    if (errors.size() > 0) {
      result.errors = errors
    }
    return result
  }*/


  @Transient
  static def validateDTONew(tipp_dto, locale) {
    def pkgLink = tipp_dto.pkg ?: tipp_dto.package
    def pltLink = tipp_dto.hostPlatform ?: tipp_dto.platform
    def titleMap = tipp_dto.title
    def result = validateDTOTitleObject(titleMap, locale)
    def errors = [:]

    if (!pkgLink) {
      result.valid = false
      errors.pkg = [[message: "Missing package link!", baddata: pkgLink]]
    } else {
      def pkg = null

      if (pkgLink instanceof Map) {
        pkg = Package.get(pkgLink.id ?: pkgLink.internalId)
      } else {
        pkg = Package.get(pkgLink)
      }

      if (!pkg) {
        result.valid = false
        errors.pkg = [[message: "Could not resolve package id!", baddata: pkgLink, code: 404]]
      }
    }

    if (!pltLink) {
      result.valid = false
      errors.hostPlatform = [[message: "Missing platform link!", baddata: pltLink]]
    } else {
      def plt = null

      if (pltLink instanceof Map) {
        plt = Platform.get(pltLink.id ?: pltLink.internalId)
      } else {
        plt = Platform.get(pltLink)
      }

      if (!plt) {
        result.valid = false
        errors.hostPlatform = [[message: "Could not resolve platform id!", baddata: pltLink, code: 404]]
      }
    }

    if (!titleMap) {
      result.valid = false
      errors.title = [[message: "Missing title link!", baddata: titleMap]]
    } else {
      def ti = null

      if (titleMap instanceof Map) {
        ti = titleMap
      }

      if (!ti) {
        result.valid = false
        errors.title = [[message: "Could not resolve title!", baddata: titleMap, code: 404]]
      }
    }

    if (!tipp_dto.name) {
      result.valid = false
      errors.title = [[message: "Missing title name!", baddata: titleMap, code: 404]]
    }

    //publicationType
    if (tipp_dto.type) {
      RefdataValue publicationType = determinePublicationType(tipp_dto)
      if (!publicationType) {
        result.valid = false
        errors.title = [[message: "Unknown publicationType", baddata: tipp_dto.type, code: 404]]
      }
    }

    String idJsonKey = 'ids'
    def ids_list = tipp_dto[idJsonKey]
    if (!ids_list) {
      idJsonKey = 'identifiers'
      ids_list = tipp_dto[idJsonKey]
    }
    if (ids_list) {
      def id_errors = Identifier.validateDTOs(ids_list, locale)
      if (id_errors.size() > 0) {
        errors.put(idJsonKey, id_errors)
      }
    }

    if (tipp_dto.coverageStatements && !tipp_dto.coverage) {
      tipp_dto.coverage = tipp_dto.coverageStatements
    }

    for (def coverage : tipp_dto.coverage) {
      LocalDateTime parsedStart = GOKbTextUtils.completeDateString(coverage.startDate)
      LocalDateTime parsedEnd = GOKbTextUtils.completeDateString(coverage.endDate, false)

      if (coverage.startDate && !parsedStart) {
        if (!errors.startDate) {
          errors.startDate = []
        }

        result.valid = false
        errors.startDate << [message: "Unable to parse coverage start date ${coverage.startDate}!", baddata: coverage.startDate]
      }

      if (coverage.endDate && !parsedEnd) {
        if (!errors.endDate) {
          errors.endDate = []
        }

        result.valid = false
        errors.endDate << [message: "Unable to parse coverage end date ${coverage.endDate}!", baddata: coverage.endDate]
      }

      if (!coverage.coverageDepth) {
        if (!errors.coverageDepth) {
          errors.coverageDepth = []
        }
       /* coverage.coverageDepth = "fulltext"
        errors.coverageDepth << [message: "Missing value for coverage depth: set to fulltext", baddata: coverage.coverageDepth]*/
      } else {
        if (coverage.coverageDepth instanceof String && !['fulltext', 'selected articles', 'abstracts'].contains(coverage.coverageDepth?.toLowerCase())) {
          if (!errors.coverageDepth) {
            errors.coverageDepth = []
          }

          result.valid = false
          errors.coverageDepth << [message: "Unrecognized value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
        } else if (coverage.coverageDepth instanceof Integer) {
          try {
            def candidate = RefdataValue.get(coverage.coverageDepth)

            if (!candidate && candidate.owner.label == RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH) {
              if (!errors.coverageDepth) {
                errors.coverageDepth = []
              }

              result.valid = false
              errors.coverageDepth << [message: "Illegal value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
            }
          } catch (Exception e) {
            log.error("Exception $e caught in TIPP.validateDTO while coverageDepth instanceof Integer")
          }
        } else if (coverage.coverageDepth instanceof Map) {
          if (coverage.coverageDepth.id) {
            try {
              def candidate = RefdataValue.get(coverage.coverageDepth.id)

              if (!candidate && candidate.owner.label == RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH) {
                if (!errors.coverageDepth) {
                  errors.coverageDepth = []
                }

                result.valid = false
                errors.coverageDepth << [message: "Illegal ID value '${coverage.coverageDepth.id}' for coverage depth", baddata: coverage.coverageDepth]
              }
            } catch (Exception e) {
              log.error("Exception $e caught in TIPP.validateDTO while coverageDepth instanceof Map")
            }
          } else if (coverage.coverageDepth.value || coverage.coverageDepth.name) {
            if (!['fulltext', 'selected articles', 'abstracts'].contains(coverage.coverageDepth?.toLowerCase())) {
              if (!errors.coverageDepth) {
                errors.coverageDepth = []
              }

              result.valid = false
              errors.coverageDepth << [message: "Unrecognized value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
            }
          }
        }
      }

      if (parsedStart && parsedEnd && (parsedEnd < parsedStart)) {
        result.valid = false
        errors.endDate = [[message: "Coverage end date must not be prior to its start date!", baddata: coverage.endDate]]
      }
    }

    if (tipp_dto.dateFirstInPrint) {
      LocalDateTime dfip = GOKbTextUtils.completeDateString(tipp_dto.dateFirstInPrint, false)
      if (!dfip) {
        errors.put('dateFirstInPrint', [message: "Unable to parse", baddata: tipp_dto.remove('dateFirstInPrint')])
      }
    }

    if (tipp_dto.dateFirstOnline) {
      LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.dateFirstOnline, false)
      if (!dfo) {
        errors.put('dateFirstOnline', [message: "Unable to parse", baddata: tipp_dto.remove('dateFirstOnline')])
      }
    }

    if (tipp_dto.lastChanged) {
      LocalDateTime lce = GOKbTextUtils.completeDateString(tipp_dto.lastChanged, false)
      if (!lce) {
        errors.put('lastChanged', [message: "Unable to parse", baddata: tipp_dto.remove('lastChanged')])
      }
    }

    if (tipp_dto.accessStartDate) {
      LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.accessStartDate, false)
      if (!dfo) {
        errors.put('accessStartDate', [message: "Unable to parse", baddata: tipp_dto.remove('accessStartDate')])
      }
    }

    if (tipp_dto.accessEndDate) {
      LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.accessEndDate, false)
      if (!dfo) {
        errors.put('accessEndDate', [message: "Unable to parse", baddata: tipp_dto.remove('accessEndDate')])
      }
    }

    if (!result.valid) {
      log.warn("Tipp failed validation: ${tipp_dto} - pkg:${pkgLink} plat:${pltLink} ti:${titleMap} -- Errors: ${errors}")
    }

    if (errors.size() > 0) {
      result.errors = errors
    }
    return result
  }

  /**
   * Please see https://github.com/openlibraryenvironment/gokb/wiki/tipp_dto
   */
  @Transient
  static TitleInstancePackagePlatform upsertDTO(tipp_dto, def user = null) {
    def result = null
    log.debug("upsertDTO(${tipp_dto})")
    def pkg = null
    def plt = null
    def ti = null

    if (tipp_dto.pkg || tipp_dto.package) {
      def pkg_info = tipp_dto.package ?: tipp_dto.pkg

      if (pkg_info instanceof Map) {
        pkg = Package.get(pkg_info.id ?: pkg_info.internalId)
      } else {
        pkg = Package.get(pkg_info)
      }

      log.debug("Package lookup: ${pkg}")
    }

    if (tipp_dto.hostPlatform || tipp_dto.platform) {
      def plt_info = tipp_dto.hostPlatform ?: tipp_dto.platform

      if (plt_info instanceof Map) {
        plt = Platform.get(plt_info.id ?: plt_info.internalId)
      } else {
        plt = Platform.get(plt_info)
      }

      log.debug("Platform lookup: ${plt}")
    }

    if (tipp_dto.title) {
      def title_info = tipp_dto.title

      if (title_info instanceof Map) {
        ti = title_info
      }
    }

    def status_current = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Current')
    def status_retired = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Retired')
    def trimmed_url = tipp_dto.url ? tipp_dto.url.trim() : null
    //TODO: Moe
    def curator = pkg?.curatoryGroups?.size() > 0 ? (user.adminStatus || user.curatoryGroups?.id.intersect(pkg?.curatoryGroups?.id)) : false

    if (pkg && plt && ti && curator) {
      log.debug("See if we already have a tipp")
      def tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp, Combo as pkg_combo, Combo as platform_combo  ' +
        'where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg ' +
        'and platform_combo.toComponent=tipp and platform_combo.fromComponent = :platform ' +
        'and tipp.name in (:tiName, :tiDtoName)',
        [pkg: pkg, platform: plt, tiName: ti.name, tiDtoName: tipp_dto.name])
      def uuid_tipp = tipp_dto.uuid ? TitleInstancePackagePlatform.findByUuid(tipp_dto.uuid) : null
      TitleInstancePackagePlatform tipp = null

      //TODO: MOE
      if (uuid_tipp && uuid_tipp.pkg == pkg && uuid_tipp.hostPlatform == plt && (uuid_tipp.name == ti.name || uuid_tipp.name == tipp_dto.name)) {
        tipp = uuid_tipp
      }

      if (!tipp) {
        switch (tipps.size()) {
          case 1:
            log.debug("found")

            if (trimmed_url && trimmed_url.size() > 0) {
              if (!tipps[0].url || tipps[0].url == trimmed_url) {
                tipp = tipps[0]
              } else {
                log.debug("matched tipp has a different url..")
              }
            } else {
              tipp = tipps[0]
            }
            break;
          case 0:
            log.debug("not found");

            break;
          default:
            if (trimmed_url && trimmed_url.size() > 0) {
              tipps = tipps.findAll { !it.url || it.url == trimmed_url };
              log.debug("found ${tipps.size()} tipps for URL ${trimmed_url}")
            }

            def cur_tipps = tipps.findAll { it.status == status_current };
            def ret_tipps = tipps.findAll { it.status == status_retired };

            if (cur_tipps.size() > 0) {
              tipp = cur_tipps[0]

              log.warn("found ${cur_tipps.size()} current TIPPs!")
            } else if (ret_tipps.size() > 0) {
              tipp = ret_tipps[0]

              log.warn("found ${ret_tipps.size()} retired TIPPs!")
            } else {
              log.debug("None of the matched TIPPs are 'Current' or 'Retired'!")
            }
            break;
        }
      }

      if (!tipp) {
        log.debug("Creating new TIPP..")
        def tmap = [
          'pkg'         : pkg,
          'hostPlatform': plt,
          'url'         : trimmed_url,
          'uuid'        : (tipp_dto.uuid ?: null),
          'status'      : (tipp_dto.status ?: null),
          'name'        : (tipp_dto.name ?: null),
          'editStatus'  : (tipp_dto.editStatus ?: null),
          'type'        : (tipp_dto.type ?: null),
          'medium'    : (tipp_dto.medium ?: null),

        ]

        tipp = tippCreate(tmap)
        // Hibernate problem

        if (!tipp) {
          log.error("TIPP creation failed!")
        }
      }

      if (tipp) {
        if (tipp.isRetired() && tipp_dto.status == "Current") {
          if (tipp.accessEndDate) {
            tipp.accessEndDate = null
          }
        }

        if (tipp_dto.accessType && tipp_dto.accessType.length() > 0) {
          def access_statement
          if (tipp_dto.accessType == 'P') {
            access_statement = 'Paid'
          } else if (tipp_dto.accessType == 'F') {
            access_statement = 'Free'
          } else {
            access_statement = tipp_dto.accessType
          }
          def access_ref = RefdataCategory.lookup(RCConstants.TIPP_ACCESS_TYPE, access_statement)
          if (access_ref) tipp.accessType = access_ref
        }

        //publicationType
        if (tipp_dto.type) {
          RefdataValue publicationType = determinePublicationType(tipp_dto)
          if (!publicationType) {
            com.k_int.ClassUtils.setRefdataIfPresent(publicationType.value, tipp, 'publicationType', RCConstants.TIPP_PUBLICATION_TYPE)
          }
        }

        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'url', trimmed_url)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'name', tipp_dto.name)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'firstAuthor', tipp_dto.firstAuthor)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'firstEditor', tipp_dto.firstEditor)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'publisherName', tipp_dto.publisherName)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'volumeNumber', tipp_dto.volumeNumber)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'editionStatement', tipp_dto.editionStatement)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'series', tipp_dto.series)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'subjectArea', tipp_dto.subjectArea)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'parentPublicationTitleId', tipp_dto.parentPublicationTitleId)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'precedingPublicationTitleId', tipp_dto.precedingPublicationTitleId)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'supersedingPublicationTitleId', tipp_dto.supersedingPublicationTitleId)
        com.k_int.ClassUtils.setStringIfDifferent(tipp, 'note', tipp_dto.coverage_notes)

        com.k_int.ClassUtils.setDateIfPresent(tipp_dto.accessStartDate, tipp, 'accessStartDate')
        com.k_int.ClassUtils.setDateIfPresent(tipp_dto.accessEndDate, tipp, 'accessEndDate')
        com.k_int.ClassUtils.setDateIfPresent(tipp_dto.dateFirstInPrint, tipp, 'dateFirstInPrint')
        com.k_int.ClassUtils.setDateIfPresent(tipp_dto.dateFirstOnline, tipp, 'dateFirstOnline')
        com.k_int.ClassUtils.setDateIfPresent(tipp_dto.lastChanged, tipp, 'lastChangedExternal')

        com.k_int.ClassUtils.setRefdataIfPresent(tipp_dto.medium, tipp, 'medium', RCConstants.TITLEINSTANCE_MEDIUM)
        com.k_int.ClassUtils.setRefdataIfPresent(tipp_dto.publicationType, tipp, 'publicationType', RCConstants.TIPP_PUBLICATION_TYPE)
        com.k_int.ClassUtils.setRefdataIfPresent(tipp_dto.oaType, tipp, 'openAccess', RCConstants.TIPP_OPEN_ACCESS)

        if (tipp_dto.coverageStatements && !tipp_dto.coverage) {
          tipp_dto.coverage = tipp_dto.coverageStatements
        }

        def new_ids = []

        tipp_dto.coverage.each { c ->
          def parsedStart = GOKbTextUtils.completeDateString(c.startDate)
          def parsedEnd = GOKbTextUtils.completeDateString(c.endDate, false)

          if (c.id) {
            new_ids.add(c.id)
          }
          com.k_int.ClassUtils.setStringIfDifferent(tipp, 'coverageNote', c.coverageNote)
          com.k_int.ClassUtils.setRefdataIfPresent(c.coverageDepth, tipp, 'coverageDepth', RCConstants.TIPP_COVERAGE_DEPTH)

          def cs_match = false
          def conflict = false
          def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
          def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)
          def conflicting_statements = []

          tipp.coverageStatements?.each { tcs ->
            if (c.id && tcs.id == c.id) {

              com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startIssue', c.startIssue)
              com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endIssue', c.endIssue)
              com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startVolume', c.startVolume)
              com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endVolume', c.endVolume)
              com.k_int.ClassUtils.setDateIfPresent(parsedStart, tcs, 'startDate')
              com.k_int.ClassUtils.setDateIfPresent(parsedEnd, tcs, 'endDate')

              com.k_int.ClassUtils.setStringIfDifferent(tcs, 'embargo', c.embargo)

              com.k_int.ClassUtils.setStringIfDifferent(tcs, 'coverageNote', c.coverageNote)
              com.k_int.ClassUtils.setRefdataIfPresent(tcs.coverageDepth, tcs, 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH)

              cs_match = true
            } else if (!cs_match) {
              if (!tcs.endDate && !endAsDate) {
                conflict = true
              } else if (tcs.startVolume && tcs.startVolume == c.startVolume) {
                log.debug("Matched CoverageStatement by startVolume")
                cs_match = true
              } else if (tcs.startDate && tcs.startDate == startAsDate) {
                log.debug("Matched CoverageStatement by startDate")
                cs_match = true
              } else if (!tcs.startVolume && !tcs.startDate && !tcs.endVolume && !tcs.endDate) {
                log.debug("Matched CoverageStatement with unspecified values")
                cs_match = true
              } else if (tcs.startDate && tcs.endDate) {
                if (startAsDate && startAsDate > tcs.startDate && startAsDate < tcs.endDate) {
                  conflict = true
                } else if (endAsDate && endAsDate > tcs.startDate && endAsDate < tcs.endDate) {
                  conflict = true
                }
              }

              if (conflict) {
                conflicting_statements.add(tcs)
              } else if (cs_match) {
                com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startIssue', c.startIssue)
                com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endIssue', c.endIssue)
                com.k_int.ClassUtils.setStringIfDifferent(tcs, 'startVolume', c.startVolume)
                com.k_int.ClassUtils.setStringIfDifferent(tcs, 'endVolume', c.endVolume)
                com.k_int.ClassUtils.setDateIfPresent(parsedStart, tcs, 'startDate')
                com.k_int.ClassUtils.setDateIfPresent(parsedEnd, tcs, 'endDate')
                com.k_int.ClassUtils.setStringIfDifferent(tcs, 'embargo', c.embargo)
                com.k_int.ClassUtils.setStringIfDifferent(tcs, 'coverageNote', c.coverageNote)
                com.k_int.ClassUtils.setRefdataIfPresent(tcs.coverageDepth, tcs, 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH)
              }
            } else {
              log.debug("Matched new coverage ${c} on multiple existing coverages!")
            }
          }

          for (def cst : conflicting_statements) {
            tipp.removeFromCoverageStatements(cst)
          }

          if (!cs_match) {

            def cov_depth = null

            if (c.coverageDepth instanceof String) {
              cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
            } else if (c.coverageDepth instanceof Integer) {
              cov_depth = RefdataValue.get(c.coverageDepth)
            } else if (c.coverageDepth instanceof Map) {
              if (c.coverageDepth.id) {
                cov_depth = RefdataValue.get(c.coverageDepth.id)
              } else {
                cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, (c.coverageDepth.name ?: c.coverageDepth.value))
              }
            }

            tipp.addToCoverageStatements('startVolume': c.startVolume,
                   'startIssue': c.startIssue,
                   'endVolume': c.endVolume,
                   'endIssue': c.endIssue,
                   'embargo': c.embargo,
                   'coverageDepth': cov_depth,
                   'coverageNote': c.coverageNote,
                   'startDate': startAsDate,
                   'endDate': endAsDate
            )
          }
          // refdata setStringIfDifferent(tipp, 'coverageDepth', c.coverageDepth)
        }

        def old_cs = tipp.coverageStatements
        if (new_ids?.size() > 0) {
          for (def cs : old_cs) {
            if (!new_ids.contains(cs.id)) {
              tipp.removeFromCoverageStatements(cs)
            }
          }
        }

        if (tipp_dto.ddc) {

            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, tipp_dto.ddc)

            if(refdataValue && !(refdataValue in tipp.ddcs)){
              tipp.addToDdcs(refdataValue)
            }
        }

        if (tipp_dto.ddcs) {
            tipp_dto.ddcs.each{ String ddc ->
              RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
              if(refdataValue && !(refdataValue in tipp.ddcs)){
                tipp.addToDdcs(refdataValue)
              }
            }
        }

        if (tipp_dto.language) {
          tipp_dto.language.each{ String lan ->
            RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.KBCOMPONENT_LANGUAGE, lan)
            if(refdataValue && !(refdataValue in tipp.languages)){
              tipp.addToLanguages(refdataValue)
            }
          }
        }

        if (tipp_dto.package_ezb_anchor) {
          Identifier canonical_identifier = ComponentLookupService.lookupOrCreateCanonicalIdentifier("package_ezb_anchor", tipp_dto.package_ezb_anchor)
          if (canonical_identifier) {
              def new_id = new Combo(fromComponent: tipp, toComponent: canonical_identifier, status: RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE), type: RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'KBComponent.Ids')).save()
          }
        }

        tipp.save(flush: true, failOnError: true);
      }

      result = tipp
    } else {
      log.debug("Not able to reference TIPP: ${tipp_dto}")
    }
    result
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
          languages.each { lan ->
            builder.'language'(lan?.value.trim())
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
            'listStatus'(listStatus?.value)
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
  getTitleIds() {
    def refdata_ids = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'KBComponent.Ids');
    def status_active = RefdataCategory.lookupOrCreate(RCConstants.COMBO_STATUS, Combo.STATUS_ACTIVE)
    def result = Identifier.executeQuery("select i.namespace.value, i.value, i.namespace.family, i.namespace.name from Identifier as i, Combo as c where c.fromComponent = ? and c.type = ? and c.toComponent = i and c.status = ?", [title, refdata_ids, status_active], [readOnly: true]);
    result
  }


  @Transient
  getTitleClass() {
    def result = KBComponent.get(title.id)?.class.getSimpleName()
    result
  }

  static determineMediumRef(titleObj) {
    if (titleObj.medium) {
      switch (titleObj.medium.toLowerCase()) {
        case "a & i database":
        case "abstract- & indexdatenbank":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "A & I Database")
        case "audio":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Audio")
        case "database":
        case "fulltext database":
        case "Volltextdatenbank":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Database")
        case "dataset":
        case "datenbestand":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Dataset")
        case "film":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Film")
        case "image":
        case "bild":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Image")
        case "journal":
        case "zeitschrift":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Journal")
        case "book":
        case "buch":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Book")
        case "published score":
        case "musiknoten":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Published Score")
        case "article":
        case "artikel":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Article")
        case "software":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Software")
        case "statistics":
        case "statistiken":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Statistics")
        case "market data":
        case "marktdaten":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Market Data")
        case "standards":
        case "normen":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Standards")
        case "biography":
        case "biografie":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Biography")
        case "legal text":
        case "gesetzestext/urteil":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Legal Text")
        case "cartography":
        case "kartenwerk":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Cartography")
        case "miscellaneous":
        case "sonstiges":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Miscellaneous")
        case "other":
          return RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Other")
        default:
          return null
      }
    }
    else {
      return null
    }
  }

  private static RefdataValue determinePublicationType(tippObj) {
    if (tippObj.type) {
      switch (tippObj.type) {
        case "serial":
        case "Serial":
        case "Journal":
        case "journal":
          return RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Serial")
          break;
        case "monograph":
        case "Monograph":
        case "Book":
        case "book":
          return RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Monograph")
          break;
        case "Database":
        case "database":
          return RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Database")
          break;
        case "Other":
        case "other":
          return RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Other")
          break;
        default:
          return null
          break;
      }
    }
    else {
      return null
    }
  }

  public static def validateDTOTitleObject(JSONObject titleDTO, Locale locale) {
    def result = ['valid': true]
    def valErrors = [:]

    if (!titleDTO.name||titleDTO.name.trim()=='') {
      result.valid = false
      valErrors.put('name', [message: "missing"])
    }
    else {
      /*LocalDateTime startDate = GOKbTextUtils.completeDateString(titleDTO.publishedFrom)
      LocalDateTime endDate = GOKbTextUtils.completeDateString(titleDTO.publishedTo, false)

      if (titleDTO.publishedFrom && !startDate) {
        result.valid = false
        valErrors.put('publishedFrom', [message: "Unable to parse", baddata: titleDTO.remove('publishedFrom')])
      }

      if (titleDTO.publishedTo && !endDate) {
        result.valid = false
        valErrors.put('publishedTo', [message: "Unable to parse", baddata: titleDTO.remove('publishedTo')])
      }

      if (startDate && endDate && (endDate < startDate)) {
        valErrors.put('publishedTo', [message: "Publishing end date must not be prior to its start date!", baddata: titleDTO.publishedTo])
        // switch dates
        def tmp = titleDTO.publishedTo
        titleDTO.publishedTo = titleDTO.publishedFrom
        titleDTO.publishedFrom = tmp
      }*/

      String idJsonKey = 'ids'
      def ids_list = titleDTO[idJsonKey]

      if (!ids_list) {
        idJsonKey = 'identifiers'
        ids_list = titleDTO[idJsonKey]
      }

      def id_errors = Identifier.validateDTOs(ids_list, locale)

      if (id_errors.size() > 0) {
        valErrors.put(idJsonKey, id_errors)
        if (titleDTO[idJsonKey].size() == 0) {
          valErrors.put(idJsonKey, [message: 'no valid identifiers left'])
        }
      }
    }

    if (titleDTO.medium) {
      RefdataValue medRef = determineMediumRef(titleDTO)
      if (!medRef) {
        valErrors.put('medium', [message: "cannot parse", baddata: titleDTO.remove('medium')])
      }
    }

    if (titleDTO.language) {
      for (def lan in titleDTO.language){
        RefdataValue languageRef = RefdataCategory.lookup('KBComponent.Language', lan)
        if (!languageRef) {
          valErrors.put('language', [message: "cannot parse", baddata: titleDTO.remove('language')])
        }
      }
    }

/*    if (titleDTO.dateFirstInPrint) {
      LocalDateTime dfip = GOKbTextUtils.completeDateString(titleDTO.dateFirstInPrint, false)
      if (!dfip) {
        valErrors.put('dateFirstInPrint', [message: "Unable to parse", baddata: titleDTO.remove('dateFirstInPrint')])
      }
    }

    if (titleDTO.dateFirstOnline) {
      LocalDateTime dfo = GOKbTextUtils.completeDateString(titleDTO.dateFirstOnline, false)
      if (!dfo) {
        valErrors.put('dateFirstOnline', [message: "Unable to parse", baddata: titleDTO.remove('dateFirstOnline')])
      }
    }*/

    if (valErrors.size() > 0) {
      if (result.errors) {
        result.errors.putAll(valErrors)
      }
      else {
        result.errors = valErrors
      }
    }
    result
  }

  @Transient
  public String getTitleID(){
      String result = null
      if(pkg.source && pkg.source.targetNamespace){
        result = getIdentifierValue(pkg.source.targetNamespace.value)
      }
    return result
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

}
