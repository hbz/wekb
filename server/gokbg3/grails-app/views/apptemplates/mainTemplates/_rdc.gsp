<dl class="dl-horizontal">
  <dt>
    <gokb:annotatedLabel owner="${d}" property="id">Internal Id</gokb:annotatedLabel>
  </dt>
  <dd>
    ${d.id?:'New record'}
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="description">Category Name / Description</gokb:annotatedLabel>
  </dt>
  <dd>
    <semui:xEditable  owner="${d}" field="desc" />
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="label">Label</gokb:annotatedLabel>
  </dt>
  <dd>
    <semui:xEditable  owner="${d}" field="label" />
  </dd>


  <g:if test="${d.id != null}">
    <dt>
      <gokb:annotatedLabel owner="${d}" property="values">Values</gokb:annotatedLabel>
    </dt>
    <dd>
      <table class="table table-bordered">
        <thead>
          <tr>
            <td>Value</td>
            <td>Deprecate (Use)</td>
            <td>Sort Key</td>
            <td>Actions</td>
          </tr>
        </thead>
        <tbody>
          <g:each in="${d.values}" var="v">
            <tr>
              <td>
                <semui:xEditable  owner="${v}" field="value" />
              </td>
              <td><semui:xEditableManyToOne owner="${v}"
                  field="useInstead" baseClass="org.gokb.cred.RefdataValue"
                  filter1="${d.desc}">
                  ${v.useInstead?.value}
                </semui:xEditableManyToOne></td>
              <td><semui:xEditable  owner="${v}" field="sortKey" /></td>
              <td></td>
            </tr>
          </g:each>
        </tbody>
      </table>
      <sec:ifAnyGranted roles="ROLE_ADMIN">
        <hr />

        <h4>
          <gokb:annotatedLabel owner="${d}" property="addRD">Add refdata value</gokb:annotatedLabel>
        </h4>
        <dl class="dl-horizontal">
          <g:form controller="ajaxSupport" action="addToCollection"
            class="form-inline">
            <input type="hidden" name="__context"
              value="${d.className}:${d.id}" />
            <input type="hidden" name="__newObjectClass"
              value="org.gokb.cred.RefdataValue" />
            <input type="hidden" name="__recip" value="owner" />
            <dt class="dt-label">Refdata Value</dt>
            <dd>
              <input type="text" class="form-control" name="value" />
            </dd>
            <dt class="dt-label">Display Class</dt>
            <dd>
              <input type="text" class="form-control" name="icon" />
            </dd>
            <dt class="dt-label">Sort Key</dt>
            <dd>
              <input type="text" class="form-control" name="sortKey" />
            </dd>
            <dt></dt>
            <dd>
              <button type="submit" class="btn btn-default btn-primary">Add</button>
            </dd>
          </g:form>
        </dl>
      </sec:ifAnyGranted>
    </dd>
  </g:if>
  <g:else>
  </g:else>
</dl>
