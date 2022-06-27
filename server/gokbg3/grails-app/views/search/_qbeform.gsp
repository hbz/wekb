%{--<g:set var="s_action" value="${s_action ?: 'componentSearch'}"/>
<g:set var="s_controller" value="${s_controller ?: 'search'}"/>--}%

<g:if test="${hide.contains('SEARCH_FORM')}">
</g:if>
<g:elseif test="${params.inline}">
    <g:form method="get" class="ui form" controller="${controllerName}" action="${actionName}" id="${params.id}">
        <input type="hidden" name="qbe" value="${params.qbe}"/>

        <g:each in="${hide}" var="hidden_var">
            <input type="hidden" name="hide" value="${hidden_var}"/>
        </g:each>

        <g:if test="${refOid}">
            <input type="hidden" name="refOid" value="${refOid}"/>
        </g:if>

        <g:each in="${formdefn}" var="fld">
            <g:if test="${((hide?.contains(fld.qparam)) || (fld.hide == true))}">
                <input type="hidden" name="${fld.qparam}" id="${fld.qparam}" value="${params[fld.qparam]}"/>
            </g:if>
        </g:each>

        <div class="ui right floated buttons">
            <button class="ui button black" type="submit" value="search"
                    name="searchAction">Search View</button>
        </div>
    </g:form>
</g:elseif>
<g:else>

    <% Map advancedSearchMap = [:] %>
    <div class="ui segment">
        <h1 class="ui header">Filter</h1>
    <g:form method="get" class="ui form" controller="${controllerName}" action="${actionName}" id="${params.id}">

        <input type="hidden" name="qbe" value="${params.qbe}"/>

        <g:if test="${refOid}">
            <input type="hidden" name="refOid" value="${refOid}"/>
        </g:if>

        <g:each in="${hide}" var="hidden_var">
            <input type="hidden" name="hide" value="${hidden_var}"/>
        </g:each>
        <div class="two fields">
        <g:each in="${formdefn}" var="fld" status="frmidx">
            <g:if test="${((hide?.contains(fld.qparam)) || (fld.hide == true))}">
                <input type="hidden" name="${fld.qparam}" id="${fld.qparam}" value="${params[fld.qparam]}"/>
            </g:if>
            <g:elseif test="${fld.advancedSearch}">
                <%
                    if (!advancedSearchMap."${fld.advancedSearch.category}") {
                        advancedSearchMap."${fld.advancedSearch.category}" = [title: fld.advancedSearch.title, formFields: []]
                    }
                    advancedSearchMap."${fld.advancedSearch.category}".formFields << fld
                %>
            </g:elseif>
            <g:else>

                <div class="field">
                    <label for="${fld.qparam}">${fld.prompt}</label>
                    <g:if test="${fld.type == 'lookup'}">
                        <div class="ui field">
                            <semui:simpleReferenceDropdown
                                    id="refdata_combo_${params.inline ? 'inline_' : ''}${fld.qparam}"
                                    name="${fld.qparam}"
                                    baseClass="${fld.baseClass}"
                                    filter1="${fld.filter1 ?: ''}"
                                    value="${params[fld.qparam]}"/>
                        </div>
                    </g:if>
                    <g:else>
                        <div class="${fld.contextTree.wildcard != null ? 'ui labeled input' : ''}">
                            <g:if test="${fld.contextTree.wildcard == 'B' || fld.contextTree.wildcard == 'L'}"><div
                                    class="ui label">*</div></g:if>
                            <input type="${fld.contextTree.type == 'java.lang.Long' ? 'number' : 'text'}"
                                   name="${fld.qparam}" id="${fld.qparam}" placeholder="${fld.placeholder}"
                                   value="${params[fld.qparam]}"/>
                            <g:if test="${fld.contextTree.wildcard == 'B' || fld.contextTree.wildcard == 'R'}"><div
                                    class="ui label">*</div></g:if>
                        </div>
                    </g:else>

                </div>

                <g:if test="${(frmidx + 1) % 2 == 0}">
                    </div>
                    <div class="two fields">
                </g:if>

            </g:else>
        </g:each>
        </div>

        <g:each in="${advancedSearchMap}" var="advancedSearch" status="fieldIndex">
            <div class="ui accordion field">
                <div class="title">
                    <i class="icon dropdown"></i>
                ${advancedSearch.value.title}
            </div>

            <div class="content">
            <div class="two fields">
                <g:each in="${advancedSearch.value.formFields}" var="field" status="formFieldsIndex">
                    <div class=" field">
                        <label for="${field.qparam}">${field.prompt}</label>
                        <g:if test="${field.type == 'lookup'}">
                            <div class="ui field">
                                <semui:simpleReferenceDropdown
                                        id="refdata_combo_${params.inline ? 'inline_' : ''}${field.qparam}"
                                        name="${field.qparam}"
                                        baseClass="${field.baseClass}"
                                        filter1="${field.filter1 ?: ''}"
                                        value="${params[field.qparam]}"/>
                            </div>
                        </g:if>
                        <g:else>
                            <div class="${field.contextTree.wildcard != null ? 'ui labeled input' : ''}">
                                <g:if test="${field.contextTree.wildcard == 'B' || field.contextTree.wildcard == 'L'}"><div
                                        class="ui label">*</div></g:if>
                                <input type="${field.contextTree.type == 'java.lang.Long' ? 'number' : 'text'}"
                                       name="${field.qparam}" id="${field.qparam}" placeholder="${field.placeholder}"
                                       value="${params[field.qparam]}"/>
                                <g:if test="${field.contextTree.wildcard == 'B' || field.contextTree.wildcard == 'R'}"><div
                                        class="ui label">*</div></g:if>
                            </div>
                        </g:else>

                    </div>

                    <g:if test="${(formFieldsIndex + 1) % 2 == 0}">
                        </div>
                        <div class="two fields">
                    </g:if>
                </g:each>
            </div>
            </div>
            </div>
        </g:each>



        <g:if test="${hide.contains('SEARCH_BUTTONS')}">
        </g:if>
        <g:else>
            <div class="ui right floated buttons">
                <g:link class="ui button" controller="${controllerName}" action="${actionName}"
                        params="[id: params.id, qbe: params.qbe]">Reset</g:link>
                <button class="ui button black" type="submit" value="Search"
                        name="searchAction">Search</button>

                <sec:ifLoggedIn>
                    <div class="ui icon dropdown button">
                        <div class="text">Save</div>
                        <i class="dropdown icon"></i>

                        <div class="menu">
                            <div class="header">
                                Save as:
                            </div>

                            <div class="ui left input">
                                <input type="text" name="searchName"
                                       placeholder="Search Name">
                            </div>

                            <div class="item">
                                <input class="ui button black" type="submit" name="searchAction"
                                       value="Save"/>
                            </div>
                        </div>
                    </div>
                </sec:ifLoggedIn>

            </div>
            <br>
            <br>
        </g:else>
    </g:form>
    </div>
</g:else>


