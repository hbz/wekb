import de.wekb.helper.RCConstants
import org.gokb.cred.RefdataCategory
import org.gokb.cred.TitleInstancePackagePlatform

databaseChangeLog = {
    
    changeSet(author: "djebeniani (modified)", id: "1633607422487-1") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "A & I Database"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "A & I Database")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-2") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Audio"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Audio")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-3") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Database"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Database")])

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1633607422487-4") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Film"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Film")])

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1633607422487-5") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Image"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Image")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-6") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Journal"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Journal")])

            }
            rollback {}
        }
    }


    changeSet(author: "djebeniani (modified)", id: "1633607422487-7") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Book"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Book")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-8") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Published Score"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Published Score")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-9") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Article"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Article")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-10") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Software"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Software")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-11") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Statistics"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Statistics")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-12") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Market Data"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Market Data")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-13") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Biography"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Biography")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-14") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Legal Text"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Legal Text")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-15") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Cartography"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Cartography")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-16") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Miscellaneous"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Miscellaneous")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-17") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Other"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Other")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-18") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Standards"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Standards")])

            }
            rollback {}
        }
    }

    changeSet(author: "djebeniani (modified)", id: "1633607422487-19") {
        grailsChange {
            change {

                TitleInstancePackagePlatform.executeQuery('update TitleInstancePackagePlatform set medium = :newMedium where medium = :oldMedium',
                        [newMedium:  RefdataCategory.lookup(RCConstants.TIPP_MEDIUM, "Dataset"), oldMedium:  RefdataCategory.lookup(RCConstants.TITLEINSTANCE_MEDIUM, "Dataset")])

            }
            rollback {}
        }
    }
  
}
