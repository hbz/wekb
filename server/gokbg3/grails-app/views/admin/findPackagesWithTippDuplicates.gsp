<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='sb-admin'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Packages with Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>


<div class="container">
    <h1>Packages with Tipp Duplicates (${totalCount})</h1>

</div>

<div class="container">
    <div class="row">
        <div class="">

            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Provider</th>
                    <th>Platform</th>
                    <th>Curatory Groups</th>
                    <th>Auto Update</th>
                    <th>Titles</th>
                    <g:sortableColumn property="tippDuplicatesByNameCount" title="Tipp Duplicates By Name"/>
                    <g:sortableColumn property="tippDuplicatesByUrlCount" title="Tipp Duplicates By Url"/>
                    <g:sortableColumn property="tippDuplicatesByTitleIDCount" title="Tipp Duplicates By Title ID"/>
                </tr>
                </thead>
                <tbody>
                <g:each in="${pkgs}" var="pkgMap" status="i">
                    <g:set var="pkg" value="${pkgMap.pkg}"/>
                    <tr class="${pkgMap.tippDuplicatesByTitleIDCount > 0 ? 'info' : (pkgMap.tippDuplicatesByUrlCount > 0 ? 'success' : '')}">
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
                            ${pkg.nominalPlatform}
                        </td>
                        <td>
                            <g:each in="${pkg.curatoryGroups}" var="curatoryGroup">
                                ${curatoryGroup.name}
                            </g:each>
                        </td>
                        <td>
                            <g:if test="${pkg.source?.automaticUpdates}">
                                <i class="fa fa-check-circle text-success fa-lg"
                                   title="${message(code: 'default.boolean.true')}"></i>
                            </g:if>
                            <g:else>
                                <i class="fa fa-times-circle text-danger fa-lg"
                                   title="${message(code: 'default.boolean.false')}"></i>
                            </g:else>
                        </td>
                        <td>
                                ${pkg.tipps.size()}
                        </td>
                        <td>
                            <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" target="_blank" params="[papaginateByName: true, max: 100, offset: 0]">
                                ${pkgMap.tippDuplicatesByNameCount}
                            </g:link>
                        </td>
                        <td>
                            <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" target="_blank" params="[papaginateByUrl: true, max: 100, offset: 0]">
                                ${pkgMap.tippDuplicatesByUrlCount}
                            </g:link>
                        </td>
                        <td>
                            <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}" target="_blank" params="[papaginateByTitleID: true, max: 100, offset: 0]">
                                ${pkgMap.tippDuplicatesByTitleIDCount}
                            </g:link>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>

</div>

</body>
</html>
