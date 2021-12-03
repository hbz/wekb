<%@page import="org.gokb.cred.TitleInstancePackagePlatform;"%>
<g:set var="curatoryGroups" value="${d instanceof TitleInstancePackagePlatform ? d.pkg.curatoryGroups : d.curatoryGroups }" />

<div class="col-md-3">
    <div class="card p-4">
        <h2>Curated By</h2>
        <ul>
            <g:each in="${curatoryGroups}" var="cg">
                <li>${cg.name}</li>
            </g:each>

            <g:if test="${!curatoryGroups}">
                <li>There are currently no linked Curatory Groups</li>
            </g:if>
        </ul>

        <div style="margin-top:10px;">
            <g:link controller="resource" action="show"
                    id="${d.uuid}">Switch to editing view (Login required)</g:link>
        </div>
        <g:if test="${actionName == 'packageContent'}">
            <br>
            &nbsp;
            <br>

            <div style="clear:both;">

                <g:if test="${d.source && (d.source.lastUpdateUrl || d.source.url)}">
                    <g:link controller="public" action="kbart" id="${params.id}">KBart File</g:link> &nbsp;
                </g:if>
                <g:link controller="public" action="packageTSVExport" id="${params.id}"><g:message
                        code="gokb.appname" default="we:kb"/> File</g:link>
            </div>
        </g:if>
    </div>
</div>