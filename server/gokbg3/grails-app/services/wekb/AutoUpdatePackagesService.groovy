package wekb

import com.k_int.ConcurrencyManagerService
import de.hbznrw.ygor.tools.UrlToolkit
import de.wekb.helper.RCConstants
import grails.converters.JSON
import groovyx.net.http.RESTClient
import org.apache.commons.lang.RandomStringUtils
import org.apache.commons.lang.StringUtils
import org.apache.xmlbeans.impl.store.Cur
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.JobResult
import org.gokb.cred.Package
import org.gokb.cred.RefdataCategory
import org.gokb.cred.Source
import org.gokb.cred.UpdateToken
import org.gokb.cred.User

import static grails.async.Promises.task
import static groovyx.net.http.Method.GET


class AutoUpdatePackagesService {

    public static boolean running = false;
    def grailsApplication
    ConcurrencyManagerService concurrencyManagerService
    Map result = [result: JobResult.STATUS_SUCCESS]

    public static Date runningStartDate

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
                if (startSourceUpdate(p, user, ignoreLastChanged)) {
                    result = [result: JobResult.STATUS_SUCCESS, message: (user ? "Manuell" : "Auto") + " Update Package success"]
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

        CuratoryGroup curatoryGroup
        if (p.curatoryGroups && p.curatoryGroups.size() > 0) {

            if(user) {
                List curatory_group_ids = user.curatoryGroups.id?.intersect(p.curatoryGroups?.id)
                if (curatory_group_ids.size() == 1) {
                    curatoryGroup = curatory_group_ids[0]
                } else if (curatory_group_ids.size() > 1) {
                    log.debug("Got more than one cg candidate!")
                    curatoryGroup = curatory_group_ids[0]
                }
            }else {
                curatoryGroup = p.curatoryGroups[0]
            }
        }

        def result_object = JobResult.findByUuid(uuid)
        if (!result_object) {
            def job_map = [
                    uuid        : uuid,
                    description : (user ? "Manuell" : "Auto") + " Update Packages Job (${p.name})",
                    resultObject: (result as JSON).toString(),
                    type        : user ? RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'ManuellUpdatePackageJob') : RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'AutoUpdatePackagesJob'),
                    statusText  : result.result,
                    startTime   : startTime,
                    endTime     : new Date(),
                    linkedItemId: p.id,
                    groupId: curatoryGroup
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
    private boolean startSourceUpdate(Package p, User user = null, boolean ignoreLastChanged = false) {
        log.debug("Source update start..")
        //println("Source update start..")
        boolean error = false
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
                            result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: ${respData.message}"]]]
                            processing = false
                            error = true
                        }
                        def statusService = new RESTClient(ygorBaseUrl + "/enrichment/getStatus?jobId=${respData.jobId}")

                        while (processing == true) {
                            log.debug("GET ygor/enrichment/getStatus?jobId=${respData.jobId}")
                            statusService.request(GET) { req ->
                                response.success = { statusResp, statusData ->
                                    log.debug("GET ygor/enrichment/getStatus?jobId=${respData.jobId} => success")
                                    log.debug("status of Ygor ${statusData.status} gokbJob #${statusData.gokbJobId}")
                                    if (statusData.status == 'FINISHED_UNDEFINED') {
                                        processing = false
                                        result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: No valid URLs found."]]]
                                        log.debug("No valid URLs found.")
                                    }

                                    if (statusData.gokbJobId) {
                                        processing = false
                                        task {
                                            checkPackageJob(statusData.gokbJobId, p)
                                        }
                                    } else {
                                        sleep(10000) // 10 sec
                                    }
                                }
                                response.failure = { statusResp, statusData ->
                                    log.error("GET ygor/enrichment/getStatus?jobId=${respData.jobId} => failure")
                                    log.error("ygor response message: $statusData.message")

                                    result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: response message: $statusData.message"]]]
                                    processing = false
                                    error = true
                                }
                            }
                        }
                    }
                    response.failure = { resp ->
                        log.error("GET ygor${path} => failure")
                        log.error("ygor response: ${resp.responseBase}")

                        result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "YGOR ERROR: response: ${resp.responseBase}"]]]
                        error = true
                    }
                }
            } catch (Exception e) {
                log.error("SourceUpdate Exception:", e);
                result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "wekb ERROR: SourceUpdate Exception"]]]
                error = true
            }
        } else {
            result = [result: JobResult.STATUS_ERROR, errors: [global: [message: "wekb ERROR: No user provided and no existing updateToken found!"]]]
            log.debug("No user provided and no existing updateToken found!")
            error = true
        }

        return !error
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
                        println(url)
                    }
                }
                aPackage.source.lastRun = new Date()
                aPackage.source.save()

                log.debug("set ${aPackage.source.getNormname()}.lastRun = now")
            }
        }
    }

}
