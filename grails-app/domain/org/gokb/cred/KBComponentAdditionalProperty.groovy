package org.gokb.cred

class KBComponentAdditionalProperty {

  AdditionalPropertyDefinition propertyDefn
  String apValue

    Date dateCreated
    Date lastUpdated

  static belongsTo = [ fromComponent:KBComponent ]

  static mapping = {
              id column:'kbcap_id'
   fromComponent column:'kbcap_kbc_fk'
    propertyDefn column:'kbcap_apd_fk'
         apValue column:'kbcap_value', type:'text'
      dateCreated column:'kbcap_date_created'
      lastUpdated column:'kbcap_last_updated'
  }

    static constraints = {
        dateCreated(nullable:true, blank:true)
        lastUpdated(nullable:true, blank:true)
    }


}
