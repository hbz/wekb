<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">

  <dt>Curatory Group Name</dt>
  <dd><semui:xEditable  owner="${d}" field="name" /></dd>

	<dt>Type</dt>
	<dd><semui:xEditableRefData owner="${d}" field="type" config="${RCConstants.CURATORY_GROUP_TYPE}" /></dd>

  <g:if test="${d.id != null}">

	  <dt>Status</dt>
	  <dd><semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" /></dd>

		<sec:ifAnyGranted roles="ROLE_ADMIN">
			<dt>
				Owner
			</dt>
			<dd>
				<semui:xEditableManyToOne owner="${d}" field="owner" baseClass="org.gokb.cred.User">${d.owner?.username}</semui:xEditableManyToOne>
			</dd>
		</sec:ifAnyGranted>
		<g:if test="${ user.isAdmin() || d.owner == user }">
	  	<dt>Members</dt>
			<dd>
				<g:if test="${ d.users }" >
					<ul>
						<g:each var="u" in="${ d.users }" >
													<sec:ifAnyGranted roles="ROLE_ADMIN">
														<li><a href="mailto:${ u.email }" ><i class="fa fa-envelope"></i>&nbsp;</a><g:link controller="resource" action="show" id="${u.getLogEntityId()}">${u.displayName ?: u.username}</g:link></li>
													</sec:ifAnyGranted>
													<sec:ifNotGranted roles="ROLE_ADMIN">
														<li>${u.displayName ?: u.username}</li>
													</sec:ifNotGranted>
						</g:each>
					</ul>
				</g:if>
				<g:else>
					<p>There are no members of this curatory group.</p>
				</g:else>
			</dd>
		</g:if>
  </g:if>
</dl>
