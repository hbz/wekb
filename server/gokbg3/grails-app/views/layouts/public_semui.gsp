<%@ page import="de.wekb.helper.ServerUtils" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">
    <asset:script> var contextPath="${grailsApplication.config.server.contextPath ?: '/'}"; </asset:script>

    <asset:javascript src="wekb.js" />
    <asset:stylesheet src="wekb.css" />

    <title><g:message code="gokb.appname" default="we:kb"/></title>

</head>

<wekb:serviceInjection />
<g:set var="currentServer" scope="page" value="${ServerUtils.getCurrentServer()}"/>

<body id="page-body">
<wekb:serverlabel server="${currentServer}"/>

<main class="main ui container ${serverLabel}">

    <div class="ui top fixed inverted stackable menu">
        <a class="item" href="${grailsApplication.config.server.contextPath ?: ''}/">
            <img alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>
        </a>

        <div class="right menu">
            <g:if test="${grailsApplication.config.gokb.ygorUrl}">
                <a class="item" href="${grailsApplication.config.gokb.ygorUrl}" target="_blank">Ygor</a>
            </g:if>
            <g:link class="item" controller="home" action="index">Login</g:link>
        </div>
    </div>

    <g:layoutBody />


</main>


<g:render template="/layouts/footer_semui"/>

</body>

</html>
