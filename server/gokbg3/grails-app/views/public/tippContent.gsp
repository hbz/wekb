
<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Tipp Content</title>
</head>

<body>

<div class="container">


    <g:if test="${flash.error}">
        <div class="alert alert-warning" style="font-weight:bold;">
            <p>${flash.error}</p>
        </div>
    </g:if>


    <g:if test="${tipp}">

        <h1>Title: <span style="font-weight:bolder;">${tipp.title.name}</span></h1>
        <div class="row">
            <div class="col-sm-9">
                <g:render template="tipp"  model="${[d: tipp]}"/>
            </div>

            <g:render template="rightBox" model="${[d: tipp.pkg]}"/>
        </div>
        <g:render template="tippTabs"  model="${[d: tipp]}"/>



    </g:if>


</div>
</body>
</html>
