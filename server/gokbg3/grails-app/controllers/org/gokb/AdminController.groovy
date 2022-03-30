package org.gokb

import com.k_int.ConcurrencyManagerService
import com.k_int.ConcurrencyManagerService.Job
import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import gokbg3.DateFormatService
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.core.CountRequest
import org.elasticsearch.client.core.CountResponse
import org.elasticsearch.client.indices.GetIndexRequest

import org.gokb.cred.*
import org.hibernate.criterion.CriteriaSpecification

import org.springframework.security.access.annotation.Secured
import wekb.AdminService
import wekb.AutoUpdatePackagesService

import java.util.concurrent.CancellationException

@Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
class AdminController {

  def packageService
  def componentStatisticService

  ConcurrencyManagerService concurrencyManagerService
  CleanupService cleanupService
  AdminService adminService
  AutoUpdatePackagesService autoUpdatePackagesService
  def ESWrapperService
  SpringSecurityService springSecurityService

  static Map typePerIndex = [
          "wekbtipps": "TitleInstancePackagePlatform",
          "wekborgs": "Org" ,
          "wekbpackages": "Package",
          "wekbplatforms": "Platform",
          "wekbdeletedcomponents": "DeletedKBComponent"
  ]

  def ensureUuids() {

    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.ensureUuids(j)
    }.startOrQueue()

