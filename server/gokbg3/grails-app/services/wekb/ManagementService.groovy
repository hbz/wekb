package wekb

import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import grails.gorm.transactions.Transactional
import grails.plugin.springsecurity.SpringSecurityService
import grails.web.mvc.FlashScope
import grails.web.servlet.mvc.GrailsParameterMap
import org.gokb.cred.Package
import org.gokb.cred.RefdataValue
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils


import javax.servlet.http.HttpServletRequest

@Transactional
class ManagementService {

    AccessService accessService
    SpringSecurityService springSecurityService

    static packageGeneralInfosBatchForm = [
            [
                    prompt     : 'Name of Package',
                    bParam     : 'pkg_batch_name',
                    placeholder: 'Package Name',
                    bProp       : 'name'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.KBCOMPONENT_STATUS,
                    prompt     : 'Status',
                    bParam     : 'pkg_batch_status',
                    bProp       : 'status'
            ],
            [
                    prompt     : 'Description URL',
                    bParam     : 'pkg_batch_descriptionURL',
                    placeholder: 'Description URL',
                    bProp       : 'descriptionURL'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_GLOBAL,
                    prompt     : 'Global Note',
                    bParam     : 'pkg_batch_globalNote',
                    bProp       : 'globalNote'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_BREAKABLE,
                    prompt     : 'Breakable',
                    bParam     : 'pkg_batch_breakable',
                    bProp       : 'breakable'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                    prompt     : 'Content Type',
                    bParam     : 'pkg_batch_contentType',
                    bProp       : 'contentType'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_FILE,
                    prompt     : 'File',
                    bParam     : 'pkg_batch_file',
                    bProp       : 'file'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                    prompt     : 'Open Access',
                    bParam     : 'pkg_batch_oa',
                    bProp       : 'openAccess'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_PAYMENT_TYPE,
                    prompt     : 'Payment Type',
                    bParam     : 'pkg_batch_paymentType',
                    bProp       : 'paymentType'
            ],
            [
                    type       : 'lookup',
                    baseClass  : 'org.gokb.cred.RefdataValue',
                    filter1    : RCConstants.PACKAGE_SCOPE,
                    prompt     : 'Scope',
                    bParam     : 'pkg_batch_scope',
                    bProp       : 'scope'
            ]
    ]

    static packageSourceInfosBatchForm = [
            [
                    type     : 'lookup',
                    baseClass: 'org.gokb.cred.RefdataValue',
                    filter1  : RCConstants.SOURCE_FREQUENCY,
                    prompt   : 'Frequency',
                    bParam   : 'pkg_batch_frequency',
                    bProp    : 'frequency'
            ],
            [
                    type     : 'lookup',
                    baseClass: 'org.gokb.cred.RefdataValue',
                    filter1  : RCConstants.SOURCE_DATA_SUPPLY_METHOD,
                    prompt   : 'Default Supply Method',
                    bParam   : 'pkg_batch_defaultSupplyMethod',
                    bProp    : 'defaultSupplyMethod'
            ],
            [
                    type     : 'lookup',
                    baseClass: 'org.gokb.cred.RefdataValue',
                    filter1  : RCConstants.SOURCE_DATA_FORMAT,
                    prompt   : 'Default Data Format',
                    bParam   : 'pkg_batch_defaultDataFormat',
                    bProp    : 'defaultDataFormat'
            ],
            [
                    type     : 'lookup',
                    baseClass: 'org.gokb.cred.RefdataValue',
                    filter1  : RCConstants.YN,
                    prompt   : 'Automatic Updates',
                    bParam   : 'pkg_batch_automaticUpdates',
                    bProp    : 'automaticUpdates',
                    bPropType : 'Boolean'
            ]
    ]

