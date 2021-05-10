<!doctype html>
<html>
<head>
    <meta name="layout" content="sb-admin">
    <title>Manage FTControl</title>
</head>

<body>

<h1 class="page-header">Manage FTControl</h1>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <table class="ui celled la-table table">
                <thead>
                <tr>
                    <th>domainClassName</th>
                    <th>lastTimestamp</th>
                    <th>lastTimestamp in Date</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${ftControls.sort{it.domainClassName}}" var="ftControl">
                    <tr>
                        <td>${ftControl.domainClassName}</td>
                        <td>
                            <gokb:xEditable owner="${ftControl}" field="lastTimestamp"/>
                        </td>
                        <td>
                            <g:formatDate date="${new Date(ftControl.lastTimestamp)}"
                                          format="${message(code: 'default.datetime.format')}"/>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>


            <h3>Indices</h3>

            <table class="ui celled la-table table">
                <thead>
                <tr>
                    <th>index</th>
                    <th>type</th>
                    <th>countIndex</th>
                    <th>countDB</th>
                    <th>countStatusDeletedInDB</th>
                    <th>Action</th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${indices.sort{it.type}}" var="indexInfo">
                    <tr>
                        <td>${indexInfo.name}</td>
                        <td>${indexInfo.type}</td>
                        <td>${indexInfo.countIndex}</td>
                        <td>${indexInfo.countDB}</td>
                        <td>${indexInfo.countDeletedInDB}</td>
                        <td><g:link action="deleteIndex" params="[name: indexInfo.name]">Delete and refill Index</g:link></td>
                    </tr>
                </g:each>
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>
