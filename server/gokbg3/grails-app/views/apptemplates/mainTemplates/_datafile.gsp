<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
  <dt> <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel> </dt>
  <dd> <gokb:xEditable  owner="${d}" field="name" /> </dd>
</dl>

<div id="content">
  <ul id="tabs" class="nav nav-tabs">
    <li class="active"><a href="#details" data-toggle="tab">Datafile Details</a></li>
    %{--<li><a href="#addprops" data-toggle="tab">Additional Properties <span class="badge badge-warning">${d.additionalProperties?.size()}</span></a></li>--}%
    <li><a href="#review" data-toggle="tab">Review Requests <span class="badge badge-warning">${d.reviewRequests?.size()}</span></a></li>
  </ul>
  <div id="my-tab-content" class="tab-content">
    <div class="tab-pane active" id="details">
      <dl class="dl-horizontal">
        <dt> <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" /> </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="canEdit">Editable</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditableRefData owner="${d}" field="canEdit" config="${RCConstants.YN}" /> </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="guid">GUID</gokb:annotatedLabel> </dt>
        <dd> ${d.guid} </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="md5">MD5</gokb:annotatedLabel> </dt>
        <dd> ${d.md5} </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="uploadFilename">Upload Filename</gokb:annotatedLabel> </dt>
        <dd> ${d.uploadName} </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="mime">Upload Mime Type</gokb:annotatedLabel> </dt>
        <dd> ${d.uploadMimeType} </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="filesize">Filesize</gokb:annotatedLabel> </dt>
        <dd> ${d.filesize} </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="doctype">Doctype</gokb:annotatedLabel> </dt>
        <dd> ${d.doctype} </dd>
        <dt> <gokb:annotatedLabel owner="${d}" property="fileData">File</gokb:annotatedLabel> </dt>
        <dd> <g:link controller="workflow" action="download" id="${d.guid}">  Download file </g:link></dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="attachedTo">Attached To</gokb:annotatedLabel> </dt>
        <dd>
          <table class="table table-striped table-bordered">
            <thead>
              <tr>
                <th>Component</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${d.incomingCombos}" var="r">
                <g:set var="linkedoid"
                  value="${org.gokb.cred.KBComponent.deproxy(r.fromComponent).class.name}:${r.fromComponent.id}" />
                <tr>
                  <td><g:link controller="resource" action="show"
                      id="${linkedoid}">
                      ${r.fromComponent.name}
                    </g:link></td>
                </tr>
              </g:each>
            </tbody>
          </table>
        </dd>
      </dl>
    </div>

    %{--<div class="tab-pane" id="addprops">
       <g:render template="/apptemplates/secondTemplates/addprops" model="${[d:d]}" />
    </div>--}%

    <div class="tab-pane" id="review">
      <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d:d]}" />
    </div>


  </div>

  <g:render template="/apptemplates/secondTemplates/componentStatus"/>

</div>


