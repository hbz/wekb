package wekb

import de.hbznrw.ygor.tools.UrlToolkit
import de.wekb.helper.RCConstants
import gokbg3.DateFormatService
import grails.gorm.transactions.Transactional
import org.apache.commons.io.FileUtils
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.util.CellRangeAddressList
import org.apache.poi.xssf.usermodel.XSSFDataValidation
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.Package
import org.gokb.cred.RefdataCategory
import org.gokb.cred.TIPPCoverageStatement
import org.gokb.cred.TitleInstancePackagePlatform

import java.nio.file.Files


@Transactional
class ExportService {

    DateFormatService dateFormatService

    def exportPackageTippsAsTSV(def outputStream, Package pkg) {

        def export_date = dateFormatService.formatDate(new Date())

        def sanitize = { it ? "${it}".trim() : "" }

        outputStream.withWriter { writer ->

            writer.write("we:kb Export : ${pkg.provider?.name} : ${pkg.name} : ${export_date}\n");

            writer.write('publication_title\t'+
                    'first_author\t'+
                    'first_editor\t'+
                    'publisher_name\t'+
                    'publication_type\t'+
                    'medium\t'+
                    'title_url\t'+
                    'print_identifier\t'+
                    'online_identifier\t'+
                    'title_id\t'+
                    'doi_identifier\t'+
                    'subject_area\t'+
                    'language\t'+
                    'access_type\t'+
                    'coverage_depth\t'+
                    'package_name\t'+
                    'package_id\t'+
                    'access_start_date\t'+
                    'access_end_date\t'+
                    'last_changed\t'+
                    'status\t'+
                    'listprice_eur\t'+
                    'listprice_usd\t'+
                    'listprice_gbp\t'+
                    'notes\t'+
                    'date_monograph_published_print\t'+
                    'date_monograph_published_online\t'+
                    'monograph_volume\t'+
                    'monograph_edition\t'+
                    'monograph_parent_collection_title\t'+
                    'parent_publication_title_id\t'+
                    'date_first_issue_online\t'+
                    'num_first_vol_online\t'+
                    'num_first_issue_online\t'+
                    'date_last_issue_online\t'+
                    'num_last_vol_online\t'+
                    'num_last_issue_online\t'+
                    'zdb_id\t'+
                    'ezb_id\t'+
                    'package_ezb_anchor\t'+
                    'oa_gold\t'+
                    'oa_hybrid\t'+
                    'oa_apc_eur\t'+
                    'oa_apc_usd\t'+
                    'oa_apc_gbp\t'+
                    'package_isil\t'+
                    'title_gokb_uuid\t'+
                    'package_gokb_uuid\t'+
                    'package_isci\t'+
                    'ill_indicator\t'+
                    'preceding_publication_title_id\t'+
                    'superseding_publication_title_id\t'+
                    'embargo_info\t'+
                    '\n'
            )

            def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
            def combo_pkg_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

            Map queryParams = [:]
            queryParams.p = pkg.id
            queryParams.sd = status_deleted
            queryParams.ct = combo_pkg_tipps

            List<TitleInstancePackagePlatform> tipps = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp, Combo as c where c.fromComponent.id=:p and c.toComponent=tipp and tipp.status != :sd and c.type = :ct order by tipp.name", queryParams, [readOnly: true])

            tipps.each { TitleInstancePackagePlatform tipp ->

                tipp.coverageStatements.each { TIPPCoverageStatement tippCoverageStatement ->
                    writer.write(sanitize(tipp.name) + '\t' +
                            sanitize(tipp.firstAuthor) + '\t' +
                            sanitize(tipp.firstEditor) + '\t' +
                            sanitize(tipp.publisherName) + '\t' + //publisher_name
                            sanitize(tipp.publicationType?.value) + '\t' +
                            sanitize(tipp.url) + '\t' +
                            sanitize(tipp.getIdentifierValue('ISSN')) + '\t' +
                            sanitize(tipp.getIdentifierValue('eISSN')) + '\t' +
                            sanitize(tipp.getTitleID()) + '\t' +
                            sanitize(tipp.getIdentifierValue('DOI')) + '\t' +
                            sanitize(tipp.subjectArea) + '\t' +
                            sanitize(tipp.languages?.value.join(',')) + '\t' +
                            sanitize(tipp.accessType?.value) + '\t' +
                            sanitize(tipp.coverageDepth?.value) + '\t' +
                            sanitize(tipp.pkg.name) + '\t' +
                            '\t' + //package_id
                            sanitize(tipp.accessStartDate) + '\t' +
                            sanitize(tipp.accessEndDate) + '\t' +
                            sanitize(tipp.lastChangedExternal) + '\t' +
                            sanitize(tipp.status?.value) + '\t' +
                            sanitize(tipp.getListPriceInEUR()) +'\t' + //listprice_eur
                            sanitize(tipp.getListPriceInUSD()) +'\t' + //listprice_usd
                            sanitize(tipp.getListPriceInGBP()) +'\t' + //listprice_gbp
                            sanitize(tipp.note) +'\t' +
                            sanitize(tipp.dateFirstInPrint) + '\t' +
                            sanitize(tipp.dateFirstOnline) + '\t' +
                            sanitize(tipp.volumeNumber) + '\t' +
                            sanitize(tipp.editionStatement) + '\t' +
                            sanitize(tipp.series) + '\t' +
                            sanitize(tippCoverageStatement.startDate) + '\t' + //date_first_issue_online
                            sanitize(tippCoverageStatement.startVolume) + '\t' + //num_first_vol_online
                            sanitize(tippCoverageStatement.startIssue) + '\t' + //num_first_issue_online
                            sanitize(tippCoverageStatement.endDate) + '\t' + //date_last_issue_online
                            sanitize(tippCoverageStatement.endVolume) + '\t' + //num_last_vol_online
                            sanitize(tippCoverageStatement.endIssue) + '\t' + //num_last_issue_online
                            sanitize(tipp.getIdentifierValue('zdb')) + '\t' +
                            sanitize(tipp.getIdentifierValue('ezb')) + '\t' +
                            '\t' + //package_ezb_anchor
                            '\t' + //oa_gold
                            '\t' + //oa_hybrid
                            sanitize(tipp.getOAAPCPriceInEUR()) +'\t' + //oa_apc_eur
                            sanitize(tipp.getOAAPCPriceInUSD()) +'\t' + //oa_apc_usd
                            sanitize(tipp.getOAAPCPriceInGBP()) +'\t' + //oa_apc_gbp
                            '\t' + //package_isil
                            sanitize(tipp.uuid) + '\t' +
                            sanitize(tipp.pkg.uuid) + '\t' +
                            '\t' + //package_isci
                            '\t' + //ill_indicator
                            sanitize(tipp.precedingPublicationTitleId) + '\t' +
                            sanitize(tipp.supersedingPublicationTitleId) + '\t' + //superseding_publication_title_id
                            sanitize(tippCoverageStatement.coverageDepth?.value) + '\t' + //embargo_info
                            '\n')
                }
            }

            writer.flush();
            writer.close();
        }
        outputStream.close()
    }

