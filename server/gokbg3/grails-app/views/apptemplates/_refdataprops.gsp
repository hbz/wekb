<g:each var="entry" in="${rd}">
	<g:if test="${ entry.key.startsWith(dtype + '.' ) }">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="${ entry.value.title }">
				${ entry.value.title }
			</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="${entry.value.name}"
				config="${entry.key}" editable="${editable}"/>
		</dd>
	</g:if>
</g:each>
