<%@ page import="de.wekb.helper.RCConstants" %>
%{--
<g:set var="editable" value="${ d.isEditable() && ((d.respondsTo('getCuratoryGroups') ? (request.curator != null && request.curator.size() > 0) : true) || (params.curationOverride == 'true' && request.user.isAdmin())) }" />
--}%
<dl class="row">
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="name"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${editable}">
            <gokb:xEditableRefData owner="${d}" field="status"
                                   config="${RCConstants.KBCOMPONENT_STATUS}"/>
        </g:if>
        <g:else>
            ${d.status}
        </g:else>
    </dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="source">Source</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:manyToOneReferenceTypedown owner="${d}" field="source"
                                         baseClass="org.gokb.cred.Source">${d.source?.name}</gokb:manyToOneReferenceTypedown></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="source">Provider</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:manyToOneReferenceTypedown owner="${d}" field="provider"
                                         baseClass="org.gokb.cred.Org">${d.provider?.name}</gokb:manyToOneReferenceTypedown></dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="editStatus">Edit Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="editStatus"
                               config="${RCConstants.KBCOMPONENT_EDIT_STATUS}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="primaryURL">Primary URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="primaryUrl">${d.primaryUrl}</gokb:xEditable>
        <g:if test="${d.primaryUrl}">
            <g:if test="${d.primaryUrl.startsWith('http')}">
                &nbsp; <a href="${d.primaryUrl}" target="new"><i class="fas fa-external-link-alt"></i></a>
            </g:if>
            <g:else>
                &nbsp; <span class="badge badge-warning">!Unknown URL format!</span>
            </g:else>
        </g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="software">Software</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="software"
                               config="${RCConstants.PLATFORM_SOFTWARE}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="service">Service</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="service"
                               config="${RCConstants.PLATFORM_SERVICE}"/>
    </dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="authentication">Authentication</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="authentication"
                               config="${RCConstants.PLATFORM_AUTH_METHOD}"/></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="ipAuthentication">IP Auth Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="ipAuthentication" config="${RCConstants.YN}"/></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="shibbolethAuthentication">Shibboleth Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="shibbolethAuthentication"
                               config="${RCConstants.YN}"/></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="passwordAuthentication">User/Pass Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="passwordAuthentication" config="${RCConstants.YN}"/></dd>


</dl>

<div id="content">
    <ul id="tabs" class="nav nav-tabs">
        <g:if test="${d.id}">
            <li role="presentation" class="nav-item">
                <a class="nav-link active" href="#titledetails" data-toggle="tab">Hosted TIPPs</a>
            </li>
            <li>
                <a class="nav-link"  href="#packages" data-toggle="tab">Packages</a>
            </li>
            <li role="presentation" class="nav-item">
                <a class="nav-link"  href="#altnames" data-toggle="tab">Alternate Names <span
                    class="badge badge-warning">${d.variantNames?.size() ?: '0'}</span></a>
            </li>
            <g:if test="${controllerName != 'public'}">
                <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">

                    <li role="presentation" class="nav-item">
                        <a class="nav-link"  href="#ds" data-toggle="tab">Decision Support</a>
                    </li>
                </g:if>

                <li role="presentation" class="nav-item">
                    <a class="nav-link" href="#review" data-toggle="tab">Review Tasks (Open/Total)<span
                        class="badge badge-warning">
                    ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open') }?.size() ?: '0'}/${d.reviewRequests.size()}

                </span></a>
                </li>
            </g:if>
        </g:if>
        <g:else>
            <li role="presentation" class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Hosted TIPPs</span></li>
            <li role="presentation" class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Packages</span></li>
            <li role="presentation" class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Alternate Names</span></li>
            <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">
                <li role="presentation" class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                        class="nav-tab-disabled">Decision Support</span></li>
            </g:if>
            <li role="presentation" class="nav-item disabled" title="${message(code: 'component.create.idMissing.label')}"><span
                    class="nav-tab-disabled">Review Tasks</span></li>
        </g:else>
    </ul>


    <div id="my-tab-content" class="tab-content">

        <div class="tab-pane fade show active" id="titledetails">

            <g:if test="${params.controller != 'create'}">
                <g:link class="display-inline" controller="search" action="index"
                        params="[qbe: 'g:tipps', qp_plat_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_cp', 'qp_pub_id', 'qp_plat', 'qp_plat_id']]"
                        id="">TIPPs on this Platform</g:link>
            </g:if>
            <g:else>
                TIPPs can be added after the creation process has been finished.
            </g:else>
        </div>

        <div class="tab-pane fade" id="packages">
            <dl>
                <dt>
                    <gokb:annotatedLabel owner="${d}" property="packages">Packages</gokb:annotatedLabel>
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_platform', 'qp_platform_id']]"
                            id="">TIPPs on this Platform</g:link>
                </dd>
            </dl>
        </div>

        <g:render template="/tabTemplates/showVariantnames"
                  model="${[d: d, showActions: true]}"/>

        <g:if test="${controllerName != 'public'}">
            <div class="tab-pane fade" id="ds">
                <g:render template="/apptemplates/secondTemplates/dstab" model="${[d: d]}"/>
            </div>

            <div class="tab-pane fade" id="review">
                <g:render template="/apptemplates/secondTemplates/revreqtab"
                          model="${[d: d]}"/>
            </div>
        </g:if>

    </div>
    <g:if test="${d.id}">
        <g:render template="/apptemplates/secondTemplates/componentStatus"
                  model="${[d: d]}"/>
    </g:if>

</div>
