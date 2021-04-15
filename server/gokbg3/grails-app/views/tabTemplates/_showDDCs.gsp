<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<div class="tab-pane" id="ddcs">
    <g:if test="${d.id != null}">
        <dl>
            <dt>
                <gokb:annotatedLabel owner="${d}" property="ddcs">Dewey Decimal Classification</gokb:annotatedLabel>
            </dt>
            <dd>
                <ul>
                    <g:each in="${d.ddcs?.sort { it.value }}" var="ddc">
                        <li>${ddc.value}: ${ddc.getI10n('value')}
                        <g:if test="${editable}">
                            <g:link controller="ajaxSupport" action="deleteDDC"
                                    params="${[object: "${d.getClassName()}:${d.id}", removeDDC: ddc.id]}">
                                Unlink
                            </g:link>
                        </g:if>
                        </li>
                    </g:each>
                </ul>

                <g:if test="${editable}">
                    <a data-toggle="modal" data-cache="false"
                       data-target="#ddcModal">Add Dewey Decimal Classification</a>

                    <div id="ddcModal" class="qmodal modal modal-wide" role="dialog" tabindex="-1">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <g:form class="ui form" url="[controller: 'ajaxSupport', action: 'addDDC']" method="post">
                                    <div class="modal-header">
                                        <h3 class="modal-title">Add Dewey Decimal Classification</h3>
                                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                                    </div>

                                    <div class="modal-body">

                                        <input type="hidden" name="object" value="${d.getClassName()}:${d.id}"/>

                                        <div class="field">
                                            <label>Dewey Decimal Classification:</label>

                                            <g:select from="${RefdataCategory.lookup(RCConstants.DDC).sort {it.value}}"
                                                      class="ui dropdown fluid"
                                                      id="ddcSelection"
                                                      optionKey="id"
                                                      optionValue="${{ it.value +': '+ it.getI10n('value')}}"
                                                      name="ddc"
                                                      value=""/>
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
            </dd>
        </dl>
    </g:if>
    <g:else>
        DDCs can be added after the creation process is finished.
    </g:else>
</div>
