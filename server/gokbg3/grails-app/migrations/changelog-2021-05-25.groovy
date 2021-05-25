databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1621944042935-1") {
        addNotNullConstraint(columnDataType: "bigint", columnName: "ct_org_fk", tableName: "contact")
    }
}
