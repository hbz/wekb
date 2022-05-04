<!DOCTYPE html>
<html>
<head>
<meta name="layout" content="sb-admin" />
<title><g:message code="gokb.appname" default="we:kb"/>: Create New ${displayobj?.getNiceName() ?: 'Component'}</title>
</head>
<body>
  <h1 class="page-header">
          Create New ${displayobj?.getNiceName() ?: 'Component'}

    <g:if test="${displayobj instanceof org.gokb.cred.Package}">
      <g:link controller="create" action="packageBatch" class="btn btn-default pull-right btn-sm">Upload Packages</g:link>
    </g:if>
  </h1>
  <div id="mainarea" class="panel panel-default">
    <div class="panel-body">
      <g:if test="${displaytemplate != null}">
        <g:if test="${displaytemplate.type=='staticgsp'}">
          <g:if test="${displaytemplate.noCreate}">
            <div id="content">
              <div style="padding:20px">
                <span class="alert alert-danger" style="font-weight:bold;">Components of this type cannot be created in a standalone context.</span>
              </div>
            </div>
          </g:if>
          <g:else>
            <g:form name="formCreateProcess" controller="create" action="process" params="[cls:params.tmpl]">
                <g:render template="/apptemplates/secondTemplates/messages"/>
                <g:render template="/apptemplates/mainTemplates/${displaytemplate.rendername}"
                          model="${[d: displayobj, rd: refdata_properties, dtype: displayobjclassname_short]}"/>
                <button id="save-btn" class="btn btn-default pull-right btn-sm">Create and Edit &gt;&gt;</button>
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

             $('form[name="formCreateProcess"]').append('<input type="hidden" name="'+editable.attr('data-name')+'" value="'+(eVal ? eVal : editable.text())+'" />');

        });

        $('a.editable').not('.editable-empty').each (function(){
            var editable = $(this);

            $('form[name="formCreateProcess"]').append('<input type="hidden" name="'+editable.attr('data-name')+'" value="'+editable.attr('target-id')+'" />');

        })

      	$('form[name="formCreateProcess"]').submit();
      });

      var hash = window.location.hash;
      hash && $('ul.nav a[href="' + hash + '"]').tab('show');

      $('.nav-tabs > li > a').not('.disabled').click(function (e) {
        $(this).tab('show');
        var scrollmem = $('body').scrollTop();
        console.log("scrollTop");
        window.location.hash = this.hash;
        $('html,body').scrollTop(scrollmem);
      });

    </asset:script>
</body>
</html>
