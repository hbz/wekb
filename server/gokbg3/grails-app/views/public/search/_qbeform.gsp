<g:set var="s_action" value="${s_action ?: actionName}"/>
<g:set var="s_controller" value="${s_controller ?: controllerName}"/>


<g:if test="${params.inline}">
    <g:form method="get" class="form-group row justify-content-end" controller="${s_controller}" action="${s_action}"
            id="${params.id}">
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

        <div class="col-sm-12">
            <div class="form-group">
                <div class="col-sm-10">
                    <g:each in="${cfg.qbeGlobals}" var="glob">
                        <g:if test="${(glob.qparam) && (glob.prompt)}">
                            <span>
                                ${glob.prompt} : <b>${params[glob.qparam] ?: glob.default}</b>
                            </span>
                        </g:if>
                    </g:each>
                </div>

                <div class="col-sm-2">
                    <div class="float-right">
                        <button name="searchAction" type="submit" class="btn btn-primary"
                                value="search">Search View</button>
                    </div>
                </div>
            </div>
        </div>
    </g:form>
</g:if>
<g:else>
    <div class="card wekb-filter mb-4">
        <h4><b>Search Filter:</b></h4>
    <g:form method="get" class="form-group row justify-content-end" controller="${s_controller}"
            action="${s_action}"
            id="${params.id}">

        <input type="hidden" name="qbe" value="${params.qbe}"/>

        <g:if test="${refOid}">
            <input type="hidden" name="refOid" value="${refOid}"/>
        </g:if>

        <g:each in="${hide}" var="hidden_var">
            <input type="hidden" name="hide" value="${hidden_var}"/>
        </g:each>

        <g:each in="${formdefn}" var="fld" status="frmidx">
            <g:if test="${((hide?.contains(fld.qparam)) || (fld.hide == true)) || (fld.notShowInPublic == true)}">
                <input type="hidden" name="${fld.qparam}" id="${fld.qparam}" value="${params[fld.qparam]}"/>
            </g:if>
            <g:else>
                <div class="col-sm-6">
                    <div class="form-group row justify-content-end">
                        <label class="col-sm-3 col-form-label" for="${fld.qparam}">${fld.prompt}</label>

                        <div class="col-sm-9">
                            <g:if test="${fld.type == 'lookup'}">
                                <%
                                    def values
                                    def domain_class = grailsApplication.getArtefact('Domain', fld.baseClass)
                                    if (domain_class) {
                                        values = domain_class.getClazz().refdataFind([filter1: (fld.filter1 ?: ''), q: ''])
                                    }

                                %>
                                <g:select id="refdata_combo_${params.inline ? 'inline_' : ''}${fld.qparam}"
                                          from="${values}"
                                          class="form-control"
                                          name="${fld.qparam}"
                                          optionKey="${{ it.id }}"
                                          optionValue="${{ it.text }}"
                                          value="${params[fld.qparam]}"
                                          noSelection="${["": 'Please Choose']}"/>
                            </g:if>
                            <g:else>
                                <div class="${fld.contextTree.wildcard != null ? 'input-group' : ''}">
                                    <g:if test="${fld.contextTree.wildcard == 'B' || fld.contextTree.wildcard == 'L'}"><span
                                            class="input-group-text">*</span></g:if>
                                    <input class="form-control"
                                           type="${fld.contextTree.type == 'java.lang.Long' ? 'number' : 'text'}"
                                           name="${fld.qparam}" id="${fld.qparam}" placeholder="${fld.placeholder}"
                                           value="${params[fld.qparam]}"/>
                                    <g:if test="${fld.contextTree.wildcard == 'B' || fld.contextTree.wildcard == 'R'}"><span
                                            class="input-group-text">*</span></g:if>
                                </div>
                            </g:else>
                        </div>
                    </div>
                </div>
            </g:else>
        </g:each>

        <div class="col-sm-12">
            <div class="form-group">
                <div class="col-sm-10">
                    <g:each in="${cfg.qbeGlobals}" var="glob">
                        <g:if test="${(glob.qparam) && (glob.prompt)}">
                            <span>
                                ${glob.prompt} : <select class="form-control"
                                                         style="display:inline;max-width:75px"
                                                         name="${glob.qparam}" value="${params[glob.qparam]}">
                                <option value="on" ${(params[glob.qparam] ?: glob.default) == 'on' ? 'selected' : ''}>On</option>
                                <option value="off" ${(params[glob.qparam] ?: glob.default) == 'off' ? 'selected' : ''}>Off</option>
                            </select>
                            </span>
                        </g:if>
                    </g:each>
                </div>
            </div>

        </div>

        <div class="col-sm-2">
            <div class="btn-group pull-right" role="group" aria-label="Search Buttons">
                <g:link class="btn btn-primary" controller="${s_controller}" action="${s_action}"
                        params="[id: params.id, qbe: params.qbe]">Reset</g:link>
                <button name="searchAction" type="submit" class="btn btn-primary"
                        value="search">Search</button>
            </div>
        </div>
        </div>
    </g:form>
    </div>
</g:else>


