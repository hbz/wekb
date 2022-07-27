package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.RefdataValue
import org.gokb.cred.TitleInstancePackagePlatform

import javax.persistence.Transient

class AutoUpdateTippInfo {

    String uuid

    String description

    Date startTime
    Date endTime

    Date dateCreated
    Date lastUpdated

    @RefdataAnnotation(cat = RCConstants.AUTO_UPDATE_STATUS)
    RefdataValue status

    @RefdataAnnotation(cat = RCConstants.AUTO_UPDATE_TYPE)
    RefdataValue type

    String tippProperty
    String kbartProperty
    String newValue
    String oldValue

    static mapping = {
        id          column:'auti_id'
        version     column:'auti_version'

        tipp         column: 'auti_tipp_fk'

        startTime         column: 'auti_start_time'
        endTime         column: 'auti_end_time'

        uuid column: 'auti_uuid'
        description column: 'auti_description', type: 'text'

        dateCreated column: 'auti_date_created'
        lastUpdated column: 'auti_last_updated'

        autoUpdatePackageInfo column: 'auti_aupi_fk'
        tipp column: 'auti_tipp_fk'

        status column: 'auti_status_fk'

        type column: 'auti_type_fk'

        tippProperty column: 'auti_tipp_property'
        kbartProperty column: 'auti_kbart_property'
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

    static belongsTo = [autoUpdatePackageInfo: AutoUpdatePackageInfo,
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
        return "Auto Update Title Info";
    }

    @Transient
    public String getDomainName() {
        return "Auto Update Title Info"
    }
}
