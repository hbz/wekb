
<div class="wekb-numberChartHero">
    <div class="container">
        <div class="row ten-columns">
            <g:each in="${componentsOfStatistic.sort{it}}" var="component">
                <%-- <g:if test="${component != 'Platform'}">Platform</g:if--%>
                <div class="col-sm-2 text-center">
                    <div class="mekb-title text-uppercase">
                       <g:message code="public.index.component.${component.toLowerCase()}"/>
                    </div>
                    <div class="mekb-bigFont">
                        <g:formatNumber number="${countComponent."${component.toLowerCase()}"}" type="number" format="###.###"/> </div>
                </div>
            </g:each>
        </div>
    </div>
</div>