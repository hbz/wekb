<r:require modules="gokbstyle" />
<r:require modules="editable" />

<h1>
	${d.id ? d.getNiceName() + ': ' + (d.name ?: d.id) : 'Create New ' + d.getNiceName()}
</h1>

<dl class="dl-horizontal">
	<dt> <g:annotatedLabel owner="${d}" property="name">Name</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="name" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="website">Website</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="website" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="email">Email</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="email" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="phoneNumber">Phone Number</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="phoneNumber" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="otherDetails">Other Details</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="otherDetails" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="address1">Address Line 1</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="addressLine1" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="address2">Address Line 2</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="addressLine2" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="city">City</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="city" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="zipCode">Zip/Postcode</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="zipPostcode" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="region">Region</g:annotatedLabel> </dt>
	<dd> <g:xEditable class="ipe" owner="${d}" field="region" /> </dd>
	<dt> <g:annotatedLabel owner="${d}" property="owner">Owner Org</g:annotatedLabel> </dt>
	<dd>
		<g:manyToOneReferenceTypedown owner="${d}" field="org"
			name="${comboprop}" baseClass="org.gokb.cred.Org">
			${d.org?.name?:''}
		</g:manyToOneReferenceTypedown>
	</dd>

	<g:if test="${d.id != null}">
		<dt> <g:annotatedLabel owner="${d}" property="country">Country</g:annotatedLabel> </dt>
		<dd> <g:xEditableRefData owner="${d}" field="country" config='Country' /> </dd> 
        </g:if>
</dl>
<script type="text/javascript">
  $(document).ready(function() {

    $.fn.editable.defaults.mode = 'inline';
    $('.ipe').editable();
  });
</script>
