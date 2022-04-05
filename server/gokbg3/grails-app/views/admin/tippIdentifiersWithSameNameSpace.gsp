<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='sb-admin'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Title Identifiers with same Identifier Namespace</title>
</head>

<body>

<wekb:serviceInjection/>


<div class="container">
    <h1>Title Identifiers with same Identifier Namespace (${total})</h1>

</div>

<div class="container">
    <div class="row">
        <div class="">

            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Name</th>
                    <th>Family</th>
                    <th>Count</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${namespaces}" var="namespaceMap" status="i">
                    <tr>
                        <td>
                            ${(params.offset ? params.offset.toInteger() : 0) + i + 1}
                        </td>
                        <td>
                            ${namespaceMap.name}
                        </td>
                        <td>
                            ${namespaceMap.family}
                        </td>
                        <td>
                            <g:link controller="admin" action="tippIdentifiersWithSameNameSpaceByNameSpace" id="${namespaceMap.namespaceID}">
                                ${namespaceMap.count}
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
