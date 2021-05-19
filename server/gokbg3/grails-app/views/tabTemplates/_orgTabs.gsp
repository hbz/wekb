<%@ page import="de.wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <div id="content">

        <g:if test="${controllerName == 'public'}">

            <ul id="tabs" class="nav nav-tabs" role="tablist">

                <li class="nav-item active">
                    <a class="nav-link" href="#identifiers" data-toggle="tab" role="tab">Identifiers <span
                            class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('ids', 'Active').size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#altnames" data-toggle="tab" role="tab">
                        Alternate Names
                        <span class="badge badge-warning">${d.variantNames.size()}</span>
                    </a>
                </li>
                %{--<li class="nav-item"><a class="nav-link" href="#relationships" data-toggle="tab" role="tab">Relations</a></li>--}%
                <li class="nav-item">
                    <a class="nav-link" href="#packages" data-toggle="tab" role="tab">Packages
                        <span class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('providedPackages', 'Active').size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#titles" data-toggle="tab" role="tab">Published Titles
                        <span class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('publishedTitles', 'Active').size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#platforms" data-toggle="tab" role="tab">Platforms
                        <span class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('providedPlatforms', 'Active').size()}</span>
                    </a>
                </li>
               %{-- <li class="nav-item">
                    <a class="nav-link" href="#addprops" data-toggle="tab" role="tab">
                        Additional Properties
                        <span class="badge badge-warning">${d.additionalProperties.size()}</span>
                    </a>
                </li>--}%
                <li class="nav-item">
                    <a class="nav-link" href="#review" data-toggle="tab" role="tab">
                        Review Tasks (Open/Total)
                        <span class="badge badge-warning">${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open') }.size()}/${d.reviewRequests.size()}</span>
                    </a>
                </li>
                %{--<li class="nav-item">
                    <a class="nav-link" href="#offices" data-toggle="tab" role="tab">
                        Offices
                        <span class="badge badge-warning"> ${d.offices.size()}</span>
                    </a>
                </li>--}%
            </ul>

        </g:if>
        <g:else>
            <ul id="tabs" class="nav nav-tabs">
                <li class="active">
                    <a href="#identifiers" data-toggle="tab">Identifiers <span
                            class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('ids', 'Active').size()}</span>
                    </a>
                </li>
                <li>
                    <a href="#altnames" data-toggle="tab">
                        Alternate Names
                        <span class="badge badge-warning">${d.variantNames.size()}</span>
                    </a>
                </li>
                %{--<li><a href="#relationships" data-toggle="tab">Relations</a></li>--}%
                <li>
                    <a href="#packages" data-toggle="tab">Packages
                        <span class="badge badge-warning">${d.providedPackages.size()}</span>
                    </a>
                </li>
                <li>
                    <a href="#titles" data-toggle="tab">Titles
                        <span class="badge badge-warning">${d.getCurrentTippCount()}</span>
                    </a>
                </li>
                <li>
                    <a href="#platforms" data-toggle="tab">Platforms
                        <span class="badge badge-warning">${d.getCombosByPropertyNameAndStatus('providedPlatforms', 'Active').size()}</span>
                    </a>
                </li>
                %{--<li>
                    <a href="#addprops" data-toggle="tab">
                        Additional Properties
                        <span class="badge badge-warning">${d.additionalProperties.size()}</span>
                    </a>
                </li>--}%
                <li>
                    <a href="#review" data-toggle="tab">
                        Review Tasks (Open/Total)
                        <span class="badge badge-warning">${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open') }.size()}/${d.reviewRequests.size()}</span>
                    </a>
                </li>
                %{-- <li>
                     <a href="#offices" data-toggle="tab">
                         Offices
                         <span class="badge badge-warning"> ${d.offices.size()}</span>
                     </a>
                 </li>--}%
            </ul>

        </g:else>


        <div id="my-tab-content" class="tab-content">

            <g:render template="/tabTemplates/showIdentifiers" model="${[d: d]}"/>

            <g:render template="/tabTemplates/showVariantnames" model="${[d: d, showActions: true]}"/>

            %{--       <div class="tab-pane" id="relationships">
                           <dl class="dl-horizontal">
                               <dt>
                                   <gokb:annotatedLabel owner="${d}" property="successor">Successor</gokb:annotatedLabel>
                               </dt>
                               <dd>
                                   <gokb:manyToOneReferenceTypedown owner="${d}" field="successor" baseClass="org.gokb.cred.Org">${d.successor?.name}</gokb:manyToOneReferenceTypedown>
                               </dd>
                               <dt>
                                   <gokb:annotatedLabel owner="${d}" property="successor">Predecessor(s)</gokb:annotatedLabel>
                               </dt>
                               <dd>
                                   <ul>
                                       <g:each in="${d.previous}" var="c">
                                           <li>
                                               <g:link controller="resource" action="show" id="${c.getClassName()+':'+c.id}">
                                                   ${c.name}
                                               </g:link>
                                           </li>
                                       </g:each>
                                   </ul>
                               </dd>
                               <dt>
                                   <gokb:annotatedLabel owner="${d}" property="parent">Parent Org</gokb:annotatedLabel>
                               </dt>
                               <dd>
                                   <gokb:manyToOneReferenceTypedown owner="${d}" field="parent" baseClass="org.gokb.cred.Org">${d.parent?.name}</gokb:manyToOneReferenceTypedown>
                               </dd>

                               <g:if test="${d.children?.size() > 0}">
                                   <dt>
                                       <gokb:annotatedLabel owner="${d}" property="children">Subsidiaries</gokb:annotatedLabel>
                                   </dt>
                                   <dd>
                                       <ul>
                                           <g:each in="${d.children}" var="c">
                                               <li>
                                                   <g:link controller="resource" action="show" id="${c.getClassName()+':'+c.id}">
                                                       ${c.name}
                                                   </g:link>
                                               </li>
                                           </g:each>
                                       </ul>
                                   </dd>
                               </g:if>
                               <dt>
                                   <gokb:annotatedLabel owner="${d}" property="imprints">Imprints</gokb:annotatedLabel>
                               </dt>
                               <dd>
                                   <table class="table table-striped table-bordered">
                                       <thead>
                                       <tr>
                                           <th>Imprint Name</th>
                                           <th>Combo Status</th>
                                           <th>Imprint From</th>
                                           <th>Imprint To</th>
                                           <th>Actions</th>
                                       </tr>
                                       </thead>
                                       <tbody>
                                       <g:each in="${d.getCombosByPropertyName('ownedImprints')}" var="p">
                                           <tr>
                                               <td><g:link controller="resource" action="show" id="${p.toComponent.class.name}:${p.toComponent.id}"> ${p.toComponent.name} </g:link></td>
                                               <td><gokb:xEditableRefData owner="${p}" field="status" config="${RCConstants.COMBO_STATUS}" /></td>
                                               <td><gokb:xEditable  owner="${p}" field="startDate" type="date" /></td>
                                               <td><gokb:xEditable  owner="${p}" field="endDate" type="date" /></td>
                                               <td><g:link controller="ajaxSupport" action="deleteCombo" id="${p.id}">Delete</g:link></td>
                                           </tr>
                                       </g:each>
                                       </tbody>
                                   </table>
                               </dd>
                           </dl>
                   </div>--}%

           %{-- <div class="tab-pane" id="addprops" role="tabpanel">
                <g:render template="/apptemplates/secondTemplates/addprops"
                          model="${[d: d]}"/>
            </div>--}%

            <div class="tab-pane" id="review">
                <dl>
                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="reviewrequests">Review Requests</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d: d]}"/>
                    </dd>
                </dl>
            </div>

            %{-- <div class="tab-pane" id="offices">
                 <dl>
                     <dt>
                         <gokb:annotatedLabel owner="${d}" property="offices">Offices</gokb:annotatedLabel>
                     </dt>
                     <dd>
                         <g:render template="/apptemplates/secondTemplates/comboList"
                                   model="${[d:d, property:'offices', noadd:true, cols:[[expr:'name',colhead:'Office Name', action:'link']],targetClass:'org.gokb.cred.Office',direction:'in',propagateDelete: 'true']}" />

                         <g:if test="${editable}">
                             <g:if test="${d.id}">
                                 <button
                                         class="hidden-license-details btn btn-default btn-primary "
                                         data-toggle="collapse" data-target="#collapseableAddOffice">
                                     Add new <i class="fas fa-plus"></i>
                                 </button>
                                 <dl id="collapseableAddOffice" class="dl-horizontal collapse">
                                     <g:form controller="ajaxSupport" action="addToCollection"
                                             class="form-inline">
                                         <input type="hidden" name="__context" value="${d.class.name}:${d.id}" />
                                         <input type="hidden" name="__newObjectClass" value="org.gokb.cred.Office" />
                                         <input type="hidden" name="__addToColl" value="offices" />
                                         <dt class="dt-label">Office Name</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="name" required />
                                         </dd>
                                         <dt class="dt-label">Website</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="website" />
                                         </dd>
                                         <dt class="dt-label">Email</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="email" />
                                         </dd>
                                         <dt class="dt-label">Number</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="phoneNumber" />
                                         </dd>
                                         <dt class="dt-label">Address 1</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="addressLine1" />
                                         </dd>
                                         <dt class="dt-label">Address 2</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="addressLine2" />
                                         </dd>
                                         <dt class="dt-label">City</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="city" />
                                         </dd>
                                         <dt class="dt-label">Region</dt>
                                         <dd>
                                             <input class="form-control" type="text" name="region" />
                                         </dd>
                                         <dt class="dt-label">Country</dt>
                                         <dd>
                                             <gokb:simpleReferenceTypedown class="form-control" name="country"
                                                                           baseClass="org.gokb.cred.RefdataValue"
                                                                           filter1="${RCConstants.COUNTRY}" />
                                         </dd>
                                         <dt class="dt-label"></dt>
                                         <dd>
                                             <button type="submit" class="btn btn-default btn-primary">Add</button>
                                         </dd>
                                     </g:form>
                                 </dl>
                             </g:if>
                             <g:else>
                                 Offices can be added after the creation process is finished.
                             </g:else>
                         </g:if>
                     </dd>
                 </dl>
             </div>--}%

            <div class="tab-pane" id="platforms">
                <dl>
                    <dt>
                        <gokb:annotatedLabel owner="${d}" property="platforms">Platforms</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <g:link class="display-inline" controller="search" action="index"
                                params="[qbe: 'g:platforms', refOid: d.getLogEntityId(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id']]"
                                id="">Titles published</g:link>
                    </dd>
                </dl>
            </div>

            <div class="tab-pane" id="titles">
                <g:link class="display-inline" controller="search" action="index"
                        params="[qbe: 'g:tipps', refOid: d.getLogEntityId(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id']]"
                        id="">Titles published</g:link>
            </div>

            <div class="tab-pane" id="packages">
                <g:link class="display-inline" controller="search" action="index"
                        params="[qbe: 'g:packages', refOid: d.getLogEntityId(), inline: true, qp_provider_id: d.id, hide: ['qp_provider', 'qp_provider_id']]"
                        id="">Packages on this Platform</g:link>
            </div>

        </div>
    </div>
</g:if>