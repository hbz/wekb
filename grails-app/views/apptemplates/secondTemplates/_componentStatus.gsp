<g:if test="${d.id}">
    <dl class="dl-horizontal">
        <dt><gokb:annotatedLabel owner="${d}" property="dateCreated">Date Created</gokb:annotatedLabel></dt>
        <dd>
            ${d.dateCreated ?: ''}
        </dd>
        <dt><gokb:annotatedLabel owner="${d}" property="lastUpdated">Last Updated</gokb:annotatedLabel></dt>
        <dd>
            ${d.lastUpdated ?: ''}
        </dd>
        <dt><gokb:annotatedLabel owner="${d}" property="uuid">UUID</gokb:annotatedLabel></dt>
        <dd>
            ${d.uuid ?: ''}
        </dd>
    </dl>
</g:if>
