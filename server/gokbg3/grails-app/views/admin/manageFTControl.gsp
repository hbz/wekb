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
                <g:each in="${ftControls}" var="ftControl">
                    <tr>
                        <td>${ftControl.domainClassName}</td>
                        <td>
                            <gokb:xEditable owner="${ftControl}" field="lastTimestamp"/>
                        </td>
                        <td>
                            <g:formatDate date="${new Date(ftControl.lastTimestamp)}"
                                          format="${message(code: 'default.date.format')}"/>
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
