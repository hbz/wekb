<dl class="dl-horizontal">
	<dt>
		<gokb:annotatedLabel owner="${d}" property="shortcode"><g:message code="gokb.appname" default="we:kb"/> Shortcode</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="shortcode" />
	</dd>

	<g:if test="${ d.ids?.size() > 0 }">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="identifiers">Identifiers</gokb:annotatedLabel>
		</dt>
		<dd>
			<ul>
				<g:each in="${d.ids}" var="id">
					<li>
						${id.namespace.value}:${id.value}
					</li>
				</g:each>
			</ul>
		</dd>
	</g:if>
	<g:if test="${!d.id || (d.id && d.name)}">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="name">
				${ d.getNiceName() } Name</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable class="ipe" owner="${d}" field="name" />
		</dd>
	</g:if>

	<g:if test="${d.id != null}">
		<g:if test="${ d.hasProperty('tags') && d.tags?.size() > 0 }">
			<dt>
				<gokb:annotatedLabel owner="${d}" property="tags">Tags</gokb:annotatedLabel>
			</dt>
			<dd>
				&nbsp;
				<ul>
					<g:each in="${d.tags}" var="t">
						<li>
							${t.value}
						</li>
					</g:each>
				</ul>
			</dd>
		</g:if>
		<g:render template="/apptemplates/secondTemplates/refdataprops"
			model="${[d:(d), rd:(rd), dtype:(dtype)]}" />
	</g:if>
</dl>
