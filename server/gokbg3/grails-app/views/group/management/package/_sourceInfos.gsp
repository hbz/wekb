<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'sources']">

    <g:render template="/group/management/managementForm" model="[batchForm: packageSourceInfosBatchForm]"/>

    <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th></th>
                <th>#</th>
                <semui:sortableColumn property="name" title="Package Name"
                                      params="${params}"/>
                <semui:sortableColumn property="source.name" title="Source Name"
                                      params="${params}"/>
                <semui:sortableColumn property="source.status" title="Source Status"
                                      params="${params}"/>
                <semui:sortableColumn property="source.url" title="URL"
                                      params="${params}"/>
                <semui:sortableColumn property="source.frequency" title="Frequency"
                                      params="${params}"/>
                <semui:sortableColumn property="source.defaultSupplyMethod" title="Default Supply Method"
                                      params="${params}"/>
                <semui:sortableColumn property="source.defaultDataFormat" title="Default Data Format"
                                      params="${params}"/>
                <semui:sortableColumn property="source.automaticUpdates" title="Automatic Updates"
                                      params="${params}"/>
                <th>Title ID Namespace</th>
                <th>Source</th>
                <th></th>
            </tr>
            </thead>
            <tbody>
            <g:each in="${new_recset}" var="r">
                <g:if test="${r != null}">
                    <g:set var="row_obj" value="${r.obj}"/>
                    <tr>
                        <td>
                            <g:if test="${editable}">
                                <g:checkBox id="selectedPackages_${row_obj.uuid}" name="selectedPackages"
                                            value="${row_obj.uuid}"
                                            checked="false"/>
                            </g:if>
                        </td>
                        <td>${++counter}</td>
                        <td>
                            ${row_obj.name}
                        </td>
                        <g:if test="${row_obj.source}">
                            <td>
                                <semui:xEditable owner="${row_obj.source}" field="name" required="true"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.source}" field="status"
                                                        config="${RCConstants.KBCOMPONENT_STATUS}"
                                                        overwriteEditable="false"/>
                            </td>
                            <td>
                                <semui:xEditable owner="${row_obj.source}" field="url" validation="url"
                                                 outGoingLink="true"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.source}" field="frequency"
                                                        config="${RCConstants.SOURCE_FREQUENCY}"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.source}" field="defaultSupplyMethod"
                                                        config="${RCConstants.SOURCE_DATA_SUPPLY_METHOD}"/>
                            </td>
                            <td>
                                <semui:xEditableRefData owner="${row_obj.source}" field="defaultDataFormat"
                                                        config="${RCConstants.SOURCE_DATA_FORMAT}"/>
                            </td>
                            <td>
                                <semui:xEditableBoolean owner="${row_obj.source}" field="automaticUpdates"/>
                            </td>
                            <td>
                                <semui:xEditableManyToOne owner="${row_obj.source}" field="targetNamespace"
                                                          baseClass="org.gokb.cred.IdentifierNamespace"
                                                          filter1="TitleInstancePackagePlatform">${row_obj.source.targetNamespace}</semui:xEditableManyToOne>
                            </td>
                            <td>
                                <g:link class="ui icon button" controller="resource" action="show"
                                        id="${row_obj.source.uuid}">
                                    <i class="edit icon"></i>
                                </g:link>
                            </td>
                        </g:if>
                        <g:else>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        </g:else>
                        <td>
                            <g:link class="ui icon button" controller="resource" action="show" id="${row_obj.uuid}">
                                <i class="edit icon"></i>
                            </g:link>
                        </td>
                    </tr>
                </g:if>
                <g:else>
                    <tr>
                        <td>Error - Row not found</td>
                    </tr>
                </g:else>
            </g:each>
            </tbody>
        </table>
    </div>

</g:form>