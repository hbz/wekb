<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<semui:tabsItemContent tab="ddcs">
    <g:if test="${d.id != null}">
        <div class="content we-inline-lists">
            <dl>
                <dt class="control-label">
                    <gokb:annotatedLabel owner="${d}" property="ddcs">Dewey Decimal Classification</gokb:annotatedLabel>
                </dt>
                <dd>
                    <ul>
                        <g:each in="${d.ddcs.sort { it.value }}" var="ddc">
                            <li>${ddc.value}: ${ddc.getI10n('value')}
                            <g:if test="${editable}">
                                <g:link controller='ajaxSupport'
                                        action='unlinkManyToMany'
                                        params="${["__context": "${d.class.name}:${d.id}", "__property": "ddcs", "__itemToRemove": "${ddc.getClassName()}:${ddc.id}", fragment: 'ddcs']}">Unlink</g:link>
                            </g:if>
                            </li>
                        </g:each>
                    </ul>

                    <g:if test="${editable}">
                        <a class="ui right floated black button" href="#"
                           onclick="$('#ddcModal').modal('show');">Add Dewey Decimal Classification</a>

                        <br>
                        <br>

                        <semui:modal id="ddcModal" title="Add Dewey Decimal Classification">

                            <g:form controller="ajaxSupport" action="addToStdCollection" class="form-inline"
                                    params="[fragment: 'ddcs']">
                                <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
                                <input type="hidden" name="__property" value="ddcs"/>
                                <input type="hidden" name="object" value="${d.getClassName()}:${d.id}"/>

                                <div class="field">
                                    <label>Dewey Decimal Classification:</label>

                                    <g:select from="${RefdataCategory.lookup(RCConstants.DDC).sort { it.value }}"
                                              class="dropdown fluid"
                                              id="ddcSelection"
                                              optionKey="${{ it.class.name + ':' + it.id }}"
                                              optionValue="${{ it.value + ': ' + it.getI10n('value') }}"
                                              name="__relatedObject"
                                              value=""/>
                                </div>
                            </g:form>
                        </semui:modal>

                    </g:if>
                </dd>
            </dl>
        </div>
    </g:if>
    <g:else>
        DDCs can be added after the creation process is finished.
    </g:else>
</semui:tabsItemContent>
