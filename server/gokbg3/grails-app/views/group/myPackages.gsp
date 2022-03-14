<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="sb-admin"/>
  <title><g:message code="gokb.appname" default="we:kb"/>: My Packages (${group.name})</title>
</head>

<body>
<h1 class="page-header">${group.name}</h1>

<div id="mainarea" class="panel panel-default">

  <h3 class="page-header">My Packages

    <g:link controller="group" action="exportMyPackages" id="${params.id}" class="btn btn-default pull-right btn-sm">Export my packages</g:link>
  </h3>
    <br>

    <g:if test="${!params.inline}">
        <div class="panel-heading">
            <h3 class="panel-title">
                Search
            </h3>
        </div>
    </g:if>
    <div class="panel-body">
        <g:if test="${(qbetemplate.message != null)}">
            <p style="text-align: center">

            <div class="alert-info">
                ${qbetemplate.message}
            </div>
            </p>
        </g:if>

        <g:render template="/search/qbeform"
                  model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>
    </div>
<!-- panel-body -->
    <g:if test="${recset && !init}">
        <g:render template="/search/qberesult"
                  model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, jumpToPage: 'jumpToPage', det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
    </g:if>
    <g:elseif test="${!init && !params.inline}">
        <div class="panel-footer">
            <g:render template="/search/qbeempty"/>
        </div>
    </g:elseif>

    <br>

    <g:link controller="create" action="packageBatch" class="btn btn-default pull-right btn-sm">Upload Packages</g:link>
    <br>
    <br>
    <br>

</div>

</body>
</html>
