<dl class="dl-horizontal">
	<dt>
		<gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="name" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="website">Website</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="website" />
                <g:if test="${d.website}">
                  &nbsp; <a href="${d.website}" target="new">Follow Link</a>
                </g:if>

	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="email">Email</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="email" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="phoneNumber">Phone Number</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="phoneNumber" />
	</dd>
	<dt>
		<gokb:annotatedLabel owner="${d}" property="otherDetails">Other Details</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="otherDetails" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="address1">Address Line 1</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="addressLine1" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="address2">Address Line 2</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="addressLine2" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="city">City</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="city" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="zipCode">Zip/Postcode</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="zipPostcode" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="state">State</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="state" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="region">Province/County</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="region" />
	</dd>
	<dt>
		<g:annotatedLabel owner="${d}" property="owner">Owner Org</g:annotatedLabel>
	</dt>
	<dd>
		<gokb:manyToOneReferenceTypedown owner="${d}" field="org"
			name="${comboprop}" baseClass="org.gokb.cred.Org">
			${d.org?.name?:''}
		</gokb:manyToOneReferenceTypedown>
	</dd>

  <dt><g:annotatedLabel owner="${d}" property="curatoryGroups">Curatory Groups</g:annotatedLabel></dt>
  <dd>
     <g:render template="/apptemplates/curatory_groups" model="${[d:d]}" />
  </dd>


	<g:if test="${d.id != null}">
		<dt>
			<g:annotatedLabel owner="${d}" property="country">Country</g:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="country" config='Country' />
		</dd>
	</g:if>
</dl>
