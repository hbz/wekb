<%@ page import="de.wekb.helper.ServerUtils;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>:Error</title>
</head>

<body>
<div class="ui segment">
    <h3 class="ui header">${message(code: "default.error.exception")}</h3>

    <p><strong>${request.forwardURI}</strong></p>

    <g:if test="${exception}">
        <p>${exception.message}</p>
        <br/>
    </g:if>
    <br>
    <button class="ui black button"
            onclick="window.history.back()">${message(code: 'default.button.back')}</button>

</div>

<g:if test="${ServerUtils.getCurrentServer() == ServerUtils.SERVER_DEV}">
    <g:renderException exception="${exception}"/>
</g:if>
<g:elseif env="development">
    <g:renderException exception="${exception}"/>
</g:elseif>

</body>
</html>
