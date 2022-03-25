package wekb

import de.hbznrw.ygor.tools.UrlToolkit
import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
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
import org.gokb.cred.ComponentPrice
import org.gokb.cred.Identifier
import org.gokb.cred.IdentifierNamespace
import org.gokb.cred.Package
import org.gokb.cred.Platform
import org.gokb.cred.RefdataCategory
import org.gokb.cred.RefdataValue
import org.gokb.cred.TIPPCoverageStatement
import org.gokb.cred.TitleInstancePackagePlatform
import org.hibernate.Session

import java.nio.file.Files
import java.text.SimpleDateFormat


@Transactional
class ExportService {

    DateFormatService dateFormatService

    /*def exportPackageTippsAsTSV(def outputStream, Package pkg) {

        def export_date = dateFormatService.formatDate(new Date())

        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')

        def sanitize = { it ? (it instanceof Date ? sdf.format(it) : "${it}".trim()) : "" }

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
                    'title_wekb_uuid\t'+
                    'package_wekb_uuid\t'+
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

                if(tipp.publicationType?.value == 'Serial') {
                    tipp.coverageStatements.each { TIPPCoverageStatement tippCoverageStatement ->
                        writer.write(sanitize(tipp.name) + '\t' +
                                sanitize(tipp.firstAuthor) + '\t' +
                                sanitize(tipp.firstEditor) + '\t' +
                                sanitize(tipp.publisherName) + '\t' + //publisher_name
                                sanitize(tipp.publicationType?.value) + '\t' +
                                sanitize(tipp.medium?.value) + '\t' +
                                sanitize(tipp.url) + '\t' +
                                sanitize(tipp.getPrintIdentifier()) + '\t' +
                                sanitize(tipp.getOnlineIdentifier()) + '\t' +
                                sanitize(tipp.getTitleID()) + '\t' +
                                sanitize(tipp.getIdentifierValue('DOI')) + '\t' +
                                sanitize(tipp.subjectArea) + '\t' +
                                sanitize(tipp.languages?.language.value.join(';')) + '\t' +
                                sanitize(tipp.accessType?.value) + '\t' +
                                sanitize(tipp.coverageDepth?.value) + '\t' +
                                sanitize(tipp.pkg.name) + '\t' +
                                '\t' + //package_id
                                sanitize(tipp.accessStartDate) + '\t' +
                                sanitize(tipp.accessEndDate) + '\t' +
                                sanitize(tipp.lastChangedExternal) + '\t' +
                                sanitize(tipp.status?.value) + '\t' +
                                sanitize(tipp.getListPriceInEUR()) + '\t' + //listprice_eur
                                sanitize(tipp.getListPriceInUSD()) + '\t' + //listprice_usd
                                sanitize(tipp.getListPriceInGBP()) + '\t' + //listprice_gbp
                                sanitize(tipp.note) + '\t' +
                                sanitize(tipp.dateFirstInPrint) + '\t' +
                                sanitize(tipp.dateFirstOnline) + '\t' +
                                sanitize(tipp.volumeNumber) + '\t' +
                                sanitize(tipp.editionStatement) + '\t' +
                                sanitize(tipp.series) + '\t' +
                                sanitize(tipp.parentPublicationTitleId) + '\t' +
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
                                sanitize(tipp.getOAAPCPriceInEUR()) + '\t' + //oa_apc_eur
                                sanitize(tipp.getOAAPCPriceInUSD()) + '\t' + //oa_apc_usd
                                sanitize(tipp.getOAAPCPriceInGBP()) + '\t' + //oa_apc_gbp
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
                }else{
                    writer.write(sanitize(tipp.name) + '\t' +
                            sanitize(tipp.firstAuthor) + '\t' +
                            sanitize(tipp.firstEditor) + '\t' +
                            sanitize(tipp.publisherName) + '\t' + //publisher_name
                            sanitize(tipp.publicationType?.value) + '\t' +
                            sanitize(tipp.medium?.value) + '\t' +
                            sanitize(tipp.url) + '\t' +
                            sanitize(tipp.getPrintIdentifier()) + '\t' +
                            sanitize(tipp.getOnlineIdentifier()) + '\t' +
                            sanitize(tipp.getTitleID()) + '\t' +
                            sanitize(tipp.getIdentifierValue('DOI')) + '\t' +
                            sanitize(tipp.subjectArea) + '\t' +
                            sanitize(tipp.languages?.language.value.join(';')) + '\t' +
                            sanitize(tipp.accessType?.value) + '\t' +
                            sanitize(tipp.coverageDepth?.value) + '\t' +
                            sanitize(tipp.pkg.name) + '\t' +
                            '\t' + //package_id
                            sanitize(tipp.accessStartDate) + '\t' +
                            sanitize(tipp.accessEndDate) + '\t' +
                            sanitize(tipp.lastChangedExternal) + '\t' +
                            sanitize(tipp.status?.value) + '\t' +
                            sanitize(tipp.getListPriceInEUR()) + '\t' + //listprice_eur
                            sanitize(tipp.getListPriceInUSD()) + '\t' + //listprice_usd
                            sanitize(tipp.getListPriceInGBP()) + '\t' + //listprice_gbp
                            sanitize(tipp.note) + '\t' +
                            sanitize(tipp.dateFirstInPrint) + '\t' +
                            sanitize(tipp.dateFirstOnline) + '\t' +
                            sanitize(tipp.volumeNumber) + '\t' +
                            sanitize(tipp.editionStatement) + '\t' +
                            sanitize(tipp.series) + '\t' +
                            sanitize(tipp.parentPublicationTitleId) + '\t' +
                            '\t' + //date_first_issue_online
                            '\t' + //num_first_vol_online
                            '\t' + //num_first_issue_online
                            '\t' + //date_last_issue_online
                            '\t' + //num_last_vol_online
                            '\t' + //num_last_issue_online
                            '\t' +
                            '\t' +
                            '\t' + //package_ezb_anchor
                            '\t' + //oa_gold
                            '\t' + //oa_hybrid
                            sanitize(tipp.getOAAPCPriceInEUR()) + '\t' + //oa_apc_eur
                            sanitize(tipp.getOAAPCPriceInUSD()) + '\t' + //oa_apc_usd
                            sanitize(tipp.getOAAPCPriceInGBP()) + '\t' + //oa_apc_gbp
                            '\t' + //package_isil
                            sanitize(tipp.uuid) + '\t' +
                            sanitize(tipp.pkg.uuid) + '\t' +
                            '\t' + //package_isci
                            '\t' + //ill_indicator
                            sanitize(tipp.precedingPublicationTitleId) + '\t' +
                            sanitize(tipp.supersedingPublicationTitleId) + '\t' + //superseding_publication_title_id
                            '\t' + //embargo_info
                            '\n')
                }
            }

            writer.flush();
            writer.close();
        }
        outputStream.close()
    }*/

