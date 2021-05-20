
<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Organization Content</title>
</head>

<body>

<div class="container">


    <g:if test="${flash.error}">
        <div class="alert alert-warning" style="font-weight:bold;">
            <p>${flash.error}</p>
        </div>
    </g:if>


    <g:if test="${org}">

        <h1>Organization: <span style="font-weight:bolder;">${org.name}</span></h1>
        <div class="row">
            <div class="col-md-9">
                <g:render template="org"  model="${[d: org]}"/>
            </div>

            <g:render template="rightBox" model="${[d: org]}"/>
        </div>
        <g:render template="/tabTemplates/orgTabs" model="${[d: org]}"/>

        <g:render template="componentStatus"
                  model="${[d: org]}"/>

    </g:if>
    <div class="row justify-content-end">
            <button class="btn btn-default btn-primary mb-5" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
    </div>
</div>
</body>
</html>
