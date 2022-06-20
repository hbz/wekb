<%@ page import="de.wekb.helper.ServerUtils;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Error</title>
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div style="font-size:2.5em;margin-top:100px;">
                ${message(code: "default.error.exception")}
            </div>

            <p><strong>${request.forwardURI}</strong></p>

            <g:if test="${exception}">
                <p>${exception.message}</p>
                <br />
            </g:if>
            <br>
            <button class="ui black button"
                    onclick="window.history.back()">${message(code: 'default.button.back')}</button>
        </div>
    </div>
</div>

<g:if test="${ServerUtils.getCurrentServer() == ServerUtils.SERVER_DEV}">
    <g:renderException exception="${exception}"/>
</g:if>
<g:elseif env="development">
    <g:renderException exception="${exception}"/>
</g:elseif>

</body>
</html>
