<div class="ui segment inverted">
<div class="ui five inverted statistics">
    <g:each in="${componentsOfStatistic.sort { it }}" var="component">
        <div class="statistic" style="!important; min-width: 18%;">
            <div class="value">
                <g:formatNumber number="${countComponent."${component.toLowerCase()}"}" type="number"
                                format="###.###"/></div>
            <div class="label">
                <g:message code="public.index.component.${component.toLowerCase()}"/>
            </div>
        </div>
    </g:each>
</div>
</div>
