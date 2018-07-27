<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="sb-admin"/>
    <title>GOKb: About</title>
  </head>
  <body>
	 <h1 class="page-header">About GOKb</h1>
   <div id="mainarea"
		class="panel panel-default">
			<div class="panel-heading">
				<h3 class="panel-title">
					Application Info
				</h3>
			</div>
	  	<table class="table table-bordered">
	      <tr><th>Build Number</th><td> <g:meta name="app.buildNumber"/></td></tr>
	      <tr><th>Build Profile</th><td> <g:meta name="app.buildProfile"/></td></tr>
              <tr><th>Git Branch</th><td> <g:meta name="git.branch"/></td></tr>
              <tr><th>Git Commit</th><td> <g:meta name="git.commit.id"/></td></tr>
	      <tr><th>App version</th><td> <g:meta name="info.app.version"/></td></tr>
	      <tr><th>Grails version</th><td> <g:meta name="info.app.grailsVersion"/></td></tr>
	      <tr><th>Groovy version</th><td> ${GroovySystem.getVersion()}</td></tr>
	      <tr><th>Environment</th><td> <g:meta name="grails.env" /></td></tr>
	      <tr><th>JVM version</th><td> ${System.getProperty('java.version')}</td></tr>
	      <tr><th>Reloading active</th><td> ${grails.util.Environment.reloadingAgentEnabled}</td></tr>
	      <tr><th>Build Date</th><td> <g:meta name="build.time"/></td></tr>
	    </table>
  	</div>
  </body>
</html>
