<%@ page import="org.gokb.cred.RefdataValue; de.wekb.helper.RDStore;" %>
<g:if test="${d}">

    <semui:tabs>
        <semui:tabsItemWithoutLink tab="currentTipps" counts="${d.getCurrentTippCount()}"
                                   defaultTab="currentTipps" activeTab="${params.activeTab}">
            Current Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="retiredTipps" counts="${d.getRetiredTippCount()}"
                                   activeTab="${params.activeTab}">
            Retired Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="expectedTipps" counts="${d.getExpectedTippCount()}"
                                   activeTab="${params.activeTab}">
            Expected Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="deletedTipps" counts="${d.getDeletedTippCount()}"
                                   activeTab="${params.activeTab}">
            Deleted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="identifiers" activeTab="${params.activeTab}" counts="${d.ids.findAll{it.value != 'Unknown'}.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="variantNames" activeTab="${params.activeTab}" counts="${d.variantNames.size()}">
            Alternate Names
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="ddcs" activeTab="${params.activeTab}" counts="${d.ddcs.size()}">
            DDCs
        </semui:tabsItemWithoutLink>

        <g:if test="${d.source && d.source.url}">
            <semui:tabsItemWithoutLink tab="autoUpdatePackageInfos" activeTab="${params.activeTab}" counts="${d.autoUpdatePackageInfos.size()}">
                Auto Update Infos
            </semui:tabsItemWithoutLink>
        </g:if>
    </semui:tabs>


    <semui:tabsItemContent tab="currentTipps" defaultTab="currentTipps" activeTab="${params.activeTab}">

        <div class="content">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id, activeTab: 'currentTipps']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="retiredTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_RETIRED.id, activeTab: 'retiredTipps']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="expectedTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_EXPECTED.id, activeTab: 'expectedTipps']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="deletedTipps" activeTab="${params.activeTab}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_DELETED.id, activeTab: 'deletedTipps']"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>

    <g:render template="/templates/tabTemplates/variantNamesTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/ddcsTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/identifiersTab" model="${[d: d]}"/>

    <g:if test="${d.source && d.source.url}">
        <semui:tabsItemContent tab="autoUpdatePackageInfos" activeTab="${params.activeTab}">

            <div class="content">

                <g:link class="display-inline" controller="search" action="inlineSearch"
                        params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, sort: params.sort, order: params.order, qbe: 'g:autoUpdatePackageInfos', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], activeTab: 'autoUpdatePackageInfos']"
                        id="">Auto Update Package Info on this Source</g:link>

            </div>

        </semui:tabsItemContent>
    </g:if>

</g:if>