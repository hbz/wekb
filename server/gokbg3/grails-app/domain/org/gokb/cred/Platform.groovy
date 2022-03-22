package org.gokb.cred

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants

import javax.persistence.Transient
import groovy.util.logging.*
import org.gokb.GOKbTextUtils

@Slf4j
class Platform extends KBComponent {

  String primaryUrl

  IdentifierNamespace titleNamespace

  RefdataValue authentication

  @RefdataAnnotation(cat = RCConstants.PLATFORM_IP_AUTH)
  RefdataValue ipAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue shibbolethAuthentication

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue passwordAuthentication

  @RefdataAnnotation(cat = RCConstants.PLATFORM_STATISTICS_FORMAT)
  RefdataValue statisticsFormat

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR3Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR4Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR5Supported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR4SushiApiSupported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterR5SushiApiSupported

  String counterR4SushiServerUrl

  String counterR5SushiServerUrl
  String counterRegistryUrl

  String statisticsAdminPortalUrl

  @RefdataAnnotation(cat = RCConstants.PLATFORM_STATISTICS_UPDATE)
  RefdataValue statisticsUpdate

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue proxySupported

  @RefdataAnnotation(cat = RCConstants.YN)
  RefdataValue counterCertified

  Date lastAuditDate

  static hasMany = [roles: RefdataValue]

  static hasByCombo = [
    provider: Org
  ]

/*  private static refdataDefaults = [
    "authentication": "Unknown"
  ]*/

  static manyByCombo = [
    hostedPackages: Package,
    hostedTipps   : TitleInstancePackagePlatform,
    linkedTipps   : TitleInstancePackagePlatform,
    hostedTitles  : TitleInstancePlatform,
    curatoryGroups: CuratoryGroup
  ]

  static mappedByCombo = [
    hostedPackages: 'nominalPlatform'
  ]

  static mapping = {
    includes KBComponent.mapping
    primaryUrl column: 'plat_primary_url', index: 'platform_primary_url_idx'
    authentication column: 'plat_authentication_fk_rv'
    ipAuthentication column: 'plat_auth_by_ip_fk_rv'
    shibbolethAuthentication column: 'plat_auth_by_shib_fk_rv'
    passwordAuthentication column: 'plat_auth_by_pass_fk_rv'
    statisticsFormat column: 'plat_statistics_format_fk_rv'
    counterR3Supported column: 'plat_counter_r3_supported_fk_rv'
    counterR4Supported column: 'plat_counter_r4_supported_fk_rv'
    counterR5Supported column: 'plat_counter_r5_supported_fk_rv'
    counterR4SushiApiSupported column: 'plat_counter_r4_sushi_api_supported_fk_rv'
    counterR5SushiApiSupported column: 'plat_counter_r5_sushi_api_supported_fk_rv'
    counterR4SushiServerUrl column: 'plat_counter_r4_sushi_server_url'
    counterR5SushiServerUrl column: 'plat_counter_r5_sushi_server_url'
    counterRegistryUrl column: 'plat_counter_registry_url'
    counterCertified column: 'plat_counter_certified'
    statisticsAdminPortalUrl column: 'plat_statistics_admin_portal_url'
    statisticsUpdate column: 'plat_statistics_update_fk_rv'
    proxySupported column: 'plat_proxy_supported_fk_rv'
    titleNamespace column: 'plat_title_namespace_fk'
    lastAuditDate column: 'plat_last_audit_date'

  }

