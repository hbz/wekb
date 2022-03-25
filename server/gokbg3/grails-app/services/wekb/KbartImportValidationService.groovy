package wekb

import de.wekb.helper.RCConstants
import gokbg3.MessageService
import grails.gorm.transactions.Transactional
import org.gokb.GOKbTextUtils
import org.gokb.cred.Identifier
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.Package
import org.gokb.cred.Platform
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject

import java.time.LocalDateTime

@Transactional
class KbartImportValidationService {

    MessageService messageService
    KbartImportService kbartImportService

    def identifierValidateDTOs(JSONArray identifierDTOs, Locale locale) {
        def id_errors = [:]
        def to_remove = []
        identifierDTOs.each { idobj ->
            def id_def = [:]
            def ns_obj = null
            if (idobj instanceof Map) {
                def id_ns = idobj.type ?: (idobj.namespace ?: null)

                id_def.value = idobj.value

                if (id_ns instanceof String) {
                    log.debug("Default namespace handling for ${id_ns}..")
                    ns_obj = IdentifierNamespace.findByValueIlike(id_ns)
                }
                else if (id_ns) {
                    log.debug("Handling namespace def ${id_ns}")
                    ns_obj = IdentifierNamespace.get(id_ns)
                }

                if (!ns_obj) {
                    id_errors.put('namespace', [message: messageService.resolveCode('default.not.found.message', ["Namespace", id_ns], locale), baddata: id_ns])
                    to_remove.add(idobj)
                }
                else {
                    id_def.type = ns_obj.value
                }
            }
            else {
                log.warn("Missing information in id object ${idobj}")
                id_errors.put(idobj.type, [message: "missing information", baddata: idobj.value])
                to_remove.add(idobj)
            }

            if (ns_obj && id_def.size() > 0) {
                if (!Identifier.findByTippIsNotNullAndValueAndNamespace(id_def.value, ns_obj)) {
                    if (ns_obj.pattern && !(id_def.value ==~ ns_obj.pattern)) {
                        log.warn("Validation for ${id_def.type}:${id_def.value} failed!")
                        id_errors.put(idobj.type, [message: "validation failed", baddata: idobj.value])
                        to_remove.add(idobj)
                    }
                    else {
                        log.debug("New identifier ..")
                    }
                }
                else {
                    log.debug("Found existing identifier ..")
                }
            }
        }
        identifierDTOs.removeAll(to_remove)
        return id_errors
    }

