package wekb

import com.k_int.ConcurrencyManagerService
import de.hbznrw.ygor.tools.DateToolkit
import de.hbznrw.ygor.tools.UrlToolkit
import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import gokbg3.MessageService
import grails.converters.JSON
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovyx.net.http.RESTClient
import org.apache.commons.io.FileUtils
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.checkerframework.checker.units.qual.K
import org.gokb.CleanupService
import org.gokb.CrossReferenceService

import org.gokb.cred.JobResult
import org.gokb.cred.KBComponent
import org.gokb.cred.Note
import org.gokb.cred.Package
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.gokb.cred.Source
import org.gokb.cred.TitleInstancePackagePlatform
import org.gokb.cred.UpdateToken
import org.gokb.cred.User
import org.grails.web.json.JSONObject
import org.mozilla.universalchardet.UniversalDetector
import org.springframework.web.multipart.MultipartFile

import java.nio.file.Files
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneId
import java.time.temporal.ChronoUnit

import static groovyx.net.http.Method.GET

import groovyx.gpars.GParsPool


class AutoUpdatePackagesService {

    static final THREAD_POOL_SIZE = 5
    public static boolean running = false;
    Map result = [result: JobResult.STATUS_SUCCESS]
    ExportService exportService
    KbartImportValidationService kbartImportValidationService
    KbartImportService kbartImportService
    CleanupService cleanupService
    MessageService messageService

    void findPackageToUpdateAndUpdate(boolean onlyRowsWithLastChanged = false) {
        List packageNeedsUpdate = []
        def updPacks = Package.executeQuery(
                "from Package p " +
                        "where p.source is not null and " +
                        "p.source.automaticUpdates = true " +
                        "and (p.source.lastRun is null or p.source.lastRun < current_date)")
        updPacks.each { Package p ->
            if (p.source.needsUpdate()) {
                packageNeedsUpdate << p
            }
        }
        log.info("findPackageToUpdateAndUpdate: Package with Source and lastRun < currentDate (${packageNeedsUpdate.size()})")
        if(packageNeedsUpdate.size() > 0){
                packageNeedsUpdate.each { Package aPackage ->
                    startAutoPackageUpdate(aPackage, onlyRowsWithLastChanged)
                }
        }

    }

