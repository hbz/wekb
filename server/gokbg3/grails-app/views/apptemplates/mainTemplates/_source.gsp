<%@ page import="de.wekb.helper.RCConstants" %>
<g:render template="/apptemplates/mainTemplates/kbcomponent"
	model="${[d:displayobj, rd:refdata_properties, dtype:'KBComponent']}" />

<g:if test="${d.id != null}">
	<dl class="dl-horizontal">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="url">URL</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable  owner="${d}" field="url" />
			<g:if test="${d.url}">
				&nbsp;<a href="${d.url}" target="new">Follow Link</a>
			</g:if>
		</dd>

		<dt>
			<gokb:annotatedLabel owner="${d}" property="frequency">Frequency</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}" />
		</dd>

		<dt>
			<gokb:annotatedLabel owner="${d}" property="defaultSupplyMethod">Default Supply Method</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="defaultSupplyMethod"
				config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="defaultDataFormat">Default Data Format</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="defaultDataFormat"
				config="${RCConstants.SOURCE_DATA_FORMAT}" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="responsibleParty">Responsible Party</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:manyToOneReferenceTypedown owner="${d}" field="responsibleParty"
				baseClass="org.gokb.cred.Org">
				${d.responsibleParty?.name?:''}
			</gokb:manyToOneReferenceTypedown>
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="zdbMatch">Automated Updates</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableBoolean owner="${d}" field="automaticUpdates" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="targetNamespace">Title ID Namespace</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:manyToOneReferenceTypedown owner="${d}" field="targetNamespace" baseClass="org.gokb.cred.IdentifierNamespace">${ d.targetNamespace }</gokb:manyToOneReferenceTypedown>
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="ezbMatch">EZB Matching Enabled</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableBoolean owner="${d}" field="ezbMatch" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="zdbMatch">ZDB Matching Enabled</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableBoolean owner="${d}" field="zdbMatch" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="lastRun">Last Run</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable  owner="${d}" type="date" field="lastRun" />
		</dd>
	</dl>
</g:if>
