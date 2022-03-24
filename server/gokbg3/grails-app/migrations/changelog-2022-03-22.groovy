
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import de.wekb.helper.RCConstants
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.Identifier

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1647956457885-1") {
        createTable(tableName: "identifier_new") {
            column(autoIncrement: "true", name: "id_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "identifier_newPK")
            }

            column(name: "id_version", type: "BIGINT")

            column(name: "id_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "id_uuid", type: "TEXT")

            column(name: "id_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "id_namespace_fk", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "id_value", type: "VARCHAR(255)")

            column(name: "id_kbcomponent_fk", type: "BIGINT")
        }
    }

/*    changeSet(author: "djebeniani (generated)", id: "1647956457885-2") {
        addUniqueConstraint(columnNames: "id_uuid", constraintName: "UC_IDENTIFIERNEWID_UUID_COL", tableName: "identifier_new")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1647956457885-3") {
        addUniqueConstraint(columnNames: "idns_targettype, idns_value", constraintName: "UKf364b72121a4a3b09831ae13f5c3", tableName: "identifier_namespace")
    }

/*    changeSet(author: "djebeniani (generated)", id: "1647956457885-4") {
        createIndex(indexName: "id_namespace_idx", tableName: "identifier_new") {
            column(name: "id_namespace_fk")
        }
    }*/

/*    changeSet(author: "djebeniani (generated)", id: "1647956457885-5") {
        createIndex(indexName: "id_value_idx", tableName: "identifier_new") {
            column(name: "id_value")
        }
    }*/

    changeSet(author: "djebeniani (generated)", id: "1647956457885-6") {
        addForeignKeyConstraint(baseColumnNames: "id_namespace_fk", baseTableName: "identifier_new", constraintName: "FK49a1oajyd4in853p7nnb3g7us", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-7") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-8") {
        addForeignKeyConstraint(baseColumnNames: "id_kbcomponent_fk", baseTableName: "identifier_new", constraintName: "FKiluk2oc8b6egk174kwwmiddbd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "kbcomponent")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-9") {
        dropForeignKeyConstraint(baseTableName: "identifier", constraintName: "fk408vsgum5mrg1kfy18dmyxs6e")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-10") {
        dropUniqueConstraint(constraintName: "unique_identifier_namespace", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (modified)", id: "1647956457885-11") {
        grailsChange {
            change {

                Map counts = [:]

                def combosCount = sql.rows("select count(combo_id) from combo where " +
                        "combo_status_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Status') and rdv_value = 'Active')" +
                        " and combo_type_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'KBComponent.Ids')")[0]

                counts.identifierComboCount = combosCount.count
                counts.identifierNewCount = 0

                for(int i = 0; i <= combosCount.count; i=i+100000) {

                    List idCombos = sql.rows("select * from combo where " +
                            "combo_status_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Status') and rdv_value = 'Active')" +
                            " and combo_type_rv_fk = (select rdv_id from refdata_value where rdv_owner = (SELECT rdc_id FROM refdata_category WHERE rdc_description = 'Combo.Type') and rdv_value = 'KBComponent.Ids')" +
                            " ORDER BY combo_id" +
                            " LIMIT 100000 OFFSET ${i}")

                    //println(idCombos.size())
                    //println(i)

                    idCombos.each { Map combo ->
                        List oldIdentifier = sql.rows("select * from identifier where kbc_id = ${combo.combo_to_fk}")
                        oldIdentifier.each { Map identifier ->
                            def kbcomponent = sql.rows("select * from kbcomponent where kbc_id = ${combo.combo_to_fk}")[0]
                            counts.identifierNewCount =  counts.identifierNewCount+1
                            sql.execute("""insert into identifier_new(id_id, id_version, id_value, id_namespace_fk, id_kbcomponent_fk, id_last_updated, id_date_created) values
                        ((select nextval ('hibernate_sequence')), 0, ${identifier.id_value}, ${identifier.id_namespace_fk}, ${combo.combo_from_fk},${kbcomponent.kbc_last_updated}, ${kbcomponent.kbc_date_created})""")
                        }
                    }
                }

                confirm("get combos and create new identifiers: ${counts}")
                changeSet.setComments("get combos and create new identifiers: ${counts}")
                println(counts)
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-12") {
        grailsChange {
            change {

                Map counts = [:]

                def identifierCount = sql.rows("select count(id_id) from identifier_new")[0]

                counts.identifierCount = identifierCount.count
                counts.identifierCountToDelete = 0
                List identifierListToDelete = []

                for(int i = 0; i <= identifierCount.count; i=i+100000) {

                    List identifiers = sql.rows("select * from identifier_new ORDER BY id_id LIMIT 100000 OFFSET ${i}")
                    identifiers.each { Map identifier_new ->
                        List title_instance = sql.rows("select * from title_instance where kbc_id = ${identifier_new.id_kbcomponent_fk}")
                        if(title_instance.size() > 0){
                            counts.identifierCountToDelete = counts.identifierCountToDelete+1
                            identifierListToDelete << identifier_new.id_id
                        }

                    }
                }

                identifierListToDelete.each{
                    sql.execute("DELETE FROM identifier_new where id_id = ${it}")
                }

                confirm("get new identifiers and delete title instance identifiers: ${counts}")
                changeSet.setComments("get new identifiers and delete title instance identifiers: ${counts}")
                println(counts)
            }
            rollback {}
        }
   }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-13") {
        grailsChange {
            change {

                def ns_objs_forPlaftorms = IdentifierNamespace.findAllByFamilyAndTargetType('ttl_prv ', RefdataValue.findByValueAndOwner('Title', RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)))

                RefdataValue targetTypeTipp = RefdataValue.findByValueAndOwner('TitleInstancePackagePlatform', RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))

                ns_objs_forPlaftorms.each{IdentifierNamespace identifierNamespace ->

                    identifierNamespace.targetType = targetTypeTipp
                    identifierNamespace.save(flush: true)
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-14") {
        grailsChange {
            change {

                def namespaces = [
                        [value: 'cup', name: 'cup', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'dnb', name: 'dnb', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'doi', name: 'DOI', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'eissn', name: 'e-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'ezb', name: 'EZB-ID', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'gnd-id', name: 'gnd-id', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'isbn', name: 'ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'issn', name: 'p-ISSN', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'issnl', name: 'ISSN-L', family: 'isxn', pattern: "^\\d{4}\\-\\d{3}[\\dX]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'pisbn', name: 'Print-ISBN', family: 'isxn', pattern: "^(?=[0-9]{13}\$|(?=(?:[0-9]+-){4})[0-9-]{17}\$)97[89]-?[0-9]{1,5}-?[0-9]+-?[0-9]+-?[0-9]\$", targetType: 'TitleInstancePackagePlatform'],
                        [value: 'oclc', name: 'oclc', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'preselect', name: 'preselect', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'TitleInstancePackagePlatform'],

                        //Kbart Import
                        [value: 'ill_indicator', name: 'Ill Indicator', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'package_isci', name: 'Package ISCI', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'package_isil', name: 'Package ISIL', targetType: 'TitleInstancePackagePlatform'],
                        [value: 'package_ezb_anchor', name: 'EZB Anchor', targetType: 'TitleInstancePackagePlatform'],


                        [value: 'Anbieter_Produkt_ID', name: 'Anbieter_Produkt_ID', targetType: 'Package'],
                        [value: 'dnb', name: 'dnb', targetType: 'Package'],
                        [value: 'doi', name: 'DOI', targetType: 'Package'],
                        [value: 'ezb', name: 'EZB-ID', targetType: 'Package'],
                        [value: 'gvk_ppn', name: 'gvk_ppn', targetType: 'Package'],
                        [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$", targetType: 'Package'],
                        [value: 'package_isci', name: 'Package ISCI', targetType: 'Package'],
                        [value: 'package_ezb_anchor', name: 'EZB Anchor', targetType: 'Package'],
                        [value: 'zdb', name: 'ZDB-ID', pattern: "^\\d+-[\\dxX]\$", targetType: 'Package'],
                        [value: 'zdb_ppn', name: 'EZB Anchor', targetType: 'Package'],


                        [value: 'gnd-id', name: 'gnd-id', targetType: 'Org'],
                        [value: 'isil', name: 'ISIL', pattern: "^(?=[0-9A-Z-]{4,16}\$)[A-Z]{1,4}-[A-Z0-9]{1,11}(-[A-Z0-9]+)?\$", targetType: 'Org'],
                        [value: 'zdb_ppn', name: 'EZB Anchor', targetType: 'Org'],
                ]

                namespaces.each { ns ->
                    RefdataValue targetType = RefdataValue.findByValueAndOwner(ns.targetType, RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
                    def ns_obj = IdentifierNamespace.findByValueAndTargetType(ns.value, targetType)

                    if (ns_obj) {
                        if (ns.pattern && !ns_obj.pattern) {
                            ns_obj.pattern = ns.pattern
                            ns_obj.save(flush: true)
                        }

                        if (ns.name && !ns_obj.name) {
                            ns_obj.name = ns.name
                            ns_obj.save(flush: true)
                        }

                        if (ns.targetType) {
                            ns_obj.targetType = targetType
                            ns_obj.save(flush: true)
                        }
                    } else {
                        ns.targetType = targetType
                        ns_obj = new IdentifierNamespace(ns).save(flush: true, failOnError: true)
                    }
                }

                Map counts = [:]

                def identifierCount = sql.rows("select count(id_id) from identifier_new")[0]

                counts.identifierCount = identifierCount.count
                counts.identifierNamespaceChangeCountPkg = 0
                counts.identifierNamespaceChangeCountOrg = 0
                counts.identifierNamespaceChangeCountTipp = 0

                RefdataValue targetTypeTipp = RefdataValue.findByValueAndOwner('TitleInstancePackagePlatform', RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
                RefdataValue targetTypePkg = RefdataValue.findByValueAndOwner('Package', RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))
                RefdataValue targetTypeOrg = RefdataValue.findByValueAndOwner('Org', RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE))

                for (int i = 0; i <= identifierCount.count; i = i + 100000) {

                    List identifiers = sql.rows("select * from identifier_new ORDER BY id_id LIMIT 100000 OFFSET ${i}")
                    identifiers.each { Map identifier_new ->
                        List pkg = sql.rows("select * from package where kbc_id = ${identifier_new.id_kbcomponent_fk}")
                        if (pkg.size() > 0) {
                            IdentifierNamespace oldNameSpace = IdentifierNamespace.findById(identifier_new.id_namespace_fk)
                            IdentifierNamespace newNameSpace = IdentifierNamespace.findByValueAndTargetType(oldNameSpace.value, targetTypePkg)

                            if (newNameSpace) {
                                Identifier identifier = Identifier.findById(identifier_new.id_id)
                                identifier.namespace = newNameSpace
                                identifier.save(flush: true)
                                counts.identifierNamespaceChangeCountPkg = counts.identifierNamespaceChangeCountPkg + 1
                            }

                        }

                        List org = sql.rows("select * from org where kbc_id = ${identifier_new.id_kbcomponent_fk}")
                        if (org.size() > 0) {
                            IdentifierNamespace oldNameSpace = IdentifierNamespace.findById(identifier_new.id_namespace_fk)
                            IdentifierNamespace newNameSpace = IdentifierNamespace.findByValueAndTargetType(oldNameSpace.value, targetTypeOrg)

                            if (newNameSpace) {
                                Identifier identifier = Identifier.findById(identifier_new.id_id)
                                identifier.namespace = newNameSpace
                                identifier.save(flush: true)
                                counts.identifierNamespaceChangeCountOrg = counts.identifierNamespaceChangeCountOrg + 1
                            }

                        }

                        List tipp = sql.rows("select * from title_instance_package_platform where kbc_id = ${identifier_new.id_kbcomponent_fk}")
                        if (tipp.size() > 0) {
                            IdentifierNamespace oldNameSpace = IdentifierNamespace.findById(identifier_new.id_namespace_fk)
                            IdentifierNamespace newNameSpace = IdentifierNamespace.findByValueAndTargetType(oldNameSpace.value, targetTypeTipp)

                            if (newNameSpace) {
                                Identifier identifier = Identifier.findById(identifier_new.id_id)
                                identifier.namespace = newNameSpace
                                identifier.save(flush: true)
                                counts.identifierNamespaceChangeCountTipp = counts.identifierNamespaceChangeCountTipp + 1
                            }

                        }
                    }
                }
                confirm("get new identifiers and change namespace of identifiers: ${counts}")
                changeSet.setComments("get new identifiers and change namespace of identifiers: ${counts}")
                println(counts)
            }
            rollback {}
        }
    }

    /*changeSet(author: "djebeniani (generated)", id: "1647956457885-14") {
        dropTable(tableName: "identifier")
    }*/




}
