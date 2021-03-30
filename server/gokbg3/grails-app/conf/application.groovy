import java.text.SimpleDateFormat

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'org.gokb.cred.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'org.gokb.cred.UserRole'
grails.plugin.springsecurity.authority.className = 'org.gokb.cred.Role'

grails.plugin.springsecurity.ui.forgotPassword.emailFrom = "we:kb<no-reply@gokb.org>"

grails.mime.file.extensions=false

grails.gorm.default.mapping = {
    autowire true
}
grails.plugin.springsecurity.ui.register.postRegisterUrl = '/public/index'

grails {
    plugin {
        auditLog {
            auditDomainClassName = "org.gokb.cred.AuditLogEvent"
            logFullClassName = false
        }
    }
}

// database migration plugin
grails.plugin.databasemigration.updateOnStart = true

grails.plugin.springsecurity.filterChain.chainMap = [
        [pattern: '/login/auth',          filters: 'none'],
        [pattern: '/assets/**',           filters: 'none'],
        [pattern: '/**/js/**',            filters: 'none'],
        [pattern: '/**/css/**',           filters: 'none'],
        [pattern: '/**/images/**',        filters: 'none'],
        [pattern: '/**/favicon.ico',      filters: 'none'],
        [pattern: '/error',               filters: 'none'],
        [pattern: '/ajaxSupport/**',      filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/fwk/**',              filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/api/**',              filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/integration/**',      filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/admin/bulkLoadUsers', filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/rest/login',          filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-basicAuthenticationFilter,-basicExceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'],
        [pattern: '/rest/**/**',          filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-basicAuthenticationFilter,-basicExceptionTranslationFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'],
        [pattern: '/rest/**',             filters: 'JOINED_FILTERS,-exceptionTranslationFilter,-basicAuthenticationFilter,-basicExceptionTranslationFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'],
        [pattern: '/oauth/**',            filters: 'JOINED_FILTERS,-exceptionTranslationFilter'],
        [pattern: '/**',                  filters: 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter,-restTokenValidationFilter,-restExceptionTranslationFilter'],
]

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
  [pattern: '/admin/**',                access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/file/**',                 access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/info',                    access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/monitoring/**',           access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/login/auth',              access: ['permitAll']],
  [pattern: '/greenmail/**',            access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/',                        access: ['permitAll']],
  [pattern: '/index',                   access: ['permitAll']],
  [pattern: '/notFound',                access: ['permitAll']],
  [pattern: '/index.gsp',               access: ['permitAll']],
  [pattern: '/register/**',             access: ['permitAll']],
  [pattern: '/public/**',               access: ['permitAll']],
  [pattern: '/package/**',              access: ['permitAll']],
  [pattern: '/packages/**',             access: ['permitAll']],
  [pattern: '/component/identifierConflicts', access: ['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/public',                  access: ['permitAll']],
  [pattern: '/error',                   access: ['permitAll']],
  [pattern: '/error/**',                access: ['permitAll']],
  [pattern: '/globalSearch/**',         access: ['ROLE_USER']],
  [pattern: '/home/**',                 access: ['ROLE_USER']],
  [pattern: '/assets/**',               access: ['permitAll']],
  [pattern: '/**/js/**',                access: ['permitAll']],
  [pattern: '/**/css/**',               access: ['permitAll']],
  [pattern: '/**/images/**',            access: ['permitAll']],
  [pattern: '/**/favicon.ico',          access: ['permitAll']],
  [pattern: '/api/find',                access: ['permitAll']],
  [pattern: '/api/scroll',              access: ['permitAll']],
  [pattern: '/api/suggest',             access: ['permitAll']],
  [pattern: '/api/esconfig',            access: ['permitAll']],
  [pattern: '/api/capabilities',        access: ['permitAll']],
  [pattern: '/api/downloadUpdate',      access: ['permitAll']],
  [pattern: '/api/checkUpdate',         access: ['permitAll']],
  [pattern: '/api/isUp',                access: ['permitAll']],
  [pattern: '/api/userData',            access: ['permitAll']],
  [pattern: '/api/refdata',             access: ['ROLE_USER']],
  [pattern: '/api/show',                access: ['ROLE_USER']],
  [pattern: '/api/namespaces',          access: ['permitAll']],
  [pattern: '/api/groups',              access: ['permitAll']],
  [pattern: '/api/elasticsearchTunnel', access: ['permitAll']],
  [pattern: '/integration/**',          access: ['permitAll']],
  [pattern: '/fwk/**',                  access: ['ROLE_USER']],
  [pattern: '/user/**',                 access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/user/search',             access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/user/edit/**',            access: ['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/role/**',                 access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/securityInfo/**',         access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/registrationCode/**',     access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/aclClass/**',             access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/aclSid/**',               access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/aclObjectIdentity/**',    access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/aclEntry/**',             access: ['ROLE_SUPERUSER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/oai',                     access: ['permitAll']],
  [pattern: '/oai/**',                  access: ['permitAll']],
  [pattern: '/rest/login',              access: ['permitAll']],
  //[pattern: '/rest/roles',              access: ['permitAll']],
  //[pattern: '/rest/curatoryGroups',     access: ['permitAll']],
  //[pattern: '/rest/curatoryGroups/**',  access: ['permitAll']],
  [pattern: '/rest/refdata',            access: ['permitAll']],
  [pattern: '/rest/refdata/**',         access: ['permitAll']],
  [pattern: '/rest/**',                 access: ['ROLE_USER', 'IS_AUTHENTICATED_FULLY']],
  [pattern: '/oauth/**',                access: ['permitAll']],
  [pattern: '/coreference/**',          access: ['permitAll']]
]


appDefaultPrefs {
  globalDateFormat='yyyy-MM-dd'
}

possible_date_formats = [
    new SimpleDateFormat('yyyy/MM/dd'),
    new SimpleDateFormat('yyyy-MM-dd'),
    new SimpleDateFormat('dd/MM/yyyy'),
    new SimpleDateFormat('dd.MM.yyyy'),
    new SimpleDateFormat('dd/MM/yy'),
    new SimpleDateFormat('yyyy/MM'),
    new SimpleDateFormat('yyyy')
];

isxn_formatter = { issn_string ->
      def result = issn_string
      def trimmed = (issn_string?:'').trim()
      if ( trimmed.length() == 8 ) {
        result = issn_string.substring(0,4)+"-"+issn_string.substring(4,8)
      }
      return result;
    }


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

