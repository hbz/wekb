<g:if test="${d.id}">
    <semui:tabs>

        <semui:tabsItemWithoutLink tab="packages" class="active">
            Packages
        </semui:tabsItemWithoutLink>
        <semui:tabsItemWithoutLink tab="notes">
            Notes
        </semui:tabsItemWithoutLink>

    </semui:tabs>

    <semui:tabsItemContent tab="packages" class="active">
        <div class="content we-inline-lists">
            <dl>
                <dt class="control-label">
                    <gokb:annotatedLabel owner="${d}" property="packages">Packages</gokb:annotatedLabel>
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:packages', qp_source_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_source', 'qp_source_id']]"
                            id="">Packages on this Source</g:link>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>

    <semui:tabsItemContent tab="notes">
        <div class="content we-inline-lists">
            <dl>
                <dt class="control-label">
                    <gokb:annotatedLabel owner="${d}" property="notes">Notes</gokb:annotatedLabel>
                </dt>
                <dd>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:notes', qp_ownerClassID: d.id, inline: true, qp_ownerClass: d.getClass().name]"
                            id="">Notes on this Source</g:link>
                </dd>
            </dl>
        </div>
    </semui:tabsItemContent>

</g:if>
