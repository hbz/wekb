<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<g:if test="${d.id != null}">
    <div class="ui bulleted list">
        <g:each in="${d.languages.sort { it.language.getI10n('value') }}" var="kbComponentLanguage">
            <div class="item">${kbComponentLanguage.language.getI10n('value')}
            <g:if test="${editable}">
                <g:link controller="ajaxSupport"
                        action="deleteLanguage" id="${kbComponentLanguage.id}"
                        params="[fragment: 'languages']">Delete</g:link>
            </g:if>
            </div>
        </g:each>
    </div>

    <g:if test="${editable}">
        <a class="ui right floated black button" href="#" onclick="$('#languageModal').modal('show');">Add Language</a>

        <br>
        <br>

        <semui:modal id="languageModal" title="Add Language">
            <g:form controller="ajaxSupport" action="addToCollection" class="ui form">
                <input type="hidden" name="__context"
                       value="${d.class.name}:${d.id}"/>
                <input type="hidden" name="__newObjectClass"
                       value="wekb.KBComponentLanguage"/>
                <input type="hidden" name="__recip" value="kbcomponent"/>

                <div class="field">
                    <label>Language:</label>

                    <semui:simpleReferenceDropdown name="language"
                                                   baseClass="org.gokb.cred.RefdataValue"
                                                   filter1="${RCConstants.KBCOMPONENT_LANGUAGE}"/>
                </div>
            </g:form>
        </semui:modal>
    </g:if>
</g:if>