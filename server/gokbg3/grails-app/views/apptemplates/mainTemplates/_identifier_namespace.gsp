<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
  <dt> Value </dt>
  <dd> <semui:xEditable  owner="${d}" field="value" /> </dd>

  <dt> Name </dt>
  <dd> <semui:xEditable  owner="${d}" field="name" /> </dd>

  <dt> Category </dt>
  <dd> <semui:xEditable  owner="${d}" field="family" /> </dd>

  <dt> Pattern </dt>
  <dd> <semui:xEditable  owner="${d}" field="pattern" /> </dd>

  <dt> Target Type </dt>
  <dd> <semui:xEditableRefData owner="${d}" field="targetType" config="${RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE}" /> </dd>
</dl>
