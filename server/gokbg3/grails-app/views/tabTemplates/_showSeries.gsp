<%@ page import="de.wekb.helper.RCConstants;" %>
<div class="tab-pane" id="series">
    <g:if test="${d.id != null}">
        <dl class="dl-horizontal">
            <dt>
                <gokb:annotatedLabel owner="${d}" property="series">Series</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditable owner="${d}" field="series"/>
            </dd>

            <dt>
                <gokb:annotatedLabel owner="${d}"
                                     property="parentPublicationTitleId">Parent publication title ID</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditable  owner="${d}" field="parentPublicationTitleId"/>
            </dd>

            <dt>
                <gokb:annotatedLabel owner="${d}"
                                     property="supercedingPublicationTitleId">Superceding publication title ID</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditable  owner="${d}" field="supercedingPublicationTitleId"/>
            </dd>

            <dt>
                <gokb:annotatedLabel owner="${d}"
                                     property="precedingPublicationTitleId">Preceding publication title ID</gokb:annotatedLabel>
            </dt>
            <dd>
                <gokb:xEditable  owner="${d}" field="precedingPublicationTitleId"/>
            </dd>
        </dl>
    </g:if>
</div>
