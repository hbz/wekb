<%@ page import="de.wekb.helper.RCConstants; de.wekb.helper.RDStore;" %>

<g:if test="${d}">
    <semui:tabs>

        <semui:tabsItemWithoutLink tab="statistic" class="active">
            Statistic
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="titledetails" counts="${d.currentTippCount}">
            Hosted Titles
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="packages" counts="${d.hostedPackages.size()}">
            Packages
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="variantNames" counts="${d.variantNames.size()}">
            Alternate Names
        </semui:tabsItemWithoutLink>
    </semui:tabs>

    <semui:tabsItemContent tab="titledetails">
        <g:link class="display-inline" controller="search" action="index"
                params="[qbe: 'g:tipps', qp_plat_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_plat', 'qp_plat_id'], qp_status: org.gokb.cred.RefdataValue.class.name + ':' + RDStore.KBC_STATUS_CURRENT.id]"
                id="">Titles on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="packages">
        <g:link class="display-inline" controller="search" action="index"
                params="[qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_platform', 'qp_platform_id']]"
                id="">Packages on this Platform</g:link>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="variantNames">
        <g:render template="/templates/tabTemplates/variantNamesTab"
                  model="${[d: d, showActions: true]}"/>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="statistic" class="active">
        <div class="content we-inline-lists">
            <dl>
                <dt class="control-label">
                    Statistics Format
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="statisticsFormat"
                                            config="${RCConstants.PLATFORM_STATISTICS_FORMAT}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Statistics Update
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="statisticsUpdate"
                                            config="${RCConstants.PLATFORM_STATISTICS_UPDATE}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Statistics Admin Portal Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="statisticsAdminPortalUrl" outGoingLink="true"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter Certified
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterCertified" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Last Audit Date
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="lastAuditDate" type="date"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter Registry Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterRegistryUrl" outGoingLink="true"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R3 Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR3Supported" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R4 Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR4Supported" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR5Supported" config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R4 Sushi Api Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR4SushiApiSupported"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Sushi Api Supported
                </dt>
                <dd>
                    <semui:xEditableRefData owner="${d}" field="counterR5SushiApiSupported"
                                            config="${RCConstants.YN}"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R4 Sushi Server Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterR4SushiServerUrl"/>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">
                    Counter R5 Sushi Server Url
                </dt>
                <dd>
                    <semui:xEditable owner="${d}" field="counterR5SushiServerUrl"/>
                </dd>

            </dl>
        </div>
    </semui:tabsItemContent>

</g:if>
