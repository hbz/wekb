<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory;" %>
<dl>
  <dt class="control-label">Provider</dt>
  <dd><semui:xEditableManyToOne owner="${d}" field="provider" baseClass="org.gokb.cred.Org"/></dd>
</dl>

<dl>
  <dt class="control-label">Source</dt>
  <dd><semui:xEditableManyToOne owner="${d}" field="source" baseClass="org.gokb.cred.Source"/></dd>
</dl>

<dl>
  <dt class="control-label">Nominal Platform</dt>
  <dd><semui:xEditableManyToOne owner="${d}" field="nominalPlatform"
                                baseClass="org.gokb.cred.Platform"/></dd>
</dl>
<dl>
  <dt class="control-label">Status</dt>
  <dd><sec:ifAnyGranted roles="ROLE_SUPERUSER">
    <semui:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}"/>
  </sec:ifAnyGranted>
  <sec:ifNotGranted roles="ROLE_SUPERUSER">
    ${d.status?.value ?: 'Not Set'}
  </sec:ifNotGranted>
  </dd>
</dl>
<dl>
  <dt class="control-label">Last Update Comment</dt>
  <dd><semui:xEditable owner="${d}" field="lastUpdateComment"/></dd>
</dl>
<dl>
  <dt class="control-label">Description</dt>
  <dd><semui:xEditable owner="${d}" field="description"/></dd>

</dl>
<dl>
  <dt class="control-label">Description URL</dt>
  <dd><semui:xEditable owner="${d}" field="descriptionURL"/>
    <g:if test="${d.descriptionURL}">
      &nbsp;<a aria-label="${d.descriptionURL}"
               href="${d.descriptionURL.startsWith('http') ? d.descriptionURL : 'http://' + d.descriptionURL}"
               target="new"><i class="fas fa-external-link-alt"></i></a>
    </g:if>
  </dd>
</dl>

<dl>
  <dt class="control-label">
    Global Note
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="globalNote"/>
  </dd>
</dl>

<dl>
  <dt class="control-label">
    Breakable
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="breakable" config="${RCConstants.PACKAGE_BREAKABLE}"/>
  </dd>
</dl>
<dl>
  <dt class="control-label">
    Content Type
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="contentType"
                     config="${RCConstants.PACKAGE_CONTENT_TYPE}"/>
  </dd>
</dl>
<dl>
  <dt class="control-label">
    File
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="file" config="${RCConstants.PACKAGE_FILE}"/>
  </dd>
</dl>
<dt class="control-label">
  Open Access
</dt>
<dd>
  <semui:xEditable owner="${d}" field="openAccess" config="${RCConstants.PACKAGE_OPEN_ACCESS}"/>
</dd>
<dl>
  <dt class="control-label">
    Payment Type
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="paymentType"
                     config="${RCConstants.PACKAGE_PAYMENT_TYPE}"/>
  </dd>
</dl>
<dl>
  <dt class="control-label">
    Scope
  </dt>
  <dd>
    <semui:xEditable owner="${d}" field="scope" config="${RCConstants.PACKAGE_SCOPE}"/>
  </dd>
</dl>
</div>
<g:if test="${controllerName != 'create'}">
  <dl>
    <dt class="control-label">
      National Range
    </dt>
    <dd>
      <g:if test="${d.scope?.value == 'National'}">
        <g:render template="/templates/nationalRange"/>
      </g:if>
    </dd>
  </dl>
  <dl>
    <dt class="control-label">
      Regional Range
    </dt>
    <dd>
      <g:if test="${RefdataCategory.lookup(RCConstants.COUNTRY, 'DE') in d.nationalRanges && d.scope?.value == 'National'}">
        <g:render template="/templates/regionalRange"/>
      </g:if>
    </dd>
  </dl>
  <dl>
    <dt class="dt-label">
      Archiving Agency
    </dt>
    <dd>
      <table class="ui small selectable striped celled table">
        <thead>
        <tr>
          <th>#</th>
          <th>Archiving Agency</th>
          <th>Open Access</th>
          <th>Post-Cancellation Access (PCA)</th>
          <th></th>
        </tr>
        </thead>
        <tbody>
        <g:each in="${d.paas?.sort { it.archivingAgency?.value }}" var="paa" status="i">
          <tr>
            <td>${i + 1}</td>
            <td><semui:xEditable owner="${paa}" field="archivingAgency"
                                 config="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
            <td><semui:xEditable owner="${paa}" field="openAccess"
                                 config="${RCConstants.PAA_OPEN_ACCESS}"/>
            </td>
            <td>
              <semui:xEditable owner="${paa}" field="postCancellationAccess"
                               config="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
            </td>
            <td>
              <g:if test="${editable}">
                <g:link controller='ajaxSupport'
                        action='delete'
                        params="${["__context": "${paa.class.name}:${paa.id}"]}">Unlink</g:link>
              </g:if>
            </td>
          </tr>
        </g:each>
        </tbody>
      </table>

      <g:if test="${editable}">
        <a data-toggle="modal" data-cache="false"
           data-target="#paaModal">Add Archiving Agency</a>
      </g:if>
    </dd>
  </dl>
</g:if>

<g:if test="${editable}">
  <bootStrap:modal id="paaModal" title="Add Archiving Agency">

    <g:form controller="ajaxSupport" action="addToCollection"
            class="form-inline">
      <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
      <input type="hidden" name="__newObjectClass" value="wekb.PackageArchivingAgency"/>
      <input type="hidden" name="__recip" value="pkg"/>
      <dt class="dt-label">Archiving Agency</dt>
      <dd>
        <gokb:simpleReferenceTypedown class="form-control" name="archivingAgency"
                                      baseClass="org.gokb.cred.RefdataValue"
                                      filter1="${RCConstants.PAA_ARCHIVING_AGENCY}"/>
      </dd>
      <dt class="dt-label">Open Access</dt>
      <dd>
        <gokb:simpleReferenceTypedown class="form-control" name="openAccess"
                                      baseClass="org.gokb.cred.RefdataValue"
                                      filter1="${RCConstants.PAA_OPEN_ACCESS}"/>
      </dd>

      <dt class="dt-label">Post-Cancellation Access (PCA)</dt>
      <dd>
        <gokb:simpleReferenceTypedown class="form-control" name="postCancellationAccess"
                                      baseClass="org.gokb.cred.RefdataValue"
                                      filter1="${RCConstants.PAA_POST_CANCELLATION_ACCESS}"/>
      </dd>
    </g:form>
  </bootStrap:modal>
</g:if>