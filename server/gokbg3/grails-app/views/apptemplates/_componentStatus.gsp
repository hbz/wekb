
<dl class="dl-horizontal">
	<dt><g:annotatedLabel owner="${d}" property="dateCreated">Date Created</g:annotatedLabel></dt>
	<dd>
	  ${d?.dateCreated?:''}
	</dd>
	<dt><g:annotatedLabel owner="${d}" property="lastUpdated">Last Updated</g:annotatedLabel></dt>
	<dd>
	  ${d?.lastUpdated?:''}
	</dd>
	<dt><g:annotatedLabel owner="${d}" property="uuid">UUID</g:annotatedLabel></dt>
	<dd>
          ${d?.uuid?:''}
	</dd>
</dl>
