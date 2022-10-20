package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.Package
import org.gokb.cred.RefdataValue

import javax.persistence.Transient

class UpdatePackageInfo {

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

    boolean automaticUpdate = false

    Date lastChangedInKbart

    @RefdataAnnotation(cat = RCConstants.UPDATE_STATUS)
    RefdataValue status

    static mapping = {
        id          column:'upi_id'
        version     column:'upi_version'

        pkg         column: 'upi_pkg_fk', index: 'upi_pkg_idx'

        startTime         column: 'upi_start_time', index: 'upi_start_time_idx'
        endTime         column: 'upi_end_time', index: 'upi_start_time_idx'

        uuid column: 'upi_uuid', index: 'upi_uuid_idx'
        description column: 'upi_description', type: 'text'


        dateCreated column: 'upi_date_created'
        lastUpdated column: 'upi_last_updated'

        status column: 'upi_status_fk', index: 'upi_status_idx'

        countKbartRows column: 'upi_count_kbart_rows'
        countProcessedKbartRows column: 'upi_count_processed_kbart_rows'
        countInValidTipps column: 'upi_count_invalid_tipps'
        countChangedTipps column: 'upi_count_changed_tipps'
        countRemovedTipps column: 'upi_count_removed_tipps'
        countNewTipps column: 'upi_count_new_tipps'
        countNowTippsInWekb column: 'upi_count_now_tipps'
        countPreviouslyTippsInWekb column: 'upi_count_previously_tipps'

        onlyRowsWithLastChanged column: 'upi_only_rows_with_last_changed'

        kbartHasWekbFields column: 'upi_kbart_has_wekb_fields'

        lastChangedInKbart column: 'upi_last_changed_in_kbart'

        automaticUpdate column: 'upi_automatic_update'
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
        lastChangedInKbart (nullable:true, default: null)
    }

    static hasMany = [updateTippInfos: UpdateTippInfo]

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
        return "Package Update Infos";
    }

    @Transient
    public String getDomainName() {
        return "Package Update Infos"
    }

    @Transient
    public int getCountUpdateTipps() {
        return updateTippInfos.size()
    }


}
