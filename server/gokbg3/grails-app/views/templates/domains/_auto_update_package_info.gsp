<%@ page import="de.wekb.helper.RCConstants" %>
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


