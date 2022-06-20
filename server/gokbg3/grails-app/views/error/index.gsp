<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="public_semui"/>
    <title>Error</title>
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="col-md-12">
          <div style="font-size:2.5em;margin-top:100px;">
            ${message(code:"default.error.exception")}
          </div>

          <button class="class="ui black button"" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
        </div>
      </div>
    </div>
  </body>
</html>
