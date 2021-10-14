<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory;" %>
<!DOCTYPE html>
<html>
<head>
    <meta name='layout' content='public'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Package Content</title>
</head>

<body>

<div class="container">
    <div class="row">
        <g:if test="${flash.error}">
            <div class="alert alert-warning" style="font-weight:bold;">
                <p>${flash.error}</p>
            </div>
        </g:if>
        <g:if test="${pkg}">
            <h1>Package: <span style="font-weight:bolder;">${pkgName}</span></h1>
            <br>
            <div class="col-md-9">
                <dl class="row">
                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="provider">Provider</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <g:if test="${pkg.provider}">
                            <g:link controller="public" action="orgContent"
                                    id="${pkg.provider.uuid}">${pkg.provider.name}</g:link>
                        </g:if>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="source">Source</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:manyToOneReferenceTypedown owner="${pkg}" field="source" baseClass="org.gokb.cred.Source" >${pkg.source?.name}</gokb:manyToOneReferenceTypedown>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="nominalPlatform">Nominal Platform</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <g:if test="${pkg.nominalPlatform}">
                            <g:link controller="public" action="platformContent"
                                    id="${pkg.nominalPlatform.uuid}">${pkg.nominalPlatform.name}</g:link>
                        </g:if>
                    </dd>
                    <g:if test="${pkg}">
                        <dt class="col-3 text-right">
                            <gokb:annotatedLabel owner="${pkg}" property="status">Status</gokb:annotatedLabel>
                        </dt>
                        <dd class="col-9 text-left">
                                ${pkg.status?.value ?: 'Not Set'}
                        </dd>
                    </g:if>

                    <dt class="col-3 text-right"> <gokb:annotatedLabel owner="${pkg}" property="lastUpdateComment">Last Update Comment</gokb:annotatedLabel> </dt>
                    <dd class="col-9 text-left"> <gokb:xEditable  owner="${pkg}" field="lastUpdateComment" /> </dd>

                    <dt class="col-3 text-right"> <gokb:annotatedLabel owner="${pkg}" property="description">Description</gokb:annotatedLabel> </dt>
                    <dd class="col-9 text-left"> <gokb:xEditable  owner="${pkg}" field="description" /> </dd>

                    <dt class="col-3 text-right"> <gokb:annotatedLabel owner="${pkg}" property="descriptionURL">URL</gokb:annotatedLabel> </dt>
                    <dd class="col-9 text-left"> <gokb:xEditable  owner="${pkg}" field="descriptionURL" />
                        <g:if test="${pkg.descriptionURL}">
                        &nbsp;<a aria-label="${pkg.descriptionURL}" href="${pkg.descriptionURL}" target="new"><i class="fas fa-external-link-alt"></i></a>
                        </g:if>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="globalNote">Global Range</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditable  owner="${pkg}" field="globalNote" />
                    </dd>


                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="type">Breakable</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditableRefData owner="${pkg}" field="breakable" config="${RCConstants.PACKAGE_BREAKABLE}"/>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="type">Content Type</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditableRefData owner="${pkg}" field="contentType" config="${RCConstants.PACKAGE_CONTENT_TYPE}"/>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="type">File</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditableRefData owner="${pkg}" field="file" config="${RCConstants.PACKAGE_FILE}"/>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="type">Open Access</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditableRefData owner="${pkg}" field="openAccess" config="${RCConstants.PACKAGE_OPEN_ACCESS}"/>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="type">Payment Type</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditableRefData owner="${pkg}" field="paymentType" config="${RCConstants.PACKAGE_PAYMENT_TYPE}"/>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="type">Scope</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <gokb:xEditableRefData owner="${pkg}" field="scope" config="${RCConstants.PACKAGE_SCOPE}"/>
                    </dd>
                    
                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="nationalRanges">National Range</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <g:if test="${pkg.scope?.value == 'National'}">
                            <g:render template="/apptemplates/secondTemplates/nationalRange" model="[d: pkg]"/>
                        </g:if>
                    </dd>

                    <dt class="col-3 text-right">
                        <gokb:annotatedLabel owner="${pkg}" property="regionalRanges">Regional Range</gokb:annotatedLabel>
                    </dt>
                    <dd class="col-9 text-left">
                        <g:if test="${RefdataCategory.lookup(RCConstants.COUNTRY, 'DE') in pkg.nationalRanges && pkg.scope?.value == 'National'}">
                            <g:render template="/apptemplates/secondTemplates/regionalRange" model="[d: pkg]"/>
                        </g:if>
                    </dd>

                </dl>
            </div>
            <g:render template="rightBox" model="${[d: pkg]}"/>
        </g:if>
    </div>
    <g:if test="${pkg}">
        <div id="row">
            <ul id="tabs" class="nav nav-tabs" role="tablist">
                <li class="nav-item">
                    <a class="nav-link ${params.tab == 'currentTipps' ? 'active' : ''}" href="#currentTipps" data-toggle="tab" role="tab">Current Titles
                        <span class="badge badge-pill badge-info">${currentTitleCount}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${params.tab == 'retiredTipps' ? 'active' : ''}" href="#retiredTipps" data-toggle="tab" role="tab">Retired Titles
                        <span class="badge badge-pill badge-info">${retiredTitleCount}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link ${params.tab == 'expectedTipps' ? 'active' : ''}" href="#expectedTipps" data-toggle="tab" role="tab">Expected Titles
                        <span class="badge badge-pill badge-info">${expectedTitleCount}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#identifiers" data-toggle="tab" role="tab">Identifiers
                        <span  class="badge badge-pill badge-info">${pkg?.getCombosByPropertyNameAndStatus('ids', 'Active').size()}</span>
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="#altnames" data-toggle="tab" role="tab">Alternate Names
                        <span class="badge badge-pill badge-info">${pkg.variantNames.size()}</span>
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link" href="#ddcs" data-toggle="tab" role="tab">DDCs
                        <span class="badge badge-pill badge-info">${pkg.ddcs.size()}</span>
                    </a>
                </li>

            </ul>
            <div id="my-tab-content" class="tab-content">
                <div class="tab-pane ${params.tab == 'currentTipps' ? 'active' : ''}" id="currentTipps" role="tabpanel">

                    <g:render template="tippsTab" model="[tippsCount: currentTitleCount, tipps: currentTipps, tab: 'currentTipps']"/>

                </div>

                <div class="tab-pane ${params.tab == 'retiredTipps' ? 'active' : ''}" id="retiredTipps" role="tabpanel">

                    <g:render template="tippsTab" model="[tippsCount: retiredTitleCount, tipps: retiredTipps, tab: 'retiredTipps']"/>

                </div>

                <div class="tab-pane ${params.tab == 'expectedTipps' ? 'active' : ''}" id="expectedTipps" role="tabpanel">

                    <g:render template="tippsTab" model="[tippsCount: expectedTitleCount, tipps: expectedTipps, tab: 'expectedTipps']"/>

                </div>

                <g:render template="/tabTemplates/showVariantnames" model="${[d: pkg]}"/>

                <g:render template="/tabTemplates/showDDCs" model="${[d:pkg]}" />

                <g:render template="/tabTemplates/showIdentifiers" model="${[d:pkg]}" />
            </div>
            <g:render template="componentStatus" model="${[d: pkg]}"/>
        </div>
    </g:if>
    <div class="row justify-content-end">
        <button class="btn btn-default btn-primary mb-5" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
    </div>
</div>
</body>
</html>
