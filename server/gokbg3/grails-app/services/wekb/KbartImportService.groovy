package wekb

import com.k_int.ClassUtils
import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import gokbg3.DateFormatService
import grails.gorm.transactions.Transactional
import org.gokb.ComponentLookupService
import org.gokb.GOKbTextUtils
import org.gokb.cred.Combo
import org.gokb.cred.Identifier
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.KBComponent
import org.gokb.cred.Org
import org.gokb.cred.Package
import org.gokb.cred.Platform
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.gokb.cred.TIPPCoverageStatement
import org.gokb.cred.TitleInstancePackagePlatform
import org.grails.web.json.JSONArray


import java.time.ZoneId

@Transactional
class KbartImportService {

    DateFormatService dateFormatService

    Platform platformUpsertDTO(platformDTO) {
        // Ideally this should be done on platformUrl, but we fall back to name here

        def result = null
        Boolean skip = false
        RefdataValue status_current = RDStore.KBC_STATUS_CURRENT
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        ArrayList name_candidates = []
        ArrayList url_candidates = []
        Boolean changed = false
        Boolean viable_url = false

        if(platformDTO.oid) {
            List platform_id_components = platformDTO.oid.split(':')
            if (platform_id_components.size() == 2) {
                result = Platform.get(Long.parseLong(platform_id_components[1].trim()))
            }
        }
        if (platformDTO.uuid) {
            result = Platform.findByUuid(platformDTO.uuid)
        }
        if (result) {
            //changed |= com.k_int.ClassUtils.setStringIfDifferent(result, 'name', platformDTO.name)
        } else {
            if (platformDTO.name.startsWith("http")) {
                try {
                    log.debug("checking if platform name is an URL..")

                    def url_as_name = new URL(platformDTO.name)

                    if (url_as_name.getProtocol()) {
                        if (!platformDTO.primaryUrl || !platformDTO.primaryUrl.trim()) {
                            log.debug("identified URL as platform name")
                            platformDTO.primaryUrl = platformDTO.name
                        }
                        platformDTO.name = url_as_name.getHost()

                        if (platformDTO.name.startsWith("www.")) {
                            platformDTO.name = platformDTO.name.substring(4)
                        }

                        log.debug("New platform name is ${platformDTO.name}.")
                    }
                } catch (MalformedURLException) {
                    log.debug("Platform name is no valid URL")
                }
            }

            name_candidates = Platform.executeQuery("from Platform where name = ? and status != ? ", [platformDTO.name, status_deleted])

            if (name_candidates.size() == 0) {
                log.debug("No platforms matched by name!")

                def variant_normname = GOKbTextUtils.normaliseString(platformDTO.name)

                def varname_candidates = Platform.executeQuery("select distinct pl from Platform as pl join pl.variantNames as v where v.normVariantName = ? and pl.status = ? ", [variant_normname, status_current])

                if (varname_candidates.size() == 1) {
                    log.debug("Platform matched by variant name!")
                    result = varname_candidates[0]
                }

            } else if (name_candidates.size() == 1 && name_candidates[0].status == status_current) {
                log.debug("Platform ${platformDTO.name} matched by name!")
                result = name_candidates[0]
            } else {
                log.warn("Could not match a specific current platform for ${platformDTO.name}!")
            }

            if (!result && platformDTO.primaryUrl && platformDTO.primaryUrl.trim().size() > 0) {
                try {
                    def inc_url = new URL(platformDTO.primaryUrl)
                    def other_candidates = []

                    if (inc_url) {
                        viable_url = true
                        String urlHost = inc_url.getHost()

                        if (urlHost.startsWith("www.")) {
                            urlHost = urlHost.substring(4)
                        }

                        def platform_crit = Platform.createCriteria()

                        //TODO: MOE Matching
                        url_candidates = platform_crit.list {
                            or {
                                like("name", "${urlHost}")
                                like("primaryUrl", "%${urlHost}%")
                            }
                        }
                    }
                } catch (MalformedURLException ex) {
                    log.error("URL of ingest Platform ${platformDTO} is broken!")
                }
            }

            if (!result && viable_url) {
                log.debug("Trying to match platform by primary URL..")

                if (url_candidates.size() == 0) {
                    log.debug("Could not match an existing platform!")
                } else if (url_candidates.size() == 1) {
                    log.debug("Matched existing platform by URL!")
                    result = url_candidates[0]
                } else if (url_candidates.size() > 1) {
                    log.warn("Matched multiple platforms by URL!")

                    def current_platforms = url_candidates.findAll { it.status == status_current }

                    if (current_platforms.size() == 1) {
                        result = current_platforms[0]
                    } else if (current_platforms.size() == 0) {
                        log.error("Matched only non-current platforms by URL!")
                        result = url_candidates[0]
                    } else {

                        // Picking randomly from multiple results is bad, but right now a result is always expected. Maybe this should be skipped...
                        // skip = true

                        log.error("Multiple matched current platforms: ${current_platforms}")
                        result = current_platforms[0]
                    }
                }
            }

            if (result && !result.primaryUrl) {
                result.primaryUrl = platformDTO.primaryUrl
                result.save(flush: true, failOnError: true)
            }

            /*if (!result && !skip) {
                log.debug("Creating new platform for: ${platformDTO}")
                result = new Platform(name: platformDTO.name, normname: KBComponent.generateNormname(platformDTO.name), primaryUrl: (viable_url ? platformDTO.primaryUrl : null), uuid: platformDTO.uuid ?: null).save(flush: true, failOnError: true)
                
            }*/
        }

        result
    }