    /*public void exportPackageTippsAsKBART(def outputStream, Package pkg) {

        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')

        def sanitize = { it ? (it instanceof Date ? sdf.format(it) : "${it}".trim()) : "" }

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
                            sanitize(tipp.getPrintIdentifier()) + '\t' +
                            sanitize(tipp.getOnlineIdentifier()) + '\t' +
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
    }*/


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
                              "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "anbieter_produkt_id", "ddc", "source_url", "frequency", "title_id_namespace", "automated_updates", "archiving_agency", "open_access_of_archiving_agency", "post_cancellation_access_of_archiving_agency"]


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
                case 'archiving_agency': //Because more than 255 values // datas = RefdataCategory.lookup(RCConstants.PAA_ARCHIVING_AGENCY).sort{it.value}.collect { it -> it.value }
                    break
                case 'open_access_of_archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_OPEN_ACCESS).sort{it.value}.collect { it -> it.value }
                    break
                case 'post_cancellation_access_of_archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_POST_CANCELLATION_ACCESS).sort{it.value}.collect { it -> it.value }
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

    String generateSeparatorTableString(Collection titleRow, Collection columnData,String separator) {
        List output = []
        output.add(titleRow.join(separator))
        columnData.each { row ->
            if(row.size() > 0)
                output.add(row.join(separator))
            else output.add(" ")
        }
        output.join("\n")
    }


