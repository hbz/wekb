<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable"
       value="${d.isEditable() && ((request.curator != null ? request.curator.size() > 0 ? true : false : true) || (params.curationOverride == 'true' && request.user.isAdmin()))}"/>--}%

<ul id="tabs" class="nav nav-tabs">

    <g:if test="${d.publicationType?.value == 'Serial'}">
         <li role="presentation" class=nav-item">
            <a class="nav-link active" href="#tippcoverage" data-toggle="tab">Coverage</a>
        </li>
    </g:if>
    <li role="presentation" class=nav-item">
        <a class="nav-link ${d.publicationType?.value != 'Serial' ? 'active' : ''}" href="#identifiers" data-toggle="tab">Identifiers <span
            class="badge badge-pill badge-info">${d?.getCombosByPropertyNameAndStatus('ids', 'Active')?.size() ?: '0'}</span>
    </a>
    </li>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#addprops" data-toggle="tab">Additional Properties
            <span class="badge badge-pill badge-info">${d.additionalProperties?.size() ?: '0'}</span>
        </a>
    </li>
    <g:if test="${controllerName != 'public'}">
        <li role="presentation" class=nav-item">
            <a class="nav-link" href="#review" data-toggle="tab">Review Requests
                <span class="badge badge-pill badge-info">${d.reviewRequests?.size() ?: '0'}</span>
            </a>
        </li>
    </g:if>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#subjectArea" data-toggle="tab">Subject Area</a>
    </li>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#series" data-toggle="tab">Series</a>
    </li>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#openAccess" data-toggle="tab">Open Access</a>
    </li>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#prices" data-toggle="tab">Prices
            <span class="badge badge-pill badge-info">${d.prices?.size() ?: '0'}</span>
        </a>
    </li>
    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#ddcs" data-toggle="tab">DDCs
            <span class="badge badge-pill badge-info">${d.ddcs.size()}</span>
        </a>
    </li>

    <li role="presentation" class=nav-item">
        <a class="nav-link" href="#openAccess" data-toggle="tab">Open Access</a>
    </li>
</ul>


<div id="my-tab-content" class="tab-content">

    <g:render template="/tabTemplates/tippTabs" model="${[d: d]}"/>

</div>
<g:render template="componentStatus"
          model="${[d: d]}"/>

