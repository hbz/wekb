<%@ page import="org.gokb.cred.TitleInstancePackagePlatform;" %>
<g:set var="curatoryGroups"
       value="${(d instanceof TitleInstancePackagePlatform && d.pkg) ? d.pkg.curatoryGroups : d.curatoryGroups}"/>


<div class="ui right rail">
    <div class="ui segment">
        <h2 class="ui header">Curated By</h2>
        <ul>
            <g:each in="${curatoryGroups}" var="cg">
                <li>${cg.name}</li>
            </g:each>

            <g:if test="${!curatoryGroups}">
                <li>There are currently no linked Curatory Groups</li>
            </g:if>
        </ul>

        <div style="margin-top:10px;">
            <g:link controller="resource" action="show" class="fluid ui button black"
                    id="${d.uuid}"><i class="edit icon"></i> Edit (Login required)</g:link>
        </div>

        <g:render template="/templates/componentStatus" model="${[d: d]}"/>

        <g:if test="${actionName == 'packageContent'}">
            <br>
            &nbsp;
            <br>

            <div style="clear:both;">

                <g:if test="${d.source && (d.source.lastUpdateUrl || d.source.url)}">
                    <g:link controller="public" action="kbart" class="ui button black" id="${params.id}">KBart File</g:link> &nbsp;
                </g:if>
                <g:link controller="public" action="packageTSVExport" class="ui button black" id="${params.id}"><g:message
                        code="gokb.appname" default="we:kb"/> File</g:link>
            </div>
        </g:if>
    </div>
</div>

