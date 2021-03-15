<!doctype html>
<html>
    <head>
        <title>Page Not Found</title>
    </head>
    <body>
        <ul class="errors">
            <li>Error: Page Not Found (404)</li>
            <li>Path: ${request.forwardURI}</li>

            <button class="btn btn-default" onclick="window.history.back()">${message(code: 'default.button.back')}</button>
        </ul>
    </body>
</html>
