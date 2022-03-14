<%@ page import="org.gokb.cred.TitleInstancePackagePlatform; org.gokb.cred.Combo; de.wekb.helper.RDStore; org.gokb.cred.Package; org.gokb.cred.Org;" %>
<dl class="dl-horizontal">
    <dt><gokb:annotatedLabel owner="${d}" property="namespace">Identifier Namespace</gokb:annotatedLabel></dt>
    <dd>${d.namespace?.value}</dd>

    <dt><gokb:annotatedLabel owner="${d}" property="value">Identifier</gokb:annotatedLabel></dt>
    <dd>${d.value}</dd>

</dl>

<br>
<br>
<h4>Identified Components:</h4>
            <%
                List tippIDs = []
                List pkgIDs = []
                List orgIDs = []

                d.identifiedComponents.each {
                    if (it.class.simpleName == TitleInstancePackagePlatform.simpleName) {
                        tippIDs << it
                    }
                    if (it.class.simpleName == Package.simpleName) {
                        pkgIDs << it
                    }
                    if (it.class.simpleName == Org.simpleName) {
                        orgIDs << it
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
                    <g:set var="combo"
                           value="${Combo.findByFromComponentAndTypeAndToComponent(component, RDStore.COMBO_TYPE_KB_IDS, d)}"/>
                    <g:if test="${combo.status == RDStore.COMBO_STATUS_ACTIVE}">
                        <tr>
                            <td>
                                <g:link controller="resource" action="show" id="${component.uuid}">
                                    ${component}
                                </g:link>
                            </td>
                            <td>
                                <g:link controller="resource" action="show" id="${component.pkg.uuid}">
                                    ${component.pkg.name}
                                </g:link>
                            </td>
                            <g:if test="${editable}">
                                <td>
                                    <g:link
                                            controller='ajaxSupport'
                                            action='deleteCombo'
                                            params="${['id': combo.id, 'fragment': fragment, 'propagate': "true"]}"
                                            class="confirm-click btn-delete"
                                            title="Delete this link"
                                            data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                                </td>
                            </g:if>
                        </tr>
                    </g:if>
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
                        <g:set var="combo"
                               value="${Combo.findByFromComponentAndTypeAndToComponent(component, RDStore.COMBO_TYPE_KB_IDS, d)}"/>
                        <g:if test="${combo.status == RDStore.COMBO_STATUS_ACTIVE}">
                            <tr>
                                <td>
                                    <g:link controller="resource" action="show" id="${component.uuid}">
                                        ${component.name}
                                    </g:link>
                                </td>
                                <g:if test="${editable}">
                                    <td>
                                        <g:link
                                                controller='ajaxSupport'
                                                action='deleteCombo'
                                                params="${['id': combo.id, 'fragment': fragment, 'propagate': "true"]}"
                                                class="confirm-click btn-delete"
                                                title="Delete this link"
                                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                                    </td>
                                </g:if>
                            </tr>
                        </g:if>
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
                        <g:set var="combo"
                               value="${Combo.findByFromComponentAndTypeAndToComponent(component, RDStore.COMBO_TYPE_KB_IDS, d)}"/>
                        <g:if test="${combo.status == RDStore.COMBO_STATUS_ACTIVE}">
                            <tr>
                                <td>
                                    <g:link controller="resource" action="show" id="${component.uuid}">
                                        ${component.name}
                                    </g:link>
                                </td>
                                <g:if test="${editable}">
                                    <td>
                                        <g:link
                                                controller='ajaxSupport'
                                                action='deleteCombo'
                                                params="${['id': combo.id, 'fragment': fragment, 'propagate': "true"]}"
                                                class="confirm-click btn-delete"
                                                title="Delete this link"
                                                data-confirm-message="Are you sure you wish to delete this Identifier from the title ${component}?">Delete</g:link>
                                    </td>
                                </g:if>
                            </tr>
                        </g:if>
                    </g:each>
                    </tbody>
                </table>
            </g:if>

