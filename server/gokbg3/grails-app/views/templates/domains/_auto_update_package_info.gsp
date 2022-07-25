<%@ page import="de.wekb.helper.RCConstants" %>
<dl>
    <dt class="control-label">
        Package
    </dt>
    <dd>
            <g:if test="${d.pkg}">
                <g:link controller="resource" action="show"
                        id="${d.pkg.uuid}">
                    ${(d.pkg.name) ?: 'Empty'}
                </g:link>
            </g:if>
            <g:else>Empty</g:else>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Description
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="description" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Status
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.AUTO_UPDATE_STATUS}" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Start Time
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="startTime" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        End Time
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date"
                         field="endTime" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Rows in Kbart-File by Update
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="countKbartRows" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Processed Kbart Rows by Update
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="countProcessedKbartRows" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        In valid Title by Update
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="countInValidTipps" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Titles changed by Update
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="countChangedTipps" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Titles Removed by Update
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="countRemovedTipps" overwriteEditable="false"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        New Titles by Update
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="countNewTipps" overwriteEditable="false"/>
    </dd>
</dl>

<dl>
    <dt class="control-label">
        Only kbart rows which last changed after last update run
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="onlyRowsWithLastChanged" overwriteEditable="false"/>
    </dd>
</dl>

<semui:tabs>
    <semui:tabsItemWithoutLink tab="autoUpdateTippInfos" counts="${d.autoUpdateTippInfos.size()}" class="active">
        Auto Update Title Infos
    </semui:tabsItemWithoutLink>
</semui:tabs>


<semui:tabsItemContent tab="autoUpdateTippInfos">

    <div class="content">

        <g:link class="display-inline" controller="search" action="inlineSearch"
                params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:autoUpdateTippInfos', qp_aup_id: d.id, inline: true]"
                id="">Auto Update Title Info on this Source</g:link>

    </div>

</semui:tabsItemContent>


