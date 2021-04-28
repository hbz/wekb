<!DOCTYPE html>
<html>
	<head>
		<title>Grails Runtime Exception</title>
                <!-- II removed this - often the controller has not completed the setup needed for the main layout. meta name="layout" content="main" -->
		<link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
	</head>
	<body>
		<g:renderException exception="${exception}" />
		<div class="row justify-content-end">
			<button class="btn btn-default btn-primary mb-5" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
		</div>
	</body>
</html>
