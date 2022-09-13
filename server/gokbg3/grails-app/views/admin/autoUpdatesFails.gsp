<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Automatic Update Fails</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>

<h1 class="ui header">Automatic Update Fails (${autoUpdates.size()})</h1>


<div class="container">
    <g:if test="${autoUpdates.size() > 0}">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th></th>
                <th>Description</th>
                <th>Package</th>
                <th>Provider</th>
                <th>Curatory Group</th>
                <th>Start Time</th>
                <th>End Time</th>

            </tr>
            </thead>
            <g:each in="${autoUpdates}" var="autoUpdate" status="i">
                <tr>
                    <td>
                        ${i + 1}
                    </td>
                    <td>
                        ${autoUpdate.description}
                        <br>
                        <br>
                        <g:set var="linkToAutoUpdateInfo" value="${grailsApplication.config.serverUrl + "resource/show/${autoUpdate.class.name}:${autoUpdate.uuid}"}"/>
                        ${linkToAutoUpdateInfo}
                    </td>
                    <td>
                        ${autoUpdate.pkg.name}
                        <br>
                        <br>
                        <g:set var="linkToPkg" value="${grailsApplication.config.serverUrl + "resource/show/${autoUpdate.pkg.uuid}"}"/>
                        ${linkToPkg}
                    </td>
                    <td>
                        ${autoUpdate.pkg.provider?.name}
                    </td>
                    <td>
                        <g:each in="${autoUpdate.pkg.curatoryGroups}" var="cg">
                            ${cg.name}
                        </g:each>
                    </td>
                    <td>
                        ${autoUpdate.startTime}
                    </td>
                    <td>
                        ${autoUpdate.endTime}
                    </td>
                </tr>
            </g:each>
        </table>
    </g:if>
    <g:else>
        No Auto Update with Fail found. Everything is right.
    </g:else>
</div>

</body>
</html>
