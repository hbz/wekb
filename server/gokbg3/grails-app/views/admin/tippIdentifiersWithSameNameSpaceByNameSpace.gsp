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
        <div class="">

            <h3>Title Identifiers with same Identifier Namespace: ${namespace} (${count})</h3>

            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Title</th>
                    <th>Identifiers</th>
                    <th>Package</th>
                    <th>Platform</th>
                    <th>Publication Type</th>
                    <th>Medium</th>
                    <th>Url</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tipps}" var="t" status="i">
                    <tr>
                        <td>
                            ${ (params.offset ? params.offset.toInteger(): 0)  + i + 1 }
                        </td>
                        <td>
                            <g:link controller="resource" action="show" id="${t.uuid}">
                                ${t.name} <b>(${t.status.value})</b>
                            </g:link>
                        </td>
                        <td>
                            <ul>
                                <g:each in="${t.ids.sort{it.namespace.value}}" var="id">
                                    <li><strong>${id.namespace.value}</strong>:<g:link controller="resource" action="show" id="${id.class.name}:${id.id}">  ${id.value}</g:link></li>
                                </g:each>
                            </ul>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.pkg?.uuid}">
                                ${t.pkg?.name}
                            </g:link>
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${t.hostPlatform.uuid}">
                                ${t.hostPlatform.name}
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

        </div>
    </div>

</div>

</body>
</html>
