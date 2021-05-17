<%@ page import="de.wekb.helper.RCConstants" %>
%{--
<g:set var="editable" value="${ d.isEditable() && ((d.respondsTo('getCuratoryGroups') ? (request.curator != null && request.curator.size() > 0) : true) || (params.curationOverride == 'true' && request.user.isAdmin())) }" />
--}%
<dl class="row">
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="name"/>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${editable}">
            <gokb:xEditableRefData owner="${d}" field="status"
                                   config="${RCConstants.KBCOMPONENT_STATUS}"/>
        </g:if>
        <g:else>
            ${d.status}
        </g:else>
    </dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="source">Source</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:manyToOneReferenceTypedown owner="${d}" field="source"
                                         baseClass="org.gokb.cred.Source">${d.source?.name}</gokb:manyToOneReferenceTypedown></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}" property="source">Provider</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:manyToOneReferenceTypedown owner="${d}" field="provider"
                                         baseClass="org.gokb.cred.Org">${d.provider?.name}</gokb:manyToOneReferenceTypedown></dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="editStatus">Edit Status</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="editStatus"
                               config="${RCConstants.KBCOMPONENT_EDIT_STATUS}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="primaryURL">Primary URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="primaryUrl">${d.primaryUrl}</gokb:xEditable>
        <g:if test="${d.primaryUrl}">
            <g:if test="${d.primaryUrl.startsWith('http')}">
                &nbsp; <a aria-label="${d.primaryUrl}" href="${d.primaryUrl}" target="new"><i class="fas fa-external-link-alt"></i></a>
            </g:if>
            <g:else>
                &nbsp; <span class="badge badge-warning">!Unknown URL format!</span>
            </g:else>
        </g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="software">Software</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="software"
                               config="${RCConstants.PLATFORM_SOFTWARE}"/>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="service">Service</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditableRefData owner="${d}" field="service"
                               config="${RCConstants.PLATFORM_SERVICE}"/>
    </dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="authentication">Authentication</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="authentication"
                               config="${RCConstants.PLATFORM_AUTH_METHOD}"/></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="ipAuthentication">IP Auth Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="ipAuthentication" config="${RCConstants.PLATFORM_IP_AUTH}"/></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="shibbolethAuthentication">Shibboleth Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="shibbolethAuthentication"
                               config="${RCConstants.YN}"/></dd>

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="passwordAuthentication">User/Pass Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="passwordAuthentication" config="${RCConstants.YN}"/></dd>


</dl>

