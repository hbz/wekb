<g:set var="perRow" value="${2}" />
<g:set var="fullRows" value="${(widgets.size() / perRow).toInteger() * perRow}" />
<g:set var="lastRow" value="${(widgets.size() % perRow).toInteger()}" />
<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="public_semui"/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Statistic</title>



  </head>
  <body>
    <h1 class="ui header">Welcome to <g:message code="gokb.appname" default="we:kb"/></h1>

    <h3 class="ui header">Statistic</h3>

    <g:if test="${params.status == '404'}">
      <div class="alert alert-danger">
        The page you requested does not exist!
      </div>
    </g:if>
%{--    <cache:block>
      <!-- Full rows -->
      <g:each var="name, widget" in="${widgets}" status="wcount" >
        <div class="col-md-${ (wcount + 1) <= fullRows ? (12 / perRow) : (12 / lastRow) }">
          <div class="panel panel-default">
            <div class="panel-heading">${name}</div>
            <!-- /.panel-heading -->
            <div class="panel-body">
            ${ gokb.chart(widget) }
            </div>
            <!-- /.panel-body -->
          </div>
          <!-- /.panel -->
        </div>
      </g:each>
    </cache:block>--}%

  <div id="chartMain" style="width: 100%; height: 500px;"></div>
  <g:javascript>
    var option;

    option = {
      xAxis: {
        type: 'category',
        data: ['lol', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          data: [150, 230, 224, 218, 135, 147, 260],
          type: 'line'
        }
      ]
    };

    var chartDom = document.getElementById('chartMain');
    var myChart2 = echarts.init(chartDom);

    option && myChart2.setOption(option);

  </g:javascript>

  </body>
</html>
