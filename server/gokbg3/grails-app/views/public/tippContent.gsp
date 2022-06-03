<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public_semui'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Title Content</title>
</head>

<body>

<g:if test="${flash.error}">
    <semui:flashMessage data="${flash}"/>
</g:if>

<g:if test="${d.id != null}">
    <h1 class="ui header">Title: ${d.name}</h1>

    <div class="ui segment">
        <g:render template="rightBox"/>
        <g:render template="tipp"/>
    </div>

    <g:render template="/templates/tabTemplates/domainTabs/tippTabs"/>

</g:if>
<div class="ui segment">
    <button class="ui right floated button black"
            onclick="window.history.back()">${message(code: 'default.button.back')}</button>
</div>
</body>
</html>