    Package packageUpsertDTO(packageHeaderDTO) {
        log.info("Upsert package with header ${packageHeaderDTO}")
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        String pkg_normname = Package.generateNormname(packageHeaderDTO.name)

        //log.debug("Checking by normname ${pkg_normname} ..")
        //List name_candidates = Package.executeQuery("from Package as p where p.normname = ? and p.status <> ?", [pkg_normname, status_deleted])
        //ArrayList full_matches = []
        //Boolean created = false
        Package result = packageHeaderDTO.uuid ? Package.findByUuid(packageHeaderDTO.uuid) : null
        boolean changed = false

        /*if (!result && name_candidates.size() > 0 && packageHeaderDTO.identifiers?.size() > 0) {
            log.debug("Got ${name_candidates.size()} matches by name. Checking against identifiers!")
            name_candidates.each { mp ->
                if (mp.ids.size() > 0) {
                    def id_match = false

                    packageHeaderDTO.identifiers.each { rid ->

                        //TODO: MOE
                        IdentifierNamespace namespace = IdentifierNamespace.findByValue(rid.type)
                        Identifier the_id = namespace ? Identifier.findByValueAndNamespace(rid.value, namespace) : null

                        if (the_id && mp.ids.contains(the_id)) {
                            id_match = true
                        }
                    }

                    if (id_match && !full_matches.contains(mp)) {
                        full_matches.add(mp)
                    }
                }
            }

            if (full_matches.size() == 1) {
                log.debug("Matched package by name + identifier!")
                result = full_matches[0]
            }
            else if (full_matches.size() == 0 && name_candidates.size() == 1) {
                result = name_candidates[0]
                log.debug("Found a single match by name!")
            }
            else {
                log.warn("Found multiple possible matches for package! Aborting..")
                return result
            }
        }
        else if (!result && name_candidates.size() == 1) {
            log.debug("Matched package by name!")
            result = name_candidates[0]
        }
        else if (result && result.name != packageHeaderDTO.name) {
            def current_name = result.name

            changed |= ClassUtils.setStringIfDifferent(result, 'name', packageHeaderDTO.name)

            if (!result.variantNames.find { it.variantName == current_name }) {
                result.ensureVariantName(current_name)
            }
        }*/

/*        if (!result) {
            log.debug("Did not find a match via name, trying existing variantNames..")
            def variant_normname = GOKbTextUtils.normaliseString(packageHeaderDTO.name)
            def variant_candidates = Package.executeQuery("select distinct p from Package as p join p.variantNames as v where v.normVariantName = ? and p.status <> ? ", [variant_normname, status_deleted])

            if (variant_candidates.size() == 1) {
                result = variant_candidates[0]
                log.debug("Package matched via existing variantName.")
            }
        }*/

        //variantNames not in ygorJson
        /*if (!result && packageHeaderDTO.variantNames?.size() > 0) {
            log.debug("Did not find a match via existing variantNames, trying supplied variantNames..")
            packageHeaderDTO.variantNames.each {

                if (it.trim().size() > 0) {
                    result = Package.findByName(it)

                    if (result) {
                        log.debug("Found existing package name for variantName ${it}")
                    }
                    else {
                        def variant_normname = GOKbTextUtils.normaliseString(it)
                        def variant_candidates = Package.executeQuery("select distinct p from Package as p join p.variantNames as v where v.normVariantName = ? and p.status <> ? ", [variant_normname, status_deleted])
                        if (variant_candidates.size() == 1) {
                            log.debug("Found existing package variant name for variantName ${it}")
                            result = variant_candidates[0]
                        }
                    }
                }
            }
        }*/

/*        if (!result) {
            log.debug("No existing package matched. Creating new package..")
            result = new Package(name: packageHeaderDTO.name, normname: pkg_normname)
            created = true
            if (packageHeaderDTO.uuid && packageHeaderDTO.uuid.trim().size() > 0) {
                result.uuid = packageHeaderDTO.uuid
            }
            result.save(flush: true, failOnError: true)
        }
        else if (user && !user.hasRole('ROLE_SUPERUSER') && result.curatoryGroups && result.curatoryGroups?.size() > 0) {
            def cur = user.curatoryGroups?.id.intersect(result.curatoryGroups?.id)
            if (!cur) {
                log.debug("No curator!")
                return result
            }
        }*/

        //not in ygorJson
        /*changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.status, result, 'status')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.scope, result, 'scope')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.breakable, result, 'breakable')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.consistent, result, 'consistent')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.paymentType, result, 'paymentType')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.file, result, 'file')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.openAccess, result, 'openAccess')
        changed |= ClassUtils.setRefdataIfPresent(packageHeaderDTO.contentType, result, 'contentType')*/

        // Platform
        Platform platform = null
        if (packageHeaderDTO.nominalPlatform && packageHeaderDTO.nominalPlatform instanceof Map) {
            platform = platformUpsertDTO(packageHeaderDTO.nominalPlatform)
            if (result && result.nominalPlatform == null) {
                result.nominalPlatform = platform
                changed = true
            }
                    else {
                        log.debug("Platform already set")
                    }
        } else {
            log.warn("Could not extract platform information from JSON! ${packageHeaderDTO.nominalPlatform}")
        }

        // Provider
        if (packageHeaderDTO.nominalProvider && packageHeaderDTO.nominalProvider instanceof Map) {

            log.debug("Trying to set package provider.. ${packageHeaderDTO.nominalProvider}")
            Org prov = null
            if (packageHeaderDTO.nominalProvider.uuid) {
                prov = Org.findByUuid(packageHeaderDTO.nominalProvider.uuid)
            }

            if(packageHeaderDTO.nominalProvider.oid) {
                List org_id_components = packageHeaderDTO.nominalProvider.oid.split(':')
                if (org_id_components.size() == 2) {
                    prov = Org.get(Long.parseLong(org_id_components[1].trim()))
                }
            }

            if (!prov && packageHeaderDTO.nominalProvider.name) {
                def norm_prov_name = KBComponent.generateNormname(packageHeaderDTO.nominalProvider.name)
                prov = Org.findByNormname(norm_prov_name)
                if (!prov) {
                    log.debug("None found by Normname ${norm_prov_name}, trying variants")
                    def variant_normname = GOKbTextUtils.normaliseString(packageHeaderDTO.nominalProvider.name)
                    def candidate_orgs = Org.executeQuery("select distinct o from Org as o join o.variantNames as v where v.normVariantName = ? and o.status != ?", [variant_normname, status_deleted])
                    if (candidate_orgs.size() == 1) {
                        prov = candidate_orgs[0]
                    }
                    else if (candidate_orgs.size() == 0) {
                        log.debug("No org match for provider ${packageHeaderDTO.nominalProvider}. Creating new org..")
                        prov = new Org(name: packageHeaderDTO.nominalProvider.name, normname: norm_prov_name, uuid: packageHeaderDTO.nominalProvider.uuid ?: null).save(flush: true, failOnError: true)
                    }
                    else {
                        log.warn("Multiple org matches for provider ${packageHeaderDTO.nominalProvider}. Skipping..")
                    }
                }
            }

            if (result && prov) {
                if (result.provider == null) {
                    result.provider = prov
                    log.debug("Provider ${prov.name} set.")
                    changed = true
                }
                else {
                    log.debug("No provider change")
                }
            }
        }
        else {
            log.warn("Could not extract nominalProvider information from JSON! ${packageHeaderDTO.nominalProvider}")
        }

        // Source
        // variantNames are handled in ComponentUpdateService
        // packageHeaderDTO.variantNames?.each {
        //   if ( it.trim().size() > 0 ) {
        //     result.ensureVariantName(it)
        //     changed=true
        //   }
        // }

        // CuratoryGroups
/*        if(packageHeaderDTO.curatoryGroups) {
            packageHeaderDTO.curatoryGroups.each {
                def cg = null
                def cgname = null

                if (it instanceof Integer) {
                    cg = CuratoryGroup.get(it)
                } else if (it instanceof String) {
                    String normname = CuratoryGroup.generateNormname(it)
                    cgname = it
                    cg = CuratoryGroup.findByNormname(normname)
                } else if (it.id) {
                    cg = CuratoryGroup.get(it.id)
                } else if (it.name) {
                    String normname = CuratoryGroup.generateNormname(it.name)
                    cgname = it.name
                    cg = CuratoryGroup.findByNormname(normname)
                }
                if (cg) {
                    if (result.curatoryGroups.find { it.id == cg.id }) {
                    } else {
                        result.curatoryGroups.add(cg)
                        changed = true
                    }
                } else if (cgname) {
                    def new_cg = new CuratoryGroup(name: cgname).save(flush: true, failOnError: true)
                    result.curatoryGroups.add(new_cg)
                    changed = true
                }
            }
        }*/

        /*if (packageHeaderDTO.source) {
            def src = null
            if (packageHeaderDTO.source instanceof Integer) {
                src = Source.get(packageHeaderDTO.source)
            }
            else if (packageHeaderDTO.source instanceof Map) {
                def sourceMap = packageHeaderDTO.source
                if (sourceMap.id) {
                    src = Source.get(sourceMap.id)
                }
                else {
                    def namespace = null
                    if (sourceMap.targetNamespace instanceof Integer) {
                        namespace = IdentifierNamespace.get(sourceMap.targetNamespace)
                    }
                    if (!result.source || result.source.name != result.name) {
                        def source_config = [
                                name           : result.name,
                                url            : sourceMap.url,
                                frequency      : sourceMap.frequency,
                                ezbMatch       : (sourceMap.ezbMatch ?: false),
                                zdbMatch       : (sourceMap.zdbMatch ?: false),
                                automaticUpdate: (sourceMap.automaticUpdate ?: false),
                                targetNamespace: namespace
                        ]
                        src = new Source(source_config).save(flush: true)
                        result.curatoryGroups.each { cg ->
                            src.curatoryGroups.add(cg)
                        }
                    }
                    else {
                        src = result.source
                        changed |= ClassUtils.setStringIfDifferent(src, 'frequency', sourceMap.frequency)
                        changed |= ClassUtils.setStringIfDifferent(src, 'url', sourceMap.url)
                        changed |= ClassUtils.setBooleanIfDifferent(src, 'ezbMatch', sourceMap.ezbMatch)
                        changed |= ClassUtils.setBooleanIfDifferent(src, 'zdbMatch', sourceMap.zdbMatch)
                        changed |= ClassUtils.setBooleanIfDifferent(src, 'automaticUpdate', sourceMap.automaticUpdate)
                        if (namespace && namespace != src.targetNamespace) {
                            src.targetNamespace = namespace
                            changed = true
                        }
                        src.save(flush: true)
                    }
                }
            }
            if (src && result.source != src) {
                result.source = src
                changed = true
            }
        }*/

        /*if (packageHeaderDTO.ddcs) {
            packageHeaderDTO.ddcs.each{ String ddc ->
                RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)

                if(refdataValue && !(refdataValue in result.ddcs)){
                    result.addToDdcs(refdataValue)
                }
            }
        }*/

        result.save(flush: true)
        result
    }

