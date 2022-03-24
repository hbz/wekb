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

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="provider">Provider</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <g:if test="${d.provider}">
            <g:link controller="public" action="orgContent"
                    id="${d.provider.uuid}">${d.provider.name}</g:link>
        </g:if>
    </dd>
    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="primaryURL">Primary URL</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:xEditable  owner="${d}" field="primaryUrl">${d.primaryUrl}</gokb:xEditable>
        <g:if test="${d.primaryUrl}">
                &nbsp; <a aria-label="${d.primaryUrl}" href="${d.primaryUrl.startsWith('http') ? d.primaryUrl : 'http://' + d.primaryUrl}" target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>
    </dd>

    <dt class="col-3 text-right">
        <gokb:annotatedLabel owner="${d}" property="titleNamespace">Title Namespace</gokb:annotatedLabel>
    </dt>
    <dd class="col-9 text-left">
        <gokb:manyToOneReferenceTypedown owner="${d}" field="titleNamespace" baseClass="org.gokb.cred.IdentifierNamespace" filter1="TitleInstancePackagePlatform">${(d.titleNamespace?.name)?:d.titleNamespace?.value}</gokb:manyToOneReferenceTypedown>
    </dd>

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

    <dt class="col-3 text-right"><gokb:annotatedLabel owner="${d}"
                             property="proxySupported">Proxy Supported</gokb:annotatedLabel></dt>
    <dd class="col-9 text-left"><gokb:xEditableRefData owner="${d}" field="proxySupported" config="${RCConstants.YN}"/></dd>


</dl>

