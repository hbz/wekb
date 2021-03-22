<g:set var="editable" value="${ d.isEditable() || d.isAdministerable() }" />
<g:if test="${d.id != null}">
  <dl class="dl-horizontal">
    <dt><gokb:annotatedLabel owner="${d}" property="username">User Name</gokb:annotatedLabel></dt>
    <dd>${d.username}</dd>
  
    <dt><gokb:annotatedLabel owner="${d}" property="displayName">Display Name</gokb:annotatedLabel></dt>
    <dd><gokb:xEditable class="ipe" owner="${d}" field="displayName"/></dd>
    <g:if test="${d == request.user || request.user.isAdmin()}">
      <dt><gokb:annotatedLabel owner="${d}" property="email">Email</gokb:annotatedLabel></dt>
      <dd>
        <gokb:xEditable class="ipe" owner="${d}" field="email"/>
      </dd>
    </g:if>
    %{--<dt><gokb:annotatedLabel owner="${d}" property="curatoryGroups">Curatory Groups</gokb:annotatedLabel></dt>
    <dd>
       <g:render template="/apptemplates/secondTemplates/curatory_groups" model="${[d:d]}" />
    </dd>--}%

    <dt><gokb:annotatedLabel owner="${d}" property="org">Organisations</gokb:annotatedLabel></dt>
    <dd>
      <ul>
        <g:each in="${d.getGroupMemberships()}" var="oum">
          <li><g:link controller="resource" action="show" id="${oum.memberOf.class.name}:${oum.memberOf.id}">${oum.memberOf.displayName} – (${oum.role?.value ?: 'Not Set'})</g:link></li>
        </g:each>
      </ul>
    </dd>

    <dt><gokb:annotatedLabel owner="${d}" property="last_alert_check">Last Alert Check</gokb:annotatedLabel></dt>
    <dd>
      <g:if test="${d.last_alert_check}"><g:formatDate format="${session.sessionPreferences?.globalDateFormat}" date="${d.last_alert_check}" /></g:if>
      <g:else>Never</g:else>
    </dd>

  </dl>
  <div id="content">
    <ul id="tabs" class="nav nav-tabs">
      <li class="active"><a href="#roles" data-toggle="tab">Roles</a></li>
    </ul>
    <div id="my-tab-content" class="tab-content">
      <div class="tab-pane active" id="roles">
        <g:link class="display-inline" controller="security" action="roles" params="${['id': (d.class.name + ':' + d.id) ]}"></g:link>
      </div>
    </div>
  </div>
</g:if>
