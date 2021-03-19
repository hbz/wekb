<div class="wekb-numberChartHero">
    <div class="container">
        <div class="row">
            <div class="col-md-12">
                <g:each in="${componentsOfStatistic}" var="component">
                    <div class="col-md-2 text-center"">
                        <div class="mekb-title text-uppercase">${component}</div>
                        <div class="mekb-bigFont">${countComponent."${component}"}</div>
                    </div>
                </g:each>
            </div>
        </div>
    </div>
</div>