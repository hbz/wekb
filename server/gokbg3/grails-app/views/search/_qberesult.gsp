<%@ page import="grails.converters.JSON" %>

<wekb:serviceInjection/>

<g:set var="counter" value="${offset}"/>
%{--<g:set var="s_action" value="${s_action ?: 'index'}"/>
<g:set var="s_controller" value="${s_controller ?: 'search'}"/>--}%

<g:if test="${request.isAjax()}">

    <div class="ui header">
        <h1>Showing results ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}</h1>
    </div>

    <g:render template="/search/pagination" model="${params}"/>

    <table class="ui selectable striped sortable celled table">
        <thead>
        <tr>
            <th>#</th>
            <g:each in="${qbeConfig.qbeResults}" var="c">
                <g:if test="${!params.hide || !params.hide.contains(c.qpEquiv)}">
                    <g:if test="${c.sort}">
                        <semui:sortableColumn property="${c.sort}" title="${colmsg == colcode ? c.heading : colmsg}"
                                              params="${params}"/>
                    </g:if>
                    <g:else>
                        <th>${colmsg == colcode ? c.heading : colmsg}</th>
                    </g:else>
                </g:if>
            </g:each>
        </tr>
        </thead>
        <tbody>
        <g:each in="${rows}" var="r">
            <g:set var="r" value="${r}"/>
            <tr class="${++counter == det ? 'positive' : ''}">
                <!-- Row ${counter} -->
                <td>${counter}</td>
                <g:each in="${r.cols}" var="c">
                    <td>
                        <g:if test="${c.value instanceof java.util.List}">
                            <div class="ui bulleted list">

                                <g:each in="${c.value}" var="element">
                                    <div class="item">
                                        <g:if test="${c.link}">
                                            <g:link controller="resource"
                                                    action="show"
                                                    id="${element instanceof org.gokb.cred.KBComponent ? element.uuid : element.class.name + ':' + element.id}">
                                                ${element.name}
                                            </g:link>
                                        </g:if><g:else>
                                            ${element.name}
                                        </g:else>
                                    </div>
                                </g:each>
                            </div>
                        </g:if>
                        <g:elseif test="${c.link != null && c.value && c.value != '-Empty-'}">
                            <g:link controller="resource"
                                    action="show"
                                    id="${c.link}">
                                ${c.value}
                            </g:link>
                        </g:elseif>
                        <g:elseif test="${c.outGoingLink != null}">
                            ${c.value}
                            <g:if test="${c.value && c.value != '-Empty-'}">
                                &nbsp;<a aria-label="${c.value}"
                                         href="${c.value.startsWith('http') ? c.value : 'http://' + c.value}"
                                         target="_blank"><i class="share square icon"></i></a>
                            </g:if>
                        </g:elseif>
                        <g:elseif test="${c.value instanceof Boolean}">
                            <g:if test="${c.value}">
                                <i class="check green circle icon"
                                   title="${message(code: 'default.boolean.true')}"></i>
                            </g:if>
                            <g:else>
                                <i class="times red circle icon"
                                   title="${message(code: 'default.boolean.false')}"></i>
                            </g:else>
                        </g:elseif>
                        <g:elseif test="${c.value instanceof java.lang.Integer}">
                            <g:if test="${c.value}">
                                <g:formatNumber number="${c.value}" type="number"/>
                            </g:if>
                            <g:else>
                                0
                            </g:else>
                        </g:elseif>
                        <g:else>
                            ${c.value}
                        </g:else>
                    </td>
                </g:each>
            </tr>
        </g:each>
        </tbody>
    </table>
    <g:render template="/search/pagination" model="${params}"/>
