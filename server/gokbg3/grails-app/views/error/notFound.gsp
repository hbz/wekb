<!doctype html>
<html>
<head>
    <meta name="layout" content="public"/>
    <title>Page Not Found</title>
    <link rel="stylesheet" href="${resource(dir: 'css', file: 'errors.css')}" type="text/css">
</head>

<body>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <div style="font-size:2.5em;margin-top:100px;" class="alert alert-default">
                Page Not Found (404)

                <br>
                <button class="btn btn-default btn-primary"
                        onclick="window.history.back()">${message(code: 'default.button.back')}</button>
            </div>
        </div>
    </div>
</div>
</body>
</html>
