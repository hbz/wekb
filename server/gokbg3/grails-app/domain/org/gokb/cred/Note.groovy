package org.gokb.cred

import javax.persistence.Transient

class Note {
  String ownerClass
  Long ownerId
  String note
  RefdataValue locale
  User creator
  Date dateCreated
  Date lastUpdated 

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
    id column:'note_id'
    note column:'note_txt', type:'text'
  }

  static constraints = {
    ownerClass(nullable:false, blank:false)
    ownerId(nullable:false, blank:false)
    note(nullable:false, blank:false)
    creator(nullable:false, blank:false)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

  @Transient
  public String getDomainName() {
    return "Note"
  }
}
