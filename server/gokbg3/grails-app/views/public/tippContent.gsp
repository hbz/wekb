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

            <div class="col-lg-12 card">

                    <g:if test="${flash.error}">
                        <div class="alert alert-warning" style="font-weight:bold;">
                            <p>${flash.error}</p>
                        </div>
                    </g:if>


                    <g:if test="${tipp}">

                        <h1>Title: <span style="font-weight:bolder;">${tipp.title.name}</span></h1>

                        <g:render template="rightBox"
                                  model="${[d: tipp.pkg]}"/>

                        <g:render template="/apptemplates/mainTemplates/tipp"
                                  model="${[d: tipp]}"/>

                    </g:if>
            </div>
        </div>
    </div>

</div>
</body>
</html>
