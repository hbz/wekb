databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1617270513617-1") {
        addColumn(tableName: "kbcomponent") {
            column(name: "kbc_language_rv_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-2") {
        addColumn(tableName: "component_history_event") {
            column(name: "last_seen", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-3") {
        addColumn(tableName: "package") {
            column(name: "pkg_file", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-4") {
        addColumn(tableName: "package") {
            column(name: "pkg_open_access", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-5") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_edition_statement", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-6") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_first_author", type: "text")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-7") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_first_editor", type: "text")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-8") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_last_change_ext", type: "timestamp")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-9") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_medium_rv_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-10") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_parent_publication_id", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-11") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_preceding_publication_id", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-12") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_publication_type_rv_fk", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-13") {
        addColumn(tableName: "title_instance_package_platform") {
            column(name: "tipp_volume_number", type: "varchar(255)")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-14") {
        addForeignKeyConstraint(baseColumnNames: "pkg_open_access", baseTableName: "package", constraintName: "FK6i7hmoyr4rsoau1avm9echbos", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-15") {
        addForeignKeyConstraint(baseColumnNames: "pkg_file", baseTableName: "package", constraintName: "FKdgwi2nrck0bhovlb2sju6iwfp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-16") {
        addForeignKeyConstraint(baseColumnNames: "kbc_language_rv_fk", baseTableName: "kbcomponent", constraintName: "FKdr92uqpirxysmd2re5v62mcay", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-17") {
        addForeignKeyConstraint(baseColumnNames: "tipp_medium_rv_fk", baseTableName: "title_instance_package_platform", constraintName: "FKiakmns0h0193nhxeru4f6dpye", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-18") {
        addForeignKeyConstraint(baseColumnNames: "tipp_publication_type_rv_fk", baseTableName: "title_instance_package_platform", constraintName: "FKmxjr8q9llpqsg8uk62h9c25qh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-19") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkajnnn4oj0lv2ek3sq3ms6squ4")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-20") {
        dropForeignKeyConstraint(baseTableName: "history", constraintName: "fkcvquo702rtt8hih985u4mqiow")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-21") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkigjskhpyi7lvpbsq1tx36bh1w")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-22") {
        dropForeignKeyConstraint(baseTableName: "package", constraintName: "fkkt416sp4afou253it51jbsbky")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-23") {
        dropTable(tableName: "classification")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-24") {
        dropTable(tableName: "content_item")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-25") {
        dropTable(tableName: "history")
    }

    changeSet(author: "djebeniani (generated)", id: "1617270513617-26") {
        dropColumn(columnName: "frequency", tableName: "source")
    }

}
