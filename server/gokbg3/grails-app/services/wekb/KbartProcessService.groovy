package wekb

import de.hbznrw.ygor.tools.DateToolkit
import de.wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.util.Holders
import org.gokb.CleanupService
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.KBComponent
import org.gokb.cred.Package
import org.gokb.cred.Platform
import org.gokb.cred.RefdataValue
import org.gokb.cred.Source
import org.gokb.cred.TitleInstancePackagePlatform
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.StatelessSession
import org.hibernate.Transaction
import org.mozilla.universalchardet.UniversalDetector

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.ZoneId

@Transactional
class KbartProcessService {

    KbartImportValidationService kbartImportValidationService
    KbartImportService kbartImportService
    CleanupService cleanupService
    SessionFactory sessionFactory

    void kbartImportManual(Package pkg, File tsvFile){
        log.info("Beginn kbartImportManual ${pkg.name}")
        List kbartRows = []
        String lastUpdateURL = ""
        Date startTime = new Date()
        UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Starting Update package.", onlyRowsWithLastChanged: false, automaticUpdate: false)
        try {
            kbartRows = kbartProcess(tsvFile, lastUpdateURL, updatePackageInfo)

            if (kbartRows.size() > 0) {
                String fPathSource = '/tmp/wekb/kbartImportTmp'
                String fPathTarget = Holders.grailsApplication.config.kbartImportStorageLocation ? "${Holders.grailsApplication.config.kbartImportStorageLocation.toString()}" : '/tmp/wekb/kbartImport'

                File folder = new File("${fPathTarget}")
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                String packageName = "${pkg.name.toLowerCase().replaceAll("\\s", '_')}_${pkg.id}"

                Path source = new File("${fPathSource}/${packageName}").toPath()
                Path target = new File("${fPathTarget}/${packageName}").toPath()
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)

                updatePackageInfo = kbartImportProcess(kbartRows, pkg, lastUpdateURL, updatePackageInfo, false)
            }

        } catch (Exception exception) {
            log.error("Error by kbartImportManual: ${exception.message}" + exception.printStackTrace())
            UpdatePackageInfo.withTransaction {
                UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                updatePackageFail.description = "An error occurred while processing the kbart file. More information can be seen in the system log."
                updatePackageFail.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageFail.startTime = startTime
                updatePackageFail.endTime = new Date()
                updatePackageFail.pkg = pkg
                updatePackageFail.onlyRowsWithLastChanged = false
                updatePackageFail.automaticUpdate = false
                updatePackageFail.save()
            }
        }
        log.info("End kbartImportManual ${pkg.name}")
    }

    UpdatePackageInfo kbartImportProcess(List kbartRows, Package pkg, String lastUpdateURL, UpdatePackageInfo updatePackageInfo, Boolean onlyRowsWithLastChanged) {
        log.info("Begin kbartImportProcess Package ($pkg.name)")
        boolean addOnly = false //Thing about it where to set or to change

        RefdataValue status_current = RDStore.KBC_STATUS_CURRENT
        RefdataValue status_deleted = RDStore.KBC_STATUS_DELETED
        RefdataValue status_retired = RDStore.KBC_STATUS_RETIRED
        RefdataValue status_expected = RDStore.KBC_STATUS_EXPECTED

        List listStatus = [status_current]

        Map headerOfKbart = kbartRows[0]

        //println("Header = ${headerOfKbart}")

        kbartRows.remove(0)

        //Needed if kbart not wekb standard
        boolean setAllTippsNotInKbartToDeleted = true


        if(kbartRows.size() > 0){
            if (headerOfKbart.containsKey("status")) {
                log.info("kbart has status field and is wekb standard")
                setAllTippsNotInKbartToDeleted = false
                listStatus = [status_current, status_expected, status_deleted, status_retired]
            }
        }

        if(addOnly){
            setAllTippsNotInKbartToDeleted = false
        }

        List<Long> existing_tipp_ids = TitleInstancePackagePlatform.executeQuery(
                "select tipp.id from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in :status and " +
                        "tipp.pkg = :package",
                [package: pkg, status: listStatus])

        int previouslyTipps = existing_tipp_ids.size()

        LinkedHashMap tippsWithCoverage = [:]
        List<Long> tippDuplicates = []
        List setTippsNotToDeleted = []
        Map errors = [global: [], tipps: []]

        List<Long> tippsFound = []
        List invalidKbartRowsForTipps = []
        int removedTipps = 0
        int newTipps = 0
        int changedTipps = 0

        int kbartRowsCount = kbartRows.size()

        List kbartRowsToCreateTipps = []

        Date lastChangedInKbart = pkg.source ? pkg.source.lastChangedInKbart : null
        List<LocalDate> lastChangedDates = []

        Platform plt = pkg.nominalPlatform
        IdentifierNamespace identifierNamespace = pkg.getTitleIDNameSpace()

        try {

            log.info("Matched package has ${previouslyTipps} TIPPs")

            int idx = 0

            if(onlyRowsWithLastChanged){
                if(headerOfKbart.containsKey("last_changed")) {
                    LocalDate currentLastChangedInKbart = convertToLocalDateViaInstant(lastChangedInKbart)
                    LocalDate lastUpdated = convertToLocalDateViaInstant(pkg.source.lastRun)
                    if(currentLastChangedInKbart && currentLastChangedInKbart.isBefore(lastUpdated)){
                        lastUpdated = currentLastChangedInKbart
                    }

                    log.info("onlyRowsWithLastChanged is set! before process only last changed rows: ${kbartRowsCount}")
                    List newKbartRows = []
                    kbartRows.eachWithIndex { Object entry, int i ->
                        if (entry.containsKey("last_changed") && entry.last_changed != null && entry.last_changed != "") {
                            LocalDate lastChanged = DateToolkit.getAsLocalDate(entry.last_changed)
                            if (lastChanged == null || lastUpdated == null || !lastChanged.isBefore(lastUpdated)) {
                                newKbartRows << entry
                            }

                            if(lastChanged){
                                lastChangedDates << lastChanged
                            }
                        } else {
                            newKbartRows << entry
                        }
                    }
                    kbartRows = newKbartRows
                    log.info("onlyRowsWithLastChanged is set! after process only last changed rows: ${newKbartRows.size()}")
                }else {

                }
            }

            if(lastChangedDates.size() > 0) {
                LocalDate maxDate = lastChangedDates.max()
                lastChangedInKbart = Date.from(maxDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
            }

            UpdatePackageInfo.withTransaction {
                if(!setAllTippsNotInKbartToDeleted){
                    updatePackageInfo.kbartHasWekbFields = true
                }

                if(lastChangedInKbart){
                    updatePackageInfo.lastChangedInKbart = lastChangedInKbart
                }
                updatePackageInfo.save()
            }

            int max = 500
            TitleInstancePackagePlatform.withSession { Session sess ->
                for (int offset = 0; offset < kbartRows.size(); offset += max) {

                    List kbartRowsToProcess = kbartRows.drop(offset).take(max)
                    for (def kbartRow : kbartRowsToProcess) {
                        idx++
                        def currentTippError = [index: idx]
                        log.info("kbartImportProcess (#$idx of $kbartRowsCount): title ${kbartRow.publication_title}")
                        if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {

                            kbartRow.pkg = pkg
                            kbartRow.nominalPlatform = plt
                            try {

                                Map tippErrorMap = [:]
                                def validation_result = kbartImportValidationService.tippValidateForAutoUpdate(kbartRow)
                                if (!validation_result.valid) {
                                    if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                        invalidKbartRowsForTipps << kbartRow.rowIndex

                                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                                description: validation_result.errorMessage,
                                                tipp: null,
                                                startTime: new Date(),
                                                endTime: new Date(),
                                                status: RDStore.UPDATE_STATUS_FAILED,
                                                type: RDStore.UPDATE_TYPE_FAILED_TITLE,
                                                oldValue: '',
                                                newValue: '',
                                                tippProperty: '',
                                                updatePackageInfo: updatePackageInfo
                                        ).save()
                                    }
                                    log.debug("TIPP Validation failed on ${kbartRow.publication_title}")
                                    /* def tipp_error = [
                                             message: validation_result.errorMessage,
                                             baddata: kbartRow
                                     ]
                                     tippErrorMap = tipp_error*/
                                } else {
                                    /*if (validation_result.errors?.size() > 0) {
                                                                tippErrorMap.putAll(validation_result.errors)
                                                            }*/
                                    TitleInstancePackagePlatform updateTipp = null
                                    try {
                                        Map autoUpdateResultTipp = kbartImportService.tippImportForUpdate(kbartRow, tippsWithCoverage, tippDuplicates, updatePackageInfo, kbartRowsToCreateTipps, identifierNamespace)

                                        kbartRowsToCreateTipps = autoUpdateResultTipp.kbartRowsToCreateTipps
                                        tippsWithCoverage = autoUpdateResultTipp.tippsWithCoverage
                                        tippDuplicates = autoUpdateResultTipp.tippDuplicates

                                        if (autoUpdateResultTipp.updatePackageInfo) {
                                            updatePackageInfo = autoUpdateResultTipp.updatePackageInfo
                                        }

                                        if (!autoUpdateResultTipp.newTipp) {
                                            updateTipp = autoUpdateResultTipp.tippObject

                                            if (autoUpdateResultTipp.removedTipp) {
                                                removedTipps++
                                            }

                                            if (autoUpdateResultTipp.changedTipp) {
                                                changedTipps++
                                            }

                                            if (setAllTippsNotInKbartToDeleted && updateTipp && updateTipp.status != RDStore.KBC_STATUS_CURRENT) {
                                                updateTipp.status = RDStore.KBC_STATUS_CURRENT
                                                setTippsNotToDeleted << updateTipp.id
                                            }
                                            if(updateTipp) {
                                                tippsFound << updateTipp.id
                                            }
                                        }

                                    }
                                    catch (grails.validation.ValidationException ve) {
                                        if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                            if (updateTipp) {
                                                invalidKbartRowsForTipps << kbartRow.rowIndex
                                                UpdateTippInfo.withTransaction {
                                                    updatePackageInfo.refresh()
                                                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                                            description: "An error occurred while processing the title: ${kbartRow.publication_title}. Check kbart row of this title.",
                                                            tipp: updateTipp,
                                                            startTime: new Date(),
                                                            endTime: new Date(),
                                                            status: RDStore.UPDATE_STATUS_FAILED,
                                                            type: RDStore.UPDATE_TYPE_FAILED_TITLE,
                                                            oldValue: '',
                                                            newValue: '',
                                                            tippProperty: '',
                                                            updatePackageInfo: updatePackageInfo
                                                    ).save()
                                                }
                                                updateTipp.discard()
                                            }
                                        }
                                        log.error("ValidationException attempting to cross reference the title: ${kbartRow.publication_title} with TIPP ${updateTipp?.id}:", ve)
                                        /*tippErrorMap.putAll(messageService.processValidationErrors(ve.errors))*/
                                    }
                                    catch (Exception ge) {
                                        if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                            if (updateTipp) {
                                                invalidKbartRowsForTipps << kbartRow.rowIndex
                                                UpdateTippInfo.withTransaction {
                                                    updatePackageInfo.refresh()
                                                    UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                                            description: "An error occurred while processing the title: ${kbartRow.publication_title}. Check kbart row of this title.",
                                                            tipp: updateTipp,
                                                            startTime: new Date(),
                                                            endTime: new Date(),
                                                            status: RDStore.UPDATE_STATUS_FAILED,
                                                            type: RDStore.UPDATE_TYPE_FAILED_TITLE,
                                                            oldValue: '',
                                                            newValue: '',
                                                            tippProperty: '',
                                                            updatePackageInfo: updatePackageInfo
                                                    ).save()
                                                }
                                                updateTipp.discard()
                                            }
                                        }
                                        log.error("Exception attempting to cross reference TIPP:", ge)
                                        /*                                      def tipp_error = [
                                                                                      message: messageService.resolveCode('crossRef.package.tipps.error', [kbartRow.publication_title], Locale.ENGLISH),
                                                                                      baddata: kbartRow,
                                                                                      errors : [message: ge.toString()]
                                                                              ]
                                                                              tippErrorMap = tipp_error*/
                                    }
                                }

                                /*if (tippErrorMap.size() > 0) {
                                    currentTippError.put('tipp', tippErrorMap)
                                }*/
                            }
                            catch (Exception ge) {
                                log.error("Exception attempting to cross reference the title: ${kbartRow.publication_title}:", ge)
                            }
                        }

                        /*if (currentTippError.size() > 1) {
                            errors.tipps.add(currentTippError)
                        }*/

                        if (idx % 100 == 0) {
                            log.info("Clean up");
                            cleanupService.cleanUpGorm()
                        }
                    }
                    sess.flush()
                    sess.clear()
                }
            }

            if(kbartRowsToCreateTipps.size() > 0){
                List newTippList = kbartImportService.createTippBatch(kbartRowsToCreateTipps, updatePackageInfo, identifierNamespace)
                newTipps = newTippList.size()
                log.debug("kbartRowsToCreateTipps: TippIds -> "+newTippList.tippID)

                /*  Package pkgTipp = pkg
                  Platform platformTipp = plt

                  newTippList.eachWithIndex{ Map newTippMap, int i ->
                      TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(newTippMap.tippID)
                      if(tipp){
                          long start = System.currentTimeMillis()
                          log.info("kbartRowsToCreateTipps: update tipp ${i+1} of ${newTipps}")
                          try {
                              LinkedHashMap result = [newTipp: true]
                              result = kbartImportService.updateTippWithKbart(result, tipp, newTippMap.kbartRowMap, newTippMap.updatePackageInfo, tippsWithCoverage, pkgTipp, platformTipp)
                              tippsWithCoverage = result.tippsWithCoverage
                              updatePackageInfo = result.updatePackageInfo

                          }catch (Exception e) {
                              log.error("kbartRowsToCreateTipps: -> ${newTippMap.kbartRowMap}:" + e.toString())
                          }

                          log.debug("kbartRowsToCreateTipps processed at: ${System.currentTimeMillis()-start} msecs")
                          if (i % 100 == 0) {
                              log.info("Clean up")
                              cleanupService.cleanUpGorm()
                          }
                      }
                  }*/

            }

            if(!onlyRowsWithLastChanged && tippDuplicates.size() > 0){
                log.info("remove tippDuplicates -> ${tippDuplicates.size()}: ${tippDuplicates}")

                tippDuplicates.each {
                    if(!(it in tippsFound)){
                        KBComponent.executeUpdate("update KBComponent set status = :removed, lastUpdated = CURRENT_DATE where id = (:tippId) and status != :removed", [removed: RDStore.KBC_STATUS_REMOVED, tippId: it])

                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                        UpdateTippInfo.withTransaction {
                            updatePackageInfo.refresh()
                            UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                    description: "Remove Title '${tipp.name}' because is a duplicate in wekb!",
                                    tipp: tipp,
                                    startTime: new Date(),
                                    endTime: new Date(),
                                    status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.UPDATE_TYPE_REMOVED_TITLE,
                                    oldValue: tipp.status.value,
                                    newValue: 'Removed',
                                    tippProperty: 'status',
                                    kbartProperty: 'status',
                                    updatePackageInfo: updatePackageInfo
                            ).save()
                            removedTipps++
                        }
                    }
                }

            }

