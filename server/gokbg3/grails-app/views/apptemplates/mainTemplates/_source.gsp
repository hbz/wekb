<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
    <dt>
        <gokb:annotatedLabel owner="${d}" property="name">Source Name</gokb:annotatedLabel>
    </dt>
    <dd>
        <gokb:xEditable owner="${d}" field="name"/>
    </dd>


    <g:if test="${d.id != null}">
        <dt>
            <gokb:annotatedLabel owner="${d}" property="url">URL</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditable owner="${d}" field="url"/>
            <g:if test="${d.url}">
                &nbsp;<a href="${d.url}" target="new">Follow Link</a>
            </g:if>
        </dd>

        <dt>
            <gokb:annotatedLabel owner="${d}" property="frequency">Frequency</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditableRefData owner="${d}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}"/>
        </dd>

        <dt>
            <gokb:annotatedLabel owner="${d}" property="defaultSupplyMethod">Default Supply Method</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditableRefData owner="${d}" field="defaultSupplyMethod"
                                   config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="defaultDataFormat">Default Data Format</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditableRefData owner="${d}" field="defaultDataFormat"
                                   config="${RCConstants.SOURCE_DATA_FORMAT}"/>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="responsibleParty">Responsible Party</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:manyToOneReferenceTypedown owner="${d}" field="responsibleParty"
                                             baseClass="org.gokb.cred.Org">
                ${d.responsibleParty?.name ?: ''}
            </gokb:manyToOneReferenceTypedown>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="automaticUpdates">Automated Updates</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditableBoolean owner="${d}" field="automaticUpdates"/>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="targetNamespace">Title ID Namespace</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:manyToOneReferenceTypedown owner="${d}" field="targetNamespace"
                                             baseClass="org.gokb.cred.IdentifierNamespace">${d.targetNamespace}</gokb:manyToOneReferenceTypedown>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="ezbMatch">EZB Matching Enabled</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditableBoolean owner="${d}" field="ezbMatch"/>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="zdbMatch">ZDB Matching Enabled</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditableBoolean owner="${d}" field="zdbMatch"/>
        </dd>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="lastRun">Last Run</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditable owner="${d}" type="date" field="lastRun">${d.lastRun}</gokb:xEditable>
        </dd>

        <dt>
            <gokb:annotatedLabel owner="${d}" property="lastUpdateUrl">Last Update Url</gokb:annotatedLabel>
        </dt>
        <dd>
            <gokb:xEditable owner="${d}" field="lastUpdateUrl" overwriteEditable="${false}"/>
            <g:if test="${d.lastUpdateUrl}">
                &nbsp;<a href="${d.lastUpdateUrl}" target="new">Follow Link</a>
            </g:if>
        </dd>

        <dt>
            <gokb:annotatedLabel owner="${d}" property="nextRun">Next Run</gokb:annotatedLabel>
        </dt>
        <dd>
            <g:formatDate date="${d.nextUpdateDate()}" format="${message(code: 'default.date.format.noZ')}"/>
        </dd>

    </g:if>
</dl>

<g:if test="${d.id}">
    <div id="my-tab-content" class="tab-content">
        <ul id="tabs" class="nav nav-tabs">
            <li class="active"><a href="#packages" data-toggle="tab">Packages</a></li>
            <li><a href="#notes" data-toggle="tab">Notes</a></li>
        </ul>

        <div class="tab-pane active" id="packages">
            <dl>
                <dt>
                    <gokb:annotatedLabel owner="${d}" property="packages">Packages</gokb:annotatedLabel>
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:packages', qp_source_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_source', 'qp_source_id']]"
                            id="">Packages on this Source</g:link>
                </dd>
            </dl>
        </div>



        <div class="tab-pane" id="notes">
            <dl>
                <dt>
                    <gokb:annotatedLabel owner="${d}" property="notes">Notes</gokb:annotatedLabel>
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:notes', qp_ownerClassID: d.id, inline: true, qp_ownerClass: d.getClass().name]"
                            id="">Notes on this Source</g:link>
                </dd>
            </dl>
        </div>
    </div>
</g:if>


<g:render template="/apptemplates/secondTemplates/componentStatus"/>