    def exportPackageTippsAsTSVNew(def outputStream, Package pkg) {

        def export_date = dateFormatService.formatDate(new Date())
        List<String> titleHeaders = getTitleHeadersTSV()
        Map<String,List> export = [titleRow:titleHeaders,rows:[]]

        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

        def sanitize = { it ? (it instanceof Date ? sdf.format(it) : "${it}".trim()) : "" }

        List<String> printIdentifier = ["issn", "pisbn"]
        List<String> onlineIdentifier = ["eissn", "isbn"]

        String doiIdentifier = "DOI"
        String zdbIdentifier = "zdb"
        String ezbIdentifier = "ezb"
        String packageEzbAnchor = "package_ezb_anchor"
        String packageIsci = "package_isci"

        String titleIdNameSpace = pkg.source ? pkg.source.targetNamespace.value : 'FAKE'

        RefdataValue priceTypeList = RDStore.PRICE_TYPE_LIST
        RefdataValue priceTypeOAAPC = RDStore.PRICE_TYPE_OA_APC

        String hqlQuery = "select tipp.id," +
                " tipp.name, " +
                " tipp.firstAuthor, " +
                " tipp.firstEditor, " +
                " tipp.publisherName, " +
                " (select value from RefdataValue where tipp.id = tipp.publicationType), " +
                " (select value from RefdataValue where tipp.id = tipp.medium), " +
                " tipp.url, " +
                " 'printIdentifier', " +
                " 'onlineIdentifier', " +
                " 'titleIdNameSpace'," +
                " 'doiIdentifier',"+
                " tipp.subjectArea, " +
                " 'languages', " +
                " (select value from RefdataValue where id = tipp.accessType), " +
                " (select value from RefdataValue where id = tipp.coverageDepth), " +
                " 'pkg.name', " +
                " '', " + // package_id
                " tipp.accessStartDate, " +
                " tipp.accessEndDate, " +
                " tipp.lastChangedExternal, " +
                " (select value from RefdataValue where id = tipp.status), " +
                " 'listprice_eur', " +
                " 'listprice_usd', " +
                " 'listprice_gbp', " +
                " tipp.note, " +
                " tipp.dateFirstInPrint, " +
                " tipp.dateFirstOnline, " +
                " tipp.volumeNumber, " +
                " tipp.editionStatement, " +
                " tipp.series, " +
                " tipp.parentPublicationTitleId, " +
                " cs.startDate, " +
                " cs.startVolume, " +
                " cs.startIssue, " +
                " cs.endDate, " +
                " cs.endVolume, " +
                " cs.endIssue, " +
                " 'zdb_id', "+
                " 'ezb_id', "+
                " 'package_ezb_anchor', " +
                " '', " + // oa_gold
                " '', " + // oa_hybrid
                " 'oa_apc_eur', " +
                " 'oa_apc_usd', " +
                " 'oa_apc_gbp', " +
                " '', " + // package_isil
                " tipp.uuid, " +
                " 'pkg.uuid', " +
                " 'package_isci', " +
                " '', " + // ill_indicator
                " tipp.precedingPublicationTitleId, " +
                " tipp.supersedingPublicationTitleId, " +
                " (select value from RefdataValue where id = cs.coverageDepth) " +
                "from TitleInstancePackagePlatform as tipp join tipp.coverageStatements as cs where tipp.id in (:tippIDs) order by tipp.name"


        def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
        def combo_pkg_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

        Map queryParams = [:]
        queryParams.p = pkg.id
        queryParams.sd = status_deleted
        queryParams.ct = combo_pkg_tipps

        List<Long> tippIDs = TitleInstancePackagePlatform.executeQuery("select tipp.id from TitleInstancePackagePlatform as tipp, Combo as c where c.fromComponent.id=:p and c.toComponent=tipp and tipp.status != :sd and c.type = :ct order by tipp.name", queryParams, [readOnly: true])

        int max = 500
        TitleInstancePackagePlatform.withSession { Session sess ->
            for (int offset = 0; offset < tippIDs.size(); offset += max) {
                List tippAttributes = TitleInstancePackagePlatform.executeQuery(hqlQuery, [tippIDs: tippIDs.drop(offset).take(max)], [readOnly: true])
                tippAttributes.each { def attribute ->
                    List row = []
                    Long tippID
                    attribute.eachWithIndex { def attributeValue, int index ->
                        if(index == 0){
                            tippID = attribute[index]
                        }
                        else {
                            switch (attribute[index]) {
                                case 'listprice_eur':
                                    List existPrice = ComponentPrice.executeQuery('select price from ComponentPrice where price is not null and owner.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeList, currency: RDStore.CURRENCY_EUR], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(sanitize(existPrice[0]))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'listprice_usd':
                                    List existPrice = ComponentPrice.executeQuery('select price from ComponentPrice where price is not null and owner.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeList, currency: RDStore.CURRENCY_USD], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(sanitize(existPrice[0]))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'listprice_gbp':
                                    List existPrice = ComponentPrice.executeQuery('select price from ComponentPrice where price is not null and owner.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeList, currency: RDStore.CURRENCY_GBP], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(sanitize(existPrice[0]))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'oa_apc_eur':
                                    List existPrice = ComponentPrice.executeQuery('select price from ComponentPrice where price is not null and owner.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeOAAPC, currency: RDStore.CURRENCY_EUR], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(sanitize(existPrice[0]))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'oa_apc_usd':
                                    List existPrice = ComponentPrice.executeQuery('select price from ComponentPrice where price is not null and owner.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeOAAPC, currency: RDStore.CURRENCY_USD], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(sanitize(existPrice[0]))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'oa_apc_gbp':
                                    List existPrice = ComponentPrice.executeQuery('select price from ComponentPrice where price is not null and owner.id = :owner and priceType = :priceType and currency = :currency ', [owner: tippID, priceType: priceTypeOAAPC, currency: RDStore.CURRENCY_GBP], [readOnly: true])

                                    if (existPrice.size() > 0) {
                                        row.add(sanitize(existPrice[0]))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'printIdentifier':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: printIdentifier, tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'onlineIdentifier':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: onlineIdentifier, tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'titleIdNameSpace':

                                    if(titleIdNameSpace == 'FAKE') {
                                        Platform platform = Platform.executeQuery('select p from Platform as p,  Combo as c where c.toComponent.id = :tippID and c.type = :comboType and c.fromComponent = p and c.status = :comboStatus', [tippID: tippID, comboType: RDStore.COMBO_TYPE_PLT_HOSTEDTIPPS, comboStatus: RDStore.COMBO_STATUS_ACTIVE], [readOnly: true])[0]
                                        titleIdNameSpace = platform.titleNamespace ? platform.titleNamespace.value : 'FAKE'
                                    }

                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: titleIdNameSpace.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'doiIdentifier':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: doiIdentifier.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'zdb_id':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: zdbIdentifier.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'ezb_id':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: ezbIdentifier.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'package_ezb_anchor':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: packageEzbAnchor.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'package_isci':
                                    List identifiers = Identifier.executeQuery('select i.value from Identifier as i where LOWER(i.namespace.value) in :namespaceValue and i.tipp.id = :tippID', [namespaceValue: packageIsci.toLowerCase(), tippID: tippID], [readOnly: true])

                                    if (identifiers.size() > 0) {
                                        row.add(sanitize(identifiers.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'languages':
                                    List<KBComponentLanguage> languages = KBComponentLanguage.executeQuery('select language.value from KBComponentLanguage where kbcomponent.id = :tippID', [tippID: tippID], [readOnly: true])

                                    if (languages.size() > 0) {
                                        row.add(sanitize(languages.join(';')))
                                    } else {
                                        row.add("")
                                    }
                                    break;
                                case 'pkg.name':
                                        row.add(sanitize(pkg.name))
                                    break;
                                case 'pkg.uuid':
                                    row.add(sanitize(pkg.uuid))
                                    break;
                                default:
                                    row.add(sanitize(attribute[index]))
                            }
                        }
                        if (attribute.size() - 1 == index) {
                            export.rows.add(row)
                        }

                    }
                }
                log.debug("flushing after ${offset} ...")
                sess.flush()
            }
        }

        outputStream.withWriter { writer ->
            writer.write("we:kb Export : Provider (${pkg.provider?.name}) : Package (${pkg.name}) : ${export_date}\n");
            writer.write(generateSeparatorTableString(export.titleRow, export.rows, '\t'))
        }
        outputStream.flush()
        outputStream.close()
    }


    def exportPackages(def outputStream, def packages) {

        def export_date = dateFormatService.formatDate(new Date())
        List<String> titleHeaders = ["package_uuid", "package_name", "provider_name", "provider_uuid", "nominal_platform_name",
                                     "nominal_platform_uuid", "description", "url", "breakable", "content_type",
                                     "file", "open_access", "payment_type", "scope", "national_range", "regional_range", "anbieter_produkt_id", "ddc",
                                     "source_url", "frequency", "title_id_namespace", "automated_updates",
                                     "archiving_agency", "open_access_of_archiving_agency", "post_cancellation_access_of_archiving_agency"]
        Map<String,List> export = [titleRow:titleHeaders,rows:[]]

        SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')

        def sanitize = { it ? (it instanceof Date ? sdf.format(it) : "${it}".trim()) : "" }

        packages.each { Package pkg ->

            List row = []
            row.add(sanitize(pkg.uuid))
            row.add(sanitize(pkg.name))
            row.add(sanitize(pkg.provider?.name))
            row.add(sanitize(pkg.provider?.uuid))
            row.add(sanitize(pkg.nominalPlatform?.name))
            row.add(sanitize(pkg.nominalPlatform?.uuid))
            row.add(sanitize(pkg.description))
            row.add(sanitize(pkg.descriptionURL))
            row.add(sanitize(pkg.breakable?.value))
            row.add(sanitize(pkg.contentType?.value))
            row.add(sanitize(pkg.file?.value))
            row.add(sanitize(pkg.openAccess?.value))
            row.add(sanitize(pkg.paymentType?.value))
            row.add(sanitize(pkg.scope?.value))
            row.add(sanitize(pkg.nationalRanges?.value.join(',')))
            row.add(sanitize(pkg.regionalRanges?.value.join(',')))
            row.add(sanitize(pkg.getIdentifierValue('Anbieter_Produkt_Id')))
            row.add(sanitize(pkg.ddcs?.value.join(',')))
            row.add(sanitize(pkg.source?.url))
            row.add(sanitize(pkg.source?.frequency?.value))
            row.add(sanitize(pkg.source?.targetNamespace?.value))
            row.add(sanitize(pkg.source?.automaticUpdates ? 'Yes': 'No'))
            row.add(sanitize(pkg.paas?.archivingAgency?.value))
            row.add(sanitize(pkg.paas?.openAccess?.value))
            row.add(sanitize(pkg.paas?.postCancellationAccess?.value))
            export.rows.add(row)
        }

        outputStream.withWriter { writer ->
           // writer.write("we:kb Export : Packages (${packages.size()}) : ${export_date}\n");
            writer.write(generateSeparatorTableString(export.titleRow, export.rows, '\t'))
        }
        outputStream.flush()
        outputStream.close()
    }


    List<String> getTitleHeadersTSV() {
        ['publication_title',
         'first_author',
         'first_editor',
         'publisher_name',
         'publication_type',
         'medium',
         'title_url',
         'print_identifier',
         'online_identifier',
         'title_id',
         'doi_identifier',
         'subject_area',
         'language',
         'access_type',
         'coverage_depth',
         'package_name',
         'package_id',
         'access_start_date',
         'access_end_date',
         'last_changed',
         'status',
         'listprice_eur',
         'listprice_usd',
         'listprice_gbp',
         'notes',
         'date_monograph_published_print',
         'date_monograph_published_online',
         'monograph_volume',
         'monograph_edition',
         'monograph_parent_collection_title',
         'parent_publication_title_id',
         'date_first_issue_online',
         'num_first_vol_online',
         'num_first_issue_online',
         'date_last_issue_online',
         'num_last_vol_online',
         'num_last_issue_online',
         'zdb_id',
         'ezb_id',
         'package_ezb_anchor',
         'oa_gold',
         'oa_hybrid',
         'oa_apc_eur',
         'oa_apc_usd',
         'oa_apc_gbp',
         'package_isil',
         'title_wekb_uuid',
         'package_wekb_uuid',
         'package_isci',
         'ill_indicator',
         'preceding_publication_title_id',
         'superseding_publication_title_id',
         'embargo_info']
    }
}
