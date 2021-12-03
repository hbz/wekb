<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="sb-admin"/>
  <title><g:message code="gokb.appname" default="we:kb"/>: Package Recent Activity</title>
</head>

<body>
<br/>
<nav class="navbar navbar-inverse">
  <div class="container-fluid">
    <div class="navbar-header">
      <span class="navbar-brand">
          ${pkg.getNiceName() ?: 'Component'} : ${pkg.id} - <strong>${pkg.getDisplayName()}</strong>
      </span>
    </div>
  </div>
</nav>

<div id="mainarea" class="panel panel-default">
  <div class="panel-heading clearfix">
    <h3 class="panel-title">Package Recent Activity (${recentActivitys.size()})</h3>
  </div>

  <div class="panel-body">

    <g:render template="/apptemplates/secondTemplates/recentActivity" model="[recentActivitys: recentActivitys]"/>

  </div>
</div>
</body>
</html>
