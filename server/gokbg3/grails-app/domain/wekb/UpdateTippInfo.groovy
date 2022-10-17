package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.RefdataValue
import org.gokb.cred.TitleInstancePackagePlatform

import javax.persistence.Transient

class UpdateTippInfo {

    String uuid

    String description

    Date startTime
    Date endTime

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.UPDATE_STATUS)
    RefdataValue status

    @RefdataAnnotation(cat = RCConstants.UPDATE_TYPE)
    RefdataValue type

    String tippProperty
    String kbartProperty
    String newValue
    String oldValue

    static mapping = {
        id          column:'auti_id'
        version     column:'auti_version'

        tipp         column: 'auti_tipp_fk', index: 'auti_tipp_idx'

        startTime         column: 'auti_start_time', index: 'auti_start_time_idx'
        endTime         column: 'auti_end_time', index: 'auti_end_time_idx'

        uuid column: 'auti_uuid', index: 'auti_uuid_idx'
        description column: 'auti_description', type: 'text'

        dateCreated column: 'auti_date_created'
        lastUpdated column: 'auti_last_updated'

        updatePackageInfo column: 'auti_aupi_fk', index: 'auti_aupi_idx'
        //tipp column: 'auti_tipp_fk'

        status column: 'auti_status_fk', index: 'auti_status_idx'

        type column: 'auti_type_fk', index: 'auti_type_idx'

        tippProperty column: 'auti_tipp_property', index: 'auti_tipp_property_idx'
        kbartProperty column: 'auti_kbart_property', index: 'auti_kbart_property_idx'
        newValue column: 'auti_new_value', type: 'text'
        oldValue column: 'auti_old_value', type: 'text'
    }

    static constraints = {
        endTime     (nullable:true)

        tippProperty (nullable:true)
        kbartProperty (nullable:true)
        newValue (nullable:true)
        oldValue (nullable:true)

    }

    static belongsTo = [updatePackageInfo: UpdatePackageInfo,
                        tipp: TitleInstancePackagePlatform]

    def beforeInsert(){
        generateUuid()
    }

    protected def generateUuid(){
        if (!uuid){
            uuid = UUID.randomUUID().toString()
        }
    }

    public String getNiceName() {
        return "Update Title Info";
    }

    @Transient
    public String getDomainName() {
        return "Update Title Info"
    }
}
