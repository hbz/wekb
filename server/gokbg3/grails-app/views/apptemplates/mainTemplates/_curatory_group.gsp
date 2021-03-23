<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">

  <dt><gokb:annotatedLabel owner="${d}" property="name">Curatory Group Name</gokb:annotatedLabel></dt>
  <dd><gokb:xEditable class="ipe" owner="${d}" field="name" /></dd>

  <g:if test="${d.id != null}">

	  <dt><gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel></dt>
	  <dd><gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" /></dd>

	  <dt><gokb:annotatedLabel owner="${d}" property="editStatus">Edit Status</gokb:annotatedLabel></dt>
	  <dd><gokb:xEditableRefData owner="${d}" field="editStatus" config="${RCConstants.KBCOMPONENT_EDIT_STATUS}" /></dd>
		<sec:ifAnyGranted roles="ROLE_ADMIN">
			<dt>
				<gokb:annotatedLabel owner="${d}" property="owner">Owner</gokb:annotatedLabel>
			</dt>
			<dd>
				<gokb:manyToOneReferenceTypedown owner="${d}" field="owner" baseClass="org.gokb.cred.User">${d.owner?.username}</gokb:manyToOneReferenceTypedown>
			</dd>
		</sec:ifAnyGranted>
		<g:if test="${ user.isAdmin() || d.owner == user }">
	  	<dt><gokb:annotatedLabel owner="${d}" property="users">Members</gokb:annotatedLabel></dt>
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
