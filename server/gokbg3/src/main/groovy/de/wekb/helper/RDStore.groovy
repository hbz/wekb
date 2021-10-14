package de.wekb.helper

import org.gokb.cred.RefdataValue
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil

//@CompileStatic
class RDStore {

    public static final KBC_STATUS_DELETED    = getRefdataValue('Deleted', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_CURRENT    = getRefdataValue('Current', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_EXPECTED    = getRefdataValue('Expected', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_RETIRED    = getRefdataValue('Retired', RCConstants.KBCOMPONENT_STATUS)

    static RefdataValue getRefdataValue(String value, String category) {
        RefdataValue result = RefdataValue.getByValueAndCategory(value, category)

        if (! result) {
            println "WARNING: No RefdataValue found by RDStore for value:'${value}', category:'${category}'"
        }
        (RefdataValue) GrailsHibernateUtil.unwrapIfProxy( result)
    }
}
