<!DOCTYPE html>
<html>
  <head>
    <meta name="layout" content="public"/>
    <title>Error</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
  </head>
  <body>
    <div class="container">
      <div class="row">
        <div class="col-md-12">
          <div style="font-size:2.5em;margin-top:100px;" class="alert alert-default">
            ${message(code:"default.error.exception")}
          </div>

          <button class="btn btn-default" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
        </div>
      </div>
    </div>
  </body>
</html>
