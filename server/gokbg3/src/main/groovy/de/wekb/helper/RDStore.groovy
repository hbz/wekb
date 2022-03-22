package de.wekb.helper

import org.gokb.cred.Combo
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil

import javax.persistence.Transient

//@CompileStatic
class RDStore {

    public static final KBC_STATUS_DELETED    = getRefdataValue('Deleted', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_CURRENT    = getRefdataValue('Current', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_EXPECTED    = getRefdataValue('Expected', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_RETIRED    = getRefdataValue('Retired', RCConstants.KBCOMPONENT_STATUS)


    public static final COMBO_TYPE_KB_IDS = getRefdataValue('KBComponent.Ids', RCConstants.COMBO_TYPE)
    public static final COMBO_TYPE_PLT_HOSTEDTIPPS = getRefdataValue('Platform.HostedTipps', RCConstants.COMBO_TYPE)

    public static final COMBO_STATUS_ACTIVE = getRefdataValue(Combo.STATUS_ACTIVE, RCConstants.COMBO_STATUS)
    public static final COMBO_STATUS_DELETED = getRefdataValue(Combo.STATUS_DELETED, RCConstants.COMBO_STATUS)

    public static final CURRENCY_EUR = RefdataCategory.lookup(RCConstants.CURRENCY, 'EUR')
    public static final CURRENCY_USD = RefdataCategory.lookup(RCConstants.CURRENCY, 'USD')
    public static final CURRENCY_GBP = RefdataCategory.lookup(RCConstants.CURRENCY, 'GBP')

    public static final PRICE_TYPE_LIST = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'list')
    public static final PRICE_TYPE_OA_APC = RefdataCategory.lookup(RCConstants.PRICE_TYPE, 'open access apc')

    public static final TIPP_PUBLIC_TYPE_SERIAL = RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Serial")
    public static final TIPP_PUBLIC_TYPE_MONO = RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Monograph")
    public static final TIPP_PUBLIC_TYPE_DB = RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Database")
    public static final TIPP_PUBLIC_TYPE_OTHER = RefdataCategory.lookup(RCConstants.TIPP_PUBLICATION_TYPE, "Other")





    static RefdataValue getRefdataValue(String value, String category) {
        RefdataValue result = RefdataValue.getByValueAndCategory(value, category)

        if (! result) {
            println "WARNING: No RefdataValue found by RDStore for value:'${value}', category:'${category}'"
        }
        (RefdataValue) GrailsHibernateUtil.unwrapIfProxy( result)
    }
}
