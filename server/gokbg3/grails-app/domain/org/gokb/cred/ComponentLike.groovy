package org.gokb.cred

class ComponentLike {

  String ownerClass
  Long ownerId
  User user

  Date dateCreated
  Date lastUpdated

  static mapping = {
    id column:'like_id'
    dateCreated column: 'cl_date_created'
    lastUpdated column: 'cl_last_updated'
  }

  static constraints = {
    ownerClass(nullable:false, blank:false)
    ownerId(nullable:false, blank:false)
    user(nullable:false, blank:false)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }
}

