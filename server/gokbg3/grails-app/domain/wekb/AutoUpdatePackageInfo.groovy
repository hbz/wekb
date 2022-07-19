package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.Package
import org.gokb.cred.RefdataValue

class AutoUpdatePackageInfo {

    Package pkg

    String uuid

    String description

    Date startTime
    Date endTime

    Date dateCreated
    Date lastUpdated

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
    }

    static constraints = {
        endTime     (nullable:true)

    }

    static hasMany = [autoUpdateTippInfo: AutoUpdateTippInfo]


}
