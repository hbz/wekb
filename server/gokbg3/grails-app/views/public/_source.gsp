<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="row">
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="name">Source Name</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable owner="${d}" field="name"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="url">URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable owner="${d}" field="url"/>
        <g:if test="${d.url}">
            &nbsp;<a href="${d.url.startsWith('http') ? d.url : 'http://' + d.url}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="frequency">Frequency</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="defaultSupplyMethod">Default Supply Method</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="defaultSupplyMethod"
                               config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="defaultDataFormat">Default Data Format</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="defaultDataFormat"
                               config="${RCConstants.SOURCE_DATA_FORMAT}"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="responsibleParty">Responsible Party</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:manyToOneReferenceTypedown owner="${d}" field="responsibleParty"
                                         baseClass="org.gokb.cred.Org">
            ${d.responsibleParty?.name ?: ''}
        </gokb:manyToOneReferenceTypedown>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="automaticUpdates">Automated Updates</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableBoolean owner="${d}" field="automaticUpdates"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="targetNamespace">Title ID Namespace</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:manyToOneReferenceTypedown owner="${d}" field="targetNamespace"
                                         baseClass="org.gokb.cred.IdentifierNamespace">${d.targetNamespace}</gokb:manyToOneReferenceTypedown>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="ezbMatch">EZB Matching Enabled</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableBoolean owner="${d}" field="ezbMatch"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="zdbMatch">ZDB Matching Enabled</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableBoolean owner="${d}" field="zdbMatch"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="lastRun">Last Run</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable owner="${d}" type="date" field="lastRun">${d.lastRun}</gokb:xEditable>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="lastUpdateUrl">Last Update Url</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable owner="${d}" field="lastUpdateUrl" overwriteEditable="${false}"/>
        <g:if test="${d.lastUpdateUrl}">
            &nbsp;<a href="${d.lastUpdateUrl.startsWith('http') ? d.lastUpdateUrl : 'http://' + d.lastUpdateUrl}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="nextRun">Next Run</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:formatDate date="${d.nextUpdateDate()}" format="${message(code: 'default.date.format.noZ')}"/>
    </dd>
</dl>

