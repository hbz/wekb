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
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.JobResult
import org.gokb.cred.KBComponent
import org.gokb.cred.Note
import org.gokb.cred.Package
import org.gokb.cred.Platform
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.gokb.cred.Source
import org.gokb.cred.TitleInstancePackagePlatform
import org.gokb.cred.UpdateToken
import org.gokb.cred.User
import org.grails.web.json.JSONObject
import org.hibernate.Session
import org.mozilla.universalchardet.UniversalDetector
import org.springframework.web.multipart.MultipartFile

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.text.ParseException
import java.time.LocalDate
import java.time.LocalTime
import java.time.Year
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.ExecutorService

import static groovyx.net.http.Method.GET

import groovyx.gpars.GParsPool
import java.util.concurrent.Future

@Transactional
class AutoUpdatePackagesService {

    static final THREAD_POOL_SIZE = 5
    public static boolean running = false;
    Map result = [result: JobResult.STATUS_SUCCESS]
    ExportService exportService
    KbartImportValidationService kbartImportValidationService
    KbartImportService kbartImportService
    CleanupService cleanupService
    MessageService messageService
    ExecutorService executorService
    Future activeFuture

    void findPackageToUpdateAndUpdate(boolean onlyRowsWithLastChanged = false) {
        List packageNeedsUpdate = []
        def updPacks = Package.executeQuery(
                "from Package p " +
                        "where p.source is not null and " +
                        "p.source.automaticUpdates = true " +
                        "and (p.source.lastRun is null or p.source.lastRun < current_date) order by p.source.lastRun")
        updPacks.each { Package p ->
            if (p.source.needsUpdate()) {
                packageNeedsUpdate << p
            }
        }
        log.info("findPackageToUpdateAndUpdate: Package with Source and lastRun < currentDate (${packageNeedsUpdate.size()})")
        if(packageNeedsUpdate.size() > 0){
              /*  packageNeedsUpdate.eachWithIndex { Package aPackage, int idx ->
                    while(!(activeFuture) || activeFuture.isDone() || idx == 0) {
                        activeFuture = executorService.submit({
                            Package pkg = Package.get(aPackage.id)
                            Thread.currentThread().setName('startAutoPackageUpdate' + aPackage.id)
                            startAutoPackageUpdate(pkg, onlyRowsWithLastChanged)
                        })
                        println("Wait")
                    }
                    println("Test:"+aPackage.name)
                }*/
            GParsPool.withPool(THREAD_POOL_SIZE) { pool ->
                packageNeedsUpdate.anyParallel { aPackage ->
                    startAutoPackageUpdate(aPackage, onlyRowsWithLastChanged)
                }
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


    static List<URL> getUpdateUrls(String url, Date lastProcessingDate, Date packageCreationDate) {
        if (lastProcessingDate == null) {
            lastProcessingDate = packageCreationDate
        }
        if (StringUtils.isEmpty(url) || lastProcessingDate == null) {
            return new ArrayList<URL>()
        }
        if (UrlToolkit.containsDateStamp(url) || UrlToolkit.containsDateStampPlaceholder(url)) {
            return UrlToolkit.getUpdateUrlList(url, lastProcessingDate.toString())
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
            List kbartRows = []
            String lastUpdateURL = ""
            Date startTime = new Date()
            if (pkg.status in [RDStore.KBC_STATUS_REMOVED, RDStore.KBC_STATUS_DELETED]) {
                AutoUpdatePackageInfo autoUpdatePackageInfo = new AutoUpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL, description: "Package status is ${pkg.status.value}. Auto update for this package is not starting.", onlyRowsWithLastChanged: onlyRowsWithLastChanged)
                autoUpdatePackageInfo.save()
            } else {
                AutoUpdatePackageInfo autoUpdatePackageInfo = new AutoUpdatePackageInfo(pkg: pkg, startTime: startTime, status: RDStore.AUTO_UPDATE_STATUS_SUCCESSFUL, description: "Starting auto update package.", onlyRowsWithLastChanged: onlyRowsWithLastChanged)
                try {
                    if (pkg.source && pkg.source.url) {
                        List<URL> updateUrls
                        if (pkg.getTippCount() <= 0 || pkg.source.lastRun == null) {
                            updateUrls = new ArrayList<>()
                            updateUrls.add(new URL(pkg.source.url))
                        } else {
                            // this package had already been filled with data
                            if ((UrlToolkit.containsDateStamp(pkg.source.url) || UrlToolkit.containsDateStampPlaceholder(pkg.source.url)) && pkg.source.lastUpdateUrl) {
                                updateUrls = getUpdateUrls(pkg.source.lastUpdateUrl, pkg.source.lastRun, pkg.dateCreated)
                            } else {
                                updateUrls = getUpdateUrls(pkg.source.url, pkg.source.lastRun, pkg.dateCreated)
                            }
                        }
                        log.info("Got ${updateUrls}")
                        Iterator urlsIterator = updateUrls.listIterator(updateUrls.size())

                        File file
                        if (updateUrls.size() > 0) {
                            LocalTime kbartFromUrlStartTime = LocalTime.now()
                            while (urlsIterator.hasPrevious()) {
                                URL url = urlsIterator.previous()
                                lastUpdateURL = url.toString()
                                try {
                                    file = exportService.kbartFromUrl(lastUpdateURL)

                                    //if (kbartFromUrlStartTime < LocalTime.now().minus(45, ChronoUnit.MINUTES)){ sense???
                                    //break
                                    //}

                                }
                                catch (Exception e) {
                                    log.info("get kbartFromUrl: ${e}")
                                    continue
                                }

                            }

                            if (file) {
                                kbartRows = kbartProcess(file, lastUpdateURL, autoUpdatePackageInfo)
                            } else {
                                AutoUpdatePackageInfo.withTransaction {
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
                    }else {
                        AutoUpdatePackageInfo.withTransaction {
                            AutoUpdatePackageInfo autoUpdatePackageFail = new AutoUpdatePackageInfo()
                            autoUpdatePackageFail.description = "No url define in the source of the package."
                            autoUpdatePackageFail.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                            autoUpdatePackageFail.startTime = startTime
                            autoUpdatePackageFail.endTime = new Date()
                            autoUpdatePackageFail.pkg = pkg
                            autoUpdatePackageFail.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                            autoUpdatePackageFail.save()
                        }
                    }

                } catch (Exception exception) {
                    log.error("Error by startAutoPackageUapdate: ${exception.message}" + exception.printStackTrace())
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

        Date lastChangedInKbart = pkg.source.lastChangedInKbart
        List<LocalDate> lastChangedDates = []

        Platform plt = pkg.nominalPlatform
        IdentifierNamespace identifierNamespace
        List<IdentifierNamespace> idnsCheck = IdentifierNamespace.executeQuery('select so.targetNamespace from Package pkg join pkg.source so where pkg = :pkg', [pkg: pkg])
        if (!idnsCheck && plt)
            idnsCheck = IdentifierNamespace.executeQuery('select plat.titleNamespace from Platform plat where plat = :plat', [plat: plt])
        if (idnsCheck && idnsCheck.size() == 1)
            identifierNamespace = idnsCheck[0]

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

            AutoUpdatePackageInfo.withTransaction {
                if(!setAllTippsNotInKbartToDeleted){
                    autoUpdatePackageInfo.kbartHasWekbFields = true
                }

                if(lastChangedInKbart){
                    autoUpdatePackageInfo.lastChangedInKbart = lastChangedInKbart
                }
                autoUpdatePackageInfo.save()
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

                                        AutoUpdateTippInfo autoUpdateTippInfo = new AutoUpdateTippInfo(
                                                description: validation_result.errorMessage,
                                                tipp: null,
                                                startTime: new Date(),
                                                endTime: new Date(),
                                                status: RDStore.AUTO_UPDATE_STATUS_FAILED,
                                                type: RDStore.AUTO_UPDATE_TYPE_FAILED_TITLE,
                                                oldValue: '',
                                                newValue: '',
                                                tippProperty: '',
                                                autoUpdatePackageInfo: autoUpdatePackageInfo
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
                                        Map autoUpdateResultTipp = kbartImportService.tippImportForAutoUpdate(kbartRow, tippsWithCoverage, tippDuplicates, autoUpdatePackageInfo, kbartRowsToCreateTipps, identifierNamespace)

                                        kbartRowsToCreateTipps = autoUpdateResultTipp.kbartRowsToCreateTipps
                                        tippsWithCoverage = autoUpdateResultTipp.tippsWithCoverage
                                        tippDuplicates = autoUpdateResultTipp.tippDuplicates

                                        if (autoUpdateResultTipp.autoUpdatePackageInfo) {
                                            autoUpdatePackageInfo = autoUpdateResultTipp.autoUpdatePackageInfo
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
                                            updateTipp.save()
                                            tippsFound << updateTipp.id
                                        }

                                    }
                                    catch (grails.validation.ValidationException ve) {
                                        if (!invalidKbartRowsForTipps.contains(kbartRow.rowIndex)) {
                                            if (updateTipp) {
                                                invalidKbartRowsForTipps << kbartRow.rowIndex
                                                AutoUpdateTippInfo.withTransaction {
                                                    autoUpdatePackageInfo.refresh()
                                                    AutoUpdateTippInfo autoUpdateTippInfo = new AutoUpdateTippInfo(
                                                            description: "An error occurred while processing the title: ${kbartRow.publication_title}. Check kbart row of this title.",
                                                            tipp: updateTipp,
                                                            startTime: new Date(),
                                                            endTime: new Date(),
                                                            status: RDStore.AUTO_UPDATE_STATUS_FAILED,
                                                            type: RDStore.AUTO_UPDATE_TYPE_FAILED_TITLE,
                                                            oldValue: '',
                                                            newValue: '',
                                                            tippProperty: '',
                                                            autoUpdatePackageInfo: autoUpdatePackageInfo
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
                                                AutoUpdateTippInfo.withTransaction {
                                                    autoUpdatePackageInfo.refresh()
                                                    AutoUpdateTippInfo autoUpdateTippInfo = new AutoUpdateTippInfo(
                                                            description: "An error occurred while processing the title: ${kbartRow.publication_title}. Check kbart row of this title.",
                                                            tipp: updateTipp,
                                                            startTime: new Date(),
                                                            endTime: new Date(),
                                                            status: RDStore.AUTO_UPDATE_STATUS_FAILED,
                                                            type: RDStore.AUTO_UPDATE_TYPE_FAILED_TITLE,
                                                            oldValue: '',
                                                            newValue: '',
                                                            tippProperty: '',
                                                            autoUpdatePackageInfo: autoUpdatePackageInfo
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
                List newTippList = kbartImportService.createTippBatch(kbartRowsToCreateTipps, autoUpdatePackageInfo, identifierNamespace)
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
                            result = kbartImportService.updateTippWithKbart(result, tipp, newTippMap.kbartRowMap, newTippMap.autoUpdatePackageInfo, tippsWithCoverage, pkgTipp, platformTipp)
                            tippsWithCoverage = result.tippsWithCoverage
                            autoUpdatePackageInfo = result.autoUpdatePackageInfo

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

            if(tippDuplicates.size() > 0){
                log.info("remove tippDuplicates -> ${tippDuplicates.size()}: ${tippDuplicates}")

                tippDuplicates.each {
                    if(!(it in tippsFound)){
                        KBComponent.executeUpdate("update KBComponent set status = :removed where id = (:tippId)", [removed: RDStore.KBC_STATUS_REMOVED, tippId: it])

                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                        AutoUpdateTippInfo.withTransaction {
                            autoUpdatePackageInfo.refresh()
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
                                    kbartProperty: 'status',
                                    autoUpdatePackageInfo: autoUpdatePackageInfo
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


            if (setAllTippsNotInKbartToDeleted) {

                List<Long> tippsIds = setTippsNotToDeleted ? TitleInstancePackagePlatform.executeQuery("select tipp.id from TitleInstancePackagePlatform tipp where " +
                        "tipp.status in :status and " +
                        "tipp.pkg = :package and tipp.id not in (:setTippsNotToDeleted)",
                        [package: pkg, status: [status_current, status_expected, status_retired], setTippsNotToDeleted: setTippsNotToDeleted]) : []

                Integer tippsToDeleted = tippsIds ? KBComponent.executeUpdate("update KBComponent set status = :deleted where id in (:tippIds)", [deleted: status_deleted, tippIds: tippsIds]) : 0

                tippsIds.each {
                    TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                    AutoUpdateTippInfo.withTransaction {
                        autoUpdatePackageInfo.refresh()
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
                                kbartProperty: 'status',
                                autoUpdatePackageInfo: autoUpdatePackageInfo
                        ).save()
                        changedTipps++
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
                    "select count(tipp.id) from TitleInstancePackagePlatform tipp where " +
                            "tipp.status in :status and " +
                            "tipp.pkg = :package",
                    [package: pkg, status: listStatus])[0]


            if(tippsFound.size() > 0 && kbartRowsCount > 0 && countExistingTippsAfterImport > (kbartRowsCount-countInvalidKbartRowsForTipps)){

                List<Long> existingTippsAfterImport = TitleInstancePackagePlatform.executeQuery(
                        "select tipp.id from TitleInstancePackagePlatform tipp where " +
                                "tipp.status in :status and " +
                                "tipp.pkg = :package",
                        [package: pkg, status: listStatus])


                List<Long> deleteTippsFromWekb = existingTippsAfterImport - tippsFound

                if(deleteTippsFromWekb.size() > 0){
                    Integer tippsToDeleted = KBComponent.executeUpdate("update KBComponent set status = :deleted where id in (:tippIds)", [deleted: RDStore.KBC_STATUS_DELETED, tippIds: deleteTippsFromWekb])

                    deleteTippsFromWekb.each {
                        TitleInstancePackagePlatform tipp = TitleInstancePackagePlatform.get(it)
                        AutoUpdateTippInfo.withTransaction {
                            autoUpdatePackageInfo.refresh()
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
                                    kbartProperty: 'status',
                                    autoUpdatePackageInfo: autoUpdatePackageInfo
                            ).save()
                            changedTipps++
                        }
                    }

                    log.info("Rows in KBART is not same with titles in wekb. RemoveTippsFromWekb: ${deleteTippsFromWekb.size()}, Set tipps to removed: ${tippsToDeleted}")
                }

            }

            String description = "Package Update: (KbartLines: ${kbartRowsCount}, " +
                    "Processed Titles in this run: ${idx}, Titles in we:kb previously: ${previouslyTipps}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps}, Changed Titles in we:kb: ${changedTipps})"

            AutoUpdatePackageInfo.executeUpdate("update AutoUpdatePackageInfo set countKbartRows = ${kbartRowsCount}, " +
                    "countChangedTipps = ${changedTipps}, " +
                    "countNowTippsInWekb = ${countExistingTippsAfterImport}, " +
                    "countPreviouslyTippsInWekb = ${previouslyTipps}, " +
                    "countNewTipps = ${newTipps}, " +
                    "countRemovedTipps = ${removedTipps}, " +
                    "countInValidTipps = ${countInvalidKbartRowsForTipps}, " +
                    "countProcessedKbartRows = ${idx}, " +
                    "endTime = ${new Date()}, " +
                    "description = ${description} " +
                    "where id = ${autoUpdatePackageInfo.id}")

            AutoUpdatePackageInfo.withTransaction {

                Package aPackage = Package.get(autoUpdatePackageInfo.pkg.id)
                if (aPackage.status != status_deleted) {
                    aPackage.lastUpdated = new Date()
                    aPackage.lastUpdateComment = "Updated package with ${kbartRowsCount} Title. (Titles in we:kb previously: ${previouslyTipps}, Titles in we:kb now: ${countExistingTippsAfterImport}, Removed Titles: ${removedTipps}, New Titles in we:kb: ${newTipps})"
                    aPackage.save()
                }

                if (aPackage.source) {
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
            AutoUpdatePackageInfo.withTransaction {
                autoUpdatePackageInfo.refresh()
                autoUpdatePackageInfo.endTime = new Date()
                autoUpdatePackageInfo.description = "An error occurred while processing the kbart file. More information can be seen in the system log. File from URL: ${lastUpdateURL}"
                autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                autoUpdatePackageInfo.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                autoUpdatePackageInfo.save()
            }
        }

        /*if(errors.global.size() > 0 || errors.tipps.size() > 0){
            log.error("Error map by kbartImportProcess: ")
        }*/
        log.info("End kbartImportProcess Package ($pkg.name)")
        return autoUpdatePackageInfo
    }

   List kbartProcess(File tsvFile, String lastUpdateURL, AutoUpdatePackageInfo autoUpdatePackageInfo) {
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
            AutoUpdatePackageInfo.withTransaction {
                autoUpdatePackageInfo.description = "Encoding of kbart file is wrong. File encoding was: ${encoding}. File from URL: ${lastUpdateURL}"
                autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                autoUpdatePackageInfo.endTime = new Date()
                autoUpdatePackageInfo.save()
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
                            AutoUpdatePackageInfo.withTransaction {
                                autoUpdatePackageInfo.description = "KBART file does not have one or any of the headers: ${minimumKbartStandard.join(', ')}. File from URL: ${lastUpdateURL}"
                                autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                                autoUpdatePackageInfo.endTime = new Date()
                                autoUpdatePackageInfo.save()
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
                        AutoUpdatePackageInfo.withTransaction {
                            autoUpdatePackageInfo.description = "Separator for the kbart was not recognized. The following separators are recognized: Tab, comma, semicolons. File from URL: ${lastUpdateURL}"
                            autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                            autoUpdatePackageInfo.endTime = new Date()
                            autoUpdatePackageInfo.save()
                        }
                    }
                }else {
                    log.error("KBART file is empty:  ${lastUpdateURL}")
                    AutoUpdatePackageInfo.withTransaction {
                        autoUpdatePackageInfo.description = "KBART file is empty. File from URL: ${lastUpdateURL}"
                        autoUpdatePackageInfo.status = RDStore.AUTO_UPDATE_STATUS_FAILED
                        autoUpdatePackageInfo.endTime = new Date()
                        autoUpdatePackageInfo.save()
                    }
                }
            } catch (Exception e) {
                log.error("Error by KbartProcess: ${e}")
                AutoUpdatePackageInfo.withTransaction {
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