    TitleInstancePackagePlatform tippUpsertDTO(tipp_dto, def user = null, LinkedHashMap tippsWithCoverage) {
        def result = null
        log.debug("tippUpsertDTO(${tipp_dto})")
        Package pkg = null
        Platform plt = null

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

        def status_current = RDStore.KBC_STATUS_CURRENT
        def status_retired = RDStore.KBC_STATUS_RETIRED
        def trimmed_url = tipp_dto.url ? tipp_dto.url.trim() : null

        //TODO: Moe
        def curator = pkg?.curatoryGroups?.size() > 0 ? (user.adminStatus || user.curatoryGroups?.id.intersect(pkg?.curatoryGroups?.id)) : false

        if (pkg && plt && curator) {
            log.debug("See if we already have a tipp")
            //and tipp.status != ? ??????
            def tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp, Combo as pkg_combo, Combo as platform_combo  ' +
                    'where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg ' +
                    'and platform_combo.toComponent=tipp and platform_combo.fromComponent = :platform ' +
                    'and tipp.name = :tiDtoName and tipp.status != :removed',
                    [pkg: pkg, platform: plt, tiDtoName: tipp_dto.name, removed: RDStore.KBC_STATUS_REMOVED])
            def uuid_tipp = tipp_dto.uuid ? TitleInstancePackagePlatform.findByUuid(tipp_dto.uuid) : null

            TitleInstancePackagePlatform tipp = null


            if (uuid_tipp && uuid_tipp.pkg == pkg && uuid_tipp.hostPlatform == plt) {
                tipp = uuid_tipp
            }

            if (!tipp) {
                if(tipps.size() == 0){
                    if (trimmed_url && trimmed_url.size() > 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with url")
                        tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp, Combo as pkg_combo, Combo as platform_combo  ' +
                                'where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg ' +
                                'and platform_combo.toComponent=tipp and platform_combo.fromComponent = :platform ' +
                                'and tipp.url = :url and tipp.status != :removed',
                                [pkg: pkg, platform: plt, url: trimmed_url, removed: RDStore.KBC_STATUS_REMOVED])
                    }

                    if(tipps.size() == 0) {
                        log.debug("not found Tipp with title. research in pkg ${pkg} with tile_id")
                        tipps = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp, Combo as pkg_combo, Combo as platform_combo  ' +
                                'where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg ' +
                                'and platform_combo.toComponent=tipp and platform_combo.fromComponent = :platform and tipp.status != :removed',
                                [pkg: pkg, platform: plt, removed: RDStore.KBC_STATUS_REMOVED])
                    }

                }
                switch (tipps.size()) {
                    case 0:
                        log.debug("not found Tipp: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tipp_dto.name}]")
                        break
                    case 1:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            if (!tipps[0].url || tipps[0].url == trimmed_url) {
                                log.debug("found tipp")
                                tipp = tipps[0]
                            } else {

                                //if url changed find tipp over title id
                                TitleInstancePackagePlatform tippMatchedByTitleID = tippMatchingByTitleID(tipp_dto.identifiers, pkg, plt)
                                if (tippMatchedByTitleID && tippMatchedByTitleID.id == tipps[0].id && tippMatchedByTitleID.status != RDStore.KBC_STATUS_REMOVED) {
                                    log.debug("found tipp")
                                    tipp = tipps[0]
                                } else {
                                    log.debug("not found Tipp because url changed: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tipp_dto.name}, url: ${trimmed_url}]")
                                }
                            }
                        } else {
                            log.debug("found tipp")
                            tipp = tipps[0]
                        }
                        break
                    default:
                        if (trimmed_url && trimmed_url.size() > 0) {
                            tipps = tipps.findAll { !it.url || it.url == trimmed_url }
                            log.debug("found ${tipps.size()} tipps for URL ${trimmed_url}")
                        }

