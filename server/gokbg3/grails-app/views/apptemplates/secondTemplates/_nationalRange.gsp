<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<g:if test="${d.id != null}">
    <ul>
        <g:each in="${d.nationalRanges.sort { it.getI10n('value') }}" var="nationalRange">
            <li>${nationalRange.value}: ${nationalRange.getI10n('value')}
            <g:if test="${editable}">
                <g:link controller='ajaxSupport'
                        action='unlinkManyToMany'
                        params="${["__context": "${d.class.name}:${d.id}", "__property": "nationalRanges", "__itemToRemove": "${nationalRange.getClassName()}:${nationalRange.id}"]}">Unlink</g:link>
            </g:if>
            </li>
        </g:each>
    </ul>

    <g:if test="${editable}">
        <a data-toggle="modal" data-cache="false"
           data-target="#nationalRangesModal">Add National Range</a>

        <div id="nationalRangesModal" class="qmodal modal modal-wide" role="dialog" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <g:form controller="ajaxSupport" action="addToStdCollection" class="form-inline">
                        <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
                        <input type="hidden" name="__property" value="nationalRanges"/>

                        <div class="modal-header">
                            <h3 class="modal-title">Add National Range</h3>
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                        </div>

                        <div class="modal-body">

                            <div class="field">
                                <label>National Range:</label>

                                <gokb:simpleReferenceTypedown class="form-inline" style="display:inline-block;"
                                                              name="__relatedObject"
                                                              baseClass="org.gokb.cred.RefdataValue"
                                                              filter1="${RCConstants.COUNTRY}"/>
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