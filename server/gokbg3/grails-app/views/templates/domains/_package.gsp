<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory;" %>
<dl>
    <dt class="control-label">
        Name
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name" required="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">Provider</dt>
    <dd><semui:xEditableManyToOne owner="${d}" field="provider" baseClass="org.gokb.cred.Org"/></dd>
</dl>

<dl>
    <dt class="control-label">Source</dt>
    <dd><semui:xEditableManyToOne owner="${d}" field="source" baseClass="org.gokb.cred.Source"/></dd>
</dl>

<dl>
    <dt class="control-label">Nominal Platform</dt>
    <dd><semui:xEditableManyToOne owner="${d}" field="nominalPlatform"
                                  baseClass="org.gokb.cred.Platform"/></dd>
</dl>
<dl>
    <dt class="control-label">Status</dt>
    <dd><sec:ifAnyGranted roles="ROLE_SUPERUSER">
        <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/>
    </sec:ifAnyGranted>
    <sec:ifNotGranted roles="ROLE_SUPERUSER">
        ${d.status?.value ?: 'Not Set'}
    </sec:ifNotGranted>
    </dd>
</dl>
<dl>
    <dt class="control-label">Last Update Comment</dt>
    <dd><semui:xEditable owner="${d}" field="lastUpdateComment"/></dd>
</dl>
<dl>
    <dt class="control-label">Description</dt>
    <dd><semui:xEditable owner="${d}" type="textarea" field="description"/></dd>

</dl>
<dl>
    <dt class="control-label">Description URL</dt>
    <dd><semui:xEditable owner="${d}" field="descriptionURL" validation="url" outGoingLink="true"/>
    </dd>
</dl>

<dl>
    <dt class="control-label">
        Global Note
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="globalNote"/>
    </dd>
</dl>

<dl>
    <dt class="control-label">
        Breakable
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="breakable" config="${RCConstants.PACKAGE_BREAKABLE}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Content Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="contentType"
                         config="${RCConstants.PACKAGE_CONTENT_TYPE}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        File
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="file" config="${RCConstants.PACKAGE_FILE}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Open Access
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="openAccess" config="${RCConstants.PACKAGE_OPEN_ACCESS}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Payment Type
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="paymentType"
                         config="${RCConstants.PACKAGE_PAYMENT_TYPE}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Scope
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="scope" config="${RCConstants.PACKAGE_SCOPE}"/>
    </dd>
</dl>


<g:if test="${controllerName != 'create'}">
    <dl>
        <dt class="control-label">
            National Range
        </dt>
        <dd>
            <g:if test="${d.scope?.value == 'National'}">
                <g:render template="/templates/nationalRange"/>
            </g:if>
        </dd>
    </dl>
    <dl>
        <dt class="control-label">
            Regional Range
        </dt>
        <dd>
            <g:if test="${RefdataCategory.lookup(RCConstants.COUNTRY, 'DE') in d.nationalRanges && d.scope?.value == 'National'}">
                <g:render template="/templates/regionalRange"/>
            </g:if>
        </dd>
    </dl>
    <dl>
        <dt class="control-label">
            Archiving Agency
        </dt>
        <dd>
            <table class="ui small selectable striped celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Archiving Agency</th>
                    <th>Open Access</th>
                    <th>Post-Cancellation Access (PCA)</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${d.paas?.sort { it.archivingAgency?.value }}" var="paa" status="i">
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

            <g:if test="${editable}">
                <a class="ui right floated black button" href="#"
                   onclick="$('#paaModal').modal('show');">Add Archiving Agency</a>

                <br>
                <br>
            </g:if>
        </dd>
    </dl>
</g:if>


<g:if test="${editable}">
    <semui:modal id="paaModal" title="Add Archiving Agency">

        <g:form controller="ajaxSupport" action="addToCollection" class="ui form">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
            <input type="hidden" name="__newObjectClass" value="wekb.PackageArchivingAgency"/>
            <input type="hidden" name="__recip" value="pkg"/>
            <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>
            <div class="field">
                              <label>Archiving Agency</label>
                <semui:simpleReferenceDropdown  name="archivingAgency"
                                              baseClass="org.gokb.cred.RefdataValue"
                                              filter1="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
            </div>
            <div class="field">
                              <label>Open Access</label>
                <semui:simpleReferenceDropdown  name="openAccess"
                                              baseClass="org.gokb.cred.RefdataValue"
                                              filter1="${RCConstants.PAA_OPEN_ACCESS}"/>
            </div>

            <div class="field">
                              <label>Post-Cancellation Access (PCA)</label>
                <semui:simpleReferenceDropdown  name="postCancellationAccess"
                                              baseClass="org.gokb.cred.RefdataValue"
                                              filter1="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
            </div>
        </g:form>
    </semui:modal>
</g:if>

