<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
  <title><g:message code="gokb.appname" default="we:kb"/>:Error</title>
</head>

<body>
<div class="ui segment">
    <h3 class="ui header">
        ${message(code: "default.error.exception")}
    </h3>

    <button class="ui black button" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
</div>
</body>
</html>
