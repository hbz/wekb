package wekb

import de.wekb.helper.RCConstants
import de.wekb.helper.ServerUtils
import grails.plugins.mail.MailService
import org.gokb.cred.JobResult
import org.gokb.cred.RefdataCategory

class SendJobInfosJob {

  MailService mailService

  static triggers = {
    // Cron timer.
    cron name: 'SendJobInfosTrigger', cronExpression: "0 0 7 * * ? *" // daily at 07:00
  }

  def execute() {
    if (grailsApplication.config.gokb.sendJobInfosJob) {
      log.debug("Beginning scheduled send job infos job.")
      sendPackageUpdateInfosJob()
      log.info("send job infos job completed.")
    } else {
      log.debug("automatic send job infos Job is not enabled - set config.gokb.sendJobInfosJob = true");
    }
  }

  private sendPackageUpdateInfosJob(){

    List<JobResult> jobResultList = JobResult.executeQuery("select jr from JobResult as jr where jr.statusText in (:status) and jr.dateCreated > (CURRENT_DATE-1) and jr.type in (:types) order by jr.dateCreated desc", [status: ["ERROR", "FAIL"], types: [RefdataCategory.lookup(RCConstants.JOB_TYPE, 'ManuellUpdatePackageJob'), RefdataCategory.lookup(RCConstants.JOB_TYPE, 'AutoUpdatePackagesJob'), RefdataCategory.lookup(RCConstants.JOB_TYPE, 'PackageCrossRef')]])

      String currentServer = ServerUtils.getCurrentServer()
      String subjectSystemPraefix = (currentServer == ServerUtils.SERVER_PROD)? "" : (ServerUtils.getCurrentServerSystemId() + " - ")
      String mailSubject = subjectSystemPraefix + "we:kb Manage Package Update Jobs"
      String currentSystemId = ServerUtils.getCurrentServerSystemId()

      try {
        mailService.sendMail {
          to "laser@hbz-nrw.de", "moetez.djebeniani@hbz-nrw.de"
          from "wekb Server <wekb-managePackageUpdateJobs@wekbServer>"
          subject mailSubject
          html (view: "/mailTemplate/html/packageUpdateJobsMail", model: [jobResultList: jobResultList])
        }
      } catch (Exception e) {
        String eMsg = e.message
        log.error("SendJobInfosJob - sendJobInfos() :: Unable to perform email due to exception ${eMsg}")
      }
  }
}
