package org.gokb.cred


import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import wekb.AutoUpdatePackageInfo
import wekb.PackageArchivingAgency
import org.gokb.GOKbTextUtils

import javax.persistence.Transient
import groovy.util.logging.*
import groovy.time.TimeCategory



@Slf4j
class Package extends KBComponent {

  def dateFormatService
  static def messageService

  // Owens defaults:
  // Status default to 'Current'
  // Scope default to 'Front File'
  // Breakable?: Y
  // Parent?: N // SO: This should not be needed really now. We should be able to test children for empty set.
  // Global?: Y
  // Consistent?: N

  // Refdata
  @RefdataAnnotation(cat = RCConstants.PACKAGE_SCOPE)
  RefdataValue scope
  @RefdataAnnotation(cat = RCConstants.PACKAGE_CONTENT_TYPE)
  RefdataValue contentType
  @RefdataAnnotation(cat = RCConstants.PACKAGE_BREAKABLE)
  RefdataValue breakable
  @RefdataAnnotation(cat = RCConstants.PACKAGE_CONSISTENT)
  RefdataValue consistent
  @RefdataAnnotation(cat = RCConstants.PACKAGE_PAYMENT_TYPE)
  RefdataValue paymentType

  @RefdataAnnotation(cat = RCConstants.PACKAGE_OPEN_ACCESS)
  RefdataValue openAccess

  @RefdataAnnotation(cat = RCConstants.PACKAGE_FILE)
  RefdataValue file

  @RefdataAnnotation(cat = RCConstants.PACKAGE_EDITING_STATUS)
  RefdataValue editingStatus

  String globalNote

  String descriptionURL

/*  private static refdataDefaults = [
    "scope"      : "Front File",
    "breakable"  : "Unknown",
    "consistent" : "Unknown",
    "paymentType": "Unknown"
  ]*/

  static manyByCombo = [
    tipps         : TitleInstancePackagePlatform,
    children      : Package,
    curatoryGroups: CuratoryGroup
  ]

  static hasByCombo = [
    parent         : Package,
    broker         : Org,
    provider       : Org,
    licensor       : Org,
    vendor         : Org,
    nominalPlatform: Platform,
    'previous'     : Package,
    successor      : Package
  ]

  static mappedByCombo = [
    children : 'parent',
    successor: 'previous',
  ]

  static hasOne = [updateToken: UpdateToken]


  static hasMany = [
          nationalRanges : RefdataValue,
          regionalRanges : RefdataValue,
          ddcs : RefdataValue,
          paas : PackageArchivingAgency,
          ids: Identifier,
          autoUpdatePackageInfos: AutoUpdatePackageInfo
  ]

  static mapping = {
    includes KBComponent.mapping
    scope column: 'pkg_scope_rv_fk'
    breakable column: 'pkg_breakable_rv_fk'
    consistent column: 'pkg_consistent_rv_fk'
    paymentType column: 'pkg_payment_type_rv_fk'
    globalNote column: 'pkg_global_note'
    descriptionURL column: 'pkg_descr_url'
    openAccess column: 'pkg_open_access'
    file column: 'pkg_file'
    editingStatus column: 'pkg_editing_status_rv_fk'

    ddcs             joinTable: [
            name:   'package_dewey_decimal_classification',
            key:    'package_fk',
            column: 'ddc_rv_fk', type:   'BIGINT'
    ], lazy: false

    nationalRanges             joinTable: [
            name:   'package_national_range',
            key:    'package_fk',
            column: 'national_range_rv_fk', type:   'BIGINT'
    ], lazy: false

    regionalRanges             joinTable: [
            name:   'package_regional_range',
            key:    'package_fk',
            column: 'regional_range_rv_fk', type:   'BIGINT'
    ], lazy: false
  }

