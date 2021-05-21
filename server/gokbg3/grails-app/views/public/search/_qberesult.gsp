<%@ page import="grails.converters.JSON" %>

<g:set var="counter" value="${offset}"/>
<g:set var="s_action" value="${s_action ?: actionName}"/>
<g:set var="s_controller" value="${s_controller ?: controllerName}"/>
<g:set var="showView" value="${null}"/>

<div class="container">
    <div class="row">
        <div class="col-sm">
            <h3>Result ${offset.toInteger() + 1} to ${lasthit.toInteger() as int} of
            ${reccount.toInteger() as int}
            </h3>
        </div>

        %{--<g:if test="${(reccount && max && reccount.toInteger() > max.toInteger())}">
                <div class="col-sm">
                    <g:form controller="public" class="form-group row justify-content-end" action="${actionName}" method="get"
                            params="${params}">
                        <label class="col-sm-6 col-form-label text-right" for="newMax">Results on Page</label>

                        <div class="col-sm-6">
                            <g:select class="form-control" name="newMax" from="[10, 25, 50, 100, 200, 500]"
                                      value="${params.max}" onChange="this.form.submit()"/>
                        </div>
                    </g:form>
                </div>
        </g:if>--}%
    </div>

    <div class="row">
        <div class="col-md-12">
            <g:if test="${request.isAjax()}">

                <g:render template="/public/search/pagination" model="${params}"/>

                <table class="table table-striped wekb-table-responsive-stack">
                    <thead>
                    <tr class="inline-nav">
                        <th>#</th>
                        <g:each in="${qbeConfig.qbeResults}" var="c">
                            <g:if test="${!params.hide || !params.hide.contains(c.qpEquiv)}">
                                <th style="white-space:nowrap;">
                                    <g:if test="${c.sort}">
                                        <g:if test="${params.sort == c.sort && params.order == 'asc'}">
                                            <g:link controller="${s_controller}" action="${s_action}"
                                                    params="${params + ['sort': c.sort, order: 'desc']}">
                                                ${c.heading}
                                                <i class="fas fa-sort-up"></i>
                                            </g:link>
                                        </g:if>
                                        <g:else>
                                            <g:if test="${params.sort == c.sort && params.order == 'desc'}">
                                                <g:link controller="${s_controller}" action="${s_action}"
                                                        params="${params + ['sort': c.sort, order: 'asc']}">
                                                    ${c.heading}
                                                    <i class="fas fa-sort-down"></i>
                                                </g:link>
                                            </g:if>
                                            <g:else>
                                                <g:link controller="${s_controller}" action="${s_action}"
                                                        params="${params + ['sort': c.sort, order: 'desc']}">
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
                        <g:set var="r" value="${r}"/>
                        <tr class="${++counter == det ? 'success' : ''}">
                            <!-- Row ${counter} -->
                            <td>${counter}</td>
                            <g:each in="${r.cols}" var="c">
                                <td>
                                    <%
                                        showView = null
                                        if (c.link != null && c.link != "") {
                                            if (c.link.contains("TitleInstancePackagePlatform")) {
                                                showView = "tippContent"
                                            }
                                            else if (c.link.contains("Package")) {
                                                showView = "packageContent"
                                            }
                                            else if (c.link.contains("Platform")) {
                                                showView = "platformContent"
                                            }
                                            else if (c.link.contains("Org")) {
                                                showView = "orgContent"
                                            }

                                        }
                                    %>
                                    <g:if test="${showView != null}">
                                        <g:link controller="public"
                                                action="${showView}"
                                                id="${c.link}">
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

                <g:render template="/public/search/pagination" model="${params}"/>
            </g:if>
            <g:else>
                <g:render template="/public/search/pagination" model="${params}"/>

                <table class="table table-striped wekb-table-responsive-stack">
                    <thead>
                    <tr>
                        <th>#</th>
                        <g:each in="${qbeConfig.qbeResults}" var="c">
                            <g:set var="colcode" value="${baseClass + '.' + c.heading}"/>
                            <g:set var="colmsg" value="${message(code: colcode, default: c.heading)}"/>
                            <g:if test="${!params.hide || !params.hide.contains(c.qpEquiv)}">
                                <th style="white-space:nowrap"><g:if test="${c.sort}">
                                    <g:if test="${params.sort == c.sort && params.order == 'asc'}">
                                        <g:link controller="${s_controller}" action="${s_action}"
                                                params="${params + ['sort': c.sort, order: 'desc']}">
                                            ${colmsg == colcode ? c.heading : colmsg}
                                            <i class="fas fa-sort-up"></i>
                                        </g:link>
                                    </g:if>
                                    <g:else>
                                        <g:if test="${params.sort == c.sort && params.order == 'desc'}">
                                            <g:link controller="${s_controller}" action="${s_action}"
                                                    params="${params + ['sort': c.sort, order: 'asc']}">
                                                ${colmsg == colcode ? c.heading : colmsg}
                                                <i class="fas fa-sort-down"></i>
                                            </g:link>
                                        </g:if>
                                        <g:else>
                                            <g:link controller="${s_controller}" action="${s_action}"
                                                    params="${params + ['sort': c.sort, order: 'desc']}">
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
                        <g:if test="${request.user?.showQuickView?.value == 'Yes'}">
                            <th></th>
                        </g:if>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${rows}" var="r">
                        <g:if test="${r != null}">
                            <g:set var="row_obj" value="${r.obj}"/>
                            <tr class="${++counter == det ? 'success' : ''}">
                                <!-- Row ${counter} -->
                                <td>${counter}</td>
                                <g:each in="${r.cols}" var="c">
                                    <td style="vertical-align:middle;">
                                        <%
                                            showView = null
                                            if (c.link != null && c.link != "") {
                                                if (c.link.contains("TitleInstancePackagePlatform")) {
                                                    showView = "tippContent"
                                                }
                                                else if (c.link.contains("Package")) {
                                                    showView = "packageContent"
                                                }
                                                else if (c.link.contains("Platform")) {
                                                    showView = "platformContent"
                                                }
                                                else if (c.link.contains("Org")) {
                                                    showView = "orgContent"
                                                }
                                            }
                                        %>
                                        <g:if test="${showView != null}">

                                            <g:link controller="public"
                                                    action="${showView}"
                                                    id="${c.link}">
                                                ${c.value}
                                            </g:link>
                                        </g:if>
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
                                        <g:else>
                                            ${c.value}
                                        </g:else></td>
                                </g:each>
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
                <g:render template="/public/search/pagination" model="${params + [dropup: true]}"/>
            </g:else>
        </div>
    </div>
</div>

