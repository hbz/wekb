package org.gokb

import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import grails.core.GrailsApplication
import grails.plugins.mail.MailService
import org.gokb.cred.*
import org.hibernate.ScrollMode
import org.hibernate.ScrollableResults
import wekb.ExportService
import wekb.SearchService

import javax.servlet.ServletOutputStream


class PublicController {

  def genericOIDService
  def ESSearchService
  def dateFormatService
  ExportService exportService
  MailService mailService
  SearchService searchService

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
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def tippContent() {
    log.debug("tippContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def identifierContent() {
    log.debug("identifierContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def orgContent() {
    log.debug("orgContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def sourceContent() {
    log.debug("sourceContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
  }

  def platformContent() {
    log.debug("tippContent::${params}")
    redirect(controller: 'resource', action: 'show', id: params.id)
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
    params.remove('search')

    result
  }

  def kbart() {

    Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = Package.findByUuid(params.id)
    }

    if(!pkg){
      response.sendError(404)
      return
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "kbart_${pkg.name}_${export_date}.txt"

    try {

      response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

      def out = response.outputStream

      exportService.exportOriginalKBART(out, pkg)

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }

  def packageTSVExport() {

    Package pkg = genericOIDService.resolveOID(params.id)

    if(!pkg){
      pkg = Package.findByUuid(params.id)
    }

    if(!pkg){
      response.sendError(404)
      return
    }

    String export_date = dateFormatService.formatDate(new Date());

    String filename = "wekb_package_${pkg.name.toLowerCase()}_${export_date}.tsv"

    try {
      response.setContentType('text/tab-separated-values');
      response.setHeader("Content-disposition", "attachment; filename=\"${filename}\"")

      ServletOutputStream out = response.outputStream

      Map<String,List> export = exportService.exportPackageTippsAsTSVNew(pkg)

      out.withWriter { writer ->
        writer.write("we:kb Export : Provider (${pkg.provider?.name}) : Package (${pkg.name}) : ${export_date}\n");
        writer.write(exportService.generateSeparatorTableString(export.titleRow, export.rows, '\t'))
      }
      out.flush()
      out.close()

    }
    catch ( Exception e ) {
      log.error("Problem with export",e);
    }
  }
}