  static constraints = {
    primaryUrl(url: true, nullable: true, blank: false)
    authentication(nullable: true, blank: false)
    ipAuthentication(nullable: true, blank: false)
    shibbolethAuthentication(nullable: true, blank: false)
    passwordAuthentication(nullable: true, blank: false)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
          def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
          def dupes = Platform.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);
          if (dupes?.size() > 0 && dupes.any {it != obj}) {
            return ['notUnique']
          }
        } else {
          return ['notNull']
        }
      }
    })
    statisticsFormat(nullable: true, blank: false)
    counterR3Supported(nullable: true, blank: false)
    counterR4Supported(nullable: true, blank: false)
    counterR5Supported(nullable: true, blank: false)
    counterR4SushiApiSupported(nullable: true, blank: false)
    counterR5SushiApiSupported(nullable: true, blank: false)
    counterR4SushiServerUrl(nullable: true, blank: false)
    counterR5SushiServerUrl(nullable: true, blank: false)
    counterRegistryUrl(nullable: true, blank: false)
    counterCertified(nullable: true, blank: false)
    statisticsAdminPortalUrl(nullable: true, blank: false)
    statisticsUpdate(nullable: true, blank: false)
    proxySupported(nullable: true, blank: false)
    titleNamespace(nullable: true)
    lastAuditDate (nullable: true)

  }

  public static final String restPath = "/platforms"

  static jsonMapping = [
    'ignore'       : [
    ],
    'es'           : [
      'providerUuid': "provider.uuid",
      'providerName': "provider.name",
      'provider'    : "provider.id"
    ],
    'defaultLinks' : [
      'provider',
      'curatoryGroups'
    ],
    'defaultEmbeds': [
      'ids',
      'variantNames',
      'curatoryGroups'
    ]
  ]

  @Transient
  static def oaiConfig = [
    id             : 'platforms',
    textDescription: 'Platform repository for GOKb',
    query          : " from Platform as o ",
    curators       : 'Platform.CuratoryGroups',
    statusFilter   : ["Deleted"]
  ]

  /**
   *  Render this package as OAI_dc
   */
  @Transient
  def toOaiDcXml(builder, attr) {
    builder.'dc'(attr) {
      'dc:title'(name)
    }
  }

  /**
   *  Render this package as GoKBXML
   */
  @Transient
  def toGoKBXml(builder, attr) {
    def identifiers = getIds()

    builder.'gokb'(attr) {
      builder.'platform'(['id': (id), 'uuid': (uuid)]) {

        addCoreGOKbXmlFields(builder, attr)

        builder.'primaryUrl'(primaryUrl)
        builder.'authentication'(authentication?.value)

        if (ipAuthentication) builder.'ipAuthentication'(ipAuthentication.value)
        if (shibbolethAuthentication) builder.'shibbolethAuthentication'(shibbolethAuthentication.value)
        if (passwordAuthentication) builder.'passwordAuthentication'(passwordAuthentication.value)

        if (provider) {
          builder.'provider'([id: provider.id, uuid: (provider.uuid)]) {
            builder.'name'(provider.name)
          }
        }
        if (roles) {
          builder.'roles' {
            roles.each { role ->
              builder.'role'(role.value)
            }
          }
        }

        builder.curatoryGroups {
          curatoryGroups.each { cg ->
            builder.group {
              builder.name(cg.name)
            }
          }
        }
      }
    }
  }

  static def refdataFind(params) {
    def result = [];
    def status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED)
    def status_filter = null

    if (params.filter1) {
      status_filter = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, params.filter1)
    }

    params.sort = 'name'

    def ql = null;
    ql = Platform.findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted, params)

    if (ql) {
      ql.each { t ->
        if (!status_filter || t.status == status_filter) {
          result.add([id: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
        }
      }
    }

    result
  }

  def availableActions() {
    [
      [code: 'platform::replacewith', label: 'Replace platform with...', perm: 'admin'],
      [code: 'method::deleteSoft', label: 'Delete Platform', perm: 'delete'],
      [code: 'method::retire', label: 'Retire Platform (with hosted TIPPs)', perm: 'admin']
    ]
  }

  /**
   *{*    name:'name',
   *    platformUrl:'platformUrl',
   *}*/

  public void retire(context) {
    log.debug("platform::retire");
    // Call the delete method on the superClass.
    log.debug("Updating platform status to retired");
    this.status = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Retired');
    this.save();

    // Delete the tipps too as a TIPP should not exist without the associated,
    // package.
    log.debug("Retiring tipps");
    def tipps = getHostedTipps()

    tipps.each { def t ->
      log.debug("deroxy ${t} ${t.class.name}");

      // SO: There are 2 deproxy methods. One in the static context that takes in an argument and one,
      // against an instance which attempts to deproxy this component. Calling deproxy(t) here will invoke the method
      // against the current package. this.deproxy(t).
      // So Package.deproxy(t) or t.deproxy() should work...
      def tipp = Package.deproxy(t)
      log.debug("Retiring tipp ${tipp.id}");
      tipp.status = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Retired');
      tipp.save()
    }
  }

  @Transient
  public getCurrentTippCount() {
    def refdata_current = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Current');
    def combo_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Platform.HostedTipps')

    int result = Combo.executeQuery("select count(c.id) from Combo as c where c.fromComponent = ? and c.type = ? and c.toComponent.status = ?"
            , [this, combo_tipps, refdata_current])[0]

    result
  }
}
