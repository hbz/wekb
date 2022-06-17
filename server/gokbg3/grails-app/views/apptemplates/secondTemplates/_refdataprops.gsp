<g:each var="entry" in="${rd.sort{it.value.title}}">
	<g:if test="${ (dtype ? entry.key.startsWith(dtype + '.' ) : true) && !(entry.key in notShowProps)}">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="${ entry.value.title }">
				${ entry.value.title }
			</gokb:annotatedLabel>
		</dt>
		<dd>
			<semui:xEditableRefData owner="${d}" field="${entry.value.name}"
				config="${entry.key}" />
		</dd>
	</g:if>
</g:each>
