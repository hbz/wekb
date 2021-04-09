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
    <g:layoutHead />
    <asset:javascript src="gokb/application-public.js" />
%{--    <asset:stylesheet src="gokb/themes/${ grailsApplication.config.gokb.theme }/theme.css"/>--}%
    <asset:stylesheet src="gokb/bootstrap-yeti.min.css"/>
    <asset:stylesheet src="gokb/fontawesome.css" />
    <asset:stylesheet src="gokb/application.css"/>

    <title><g:message code="gokb.appname" default="we:kb"/></title>

    <!-- Fonts -->
    <link href="https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800" rel="stylesheet" type="text/css">
    <link href="https://fonts.googleapis.com/css?family=Josefin+Slab:100,300,400,600,700,100italic,300italic,400italic,600italic,700italic" rel="stylesheet" type="text/css">

    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
        <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->

</head>

<wekb:serviceInjection />
<g:set var="currentServer" scope="page" value="${ServerUtils.getCurrentServer()}"/>

<body id="page-body" class="theme-${ grailsApplication.config.gokb.theme }">

<g:if test="${currentServer == ServerUtils.SERVER_DEV}">
    <div class="text-success label big wb-server-label">
        <span>DEV</span>
    </div>
</g:if>
<g:if test="${currentServer == ServerUtils.SERVER_QA}">
    <div class="text-danger label big wb-server-label">
        <span>QA</span>
    </div>
</g:if>
<g:if test="${currentServer == ServerUtils.SERVER_LOCAL}">
    <div class="text-primary wb-server-label">
        <span>LOCAL</span>
    </div>
</g:if>

    <nav class="navbar navbar-expand-sm navbar-dark bg-dark" id="primary-nav-bar" role="navigation">
     <div class="container">
         <div class="d-flex flex-grow-1">
             <a class="navbar-brand navbar-image mekb-logo" href="${grailsApplication.config.server.contextPath ?: ''}/" >
                 <img  alt="Logo wekb"  src="${resource(dir: 'images', file: 'logo.svg')}"/>
             </a>
         </div>
         <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExample03" aria-controls="navbarsExample03" aria-label="Toggle navigation">
             <span class="navbar-toggler-icon"></span>
         </button>



        <div class="collapse navbar-collapse flex-grow-1 text-right">
           <ul class="navbar-nav ml-auto flex-nowrap">
              <g:if test="${grailsApplication.config.gokb.ygorUrl}">
                <li class="nav-item"><a class="nav-link" href ="${grailsApplication.config.gokb.ygorUrl}">Ygor</a></li>
              </g:if>

              <li class="nav-item"><g:link class="nav-link" controller="home" action="index">Login</g:link></li>
              %{--<g:if test="${grailsApplication.config.gokb.uiUrl}">
                <li><a style="font-weight:bold;" href ="${grailsApplication.config.gokb.uiUrl}">GOKb Client</a></li>
              </g:if>--}%
           </ul>
        </div>

     </div>
   </nav>




    <g:layoutBody />

    <g:render template="/layouts/footer"   />

</body>

</html>
