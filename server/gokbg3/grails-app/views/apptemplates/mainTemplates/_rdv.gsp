<dl class="dl-horizontal">
  <dt> <gokb:annotatedLabel owner="${d}" property="value">Value</gokb:annotatedLabel> </dt>
  <dd> ${d?.value} </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="category">Namespace</gokb:annotatedLabel> </dt>
  <dd> <g:link controller="resource" action="show" id="org.gokb.cred.RefdataCategory:${d.owner.id}">${d?.owner?.desc}</g:link></dd>
</dl>
