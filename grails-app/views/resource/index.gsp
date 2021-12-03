<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="sb-admin"/>
    <title><g:message code="gokb.appname" default="we:kb"/></title>
  </head>
  <body>
    <g:if test="${params.status == '404'}">
    <h2>
      <g:message code="component.notFound.label" args="[params.id]"/>
    </h2>
    </g:if>
    <g:else>
      <h1 class="page-header"><g:message code="gokb.appname" default="we:kb"/> Resources</h1>

      <li><g:link controller="search" action="index" params="[qbe:'g:tipps']" title="Search Titles" > Titles</g:link></li>
      <li><g:link controller="search" action="index" params="[qbe:'g:packages']" title="Search Packages" > Packages</g:link></li>
      <li><g:link controller="search" action="index" params="[qbe:'g:platforms']" title="Search Platforms" > Platforms</g:link></li>

      <li><g:link controller="search" action="index" params="[qbe:'g:curatoryGroups']" title="Search Curatory Groups" > Curatory Groups</g:link></li>
      <li><g:link controller="search" action="index" params="[qbe:'g:orgs']" title="Search Orgs" > Organizations</g:link></li>
      <li><g:link controller="search" action="index" params="[qbe:'g:sources']" title="Search Sources" > Sources</g:link></li>
    </g:else>
  </body>
</html>