    public void exportPackageTippsAsKBART(def outputStream, Package pkg) {


        def sanitize = { it ? "${it}".trim() : "" }

        outputStream.withWriter { writer ->

            writer.write('publication_title\t'+
                    'print_identifier\t'+
                    'online_identifier\t'+
                    'date_first_issue_online\t'+
                    'num_first_vol_online\t'+
                    'num_first_issue_online\t'+
                    'date_last_issue_online\t'+
                    'num_last_vol_online\t'+
                    'num_last_issue_online\t'+
                    'title_url\t'+
                    'first_author\t'+
                    'title_id\t'+
                    'embargo_info\t'+
                    'coverage_depth\t'+
                    'notes\t'+
                    'publisher_name\t'+
                    'publication_type\t'+
                    'date_monograph_published_print\t'+
                    'date_monograph_published_online\t'+
                    'monograph_volume\t'+
                    'monograph_edition\t'+
                    'first_editor\t'+
                    'parent_publication_title_id\t'+
                    'preceding_publication_title_id\t'+
                    'access_type\t'+
                    '\n'
            )

            def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
            def combo_pkg_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

            Map queryParams = [:]
            queryParams.p = pkg.id
            queryParams.sd = status_deleted
            queryParams.ct = combo_pkg_tipps

            List<TitleInstancePackagePlatform> tipps = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp, Combo as c where c.fromComponent.id=:p and c.toComponent=tipp  and tipp.status <> :sd and c.type = :ct order by tipp.name", queryParams, [readOnly: true])

            tipps.each { TitleInstancePackagePlatform tipp ->

                tipp.coverageStatements.each { TIPPCoverageStatement tippCoverageStatement ->
                    writer.write(sanitize(tipp.name) + '\t' +
                            sanitize(tipp.getIdentifierValue('ISSN')) + '\t' +
                            sanitize(tipp.getIdentifierValue('eISSN')) + '\t' +
                            sanitize(tippCoverageStatement.startDate) + '\t' + //date_first_issue_online
                            sanitize(tippCoverageStatement.startVolume) + '\t' + //num_first_vol_online
                            sanitize(tippCoverageStatement.startIssue) + '\t' + //num_first_issue_online
                            sanitize(tippCoverageStatement.endDate) + '\t' + //date_last_issue_online
                            sanitize(tippCoverageStatement.endVolume) + '\t' + //num_last_vol_online
                            sanitize(tippCoverageStatement.endIssue) + '\t' + //num_last_issue_online
                            sanitize(tipp.url) + '\t' +
                            sanitize(tipp.firstAuthor) + '\t' +
                            sanitize(tipp.getTitleID()) + '\t' +
                            sanitize(tippCoverageStatement.embargo) + '\t' +
                            sanitize(tippCoverageStatement.coverageDepth) + '\t' +
                            sanitize(tipp.note) + '\t' +
                            sanitize(tipp.publisherName) + '\t' +
                            sanitize(tipp.publicationType?.value) + '\t' +
                            sanitize(tipp.dateFirstInPrint) + '\t' +
                            sanitize(tipp.dateFirstOnline) + '\t' +
                            sanitize(tipp.volumeNumber) + '\t' +
                            sanitize(tipp.editionStatement) + '\t' +
                            sanitize(tipp.firstEditor) + '\t' +
                            sanitize(tipp.parentPublicationTitleId) + '\t' +
                            sanitize(tipp.precedingPublicationTitleId) + '\t' +
                            sanitize(tipp.accessType?.value) + '\t' +
                            '\n')
                }
            }

            writer.flush();
            writer.close();
        }
        outputStream.close()
    }


