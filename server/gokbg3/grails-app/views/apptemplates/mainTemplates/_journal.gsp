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
      <gokb:xEditable  owner="${d}" field="name"/>
    </g:else>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="source">Source</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="source"
                                  baseClass="org.gokb.cred.Source">${d.source?.name}</gokb:manyToOneReferenceTypedown>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
  </dt>
  <dd>
    <sec:ifAnyGranted roles="ROLE_SUPERUSER">
      <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/>
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
                        config="${RCConstants.TITLEINSTANCE_REASON_RETIRED}"/>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="language">Language</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:render template="/apptemplates/secondTemplates/languages"/>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="currentPubisher">Latest Publisher</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:if test="${d.currentPublisher}">
      ${d.currentPublisher.name} <g:link controller="resource" action="show"
                                         id="org.gokb.cred.Org:${d.currentPublisher.id}"><i
          class="fas fa-eye"></i></g:link>
    </g:if>
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
    <gokb:annotatedLabel owner="${d}" property="publishedFrom">Published From</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" type="date" field="publishedFrom"/>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="publishedTo">Published To</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" type="date" field="publishedTo"/>
  </dd>

  <g:if test="${d?.id != null && d.titleHistory}">
    <dt>
      <gokb:annotatedLabel owner="${d}" property="titleHistory">Title History</gokb:annotatedLabel>
    </dt>
    <dd>
      <g:render template="/apptemplates/secondTemplates/fullth" model="${[d: d]}"/>
    </dd>
  </g:if>
</dl>

