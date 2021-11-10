package org.gokb

import com.k_int.ConcurrencyManagerService
import com.k_int.ConcurrencyManagerService.Job
import de.wekb.helper.RCConstants
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

  def uploadAnalysisService
  def FTUpdateService
  def packageService
  def componentStatisticService
  def grailsCacheAdminService
  def titleAugmentService
  ConcurrencyManagerService concurrencyManagerService
  CleanupService cleanupService
  AdminService adminService
  AutoUpdatePackagesService autoUpdatePackagesService
  def ESWrapperService
  DateFormatService dateFormatService
  SpringSecurityService springSecurityService

  static Map typePerIndex = [
          "wekbtipps": "TitleInstancePackagePlatform",
          "wekborgs": "Org" ,
          "wekbpackages": "Package",
          "wekbplatforms": "Platform",
          "wekbdeletedcomponents": "DeletedKBComponent"
  ]

  @Deprecated
  def tidyOrgData() {

    Job j = concurrencyManagerService.createJob {

      // Cleanup our problem orgs.
      cleanupService.tidyMissnamedPublishers()


      def result = [:]

      def publisher_combo_type = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Publisher');

      result.nonMasterOrgs = Org.executeQuery('''
      select org
      from org.gokb.cred.Org as org
          join org.tags as tag
      where tag.owner.desc = 'Org.Authorized'
        and tag.value = 'N'
      ''');

      result.nonMasterOrgs.each { nmo ->

        if (nmo.parent != null) {

          nmo.parent.variantNames.add(new KBComponentVariantName(variantName: nmo.name, owner: nmo.parent))
          nmo.parent.save();

          log.debug("${nmo.id} ${nmo.parent?.id}")
          def combosToDelete = []
          nmo.incomingCombos.each { ic ->
            combosToDelete.add(ic); //ic.delete(flush:true)

            if (ic.type == publisher_combo_type) {
              log.debug("Got a publisher combo");
              if (nmo.parent != null) {
                def new_pub_combo = new Combo(fromComponent: ic.fromComponent, toComponent: nmo.parent, type: ic.type, status: ic.status).save();
              } else {
                def authorized_rdv = RefdataCategory.lookupOrCreate('Org.Authorized', 'Y')
                log.debug("No parent set.. try and find an authorised org with the appropriate name(${ic.toComponent.name})");
                def authorized_orgs = Org.executeQuery("select distinct o from Org o join o.variantNames as vn where ( o.name = ? or vn.variantName = ?) AND ? in elements(o.tags)", [ic.toComponent.name, ic.toComponent.name, authorized_rdv]);
                if (authorized_orgs.size() == 1) {
                  def ao = authorized_orgs.get(0)
                  log.debug("Create new publisher link to ${ao}");
                  def new_pub_combo = new Combo(fromComponent: ic.fromComponent, toComponent: ao, type: ic.type, status: ic.status).save();
                }
              }
            }
          }
          nmo.outgoingCombos.each { oc ->
            combosToDelete.add(oc); //ic.delete(flush:true)
            // oc.delete(flush:true)
          }

          nmo.incomingCombos.clear();
          nmo.outgoingCombos.clear();

          combosToDelete.each { cd ->
            cd.delete(flush: true)
          }

          nmo.delete(flush: true)
        }
      }
    }.startOrQueue()

    j.description = "Tidy Orgs Data"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TidyOrgsData')

    redirect(controller: 'admin', action: 'jobs');
  }

  def reSummariseLicenses() {

    Job j = concurrencyManagerService.createJob {
      DataFile.executeQuery("select d from DataFile as d where d.doctype=?", ['http://www.editeur.org/onix-pl:PublicationsLicenseExpression']).each { df ->
        log.debug(df);
        df.incomingCombos.each { ic ->
          log.debug(ic);
          if (ic.fromComponent instanceof License) {
            def source_file
            try {
              log.debug("Regenerate license for ${ic.fromComponent.id}");
              if (df.fileData) {
                source_file = copyUploadedFile(df.fileData, df.guid)
                ic.fromComponent.summaryStatement = uploadAnalysisService.generateSummary(source_file);
                ic.fromComponent.save(flush: true);
                log.debug("Completed regeneration... size is ${ic.fromComponent.summaryStatement?.length()}");
              } else {
                log.error("No file data attached to DataFile ${df.guid}")
              }
            }
            catch (Exception e) {
              log.error("Problem", e);
            } finally {
              source_file?.delete()
            }
          }
        }
      }
    }.startOrQueue()

    j.description = "Regenerate License Summaries"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RegenerateLicenseSummaries')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def ensureUuids() {

    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.ensureUuids(j)
    }.startOrQueue()

    j.description = "Ensure UUIDs for components"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnsureUUIDs')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');

  }

  def ensureTipls() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.ensureTipls(j)
    }.startOrQueue()

    j.description = "Ensure TIPLs for all TIPPs"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnsureTIPLs')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }


  def markInconsistentDates() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.reviewDates(j)
    }.startOrQueue()

    j.description = "Mark insonsistent date ranges"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'MarkInconsDateRanges')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def copyUploadedFile(inputfile, deposit_token) {

    def baseUploadDir = grailsApplication.config.baseUploadDir ?: '.'

    log.debug("copyUploadedFile...");
    def sub1 = deposit_token.substring(0, 2);
    def sub2 = deposit_token.substring(2, 4);
    validateUploadDir("${baseUploadDir}");
    validateUploadDir("${baseUploadDir}/${sub1}");
    validateUploadDir("${baseUploadDir}/${sub1}/${sub2}");
    def temp_file_name = "${baseUploadDir}/${sub1}/${sub2}/${deposit_token}";
    def temp_file = new File(temp_file_name)

    OutputStream outStream = null;
    ByteArrayOutputStream byteOutStream = null;
    try {
      outStream = new FileOutputStream(temp_file);
      byteOutStream = new ByteArrayOutputStream();
      // writing bytes in to byte output stream
      byteOutStream.write(inputfile); //data
      byteOutStream.writeTo(outStream);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      outStream.close();
    }
    log.debug("Created temp_file ${temp_file.size()}")

    temp_file
  }

  private def validateUploadDir(path) {
    File f = new File(path);
    if (!f.exists()) {
      log.debug("Creating upload directory path")
      f.mkdirs();
    }
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

  def masterListUpdate() {
    log.debug("Force master list update")
    Job j = concurrencyManagerService.createJob {
      packageService.updateAllMasters(true)
    }.startOrQueue()

    j.description = "Master List Update"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'MasterListUpdate')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def clearBlockCache() {
    // clear the cache used by the blocks tag…
    log.debug("Clearing block cache .. ")
    grailsCacheAdminService.clearBlocksCache()

    forward(controller: 'home', action: 'index', params: [reset: true])
  }

  def triggerEnrichments() {
    Job j = concurrencyManagerService.createJob {
      log.debug("manually trigger enrichment service");
      titleAugmentService.doEnrichment();
    }.startOrQueue()

    j.description = "Enrichment Service"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'EnrichmentService')
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

  def housekeeping() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.housekeeping(j)
    }.startOrQueue()

    j.description = "Housekeeping"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'Housekeeping')
    j.startTime = new Date()

    log.debug "Triggering housekeeping task. Started job #${j.uuid}"

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


  def cleanupOrphanedTipps() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.deleteOrphanedTipps(j)
    }.startOrQueue()

    log.debug("Triggering cleanup orphaned TIPPs task. Started job #${j.uuid}")

    j.description = "TIPP Cleanup"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'TIPPCleanup')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }


  def cleanupOrphanedIdentifiers() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.deleteOrphanedIdentifiers(j)
    }.startOrQueue()

    log.debug("Triggering cleanup orphaned Identifiers task. Started job #${j.uuid}")

    j.description = "Identifier Cleanup"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'IdentifierCleanup')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }


  def rejectWrongTitles() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.rejectWrongTitles(j)
    }.startOrQueue()

    log.debug("Reject wrong titles. Started job #${j.uuid}")

    j.description = "Set status of TitleInstances without package+history to 'Deleted'"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'DeleteTIWithoutHistory')
    j.startTime = new Date()

    redirect(controller: 'admin', action: 'jobs');
  }

  def rejectNoIdTitles() {
    Job j = concurrencyManagerService.createJob { Job j ->
      cleanupService.rejectNoIdTitles(j)
    }.startOrQueue()

    log.debug("Reject wrong titles. Started job #${j.uuid}")

    j.description = "Set status of TitleInstances without identifiers+tipps to 'Rejected'"
    j.type = RefdataCategory.lookupOrCreate(RCConstants.JOB_TYPE, 'RejectTIWithoutIdentifier')
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

  def exportGroups() {
    def result = [:]
    CuratoryGroup.createCriteria().list({
      createAlias('status', 'cstatus', CriteriaSpecification.LEFT_JOIN)
      or {
        isNull 'status'
        and {
          ne 'cstatus.value', KBComponent.STATUS_DELETED
          ne 'cstatus.value', KBComponent.STATUS_RETIRED
        }
      }
    })?.each { CuratoryGroup group ->
      result["${group.name}"] = [
              users     : group.users.collect { it.username },
              owner     : group.owner?.username,
              status    : group.status?.value
      ]
    }

    render result as JSON
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
      // find all updateable packages
      def updPacks = Package.executeQuery(
              "from Package p " +
                      "where p.source is not null and " +
                      "p.source.automaticUpdates = true " +
                      "and (p.source.lastRun is null or p.source.lastRun < current_date)")
      updPacks.each { Package p ->
        if (p.source.needsUpdate()) {
            def result = autoUpdatePackagesService.updateFromSource(p)
            log.debug("Result of update: ${result}")
            sleep(10000)
        }
      }
      log.info("auto update packages job completed.")

    redirect(controller: 'admin', action: 'jobs');
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

      String query = "select count(id) from ${typePerIndex.get(indice)}"
      indexInfo.countDB = FTControl.executeQuery(query)[0]
      indexInfo.countDeletedInDB = FTControl.executeQuery(query+ " where status = :status", [status: status_deleted]) ? FTControl.executeQuery(query+ " where status = :status", [status: status_deleted])[0] : 0
      result.indices << indexInfo
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
              IndexResponse indexResponse = esclient.prepareIndex("gokbdeletedcomponents", 'component', recid).setSource(it).get()
              if (indexResponse.getResult() != DocWriteResponse.Result.CREATED) {
                log.error("Error on record DeletedKBComponent in Index 'gokbdeletedcomponents'")
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
}
