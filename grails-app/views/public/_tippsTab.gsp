<div class="row">
    <div class="col-sm">
        <h2>Titles (${tippsCount})</h2>
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
<table class="table table-striped wekb-table-responsive-stack">
    <thead>
    <tr>
        <th>#</th>
        <g:sortableColumn property="tipp.name" title="Title"/>
        <th>Identifiers</th>
        <th>Platform</th>
        <g:sortableColumn property="tipp.publicationType" title="Publication Type"/>
        <g:sortableColumn property="tipp.medium" title="Medium"/>
        <th>Coverage</th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${tipps}" var="t" status="i">
        <tr>
            <td>
                ${ (params.offset ? params.offset.toInteger(): 0)  + i + 1 }
            </td>
            <td>
                <g:link controller="public" action="tippContent" id="${t.uuid}">
                    ${t.name}
                </g:link>
            </td>
            <td>
                <ul>
                    <g:each in="${t.ids.sort{it.namespace.value}}" var="id">
                        <li><strong>${id.namespace.value}</strong> : ${id.value}</li>
                    </g:each>
                </ul>
            </td>
            <td>
                <g:link controller="public" action="platformContent"
                        id="${t.hostPlatform?.uuid}">
                    ${t.hostPlatform?.name}
                </g:link>
            </td>
            <td>${t.publicationType?.value}</td>
            <td>${t.medium?.value}</td>
            <td>
                ${t.coverageDepth?.value}<br/>${t.coverageNote}
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<g:if test="${tippsCount ?: 0 > 0}">
    <div class="pagination mb-4 d-flex justify-content-center">
        <g:paginate controller="public" action="packageContent" params="${params+[tab: tab]}" next="&raquo;" prev="&laquo;"
                    max="${max}" total="${tippsCount}"/>
    </div>
</g:if>