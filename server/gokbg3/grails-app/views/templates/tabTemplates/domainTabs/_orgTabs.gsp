<%@ page import="de.wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <semui:tabs>
        <semui:tabsItemWithoutLink tab="identifiers" class="active" counts="${d.ids.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="variantNames" counts="${d.variantNames.size()}">
            Alternate Names
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="packages" counts="${d.providedPackages.size()}">
            Packages
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="titles" counts="${d.getCurrentTippCount()}">
            Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="platforms" counts="${d.getCombosByPropertyNameAndStatus('providedPlatforms', 'Active').size()}">
            Platforms
        </semui:tabsItemWithoutLink>
    </semui:tabs>


    <g:render template="/templates/tabTemplates/identifiersTab" model="${[d: d, activeTab: 'true']}"/>

    <g:render template="/templates/tabTemplates/variantNamesTab" model="${[d: d, showActions: true]}"/>

    <semui:tabsItemContent tab="platforms">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[qbe: 'g:platforms', refOid: d.getLogEntityId(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id']]"
                    id="">Titles published</g:link>
    </semui:tabsItemContent>
    <semui:tabsItemContent tab="titles">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[qbe: 'g:tipps', refOid: d.getLogEntityId(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id']]"
                    id="">Titles published</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[qbe: 'g:packages', refOid: d.getLogEntityId(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id']]"
                    id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>

</g:if>