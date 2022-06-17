<%@ page import="org.gokb.cred.RefdataValue; de.wekb.helper.RDStore;" %>
<g:if test="${d}">

    <semui:tabs>
        <semui:tabsItemWithoutLink tab="currentTipps" counts="${currentTitleCount}"
                                   class="${(params.tab == null || params.tab == 'currentTipps') ? 'active' : ''}">
            Current Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="retiredTipps" counts="${retiredTitleCount}"
                                   class="${params.tab == 'retiredTipps' ? 'active' : ''}">
            Retired Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="expectedTipps" counts="${expectedTitleCount}"
                                   class="${params.tab == 'expectedTipps' ? 'active' : ''}">
            Expected Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="deletedTipps" counts="${deletedTitleCount}"
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

        <div class="float-right">
            <g:link controller="public" action="search" class="btn btn-primary"
                    params="[qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id]"
                    id="">Search View</g:link>
        </div>

        <br>
        <br>

        <g:render template="/templates/tabTemplates/tippsTab"
                  model="[tippsCount: currentTitleCount, tipps: currentTipps, tab: 'currentTipps']"/>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="retiredTipps" class="${params.tab == 'retiredTipps' ? 'active' : ''}">
        <div class="float-right">
            <g:link controller="public" action="search" class="btn btn-primary"
                    params="[qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_RETIRED.id]"
                    id="">Search View</g:link>
        </div>

        <br>
        <br>

        <g:render template="/templates/tabTemplates/tippsTab"
                  model="[tippsCount: retiredTitleCount, tipps: retiredTipps, tab: 'retiredTipps']"/>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="expectedTipps" class="${params.tab == 'expectedTipps' ? 'active' : ''}">
        <div class="float-right">
            <g:link controller="public" action="search" class="btn btn-primary"
                    params="[qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_EXPECTED.id]"
                    id="">Search View</g:link>
        </div>

        <br>
        <br>

        <g:render template="/templates/tabTemplates/tippsTab"
                  model="[tippsCount: expectedTitleCount, tipps: expectedTipps, tab: 'expectedTipps']"/>

    </semui:tabsItemContent>


    <semui:tabsItemContent tab="deletedTipps" class="${params.tab == 'deletedTipps' ? 'active' : ''}">

        <div class="float-right">
            <g:link controller="public" action="search" class="btn btn-primary"
                    params="[qbe: 'g:tippsOfPkg', qp_pkg_id: d.id, refOid: d.getLogEntityId(), hide: ['qp_pkg_id', 'qp_pkg'], qp_status: RefdataValue.class.name + ':' + RDStore.KBC_STATUS_DELETED.id]"
                    id="">Search View</g:link>
        </div>

        <br>
        <br>

        <g:render template="/templates/tabTemplates/tippsTab"
                  model="[tippsCount: deletedTitleCount, tipps: deletedTipps, tab: 'deletedTipps']"/>

    </semui:tabsItemContent>

    <g:render template="/templates/tabTemplates/variantNamesTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/ddcsTab" model="${[d: d]}"/>

    <g:render template="/templates/tabTemplates/identifiersTab" model="${[d: d]}"/>

</g:if>