   /* @Deprecated
    Map updateFromSource(Package p, User user = null, ignoreLastChanged = false) {
        log.debug("updateFromSource")

        Date currentDate = new Date()
        Date currentDatePlus12Hours

        if(runningStartDate) {
            Calendar c = Calendar.getInstance()
            c.setTime(runningStartDate)
            c.add(Calendar.HOUR, +12)
            currentDatePlus12Hours = c.getTime()
        }

        //Wenn Running blockiert ist und dies länger als ein Tag
        if(running && runningStartDate && currentDatePlus12Hours.after(currentDate)){
            running = false
            runningStartDate = null
        }

        Date startTime = new Date()
        def uuid = UUID.randomUUID().toString()
        if(p.source) {
            if (running == false || user) {

                if (!user) {
                    runningStartDate = new Date()
                    running = true
                }

                log.debug("UpdateFromSource started")
                Map ygorData = startSourceUpdate(p, user, ignoreLastChanged)
                if (ygorData.size() > 0) {
                    result = [ygorData: ygorData, result: JobResult.STATUS_SUCCESS, message: (user ? "Manuell" : "Auto") + " Update Package success"]
                }
                if (!user) {
                    runningStartDate = null
                    running = false
                }
            } else {
                log.debug("update skipped - already running")
                result = [result: JobResult.STATUS_FAIL, message: (user ? "Manuell" : "Auto") + " Update Package is already running"]
            }
        }else{
            log.debug("update skipped - packge have no source")
            result = [result: JobResult.STATUS_FAIL, message: " Package have no source"]
        }

        Long curatoryGroupId
        if (p.curatoryGroups && p.curatoryGroups.size() > 0) {

            if(user) {
                List curatory_group_ids = user.curatoryGroups.id?.intersect(p.curatoryGroups?.id)
                if (curatory_group_ids.size() == 1) {
                    curatoryGroupId = curatory_group_ids[0]
                } else if (curatory_group_ids.size() > 1) {
                    log.debug("Got more than one cg candidate!")
                    curatoryGroupId = curatory_group_ids[0]
                }
            }else {
                curatoryGroupId = p.curatoryGroups.id[0]
            }
        }

        def result_object = JobResult.findByUuid(uuid)
        if (!result_object) {
            def job_map = [
                    uuid        : uuid,
                    description : (user ? "Manuell" : "Auto") + " Update Packages Job (${p.name})",
                    resultObject: result.toString(),
                    type        : user ? RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'ManuellUpdatePackageJob') : RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'AutoUpdatePackagesJob'),
                    statusText  : result.result,
                    startTime   : startTime,
                    endTime     : new Date(),
                    linkedItemId: p.id,
                    groupId: curatoryGroupId
            ]
            new JobResult(job_map).save(flush: true)
        }
        result
    }*/

/*    @Deprecated
    *//**
     * this method calls Ygor to perform an automated Update on this package.
     * Bad configurations will result in failure.
     * The autoUpdate frequency in the source is ignored: the update starts immediately.
     *//*
    private Map startSourceUpdate(Package p, User user = null, boolean ignoreLastChanged = false) {
        log.debug("Source update start..")
        boolean error = false
        Map ygorData = [:]
        ygorData.pkg = p
        def ygorBaseUrl = grailsApplication.config.gokb.ygorUrl


        if (ygorBaseUrl?.endsWith('/')) {
            ygorBaseUrl = ygorBaseUrl.length() - 1
        }

        def updateTrigger
        def tokenValue = p.updateToken?.value ?: null
        def respData

        if (user && !tokenValue) {
            String charset = (('a'..'z') + ('0'..'9')).join()
            tokenValue = RandomStringUtils.random(255, charset.toCharArray())


            if (p.updateToken) {
                UpdateToken currentToken = p.updateToken
                currentToken.updateUser = user
                currentToken.value = tokenValue
                currentToken.save(flush: true)
            } else {
                UpdateToken newToken = new UpdateToken(pkg: p, updateUser: user, value: tokenValue).save(flush: true)
            }

        }


        if (tokenValue && ygorBaseUrl) {
            def path = "/enrichment/processGokbPackage?pkgId=${p.id}&updateToken=${tokenValue}"
            if (ignoreLastChanged) {
                path = "/enrichment/processGokbPackage?pkgId=${p.id}&ignoreLastChanged=true&updateToken=${tokenValue}"
            }
            updateTrigger = new RESTClient(ygorBaseUrl + path)

            try {
                log.debug("GET ygor" + path)
                updateTrigger.request(GET) { request ->
                    response.success = { resp, data ->
                        log.debug("GET ygor${path} => success")
                        // wait for ygor to finish the enrichment
                        boolean processing = true
                        respData = data

                        if (!respData || !respData.jobId) {
                            log.error("no ygor job Id received, skipping update of ${p.id}!")
                            if (respData?.message) {
                                log.error("ygor message: ${respData.message}")
                            }

                            if (respData?.ygorFeedback) {
                                log.error("ygorFeedback: ${respData.ygorFeedback}")
                            }

                            result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: ${respData.message}"]]]
                            processing = false
                            //error = true
                        }

                        String statusUrl = ygorBaseUrl + "/enrichment/getStatus?noGokbJobId=true&jobId=${respData.jobId}"
                        def statusService = new RESTClient(statusUrl)

                        LocalTime ygorStartTime = LocalTime.now()
                        String ygorStatus = ""
                        while (processing == true) {
                            log.debug("GET ${statusUrl}")
                            statusService.request(GET) { req ->
                                response.success = { statusResp, statusData ->
                                    log.debug("GET  ${statusUrl} => success")
                                    log.debug("status of Ygor ${statusData.status} gokbJob #${statusData.gokbJobId}")
                                    if (statusData.status == 'FINISHED_UNDEFINED') {
                                        processing = false
                                        result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: ${statusData.message}"]]]
                                        log.debug("${statusData.message}")
                                    } else if (statusData.status == 'ERROR') {
                                        processing = false
                                        result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: ${statusData.message}"]]]
                                        log.debug("${statusData.message}")
                                    }
                                    else if (statusData.status == 'SUCCESS') {
                                        processing = false
                                        ygorData.ygorJobId = respData.jobId
                                        ygorData.ygorResultHash = statusData.resultHash
                                        *//*task {
                                            checkPackageJob(respData.jobId, p)
                                        }*//*
                                    } else {

                                        if (ygorStartTime < LocalTime.now().minus(45, ChronoUnit.MINUTES)){
                                            processing = false
                                            log.error("ygor status is still the same after 45 minutes: $statusData.status")
                                            result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: Ygor couldn't process the kbart after 45 minutes"]]]
                                            //error = true
                                        }

                                        if(ygorStatus != statusData.status)
                                        {
                                            ygorStatus = statusData.status
                                            ygorStartTime = LocalTime.now()
                                        }


                                        sleep(10000) // 10 sec
                                    }
                                }
                                response.failure = { statusResp, statusData ->
                                    log.error("GET ygor/enrichment/getStatus?jobId=${respData.jobId} => failure")
                                    log.error("ygor response message: $statusData.message")

                                    result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: Response message: $statusData.message"]]]
                                    processing = false
                                    //error = true
                                }
                            }
                        }
                    }
                    response.failure = { resp, data ->
                        log.error("GET ygor${path} => failure")
                        log.error("ygor response: ${resp.responseBase}")
                        respData = data

                        if (respData.ygorFeedback) {
                            log.error("ygorFeedback: ${respData.ygorFeedback}")
                        }

                        result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: Response message: ${respData.message}"]]]
                        //error = true
                    }
                }
            } catch (Exception e) {
                log.error("SourceUpdate Exception:", e);
                result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "wekb ERROR: SourceUpdate Exception"]]]
                //error = true
            }
        } else {
            result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "wekb ERROR: No user provided and no existing updateToken found!"]]]
            log.debug("No user provided and no existing updateToken found!")
            //error = true
        }

        return ygorData
    }*/


