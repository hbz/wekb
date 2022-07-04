<g:set var="json" value="${d.resultJson}" />
<dl class="dl-horizontal">
  <dt>
    Internal ID
  </dt>
  <dd>
    ${d.uuid}
  </dd>
  <dt>
   Description
  </dt>
  <dd>
    ${d.description}
  </dd>
  <dt>Status Text
  </dt>
  <dd>
    ${d.statusText}
  </dd>
  <dt>
    Type
  </dt>
  <dd>
    ${d.type?.value}
  </dd>
  <dt>
    Curatory Group
  </dt>
  <dd>
    <g:set var="curatoryGroup" value="${ d.getCuratoryGroup()}" />
    <g:if test="${curatoryGroup}">
      <g:link controller="resource" action="show" id="${curatoryGroup.uuid}">${curatoryGroup.name}</g:link>
    </g:if>
  </dd>
  <dt>
    Linked Component
  </dt>
  <dd>
    <g:set var="item" value="${ d.getLinkedItem()}" />
    <g:if test="${item}">
      <g:link controller="resource" action="show" id="${item.uuid}">${item.name}</g:link>
    </g:if>
  </dd>
  <dt>
    Start Time
  </dt>
  <dd>
    ${d.startTime}
  </dd>
  <dt>
    End Time
  </dt>
  <dd>
    ${d.endTime}
  </dd>
  <dt>
    Result Message
  </dt>
  <dd>
    ${json?.message}
  </dd>
  <dt>
    Messages
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
    Errors
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