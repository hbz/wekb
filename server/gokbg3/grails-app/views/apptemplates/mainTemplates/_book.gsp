<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
  <dt>
    <gokb:annotatedLabel owner="${d}" property="name">Title</gokb:annotatedLabel>
  </dt>
  <dd style="max-width:60%">
    <g:if test="${displayobj?.id != null}">
      <div>
        ${d.name}<br/>
        <span style="white-space:nowrap;">(Modify title through <i>Alternate Names</i> below)</span>
      </div>
    </g:if>
    <g:else>
      <gokb:xEditable  owner="${d}" field="name" />
    </g:else>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="status">Work</gokb:annotatedLabel>
  </dt>
  <dd style="width:50%">
    <g:if test="${d.work}">
      <g:link controller="resource" action="show" id="${d.work.class.name}:${d.work.id}"> ${d.work.name} </g:link>
    </g:if>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="source">Source</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="source" baseClass="org.gokb.cred.Source">${d.source?.name}</gokb:manyToOneReferenceTypedown>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
  </dt>
  <dd>
    <sec:ifAnyGranted roles="ROLE_SUPERUSER">
      <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" />
    </sec:ifAnyGranted>
    <sec:ifNotGranted roles="ROLE_SUPERUSER">
      ${d.status?.value ?: 'Not Set'}
    </sec:ifNotGranted>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="reasonRetired">Status Reason</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="reasonRetired"
      config="${RCConstants.TITLEINSTANCE_REASON_RETIRED}" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="editStatus">Edit Status</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="editStatus"
      config="${RCConstants.KBCOMPONENT_EDIT_STATUS}" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="language">Language</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="language" config="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="currentPubisher">Latest Publisher</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.currentPublisher}&nbsp;
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="imprint">Imprint</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="imprint"
      baseClass="org.gokb.cred.Imprint">
      ${d.imprint?.name}
    </gokb:manyToOneReferenceTypedown>
    &nbsp;
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="firstAuthor">First Author</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" field="firstAuthor" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="firstEditor">First Editor</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" field="firstEditor" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="publishedFrom">Published From</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" type="date" field="publishedFrom" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="publishedTo">Published To</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" type="date" field="publishedTo" />
  </dd>

  <g:if test="${d.id != null && d.titleHistory}">
    <dt>
      <gokb:annotatedLabel owner="${d}" property="titleHistory">Title History</gokb:annotatedLabel>
    </dt>
    <dd>
      <g:render template="/apptemplates/secondTemplates/fullth" model="${[d:d]}" />
    </dd>
  </g:if>
</dl>

