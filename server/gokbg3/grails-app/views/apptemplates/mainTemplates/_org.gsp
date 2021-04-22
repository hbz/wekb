<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable" value="${ d.isEditable() && ((d.respondsTo('getCuratoryGroups') ? (request.curator != null && request.curator.size() > 0) : true) || (params.curationOverride == 'true' && request.user.isAdmin())) }" />--}%
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
    <sec:ifAnyGranted roles="ROLE_SUPERUSER">
      <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" />
    </sec:ifAnyGranted>
    <sec:ifNotGranted roles="ROLE_SUPERUSER">
      ${d.status?.value ?: 'Not Set'}
    </sec:ifNotGranted>
  </dd>
  <dt>
          <gokb:annotatedLabel owner="${d}" property="reference">Reference</gokb:annotatedLabel>
  </dt>
  <dd>
          <gokb:xEditable  owner="${d}" field="reference" />
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="source">Source</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="source" baseClass="org.gokb.cred.Source">${d.source?.name}</gokb:manyToOneReferenceTypedown>
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="titleNamespace">Title Namespace</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="titleNamespace" baseClass="org.gokb.cred.IdentifierNamespace">${(d.titleNamespace?.name)?:d.titleNamespace?.value}</gokb:manyToOneReferenceTypedown>
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="packageNamespace">Package Namespace</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:manyToOneReferenceTypedown owner="${d}" field="packageNamespace" baseClass="org.gokb.cred.IdentifierNamespace">${(d.packageNamespace?.name)?:d.packageNamespace?.value}</gokb:manyToOneReferenceTypedown>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="mission">Mission</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="mission" config="${RCConstants.ORG_MISSION}" />
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="homepage">Homepage</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" field="homepage" />
  </dd>
  <dt class="dt-label">
    <gokb:annotatedLabel owner="${d}" property="roles">Roles</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:if test="${d.id != null}">
      <g:if test="${d.roles}">
        <ul>
          <g:each in="${d.roles?.sort{it.getI10n('value')}}" var="t">
            <li>
              ${t.value}
            <g:if test="${editable}">
              <g:link controller='ajaxSupport'
                      action='unlinkManyToMany'
                      params="${ ["__context" : "${d.class.name}:${d.id}", "__property" : "roles", "__itemToRemove" : "${t.getClassName()}:${t.id}" ] }">Unlink</g:link>
            </g:if>
            </li>
          </g:each>
        </ul>
      </g:if>

      <g:if test="${editable}">
        <a data-toggle="modal" data-cache="false"
           data-target="#rolesModal">Add Role</a>

        <bootStrap:modal id="rolesModal" title="Add Role">

          <g:form controller="ajaxSupport" action="addToStdCollection" class="form-inline">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}" />
            <input type="hidden" name="__property" value="roles" />
            Role: <gokb:simpleReferenceTypedown class="form-inline" style="display:inline-block;" name="__relatedObject"
                                          baseClass="org.gokb.cred.RefdataValue" filter1="${RCConstants.ORG_ROLE}" />
          </g:form>
        </bootStrap:modal>
      </g:if>
    </g:if>
  </dd>
