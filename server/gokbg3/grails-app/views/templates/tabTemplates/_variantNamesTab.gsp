<%@ page import="de.wekb.helper.RCConstants" %>
<semui:tabsItemContent tab="variantNames">
  <g:if test="${d.id != null}">
        <table class="ui selectable striped sortable celled table">
          <thead>
            <tr>
              <th>Alternate Name</th>
              <th>Status</th>
              <th>Variant Type</th>
              <th>Locale</th>
                        <g:if test="${ editable && showActions }">
                        <th>Actions</th>
                        </g:if>
            </tr>
          </thead>
          <tbody>
            <g:each in="${d.variantNames.sort{it.variantName}}" var="v">
              <tr>
                <td>
                  ${v.variantName}
                </td>
                <td><semui:xEditableRefData owner="${v}" field="status" config="${RCConstants.KBCOMPONENT_VARIANTNAME_STATUS}" overWriteEditable="${editable}"/></td>
                <td><semui:xEditableRefData owner="${v}" field="variantType" config="${RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE}" overWriteEditable="${editable}"/></td>
                <td><semui:xEditableRefData owner="${v}" field="locale" config="${RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL}" overWriteEditable="${editable}"/></td>
                  <g:if test="${ editable && showActions }">
                    <td>
                              <g:link controller="ajaxSupport" action="authorizeVariant" id="${v.id}" params="[fragment: 'variantNames']">Make Authorized</g:link>,
                              <g:link controller="ajaxSupport" class="confirm-click" data-confirm-message="Are you sure you wish to delete this Variant?"
                                action="deleteVariant" id="${v.id}" params="[fragment: 'variantNames']">Delete</g:link>
                    </td>
                  </g:if>
              </tr>
            </g:each>
          </tbody>
        </table>

        <g:if test="${editable}">
            <a class="ui right floated black button" href="#" onclick="$('#variantnamesModal').modal('show');">Add Variant Name</a>

            <br>
            <br>

            <semui:modal id="variantnamesModal" title="Add Variant Name">

              <g:form controller="ajaxSupport" action="addToCollection" class="ui form">
                <input type="hidden" name="__context"
                       value="${d.class.name}:${d.id}" />
                <input type="hidden" name="__newObjectClass"
                       value="org.gokb.cred.KBComponentVariantName" />
                <input type="hidden" name="__recip" value="owner" />
                <input type="hidden" name="fragment" value="variantNames" />
                  <div class="field">
                              <label>Variant Name</label>

                    <input type="text" name="variantName" />
                  </div>
                  <div class="field">
                              <label>Locale</label>
                  <semui:simpleReferenceDropdown  name="locale"
                                                baseClass="org.gokb.cred.RefdataValue"
                                                filter1="${RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL}" />
                  </div>
                  <div class="field">
                                <label>Variant Type</label>
                  <semui:simpleReferenceDropdown  name="variantType"
                                                baseClass="org.gokb.cred.RefdataValue"
                                                filter1="${RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE}" />
                  </div>
              </g:form>
            </semui:modal>
        </g:if>
  </g:if>
  <g:else>
    Alternate names can be added after the creation process is finished.
  </g:else>
</semui:tabsItemContent>
