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
