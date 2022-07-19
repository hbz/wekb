<%@ page import="org.gokb.cred.RefdataValue; de.wekb.helper.RDStore;" %>
<g:if test="${d}">

    <semui:tabs>
        <semui:tabsItemWithoutLink tab="currentTipps" counts="${d.getCurrentTippCount()}"
                                   class="${(params.tab == null || params.tab == 'currentTipps') ? 'active' : ''}">
            Current Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="retiredTipps" counts="${d.getRetiredTippCount()}"
                                   class="${params.tab == 'retiredTipps' ? 'active' : ''}">
            Retired Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="expectedTipps" counts="${d.getExpectedTippCount()}"
                                   class="${params.tab == 'expectedTipps' ? 'active' : ''}">
            Expected Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="deletedTipps" counts="${d.getDeletedTippCount()}"
                                   class="${params.tab == 'deletedTipps' ? 'active' : ''}">
            Deleted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="identifiers" counts="${d.ids.size()}">
            Identifiers
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="variantNames" counts="${d.variantNames.size()}">
            Alternate Names
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="ddcs" counts="${d.ddcs.size()}">
            DDCs
        </semui:tabsItemWithoutLink>
    </semui:tabs>


    <semui:tabsItemContent tab="currentTipps" class="${(params.tab == null || params.tab == 'currentTipps') ? 'active' : ''}">

        <div class="content">
            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id]"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="retiredTipps" class="${params.tab == 'retiredTipps' ? 'active' : ''}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_RETIRED.id]"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="expectedTipps" class="${params.tab == 'expectedTipps' ? 'active' : ''}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_EXPECTED.id]"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="deletedTipps" class="${params.tab == 'deletedTipps' ? 'active' : ''}">

        <div class="content">

            <g:link class="display-inline" controller="search" action="inlineSearch"
                    params="[s_controllerName: controllerName, s_actionName: actionName, objectUUID: params.id, max: params.max, offset: params.offset, qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_DELETED.id]"
                    id="">Packages on this Source</g:link>

        </div>

    </semui:tabsItemContent>

    <g:render template="/templates/tabTemplates/variantNamesTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/ddcsTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/identifiersTab" model="${[d: d]}"/>

</g:if>