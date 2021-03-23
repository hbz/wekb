<dl class="dl-horizontal">
  <dt> <gokb:annotatedLabel owner="${d}" property="value">Value</gokb:annotatedLabel> </dt>
  <dd> ${d?.value} </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="namespace">Namespace</gokb:annotatedLabel> </dt>
  <dd> ${d?.namespace?.value} </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="identifiedComponents">Identified Components</gokb:annotatedLabel> </dt>
  <dd>
    <g:render template="/apptemplates/secondTemplates/combosByType"
      model="${[d:d, property:'identifiedComponents', combo_status: null, cols:[
                [expr:'fromComponent.name', colhead:'Name', action:'link'],
                [expr:'fromComponent.status.value', colhead: 'Status']]]}" />
  </dd>
</dl>
