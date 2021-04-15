databaseChangeLog = {

    changeSet(author: "agalffy (hand-coded)", id: "1618484101333-1") {
        grailsChange {
            change {
                sql.execute('truncate table review_request_allocation_log;')
            }
            rollback {}
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618484101333-2") {
        grailsChange {
            change {
                sql.execute('truncate table allocated_review_group;')
            }
            rollback {}
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618484101333-3") {
        grailsChange {
            change {
                sql.execute('delete from review_request;')
            }
            rollback {}
        }
    }

}