<div id="content">
  <ul id="tabs" class="nav nav-tabs">
    <li class="active"><a href="#titledetails" data-toggle="tab">Title Details</a></li>
    <g:if test="${d.id}">
      <li><a href="#altnames" data-toggle="tab">Alternate Names <span
          class="badge badge-warning">${d.variantNames?.size() ?: '0'}</span></a></li>

      <g:if test="${d.isEditable()}">
        <li><a href="#history" data-toggle="tab">Add to Title History</a></li>
      </g:if>
      <li><a href="#identifiers" data-toggle="tab">Identifiers <span
          class="badge badge-warning">${d?.getCombosByPropertyNameAndStatus('ids', 'Active')?.size() ?: '0'}</span></a>
      </li>
      <li><a href="#publishers" data-toggle="tab">Publishers <span
          class="badge badge-warning">
        ${d.getCombosByPropertyNameAndStatus('publisher', params.publisher_status)?.size() ?: '0'}
      </span></a></li>
      <li><a href="#availability" data-toggle="tab">Package Availability <span
          class="badge badge-warning">
        ${d?.tipps?.findAll { it.status?.value == 'Current' }?.size() ?: '0'}
      </span></a></li>
      <li><a href="#tipls" data-toggle="tab">Platforms <span
          class="badge badge-warning">
        ${d?.tipls?.findAll { it.status?.value == 'Current' }?.size() ?: '0'}
      </span></a></li>
     %{-- <li><a href="#addprops" data-toggle="tab">Additional Properties <span
          class="badge badge-warning">
        ${d.additionalProperties?.size() ?: '0'}
      </span></a></li>--}%
      <li><a href="#review" data-toggle="tab">Review Tasks (Open/Total)
        <span class="badge badge-warning">
          ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open') }?.size() ?: '0'}/${d.reviewRequests.size()}
        </span>
      </a></li>
    </g:if>
    <g:else>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Alternate Names</span></li>
      <g:if test="${d.isEditable()}">
        <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
            class="nav-tab-disabled">Add to Title History</span></li>
      </g:if>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Identifiers</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Publishers</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Package Availability</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Platforms</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Additional Properties</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Review Tasks</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Subject Group</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">Series</span></li>
      <li class="disabled" title="${message(code: 'component.create.idMissing.label')}"><span
          class="nav-tab-disabled">List Price</span></li>
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
                                config="${RCConstants.TITLEINSTANCE_MEDIUM}"/>
          </g:if>
          <g:else>
            Journal
          </g:else>
        </dd>

        <dt>
          <gokb:annotatedLabel owner="${d}" property="OAStatus">OA Status</gokb:annotatedLabel>
        </dt>
        <dd>
          <gokb:xEditableRefData owner="${d}" field="OAStatus"
                              config="${RCConstants.TITLEINSTANCE_OA_STATUS}"/>
        </dd>

        <dt>
          <gokb:annotatedLabel owner="${d}" property="continuingSeries">Continuing Series</gokb:annotatedLabel>
        </dt>
        <dd>
          <gokb:xEditableRefData owner="${d}" field="continuingSeries"
                              config='TitleInstance.ContinuingSeries'/>
        </dd>
      </dl>
    </div>

    <g:render template="/tabTemplates/showVariantnames" model="${[showActions: true]}"/>

    <div class="tab-pane" id="history">
      <g:if test="${d.id != null}">
        <dl class="dl-horizontal">
          <g:form name="AddHistoryForm" controller="workflow"
                  action="createTitleHistoryEvent">
            <dt style="width:100px;">
              Titles
            </dt>
            <dd style="width:80%">
              <table style="width:100%">
                <tr>
                  <th>Before</th>
                  <th></th>
                  <th>After</th>
                </tr>
                <tr>
                  <td><select name="beforeTitles" size="5" multiple
                              class="input-xxlarge" style="width:100%">
                    <option value="org.gokb.cred.JournalInstance:${d.id}">
                      ${d.name}
                    </option>
                  </select><br/></td>
                  <td style="text-align:center;">
                    <button class="btn btn-sm" style="margin: 2px 5px;" type="button"
                            onClick="SelectMoveRows(document.AddHistoryForm.beforeTitles, document.AddHistoryForm.afterTitles)">&gt;</button>

                    <div style="height:2px;"></div>
                    <button class="btn btn-sm" style="margin: 2px 5px;" type="button"
                            onClick="SelectMoveRows(document.AddHistoryForm.afterTitles, document.AddHistoryForm.beforeTitles)">&lt;</button>
                  </td>
                  <td><select name="afterTitles" size="5" multiple="multiple"
                              class="input-xxlarge" style="width:100%"></select></td>
                </tr>
                <tr>
                  <td><gokb:simpleReferenceTypedown class="form-control" name="fromTitle"
                                                 baseClass="org.gokb.cred.JournalInstance"/> <br/>
                    <button class="btn btn-sm" type="button"
                            onClick="AddTitle(document.AddHistoryForm.fromTitle, document.AddHistoryForm.beforeTitles)">Add</button>
                    <button class="btn btn-sm" type="button" onClick="removeTitle('beforeTitles')">Remove</button></td>
                  <td></td>
                  <td><gokb:simpleReferenceTypedown class="form-control" name="ToTitle"
                                                 baseClass="org.gokb.cred.JournalInstance"/> <br/>
                    <button class="btn btn-sm" type="button"
                            onClick="AddTitle(document.AddHistoryForm.ToTitle, document.AddHistoryForm.afterTitles)">Add</button>
                    <button class="btn btn-sm" type="button" onClick="removeTitle('afterTitles')">Remove</button></td>
                </tr>
              </table>
            </dd>
            <dt class="dt-label" style="width:100px;">Event Date</dt>
            <dd>
              <input type="date" format="yyyy-mm-dd" class="form-control" name="EventDate" required />
            </dd>
            <dt style="width:100px;"></dt>
            <dd>
              <button class="btn btn-default btn-primary"
                      onClick="submitTitleHistoryEvent(document.AddHistoryForm.beforeTitles, document.AddHistoryForm.afterTitles)">Add
              Title History Event</button>
            </dd>
          </g:form>
        </dl>
      </g:if>
      <g:else>
        The title history can be edited once the creation process is finished
      </g:else>
    </div>

    <div class="tab-pane" id="availability">
      <g:if test="${d.id}">
        <dt>
          <gokb:annotatedLabel owner="${d}" property="availability">Package Availability</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:link class="display-inline" controller="search" action="index"
                  params="[qbe: 'g:tipps', inline: true, refOid: d.getLogEntityId(), qp_title_id: d.id, hide: ['qp_title_id', 'qp_title']]"
                  id="">Availability of this Title</g:link>
        </dd>
      </g:if>
    </div>

    <div class="tab-pane" id="tipls">
      <dt>
        <gokb:annotatedLabel owner="${d}" property="tipls">Platforms</gokb:annotatedLabel>
      </dt>

      <div style="margin:5px 0px;">
        <g:form method="POST" controller="${controllerName}" action="${actionName}" fragment="tipls"
                params="${params.findAll { k, v -> k != 'tipl_status' }}">

          <span>Hide Deleted:</span> <g:select name="tipl_status" optionKey="key" optionValue="value"
                                               from="${[null: 'Off', 'Active': 'On']}" value="${params.tipl_status}"/>
        </g:form>
      </div>
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
              <td><g:link controller="resource" action="show"
                          id="${tipl.tiplHostPlatform.class.name}:${tipl.tiplHostPlatform.id}">${tipl.tiplHostPlatform.name}</g:link></td>
              <td>${tipl.url}</td>
              <td><gokb:xEditableRefData owner="${tipl}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/></td>
            </tr>
          </g:each>
          </tbody>
        </table>
      </dd>

    </div>

    <div class="tab-pane" id="publishers">
      <g:render template="/tabTemplates/showPublishers"
                model="${[d: d]}"/>
    </div>

    <g:render template="/tabTemplates/showIdentifiers" model="${[d: d]}" />

    %{--<div class="tab-pane" id="addprops">
      <dl>
        <dt>
          <gokb:annotatedLabel owner="${d}" property="customProperties">Additional Properties</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:render template="/apptemplates/secondTemplates/addprops"
                    model="${[d: d]}"/>
      </dl>
    </dl>
    </div>--}%

    <div class="tab-pane" id="review">
      <g:render template="/apptemplates/secondTemplates/revreqtab"
                model="${[d: d]}"/>
    </div>
  </div>


    <g:render template="/apptemplates/secondTemplates/componentStatus"/>

