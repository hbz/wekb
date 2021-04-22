<footer class="footer-bs">
    <div class="container">
        <div class="row">
            <div class="col-sm-4 footer-brand ">
                <img height="30" alt="Logo wekb"  src="${resource(dir: 'images', file: 'logo.svg')}"/>
                <p>©2021 Hochschulbibliothekszentrum des Landes Nordrhein-Westfalen (hbz) ‧ Jülicher Straße 6 ‧ 50674 Köln ‧ +49 221 400 75-0</p>
                <a rel="license" href="http://creativecommons.org/publicdomain/zero/1.0/">
                    <img alt="CC0 1.0 Universal - Public Domain Dedication" src="${resource(dir: 'images', file: 'CC-Zero-badge.svg.png')}"/>
                </a>
            </div>
            <div class="col-sm-8">
                <div class="col-sm-4 footer-social">
                 <h2>Contact Us</h4>
                 <ul>
                     <li><a class="content" href="mailto:laser@hbz-nrw.de">E-Mail</a></li>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/ueber-uns/kontakt/anreise">How to reach us</a></li>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/impressum">Legal Notice</a></li>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/datenschutz">Data Privacy Statement</a></li>
                 </ul>
                </div>
                <div class="col-sm-4 footer-social">
                 <h2>Accessibility</h4>
                 <ul>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/barrierefreiheit">Accessibility Statement</a></li>
                     <li><g:link controller="public" action="wcagPlainEnglish">Plain English</g:link></li>
                     <li><g:link controller="public" action="wcagFeedbackForm">Accessibility Feedback Form</g:link></li>
                 </ul>
                </div>
                <div class="col-sm-4 footer-social">
                 <h2>Technical Details</h4>
                 <ul>
                     <%-- App version --%>
                     <li>
                         <a target="_blank" class="content" href="https://github.com/hbz/laser-gokb/releases">Version: ${grailsApplication.metadata['info.app.version']}</a>
                     </li>
                     <%-- Git branch --%>
                    <li>
                         <g:if test="${grailsApplication.metadata['build.git.branch']}">
                             <a target="_blank" class="content"  href="https://github.com/hbz/laser-gokb/tree/${grailsApplication.metadata['git.branch']}">
                                 Branch: ${grailsApplication.metadata['build.git.branch']}
                             </a>
                         </g:if>
                     </li>
                     <%-- Git Commit --%>
                     <li>
                         <g:if test="${grailsApplication.metadata['build.git.revision']}">
                             <a target="_blank" class="content" href="https://github.com/hbz/laser-gokb/tree/${grailsApplication.metadata['build.git.revision']}">
                                 Git Commit: ${grailsApplication.metadata['build.git.revision']}
                             </a>
                         </g:if>
                     </li>
                     <%-- Timestamp --%>
                     <li>
                         <g:if test="${grailsApplication.metadata['build.time']}">
                             Build: ${grailsApplication.metadata['build.time']}
                         </g:if>
                     </li>
                 </ul>
                </div>
            </div>
        </div>
    </div>
</footer>
