<%@ page import="de.wekb.helper.RCConstants" %>
<dl>
    <dt class="control-label">
        Name
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name" required="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Status
    </dt>
    <dd>
        <sec:ifAnyGranted roles="ROLE_SUPERUSER">
            <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/>
        </sec:ifAnyGranted>
        <sec:ifNotGranted roles="ROLE_SUPERUSER">
            ${d.status?.value ?: 'Not Set'}
        </sec:ifNotGranted>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Mission
    </dt>
    <dd>
        <semui:xEditableRefData owner="${d}" field="mission" config="${RCConstants.ORG_MISSION}"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Homepage
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="homepage" outGoingLink="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Metadata Downloader URL
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="metadataDownloaderURL" validation="url" outGoingLink="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        KBART Downloader URL
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="kbartDownloaderURL" validation="url" outGoingLink="true"/>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Roles
    </dt>
    <dd>
        <div class="ui bulleted list">
            <g:each in="${d.roles?.sort { it.getI10n('value') }}" var="t">
                <div class="item">
                    ${t.value}
                    <g:if test="${editable}">
                        <g:link controller='ajaxSupport'
                                action='unlinkManyToMany'
                                params="${["__context": "${d.class.name}:${d.id}", "__property": "roles", "__itemToRemove": "${t.getClassName()}:${t.id}"]}">Delete</g:link>
                    </g:if>
                </div>
            </g:each>
        </div>

        <g:if test="${editable}">
            <a class="ui right floated black button" href="#" onclick="$('#rolesModal').modal('show');">Add Role</a>

            <br>
            <br>
        </g:if>
    </dd>
</dl>
<dl>
    <dt class="control-label">
        Contacts
    </dt>
    <dd>
        <g:if test="${d.id != null}">

            <table class="ui small selectable striped celled table">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Value</th>
                    <th>Content Type</th>
                    <th>Contact Type</th>
                    <th>Language</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${d.contacts?.sort { it.content }}" var="contact" status="i">
                    <tr>
                        <td>${i + 1}</td>
                        <td><semui:xEditable owner="${contact}" field="content"/></td>
                        <td><semui:xEditableRefData owner="${contact}" field="contentType"
                                                    config="${RCConstants.CONTACT_CONTENT_TYPE}"/>
                        <td><semui:xEditableRefData owner="${contact}" field="type"
                                                    config="${RCConstants.CONTACT_TYPE}"/>
                        </td>
                        <td>
                            <semui:xEditableRefData owner="${contact}" field="language"
                                                    config="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
                        </td>
                        <td>
                            <g:if test="${editable}">
                                <g:link controller='ajaxSupport'
                                        action='delete'
                                        params="${["__context": "${contact.class.name}:${contact.id}", curationOverride: params.curationOverride]}">Delete</g:link>
                            </g:if>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <g:if test="${editable}">
                <a class="ui right floated black button" href="#"
                   onclick="$('#contactModal').modal('show');">Add Contact</a>

                <br>
                <br>
            </g:if>
        </g:if>
    </dd>
</dl>

<g:if test="${editable}">
    <semui:modal id="contactModal" title="Add Contact">

        <g:form controller="ajaxSupport" action="addToCollection" class="ui form">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
            <input type="hidden" name="__newObjectClass" value="wekb.Contact"/>
            <input type="hidden" name="__recip" value="org"/>
            <input type="hidden" name="fragment" value="contact"/>
            <div class="field">
                    <label>Value</label>

                <input type="text" name="content"/>
            </div>
            <div class="field">
                    <label>Language</label>
                <semui:simpleReferenceDropdown name="language"
                                               baseClass="org.gokb.cred.RefdataValue"
                                               filter1="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
            </div>
            <div class="field">
                    <label>Content Type</label>
                <semui:simpleReferenceDropdown name="contentType"
                                               baseClass="org.gokb.cred.RefdataValue"
                                               filter1="${RCConstants.CONTACT_CONTENT_TYPE}"/>
            </div>

            <div class="field">
                    <label>Contact Type</label>
                <semui:simpleReferenceDropdown name="type"
                                               baseClass="org.gokb.cred.RefdataValue"
                                               filter1="${RCConstants.CONTACT_TYPE}"/>
            </div>
        </g:form>
    </semui:modal>

    <semui:modal id="rolesModal" title="Add Role">

        <g:form controller="ajaxSupport" action="addToStdCollection" class="ui form">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
            <input type="hidden" name="__property" value="roles"/>
            <div class="field">
                              <label>Role:</label> <semui:simpleReferenceDropdown name="__relatedObject"
                                                 baseClass="org.gokb.cred.RefdataValue"
                                                 filter1="${RCConstants.ORG_ROLE}"/>
            </div>
        </g:form>
    </semui:modal>
</g:if>

