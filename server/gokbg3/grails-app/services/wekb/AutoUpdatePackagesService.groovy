package wekb


import de.hbznrw.ygor.tools.UrlToolkit
import de.wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import org.apache.commons.lang.StringUtils
import org.gokb.cred.JobResult
import org.gokb.cred.Package

import java.time.LocalTime
import java.util.concurrent.ExecutorService

import groovyx.gpars.GParsPool
import java.util.concurrent.Future

@Transactional
class AutoUpdatePackagesService {

    static final THREAD_POOL_SIZE = 5
    public static boolean running = false;
    Map result = [result: JobResult.STATUS_SUCCESS]
    ExportService exportService
    ExecutorService executorService
    Future activeFuture

    KbartProcessService kbartProcessService

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
                UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, endTime: new Date(), status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Package status is ${pkg.status.value}. Update for this package is not starting.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true)
                updatePackageInfo.save()
            } else {
                UpdatePackageInfo updatePackageInfo = new UpdatePackageInfo(pkg: pkg, startTime: startTime, status: RDStore.UPDATE_STATUS_SUCCESSFUL, description: "Starting Update package.", onlyRowsWithLastChanged: onlyRowsWithLastChanged, automaticUpdate: true)
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
                                kbartRows = kbartProcessService.kbartProcess(file, lastUpdateURL, updatePackageInfo)
                            } else {
                                UpdatePackageInfo.withTransaction {
                                    updatePackageInfo.description = "No KBART File found by URL: ${lastUpdateURL}!"
                                    updatePackageInfo.status = RDStore.UPDATE_STATUS_FAILED
                                    updatePackageInfo.endTime = new Date()
                                    updatePackageInfo.save()
                                }
                            }

                        }

                        if (kbartRows.size() > 0) {
                            updatePackageInfo = kbartProcessService.kbartImportProcess(kbartRows, pkg, lastUpdateURL, updatePackageInfo, onlyRowsWithLastChanged)
                        }
                    }else {
                        UpdatePackageInfo.withTransaction {
                            UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                            updatePackageFail.description = "No url define in the source of the package."
                            updatePackageFail.status = RDStore.UPDATE_STATUS_FAILED
                            updatePackageFail.startTime = startTime
                            updatePackageFail.endTime = new Date()
                            updatePackageFail.pkg = pkg
                            updatePackageFail.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                            updatePackageFail.automaticUpdate = true
                            updatePackageFail.save()
                        }
                    }

                } catch (Exception exception) {
                    log.error("Error by startAutoPackageUapdate: ${exception.message}" + exception.printStackTrace())
                    UpdatePackageInfo.withTransaction {
                        UpdatePackageInfo updatePackageFail = new UpdatePackageInfo()
                        updatePackageFail.description = "An error occurred while processing the kbart file. More information can be seen in the system log. File from URL: ${lastUpdateURL}"
                        updatePackageFail.status = RDStore.UPDATE_STATUS_FAILED
                        updatePackageFail.startTime = startTime
                        updatePackageFail.endTime = new Date()
                        updatePackageFail.pkg = pkg
                        updatePackageFail.onlyRowsWithLastChanged = onlyRowsWithLastChanged
                        updatePackageFail.automaticUpdate = true
                        updatePackageFail.save()
                    }
                }
            }
        log.info("End startAutoPackageUpdate Package ($pkg.name)")
    }



}