    public void exportOriginalKBART(def outputStream, Package pkg) {

        if((pkg.source.lastUpdateUrl || pkg.source.url)){
            if(pkg.source.lastUpdateUrl){
                File file = kbartFromUrl(pkg.source.lastUpdateUrl)
                outputStream << file.bytes
            }else{
                File file = kbartFromUrl(pkg.source.url)
                outputStream << file.bytes
            }
        }else {

            outputStream.withWriter { writer ->

                writer.write('publication_title\t' +
                        'print_identifier\t' +
                        'online_identifier\t' +
                        'date_first_issue_online\t' +
                        'num_first_vol_online\t' +
                        'num_first_issue_online\t' +
                        'date_last_issue_online\t' +
                        'num_last_vol_online\t' +
                        'num_last_issue_online\t' +
                        'title_url\t' +
                        'first_author\t' +
                        'title_id\t' +
                        'embargo_info\t' +
                        'coverage_depth\t' +
                        'notes\t' +
                        'publisher_name\t' +
                        'publication_type\t' +
                        'date_monograph_published_print\t' +
                        'date_monograph_published_online\t' +
                        'monograph_volume\t' +
                        'monograph_edition\t' +
                        'first_editor\t' +
                        'parent_publication_title_id\t' +
                        'preceding_publication_title_id\t' +
                        'access_type\t' +
                        '\n'
                )
                writer.flush();
                writer.close();
            }
            outputStream.close()
        }
    }

