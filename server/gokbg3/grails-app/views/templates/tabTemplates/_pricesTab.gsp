<%@ page import="de.wekb.helper.RCConstants " %>
<semui:tabsItemContent tab="prices">
    <g:if test="${d.id != null}">
                <table class="ui selectable striped sortable celled table">
                    <thead>
                    <tr>
                        <th>Price Type</th>
                        <th>Value</th>
                        <th>Currency</th>
                       %{-- <th>Start Date</th>
                        <th>End Date</th>--}%
                        <g:if test="${editable}">
                            <th>Actions</th>
                        </g:if>
                    </tr>
                    </thead>
                    <tbody>
                    <g:each in="${d.prices.findAll{it.endDate == null}}" var="somePrice">
                        <tr>
                            <td><semui:xEditableRefData owner="${somePrice}" field="priceType" config="${RCConstants.PRICE_TYPE}"/></td>
                            <td><semui:xEditable owner="${somePrice}" field="price"/></td>
                            <td><semui:xEditableRefData owner="${somePrice}" field="currency" config="${RCConstants.CURRENCY}"/></td>
                        %{--    <td><semui:xEditable owner="${somePrice}" field="startDate" type="date"/></td>
                            <td><semui:xEditable owner="${somePrice}" field="endDate" type="date"/></td>--}%
                        <g:if test="${editable}">
                            <td>
                                    <g:link controller="ajaxSupport" class="confirm-click"
                                            data-confirm-message="Are you sure you wish to delete this Price?"
                                            action="deletePrice" params="[id: somePrice.id, fragment: 'prices']">Delete</g:link>

                            </td>
                        </g:if>
                        </tr>
                    </g:each>
                    </tbody>
                </table>

                <g:if test="${editable}">
                        <a data-toggle="modal" data-cache="false"
                           data-target="#pricesModal">Add Price</a>

                        <semui:modal id="pricesModal" title="Add Price">

                            <g:form controller="ajaxSupport" action="addToCollection" params="[fragment: 'prices']"
                                    class="form-inline">
                                <input type="hidden" name="__context"
                                       value="${d.class.name}:${d.id}"/>
                                <input type="hidden" name="__newObjectClass"
                                       value="org.gokb.cred.ComponentPrice"/>
                                <input type="hidden" name="__recip" value="owner"/>
                                <dt class="dt-label">Price Type</dt>
                                <dd>
                                    <semui:simpleReferenceTypedown class="form-control"
                                                                  name="priceType"
                                                                  baseClass="org.gokb.cred.RefdataValue"
                                                                  filter1="${RCConstants.PRICE_TYPE}"/>
                                </dd>
                                <dt class="dt-label">Price</dt>
                                <dd>
                                    <input type="number" class="form-control select-m" name="price" step="0.01"/>
                                </dd>
                                <dt class="dt-label">Currency</dt>
                                <dd>
                                    <semui:simpleReferenceTypedown class="form-control" name="currency"
                                                                  baseClass="org.gokb.cred.RefdataValue"
                                                                  filter1="${RCConstants.CURRENCY}"/>
                                </dd>
                            </g:form>
                        </semui:modal>
                </g:if>
            </dd>
        </dl>
    </g:if>
    <g:else>
        Prices can be added after the creation process is finished.
    </g:else>
</semui:tabsItemContent>
