<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="public_semui"/>
    <title>Kbart Import</title>
</head>

<body>

<semui:flashMessage data="${flash}"/>

<g:if test="${pkg}">

    <h1 class="ui header">Kbart Import for Package: ${pkg.name}</h1>

    <div class="ui segment">
        <h3 class="ui header">Information</h3>

        <div class="content">
            The kbart import allows you to update this package with titles.
            <br><br>
            <b>There are two things to keep in mind when importing the kbart.</b>

            <div class="ui ordered list">
                <div class="item">The kbart file must be <b><u>tab-delimited</u></b> and encoded in <b><u>UTF-8</u></b>. As required by the Kbart standard.</div>

                <div class="item">The kbart file must contain <b><u>all your titles</u></b> for this package even if you want to update even a small amount of titles.</div>
            </div>
        </div>
    </div>


    <div class="ui segment">
        <div class="content wekb-inline-lists">
            <dl>
                <dt class="control-label">
                    Package
                </dt>
                <dd>
                    <g:link controller="resource" action="show" id="${pkg.uuid}">
                        ${pkg.name}
                    </g:link>
                </dd>
            </dl>
            <dl>
                <dt class="control-label">Provider</dt>
                <dd><semui:xEditableManyToOne owner="${pkg}" field="provider" baseClass="org.gokb.cred.Org"
                                              overwriteEditable="false"/></dd>
            </dl>

            <dl>
                <dt class="control-label">Source</dt>
                <dd><semui:xEditableManyToOne owner="${pkg}" field="source" baseClass="org.gokb.cred.Source"
                                              overwriteEditable="false"/></dd>
            </dl>

            <dl>
                <dt class="control-label">Nominal Platform</dt>
                <dd><semui:xEditableManyToOne owner="${pkg}" field="nominalPlatform"
                                              baseClass="org.gokb.cred.Platform" overwriteEditable="false"/></dd>
            </dl>

            <dl>
                <dt class="control-label">Identifier Namespace for title_id field in kbart</dt>
                <dd><g:set var="idNamespace" value="${pkg.getTitleIDNameSpace()}"/>
                    <g:if test="${idNamespace}">
                        Identifier Namespace Name: ${idNamespace.name}<br>
                        Identifier Namespace Value: ${idNamespace.value}
                    </g:if><g:else>
                    Empty
                </g:else>
                </dd>
            </dl>
        </div>
    </div>

    <div class="ui segment">
        <h3 class="ui header">Kbart Import File</h3>

        <div class="content">
            <g:uploadForm class="ui form" action="processKbartImport" method="post" id="${pkg.id}">
                <div class="fields">
                    <div class="field">
                        <input type="file" class="ui button" name="tsvFile" accept=".tsv, .txt"/>
                    </div>
                    <button class="ui black button" type="submit">Process Kbart Import</button>
                </div>
            </g:uploadForm>
        </div>
    </div>

</g:if>
</body>
</html>
