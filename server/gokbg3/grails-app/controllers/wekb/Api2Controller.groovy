package wekb

import de.wekb.helper.RCConstants
import grails.converters.JSON
import groovy.util.logging.Slf4j
import org.gokb.cred.CuratoryGroup
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.Platform
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.gokb.cred.User
import org.springframework.security.access.annotation.Secured

import java.security.SecureRandom

class Api2Controller {
    def ESSearchService
    def genericOIDService

    SecureRandom rand = new SecureRandom()

    /**
     * Check if the api is up. Just return true.
     */
    def isUp() {
        apiReturn(["isUp": true])
    }

    // Internal API return object that ensures consistent formatting of API return objects
    private def apiReturn = { result, String message = "", String status = (result instanceof Throwable) ? "error" : "success" ->

        // If the status is error then we should log an entry.
        if (status == 'error') {

            // Generate 6bytes of random data to be base64 encoded which can be returned to the user to help with tracking issues in the logs.
            byte[] randomBytes = new byte[6]
            rand.nextBytes(randomBytes)
            def ticket = Base64.encodeBase64String(randomBytes);

            // Let's see if we have a throwable.
            if (result && result instanceof Throwable) {

                // Log the error with the stack...
                log.error("[[${ticket}]] - ${message == "" ? result.getLocalizedMessage() : message}", result)
            } else {
                log.error("[[${ticket}]] - ${message == "" ? 'An error occured, but no message or exception was supplied. Check preceding log entries.' : message}")
            }

            // Ensure we have something to send back to the user.
            if (message == "") {
                message = "An unknow error occurred."
            } else {

                // We should now send the message along with the ticket.
                message = "${message}".replaceFirst("\\.\\s*\$", ". The error has been logged with the reference '${ticket}'")
            }
        }

        def data = [
                code   : (status),
                result : (result),
                message: (message),
        ]

        def json = data as JSON
        //  log.debug (json)
        render json
        //    render (text: "${params.callback}(${json})", contentType: "application/javascript", encoding: "UTF-8")
    }

    def index() {
    }


    def namespaces() {

        def result = []
        def all_ns = null

        if (params.category && params.category?.trim().size() > 0) {
            all_ns = IdentifierNamespace.findAllByFamily(params.category)
        } else {
            all_ns = IdentifierNamespace.findAll()
        }

        all_ns.each { ns ->
            result.add([value: ns.value, namespaceName: ns.name, category: ns.family ?: "", id: ns.id])
        }

        apiReturn(result)
    }

    def groups() {

        def result = []

        CuratoryGroup.list().each {
            result << [
                    'id'    : it.id,
                    'name'  : it.name,
                    'status': it.status?.value ?: null,
                    'uuid'  : it.uuid
            ]
        }

        apiReturn(result)
    }

    /**
     * suggest : Get a list of autocomplete suggestions from ES
     *
     * @param max : Define result size
     * @param offset : Define offset
     * @param from : Define offset
     * @param q : Search term
     * @param componentType : Restrict search to specific component type (Package, Org, Platform, TIPP)
     * @param role : Filter by Org role (only in context of componentType=Org)
     * @return JSON Object
     * */

    def suggest() {
        log.info("API Call: suggest - " + params)
        def result = [:]
        def searchParams = params

        try {

            if (params.q?.length() > 0) {
                searchParams.suggest = params.q
                searchParams.remove("q")

                if (!searchParams.mapRecords) {
                    searchParams.skipDomainMapping = true
                } else {
                    searchParams.remove("mapRecords")
                }

                result = ESSearchService.find(searchParams)
            } else {
                result.errors = ['fatal': "No query parameter 'q=' provided"]
                result.result = "ERROR"
            }

        } finally {
            if (result.errors) {
                response.setStatus(400)
            }
        }

        render result as JSON
    }

    /**
     * find : Query the Elasticsearch index via ESSearchService
     **/
    def find() {
        log.info("API Call: find - " + params)
        def result = [:]
        def searchParams = params

        if (!searchParams.mapRecords) {
            searchParams.skipDomainMapping = true
        } else {
            searchParams.remove("mapRecords")
        }

        try {
            result = ESSearchService.find(searchParams)
        }
        finally {
            if (result.errors) {
                response.setStatus(400)
            }
        }
        render result as JSON
    }


    /**
     * scroll : Deliver huge amounts of Elasticsearch data
     **/
    def scroll() {
        log.info("API Call: scroll - " + params)
        def result = [:]
        try {
            result = ESSearchService.scroll(params)
        }
        catch (Exception e) {
            result.result = "ERROR"
            result.message = e.message
            result.cause = e.cause
            log.error("Could not process scroll request. Exception was: ${e.message}")
            response.setStatus(400)
        }
        render result as JSON
    }

    def sushiSources() {
        Map<String, Object> result = [:]
        RefdataValue yes = RefdataCategory.lookup(RCConstants.YN, 'Yes')
        //Set<Platform> counter4Platforms = Platform.findAllByCounterR4SushiApiSupportedAndCounterR5SushiApiSupportedNotEqual(yes, yes).toSet(), counter5Platforms = Platform.findAllByCounterR5SushiApiSupported(yes).toSet()
        Set counter4Platforms = Platform.executeQuery("select plat.uuid, plat.counterR4SushiServerUrl, plat.statisticsUpdate.value from Platform plat where plat.counterR4SushiApiSupported = :r4support and plat.counterR5SushiApiSupported != :r5support and plat.counterR4SushiServerUrl is not null", [r4support: yes, r5support: yes]).toSet()
        Set counter5Platforms = Platform.executeQuery("select plat.uuid, plat.counterR5SushiServerUrl, plat.statisticsUpdate.value from Platform plat where plat.counterR5SushiApiSupported = :r5support and plat.counterR5SushiServerUrl is not null", [r5support: yes]).toSet()
        result.counter4ApiSources = counter4Platforms.size() > 0 ? counter4Platforms : []
        result.counter5ApiSources = counter5Platforms.size() > 0 ? counter5Platforms : []
        render result as JSON
    }


}
