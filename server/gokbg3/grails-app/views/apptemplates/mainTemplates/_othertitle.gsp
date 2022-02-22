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
    <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="reasonRetired">Status Reason</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="reasonRetired"
      config="${RCConstants.TITLEINSTANCE_REASON_RETIRED}" />
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
    ${d.currentPublisher}&nbsp;
  </dd>

</dl>

<div id="content">
  <ul id="tabs" class="nav nav-tabs">
    <li class="active"><a href="#titledetails" data-toggle="tab">Title Details</a></li>
    <g:if test="${d.id}">
      <li><a href="#altnames" data-toggle="tab">Alternate Names <span class="badge badge-warning"> ${d.variantNames?.size() ?: '0'}</span> </a></li>

      <li><a href="#identifiers" data-toggle="tab">Identifiers <span class="badge badge-warning"> ${d.ids?.size() ?: '0'} </span></a></li>

      <li><a href="#publishers" data-toggle="tab">Publishers <span
          class="badge badge-warning">
            ${d.publisher?.size() ?: '0'}
        </span></a></li>

      <li><a href="#availability" data-toggle="tab">Availability <span
          class="badge badge-warning">
            ${d.tipps?.size() ?: '0'}
        </span></a></li>

    %{--  <li><a href="#addprops" data-toggle="tab">Additional Properties <span
          class="badge badge-warning">
            ${d.additionalProperties?.size() ?: '0'}
        </span></a></li>--}%

      <li><a href="#review" data-toggle="tab">Review Tasks <span
          class="badge badge-warning">
            ${d.reviewRequests?.size() ?: '0'}
        </span></a></li>
      <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">
        <li><a href="#ds" data-toggle="tab">Decision Support</a></li>
      </g:if>

    </g:if>
    <g:else>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Alternate Names </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Identifiers </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Publishers </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Availability </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Additional Properties </span></li>
      <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Review Tasks </span></li>
      <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Decision Support </span></li>
      </g:if>
    </g:else>
  </ul>
  <div id="my-tab-content" class="tab-content">
    <div class="tab-pane active" id="titledetails">

      <g:if test="${d.id != null}">

        <dl class="dl-horizontal">

          <dt>
            <gokb:annotatedLabel owner="${d}" property="medium">Medium</gokb:annotatedLabel>
          </dt>
          <dd>
            <gokb:xEditableRefData owner="${d}" field="medium"
              config="${RCConstants.TITLEINSTANCE_MEDIUM}" />
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
      </g:if>
    </div>

    <g:render template="/tabTemplates/showVariantnames" model="${[showActions:true]}" />

    <div class="tab-pane" id="history">
      <g:if test="${d.id != null}">
        <dl class="dl-horizontal">
          <g:form name="AddHistoryForm" controller="workflow"
            action="createTitleHistoryEvent">
            <dt>
              Titles
              </dt>
            <dd>
              <table>
                <tr>
                  <th>Before</th>
                  <th></th>
                  <th>After</th>
                </tr>
                <tr>
                  <td><select name="beforeTitles" size="5" multiple
                    class="input-xxlarge" style="width: 500px;">
                      <option value="org.gokb.cred.TitleInstance:${d.id}">
                        ${d.name}
                      </option>
                  </select><br /></td>
                  <td>
                    <button type="button"
                      onClick="SelectMoveRows(document.AddHistoryForm.beforeTitles,document.AddHistoryForm.afterTitles)">&gt;</button>
                    <br />
                    <button type="button"
                      onClick="SelectMoveRows(document.AddHistoryForm.afterTitles,document.AddHistoryForm.beforeTitles)">&lt;</button>
                    <br />
                  </td>
                  <td><select name="afterTitles" size="5" multiple="multiple"
                    class="input-xxlarge" style="width: 500px;" ></select></td>
                </tr>
                <tr>
                  <td><gokb:simpleReferenceTypedown class="form-control" name="fromTitle"
                      baseClass="org.gokb.cred.TitleInstance" /> <br />
                    <button type="button"
                      onClick="AddTitle(document.AddHistoryForm.fromTitle, document.AddHistoryForm.beforeTitles)">Add</button>
                    <button type="button" onClick="removeTitle('beforeTitles')">Remove</button></td>
                  <td></td>
                  <td><gokb:simpleReferenceTypedown class="form-control" name="ToTitle"
                      baseClass="org.gokb.cred.TitleInstance" /> <br />
                    <button type="button"
                      onClick="AddTitle(document.AddHistoryForm.ToTitle, document.AddHistoryForm.afterTitles)">Add</button>
                    <button type="button" onClick="removeTitle('afterTitles')">Remove</button></td>
                </tr>
              </table>
            </dd>
            <dt>Event Date</dt>
            <dd>
              <input type="date" name="EventDate" />
            </dd>
            <dt></dt>
            <dd>
              <button
                onClick="submitTitleHistoryEvent(document.AddHistoryForm.beforeTitles,document.AddHistoryForm.afterTitles)">Add
                Title History Event -&gt;</button>
            </dd>
          </g:form>
        </dl>
      </g:if>
    </div>

    <div class="tab-pane" id="availability">
      <dt>
        <gokb:annotatedLabel owner="${d}" property="availability">Availability</gokb:annotatedLabel>
      </dt>
      <dd>
        <g:render template="/apptemplates/secondTemplates/tippdisplay" model="${[d:d.tipps]}" />
      </dd>

    </div>

    <div class="tab-pane" id="publishers">
      <g:render template="/tabTemplates/showPublishers"/>
    </div>

    <g:render template="/tabTemplates/showIdentifiers" model="${[d: d]}" />

   %{-- <div class="tab-pane" id="addprops">
      <g:render template="/apptemplates/secondTemplates/addprops"
        model="${[d:d]}" />
    </div>--}%

    <div class="tab-pane" id="review">
      <g:render template="/apptemplates/secondTemplates/revreqtab"
        model="${[d:d]}" />
    </div>

    <div class="tab-pane" id="ds">
      <g:render template="/apptemplates/secondTemplates/dstab" model="${[d:d]}" />
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