</dl>
<div id="content">
    <ul id="tabs" class="nav nav-tabs">
      <g:if test="${d.id}">
        <li class="active">
          <a href="#identifiers" data-toggle="tab">Identifiers <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('ids','Active')?.size() ?: '0'} </span></a>
        </li>
        <li>
          <a href="#altnames" data-toggle="tab">
            Alternate Names
            <span class="badge badge-warning"> ${d.variantNames?.size() ?: '0'}</span>
          </a>
        </li>
        <li><a href="#relationships" data-toggle="tab">Relations</a></li>
        <li>
          <a href="#packages" data-toggle="tab">Packages
            <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('providedPackages','Active')?.size() ?: '0'}</span>
          </a>
        </li>
        <li>
          <a href="#titles" data-toggle="tab">Published Titles
            <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('publishedTitles','Active')?.size() ?: '0'}</span>
          </a>
        </li>
        <li>
          <a href="#platforms" data-toggle="tab">Platforms
            <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('providedPlatforms','Active')?.size() ?: '0'}</span>
          </a>
        </li>
        <li>
          <a href="#addprops" data-toggle="tab">
            Custom Fields
            <span class="badge badge-warning"> ${d.additionalProperties?.size() ?: '0'}</span>
          </a>
        </li>
        <li>
          <a href="#review" data-toggle="tab">
            Review Tasks (Open/Total)
            <span class="badge badge-warning"> ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS,'Open') }?.size() ?: '0'}/${d.reviewRequests.size()} </span>
          </a>
        </li>
        <li>
          <a href="#offices" data-toggle="tab">
            Offices
            <span class="badge badge-warning"> ${d.offices?.size() ?: '0'}</span>
          </a>
        </li>
      </g:if>
      <g:else>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">IDs </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Alternate Names </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Relations </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Packages </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Published Titles </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Platforms </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Custom Fields </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Review Tasks </span></li>
        <li class="disabled" title="${message(code:'component.create.idMissing.label')}"><span class="nav-tab-disabled">Offices </span></li>
      </g:else>
    </ul>

    <div id="my-tab-content" class="tab-content">

        <div class="tab-pane active" id="identifiers">
          <dl>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="ids">Identifiers</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/combosByType"
                model="${[d:d, property:'ids', fragment:'identifiers', cols:[
                          [expr:'toComponent.namespace.value', colhead:'Namespace'],
                          [expr:'toComponent.value', colhead:'ID', action:'link']]]}" />
              <g:if test="${editable}">
                <g:render template="/apptemplates/secondTemplates/addIdentifier" model="${[d:d, hash:'#identifiers']}"/>
              </g:if>
            </dd>
          </dl>
        </div>

      <g:render template="/tabTemplates/showVariantnames" model="${[d:d, showActions:true]}" />

        <div class="tab-pane" id="relationships">
          <g:if test="${d.id != null}">
            <dl class="dl-horizontal">
              <dt>
                <gokb:annotatedLabel owner="${d}" property="successor">Successor</gokb:annotatedLabel>
              </dt>
              <dd>
                <gokb:manyToOneReferenceTypedown owner="${d}" field="successor" baseClass="org.gokb.cred.Org">${d.successor?.name}</gokb:manyToOneReferenceTypedown>
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
                <gokb:annotatedLabel owner="${d}" property="parent">Parent Org</gokb:annotatedLabel>
              </dt>
              <dd>
                <gokb:manyToOneReferenceTypedown owner="${d}" field="parent" baseClass="org.gokb.cred.Org">${d.parent?.name}</gokb:manyToOneReferenceTypedown>
              </dd>

              <g:if test="${d.children?.size() > 0}">
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
              <dt>
                <gokb:annotatedLabel owner="${d}" property="imprints">Imprints</gokb:annotatedLabel>
              </dt>
              <dd>
                <table class="table table-striped table-bordered">
                  <thead>
                    <tr>
                      <th>Imprint Name</th>
                      <th>Combo Status</th>
                      <th>Imprint From</th>
                      <th>Imprint To</th>
                      <th>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    <g:each in="${d.getCombosByPropertyName('ownedImprints')}" var="p">
                      <tr>
                        <td><g:link controller="resource" action="show" id="${p.toComponent.class.name}:${p.toComponent.id}"> ${p.toComponent.name} </g:link></td>
                        <td><gokb:xEditableRefData owner="${p}" field="status" config="${RCConstants.COMBO_STATUS}" /></td>
                        <td><gokb:xEditable  owner="${p}" field="startDate" type="date" /></td>
                        <td><gokb:xEditable  owner="${p}" field="endDate" type="date" /></td>
                        <td><g:link controller="ajaxSupport" action="deleteCombo" id="${p.id}">Delete</g:link></td>
                      </tr>
                    </g:each>
                  </tbody>
                </table>
              </dd>
            </dl>
          </g:if>
          <g:else>
            Relations can be defined after the creation process is finished.
          </g:else>
        </div>

        <div class="tab-pane" id="addprops">
          <dl>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="addprops">Additional Properties</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/addprops" model="${[d:d]}" />
            </dd>
          </dl>
        </div>

        <div class="tab-pane" id="review">
          <dl>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="reviewrequests">Review Requests</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d:d]}" />
            </dd>
          </dl>
        </div>

        <div class="tab-pane" id="offices">
          <dl>
            <dt>
                    <gokb:annotatedLabel owner="${d}" property="offices">Offices</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/comboList"
                      model="${[d:d, property:'offices', noadd:true, cols:[[expr:'name',colhead:'Office Name', action:'link']],targetClass:'org.gokb.cred.Office',direction:'in',propagateDelete: 'true']}" />

              <g:if test="${d.isEditable()}">
                <g:if test="${d.id}">
                  <button
                    class="hidden-license-details btn btn-default btn-primary "
                    data-toggle="collapse" data-target="#collapseableAddOffice">
                    Add new <i class="fas fa-plus"></i>
                  </button>
                  <dl id="collapseableAddOffice" class="dl-horizontal collapse">
                    <g:form controller="ajaxSupport" action="addToCollection"
                            class="form-inline">
                      <input type="hidden" name="__context" value="${d.class.name}:${d.id}" />
                      <input type="hidden" name="__newObjectClass" value="org.gokb.cred.Office" />
                      <input type="hidden" name="__addToColl" value="offices" />
                      <dt class="dt-label">Office Name</dt>
                      <dd>
                        <input class="form-control" type="text" name="name" required />
                      </dd>
                      <dt class="dt-label">Website</dt>
                      <dd>
                        <input class="form-control" type="text" name="website" />
                      </dd>
                      <dt class="dt-label">Email</dt>
                      <dd>
                        <input class="form-control" type="text" name="email" />
                      </dd>
                      <dt class="dt-label">Number</dt>
                      <dd>
                        <input class="form-control" type="text" name="phoneNumber" />
                      </dd>
                      <dt class="dt-label">Address 1</dt>
                      <dd>
                        <input class="form-control" type="text" name="addressLine1" />
                      </dd>
                      <dt class="dt-label">Address 2</dt>
                      <dd>
                        <input class="form-control" type="text" name="addressLine2" />
                      </dd>
                      <dt class="dt-label">City</dt>
                      <dd>
                        <input class="form-control" type="text" name="city" />
                      </dd>
                      <dt class="dt-label">Region</dt>
                      <dd>
                        <input class="form-control" type="text" name="region" />
                      </dd>
                      <dt class="dt-label">Country</dt>
                      <dd>
                        <gokb:simpleReferenceTypedown class="form-control" name="country"
                          baseClass="org.gokb.cred.RefdataValue"
                          filter1="${RCConstants.COUNTRY}" />
                      </dd>
                      <dt class="dt-label"></dt>
                      <dd>
                        <button type="submit" class="btn btn-default btn-primary">Add</button>
                      </dd>
                    </g:form>
                  </dl>
                </g:if>
                <g:else>
                  Offices can be added after the creation process is finished.
                </g:else>
              </g:if>
            </dd>
          </dl>
        </div>

        <div class="tab-pane" id="licenses">
          <dl>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="licenses">Licenses</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/comboList"
                        model="${[d:d, property:'heldLicenses', cols:[[expr:'name',colhead:'License Name']],targetClass:'org.gokb.cred.License']}" />
            </dd>
          </dl>
        </div>

        <div class="tab-pane" id="platforms">
          <dl>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="platforms">Platforms</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/comboList"
                        model="${[d:d, property:'providedPlatforms', cols:[[expr:'name',colhead:'Platform Name',targetClass:'org.gokb.cred.Platform', action:'link'],[expr:'primaryUrl',colhead:'Primary URL',targetClass:'org.gokb.cred.Platform']]]}" />
            </dd>
          </dl>
        </div>

        <div class="tab-pane" id="titles">
          <g:if test="${d.id}">
            <g:link class="display-inline" controller="search" action="index"
                    params="[qbe:'g:titles', refOid: d.getLogEntityId(), inline:true, qp_pub_id:d.id, hide:['qp_pub','qp_pub_id']]"
                    id="">Titles published</g:link>
          </g:if>
        </div>

        <div class="tab-pane" id="packages">
          <dl>
            <dt>
              <gokb:annotatedLabel owner="${d}" property="packages">Packages</gokb:annotatedLabel>
            </dt>
            <dd>
              <g:render template="/apptemplates/secondTemplates/comboList"
                        model="${[d:d, property:'providedPackages', cols:[[expr:'name',colhead:'Package Name', action:'link']],targetClass:'org.gokb.cred.Package']}" />
            </dd>
          </dl>
        </div>
    </div>
    <g:if test="${d.id}">
      <g:render template="/apptemplates/secondTemplates/componentStatus"
                model="${[d:displayobj]}" />
    </g:if>
</div>
