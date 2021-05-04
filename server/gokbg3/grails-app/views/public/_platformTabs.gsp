<%@ page import="de.wekb.helper.RCConstants" %>
%{--
<g:set var="editable" value="${ d.isEditable() && ((d.respondsTo('getCuratoryGroups') ? (request.curator != null && request.curator.size() > 0) : true) || (params.curationOverride == 'true' && request.user.isAdmin())) }" />
--}%


<div id="content">
    <ul id="tabs" class="nav nav-tabs" role="tablist">
        <g:if test="${d.id}">
            <li class="nav-item">
                <a class="nav-link active" href="#titledetails" data-toggle="tab" role="tab">Hosted Titles</a>
            </li>
            <li>
                <a class="nav-link"  href="#packages" data-toggle="tab" role="tab">Packages</a>
            </li>
            <li class="nav-item">
                <a class="nav-link"  href="#altnames" data-toggle="tab" role="tab">Alternate Names <span
                    class="badge badge-pill badge-info">${d.variantNames?.size() ?: '0'}</span></a>
            </li>
            <g:if test="${controllerName != 'public'}">
                <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">

                    <li class="nav-item">
                        <a class="nav-link"  href="#ds" data-toggle="tab" role="tab">Decision Support</a>
                    </li>
                </g:if>

                <li class="nav-item">
                    <a class="nav-link" href="#review" data-toggle="tab" role="tab">Review Tasks (Open/Total)<span
                        class="badge badge-pill badge-info">
                    ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open') }?.size() ?: '0'}/${d.reviewRequests.size()}

                </span></a>
                </li>
            </g:if>
        </g:if>
        <g:else>
            <li class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Hosted Titles</span></li>
            <li class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Packages</span></li>
            <li class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Alternate Names</span></li>
            <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">
                <li class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                        class="nav-tab-disabled">Decision Support</span></li>
            </g:if>
            <li class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Review Tasks</span></li>
        </g:else>
    </ul>


    <div id="my-tab-content" class="tab-content">

        <div class="tab-pane fade show active" id="titledetails" role="tabpanel">

            <g:if test="${params.controller != 'create'}">
                <g:link class="display-inline" controller="search" action="index"
                        params="[qbe: 'g:tipps', qp_plat_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_cp', 'qp_pub_id', 'qp_plat', 'qp_plat_id']]"
                        id="">Titles on this Platform</g:link>
            </g:if>
            <g:else>
                Titles can be added after the creation process has been finished.
            </g:else>
        </div>

        <div class="tab-pane fade" id="packages" role="tabpanel">
            <dl>
                <dt>
                    <gokb:annotatedLabel owner="${d}" property="packages">Packages</gokb:annotatedLabel>
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_platform', 'qp_platform_id']]"
                            id="">Packages on this Platform</g:link>
                </dd>
            </dl>
        </div>

        <g:render template="/tabTemplates/showVariantnames"
                  model="${[d: d, showActions: true]}"/>

        <g:if test="${controllerName != 'public'}">
            <div class="tab-pane fade" id="ds" role="tabpanel">
                <g:render template="/apptemplates/secondTemplates/dstab" model="${[d: d]}"/>
            </div>

            <div class="tab-pane fade" id="review" role="tabpanel">
                <g:render template="/apptemplates/secondTemplates/revreqtab"
                          model="${[d: d]}"/>
            </div>
        </g:if>

    </div>
    <g:if test="${d.id}">
        <g:render template="componentStatus"
                  model="${[d: d]}"/>
    </g:if>

</div>
