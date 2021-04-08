package de.wekb.helper

import grails.core.GrailsApplication
import grails.util.Environment

class ServerUtils {

    static final SERVER_LOCAL = 'SERVER_LOCAL'
    static final SERVER_DEV   = 'SERVER_DEV'
    static final SERVER_QA    = 'SERVER_QA'
    static final SERVER_PROD  = 'SERVER_PROD'

    static GrailsApplication grailsApplication

    static String getCurrentServer() {

        if (! Environment.isDevelopmentMode()) {

            switch (grailsApplication.config.systemId) {
                case 'we:kb-Dev':
                    return SERVER_DEV
                    break
                case 'we:kb-Qa':
                    return SERVER_QA
                    break
                case 'we:kb-Prod':
                    return SERVER_PROD
                    break
            }
        }

        return SERVER_LOCAL
    }
}
