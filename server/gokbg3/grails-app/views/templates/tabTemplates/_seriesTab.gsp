<%@ page import="de.wekb.helper.RCConstants;" %>
<semui:tabsItemContent tab="series">
    <g:if test="${d.id != null}">
        <dl>
            <dt>
                Series
            </dt>
            <dd>
                <semui:xEditable owner="${d}" field="series"/>
            </dd>

            <dt>
                Parent publication title ID
            </dt>
            <dd>
                <semui:xEditable  owner="${d}" field="parentPublicationTitleId"/>
            </dd>

            <dt>
                Superseding publication title ID
            </dt>
            <dd>
                <semui:xEditable  owner="${d}" field="supersedingPublicationTitleId"/>
            </dd>

            <dt>
                Preceding publication title ID
            </dt>
            <dd>
                <semui:xEditable  owner="${d}" field="precedingPublicationTitleId"/>
            </dd>
        </dl>
    </g:if>
</semui:tabsItemContent>
