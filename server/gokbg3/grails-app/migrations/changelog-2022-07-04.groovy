import org.gokb.cred.AllocatedReviewGroup
import org.gokb.cred.Combo
import org.gokb.cred.ComponentHistoryEvent
import org.gokb.cred.ComponentHistoryEventParticipant
import org.gokb.cred.ComponentWatch
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.KBComponent
import org.gokb.cred.KBComponentVariantName
import org.gokb.cred.ReviewRequest
import org.gokb.cred.ReviewRequestAllocationLog
import org.gokb.cred.User

databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1656918437150-1") {
        grailsChange {
            change {
                def titleCount = sql.rows("select count(kbc_id) from title_instance")[0]

                println("Prepare to delete ${titleCount.count} TitleInstance")

                for (int i = 0; i <= titleCount.count; i = i + 100000) {

                    println("Deleting Title_Instances Index: ${i}")

                    List idList = sql.rows("select kbc_id from title_instance ORDER BY kbc_id LIMIT 100000 OFFSET ${i}")

                    idList.each { def map ->
                        println("Deleting Title_Instance with ID: ${map.kbc_id}")
                        sql.execute("delete from combo as c where c.combo_from_fk=:component or c.combo_to_fk=:component;", [component: map.kbc_id])
                        sql.execute("delete from component_watch as cw where cw.cw_component=:component;", [component: map.kbc_id])
                        sql.execute("delete from kbcomponent_variant_name as c where c.cvn_kbc_fk=:component;", [component: map.kbc_id])
                        sql.execute("delete from review_request_allocation_log as c where c.rr_id in ( select r.rr_id from review_request as r where r.component_to_review_id=:component);", [component: map.kbc_id])
                        sql.execute("delete from allocated_review_group as g where g.review_id in ( select r.rr_id from review_request as r where r.component_to_review_id=:component);", [component: map.kbc_id])
                        def events_to_delete = sql.rows("select c.event_id from component_history_event_participant as c where c.participant_id = :component;", [component: map.kbc_id])
                        events_to_delete.each {
                            sql.execute("delete from component_history_event_participant as c where c.event_id = ?;", [it.event_id])
                            sql.execute("delete from component_history_event as c where c.id = ?;", [it.event_id])
                        }
                        sql.execute("delete from review_request as c where c.component_to_review_id=:component;", [component: map.kbc_id])
                        sql.execute("update kbcomponent set kbc_duplicate_of = NULL where kbc_duplicate_of=:component;", [component: map.kbc_id])
                        sql.execute("delete from component_price where cp_owner_component_fk=:component;", [component: map.kbc_id])
                        sql.execute("delete from title_instance where kbc_id = :kbc_id;", [kbc_id: map.kbc_id])
                        sql.execute("delete from kbcomponent where kbc_id = :kbc_id;", [kbc_id: map.kbc_id])
                    }
                }
                confirm("Finish deleting TitleInstance: ${titleCount.count}")
                changeSet.setComments("Finish deleting TitleInstance: ${titleCount.count}")
            }
            rollback {}
        }
    }

  /*  changeSet(author: "djebeniani (generated)F id: "1656918437150-2") {
        dropForeignKeyConstraint(baseTableName: "activity", constraintName: "fk36xcqu4rvbkjhb1kiejwkbfp7")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-3") {
        dropForeignKeyConstraint(baseTableName: "folder_entry", constraintName: "fk4sann6e8xej2j3tl3kb7vkarl")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-4") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fk53crjhlw2e45klohjtpmjcj43")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-5") {
        dropForeignKeyConstraint(baseTableName: "component_like", constraintName: "fk695wmrclintlpomggyet72j3s")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-6") {
        dropForeignKeyConstraint(baseTableName: "folder_entry", constraintName: "fk8xdck8vgx7yb7ymmlm400kuga")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-7") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fkddq4cyit5bv5iciwyk0i0f5in")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-8") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fkes1slhq1sbmothegvrpocgr9e")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-9") {
        dropForeignKeyConstraint(baseTableName: "activity", constraintName: "fkld7dbrn0jruks54my8pinidtg")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-10") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fknpmbxucn52ulosptk44bmffkg")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-11") {
        dropForeignKeyConstraint(baseTableName: "activity", constraintName: "fkpcf36i360o9cyyrvo8vqwma7i")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-12") {
        dropForeignKeyConstraint(baseTableName: "title_instance", constraintName: "fkqf3s7eahha5frt9c3mgdr22m")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-13") {
        dropForeignKeyConstraint(baseTableName: "office", constraintName: "fkrr2uet4tl16s5baep5skseuy0")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-14") {
        dropForeignKeyConstraint(baseTableName: "folder_entry", constraintName: "fkse1dy2yymgie3ucb2i2guo4pa")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-15") {
        dropUniqueConstraint(constraintName: "unique_identifier_namespace", tableName: "identifier_namespace")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-16") {
        dropTable(tableName: "activity")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-17") {
        dropTable(tableName: "annotation")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-18") {
        dropTable(tableName: "book_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-19") {
        dropTable(tableName: "component_like")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-20") {
        dropTable(tableName: "database_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-21") {
        dropTable(tableName: "folder_entry")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-22") {
        dropTable(tableName: "journal_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-23") {
        dropTable(tableName: "office")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-24") {
        dropTable(tableName: "other_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-25") {
        dropTable(tableName: "title_instance")
    }

    changeSet(author: "djebeniani (generated)", id: "1656918437150-26") {
        dropTable(tableName: "title_instance_platform")
    }*/
}
