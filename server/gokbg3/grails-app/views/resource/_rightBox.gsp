<%@ page import="grails.plugin.springsecurity.SpringSecurityUtils; org.gokb.cred.CuratoryGroup; org.gokb.cred.TitleInstancePackagePlatform;" %>
<g:set var="curatoryGroups"
       value="${(d instanceof TitleInstancePackagePlatform && d.pkg) ? d.pkg.curatoryGroups : (d.hasProperty('curatoryGroups') ? d.curatoryGroups : [])}"/>
<wekb:serviceInjection/>


<div class="ui inverted blue right floated segment">
    <g:if test="${d instanceof org.gokb.cred.KBComponent}">
    <h2 class="ui header">Curated By</h2>

    <div class="ui bulleted list">
        <g:each in="${curatoryGroups}" var="cg">
            <div class="item">${cg.name}
                <g:if test="${params.curationOverride == 'true' && springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")}">
                    <g:link controller="ajaxSupport" action="unlinkManyToMany" class="ui negative button"
                    params="['curationOverride': params.curationOverride, '__property':'curatoryGroups', '__context':d.getClassName() + ':' + d.id, '__itemToRemove' : cg.getClassName() + ':' + cg.id]">Unlink Curatory Group</g:link>
                </g:if>
            </div>
        </g:each>

        <g:if test="${!curatoryGroups}">
            <div class="item">There are currently no linked Curatory Groups</div>
        </g:if>

        <g:if test="${params.curationOverride == 'true' && !(d instanceof CuratoryGroup) && !(d instanceof TitleInstancePackagePlatform) && springSecurityService.isLoggedIn() && SpringSecurityUtils.ifAnyGranted("ROLE_ADMIN")}">
            <div class="ui segment">
                <g:form controller="ajaxSupport" action="addToStdCollection" class="ui form">
                    <input type="hidden" name="__context" value="${d.getClassName()}:${d.id}"/>
                    <input type="hidden" name="__property" value="curatoryGroups"/>
                    <input type="hidden" name="curationOverride" value="${params.curationOverride}"/>

                    <div class="field">
                        <label>Select a Curatory Group to link with this component</label>
                        <semui:simpleReferenceDropdown name="__relatedObject"
                                                       baseClass="org.gokb.cred.CuratoryGroup"
                                                       filter1="Current"/>
                    </div>

                    <button type="submit" class="ui black button">Link</button>
                </g:form>
            </div>

        </g:if>
    </div>

    </g:if>
    <sec:ifNotLoggedIn>
        <div style="margin-top:10px;">
            <g:link controller="resource" action="showLogin" class="ui icon inverted button"
                    id="${d instanceof org.gokb.cred.KBComponent ? d.uuid : d.class.name + ':' + d.id}"><i class="edit icon"></i> Edit (Login required)</g:link>
        </div>
    </sec:ifNotLoggedIn>
    <sec:ifLoggedIn>
        <g:if test="${(d.respondsTo("getCuratoryGroups") || d instanceof org.gokb.cred.KBComponent) && !((request.curator != null ? request.curator.size() > 0 : true))}">
            <div class="ui segment">
                <h4 class="ui header">Info</h4>

                <p>You are not a curator of this component. If you notice any errors, please contact a curator or request a review.</p>
            </div>

            <sec:ifAnyGranted roles="ROLE_ADMIN">
                <div class="ui segment">
                    <h4 class="ui header">Warning</h4>

                    <p>As an admin you can still edit, but please contact a curator before making major changes.</p>

                    <g:if test="${params.curationOverride == 'true'}">
                        <g:link class="ui button green"
                                controller="${params.controller}"
                                action="${params.action}"
                                id="${displayobj.class.name}:${displayobj.id}"
                                params="${(request.param ?: [:])}">
                            Disable admin override
                        </g:link>
                    </g:if>
                    <g:else>
                        <g:link class="ui button red"
                                controller="${params.controller}"
                                action="${params.action}"
                                id="${displayobj.class.name}:${displayobj.id}"
                                params="${(request.param ?: [:]) + ["curationOverride": true]}">
                            Enable admin override
                        </g:link>
                    </g:else>
                </div>
            </sec:ifAnyGranted>
        </g:if>
    </sec:ifLoggedIn>

    <g:render template="/templates/componentStatus" model="${[d: d]}"/>

    <g:if test="${d instanceof org.gokb.cred.Package}">
        <br>
        &nbsp;
        <br>

        <div class="ui buttons">

            <g:if test="${d.source && (d.source.lastUpdateUrl || d.source.url)}">
                <g:link controller="public" action="kbart" class="ui inverted button"
                        id="${params.id}">KBart File</g:link> &nbsp;
                <div class="or"></div>
            </g:if>
            <g:link controller="public" action="packageTSVExport" class="ui inverted button"
                    id="${params.id}"><g:message
                    code="gokb.appname" default="we:kb"/> File</g:link>
        </div>
    </g:if>
</div>


