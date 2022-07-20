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
        Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="type" config="${RCConstants.AUTO_UPDATE_TYPE}" overwriteEditable="false"/>
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



