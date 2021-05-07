<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='sb-admin'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Packages Changes</title>
</head>

<body>

<wekb:serviceInjection/>


<div class="container">
    <h1>Packages Changes (${packagesCount})</h1>

</div>


<div class="container">
    <div class="row">
        <div class="col-md-12">


            <table class="table table-striped ">
                <thead>
                <tr>
                    <th>#</th>
                    <th>Package Name</th>
                    <th>Provider</th>
                    <th>Curatory Groups</th>
                    <th>Title Count</th>
                    <th>Last Updated</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <g:each in="${packages}" var="pkg" status="i">
                    <tr>
                        <td>
                            ${ (params.int('offset') ?: 0)  + i + 1 }
                        </td>
                        <td>
                            <g:link controller="resource" action="show"
                                    id="${pkg.id}">${pkg.name}</g:link>

                        </td>
                        <td>${pkg.provider?.name}</td>
                        <td>
                            <g:if test="${pkg.curatoryGroups?.size() > 0}">
                                <g:each in="${pkg.curatoryGroups}" var="cg" status="c">
                                    <g:if test="${c > 0}"><br></g:if>
                                    ${cg.name}
                                    <g:if test="${cg.type}">
                                        (${cg.type.value})
                                    </g:if>
                                </g:each>
                            </g:if>
                            <g:else>
                                <div>No Curators</div>
                            </g:else>
                        </td>
                        <td>${pkg.currentTippCount}</td>
                        <td>
                            <g:if test="${pkg.lastUpdated}">
                                <g:formatDate format="${message(code: 'default.date.format')}"
                                              date="${pkg.lastUpdated}"/>
                            </g:if>
                        </td>
                        <td>
                            <a data-toggle="modal" data-cache="false"
                               title="Show History (with Combos)"
                               data-remote='<g:createLink controller="fwk" action="history"
                                                          id="${pkg.class.name}:${pkg.id}"
                                                          params="[withCombos: true]"/>'
                               data-target="#infoModal"><i class="fas fa-history"></i></a>

                            <br>
                            <a data-toggle="modal" data-cache="false"
                               title="Show History"
                               data-remote='<g:createLink controller="fwk" action="history" id="${pkg.class.name}:${pkg.id}"/>'
                               data-target="#infoModal"><i class="far fa-clock"></i></a>

                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

            <g:if test="${packagesCount ?: 0 > 0}">
                <div class="pagination mb-4 d-flex justify-content-center">
                    <g:paginate controller="${controllerName}" action="${actionName}" params="${params}" next="&raquo;" prev="&laquo;"
                                max="${max}" total="${packagesCount}"/>
                </div>
            </g:if>

        </div>
    </div>

</div>

</div> <!-- /.container -->

<div id="infoModal" class="qmodal modal fade modal-wide" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>

                <h3 class="modal-title">Loading Content..</h3>
            </div>

            <div class="modal-body"></div>

            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<asset:script type="text/javascript">

    $(document).on('show.bs.modal','#infoModal', function(){
      $(".modal-content").empty();
      $(".modal-content").append('<div class="modal-loading"><h4>Loading <asset:image src="img/loading.gif"/></h4></div>');
        });

</asset:script>

</body>
</html>
