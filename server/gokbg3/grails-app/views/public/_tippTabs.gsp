<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%

<ul id="tabs" class="nav nav-tabs">

    <g:if test="${d.niceName == 'Journal'}">
         <li role="presentation" class=nav-item">
            <a class="nav-link active" href="#tippcoverage" data-toggle="tab">Coverage</a>
        </li>
    </g:if>
    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#identifiers" data-toggle="tab">Identifiers <span
            class="badge badge-dark">${d?.getCombosByPropertyNameAndStatus('ids', 'Active')?.size() ?: '0'}</span>
    </a>
    </li>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#addprops" data-toggle="tab">Additional Properties
            <span class="badge badge-dark">${d.additionalProperties?.size() ?: '0'}</span>
        </a>
    </li>
    <g:if test="${controllerName != 'public'}">
        <li role="presentation" class=nav-item">
            <a class="nav-link" href="#review" data-toggle="tab">Review Requests
                <span class="badge badge-dark">${d.reviewRequests?.size() ?: '0'}</span>
            </a>
        </li>
    </g:if>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#subjectArea" data-toggle="tab">Subject Area</a>
    </li>
    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#series" data-toggle="tab">Series</a>
    </li>
    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#prices" data-toggle="tab">Prices
            <span class="badge badge-dark">${d.prices?.size() ?: '0'}</span>
        </a>
    </li>
</ul>


