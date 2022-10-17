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
        id          column:'uti_id'
        version     column:'uti_version'

        tipp         column: 'uti_tipp_fk', index: 'uti_tipp_idx'

        startTime         column: 'uti_start_time', index: 'uti_start_time_idx'
        endTime         column: 'uti_end_time', index: 'uti_end_time_idx'

        uuid column: 'uti_uuid', index: 'uti_uuid_idx'
        description column: 'uti_description', type: 'text'

        dateCreated column: 'uti_date_created'
        lastUpdated column: 'uti_last_updated'

        updatePackageInfo column: 'uti_upi_fk', index: 'uti_upi_idx'

        status column: 'uti_status_fk', index: 'uti_status_idx'

        type column: 'uti_type_fk', index: 'uti_type_idx'

        tippProperty column: 'uti_tipp_property', index: 'uti_tipp_property_idx'
        kbartProperty column: 'uti_kbart_property', index: 'uti_kbart_property_idx'
        newValue column: 'uti_new_value', type: 'text'
        oldValue column: 'uti_old_value', type: 'text'
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
        return "Title Update Infos";
    }

    @Transient
    public String getDomainName() {
        return "Title Update Infos"
    }
}
