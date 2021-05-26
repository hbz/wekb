databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1621944042935-1") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "ct_org_fk", tableName: "contact")
    }

    changeSet(author: "djebeniani (generated)", id: "1621944042935-2") {
        addColumn(tableName: "platform") {
            column(name: "plat_counter_certified", type: "int8")
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1621944042935-3") {
        addForeignKeyConstraint(baseColumnNames: "plat_counter_certified", baseTableName: "platform", constraintName: "FK63pqqf09ejgndx0snu69x0pk7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }
}
