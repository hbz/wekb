<%@ page import="org.gokb.cred.RefdataCategory; de.wekb.helper.RCConstants; de.wekb.helper.RDStore;" %>

  <dl class="dl-horizontal">
  <dt>
    <gokb:annotatedLabel owner="${d}" property="name">Package Name</gokb:annotatedLabel>
  </dt>
  <dd style="max-width:60%">
    <g:if test="${displayobj?.id != null}">
      <div>
        ${d.name}<br/>
        <span style="white-space:nowrap;">(Modify title through <i>Alternate Names</i> below)</span>
      </div>
      <g:if test="${d.source && (d.source.lastUpdateUrl || d.source.url)}">
      <g:link controller="public" action="kbart" id="${params.id}">KBart File</g:link> &nbsp;
      </g:if>
      <g:link controller="public" action="packageTSVExport" id="${params.id}"><g:message code="gokb.appname" default="we:kb"/> File</g:link>
    </g:if>
    <g:else>
      <gokb:xEditable  owner="${d}" field="name" />
    </g:else>
  </dd>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="provider">Provider</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:manyToOneReferenceTypedown owner="${d}" field="provider" baseClass="org.gokb.cred.Org" >${d.provider?.name}</gokb:manyToOneReferenceTypedown>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="source">Source</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:manyToOneReferenceTypedown owner="${d}" field="source" baseClass="org.gokb.cred.Source" >${d.source?.name}</gokb:manyToOneReferenceTypedown>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="nominalPlatform">Nominal Platform</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:manyToOneReferenceTypedown owner="${d}" field="nominalPlatform" baseClass="org.gokb.cred.Platform" >
        ${d.nominalPlatform?.name ?: ''}
      </gokb:manyToOneReferenceTypedown>
    </dd>
    <g:if test="${d}">
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
    </g:if>

    <dt> <gokb:annotatedLabel owner="${d}" property="lastUpdateComment">Last Update Comment</gokb:annotatedLabel> </dt>
    <dd> <gokb:xEditable  owner="${d}" field="lastUpdateComment" /> </dd>

    <dt> <gokb:annotatedLabel owner="${d}" property="description">Description</gokb:annotatedLabel> </dt>
    <dd> <gokb:xEditable  owner="${d}" field="description" /> </dd>

    <dt> <gokb:annotatedLabel owner="${d}" property="descriptionURL">URL</gokb:annotatedLabel> </dt>
    <dd> <gokb:xEditable  owner="${d}" field="descriptionURL" /> </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="globalNote">Global Note</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditable  owner="${d}" field="globalNote" />
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="type">Breakable</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="breakable" config="${RCConstants.PACKAGE_BREAKABLE}"/>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="type">Content Type</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="contentType" config="${RCConstants.PACKAGE_CONTENT_TYPE}"/>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="type">File</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="file" config="${RCConstants.PACKAGE_FILE}"/>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="type">Open Access</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="openAccess" config="${RCConstants.PACKAGE_OPEN_ACCESS}"/>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="type">Payment Type</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="paymentType" config="${RCConstants.PACKAGE_PAYMENT_TYPE}"/>
    </dd>

    <dt>
      <gokb:annotatedLabel owner="${d}" property="type">Scope</gokb:annotatedLabel>
    </dt>
    <dd>
      <gokb:xEditableRefData owner="${d}" field="scope" config="${RCConstants.PACKAGE_SCOPE}"/>
    </dd>

    <g:if test="${controllerName != 'create'}">
        <dt>
          <gokb:annotatedLabel owner="${d}" property="nationalRanges">National Range</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:if test="${d.scope?.value == 'National'}">
            <g:render template="/apptemplates/secondTemplates/nationalRange" />
          </g:if>
        </dd>

        <dt>
          <gokb:annotatedLabel owner="${d}" property="regionalRanges">Regional Range</gokb:annotatedLabel>
        </dt>
        <dd>
          <g:if test="${RefdataCategory.lookup(RCConstants.COUNTRY, 'DE') in d.nationalRanges && d.scope?.value == 'National'}">
            <g:render template="/apptemplates/secondTemplates/regionalRange" />
          </g:if>
        </dd>
    </g:if>


  </dl>

  <div id="content">
    <ul id="tabs" class="nav nav-tabs">
      <g:if test="${d.id}">
        <li role="presentation" class="active"><a href="#currentTitles" data-toggle="tab">Current Titles <span class="badge badge-warning"> ${d.currentTippCount} </span></a></li>
        <li role="presentation"><a href="#retiredTitles" data-toggle="tab">Retired Titles <span class="badge badge-warning"> ${d.retiredTippCount} </span></a></li>
        <li role="presentation"><a href="#expectedTitles" data-toggle="tab">Expected Titles <span class="badge badge-warning"> ${d.expectedTippCount} </span></a></li>
        <li role="presentation"><a href="#deletedTitles" data-toggle="tab">Deleted Titles <span class="badge badge-warning"> ${d.deletedTippCount} </span></a></li>
        <li role="presentation"><a href="#identifiers" data-toggle="tab">Identifiers <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('ids','Active').size()} </span></a></li>

        <li role="presentation"><a href="#altnames" data-toggle="tab">Alternate Names
          <span class="badge badge-warning"> ${d.variantNames.size()}</span>
        </a></li>

        <li>
          <a href="#ddcs" data-toggle="tab">DDCs
            <span class="badge badge-warning">${d.ddcs.size()}</span>
          </a>
        </li>

        <li><a href="#relationships" data-toggle="tab">Relations</a></li>
        <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">
          <li role="presentation"><a href="#ds" data-toggle="tab">Decision Support</a></li>
        </g:if>
        <li role="presentation"><a href="#activity" data-toggle="tab">Activity</a></li>
        <li role="presentation"><a href="#review" data-toggle="tab">Review Requests</a></li>
        <g:if test="${d.source && d.source.automaticUpdates}">
          <li role="presentation"><a href="#autoUp" data-toggle="tab">Automatic Updates History</a></li>
        </g:if>
      </g:if>
      <g:else>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Titles </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Identifiers </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Alternate Names </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Relations </span></li>
        <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">
          <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Decision Support </span></li>
        </g:if>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Activity </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Review Requests </span></li>
      </g:else>
    </ul>

    <div id="my-tab-content" class="tab-content">

      <div class="tab-pane active" id="currentTitles">
        <g:if test="${params.controller != 'create'}">
          <dl>
            <dt><gokb:annotatedLabel owner="${d}" property="tipps">Current Titles</gokb:annotatedLabel></dt>
            <dd>
              <g:link class="display-inline" controller="search" action="index"
                      params="[qbe: 'g:tipps', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status_id: RDStore.KBC_STATUS_CURRENT.id]"
                      id="">Titles in this package</g:link>
            </dd>
          </dl>
        </g:if>
      </div>

      <div class="tab-pane" id="retiredTitles">
        <g:if test="${params.controller != 'create'}">
          <dl>
            <dt><gokb:annotatedLabel owner="${d}" property="tipps">Retired Titles</gokb:annotatedLabel></dt>
            <dd>
              <g:link class="display-inline" controller="search" action="index"
                      params="[qbe: 'g:tipps', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status_id: RDStore.KBC_STATUS_RETIRED.id]"
                      id="">Titles in this package</g:link>
            </dd>
          </dl>
        </g:if>
      </div>

      <div class="tab-pane" id="expectedTitles">
        <g:if test="${params.controller != 'create'}">
          <dl>
            <dt><gokb:annotatedLabel owner="${d}" property="tipps">Expected Titles</gokb:annotatedLabel></dt>
            <dd>
              <g:link class="display-inline" controller="search" action="index"
                      params="[qbe: 'g:tipps', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status_id: RDStore.KBC_STATUS_EXPECTED.id]"
                      id="">Titles in this package</g:link>
            </dd>
          </dl>
        </g:if>
      </div>

      <div class="tab-pane" id="deletedTitles">
        <g:if test="${params.controller != 'create'}">
          <dl>
            <dt><gokb:annotatedLabel owner="${d}" property="tipps">Deleted Titles</gokb:annotatedLabel></dt>
            <dd>
              <g:link class="display-inline" controller="search" action="index"
                      params="[qbe: 'g:tipps', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status_id: RDStore.KBC_STATUS_DELETED.id]"
                      id="">Titles in this package</g:link>
            </dd>
          </dl>
        </g:if>
      </div>

      <g:render template="/tabTemplates/showVariantnames" model="${[showActions:true]}" />

      <g:render template="/tabTemplates/showDDCs" model="${[showActions:true]}" />

      <g:render template="/tabTemplates/showIdentifiers" model="${[d: d]}" />

      <div class="tab-pane" id="relationships">
        <g:if test="${d.id != null}">
          <dl class="dl-horizontal">
            <dt>
              <gokb:annotatedLabel owner="${d}" property="successor">Successor</gokb:annotatedLabel>
            </dt>
            <dd>
              <gokb:manyToOneReferenceTypedown owner="${d}" field="successor" baseClass="org.gokb.cred.Package">${d.successor?.name}</gokb:manyToOneReferenceTypedown>
            </dd>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="successor">Predecessor(s)</gokb:annotatedLabel>
            </dt>
            <dd>
              <ul>
                <g:each in="${d.previous}" var="c">
                  <li>
                    <g:link controller="resource" action="show" id="${c.getClassName()+':'+c.id}">
                      ${c.name}
                    </g:link>
                  </li>
                </g:each>
              </ul>
            </dd>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="parent">Parent</gokb:annotatedLabel>
            </dt>
            <dd>
              <gokb:manyToOneReferenceTypedown owner="${d}" field="parent" baseClass="org.gokb.cred.Package">${d.parent?.name}</gokb:manyToOneReferenceTypedown>
            </dd>

            <g:if test="${d.children.size() > 0}">
              <dt>
                <gokb:annotatedLabel owner="${d}" property="children">Subsidiaries</gokb:annotatedLabel>
              </dt>
              <dd>
                <ul>
                  <g:each in="${d.children}" var="c">
                    <li>
                      <g:link controller="resource" action="show" id="${c.getClassName()+':'+c.id}">
                        ${c.name}
                      </g:link>
                    </li>
                  </g:each>
                </ul>
              </dd>
            </g:if>
          </dl>
        </g:if>
      </div>

      <div class="tab-pane" id="ds">
        <g:render template="/apptemplates/secondTemplates/dstab" model="${[d:d]}" />
      </div>

      <div class="tab-pane" id="activity">
        <g:set var="recentActivitys" value="${d?.getRecentActivity()}"/>
        <g:if test="${recentActivitys.size() > 25}">
          <g:link controller="package" action="recentActivity" id="${d.id}">All Recent Activitys (${recentActivitys.size()})</g:link>
          <g:set var="recentActivitys" value="${recentActivitys.take(25)}"/>
          <br><br>
        </g:if>
        <g:render template="/apptemplates/secondTemplates/recentActivity" model="[recentActivitys: recentActivitys]"/>
      </div>

      <div class="tab-pane" id="review">
        <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d:d]}" />

        <div class="connected-rr">
          <h3>Review Requests for connected Titles</h3>
          <div>
            <span style="margin-right:10px;">
              <button class="btn btn-default" id="rr-only-open">Load Open Requests</button>
              <button class="btn btn-default" id="rr-all">Load All Requests</button>
            </span>
            <span style="white-space:nowrap;">
              <span>TIPP status restriction: </span>
              <select class="form-control" id="rr-tipp-status">
                <option>None</option>
                <option>Current</option>
              </select>
            </span>
          </div>
          <div id="rr-loaded"></div>
        </div>
      </div>

      <div class="tab-pane" id="autoUp">

        <h3>History of Auto Updates</h3>

        <table class="table table-bordered table-striped">
          <thead>
          <tr>
            <th>#</th>
            <th>Start Time</th>
            <th>End Time</th>
            <th>Status</th>
            <th>Info</th>
          </tr>
          </thead>
          <tbody>
          <g:each in="${d.getAutoUpdateJobResult()}" var="jobResult" status="i">
            <g:set var="json" value="${jobResult.resultJson}" />
            <tr>
              <td>${i+1}</td>
              <td>${jobResult.startTime}</td>
              <td>${jobResult.endTime}</td>
              <td>${jobResult.statusText}</td>

              <td><g:if test="${json}">

                <b>${json.message}</b>
                <br>
                <br>
                <g:if test="${json.packageUpdateNote}">
                  ${json.packageUpdateNote}
                  <br>
                  <br>
                </g:if>

                <g:if test="${json.messages}">
                  Messages:
                  <ul>
                    <g:each in="${json.messages}" var="m">
                      <g:if test="${m instanceof String}">
                        <li>${m}</li>
                      </g:if>
                      <g:else>
                        <li>${m.message}</li>
                      </g:else>
                    </g:each>
                  </ul>
                </g:if>
                <g:if test="${!json.messages}">
                  No Messages
                </g:if>

                <g:if test="${json.errors?.global}">
                  <div>Global Errors</div>
                  <ul>
                    <g:each in="${json.errors?.global}" var="ge">
                      <li>${ge}</li>
                    </g:each>
                  </ul>
                </g:if>
                <g:if test="${json.errors?.tipps}">
                  <div>Titles Errors</div>
                  <ul>
                    <g:each in="${json.errors?.tipps}" var="te">
                      <li>${te}</li>
                    </g:each>
                  </ul>
                </g:if>
                <g:if test="${!json.errors}">
                  No Errors
                </g:if>


                <g:if test="${json.ygorStatisticResultHash}">
                  <br>
                  <br>
                  <g:link controller="package" action="showYgorStatistic" id="${json.ygorStatisticResultHash}" class="btn btn-default" target="_blank">Show Ygor Statistic</g:link>
                </g:if>

              </g:if>
              </td>
            </tr>
          </g:each>
          </tbody>
        </table>
      </div>

    </div>

      <g:render template="/apptemplates/secondTemplates/componentStatus"/>

  </div>

    <g:javascript>
      $(document).ready(function(){
        $("#rr-only-open").click(function(e) {
          e.preventDefault();

          var tipp_restrict = $("#rr-tipp-status").val();

          $.ajax({
            url: "/gokb/packages/connectedRRs",
            data: {id: "${d.id}", restrict: tipp_restrict},
            beforeSend: function() {
              $('#rr-loaded').empty();
              $('#rr-loaded').after('<div id="rr-loading" style="height:50px;vertical-align:middle;text-align:center;"><span>Loading list <asset:image src="loading.gif" /></span></div>');
            },
            complete: function() {
              $('#rr-loading').remove();
            },
            success: function(result) {
              $("#rr-loaded").html(result);
            }
          });
        });
        $("#rr-all").click(function(e) {
          e.preventDefault();

          var tipp_restrict = $("#rr-tipp-status").val();

          $.ajax({
            url: "/gokb/packages/connectedRRs",
            data: {id: "${d.id}", getAll: true, restrict: tipp_restrict},
            beforeSend: function() {
              $('#rr-loaded').empty();
              $('#rr-loaded').after('<div id="rr-loading" style="height:50px;vertical-align:middle;text-align:center;"><span>Loading list <asset:image src="loading.gif" /></span></div>');
            },
            complete: function() {
              $('#rr-loading').remove();
            },
            success: function(result) {
              $("#rr-loaded").html(result);
            }
          });
        });
      });
    </g:javascript>