    private File kbartFromUrl(String urlString) throws Exception{
        URL url = new URL(urlString)
        File folder = new File("/tmp/wekb/kbartExport")
        HttpURLConnection connection
        try {
            connection = (HttpURLConnection) url.openConnection()
            connection.addRequestProperty("User-Agent", "Mozilla/5.0")
        }
        catch (IOException e) {
            throw new RuntimeException("URL Connection was not established.")
        }
        connection.connect()
        connection = UrlToolkit.resolveRedirects(connection, 5)
        log.debug("Final URL after redirects: ${connection.getURL()}")

        String fileName = folder.absolutePath.concat(File.separator).concat(urlStringToFileString(url.toExternalForm()))
        fileName = fileName.split("\\?")[0]
        File file = new File(fileName)

        byte[] content = getByteContent(connection.getInputStream())
        //InputStream inputStream = new ByteArrayInputStream(content)
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(content), file)
            // copy content to local file
            Files.write(file.toPath(), content)
        }
        return file
    }

    private byte[] getByteContent(InputStream inputStream){
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        byte[] buf = new byte[4096]
        int n = 0
        while ((n = inputStream.read(buf)) >= 0){
            baos.write(buf, 0, n)
        }
        baos.toByteArray()
    }


    static String urlStringToFileString(String url){
        url.replace("://", "_").replace(".", "_").replace("/", "_")
    }

    def exportPackageBatchImportTemplate(def outputStream) {

        List titles = ["package_uuid", "package_name", "provider_uuid", "nominal_platform_uuid", "description", "url", "breakable", "content_type",
                              "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "anbieter_produkt_id", "ddc", "source_url", "frequency", "title_id_namespace", "automated_updates"]


        XSSFWorkbook workbook = new XSSFWorkbook()
        XSSFSheet sheet = workbook.createSheet("Packages")

        Row headerRow = sheet.createRow(0)
        headerRow.setHeightInPoints(16.75f)
        titles.eachWithIndex{ titleName, int i ->
            Cell cell = headerRow.createCell(i)
            cell.setCellValue(titleName)
        }
        sheet.createFreezePane(0,1)

        titles.eachWithIndex{ titleName, int i ->
            String[] datas
            switch(titleName) {
                case 'breakable': datas = RefdataCategory.lookup(RCConstants.PACKAGE_BREAKABLE).sort{it.value}.collect { it -> it.value }
                    break
                case 'consistent': datas = RefdataCategory.lookup(RCConstants.PACKAGE_CONSISTENT).sort{it.value}.collect { it -> it.value }
                    break
                case 'content_type': datas = RefdataCategory.lookup(RCConstants.PACKAGE_CONTENT_TYPE).sort{it.value}.collect { it -> it.value }
                    break
                case 'file': datas = RefdataCategory.lookup(RCConstants.PACKAGE_FILE).sort{it.value}.collect { it -> it.value }
                    break
                case 'open_access': datas = RefdataCategory.lookup(RCConstants.PACKAGE_OPEN_ACCESS).sort{it.value}.collect { it -> it.value }
                    break
                case 'payment_type': datas = RefdataCategory.lookup(RCConstants.PACKAGE_PAYMENT_TYPE).sort{it.value}.collect { it -> it.value }
                    break
                case 'scope': datas = RefdataCategory.lookup(RCConstants.PACKAGE_SCOPE).sort{it.value}.collect { it -> it.value }
                    break
                case 'editing_status': datas = RefdataCategory.lookup(RCConstants.PACKAGE_EDITING_STATUS).sort{it.value}.collect { it -> it.value }
                    break
                case 'national_range': //Because many to many
                    break
                case 'regional_range': //Because many to many
                    break
                case 'ddc': //Because many to many
                    break
                case 'frequency': datas = RefdataCategory.lookup(RCConstants.SOURCE_FREQUENCY).sort{it.value}.collect { it -> it.value }
                    break
                case 'title_id_namespace': //Because more than 255 values // datas = IdentifierNamespace.findAllByFamily('ttl_prv').sort{it.value}.collect{ it -> it.value}
                    break
                case 'automated_updates': datas = RefdataCategory.lookup(RCConstants.YN).sort{it.value}.collect { it -> it.value }
                    break
            }

            if(datas){
                setInExcelDropDownList(sheet, datas, i)
            }

        }

        try {

            workbook.write(outputStream)
            outputStream.flush()
            outputStream.close()

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private setInExcelDropDownList(XSSFSheet sheet, String[] datas, Integer column){

        //println(datas)
        XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet)
        XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createExplicitListConstraint(datas)
        CellRangeAddressList addressList = null
        XSSFDataValidation validation = null

        addressList = new CellRangeAddressList(1, 500, column, column)
        validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList)

        // These two lines set the cell can only be a table of contents, otherwise an error
        validation.setSuppressDropDownArrow(true)
        validation.setShowErrorBox(true)

        sheet.addValidationData(validation)

    }
}
