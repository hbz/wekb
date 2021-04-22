<%@ page import="de.wekb.helper.RCConstants" %>
<g:render template="/apptemplates/mainTemplates/kbcomponent" model="${[d:displayobj, rd:refdata_properties, dtype:'KBComponent']}" />
<g:if test="${d.id != null}">
	<dl class="dl-horizontal">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="licenseURL">License URL</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable  owner="${d}" field="url" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="licenseType">License Type</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="type" config="${RCConstants.LICENSE_TYPE}" />
		</dd>

    %{--<dt><gokb:annotatedLabel owner="${d}" property="curatoryGroups">Curatory Groups</gokb:annotatedLabel></dt>
    <dd>
       <g:render template="/apptemplates/secondTemplates/curatory_groups" model="${[d:d]}" />
    </dd>--}%

	</dl>

	<ul id="tabs" class="nav nav-tabs">
		<li class="active"><a href="#licsummary" data-toggle="tab">License
				Summary</a></li>
		<li><a href="#files" data-toggle="tab">Files</a></li>
	</ul>
	<div id="my-tab-content" class="tab-content">
		<div class="tab-pane active" id="licsummary">
			<g:if
				test="${((d.summaryStatement != null) && (d.summaryStatement.length() > 0 ) )}">
				<h4>Summary Of License</h4>
				${d.summaryStatement}
			</g:if>
		</div>

		<g:render template="/tabTemplates/showDataFiles" model="${[d:displayobj, allowEdit:true]}" />
	</div>
	<g:javascript>
	$('.trigger').each(function(){
		var me = $(this);
		var showEl = jq((me.attr('link-id')));

		me.click(function(){
			$(showEl).toggle();
		});
	});
	function jq( myid ) {
	    return "#" + myid.replace( /(:|\.|\[|\]|,)/g, "\\$1" );
	}
	function Tip(desc){
		// console.log(desc)
	}
	function UnTip(){}
	</g:javascript>
</g:if>
