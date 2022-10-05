<g:if test="${d}">
    <semui:tabs>

        <g:if test="${d.publicationType?.value == 'Serial'}">
            <semui:tabsItemWithoutLink tab="tippcoverage"
                                       class="active">
                Coverage
            </semui:tabsItemWithoutLink>
        </g:if>
        <semui:tabsItemWithoutLink tab="identifiers" class="${(d.publicationType?.value != 'Serial') ? 'active' : ''}" counts="${d.ids.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="subjectArea">
            Subject Area
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="ddcs" counts="${d.ddcs.size()}">
            DDCs
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="series">
            Series
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="openAccess">
            Open Access
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="prices" counts="${d.prices.size()}">
            Prices
        </semui:tabsItemWithoutLink>

        <g:if test="${d.pkg && d.pkg.source && d.pkg.source.url}">
            <semui:tabsItemWithoutLink tab="autoUpdatePackageInfos" activeTab="${params.activeTab}" counts="${d.autoUpdateTippInfos.size()}">
                Auto Update Infos
            </semui:tabsItemWithoutLink>
        </g:if>
    </semui:tabs>


    <g:if test="${d.publicationType?.value == 'Serial'}">
        <g:render template="/templates/tabTemplates/coverageTab" model="${[d: d]}"/>
    </g:if>

    <g:render template="/templates/tabTemplates/identifiersTab"
              model="${[d: d, activeTab: (d.publicationType?.value != 'Serial')]}"/>

    <g:render template="/templates/tabTemplates/subjectAreaTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/ddcsTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/seriesTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/openAccessTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/pricesTab" model="${[d: d]}"/>

    <g:if test="${d.pkg && d.pkg.source && d.pkg.source.url}">
        <semui:tabsItemContent tab="autoUpdatePackageInfos" activeTab="${params.activeTab}">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:autoUpdateTippInfos', qp_tipp_id: d.id, inline: true, refOid: d.getLogEntityId()]"
                        id="">Auto Update Package Info on this Source</g:link>

            </div>

        </semui:tabsItemContent>
    </g:if>

</g:if>
