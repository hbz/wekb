<dl class="dl-horizontal">
	<dt><gokb:annotatedLabel owner="${d}" property="id">Internal Id</gokb:annotatedLabel></dt>
	<dd>${d.id?:'New record'}</dd>
	<dt><gokb:annotatedLabel owner="${d}" property="propertyName">Property Name</gokb:annotatedLabel></dt>
	<dd><gokb:xEditable owner="${d}" field="propertyName"/></dd>
</dl>
