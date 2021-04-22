<%@ page import="de.wekb.helper.RCConstants;" %>
<div class="tab-pane" id="subjectArea">
    <g:if test="${d.id != null}">
        <dl class="dl-horizontal">
            <dt>
                <gokb:annotatedLabel owner="${d}" property="subjectArea">Subject Area</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditable owner="${d}" field="subjectArea"/>
            </dd>
        </dl>
    </g:if>
</div>
