import de.wekb.helper.RCConstants
import org.gokb.cred.BookInstance
import org.gokb.cred.DatabaseInstance
import org.gokb.cred.JournalInstance
import org.gokb.cred.OtherInstance
import org.gokb.cred.RefdataCategory
import org.gokb.cred.TitleInstancePackagePlatform

databaseChangeLog = {

    changeSet(author: "djebeniani (modified)", id: "1617261017626-1") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set date_first_online = bi.bk_datefirstonline from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_datefirstonline is not null and tipp.date_first_online is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-2") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set date_first_in_print = bi.bk_datefirstinprint from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_datefirstinprint is not null and tipp.date_first_in_print is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-3") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set tipp_volume_number = bi.bk_volume from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_volume is not null and tipp.tipp_volume_number is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-4") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set tipp_first_editor = bi.bk_firsteditor from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_firsteditor is not null and tipp.tipp_first_editor is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-5") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set tipp_first_author = bi.bk_firstauthor from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_firstauthor is not null and tipp.tipp_first_author is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-6") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set tipp_first_author = bi.bk_firsteditor from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_firsteditor is not null and tipp.tipp_first_author is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1617261017626-7") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set tipp_edition_statement = bi.bk_editionstatement from book_instance as bi\n" +
                                "    join combo on bi.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where bi.kbc_id = combo_from_fk and bi.bk_editionstatement is not null and tipp.tipp_edition_statement is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1617261017626-8") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        sql.execute("update title_instance_package_platform set tipp_medium_rv_fk = ti.medium_id from title_instance as ti\n" +
                                "    join combo on ti.kbc_id = combo_from_fk\n" +
                                "    join title_instance_package_platform as tipp on tipp.kbc_id = combo_to_fk where ti.kbc_id = combo_from_fk and ti.medium_id is not null and tipp.tipp_medium_rv_fk is null;")
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1617261017626-9") {
        grailsChange {
            change {
                grailsChange {
                    change {
                        TitleInstancePackagePlatform.findAllByPublicationTypeIsNull().each {TitleInstancePackagePlatform tipp ->

                            if(tipp.title instanceof BookInstance){
                                tipp.publicationType = RefdataCategory.lookupOrCreate(RCConstants.TIPP_PUBLICATION_TYPE, 'Monograph')
                            }

                            if(tipp.title instanceof OtherInstance){
                                tipp.publicationType = RefdataCategory.lookupOrCreate(RCConstants.TIPP_PUBLICATION_TYPE, 'Other')
                            }

                            if(tipp.title instanceof JournalInstance){
                                tipp.publicationType = RefdataCategory.lookupOrCreate(RCConstants.TIPP_PUBLICATION_TYPE, 'Serial')
                            }

                            if(tipp.title instanceof DatabaseInstance){
                                tipp.publicationType = RefdataCategory.lookupOrCreate(RCConstants.TIPP_PUBLICATION_TYPE, 'Database')
                            }
                            tipp.save()

                        }
                    }
                    rollback {}
                }
            }
            rollback {}
        }
    }


}
