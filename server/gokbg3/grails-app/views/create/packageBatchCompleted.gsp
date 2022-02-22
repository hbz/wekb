<%@ page import="org.gokb.cred.IdentifierNamespace; de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory; org.gokb.cred.IdentifierNamespace;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="sb-admin"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Package batch processing</title>
</head>

<body>
<h1 class="page-header">
    Package batch processing completed

    <g:link controller="create" action="packageBatch" class="btn btn-default pull-right btn-sm">Back to Package batch processing</g:link>
</h1>

<div class="panel panel-default">
    <div class="panel-heading clearfix">
        <h3 class="panel-title">${packages.size()} of ${rowsCount} packages were created/changed</h3>
    </div>


    <g:set var="counter" value="${1}" />
    <div class="panel-body">
        <table class="celled striped table la-table">
            <thead>
            <tr>
                <th>#</th>
                <th>Name</th>
                <th>Provider</th>
                <th>Platform</th>
                <th>Source</th>
                <th>Frequency</th>
                <th>Title ID Namespace</th>
                <th>Automated Updates</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${packages}" var="pkg">
                <tr>
                    <td>${counter++}</td>
                    <td>
                        <g:link controller="resource" action="show" id="${pkg.id}">${pkg.name}</g:link>
                    </td>

                    <td>
                        <g:if test="${pkg.provider}">
                            <g:link controller="resource" action="show" id="${pkg.provider.id}">${pkg.provider.name}</g:link>
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.nominalPlatform}">
                            <g:link controller="resource" action="show" id="${pkg.nominalPlatform.id}">${pkg.nominalPlatform.name}</g:link>
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.source}">
                            <g:link controller="resource" action="show" id="${pkg.source.id}">${pkg.source.name}</g:link>
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.source && pkg.source.frequency}">
                            ${pkg.source.frequency.getI10n('value')}
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.source && pkg.source.targetNamespace}">
                            ${pkg.source.targetNamespace.value}
                        </g:if>
                    </td>

                    <td>
                        <g:if test="${pkg.source}">
                            ${pkg.source.automaticUpdates ? 'Yes' : 'No'}
                        </g:if>
                    </td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>

<div class="panel panel-default">
    <div class="panel-heading clearfix">
        <h3 class="panel-title">Package not created/changed due to errors</h3>
    </div>


    <g:set var="counter" value="${1}" />
    <div class="panel-body">
        <table class="celled striped table la-table">
            <thead>
            <tr>
                <th>#</th>
                <th>Error</th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${errors}" var="error">
                <tr>
                    <td>${counter++}</td>
                    <td>
                        ${error}
                    </td>
                </tr>
            </g:each>
            </tbody>
        </table>
    </div>
</div>

</body>
</html>
