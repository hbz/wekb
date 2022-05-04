package wekb

import com.k_int.ConcurrencyManagerService
import de.hbznrw.ygor.tools.UrlToolkit
import de.wekb.helper.RCConstants
import grails.converters.JSON
import groovyx.net.http.RESTClient
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils

import org.gokb.CrossReferenceService

import org.gokb.cred.JobResult
import org.gokb.cred.Package
import org.gokb.cred.RefdataCategory
import org.gokb.cred.Source
import org.gokb.cred.UpdateToken
import org.gokb.cred.User

import java.time.LocalTime
import java.time.temporal.ChronoUnit

import static groovyx.net.http.Method.GET

import groovyx.gpars.GParsPool


class AutoUpdatePackagesService {

    static final THREAD_POOL_SIZE = 5
    public static boolean running = false;
    def grailsApplication
    ConcurrencyManagerService concurrencyManagerService
    Map result = [result: JobResult.STATUS_SUCCESS]
    CrossReferenceService crossReferenceService

    public static Date runningStartDate

    Map findPackageToUpdateAndUpdate() {
        List ygorDataList = []
        def updPacks = Package.executeQuery(
                "from Package p " +
                        "where p.source is not null and " +
                        "p.source.automaticUpdates = true " +
                        "and (p.source.lastRun is null or p.source.lastRun < current_date)")
        updPacks.each { Package p ->
            if (p.source.needsUpdate()) {
                def result = updateFromSource(p)
                if(result.ygorData){
                    ygorDataList << result.ygorData
                }
                sleep(10000)
            }
        }
        log.debug("findPackageToUpdateAndUpdate: Package with Source and lastRun < currentDate (${updPacks.size()})")
        if(ygorDataList.size() > 0){
            log.debug("findPackageToUpdateAndUpdate: updPacks: ${updPacks.size()}, ygorDataList: ${ygorDataList.size()}")

            GParsPool.withPool(THREAD_POOL_SIZE) { pool ->
                ygorDataList.eachWithIndexParallel { Map ygorData ->
                    importJsonFromUpdateSource(ygorData)
                }
            }
        }

    }

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
    }

    /**
     * this method calls Ygor to perform an automated Update on this package.
     * Bad configurations will result in failure.
     * The autoUpdate frequency in the source is ignored: the update starts immediately.
     */
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
                                        /*task {
                                            checkPackageJob(respData.jobId, p)
                                        }*/
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
    }


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

    @Deprecated
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
    }

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

}