<div id="my-tab-content" class="tab-content">

    <g:if test="${d.niceName == 'Journal'}">
        <div class="tab-pane fade show active" id="tippcoverage">
            <dl class="row">
                <dt class="col-3 text-right">
                    <gokb:annotatedLabel owner="${d}" property="coverage">Coverage</gokb:annotatedLabel>
                </dt>
                <dd class="col-9 text-left">
                    <table class="table table-striped">
                        <thead>
                        <tr>
                            <th>Start Date</th>
                            <th>Start Volume</th>
                            <th>Start Issue</th>
                            <th>End Date</th>
                            <th>End Volume</th>
                            <th>End Issue</th>
                            <th>Embargo</th>
                            <th>Note</th>
                            <th>Depth</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <g:if test="${d.coverageStatements?.size() > 0}">
                            <g:each var="cs" in="${d.coverageStatements.sort { it.startDate }}">
                                <tr>
                                    <td><gokb:xEditable class="ipe" owner="${cs}" type="date"
                                                        field="startDate"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}"
                                                        field="startVolume"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}"
                                                        field="startIssue"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}" type="date"
                                                        field="endDate"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}" field="endVolume"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}" field="endIssue"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}" field="embargo"/></td>
                                    <td><gokb:xEditable class="ipe" owner="${cs}" field="coverageNote"/></td>
                                    <td><gokb:xEditableRefData owner="${cs}" field="coverageDepth"
                                                               config="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
                                    </td>
                                    <td><g:if test="${editable}"><g:link controller="ajaxSupport"
                                                                         action="deleteCoverageStatement"
                                                                         params="[id: cs.id, fragment: 'tippcoverage']">Delete</g:link></g:if></td>
                                </tr>
                            </g:each>
                        </g:if>
                        <g:else>
                            <tr><td colspan="8"
                                    style="text-align:center">${message(code: 'tipp.coverage.empty', default: 'No coverage defined')}</td>
                            </tr>
                        </g:else>
                        </tbody>
                    </table>
                    <g:if test="${editable}">
                        <button
                                class="hidden-license-details btn btn-default btn-sm btn-primary "
                                data-toggle="collapse" data-target="#collapseableAddCoverageStatement">
                            Add new <i class="fas fa-plus"></i>
                        </button>
                        <dl id="collapseableAddCoverageStatement" class="row collapse">
                            <g:form controller="ajaxSupport" action="addToCollection"
                                    class="form-inline" params="[fragment: 'tippcoverage']">
                                <input type="hidden" name="__context"
                                       value="${d.class.name}:${d.id}"/>
                                <input type="hidden" name="__newObjectClass"
                                       value="org.gokb.cred.TIPPCoverageStatement"/>
                                <input type="hidden" name="__recip" value="owner"/>
                                <dt class="dt-label">Start Date</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="date" name="startDate"/>
                                </dd>
                                <dt class="dt-label">Start Volume</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="text" name="startVolume"/>
                                </dd>
                                <dt class="dt-label">Start Issue</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="text" name="startIssue"/>
                                </dd>
                                <dt class="dt-label">End Date</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="date" name="endDate"/>
                                </dd>
                                <dt class="dt-label">End Volume</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="text" name="endVolume"/>
                                </dd>
                                <dt class="dt-label">End Issue</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="text" name="endIssue"/>
                                </dd>
                                <dt class="dt-label">Embargo</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="text" name="embargo"/>
                                </dd>
                                <dt class="dt-label">Coverage Depth</dt>
                                <dd class="col-9 text-left">
                                    <gokb:simpleReferenceTypedown name="coverageDepth"
                                                                  baseClass="org.gokb.cred.RefdataValue"
                                                                  filter1="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
                                </dd>
                                <dt class="dt-label">Coverage Note</dt>
                                <dd class="col-9 text-left">
                                    <input class="form-control" type="text" name="coverageNote"/>
                                </dd>
                                <dt class="col-3 text-right"></dt>
                                <dd class="col-9 text-left">
                                    <button type="submit"
                                            class="btn btn-default btn-primary btn-sm ">Add</button>
                                </dd>
                            </g:form>
                        </dl>
                    </g:if>
                </dd>
                <dt class="col-3 text-right">
                    <gokb:annotatedLabel owner="${d}" property="coverageNote">Coverage Note</gokb:annotatedLabel>
                </dt>
                <dd class="col-9 text-left">
                    <gokb:xEditable class="ipe" owner="${d}" field="coverageNote"/>
                </dd>
                <dt class="col-3 text-right">
                    <gokb:annotatedLabel owner="${d}" property="coverageDepth">Coverage Depth</gokb:annotatedLabel>
                </dt>
                <dd class="col-9 text-left">
                    <gokb:xEditableRefData owner="${d}" field="coverageDepth"
                                           config="${RCConstants.TIPP_COVERAGE_DEPTH}"/>
                </dd>
            </dl>
        </div>
    </g:if>


    <div class="tab-pane fade" id="identifiers">
        <dl class="row">
            <dt class="col-3 text-right">
                <gokb:annotatedLabel owner="${d}" property="ids">Identifiers</gokb:annotatedLabel>
            </dt>
            <dd class="col-9 text-left">
                <g:render template="/apptemplates/secondTemplates/combosByType"
                          model="${[d: d, property: 'ids', fragment: 'identifiers', combo_status: 'Active', cols: [
                                  [expr: 'toComponent.namespace.value', colhead: 'Namespace'],
                                  [expr: 'toComponent.value', colhead: 'ID', action: 'link']]]}"/>
                <g:if test="${editable}">
                    <h4>
                        <gokb:annotatedLabel owner="${d}"
                                             property="addIdentifier">Add new Identifier</gokb:annotatedLabel>
                    </h4>
                    <g:render template="/apptemplates/secondTemplates/addIdentifier" model="${[d: d, hash: '#identifiers']}"/>
                </g:if>
            </dd>
        </dl>

    </div>


    <div class="tab-pane fade" id="addprops">
        <g:render template="/apptemplates/secondTemplates/addprops" model="${[d: d]}"/>
    </div>

    <g:if test="${controllerName != 'public'}">
        <div class="tab-pane fade" id="review">
            <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d: d]}"/>
        </div>
    </g:if>

    <div class="tab-pan fade" id="subjectArea">
        <dl class="dl-horizontal">
            <dt class="col-3 text-right">
                <gokb:annotatedLabel owner="${d}" property="subjectArea">Subject Area</gokb:annotatedLabel>
            </dt>
            <dd class="col-9 text-left">
                <gokb:xEditable owner="${d}" field="subjectArea"/>
            </dd>
        </dl>
    </div>

    <div class="tab-pane fade" id="series">

        <dl class="row">
            <dt class="col-3 text-right">
                <gokb:annotatedLabel owner="${d}" property="series">Series</gokb:annotatedLabel>
            </dt>
            <dd class="col-9 text-left">
                <gokb:xEditable owner="${d}" field="series"/>
            </dd>
        </dl>
    </div>
    <g:render template="/tabTemplates/showPrices" model="${[d: d]}"/>
</div>
<g:render template="componentStatus"
          model="${[d: d]}"/>

