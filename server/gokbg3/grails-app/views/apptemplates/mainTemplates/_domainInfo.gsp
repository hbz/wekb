<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
	<dt>
		<gokb:annotatedLabel owner="${d}" property="domainClassName">Domain Class Name</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable  owner="${d}" field="dcName" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="displayName">Display Name</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable  owner="${d}" field="displayName" />
	</dd>

	<dt>
		<gokb:annotatedLabel owner="${d}" property="dcSortOrder">Sort Order</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable  owner="${d}" field="dcSortOrder" />
	</dd>

	<dt>
		<gokb:annotatedLabel owner="${d}" property="type">Type/Category</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditableRefData owner="${d}" field="type" config="${RCConstants.DC_TYPE}" />
	</dd>
</dl>

<div id="content">
	<ul id="tabs" class="nav nav-tabs">
		<li class="active"><a href="#role-permissions" data-toggle="tab">Role Permissions</a></li>
	</ul>
	<div id="my-tab-content" class="tab-content">
		<div class="tab-pane active" id="role-permissions">
			<g:link class="display-inline" controller="security"
				action="rolePermissions"
				params="${['id': (d.class.name + ':' + d.id) ]}"></g:link>
		</div>
	</div>
</div>
