<%@ page import="de.wekb.helper.RCConstants;" %>
<semui:tabsItemContent tab="openAccess">
    <g:if test="${d.id != null}">
        <dl>
            <dt class="control-label">
                Open Access
            </dt>
            <dd>
                <semui:xEditableRefData owner="${d}" field="openAccess" config="${RCConstants.TIPP_OPEN_ACCESS}"/>
            </dd>
        </dl>
    </g:if>
</semui:tabsItemContent>
