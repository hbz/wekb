<%@ page import="org.gokb.cred.Org" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Packages</title>
</head>

<body>

<wekb:serviceInjection/>

<g:render template="number-chart-hero"/>
<div class="container">
    <h1>Filter</h1>

    <div class="card wekb-filter mb-4">
        <g:form controller="public" class="form"   action="index" method="get" params="${params}">
            <div class="row">
                <div class="col-sm-4">
                    <div class="form-group input-group-md">
                        <label for="q">Search for packages...</label>
                        <input type="text" class="form-control" placeholder="Find package like..." value="${params.q}"
                               name="q">

                    </div>
                    %{--Showing results ${firstrec} to ${lastrec} of ${resultsTotal}--}%
                </div>

                <g:each in="${facets?.sort { it.key }}" var="facet">
                    <div class="col-sm-4">
                        <div class="">
                            <g:if test="${facet.key != 'type'}">
                                <label for="${facet.key}" class=""><g:message code="facet.so.${facet.key}"
                                                                              default="${facet.key}"/></label>
                                <select name="${facet.key}" class="wekb-multiselect" multiple
                                        aria-label="Default select example">
                                    <g:each in="${facet.value?.sort { it.display.toLowerCase() }}" var="v">

                                        <g:if test="${params.list(facet.key).contains('"'+v.term+'"')}">
                                            <option value="${'"'+v.term+'"'}"
                                                    selected="selected">${v.display} (${v.count})</option>
                                        </g:if>
                                        <g:else>
                                            <option value="${'"'+v.term+'"'}">${v.display} (${v.count})</option>
                                        </g:else>
                                    </g:each>
                                </select>
                            </g:if>
                        </div>
                    </div>
                </g:each>
            </div>

            <div class="form-group row justify-content-end">
                <div class="col-4">
                    <div class="float-right">
                        <g:link class="btn btn-dark" controller="public">Reset</g:link>
                        <button class="btn btn-primary " type="submit" value="yes" name="search">Search</button>
                    </div>
                </div>
            </div>
        </g:form>
    </div>
</div>


<div class="container">
    <div class="row">
        <div class="col-sm">
            <h1>Results <span class="label label-default">${resultsTotal}</span></h1>
        </div>
        <div class="col-sm">
            <g:form controller="public" class="form-group row justify-content-end"   action="${actionName}" method="get" params="${params}">
                <label class="col-sm-6 col-form-label text-right" for="newMax">Results on Page</label>
                <div class="col-sm-6">
                    <g:select class="form-control"  name="newMax" from="[10, 25, 50, 100]" value="${params.max}" onChange="this.form.submit()"/>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <table class="table table-striped wekb-table-responsive-stack">
                <thead>
                <tr>
                    <th>#</th>
                    <g:sortableColumn property="sortname" title="Package Name"/>
                    <g:sortableColumn property="cpname" title="Provider"/>
                    <th>Curatory Groups</th>
                    <g:sortableColumn property="contentType" title="Content Type"/>
                    <g:sortableColumn property="titleCount" title="Title Count"/>
                    <g:sortableColumn property="lastUpdatedDisplay" title="Last Updated"/>
                </tr>
                </thead>
                <tbody>
                <g:each in="${hits}" var="hit" status="i">
                    <tr>
                        <td>
                            ${ (params.int('offset') ?: 0)  + i + 1 }
                        </td>
                        <td>
                            <g:link controller="public" action="packageContent"
                                    id="${hit.id}">${hit.getSourceAsMap().name}</g:link>

                        </td>
                        <td>
                            <g:if test="${hit.getSourceAsMap().providerUuid}">
                                <g:link controller="public" action="orgContent"
                                        id="${hit.getSourceAsMap().providerUuid}">${Org.findByUuid(hit.getSourceAsMap().providerUuid).name}</g:link>
                            </g:if>

                        </td>
                        <td>
                            <g:if test="${hit.getSourceAsMap().curatoryGroups?.size() > 0}">
                                <g:each in="${hit.getSourceAsMap().curatoryGroups}" var="cg" status="c">
                                    <g:if test="${c > 0}"><br></g:if>
                                    ${cg.name}
                                    <g:if test="${cg.type}">
                                    (${cg.type})
                                    </g:if>
                                </g:each>
                            </g:if>
                            <g:else>
                                <div>No Curators</div>
                            </g:else>
                        </td>
                        <td>${hit.getSourceAsMap().contentType}</td>
                        <td>${hit.getSourceAsMap().titleCount}</td>
                        <td>
                            <g:if test="${hit.getSourceAsMap().lastUpdatedDisplay}">
                                <g:formatDate format="${message(code: 'default.date.format')}"
                                              date="${dateFormatService.parseDate(hit.getSourceAsMap().lastUpdatedDisplay)}"/>
                            </g:if>
                        </td>
                    </tr>
                </g:each>
                </tbody>
            </table>

        %{--            <div style="font-size:0.8em;">
                        <b>*</b> The editing status of this package is marked as 'In Progress'. The number of titles in this package should therefore not be taken as final.
                    </div>--}%

            <g:if test="${resultsTotal ?: 0 > 0}">
                <div class="pagination mb-4 d-flex justify-content-center">
                    <g:paginate controller="public" action="index" params="${params}" next="&raquo;" prev="&laquo;"
                                max="${max}" total="${resultsTotal}"/>
                </div>
            </g:if>

        </div>
    </div>

</div>

</div> <!-- /.container -->
<g:javascript>
    // When DOM is ready.
    $(document).ready(function () {

        var form_selects = $(".wekb-multiselect");

        form_selects.each(function () {

            var conf = {
                placeholder: "Please select",
                allowClear: true,
                width: '100%',
                minimumInputLength: 0,
                /*                ajax: { // instead of writing the function to execute the request we use Select2's convenient helper
                                    url: gokb.config.lookupURI,
                                    dataType: 'json',
                                    data: function (term, page) {
                                        return {
                                            format:'json',
                                            q: term,
                                            baseClass:$(this).data('domain'),
                                            filter1:$(this).data('filter1'),
                                            addEmpty:'Y'
                                        };
                                    },
                                    results: function (data, page) {
                                        // console.log("resultsFn");
                                        return {results: data.values};
                                    }
                                }*/
            };

            var me = $(this);


            me.select2(conf);
        });

    });
</g:javascript>
</body>
</html>
