<%@ page import="org.gokb.cred.RefdataValue; de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory; de.wekb.helper.RDStore;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public_semui'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Package Content</title>
</head>

<body>

<g:if test="${flash.error}">
    <semui:flashMessage data="${flash}"/>
</g:if>
<g:if test="${d}">

    <h1 class="ui header">Package: ${d.name}</h1>

    <div class="ui segment">
        <g:render template="rightBox" model="${[d: d]}"/>

        <div class="content">
            <g:render template="package"/>
    </div>

</g:if>

<g:render template="/templates/tabTemplates/domainTabs/packageTabs"/>

<div class="ui segment">
    <button class="ui right floated button black"
            onclick="window.history.back()">${message(code: 'default.button.back')}</button>
</div>

</body>
</html>
