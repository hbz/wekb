package org.gokb.cred
import com.k_int.ClassUtils
import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants

import javax.persistence.Transient

class IdentifierNamespace {

  @Transient
  def springSecurityService

  String name
  String value

  @RefdataAnnotation(cat = RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)
  RefdataValue targetType

  String pattern
  String family

  Date dateCreated
  Date lastUpdated

  static mapping = {
    name column:'idns_name'
    value column:'idns_value'
    targetType column:'idns_targettype'
    family column:'idns_family'
    pattern column:'idns_pattern'
    dateCreated column:'idns_date_created'
    lastUpdated column:'idns_last_updated'
  }

  static constraints = {
    name (nullable:true)
    value (nullable:true, blank:false)
    family (nullable:true, blank:false)
    pattern (nullable:true, blank:false)
    targetType (nullable:true, blank:false)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)

    value(unique: ['value', 'targetType'])
  }

  public static final String restPath = "/namespaces"
  // used by @gokbg3.RestMappingService.selectJsonLabel
  public static final String jsonLabel = "name"

  @Override
  public boolean equals(Object obj) {
    if (obj != null) {

      def dep = ClassUtils.deproxy(obj)
      if (dep instanceof IdentifierNamespace) {
        // Check the value attributes.
        return (this.value == dep.value)
      }
    }
    return false
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    if(params.filter1){
      if(params.filter1 == "all"){
        ql = IdentifierNamespace.executeQuery("from IdentifierNamespace as t order by t.value")
      }else {
        RefdataValue refdataValue = RefdataValue.findByValueAndOwner(params.filter1, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
        ql = IdentifierNamespace.executeQuery("from IdentifierNamespace as t where lower(t.value) like :value and (t.targetType is null or t.targetType = :targetType) order by t.value", [value: "${params.q?.toLowerCase()}%", targetType: refdataValue])
      }
    }else {
      ql = IdentifierNamespace.executeQuery("from IdentifierNamespace as t where lower(t.value) like :value and t.targetType is null order by t.value", [value: "${params.q?.toLowerCase()}%"])
    }
    if ( ql ) {
      ql.each { t ->
        result.add([id:"org.gokb.cred.IdentifierNamespace:${t.id}",text:"${t.value} ${params.filter1 == 'all' ? ( t.targetType ? '(for '+t.targetType.value+')' : '' ) : ''}"])
      }
    }
    result
  }

  def beforeInsert() {
    value = value.toLowerCase()
  }

  public String toString() {
    "${name ?: value}".toString()
  }

  @Transient
  def getIdentifiersCount() {
    return Identifier.executeQuery("select count(value) from Identifier where namespace = :namespace", [namespace: this])[0]
  }

  @Transient
  def availableActions() {
    [
            [code: 'deleteIdentifierNamespace', label: 'Delete Namespace', perm: 'su']
    ]
  }

  @Transient
  userAvailableActions(){
    def user = springSecurityService.currentUser
    def allActions = []
    def result = []
    if (this.respondsTo('availableActions')){
      allActions = this.availableActions()
      allActions.each{ ao ->
        if (ao.perm in ["delete", "admin", "su"] && !user.hasRole('ROLE_SUPERUSER')) {
        }
        else{
          result.add(ao)
        }
      }
    }
    result
  }

  @Transient
  public String getDomainName() {
    return "Identifier Namespace"
  }
}
