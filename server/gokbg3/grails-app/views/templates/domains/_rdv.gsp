<dl>
  <dt class="control-label"> Value </dt>
  <semui:xEditable owner="${d}" field="value"/>
</dl>

<dl>
  <dt class="control-label">
    Value EN
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="value_en"/>
</dl>

<dl>
  <dt class="control-label">
    Value DE
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="value_de"/>
</dl>

<dl>
  <dt class="control-label">
    Description
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="description"/>
</dl>


<dl>
  <dt class="control-label">
    Hard Data
  </dt>
  <dd>
    <semui:xEditableBoolean owner="${d}" field="isHardData" overwriteEditable="false"/>
</dl>
<dl>
  <dt class="control-label">Refdata Category</dt>
  <dd> <g:link controller="resource" action="show" id="org.gokb.cred.RefdataCategory:${d.owner.id}">${d?.owner?.desc}</g:link></dd>
</dl>
