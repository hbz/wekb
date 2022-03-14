<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
  <dt> <gokb:annotatedLabel owner="${d}" property="value">Value</gokb:annotatedLabel> </dt>
  <dd> <gokb:xEditable  owner="${d}" field="value" /> </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel> </dt>
  <dd> <gokb:xEditable  owner="${d}" field="name" /> </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="family">Category</gokb:annotatedLabel> </dt>
  <dd> <gokb:xEditable  owner="${d}" field="family" /> </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="pattern">Pattern</gokb:annotatedLabel> </dt>
  <dd> <gokb:xEditable  owner="${d}" field="pattern" /> </dd>

  <dt> <gokb:annotatedLabel owner="${d}" property="targetType">Target Type</gokb:annotatedLabel> </dt>
  <dd> <gokb:xEditableRefData owner="${d}" field="targetType" config="${RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE}" /> </dd>
</dl>
