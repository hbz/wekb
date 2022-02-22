<%@ page import="org.gokb.cred.TitleInstancePackagePlatform" %>
<div class="tab-pane ${activeTab ? 'active' : ''}" id="identifiers" role="tabpanel">
            <g:render template="/apptemplates/secondTemplates/combosByType"
                      model="${[d: d, property: 'ids', fragment: 'identifiers', combo_status: 'Active', onlyUnlink: 'true',
                                cols: [ [expr: 'toComponent.namespace.value', colhead: 'Namespace'], [expr: 'toComponent.value', colhead: 'ID', action: 'link']]]}"/>
            <g:if test="${editable}">
                <g:render template="/apptemplates/secondTemplates/addIdentifier"
                          model="${[d: d, hash: '#identifiers']}"/>
            </g:if>
</div>