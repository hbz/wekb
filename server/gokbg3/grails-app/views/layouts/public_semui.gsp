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
    <asset:script>var contextPath="${grailsApplication.config.server.contextPath ?: '/'}";</asset:script>

    <asset:javascript src="wekb.js"/>
    <asset:stylesheet src="wekb.css"/>

    <title><g:message code="gokb.appname" default="we:kb"/></title>

</head>

<wekb:serviceInjection/>
<g:set var="currentServer" scope="page" value="${ServerUtils.getCurrentServer()}"/>

<body id="page-body">
<wekb:serverlabel server="${currentServer}"/>

<div class="ui top fixed inverted stackable menu">
    <div class="ui container">
        <img alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>

        <div class="right menu">
            <g:if test="${grailsApplication.config.gokb.ygorUrl}">
                <div class="item">
                    <a class="ui inverted button" href="${grailsApplication.config.gokb.ygorUrl}"
                       target="_blank">Ygor</a>
                </div>
            </g:if>
            <div class="item">
                <g:link class="ui inverted button" controller="home" action="index"><i class="sign-in icon"></i>Login</g:link>
            </div>
        </div>
    </div>
</div>

<main class="ui main container ${serverLabel}">
    <br>
    <br>
    <br>
    <g:layoutBody/>

</main>


<g:render template="/layouts/footer_semui"/>

</body>

</html>