  static constraints = {
    scope(nullable: true, blank: false)
    breakable(nullable: true, blank: false)
    consistent(nullable: true, blank: false)
    paymentType(nullable: true, blank: false)
    globalNote(nullable: true, blank: true)
    openAccess (nullable: true, blank: true)
    file (nullable: true, blank: true)
    editingStatus (nullable: true, blank: true)
    descriptionURL(nullable: true, blank: true)
    name(validator: { val, obj ->
      if (obj.hasChanged('name')) {
        if (val && val.trim()) {
          def status_deleted = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
          def dupes = Package.findAllByNameIlikeAndStatusNotEqual(val, status_deleted);

          if (dupes?.size() > 0 && dupes.any { it != obj }) {
            return ['notUnique']
          }
        }
        else {
          return ['notNull']
        }
      }
    })
    nationalRanges(nullable:true)
    regionalRanges(nullable:true)
    ddcs(nullable:true)
    paas(nullable:true)
  }

  public String getRestPath() {
    return "/packages"
  }

  static jsonMapping = [
    'ignore'       : [
      'updateToken'
    ],
    'es'           : [
      'nominalPlatformUuid': "nominalPlatform.uuid",
      'nominalPlatformName': "nominalPlatform.name",
      'nominalPlatform'    : "nominalPlatform.id",
      'cpname'             : false,
      'provider'           : "provider.id",
      'providerName'       : "provider.name",
      'providerUuid'       : "provider.uuid",
      'titleCount'         : false,
      'paymentType'        : false,
      'file'               : "refdata",
      'editingStatus'      : "refdata",
      'openAccess'         : "refdata",
      'contentType'        : "refdata",
      'scope'              : "refdata"
    ],
    'defaultLinks' : [
      'provider',
      'nominalPlatform',
      'curatoryGroups'
    ],
    'defaultEmbeds': [
      'ids',
      'variantNames',
      'curatoryGroups'
    ]
  ]

  static def refdataFind(params) {
    def result = [];
    def status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, KBComponent.STATUS_DELETED)
    def status_filter = null

