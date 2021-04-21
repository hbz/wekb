<div class="tab-pane ${d.publicationType?.value != 'Serial' ? 'active' : ''}" id="identifiers">
	<dl>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="ids">Identifiers</gokb:annotatedLabel>
		</dt>
		<dd>
			<g:render template="/apptemplates/secondTemplates/combosByType"
					  model="${[d: d, property: 'ids', fragment: 'identifiers', combo_status: 'Active', cols: [
							  [expr: 'toComponent.namespace.value', colhead: 'Namespace'],
							  [expr: 'toComponent.value', colhead: 'ID', action: 'link']]]}"/>
			<g:if test="${editable}">
				<h4>
					<gokb:annotatedLabel owner="${d}"
										 property="addIdentifier">Add new Identifier</gokb:annotatedLabel>
				</h4>
				<g:render template="/apptemplates/secondTemplates/addIdentifier"
						  model="${[d: d, hash: '#identifiers']}"/>
			</g:if>
		</dd>
	</dl>
</div>