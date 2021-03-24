<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Tipp Content</title>
</head>

<body>

<div class="container">

    <div class="row">
        <div class="box">

            <div class="col-lg-12 ">
                <div class="well">

                    <g:if test="${flash.error}">
                        <div class="alert alert-warning" style="font-weight:bold;">
                            <p>${flash.error}</p>
                        </div>
                    </g:if>


                    <g:if test="${tipp}">

                        <h1>Title: <span style="font-weight:bolder;">${tipp.title.name}</span></h1>

                        <div class="col-xs-3 pull-right well" style="min-width:320px;">
                            <h2>Curated By</h2>
                            <ul>
                                <g:each in="${tipp.pkg.curatoryGroups}" var="cg">
                                    <li>${cg.name}</li>
                                </g:each>
                            </ul>

                            <div style="margin-top:10px;">
                                <g:link controller="resource" action="show"
                                        id="${tipp.uuid}">Switch to editing view (Login required)</g:link>
                            </div>
                        </div>

                        <g:render template="/apptemplates/mainTemplates/tipp"
                                  model="${[d: tipp]}"/>

                    </g:if>

                </div>
            </div>
        </div>
    </div>

</div>
</body>
</html>
