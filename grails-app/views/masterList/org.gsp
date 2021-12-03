<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="sb-admin"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Master Lists</title>
  </head>
  <body>
	  <h1 class="page-header">
	    Master lists for <g:render template="/apptemplates/secondTemplates/component_heading" model="${[d:o]}" />
	  </h1>
    <div id="mainarea" class="panel panel-default">
      <div class="panel-body">
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Package</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${org_packages}" var="pkg">
              <tr>
                <td>${pkg.name}</td>
              </tr>
            </g:each>
          </tbody>
        </table>

        <table class="table table-striped">
          <thead>
            <tr>
	            <th></th>
	            <th>Titles</th>
	            <th>Package</th>
	            <th>Platform</th>
	          </tr>
          </thead>
          <tbody>
            <g:each in="${tipps}" var="tipp">
              <tr>
                <td><g:link controller="resource" action="show" id="${tipp.class.name}:${tipp.id}">tipp ${tipp.id}</g:link></td>
                <td>${tipp.title.name}</td>
                <td>${tipp.pkg.name}</td>
                <td>${tipp.hostPlatform.name}</td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>
