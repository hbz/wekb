import com.k_int.UserPasswordEncoderListener
import de.wekb.custom.CustomMigrationCallbacks
import org.springframework.web.servlet.i18n.SessionLocaleResolver

beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)

    localeResolver(SessionLocaleResolver) {
        defaultLocale= new Locale('en')
    }

    migrationCallbacks( CustomMigrationCallbacks ) {
        grailsApplication = ref('grailsApplication')
    }

}
