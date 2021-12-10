package wekb

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import org.gokb.cred.KBComponent
import org.gokb.cred.RefdataValue

class KBComponentLanguage {

    @RefdataAnnotation(cat = RCConstants.KBCOMPONENT_LANGUAGE)
    RefdataValue language

    Date dateCreated
    Date lastUpdated

    static belongsTo = [
            kbcomponent: KBComponent
    ]

    static constraints = {
        kbcomponent (nullable: true)
        dateCreated (nullable: true)
        lastUpdated (nullable: true)
    }

    static mapping = {
        id                    column: 'kbc_lang_id'
        version               column: 'kbc_lang_version'
        language              column: 'kbc_lang_rv_fk' , index: 'kbc_lang_language_idx'
        kbcomponent           column: 'kbc_lang_kbc_fk', index: 'kbc_lang_kbc_idx'
        dateCreated           column: 'kbc_lang_date_created'
        lastUpdated           column: 'kbc_lang_last_updated'
    }
}
