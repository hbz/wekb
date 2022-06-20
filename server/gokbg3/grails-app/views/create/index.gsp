<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname"
                      default="we:kb"/>: Create New ${displayobj?.getNiceName() ?: 'Component'}</title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<h1 class="ui header">
    Create new ${displayobj?.getNiceName() ?: 'Component'}

    <g:if test="${displayobj instanceof org.gokb.cred.Package}">
        <div class="ui right floated buttons">
            <g:link controller="create" action="packageBatch" class="ui black button">Upload Packages</g:link>
        </div>
    </g:if>
</h1>

<div class="ui segment">
    <div class="content wekb-inline-lists">
        <g:if test="${displaytemplate != null}">
            <!-- Using display template ${displaytemplate.rendername} -->
            <g:if test="${displaytemplate.type == 'staticgsp'}">
                <g:if test="${displaytemplate.noCreate}">
                    <div id="content">
                        <div style="padding:20px">
                            <span class="alert alert-danger"
                                  style="font-weight:bold;">Components of this type cannot be created in a standalone context.</span>
                        </div>
                    </div>
                </g:if>
                <g:else>
                    <g:form name="formCreateProcess" controller="create" action="process" params="[cls: params.tmpl]">
                        <g:render template="/templates/domains/${displaytemplate.rendername}"
                                  model="${[d: displayobj, rd: refdata_properties, dtype: displayobjclassname_short]}"/>

                        <button id="save-btn" class="ui black button">Create and Edit </button>
                    </g:form>
                </g:else>
            </g:if>
        </g:if>
    </div>
</div>

<asset:script type="text/javascript">

    $('#save-btn').click(function() {
      $('span.editable').not('.editable-empty').each (function(){
          var editable = $(this);

          // Add the parameter to the params object.
          var eVal = editable.editable('getValue', true);

           $('form[name="formCreateProcess"]').append('<input type="hidden" name="'+editable.attr('data-name')+'" value="'+(eVal ? eVal : editable.text())+'"/>');

        });

        $('a.editable').not('.editable-empty').each (function(){
            var editable = $(this);

            $('form[name="formCreateProcess"]').append('<input type="hidden" name="'+editable.attr('data-name')+'" value="'+editable.attr('target-id')+'"/>');

        })

      	$('form[name="formCreateProcess"]').submit();
      });

/*      var hash = window.location.hash;
      hash && $('ul.nav a[href="' + hash + '"]').tab('show');

      $('.nav-tabs > li > a').not('.disabled').click(function (e) {
        $(this).tab('show');
        var scrollmem = $('body').scrollTop();
        console.log("scrollTop");
        window.location.hash = this.hash;
        $('html,body').scrollTop(scrollmem);
      });*/

</asset:script>
</body>
</html>
