package org.gokb

import de.wekb.helper.RCConstants
import grails.core.GrailsApplication
import grails.plugins.mail.MailService
import org.gokb.cred.*
import org.hibernate.ScrollMode
import org.hibernate.ScrollableResults
import wekb.ExportService


class PublicController {

  def genericOIDService
  def ESSearchService
  def dateFormatService
  def sessionFactory
  def classExaminationService
  ExportService exportService
  MailService mailService

  public static String TIPPS_QRY = 'from TitleInstancePackagePlatform as tipp, Combo as c where c.fromComponent.id=? and c.toComponent=tipp and c.type = ? and tipp.status = ?';

  def wcagPlainEnglish() {
    log.debug("wcagPlainEnglish::${params}")
    def result = [:]
    //println(params)
    result
  }

  def sendFeedbackForm() {
    def result = [:]
    try {

      mailService.sendMail {
        to 'barrierefreiheitsbelange@hbz-nrw.de'
        from 'laser@hbz-nrw.de'
        subject grailsApplication.config.systemId + ' - Feedback-Mechanismus Barrierefreiheit'
        body (view: '/mailTemplate/text/wcagFeedback', model: [name:params.name, email:params.eMail, url:params.url, comment:params.comment])

      }
    }
    catch (Exception e) {
      println "Unable to perform email due to exception ${e.message}"
    }
    result
  }

  def wcagFeedbackForm() {
    log.debug("wcagFeedbackForm::${params}")
    def result = [:]
    //println(params)
    result
  }

  def packageContent() {
    log.debug("packageContent::${params}")
    def result = [:]
    if ( params.id ) {
      def pkg_id_components = params.id.split(':');
      
      if ( pkg_id_components?.size() == 2 ) {
        result.pkg = Package.get(Long.parseLong(pkg_id_components[1]));
      }
      else {
        result.pkg = Package.findByUuid(params.id)
      }
      
      if (result.pkg) {
        def tipp_combo_rdv = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE,'Package.Tipps')
        def status_current = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS,'Current')
        
        result.pkgId = result.pkg.id
        result.pkgName = result.pkg.name
        log.debug("Tipp qry name: ${result.pkgName}")

        result.refdata_properties = classExaminationService.getRefdataPropertyNames(result.pkg.class.name)

        Map newParams = [:]
        params.sort = params.sort ? "${params.sort}" : 'lower(tipp.name)'
        params.order = params.order ? "${params.order}" : 'asc'

        if (params.newMax) {
          session.setAttribute("newMax", params.newMax)
          params.remove(params.newMax)
          params.offset = 0
        }
        params.offset = params.offset ?: 0
        params.max = session.getAttribute("newMax") ?: 10

        result.titleCount = TitleInstancePackagePlatform.executeQuery('select count(tipp.id) '+TIPPS_QRY,[result.pkgId, tipp_combo_rdv, status_current])[0]
        result.tipps = TitleInstancePackagePlatform.executeQuery('select tipp '+TIPPS_QRY,[result.pkgId, tipp_combo_rdv, status_current], params)
        log.debug("Tipp qry done ${result.tipps?.size()}")

      }else {
        flash.error = "Package not found"
      }
    }
    result
  }

  def tippContent() {
    log.debug("tippContent::${params}")
    def result = [:]
    if ( params.id ) {
      def tipp_id_components = params.id.split(':');

      if ( tipp_id_components?.size() == 2 ) {
        result.tipp = TitleInstancePackagePlatform.get(Long.parseLong(tipp_id_components[1]));
      }
      else {
        result.tipp = TitleInstancePackagePlatform.findByUuid(params.id)
      }

      if (!result.tipp) {
        flash.error = "Tipp not found"
      }
    }
    result
  }

  def platformContent() {
    log.debug("tippContent::${params}")
    def result = [:]
    if ( params.id ) {
      def platform_id_components = params.id.split(':');

      if ( platform_id_components?.size() == 2 ) {
        result.platform = Platform.get(Long.parseLong(platform_id_components[1]));
      }
      else {
        result.platform = Platform.findByUuid(params.id)
      }

      if (!result.platform) {
        flash.error = "Platform not found"
      }
    }
    result
  }


  def index() {
    log.debug("PublicController::index ${params}");
    def result = [:]

    def mutableParams = new HashMap(params)

    if (mutableParams.newMax) {
      session.setAttribute("newMax", mutableParams.newMax)
    }

    if (mutableParams.max == null && !session.getAttribute("newMax")){
      mutableParams.max = 10
    }
    else {
      mutableParams.max = session.getAttribute("newMax") ? Integer.parseInt(session.getAttribute("newMax")) : Integer.parseInt(mutableParams.max)
    }

    if (mutableParams.offset == null || mutableParams.newMax ) {
      mutableParams.offset = 0
    }
    else {
      mutableParams.offset = Integer.parseInt(mutableParams.offset)
    }

    if (!mutableParams.sort){
      mutableParams.sort='sortname'
      mutableParams.order = 'asc'
    }

    mutableParams.componentType = "Package" // Tells ESSearchService what to look for

    if((mutableParams.q == null ) || (mutableParams.q == '') ) {
      mutableParams.q = '*'
    }
    // params.remove('q');
    // params.isPublic="Yes"

    if(mutableParams.lastUpdated){
      mutableParams.lastModified ="[${params.lastUpdated} TO 2100]"
    }

    if(mutableParams.search.equals('yes')){
      //when searching make sure results start from first page
      mutableParams.offset = 0
      mutableParams.search = null
    }

    result =  ESSearchService.search(mutableParams)


    def query_params = [forbiddenStatus : RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED)]

    List providerRoles = [RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Content Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Platform Provider'), RefdataCategory.lookup(RCConstants.ORG_ROLE, 'Publisher')]

    def query_params2 = [forbiddenStatus : RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED), roles: providerRoles]

    result.componentsOfStatistic = ["Provider", "Package", "Platform", "CuratoryGroup", "TitleInstancePackagePlatform"]

    result.countComponent = [:]
    result.componentsOfStatistic.each { component ->
      if(component == "Provider"){
        result.countComponent."${component.toLowerCase()}" = Org.executeQuery("select count(o.id) from Org as o join o.roles rdv where rdv in (:roles) and o.status != :forbiddenStatus", query_params2, [readOnly: true])[0]
      }else {
        def fetch_all = "select count(o.id) from ${component} as o where status != :forbiddenStatus"
        result.countComponent."${component.toLowerCase()}" = KBComponent.executeQuery(fetch_all.toString(), query_params, [readOnly: true])[0]
      }


    }

    params.max = mutableParams.max
    params.offset = mutableParams.offset
    params.remove('newMax')

    result
  }

  def kbart() {

    Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = KBComponent.findByUuid(params.id)
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "kbart_${pkg.name}_${export_date}.tsv"

    try {
      response.setContentType('text/tab-separated-values');
      response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

      def out = response.outputStream

      exportService.exportPackageTippsAsKBART(out, pkg)

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }

  def packageTSVExport() {

    Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = KBComponent.findByUuid(params.id)
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "wekb_package_${pkg.name.toLowerCase()}_${export_date}.tsv"

    try {
      response.setContentType('text/tab-separated-values');
      response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

      def out = response.outputStream

      exportService.exportPackageTippsAsTSV(out, pkg)

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }
}
