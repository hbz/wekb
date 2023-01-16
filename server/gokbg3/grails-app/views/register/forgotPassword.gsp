<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: <g:message
            code='spring.security.ui.forgotPassword.title'/></title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<g:if test='${emailSent}'>
    <semui:message>
        <g:message code="spring.security.ui.forgotPassword.sent"/>
    </semui:message>
</g:if>

<g:if test="${!emailSent}">
    <div class="ui middle aligned center aligned grid">
        <div class="six wide column">
            <h2 class="ui header">
                <div class="content">
                    <g:message code='spring.security.ui.forgotPassword.title'/>
                </div>
            </h2>
            <g:form action='resetForgottenPassword' name="forgotPasswordForm" autocomplete='off' class="ui form">

                <div class="ui stacked segment">
                    <div class="field">
                        <div class="ui left icon input">
                            <i class="user icon"></i>
                            <g:textField id="fgp_username" placeholder="Requested Username" name="fgp_username" size="25"/>
                        </div>
                    </div>

                    <button class="ui fluid submit black button" type="submit">Send Password Recovery Email...</button>
                </div>

            </g:form>

            <div class="ui message">
                <a href="#" onclick="$('#infoModal').modal('show');">Not yet registered for a <g:message
                        code="gokb.appname"
                        default="we:kb"/>: account?</a>
            </div>
        </div>
    </div>

    <semui:modal id="infoModal" title="Info" hideSubmitButton="true">
        Contact us at <a
            href="mailto:laser@hbz-nrw.de">laser@hbz-nrw.de</a> so that we can set up an account for you and provide you with your initial login information.
    </semui:modal>

</g:if>

</body>
</html>
