<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="public_semui" />

<title><g:message code="gokb.appname" default="we:kb"/>: Platform Replacement</title>
</head>
<body>

		<h1 class="page-header">Replace Platform Complete</h1>
		<div id="mainarea" class="panel panel-default">
			<div class="panel-body">
				<h3>Process result:</h3>
			<ul>
				<li>${result['count']} TIPPs have successfully been updated to platform ${result['new']}.</li>
				<li> The following platforms have been retired:
					<ul>
						<g:each in="${result['old']}" var="o">
						<li>${o}</li>
						</g:each>

					</ul>
				</li>
			</ul>									
			</div>
		</div>
</body>


</html>

