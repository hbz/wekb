package org.gokb

import de.wekb.helper.RCConstants
import grails.converters.*
import grails.gorm.transactions.*
import org.springframework.security.acls.model.NotFoundException
import org.springframework.security.access.annotation.Secured;
import org.gokb.cred.*
import org.springframework.web.multipart.MultipartHttpServletRequest
import com.k_int.ConcurrencyManagerService;
import com.k_int.ConcurrencyManagerService.Job
import java.security.MessageDigest
import grails.converters.JSON
import grails.core.GrailsClass
import groovyx.net.http.URIBuilder

import org.grails.datastore.mapping.model.*
import org.grails.datastore.mapping.model.types.*

import org.hibernate.ScrollMode
import org.hibernate.ScrollableResults
import org.hibernate.type.*
import org.hibernate.Hibernate

@Transactional(readOnly = true)
class PackagesController {

  def dateFormatService
  def genericOIDService
  def springSecurityService
  def concurrencyManagerService
  def packageService

  public static String TIPPS_QRY = 'select tipp from TitleInstancePackagePlatform as tipp, Combo as c where c.fromComponent.id=? and c.toComponent=tipp  and c.type.value = ? order by tipp.id';

  def packageContent() {
    log.debug("packageContent::${params}")
    def result = [:]
    if (params.id) {
      def pkg_id_components = params.id.split(':');
      def pkg_id = pkg_id_components[1]
      result.pkgData = Package.executeQuery('select p.id, p.name from Package as p where p.id=?', [Long.parseLong(pkg_id)])
      result.pkgId = result.pkgData[0][0]
      result.pkgName = result.pkgData[0][1]
      log.debug("Tipp qry name: ${result.pkgName}");
      result.tipps = TitleInstancePackagePlatform.executeQuery(TIPPS_QRY, [result.pkgId, 'Package.Tipps'], [offset: 0, max: 10])
      log.debug("Tipp qry done ${result.tipps?.size()}");
    }
    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def compareContents() {
    log.debug("compareContents")
    def result = [params: params, result: 'OK']
    def user = springSecurityService.currentUser

    if (params.one && params.two) {
      def date = params.date ? dateFormatService.parseDate(params.date)  : null
      def full = params.full ? params.boolean('full') : false
      def listOne = params.list('one')
      def listTwo = params.list('two')

      if (params.wait) {
        result = packageService.compareLists(listOne, listTwo, full, date)
      }
      else {
        def background_job = concurrencyManagerService.createJob { Job job ->
          packageService.compareLists(listOne, listTwo, full, date, job)
        }.startOrQueue()

        background_job.description = "Package comparison"
        background_job.type = RefdataCategory.lookup(RCConstants.JOB_TYPE, 'PackageComparison')
        background_job.ownerId = user.id
        result.job_id = background_job.uuid
      }
    }
    else {
      log.debug("Missing info..")
    }

    render result as JSON
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def connectedRRs() {
    log.debug("connectedRRs::${params}")
    def result = [:]
    if (params.id) {
      def pkg = Package.get(params.id)
      def open_only = true
      def restr = false
      result.restriction = 'open'

      if (params.getAll) {
        open_only = false
        result.restriction = 'all'
      }
      if (params.restriction == 'Current') {
        restr = true
      }

      result.reviewRequests = pkg.getReviews(open_only, restr)
    }
    withFormat {
      html { render template: 'revreqtabpkg', model: [d: result], contentType: 'text/html' }
      json { render result as JSON }
    }
  }

  def copyUploadedFile(inputfile, deposit_token) {
    def baseUploadDir = grailsApplication.config.project_dir ?: '.'
    log.debug("copyUploadedFile...");
    def sub1 = deposit_token.substring(0, 2);
    def sub2 = deposit_token.substring(2, 4);
    validateUploadDir("${baseUploadDir}");
    validateUploadDir("${baseUploadDir}/${sub1}");
    validateUploadDir("${baseUploadDir}/${sub1}/${sub2}");
    def temp_file_name = "${baseUploadDir}/${sub1}/${sub2}/${deposit_token}";
    def temp_file = new File(temp_file_name);

    // Copy the upload file to a temporary space
    inputfile.transferTo(temp_file);

    temp_file
  }

  private def validateUploadDir(path) {
    File f = new File(path);
    if (!f.exists()) {
      log.debug("Creating upload directory path")
      f.mkdirs();
    }
  }

  def analyse(temp_file) {

    def result = [:]
    result.filesize = 0;

    log.debug("analyze...");

    // Create a checksum for the file..
    MessageDigest md5_digest = MessageDigest.getInstance("MD5");
    InputStream md5_is = new FileInputStream(temp_file);
    byte[] md5_buffer = new byte[8192];
    int md5_read = 0;
    while ((md5_read = md5_is.read(md5_buffer)) >= 0) {
      md5_digest.update(md5_buffer, 0, md5_read);
      result.filesize += md5_read
    }
    md5_is.close();
    byte[] md5sum = md5_digest.digest();
    result.md5sumHex = new BigInteger(1, md5sum).toString(16);

    log.debug("MD5 is ${result.md5sumHex}");
    result
  }

  /*@Transactional(readOnly = true)
  def kbart() {
    if (request.method == "GET") {
      if (params.id == "all") {
        Package.all.each { pack ->
          packageService.createKbartExport(pack, response)
        }
        return response
      }
      def pkg = Package.findByUuid(params.id) ?: genericOIDService.resolveOID(params.id)
      if (pkg)
        packageService.sendFile(pkg, PackageService.ExportType.KBART, response)
      else
        log.error("Cant find package with ID ${params.id}")
    }
    else if (request.method == "POST") {
      def packs = []
      request.JSON.data.ids.each { id ->
        def pkg = Package.findByUuid(id) ?: genericOIDService.resolveOID(id)
        if (pkg)
          packs << pkg
      }
      packageService.sendZip(packs, PackageService.ExportType.KBART, response)
    }
  }*/

  /*@Transactional(readOnly = true)
  def packageTSVExport() {
    if (request.method == "GET") {
      if (params.id == "all") {
        Package.all.each { pack ->
          packageService.createTsvExport(pack, response)
        }
        return response
      }
      def pkg = Package.findByUuid(params.id) ?: genericOIDService.resolveOID(params.id)
      if (pkg)
        packageService.sendFile(pkg, PackageService.ExportType.TSV, response)
      else
        log.error("Cant find package with ID ${params.id}")
    } else if (request.method == "POST") {
      def packs = []
      request.JSON.data.ids.each { id ->
        def pkg = Package.findByUuid(id) ?: genericOIDService.resolveOID(id)
        if (pkg)
          packs << pkg
      }
      packageService.sendZip(packs, PackageService.ExportType.TSV, response)
    }
  }*/
}
