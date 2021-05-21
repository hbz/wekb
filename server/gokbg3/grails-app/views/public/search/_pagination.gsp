<g:if test="${recset != null}">
    <g:set var="s_action" value="${s_action ?: actionName}"/>
    <g:set var="s_controller" value="${s_controller ?: controllerName}"/>
    <g:set var="jumpToPage" value="${jumpToPage ?: 'jumpToPage'}"/>
    <g:set var="custom_offset" value="${offset_param ?: 'offset'}"/>

%{--nav selector wichtig für inline sachen--}%
    <div class="pagination mb-4 d-flex justify-content-center nav">
        <g:if test="${page == 1}">
            <a class='disabled' href='#'><i
                    class="fas fa-chevron-left"></i></a>
        </g:if>
        <g:else>
            <g:link title="Previous Page" controller="${s_controller}"
                    action="${s_action}"
                    params="${params + ["${custom_offset}": (offset.toInteger() - max.toInteger()), det: null]}">
                <i class="fas fa-chevron-left"></i>
            </g:link>
        </g:else>

        <g:if test="${!request.isAjax()}">
            <span class="navbar-text"><g:form
                    controller="${s_controller}" action="${s_action}" params="${withoutJump}"
                    method="post">Page <input type="text" class="" name="${jumpToPage}"
                                              size="5" value="${page}"
                                              style="color:#000000;"/> of ${page_max}</g:form></span>
        </g:if>

        <g:if test="${page == page_max}">
            <a href='#'>
                <i class="fas fa-chevron-right"></i></a>
        </g:if>
        <g:else>
            <g:link title="Next Page" controller="${s_controller}"
                    action="${s_action}"
                    params="${params + ["${custom_offset}": (offset.toInteger() + max.toInteger()), det: null]}">
                <i class="fas fa-chevron-right"></i>
            </g:link>
        </g:else>
    </div>
</g:if>
