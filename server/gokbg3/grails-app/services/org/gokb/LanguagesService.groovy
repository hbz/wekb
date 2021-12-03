package org.gokb

import de.wekb.helper.RCConstants
import grails.gorm.transactions.Transactional
import grails.util.Holders
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.RESTClient
import org.gokb.cred.KBComponent
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue

import static groovyx.net.http.Method.GET

@Transactional
class LanguagesService{

  static Map languages = [:]

  /**
   * Fills the RefdataCategory given by {@link RCConstants.KBCOMPONENT_LANGUAGE} with a list of all language codes
   * provided in the ISO-639-2 map specified by the referenced languages microservice. See
   * https://github.com/hbz/languages-microservice#get-the-whole-iso-639-2-list for details.
   */
  static void initialize(){
    String uriString = "${Holders.grailsApplication.config.gokb.languagesUrl}/api/listIso639two"
    URI microserviceUrl = new URI(uriString)
    def httpBuilder = new HTTPBuilder(microserviceUrl)
    httpBuilder.request(GET) { request ->
      response.success = { statusResp, responseData ->
        log.debug("GET ${uriString} => success")
        languages = responseData
      }
      response.failure = { statusResp, statusData ->
        log.debug("GET ${uriString} => failure => will be showing languages as shortcodes")
        return
      }
    }
    for (def entry in languages){

      Map<String, Object> map = [
              token   : entry.key,
              rdc     : RCConstants.KBCOMPONENT_LANGUAGE,
              value_de: entry.value.ger ?: null,
              value_en: entry.value.eng ?: null,
              hardData: true
      ]

      RefdataValue.construct(map)
    }
  }

}
