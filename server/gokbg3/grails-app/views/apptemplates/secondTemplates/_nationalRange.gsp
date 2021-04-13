<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<ul>
    <g:each in="${d.nationalRanges.sort { it.value }}" var="nationalRange">
        <li>${nationalRange.value}
        <g:if test="${editable}">
            <g:link controller="ajaxSupport" action="deleteNationalRange"
                    params="[package: d.id, removeNationalRange: nationalRange.id]">
                Unlink
            </g:link>
        </g:if>
        </li>
    </g:each>
</ul>

<g:if test="${editable}">
    <a data-toggle="modal" data-cache="false"
       data-target="#nationalRangeModal">Add National Range</a>

    <div id="nationalRangeModal" class="qmodal modal modal-wide" role="dialog" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <g:form class="ui form" url="[controller: 'ajaxSupport', action: 'addNationalRange']" method="post">
                    <div class="modal-header">
                        <h3 class="modal-title">Add National Range</h3>
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    </div>

                    <div class="modal-body">

                        <input type="hidden" name="package" value="${d.id}"/>

                        <div class="field">
                            <label>National Range:</label>

                            <g:select from="${RefdataCategory.lookup(RCConstants.COUNTRY)}"
                                      class="ui dropdown fluid"
                                      id="nationalRangeSelection"
                                      optionKey="id"
                                      optionValue="${{ it.value }}"
                                      name="nationalRange"
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