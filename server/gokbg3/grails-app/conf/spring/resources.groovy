import com.k_int.UserPasswordEncoderListener
import de.wekb.custom.CustomMigrationCallbacks
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy

beans = {
    userPasswordEncoderListener(UserPasswordEncoderListener)

    localeResolver(SessionLocaleResolver) {
        defaultLocale= new Locale('en')
    }

    migrationCallbacks( CustomMigrationCallbacks ) {
        grailsApplication = ref('grailsApplication')
    }

    // [ user counter ..
    sessionRegistry( SessionRegistryImpl )

    registerSessionAuthenticationStrategy( RegisterSessionAuthenticationStrategy, ref('sessionRegistry') )

    sessionFixationProtectionStrategy( SessionFixationProtectionStrategy )

    concurrentSessionControlAuthenticationStrategy( ConcurrentSessionControlAuthenticationStrategy, ref('sessionRegistry') ){
        maximumSessions = -1
        // exceptionIfMaximumExceeded = true
    }

    sessionAuthenticationStrategy( CompositeSessionAuthenticationStrategy, [
            ref('concurrentSessionControlAuthenticationStrategy'),
            ref('sessionFixationProtectionStrategy'),
            ref('registerSessionAuthenticationStrategy')
    ])
    // .. ]

}
