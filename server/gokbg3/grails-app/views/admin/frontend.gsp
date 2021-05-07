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
                <ul id="tabs" class="nav nav-tabs">
                    <li class="active"><a href="https://www.spiegel.de" data-toggle="tab" aria-expanded="false" >Tab 1</a></li>
                    <li class=""><a href="https://www.express.de" data-toggle="tab" aria-expanded="false" >Tab 2</a></li>
                    <li class=""><a href="http://localhost:8080/gokb/admin/frontend" data-toggle="tab" aria-expanded="false">Tab 3</a></li>
                </ul>

                <div id="my-tab-content" class="tab-content" >
                    <div class="tab-pane active" id="panel1"  role="tabpanel">
                        Panel 1
                    </div>
                    <div class="tab-pane" id="panel2" role="tabpanel">
                        Panel 2
                    </div>
                    <div class="tab-pane fade" id="panel3" role="tabpanel">
                        Panel 3
                    </div>
                </div>
            </div>
        </div>


    </div>
</div>
<script>


</script>


</body>
</html>
