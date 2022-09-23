package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.Package
import org.gokb.cred.RefdataValue

import javax.persistence.Transient

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
    int countNowTippsInWekb = 0
    int countPreviouslyTippsInWekb = 0

    boolean onlyRowsWithLastChanged = false

    boolean kbartHasWekbFields = false

    @RefdataAnnotation(cat = RCConstants.AUTO_UPDATE_STATUS)
    RefdataValue status

    static mapping = {
        id          column:'aupi_id'
        version     column:'aupi_version'

        pkg         column: 'aupi_pkg_fk', index: 'aupi_pkg_idx'

        startTime         column: 'aupi_start_time', index: 'aupi_start_time_idx'
        endTime         column: 'aupi_end_time', index: 'aupi_start_time_idx'

        uuid column: 'aupi_uuid', index: 'aupi_uuid_idx'
        description column: 'aupi_description', type: 'text'


        dateCreated column: 'aupi_date_created'
        lastUpdated column: 'aupi_last_updated'

        status column: 'aupi_status_fk', index: 'aupi_status_idx'

        countKbartRows column: 'aupi_count_kbart_rows'
        countProcessedKbartRows column: 'aupi_count_processed_kbart_rows'
        countInValidTipps column: 'aupi_count_invalid_tipps'
        countChangedTipps column: 'aupi_count_changed_tipps'
        countRemovedTipps column: 'aupi_count_removed_tipps'
        countNewTipps column: 'aupi_count_new_tipps'
        countNowTippsInWekb column: 'aupi_count_now_tipps'
        countPreviouslyTippsInWekb column: 'aupi_count_previously_tipps'

        onlyRowsWithLastChanged column: 'aupi_only_rows_with_last_changed'

        kbartHasWekbFields column: 'aupi_kbart_has_wekb_fields'
    }

    static constraints = {
        endTime     (nullable:true)

        countKbartRows (nullable:true)
        countProcessedKbartRows (nullable:true)
        countInValidTipps (nullable:true)
        countChangedTipps (nullable:true)
        countRemovedTipps (nullable:true)
        countNewTipps (nullable:true)
        countNowTippsInWekb (nullable:true)
        countPreviouslyTippsInWekb (nullable:true)
        onlyRowsWithLastChanged  (nullable:true)      
    }

    static hasMany = [autoUpdateTippInfos: AutoUpdateTippInfo]

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
        return "Auto Update Package Info";
    }

    @Transient
    public String getDomainName() {
        return "Auto Update Package Info"
    }

    @Transient
    public int getCountAutoUpdateTipps() {
        return autoUpdateTippInfos.size()
    }


}
