<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="sb-admin"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Frontend</title>
</head>

<body>
<h1 class="page-header">Frontend Bootstrap 3 (closed we:kb area)</h1>
<h2 class="page-header">Tabulators</h2>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12">
            <div id="content">

                <h3>Bootstrap 3.3.7</h3>
                <!-- Nav tabs -->
                <div id="js-tabPanelWidget">
                    <bootStrap:tabs>
                        <li class="active"><a  href="#home" role="tab">Home</a></li>
                        <bootStrap:tabsItem href="#profile" controller="frontend" action="tabExample" text="Profile" />
                        <bootStrap:tabsItem href="#messages" controller="frontend" action="tabExample" text="Messages" />
                    </bootStrap:tabs>

                <!-- Tab panes -->
                    <div class="tab-content">
                        <div role="tabpanel" class="tab-pane fade in active" id="home">1</div>
                        <div role="tabpanel" class="tab-pane fade" id="profile">2</div>
                        <div role="tabpanel" class="tab-pane fade" id="messages">3</div>
                    </div>
                </div>

            </div>
        </div>

    </div>
</div>


<script>
  //AJAX for Tab Panel Widget
  $('#js-tabPanelWidget').on('click','#js-tabList a',function (e) {
    e.preventDefault();
    var url = $(this).attr("data-url");
    if (typeof url !== "undefined") {
      var pane = $(this),
          href = this.hash;
      // ajax load from data-url
      $(href).load(url,function(result){
        pane.tab('show');
      });
    } else {
      $(this).tab('show');
    }
  });
</script>


</body>
</html>
