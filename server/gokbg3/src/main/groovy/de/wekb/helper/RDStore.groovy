package de.wekb.helper

import org.gokb.cred.Combo
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil

import javax.persistence.Transient

//@CompileStatic
class RDStore {

    public static final UPDATE_STATUS_SUCCESSFUL    = getRefdataValue('Successful', RCConstants.UPDATE_STATUS)
    public static final UPDATE_STATUS_FAILED    = getRefdataValue('Failed', RCConstants.UPDATE_STATUS)
    public static final UPDATE_STATUS_WARNING    = getRefdataValue('Warning', RCConstants.UPDATE_STATUS)

    public static final UPDATE_TYPE_NEW_TITLE    = getRefdataValue('New Title', RCConstants.UPDATE_TYPE)
    public static final UPDATE_TYPE_CHANGED_TITLE    = getRefdataValue('Changed Title', RCConstants.UPDATE_TYPE)
    public static final UPDATE_TYPE_REMOVED_TITLE    = getRefdataValue('Removed Title', RCConstants.UPDATE_TYPE)
    public static final UPDATE_TYPE_FAILED_TITLE    = getRefdataValue('Failed Title', RCConstants.UPDATE_TYPE)

    public static final KBC_STATUS_DELETED    = getRefdataValue('Deleted', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_CURRENT    = getRefdataValue('Current', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_EXPECTED    = getRefdataValue('Expected', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_RETIRED    = getRefdataValue('Retired', RCConstants.KBCOMPONENT_STATUS)
    public static final KBC_STATUS_REMOVED    = getRefdataValue('Removed', RCConstants.KBCOMPONENT_STATUS)


    public static final COMBO_TYPE_KB_IDS = getRefdataValue('KBComponent.Ids', RCConstants.COMBO_TYPE)

    public static final CONTACT_CONTENT_TYPE_EMAIL = getRefdataValue('E-Mail', RCConstants.CONTACT_CONTENT_TYPE)


    public static final COMBO_STATUS_ACTIVE = getRefdataValue(Combo.STATUS_ACTIVE, RCConstants.COMBO_STATUS)
    public static final COMBO_STATUS_DELETED = getRefdataValue(Combo.STATUS_DELETED, RCConstants.COMBO_STATUS)

    public static final CURRENCY_EUR = getRefdataValue('EUR', RCConstants.CURRENCY)
    public static final CURRENCY_USD = getRefdataValue('USD', RCConstants.CURRENCY)
    public static final CURRENCY_GBP = getRefdataValue('GBP', RCConstants.CURRENCY)

    public static final PRICE_TYPE_LIST = getRefdataValue('list', RCConstants.PRICE_TYPE)
    public static final PRICE_TYPE_OA_APC = getRefdataValue('open access apc', RCConstants.PRICE_TYPE)

    public static final TIPP_PUBLIC_TYPE_SERIAL = getRefdataValue('Serial', RCConstants.TIPP_PUBLICATION_TYPE)
    public static final TIPP_PUBLIC_TYPE_MONO = getRefdataValue('Monograph', RCConstants.TIPP_PUBLICATION_TYPE)
    public static final TIPP_PUBLIC_TYPE_DB = getRefdataValue('Database', RCConstants.TIPP_PUBLICATION_TYPE)
    public static final TIPP_PUBLIC_TYPE_OTHER = getRefdataValue('Other', RCConstants.TIPP_PUBLICATION_TYPE)

    public static final TIPP_ACCESS_TYPE_FREE = getRefdataValue('Free', RCConstants.TIPP_ACCESS_TYPE)
    public static final TIPP_ACCESS_TYPE_PAID = getRefdataValue('Paid', RCConstants.TIPP_ACCESS_TYPE)

    public static final IDENTIFIER_NAMESPACE_TARGET_TYPE_TIPP = getRefdataValue('TitleInstancePackagePlatform', RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)
    public static final IDENTIFIER_NAMESPACE_TARGET_TYPE_PACKAGE = getRefdataValue('Package', RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)



    public static final YN_YES              = getRefdataValue('Yes', RCConstants.YN)
    public static final YN_NO               = getRefdataValue('No', RCConstants.YN)



    static RefdataValue getRefdataValue(String value, String category) {
        RefdataValue result = RefdataValue.getByValueAndCategory(value, category)

        if (! result) {
            println "WARNING: No RefdataValue found by RDStore for value:'${value}', category:'${category}'"
        }
        (RefdataValue) GrailsHibernateUtil.unwrapIfProxy( result)
    }
}
