
<g:if test="${flash.error}">
  <div id="error" style="display:none">
    <ul>
    <g:if test="${flash.error instanceof Map || flash.error instanceof List }">
      <g:each in="${flash.error}" var="error">
        <g:if test="${error instanceof String}">
          <li>${error}</li>
        </g:if>
        <g:else>
          <g:each in="${error}" var="eo">
            <li>${eo.key}
              <ul>
                <g:each in="${eo.value}" var="em">
                  <li>${em.message}</li>
                </g:each>
              </ul>
            </li>
          </g:each>
        </g:else>
      </g:each>
    </g:if>
    <g:else>
      <li>${flash.error}</li>
    </g:else>
    </ul>
  </div>
</g:if>
<g:elseif test="${flash.success}">
  <div id="success" style="display:none">
    <ul>
    <g:if test="${flash.success instanceof Map || flash.success instanceof List }">
      <g:each in="${flash.success}" var="success">
        <li>${success}</li>
      </g:each>
    </g:if>
    <g:else>
      <li>${flash.success}</li>
    </g:else>
    </ul>
  </div>
</g:elseif>
<g:elseif test="${flash.message}">
  <div id="msg" style="display:none">
    <ul>
    <g:if test="${flash.message instanceof Map || flash.message instanceof List }">
      <g:each in="${flash.message}" var="msg">
        <li>${msg}</li>
      </g:each>
    </g:if>
    <g:else>
      <li>${flash.message}</li>
    </g:else>
    </ul>
  </div>
</g:elseif>
