databaseChangeLog = {

    changeSet(author: "djebeniani (generated)", id: "1646901131691-1") {
        dropForeignKeyConstraint(baseTableName: "identifier_namespace", constraintName: "fk7csknbpyn4tb2wj3jg35a0b90")
    }

    changeSet(author: "djebeniani (generated)", id: "1646901131691-2") {
        dropColumn(columnName: "idns_datatype", tableName: "identifier_namespace")
    }


}
