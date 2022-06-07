<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="row">
    <dt>
        Name
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="name"/>
    </dd>
    <dt>
        Status
    </dt>
    <dd>
        <g:if test="${editable}">
            <semui:xEditableRefData owner="${d}" field="status"
                                    config="${RCConstants.KBCOMPONENT_STATUS}"/>
        </g:if>
        <g:else>
            ${d.status}
        </g:else>
    </dd>

    <dt>
        Provider
    </dt>
    <dd>
        <semui:xEditableManyToOne owner="${d}" field="provider"
                                  baseClass="org.gokb.cred.Org">${d.provider?.name}</semui:xEditableManyToOne>
    </dd>
    <dt>
        Primary URL
    </dt>
    <dd>
        <semui:xEditable owner="${d}" field="primaryUrl">${d.primaryUrl}</semui:xEditable>
        <g:if test="${d.primaryUrl}">
            &nbsp; <a aria-label="${d.primaryUrl}"
                      href="${d.primaryUrl.startsWith('http') ? d.primaryUrl : 'http://' + d.primaryUrl}"
                      target="new"><i class="fas fa-external-link-alt"></i></a>
        </g:if>
    </dd>

    <dt>
        Title Namespace
    </dt>
    <dd>
        <semui:xEditableManyToOne owner="${d}" field="titleNamespace" baseClass="org.gokb.cred.IdentifierNamespace"
                                  filter1="TitleInstancePackagePlatform">${(d.titleNamespace?.name) ?: d.titleNamespace?.value}</semui:xEditableManyToOne>
    </dd>

    <dt>IP Auth Supported
    <dd><semui:xEditableRefData owner="${d}" field="ipAuthentication"
                                                        config="${RCConstants.PLATFORM_IP_AUTH}"/></dd>

    <dt>Shibboleth Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="shibbolethAuthentication"
                                                        config="${RCConstants.YN}"/></dd>

    <dt>User/Pass Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="passwordAuthentication"
                                                        config="${RCConstants.YN}"/></dd>

    <dt>Proxy Supported</dt>
    <dd><semui:xEditableRefData owner="${d}" field="proxySupported"
                                                        config="${RCConstants.YN}"/></dd>

</dl>

