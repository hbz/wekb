<%@ page import="de.wekb.helper.RCConstants" %>
<div id="content">

  <dl class="dl-horizontal">
    <dt> <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel> </dt>
    <dd> <gokb:xEditable  owner="${d}" field="name" /> </dd>
    <dt> <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel> </dt>
    <dd> <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" /> </dd>
    <dt> <gokb:annotatedLabel owner="${d}" property="status">Instances</gokb:annotatedLabel> </dt>
    <g:if test="${d.id != null && d.instances}">
      <dd> 
        <table class="table table-striped">
          <thead>
            <tr>
              <th>Title</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${d.instances}" var="i">
              <tr>
                <td><g:link controller="resource" action="show" id="${i.class.name}:${i.id}"> ${i.name} </g:link></td>
              </tr>
            </g:each>
          </tbody>
      </dd> 
    </g:if>
  </dl>
</div>