    Map processPackageManagement(GrailsParameterMap parameterMap) {
        Map<String, Object> result = [:]
        log.debug("processPackageManagement: $parameterMap")

        switch (parameterMap.activeTab) {
            case "generalInfos":
                if(parameterMap.processOption) {
                    processPackagesProperties(parameterMap)
                    parameterMap.remove('processOption')
                    packageGeneralInfosBatchForm.each { Map formMap ->
                        if (parameterMap[formMap.bParam]) {
                            parameterMap.remove(formMap.bParam)
                        }
                    }
                }
                break
            case "rangeInfos":
                if(parameterMap.processOption) {
                }
                break
            case "archivingAgencyInfos":
                if(parameterMap.processOption) {
                    processPackageArchivingAgencies(parameterMap)
                    parameterMap.remove('processOption')
                    parameterMap.remove('archivingAgency')
                    parameterMap.remove('openAccess')
                    parameterMap.remove('postCancellationAccess')
                }
                break
            case "identifiers":
                if(parameterMap.processOption) {
                    parameterMap.remove('processOption')
                }
                break
            case "ddcs":
                if(parameterMap.processOption) {
                    processPackageDdcs(parameterMap)
                    parameterMap.remove('processOption')
                }
                break
            case "sources":
                if(parameterMap.processOption) {
                    processPackageSourceProperties(parameterMap)
                    parameterMap.remove('processOption')
                    packageSourceInfosBatchForm.each { Map formMap ->
                        if (parameterMap[formMap.bParam]) {
                            parameterMap.remove(formMap.bParam)
                        }
                    }
                }
                break
        }

        result

    }

    void processPackagesProperties(GrailsParameterMap params) {
        log.debug("processPackagesProperties: $params")
        Map<String, Object> result = [:]
        result.user = springSecurityService.currentUser
        List successChanges = []
        if (accessService.checkReadable("org.gokb.cred.Package")) {
            FlashScope flash = getCurrentFlashScope()
            List selectedPackages = params.list("selectedPackages")
            if (selectedPackages) {
                Set<Package> packages = Package.findAllByUuidInList(selectedPackages)
                if (params.processOption == 'changeProperties') {
                    packages.each { Package pkg ->
                        if (accessService.checkEditableObject(pkg, params)) {
                            packageGeneralInfosBatchForm.each { Map formMap ->
                                if (params[formMap.bParam]) {
                                    if(formMap.type == 'lookup' && formMap.baseClass  == 'org.gokb.cred.RefdataValue') {
                                        List splitBParam = params[formMap.bParam].split(':')
                                        Long refDataId = Long.parseLong(splitBParam[1])
                                        pkg."${formMap.bProp}" = RefdataValue.get(refDataId) ?: pkg."${formMap.bProp}"
                                    }else {
                                        pkg."${formMap.bProp}" = params[formMap.bParam]
                                    }
                                }
                            }
                            if(pkg.isDirty()){
                                pkg.save()

                                successChanges << "The changes were made to the package '${pkg.name}'."
                            }
                        }
                    }
                }
            }else {
                flash.error = "You have not selected any packages to make the changes!"
            }

            if(successChanges.size() > 0){
                flash.success = successChanges.join('<br>')
            }
        }
    }

    void processPackageSourceProperties(GrailsParameterMap params) {
        log.debug("processPackageSourceProperties: $params")
        Map<String, Object> result = [:]
        result.user = springSecurityService.currentUser
        List successChanges = []
        if (accessService.checkReadable("org.gokb.cred.Package")) {
            FlashScope flash = getCurrentFlashScope()
            List selectedPackages = params.list("selectedPackages")
            if (selectedPackages) {
                Set<Package> packages = Package.findAllByUuidInList(selectedPackages)
                if (params.processOption == 'changeProperties') {
                    packages.each { Package pkg ->
                        if (accessService.checkEditableObject(pkg, params)) {
                            packageSourceInfosBatchForm.each { Map formMap ->
                                if (params[formMap.bParam] && pkg.source) {
                                    if(formMap.type == 'lookup' && formMap.baseClass  == 'org.gokb.cred.RefdataValue') {
                                        List splitBParam = params[formMap.bParam].split(':')
                                        Long refDataId = Long.parseLong(splitBParam[1])
                                        RefdataValue value = RefdataValue.get(refDataId)
                                        pkg.source."${formMap.bProp}" = (formMap.bPropType == 'Boolean') ? (value == RDStore.YN_YES ? true : false) : (value ?: pkg.source."${formMap.bProp}")
                                    }else {
                                        pkg.source."${formMap.bProp}" = params[formMap.bParam]
                                    }
                                }
                            }
                            if(pkg.source && pkg.source.isDirty()){
                                pkg.source.save()

                                successChanges << "The Source '${pkg.source.name}' on the package '${pkg.name}' was updated."
                            }
                        }
                    }
                }
            }else {
                flash.error = "You have not selected any packages to make the changes!"
            }

            if(successChanges.size() > 0){
                flash.success = successChanges.join('<br>')
            }
        }
    }

