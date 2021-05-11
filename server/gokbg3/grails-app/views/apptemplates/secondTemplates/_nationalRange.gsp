<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<ul>
    <g:each in="${d.nationalRanges.sort { it.getI10n('value') }}" var="nationalRange">
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

    <bootStrap:modal id="nationalRangeModal" title="Add National Range">
                <g:form class="form" url="[controller: 'ajaxSupport', action: 'addNationalRange']" method="post">
                    <input type="hidden" name="package" value="${d.id}"/>

                        <div class="field">
                            <label>National Range:</label>

                            <g:select from="${RefdataCategory.lookup(RCConstants.COUNTRY).sort {it.getI10n('value')}}"
                                      class="dropdown fluid"
                                      id="nationalRangeSelection"
                                      optionKey="id"
                                      optionValue="${{ it.getI10n('value') }}"
                                      name="nationalRange"
                                      value=""/>
                        </div>
                </g:form>
    </bootStrap:modal>
</g:if>