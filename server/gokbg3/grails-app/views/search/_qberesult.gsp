<%@ page import="grails.converters.JSON"%>

<wekb:serviceInjection/>

<g:set var="counter" value="${offset}" />
<g:set var="s_action" value="${s_action?:'index'}"/>
<g:set var="s_controller" value="${s_controller?:'search'}"/>

<g:if test="${ request.isAjax() }">

  <g:render template="/search/pagination" model="${params}" />

  <table class="table table-striped table-condensed table-bordered">
    <thead>
      <tr class="inline-nav">
        <th>#</th>
        <g:each in="${qbeConfig.qbeResults}" var="c">
          <g:if test="${!params.hide || !params.hide.contains(c.qpEquiv)}">
            <th style="white-space:nowrap;">
              <g:if test="${c.sort}">
                <g:if test="${params.sort==c.sort && params.order=='asc'}">
                  <g:link controller="${s_controller}" action="${s_action}" params="${params+['sort':c.sort,order:'desc']}">
                    ${c.heading}
                    <i class="fas fa-sort-up"></i>
                  </g:link>
                </g:if>
                <g:else>
                  <g:if test="${params.sort==c.sort && params.order=='desc'}">
                    <g:link controller="${s_controller}" action="${s_action}" params="${params+['sort':c.sort,order:'asc']}">
                      ${c.heading}
                      <i class="fas fa-sort-down"></i>
                    </g:link>
                  </g:if>
                  <g:else>
                    <g:link controller="${s_controller}" action="${s_action}" params="${params+['sort':c.sort,order:'desc']}">
                      ${c.heading}
                      <i class="fas fa-sort"></i>
                    </g:link>
                  </g:else>
                </g:else>
              </g:if>
              <g:else>
                ${c.heading}
              </g:else>
            </th>
          </g:if>
        </g:each>
      </tr>
    </thead>
    <tbody>
      <g:each in="${rows}" var="r">
        <g:set var="r" value="${r}" />
        <tr class="${++counter==det ? 'success':''}">
          <!-- Row ${counter} -->
          <td>${counter}</td>
          <g:each in="${r.cols}" var="c">
            <td>
              <g:if test="${c.link != null }">
                <g:link controller="resource"
                    action="show"
                    id="${c.link}"
                    params="${c.link_params!=null?groovy.util.Eval.x(pageScope,c.link_params):[]}">
                    ${c.value}
                </g:link>
              </g:if>
              <g:else>
                ${c.value}
              </g:else>
            </td>
          </g:each>
        </tr>
      </g:each>
    </tbody>
  </table>
