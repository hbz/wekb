<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
        <dt>
          Internal ID
        </dt>
        <dd>
          ${d.id?:'New record'}&nbsp;
        </dd>
        <dt>
          Type
        </dt>
        <dd>
          ${d.stdDesc ?  d.stdDesc.value : 'None'}
        </dd>
        <dt>
          Cause
        </dt>
        <dd style="max-width:60%">
          <semui:xEditable  owner="${d}" field="descriptionOfCause" />
        </dd>
        <dt>
          Review Request
        </dt>
        <dd>
          <semui:xEditable  owner="${d}" field="reviewRequest" />
        </dd>
     </dl>
<div id="content">
  <ul id="tabs" class="nav nav-tabs">
    <li class="active"><a href="#rrdets" data-toggle="tab">Review Request Details</a></li>
  </ul>
  <div id="my-tab-content" class="tab-content">
    <div class="tab-pane active" id="rrdets">
      <dl class="dl-horizontal">
        <dt>
          Request Status
        </dt>
        <dd>
          <semui:xEditableRefData owner="${d}" field="status"
            config="${RCConstants.REVIEW_REQUEST_STATUS}" />
        </dd>

        <dt>
          Component
        </dt>

        <dd>
          <semui:xEditableManyToOne owner="${d}"
            field="componentToReview" baseClass="org.gokb.cred.KBComponent">
            ${d.componentToReview?.displayName?:''}
          </semui:xEditableManyToOne>
        </dd>
        <g:if test="${d.additional?.otherComponents}">

          <dt>
            Other Relevant Components
          </dt>
          <dd>
            <ul>
              <g:each in="${d.additional?.otherComponents}" var="oc">
                <li>
                  <g:link controller="resource" action="show" id="${oc.oid}">${oc.name}</g:link>
                </li>
              </g:each>
            </ul>
          <dd>
        </g:if>
        <g:if test="${d.additional?.skippedItems}">

          <dt>
            Skipped Items
          </dt>
          <dd>
            <ul>
              <g:each in="${d.additional?.skippedItems}" var="si">
                <li>
                  ${si.name ?: si.title.name}
                </li>
              </g:each>
            </ul>
          <dd>
        </g:if>
        <sec:ifAnyGranted roles="ROLE_SUPERUSER">
          <g:if test="${d.id != null}">
            <dt>
              Request Timestamp
            </dt>
            <dd>
              ${d.dateCreated}&nbsp;
            </dd>
          </g:if>
        </sec:ifAnyGranted>

        <dt>
          Allocated Groups
        </dt>
        <dd>
          <g:each in="${d.allocatedGroups}" var="ag">
            <g:link controller="resource" class="badge badge-primary" action="show" id="${ag.group.uuid}">${ag.group.name}</g:link>
          </g:each>
        </dd>

        <g:if test="${d.additional?.problems}">
          <g:form name="AddRules" controller="workflow" action="addToRulebase">
            <input type="hidden" name="sourceName" value="${d.additional.sourceName}"/>
            <input type="hidden" name="sourceId" value="${d.additional.sourceId}"/>
            <input type="hidden" name="prob_seq_count" value="${d.additional.probcount}" />
            <table class="ui selectable striped sortable celled table">
              <thead>
                <tr>
                  <th>Specific Problems</th>
                  <th>Possible Resolutions</th>
                </tr>
              </thead>
              <tbody>
                <g:each in="${d.additional?.problems}" var="revreq_problem" status="i">
                  <tr>
                   <td>
                     <input type="hidden" name="pr.prob_res_${revreq_problem?.problemSequence}.probfingerprint" value='${revreq_problem?.problemFingerprint.encodeAsHTML()}' />
                     <input type="hidden" name="pr.prob_res_${revreq_problem?.problemSequence}.probcode" value='${revreq_problem?.problemCode}' />
                     <input type="hidden" name="pr.prob_res_${revreq_problem?.problemSequence}.idstr" value='${revreq_problem?.submittedIdentifiers?.encodeAsHTML()}' />
                     <input type="hidden" name="pr.prob_res_${revreq_problem?.problemSequence}.title" value='${revreq_problem?.submittedTitle?.encodeAsHTML()}' />
                     <h2>${revreq_problem.problemCode}</h2>
                     <p>${revreq_problem.problemDescription}</p>
                   </td>
                   <td>
                     <g:render template="/reviewRequestCases/${revreq_problem.problemCode}"  model="${[d:d, prob:revreq_problem, status:i]}" />
                   </td>
                  </tr>
                </g:each>
              </tbody>
            </table>

            <button type="submit" class="btn btn-success pull-right">Add/Update Selected Rules -></button>
          </g:form>
        </g:if>
      </dl>
    </div>
  </div>

  <div id="modal" class="qmodal modal fade" role="dialog">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
          <h3 class="modal-title">Modal header</h3>
        </div>
        <div class="modal-body"></div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</div>
