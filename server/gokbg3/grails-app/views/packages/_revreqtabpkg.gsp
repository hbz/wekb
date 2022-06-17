<%@ page import="de.wekb.helper.RCConstants" %>
<h4>Showing <b>${d.restriction}</b> Requests (${d.reviewRequests?.size() ?: '0'})</h4>

<table class="table table-bordered">
  <thead>
    <tr>
      <th>Cause</th>
      <th>Request</th>
      <th>Component</th>
      <th>Status</th>
      <th>Date Created</th>
    </tr>
  </thead>
  <tbody>
    <g:each in="${d.reviewRequests}" var="rr">
      <tr>
        <td>
          <g:link controller="resource" action="show" id="org.gokb.cred.ReviewRequest:${rr.id}">${rr.descriptionOfCause}</g:link>
        </td>
        <td>
          <g:link controller="resource" action="show" id="org.gokb.cred.ReviewRequest:${rr.id}">${rr.reviewRequest}</g:link>
        </td>
        <td>
          <g:link controller="resource" action="show" id="org.gokb.cred.KBComponent:${rr.componentToReview.id}">${rr.componentToReview?.name}</g:link>
        </td>
        <td>
          <semui:xEditableRefData owner="${rr}" field="status" config="${RCConstants.REVIEW_REQUEST_STATUS}" />
        </td>
        <td>
          ${rr.dateCreated}
        </td>
      </tr>
    </g:each>
  </tbody>
</table>
