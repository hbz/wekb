<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='sb-admin'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>


<semui:flashMessage data="${flash}"/>


<div class="container">
    <h1>Tipp Duplicates</h1>

</div>

<div class="container">
    <ul id="tabs" class="nav nav-tabs">
        <li role="presentation" class="${!params.papaginateByUrl && !params.papaginateByTitleID ? 'active' : ''}"><a href="#byName" data-toggle="tab">Tipps Duplicates By Name <span
                class="badge badge-warning">${totalCountByName}</span></a></li>
        <li role="presentation" class="${params.papaginateByUrl && !params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}"><a href="#byUrl" data-toggle="tab">Tipps Duplicates By Url <span
                class="badge badge-warning">${totalCountByUrl}</span></a></li>
        <li role="presentation" class="${!params.papaginateByUrl && params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}"><a href="#byTitleID" data-toggle="tab">Tipps Duplicates By Title ID <span
                class="badge badge-warning">${totalCountByTitleID}</span></a></li>
    </ul>

    <div class="tab-content">

        <div class="tab-pane ${!params.papaginateByUrl && !params.papaginateByTitleID ? 'active' : ''}" id="byName">

            <h3>Tipps Duplicates By Name (${totalCountByName})</h3>

            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Identifiers</th>
                    <th>Platform</th>
                    <th>Publication Type</th>
                    <th>Medium</th>
                    <th>Url</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippsDuplicatesByName}" var="t" status="i">
                    <tr>
                        <td>
                            ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                        </td>
                        <td>
                            <g:link controller="resource" action="show" id="${t.uuid}">
                                ${t.name} <b>(${t.status.value})</b>
                            </g:link>
                        </td>
                        <td>
                            <ul>
                                <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                       action="show"
                                                                                       id="${id.class.name}:${id.id}">${id.value}</g:link>
                                    </li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.hostPlatform?.uuid}">
                                ${t.hostPlatform?.name}
                            </g:link>
                        </td>
                        <td>${t.publicationType?.value}</td>
                        <td>${t.medium?.value}</td>
                        <td>
                            ${t.url}
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div class="pagination mb-4 d-flex justify-content-center">
                <g:paginate controller="${controllerName}" action="${actionName}"
                            params="[id: params.id, papaginateByName: true]" next="&raquo;" prev="&laquo;"
                            max="${maxByName}" offset="${offsetByName}" total="${totalCountByName}"/>
            </div>

        </div>

        <div class="tab-pane ${params.papaginateByUrl && !params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}" id="byUrl">
            <h3>Tipps Duplicates By Url (${totalCountByUrl})</h3>

            <g:link controller="admin" action="removeTippDuplicatesByUrl" id="${params.id}" class="btn btn-default pull-right btn-sm">Remove Tipps Duplicates By Url</g:link>

            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Identifiers</th>
                    <th>Platform</th>
                    <th>Publication Type</th>
                    <th>Medium</th>
                    <th>Url</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippsDuplicatesByUrl}" var="t" status="i">
                    <tr>
                        <td>
                            ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                        </td>
                        <td>
                            <g:link controller="resource" action="show" id="${t.uuid}">
                                ${t.name}
                            </g:link> <b>(${t.status.value})</b>
                        </td>
                        <td>
                            <ul>
                                <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                       action="show"
                                                                                       id="${id.class.name}:${id.id}">${id.value}</g:link>
                                    </li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.hostPlatform?.uuid}">
                                ${t.hostPlatform?.name}
                            </g:link>
                        </td>
                        <td>${t.publicationType?.value}</td>
                        <td>${t.medium?.value}</td>
                        <td>
                            ${t.url}
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div class="pagination mb-4 d-flex justify-content-center">
                <g:paginate controller="${controllerName}" action="${actionName}"
                            params="[id: params.id, papaginateByUrl: true]" next="&raquo;" prev="&laquo;"
                            max="${maxByUrl}" offset="${offsetByUrl}" total="${totalCountByUrl}"/>
            </div>
        </div>

        <div class="tab-pane ${!params.papaginateByUrl && params.papaginateByTitleID && !params.papaginateByName ? 'active' : ''}" id="byTitleID">

            <h3>Tipps Duplicates By Title ID (${totalCountByTitleID})</h3>
            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Identifiers</th>
                    <th>Platform</th>
                    <th>Publication Type</th>
                    <th>Medium</th>
                    <th>Url</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippsDuplicatesByTitleID}" var="t" status="i">
                    <tr>
                        <td>
                            ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                        </td>
                        <td>
                            <g:link controller="resource" action="show" id="${t.uuid}">
                                ${t.name} <b>(${t.status.value})</b>
                            </g:link>
                        </td>
                        <td>
                            <ul>
                                <g:each in="${t.ids.sort { it.namespace.value }}" var="id">
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="resource"
                                                                                       action="show"
                                                                                       id="${id.class.name}:${id.id}">${id.value}</g:link>
                                    </li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.hostPlatform?.uuid}">
                                ${t.hostPlatform?.name}
                            </g:link>
                        </td>
                        <td>${t.publicationType?.value}</td>
                        <td>${t.medium?.value}</td>
                        <td>
                            ${t.url}
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <div class="pagination mb-4 d-flex justify-content-center">
                <g:paginate controller="${controllerName}" action="${actionName}"
                            params="[id: params.id, papaginateByTitleID: true]" next="&raquo;" prev="&laquo;"
                            max="${maxByTitleID}" offset="${offsetByTitleID}" total="${totalCountByTitleID}"/>
            </div>

        </div>
    </div>
</div>

</body>
</html>
