<%@ page import="de.wekb.helper.ServerUtils" %>
<!DOCTYPE html>
<html lang="en">
<head>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="description" content="">
    <meta name="author" content="">
    <link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon">

    <asset:javascript src="wekb.js"/>
    <asset:stylesheet src="wekb.css"/>

    <title><g:message code="gokb.appname" default="we:kb"/></title>

</head>

<wekb:serviceInjection/>
<g:set var="currentServer" scope="page" value="${ServerUtils.getCurrentServer()}"/>

<body id="page-body">
<wekb:serverlabel server="${currentServer}"/>

<div class="ui top fixed inverted stackable menu">
    <div class="ui container">
        <div class="ui category search item inverted" style="flex-grow:1;">
            <div class="ui inverted icon input">
                <input class="prompt" type="text" placeholder="Search for Packages, Titles, Providers, Platforms...">
                <i class="search link icon"></i>
            </div>

            <div class="results"></div>
        </div>

        <div class="right menu">
            <g:if test="${grailsApplication.config.gokb.ygorUrl}">
                <div class="item">
                    <a class="ui inverted button" href="${grailsApplication.config.gokb.ygorUrl}"
                       target="_blank">Ygor</a>
                </div>
            </g:if>
            <sec:ifNotLoggedIn>
                <div class="item">
                    <g:link class="ui inverted button" controller="home" action="index"><i
                            class="sign-in icon"></i>Login</g:link>
                </div>
            </sec:ifNotLoggedIn>

            <sec:ifLoggedIn>
                <div class="item">
                    <g:link class="ui inverted button" controller="logout"><i class="sign-out icon"></i>Logout</g:link>
                </div>
            </sec:ifLoggedIn>
        </div>
    </div>
</div>

