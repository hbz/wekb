<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%
<dl class="row">
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="name">Title</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        ${d.name}
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
            <g:if test="${d.pkg}">
                <g:link controller="public" action="packageContent"
                        id="${d.pkg.uuid}">${d.pkg.name}</g:link>
            </g:if>
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
            <g:if test="${d.hostPlatform}">
                <g:link controller="public" action="platformContent"
                        id="${d.hostPlatform.uuid}">${d.hostPlatform.name}</g:link>
            </g:if>
        </g:else>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="url">Host Platform URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="url"/>
        <g:if test="${d.url}">
            &nbsp;<a aria-label="${d.url}" href="${d.url.startsWith('http') ? d.url : 'http://' + d.url}" target="new"><i class="fas fa-external-link-alt"></i></a>
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
        <g:render template="/apptemplates/secondTemplates/languages"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="firstAuthor">First Author</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="firstAuthor"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="firstAuthor">First Editor</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="firstEditor"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="publisherName">Publisher Name</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="publisherName"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="dateFirstInPrint">Date first in print</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" type="date"
                        field="dateFirstInPrint"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="dateFirstOnline">Date first online</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" type="date"
                        field="dateFirstOnline"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="accessStartDate">Access Start Date</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" type="date"
                        field="accessStartDate"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="accessEndDate">Access End Date</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" type="date"
                        field="accessEndDate"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="volumeNumber">Volume Number</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="volumeNumber"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="editionStatement">Edition</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="editionStatement"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="accessType">Access Type</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="accessType"
                               config="${RCConstants.TIPP_ACCESS_TYPE}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="note">Notes</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable owner="${d}" field="note"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="status"
                               config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </dd>


    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="lastChangedExternal">Last Changed</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="lastChangedExternal" type='date'/>
    </dd>

</dl>

