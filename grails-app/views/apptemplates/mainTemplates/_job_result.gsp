<g:set var="json" value="${d.resultJson}" />
<dl class="dl-horizontal">
  <dt>
    <gokb:annotatedLabel owner="${d}" property="uuid">Internal ID</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.uuid}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="description">Description</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.description}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="statusText">Status Text</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.statusText}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="type">Type</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.type?.value}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="curatoryGroup">Curatory Group</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:set var="curatoryGroup" value="${ d.getCuratoryGroup()}" />
    <g:if test="${curatoryGroup}">
      <g:link controller="resource" action="show" id="${curatoryGroup.uuid}">${curatoryGroup.name}</g:link>
    </g:if>
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="linkedItemId">Linked Component</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:set var="item" value="${ d.getLinkedItem()}" />
    <g:if test="${item}">
      <g:link controller="resource" action="show" id="${item.uuid}">${item.name}</g:link>
    </g:if>
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="startTime">Start Time</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.startTime}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="endTime">End Time</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.endTime}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="resultJson">Result Message</gokb:annotatedLabel>
  </dt>
  <dd>
    ${json?.message}
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="resultJson">Messages</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:if test="${json?.messages}">
      messages:
      <ul>
        <g:each in="${json.messages}" var="m">
          <g:if test="${m instanceof String}">
            <li>${m}</li>
          </g:if>
          <g:else>
            <li>${m.message}</li>
          </g:else>
        </g:each>
      </ul>
    </g:if>
    <g:if test="${!json?.messages}">
      No Messages
    </g:if>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="resultJson">Errors</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:if test="${json?.errors?.global}">
      <div>Global</div>
      <ul>
        <g:each in="${json?.errors?.global}" var="ge">
          <li>${ge}</li>
        </g:each>
      </ul>
    </g:if>
    <g:if test="${json?.errors?.tipps}">
      <div>Titles</div>
      <ul>
        <g:each in="${json?.errors?.tipps}" var="te">
          <li>${te}</li>
        </g:each>
      </ul>
    </g:if>
    <g:if test="${!json?.errors}">
      No Errors
    </g:if>
  </dd>
</dl>