<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Accessibility Feedback Form</title>
</head>

<body>

<div class="container">
        <h1 class="mb-4">Accessibility Feedback Form</h1>

        <g:form action="sendFeedbackForm" controller="public" method="get" class="ui small form">
        <div class="form-group row">
            <label for="name" class="col-sm-2 col-form-label">Name</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" id="name" name="name" placeholder="Name" value=""/>
            </div>
        </div>
        <div class="form-group row">
            <label for="eMail" class="col-sm-2 col-form-label">E-Mail</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" name="eMail" id="eMail" placeholder="E-Mail" value=""/>
            </div>
        </div>
        <div class="form-group row">
            <label for="url" class="col-sm-2 col-form-label">URL of the page you are commenting on</label>
            <div class="col-sm-10">
                <input class="form-control" type="text" name="url" id="url" placeholder="url" value=""/>
            </div>
        </div>

        <div class="form-group row">
            <label for="comment" class="col-sm-2 col-form-label">Comment</label>
            <div class="col-sm-10">
                <g:textArea  class="form-control"  name="comment"  id="comment" rows="5" cols="40"/>
            </div>
        </div>
        <div class="form-group row justify-content-end">
            <div class="col-4">
                <div class="float-right">
                    <a href="${request.forwardURI}" class="btn btn-dark">Reset</a>
                    <input type="submit" class="btn btn-primary " value="Send">
                </div>
            </div>
        </div>
        </g:form>

</div>
</body>
</html>
