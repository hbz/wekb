package org.gokb.cred

import groovy.util.logging.*
import static grails.async.Promises.*

@Slf4j
class JournalInstance extends TitleInstance {

  private static refdataDefaults = [
    "TitleInstance.medium"		: "Journal"
  ]

  static mapping = {
    includes TitleInstance.mapping
  }

  static constraints = {
  }

  @Override
  String getLogEntityId() {
      "${this.class.name}:${id}"
  }

  @Override
  String getNiceName() {
    return "Journal"
  }

  public static final String restPath = "/titles"

  /**
   * Auditable plugin, on change
   *
   * See if properties that might impact the mapping of this instance to a work have changed.
   * If so, fire the appropriate event to cause a remap.
   */

  def afterUpdate() {
    // Currently, serial items are mapped based on the name of the journal. We may need to add a discriminator property
    if ( ( hasChanged('name') ) ||
         ( hasChanged('componentDiscriminator') )) {
    }
    touchAllDependants()
  }


  // audit plugin, onSave fires on a new item - we always want to map a work in this case, so directly call and wait
  def afterInsert() {

  }

}
