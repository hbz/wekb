package wekb

class WekbInjectionTagLib {
    static namespace = 'wekb'


    def serviceInjection = { attrs, body ->

        g.set( var:'accessService',             bean:'accessService' )
        g.set( var:'dateFormatService',             bean:'dateFormatService' )
    }

}
