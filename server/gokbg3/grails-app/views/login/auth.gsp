<g:set var='securityConfig' value='${applicationContext.springSecurityService.securityConfig}'/>
<html>
<head>
  <meta name="layout" content="public"/>
  <s2ui:title messageCode='springSecurity.login.header'/>
  <asset:stylesheet src='spring-security-ui-auth.css'/>
</head>
<body>
  <div class="container">
    <div class="row justify-content-md-center">
      <div class="col-lg-6">
            <g:form class="card p-5 m-5" controller="login" action="authenticate" method="post" name="loginForm" elementId="loginForm" autocomplete="off">
            <h2>Login</h2>
              <g:if test="${params.login_error}">
                <div class="alert alert-danger"><g:message code='springSecurity.login.error.message'/></div>
              </g:if>
               <div class="form-group">
                 <label for="username"><g:message code='springSecurity.login.username.label'/></label>
                 <input type="text" class="form-control" id="username" aria-describedby="usernameHelp" placeholder="Username" name="${securityConfig.apf.usernameParameter}">
               </div>

               <div class="form-group">
                 <label for="password"><g:message code='springSecurity.login.password.label'/></label>
                 <input type="password" class="form-control" id="password" aria-describedby="passwordHelp" placeholder="" name="${securityConfig.apf.passwordParameter}">
               </div>

               <!-- input type="checkbox" class="checkbox" name="${securityConfig.rememberMe.parameter}" id="remember_me" checked="checked"-->
               <!-- label for='remember_me'><g:message code='spring.security.ui.login.rememberme'/></label -->
               <button class="btn btn-primary" type="submit">Login</button>
                <small ><g:link controller="register" action="forgotPassword"><g:message code="spring.security.ui.login.forgotPassword" /></g:link></small>

                <small ><a href data-toggle="modal" data-cache="false"
                                                    data-target="#infoModal">Not yet registered for a <g:message code="gokb.appname" default="we:kb"/>: account?</a></small>
            </g:form>
      </div>
    </div>
  </div>

<div id="infoModal" class="modal" role="dialog" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h3 class="modal-title">Not yet registered for a <g:message code="gokb.appname" default="we:kb"/>: account?</h3>
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
            </div>

            <div class="modal-body">Contact us at <a href="mailto:laser@hbz-nrw.de">laser@hbz-nrw.de</a> so that we can set up an account for you and provide you with your initial login information.</div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
