<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<g:if test="${d.id != null}">
    <ul>
        <g:each in="${d.languages.sort { it.language.getI10n('value') }}" var="kbComponentLanguage">
            <li>${kbComponentLanguage.language.getI10n('value')}
            <g:if test="${editable}">
                <g:link controller="ajaxSupport"
                        action="deleteLanguage" id="${kbComponentLanguage.id}" params="[fragment: 'altnames']">Unlink</g:link>
            </g:if>
            </li>
        </g:each>
    </ul>

    <g:if test="${editable}">
        <a data-toggle="modal" data-cache="false"
           data-target="#languageModal">Add Language</a>

        <div id="languageModal" class="qmodal modal modal-wide" role="dialog" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <g:form controller="ajaxSupport" action="addToCollection"
                            class="form-inline">
                        <input type="hidden" name="__context"
                               value="${d.class.name}:${d.id}" />
                        <input type="hidden" name="__newObjectClass"
                               value="wekb.KBComponentLanguage" />
                        <input type="hidden" name="__recip" value="kbcomponent" />
                        <div class="modal-header">
                            <h3 class="modal-title">Add Language</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>

                        <div class="modal-body">

                            <div class="field">
                                <label>Language:</label>

                                <gokb:simpleReferenceTypedown class="form-inline" style="display:inline-block;"
                                                              name="language"
                                                              baseClass="org.gokb.cred.RefdataValue"
                                                              filter1="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
                            </div>

                        </div>

                        <div class="modal-footer">
                            <button type="submit" class="btn btn-default">Add</button>
                        </div>
                    </g:form>
                </div>
            </div>
        </div>
    </g:if>
</g:if>