<div class="ui left fixed vertical inverted menu">
    <g:link controller="public" action="index" class="header item">
        <img alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>
    </g:link>


    <div class="item">
        <div class="header">Search</div>

        <div class="menu">
            <g:link class="item" controller="search" action="index" params="[qbe: 'g:packages']">Packages</g:link>
            <g:link class="item" controller="search" action="index" params="[qbe: 'g:platforms']">Platforms</g:link>
            <g:link class="item" controller="search" action="index" params="[qbe: 'g:orgs']">Providers</g:link>
            <g:link class="item" controller="search" action="index" params="[qbe: 'g:tipps']">Titles</g:link>
        </div>

        <div class="menu">
            <g:link class="item" controller="search" action="index"
                    params="[qbe: 'g:curatoryGroups']">Curatory Groups</g:link>
            <g:link class="item" controller="search" action="index" params="[qbe: 'g:sources']">Sources</g:link>
            <g:link class="item" controller="search" action="index"
                    params="[qbe: 'g:identifiers']">Identifiers</g:link>

        </div>
    </div>
    <sec:ifLoggedIn>
        <div class="item">
            <div class="header">Statistics</div>

            <div class="menu">
                <g:link class="item" controller="home">Statistics</g:link>
            </div>
        </div>
        <sec:ifAnyGranted roles='ROLE_ADMIN,ROLE_EDITOR,ROLE_CONTRIBUTOR'>
            <div class="item">
                <div class="header">Create</div>

                <div class="menu">
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'org.gokb.cred.Package']">Packages</g:link>
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'org.gokb.cred.Platform']">Platforms</g:link>
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'org.gokb.cred.Source']">Sources</g:link>
                    <g:link class="item" controller="create" action="index"
                            params="[tmpl: 'org.gokb.cred.TitleInstancePackagePlatform']">Titles</g:link>
                </div>
            </div>
        </sec:ifAnyGranted>
        <sec:ifAnyGranted roles="ROLE_ADMIN">
            <div class="item">
                <div class="header">Admin</div>

                <div class="menu">
                    <div class="ui dropdown item"><i class="dropdown icon"></i> Admin Views <div class="menu">
                        <g:link class="item" controller="user" action="search">User Management Console</g:link>
                        <g:link class="item" controller="admin" action="jobs">Manage Jobs</g:link>
                        <g:link class="item" controller="admin" action="manageFTControl">Manage FT Control</g:link>
                        <g:link class="item" controller="admin" action="packagesChanges">Packages Changes</g:link>
                        <g:link class="item" controller="admin"
                                action="findPackagesWithTippDuplicates">Packages with Tipp Duplicates</g:link>
                        <g:link class="item" controller="admin"
                                action="tippIdentifiersWithSameNameSpace">Title Identifiers with same Identifier Namespace</g:link>
                    </div>
                    </div>


                    <g:if test="${session.menus?.admin?.search}">
                        <div class="ui dropdown item"><i class="dropdown icon"></i> Admin Search <div class="menu">
                            <g:each in="${session.menus.admin.search.sort { it.text }}" var="item">
                                ${g.link(item.link + item.attr) { " ${item.text}" }}
                            </g:each>
                        </div>
                        </div>
                    </g:if>
                    <g:if test="${session.menus?.admin?.create}">
                        <div class="ui dropdown item"><i class="dropdown icon"></i> Admin Create <div class="menu">
                            <g:each in="${session.menus.admin.create.sort { it.text }}" var="item">
                                ${g.link(item.link + item.attr) { " ${item.text}" }}
                            </g:each>
                        </div>
                        </div>
                    </g:if>

                    <div class="ui dropdown item"><i class="dropdown icon"></i> Admin Jobs <div class="menu">
                        <g:link class="item" controller="admin" action="updateTextIndexes"
                                onclick="return confirm('Are you sure?')">Update Free Text Indexes</g:link>
                        %{--              <g:link class="item" controller="admin" action="resetTextIndexes" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Reset Free Text Indexes</g:link>--}%
                        <g:link class="item" controller="admin" action="recalculateStats"
                                onclick="return confirm('Are you sure?')">Recalculate Statistics</g:link>
                        <g:link class="item" controller="admin" action="cleanup"
                                onclick="return confirm('Are you sure?')">Expunge Deleted Records</g:link>
                        <g:link class="item" controller="admin" action="cleanupPlatforms"
                                onclick="return confirm('Are you sure?')">Deprecate Platforms Without URLs</g:link>
                        <g:link class="item" controller="admin" action="reviewDatesOfTippCoverage"
                                onclick="return confirm('Are you sure?')">Add Reviews for wrong Tipp Coverage Dates</g:link>
                        <g:link class="item" controller="admin" action="ensureUuids"
                                onclick="return confirm('Are you sure?')">Ensure UUIDs</g:link>
                        <g:link class="item" controller="admin" action="addPackageTypes"
                                onclick="return confirm('Are you sure?')">Ensure Package Content Types</g:link>
                        <g:link class="item" controller="admin" action="autoUpdatePackages"
                                onclick="return confirm('Are you sure?')">Auto Update Packages</g:link>
                        <g:link class="item" controller="admin" action="cleanupTippIdentifersWithSameNamespace"
                                onclick="return confirm('Are you sure?')">Cleanup Tipp Identifers with same Namespace</g:link>
                        <g:link class="item" controller="admin" action="setTippsWithoutUrlToDeleted"
                                onclick="return confirm('Are you sure?')">Set Tipps without Url to deleted</g:link>
                    </div>
                    </div>
                </div>
            </div>

            <div class="item">
                <div class="header">Infos</div>

                <div class="menu">
                    <g:link class="item" controller="frontend" action="index">Frontend</g:link>
                    <g:link class="item" controller="home" action="about">Operating environment</g:link>
                </div>
            </div>

        </sec:ifAnyGranted>
    </sec:ifLoggedIn>

</div>

<main class="ui main container ${serverLabel}">
    <br>
    <br>
    <br>
    <g:layoutBody/>

</main>


<g:render template="/layouts/footer_semui"/>

</body>

</html>
