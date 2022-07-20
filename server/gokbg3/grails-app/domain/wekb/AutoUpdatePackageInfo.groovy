package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.Package
import org.gokb.cred.RefdataValue

class AutoUpdatePackageInfo {

    String uuid

    String description

    Date startTime
    Date endTime

    Date dateCreated
    Date lastUpdated

    int countKbartRows = 0
    int countProcessedKbartRows = 0
    int countInValidTipps = 0
    int countChangedTipps = 0
    int countRemovedTipps = 0
    int countNewTipps = 0

    @RefdataAnnotation(cat = RCConstants.AUTO_UPDATE_STATUS)
    RefdataValue status

    static mapping = {
        id          column:'aupi_id'
        version     column:'aupi_version'

        pkg         column: 'aupi_pkg_fk'

        startTime         column: 'aupi_start_time'
        endTime         column: 'aupi_end_time'

        uuid column: 'aupi_uuid'
        description column: 'aupi_description', type: 'text'


        dateCreated column: 'aupi_date_created'
        lastUpdated column: 'aupi_last_updated'

        autoUpdateTippInfo column: 'aupi_auti_fk'

        status column: 'aupi_status_fk'

        countKbartRows column: 'aupi_count_kbart_rows'
        countProcessedKbartRows column: 'aupi_count_processed_kbart_rows'
        countInValidTipps column: 'aupi_count_invalid_tipps'
        countChangedTipps column: 'aupi_count_changed_tipps'
        countRemovedTipps column: 'aupi_count_removed_tipps'
        countNewTipps column: 'aupi_count_new_tipps'
    }

    static constraints = {
        endTime     (nullable:true)

        countKbartRows (nullable:true)
        countProcessedKbartRows (nullable:true)
        countInValidTipps (nullable:true)
        countChangedTipps (nullable:true)
        countRemovedTipps (nullable:true)
        countNewTipps (nullable:true)

    }

    static hasMany = [autoUpdateTippInfo: AutoUpdateTippInfo]

    static belongsTo = [pkg: Package]

    def beforeInsert(){
        generateUuid()
    }

    protected def generateUuid(){
        if (!uuid){
            uuid = UUID.randomUUID().toString()
        }
    }


    public String getNiceName() {
        return "Auto Update Info";
    }


}
