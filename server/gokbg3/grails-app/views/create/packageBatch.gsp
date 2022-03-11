<%@ page import="org.gokb.cred.IdentifierNamespace; de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory; org.gokb.cred.IdentifierNamespace;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="sb-admin"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Upload Packages</title>
</head>

<body>
<h1 class="page-header">
    Upload Packages
</h1>

<g:render template="/apptemplates/secondTemplates/messages"/>

<div class="panel panel-default">
    <div class="panel-heading clearfix">
        <h3 class="panel-title">Information</h3>
    </div>

    <div class="panel-body">
        The option "Upload Packages" allows you to create several packages at once via the provided package template below.
        <br><br>
        In order to create your packages, download the template below and type in the name of the packages. Give all the relevant information for each individual package and save the template file as a csv file (tab separated and UTF-8 coded).
        <br>
        For the upload of the package template, click on „Durchsuchen“ to choose the file you created and Upload it to the we:kb.
        <br><br>
        <g:link action="exportPackageBatchImportTemplate"><p>The template file for the batch processing package can be downloaded here</p></g:link>
    </div>
</div>

<div class="panel panel-default">
    <div class="panel-heading clearfix">
        <h3 class="panel-title">Upload Template</h3>
    </div>

    <div class="panel-body">
        <g:uploadForm action="processPackageBatch" method="post">
            <dl>
                <input type="file" class="btn btn-default" name="tsvFile"  accept=".tsv, .txt"/>
                <br>
                <button class="btn btn-default btn-sm" type="submit">Upload</button>
            </dl>
        </g:uploadForm>
    </div>
</div>

<div class="panel panel-default">
    <div class="panel-heading clearfix">
        <h3 class="panel-title">Template Description</h3>
    </div>

    <div class="panel-body">
        <table class="celled striped table la-table">
            <thead>
            <tr>
                <th>ColumnName</th>
                <th>Description of Column</th>
                <th>Necessary Format</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${mappingCols}" var="mpg">
                <%
                    List args = []
                    switch (mpg) {
                        case 'breakable': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_BREAKABLE).sort { it.value }.collect { it -> it.value })
                            break
                        case 'consistent': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_CONSISTENT).sort { it.value }.collect { it -> it.value })
                            break
                        case 'content_type': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_CONTENT_TYPE).sort { it.value }.collect { it -> it.value })
                            break
                        case 'file': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_FILE).sort { it.value }.collect { it -> it.value })
                            break
                        case 'open_access': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_OPEN_ACCESS).sort { it.value }.collect { it -> it.value })
                            break
                        case 'payment_type': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_PAYMENT_TYPE).sort { it.value }.collect { it -> it.value })
                            break
                        case 'scope': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_SCOPE).sort { it.value }.collect { it -> it.value })
                            break
                        case 'editing_status': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_EDITING_STATUS).sort{it.value}.collect { it -> it.value })
                            break
                        case 'national_range': args.addAll(["EN", "FRE", "GER"])
                            break
                        case 'regional_range': args.addAll(RefdataCategory.lookup(RCConstants.PACKAGE_REGIONAL_RANGE).sort { it.value }.collect { it -> it.value })
                            break
                        case 'ddc': args.addAll(["001", "101", "202"])
                            break
                        case 'frequency': args.addAll(RefdataCategory.lookup(RCConstants.SOURCE_FREQUENCY).sort { it.value }.collect { it -> it.value })
                            break
                        case 'title_id_namespace': args.addAll(IdentifierNamespace.findAllByFamily('ttl_prv').sort { it.value }.collect { it -> it.value })
                            break
                        case 'automated_updates': args.addAll(RefdataCategory.lookup(RCConstants.YN).sort { it.value }.collect { it -> it.value })
                            break
                        case 'archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_ARCHIVING_AGENCY).sort{it.value}.collect { it -> it.value }
                            break
                        case 'open_access_of_archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_OPEN_ACCESS).sort{it.value}.collect { it -> it.value }
                            break
                        case 'post_cancellation_access_of_archiving_agency': datas = RefdataCategory.lookup(RCConstants.PAA_POST_CANCELLATION_ACCESS).sort{it.value}.collect { it -> it.value }
                            break
                    }
                %>
                <tr>
                    <td>${message(code: "packageBatch.columnName.${mpg}", args: args ?: '')}</td>
                    <td>${message(code: "packageBatch.description.${mpg}") ?: ''}</td>
                    <td>${message(code: "packageBatch.format.${mpg}", args: [raw("<ul><li>${args.join('</li><li>')}</li></ul>")]) ?: ''}</td>
                </tr>
            </g:each>
            </tbody>
        </table>

    </div>
</div>
</div>
</body>
</html>
