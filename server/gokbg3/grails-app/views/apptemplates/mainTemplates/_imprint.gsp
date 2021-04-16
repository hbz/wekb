<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
  <dt>
          <gokb:annotatedLabel owner="${d}" property="name">Imprint Name</gokb:annotatedLabel>
  </dt>
  <dd>
          <gokb:xEditable  owner="${d}" field="name" />
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="org">Represented Org</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="org" baseClass="org.gokb.cred.Org">${d.org?.name}</gokb:manyToOneReferenceTypedown>
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="currentOwner">Current Owner</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.currentOwner}&nbsp;
  </dd>

  <dt class="dt-label">
    <gokb:annotatedLabel owner="${d}" property="owners">Owners</gokb:annotatedLabel>
  </dt>

  <dd>
    <table class="table table-striped table-bordered">
      <thead>
        <tr>
          <th>Owner Name</th>
          <th>Combo Status</th>
          <th>Owner From</th>
          <th>Owner To</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <g:each in="${d.getCombosByPropertyName('owners')}" var="p">
          <tr>
            <td><g:link controller="resource" action="show" id="${p.fromComponent.class.name}:${p.fromComponent.id}"> ${p.fromComponent.name} </g:link></td>
            <td><gokb:xEditableRefData owner="${p}" field="status" config="${RCConstants.COMBO_STATUS}" /></td>
            <td><gokb:xEditable  owner="${p}" field="startDate" type="date" /></td>
            <td><gokb:xEditable  owner="${p}" field="endDate" type="date" /></td>
            <td><g:link controller="ajaxSupport" action="deleteCombo" id="${p.id}">Delete</g:link></td>
          </tr>
        </g:each>
      </tbody>
    </table>
  </dd>
  <g:if test="${d.id}">
    <g:form controller="ajaxSupport" action="addToStdCollection" class="form-inline">
      <input type="hidden" name="__context" value="${d.class.name}:${d.id}" />
      <input type="hidden" name="__property" value="owners" />
      <dt class="dt-label">Add Owner:</td>
      <dd>
        <gokb:simpleReferenceTypedown class="form-inline select-ml" name="__relatedObject" baseClass="org.gokb.cred.Org" filter1="Current"/>&nbsp;<button type="submit" class="btn btn-default btn-primary">Add</button>
      </dd>
    </g:form>
  </g:if>
</dl>
