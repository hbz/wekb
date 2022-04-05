<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public"/>
    <title>Search <g:if test="${qbetemplate}">${qbetemplate.title}</g:if></title>
</head>

<body>
<g:set var="showView" value="${null}"/>


<div class="container">
    <g:if test="${qbetemplate}">
        <h1 class="page-header">${qbetemplate.title ?: ''}
            <g:if test="${refObject}">
                <%
                    showView = null
                    if (refObject.niceName != null && refObject.niceName != "") {
                        if (refObject.niceName.contains("TitleInstancePackagePlatform")) {
                            showView = "tippContent"
                        }
                        else if (refObject.niceName.contains("Package")) {
                            showView = "packageContent"
                        }
                        else if (refObject.niceName.contains("Platform")) {
                            showView = "platformContent"
                        }
                        else if (refObject.niceName.contains("Org")) {
                            showView = "orgContent"
                        }

                    }
                %>

            for ${refObject.niceName}: <g:link
                controller="public" action="${showView}" id="${refObject.class.name}:${refObject.id}">${refObject.name}</g:link>
            </g:if>
        </h1>
        <br>
        <br>
    </g:if>
    <g:else>
        <h1 class="page-header">Search</h1>
    </g:else>

    <div class="row">
        <div class="col-sm">
            <div id="mainarea" class="panel panel-default">

                <g:if test="${qbetemplate == null}">

                    <g:if test="${(message != null)}">
                        <p style="text-align: center">

                        <div style="font-size:2.5em;margin-top:100px;">
                            ${message}
                        </div>
                        </p>
                    </g:if>

                </g:if>
                <g:else>
                    <div class="panel-body">
                    <g:if test="${(qbetemplate.message != null)}">
                        <p style="text-align: center">

                        <div class="alert-info">
                            ${qbetemplate.message}
                        </div>
                        </p>
                    </g:if>

                    <g:render template="/public/search/qbeform"
                              model="${[formdefn: qbetemplate.qbeConfig?.qbeForm, 'hide': (hide), cfg: qbetemplate.qbeConfig]}"/>
                    </div>
                    <!-- panel-body -->
                    <g:if test="${recset && !init}">
                        <g:render template="/public/search/qberesult"
                                  model="${[qbeConfig: qbetemplate.qbeConfig, rows: new_recset, offset: offset, jumpToPage: 'jumpToPage', det: det, page: page_current, page_max: page_total, baseClass: qbetemplate.baseclass]}"/>
                    </g:if>
                    <g:elseif test="${!init && !params.inline}">
                        <div class="panel-footer">
                            <g:render template="/public/search/qbeempty"/>
                        </div>
                    </g:elseif>
                    <g:else>
                        <div class='no-results'>
                            <p>No results.</p>
                        </div>
                    </g:else>
                </g:else>

                <g:if test="${!params.inline}">
                    <div class="row justify-content-end">
                        <button class="btn btn-default btn-primary mb-5"
                                onclick="window.history.back()">${message(code: 'default.button.back')}</button>
                    </div>
                </g:if>
            </div>
        </div>
    </div>
</div>

</body>
</html>
