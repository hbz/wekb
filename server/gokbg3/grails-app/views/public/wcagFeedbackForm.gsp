<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Accessibility Feedback Form</title>
</head>

<body>

<div class="container">
        <h1>Accessibility Feedback Form</h1>
        <div class="ui  segment">
            <g:form action="sendFeedbackForm" controller="public" method="get" class="ui small form">
                <div class="field">
                    <label>Name</label>

                    <div class="ui input">
                        <input type="text" name="name"
                               placeholder="Name"
                               value=""/>
                    </div>
                </div>

                <div class="field">
                    <label>E-Mail-Adresse</label>

                    <div class="ui input">
                        <input type="text" name="email"
                               placeholder="E-Mail-Adresse"
                               value=""/>
                    </div>
                </div>

                <div class="field">
                    <label>URL der Seite, die Sie kommentieren</label>

                    <div class="ui input">
                        <input type="text" name="url"
                               placeholder="URL der Seite, die Sie kommentieren"
                               value=""/>
                    </div>
                </div>

                <div class="field">
                    <label>Kommentar</label>

                    <g:textArea name="comment"  rows="5" cols="40"/>

                </div>

                <div class="field la-field-right-aligned ">
                    <a href="${request.forwardURI}" class="ui reset primary button">${message(code:'default.button.reset.label')}</a>
                    <input type="submit" class="ui secondary button" value="Abschicken">
                </div>

            </g:form>
        </div>

</div>
</body>
</html>
