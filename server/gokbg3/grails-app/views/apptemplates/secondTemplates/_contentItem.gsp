<dl class="dl-horizontal">
  <dt> <gokb:annotatedLabel owner="${d}" property="key">Key</gokb:annotatedLabel> </dt>
  <dd> <semui:xEditable  owner="${d}" field="key" /> </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="locale">Locale</gokb:annotatedLabel> </dt>
  <dd> ${d?.locale?.value} </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="content">Content</gokb:annotatedLabel> </dt>
  <dd> <semui:xEditable  owner="${d}" field="content" /> </dd>
</dl>
