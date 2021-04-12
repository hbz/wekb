
println("------- Using local config from ~/.grails/wekb-config.groovy ------- ")

grails.mail.host = 'localhost'
grails.mail.port = 25

gokb.alerts.emailFrom = 'localhost@localhost.de'
gokb.alerts.subject = 'Your we:kb User Alerts'
gokb.anonymizeUsers = false
gokb.costInfo = false
gokb.decisionSupport.active = false
gokb.decisionSupport.otherVoters = false
gokb.defaultCuratoryGroup = Local
gokb.es.cluster = 'gokbes'
gokb.es.host = 'localhost'
gokb.ftupdate_enabled = false
gokb.languagesUrl = 'localhost/languages'
gokb.masterListGenerationEnabled = false
gokb.packageUpdate.enabled = false
gokb.enable_statsrewrite = false
gokb.tsvExportTempDirectory = '/tmp'
gokb.ygorUrl = 'localhost/ygor'

deployBackupLocation = ''
pgDumpPath = ''

dataSource.username = 'gokb'
dataSource.password = 'gokb'
dataSource.url = 'jdbc:postgresql://localhost:5432/gokb'

serverUrl= 'http://localhost:8080/gokb'
baseUrl= 'http://localhost:8080/gokb'
server.contextPath = '/gokb'

systemId = 'we:kb-Dev'

logSql = true
formatSql = true


