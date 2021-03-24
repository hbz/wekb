
 <footer class="footer panel-footer wekb-footer">
    <div class="container">
        <!-- Example row of columns -->
        <div class="row">
            <div class="col-sm-4">
                <p xmlns:dct="http://purl.org/dc/terms/" xmlns:vcard="http://www.w3.org/2001/vcard-rdf/3.0#">
                    <a rel="license"
                       href="http://creativecommons.org/publicdomain/zero/1.0/">
                        <img src="http://i.creativecommons.org/p/zero/1.0/88x31.png" style="border-style: none;" alt="CC0" />
                    </a>
                    <br />
                    To the extent possible under law,
                    <a rel="dct:publisher"
                       href="https://www.hbz-nrw.de/">
                        <span property="dct:title">Hochschulbibliothekszentrum des Landes Nordrhein-Westfalen (hbz)</span></a>
                    has waived all copyright and related or neighboring rights to
                    <span property="dct:title">we:kb</span>.
                This work is published from:
                    <span property="vcard:Country" datatype="dct:ISO3166"
                          content="DE" about="https://www.hbz-nrw.de/">
                        Germany</span>.
                </p>
            </div>
             <div class="col-sm-8">
                <div class="col-sm-4">
                    <h2>Contact</h2>
                    <div class="ui inverted link list">
                        <div class="item">
                            <i class="fa fa-envelope-open fa-fw"></i><a class="content" href="mailto:laser@hbz-nrw.de">E-Mail Address</a>
                        </div>
                        <div class="item">
                            <i class="fa fa-map-signs fa-fw"></i><a target="_blank" class="content" href="https://www.hbz-nrw.de/ueber-uns/kontakt/anreise">Location / Access</a>
                        </div>
                        <div class="item">
                            <i class="fa fa-file  fa-fw"></i><a target="_blank" class="content" href="https://www.hbz-nrw.de/impressum">Imprint</a>
                        </div>
                        <div class="item">
                            <i class="fa fa-lock  fa-fw"></i><a target="_blank" class="content" href="https://www.hbz-nrw.de/datenschutz">Pivacy Policy</a>
                        </div>
                    </div>
                </div>
                <div class="col-sm-4">
                    <h2>Accessibility</h2>
                    <div class="ui inverted link list">
                        <div class="item">
                            <i class="fa fa-blind fa-fw"></i>
                            <a target="_blank" class="content" href="https://www.hbz-nrw.de/barrierefreiheit">
                                Declaration on Accessibility
                            </a>
                        </div>
    %{--                    <div class="item">
                            <i class="fa fa-blind fa-fw"></i>
                            <g:link controller="public" action="wcagFeedbackForm" class="content">

                            </g:link>
                        </div>

                        <div class="item">
                            <i class="fa fa-blind fa-fw"></i>
                            <g:link controller="public" action="wcagEasyLanguage" class="content">

                            </g:link>
                        </div>--}%
                    </div>
                </div>
                <div class="col-sm-4">
                    <h2>Technical Details</h2>
                    <div class="ui inverted link list">
                        <%-- App version --%>
                        <div class="item">
                            <g:if test="${grailsApplication.metadata['info.app.version']}">
                                <a target="_blank" class="item" href="https://github.com/hbz/laser-gokb/releases">
                                    Version: ${grailsApplication.metadata['info.app.version']}
                                </a>
                            </g:if>
                        </div>
                        <%-- Git branch --%>
                        <div class="item">
                            <g:if test="${grailsApplication.metadata['build.git.branch']}">
                                <a target="_blank" class="item" href="https://github.com/hbz/laser-gokb/tree/${grailsApplication.metadata['git.branch']}">
                                    Branch: ${grailsApplication.metadata['build.git.branch']}
                                </a>
                            </g:if>
                        </div>
                        <%-- Git Commit --%>
                        <div class="item">
                            <g:if test="${grailsApplication.metadata['build.git.revision']}">
                                <a target="_blank" class="item" href="https://github.com/hbz/laser-gokb/tree/${grailsApplication.metadata['build.git.revision']}">
                                    Git Commit: ${grailsApplication.metadata['build.git.revision']}
                                </a>
                            </g:if>
                        </div>
                        <%-- Timestamp --%>
                        <div class="item">
                            <g:if test="${grailsApplication.metadata['build.time']}">
                                    Build: ${grailsApplication.metadata['build.time']}
                            </g:if>
                        </div>
                    </div>
                </div>
             </div>
        </div>
    </div>
 </footer>
