<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='sb-admin'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Packges with Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>


<div class="container">
    <h1>Packges with Tipp Duplicates</h1>

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
                    <g:sortableColumn property="tippDuplicatesByNameCount" title="Tipp Duplicates By Name"/>
                    <g:sortableColumn property="tippDuplicatesByUrlCount" title="Tipp Duplicates By Url"/>
                </tr>
                </thead>
                <tbody>
                <g:each in="${pkgs}" var="pkgMap" status="i">
                    <g:set var="pkg" value="${pkgMap.pkg}"/>
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
                            ${pkg.nominalPlatform}
                        </td>
                        <td>
                            <g:each in="${pkg.curatoryGroups}" var="curatoryGroup">
                                ${curatoryGroup.name}
                            </g:each>
                        </td>
                        <td>
                            <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}">
                                ${pkgMap.tippDuplicatesByNameCount}
                            </g:link>
                        </td>
                        <td>
                            <g:link controller="admin" action="findTippDuplicatesByPkg" id="${pkg.uuid}">
                                ${pkgMap.tippDuplicatesByUrlCount}
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