    static List<URL> getUpdateUrls(String url, String lastProcessingDate, String packageCreationDate) {
        if (StringUtils.isEmpty(lastProcessingDate)) {
            lastProcessingDate = packageCreationDate
        }
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(lastProcessingDate)) {
            return new ArrayList<URL>()
        }
        if (UrlToolkit.containsDateStamp(url) || UrlToolkit.containsDateStampPlaceholder(url)) {
            return UrlToolkit.getUpdateUrlList(url, lastProcessingDate)
        } else {
            return Arrays.asList(new URL(url))
        }
    }

  /*  @Deprecated
    private void checkPackageJob(String gokbJobId, Package aPackage) {
        log.debug("task start...")

        ConcurrencyManagerService.Job job = concurrencyManagerService.getJob(gokbJobId)
        while (!job.isDone() && job.get() == null) {
            this.wait(5000) // 5 sec
            log.debug("checking xRefPackage status...")
        }
        log.debug("xRefPackage Job done!")
        def xRefResult = job.get()
        if (xRefResult) {
            if (xRefResult.result == "OK") {
                log.debug("xRefPackage result OK")
                Source source = aPackage.source
                List<URL> updateUrls
                if (aPackage.currentTippCount == 0) {
                    // this is obviously a new package --> update with older timestamp
                    updateUrls = new ArrayList<>()
                    updateUrls.add(new URL(source.url))
                } else {
                    // this package had already been filled with data
                    updateUrls = getUpdateUrls(source.url, source.lastRun, aPackage.dateCreated)
                }
                log.info("Got ${updateUrls}")
                updateUrls = UrlToolkit.removeNonExistentURLs(updateUrls)
                Iterator urlsIterator = updateUrls.listIterator(updateUrls.size())
                if (updateUrls.size() > 0) {
                    while (urlsIterator.hasPrevious()) {
                        URL url = urlsIterator.previous()
                    }
                }
                aPackage.source.lastRun = new Date()
                aPackage.source.save()

                log.debug("set ${aPackage.source.getNormname()}.lastRun = now")
            }
        }
    }*/

