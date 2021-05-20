package org.gokb.cred

import javax.persistence.Transient

class ComponentPerson {

  KBComponent component
  Person person
  RefdataValue role
  Date dateCreated
  Date lastUpdated

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
    component column:'cp_comp_fk'
    person column:'cp_person_fk'
    role column:'cp_role_fk'
    dateCreated column:'cp_date_created'
    lastUpdated column:'cp_last_updated'
  }

  static constraints = {
    component(nullable:false, blank:false)
    person(nullable:false, blank:false)
    role(nullable:false, blank:false)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
  } 
}
