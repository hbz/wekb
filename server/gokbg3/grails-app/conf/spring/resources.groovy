import com.k_int.UserPasswordEncoderListener
import org.grails.spring.context.support.PluginAwareResourceBundleMessageSource;
import org.springframework.web.servlet.i18n.SessionLocaleResolver

beans = {
  userPasswordEncoderListener(UserPasswordEncoderListener)


beans = {
    localeResolver(SessionLocaleResolver) {
        defaultLocale= new Locale('en')
    }
}

}
