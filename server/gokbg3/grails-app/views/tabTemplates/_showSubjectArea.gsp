<%@ page import="de.wekb.helper.RCConstants;" %>
<div class="tab-pane" id="subjectArea" role="tabpanel">
    <g:if test="${d.id != null}">
        <dl>
            <dt>
                <gokb:annotatedLabel owner="${d}" property="subjectArea">Subject Area</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditable owner="${d}" field="subjectArea"/>
            </dd>
        </dl>
    </g:if>
</div>
