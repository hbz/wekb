<%@ page import="de.wekb.helper.RCConstants" %>
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="sb-admin"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: My Preferences</title>
  </head>
  <body>  
  <h1 class="page-header">My Preferences</h1>
   <div id="mainarea"
    class="panel panel-default">
      <div class="panel-body" >
        <dl class="dl-horizontal">

	        <dt>Show Info Icon :</dt>
	        <dd>
	          <gokb:xEditableRefData owner="${user}" field="showInfoIcon"
	            config="${RCConstants.YN}" />
	        </dd>

	        <dt>Show Quick View :</dt>
	        <dd>
	          <gokb:xEditableRefData owner="${user}" field="showQuickView" config="${RCConstants.YN}" />
	        </dd>

	        <dt>Default Page Size :</dt>
	        <dd><gokb:xEditable owner="${user}" field="defaultPageSize" /></dd>

	        <dt>Send Alert Emails :</dt>
	        <dd><gokb:xEditableRefData owner="${user}" field="send_alert_emails" config="${RCConstants.YN}" /></dd>
	      </dl>
      </div>
    </div>
</body>
</html>
