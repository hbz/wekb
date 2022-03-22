import de.wekb.helper.RDStore

databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1647956457885-1") {
        createTable(tableName: "identifierNew") {
            column(autoIncrement: "true", name: "id_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "identifierNewPK")
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
        addUniqueConstraint(columnNames: "id_uuid", constraintName: "UC_IDENTIFIERNEWID_UUID_COL", tableName: "identifierNew")
    }*/

    changeSet(author: "djebeniani (generated)", id: "1647956457885-3") {
        addUniqueConstraint(columnNames: "idns_targettype, idns_value", constraintName: "UKf364b72121a4a3b09831ae13f5c3", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-4") {
        createIndex(indexName: "id_namespace_idx", tableName: "identifierNew") {
            column(name: "id_namespace_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-5") {
        createIndex(indexName: "id_value_idx", tableName: "identifierNew") {
            column(name: "id_value")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-6") {
        addForeignKeyConstraint(baseColumnNames: "id_namespace_fk", baseTableName: "identifierNew", constraintName: "FK49a1oajyd4in853p7nnb3g7us", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-7") {
        addForeignKeyConstraint(baseColumnNames: "idns_targettype", baseTableName: "identifier_namespace", constraintName: "FK7csknbpyn4tb2wj3jg35a0b90", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1647956457885-8") {
        addForeignKeyConstraint(baseColumnNames: "id_kbcomponent_fk", baseTableName: "identifierNew", constraintName: "FKiluk2oc8b6egk174kwwmiddbd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "kbcomponent")
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
                def idCombos = sql.rows("select * from combo where combo_status_rv_fk = ${RDStore.COMBO_STATUS_ACTIVE.id} and combo_type_rv_fk = ${RDStore.COMBO_TYPE_KB_IDS}")

                idCombos.each { Map combo ->

                    def oldIdentifier = sql.rows("select * from identifier where kbc_id = ${combo.combo_to_fk}")

                    oldIdentifier.each { Map identifier ->
                            sql.execute("""insert into identifierNew(id_id, id_version, id_value, id_namespace_fk, id_kbcomponent_fk, id_last_updated, id_date_created) values
                        ((select nextval ('hibernate_sequence')), 0, ${identifier.id_value}, ${identifier.id_namespace_fk}, ${combo.combo_from_fk}, (select now()), (select now())) 
                        """)
                    }
                }
            }
            rollback {}
        }
    }

    /*changeSet(author: "djebeniani (generated)", id: "1647956457885-12") {
        dropTable(tableName: "identifier")
    }*/




}