    void processPackageArchivingAgencies(GrailsParameterMap params) {
        log.debug("processPackageArchivingAgencies: $params")
        Map<String, Object> result = [:]
        result.user = springSecurityService.currentUser
        List successChanges = []
        if (accessService.checkReadable("org.gokb.cred.Package")) {
            FlashScope flash = getCurrentFlashScope()
            List selectedPackages = params.list("selectedPackages")
            if (selectedPackages) {
                Set<Package> packages = Package.findAllByUuidInList(selectedPackages)
                if (params.processOption == 'changeArchivingAgencies') {
                    packages.each { Package pkg ->
                        if (accessService.checkEditableObject(pkg, params) && params['archivingAgency']) {
                            List splitArchivingAgencyParam = params['archivingAgency'].split(':')
                            Long refArchivingAgencyId = Long.parseLong(splitArchivingAgencyParam[1])
                            if (refArchivingAgencyId) {
                                RefdataValue refdataValue = RefdataValue.get(refArchivingAgencyId)
                                if (refdataValue) {
                                    PackageArchivingAgency packageArchivingAgency
                                    packageArchivingAgency = PackageArchivingAgency.findByPkgAndArchivingAgency(pkg, refdataValue)
                                    if (!packageArchivingAgency) {
                                        packageArchivingAgency = new PackageArchivingAgency(archivingAgency: refdataValue, pkg: pkg)
                                    }
                                    if (packageArchivingAgency.save()) {
                                        if (params['openAccess']) {
                                            List splitOpenAccess = params['openAccess'].split(':')
                                            Long refOpenAccessId = Long.parseLong(splitOpenAccess[1])
                                            if (refOpenAccessId) {
                                                RefdataValue refdataValuePaaOP = RefdataValue.get(refOpenAccessId)
                                                if (refdataValuePaaOP)
                                                    packageArchivingAgency.openAccess = refdataValuePaaOP
                                            }
                                        }

                                        if (params['postCancellationAccess']) {
                                            List splitPCA = params['postCancellationAccess'].split(':')
                                            Long refPCAId = Long.parseLong(splitPCA[1])
                                            if (refPCAId) {
                                                RefdataValue refdataValuePaaPCA = RefdataValue.get(refPCAId)
                                                if (refdataValuePaaPCA)
                                                    packageArchivingAgency.postCancellationAccess = refdataValuePaaPCA
                                            }
                                        }
                                        if(packageArchivingAgency.isDirty()) {
                                            packageArchivingAgency.save()

                                            successChanges << "The Archiving Agency '${packageArchivingAgency.archivingAgency.value}' on the package '${pkg.name}' was updated or created."
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }else {
                flash.error = "You have not selected any packages to make the changes!"
            }

            if(successChanges.size() > 0){
                flash.success = successChanges.join('<br>')
            }
        }
    }

    void processPackageDdcs(GrailsParameterMap params) {
        log.debug("processPackageDdcs: $params")
        Map<String, Object> result = [:]
        result.user = springSecurityService.currentUser
        List successChanges = []
        List failChanges = []
        if (accessService.checkReadable("org.gokb.cred.Package")) {
            FlashScope flash = getCurrentFlashScope()
            List selectedPackages = params.list("selectedPackages")
            if (selectedPackages) {
                Set<Package> packages = Package.findAllByUuidInList(selectedPackages)
                if (params.processOption == 'changeDdcs') {
                    packages.each { Package pkg ->
                        if (accessService.checkEditableObject(pkg, params)) {
                            List splitDdcParam = params['ddc'].split(':')
                            Long refDdcId = Long.parseLong(splitDdcParam[1])
                            if (refDdcId) {
                                RefdataValue refdataValue = RefdataValue.get(refDdcId)
                                if (refdataValue && !(refdataValue in pkg.ddcs)) {
                                    pkg.addToDdcs(refdataValue)
                                }
                            }
                        }
                        if(pkg.isDirty()){
                            pkg.save()

                            successChanges << "The changes were made to the package '${pkg.name}'."
                        }
                    }
                }
            }else {
                flash.error = "You have not selected any packages to make the changes!"
            }

            if(successChanges.size() > 0){
                flash.success = successChanges.join('<br>')
            }
        }
    }

    FlashScope getCurrentFlashScope() {
        GrailsWebRequest grailsWebRequest = WebUtils.retrieveGrailsWebRequest()
        HttpServletRequest request = grailsWebRequest.getCurrentRequest()

        grailsWebRequest.attributes.getFlashScope(request)
    }
}
