<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Kbart Import</title>
</head>

<body>
<h1 class="ui header">Kbart Import for Package: ${pkg.name}</h1>

<div class="ui segment">
    <h3 class="ui header">Information</h3>

    <div class="content">
        The kbart import allows you to update this package with titles.
        <br><br>
        <b>There are two things to keep in mind when importing the kbart.</b>

        <div class="ui ordered list">
            <div class="item">The kbart file must be tab-delimited and encoded in UTF-8. As required by the Kbart standard.</div>

            <div class="item">The kbart file must contain all your titles for this package even if you want to update even a small amount of titles.</div>
        </div>
    </div>
</div>

<div class="ui segment">
    <h3 class="ui header">Kbart import file</h3>

    <div class="content">
        <g:uploadForm class="ui form" action="processKbartImport" method="post">
            <div class="fields">
                <div class="field">
                    <input type="file" class="ui button" name="tsvFile" accept=".tsv, .txt"/>
                </div>
                <button class="ui black button" type="submit">Kbart import upload</button>
            </div>
        </g:uploadForm>
    </div>
</div>
</body>
</html>