<div id="content">
  <ul id="tabs" class="nav nav-tabs">
    <li class="active"><a href="#titledetails" data-toggle="tab">Title Details</a></li>
    <li><a href="#bookdetails" data-toggle="tab">Book Details</a></li>

    <g:if test="${d.id}">
      <li><a href="#altnames" data-toggle="tab">Alternate Names <span class="badge badge-warning"> ${d.variantNames?.size() ?: '0'}</span> </a></li>
      <li><a href="#identifiers" data-toggle="tab">Identifiers <span class="badge badge-warning"> ${d?.getCombosByPropertyNameAndStatus('ids','Active')?.size() ?: '0'} </span></a></li>
      <li><a href="#publishers" data-toggle="tab">Publishers <span
          class="badge badge-warning">
            ${d.publisher?.size() ?: '0'}
        </span></a></li>
      <li><a href="#availability" data-toggle="tab">Package Availability <span
        class="badge badge-warning">
          ${d?.tipps?.findAll{ it.status?.value == 'Current'}?.size() ?: '0'}
      </span></a></li>
      <li><a href="#tipls" data-toggle="tab">Platforms <span
          class="badge badge-warning">
            ${d?.tipls?.findAll{ it.status?.value == 'Current'}?.size() ?: '0'}
        </span></a></li>
      <li><a href="#addprops" data-toggle="tab">Custom Fields <span
        class="badge badge-warning">
          ${d.additionalProperties?.size() ?: '0'}
      </span></a></li>
      <li><a href="#review" data-toggle="tab">Review Tasks (Open/Total)
        <span class="badge badge-warning">
          ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS,'Open') }?.size() ?: '0'}/${d.reviewRequests.size()}
        </span>
      </a></li>
      <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}" >
        <li><a href="#ds" data-toggle="tab">Decision Support</a></li>
      </g:if>
    </g:if>
    <g:else>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Alternate Names </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Identifiers </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Publishers </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Package Availability </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Platforms </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Custom Fields </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Review Tasks </span></li>
      <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}" >
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Decision Support </span></li>
      </g:if>
    </g:else>

  </ul>
  <div id="my-tab-content" class="tab-content">
    <div class="tab-pane active" id="titledetails">
      <dl class="dl-horizontal">

        <dt>
          <gokb:annotatedLabel owner="${d}" property="medium">Medium</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:if test="${d.id != null}">
            <gokb:xEditableRefData owner="${d}" field="medium"
              config="${RCConstants.TITLEINSTANCE_MEDIUM}" />
          </g:if>
          <g:else>
            Book
          </g:else>
        </dd>

        <dt>
          <gokb:annotatedLabel owner="${d}" property="OAStatus">OA Status</gokb:annotatedLabel>
        </dt>
        <dd>
          <gokb:xEditableRefData owner="${d}" field="OAStatus"
            config="${RCConstants.TITLEINSTANCE_OA_STATUS}" />
        </dd>

        <dt>
          <gokb:annotatedLabel owner="${d}" property="continuingSeries">Continuing Series</gokb:annotatedLabel>
        </dt>
        <dd>
          <gokb:xEditableRefData owner="${d}" field="continuingSeries"
            config='TitleInstance.ContinuingSeries' />
        </dd>
      </dl>
    </div>

    <div class="tab-pane" id="bookdetails">
      <dl class="dl-horizontal">
        <dt> <gokb:annotatedLabel owner="${d}" property="editionNumber">Edition Number</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" field="editionNumber" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="coverImage">Cover Image URL</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" field="coverImage" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="editionDifferentiator">Edition Differentiator</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" field="editionDifferentiator" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="editionStatement">Edition Statement</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" field="editionStatement" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="volumeNumber">Volume Number</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" field="volumeNumber" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="dateFirstInPrint">Date first in print</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" type="date" field="dateFirstInPrint" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="dateFirstOnline">Date first online</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" type="date" field="dateFirstOnline" /> </dd>

        <dt> <gokb:annotatedLabel owner="${d}" property="summaryOfContent">Summary of content</gokb:annotatedLabel> </dt>
        <dd> <gokb:xEditable  owner="${d}" field="summaryOfContent" /> </dd>
      </dl>
    </div>

    <g:render template="/tabTemplates/showVariantnames"
      model="${[d:displayobj, showActions:true]}" />

    <div class="tab-pane" id="availability">
      <g:if test="${d.id}">
        <dt>
          <gokb:annotatedLabel owner="${d}" property="availability">Package Availability</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:link class="display-inline" controller="search" action="index"
            params="[qbe:'g:tipps', inline:true, refOid: d.getLogEntityId(), qp_title_id:d.id, hide:['qp_title_id', 'qp_title']]"
            id="">Availability of this Title</g:link>
        </dd>
      </g:if>
    </div>

    <div class="tab-pane" id="tipls">
      <dt>
        <gokb:annotatedLabel owner="${d}" property="tipls">Platforms</gokb:annotatedLabel>
      </dt>
      <dd>
        <table class="table table-striped table-bordered">
          <thead>
            <tr>
              <th>Platform</th>
              <th>Url</th>
              <th>Status</th>
            </tr>
          </thead>
          <tbody>
            <g:each in="${d.tipls}" var="tipl">
              <tr>
                <td><g:link controller="resource" action="show" id="${tipl.tiplHostPlatform.class.name}:${tipl.tiplHostPlatform.id}"> ${tipl.tiplHostPlatform.name} </g:link></td>
                <td>${tipl.url}</td>
                <td><gokb:xEditableRefData owner="${tipl}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" /></td>
              </tr>
            </g:each>
          </tbody>
        </table>
      </dd>

    </div>

    <div class="tab-pane" id="publishers">
      <g:render template="/tabTemplates/showPublishers"
      model="${[d:displayobj]}" />
    </div>

    <div class="tab-pane" id="identifiers">
      <dl>
        <dt>
          <gokb:annotatedLabel owner="${d}" property="ids">Identifiers</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:render template="/apptemplates/secondTemplates/combosByType"
            model="${[d:d, property:'ids', fragment:'identifiers', cols:[
                      [expr:'toComponent.namespace.value', colhead:'Namespace'],
                      [expr:'toComponent.value', colhead:'ID', action:'link']]]}" />
          <g:if test="${d.isEditable()}">
            <h4>
              <gokb:annotatedLabel owner="${d}" property="addIdentifier">Add new Identifier</gokb:annotatedLabel>
            </h4>
            <g:render template="/apptemplates/secondTemplates/addIdentifier" model="${[d:d, hash:'#identifiers', targetType:'book']}"/>
          </g:if>
        </dd>
      </dl>
    </div>

    <div class="tab-pane" id="addprops">
      <g:render template="/apptemplates/secondTemplates/addprops"
        model="${[d:d]}" />
    </div>

    <div class="tab-pane" id="review">
      <g:render template="/apptemplates/secondTemplates/revreqtab"
        model="${[d:d]}" />
    </div>

    <div class="tab-pane" id="ds">
      <g:render template="/apptemplates/secondTemplates/dstab" model="${[d:d]}" />
    </div>

  </div>
  <g:if test="${d.id}">
    <g:render template="/apptemplates/secondTemplates/componentStatus"
      model="${[d:displayobj]}" />
  </g:if>
</div>


<asset:script type="text/javascript">

  $("select[name='publisher_status']").change(function(event) {
  console.log("In here")
    var form =$(event.target).closest("form")
    form.submit();
  });


</asset:script>
