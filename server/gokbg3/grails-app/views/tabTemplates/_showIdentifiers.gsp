<%@ page import="org.gokb.cred.TitleInstancePackagePlatform" %>
<div class="tab-pane ${(d instanceof TitleInstancePackagePlatform && d.publicationType?.value != 'Serial') ? 'active' : ''}" id="identifiers" role="tabpanel">
    <dl>
        <dt>
            <gokb:annotatedLabel owner="${d}" property="ids">Identifiers</gokb:annotatedLabel>
        </dt>
        <dd>
            <g:render template="/apptemplates/secondTemplates/combosByType"
                      model="${[d: d, property: 'ids', fragment: 'identifiers', combo_status: 'Active', onlyUnlink: 'true',
                                cols: [ [expr: 'toComponent.namespace.value', colhead: 'Namespace'], [expr: 'toComponent.value', colhead: 'ID', action: 'link']]]}"/>
            <g:if test="${editable}">
                <g:render template="/apptemplates/secondTemplates/addIdentifier"
                          model="${[d: d, hash: '#identifiers']}"/>
            </g:if>
        </dd>
    </dl>
</div>