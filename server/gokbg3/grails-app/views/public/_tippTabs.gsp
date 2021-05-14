<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%

<ul id="tabs" class="nav nav-tabs" role="tablist">

    <g:if test="${d.publicationType?.value == 'Serial'}">
        <li class=nav-item">
            <a class="nav-link active" href="#tippcoverage" data-toggle="tab" role="tab">Coverage</a>
        </li>
    </g:if>

    <li class=nav-item">
        <a class="nav-link ${d.publicationType?.value != 'Serial' ? 'active' : ''}" href="#identifiers"
           data-toggle="tab" role="tab">Identifiers <span
                class="badge badge-pill badge-info">${d?.getCombosByPropertyNameAndStatus('ids', 'Active').size()}</span>
        </a>
    </li>

    <li class=nav-item">
        <a class="nav-link" href="#addprops" data-toggle="tab" role="tab">Additional Properties
            <span class="badge badge-pill badge-info">${d.additionalProperties.size()}</span>
        </a>
    </li>

    <li class=nav-item">
        <a class="nav-link" href="#subjectArea" data-toggle="tab" role="tab">Subject Area</a>
    </li>

    <li class=nav-item">
        <a class="nav-link" href="#ddcs" data-toggle="tab" role="tab">DDCs
            <span class="badge badge-pill badge-info">${d.ddcs.size()}</span>
        </a>
    </li>

    <li class=nav-item">
        <a class="nav-link" href="#series" data-toggle="tab" role="tab">Series</a>
    </li>

    <li class=nav-item">
        <a class="nav-link" href="#openAccess" data-toggle="tab" role="tab">Open Access</a>
    </li>

    <li class=nav-item">
        <a class="nav-link" href="#prices" data-toggle="tab" role="tab">Prices
            <span class="badge badge-pill badge-info">${d.prices.size()}</span>
        </a>
    </li>

    <g:if test="${controllerName != 'public'}">
        <li class=nav-item">
            <a class="nav-link" href="#review" data-toggle="tab" role="tab">Review Requests
                <span class="badge badge-pill badge-info">${d.reviewRequests.size()}</span>
            </a>
        </li>
    </g:if>

</ul>


<div id="my-tab-content" class="tab-content">

    <g:render template="/tabTemplates/tippTabs" model="${[d: d]}"/>

</div>
<g:render template="componentStatus"
          model="${[d: d]}"/>