/*            if (invalidKbartRowsForTipps.size() > 0) {
                String msg = messageService.resolveCode('crossRef.package.tipps.ignored', [invalidKbartRowsForTipps.size()], Locale.ENGLISH)
                log.warn(msg)
                errors.global.add([message: msg, baddata: pkg.name])
            }*/

            int countInvalidKbartRowsForTipps = invalidKbartRowsForTipps.size()

            if (kbartRows.size() > 0 && kbartRows.size() > countInvalidKbartRowsForTipps) {
            } else {
                log.info("imported Package $pkg.name contains no valid TIPPs")
            }


            /*if (!onlyRowsWithLastChanged && setAllTippsNotInKbartToDeleted) {

                List<Long> tippsIds = setTippsNotToDeleted ? TitleInstancePackagePlatform.executeQuery("select tipp.id from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in (:status) and " +
                        "tipp.pkg = :package and tipp.id not in (:setTippsNotToDeleted)",
                        [package: pkg, status: [status_current, status_expected, status_retired], setTippsNotToDeleted: setTippsNotToDeleted]) : []

                int deletedCount = tippsIds.size()
                int idxDeleted = 0
                if(deletedCount > 0) {
                    int maxDeleted = 30000
                    for (int offset = 0; offset < deletedCount; offset += maxDeleted) {
                        List deleteTippsFromWekbToProcess = tippsIds.drop(offset).take(maxDeleted)
                        KBComponent.executeUpdate("update KBComponent set status = :deleted, lastUpdated = CURRENT_DATE where id in (:tippIDs) and status != :deleted", [deleted: RDStore.KBC_STATUS_DELETED, tippIDs: deleteTippsFromWekbToProcess])
                    }

                    StatelessSession session = sessionFactory.openStatelessSession()
                    Transaction tx = session.beginTransaction()
                    tippsIds.each { tippID ->
                        idxDeleted++
                        log.info("setAllTippsNotInKbartToDeleted (#$idxDeleted of $deletedCount): tippID ${tippID}")
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Delete Title '${tipp.name}' because is not in KBART!",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                oldValue: tipp.status.value,
                                newValue: 'Deleted',
                                tippProperty: 'status',
                                kbartProperty: 'status',
                                updatePackageInfo: updatePackageInfo,
                                lastUpdated: new Date(),
                                dateCreated: new Date(),
                                uuid: UUID.randomUUID().toString()
                        )

                        session.insert(updateTippInfo)
                    }
                    tx.commit()
                    session.close()
                }

                log.info("kbart is not wekb standard. set title to deleted. Found tipps: ${tippsIds.size()}, Set tipps to deleted: ${idxDeleted}")
            }*/

            log.info("tippsWithCoverage: ${tippsWithCoverage.size()}")

            tippsWithCoverage.each {
                TitleInstancePackagePlatform titleInstancePackagePlatform = TitleInstancePackagePlatform.get(it.key)

                if (titleInstancePackagePlatform) {
                    kbartImportService.createOrUpdateCoverageForTipp(titleInstancePackagePlatform, it.value)
                }

            }

            int countExistingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                    "select count(tipp.id) from TitleInstancePackagePlatform tipp where " +
                            "tipp.status in (:status) and " +
                            "tipp.pkg = :package",
                    [package: pkg, status: listStatus])[0]


            //TODO: countExistingTippsAfterImport > (kbartRowsCount-countInvalidKbartRowsForTipps) ??? nötig noch
            if(!onlyRowsWithLastChanged && tippsFound.size() > 0 && kbartRowsCount > 0 && countExistingTippsAfterImport > (kbartRowsCount-countInvalidKbartRowsForTipps)){

                List<Long> existingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                        "select tipp.id from TitleInstancePackagePlatform tipp where " +
                                "tipp.status in (:status) and " +
                                "tipp.pkg = :package",
                        [package: pkg, status: listStatus])


                List<Long> deleteTippsFromWekb = existingTippsAfterImport - tippsFound

                log.info("deleteTippsFromWekb: ${deleteTippsFromWekb.size()}")
                if(deleteTippsFromWekb.size() > 0){

                    int maxDeleted = 30000
                    int idxDeleted = 0
                    int deletedCount = deleteTippsFromWekb.size()

                    for (int offset = 0; offset < deletedCount; offset += maxDeleted) {
                        List deleteTippsFromWekbToProcess = deleteTippsFromWekb.drop(offset).take(maxDeleted)
                        KBComponent.executeUpdate("update KBComponent set status = :deleted, lastUpdated = CURRENT_DATE where id in (:tippIDs) and status != :deleted", [deleted: RDStore.KBC_STATUS_DELETED, tippIDs: deleteTippsFromWekbToProcess])
                    }

                    StatelessSession session = sessionFactory.openStatelessSession()
                    Transaction tx = session.beginTransaction()
                    deleteTippsFromWekb.each {tippID ->
                        idxDeleted++
                        log.info("deleteTippsFromWekb (#$idxDeleted of $deletedCount): tippID ${tippID}")
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(tippID)
                        UpdateTippInfo updateTippInfo = new UpdateTippInfo(
                                description: "Delete Title '${tipp.name}' because is not in KBART!",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.UPDATE_TYPE_CHANGED_TITLE,
                                oldValue: tipp.status.value,
                                newValue: 'Deleted',
                                tippProperty: 'status',
                                kbartProperty: 'status',
                                updatePackageInfo: updatePackageInfo,
                                lastUpdated: new Date(),
                                dateCreated: new Date(),
                                uuid: UUID.randomUUID().toString()
                        )
                        changedTipps++
                        session.insert(updateTippInfo)
                    }
                    tx.commit()
                    session.close()

                    log.info("Rows in KBART is not same with titles in wekb. DeleteTippsFromWekb: ${deleteTippsFromWekb.size()}, Set tipps to deleted: ${idxDeleted}")
                }

            }

            String description = "Package Update: (KbartLines: ${kbartRowsCount}, " +
                    "Processed Titles in this run: ${idx}, Titles in we:kb previously: ${previouslyTipps}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps}, Changed Titles in we:kb: ${changedTipps})"

            UpdatePackageInfo.executeUpdate("update UpdatePackageInfo set countKbartRows = ${kbartRowsCount}, " +
                    "countChangedTipps = ${changedTipps}, " +
                    "countNowTippsInWekb = ${countExistingTippsAfterImport}, " +
                    "countPreviouslyTippsInWekb = ${previouslyTipps}, " +
                    "countNewTipps = ${newTipps}, " +
                    "countRemovedTipps = ${removedTipps}, " +
                    "countInValidTipps = ${countInvalidKbartRowsForTipps}, " +
                    "countProcessedKbartRows = ${idx}, " +
                    "endTime = ${new Date()}, " +
                    "description = ${description}, " +
                    "lastUpdated = CURRENT_DATE " +
                    "where id = ${updatePackageInfo.id}")

            UpdatePackageInfo.withTransaction {

                Package aPackage = Package.get(updatePackageInfo.pkg.id)
                if (aPackage.status != status_deleted) {
                    aPackage.lastUpdated = new Date()
                    aPackage.lastUpdateComment = "Updated package with ${kbartRowsCount} Title. (Titles in we:kb previously: ${previouslyTipps}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps})"
                    aPackage.save()
                }

                if (aPackage.source && updatePackageInfo.automaticUpdate) {
                    Source src = Source.get(aPackage.source.id)
                    src.kbartHasWekbFields = !setAllTippsNotInKbartToDeleted
                    src.lastRun = new Date()
                    src.lastUpdateUrl = lastUpdateURL
                    src.lastChangedInKbart = lastChangedInKbart
                    src.save()
                }
            }
            /* log.debug("final flush");
             cleanupService.cleanUpGorm()*/

        } catch (Exception e) {
            log.error("Error by kbartImportProcess: ", e)
            UpdatePackageInfo.withTransaction {
                updatePackageInfo.refresh()
                updatePackageInfo.endTime = new Date()
                String description = "An error occurred while processing the kbart file. More information can be seen in the system log. "
                if(updatePackageInfo.automaticUpdate){
                    description = description+ "File from URL: ${lastUpdateURL}"
                }
                updatePackageInfo.description = description
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                updatePackageInfo.save()
            }
        }

        /*if(errors.global.size() > 0 || errors.tipps.size() > 0){
            log.error("Error map by kbartImportProcess: ")
        }*/
        log.info("End kbartImportProcess Package ($pkg.name)")
        return updatePackageInfo
    }

    List kbartProcess(File tsvFile, String lastUpdateURL, UpdatePackageInfo updatePackageInfo) {
        log.info("Begin KbartProcess, transmitted: ${tsvFile.length()}")
        boolean encodingPass
        int countRows = 0
        List result = []
        String encoding
        if(StandardCharsets.US_ASCII.newEncoder().canEncode(tsvFile.newInputStream().text)) {
            encodingPass = true
        }
        else {
            encoding = UniversalDetector.detectCharset(tsvFile)
            encodingPass = encoding == "UTF-8"
        }
        if(!encodingPass) {
            log.error("Encoding of file is wrong. File encoding is: ${encoding}")
            UpdatePackageInfo.withTransaction {
                String description = "Encoding of kbart file is wrong. File encoding was: ${encoding}. "
                if(updatePackageInfo.automaticUpdate){
                    description = description+ "File from URL: ${lastUpdateURL}"
                }
                updatePackageInfo.description = description
                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                updatePackageInfo.endTime = new Date()
                updatePackageInfo.save()
            }
        }
        else {
            List minimumKbartStandard = ['publication_title',
                                         'title_url',
                                         'title_id',
                                         'publication_type']
            int countMinimumKbartStandard = 0

            try {



                List<String> rows = tsvFile.newInputStream().text.split('\n')
                if(rows.size() > 1){
                    Map<String, Integer> colMap = [:]

                    String delimiter = getDelimiter(rows[0])
                    if(delimiter) {
                        rows[0].split(delimiter).eachWithIndex { String headerCol, int c ->
                            if (headerCol.startsWith("\uFEFF"))
                                headerCol = headerCol.substring(1)
                            //println("headerCol: ${headerCol}")
                            switch (headerCol.toLowerCase().trim()) {
                                case "publication_title": colMap.publication_title = c
                                    countMinimumKbartStandard++
                                    break
                                case "print_identifier": colMap.print_identifier = c
                                    break
                                case "online_identifier": colMap.online_identifier = c
                                    break
                                case "date_first_issue_online": colMap.date_first_issue_online = c
                                    break
                                case "num_first_vol_online": colMap.num_first_vol_online = c
                                    break
                                case "date_last_issue_online": colMap.date_last_issue_online = c
                                    break
                                case "num_first_issue_online": colMap.num_first_issue_online = c
                                    break
                                case "num_last_vol_online": colMap.num_last_vol_online = c
                                    break
                                case "num_last_issue_online": colMap.num_last_issue_online = c
                                    break
                                case "title_url": colMap.title_url = c
                                    countMinimumKbartStandard++
                                    break
                                case "first_author": colMap.first_author = c
                                    break
                                case "title_id": colMap.title_id = c
                                    countMinimumKbartStandard++
                                    break
                                case "embargo_info": colMap.embargo_info = c
                                    break
                                case "coverage_depth": colMap.coverage_depth = c
                                    break
                                case "notes": colMap.notes = c
                                    break
                                case "publisher_name": colMap.publisher_name = c
                                    break
                                case "publication_type": colMap.publication_type = c
                                    countMinimumKbartStandard++
                                    break
                                case "date_monograph_published_print": colMap.date_monograph_published_print = c
                                    break
                                case "date_monograph_published_online": colMap.date_monograph_published_online = c
                                    break
                                case "monograph_volume": colMap.monograph_volume = c
                                    break
                                case "monograph_edition": colMap.monograph_edition = c
                                    break
                                case "first_editor": colMap.first_editor = c
                                    break
                                case "parent_publication_title_id": colMap.parent_publication_title_id = c
                                    break
                                case "preceding_publication_title_id": colMap.preceding_publication_title_id = c
                                    break
                                case "access_type": colMap.access_type = c
                                    break

                                    //beginn with headercolumn spec for wekb
                                case "medium": colMap.medium = c
                                    break
                                case "doi_identifier": colMap.doi_identifier = c
                                    break
                                case "subject_area": colMap.subject_area = c
                                    break
                                case "language": colMap.language = c
                                    break
                                case "package_name": colMap.package_name = c
                                    break
                                case "package_id": colMap.package_id = c
                                    break
                                case "access_start_date": colMap.access_start_date = c
                                    break
                                case "access_end_date": colMap.access_end_date = c
                                    break
                                case "last_changed": colMap.last_changed = c
                                    break
                                case "status": colMap.status = c
                                    break
                                case "listprice_eur": colMap.listprice_eur = c
                                    break
                                case "listprice_usd": colMap.listprice_usd = c
                                    break
                                case "listprice_gbp": colMap.listprice_gbp = c
                                    break
                                case "monograph_parent_collection_title": colMap.monograph_parent_collection_title = c
                                    break
                                case "zdb_id": colMap.zdb_id = c
                                    break
                                case "ezb_id": colMap.ezb_id = c
                                    break
                                case "package_ezb_anchor": colMap.package_ezb_anchor = c
                                    break
                                case "oa_gold": colMap.oa_gold = c
                                    break
                                case "oa_hybrid": colMap.oa_hybrid = c
                                    break
                                case "oa_apc_eur": colMap.oa_apc_eur = c
                                    break
                                case "oa_apc_usd": colMap.oa_apc_usd = c
                                    break
                                case "oa_apc_gbp": colMap.oa_apc_gbp = c
                                    break
                                case "package_isil": colMap.package_isil = c
                                    break
                                case "package_isci": colMap.package_isci = c
                                    break
                                case "ill_indicator": colMap.ill_indicator = c
                                    break
                                case "title_gokb_uuid": colMap.title_gokb_uuid = c
                                    break
                                case "package_gokb_uuid": colMap.package_gokb_uuid = c
                                    break
                                case "title_wekb_uuid": colMap.title_wekb_uuid = c
                                    break
                                case "package_wekb_uuid": colMap.package_wekb_uuid = c
                                    break
                                default: log.info("unhandled parameter type ${headerCol}, ignoring ...")
                                    break
                            }
                        }

                        if (minimumKbartStandard.size() != countMinimumKbartStandard) {
                            log.error("KBART file does not have one or any of the headers: ${minimumKbartStandard}")
                            UpdatePackageInfo.withTransaction {
                                String description = "KBART file does not have one or any of the headers: ${minimumKbartStandard.join(', ')}. "
                                if(updatePackageInfo.automaticUpdate){
                                    description = description+ "File from URL: ${lastUpdateURL}"
                                }
                                updatePackageInfo.description = description
                                updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                updatePackageInfo.endTime = new Date()
                                updatePackageInfo.save()
                            }


                        } else {
                            //Don't delete the header
                            //rows.remove(0)
                            countRows = rows.size() - 1
                            //log.debug("Begin kbart processing rows ${countRows}")
                            rows.eachWithIndex { row, Integer r ->
                                //log.debug("now processing entry ${row}")
                                List<String> cols = row.split(delimiter)
                                Map rowMap = [:]
                                colMap.eachWithIndex { def entry, int i ->
                                    if (cols[entry.value] && !cols[entry.value].isEmpty()) {
                                        rowMap."${entry.key}" = cols[entry.value].replace("\r", "")
                                    }
                                    rowMap."${entry.key}" = rowMap."${entry.key}" ? rowMap."${entry.key}".replaceAll(/\"/, "") : rowMap."${entry.key}"
                                    rowMap."${entry.key}" = rowMap."${entry.key}" ? rowMap."${entry.key}".replaceAll("\\x00", "") : rowMap."${entry.key}"
                                }
                                rowMap.rowIndex = r
                                result << rowMap
                            }
                            //log.debug("End kbart processing rows ${countRows}")
                        }
                    }else {
                        log.error("no delimiter $delimiter: ${lastUpdateURL}")
                        UpdatePackageInfo.withTransaction {
                            String description = "Separator for the kbart was not recognized. The following separators are recognized: Tab, comma, semicolons. "
                            if(updatePackageInfo.automaticUpdate){
                                description = description+ "File from URL: ${lastUpdateURL}"
                            }
                            updatePackageInfo.description = description
                            updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                            updatePackageInfo.endTime = new Date()
                            updatePackageInfo.save()
                        }
                    }
                }else {
                    log.error("KBART file is empty:  ${lastUpdateURL}")
                    UpdatePackageInfo.withTransaction {
                        String description = "KBART file is empty. "
                        if(updatePackageInfo.automaticUpdate){
                            description = description+ "File from URL: ${lastUpdateURL}"
                        }
                        updatePackageInfo.description = description
                        updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                        updatePackageInfo.endTime = new Date()
                        updatePackageInfo.save()
                    }
                }
            } catch (Exception e) {
                log.error("Error by KbartProcess: ${e}")
                UpdatePackageInfo.withTransaction {
                    updatePackageInfo.refresh()
                    String description = "An error occurred while processing the kbart file. More information can be seen in the system log. "
                    if(updatePackageInfo.automaticUpdate){
                        description = description+ "File from URL: ${lastUpdateURL}"
                    }
                    updatePackageInfo.description = description
                    updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                    updatePackageInfo.endTime = new Date()
                    updatePackageInfo.save(flush: true)
                }

            }
        }
        log.info("End KbartProcess with ${countRows} rows")

        result
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert ? dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null
    }

    private String getDelimiter(String line) {
        log.debug("Getting delimiter for line: ${line}")
        int maxCount = 0
        String delimiter
        if (line) {

            if (line.startsWith("\uFEFF")) {
                line = line.substring(1)
            }

            for (String prop : ['comma', 'semicolon', 'tab']) {
                int num = line.count(resolver.get(prop).toString())
                if (maxCount < num) {
                    maxCount = num
                    delimiter = prop
                }
            }

        }
        log.debug("delimiter is: ${delimiter}")

        if(delimiter){
            delimiter = resolver.get(delimiter)
        }

        return delimiter
    }

    static def resolver = [
            'comma'      : ',',
            'semicolon'  : ';',
            'tab'        : '\t',
    ]
}