</div>


<asset:script type="text/javascript">

  $("select[name='publisher_status']").change(function(event) {
  console.log("In here")
    var form =$(event.target).closest("form")
    form.submit();
  });

  function SelectMoveRows(SS1,SS2) {
    var SelID='';
    var SelText='';
    // Move rows from SS1 to SS2 from bottom to top
    for (i=SS1.options.length - 1; i>=0; i--) {
        if (SS1.options[i].selected == true) {
            SelID=SS1.options[i].value;
            SelText=SS1.options[i].text;
            var newRow = new Option(SelText,SelID);
            SS2.options[SS2.length]=newRow;
            SS1.options[i]=null;
        }
    }
    SelectSort(SS2);
  }

  function SelectSort(SelList) {
    var ID='';
    var Text='';
    for (x=0; x < SelList.length - 1; x++) {
        for (y=x + 1; y < SelList.length; y++) {
            if (SelList[x].text > SelList[y].text) {
                ID=SelList[x].value;
                Text=SelList[x].text;
                SelList[x].value=SelList[y].value;
                SelList[x].text=SelList[y].text;
                SelList[y].value=ID;
                SelList[y].text=Text;
            }
        }
    }
  }
  function removeTitle(selectName) {
    $("select[name='"+selectName+"']").find(":selected").remove()
  }

  function AddTitle(titleIdHidden,ss) {
    // alert(titleIdHidden.value);
    // alert(titleIdHidden.parentNode.getElementsByTagName('div')[0].getElementsByTagName('span')[0].innerHTML);
    var newRow=new Option(titleIdHidden.parentNode.getElementsByTagName('div')[0].getElementsByTagName('span')[0].innerHTML,
                          titleIdHidden.value);
    ss.options[ss.length] = newRow;
    SelectSort[ss];
  }

  function submitTitleHistoryEvent(ss1,ss2) {
    selectAll(ss1);
    selectAll(ss2);
  }

  function selectAll(ss) {
    for (i=ss.options.length - 1; i>=0; i--) {
      ss.options[i].selected = true;
    }
  }

</asset:script>
