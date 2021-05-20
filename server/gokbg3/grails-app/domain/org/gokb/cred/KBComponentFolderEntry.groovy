package org.gokb.cred

import javax.persistence.Transient

/**
 * A folder entry that points to a component
 */
class KBComponentFolderEntry extends FolderEntry {

  KBComponent linkedComponent

  Date dateCreated
  Date lastUpdated

  @Transient
  @Override
  public Object getLinkedItem() {
    linkedComponent;
  }

  @Transient
  @Override
  public String getDisplayName() {
    linkedComponent.name?.toString()
  }

  static mapping = {
    dateCreated column: 'kbcfe_date_created'
    lastUpdated column: 'kbcfe_last_updated'
  }

  static constraints = {
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

}
