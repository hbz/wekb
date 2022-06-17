<%@ page import="de.wekb.helper.RCConstants; org.gokb.cred.RefdataCategory" %>
<g:if test="${d.id != null}">
    <div class="ui bulleted list">
        <g:each in="${d.regionalRanges.sort { it.getI10n('value') }}" var="regionalRange">
            <div class="item">${regionalRange.getI10n('value')}
            <g:if test="${editable}">
                <g:link controller='ajaxSupport'
                        action='unlinkManyToMany'
                        params="${["__context": "${d.class.name}:${d.id}", "__property": "regionalRanges", "__itemToRemove": "${regionalRange.getClassName()}:${regionalRange.id}"]}">Unlink</g:link>
            </g:if>
            </div>
        </g:each>
    </div>

    <g:if test="${editable}">
        <a class="ui right floated black button" href="#" onclick="$('#regionalRangesModal').modal('show');">Add Regional Range</a>

        <br>
        <br>

        <semui:modal id="regionalRangesModal" text="Add Regional Range">
            <g:form controller="ajaxSupport" action="addToStdCollection" class="form-inline">
                <input type="hidden" name="__context" value="${d.class.name}:${d.id}"/>
                <input type="hidden" name="__property" value="regionalRanges"/>

                <div class="modal-header">
                    <h3 class="modal-title">Add Regional Range</h3>
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                </div>

                <div class="modal-body">

                    <div class="field">
                        <label>Regional Range:</label>

                        <semui:xEditableManyToOne class="form-inline" style="display:inline-block;"
                                                      name="__relatedObject"
                                                      baseClass="org.gokb.cred.RefdataValue"
                                                      filter1="${RCConstants.PACKAGE_REGIONAL_RANGE}"/>
                    </div>

                </div>

                <div class="modal-footer">
                    <button type="submit" class="btn btn-default">Add</button>
                </div>
            </g:form>
        </semui:modal>
    </g:if>
</g:if>