    /**
     * Definitive rules for a valid package header
     */
    def packagevalidateDTO(packageHeaderDTO, locale) {
        def result = [valid: true, errors: [:], match: false]

        if (!packageHeaderDTO.name?.trim()) {
            result.valid = false
            result.errors.name = [[message: messageService.resolveCode('crossRef.package.error.name', null, locale), baddata: packageHeaderDTO.name]]
        }

/*        String idJsonKey = 'ids'
        def ids_list = packageHeaderDTO[idJsonKey]
        if (!ids_list) {
            idJsonKey = 'identifiers'
            ids_list = packageHeaderDTO[idJsonKey]
        }
        if (ids_list) {
            def id_errors = identifierValidateDTOs(ids_list, locale)
            if (id_errors.size() > 0) {
                result.errors.put(idJsonKey, id_errors)
            }
        }*/

/*        if (packageHeaderDTO.provider && packageHeaderDTO.provider instanceof Integer) {
            def prov = Org.get(packageHeaderDTO.provider)

            if (!prov) {
                result.errors.provider = [[message: messageService.resolveCode('crossRef.error.lookup', ["Provider", "ID"], locale), code: 404, baddata: packageHeaderDTO.provider]]
                result.valid = false
            }
        }

        if (packageHeaderDTO.nominalPlatform && packageHeaderDTO.nominalPlatform instanceof Integer) {
            def prov = Platform.get(packageHeaderDTO.nominalPlatform)

            if (!prov) {
                result.errors.nominalPlatform = [[message: messageService.resolveCode('crossRef.error.lookup', ["Platform", "ID"], locale), code: 404, baddata: packageHeaderDTO.nominalPlatform]]
                result.valid = false
            }
        }*/

        /*if (result.valid) {
            def status_deleted = RDStore.KBC_STATUS_DELETED
            def pkg_normname = Package.generateNormname(packageHeaderDTO.name)

            def name_candidates = Package.executeQuery("from Package as p where p.normname = ? and p.status <> ?", [pkg_normname, status_deleted])

            if (packageHeaderDTO.uuid) {
                result.match = Package.findByUuid(packageHeaderDTO.uuid) ? true : false
            }

            if (!result.match && name_candidates.size() == 1) {
                result.match = true
            }

            if (!result.match) {
                def variant_normname = GOKbTextUtils.normaliseString(packageHeaderDTO.name)
                def variant_candidates = Package.executeQuery("select distinct p from Package as p join p.variantNames as v where v.normVariantName = ? and p.status <> ? ", [variant_normname, status_deleted])

                if (variant_candidates.size() == 1) {
                    result.match = true
                    log.debug("Package matched via existing variantName.")
                }
            }

            if (!result.match) {
                log.debug("Did not find a match via existing variantNames, trying supplied variantNames..")
                packageHeaderDTO.variantNames.each {

                    if (it.trim().size() > 0) {
                        def var_pkg = Package.findByName(it)

                        if (var_pkg) {
                            log.debug("Found existing package name for variantName ${it}")
                        }
                        else {

                            def variant_normname = GOKbTextUtils.normaliseString(it)
                            def variant_candidates = Package.executeQuery("select distinct p from Package as p join p.variantNames as v where v.normVariantName = ? and p.status <> ? ", [variant_normname, status_deleted])

                            if (variant_candidates.size() == 1) {
                                log.debug("Found existing package variant name for variantName ${it}")
                                result.match = true
                            }
                        }
                    }
                }
            }
        }*/

        result
    }

    def platformValidateDTO(platformDTO) {
        def result = ['valid': true, 'errors': [:]]

        if (platformDTO.name?.trim()) {
        } else {
            result.valid = false
            result.errors.name = [[message: "Platform name is missing!", baddata: (platformDTO.name ?: null)]]
        }

        if (!result.valid) {
            log.error("platform failed validation ${platformDTO}")
        }

        result
    }

