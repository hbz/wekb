<g:set var="ctxoid" value="${org.gokb.cred.KBComponent.deproxy(d).class.name}:${d.id}"/>

<g:if test="${d.id}">
   <a data-toggle="modal" data-cache="false"
       data-target="#identifiersModal">Add Identifier</a>

    <bootStrap:modal id="identifiersModal" title="Add Identifier">

      <g:form controller="ajaxSupport" action="addIdentifier" class="form-inline">
        <input type="hidden" name="hash" value="${hash}"/>

        <input type="hidden" name="__context" value="${ctxoid}" />

        <dt class="dt-label">Identifier Namespace</dt>
        <dd>
          <gokb:simpleReferenceTypedown class="form-control" name="identifierNamespace" baseClass="org.gokb.cred.IdentifierNamespace" filter1="${d.class.simpleName}"/>
        </dd>

        <dt class="dt-label">Identifier Value</dt>
        <dd>
          <input type="text" class="form-control" name="identifierValue" required />
        </dd>

      </g:form>
    </bootStrap:modal>

</g:if>
<g:else>
  Identifiers can be added after the creation process is finished.
</g:else>

