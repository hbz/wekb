<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%
<dl class="dl-horizontal">

    <dt>
        <gokb:annotatedLabel owner="${d}" property="name">Title</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="name"/>
    </dd>


    <sec:ifAnyGranted roles="ROLE_ADMIN">
        <g:if test="${controllerName != 'create'}">
            <dt>
                <gokb:annotatedLabel owner="${d}" property="title">Title</gokb:annotatedLabel>
            </dt>
            <dd style="max-width:60%">
                <g:link controller="resource" action="show"
                        id="${d.title?.class?.name + ':' + d.title?.id}">
                    ${(d.title?.name) ?: 'Empty'}
                </g:link>
                <g:if test="${d.title}">(${d.title.niceName})</g:if> (visible for ROLE_ADMIN only)
            </dd>
        </g:if>
    </sec:ifAnyGranted>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="package">Package</gokb:annotatedLabel>
    </dt>
    <dd>
        <g:if test="${controllerName == 'create'}">
            <gokb:manyToOneReferenceTypedown owner="${d}" field="pkg" baseClass="org.gokb.cred.Package"/>
        </g:if>
        <g:elseif test="${controllerName != 'public'}">
            <g:link controller="resource" action="show"
                    id="${d.pkg?.class?.name + ':' + d.pkg?.id}">
                ${(d.pkg?.name) ?: 'Empty'}
            </g:link>
        </g:elseif>
        <g:else>
            ${(d.pkg?.name) ?: 'Empty'}
        </g:else>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="platform">Platform</gokb:annotatedLabel>
    </dt>
    <dd>
        <g:if test="${controllerName == 'create'}">
            <gokb:manyToOneReferenceTypedown owner="${d}" field="hostPlatform" baseClass="org.gokb.cred.Platform"/>
        </g:if>
        <g:elseif test="${controllerName != 'public'}">
            <g:link controller="resource" action="show"
                    id="${d.hostPlatform?.class?.name + ':' + d.hostPlatform?.id}">
                ${(d.hostPlatform?.name) ?: 'Empty'}
            </g:link>
        </g:elseif>
        <g:else>
            ${(d.hostPlatform?.name) ?: 'Empty'}
        </g:else>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="url">Host Platform URL</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="url"/>
        <g:if test="${d.url}">
            &nbsp;<a href="${d.url}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>

    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="publicationType">Publication Type</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditableRefData owner="${d}" field="publicationType" config="${RCConstants.TIPP_PUBLICATION_TYPE}"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="medium">Medium</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditableRefData owner="${d}" field="medium" config="${RCConstants.TIPP_MEDIUM}"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="language">Language</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditableRefData owner="${d}" field="language" config="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="firstAuthor">First Author</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="firstAuthor"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="firstAuthor">First Editor</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="firstEditor"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="publisherName">Publisher Name</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="publisherName"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="dateFirstInPrint">Date first in print</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" type="date"
                        field="dateFirstInPrint"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="dateFirstOnline">Date first online</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" type="date"
                        field="dateFirstOnline"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="accessStartDate">Access Start Date</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" type="date"
                        field="accessStartDate"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="accessEndDate">Access End Date</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" type="date"
                        field="accessEndDate"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="volumeNumber">Volume Number</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="volumeNumber"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="editionStatement">Edition</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="editionStatement"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="accessType">Access Type</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditableRefData owner="${d}" field="accessType"
                               config="${RCConstants.TIPP_ACCESS_TYPE}"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="note">Notes</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="note"/>
    </dd>

    <dt>
        <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditableRefData owner="${d}" field="status"
                               config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </dd>


    <dt>
        <gokb:annotatedLabel owner="${d}" property="lastChangedExternal">Last Changed</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="lastChangedExternal" type='date'/>
    </dd>

</dl>

<g:if test="${d.id != null}">
    <ul id="tabs" class="nav nav-tabs">

        <g:if test="${d.publicationType?.value == 'Serial'}">
            <li class="active">
                <a href="#tippcoverage" data-toggle="tab">Coverage</a>
            </li>
        </g:if>
        <li class="${d.publicationType?.value != 'Serial' ? 'active' : ''}"><a href="#identifiers"
                                                                               data-toggle="tab">Identifiers <span
                    class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('ids', 'Active')?.size() ?: '0'}</span>
        </a>
        </li>

        <li>
            <a href="#addprops" data-toggle="tab">Additional Properties
                <span class="badge badge-warning">${d.additionalProperties.size()}</span>
            </a>
        </li>

        <li>
            <a href="#subjectArea" data-toggle="tab">Subject Area</a>
        </li>

        <li>
            <a href="#ddcs" data-toggle="tab">DDCs
                <span class="badge badge-warning">${d.ddcs.size()}</span>
            </a>
        </li>

        <li>
            <a href="#series" data-toggle="tab">Series</a>
        </li>

        <li>
            <a href="#openAccess" data-toggle="tab">Open Access</a>
        </li>

        <li>
            <a href="#prices" data-toggle="tab">Prices
                <span class="badge badge-warning">${d.prices.size()}</span>
            </a>
        </li>

        <g:if test="${controllerName != 'public'}">
            <li>
                <a href="#review" data-toggle="tab">Review Requests
                    <span class="badge badge-warning">${d.reviewRequests.size()}</span>
                </a>
            </li>
        </g:if>
    </ul>


    <div id="my-tab-content" class="tab-content">

        <g:render template="/tabTemplates/tippTabs" model="${[d: d]}"/>

    </div>
    <g:render template="/apptemplates/secondTemplates/componentStatus"
              model="${[d: d]}"/>
</g:if>
