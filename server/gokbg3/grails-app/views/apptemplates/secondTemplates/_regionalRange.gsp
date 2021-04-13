<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<ul>
    <g:each in="${d.regionalRanges.sort { it.value }}" var="regionalRange">
        <li>${regionalRange.value}
        <g:if test="${editable}">
            <g:link controller="ajaxSupport" action="deleteRegionalRange"
                    params="[package: d.id, removeRegionalRange: regionalRange.id]">
                Unlink
            </g:link>
        </g:if>
        </li>
    </g:each>
</ul>

<g:if test="${editable}">
    <a data-toggle="modal" data-cache="false"
       data-target="#regionalRangeModal">Add Regional Range</a>

    <div id="regionalRangeModal" class="qmodal modal modal-wide" role="dialog" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <g:form class="ui form" url="[controller: 'ajaxSupport', action: 'addRegionalRange']" method="post">
                    <div class="modal-header">
                        <h3 class="modal-title">Add Regional Range</h3>
                        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                    </div>

                    <div class="modal-body">

                        <input type="hidden" name="package" value="${d.id}"/>

                        <div class="field">
                            <label>Regional Range:</label>

                            <g:select from="${RefdataCategory.lookup(RCConstants.PACKAGE_REGIONAL_RANGE)}"
                                      class="ui dropdown fluid"
                                      id="regionalRangeSelection"
                                      optionKey="id"
                                      optionValue="${{ it.value }}"
                                      name="regionalRange"
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