    if (params.filter1) {
      status_filter = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, params.filter1)
    }

    params.sort = 'name'

    def ql = null;
    ql = Package.findAllByNameIlikeAndStatusNotEqual("${params.q}%", status_deleted, params)

    if (ql) {
      ql.each { t ->
        if (!status_filter || t.status == status_filter) {
          result.add([id: "${t.class.name}:${t.id}", text: "${t.name}", status: "${t.status?.value}"])
        }
      }
    }

    result
  }


  @Transient
  public getCurrentTippCount() {
    def refdata_status = RDStore.KBC_STATUS_CURRENT
    def combo_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

    int result = Combo.executeQuery("select count(c.id) from Combo as c where c.fromComponent = ? and c.type = ? and c.toComponent.status = ?"
      , [this, combo_tipps, refdata_status])[0]

    result
  }

  @Transient
  public getTippCount() {
    def combo_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

    int result = Combo.executeQuery("select count(c.id) from Combo as c where c.fromComponent = ? and c.type = ?"
            , [this, combo_tipps])[0]

    result
  }

  @Transient
  public getRetiredTippCount() {
    def refdata_status = RDStore.KBC_STATUS_RETIRED
    def combo_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

    int result = Combo.executeQuery("select count(c.id) from Combo as c where c.fromComponent = ? and c.type = ? and c.toComponent.status = ?"
            , [this, combo_tipps, refdata_status])[0]

    result
  }

  @Transient
  public getExpectedTippCount() {
    def refdata_status = RDStore.KBC_STATUS_EXPECTED
    def combo_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

    int result = Combo.executeQuery("select count(c.id) from Combo as c where c.fromComponent = ? and c.type = ? and c.toComponent.status = ?"
            , [this, combo_tipps, refdata_status])[0]

    result
  }

  @Transient
  public getDeletedTippCount() {
    def refdata_status = RDStore.KBC_STATUS_DELETED
    def combo_tipps = RefdataCategory.lookup(RCConstants.COMBO_TYPE, 'Package.Tipps')

    int result = Combo.executeQuery("select count(c.id) from Combo as c where c.fromComponent = ? and c.type = ? and c.toComponent.status = ?"
            , [this, combo_tipps, refdata_status])[0]

    result
  }

  @Transient
  public getReviews(def onlyOpen = true, def onlyCurrent = false) {
    def all_rrs = null
    def refdata_current = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Current');

   /* if (onlyOpen) {

      log.debug("Looking for more ReviewRequests connected to ${this}")

      def refdata_open = RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Open');

      if (onlyCurrent) {
        all_rrs = ReviewRequest.executeQuery('''select distinct rr
          from ReviewRequest as rr,
            TitleInstance as title,
            Combo as pkgCombo,
            Combo as titleCombo,
            TitleInstancePackagePlatform as tipp
          where pkgCombo.toComponent=tipp
            and pkgCombo.fromComponent=?
            and tipp.status = ?
            and titleCombo.toComponent=tipp
            and titleCombo.fromComponent=title
            and rr.componentToReview = title
            and rr.status = ?'''
          , [this, refdata_current, refdata_open]);
      }
      else {
        all_rrs = ReviewRequest.executeQuery('''select distinct rr
          from ReviewRequest as rr,
            TitleInstance as title,
            Combo as pkgCombo,
            Combo as titleCombo,
            TitleInstancePackagePlatform as tipp
          where pkgCombo.toComponent=tipp
            and pkgCombo.fromComponent=?
            and titleCombo.toComponent=tipp
            and titleCombo.fromComponent=title
            and rr.componentToReview = title
            and rr.status = ?'''
          , [this, refdata_open]);
      }
    }
    else {
      if (onlyCurrent) {
        all_rrs = ReviewRequest.executeQuery('''select rr
          from ReviewRequest as rr,
            TitleInstance as title,
            Combo as pkgCombo,
            Combo as titleCombo,
            TitleInstancePackagePlatform as tipp
          where pkgCombo.toComponent=tipp
            and pkgCombo.fromComponent=?
            and tipp.status = ?
            and titleCombo.toComponent=tipp
            and titleCombo.fromComponent=title
            and rr.componentToReview = title'''
          , [this, refdata_current]);
      }
      else {
        all_rrs = ReviewRequest.executeQuery('''select rr
          from ReviewRequest as rr,
            TitleInstance as title,
            Combo as pkgCombo,
            Combo as titleCombo,
            TitleInstancePackagePlatform as tipp
          where pkgCombo.toComponent=tipp
            and pkgCombo.fromComponent=?
            and titleCombo.toComponent=tipp
            and titleCombo.fromComponent=title
            and rr.componentToReview = title'''
          , [this]);
      }
    }*/

    return all_rrs;
  }

  private static OAI_PKG_CONTENTS_QRY = '''
select tipp.id,
       title.name,
       title.id,
       plat.name,
       plat.id,
       tipp.url,
       tipp.status,
       tipp.accessStartDate,
       tipp.accessEndDate,
       plat.primaryUrl,
       tipp.lastUpdated,
       tipp.uuid,
       title.uuid,
       plat.uuid,
       title.status,
       tipp.series,
       tipp.subjectArea,
       tipp.name,
       tipp.publisherName,
       tipp.dateFirstInPrint,
       tipp.dateFirstOnline
    from TitleInstancePackagePlatform as tipp,
         Combo as hostPlatformCombo,
         Combo as titleCombo,
         Combo as pkgCombo,
         Platform as plat
    where pkgCombo.toComponent=tipp
      and pkgCombo.fromComponent= ?
      and pkgCombo.type= ?
      and hostPlatformCombo.toComponent=tipp
      and hostPlatformCombo.type = ?
      and hostPlatformCombo.fromComponent = plat
      and titleCombo.toComponent=tipp
      and titleCombo.type = ?
      and titleCombo.fromComponent=title
    order by tipp.id''';

  public void deleteSoft(context) {
    // Call the delete method on the superClass.
    super.deleteSoft(context)

    // Delete the tipps too as a TIPP should not exist without the associated,
    // package.
    def tipps = getTipps()
    Date now = new Date()

    if (tipps?.size() > 0) {
      def deleted_status = RefdataCategory.lookup(RCConstants.KBCOMPONENT_STATUS, 'Deleted')
      def tipp_ids = tipps?.collect { it.id }

      TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :del, t.lastUpdateComment = 'Deleted via Package delete', t.lastUpdated = :now where t.status != :del and t.id IN (:ttd)", [del: deleted_status, ttd: tipp_ids, now: now])
    }
  }


  public void retire(context) {
    log.debug("package::retire");
    // Call the delete method on the superClass.
    log.debug("Updating package status to retired");
    def retired_status = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Retired');
    this.status = retired_status
    this.save();

    // Delete the tipps too as a TIPP should not exist without the associated,
    // package.
    log.debug("Retiring tipps");

    def tipps = getTipps()
    Date now = new Date()

    if (tipps?.size() > 0) {
      def tipp_ids = tipps?.collect { it.id }

      TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :ret, t.lastUpdateComment = 'Retired via Package retire', t.lastUpdated = :now where t.id IN (:ttd)", [ret: retired_status, ttd: tipp_ids, now: now])
    }
  }

  public void removeWithTipps() {
    log.debug("package::removeWithTipps");
    log.debug("Updating package status to removed");
    def removedStatus = RDStore.KBC_STATUS_REMOVED
    this.status = removedStatus
    this.save()

    log.debug("removed tipps")

    def tipps = getTipps()
    Date now = new Date()

    if (tipps?.size() > 0) {
      def tipp_ids = tipps?.collect { it.id }

      TitleInstancePackagePlatform.executeUpdate("update TitleInstancePackagePlatform as t set t.status = :ret, t.lastUpdateComment = 'Removed via Package action removeWithTipps', t.lastUpdated = :now where t.id IN (:ttd)", [ret: removedStatus, ttd: tipp_ids, now: now])
    }
  }


  @Transient
  def availableActions() {
    [
      [code: 'method::deleteSoft', label: 'Delete Package (with associated Titles)', perm: 'delete'],
      [code: 'method::retire', label: 'Retire Package (with associated Titles)'],
      [code: 'method::removeWithTipps', label: 'Remove Package (with associated Titles)', perm: 'delete'],
      /*[code: 'verifyTitleList', label: 'Verify Title List'],*/
      [code: 'packageUrlUpdate', label: 'Trigger Update (Changed Titles)'],
      [code: 'packageUrlUpdateAllTitles', label: 'Trigger Update (all Titles)']
    ]
  }

  @Transient
  static def oaiConfig = [
    id             : 'packages',
    textDescription: 'Package repository for GOKb',
    query          : " from Package as o ",
    curators       : 'Package.CuratoryGroups',
    pageSize       : 3
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

    log.debug("toGoKBXml... ${this.class.name}:${id}");

    def refdata_package_tipps = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Package.Tipps');
    def refdata_hosted_tipps = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'Platform.HostedTipps');
    def refdata_ti_tipps = RefdataCategory.lookupOrCreate(RCConstants.COMBO_TYPE, 'TitleInstance.Tipps');
    def refdata_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Deleted');

    // log.debug("Running package contents qry : ${OAI_PKG_CONTENTS_QRY}");

    // Get the tipps manually rather than iterating over the collection - For better management
    def tipps = this.status != refdata_deleted ? TitleInstancePackagePlatform.executeQuery(OAI_PKG_CONTENTS_QRY, [this, refdata_package_tipps, refdata_hosted_tipps, refdata_ti_tipps], [readOnly: true]) : []

    log.debug("Query complete...");

    builder.'gokb'(attr) {
      builder.'package'(['id': (id), 'uuid': (uuid)]) {
        addCoreGOKbXmlFields(builder, attr)

        'scope'(scope?.value)
        'breakable'(breakable?.value)
        'consistent'(consistent?.value)
        'paymentType'(paymentType?.value)
        'globalNote'(globalNote)
        'contentType'(contentType?.value)
        'openAccess'(openAccess?.value)
        'file'(file?.value)
        'editingStatus'(editingStatus?.value)

        if (nominalPlatform) {
          builder.'nominalPlatform'([id: nominalPlatform.id, uuid: nominalPlatform.uuid]) {
            'primaryUrl'(nominalPlatform.primaryUrl)
            'name'(nominalPlatform.name)
          }
        }

        if (provider) {
          builder.'nominalProvider'([id: provider.id, uuid: provider.uuid]) {
            'name'(provider.name)
          }
        }

        builder.'curatoryGroups' {
          curatoryGroups.each { cg ->
            builder.'group' {
              builder.'name'(cg.name)
            }
          }
        }

        'dateCreated'(dateFormatService.formatIsoTimestamp(dateCreated))
        'TIPPs'(count: tipps?.size()) {
          tipps.each { tipp ->
            builder.'TIPP'(['id': tipp[0], 'uuid': tipp[12]]) {
              builder.'status'(tipp[6]?.value)
              builder.'name'(tipp[18])
              builder.'lastUpdated'(tipp[11] ? dateFormatService.formatIsoTimestamp(tipp[11]) : null)
              builder.'series'(tipp[16])
              builder.'subjectArea'(tipp[17])
              builder.'publisherName'(tipp[19])
              builder.'dateFirstInPrint'(tipp[20])
              builder.'dateFirstOnline'(tipp[21])
              builder.'medium'(tipp[9]?.value)
              builder.'title'(['id': tipp[2], 'uuid': tipp[13]]) {
                builder.'name'(tipp[1]?.trim())
                builder.'type'(getTitleClass(tipp[2]))
                builder.'status'(tipp[15]?.value)
              }
              'platform'([id: tipp[4], 'uuid': tipp[14]]) {
                'primaryUrl'(tipp[10]?.trim())
                'name'(tipp[3]?.trim())
              }
              'access'(start: tipp[7] ? dateFormatService.formatIsoTimestamp(tipp[7]) : null, end: tipp[8] ? dateFormatService.formatIsoTimestamp(tipp[8]) : null)
              def cov_statements = getCoverageStatements(tipp[0])
              if (cov_statements?.size() > 0) {
                cov_statements.each { tcs ->
                  'coverage'(
                    startDate: (tcs.startDate ? dateFormatService.formatIsoTimestamp(tcs.startDate) : null),
                    startVolume: (tcs.startVolume),
                    startIssue: (tcs.startIssue),
                    endDate: (tcs.endDate ? dateFormatService.formatIsoTimestamp(tcs.endDate) : null),
                    endVolume: (tcs.endVolume),
                    endIssue: (tcs.endIssue),
                    coverageDepth: (tcs.coverageDepth?.value ?: null),
                    coverageNote: (tcs.coverageNote),
                    embargo: (tcs.embargo)
                  )
                }
              }
              'url'(tipp[5] ?: "")
            }
          }
        }
      }
    }

    log.debug("toGoKBXml complete...");
  }

  @Transient
  private static getTitleClass(Long title_id) {
    def result = KBComponent.get(title_id)?.class.getSimpleName();

    result
  }

  @Transient
  private static getCoverageStatements(Long tipp_id) {
    def result = TIPPCoverageStatement.executeQuery("from TIPPCoverageStatement as tcs where tcs.owner.id = :tipp", ['tipp': tipp_id], [readOnly: true])
    result
  }

  @Transient
  public getRecentActivity() {
    def result = [];

    if (this.id) {
      def status_deleted = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_STATUS, 'Deleted')

      // select tipp, accessStartDate, 'Added' from tipps UNION select tipp, accessEndDate, 'Removed' order by date

//       def additions = TitleInstancePackagePlatform.executeQuery('select tipp, tipp.accessStartDate, \'Added\' ' +
//                        'from TitleInstancePackagePlatform as tipp, Combo as c '+
//                        'where c.fromComponent=? and c.toComponent=tipp and tipp.accessStartDate is not null order by tipp.dateCreated DESC',
//                       [this], [max:n]);
//       def deletions = TitleInstancePackagePlatform.executeQuery('select tipp, tipp.accessEndDate, \'Removed\' ' +
//                        'from TitleInstancePackagePlatform as tipp, Combo as c '+
//                        'where c.fromComponent= :pkg and c.toComponent=tipp and tipp.accessEndDate is not null order by tipp.lastUpdated DESC',
//                        [pkg: this], [max:n]);

      def changes = TitleInstancePackagePlatform.executeQuery('select tipp from TitleInstancePackagePlatform as tipp, Combo as c ' +
        'where c.fromComponent= ? and c.toComponent=tipp',
        [this]);

      use(TimeCategory) {
        changes.each {
          if (it.isDeleted()) {
            result.add([it, it.lastUpdated, 'Deleted (Status)'])
          }
          else if (it.isRetired()) {
            result.add([it, it.lastUpdated, it.accessEndDate ? "Retired (${it.accessEndDate})" : 'Retired (Status)'])
          }
          else if (it.lastUpdated <= it.dateCreated + 1.minute) {
            result.add([it, it.dateCreated, it.accessStartDate ? "Added (${it.accessStartDate})" : 'Newly Added'])
          }
          else {
            result.add([it, it.lastUpdated, 'Updated'])
          }
        }
      }

//       result.addAll(additions)
//       result.addAll(deletions)
      result.sort { it[1] }
      result = result.reverse();
      //result = result.take(n);
    }

    return result;
  }

  public void addCuratoryGroupIfNotPresent(String cgname) {
    boolean add_needed = true;
    curatoryGroups.each { cgtest ->
      if (cgtest.name.equalsIgnoreCase(cgname))
        add_needed = false;
    }

    if (add_needed) {
      def cg = CuratoryGroup.findByName(cgname) ?: new CuratoryGroup(name: cgname).save(flush: true, failOnError: true);
      curatoryGroups.add(cg);
    }
  }



  @Transient
  public getAutoUpdateJobResult() {
    def result = []

    if (this.id) {
      result = JobResult.executeQuery('select jobR from JobResult as jobR where jobR.linkedItemId = :packageID and jobR.type in (:types) order by jobR.startTime desc',
              [packageID: this.id,
              types: [RefdataCategory.lookup(RCConstants.JOB_TYPE, 'PackageCrossRef Auto')]])
    }
    return result
  }

  void createCoreIdentifiersIfNotExist(){
     boolean isChanged = false
      ['Anbieter_Produkt_ID'].each{ coreNs ->
        if ( ! ids.find {it.namespace.value == coreNs}){
          addOnlySpecialIdentifiers(coreNs, 'Unknown')
          isChanged = true
        }
      }
      if (isChanged) refresh()
  }

  void addOnlySpecialIdentifiers(String ns, String value) {
    boolean found = false
    this.ids.each {
      if ( it.namespace?.value == ns && it.value == value ) {
        found = true
      }
    }

    if ( !found && value != '') {
      value = value?.trim()
      ns = ns.trim()
      RefdataCategory refdataCategory = RefdataCategory.findByDesc(RCConstants.IDENTIFIER_NAMESPACE_TARGET_TYPE)

      IdentifierNamespace namespace = IdentifierNamespace.findByValueIlikeAndTargetType(ns, RefdataValue.findByValueAndOwner('Package', refdataCategory))
      Identifier identifier = new Identifier(namespace: namespace, value: value, pkg: this).save()

    }
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippDuplicatesByName() {

    List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp, Combo as pkg_combo" +
            " where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg and tipp.status != :removed and" +
            " tipp.name in (select tipp2.name from TitleInstancePackagePlatform tipp2, Combo as pkg_combo2 where pkg_combo2.toComponent=tipp2 and pkg_combo2.fromComponent = :pkg and tipp2.status != :removed group by tipp2.name having count(tipp2.name) > 1)" +
            " order by tipp.name",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippDuplicatesByURL() {

    List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp, Combo as pkg_combo" +
            " where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg and tipp.status != :removed and" +
            " tipp.url in (select tipp2.url from TitleInstancePackagePlatform tipp2, Combo as pkg_combo2 where pkg_combo2.toComponent=tipp2 and pkg_combo2.fromComponent = :pkg and tipp2.status != :removed group by tipp2.url having count(tipp2.url) > 1)" +
            " order by tipp.url",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
  }

  @Transient
  List<TitleInstancePackagePlatform> findTippDuplicatesByTitleID() {

    IdentifierNamespace identifierNamespace = this.source ? this.source.targetNamespace : null

    if(identifierNamespace) {
      List<TitleInstancePackagePlatform> tippsDuplicates = TitleInstancePackagePlatform.executeQuery("select tipp from TitleInstancePackagePlatform as tipp join tipp.ids as ident, Combo as pkg_combo" +
              " where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg and tipp.status != :removed " +
              " and ident.value in (select ident2.value FROM Identifier AS ident2, TitleInstancePackagePlatform as tipp2, Combo as pkg_combo WHERE ident2.namespace = :namespace and ident2.tipp = tipp2 and pkg_combo.toComponent=tipp2 and pkg_combo.fromComponent = :pkg and tipp2.status != :removed" +
              " group by ident2.value having count(ident2.value) > 1) order by ident.value",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED]) ?: []
    }else {
      return []
    }
  }

  @Transient
  Integer getTippDuplicatesByNameCount() {

    int result = TitleInstancePackagePlatform.executeQuery("select count(tipp.id) from TitleInstancePackagePlatform as tipp, Combo as pkg_combo" +
            " where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg and tipp.status != :removed and" +
            " tipp.name in (select tipp2.name from TitleInstancePackagePlatform tipp2, Combo as pkg_combo2 where pkg_combo2.toComponent=tipp2 and pkg_combo2.fromComponent = :pkg and tipp2.status != :removed group by tipp2.name having count(tipp2.name) > 1)",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED])[0]
    return result
  }

  @Transient
  Integer getTippDuplicatesByURLCount() {

    int result = TitleInstancePackagePlatform.executeQuery("select count(tipp.id) from TitleInstancePackagePlatform as tipp, Combo as pkg_combo" +
            " where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg and tipp.status != :removed and " +
            " tipp.url in (select tipp2.url from TitleInstancePackagePlatform tipp2, Combo as pkg_combo2 where pkg_combo2.toComponent=tipp2 and pkg_combo2.fromComponent = :pkg and tipp2.status != :removed group by tipp2.url having count(tipp2.url) > 1)",
            [pkg: this, removed: RDStore.KBC_STATUS_REMOVED])[0]

    return result
  }

  @Transient
  Integer getTippDuplicatesByTitleIDCount() {
    IdentifierNamespace identifierNamespace = this.source ? this.source.targetNamespace : null

    if(identifierNamespace) {
      int result = TitleInstancePackagePlatform.executeQuery("select count(tipp.id) from TitleInstancePackagePlatform as tipp join tipp.ids as ident, Combo as pkg_combo" +
              " where pkg_combo.toComponent=tipp and pkg_combo.fromComponent = :pkg and tipp.status != :removed " +
              " and ident.value in (select ident2.value FROM Identifier AS ident2, TitleInstancePackagePlatform as tipp2, Combo as pkg_combo WHERE ident2.namespace = :namespace and ident2.tipp = tipp2 and pkg_combo.toComponent=tipp2 and pkg_combo.fromComponent = :pkg and tipp2.status != :removed" +
              " group by ident2.value having count(ident2.value) > 1)",
              [pkg: this, namespace: identifierNamespace, removed: RDStore.KBC_STATUS_REMOVED])[0]
      return result
    }else {
      return 0
    }
  }

  @Transient
  String getIdentifierValue(idtype){
    // Null returned if no match.
    ids?.find{ it.namespace.value.toLowerCase() == idtype.toLowerCase() }?.value
  }

  @Transient
  public String getDomainName() {
   return "Package"
  }

  @Transient
  public String getAnbieterProduktIDs() {
    return ids.findAll{it.namespace.value == 'Anbieter_Produkt_ID' && it.value != 'Unknown'}.value.join(', ')
  }

}
