<h1>Project: ${d.name}</h1>

<dl>
  <dt>Internal ID</dt>
  <dd>${d.id}</dd>
  <dt>Project Name</dt>
  <dd>${d.name}</dd>
  <dt>Description</dt>
  <dd>${d.description}</dd>
  <dt>Provider</dt>
  <dd>${d.provider?.name}</dd>
  <dt>Checked In?</dt>
  <dd>${d.checkedIn}</dd>
  <dt>Checked Out By</dt>
  <dd>${d.checkedOutBy}</dd>
  <hr/>
  <dt>Last validation result</dt>
  <dd>${d.lastValidationResult}</dd>
  <dt>Candidate Rules</dt>
  <dd>
    <table class="table">
      <tbody>
        <g:each in="${d.possibleRulesAsList()}" var="r">
          <tr><td>${r}</td></tr>
        </g:each>
      </tbody>
    <table>
  </dd>
</dl>

