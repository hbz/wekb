package org.gokb.cred

import javax.persistence.Transient

class DSAppliedCriterion {

  KBComponent appliedTo  //get decision support lines doesn't query for users i.e. component
  DSCriterion criterion //Pre-made formats i.e. Downloadable PDF, i.e. crit
  RefdataValue value //colour Red, Green, Amber, null = undecided
  User user  //Addition so each user can have a say on decisions

  static hasMany = [
    notes: DSNote
  ]

  static mappedBy = [
    notes: 'criterion'
  ]

  static mapping = {
    appliedTo column:'dsac_component_fk'
    criterion column:'dsac_crit_fk'
    value column:'dsac_value_fk'
    notes sort: 'lastUpdated', order: 'desc' //default ordering
  }

  static constraints = {
    appliedTo(nullable:false, blank:false)
    criterion(nullable:false, blank:false)
    value(nullable:false, blank:false)
    user(nullable:false, blank:false)
  }

}