                        if (tipps.size() > 0) {
                            log.warn("found ${tipps.size()} TIPPs with URL ${trimmed_url}!")
                            if (tipps.size() == 1) {
                                tipp = tipps[0]
                            } else {
                                TitleInstancePackagePlatform tippMatchedByTitleID = tippMatchingByTitleID(tipp_dto.identifiers, pkg, plt)
                                if (tippMatchedByTitleID && tippMatchedByTitleID.status != RDStore.KBC_STATUS_REMOVED) {
                                    log.debug("found tipp")
                                    tipp = tipps.find { it.id == tippMatchedByTitleID.id }
                                } else {
                                    log.debug("not found Tipp after tipps and tippMatchingByTitleID: [pkg: ${pkg}, platform: ${plt}, tiDtoName: ${tipp_dto.name}, url: ${trimmed_url}, ids: ${tipp_dto.identifiers}]")
                                }
                            }
                        } else {
                            log.debug("None of the matched TIPPs are found!")
                        }
                        break
                }

            }

            if (!tipp) {
                log.debug("Creating new TIPP..")
                def tmap = [
                        'pkg'         : pkg,
                        'hostPlatform': plt,
                        'url'         : trimmed_url,
                        'uuid'        : (tipp_dto.uuid ?: null),
                        'status'      : (tipp_dto.status ?: 'Current'),
                        'name'        : (tipp_dto.name ?: null),
                        'type'        : (tipp_dto.type ?: null),
                        'medium'    : (tipp_dto.medium ?: null),

                ]

                tipp = tippCreate(tmap)
                if (!tipp) {
                    log.error("TIPP creation failed!")
                }
            }

            if (tipp) {

                //Kbart Fields to Ygor and then to wekb (siehe Wiki)

                // KBART -> publication_title -> name -> name
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'name', tipp_dto.name)
                // KBART -> first_author -> firstAuthor -> firstAuthor
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'firstAuthor', tipp_dto.firstAuthor)
                // KBART -> first_editor -> firstEditor -> firstEditor
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'firstEditor', tipp_dto.firstEditor)
                // KBART -> publisher_name -> publisherName -> publisherName
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'publisherName', tipp_dto.publisherName)


                // KBART -> publication_type -> publicationType -> publicationType
                RefdataValue publicationType
                if (tipp_dto.type) {
                    publicationType = determinePublicationType(tipp_dto)
                    if (publicationType) {
                        com.k_int.ClassUtils.setRefdataIfDifferent(publicationType.value, tipp, 'publicationType', RCConstants.TIPP_PUBLICATION_TYPE, false)
                    }
                }

                // KBART -> medium -> medium -> medium
                if (tipp_dto.medium) {
                    RefdataValue mediumRef = determineMediumRef(tipp_dto)
                    if (mediumRef) {
                        com.k_int.ClassUtils.setRefdataIfDifferent(mediumRef.value, tipp, 'medium', RCConstants.TIPP_MEDIUM, false)
                    }
                }

                // KBART -> title_url -> url -> url
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'url', trimmed_url)
                // KBART -> subject_area -> subjectArea -> subjectArea
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'subjectArea', tipp_dto.subjectArea)

                // KBART -> ddc -> ddc -> ddcs
                if(tipp_dto.ddc != "") {
                    if (tipp.ddcs) {
                        def ddcsIDs = tipp.ddcs.id.clone()
                        ddcsIDs.each {
                            tipp.removeFromDdcs(RefdataValue.get(it))
                        }
                        tipp.save()
                    }
                }
                // KBART -> ddc -> ddc -> ddcs
                if (tipp_dto.ddc instanceof String) {

                    RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, tipp_dto.ddc)

                    if(refdataValue && !(refdataValue in tipp.ddcs)){
                        tipp.addToDdcs(refdataValue)
                    }
                }

                // KBART -> ddc -> ddc -> ddcs
                if (tipp_dto.ddc instanceof List) {
                    tipp_dto.ddc.each{ String ddc ->
                        RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.DDC, ddc)
                        if(refdataValue && !(refdataValue in tipp.ddcs)){
                            tipp.addToDdcs(refdataValue)
                        }
                    }
                }

                // KBART -> language -> language -> languages
                if (tipp_dto.language) {
                    if (tipp.languages) {
                        def langIDs = tipp.languages.id.clone()
                        langIDs.each {
                            tipp.removeFromLanguages(KBComponentLanguage.get(it))
                            KBComponentLanguage.get(it).delete()
                        }
                        tipp.save()
                        //KBComponentLanguage.executeUpdate("delete from KBComponentLanguage where kbcomponent = :tipp", [tipp: tipp])
                    }

                    tipp_dto.language.each{ String lan ->
                        RefdataValue refdataValue = RefdataCategory.lookup(RCConstants.KBCOMPONENT_LANGUAGE, lan)
                        if(refdataValue){
                            if(!KBComponentLanguage.findByKbcomponentAndLanguage(tipp, refdataValue)){
                                KBComponentLanguage kbComponentLanguage = new KBComponentLanguage(kbcomponent: tipp, language: refdataValue)
                                kbComponentLanguage.save()
                            }
                        }
                    }

                    tipp.save()
                    tipp.refresh()
                }

                // KBART -> access_type -> accessType -> accessType
                if (tipp_dto.accessType && tipp_dto.accessType.length() > 0) {
                    def access_statement
                    if (tipp_dto.accessType == 'P') {
                        access_statement = 'Paid'
                    } else if (tipp_dto.accessType == 'F') {
                        access_statement = 'Free'
                    } else {
                        access_statement = tipp_dto.accessType
                    }
                    RefdataValue access_ref = RefdataCategory.lookup(RCConstants.TIPP_ACCESS_TYPE, access_statement)
                    if (access_ref) tipp.accessType = access_ref
                }

                // KBART -> access_start_date -> accessStartDate -> accessStartDate
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.accessStartDate, tipp, 'accessStartDate', true)
                // KBART -> access_end_date -> accessEndDate -> accessEndDate
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.accessEndDate, tipp, 'accessEndDate', true)
                // KBART -> last_changed -> lastChangedExternal -> lastChangedExternal
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.lastChanged, tipp, 'lastChangedExternal', true)

                // KBART -> status -> status -> status
                com.k_int.ClassUtils.setRefdataIfDifferent(tipp_dto.status, tipp, 'status', RCConstants.KBCOMPONENT_STATUS, false)

                // KBART -> listprice_eur, listprice_usd, listprice_gbp
                if (tipp_dto.prices) {
                    for (def priceData : tipp_dto.prices) {
                        if (priceData.amount != null && priceData.currency) {
                            tipp.setPrice(priceData.type, priceData.amount, priceData.currency, priceData.startDate ? dateFormatService.parseDate(priceData.startDate) : null, priceData.endDate ? dateFormatService.parseDate(priceData.endDate) : null)
                        }
                    }
                }

                // KBART -> notes -> coverage_notes -> note
                //com.k_int.ClassUtils.setStringIfDifferent(tipp, 'note', tipp_dto.coverage_notes)

                // KBART -> date_monograph_published_print -> dateFirstInPrint -> dateFirstInPrint
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.dateFirstInPrint, tipp, 'dateFirstInPrint', true)
                // KBART -> date_monograph_published_online -> dateFirstOnline -> dateFirstOnline
                com.k_int.ClassUtils.setDateIfPresent(tipp_dto.dateFirstOnline, tipp, 'dateFirstOnline', true)

                // KBART -> monograph_volume -> volumeNumber -> volumeNumber
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'volumeNumber', tipp_dto.volumeNumber)
                // KBART -> monograph_edition -> editionStatement -> editionStatement
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'editionStatement', tipp_dto.editionStatement)
                // KBART -> monograph_parent_collection_title -> series -> series
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'series', tipp_dto.series)

                // KBART -> parent_publication_title_id -> parentPublicationTitleId -> parentPublicationTitleId
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'parentPublicationTitleId', tipp_dto.parent_publication_title_id)

                // KBART -> oa_type -> oaType -> openAccess
                com.k_int.ClassUtils.setRefdataIfDifferent(tipp_dto.oaType, tipp, 'openAccess', RCConstants.TIPP_OPEN_ACCESS, true)

                // KBART -> oa_apc_eur -> oa_apc_eur -> prices
                if (tipp_dto.oa_apc_eur) {
                            tipp.setPrice(RDStore.PRICE_TYPE_OA_APC.value, tipp_dto.oa_apc_eur, RDStore.CURRENCY_EUR.value, null, null)
                }

                // KBART -> oa_apc_usd -> oa_apc_usd -> prices
                if (tipp_dto.oa_apc_usd) {
                    tipp.setPrice(RDStore.PRICE_TYPE_OA_APC.value, tipp_dto.oa_apc_usd, RDStore.CURRENCY_USD.value, null, null)
                }

                // KBART -> oa_apc_gbp -> oa_apc_gbp -> prices
                if (tipp_dto.oa_apc_gbp) {
                    tipp.setPrice(RDStore.PRICE_TYPE_OA_APC.value, tipp_dto.oa_apc_gbp, RDStore.CURRENCY_GBP.value, null, null)
                }

                // KBART -> package_isil -> package_isil -> identifiers['package_isil']
                if (tipp_dto.package_isil) {
                    createOrUpdateIdentifierForTipp(tipp, "package_isil", tipp_dto.package_isil)
                }

                // KBART -> package_isci -> package_isci -> identifiers['package_isci']
                if (tipp_dto.package_isci) {
                    createOrUpdateIdentifierForTipp(tipp, "package_isci", tipp_dto.package_isci)
                }

                // KBART -> ill_indicator -> ill_indicator -> identifiers['ill_indicator']
                if (tipp_dto.ill_indicator) {
                    createOrUpdateIdentifierForTipp(tipp, "ill_indicator", tipp_dto.ill_indicator)
                }


                // KBART -> preceding_publication_title_id -> preceding_publication_title_id -> precedingPublicationTitleId
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'precedingPublicationTitleId', tipp_dto.preceding_publication_title_id)
                // KBART -> superseding_publication_title_id -> superceding_publication_title_id -> supersedingPublicationTitleId
                com.k_int.ClassUtils.setStringIfDifferent(tipp, 'supersedingPublicationTitleId', tipp_dto.superceding_publication_title_id)

                // KBART -> date_first_issue_online, date_last_issue_online,
                // num_first_vol_online, num_first_issue_online,
                // num_last_vol_online, num_last_issue_online
                if (tipp_dto.coverageStatements && !tipp_dto.coverage) {
                    tipp_dto.coverage = tipp_dto.coverageStatements
                }

                if (tipp_dto.coverage && tipp_dto.coverage.size() > 0 && publicationType && publicationType == RDStore.TIPP_PUBLIC_TYPE_SERIAL) {

                    if(tippsWithCoverage[tipp.id]){
                        tippsWithCoverage[tipp.id] << tipp_dto.coverage[0]
                    }else {
                        tippsWithCoverage[tipp.id] = [tipp_dto.coverage[0]]
                    }
                }

                if (tipp_dto.coverage && tipp_dto.coverage.size() > 0 && publicationType && publicationType != RDStore.TIPP_PUBLIC_TYPE_SERIAL) {
                    com.k_int.ClassUtils.setStringIfDifferent(tipp, 'note', tipp_dto.coverage[0].coverageNote)
                    if(tipp.coverageStatements.size() > 0){
                        def cStsIDs = tipp.coverageStatements.id.clone()
                        cStsIDs.each {
                            tipp.removeFromCoverageStatements(TIPPCoverageStatement.get(it))
                        }
                        tipp.save()
                    }
                }

                // KBART -> package_ezb_anchor -> package_ezb_anchor -> identifiers['package_ezb_anchor']
                if (tipp_dto.package_ezb_anchor) {
                    createOrUpdateIdentifierForTipp(tipp, "package_ezb_anchor", tipp_dto.package_ezb_anchor)
                }

                // KBART -> zdb_id, ezb_id, print_identifier, online_identifier, title_id, doi_identifier  -> identifiers
                tipp_dto.identifiers.each { identifierMap ->
                    def namespace_val = identifierMap.type ?: identifierMap.namespace

                    if (namespace_val && identifierMap.value && namespace_val.toLowerCase() != "originediturl") {
                        createOrUpdateIdentifierForTipp(tipp, namespace_val, identifierMap.value)
                    }
                }

                tipp.save(flush: true, failOnError: true)
            }

            result = tipp
        } else {
            log.debug("Not able to reference TIPP: ${tipp_dto}")
        }
        result
    }
    
    TitleInstancePackagePlatform tippMatchingByTitleID(JSONArray identifiers, Package aPackage, Platform platform) {
        if(identifiers && aPackage.source && aPackage.source.targetNamespace){

            String value = identifiers.find {it.type == aPackage.source.targetNamespace.value}?.value

            List<TitleInstancePackagePlatform> tippList = Identifier.executeQuery('select i.tipp from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.value = :value and i.tipp is not null', [namespaceValue: aPackage.source.targetNamespace.value.toLowerCase(), value: value])

            if(tippList.size() == 1){
                    log.debug("tippMatchingByTitleID provider internal identifier matching by "+tippList.size() + ": "+ tippList.id)
                    return tippList[0]
            }
        }
        else if(identifiers && platform.titleNamespace){
            String value = identifiers.find {it.type == platform.titleNamespace.value}?.value

            List<TitleInstancePackagePlatform> tippList = Identifier.executeQuery('select i.tipp from Identifier as i where LOWER(i.namespace.value) = :namespaceValue and i.value = :value and i.tipp is not null', [namespaceValue: platform.titleNamespace.value.toLowerCase(), value: value])

            if(tippList.size() == 1){
                log.debug("tippMatchingByTitleID provider internal identifier matching by "+tippList.size() + ": "+ tippList.id)
                return tippList[0]
            }
        }

    }

    /**
     * Create a new TIPP
     */
    TitleInstancePackagePlatform tippCreate(tipp_fields = [:]) {

        RefdataValue tipp_status = tipp_fields.status ? RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, tipp_fields.status) : null

        RefdataValue tipp_medium = null
        if (tipp_fields.medium) {
            tipp_medium = determineMediumRef(tipp_fields)
        }
        RefdataValue tipp_publicationType = null
        if (tipp_fields.type) {
            tipp_publicationType = determinePublicationType(tipp_fields)
        }

        TitleInstancePackagePlatform result = new TitleInstancePackagePlatform(
                uuid: tipp_fields.uuid ?: UUID.randomUUID().toString(),
                status: tipp_status,
                name: tipp_fields.name,
                medium: tipp_medium,
                publicationType: tipp_publicationType,
                url: tipp_fields.url)
        if (result) {

            def pkg_combo_type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Tipps')
            new Combo(toComponent: result, fromComponent: tipp_fields.pkg, type: pkg_combo_type).save(flush: true, failOnError: true)

            def plt_combo_type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.HostedTipps')
            new Combo(toComponent: result, fromComponent: tipp_fields.hostPlatform, type: plt_combo_type).save(flush: true, failOnError: true)

        } else {
            log.error("TIPP creation failed!")
        }

        result.save(flush: true, failOnError: true)
        result.refresh()

        result
    }

    static RefdataValue determinePublicationType(tippObj) {
        if (tippObj.type) {
            switch (tippObj.type) {
                case "serial":
                case "Serial":
                case "Journal":
                case "journal":
                    return RDStore.TIPP_PUBLIC_TYPE_SERIAL
                    break;
                case "monograph":
                case "Monograph":
                case "Book":
                case "book":
                    return RDStore.TIPP_PUBLIC_TYPE_MONO
                    break;
                case "Database":
                case "database":
                    return RDStore.TIPP_PUBLIC_TYPE_DB
                    break;
                case "Other":
                case "other":
                    return RDStore.TIPP_PUBLIC_TYPE_OTHER
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

    static RefdataValue determineMediumRef(titleObj) {
        if (titleObj.medium) {
            switch (titleObj.medium.toLowerCase()) {
                case "a & i database":
                case "abstract- & indexdatenbank":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "A & I Database")
                case "audio":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Audio")
                case "database":
                case "fulltext database":
                case "Volltextdatenbank":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Database")
                case "dataset":
                case "datenbestand":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Dataset")
                case "film":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Film")
                case "image":
                case "bild":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Image")
                case "journal":
                case "zeitschrift":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Journal")
                case "book":
                case "buch":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Book")
                case "published score":
                case "musiknoten":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Published Score")
                case "article":
                case "artikel":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Article")
                case "software":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Software")
                case "statistics":
                case "statistiken":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Statistics")
                case "market data":
                case "marktdaten":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Market Data")
                case "standards":
                case "normen":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Standards")
                case "biography":
                case "biografie":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Biography")
                case "legal text":
                case "gesetzestext/urteil":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Legal Text")
                case "cartography":
                case "kartenwerk":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Cartography")
                case "miscellaneous":
                case "sonstiges":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Miscellaneous")
                case "other":
                    return RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Other")
                default:
                    return null
            }
        }
        else {
            return null
        }
    }

    void createOrUpdateIdentifierForTipp(TitleInstancePackagePlatform tipp, String namespace_val, String identifierValue){
        Identifier identifier
        IdentifierNamespace ns = IdentifierNamespace.findByValueAndTargetType(namespace_val, RDStore.IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP)

        //tipp = tipp.refresh()

        LinkedHashSet<Identifier> identifiersWithSameNamespace = tipp.ids.findAll{it.namespace.value == namespace_val}

        switch (identifiersWithSameNamespace.size()) {
            case 0:
                identifier = new Identifier(namespace: ns, value: identifierValue, tipp: tipp).save(flush: true, failOnError: true)
                break
            case 1:
                identifiersWithSameNamespace[0].value = identifierValue
                identifiersWithSameNamespace[0].save(flush: true, failOnError: true)
                identifier = identifiersWithSameNamespace[0]
                break
            default:
                List toDeletedIdentifier = []
                identifiersWithSameNamespace.each{Identifier tippIdentifier ->
                    toDeletedIdentifier << tippIdentifier.id
                }

                toDeletedIdentifier.each{
                    Identifier.executeUpdate("delete from Identifier where id_id = :id", [id: it])
                }

                identifier = new Identifier(namespace: ns, value: identifierValue, tipp: tipp).save(flush: true, failOnError: true)
                break
        }

    }

    void createOrUpdateCoverageForTipp(TitleInstancePackagePlatform tipp, def coverage){

        tipp = tipp.refresh()

        Integer countNewCoverages = coverage.size()
        Integer countTippCoverages = tipp.coverageStatements.size()

        if(countNewCoverages == 1 && countTippCoverages == 1){
            RefdataValue cov_depth = null

            if (coverage[0].coverageDepth instanceof String) {
                cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, coverage[0].coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
            }

            def parsedStart = GOKbTextUtils.completeDateString(coverage[0].startDate)
            def parsedEnd = GOKbTextUtils.completeDateString(coverage[0].endDate, false)

            com.k_int.ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'startIssue', coverage[0].startIssue)
            com.k_int.ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'endIssue', coverage[0].endIssue)
            com.k_int.ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'startVolume', coverage[0].startVolume)
            com.k_int.ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'endVolume', coverage[0].endVolume)
            com.k_int.ClassUtils.setDateIfPresent(parsedStart, tipp.coverageStatements[0], 'startDate', true)
            com.k_int.ClassUtils.setDateIfPresent(parsedEnd, tipp.coverageStatements[0], 'endDate', true)
            com.k_int.ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'embargo', coverage[0].embargo)
            com.k_int.ClassUtils.setStringIfDifferent(tipp.coverageStatements[0], 'coverageNote', coverage[0].coverageNote)
            if(cov_depth) {
                com.k_int.ClassUtils.setRefdataIfDifferent(cov_depth.value, tipp.coverageStatements[0], 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, true)
            }
        }
        else if(countNewCoverages == 0 && countTippCoverages > 0){
            def cStsIDs = tipp.coverageStatements.id.clone()
            cStsIDs.each {
                tipp.removeFromCoverageStatements(TIPPCoverageStatement.get(it))
            }
            tipp.save()

        }else if(countNewCoverages > 0 && countTippCoverages == 0){
            coverage.each { c ->
                def parsedStart = GOKbTextUtils.completeDateString(c.startDate)
                def parsedEnd = GOKbTextUtils.completeDateString(c.endDate, false)
                def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
                def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)

                RefdataValue cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                }

                tipp.addToCoverageStatements(
                        'startVolume': c.startVolume,
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
        }else if(countNewCoverages > 1 && countTippCoverages > 1) {
            def cStsIDs = tipp.coverageStatements.id.clone()
            cStsIDs.each {
                tipp.removeFromCoverageStatements(TIPPCoverageStatement.get(it))
            }
            tipp.save()

            coverage.each { c ->
                def parsedStart = GOKbTextUtils.completeDateString(c.startDate)
                def parsedEnd = GOKbTextUtils.completeDateString(c.endDate, false)
                def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
                def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)

                RefdataValue cov_depth = null

                if (c.coverageDepth instanceof String) {
                    cov_depth = RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, c.coverageDepth) ?: RefdataCategory.lookup(RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, "Fulltext")
                }

                tipp.addToCoverageStatements(
                        'startVolume': c.startVolume,
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

        }

       /* coverage.each { c ->
            def parsedStart = GOKbTextUtils.completeDateString(c.startDate)
            def parsedEnd = GOKbTextUtils.completeDateString(c.endDate, false)

            //com.k_int.ClassUtils.setStringIfDifferent(tipp, 'coverageNote', c.coverageNote)
            //com.k_int.ClassUtils.setRefdataIfDifferent(c.coverageDepth, tipp, 'coverageDepth', RCConstants.TIPP_COVERAGE_DEPTH, true)

            def cs_match = false
            def conflict = false
            def startAsDate = (parsedStart ? Date.from(parsedStart.atZone(ZoneId.systemDefault()).toInstant()) : null)
            def endAsDate = (parsedEnd ? Date.from(parsedEnd.atZone(ZoneId.systemDefault()).toInstant()) : null)
            def conflicting_statements = []



            Map cMap = ["startIssue": c.startIssue,
                          "endIssue": c.endIssue,
                          "startVolume": c.startVolume,
                          "endVolume": c.endVolume]

            println(cMap)

            tipp.coverageStatements?.each { TIPPCoverageStatement tcs ->
                Map tcsMap = ["startIssue": tcs.startIssue,
                              "endIssue": tcs.endIssue,
                              "startVolume": tcs.startVolume,
                              "endVolume": tcs.endVolume]
                println( tcsMap.equals(cMap))
                println( tcsMap.toString() == cMap.toString())
                println(tcsMap)
                *//*if (!cs_match) {
                    if (!tcs.endDate && !endAsDate) {
                        conflict = true
                    } else if (tcs.toString() == c.toString()) {
                        log.debug("Matched CoverageStatement by Map")
                        cs_match = true
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
                        com.k_int.ClassUtils.setDateIfPresent(parsedStart, tcs, 'startDate', true)
                        com.k_int.ClassUtils.setDateIfPresent(parsedEnd, tcs, 'endDate', true)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'embargo', c.embargo)
                        com.k_int.ClassUtils.setStringIfDifferent(tcs, 'coverageNote', c.coverageNote)
                        com.k_int.ClassUtils.setRefdataIfDifferent(tcs.coverageDepth?.value, tcs, 'coverageDepth', RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH, true)
                    }
                } else {
                    log.debug("Matched new coverage ${c} on multiple existing coverages!")
                }*//*
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

                tipp.addToCoverageStatements(
                        'startVolume': c.startVolume,
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
        }*/
    }

}
