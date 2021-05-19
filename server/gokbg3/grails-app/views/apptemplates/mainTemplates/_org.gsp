<%@ page import="de.wekb.helper.RCConstants" %>
%{--<g:set var="editable" value="${ d.isEditable() && ((d.respondsTo('getCuratoryGroups') ? (request.curator != null && request.curator.size() > 0) : true) || (params.curationOverride == 'true' && request.user.isAdmin())) }" />--}%
<dl class="dl-horizontal">
  <dt>
          <gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
  </dt>
  <dd>
          <gokb:xEditable  owner="${d}" field="name" />
  </dd>
  <dt>
          <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
  </dt>
  <dd>
    <sec:ifAnyGranted roles="ROLE_SUPERUSER">
      <gokb:xEditableRefData owner="${d}" field="status" config="${RCConstants.KBCOMPONENT_STATUS}" />
    </sec:ifAnyGranted>
    <sec:ifNotGranted roles="ROLE_SUPERUSER">
      ${d.status?.value ?: 'Not Set'}
    </sec:ifNotGranted>
  </dd>

  <dt>
    <gokb:annotatedLabel owner="${d}" property="mission">Mission</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditableRefData owner="${d}" field="mission" config="${RCConstants.ORG_MISSION}" />
  </dd>
  <dt>
    <gokb:annotatedLabel owner="${d}" property="homepage">Homepage</gokb:annotatedLabel>
  </dt>
  <dd>
    <gokb:xEditable  owner="${d}" field="homepage" />
  </dd>
  <dt class="dt-label">
    <gokb:annotatedLabel owner="${d}" property="roles">Roles</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:if test="${d.id != null}">
      <g:if test="${d.roles}">
        <ul>
          <g:each in="${d.roles?.sort{it.getI10n('value')}}" var="t">
            <li>
              ${t.value}
            <g:if test="${editable}">
              <g:link controller='ajaxSupport'
                      action='unlinkManyToMany'
                      params="${ ["__context" : "${d.class.name}:${d.id}", "__property" : "roles", "__itemToRemove" : "${t.getClassName()}:${t.id}" ] }">Unlink</g:link>
            </g:if>
            </li>
          </g:each>
        </ul>
      </g:if>

      <g:if test="${editable}">
        <a data-toggle="modal" data-cache="false"
           data-target="#rolesModal">Add Role</a>

        <bootStrap:modal id="rolesModal" title="Add Role">

          <g:form controller="ajaxSupport" action="addToStdCollection" class="form-inline">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}" />
            <input type="hidden" name="__property" value="roles" />
            Role: <gokb:simpleReferenceTypedown class="form-inline" style="display:inline-block;" name="__relatedObject"
                                          baseClass="org.gokb.cred.RefdataValue" filter1="${RCConstants.ORG_ROLE}" />
          </g:form>
        </bootStrap:modal>
      </g:if>
    </g:if>
  </dd>

  <dt class="dt-label">
    <gokb:annotatedLabel owner="${d}" property="technicalSupport">Technical Support</gokb:annotatedLabel>
  </dt>
  <dd>
    <g:if test="${d.id != null}">
      <g:if test="${d.contacts}">
        <ul>
          <g:each in="${d.contacts?.sort{it.content}}" var="contact">
            <li>
              ${contact.content}: ${contact.contentType.getI10n('value')} (Language: ${contact.language.getI10n('value')})
              <g:if test="${editable}">
                <g:link controller='ajaxSupport'
                        action='unlinkManyToMany'
                        params="${ ["__context" : "${d.class.name}:${d.id}", "__property" : "contacts", "__itemToRemove" : "${contact.class.name}:${contact.id}" ] }">Unlink</g:link>
              </g:if>
            </li>
          </g:each>
        </ul>
      </g:if>

      <g:if test="${editable}">
        <a data-toggle="modal" data-cache="false"
           data-target="#technicalSupportModal">Add Technical Support</a>

        <bootStrap:modal id="technicalSupportModal" title="Add Technical Support">

          <g:form controller="ajaxSupport" action="addToCollection"
                  class="form-inline">
            <input type="hidden" name="__context" value="${d.class.name}:${d.id}" />
            <input type="hidden" name="__newObjectClass" value="wekb.Contact" />
            <input type="hidden" name="__recip" value="org" />
            <input type="hidden" name="fragment" value="technicalSupport" />
            <dt class="dt-label">Content</dt>
            <dd>
              <input type="text" class="form-control select-m" name="content" />
            </dd>
            <dt class="dt-label">Language</dt>
            <dd>
              <gokb:simpleReferenceTypedown class="form-control" name="language"
                                            baseClass="org.gokb.cred.RefdataValue"
                                            filter1="${RCConstants.KBCOMPONENT_LANGUAGE}" />
            </dd>
            <dt class="dt-label">Content Type</dt>
            <dd>
              <gokb:simpleReferenceTypedown class="form-control" name="contentType"
                                            baseClass="org.gokb.cred.RefdataValue"
                                            filter1="${RCConstants.CONTACT_CONTENT_TYPE}" />
            </dd>

            <dt class="dt-label">Type</dt>
            <dd>
              <gokb:simpleReferenceTypedown class="form-control" name="type"
                                            baseClass="org.gokb.cred.RefdataValue"
                                            filter1="${RCConstants.CONTACT_TYPE}" />
            </dd>
          </g:form>
        </bootStrap:modal>
      </g:if>
    </g:if>
  </dd>

</dl>
  <g:render template="/tabTemplates/orgTabs"/>

  <g:render template="/apptemplates/secondTemplates/componentStatus"/>

