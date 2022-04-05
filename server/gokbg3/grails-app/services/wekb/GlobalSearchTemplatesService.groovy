package wekb

import de.wekb.helper.RCConstants
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
        globalSearchTemplates.put('domains', domains())
        globalSearchTemplates.put('identifiers', identifiers())
        globalSearchTemplates.put('jobResults', jobResults())
        globalSearchTemplates.put('namespaces', namespaces())
        globalSearchTemplates.put('notes', notes())
        globalSearchTemplates.put('offices', offices())
        globalSearchTemplates.put('orgs', orgs())
        globalSearchTemplates.put('packages', packages())
        globalSearchTemplates.put('platforms', platforms())
        globalSearchTemplates.put('refdataCategories', refdataCategories())
        globalSearchTemplates.put('refdataValues', refdataValues())
        globalSearchTemplates.put('reviewRequests', reviewRequests())
        globalSearchTemplates.put('sources', sources())
        globalSearchTemplates.put('tipps', tipps())
        globalSearchTemplates.put('tippsOfPkg', tippsOfPkg())
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
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.CURATORY_GROUP_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_type',
                                        placeholder: 'Type',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'type'],
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

    Map domains() {
        Map result = [
                baseclass: 'org.gokb.cred.KBDomainInfo',
                title    : 'Domains',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'dcName', 'wildcard': 'B']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'dcName', link: true],
                                [heading: 'Display Name', property: 'displayName'],
                                [heading: 'Sort Key', property: 'dcSortOrder'],
                                [heading: 'Type', property: 'type?.value'],
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
                                [heading: 'Date Created', property: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map offices() {
        Map result = [
                baseclass: 'org.gokb.cred.Office',
                title    : 'Offices',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of Office',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                        ],
                        qbeGlobals: [
                               /* ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]

        result
    }

    Map orgs() {
        Map result = [
                baseclass   : 'org.gokb.cred.Org',
                title       : 'Organizations',
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
                                        prompt     : 'Source ID',
                                        qparam     : 'qp_source_id',
                                        placeholder: 'Source ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'source.id', 'type': 'java.lang.Long'],
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
                                [heading: 'Titles', property: 'currentTippCount'],
                               //[heading: 'T', property: 'tippDuplicatesByURLCount'],
                                [heading: 'Source', property: 'source?.name', link: true],
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
                        ],
                        qbeGlobals: [
                              /*  ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Primary URL', property: 'primaryUrl'],
                                [heading: 'Provider', property: 'provider.name'],
                                [heading: 'Current Titles', property: 'currentTippCount'],
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
                        ],
                        qbeGlobals: [
                                ['ctxtp': 'filter', 'prop': 'desc', 'comparator': 'ilike', 'value': 'Combo.%', 'negate': true]
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
                defaultSort : 'owner',
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
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Description', sort: 'desc', property: 'desc', link: true],
                                [heading: 'Value', sort: 'value', property: 'value'],
                                /*[heading: 'Value EN', sort: 'value_en', property: 'value_en'],
                                [heading: 'Value DE', sort: 'value_de', property: 'value_de'],
                                [heading: 'Hard Data', sort: 'isHardData', property: 'isHardData'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Refdata Category', sort: 'owner', property: 'owner'],*/
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
                        ],
                        qbeGlobals: [
                              /*  ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']*/
                        ],
                        qbeResults: [
                                [heading: 'ID', property: 'id', sort: 'id', link: true],
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: true],
                                [heading: 'Url', property: 'url', sort: 'url'],
                                [heading: 'automatic Updates', property: 'automaticUpdates'],
                                [heading: 'Frequency', property: 'frequency?.value'],
                                [heading: 'Last Run', property: 'lastRun'],
                                [heading: 'Last Run', property: 'nextUpdateTimestamp'],
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
                                [heading: 'Package', qpEquiv: 'qp_pkg_id', property: 'pkg?.name', link: true],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform?.name', link: true],
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
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: true],
                                [heading: 'Type', property: 'publicationType?.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium?.value', sort: 'medium.value'],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform?.name', link: true],
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
