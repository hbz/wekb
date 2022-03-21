<%@ page import="de.wekb.helper.RDStore; org.gokb.cred.TitleInstancePackagePlatform; org.gokb.cred.Combo;" %>
<div class="tab-pane ${activeTab ? 'active' : ''}" id="identifiers" role="tabpanel">

    <table class="table table-striped table-bordered">
        <thead>
        <tr>
            <th>Identifier Namespace</th>
            <th>Identifier</th>
            <g:if test="${editable}">
                <th>Actions</th>
            </g:if>
        </tr>
        </thead>
        <tbody>
            <g:each in="${d.ids?.sort { it.namespace?.value }}" var="identifier">
                <g:set var="combo" value="${Combo.findByFromComponentAndTypeAndToComponent(d, RDStore.COMBO_TYPE_KB_IDS, identifier)}"/>
                <tr>
                    <td>
                        ${identifier.namespace.value}
                    </td>
                    <td>
                        <g:if test="${controllerName == 'public'}">
                                <g:link controller="public" action="identifierContent" id="${identifier.uuid}">
                                    ${identifier.value}
                                </g:link>
                        </g:if>
                        <g:else>
                            <g:link controller="resource" action="show" id="${identifier.uuid}">
                                ${identifier.value}
                            </g:link>
                        </g:else>
                    </td>
                    <g:if test="${editable && combo}">
                        <td>
                            <g:link
                                    controller='ajaxSupport'
                                    action='deleteCombo'
                                    params="${['id': combo.id, 'fragment': 'identifiers', 'propagate': "true"]}"
                                    class="confirm-click btn-delete"
                                    title="Delete this link"
                                    data-confirm-message="Are you sure you wish to delete this Identifier (${identifier.namespace.value}: ${identifier.value})?">Delete</g:link></td>
                    </g:if>
                </tr>
            </g:each>
        </tbody>
    </table>

            <g:if test="${editable}">
                <g:render template="/apptemplates/secondTemplates/addIdentifier"
                          model="${[d: d, hash: '#identifiers']}"/>
            </g:if>
</div>