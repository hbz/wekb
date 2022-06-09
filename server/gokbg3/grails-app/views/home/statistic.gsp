<g:set var="perRow" value="${2}" />
<g:set var="fullRows" value="${(widgets.size() / perRow).toInteger() * perRow}" />
<g:set var="lastRow" value="${(widgets.size() % perRow).toInteger()}" />
<!DOCTYPE html>
<html>
  <head>
    <meta name='layout' content='public_semui'/>
    <title><g:message code="gokb.appname" default="we:kb"/>: Statistic</title>

  </head>
  <body>
    <h1 class="page-header">Welcome to <g:message code="gokb.appname" default="we:kb"/></h1>
    <g:if test="${params.status == '404'}">
      <div class="alert alert-danger">
        The page you requested does not exist!
      </div>
    </g:if>
    <cache:block>
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

      <div id="main" class="echarts-wrapper"></div>
      <g:javascript>
        var chartDom = document.getElementById('main');
        var myChart = echarts.init(chartDom);
        var option;

        option = {
          title: {
            text: 'Stacked Line'
          },
          tooltip: {
            trigger: 'axis'
          },
          legend: {
            data: ['Email', 'Union Ads', 'Video Ads', 'Direct', 'Search Engine']
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          toolbox: {
            feature: {
              saveAsImage: {}
            }
          },
          xAxis: {
            type: 'category',
            boundaryGap: false,
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
          },
          yAxis: {
            type: 'value'
          },
          series: [
            {
              name: 'Email',
              type: 'line',
              stack: 'Total',
              data: [120, 132, 101, 134, 90, 230, 210]
            },
            {
              name: 'Union Ads',
              type: 'line',
              stack: 'Total',
              data: [220, 182, 191, 234, 290, 330, 310]
            },
            {
              name: 'Video Ads',
              type: 'line',
              stack: 'Total',
              data: [150, 232, 201, 154, 190, 330, 410]
            },
            {
              name: 'Direct',
              type: 'line',
              stack: 'Total',
              data: [320, 332, 301, 334, 390, 330, 320]
            },
            {
              name: 'Search Engine',
              type: 'line',
              stack: 'Total',
              data: [820, 932, 901, 934, 1290, 1330, 1320]
            }
          ]
        };

        option && myChart.setOption(option);
</g:javascript>

    </cache:block>

  <style>
  .echarts-wrapper {
    width: 100%;
    height: 150px;
  }
  </style>
  </body>
</html>
