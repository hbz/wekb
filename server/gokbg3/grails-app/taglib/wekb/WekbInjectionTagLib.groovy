package wekb

import asset.pipeline.grails.AssetsTagLib
import de.wekb.helper.ServerUtils
import org.grails.io.support.GrailsResourceUtils

class WekbInjectionTagLib {
    static namespace = 'wekb'


    def serviceInjection = { attrs, body ->

        g.set( var:'accessService',             bean:'accessService' )
        g.set( var:'dateFormatService',         bean:'dateFormatService' )
        g.set( var: 'springSecurityService',     bean: 'springSecurityService')
    }

    def serverlabel = {attrs, body ->
        switch (attrs.server) {
            case ServerUtils.SERVER_DEV:
                g.set( var:'serverLabel', value: 'wekb-dev' )
                break
            case ServerUtils.SERVER_QA:
                g.set( var:'serverLabel', value: 'wekb-qa' )
                break
            case ServerUtils.SERVER_LOCAL:
                g.set( var:'serverLabel', value: 'wekb-local' )
                break
            default:
                g.set( var:'serverLabel', value: '' )
                break
        }
    }
    def script = { attrs, body ->


        Map<String, Object> map = [:]

        if (ServerUtils.getCurrentServer() != ServerUtils.SERVER_PROD) {
            if (attrs.file) {
                map = [file: GrailsResourceUtils.getPathFromBaseDir(attrs.file)]
            }
            else {
                map = [uri: request.getRequestURI()]
            }
        }
        asset.script(map, body())
    }

}
