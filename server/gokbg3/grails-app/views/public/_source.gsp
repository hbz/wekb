<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="row">
    <dt>
        Source Name
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name"/>
    </dd>

    <dt>
        URL
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="url"/>
        <g:if test="${d.url}">
            &nbsp;<a href="${d.url.startsWith('http') ? d.url : 'http://' + d.url}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>
    </dd>

    <dt>
        Frequency
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="frequency" config="${RCConstants.SOURCE_FREQUENCY}"/>
    </dd>

    <dt>
        Default Supply Method
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="defaultSupplyMethod"
                               config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
    </dd>
    <dt>
        Default Data Format
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="defaultDataFormat"
                               config="${RCConstants.SOURCE_DATA_FORMAT}"/>
    </dd>
    <dt>
        Responsible Party
    </dt>
    <dd>
        <semui:xEditableManyToOne owner="${d}" field="responsibleParty"
                                         baseClass="org.gokb.cred.Org">
            ${d.responsibleParty?.name ?: ''}
        </semui:xEditableManyToOne>
    </dd>
    <dt>
        Automated Updates
    </dt>
    <dd>
        <semui:xEditableBoolean owner="${d}" field="automaticUpdates"/>
    </dd>
    <dt>
        Title ID Namespace
    </dt>
    <dd>
        <semui:xEditableManyToOne owner="${d}" field="targetNamespace"
                                         baseClass="org.gokb.cred.IdentifierNamespace" filter1="TitleInstancePackagePlatform">${d.targetNamespace}</semui:xEditableManyToOne>
    </dd>
    <dt>
        EZB Matching Enabled
    </dt>
    <dd>
        <semui:xEditableBoolean owner="${d}" field="ezbMatch"/>
    </dd>
    <dt>
        ZDB Matching Enabled
    </dt>
    <dd>
        <semui:xEditableBoolean owner="${d}" field="zdbMatch"/>
    </dd>
    <dt>
        Last Run
    </dt>
    <dd>
        <semui:xEditable owner="${d}" type="date" field="lastRun">${d.lastRun}</semui:xEditable>
    </dd>

    <dt>
        Last Update Url
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="lastUpdateUrl" overwriteEditable="${false}"/>
        <g:if test="${d.lastUpdateUrl}">
            &nbsp;<a href="${d.lastUpdateUrl.startsWith('http') ? d.lastUpdateUrl : 'http://' + d.lastUpdateUrl}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>
    </dd>

    <dt>
        Next Run
    </dt>
    <dd>
        ${d.getNextUpdateTimestamp()}
    </dd>
</dl>



