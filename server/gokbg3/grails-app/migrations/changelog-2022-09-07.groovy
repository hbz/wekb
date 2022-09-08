databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1662579394414-1") {
        addColumn(tableName: "platform") {
            column(name: "plat_open_athens_fk_rv", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1662579394414-2") {
        addForeignKeyConstraint(baseColumnNames: "plat_open_athens_fk_rv", baseTableName: "platform", constraintName: "FKhyjognfgw6ne6nqvis5d4uw4y", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "djebeniani (generated)", id: "1662579394414-3") {
        addColumn(tableName: "source") {
            column(name: "source_kbart_wekb_fields", type: "boolean")
        }
    }
}