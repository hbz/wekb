// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

import com.k_int.TextUtils

println("-- using runtime.groovy config file !!! --")


project_dir = new java.io.File(org.grails.io.support.GrailsResourceUtils.GRAILS_APP_DIR + "/../project-files/").getCanonicalPath() + "/"

//refine_min_version = "3.0.0"

// Config for the refine extension build process.
refine = [
  refineRepoURL           : "https://github.com/OpenRefine/OpenRefine.git",
  refineRepoBranch        : "master",
  refineRepoTagPattern    : /\Q2.6-rc.2\E/,
  refineRepoPath          : "gokb-build/refine",
  gokbRepoURL             : "https://github.com/k-int/gokb-phase1.git",
  gokbRepoBranch          : "release",
  gokbRepoTagPattern      : "\\QCLIENT_\\E(${TextUtils.VERSION_REGEX})",
  gokbRepoTestURL         : "https://github.com/k-int/gokb-phase1.git",
  gokbRepoTestBranch      : "test",
  gokbRepoTestTagPattern  : "\\QTEST_CLIENT_\\E(${TextUtils.VERSION_REGEX})",
  extensionRepoPath       : "gokb-build/extension",
  gokbExtensionPath       : "refine/extensions/gokb",
  gokbExtensionTarget     : "extensions/gokb/",
  refineBuildFile         : "build.xml",
  refineBuildTarget       : null,
  extensionBuildFile      : "build.xml",
  extensionBuildTarget    : "dist",
]


validation.regex.issn = "^\\d{4}\\-\\d{3}[\\dX]\$"
validation.regex.eissn = "^\\d{4}\\-\\d{3}[\\dX]\$"
validation.regex.isbn = "^(97(8|9))?\\d{9}[\\dX]\$"
validation.regex.uri = "^(f|ht)tp(s?):\\/\\/([a-zA-Z\\d\\-\\.])+(:\\d{1,4})?(\\/[a-zA-Z\\d\\-\\._~\\/\\?\\#\\[\\]@\\!\\%\\:\\\$\\&'\\(\\)\\*\\+,;=]*)?\$"
validation.regex.date = "^[1-9][0-9]{3,3}\\-(0[1-9]|1[0-2])\\-(0[1-9]|[1-2][0-9]|3[0-1])\$"
validation.regex.kbartembargo = "^([RP]\\d+[DMY](;?))+\$"
validation.regex.kbartcoveragedepth = "^(\\Qfull ?text\\E|\\Qselected articles\\E|\\Qabstracts\\E|\\Q\\E)\$"
validation.regex.zdb = "^\\d+\\-[\\dX]\$"

// class_one_cols = [:]
// identifiers.class_ones.each { name ->
//   class_one_cols[name] = "${IngestService.IDENTIFIER_PREFIX}${name}"
// }

permNames = [
  1 : [name:'Read', inst:org.springframework.security.acls.domain.BasePermission.READ],
  2 : [name:'Write', inst:org.springframework.security.acls.domain.BasePermission.WRITE],
  4 : [name:'Create', inst:org.springframework.security.acls.domain.BasePermission.CREATE],
  8 : [name:'Delete', inst:org.springframework.security.acls.domain.BasePermission.DELETE],
  16 : [name:'Administration', inst:org.springframework.security.acls.domain.BasePermission.ADMINISTRATION],
]

cosine.good_threshold = 0.75

extensionDownloadUrl = 'https://github.com/k-int/gokb-phase1/wiki/GOKb-Refine-Extensions'

defaultOaiConfig = [
  lastModified:'lastUpdated',
  schemas:[
    'oai_dc':[
      type:'method',
      methodName:'toOaiDcXml',
      schema:'http://www.openarchives.org/OAI/2.0/oai_dc.xsd',
      metadataNamespaces: [
        '_default_' : 'http://www.openarchives.org/OAI/2.0/oai_dc/',
        'dc'        : "http://purl.org/dc/elements/1.1/"
      ]],
    'gokb':[
      type:'method',
      methodName:'toGoKBXml',
      schema:'http://www.gokb.org/schemas/oai_metadata.xsd',
      metadataNamespaces: [
        '_default_': 'http://www.gokb.org/oai_metadata/'
      ]],
  ]
]

apiClasses = [
  "com.k_int.apis.GrailsDomainHelpersApi"
]


waiting {
  timeout = 60
  retryInterval = 0.5
}

cache.headers.presets = [
  "none": false,
  "until_changed": [shared:true, validFor: (3600 * 12)] // cache content for 12 hours.
]

globalSearch = [
  'indices'     : ['wekbtipps', 'wekborgs', 'wekbpackages', 'wekbplatforms'],
  'types'       : 'component',
  'typingField' : 'componentType',
  'port'        : 9300
]

searchApi = [
  'path'        : '/',
  'indices'     : ['wekbtipps', 'wekborgs', 'wekbpackages', 'wekbplatforms', 'wekbdeletedcomponents'],
  'types'       : 'component',
  'typingField' : 'componentType',
  'port'        : 9200
]

//concurrency.pools = [
//  "smallJobs" : [
//    type: 'SingleThreadExecutor'
//  ]
//]

//beans {
//  executorService {
//    executor = Executors.newFixedThreadPool(100)
//  }
//}

// cors.headers = ['Access-Control-Allow-Origin': '*']
// 'Access-Control-Allow-Origin': 'http://xissn.worldcat.org'
//     'My-Custom-Header': 'some value'




identifiers = [
        'class_ones' : [
                'issn',
                'eissn',
                'doi',
                'isbn',
                'issnl',
                'zdb',
                'uri'
        ],

        // Class ones that need to be cross-checked. If an Identifier supplied as an ISSN,
        // is found against a title but as an eISSN we still treat this as a match
        'cross_checks' : [
                ['issn', 'eissn'],
                ['issn', 'issnl'],
                ['eissn', 'issn'],
                ['eissn', 'issnl'],
                ['issnl', 'issn'],
                ['issnl', 'eissn']
        ],

        formatters : [
                'issn' : isxn_formatter,
                'eissn' : isxn_formatter
        ]
]

isxn_formatter = { issn_string ->
    def result = issn_string
    def trimmed = (issn_string?:'').trim()
    if ( trimmed.length() == 8 ) {
        result = issn_string.substring(0,4)+"-"+issn_string.substring(4,8)
    }
    return result;
}


appDefaultPrefs {
    globalDateFormat='yyyy-MM-dd'
}
