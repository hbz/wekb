<g:if test="${controllerName == 'public'}">
    <ul id="tabs" class="nav nav-tabs" role="tablist">

        <g:if test="${d.publicationType?.value == 'Serial'}">
            <li class="nav-item">
                <a class="nav-link active" href="#tippcoverage" data-toggle="tab" role="tab">Coverage</a>
            </li>
        </g:if>

        <li class="nav-item">
            <a class="nav-link ${d.publicationType?.value != 'Serial' ? 'active' : ''}" href="#identifiers"
               data-toggle="tab" role="tab">Identifiers <span
                    class="badge badge-pill badge-info">${d.ids.size()}</span>
            </a>
        </li>

       %{-- <li class="nav-item">
            <a class="nav-link" href="#addprops" data-toggle="tab" role="tab">Additional Properties
                <span class="badge badge-pill badge-info">${d.additionalProperties.size()}</span>
            </a>
        </li>--}%

        <li class="nav-item">
            <a class="nav-link" href="#subjectArea" data-toggle="tab" role="tab">Subject Area</a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="#ddcs" data-toggle="tab" role="tab">DDCs
                <span class="badge badge-pill badge-info">${d.ddcs.size()}</span>
            </a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="#series" data-toggle="tab" role="tab">Series</a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="#openAccess" data-toggle="tab" role="tab">Open Access</a>
        </li>

        <li class="nav-item">
            <a class="nav-link" href="#prices" data-toggle="tab" role="tab">Prices
                <span class="badge badge-pill badge-info">${d.prices.size()}</span>
            </a>
        </li>

    </ul>
</g:if>
<g:else>
    <ul id="tabs" class="nav nav-tabs">

        <g:if test="${d.publicationType?.value == 'Serial'}">
            <li class="active">
                <a href="#tippcoverage" data-toggle="tab">Coverage</a>
            </li>
        </g:if>
        <li class="${d.publicationType?.value != 'Serial' ? 'active' : ''}"><a href="#identifiers"
                                                                               data-toggle="tab">Identifiers <span
                    class="badge badge-warning">${d.ids.size()}</span>
        </a>
        </li>

       %{-- <li>
            <a href="#addprops" data-toggle="tab">Additional Properties
                <span class="badge badge-warning">${d.additionalProperties.size()}</span>
            </a>
        </li>--}%

        <li>
            <a href="#subjectArea" data-toggle="tab">Subject Area</a>
        </li>

        <li>
            <a href="#ddcs" data-toggle="tab">DDCs
                <span class="badge badge-warning">${d.ddcs.size()}</span>
            </a>
        </li>

        <li>
            <a href="#series" data-toggle="tab">Series</a>
        </li>

        <li>
            <a href="#openAccess" data-toggle="tab">Open Access</a>
        </li>

        <li>
            <a href="#prices" data-toggle="tab">Prices
                <span class="badge badge-warning">${d.prices.size()}</span>
            </a>
        </li>
        <li>
            <a href="#review" data-toggle="tab">Review Requests
                <span class="badge badge-warning">${d.reviewRequests.size()}</span>
            </a>
        </li>
    </ul>
</g:else>

<div id="my-tab-content" class="tab-content">
    <g:render template="/tabTemplates/showCoverages" model="${[d: d]}"/>

    <g:render template="/tabTemplates/showIdentifiers" model="${[d: d, activeTab: d.publicationType?.value != 'Serial']}"/>

   %{-- <div class="tab-pane" id="addprops" role="tabpanel">
        <g:render template="/apptemplates/secondTemplates/addprops"
                  model="${[d: d]}"/>
    </div>--}%

    <g:render template="/tabTemplates/showSubjectArea" model="${[d: d]}"/>

    <g:render template="/tabTemplates/showDDCs" model="${[d: d]}"/>

    <g:render template="/tabTemplates/showSeries" model="${[d: d]}"/>

    <g:render template="/tabTemplates/showOpenAccess" model="${[d: d]}"/>

    <g:render template="/tabTemplates/showPrices" model="${[d: d]}"/>

    <g:if test="${controllerName != 'public'}">
        <div class="tab-pane" id="review" role="tabpanel">
            <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d: d]}"/>
        </div>
    </g:if>
</div>
