<%@ page import="de.wekb.helper.RCConstants;" %>
<div class="tab-pane" id="openAccess" role="tabpanel">
    <g:if test="${d.id != null}">
        <dl>
            <dt>
                <gokb:annotatedLabel owner="${d}" property="openAccess">Open Access</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditableRefData owner="${d}" field="openAccess" config="${RCConstants.TIPP_OPEN_ACCESS}"/>
            </dd>
        </dl>
    </g:if>
</div>
