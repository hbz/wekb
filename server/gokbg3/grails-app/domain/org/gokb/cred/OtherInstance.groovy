package org.gokb.cred

import javax.persistence.Transient
import org.gokb.GOKbTextUtils
import org.gokb.DomainClassExtender
import groovy.util.logging.*
import static grails.async.Promises.*


@Slf4j
class OtherInstance extends TitleInstance {

  @Transient
  def titleLookupService

  String summaryOfContent

  private static refdataDefaults = [
    "TitleInstance.medium"		: "Other"
  ]

  static mapping = {
    includes TitleInstance.mapping
         summaryOfContent column:'bk_summaryOfContent'
  }

  static constraints = {
         summaryOfContent (nullable:true, blank:false)
  }

  /**
   * Auditable plugin, on change
   *
   * See if properties that might impact the mapping of this instance to a work have changed.
   * If so, fire the appropriate event to cause a remap.
   */

  def afterUpdate() {
  }

  def afterInsert() {
  }

  @Override
  public String getNiceName() {
    return "Other";
  }

}