/*
    @Deprecated
    void importJsonFromUpdateSource(Map ygorData) {
        log.debug("importJsonFromUpdateSource .. ${ygorData}")
        if (grailsApplication.config.ygorUploadJsonLocation && ygorData.size() > 0) {
            boolean addOnly = false
            boolean fullsync = false
            if (ygorData.ygorResultHash) {

                //String jsonSlurper = new JsonSlurper().parse().toString()
                JSON json = new JSON()
                InputStream inputStream = new FileInputStream(new File("${grailsApplication.config.ygorUploadJsonLocation}/${ygorData.ygorResultHash}.packageWithTitleData.json"))
                def jsonData = json.parse(inputStream, 'UTF-8')

                UpdateToken updateToken = UpdateToken.findByPkg(ygorData.pkg)
                User request_user = updateToken.updateUser

                if (jsonData.packageHeader) {
                    jsonData.packageHeader.uuid = updateToken.pkg.uuid
                }

                if (ygorData.ygorResultHash) {
                    jsonData.ygorStatisticResultHash = ygorData.ygorResultHash
                }

                ConcurrencyManagerService.Job background_job = concurrencyManagerService.createJob { ConcurrencyManagerService.Job job ->
                    crossReferenceService.xRefPkg(jsonData, addOnly, fullsync,
                            true, Locale.ENGLISH, request_user, job)
                }
                log.debug("Starting job ${background_job}..")
                background_job.description = "Package CrossRef (${jsonData.packageHeader.name})"
                background_job.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'PackageCrossRef')
                background_job.linkedItem = [name: jsonData.packageHeader.name,
                                             type: "Package"]
                background_job.message("Starting upsert for Package ${jsonData.packageHeader.name}")
                background_job.startOrQueue()
                background_job.startTime = new Date()
            }
        }

    }
*/

    void startAutoPackageUpdate(Package pkg, boolean onlyRowsWithLastChanged = false){
        log.info("Begin startAutoPackageUpdate Package ($pkg.name)")
        List kbartRows =  []
        String lastUpdateURL =""
        Date startTime = new Date()
        if(pkg.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]){
            AutoUpdatePackageInfo autoUpdatePackageInfo = new AutoUpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL, description: "Package status is ${pkg.status.value}. Auto update for this package is not starting.", onlyRowsWithLastChanged: onlyRowsWithLastChanged).save()
        }else {
            AutoUpdatePackageInfo autoUpdatePackageInfo = new AutoUpdatePackageInfo(pkg: pkg, startTime: startTime, status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL, description: "Starting auto update package.", onlyRowsWithLastChanged: onlyRowsWithLastChanged)
            try {
                if (pkg.source) {
                    List<URL> updateUrls
                    if (pkg.getTippCount() <= 0) {
                        updateUrls = new ArrayList<>()
                        updateUrls.add(new URL(pkg.source.url))
                    } else {
                        // this package had already been filled with data
                        if (pkg.source.lastUpdateUrl) {
                            updateUrls = getUpdateUrls(pkg.source.lastUpdateUrl, pkg.source.lastRun.toString(), pkg.dateCreated.toString())
                        } else {
                            updateUrls = getUpdateUrls(pkg.source.url, pkg.source.lastRun.toString(), pkg.dateCreated.toString())
                        }
                    }
                    log.info("Got ${updateUrls}")
                    Iterator urlsIterator = updateUrls.listIterator(updateUrls.size())

                    File file
                    if (updateUrls.size() > 0) {
                        while (urlsIterator.hasPrevious()) {
                            URL url = urlsIterator.previous()
                            lastUpdateURL = url.toString()
                            try {
                                file = exportService.kbartFromUrl(lastUpdateURL)

                            }
                            catch (Exception e) {
                                log.error("get kbartFromUrl: ${e.message}")
                                continue
                            }

                        }

                        if (file) {
                            kbartRows = kbartProcess(file, lastUpdateURL, autoUpdatePackageInfo)
                        } else {
                            AutoUpdatePackageInfo.withNewTransaction {
                                autoUpdatePackageInfo.description = "No KBART File found by URL: ${lastUpdateURL}!"
                                autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                                autoUpdatePackageInfo.endTime = new Date()
                                autoUpdatePackageInfo.save()
                            }
                        }

                    }

                    if (kbartRows.size() > 0) {
                        autoUpdatePackageInfo = kbartImportProcess(kbartRows, pkg, lastUpdateURL, autoUpdatePackageInfo, onlyRowsWithLastChanged)
                    }
                }

            } catch (Exception exception) {
                log.error("startAutoPackageUapdate: ${exception.message}")
                AutoUpdatePackageInfo.withTransaction {
                    AutoUpdatePackageInfo autoUpdatePackageFail = new AutoUpdatePackageInfo()
                    autoUpdatePackageFail.description = "An error occurred while processing the kbart file. More information can be seen in the system log. File from URL: ${lastUpdateURL}"
                    autoUpdatePackageFail.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                    autoUpdatePackageFail.startTime = startTime
                    autoUpdatePackageFail.endTime = new Date()
                    autoUpdatePackageFail.pkg = pkg
                    autoUpdatePackageFail.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                    autoUpdatePackageFail.save()
                }
            }
        }

        log.info("End startAutoPackageUpdate Package ($pkg.name)")
    }

    AutoUpdatePackageInfo kbartImportProcess(List kbartRows, Package pkg, String lastUpdateURL, AutoUpdatePackageInfo autoUpdatePackageInfo, Boolean onlyRowsWithLastChanged) {
        log.info("Begin kbartImportProcess Package ($pkg.name)")
        int total = 0
        boolean addOnly = false //Thing about it where to set or to change

        AutoUpdatePackageInfo.withNewTransaction {
            autoUpdatePackageInfo.save()
        }

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
            if(headerOfKbart.containsKey("status")){
                log.info("kbart has status field and is wekb standard")
                setAllTippsNotInKbartToDeleted = false
                listStatus = [status_current, status_expected, status_deleted, status_retired]
            }
        }

        if(addOnly){
            setAllTippsNotInKbartToDeleted = false
        }

        List<Long> existing_tipp_ids = TitleInstancePackagePlatform.executeQuery(
                "select tipp.id from TitleInstancePackagePlatform tipp, Combo combo where " +
                        "tipp.status in :status and " +
                        "combo.toComponent = tipp and " +
                        "combo.fromComponent = :package",
                [package: pkg, status: listStatus])

        LinkedHashMap tippsWithCoverage = [:]
        List<Long> tippDuplicates = []
        List setTippsNotToDeleted = []
        Map errors = [global: [], tipps: []]

        List<Long> tippsFound = []
        def invalidTipps = []
        int removedTipps = 0
        int newTipps = 0
        int changedTipps = 0

        int kbartRowsCount = kbartRows.size()

        try {

            log.info("Matched package has ${pkg.tipps.size()} TIPPs")

            int idx = 0

            if(onlyRowsWithLastChanged){
                if(headerOfKbart.containsKey("last_changed")) {
                    log.info("onlyRowsWithLastChanged is set! before process only last changed rows: ${kbartRowsCount}")
                    List newKbartRows = []
                    LocalDate lastUpdated = convertToLocalDateViaInstant(pkg.source.lastRun)
                    kbartRows.eachWithIndex { Object entry, int i ->
                        if (entry.containsKey("last_changed") && entry.last_changed != null && entry.last_changed != "") {
                            LocalDate lastChanged = DateToolkit.getAsLocalDate(entry.last_changed)
                            if (lastChanged == null || lastUpdated == null || !lastChanged.isBefore(lastUpdated)) {
                                newKbartRows << entry
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

            for (def kbartRow : kbartRows) {
                idx++
                def currentTippError = [index: idx]
                log.info("kbartImportProcess (#$idx of $kbartRowsCount): title ${kbartRow.publication_title}")
                if (!invalidTipps.contains(kbartRow)) {

                    kbartRow.pkg = pkg
                    kbartRow.nominalPlatform = pkg.nominalPlatform

                    Map tippErrorMap = [:]
                    def validation_result = kbartImportValidationService.tippValidateForAutoUpdate(kbartRow)
                    if (!validation_result.valid) {
                        invalidTipps << kbartRow
                        log.debug("TIPP Validation failed on ${kbartRow.publication_title}")
                        tippErrorMap = validation_result.errors
                    }
                    else {
                        if (validation_result.errors?.size() > 0) {
                            tippErrorMap.putAll(validation_result.errors)
                        }
                        TitleInstancePackagePlatform updateTipp = null
                        try {
                            Map autoUpdateResultTipp = kbartImportService.tippImportForAutoUpdate(kbartRow, tippsWithCoverage, tippDuplicates, autoUpdatePackageInfo)
                            updateTipp = autoUpdateResultTipp.tippObject

                            if(autoUpdateResultTipp.newTipp){
                                newTipps++
                            }

                            if(autoUpdateResultTipp.removedTipp){
                                removedTipps++
                            }

                            if(autoUpdateResultTipp.changedTipp){
                                changedTipps++
                            }

                            if(setAllTippsNotInKbartToDeleted && updateTipp && updateTipp.status !=  RDStore.KBC_STATUS_CURRENT){
                                updateTipp.status = RDStore.KBC_STATUS_CURRENT
                                setTippsNotToDeleted << updateTipp.id
                            }

                            updateTipp.save(flush: true)
                            tippsFound << updateTipp.id

                        }
                        catch (grails.validation.ValidationException ve) {
                            if(!invalidTipps.contains(kbartRow)) {
                                invalidTipps << kbartRow
                            }

                            log.error("ValidationException attempting to cross reference TIPP", ve)
                            updateTipp?.discard()
                            tippErrorMap.putAll(messageService.processValidationErrors(ve.errors))
                        }
                        catch (Exception ge) {
                            if(!invalidTipps.contains(kbartRow)) {
                                invalidTipps << kbartRow
                            }
                            log.error("Exception attempting to cross reference TIPP:", ge)
                            def tipp_error = [
                                    message: messageService.resolveCode('crossRef.package.tipps.error', [kbartRow.publication_title], Locale.ENGLISH),
                                    baddata: kbartRow,
                                    errors : [message: ge.toString()]
                            ]
                            updateTipp?.discard()
                            tippErrorMap = tipp_error
                        }
                        if (updateTipp) {

                            if (updateTipp.status != RDStore.KBC_STATUS_CURRENT && (!kbartRow.status || kbartRow.status == "Current")) {
                                updateTipp.status = status_current
                            }

                            updateTipp.save(flush: true)

                            if (updateTipp.isCurrent() && updateTipp.hostPlatform?.status != status_current) {
                            }
                        }
                        else {
                            log.debug("Could not reference TIPP")
                            invalidTipps << kbartRow
                            def tipp_error = [
                                    message: messageService.resolveCode('crossRef.package.tipps.error', [kbartRow.publication_title], Locale.ENGLISH),
                                    baddata: kbartRow
                            ]
                            tippErrorMap = tipp_error
                        }
                    }

                    if (tippErrorMap.size() > 0) {
                        currentTippError.put('tipp', tippErrorMap)
                    }
                }

                if (currentTippError.size() > 1) {
                    errors.tipps.add(currentTippError)
                }

                if (idx % 10 == 0) {
                    log.info("Clean up");
                    cleanupService.cleanUpGorm()
                }
            }

            if(tippDuplicates.size() > 0){
                log.info("remove tippDuplicates -> ${tippDuplicates.size()}: ${tippDuplicates}")

                tippDuplicates.each {
                    if(!(it in tippsFound)){
                        KBComponent.executeUpdate("update KBComponent set status = :removed where id = (:tippId)", [removed: RDStore.KBC_STATUS_REMOVED, tippId: it])

                        AutoUpdateTippInfo.withTransaction {
                            autoUpdatePackageInfo.refresh()
                            TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                            AutoUpdateTippInfo autoUpdateTippInfo = new AutoUpdateTippInfo(
                                    description: "Remove Title '${tipp.name}' because is a duplicate in wekb!",
                                    tipp: tipp,
                                    startTime: new Date(),
                                    endTime: new Date(),
                                    status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.AUTO_UPDATE_TYPE_REMOVED_TITLE,
                                    oldValue: tipp.status.value,
                                    newValue: 'Removed',
                                    tippProperty: 'status',
                                    autoUpdatePackageInfo: autoUpdatePackageInfo
                            ).save()
                        }
                    }
                }

            }

/*            if (invalidTipps.size() > 0) {
                String msg = messageService.resolveCode('crossRef.package.tipps.ignored', [invalidTipps.size()], Locale.ENGLISH)
                log.warn(msg)
                errors.global.add([message: msg, baddata: pkg.name])
            }*/

            if (kbartRows.size() > 0 && kbartRows.size() > invalidTipps.size()) {
            } else {
                log.info("imported Package $pkg.name contains no valid TIPPs")
            }


            if (setAllTippsNotInKbartToDeleted) {

                List<Long> tippsIds = setTippsNotToDeleted ? TitleInstancePackagePlatform.executeQuery("select tipp.id from TitleInstancePackagePlatform tipp, Combo combo where " +
                        "tipp.status in :status and " +
                        "combo.toComponent = tipp and " +
                        "combo.fromComponent = :package and tipp.id not in (:setTippsNotToDeleted)",
                        [package: pkg, status: [status_current, status_expected, status_retired], setTippsNotToDeleted: setTippsNotToDeleted]) : []

                Integer tippsToDeleted = tippsIds ? KBComponent.executeUpdate("update KBComponent set status = :deleted where id in (:tippIds)", [deleted: status_deleted, tippIds: tippsIds]) : 0

                tippsIds.each {
                    AutoUpdateTippInfo.withTransaction {
                        autoUpdatePackageInfo.refresh()
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                        AutoUpdateTippInfo autoUpdateTippInfo = new AutoUpdateTippInfo(
                                description: "Delete Title '${tipp.name}' because is not in KBART!",
                                tipp: tipp,
                                startTime: new Date(),
                                endTime: new Date(),
                                status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL,
                                type: RDStore.AUTO_UPDATE_TYPE_CHANGED_TITLE,
                                oldValue: tipp.status.value,
                                newValue: 'Deleted',
                                tippProperty: 'status',
                                autoUpdatePackageInfo: autoUpdatePackageInfo
                        ).save()
                    }
                }

                log.info("kbart is not wekb standard. set title to deleted. Found tipps: ${tippsIds.size()}, Set tipps to deleted: ${tippsToDeleted}")
            }


            tippsWithCoverage.each {
                TitleInstancePackagePlatform titleInstancePackagePlatform = TitleInstancePackagePlatform.get(it.key)

                if (titleInstancePackagePlatform) {
                    kbartImportService.createOrUpdateCoverageForTipp(titleInstancePackagePlatform, it.value)
                }

            }

            int countExistingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                    "select count(tipp.id) from TitleInstancePackagePlatform tipp, Combo combo where " +
                            "tipp.status in :status and " +
                            "combo.toComponent = tipp and " +
                            "combo.fromComponent = :package",
                    [package: pkg, status: listStatus])[0]


            if(countExistingTippsAfterImport > kbartRowsCount){

                List<Long> existingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                        "select tipp.id from TitleInstancePackagePlatform tipp, Combo combo where " +
                                "tipp.status in :status and " +
                                "combo.toComponent = tipp and " +
                                "combo.fromComponent = :package",
                        [package: pkg, status: listStatus])

                List<Long> removeTippsFromWekb = existingTippsAfterImport - tippsFound

                if(removeTippsFromWekb.size()){
                    Integer tippsToRemoved = KBComponent.executeUpdate("update KBComponent set status = :removed where id in (:tippIds)", [removed: RDStore.KBC_STATUS_REMOVED, tippIds: removeTippsFromWekb])

                    removeTippsFromWekb.each {
                        AutoUpdateTippInfo.withTransaction {
                            autoUpdatePackageInfo.refresh()
                            TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                            AutoUpdateTippInfo autoUpdateTippInfo = new AutoUpdateTippInfo(
                                    description: "Remove Title '${tipp.name}' because is not in KBART!",
                                    tipp: tipp,
                                    startTime: new Date(),
                                    endTime: new Date(),
                                    status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL,
                                    type: RDStore.AUTO_UPDATE_TYPE_REMOVED_TITLE,
                                    oldValue: tipp.status.value,
                                    newValue: 'Removed',
                                    tippProperty: 'status',
                                    autoUpdatePackageInfo: autoUpdatePackageInfo
                            ).save()
                        }
                    }

                    log.info("Rows in KBART is not same with titles in wekb. RemoveTippsFromWekb: ${removeTippsFromWekb.size()}, Set tipps to removed: ${tippsToRemoved}")
                }

            }


            AutoUpdatePackageInfo.withNewTransaction {
                autoUpdatePackageInfo.refresh()
                autoUpdatePackageInfo.countKbartRows = kbartRowsCount
                autoUpdatePackageInfo.countChangedTipps = changedTipps
                autoUpdatePackageInfo.countNewTipps = newTipps
                autoUpdatePackageInfo.countRemovedTipps = removedTipps
                autoUpdatePackageInfo.countInValidTipps = invalidTipps.size()
                autoUpdatePackageInfo.countProcessedKbartRows = idx
                autoUpdatePackageInfo.endTime = new Date()
                autoUpdatePackageInfo.description = "Package Update: (KbartLines: ${kbartRowsCount}, " +
                        "Processed Titles in this run: ${idx}, Titles in we:kb previously: ${existing_tipp_ids.size()}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps}, Changed Titles in we:kb: ${changedTipps})"
                //autoUpdatePackageInfo.save(failOnError:true)
                autoUpdatePackageInfo.save()

                Package aPackage = Package.get(autoUpdatePackageInfo.pkg.id)
                if (aPackage.status != status_deleted) {
                    aPackage.lastUpdateComment = "Updated package with ${kbartRowsCount} Title. (Titles in we:kb previously: ${existing_tipp_ids.size()}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps})"
                    aPackage.save()
                }

                if (aPackage.source) {

                    Source src = Source.get(aPackage.source.id)
                    src.lastRun = new Date()
                    src.lastUpdateUrl = lastUpdateURL
                    src.save()
                }
          }
           /* log.debug("final flush");
            cleanupService.cleanUpGorm()*/

        } catch (Exception e) {
            log.error("exception caught: ", e)
            AutoUpdatePackageInfo.withNewTransaction {
                autoUpdatePackageInfo.refresh()
                autoUpdatePackageInfo.endTime = new Date()
                autoUpdatePackageInfo.description = "An error occurred while processing the kbart file. More information can be seen in the system log. File from URL: ${lastUpdateURL}"
                autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                autoUpdatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                autoUpdatePackageInfo.save()
            }
        }

        log.info("End kbartImportProcess Package ($pkg.name)")
        return autoUpdatePackageInfo
    }

   List kbartProcess(File tsvFile, String lastUpdateURL, AutoUpdatePackageInfo autoUpdatePackageInfo) {
       log.info("Begin KbartProcess")

       int countRows = 0
       List result = []

       String encoding = UniversalDetector.detectCharset(tsvFile.newInputStream())
        if(encoding != "UTF-8") {
            log.error("Encoding of file is wrong. File encoding is: ${encoding}")
            AutoUpdatePackageInfo.withNewTransaction {
                autoUpdatePackageInfo.description = "Encoding of kbart file is wrong. File encoding was: ${encoding}. File from URL: ${lastUpdateURL}"
                autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                autoUpdatePackageInfo.endTime = new Date()
                autoUpdatePackageInfo.save()
            }

        }else {
            List minimumKbartStandard = ['publication_title',
                                         'title_url',
                                         'title_id',
                                         'publication_type']
            int countMinimumKbartStandard = 0

            try {
                List<String> rows = tsvFile.newInputStream().text.split('\n')
                Map<String, Integer> colMap = [:]
                rows[0].split('\t').eachWithIndex { String headerCol, int c ->
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
                    log.info("KBART file has in header not the minimumKbartStandard: ${minimumKbartStandard}")
                    AutoUpdatePackageInfo.withNewTransaction {
                        autoUpdatePackageInfo.description = "KBART file has in header not minimum of this headers: ${minimumKbartStandard.join(', ')}. File from URL: ${lastUpdateURL}"
                        autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                        autoUpdatePackageInfo.endTime = new Date()
                        autoUpdatePackageInfo.save()
                    }


                } else {
                    //Don't delete the header
                    //rows.remove(0)
                    countRows = rows.size()-1
                    rows.eachWithIndex { row, Integer r ->
                        log.debug("now processing entry ${row}")
                        List<String> cols = row.split('\t')
                        Map rowMap = [:]
                        colMap.eachWithIndex { def entry, int i ->
                            if (cols[entry.value] && !cols[entry.value].isEmpty())
                                rowMap."${entry.key}" = cols[entry.value].replace("\r", "")
                                rowMap."${entry.key}" = rowMap."${entry.key}" ? rowMap."${entry.key}".replaceAll(/\"/,"") : rowMap."${entry.key}"
                        }

                        result << rowMap
                    }
                }
            } catch (Exception e) {
                log.error("${e}")
                AutoUpdatePackageInfo.withNewTransaction {
                    autoUpdatePackageInfo.refresh()
                    autoUpdatePackageInfo.description = "An error occurred while processing the kbart file. More information can be seen in the system log. File from URL: ${lastUpdateURL}"
                    autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                    autoUpdatePackageInfo.endTime = new Date()
                    autoUpdatePackageInfo.save(flush: true)
                }

            }
        }
        log.info("End KbartProcess with ${countRows} rows")

        result
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
