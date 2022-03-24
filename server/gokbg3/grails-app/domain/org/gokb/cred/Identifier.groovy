package org.gokb.cred

import groovy.util.logging.*


@Slf4j
class Identifier {

  IdentifierNamespace namespace
  String value
  String uuid

  // Timestamps
  Date dateCreated
  Date lastUpdated

  static belongsTo = [
          kbcomponent: KBComponent
  ]


  static constraints = {
    uuid(nullable: true, unique: false, blank: false, maxSize: 2048)
    namespace(nullable: false, blank: false)
    value(validator: { val, obj ->
      if (!val || !val.trim()) {
        return ['notNull']
      }

      def dupes = Identifier.findAllByNamespaceAndValue(obj.namespace, val)
      def pattern = obj.namespace.pattern ? ~"${obj.namespace.pattern}" : null
      def isDupe = false

      dupes.each { d ->
        if (d != obj) {
          isDupe = true
        }
      }
      if (isDupe) {
        return ['notUnique']
      }

      if (pattern && !(val ==~ pattern)) {
        return ['illegalIdForm']
      }
    })
  }

  static mapping = {
    table("identifier_new")
    id column: 'id_id'
    version column: 'id_version'
    value column: 'id_value', index: 'id_value_idx'
    namespace column: 'id_namespace_fk', index: 'id_namespace_idx'
    uuid column: 'id_uuid', type: 'text', index: 'id_uuid_idx'
    kbcomponent column: 'id_kbcomponent_fk'
    lastUpdated column: 'id_last_updated'
    dateCreated column: 'id_date_created'
  }

  protected def generateUuid(){
    if (!uuid){
      uuid = UUID.randomUUID().toString()
    }
  }

  def beforeInsert(){
    generateUuid()
  }

  def beforeUpdate(){
    log.debug("beforeUpdate for ${this}")
    if (!uuid){
      generateUuid()
    }
  }


  public static normalizeIdentifier(String id) {
    return id.toLowerCase().trim().replaceAll("\\W", "")
  }


  public String getName() {
    return value
  }

  public String toString() {
    "${namespace.value}:${value} (Identifier ${id})".toString()
  }

 /* static Identifier construct(Map<String, Object> map) {

    withTransaction {
      String value     = map.get('value')
      KBComponent kbcomponent = map.get('kbcomponent')
      def namespace    = map.get('namespace')
      def targetType    = map.get('targetType')

      if(targetType){
        targetType = RefdataValue.findByValueAndOwner(targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
      }

      IdentifierNamespace ns
      if (namespace instanceof IdentifierNamespace) {
        ns = namespace
      }
      else {
        if (targetType){
          ns = IdentifierNamespace.findByValueAndTargetType(namespace?.trim(), RefdataValue.findByValueAndOwner(targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)))
        } else {
          ns = IdentifierNamespace.findByValue(namespace?.trim())
        }

        if(! ns) {
          if (targetType){
            ns = new IdentifierNamespace(value: namespace, name:  namespace, targetType: targetType)
          } else {
            ns = new IdentifierNamespace(value: namespace, name: namespace)
          }
          ns.save()
        }
      }

      String attr = Identifier.getAttributeName(reference)

      def ident = Identifier.executeQuery(
              'select ident from Identifier ident where ident.value = :val and ident.ns = :ns and ident.kbcomponent = :kbcomponent order by ident.id',
              [val: value, ns: ns, kbcomponent: kbcomponent]

      )
      if (ident){
        factoryResult.status += FactoryResult.STATUS_ERR_UNIQUE_BUT_ALREADY_EXISTS_IN_REFERENCE_OBJ
      }
      if (! ident.isEmpty()) {
        if (ident.size() > 1) {
          factoryResult.status += FactoryResult.STATUS_ERR_UNIQUE_BUT_ALREADY_SEVERAL_EXIST_IN_REFERENCE_OBJ
          static_logger.debug("WARNING: multiple matches found for ( ${value}, ${ns}, ${reference} )")
        }
        ident = ident.first()
      }

      if (! ident) {
        Identifier identifierInDB = Identifier.findByNsAndValue(ns, value)
        if (ns.isUnique && identifierInDB && value != "Unknown") {
          static_logger.debug("NO IDENTIFIER CREATED: multiple occurrences found for unique namespace ( ${value}, ${ns} )")
          factoryResult.status += FactoryResult.STATUS_ERR_UNIQUE_BUT_ALREADY_SEVERAL_EXIST_IN_REFERENCE_OBJ
//                factoryResult.result = identifierInDB
          ident = identifierInDB
        } else {
          static_logger.debug("INFO: no match found; creating new identifier for ( ${value}, ${ns}, ${reference.class} )")
          ident = new Identifier(ns: ns, value: value)
          if(parent)
            ident.instanceOf = parent
          ident.setReference(reference)
          ident.note = note
          boolean success = ident.save()
          if (success){
            factoryResult.status += FactoryResult.STATUS_OK
          } else {
            factoryResult.status += FactoryResult.STATUS_ERR
          }
        }
      }
    }
  }*/
}
