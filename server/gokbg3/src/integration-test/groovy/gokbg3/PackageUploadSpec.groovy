package gokbg3

import grails.core.GrailsApplication
import grails.plugins.rest.client.RestBuilder
import grails.plugins.rest.client.RestResponse
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource
import org.springframework.beans.factory.annotation.*
import org.springframework.web.context.WebApplicationContext
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Shared


@Integration
@Rollback
@Ignore
class PackageUploadSpec extends Specification {


    GrailsApplication grailsApplication

    @Shared
    RestBuilder rest = new RestBuilder()

    @Autowired
    WebApplicationContext ctx

    def setup() {
    }

    def cleanup() {
    }

    
    void "testApiCall"() {
      // This is a place-holder for API call tests...
      true
    }
}
