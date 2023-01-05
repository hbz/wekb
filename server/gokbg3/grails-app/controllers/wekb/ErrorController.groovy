package wekb

import grails.converters.JSON
import grails.util.Environment
import groovy.util.logging.*
import org.grails.exceptions.ExceptionUtils

@Slf4j
class ErrorController {

    def serverError() {
        log.debug("serverError-> ${request.forwardURI} -> params: " + params)
        def resp = [code: 500, message: 'Server Error']
        def exception = request.getAttribute('exception') ?: request.getAttribute('javax.servlet.error.exception')

        if (exception && Environment.current == Environment.DEVELOPMENT) {
            resp.exception = exception
        }

        if (exception) {

            Throwable excep = (Throwable) exception
            Throwable root = ExceptionUtils.getRootCause(excep)

            String nl = " %0D%0A"

            resp.mailString =
                    "mailto:laser@hbz-nrw.de?subject=Error report - " +
                            "&body=Your error description (please provide): " + nl + nl +
                            "URI: " + request.forwardURI + nl +
                            "Date/Time: " + new Date() + nl +
                            "Class: " + (root?.class?.name ?: exception.class.name) + nl

            if (excep.message) {
                resp.mailString += "Message: " + excep.message + nl
            }
            if (root?.message != excep.message) {
                resp.mailString += "Cause: " + root.message + nl
            }
        }

        withFormat {
            html {
                render view: 'error', model: resp
            }
            json {
                response.setStatus(500)
                render resp as JSON
            }
        }
    }

    def wrongMethod() {
        log.debug("wrongMethod -> ${request.forwardURI} -> params: " + params)
        def resp = [code: 405, message: 'Method not allowed']

        withFormat {
            html {
                render view: 'error', model: resp
            }
            json {
                response.setStatus(405)
                render resp as JSON
            }
        }
    }

    def notFound() {
        log.debug("notFound -> ${request.forwardURI} -> params: " + params)
        def resp = [code: 404, message: 'Not Found']

        withFormat {
            html {
                render view: 'notFound', model: resp
            }
            json {
                response.setStatus(404)
                render resp as JSON
            }
        }
    }

    def forbidden() {
        log.debug("forbidden -> ${request.forwardURI} -> params: " + params)
        def resp = [code: 403, message: 'Forbidden']

        withFormat {
            html {
                render view: 'forbidden', model: resp
            }
            json {
                response.setStatus(403)
                render resp as JSON
            }
        }
    }

    def unauthorized() {
        log.debug("unauthorized -> ${request.forwardURI} -> params: " + params)
        def resp = [code: 401, message: 'Unauthorized']

        withFormat {
            html {
                render view: 'forbidden', model: resp
            }
            json {
                response.setStatus(401)
                render resp as JSON
            }
        }
    }

    def badRequest() {
        log.debug("badRequest -> ${request.forwardURI} -> params: " + params)
        def resp = [code: 400, message: 'Bad Request']

        def exception = request.getAttribute('exception') ?: request.getAttribute('javax.servlet.error.exception')

        if (exception && Environment.current == Environment.DEVELOPMENT) {
            resp.exception = exception
        }

        if (exception) {

            Throwable excep = (Throwable) exception
            Throwable root = ExceptionUtils.getRootCause(excep)

            String nl = " %0D%0A"

            resp.mailString =
                    "mailto:laser@hbz-nrw.de?subject=Error report - " +
                            "&body=Your error description (please provide): " + nl + nl +
                            "URI: " + request.forwardURI + nl +
                            "Date/Time: " + new Date() + nl +
                            "Class: " + (root?.class?.name ?: exception.class.name) + nl

            if (excep.message) {
                resp.mailString += "Message: " + excep.message + nl
            }
            if (root?.message != excep.message) {
                resp.mailString += "Cause: " + root.message + nl
            }
        }

        withFormat {
            html {
                render view: 'error', model: resp
            }
            json {
                response.setStatus(500)
                render resp as JSON
            }
        }
    }
}