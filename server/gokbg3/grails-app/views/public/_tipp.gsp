<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%
<dl class="row">
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="title">Title</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left"">
        <g:if test="${controllerName != 'public'}">
            <g:link controller="resource" action="show"
                    id="${d.title?.class?.name + ':' + d.title?.id}">
                ${(d.title?.name) ?: 'Empty'}
            </g:link>
        </g:if><g:else>
            ${(d.title?.name) ?: 'Empty'}
        </g:else>
        <g:if test="${d.title}">(${d.title.niceName})</g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="package">Package</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${controllerName != 'public'}">
            <g:link controller="resource" action="show"
                    id="${d.pkg?.class?.name + ':' + d.pkg?.id}">
                ${(d.pkg?.name) ?: 'Empty'}
            </g:link>
        </g:if>
        <g:else>
            ${(d.pkg?.name) ?: 'Empty'}
        </g:else>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="platform">Platform</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${controllerName != 'public'}">
            <g:link controller="resource" action="show"
                    id="${d.hostPlatform?.class?.name + ':' + d.hostPlatform?.id}">
                ${(d.hostPlatform?.name) ?: 'Empty'}
            </g:link>
        </g:if>
        <g:else>
            ${(d.hostPlatform?.name) ?: 'Empty'}
        </g:else>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="url">Host Platform URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="url"/>
        <g:if test="${d.url}">
            &nbsp;<a href="${d.url}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>

    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="publicationType">Publication Type</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="publicationType" config="${RCConstants.TIPP_PUBLICATION_TYPE}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="medium">Medium</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="medium" config="${RCConstants.TIPP_MEDIUM}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="language">Language</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="language" config="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="firstAuthor">First Author</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="firstAuthor"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="publisherName">Publisher Name</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="publisherName"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="dateFirstInPrint">Date first in print</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" type="date"
                        field="dateFirstInPrint"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="dateFirstOnline">Date first online</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" type="date"
                        field="dateFirstOnline"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="accessStartDate">Access Start Date</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" type="date"
                        field="accessStartDate"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="accessEndDate">Access End Date</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" type="date"
                        field="accessEndDate"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="volumeNumber">Volume Number</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="volumeNumber"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="editionStatement">Edition</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="editionStatement"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}"
                             property="parentPublicationTitleId">Parent publication title ID</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="parentPublicationTitleId"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}"
                             property="precedingPublicationTitleId">Preceding publication title ID</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="precedingPublicationTitleId"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="reference">Reference</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="reference"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="editStatus">Edit Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="editStatus"
                               config="${RCConstants.KBCOMPONENT_EDIT_STATUS}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="status"
                               config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </dd>


    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="lastChangedExternal">Last change</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable class="ipe" owner="${d}" field="lastChangedExternal" type='date'/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="format">Format</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="format"
                               config="${RCConstants.TIPP_FORMAT}"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="paymentType">Payment Type</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="paymentType"
                               config="${RCConstants.TIPP_PAYMENT_TYPE}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="accessType">Access Type</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="accessType"
                               config="${RCConstants.TIPP_ACCESS_TYPE}"/>
    </dd>

</dl>

