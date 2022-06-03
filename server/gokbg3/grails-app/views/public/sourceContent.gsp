<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Source Content</title>
</head>

<body>

<g:if test="${flash.error}">
    <semui:flashMessage data="${flash}"/>
</g:if>


<g:if test="${d}">

    <h1 class="ui header">Source: ${d.name}</h1>

    <div class="ui segment">
        <g:render template="rightBox"/>
        <g:render template="source"/>
    </div>

</g:if>

<div class="ui segment">
    <button class="ui right floated button black"
            onclick="window.history.back()">${message(code: 'default.button.back')}</button>
</div>

</body>
</html>