    def tippValidateDTONew(tipp_dto, locale) {
        def pkgLink = tipp_dto.pkg ?: tipp_dto.package
        def pltLink = tipp_dto.hostPlatform ?: tipp_dto.platform
        //def titleMap = tipp_dto.title
        def result = ['valid': true]
        //def result = validateDTOTitleObject(titleMap, locale)
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

/*        if (!titleMap) {
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
        }*/

        if (!tipp_dto.name) {
            result.valid = false
            errors.title = [[message: "Missing title name!", baddata: tipp_dto, code: 404]]
        }

        //publicationType
        if (tipp_dto.type) {
            RefdataValue publicationType = kbartImportService.determinePublicationType(tipp_dto)
            if (!publicationType) {
                result.valid = false
                errors.title = [[message: "Unknown publicationType", baddata: tipp_dto.type, code: 404]]
            }
        }else {
            result.valid = false
            errors.title = [[message: "No publication type set", baddata: tipp_dto, code: 404]]
        }

        String idJsonKey = 'ids'
        def ids_list = tipp_dto[idJsonKey]
        if (!ids_list) {
            idJsonKey = 'identifiers'
            ids_list = tipp_dto[idJsonKey]
        }
        if (ids_list) {
            def id_errors = identifierValidateDTOs(ids_list, locale)
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

                //result.valid = false
                errors.startDate << [message: "Unable to parse coverage start date ${coverage.startDate}!", baddata: coverage.startDate]
            }

            if (coverage.endDate && !parsedEnd) {
                if (!errors.endDate) {
                    errors.endDate = []
                }

                //result.valid = false
                errors.endDate << [message: "Unable to parse coverage end date ${coverage.endDate}!", baddata: coverage.endDate]
            }

            if (!coverage.coverageDepth) {
                if (tipp_dto.type != "other" && !errors.coverageDepth) {
                    errors.coverageDepth = []
                }
                /* coverage.coverageDepth = "fulltext"
                 errors.coverageDepth << [message: "Missing value for coverage depth: set to fulltext", baddata: coverage.coverageDepth]*/
            } else {
                if (coverage.coverageDepth instanceof String && !['fulltext', 'full text', 'selected articles', 'abstracts'].contains(coverage.coverageDepth?.toLowerCase())) {
                    if (!errors.coverageDepth) {
                        errors.coverageDepth = []
                    }

                    //result.valid = false
                    errors.coverageDepth << [message: "Unrecognized value '${coverage.coverageDepth}' for coverage depth", baddata: coverage.coverageDepth]
                } else if (coverage.coverageDepth instanceof Integer) {
                    try {
                        def candidate = RefdataValue.get(coverage.coverageDepth)

                        if (!candidate && candidate.owner.label == RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH) {
                            if (!errors.coverageDepth) {
                                errors.coverageDepth = []
                            }

                            //result.valid = false
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

                                //result.valid = false
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

                            //result.valid = false
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
                errors.put('dateFirstInPrint', [message: "Unable to parse date", baddata: tipp_dto.remove('dateFirstInPrint')])
            }
        }

        if (tipp_dto.dateFirstOnline) {
            LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.dateFirstOnline, false)
            if (!dfo) {
                errors.put('dateFirstOnline', [message: "Unable to parse date", baddata: tipp_dto.remove('dateFirstOnline')])
            }
        }

        if (tipp_dto.lastChanged) {
            LocalDateTime lce = GOKbTextUtils.completeDateString(tipp_dto.lastChanged, false)
            if (!lce) {
                errors.put('lastChanged', [message: "Unable to parse date", baddata: tipp_dto.remove('lastChanged')])
            }
        }

        if (tipp_dto.accessStartDate) {
            LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.accessStartDate, false)
            if (!dfo) {
                errors.put('accessStartDate', [message: "Unable to parse date", baddata: tipp_dto.remove('accessStartDate')])
            }
        }

        if (tipp_dto.accessEndDate) {
            LocalDateTime dfo = GOKbTextUtils.completeDateString(tipp_dto.accessEndDate, false)
            if (!dfo) {
                errors.put('accessEndDate', [message: "Unable to parse date", baddata: tipp_dto.remove('accessEndDate')])
            }
        }

        if (!result.valid) {
            log.warn("Tipp failed validation: ${tipp_dto} - pkg:${pkgLink} plat:${pltLink} -- Errors: ${errors}")
        }

        if (errors.size() > 0) {
            result.errors = errors
        }
        return result
    }

    /**
     * Please see https://github.com/openlibraryenvironment/gokb/wiki/tipp_dto
     */
    /*@Transient
    static def tippvalidateDTO(tipp_dto, locale) {
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
        def id_errors = identifierValidateDTOs(ids_list, locale)
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
        RefdataValue[] media = RefdataCategory.lookup(RCConstants.TIPP_MEDIUM)
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

    @Deprecated
    def validateDTOTitleObject(JSONObject titleDTO, Locale locale) {
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

            def id_errors = identifierValidateDTOs(ids_list, locale)

            if (id_errors.size() > 0) {
                valErrors.put(idJsonKey, id_errors)
                if (titleDTO[idJsonKey].size() == 0) {
                    valErrors.put(idJsonKey, [message: 'no valid identifiers left'])
                }
            }
        }

        if (titleDTO.medium) {
            RefdataValue medRef = kbartImportService.determineMediumRef(titleDTO)
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
}
