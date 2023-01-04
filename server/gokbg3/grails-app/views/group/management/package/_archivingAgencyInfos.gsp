<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory;" %>
<g:set var="counter" value="${offset}"/>

<g:form method="post" class="ui form" controller="${controllerName}" action="${actionName}"
        params="[activeTab: 'archivingAgencyInfos']">

    <div class="ui segment">
        <h1 class="ui header">Bulk Process</h1>

        <div class="field">
            <label>Archiving Agency</label>
            <semui:simpleReferenceDropdown name="archivingAgency"
                                           baseClass="org.gokb.cred.RefdataValue"
                                           filter1="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
        </div>

        <div class="field">
            <label>Open Access</label>
            <semui:simpleReferenceDropdown name="openAccess"
                                           baseClass="org.gokb.cred.RefdataValue"
                                           filter1="${RCConstants.PAA_OPEN_ACCESS}"/>
        </div>

        <div class="field">
            <label>Post-Cancellation Access (PCA)</label>
            <semui:simpleReferenceDropdown name="postCancellationAccess"
                                           baseClass="org.gokb.cred.RefdataValue"
                                           filter1="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
        </div>

        <button class="ui button black" type="submit" value="changeArchivingAgencies"
                name="processOption">Do bulk process to the selected items</button>

        <br>
        <br>

    </div>

    <div style="overflow-x: auto">
        <table class="ui selectable striped sortable celled table">
            <thead>
            <tr>
                <th rowspan="2"></th>
                <th rowspan="2">#</th>
                <semui:sortableColumn property="name" title="Name" rowspan="2"
                                      params="${params}"/>
                <th colspan="5">
                </th>
                <th rowspan="2"></th>
            </tr>
            <tr>
                <th>#</th>
                <th>Archiving Agency</th>
                <th>Open Access</th>
                <th>Post-Cancellation Access (PCA)</th>
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
                            <semui:xEditable owner="${row_obj}" field="name" required="true"
                                             overwriteEditable="${false}"/>
                        </td>
                        <td colspan="5">
                            <g:if test="${row_obj.paas}">
                                <table class="ui small selectable striped celled table">
                                    <tbody>
                                    <g:each in="${row_obj.paas?.sort { it.archivingAgency?.value }}" var="paa"
                                            status="i">
                                        <tr>
                                            <td>${i + 1}</td>
                                            <td><semui:xEditableRefData owner="${paa}" field="archivingAgency"
                                                                        config="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
                                            <td><semui:xEditableRefData owner="${paa}" field="openAccess"
                                                                        config="${RCConstants.PAA_OPEN_ACCESS}"/>
                                            </td>
                                            <td>
                                                <semui:xEditableRefData owner="${paa}" field="postCancellationAccess"
                                                                        config="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
                                            </td>
                                            <td>
                                                <g:if test="${editable}">
                                                    <g:link controller='ajaxSupport'
                                                            action='delete'
                                                            params="${["__context": "${paa.class.name}:${paa.id}", curationOverride: params.curationOverride]}">Delete</g:link>
                                                </g:if>
                                            </td>
                                        </tr>
                                    </g:each>
                                    </tbody>
                                </table>
                            </g:if>
                        </td>
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