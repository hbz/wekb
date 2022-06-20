<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
	<dt>
		<gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="name" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="website">Website</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="website" />
                <g:if test="${d.website}">
                  &nbsp; <a href="${d.website}" target="new">Follow Link</a>
                </g:if>

	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="email">Email</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="email" validation="email"/>
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="phoneNumber">Phone Number</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="phoneNumber" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="otherDetails">Other Details</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="otherDetails" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="address1">Address Line 1</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="addressLine1" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="address2">Address Line 2</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="addressLine2" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="city">City</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="city" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="zipCode">Zip/Postcode</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="zipPostcode" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="state">State</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="state" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="region">Province/County</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditable  owner="${d}" field="region" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="owner">Owner Org</gokb:annotatedLabel>
	</dt>
	<dd>
		<semui:xEditableManyToOne owner="${d}" field="org" baseClass="org.gokb.cred.Org">
			${d.org?.name?:''}
		</semui:xEditableManyToOne>
	</dd>

  %{--<dt><gokb:annotatedLabel owner="${d}" property="curatoryGroups">Curatory Groups</gokb:annotatedLabel></dt>
  <dd>
     <g:render template="/apptemplates/secondTemplates/curatory_groups" model="${[d:d]}" />
  </dd>--}%


	<g:if test="${d.id != null}">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="country">Country</gokb:annotatedLabel>
		</dt>
		<dd>
			<semui:xEditableRefData owner="${d}" field="country" config="${RCConstants.COUNTRY}" />
		</dd>
	</g:if>
</dl>