</g:if>
<g:else>

    <div class="ui header">
        <h1>Showing results ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}</h1>
    </div>

    <div class="batch-all-info" style="display:none;"></div>

    <g:render template="/search/pagination" model="${params}"/>
    <g:form controller="workflow" action="action" method="post" params="${params}" class='action-form'>
        <table class="ui selectable striped sortable celled table">
            <thead>
            <sec:ifLoggedIn>
              <tr>
                <th></th>
                <th colspan="${qbeConfig.qbeResults.size() + 1}"></th>
                %{--<!-- see grails-app/assets/javascripts/gokb/action-forms.js for code relating to bulk actions -->
                <g:if test="${!hideActions}">
                  <li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Actions <b class="caret"></b></a>
                    <ul class="dropdown-menu actions"></ul>
                  </li>
                  <li class="divider-vertical"></li>
                </g:if>--}%
              </tr>
            </sec:ifLoggedIn>
            <tr>
                <sec:ifLoggedIn>
                    <th></th>
                </sec:ifLoggedIn>
                <th>#</th>
                <g:each in="${qbeConfig.qbeResults}" var="c">
                    <g:set var="colcode" value="${baseClass + '.' + c.heading}"/>
                    <g:set var="colmsg" value="${message(code: colcode, default: c.heading)}"/>
                    <g:if test="${!params.hide || !params.hide.contains(c.qpEquiv)}">
                        <g:if test="${c.sort}">
                            <semui:sortableColumn property="${c.sort}" title="${colmsg == colcode ? c.heading : colmsg}"
                                                  params="${params}"/>
                        </g:if>
                        <g:else>
                            <th>${colmsg == colcode ? c.heading : colmsg}</th>
                        </g:else>
                    </g:if>
                </g:each>
            %{--  <g:if test="${request.user?.showQuickView?.value=='Yes'}">
                <th></th>
              </g:if>--}%
            </tr>
            </thead>
            <tbody>
            <g:each in="${rows}" var="r">
                <g:if test="${r != null}">
                    <g:set var="row_obj" value="${r.obj}"/>
                    <tr class="${++counter == det ? 'positive' : ''}">
                    <!-- Row ${counter} -->
                        <sec:ifLoggedIn>
                            <td>
                                <g:set var="objEditable" value="${accessService.checkEditableObject(row_obj, params)}"/>
                                <g:if test="${objEditable && row_obj.respondsTo('availableActions')}">
                                    <g:set var="al"
                                           value="${new JSON(row_obj?.userAvailableActions()).toString().encodeAsHTML()}"/>
                                    <input type="checkbox" name="bulk:${r.oid}" data-actns="${al}"
                                           class="obj-action-ck-box"/>
                                </g:if>
                                <g:else>
                                    <input type="checkbox"
                                           title="${!objEditable ? 'Component is read only' : 'No actions available'}"
                                           disabled="disabled" readonly="readonly"/>
                                </g:else>
                            </td>
                        </sec:ifLoggedIn>
                        <td>${counter}</td>
                        <g:each in="${r.cols}" var="c">
                            <td>
                                <g:if test="${c.value instanceof java.util.List}">
                                    <div class="ui bulleted list">

                                        <g:each in="${c.value}" var="element">
                                            <div class="item">
                                                <g:if test="${c.link}">
                                                    <g:link controller="resource"
                                                            action="show"
                                                            id="${element instanceof org.gokb.cred.KBComponent ? element.uuid : element.class.name + ':' + element.id}">
                                                        ${element.name}
                                                    </g:link>
                                                </g:if><g:else>
                                                    ${element.name}
                                                </g:else>
                                            </div>
                                        </g:each>
                                    </div>
                                </g:if>
                                <g:elseif test="${c.link != null && c.value && c.value != '-Empty-'}">
                                    <g:link controller="resource"
                                            action="show"
                                            id="${c.link}">
                                        ${c.value}
                                    </g:link>
                                </g:elseif>
                                <g:elseif test="${c.outGoingLink != null}">
                                    ${c.value}
                                    <g:if test="${c.value && c.value != '-Empty-'}">
                                        &nbsp;<a aria-label="${c.value}"
                                                 href="${c.value.startsWith('http') ? c.value : 'http://' + c.value}"
                                                 target="_blank"><i class="share square icon"></i></a>
                                    </g:if>
                                </g:elseif>
                                <g:elseif test="${c.value instanceof Boolean}">
                                    <g:if test="${c.value}">
                                        <i class="check green circle icon"
                                           title="${message(code: 'default.boolean.true')}"></i>
                                    </g:if>
                                    <g:else>
                                        <i class="times red circle icon"
                                           title="${message(code: 'default.boolean.false')}"></i>
                                    </g:else>
                                </g:elseif>
                                <g:elseif test="${c.value instanceof java.lang.Integer}">
                                    <g:if test="${c.value}">
                                        <g:formatNumber number="${c.value}" type="number"/>
                                    </g:if>
                                    <g:else>
                                        0
                                    </g:else>
                                </g:elseif>
                                <g:else>
                                    ${c.value}
                                </g:else>
                            </td>
                        </g:each>
                    %{--<g:if test="${request.user?.showQuickView?.value=='Yes'}">
                      <td>
                        <g:link class="btn btn-xs btn-default pull-right desktop-only" controller="search"
                          action="componentSearch" params="${params+['det':counter]}"><i class="fa fa-eye" ></i></g:link>
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
    <g:render template="/search/pagination" model="${params}"/>
</g:else>

<br>

