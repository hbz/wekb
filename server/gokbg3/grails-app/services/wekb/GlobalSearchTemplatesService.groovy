package wekb

import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import grails.gorm.transactions.Transactional

@Transactional
class GlobalSearchTemplatesService {

    private globalSearchTemplates = new java.util.HashMap<String, Map>()

    @javax.annotation.PostConstruct
    def init() {
        globalSearchTemplates.put('additionalPropertyDefinitions', additionalPropertyDefinitions())
        globalSearchTemplates.put('allocatedReviewGroups', allocatedReviewGroups())
        globalSearchTemplates.put('components', components())
        globalSearchTemplates.put('curatoryGroups', curatoryGroups())
        globalSearchTemplates.put('identifiers', identifiers())
        globalSearchTemplates.put('jobResults', jobResults())
        globalSearchTemplates.put('namespaces', namespaces())
        globalSearchTemplates.put('notes', notes())
        globalSearchTemplates.put('orgs', orgs())
        globalSearchTemplates.put('packages', packages())
        globalSearchTemplates.put('publicPackages', publicPackages())
        globalSearchTemplates.put('platforms', platforms())
        globalSearchTemplates.put('refdataCategories', refdataCategories())
        globalSearchTemplates.put('refdataValues', refdataValues())
        globalSearchTemplates.put('reviewRequests', reviewRequests())
        globalSearchTemplates.put('sources', sources())
        globalSearchTemplates.put('tipps', tipps())
        globalSearchTemplates.put('tippsOfPkg', tippsOfPkg())
        globalSearchTemplates.put('updatePackageInfos', updatePackageInfos())
        globalSearchTemplates.put('updateTippInfos', updateTippInfos())
        globalSearchTemplates.put('userOrganisation', userOrganisations())
        globalSearchTemplates.put('users', users())
        globalSearchTemplates.put('userJobs', userJobs())
        globalSearchTemplates.put('userWatchedComponents', userWatchedComponents())

    }

    public Map getGlobalSearchTemplate(String type) {
        return globalSearchTemplates.get(type);
    }

    public def findAllByBaseClass(String baseClass){
        def result = globalSearchTemplates.findAll {it.value.baseclass==baseClass}
        result
    }

