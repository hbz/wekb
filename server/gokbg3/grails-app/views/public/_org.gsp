<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%
<dl class="row">
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        ${d.name}
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
            ${d.status?.value ?: 'Not Set'}
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="mission">Mission</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        ${d.mission}
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="homepage">Homepage</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        ${d.homepage}
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="metadataDownloaderURL">Metadata URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="metadataDownloaderURL" />
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="kbartDownloaderURL">KBART URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="kbartDownloaderURL" />
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="roles">Roles</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${d.id != null}">
            <ul>
                <g:each in="${d.roles?.sort{it.getI10n('value')}}" var="t">
                    <li>
                        ${t.value}
                    </li>
                </g:each>
            </ul>
        </g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="contacts">Contacts</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${d.id != null}">

            <ul>
                <g:each in="${d.contacts?.sort { it.content }}" var="contact">
                    <li>
                        ${contact.content}: ${contact.contentType?.getI10n('value')} (Language: ${contact.language?.getI10n('value')})
                    </li>
                </g:each>
            </ul>
        </g:if>
    </dd>

</dl>

