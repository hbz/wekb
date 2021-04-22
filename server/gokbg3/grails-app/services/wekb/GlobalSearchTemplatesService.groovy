package wekb

import de.wekb.helper.RCConstants
import grails.gorm.transactions.Transactional

@Transactional
class GlobalSearchTemplatesService {

    private globalSearchTemplates = new java.util.HashMap<String, Map>()

    @javax.annotation.PostConstruct
    def init() {
        globalSearchTemplates.put('additionalPropertyDefinitions', additionalPropertyDefinitions())
        globalSearchTemplates.put('components', components())
        globalSearchTemplates.put('curatoryGroups', curatoryGroups())
        globalSearchTemplates.put('dataFiles', dataFiles())
        globalSearchTemplates.put('dsCategorys',dsCategorys())
        globalSearchTemplates.put('dsCriterions', dsCriterions())
        globalSearchTemplates.put('domains', domains())
        globalSearchTemplates.put('books', books())
        globalSearchTemplates.put('databases', databases())
        globalSearchTemplates.put('journals', journals())
        globalSearchTemplates.put('folderContents', folderContents())
        globalSearchTemplates.put('imprints', imprints())
        globalSearchTemplates.put('jobResults', jobResults())
        globalSearchTemplates.put('licenses', licenses())
        globalSearchTemplates.put('macros', macros())
        globalSearchTemplates.put('namespaces', namespaces())
        globalSearchTemplates.put('offices', offices())
        globalSearchTemplates.put('orgs', orgs())
        globalSearchTemplates.put('packages', packages())
        globalSearchTemplates.put('platforms', platforms())
        globalSearchTemplates.put('projects', projects())
        globalSearchTemplates.put('refdataCategories', refdataCategories())
        globalSearchTemplates.put('reviewRequests', reviewRequests())
        globalSearchTemplates.put('rules', rules())
        globalSearchTemplates.put('sources', sources())
        globalSearchTemplates.put('tipps', tipps())
        globalSearchTemplates.put('titles', titles())
        globalSearchTemplates.put('userOrganisation', userOrganisations())
        globalSearchTemplates.put('users', users())
        globalSearchTemplates.put('userWatchedComponents', userWatchedComponents())
        globalSearchTemplates.put('works', works())

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
                                [heading: 'Property Name', property: 'propertyName', sort: 'propertyName', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                // [heading:'Property Name', property:'propertyName', link:[controller:'search',action:'index',params:'x.params+[\'det\':x.counter]']]
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
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : 'KBComponent.EditStatus',
                                        prompt     : 'Edit Status',
                                        qparam     : 'qp_editstatus',
                                        placeholder: 'Component Edit Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'editStatus']
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Type', property: 'niceName'],
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Edit Status', property: 'editStatus?.value', sort: 'editStatus'],
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
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Edit Status', property: 'editStatus?.value', sort: 'editStatus']
                        ]
                ]
        ]
        result
    }

    Map dataFiles() {
        Map result = [
                baseclass: 'org.gokb.cred.DataFile',
                title    : 'Data Files',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'File Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Created On', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Mime Type', property: 'uploadMimeType', sort: 'uploadMimeType'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

    Map dsCategorys() {
        Map result = [
                baseclass: 'org.gokb.cred.DSCategory',
                title    : 'DS Categories',
                group    : 'Tertiary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_descr',
                                        placeholder: 'Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'description', 'wildcard': 'B']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Code', property: 'code', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Description', property: 'description', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                        ]
                ]
        ]
        result
    }

    Map dsCriterions() {
        Map result = [
                baseclass: 'org.gokb.cred.DSCriterion',
                title    : 'DS Criterion',
                group    : 'Tertiary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_descr',
                                        placeholder: 'Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'description', 'wildcard': 'B']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Category', property: 'owner.description', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Title', property: 'title'],
                                [heading: 'Description', property: 'description'],
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
                                [heading: 'Name', property: 'dcName', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Display Name', property: 'displayName'],
                                [heading: 'Sort Key', property: 'dcSortOrder'],
                                [heading: 'Type', property: 'type?.value'],
                        ]
                ]
        ]
        result
    }

    Map books() {
        Map result = [
                baseclass   : 'org.gokb.cred.BookInstance',
                title       : 'eBooks',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Book Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_pub',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisher'],
                                        hide       : true
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
                                        baseClass  : 'org.gokb.cred.Person',
                                        prompt     : 'Person',
                                        qparam     : 'qp_person',
                                        placeholder: 'Person',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'people.person'],
                                        hide       : true
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id'], sort: 'name'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map databases() {
        Map result = [
                baseclass   : 'org.gokb.cred.DatabaseInstance',
                title       : 'Databases',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Database Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_pub',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisher'],
                                        hide       : true
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id'], sort: 'name'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map journals() {
        Map result = [
                baseclass   : 'org.gokb.cred.JournalInstance',
                title       : 'Journals',
                group       : 'Secondary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Journal Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R']
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_pub',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisher'],
                                        hide       : false
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
                                        placeholder: 'Status of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id'], sort: 'name'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                        ]
                ]
        ]
        result
    }

    Map folderContents() {
        Map result = [
                baseclass   : 'org.gokb.cred.FolderEntry',
                title       : 'Folder Contents',
                group       : 'Secondary',
                defaultSort : 'id',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Folder ID',
                                        qparam     : 'qp_folder_id',
                                        placeholder: 'Folder ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'folder.id', 'type': 'java.lang.Long']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'displayName', link: [controller: 'resource', action: 'show', id: 'x.r.linkedItem.class.name+\':\'+x.r.linkedItem.id']],
                                [heading: 'Availability', property: 'linkedItem.tipps?.size()?:"none"'],
                        ]
                ]
        ]
        result
    }

    Map imprints() {
        Map result = [
                baseclass   : 'org.gokb.cred.Imprint',
                title       : 'Imprints',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
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
                        qbeGlobals: [
                                ['ctxtp': 'filter', 'prop': 'ownerId', 'comparator': 'eq', 'value': '__USERID', 'default': 'on', 'qparam': 'qp_owner', 'type': 'java.lang.Long', 'hidden': true]
                        ],
                        qbeResults: [
                                [heading: 'Description', property: 'description', link: [controller: 'resource', action: 'show', id: 'x.r.uuid']],
                                [heading: 'Type', property: 'type?.value', sort: 'type'],
                                [heading: 'Status', property: 'statusText'],
                                [heading: 'Start Time', property: 'startTime', sort: 'startTime'],
                                [heading: 'End Time', property: 'endTime', sort: 'endTime'],
                        ]
                ]
        ]
        result
    }

    Map licenses() {
        Map result = [
                baseclass: 'org.gokb.cred.License',
                title    : 'Licenses',
                group    : 'Secondary',
                message  : 'Please contact nisohq@niso.org for more information on license downloads',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name of License',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]

        result
    }

    Map macros() {
        Map result = [
                baseclass: 'org.gokb.cred.Macro',
                title    : 'Macros',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of Macro',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name']
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]

        result
    }

    Map namespaces() {
        Map result = [
                baseclass: 'org.gokb.cred.IdentifierNamespace',
                title    : 'Namespaces',
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
                        ],
                        qbeGlobals: [
                        ],
                        qbeResults: [
                                [heading: 'Value', property: 'value', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Name', property: 'name'],
                                [heading: 'RDF Datatype', property: 'datatype?.value'],
                                [heading: 'Category', property: 'family'],
                                [heading: 'Target Type', property: 'targetType.value']
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
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
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
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status.value', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on']
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Status', sort: 'status', property: 'status?.value'],
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
                                        filter1    : RCConstants.PACKAGE_LIST_STATUS,
                                        prompt     : 'List Status',
                                        qparam     : 'qp_liststatus',
                                        placeholder: 'List Status',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'listStatus'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.PACKAGE_SCOPE,
                                        prompt     : 'Availability',
                                        qparam     : 'qp_scope',
                                        placeholder: 'Availability',
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
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Provider',
                                        qparam     : 'qp_provider',
                                        placeholder: 'Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'provider'],
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
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Provider', property: 'provider?.name', link: true],
                                [heading: 'Name', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Nominal Platform', property: 'nominalPlatform?.name'],
                                [heading: 'Content Type', property: 'contentType?.value', sort: 'contentType'],
                                [heading: 'Availability', property: 'scope', sort: 'scope'],
                                [heading: 'List Status', property: 'listStatus?.value', sort: 'listStatus'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Titles', property: 'currentTippCount'],
                        ],
                        actions   : [
                                [name: 'Register Web Hook for all Packages', code: 'general::registerWebhook', iconClass: 'glyphicon glyphicon-link']
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
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Primary URL', property: 'primaryUrl'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

    Map projects() {
        Map result = [
                baseclass: 'org.gokb.refine.RefineProject',
                title    : 'Projects',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name',
                                        qparam     : 'qp_name',
                                        placeholder: 'Project Name',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'B']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Name', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Provider', sort: 'provider.name', property: 'provider?.name'],
                                [heading: 'Status', sort: 'status', property: 'status?.value'],
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
                                [heading: 'Description', sort: 'desc', property: 'desc', link: [controller: 'resource', action: 'show', id: 'x.r.className+\':\'+x.r.id']],
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
                        ],
                        qbeResults: [
                                [heading: 'Cause', property: 'descriptionOfCause', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Request', property: 'reviewRequest'],
                                [heading: 'Status', property: 'status?.value'],
                                [heading: 'Type', property: 'type?.value'],
                                [heading: 'Timestamp', property: 'dateCreated', sort: 'dateCreated'],
                        ]
                ]
        ]
        result
    }

    Map rules() {
        Map result = [
                baseclass: 'org.gokb.refine.Rule',
                title    : 'Rules',
                group    : 'Secondary',
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Description',
                                        qparam     : 'qp_description',
                                        placeholder: 'Rule Description',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'description']
                                ],
                        ],
                        qbeResults: [
                                [heading: 'Fingerprint', property: 'fingerprint'],
                                [heading: 'Description', property: 'description', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
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
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'ID', property: 'id', sort: 'id', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Url', property: 'url', sort: 'url'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

    Map titles() {
        Map result = [
                baseclass: 'org.gokb.cred.TitleInstance',
                title    : 'Titles (General)',
                group    : 'Secondary',
                // defaultSort:'name',
                // defaultOrder:'asc',
                // useDistinct: true,
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Name or Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        // contextTree:['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name','wildcard':'R']
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R'] // , normalise:true
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Publisher',
                                        qparam     : 'qp_pub',
                                        placeholder: 'Publisher',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisher'],
                                        hide       : false
                                ],
                                [
                                        prompt     : 'Identifier',
                                        qparam     : 'qp_identifier',
                                        placeholder: 'Identifier Value',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'ids.value'],
                                        hide       : false
                                ],
                                [
                                        prompt     : 'Title Publisher ID',
                                        qparam     : 'qp_pub_id',
                                        placeholder: 'Title Publisher ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publisher.id', 'type': 'java.lang.Long'],
                                        hide       : true
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TITLEINSTANCE_MEDIUM,
                                        prompt     : 'Type',
                                        qparam     : 'qp_medium',
                                        placeholder: 'Medium of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'medium'],
                                        // II: Default not yet implemented
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'status'],
                                        // II: Default not yet implemented
                                        default    : [type: 'query', query: 'select r from RefdataValue where r.value=:v and r.owner.description=:o', params: ['Current', RCConstants.KBCOMPONENT_STATUS]]
                                ],

                                // In order for this to work as users expect, we're going to need a unique clause at the root context, or we get
                                // repeated rows where a wildcard matches multiple titles. [That or this clause needs to be an "exists" caluse]
                                // [
                                //   prompt:'Identifier',
                                //   qparam:'qp_identifier',
                                //   placeholder:'Any identifier',
                                //   contextTree:['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'ids.value','wildcard':'B']
                                // ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'ID', property: 'id', link: [controller: 'resource', action: 'show', id: 'x.r?.class?.name+\':\'+x.r?.id'], sort: 'name'],
                                [heading: 'Name/Title', property: 'name', sort: 'name', link: [controller: 'resource', action: 'show', id: 'x.r?.class?.name+\':\'+x.r?.id']],
                                [heading: 'Type', property: 'medium?.value', sort: 'name'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                                [heading: 'Date Created', property: 'dateCreated', sort: 'dateCreated'],
                                [heading: 'Last Updated', property: 'lastUpdated', sort: 'lastUpdated'],
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
                qbeConfig: [
                        qbeForm   : [
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_title',
                                        placeholder: 'Title',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name'],
                                ],
                                [
                                        prompt     : 'Title ID',
                                        qparam     : 'qp_title_id',
                                        placeholder: 'Title ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'title.id', 'type': 'java.lang.Long'],
                                        hide       : true
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
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.TIPP_PUBLICATION_TYPE,
                                        prompt     : 'Type',
                                        qparam     : 'qp_publicationType',
                                        placeholder: 'Type of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'publicationType'],
                                ],
                                [
                                        type       : 'lookup',
                                        baseClass  : 'org.gokb.cred.Org',
                                        prompt     : 'Content Provider',
                                        qparam     : 'qp_cp',
                                        placeholder: 'Content Provider',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.provider']
                                ],
                                [
                                        prompt     : 'Package ID',
                                        qparam     : 'qp_pkg_id',
                                        placeholder: 'Package ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.id', 'type': 'java.lang.Long'],
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
                                        prompt     : 'Platform ID',
                                        qparam     : 'qp_plat_id',
                                        placeholder: 'Platform ID',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'hostPlatform.id', 'type': 'java.lang.Long'],
                                        hide       : true
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
                                        baseClass  : 'org.gokb.cred.RefdataValue',
                                        filter1    : RCConstants.KBCOMPONENT_STATUS,
                                        prompt     : 'Status',
                                        qparam     : 'qp_status',
                                        placeholder: 'Status',
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
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'eq', 'prop': 'pkg.curatoryGroups'],
                                        hide       : false
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Title Persistent Id', property: 'persistentId', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Title', property: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                                [heading: 'Type', property: 'publicationType?.value', sort: 'publicationType.value'],
                                [heading: 'Medium', property: 'medium?.value', sort: 'medium.value'],
                                [heading: 'Package', qpEquiv: 'qp_pkg_id', property: 'pkg?.name', link: [controller: 'resource', action: 'show', id: 'x.r.pkg?.class?.name+\':\'+x.r.pkg?.id']],
                                [heading: 'Platform', qpEquiv: 'qp_plat_id', property: 'hostPlatform?.name', link: [controller: 'resource', action: 'show', id: 'x.r.hostPlatform?.class?.name+\':\'+x.r.hostPlatform?.id']],
                                [heading: 'Status', property: 'status?.value', sort: 'status.value']
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
                                [heading: 'Username', property: 'username', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
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
                                [heading: 'Name', property: 'displayName', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id']],
                        ]
                ]
        ]

        result
    }

    Map userWatchedComponents() {
        Map result = [
                baseclass   : 'org.gokb.cred.ComponentWatch',
                title       : 'My Components',
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
                                [heading: 'Name', property: 'component.name', link: [controller: 'resource', action: 'show', id: 'x.r.component.class.name+\':\'+x.r.component.id']],
                                [heading: 'Type', property: 'component.niceName'],
                                [heading: 'Last Update on', property: 'component.lastUpdated'],
                                [heading: 'Last Update by', property: 'component.lastUpdatedBy?.username'],
                                [heading: 'Last Update Comment', property: 'component.lastUpdateComment']
                        ]
                ]
        ]
        result
    }

    Map works() {
        Map result = [
                baseclass   : 'org.gokb.cred.Work',
                title       : 'Works',
                group       : 'Primary',
                defaultSort : 'name',
                defaultOrder: 'asc',
                qbeConfig   : [
                        qbeForm   : [
                                [
                                        prompt     : 'Title',
                                        qparam     : 'qp_name',
                                        placeholder: 'Name or title of item',
                                        contextTree: ['ctxtp': 'qry', 'comparator': 'ilike', 'prop': 'name', 'wildcard': 'R']
                                ],
                        ],
                        qbeGlobals: [
                                ['ctxtp' : 'filter', 'prop': 'status', 'comparator': 'eq', 'value': 'Current', 'negate': false, 'prompt': 'Only Current',
                                 'qparam': 'qp_onlyCurrent', 'default': 'on', 'cat': RCConstants.KBCOMPONENT_STATUS, 'type': 'java.lang.Object']
                        ],
                        qbeResults: [
                                [heading: 'Title', property: 'name', link: [controller: 'resource', action: 'show', id: 'x.r.class.name+\':\'+x.r.id'], sort: 'name'],
                                [heading: 'Bucket Hash', property: 'bucketHash'],
                                [heading: 'Status', property: 'status?.value', sort: 'status'],
                        ]
                ]
        ]
        result
    }

}
