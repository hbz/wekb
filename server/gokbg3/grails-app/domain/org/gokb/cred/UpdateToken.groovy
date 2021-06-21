package org.gokb.cred

class UpdateToken {

  String value
  Package pkg
  User updateUser

  Date dateCreated
  Date lastUpdated

  static belongsTo = [pkg: Package, updateUser: User]

  static mapping = {
    id column:'ut_id'
    value column: 'ut_value'
    pkg column:'ut_pkg_fk'
    updateUser column:'ut_update_user_fk'
    dateCreated column:'ut_date_created'
    lastUpdated column:'ut_last_updated'
  }

  static constraints = {
    value(nullable:false, blank:false);
    pkg(nullable:false, blank:false);
    updateUser(nullable:false, blank:false);

    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  }
}
