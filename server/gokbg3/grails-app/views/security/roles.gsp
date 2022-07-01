<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="public_semui" />
<title><g:message code="gokb.appname" default="we:kb"/></title>
</head>
<body class="">
  <div id="mainarea" class="container well">
    <table class="ui selectable striped sortable celled table">
      <tr>
        <th>Status</th>
        <th>Role</th>
      </tr>
      <g:each in="${ currentRoles }" var="role, status">
        <tr>
          <td class="group-status" >
            <g:if test="${ status }" >
              <i class="group-member fa fa-check-circle text-success" ></i>
              <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                <g:link class="editable open-inline" controller="security" action="updateRole" params="${ ['id' : (d.class.name + ':' + d.id) ,('role' + role.id) : false ]}" title="Remove from role" >
                  <i class="group-member fa fa-minus-circle text-muted" ></i>
                </g:link>
              </sec:ifAnyGranted>
            </g:if>
            <g:else>
              <sec:ifAnyGranted roles="ROLE_SUPERUSER">
                <g:link class="editable open-inline" controller="security" action="updateRole" params="${ ['id' : (d.class.name + ':' + d.id) ,('role' + role.id) : true ]}" title="Add to role" >
                  <i class="group-member fa fa-plus-circle text-muted" ></i>
                </g:link>
              </sec:ifAnyGranted>
              <i class="group-member fa fa-times-circle text-danger" ></i>
            </g:else>
          </td>
          <td>
            ${ message(code:'role.' + role.authority + '', default: role.authority) }
          </td>
        </tr>
      </g:each>
    </table>
  </div>
</body>
</html>
