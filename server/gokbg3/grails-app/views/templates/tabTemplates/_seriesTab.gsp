<%@ page import="de.wekb.helper.RCConstants;" %>
<semui:tabsItemContent tab="series">
    <g:if test="${d.id != null}">
        <dl>
            <dt class="control-label">
                Series
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="series"/>
            </dd>

            <dt class="control-label">
                Parent publication title ID
            </dt>
            <dd>
                <semui:xEditable  owner="${d}" field="parentPublicationTitleId"/>
            </dd>

            <dt class="control-label">
                Superseding publication title ID
            </dt>
            <dd>
                <semui:xEditable  owner="${d}" field="supersedingPublicationTitleId"/>
            </dd>

            <dt class="control-label">
                Preceding publication title ID
            </dt>
            <dd>
                <semui:xEditable  owner="${d}" field="precedingPublicationTitleId"/>
            </dd>
        </dl>
    </g:if>
</semui:tabsItemContent>