</g:if>
<g:else>
  <div class="batch-all-info" style="display:none;"></div>

  <g:render template="/search/pagination" model="${params}" />
  <g:form controller="workflow" action="action" method="post" params="${params}" class='action-form' >
    <table class="table table-striped table-condensed table-bordered">
      <thead>
        <tr>
          <th></th>
          <th>#</th>
          <g:each in="${qbeConfig.qbeResults}" var="c">
            <g:set var="colcode" value="${baseClass + '.' + c.heading}" />
            <g:set var="colmsg" value="${message(code: colcode, default:c.heading)}" />
            <g:if test="${!params.hide || !params.hide.contains(c.qpEquiv)}">
              <th style="white-space:nowrap"><g:if test="${c.sort}">
                  <g:if test="${params.sort==c.sort && params.order=='asc'}">
                    <g:link controller="${s_controller}" action="${s_action}" params="${params+['sort':c.sort,order:'desc']}">
                      ${colmsg == colcode ? c.heading : colmsg}
                      <i class="fas fa-sort-up"></i>
                    </g:link>
                  </g:if>
                  <g:else>
                    <g:if test="${params.sort==c.sort && params.order=='desc'}">
                      <g:link controller="${s_controller}" action="${s_action}" params="${params+['sort':c.sort,order:'asc']}">
                        ${colmsg == colcode ? c.heading : colmsg}
                        <i class="fas fa-sort-down"></i>
                      </g:link>
                    </g:if>
                    <g:else>
                      <g:link controller="${s_controller}" action="${s_action}" params="${params+['sort':c.sort,order:'desc']}">
                        ${colmsg == colcode ? c.heading : colmsg}
                        <i class="fas fa-sort"></i>
                      </g:link>
                    </g:else>
                  </g:else>
                </g:if> <g:else>
                  ${colmsg == colcode ? c.heading : colmsg}
                </g:else></th>
            </g:if>
          </g:each>
        %{--  <g:if test="${request.user?.showQuickView?.value=='Yes'}">
            <th></th>
          </g:if>--}%
        </tr>
      </thead>
      <tbody>
        <g:each in="${rows}" var="r">
          <g:if test="${r != null }">
            <g:set var="row_obj" value="${r.obj}" />
            <tr class="${++counter==det ? 'success':''}">
              <!-- Row ${counter} -->
              <td style="vertical-align:middle;">
                <g:set var="objEditable" value="${accessService.checkEditableObject(row_obj, params)}"/>
                <g:if test="${objEditable && row_obj.respondsTo('availableActions')}">
                  <g:set var="al" value="${new JSON(row_obj?.userAvailableActions()).toString().encodeAsHTML()}"/>
                  <input type="checkbox" name="bulk:${r.oid}" data-actns="${al}" class="obj-action-ck-box"/>
                </g:if>
                <g:else>
                  <input type="checkbox"
                         title="${!objEditable ? 'Component is read only' : 'No actions available'}"
                         disabled="disabled" readonly="readonly"/>
                </g:else>
              </td>
              <td>${counter}</td>
              <g:each in="${r.cols}" var="c">
                <td style="vertical-align:middle;">
                  <g:if test="${c.link != null}">
                    <g:link controller="resource"
                            action="show"
                            id="${c.link}">
                      ${c.value}
                    </g:link>
                  </g:if>
                  <g:elseif test="${c.outGoingLink != null}">
                    ${c.value}
                    <g:if test="${c.value && c.value != '-Empty-'}">
                      &nbsp;<a aria-label="${c.value}" href="${c.value.startsWith('http') ? c.value : 'http://' + c.value}" target="new"><i class="fas fa-external-link-alt"></i></a>
                    </g:if>
                  </g:elseif>
                  <g:elseif test="${c.value instanceof Boolean}">
                    <g:if test="${c.value}">
                      <i class="fa fa-check-circle text-success fa-lg"
                         title="${message(code: 'default.boolean.true')}"></i>
                    </g:if>
                    <g:else>
                      <i class="fa fa-times-circle text-danger fa-lg"
                         title="${message(code: 'default.boolean.false')}"></i>
                    </g:else>
                  </g:elseif>
                  <g:elseif test="${c.value instanceof java.lang.Integer}">
                    <g:if test="${c.value}">
                      <g:formatNumber number="${c.value}" type="number"/>
                    </g:if>
                    <g:else>
                      <i class="fa fa-times-circle text-danger fa-lg"
                         title="${message(code: 'default.boolean.false')}"></i>
                    </g:else>
                  </g:elseif>
                  <g:else>
                    ${c.value}
                  </g:else></td>
              </g:each>
              %{--<g:if test="${request.user?.showQuickView?.value=='Yes'}">
                <td>
                  <g:link class="btn btn-xs btn-default pull-right desktop-only" controller="search"
                    action="index" params="${params+['det':counter]}"><i class="fa fa-eye" ></i></g:link>
                </td>
              </g:if>--}%
            </tr>
          </g:if>
          <g:else>
            <tr>
              <td>Error - Row not found</td>
            </tr>
          </g:else>
        </g:each>
      </tbody>
    </table>
    </g:form>
  <g:render template="/search/pagination" model="${params + [dropup : true]}" />
</g:else>

<script language="JavaScript">
function jumpToPage() {
  alert("jump to page");
}
</script>
