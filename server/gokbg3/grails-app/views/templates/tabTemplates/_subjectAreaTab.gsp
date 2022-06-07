<%@ page import="de.wekb.helper.RCConstants;" %>
<semui:tabsItemContent tab="subjectArea">
    <g:if test="${d.id != null}">
        <dl>
            <dt>
                Subject Area
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="subjectArea"/>
            </dd>
        </dl>
    </g:if>
</semui:tabsItemContent>
