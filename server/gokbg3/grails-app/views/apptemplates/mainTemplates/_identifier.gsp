<%@ page import="org.gokb.cred.TitleInstancePackagePlatform; org.gokb.cred.Identifier; org.gokb.cred.Combo; de.wekb.helper.RDStore; org.gokb.cred.Package; org.gokb.cred.Org;" %>
<dl class="dl-horizontal">
    <dt><gokb:annotatedLabel owner="${d}" property="namespace">Identifier Namespace</gokb:annotatedLabel></dt>
    <dd>${d.namespace?.value}</dd>

    <dt><gokb:annotatedLabel owner="${d}" property="value">Identifier</gokb:annotatedLabel></dt>
    <dd>${d.value}</dd>

</dl>

<br>
<br>
<h4>Identified Components with same Identifier:</h4>
            <%
                List tippIDs = []
                List pkgIDs = []
                List orgIDs = []

                Identifier.findAllByValue(d.value).each {
                    if (it.kbcomponent.class.simpleName == TitleInstancePackagePlatform.simpleName) {
                        tippIDs << it.kbcomponent
                    }
                    if (it.kbcomponent.class.simpleName == Package.simpleName) {
                        pkgIDs << it.kbcomponent
                    }
                    if (it.kbcomponent.class.simpleName == Org.simpleName) {
                        orgIDs << it.kbcomponent
                    }
                }
            %>


            <g:if test="${tippIDs}">
            <table class="table table-striped table-bordered">
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Package</th>
                    <g:if test="${editable}">
                        <th>Actions</th>
                    </g:if>
                </tr>
                </thead>
                <tbody>
                <g:each in="${tippIDs.sort { it.name }}" var="component">
                        <tr>
                            <td>
                                <g:if test="${controllerName == 'public'}">
                                    <g:link controller="public" action="tippContent" id="${component.uuid}">
                                        ${component.name}
                                    </g:link>
                                </g:if>
                                <g:else>
                                        <g:link controller="resource" action="show" id="${component.uuid}">
                                            ${component.name}
                                        </g:link>
                                </g:else>
                            </td>
                            <td>
                        <g:if test="${controllerName == 'public'}">
                            <g:link controller="public" action="packageContent" id="${component.pkg.uuid}">
                                ${component.pkg.name}
                            </g:link>
                        </g:if>
                        <g:else>
                                <g:link controller="resource" action="show" id="${component.pkg.uuid}">
                                    ${component.pkg.name}
                                </g:link>
                        </g:else>
                            </td>
                            <g:set var="identifierOfComponent" value="${Identifier.findByValueAndKbcomponent(d.value, component)}"/>
                            <g:if test="${editable && identifierOfComponent}">
                                <td>
                                    <g:link controller='ajaxSupport'
                                            action='delete'
                                            params="${["__context": "${identifierOfComponent.class.name}:${identifierOfComponent.id}", 'fragment': fragment]}"
                                            class="confirm-click btn-delete"
                                            title="Delete this link"
                                            data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                                </td>
                            </g:if>
                        </tr>
                </g:each>
                </tbody>
            </table>
            </g:if>

            <g:if test="${pkgIDs}">
                <table class="table table-striped table-bordered">
                    <thead>
                    <tr>
                        <th>Package</th>
                        <g:if test="${editable}">
                            <th>Actions</th>
                        </g:if>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${pkgIDs.sort { it.name }}" var="component">
                            <tr>
                                <td>
                                    <g:if test="${controllerName == 'public'}">
                                        <g:link controller="public" action="packageContent" id="${component.uuid}">
                                            ${component.name}
                                        </g:link>
                                    </g:if>
                                    <g:else>
                                            <g:link controller="resource" action="show" id="${component.uuid}">
                                                ${component.name}
                                            </g:link>
                                    </g:else>
                                </td>
                                <g:set var="identifierOfComponent" value="${Identifier.findByValueAndKbcomponent(d.value, component)}"/>
                                <g:if test="${editable && identifierOfComponent}">
                                    <td>
                                        <g:link controller='ajaxSupport'
                                                action='delete'
                                                params="${["__context": "${identifierOfComponent.class.name}:${identifierOfComponent.id}", 'fragment': fragment]}"
                                                class="confirm-click btn-delete"
                                                title="Delete this link"
                                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                                    </td>
                                </g:if>
                            </tr>
                    </g:each>
                    </tbody>
                </table>
            </g:if>

            <g:if test="${orgIDs}">
                <table class="table table-striped table-bordered">
                    <thead>
                    <tr>
                        <th>Provider</th>
                        <g:if test="${editable}">
                            <th>Actions</th>
                        </g:if>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${orgIDs.sort { it.name }}" var="component">
                            <tr>
                                <td>
                                    <g:if test="${controllerName == 'public'}">
                                        <g:link controller="public" action="orgContent" id="${component.uuid}">
                                            ${component.name}
                                        </g:link>
                                    </g:if>
                                    <g:else>
                                            <g:link controller="resource" action="show" id="${component.uuid}">
                                                ${component.name}
                                            </g:link>
                                    </g:else>
                                </td>
                                <g:set var="identifierOfComponent" value="${Identifier.findByValueAndKbcomponent(d.value, component)}"/>
                                <g:if test="${editable && identifierOfComponent}">
                                    <td>
                                        <g:link controller='ajaxSupport'
                                                action='delete'
                                                params="${["__context": "${identifierOfComponent.class.name}:${identifierOfComponent.id}", 'fragment': fragment]}"
                                                class="confirm-click btn-delete"
                                                title="Delete this link"
                                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                                    </td>
                                </g:if>
                            </tr>
                    </g:each>
                    </tbody>
                </table>
            </g:if>

