<footer class="footer-bs">
    <div class="container">
        <div class="row">
            <div class="col-sm-4 footer-brand ">
                <a rel="license" href="http://creativecommons.org/publicdomain/zero/1.0/">
                 <img src="${resource(dir: 'images', file: 'CC-Zero-badge.svg.png')}"/>
                </a>
                <h2><img height="30" alt="Logo wekb"  src="${resource(dir: 'images', file: 'logo.svg')}"/></h2>
                <p>©2021 Hochschulbibliothekszentrum des Landes Nordrhein-Westfalen (hbz) ‧ Jülicher Straße 6 ‧ 50674 Köln ‧ +49 221 400 75-0</p>
            </div>
            <div class="col-sm-8">
                <div class="col-sm-4 footer-social">
                 <h4>Contact Us</h4>
                 <ul>
                     <li><a class="content" href="mailto:laser@hbz-nrw.de">E-Mail</a></li>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/ueber-uns/kontakt/anreise">How to reach us</a></li>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/impressum">Legal Notice</a></li>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/datenschutz">Data Privacy Statement</a></li>
                 </ul>
                </div>
                <div class="col-sm-4 footer-social">
                 <h4>Accessibility</h4>
                 <ul>
                     <li><a target="_blank" class="content" href="https://www.hbz-nrw.de/barrierefreiheit">Accessibility Statement</a></li>
                 </ul>
                </div>
                <div class="col-sm-4 footer-social">
                 <h4>Technical Details</h4>
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
