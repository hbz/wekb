
<g:render template="/tabTemplates/showCoverages" model="${[d: d]}"/>

<g:render template="/tabTemplates/showIdentifiers" model="${[d: d]}"/>

<div class="tab-pane" id="addprops" role="tabpanel">
    <g:render template="/apptemplates/secondTemplates/addprops"
              model="${[d: d]}"/>
</div>

<g:render template="/tabTemplates/showSubjectArea" model="${[d: d]}"/>

<g:render template="/tabTemplates/showDDCs" model="${[d: d]}"/>

<g:render template="/tabTemplates/showSeries" model="${[d: d]}"/>

<g:render template="/tabTemplates/showOpenAccess" model="${[d: d]}"/>

<g:render template="/tabTemplates/showPrices" model="${[d: d]}"/>

<g:if test="${controllerName != 'public'}">
    <div class="tab-pane" id="review" role="tabpanel">
        <g:render template="/apptemplates/secondTemplates/revreqtab" model="${[d: d]}"/>
    </div>
</g:if>