databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1621352515660-1") {
        createTable(tableName: "contact") {
            column(autoIncrement: "true", name: "ct_id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "contactPK")
            }

            column(name: "ct_version", type: "BIGINT")

            column(name: "ct_org_fk", type: "BIGINT")

            column(name: "ct_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "ct_last_updated", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "ct_type_rv_fk", type: "BIGINT")

            column(name: "ct_content", type: "VARCHAR(255)")

            column(name: "ct_content_type_rv_fk", type: "BIGINT")

            column(name: "ct_language_rv_fk", type: "BIGINT")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-2") {
        addColumn(tableName: "platform") {
            column(name: "plat_title_namespace_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-3") {
        createIndex(indexName: "ct_org_idx", tableName: "contact") {
            column(name: "ct_org_fk")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-4") {
        addForeignKeyConstraint(baseColumnNames: "plat_title_namespace_fk", baseTableName: "platform", constraintName: "FK7afossud20vb74bb15dcd4q7b", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-5") {
        addForeignKeyConstraint(baseColumnNames: "ct_content_type_rv_fk", baseTableName: "contact", constraintName: "FKc5g8fruti19ntaxu0l3oolxk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-6") {
        addForeignKeyConstraint(baseColumnNames: "ct_language_rv_fk", baseTableName: "contact", constraintName: "FKk7cibsotlcmfot0vilqt26926", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-7") {
        addForeignKeyConstraint(baseColumnNames: "ct_org_fk", baseTableName: "contact", constraintName: "FKolaxb1rbuquu3r1cx892to7o7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "kbc_id", referencedTableName: "org")
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-8") {
        addForeignKeyConstraint(baseColumnNames: "ct_type_rv_fk", baseTableName: "contact", constraintName: "FKsfunahghe7yd9c9rafnvulg7i", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-9") {
        dropForeignKeyConstraint(baseTableName: "org", constraintName: "fk6dj9d6ico7o1pon06b48qum75")
    }

    changeSet(author: "djebeniani (generated)", id: "1621352515660-10") {
        dropColumn(columnName: "title_namespace_id", tableName: "org")
    }
}
