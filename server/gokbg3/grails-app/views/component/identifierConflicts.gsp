<%@ page import="org.gokb.cred.TitleInstancePackagePlatform; org.gokb.cred.Identifier; org.gokb.cred.Platform; org.gokb.cred.Package; org.gokb.cred.Org;" %>

<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="sb-admin" />
<title><g:message code="gokb.appname" default="we:kb"/>: Identifier Review</title>
</head>
<body>
  <h1 class="page-header">Identifier Review</h1>
  <div id="mainarea" class="panel panel-default">
    <div class="panel-body">
      <dl class="dl-horizontal">
        <g:form controller="component" action="identifierConflicts" class="form-horizontal">
          <div class="input-group">
            <dt class="dt-label">Identifier Namespace</dt>
            <dd>
              <gokb:simpleReferenceTypedown class="form-control" name="id" baseClass="org.gokb.cred.IdentifierNamespace" value="${namespace ? 'org.gokb.cred.IdentifierNamespace:' + namespace.id : ''}" filter1="all"/>
            </dd>
            <dt class="dt-label">Conflict type</dt>
            <dd>
              <select class="form-control" id="ctype" name="ctype">
                <option value="st" ${ctype == 'st' ? 'selected' : ''}>Multiple occurrences of one namespace on one title</option>
                <option value="di" ${ctype == 'di' ? 'selected' : ''}>Identifers connected to multiple components</option>
              </select>
            </dd>
            <span class="input-group-btn">
              <button type="submit" class="btn btn-default" >Search</button>
            </span>
          </div>
        </g:form>
      </dl>
    </div>
  </div>
  <g:if test="${namespace}">
    <g:if test="${ctype == 'st'}">
      <h1 class="page-header">Components with multiple Identifiers of namespace <g:link controller="resource" action="show" id="org.gokb.cred.IdentifierNamespace:${namespace.id}">${namespace.value}</g:link> (${titleCount})</h1>
      <div id="mainarea" class="panel panel-default">

        <g:if test="${singleTitles.size() > 0}">
          <table class="table table-striped table-condensed table-bordered">
            <thead style="white-space:nowrap;">
              <tr class="inline-nav">
                <th>Component</th>
                <th>Identifiers</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${singleTitles}" var="st">
                <tr>
                  <td>
                    <g:if test="${st instanceof TitleInstancePackagePlatform}">
                      Title: <g:link controller="resource" action="show" id="${st.uuid}">${st.name}</g:link> (Package: <g:link controller="resource" action="show" id="${st.pkg.uuid}">${st.pkg.name}</g:link>)
                    </g:if>

                    <g:if test="${st instanceof Platform}">
                      Plaftorm: <g:link controller="resource" action="show" id="${st.uuid}">${st.name}</g:link>
                    </g:if>

                    <g:if test="${st instanceof Package}">
                      Package: <g:link controller="resource" action="show" id="${st.uuid}">${st.name}</g:link>
                    </g:if>

                    <g:if test="${st instanceof Org}">
                      Provider <g:link controller="resource" action="show" id="${st.uuid}">${st.name}</g:link>
                    </g:if>
                  </td>
                  <td>
                    <ul>
                    <g:each in="${st.ids.sort{it.namespace.value}}" var="cid">
                      <li><span style="${cid.namespace.value == namespace.value ?'font-weight:bold;':''}">${cid.namespace.value}:${cid.value}</span></li>
                    </g:each>
                    </ul>
                  </td>
                </tr>
              </g:each>
            </tbody>
          </table>
          <div class="pagination" style="text-align:center">
            <g:if test="${titleCount?:0 > 0 }" >
              <g:paginate
                controller="component"
                action="identifierConflicts"
                params="${[ctype: ctype, id: params.id]}"
                next="Next"
                prev="Prev"
                max="${max}"
                total="${titleCount}"
              />
            </g:if>
          </div>
        </g:if>
        <g:else>
          <div style="text-align:center">
            <div class="alert alert-info" style="display:inline-block;font-weight:bolder;margin:10px;">No occurrences found!</div>
          </div>
        </g:else>
      </div>
    </g:if>

    <g:if test="${ctype == 'di'}">
      <h1 class="page-header">Identifiers connected to multiple components for namespace <g:link controller="resource" action="show" id="org.gokb.cred.IdentifierNamespace:${namespace.id}">${namespace.value}</g:link> (${idsCount})</h1>
      <div id="mainarea" class="panel panel-default">

        <g:if test="${dispersedIds.size() > 0}">
          <table class="table table-striped table-condensed table-bordered">
            <thead>
              <tr>
                <th>Identifier</th>
                <th>Identified Components with same Identifier</th>
              </tr>
            </thead>
            <tbody>
              <g:each in="${dispersedIds}" var="did">
                  <tr>
                    <td><g:link controller="resource" action="show" id="${did.class.name}:${did.id}"><span style="white-space:nowrap">${did.value}</span></g:link></td>
                    <td>
                      <g:each in="${Identifier.findAllByValue(did.value)}" var="idc">
                        <div>
                        <g:if test="${idc.tipp}">
                          Title: <g:link controller="resource" action="show" id="${idc.uuid}">${idc.tipp.name}</g:link> (Package: <g:link controller="resource" action="show" id="${idc.tipp.pkg.uuid}">${idc.tipp.pkg.name}</g:link>)
                        </g:if>

                        <g:if test="${idc.platform}">
                          Plaftorm: <g:link controller="resource" action="show" id="${idc.uuid}">${idc.platform.name}</g:link>
                        </g:if>

                        <g:if test="${idc.pkg}">
                          Package: <g:link controller="resource" action="show" id="${idc.uuid}">${idc.pkg.name}</g:link>
                        </g:if>

                        <g:if test="${idc.org}">
                          Provider <g:link controller="resource" action="show" id="${idc.uuid}">${idc.org.name}</g:link>
                        </g:if>
                        </div>

                      </g:each>
                    </td>
                  </tr>
              </g:each>
            </tbody>
          </table>
          <div class="pagination" style="text-align:center">
            <g:if test="${idsCount?:0 > 0 }" >
              <g:paginate
                controller="component"
                action="identifierConflicts"
                params="${[ctype: ctype, id: params.id]}"
                next="Next"
                prev="Prev"
                max="${max}"
                total="${idsCount}"
              />
            </g:if>
          </div>
        </g:if>
        <g:else>
          <div style="text-align:center">
            <div class="alert alert-info" style="display:inline-block;font-weight:bolder;margin:10px;">No occurrences found!</div>
          </div>
        </g:else>
      </div>
    </g:if>
  </g:if>

</body>
</html>
