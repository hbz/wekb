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
                    <ul  class="nav nav-tabs"  id="js-tabList" role="tablist">
                        <li class="active"><a  href="#home" role="tab">Home</a></li>
                        <li><a href="#profile" data-url="${createLink(controller:'home',action:'index')}" role="tab">Profile</a></li>
                        <li><a href="#messages" data-url="${createLink(controller:'home',action:'userdash')}" role="tab">Messages</a></li>
                    </ul>

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
      console.log(href)
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
