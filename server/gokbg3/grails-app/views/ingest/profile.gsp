<%@ page import="de.wekb.helper.RCConstants" %>
<html>
  <head>
    <meta name="layout" content="sb-admin" />
    <title><g:message code="gokb.appname" default="we:kb"/> Direct Ingest Service</title>
  </head>
  <body>
    <g:if test="${ip?.id}">
      <h1 class="page-header">Direct File Ingest :: ${ip.name}</h1>
      <div class="col-md-12" >
        <div id="mainarea" class="panel panel-default">
          <div class="panel-heading">
            <h3 class="panel-title">Profile Information</h3>
          </div>
          <div class="panel-body">
            <dl class="dl-horizontal">
              <dt>
                <gokb:annotatedLabel owner="${ip}" property="name">Profile Name</gokb:annotatedLabel>
              </dt>
              <dd>
                ${ip.name}
              </dd>
              <dt>
                <gokb:annotatedLabel owner="${ip}" property="status">Status</gokb:annotatedLabel>
              </dt>
              <dd>
                <gokb:xEditableRefData owner="${ip}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" />
              </dd>
              <dt>
                <gokb:annotatedLabel owner="${ip}" property="packageName">Package Name</gokb:annotatedLabel>
              </dt>
              <dd>
                ${ip.packageName}
              </dd>
              <dt>
                <gokb:annotatedLabel owner="${ip}" property="providerNamespace">Provider Namespace</gokb:annotatedLabel>
              </dt>
              <dd>
                <gokb:manyToOneReferenceTypedown owner="${ip}" field="providerNamespace" baseClass="org.gokb.cred.IdentifierNamespace">${ip.providerNamespace?.value}</gokb:manyToOneReferenceTypedown>
              </dd>
              <dt>
                <gokb:annotatedLabel owner="${ip}" property="packageType">Data Format</gokb:annotatedLabel>
              </dt>
              <dd>
                ${ip.packageType}
              </dd>
              <dt>
                <gokb:annotatedLabel owner="${ip}" property="platformUrl">URL</gokb:annotatedLabel>
              </dt>
              <dd>
                <gokb:xEditable class="ipe" owner="${ip}" field="platformUrl" />
              </dd>
            </dl>
          </div>
        </div>
        <div id="mainarea" class="panel panel-default">
          <div class="panel-heading">
            <h3 class="panel-title">Ingest Latest Revision Of File</h3>
          </div>
          <div class="panel-body">
            <g:form controller="ingest" action="profile" id="${ip.id}" method="post" enctype="multipart/form-data" params="${params}">
              <div class="input-group" >
                <span class="input-group-btn">
                  <span class="btn btn-default btn-file">
                    Browse <input type="file" id="submissionFile" name="submissionFile"  onchange='$("#upload-file-info").html($(this).val());' />
                  </span>
                </span>
                <span class='form-control' id="upload-file-info"><label for="submissionFile" >Select a file...</label></span>
                <span class="input-group-btn">
                  <button type="submit" class="btn btn-primary">Upload</button>
                </span>
              </div>
            </g:form>
          </div>
        </div>
        <div id="mainarea" class="panel panel-default">
          <div class="panel-heading">
            <h3 class="panel-title">Ingest History</h3>
          </div>
          <div class="panel-body">
          </div>
        </div>
      </div>
    </g:if>
  </body>
</html>
