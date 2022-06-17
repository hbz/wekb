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
        <semui:tabsItemWithoutLink tab="ddcs">
            DDCs
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="series">
            Series
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="openAccess">
            Open Access
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="prices">
            Prices
        </semui:tabsItemWithoutLink>
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

</g:if>
