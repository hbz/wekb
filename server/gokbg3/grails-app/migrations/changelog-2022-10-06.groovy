databaseChangeLog = {


    changeSet(author: "djebeniani (modified)", id: "1665088014984-1") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Update.Status' where rdc_description = 'AutoUpdate.Status'; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665088014984-2") {
        grailsChange {
            change {
                sql.executeUpdate('''update refdata_category set rdc_description = 'Update.Type' where rdc_description = 'AutoUpdate.Type'; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665088014984-3") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table auto_update_package_info rename to update_package_info; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665088014984-4") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table auto_update_tipp_info rename to update_tipp_info; ''')
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (generated)", id: "1665088014984-5") {
        addColumn(tableName: "update_package_info") {
            column(name: "upi_automatic_update", type: "boolean")
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1665088014984-6") {
        grailsChange {
            change {
                sql.executeUpdate('''update update_package_info set upi_automatic_update = true where upi_automatic_update is null; ''')
            }
            rollback {}
        }
    }

    /*changeSet(author: "djebeniani (modified)", id: "1665088014984-6") {
        grailsChange {
            change {
                sql.executeUpdate('''alter table public.auto_update_package_info rename column aupi_id to upi_id;''')
            }
            rollback {}
        }
    }*/
}
