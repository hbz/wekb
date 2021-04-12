<g:each var="entry" in="${rd.sort{it.value.title}}">
	<g:if test="${ (dtype ? entry.key.startsWith(dtype + '.' ) : true) && !(entry.key in notShowProps)}">
		<dt class="col-3 text-right">
			<gokb:annotatedLabel owner="${d}" property="${ entry.value.title }">
				${ entry.value.title }
			</gokb:annotatedLabel>
		</dt>
		<dd class="col-9 text-left">
			<gokb:xEditableRefData owner="${d}" field="${entry.value.name}"
				config="${entry.key}" />
		</dd>
	</g:if>
</g:each>
