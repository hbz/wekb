<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%
<dl>
    <dt class="control-label">
        Title
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Package
    </dt>
    <dd>
        <g:link controller="resource" action="show"
                id="${d.pkg?.class?.name + ':' + d.pkg?.id}">
            ${(d.pkg?.name) ?: 'Empty'}
        </g:link>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Platform
    </dt>
    <dd>
            <g:if test="${d.hostPlatform}">
                <g:link controller="resource" action="show"
                        id="${d.hostPlatform.uuid}">${d.hostPlatform.name}</g:link>
            </g:if>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Host Platform URL
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="url" outGoingLink="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Publication Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="publicationType" config="${RCConstants.TIPP_PUBLICATION_TYPE}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Medium
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="medium" config="${RCConstants.TIPP_MEDIUM}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Language
    </dt>
    <dd>
        <g:render template="/templates/languages"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        First Author
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="firstAuthor"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        First Editor
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="firstEditor"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Publisher Name
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="publisherName"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Date first in print
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="dateFirstInPrint"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Date first online
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="dateFirstOnline"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Access Start Date
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="accessStartDate"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Access End Date
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="accessEndDate"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Volume Number
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="volumeNumber"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Edition
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="editionStatement"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Access Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="accessType"
                                config="${RCConstants.TIPP_ACCESS_TYPE}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Notes
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="note"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Status
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="status"
                                config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </dd>

</dl>
<dl>
    <dt class="control-label">
        Last Changed
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="lastChangedExternal" type="date"/>
    </dd>

</dl>
