<%@ page import="de.wekb.helper.RDStore; org.gokb.cred.TitleInstancePackagePlatform; org.gokb.cred.Combo;" %>
<semui:tabsItemContent tab="identifiers" class="${activeTab ? 'active' : ''}" counts="${d.ids.size()}">

    <table class="ui selectable striped sortable celled table">
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
                <tr>
                    <td>
                        ${identifier.namespace.value}
                    </td>
                    <td>
                        <g:if test="${controllerName == 'public'}">
                                <g:link controller="public" action="identifierContent" id="${identifier.class.name}:${identifier.id}">
                                    ${identifier.value}
                                </g:link>
                        </g:if>
                        <g:else>
                            <gokb:xEditable owner="${identifier}" field="value" />
                            &nbsp;
                            <g:link controller="resource" action="show" id="${identifier.class.name}:${identifier.id}" title="Jump to resource"><i class="fas fa-eye"></i></g:link>
                        </g:else>
                    </td>
                    <g:if test="${editable}">
                        <td>
                            <g:link controller='ajaxSupport'
                                    action='delete'
                                    params="${["__context": "${identifier.class.name}:${identifier.id}", 'fragment': fragment]}"
                                    class="confirm-click btn-delete"
                                    title="Delete this link"
                                    data-confirm-message="Are you sure you wish to delete this Identifier (${identifier.namespace.value}: ${identifier.value})?">Delete</g:link>
                        </td>
                    </g:if>
                </tr>
            </g:each>
        </tbody>
    </table>

            <g:if test="${editable}">
                <g:render template="/apptemplates/secondTemplates/addIdentifier"
                          model="${[d: d, hash: '#identifiers']}"/>
            </g:if>
</semui:tabsItemContent>