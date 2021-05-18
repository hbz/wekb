<%@ page import="de.wekb.helper.RCConstants" %>
<g:if test="${d.id}">
    <div id="content">

        <g:if test="${controllerName == 'public'}">

            <ul id="tabs" class="nav nav-tabs" role="tablist">

                <li class="nav-item active">
                    <a class="nav-link" href="#identifiers" data-toggle="tab" role="tab">Identifiers <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('ids','Active').size()} </span></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#altnames" data-toggle="tab" role="tab">
                        Alternate Names
                        <span class="badge badge-warning"> ${d.variantNames.size()}</span>
                    </a>
                </li>
                <li class="nav-item"><a class="nav-link" href="#relationships" data-toggle="tab" role="tab">Relations</a></li>
                <li class="nav-item">
                    <a class="nav-link" href="#packages" data-toggle="tab" role="tab">Packages
                        <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('providedPackages','Active').size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#titles" data-toggle="tab" role="tab">Published Titles
                        <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('publishedTitles','Active').size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#platforms" data-toggle="tab" role="tab">Platforms
                        <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('providedPlatforms','Active').size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#addprops" data-toggle="tab" role="tab">
                        Custom Fields
                        <span class="badge badge-warning"> ${d.additionalProperties.size()}</span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#review" data-toggle="tab" role="tab">
                        Review Tasks (Open/Total)
                        <span class="badge badge-warning"> ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS,'Open') }.size()}/${d.reviewRequests.size()} </span>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#offices" data-toggle="tab" role="tab">
                        Offices
                        <span class="badge badge-warning"> ${d.offices.size()}</span>
                    </a>
                </li>
            </ul>

        </g:if>
        <g:else>
            <ul id="tabs" class="nav nav-tabs">
                    <li class="active">
                        <a href="#identifiers" data-toggle="tab">Identifiers <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('ids','Active').size()} </span></a>
                    </li>
                    <li>
                        <a href="#altnames" data-toggle="tab">
                            Alternate Names
                            <span class="badge badge-warning"> ${d.variantNames.size()}</span>
                        </a>
                    </li>
                    <li><a href="#relationships" data-toggle="tab">Relations</a></li>
                    <li>
                        <a href="#packages" data-toggle="tab">Packages
                            <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('providedPackages','Active').size()}</span>
                        </a>
                    </li>
                    <li>
                        <a href="#titles" data-toggle="tab">Published Titles
                            <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('publishedTitles','Active').size()}</span>
                        </a>
                    </li>
                    <li>
                        <a href="#platforms" data-toggle="tab">Platforms
                            <span class="badge badge-warning"> ${d.getCombosByPropertyNameAndStatus('providedPlatforms','Active').size()}</span>
                        </a>
                    </li>
                    <li>
                        <a href="#addprops" data-toggle="tab">
                            Custom Fields
                            <span class="badge badge-warning"> ${d.additionalProperties.size()}</span>
                        </a>
                    </li>
                    <li>
                        <a href="#review" data-toggle="tab">
                            Review Tasks (Open/Total)
                            <span class="badge badge-warning"> ${d.reviewRequests?.findAll { it.status == org.gokb.cred.RefdataCategory.lookup(RCConstants.REVIEW_REQUEST_STATUS,'Open') }.size()}/${d.reviewRequests.size()} </span>
                        </a>
                    </li>
                    <li>
                        <a href="#offices" data-toggle="tab">
                            Offices
                            <span class="badge badge-warning"> ${d.offices.size()}</span>
                        </a>
                    </li>
            </ul>

        </g:else>


        <div id="my-tab-content" class="tab-content">


        </div>
    </div>
</g:if>