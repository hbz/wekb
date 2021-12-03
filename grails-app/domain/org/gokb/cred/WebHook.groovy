package org.gokb.cred

class WebHook {
  String oid
  WebHookEndpoint endpoint

  Date dateCreated
  Date lastUpdated

  static constraints = {
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }

  static mapping = {
    dateCreated column:'wh_date_created'
    lastUpdated column:'wh_last_updated'
  }
}
