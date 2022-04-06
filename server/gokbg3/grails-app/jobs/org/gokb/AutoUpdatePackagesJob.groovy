package org.gokb

import org.gokb.cred.Package
import wekb.AutoUpdatePackagesService

class AutoUpdatePackagesJob {

  AutoUpdatePackagesService autoUpdatePackagesService
  // Allow only one run at a time.
  static concurrent = false

  static triggers = {
    // Cron timer.
    cron name: 'AutoUpdatePackageTrigger', cronExpression: "0 0 20 * * ? *" // daily at 8:00 pm
// for testing: every 5 minutes   cron name: 'AutoUpdatePackageTrigger', cronExpression: "0 1/5 * * * ? *" // daily at 6:00 am
  }

  def execute() {
    if (grailsApplication.config.gokb.packageUpdate.enabled && grailsApplication.config.gokb.ygorUrl) {
      log.debug("Beginning scheduled auto update packages job.")

      autoUpdatePackagesService.findPackageToUpdateAndUpdate()

      log.info("auto update packages job completed.")
    } else {
      log.debug("automatic package update is not enabled - set config.gokb.packageUpdate_enabled = true and config.gokb.ygorUrl in config to enable");
    }
  }
}
