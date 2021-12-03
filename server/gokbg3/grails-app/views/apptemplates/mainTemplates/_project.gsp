<%@ page import="de.wekb.helper.RCConstants" %>
<div id="content">

	<dl class="dl-horizontal">
		<dt>
			<gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable  owner="${d}" field="name" />
		</dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="status"
				config="${RCConstants.KBCOMPONENT_STATUS}" />
		</dd>

		<dt>
			<gokb:annotatedLabel owner="${d}" property="accessUrl">Access URL</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable owner="${d}" field="accessUrl" />
		</dd>

		<dt>
			<gokb:annotatedLabel owner="${d}" property="dataUrl">Data URL</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditable owner="${d}" field="dataUrl" />
		</dd>

		<dt>
			<gokb:annotatedLabel owner="${d}" property="defaultSupplyMethod">Default Supply Method</gokb:annotatedLabel>
		</dt>
		<dd>
			<gokb:xEditableRefData owner="${d}" field="defaultSupplyMethod"
				config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}" />
		</dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="defaultDataFormat">Default Data Format</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="defaultDataFormat"
        config="${RCConstants.SOURCE_DATA_FORMAT}" />
    </dd>

    
	    <dt>
	      <gokb:annotatedLabel owner="${d}" property="sourceFile">Source file</gokb:annotatedLabel>
	    </dt>
	    <dd>
	      <g:if test="${ d.sourceFile }" >
	        <g:link controller="resource" action="download" id="${d.getClassName()}:${d.id}" params="${ ["prop" : "sourceFile"] }">Download the source file</g:link>
	      </g:if>
	      <g:else>
	        No source file	      
	      </g:else>
	    </dd>
	</dl>

	<ul id="tabs" class="nav nav-tabs">
		<li class="active"><a href="#projdetails" data-toggle="tab">Project
				Details</a></li>
	</ul>

	<div id="my-tab-content" class="tab-content">
		<div class="tab-pane active" id="projdetails">
			<dl class="dl-horizontal">
				<dt>
					<gokb:annotatedLabel owner="${d}" property="createdBy">Created By</gokb:annotatedLabel>
				</dt>
				<dd>
					${ d.createdBy?.displayName ?: d.createdBy?.username }
				</dd>
				
        <dt>
          <gokb:annotatedLabel owner="${d}" property="projectStatus">Project Status</gokb:annotatedLabel>
        </dt>
        <dd>
          ${d.projectStatus.name ?: ''}
        </dd>

        <g:if test="${ d.projectStatus == org.gokb.refine.RefineProject.Status.CHECKED_OUT }" >
					<dt>
						<gokb:annotatedLabel owner="${d}" property="checkedOutBy">Checked Out By</gokb:annotatedLabel>
					</dt>
				</g:if>
				<g:else>
				  <dt>
            <gokb:annotatedLabel owner="${d}" property="lastCheckedOutBy">Last Checked Out By</gokb:annotatedLabel>
          </dt>
				</g:else>
        <dd>
          ${ d.lastCheckedOutBy?.displayName ?: d.lastCheckedOutBy?.username }
        </dd>

				<dt>
					<gokb:annotatedLabel owner="${d}" property="lastModifiedBy">Last Modified By</gokb:annotatedLabel>
				</dt>
				<dd>
					${ d.modifiedBy?.displayName ?: d.modifiedBy?.username }
				</dd>

				<g:if test="${d.id != null}">
					<dt>
						<gokb:annotatedLabel owner="${d}" property="provider">Provider</gokb:annotatedLabel>
					</dt>
					<dd>
                                        
                                                           <gokb:manyToOneReferenceTypedown owner="${d}" field="provider"
                                                                baseClass="org.gokb.cred.Org">
                                                                ${d.provider?.name}
                                                        </gokb:manyToOneReferenceTypedown>
					</dd>

					<dd>
						${d.provider?.name ?: 'Not yet set'}
					</dd>
					<dt>
						<gokb:annotatedLabel owner="${d}" property="lastValidationResult">Last validation result</gokb:annotatedLabel>
					</dt>
					<dd>
						${d.lastValidationResult ?: 'Not yet validated'}
					</dd>
				</g:if>
			</dl>
		</div>
	</div>
</div>