    j.description = "Ensure UUIDs for components"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnsureUUIDs')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');

  }


  def reviewDatesOfTippCoverage() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.reviewDatesOfTippCoverage(j)
    }.startOrQueue()

    j.description = "Mark insonsistent date ranges"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'MarkInconsDateRanges')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def updateTextIndexes() {
    log.debug("Call to update indexe");

    Job j = concurrencyManagerService.createJob {
      FTUpdateService.updateFTIndexes();
    }.startOrQueue()

    j.description = "Update Free Text Indexes"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'UpdateFreeTextIndexes')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def resetTextIndexes() {
    log.debug("Call to update indexe")
    Job j = concurrencyManagerService.createJob {
      FTUpdateService.clearDownAndInitES()
    }.startOrQueue()

    j.description = "Reset Free Text Indexes"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'ResetFreeTextIndexes')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def addPackageTypes() {
    Job j = concurrencyManagerService.createJob { Job j ->
      log.debug("Generating missing package content types ..")
      packageService.generatePackageTypes(j)
    }.startOrQueue()

    j.description = "Generate Package Types"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'GeneratePackageTypes')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def jobs() {
    log.debug("Jobs");
    def result = [:]
    log.debug("Sort");
    result.jobs = concurrencyManagerService.jobs.sort { a, b -> b.value.startTime <=> a.value.startTime }
    log.debug("concurrency manager service");
    result.cms = concurrencyManagerService

    result.jobs.each { k, j ->
      if (j.isDone() && !j.endTime) {

        try {
          def job_res = j.get()

          if (job_res && job_res instanceof Date) {
            j.endTime = j.get()
          }
        }
        catch (CancellationException e) {
          log.debug("Cancelled")
        }
        catch (Exception e) {
          log.debug("${e}")

          if (j.messages?.size() == 0) {
            j.message("There has been an exception processing this job! Please check the logs!")
          }
        }
      }
    }

    log.debug("Render");
    if (request.format == 'JSON') {
      log.debug("JSON Render");
      render result as JSON
    }

    log.debug("Return");
    result
  }

  def cleanJobList() {
    log.debug("clean job list..")
    def jobs = concurrencyManagerService.jobs
    def maxId = jobs.max { it.key }.key

    jobs.each { k, j ->
      if (j.isDone()) {
        jobs.remove(k)
      }
    }
    redirect(url: request.getHeader('referer'))
  }

  def cancelJob() {
    Job j = concurrencyManagerService.getJob(params.id)

    j?.forceCancel()
    redirect(controller: 'admin', action: 'jobs');
  }


  def cleanup() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.expungeDeletedComponents(j)
    }.startOrQueue()

    log.debug "Triggering cleanup task. Started job #${j.uuid}"

    j.description = "Cleanup Deleted Components"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'CleanupDeletedComponents')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def cleanupRejected() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.expungeRejectedComponents(j)
    }.startOrQueue()

    log.debug "Triggering cleanup task. Started job #${j.uuid}"

    j.description = "Cleanup Rejected Components"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'CleanupRejectedComponents')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def cleanupPlatforms() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.deleteNoUrlPlatforms(j)
    }.startOrQueue()

    log.debug("Triggering cleanup task. Started job #${j.uuid}")

    j.description = "Platform Cleanup"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'PlatformCleanup')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def recalculateStats() {
    Job j = concurrencyManagerService.createJob {
      componentStatisticService.updateCompStats(12, 0, true)
    }.startOrQueue()

    log.debug "Triggering statistics rewrite, job #${j.uuid}"
    j.description = "Recalculate Statistics"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RecalculateStatistics')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  @Secured(['ROLE_SUPERUSER'])
  def setupAcl() {

    adminService.setupDefaultAcl()

    redirect(controller: 'admin', action: 'jobs');
  }

  @Secured(['ROLE_SUPERUSER'])
  def autoUpdatePackages() {
      log.debug("Beginning scheduled auto update packages job.")
    Job j = concurrencyManagerService.createJob {
      autoUpdatePackagesService.findPackageToUpdateAndUpdate()
    }.startOrQueue()

    j.description = "Start Manuel Auto Update Packages"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'AutoUpdatePackagesJob')
    j.startTime = new Date()

    log.info("auto update packages job completed.")

    redirect(controller: 'admin', action: 'jobs')
  }

  @Secured(['ROLE_SUPERUSER'])
  def manageFTControl() {
    Map<String, Object> result = [:]
    log.debug("manageFTControl ...")
    result.ftControls = FTControl.list()
    result.ftUpdateService = [:]
    result.editable = true

    RefdataValue status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

    /*Client esclient = ESWrapperService.getClient()

    result.indices = []
    def esIndices = grailsApplication.config.gokb.es.indices?.values()

    esIndices.each{ String indexName ->
      Map indexInfo = [:]
      indexInfo.name = indexName
      indexInfo.type = typePerIndex.get(indexName)

      SearchResponse response = esclient.prepareSearch(indexName)
              .setSize(0)
              .execute().actionGet()

      SearchHits hits = response.getHits()
      indexInfo.countIndex = hits.getTotalHits()

      String query = "select count(id) from ${typePerIndex.get(indexName)}"
      indexInfo.countDB = FTControl.executeQuery(query)[0]
      indexInfo.countDeletedInDB = FTControl.executeQuery(query+ " where status = :status", [status: status_deleted]) ? FTControl.executeQuery(query+ " where status = :status", [status: status_deleted])[0] : 0
      result.indices << indexInfo
    }*/

    RestHighLevelClient esclient = ESWrapperService.getClient()

    result.indices = []
    def esIndices = ESWrapperService.es_indices
    esIndices.each{ def indice ->
      Map indexInfo = [:]
      indexInfo.name = indice.value
      indexInfo.type = indice.key

      GetIndexRequest request = new GetIndexRequest(indice.value)

      if (esclient.indices().exists(request, RequestOptions.DEFAULT)) {
        CountRequest countRequest = new CountRequest(indice.value)
        CountResponse countResponse = esclient.count(countRequest, RequestOptions.DEFAULT)
        indexInfo.countIndex = countResponse ? countResponse.getCount().toInteger() : 0
      }else {
        indexInfo.countIndex = ""
      }

      String query = "select count(id) from ${typePerIndex.get(indice.value)}"
      indexInfo.countDB = FTControl.executeQuery(query)[0]
      indexInfo.countDeletedInDB = FTControl.executeQuery(query+ " where status = :status", [status: status_deleted]) ? FTControl.executeQuery(query+ " where status = :status", [status: status_deleted])[0] : 0
      result.indices << indexInfo
    }

    try {
      esclient.close()
    }
    catch (Exception e) {
      log.error("Problem by Close ES Client", e)
    }

    result
  }


  @Secured(['ROLE_SUPERUSER'])
  def deleteIndex() {
    String indexName = params.name
    List deletedKBComponentList = []
    if (indexName) {
      ESWrapperService.deleteIndex(indexName)
      ESWrapperService.createIndex(indexName)
     /* if (typePerIndex.get(indexName) == DeletedKBComponent.class.simpleName) {

        DeletedKBComponent.getAll().each { DeletedKBComponent deletedKBComponent ->
          Map idx_record = [:]
          idx_record.recid = "${deletedKBComponent.class.name}:${deletedKBComponent.id}"
          idx_record.uuid = deletedKBComponent.uuid
          idx_record.name = deletedKBComponent.name
          idx_record.componentType = deletedKBComponent.componentType
          idx_record.status = deletedKBComponent.status.value
          idx_record.dateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.dateCreated)
          idx_record.lastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.lastUpdated)
          idx_record.oldDateCreated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldDateCreated)
          idx_record.oldLastUpdated = dateFormatService.formatIsoTimestamp(deletedKBComponent.oldLastUpdated)
          idx_record.oldId = deletedKBComponent.oldId

          deletedKBComponentList << idx_record
        }

      }

      Job j = concurrencyManagerService.createJob {

        log.info("deleteIndex ${indexName} ...")
        Client esclient = ESWrapperService.getClient()
        IndicesAdminClient adminClient = esclient.admin().indices()

        if (adminClient.prepareExists(indexName).execute().actionGet().isExists()) {
          DeleteIndexRequestBuilder deleteIndexRequestBuilder = adminClient.prepareDelete(indexName)
          DeleteIndexResponse deleteIndexResponse = deleteIndexRequestBuilder.execute().actionGet()
          if (deleteIndexResponse.isAcknowledged()) {
            log.info("Index ${indexName} successfully deleted!")
          } else {
            log.info("Index deletetion failed: ${deleteIndexResponse}")
          }
        }
        log.info("ES index ${indexName} did not exist, creating..")
        CreateIndexRequestBuilder createIndexRequestBuilder = adminClient.prepareCreate(indexName)
        log.info("Adding index settings..")
        createIndexRequestBuilder.setSettings(ESWrapperService.getSettings().get("settings"))
        log.info("Adding index mappings..")
        createIndexRequestBuilder.addMapping("component", ESWrapperService.getMapping())

        CreateIndexResponse createIndexResponse = createIndexRequestBuilder.execute().actionGet()
        if (createIndexResponse.isAcknowledged()) {
          log.info("Index ${indexName} successfully created!")
          if (typePerIndex.get(indexName) == DeletedKBComponent.class.simpleName) {
            deletedKBComponentList.each {
              String recid = it.recid
              it.remove('recid')
              IndexResponse indexResponse = esclient.prepareIndex("wekbdeletedcomponents", 'component', recid).setSource(it).get()
              if (indexResponse.getResult() != DocWriteResponse.Result.CREATED) {
                log.error("Error on record DeletedKBComponent in Index 'wekbdeletedcomponents'")
              }

            }

          } else {
            FTControl.withTransaction {
              def res = FTControl.executeUpdate("delete FTControl c where c.domainClassName = :deleteFT", [deleteFT: "org.gokb.cred.${typePerIndex.get(indexName)}"])
              log.info("Result: ${res}")
            }
            FTUpdateService.updateFTIndexes()
          }
        } else {
          log.info("Index creation failed: ${createIndexResponse}")
        }
      }.startOrQueue()
      j.description = "Delete index ${params.name}"
      j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'ResetFreeTextIndexes')
      j.startTime = new Date()*/
    }

    redirect(action: 'manageFTControl')
  }


  def packagesChanges() {
    log.debug("packageChanges::${params}")
    def result = [:]

    User user = springSecurityService.getCurrentUser()

    String query = 'from Package as pkg where pkg.status != :status'

    RefdataValue status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

    params.offset = params.offset ?: 0
    params.max = params.max ? Integer.parseInt(params.max) : user.defaultPageSizeAsInteger

    result.packagesCount = Package.executeQuery('select count(pkg) ' + query, [status: status_deleted])[0]
    result.packages = Package.executeQuery('select pkg ' + query + " order by pkg.lastUpdated desc", [status: status_deleted], params)

    result
  }


  def frontend() {
    log.debug("frontend::${params}")
    def result = [:]
    result
  }

  def findTippDuplicatesByPkg() {
    log.debug("findTippDuplicates::${params}")
    def result = [:]

    Package aPackage = Package.findByUuid(params.id)

    List<TitleInstancePackagePlatform> tippsDuplicatesByName = aPackage.findTippDuplicatesByName()
    List<TitleInstancePackagePlatform> tippsDuplicatesByUrl = aPackage.findTippDuplicatesByURL()

    result.tippsDuplicatesByName = tippsDuplicatesByName
    result.tippsDuplicatesByUrl = tippsDuplicatesByUrl

    //println(result)

    result
  }

  def findPackagesWithTippDuplicates() {
    log.debug("findPackagesWithTippDuplicates::${params}")
    def result = [:]

    List pkgs = []

    Package.findAllByStatus(RDStore.KBC_STATUS_CURRENT, [sort: 'name']).each {Package aPackage ->
      Integer tippDuplicatesByNameCount = aPackage.getTippDuplicatesByNameCount()
      Integer tippDuplicatesByUrlCount = aPackage.getTippDuplicatesByURLCount()

      if(tippDuplicatesByNameCount > 0 || tippDuplicatesByUrlCount > 0){
        pkgs << [pkg: aPackage, tippDuplicatesByNameCount: tippDuplicatesByNameCount, tippDuplicatesByUrlCount: tippDuplicatesByUrlCount]
      }
    }

    if (params.sort == 'tippDuplicatesByNameCount') {
      result.pkgs = pkgs.sort {
        it.tippDuplicatesByNameCount
      }
    } else if (params.sort == 'tippDuplicatesByUrlCount') {
      result.pkgs = pkgs.sort {
        it.tippDuplicatesByUrlCount
      }
    } else {
      result.pkgs = pkgs
    }
    result
  }
}
