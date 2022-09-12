<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname"
                      default="we:kb"/>: Create New ${displayobj?.getNiceName() ?: 'Component'}</title>
</head>

<body>

<div id="msg" class="ui hidden error message">
</div>

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
                    <div id="formCreateProcess">
                        <g:render template="/templates/domains/${displaytemplate.rendername}"
                                  model="${[d: displayobj, rd: refdata_properties, dtype: displayobjclassname_short]}"/>

                        <button id="save-btn" class="ui black button" type="button">Create and Edit </button>
                    </div>
                </g:else>
            </g:if>
        </g:if>
    </div>
</div>

<g:javascript>
       $('#save-btn').click(function() {


        $('.xEditableValue, .xEditableManyToOne').editable('submit', {
               url: "${g.createLink(controller: 'create', action: 'process', params: [cls: params.tmpl])}",
               ajaxOptions: {
                   dataType: 'json' //assuming json response
               },
               success: function(data, config) {
                   if(data && data.newobj && data.newobj.id) {
                       var urlID = data.newobj.uuid ? data.newobj.uuid : data.objectClassName+':'+data.newobj.id;
                       window.location.href = "${g.createLink(controller: 'resource', action: 'show')}/"+urlID;
                   } else if(data && data.errors){
                       config.error.call(this, data.errors);
                   }
               },
               error: function(errors) {
                   var msg = '<i class="close icon"></i><div class="header">Creation failed</div><ul class="list">';
                   if(errors && errors.responseText) {
                        msg = errors.responseText;
                   } else {
                        $.each(errors, function(k, v) { msg += "<li>"+v+"</li><br>"; });
                        msg += '</ul>';
                   }
                    $('#msg').removeClass('hidden').addClass('visible').html(msg).show();
           }
       });
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

</g:javascript>
</body>
</html>
