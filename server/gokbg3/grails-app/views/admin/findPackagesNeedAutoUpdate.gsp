<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Packages needs Auto Updates</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>

<h1 class="ui header">Packages needs Auto Updates (${pkgs.size()})</h1>


<div class="container">

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <th>Name</th>
            <th>Provider</th>
            <th>Curatory Groups</th>
            <th>Source / Kbart URL</th>
            <th>Last Update URL</th>
            <th>Last Run</th>
            <th>Next Run</th>
            <th>Titles</th>
            <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${pkgs}" var="pkg" status="i">
            <tr>
                <td>
                    ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.uuid}">
                        ${pkg.name}
                    </g:link>
                </td>
                <td>
                    ${pkg.provider}
                </td>
                <td>
                    <g:each in="${pkg.curatoryGroups}" var="curatoryGroup">
                        ${curatoryGroup.name}
                    </g:each>
                </td>
                <td>
                    <g:link controller="resource" action="show" id="${pkg.source.uuid}">
                        ${pkg.source.name}
                    </g:link>
                    <semui:showOutGoingLink text="Kbart Url" outGoingLink="${pkg.source.url}"/>
                </td>
                <td>
                    <semui:showOutGoingLink text="Last Update Url" outGoingLink="${pkg.source.lastUpdateUrl}"/>
                </td>
                <td>
                    <g:if test="${pkg.source.lastRun}">
                        <g:formatDate date="${pkg.source.lastRun}"
                                      format="${message(code: 'default.date.format')}"/>
                    </g:if>
                </td>
                <td>
                    <g:set var="nextRun" value="${pkg.source.getNextUpdateTimestamp()}"/>
                    <g:if test="${nextRun}">
                        <g:formatDate date="${nextRun}"
                                      format="${message(code: 'default.date.format')}"/>
                    </g:if>
                </td>
                <td>
                    ${pkg.getTippCount()}
                </td>
                <td>
                    <g:set var="object" value="${pkg.class.name}:${pkg.id}"/>
                    <g:link class="ui button" controller="workflow" action="action"
                            params="[component: object, selectedBulkAction: 'packageUrlUpdate', curationOverride: true]">Trigger Update (Changed Titles) </g:link>

                    <br>
                    <br>
                    <g:link class="ui button black" controller="workflow" action="action"
                            params="[component: object, selectedBulkAction: 'packageUrlUpdateAllTitles', curationOverride: true]">Trigger Update (all Titles)</g:link>
                </td>
            </tr>
        </g:each>
        </tbody>
    </table>

</div>

</body>
</html>