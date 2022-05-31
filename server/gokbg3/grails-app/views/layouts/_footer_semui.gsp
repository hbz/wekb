<footer class="ui inverted vertical footer segment">
    <div class="ui center aligned container">

        <div class="ui stackable inverted divided equal height stackable grid center aligned">

            <div class="four wide column ">
                <img height="30" alt="Logo wekb" src="${resource(dir: 'images', file: 'logo.svg')}"/>

                <p>©${new Date().format('yyyy')} Hochschulbibliothekszentrum des Landes Nordrhein-Westfalen (hbz) ‧ Jülicher Straße 6 ‧ 50674 Köln ‧ +49 221 400 75-0</p>
                <a rel="license" href="http://creativecommons.org/publicdomain/zero/1.0/">
                    <img alt="CC0 1.0 Universal - Public Domain Dedication"
                         src="${resource(dir: 'images', file: 'CC-Zero-badge.svg.png')}"/>
                </a>
            </div>

            <div class="four wide column ">

                <h2>About <g:message code="gokb.appname"/></h2>
                <ul>
                    <li><a target="_blank" class="content"
                           href="https://service-wiki.hbz-nrw.de/display/WEKB/About+we%3Akb">About  <g:message
                                code="gokb.appname"/></a></li>
                    <li><a target="_blank" class="content"
                           href="https://service-wiki.hbz-nrw.de/pages/viewpage.action?pageId=771129406">Manual  <g:message
                                code="gokb.appname"/></a></li>
                </ul>

                <h2>Technical Details</h2>
                <ul>
                    <%-- App version --%>
                    <li>
                        <a target="_blank" class="content"
                           href="https://github.com/hbz/wekb/releases">Version: ${grailsApplication.metadata['info.app.version']}</a>
                    </li>
                    <g:if test="${grailsApplication.metadata['build.git.branch']}">
                    <%-- Git branch --%>
                        <li>
                            <a target="_blank" class="content"
                               href="https://github.com/hbz/wekb/tree/${grailsApplication.metadata['git.branch']}">
                                Branch: ${grailsApplication.metadata['build.git.branch']}
                            </a>
                        </li>
                    </g:if>
                    <g:if test="${grailsApplication.metadata['build.git.revision']}">
                    <%-- Git Commit --%>
                        <li>
                            <a target="_blank" class="content"
                               href="https://github.com/hbz/wekb/tree/${grailsApplication.metadata['build.git.revision']}">
                                Git Commit: ${grailsApplication.metadata['build.git.revision']}
                            </a>
                        </li>
                    </g:if>
                    <g:if test="${grailsApplication.metadata['build.time']}">
                    <%-- Timestamp --%>
                        <li>
                            Build: ${grailsApplication.metadata['build.time']}
                        </li>
                    </g:if>
                </ul>
            </div>

            <div class="four wide column ">
                <h2>Contact Us</h2>
                <ul>
                    <li><a class="content" href="mailto:laser@hbz-nrw.de">E-Mail</a></li>
                    <li><a target="_blank" class="content"
                           href="https://www.hbz-nrw.de/ueber-uns/kontakt/anreise">How to reach us</a></li>
                    <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/impressum">Legal Notice</a></li>
                    <li><a target="_blank" class="content"
                           href="https://www.hbz-nrw.de/datenschutz">Data Privacy Statement</a></li>
                </ul>
            </div>

            <div class="four wide column ">
                <h2>Accessibility</h2>
                <ul>
                    <li><a target="_blank" class="content"
                           href="https://www.hbz-nrw.de/barrierefreiheit">Accessibility Statement</a></li>
                    <li><g:link controller="public" action="wcagPlainEnglish">Plain English</g:link></li>
                    <li><g:link controller="public" action="wcagFeedbackForm">Accessibility Feedback Form</g:link></li>
                </ul>
            </div>
        </div>
    </div>

</footer>
