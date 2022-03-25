<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='sb-admin'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Tipp Duplicates</title>
</head>

<body>

<wekb:serviceInjection/>


<div class="container">
    <h1>Tipp Duplicates</h1>

</div>

<div class="container">
    <div class="row">
        <div class="col-md-12">


            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <g:sortableColumn property="tipp.name" title="Title"/>
                    <th>Identifiers</th>
                    <th>Platform</th>
                    <g:sortableColumn property="tipp.publicationType" title="Publication Type"/>
                    <g:sortableColumn property="tipp.medium" title="Medium"/>
                    <th>Coverage</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tipps}" var="t" status="i">
                    <tr>
                        <td>
                            ${ (params.offset ? params.offset.toInteger(): 0)  + i + 1 }
                        </td>
                        <td>
                            <g:link controller="public" action="tippContent" id="${t.uuid}">
                                ${t.name}
                            </g:link>
                        </td>
                        <td>
                            <ul>
                                <g:each in="${t.ids.sort{it.namespace.value}}" var="id">
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="public" action="identifierContent" id="${id.uuid}">  ${id.value}</g:link></li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="public" action="platformContent"
                                    id="${t.hostPlatform?.uuid}">
                                ${t.hostPlatform?.name}
                            </g:link>
                        </td>
                        <td>${t.publicationType?.value}</td>
                        <td>${t.medium?.value}</td>
                        <td>
                            ${t.coverageDepth?.value}<br/>${t.coverageNote}
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
