databaseChangeLog = {

    changeSet(author: "agalffy (hand-coded)", id: "1618480926658-1") {
        grailsChange {
            change {
                sql.execute("delete from review_request_allocation_log where rr_id in (select rr_id from review_request where date_created < '2021-02-02');")
            }
            rollback {}
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618480926658-2") {
        grailsChange {
            change {
                sql.execute("delete from allocated_review_group where review_id in (select rr_id from review_request where date_created < '2021-02-02');")
            }
            rollback {}
        }
    }

    changeSet(author: "agalffy (hand-coded)", id: "1618480926658-3") {
        grailsChange {
            change {
                sql.execute("delete from review_request where date_created < '2021-02-02';")
            }
            rollback {}
        }
    }
}
