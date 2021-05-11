<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<ul>
    <g:each in="${d.regionalRanges.sort { it.getI10n('value') }}" var="regionalRange">
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

    <bootStrap:modal id="regionalRangeModal" title="Add Regional Range">

                <g:form class="form" url="[controller: 'ajaxSupport', action: 'addRegionalRange']" method="post">

                        <input type="hidden" name="package" value="${d.id}"/>

                        <div class="field">
                            <label>Regional Range:</label>

                            <g:select from="${RefdataCategory.lookup(RCConstants.PACKAGE_REGIONAL_RANGE)?.sort {it.getI10n('value')}}"
                                      class="dropdown fluid"
                                      id="regionalRangeSelection"
                                      optionKey="id"
                                      optionValue="${{ it.getI10n('value') }}"
                                      name="regionalRange"
                                      value=""/>
                        </div>
                </g:form>
    </bootStrap:modal>
</g:if>