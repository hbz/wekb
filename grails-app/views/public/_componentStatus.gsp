
<dl class="row">
	<dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="dateCreated">Date Created</gokb:annotatedLabel></dt>
	<dd class="col-9 text-left">
	  ${d?.dateCreated?:''}
	</dd>
	<dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="lastUpdated">Last Updated</gokb:annotatedLabel></dt>
	<dd class="col-9 text-left">
	  ${d?.lastUpdated?:''}
	</dd>
	<dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="uuid">UUID</gokb:annotatedLabel></dt>
	<dd class="col-9 text-left">
          ${d?.uuid?:''}
	</dd>
</dl>
