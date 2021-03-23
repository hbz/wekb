<div class="wekb-numberChartHero">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <g:each in="${componentsOfStatistic.sort{it}}" var="component">
                    <div class="col-md-2 text-center">
                        <div class="mekb-title text-uppercase">
                           <g:message code="public.index.component.${component.toLowerCase()}"/>
                        </div>
                        <div class="mekb-bigFont">
                            <g:formatNumber number="${countComponent."${component}"}" type="number" format="###.###"/> </div>
                    </div>
                </g:each>
            </div>
        </div>
    </div>
</div>