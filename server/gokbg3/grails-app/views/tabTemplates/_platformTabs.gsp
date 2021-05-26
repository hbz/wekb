<%@ page import="de.wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <div id="content">

        <g:if test="${controllerName == 'public'}">
            <ul id="tabs" class="nav nav-tabs" role="tablist">

                <li class="nav-item show active"><a class="nav-link" href="#statistic" data-toggle="tab">Statistic</a></li>

                <li class="nav-item"><a class="nav-link" href="#titledetails" data-toggle="tab"
                                               role="tab">Hosted Titles <span
                            class="badge badge-warning">${d.currentTippCount}</span></a></li>
                <li class="nav-item"><a class="nav-link" href="#packages" data-toggle="tab" role="tab">Packages <span
                        class="badge badge-warning">${d.hostedPackages.size()}</span></a></li>
                <li class="nav-item"><a class="nav-link" href="#altnames" data-toggle="tab" role="tab">Alternate Names <span
                        class="badge badge-warning">${d.variantNames.size()}</span></a></li>



            </ul>

        </g:if>
        <g:else>
            <ul id="tabs" class="nav nav-tabs">
                <li class="active"><a href="#statistic" data-toggle="tab">Statistic</a></li>
                <li><a href="#titledetails" data-toggle="tab">Hosted Titles <span
                        class="badge badge-warning">${d.currentTippCount}</span></a></li>
                <li><a href="#packages" data-toggle="tab">Packages <span
                        class="badge badge-warning">${d.hostedPackages.size()}</span></a></li>
                <li><a href="#altnames" data-toggle="tab">Alternate Names <span
                        class="badge badge-warning">${d.variantNames.size()}</span></a></li>

                <g:if test="${grailsApplication.config.gokb.decisionSupport?.active}">

                    <li><a href="#ds" data-toggle="tab">Decision Support</a></li>
                </g:if>

                <li><a href="#review" data-toggle="tab">Review Tasks (Open/Total)<span
                        class="badge badge-warning">
                    ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS, 'Open') }.size()}/${d.reviewRequests.size()}

                </span></a></li>
            </ul>

        </g:else>


        <div id="my-tab-content" class="tab-content">

            <div class="tab-pane fade" id="titledetails" role="tabpanel">

                <g:if test="${controllerName == 'public'}">
                    <g:link class="display-inline" controller="public" action="search"
                            params="[qbe: 'g:tipps', qp_plat_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_plat', 'qp_plat_id']]"
                            id="">Titles on this Platform</g:link>
                </g:if>
                <g:else>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:tipps', qp_plat_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_plat', 'qp_plat_id']]"
                            id="">Titles on this Platform</g:link>
                </g:else>

            </div>

            <div class="tab-pane fade" id="packages" role="tabpanel">

                <g:if test="${controllerName == 'public'}">
                    <g:link class="display-inline" controller="public" action="search"
                            params="[qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_platform', 'qp_platform_id']]"
                            id="">Packages on this Platform</g:link>
                </g:if>
                <g:else>
                    <g:link class="display-inline" controller="search" action="index"
                            params="[qbe: 'g:packages', qp_platform_id: d.id, inline: true, refOid: d.getLogEntityId(), hide: ['qp_platform', 'qp_platform_id']]"
                            id="">Packages on this Platform</g:link>
                </g:else>
            </div>

            <g:render template="/tabTemplates/showVariantnames"
                      model="${[d: d, showActions: true]}"/>


            <div class="tab-pane show active" id="statistic" role="tabpanel">
                <dl>
                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="statisticsFormat">Statistics Format</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="statisticsFormat"
                                               config="${RCConstants.PLATFORM_STATISTICS_FORMAT}"/>
                    </dd>
                    
                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="statisticsUpdate">Statistics Update</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="statisticsUpdate"
                                               config="${RCConstants.PLATFORM_STATISTICS_UPDATE}"/>
                    </dd>
                    
                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="statisticsAdminPortalUrl">Statistics Admin Portal Url</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditable owner="${d}" field="statisticsAdminPortalUrl"/>
                    </dd>
                    
                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterCertified">Counter Certified</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="counterCertified" config="${RCConstants.YN}"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterRegistryUrl">Counter Registry Url</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditable owner="${d}" field="counterRegistryUrl"/>
                    </dd>


                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR3Supported">Counter R3 Supported</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="counterR3Supported" config="${RCConstants.YN}"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR4Supported">Counter R4 Supported</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="counterR4Supported" config="${RCConstants.YN}"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR5Supported">Counter R5 Supported</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="counterR5Supported" config="${RCConstants.YN}"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR4SushiApiSupported">Counter R4 Sushi Api Supported</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="counterR4SushiApiSupported"
                                               config="${RCConstants.YN}"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR5SushiApiSupported">Counter R5 Sushi Api Supported</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditableRefData owner="${d}" field="counterR5SushiApiSupported"
                                               config="${RCConstants.YN}"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR4SushiServerUrl">Counter R4 Sushi Server Url</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditable owner="${d}" field="counterR4SushiServerUrl"/>
                    </dd>

                    <dt>
                        <gokb:annotatedLabel owner="${d}"
                                             property="counterR5SushiServerUrl">Counter R5 Sushi Server Url</gokb:annotatedLabel>
                    </dt>
                    <dd>
                        <gokb:xEditable owner="${d}" field="counterR5SushiServerUrl"/>
                    </dd>

                </dl>
            </div>

            <g:if test="${controllerName != 'public'}">
                <div class="tab-pane fade" id="ds" role="tabpanel">
                    <g:render template="/apptemplates/secondTemplates/dstab" model="${[d: d]}"/>
                </div>

                <div class="tab-pane fade" id="review" role="tabpanel">
                    <g:render template="/apptemplates/secondTemplates/revreqtab"
                              model="${[d: d]}"/>
                </div>
            </g:if>

        </div>
    </div>
</g:if>