    Map additionalPropertyDefinitions() {
        Map result = [
                baseclass: 'org.gokb.cred.AdditionalPropertyDefinition',
                title    : 'Additional Property Definitions',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Property Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Property Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'propertyName']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Property Name', property: 'propertyName', sort: 'propertyName', link: true],
                                // [heading:'Property Name', property:'propertyName', link:[controller:'search',action:'index',params:'x.params+[\'det\':x.counter]']]
                        ]
                ]
        ]
        result
    }

    Map allocatedReviewGroups() {
        Map result = [
                baseclass: 'org.gokb.cred.AllocatedReviewGroup',
                title    : 'Requests For Review by Group',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.REVIEW_REQUEST_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        prompt     : 'Cause',
                                        qparam     : 'qp_cause',
                                        placeholder: 'Cause',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'like', 'prop': 'descriptionOfCause']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.REVIEW_REQUEST_STD_DESC,
                                        prompt     : 'Type',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Standard description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'stdDesc']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.REVIEW_REQUEST_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'group'],
                                        hide       : false
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Cause', property: 'review.descriptionOfCause', link: true],
                                [heading: 'Request', property: 'review.reviewRequest'],
                                [heading: 'Status', property: 'review.status?.value'],
                                [heading: 'Type', property: 'review.type?.value'],
                                [heading: 'Timestamp', property: 'review.dateCreated', sort: 'review.dateCreated'],
                        ]
                ]
        ]
        result
    }

    Map updatePackageInfos() {
        Map result = [
                baseclass: 'wekb.UpdatePackageInfo',
                title    : 'Package Update Infos',
                group    : 'Secondary',
                defaultSort : 'startTime',
                defaultOrder: 'desc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'pkg.curatoryGroups'],
                                        hide       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Package',
                                        prompt     : 'Package',
                                        qparam     : 'qp_pkg',
                                        placeholder: 'Package',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.UPDATE_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Automatic Update',
                                        qparam     : 'qp_automaticUpdate',
                                        placeholder: 'Automatic Update',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'automaticUpdate'],
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Package', property: 'pkg.name', link: true],
                                [heading: 'Status', property: 'status', sort: 'status.value'],
                                [heading: 'Automatic Update', property: 'automaticUpdate', sort: 'automaticUpdate'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Only Last Changed Update', property: 'onlyRowsWithLastChanged', sort: 'onlyRowsWithLastChanged'],
                                [heading: 'Titles in we:kb before update', property: 'countPreviouslyTippsInWekb', sort: 'countPreviouslyTippsInWekb'],
                                [heading: 'Titles in we:kb after update', property: 'countNowTippsInWekb', sort: 'countNowTippsInWekb'],
                                [heading: 'Rows in KBART-File', property: 'countKbartRows', sort: 'countKbartRows'],
                                [heading: 'Processed KBART Rows', property: 'countProcessedKbartRows', sort: 'countProcessedKbartRows'],
                                [heading: 'Changed Titles ', property: 'countChangedTipps', sort: 'countChangedTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Changed Title'],
                                [heading: 'Removed Titles ', property: 'countRemovedTipps', sort: 'countRemovedTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Removed Title'],
                                [heading: 'New Titles', property: 'countNewTipps', sort: 'countNewTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=New Title'],
                                [heading: 'Invalid Titles', property: 'countInValidTipps', sort: 'countInValidTipps', jumpToLink: '/search/componentSearch/wekb.UpdatePackageInfo:objectID?qbe=g:updateTippInfos&qp_aup_id=objectID&&qp_type_value=Failed Title'],

                        ]
                ]
        ]
        result
    }

    Map updateTippInfos() {
        Map result = [
                baseclass: 'wekb.UpdateTippInfo',
                title    : 'Title Update Infos',
                group    : 'Secondary',
                defaultSort : 'startTime',
                defaultOrder: 'desc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        qparam     : 'qp_aup_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'updatePackageInfo.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        qparam     : 'qp_tipp_id',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipp.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'KBART Field',
                                        qparam     : 'qp_kbartProperty',
                                        placeholder: 'Kbart Field',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'kbartProperty']
                                ],
                                [
                                        prompt     : 'Old Value',
                                        qparam     : 'qp_oldValue',
                                        placeholder: 'Old Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'oldValue']
                                ],
                                [
                                        prompt     : 'New Value',
                                        qparam     : 'qp_newValue',
                                        placeholder: 'New Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'newValue']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.UPDATE_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.UPDATE_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],
                                [
                                        qparam     : 'qp_type_value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type.value'],
                                        hide: true
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Title', property: 'tipp.name', link: true],
                                [heading: 'Status', property: 'status', sort: 'status.value'],
                                [heading: 'Type', property: 'type', sort: 'type.value'],
                                [heading: 'KBART Field', property: 'kbartProperty', sort: 'kbartProperty'],
                                [heading: 'New Value', property: 'newValue'],
                                [heading: 'Old Value', property: 'oldValue'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                        ]
                ]
        ]
        result
    }

    Map components() {
        Map result = [
                baseclass: 'org.gokb.cred.KBComponent',
                title    : 'Components',
                group    : 'Secondary',
                qbeConfig: [
                        // For querying over associations and joins, here we will need to set up scopes to be referenced in the qbeForm config
                        // Until we need them tho, they are omitted. qbeForm entries with no explicit scope are at the root object.
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        prompt     : 'ID',
                                        qparam     : 'qp_id',
                                        placeholder: 'ID of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'id', 'type': 'java.lang.Long']
                                ],
                                [
                                        prompt     : 'SID',
                                        qparam     : 'qp_sid',
                                        placeholder: 'SID for item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ]
                        ],
                        qbeGlobals: [
                              /*  ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Type', property: 'niceName'],
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

    Map curatoryGroups() {
        Map result = [
                baseclass: 'org.gokb.cred.CuratoryGroup',
                title    : 'Curatory Groups',
                group    : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name of Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.CURATORY_GROUP_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated']
                        ]
                ]
        ]
        result
    }

    Map identifiers() {
        Map result = [
                baseclass: 'org.gokb.cred.Identifier',
                title    : 'Identifiers',
                group    : 'Tertiary',
                defaultSort : 'value',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Namespace',
                                        qparam     : 'qp_namespace_value',
                                        placeholder: 'Namespace',
                                        filter1    : 'all',
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.IdentifierNamespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'namespace'],
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'value'],
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Namespace', property: 'namespace.value', sort: 'namespace.value'],
                                [heading: 'Value', property: 'value', link: true, sort: 'value'],
                                [heading: 'Component', property: 'reference'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map jobResults() {
        Map result = [
                baseclass   : 'org.gokb.cred.JobResult',
                title       : 'Job Results',
                group       : 'Secondary',
                defaultSort : 'id',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.JOB_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type of Job',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Component', property: 'linkedItem'],
                                [heading: 'Type', property: 'type?.value', sort: 'type'],
                                [heading: 'Status', property: 'statusText'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Curatory Group', property: 'curatoryGroup'],
                        ]
                ]
        ]
        result
    }

    Map namespaces() {
        Map result = [
                baseclass: 'org.gokb.cred.IdentifierNamespace',
                title    : 'Identifier Namespaces',
                group    : 'Tertiary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Namespace',
                                        qparam     : 'qp_value',
                                        placeholder: 'value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'value', 'wildcard': 'B']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE,
                                        prompt     : 'Target Type',
                                        qparam     : 'qp_targetType',
                                        placeholder: 'Target Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'targetType']
                                ],
                                [
                                        prompt     : 'Category',
                                        qparam     : 'qp_family',
                                        placeholder: 'Category',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'family', 'wildcard': 'B']
                                ],
                        ],

                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name'],
                                [heading: 'Value', property: 'value', link: true, sort: 'value'],
                                [heading: 'Category', property: 'family', sort: 'family'],
                                [heading: 'Target Type', property: 'targetType.value', sort: 'targetType.value'],
                                [heading: 'Count', property: 'identifiersCount', sort: 'identifiersCount']
                        ]
                ]
        ]
        result
    }

    Map notes() {
        Map result = [
                baseclass: 'org.gokb.cred.Note',
                title    : 'Notes',
                group    : 'Tertiary',
                defaultSort : 'dateCreated',
                defaultOrder: 'desc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Note',
                                        qparam     : 'qp_note',
                                        placeholder: 'Note',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'note', 'wildcard': 'B']
                                ],
                                [
                                        prompt     : 'Owner Class',
                                        qparam     : 'qp_ownerClass',
                                        placeholder: 'Owner Class',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ownerClass'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Owner ID',
                                        qparam     : 'qp_ownerClassID',
                                        placeholder: 'Owner Class ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ownerId', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Note', property: 'note'],
                                [heading: 'Note', property: 'note'],
                                [heading: 'Date Created', property: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map orgs() {
        Map result = [
                baseclass   : 'org.gokb.cred.Org',
                title       : 'Providers',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R']
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],
                                        hide       : false
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.ORG_ROLE,
                                        prompt     : 'Role',
                                        qparam     : 'qp_roles',
                                        placeholder: 'Role',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'roles'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups'],
                                        hide       : false
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups'],
                                        hide       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],
                        ],
                        qbeGlobals: [
                                /*['ctxtp' : 'filter', 'prop': 'status.value', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on']*/
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Homepage', property: 'homepage', sort: 'homepage', outGoingLink: true],
                                [heading: 'Status', sort: 'status', property: 'status?.value'],
                                [heading: 'Misson', sort: 'misson', property: 'misson?.value'],
                                [heading: 'Current Titles', property: 'currentTippCount'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                        ]
                ]
        ]

        result
    }

    Map packages() {
        Map result = [
                baseclass   : 'org.gokb.cred.Package',
                title       : 'Packages',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Name of Package',
                                        qparam     : 'qp_name',
                                        placeholder: 'Package Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B', normalise: false]
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_SCOPE,
                                        prompt     : 'Scope',
                                        qparam     : 'qp_scope',
                                        placeholder: 'Scope',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'scope'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                                        prompt     : 'Content Type',
                                        qparam     : 'qp_content',
                                        placeholder: 'Content Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'contentType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_oa',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],
                                        hide       : false
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Platform',
                                        prompt     : 'Platform',
                                        qparam     : 'qp_platform',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PAA_ARCHIVING_AGENCY,
                                        prompt     : 'Package Archiving Agency',
                                        qparam     : 'qp_archivingAgency',
                                        placeholder: 'Package Archiving Agency',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paas.archivingAgency']
                                ],
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_platform_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Source',
                                        prompt     : 'Source',
                                        qparam     : 'qp_source',
                                        placeholder: 'Source',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'source'],
                                        hide       : false,
                                        notShowInPublic       : true
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Source Automatic Updates',
                                        qparam     : 'qp_source_automaticUpdates',
                                        placeholder: 'Source Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'source.automaticUpdates'],
                                ],

                                [
                                        prompt     : 'Source ID',
                                        qparam     : 'qp_source_id',
                                        placeholder: 'Source ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'source.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],

                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups'],
                                        hide       : true
                                ],
                        ],
                        qbeGlobals: [
                                /*['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Provider', property: 'provider?.name', link: true],
                                [heading: 'Nominal Platform', property: 'nominalPlatform?.name', link: true],
                                [heading: 'Content Type', property: 'contentType?.value', sort: 'contentType'],
                                [heading: 'Scope', property: 'scope', sort: 'scope'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Titles', property: 'currentTippCount', sort: 'currentTippCount'],
                               //[heading: 'T', property: 'tippDuplicatesByURLCount'],
                                [heading: 'Product IDs', property: 'anbieterProduktIDs'],
                                [heading: 'Source', property: 'source?.name', link: true, sort: 'source.name'],
                                [heading: 'Automatic Updates', property: 'source?.automaticUpdates']
                        ],
                        actions   : [
                        ]
                ]
        ]

        result
    }

    Map publicPackages() {
        Map result = [
                baseclass   : 'org.gokb.cred.Package',
                title       : 'Packages',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_platform_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Source ID',
                                        qparam     : 'qp_source_id',
                                        placeholder: 'Source ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'source.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Name of Package',
                                        qparam     : 'qp_name',
                                        placeholder: 'Package Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B', normalise: false]
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Source Automatic Updates',
                                        qparam     : 'qp_source_automaticUpdates',
                                        placeholder: 'Source Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'source.automaticUpdates'],
                                ],
                                //Package Filter
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_CONTENT_TYPE,
                                        prompt     : 'Content Type',
                                        qparam     : 'qp_contentType',
                                        placeholder: 'Content Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'contentType'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ddcs'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_PAYMENT_TYPE,
                                        prompt     : 'Paid',
                                        qparam     : 'qp_paymentType',
                                        placeholder: 'Paid',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paymentType'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_oa',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PAA_ARCHIVING_AGENCY,
                                        prompt     : 'Package Archiving Agency',
                                        qparam     : 'qp_archivingAgency',
                                        placeholder: 'Package Archiving Agency',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'paas.archivingAgency'],
                                        advancedSearch: [title: "Search Packages by ...", category: 'Package']
                                ],
                                //Title Filter
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.name'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_tippIdentifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.ids.value'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status_tipp',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        qparam     : 'qp_publicationType_tipp',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.publicationType'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        qparam     : 'qp_medium_tipp',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.medium'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        qparam     : 'qp_accessType_tipp',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.accessType'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName_tipp',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.publisherName'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Author',
                                        qparam     : 'qp_firstAuthor_tipp',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.firstAuthor'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        prompt     : 'Editor',
                                        qparam     : 'qp_firstEditor_tipp',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.firstEditor'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],

                                [
                                        prompt     : 'Subject Area',
                                        qparam     : 'qp_subjectArea_tipp',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'tipps.subjectArea'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc_tipp',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.ddcs'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        qparam     : 'qp_language_tipp',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'tipps.languages'],
                                        advancedSearch: [title: "Search Titles by ...", category: 'Title']
                                ],
                                //Platform Filter
                                [
                                        prompt     : 'Name of Platform',
                                        qparam     : 'qp_name_platform',
                                        placeholder: 'Name of Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'nominalPlatform.name'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Shibboleth Supported',
                                        qparam     : 'qp_shibbolethAuthentication_platform',
                                        placeholder: 'Shibboleth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.shibbolethAuthentication'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Open Athens Supported',
                                        qparam     : 'qp_openAthens_platform',
                                        placeholder: 'Open Athens Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.openAthens'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_IP_AUTH,
                                        prompt     : 'IP Auth Supported',
                                        qparam     : 'qp_ipAuthentication_platform',
                                        placeholder: 'IP Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.ipAuthentication'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_STATISTICS_FORMAT,
                                        prompt     : 'Statistics Format',
                                        qparam     : 'qp_statisticsFormat_platform',
                                        placeholder: 'Statistics Format',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.statisticsFormat'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R3 Supported',
                                        qparam     : 'qp_counterR3Supported_platform',
                                        placeholder: 'Counter R3 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR3Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Supported',
                                        qparam     : 'qp_counterR4Supported_platform',
                                        placeholder: 'Counter R4 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR4Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Supported',
                                        qparam     : 'qp_counterR5Supported_platform',
                                        placeholder: 'Counter R5 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR5Supported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Sushi Api Supported',
                                        qparam     : 'qp_counterR4SushiApiSupported_platform',
                                        placeholder: 'Counter R4 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR4SushiApiSupported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Sushi Api Supported',
                                        qparam     : 'qp_counterR5SushiApiSupported_platform',
                                        placeholder: 'Counter R5 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'nominalPlatform.counterR5SushiApiSupported'],
                                        advancedSearch: [title: "Search Platform by ...", category: 'Platform']
                                ],

                        ],
                        qbeGlobals: [
                                /*['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: true],
                                [heading: 'Provider', property: 'provider?.name', link: true],
                                [heading: 'Nominal Platform', property: 'nominalPlatform?.name', link: true],
                                [heading: 'Curatory Groups', property: 'curatoryGroups', link: true],
                                [heading: 'Content Type', property: 'contentType?.value', sort: 'contentType'],
                                [heading: 'Product IDs', property: 'anbieterProduktIDs'],
                                [heading: 'Titles', property: 'currentTippCount', sort: 'currentTippCount'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Automatic Updates', property: 'source?.automaticUpdates']
                        ],
                        actions   : [
                        ]
                ]
        ]

        result
    }

    Map platforms() {
        Map result = [
                baseclass   : 'org.gokb.cred.Platform',
                title       : 'Platforms',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        prompt     : 'Platform URL',
                                        qparam     : 'qp_url',
                                        placeholder: 'Platform URL',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'primaryUrl']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Component Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups'],
                                        hide       : false
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups'],
                                        hide       : true
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],
                                        hide       : false
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],

                                [
                                        filter1    : 'all',
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.IdentifierNamespace',
                                        prompt     : 'Title Namespace',
                                        qparam     : 'qp_titleNamespace',
                                        placeholder: 'Title Namespace',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'titleNamespace'],
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Shibboleth Supported',
                                        qparam     : 'qp_shibbolethAuthentication',
                                        placeholder: 'Shibboleth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'shibbolethAuthentication'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Open Athens Supported',
                                        qparam     : 'qp_openAthens',
                                        placeholder: 'Open Athens Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAthens']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_IP_AUTH,
                                        prompt     : 'IP Auth Supported',
                                        qparam     : 'qp_ipAuthentication',
                                        placeholder: 'IP Auth Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ipAuthentication'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PLATFORM_STATISTICS_FORMAT,
                                        prompt     : 'Statistics Format',
                                        qparam     : 'qp_statisticsFormat',
                                        placeholder: 'Statistics Format',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'statisticsFormat'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R3 Supported',
                                        qparam     : 'qp_counterR3Supported',
                                        placeholder: 'Counter R3 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR3Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Supported',
                                        qparam     : 'qp_counterR4Supported',
                                        placeholder: 'Counter R4 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Supported',
                                        qparam     : 'qp_counterR5Supported',
                                        placeholder: 'Counter R5 Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5Supported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R4 Sushi Api Supported',
                                        qparam     : 'qp_counterR4SushiApiSupported',
                                        placeholder: 'Counter R4 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR4SushiApiSupported'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Counter R5 Sushi Api Supported',
                                        qparam     : 'qp_counterR5SushiApiSupported',
                                        placeholder: 'Counter R5 Sushi Api Supported',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'counterR5SushiApiSupported'],
                                ],
                        ],
                        qbeGlobals: [
                              /*  ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Primary URL', property: 'primaryUrl', sort: 'primaryUrl', outGoingLink: true],
                                [heading: 'Provider', property: 'provider.name', link: true],
                                [heading: 'Current Titles', property: 'currentTippCount'],
                                [heading: 'Current Packages', property: 'packagesCount'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

    Map refdataCategories() {
        Map result = [
                baseclass: 'org.gokb.cred.RefdataCategory',
                title    : 'Refdata Categories ',
                group    : 'Secondary',
                defaultSort : 'desc',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Category Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc']
                                ],

                                [
                                        prompt     : 'Description EN',
                                        qparam     : 'qp_desc_en',
                                        placeholder: 'Category Description En',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc_en']
                                ],
                        ],
                        qbeGlobals: [
                               /* ['ctxtp': 'filter', 'prop': 'desc', 'comparator': 'ilike', 'value': 'Combo.%', 'negate': true]*/
                        ],
                        qbeResults: [
                                [heading: 'Description', sort: 'desc', property: 'desc', link: true],
                                [heading: 'Description EN', sort: 'desc_en', property: 'desc_en'],
                                [heading: 'Description DE', sort: 'desc_de', property: 'desc_de'],
                                [heading: 'Hard Data', sort: 'isHardData', property: 'isHardData'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Refdata Values', sort: 'valuesCount', property: 'valuesCount'],
                        ]
                ]
        ]

        result
    }

    Map refdataValues() {
        Map result = [
                baseclass: 'org.gokb.cred.RefdataValue',
                title    : 'Refdata Values ',
                group    : 'Secondary',
                defaultSort : 'value',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'desc']
                                ],

                                [
                                        prompt     : 'Value',
                                        qparam     : 'qp_value',
                                        placeholder: 'Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'value']
                                ],

                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataCategory',
                                        prompt     : 'Refdata Category',
                                        qparam     : 'qp_owner',
                                        placeholder: 'Refdata Category',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'owner']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Value', sort: 'value', property: 'value', link: true],
                                [heading: 'Value EN', sort: 'value_en', property: 'value_en'],
                                [heading: 'Value DE', sort: 'value_de', property: 'value_de'],
                                [heading: 'Hard Data', sort: 'isHardData', property: 'isHardData'],
                                [heading: 'Description', sort: 'desc', property: 'desc'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Refdata Category', sort: 'owner', property: 'owner.desc'],
                        ]
                ]
        ]

        result
    }

    Map reviewRequests() {
        Map result = [
                baseclass: 'org.gokb.cred.ReviewRequest',
                title    : 'Requests For Review',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.REVIEW_REQUEST_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status']
                                ],
                                [
                                        prompt     : 'Cause',
                                        qparam     : 'qp_cause',
                                        placeholder: 'Cause',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'like', 'prop': 'descriptionOfCause']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.REVIEW_REQUEST_STD_DESC,
                                        prompt     : 'Type',
                                        qparam     : 'qp_desc',
                                        placeholder: 'Standard description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'stdDesc']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.REVIEW_REQUEST_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Cause', property: 'descriptionOfCause', link: true],
                                [heading: 'Request', property: 'reviewRequest'],
                                [heading: 'Status', property: 'status?.value'],
                                [heading: 'Type', property: 'type?.value'],
                                [heading: 'Timestamp', property: 'dateCreated', sort: 'dateCreated'],
                        ]
                ]
        ]
        result
    }

    Map sources() {
        Map result = [
                baseclass: 'org.gokb.cred.Source',
                title    : 'Source',
                group    : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name of Source',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name of Source',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'curatoryGroups'],
                                        hide       : false
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'curatoryGroups'],
                                        hide       : true
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.YN,
                                        prompt     : 'Automatic Updates',
                                        qparam     : 'qp_automaticUpdates',
                                        placeholder: 'Automatic Updates',
                                        propType   : 'Boolean',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'automaticUpdates'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.SOURCE_FREQUENCY,
                                        prompt     : 'Frequency',
                                        qparam     : 'qp_frequency',
                                        placeholder: 'Frequencys',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'frequency'],
                                ],
                        ],
                        qbeGlobals: [
                              /*  ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Url', property: 'url', sort: 'url', outGoingLink: true],
                                [heading: 'automatic Updates', property: 'automaticUpdates'],
                                [heading: 'Packages', property: 'packages', link: true],
                                [heading: 'Frequency', property: 'frequency?.value'],
                                [heading: 'Last Run', property: 'lastRun', sort: 'lastRun'],
                                [heading: 'Next Run', property: 'nextUpdateTimestamp'],
                                [heading: 'Identifier Namespace', property: 'targetNamespace?.value'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

    Map tipps() {
        Map result = [
                baseclass: 'org.gokb.cred.TitleInstancePackagePlatform',
                title    : 'Titles',
                group    : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [

                                [
                                        prompt     : 'Status ID',
                                        qparam     : 'qp_status_id',
                                        placeholder: 'Status ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title ID',
                                        qparam     : 'qp_title_id',
                                        placeholder: 'Title ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_plat_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name'],
                                ],

                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],
                                        hide       : false
                                ],
                                [
                                        prompt     : 'Url',
                                        qparam     : 'qp_url',
                                        placeholder: 'Url',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'url'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider'],
                                        hide       : false
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Package',
                                        prompt     : 'Package',
                                        qparam     : 'qp_pkg',
                                        placeholder: 'Package',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Platform',
                                        prompt     : 'Platform',
                                        qparam     : 'qp_plat',
                                        placeholder: 'Platform',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroup',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.curatoryGroups'],
                                        hide       : false
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'pkg.curatoryGroups'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'publisherName'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                ],
                                [
                                        prompt     : 'Author',
                                        qparam     : 'qp_firstAuthor',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstAuthor'],
                                ],
                                [
                                        prompt     : 'Editor',
                                        qparam     : 'qp_firstEditor',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstEditor'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        qparam     : 'qp_accessType',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessType'],
                                ],
                                [
                                        prompt     : 'Subject Area',
                                        qparam     : 'qp_subjectArea',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'subjectArea'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_openAccess',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        qparam     : 'qp_language',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'languages'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],
                        ],
                       /* qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],*/
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true],
                                [heading: 'Type', property: 'publicationType?.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium?.value', sort: 'medium.value'],
                                [heading: 'Package', qpEquiv: 'qp_pkg_id', property: 'pkg.name', sort: 'pkg.name', link: true],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform.name', sort: 'hostPlatform.name', link: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status.value'],
                                [heading: 'URL', property: 'url', sort: 'url', outGoingLink: true]
                        ]
                ]
        ]

        result
    }

    Map tippsOfPkg() {
        Map result = [
                baseclass: 'org.gokb.cred.TitleInstancePackagePlatform',
                title    : 'Titles',
                group    : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Status ID',
                                        qparam     : 'qp_status_id',
                                        placeholder: 'Status ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title ID',
                                        qparam     : 'qp_title_id',
                                        placeholder: 'Title ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Provider ID',
                                        qparam     : 'qp_provider_id',
                                        placeholder: 'Provider ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_plat_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name'],
                                ],
                                [
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_publisherName',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'publisherName'],
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],
                                        hide       : false
                                ],
                                [
                                        prompt     : 'Url',
                                        qparam     : 'qp_url',
                                        placeholder: 'Url',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'url'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Publication Type',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_MEDIUM,
                                        prompt     : 'Medium',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                ],
                                [
                                        prompt     : 'Author',
                                        qparam     : 'qp_firstAuthor',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstAuthor'],
                                ],
                                [
                                        prompt     : 'Editor',
                                        qparam     : 'qp_firstEditor',
                                        placeholder: 'Editor',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'firstEditor'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_ACCESS_TYPE,
                                        prompt     : 'Access Type',
                                        qparam     : 'qp_accessType',
                                        placeholder: 'Access Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'accessType'],
                                ],
                                [
                                        prompt     : 'Subject Area',
                                        qparam     : 'qp_subjectArea',
                                        placeholder: 'Subject Area',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'subjectArea'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.DDC,
                                        prompt     : 'DDC',
                                        qparam     : 'qp_ddc',
                                        placeholder: 'DDC',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ddcs'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_OPEN_ACCESS,
                                        prompt     : 'Open Access',
                                        qparam     : 'qp_openAccess',
                                        placeholder: 'Open Access',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'openAccess'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_LANGUAGE,
                                        prompt     : 'Language',
                                        qparam     : 'qp_language',
                                        placeholder: 'Language',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'languages'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],
                                //FOR My Components Area
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.CuratoryGroup',
                                        prompt     : 'Curatory Group',
                                        qparam     : 'qp_curgroups',
                                        placeholder: 'Curatory Group',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'exists', 'prop': 'pkg.curatoryGroups'],
                                        hide       : true
                                ],

                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true],
                                [heading: 'Type', property: 'publicationType?.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium?.value', sort: 'medium.value'],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform.name', sort: 'hostPlatform.name',  link: true],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status.value'],
                                [heading: 'URL', property: 'url', sort: 'url', outGoingLink: true]
                        ]
                ]
        ]

        result
    }

    Map users() {
        Map result = [
                baseclass   : 'org.gokb.cred.User',
                title       : 'Users',
                group       : 'Admin',
                defaultSort : 'username',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Username',
                                        qparam     : 'qp_name',
                                        placeholder: 'Username',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'username']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Username', property: 'username', link: true],
                                [heading: 'Enabled', property: 'enabled'],
                                [heading: 'Contributor', property: 'contributorStatus'],
                                [heading: 'Editor', property: 'editorStatus'],
                                [heading: 'API-User', property: 'apiUserStatus'],
                                [heading: 'Admin', property: 'adminStatus']
                                // [heading:'Username', property:'username', link:[controller:'search',action:'index',params:'x.params+[\'det\':x.counter]']]
                        ]
                ]
        ]
        result
    }

    Map userOrganisations() {
        Map result = [
                baseclass: 'org.gokb.cred.UserOrganisation',
                title    : 'User Organisations',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Username',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'username']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'displayName', link: true],
                        ]
                ]
        ]

        result
    }

    Map userJobs() {
        Map result = [
                baseclass   : 'org.gokb.cred.JobResult',
                title       : 'User Jobs',
                group       : 'Secondary',
                defaultSort : 'id',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.JOB_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type of Job',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type']
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp': 'filter', 'prop': 'ownerId', 'comparator': 'eq', 'value': '__USERID', 'default': 'on', 'qparam': 'qp_owner', 'type': 'java.lang.Long', 'hidden': true]
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: true],
                                [heading: 'Component', property: 'linkedItem'],
                                [heading: 'Type', property: 'type?.value', sort: 'type'],
                                [heading: 'Status', property: 'statusText'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                                [heading: 'Curatory Group', property: 'curatoryGroup'],
                        ]
                ]
        ]
        result
    }

    Map userWatchedComponents() {
        Map result = [
                baseclass   : 'org.gokb.cred.ComponentWatch',
                title       : 'My Watched Components',
                group       : 'Tertiary',
                defaultSort : 'component.lastUpdated',
                defaultOrder: 'desc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Component Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'component.name', 'wildcard': 'R']
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp': 'filter', 'prop': 'user.id', 'comparator': 'eq', 'value': '__USERID', 'default': 'on', 'qparam': 'qp_user', 'type': 'java.lang.Long', 'hidden': true]
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'component.name', link: true],
                                [heading: 'Type', property: 'component.niceName'],
                                [heading: 'Last Update on', property: 'component.lastUpdated'],
                                [heading: 'Last Update Comment', property: 'component.lastUpdateComment']
                        ]
                ]
        ]
        result
    }

}
