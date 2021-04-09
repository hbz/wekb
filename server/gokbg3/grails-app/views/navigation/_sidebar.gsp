<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<%@page expressionCodec="none" %>

<div class="navbar-default sidebar" role="navigation">
  <div class="sidebar-nav navbar-collapse">
    <ul class="nav" id="side-menu">
      <sec:ifLoggedIn>

        <li class="list-group-item">
          <g:form controller="globalSearch" action="index" method="get">
            <label for="global-search" class="sr-only">Global Search</label>
            <div class="input-group custom-search-form">
              <input id="global-search" name="q" type="text" class="form-control" placeholder="Global Search...">
              <span class="input-group-btn">
                <button class="btn btn-default" type="submit">
                  <i class="fa fa-search"></i>
                </button>
              </span>
            </div><!-- /input-group -->
          </g:form>
        </li>

        <li class="${params?.controller == "home" && (params?.action == 'index' || params?.action == 'dashboard') ? 'active' : ''}"><g:link controller="home"><i class="fa fa-dashboard fa-fw"></i> Statistics</g:link></li>
        <li class="${params?.controller == "home" && (params?.action == 'userdash') ? 'active' : ''}"><g:link controller="home" action="userdash"><i class="fa fa-dashboard fa-fw"></i> My Dashboard</g:link></li>
        <li class="${(params?.controller == "search" || params?.controller == "globalSearch")  ? 'active' : ''}"><a href="#"><i class="fa fa-search fa-fw"></i> Search<span class="fa arrow"></span></a>
          <ul class="nav nav-second-level">
            <li class="divider"></li>
            <li><g:link controller="search" action="index" params="[qbe:'g:tipps']" title="Search Titles" ><i class='fa fa-angle-double-right fa-fw'></i> Titles</g:link></li>
            <li><g:link controller="search" action="index" params="[qbe:'g:packages']" title="Search Packages" ><i class='fa fa-angle-double-right fa-fw'></i> Packages</g:link></li>
            <li><g:link controller="search" action="index" params="[qbe:'g:platforms']" title="Search Platforms" ><i class='fa fa-angle-double-right fa-fw'></i> Platforms</g:link></li>

            <li class="divider"></li>
            <li><g:link controller="search" action="index" params="[qbe:'g:curatoryGroups']" title="Search Curatory Groups" ><i class='fa fa-angle-double-right fa-fw'></i> Curatory Groups</g:link></li>
            <li><g:link controller="search" action="index" params="[qbe:'g:orgs']" title="Search Orgs" ><i class='fa fa-angle-double-right fa-fw'></i> Organizations</g:link></li>
            <li><g:link controller="search" action="index" params="[qbe:'g:sources']" title="Search Sources" ><i class='fa fa-angle-double-right fa-fw'></i> Sources</g:link></li>

          </ul> <!-- /.nav-second-level -->
        </li>
        <sec:ifAnyGranted roles='ROLE_ADMIN,ROLE_EDITOR,ROLE_CONTRIBUTOR'>
          <li class="${params?.controller == "create" ? 'active' : ''}"><a href="#"><i class="fa fa-plus fa-fw"></i> Create<span class="fa arrow"></span></a>
            <ul class="nav nav-second-level">

              <li class="divider"></li>
              <li><g:link controller="create" action="index" params="[tmpl:'org.gokb.cred.TitleInstancePackagePlatform']" title="New Titles" ><i class='fa fa-angle-double-right fa-fw'></i> Titles</g:link></li>
              <li><g:link controller="create" action="index" params="[tmpl:'org.gokb.cred.Package']" title="New Packages" ><i class='fa fa-angle-double-right fa-fw'></i> Packages</g:link></li>
              <li><g:link controller="create" action="index" params="[tmpl:'org.gokb.cred.Platform']" title="New Platforms" ><i class='fa fa-angle-double-right fa-fw'></i> Platforms</g:link></li>

              <li class="divider"></li>
              <li><g:link controller="create" action="index" params="[tmpl:'org.gokb.cred.Source']" title="New Sources" ><i class='fa fa-angle-double-right fa-fw'></i> Sources</g:link></li>

            </ul> <!-- /.nav-second-level -->
          </li>
        </sec:ifAnyGranted>

        <li><g:link controller="welcome"><i class="fa fa-tasks fa-fw"></i> To Do<span class="fa arrow"></span></g:link>

          <ul class="nav nav-second-level">
            <li><g:link controller="search" action="index"
                        params="[
                                qbe:'g:reviewRequests',
                                qp_allocatedto:'org.gokb.cred.User:'+ applicationContext.springSecurityService.principal.id,
                                qp_status: ('org.gokb.cred.RefdataValue:'+(RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open').id))
                        ]">
              <i class="fa fa-angle-double-right fa-fw"></i> My ToDos</g:link></li>
            <li><g:link controller="search" action="index"
                        params="${[
                                qbe:'g:reviewRequests',
                                qp_status: ('org.gokb.cred.RefdataValue:'+(RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open').id))
                        ]}"><i class="fa fa-angle-double-right fa-fw"></i>
              Data Review</g:link></li>
            <li><g:link controller="component" action="identifierConflicts"><i class="fa fa-angle-double-right fa-fw"></i>
              Identifier Review</g:link></li>
          </ul>
        </li>


        <g:if test="${session.curatorialGroups && ( session.curatorialGroups.size() > 0 ) }">
          <li><a href="#"><i class="fa fa-search fa-fw"></i> My Groups<span class="fa arrow"></span></a>
            <ul class="nav nav-second-level">
              <g:each in="${session.curatorialGroups}" var="cg">
                <li><g:link controller="group" action="index" id="${cg.id}">${cg.name}</g:link></li>
              </g:each>
            </ul>
          </li>
        </g:if>

        <li class="${params?.controller == "coreference" ? 'active' : ''}"><g:link controller="coreference" action="index"><i class="fa fa-list-alt fa-fw"></i> Coreference</g:link></li>


        <g:if test="${session.menus?.admin?.search}">
          <li><a href="#"><i class="fa fa-search fa-fw"></i> Admin Search<span class="fa arrow"></span></a>
            <ul class="nav nav-second-level">
              <li class="divider"></li>
                <g:each in="${session.menus.admin.search}" var="item">
                  <li class="menu-search-admin">${ g.link(item.link + item.attr) { "<i class='fa fa-angle-double-right fa-fw'></i> ${item.text}" } }</li>
                </g:each>
            </ul> <!-- /.nav-second-level -->
          </li>
        </g:if>

        <g:if test="${session.menus?.admin?.create}">
          <li><a href="#"><i class="fa fa-plus fa-fw"></i> Admin Create<span class="fa arrow"></span></a>
            <ul class="nav nav-second-level">
                <g:each in="${session.menus.admin.create}" var="item">
                  <li class="menu-create-admin">${ g.link(item.link + item.attr) { "<i class='fa fa-angle-double-right fa-fw'></i> ${item.text}" } }</li>
                </g:each>

            </ul> <!-- /.nav-second-level -->
          </li>
        </g:if>

        <sec:ifAnyGranted roles="ROLE_ADMIN">
          <li class="${params?.controller == "admin" ? 'active' : ''}"><a href="#"><i class="fa fa-wrench fa-fw"></i> Admin<span class="fa arrow"></span></a>
            <ul class="nav nav-second-level">
              <li><g:link controller="user" action="search"><i class="fa fa-angle-double-right fa-fw"></i> User Management Console</g:link></li>
              <%-- <li><g:link controller="admin" action="tidyOrgData" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Tidy Orgs Data</g:link></li> --%>
                <li><g:link controller="admin" action="jobs"><i class="fa fa-angle-double-right fa-fw"></i> Manage Jobs</g:link></li>
                <li class="divider">Jobs</li>
                <li><g:link controller="admin" action="reSummariseLicenses" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Regenerate License Summaries</g:link></li>
                <li><g:link controller="admin" action="updateTextIndexes" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Update Free Text Indexes</g:link></li>
                <li><g:link controller="admin" action="resetTextIndexes" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Reset Free Text Indexes</g:link></li>
                <li><g:link controller="admin" action="masterListUpdate" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Force Master List Update</g:link></li>
                <li><g:link controller="admin" action="clearBlockCache" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Clear Block Cache (eg Stats)</g:link></li>
                <li><g:link controller="admin" action="recalculateStats" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Recalculate Statistics</g:link></li>
                <li><g:link controller="admin" action="cleanup" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Expunge Deleted Records</g:link></li>
                <li><g:link controller="admin" action="cleanupPlatforms" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Deprecate Platforms Without URLs</g:link></li>
                <li><g:link controller="admin" action="markInconsistentDates" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Add Reviews for wrong Dates</g:link></li>
                <li><g:link controller="admin" action="cleanupRejected" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Expunge Rejected Records</g:link></li>
                <li><g:link controller="admin" action="cleanupOrphanedTipps" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Expunge Orphaned TIPPs</g:link></li>
                <li><g:link controller="admin" action="cleanupOrphanedIdentifiers" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Expunge Orphaned Identifiers</g:link></li>
                <li><g:link controller="admin" action="rejectWrongTitles" onclick="return confirm('This will set ALL titles without any active TIPPs to status -Deleted-!\\n\\nAre you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Cleanup Titles without TIPPs</g:link></li>
                <li><g:link controller="admin" action="rejectNoIdTitles" onclick="return confirm('This will set ALL titles without any connected Identifiers to editStatus -Rejected-!\\n\\nAre you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Cleanup Titles without IDs</g:link></li>
                <li><g:link controller="admin" action="ensureUuids" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Ensure UUIDs</g:link></li>
                <li><g:link controller="admin" action="ensureTipls" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Ensure TIPLs</g:link></li>
                <li><g:link controller="admin" action="addPackageTypes" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Ensure Package Content Types</g:link></li>
                <li><g:link controller="admin" action="triggerEnrichments" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Trigger enrichments</g:link></li>
                %{--<li><g:link controller="admin" action="logViewer"><i class="fa fa-angle-double-right fa-fw"></i> Log Viewer</g:link></li>--}%
              <%--      <li><g:link controller="admin" action="housekeeping" onclick="return confirm('Are you sure?')"><i class="fa fa-angle-double-right fa-fw"></i> Housekeeping</g:link></li> --%>

              <!--
              <li><g:link controller="api" action="downloadUpdate"><i class="fa fa-angle-double-right fa-fw"></i> Get Refine Extension</g:link></li>
              -->
              <li class="divider"></li>
              <li><a href="https://github.com/openlibraryenvironment/gokb/wiki/Integration-APIs:-Telling-GOKb-about-new-or-corresponding-resources-and-local-identifiers"><i class="fa fa-database fa-fw"></i> Integration API</a></li>
            </ul>
          </li>
          <li class="${params?.controller == "home" && params?.action == 'about' ? 'active' : ''}" ><g:link controller="home" action="about"><i class="fa fa-info fa-fw"></i> Operating environment</g:link></li>
        </sec:ifAnyGranted>

        <g:if test="${ grailsApplication.config.gokb.decisionSupport}">
          <sec:ifAnyGranted roles="ROLE_EDITOR">
            <li><g:link controller="decisionSupport"><i class="fa fa-search fa-fw"></i> Decision Support Dashboard</g:link></li>
          </sec:ifAnyGranted>
        </g:if>

      </sec:ifLoggedIn>
      <sec:ifNotLoggedIn>
        <li class="${params?.controller == "public" && params?.action == 'public' ? 'active' : ''}"><g:link controller="public"><i class="fa fa-home fa-fw"></i> <g:message code="gokb.appname" default="we:kb"/></g:link></li>
        <li class="${params?.controller == "register" ? 'active' : ''}"><g:link controller="register" action="forgotPassword"><i class="fa fa-edit fa-fw"></i> Forgot Password</g:link></li>
        <li class="${params?.controller == "login" ? 'active' : ''}"><g:link controller="login"><i class="fa fa-sign-in fa-fw"></i> Sign in</g:link></li>
      </sec:ifNotLoggedIn>

    </ul>
  </div>
      <!-- /.sidebar-collapse -->
</div>
<!-- /.navbar-static-side -->