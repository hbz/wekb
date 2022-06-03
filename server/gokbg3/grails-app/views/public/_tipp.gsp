<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%
<dl class="row">
    <dt>
        Title
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name"/>
    </dd>

    <dt>
        Package
    </dt>
    <dd>
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

    <dt>
        Platform
    </dt>
    <dd>
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

    <dt>
        Host Platform URL
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="url"/>
        <g:if test="${d.url}">
            &nbsp;<a aria-label="${d.url}" href="${d.url.startsWith('http') ? d.url : 'http://' + d.url}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>

    </dd>

    <dt>
        Publication Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="publicationType" config="${RCConstants.TIPP_PUBLICATION_TYPE}"/>
    </dd>

    <dt>
        Medium
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="medium" config="${RCConstants.TIPP_MEDIUM}"/>
    </dd>

    <dt>
        Language
    </dt>
    <dd>
        <g:render template="/templates/languages"/>
    </dd>

    <dt>
        First Author
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="firstAuthor"/>
    </dd>

    <dt>
        First Editor
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="firstEditor"/>
    </dd>

    <dt>
        Publisher Name
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="publisherName"/>
    </dd>

    <dt>
        Date first in print
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" type="date"
                        field="dateFirstInPrint"/>
    </dd>

    <dt>
        Date first online
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" type="date"
                        field="dateFirstOnline"/>
    </dd>

    <dt>
        Access Start Date
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" type="date"
                        field="accessStartDate"/>
    </dd>

    <dt>
        Access End Date
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" type="date"
                        field="accessEndDate"/>
    </dd>

    <dt>
        Volume Number
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="volumeNumber"/>
    </dd>

    <dt>
        Edition
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="editionStatement"/>
    </dd>

    <dt>
        Access Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="accessType"
                               config="${RCConstants.TIPP_ACCESS_TYPE}"/>
    </dd>

    <dt>
        Notes
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="note"/>
    </dd>

    <dt>
        Status
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="status"
                               config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </dd>


    <dt>
        Last Changed
    </dt>
    <dd>
        <semui:xEditable  owner="${d}" field="lastChangedExternal" type='date'/>
    </dd>

</dl>

