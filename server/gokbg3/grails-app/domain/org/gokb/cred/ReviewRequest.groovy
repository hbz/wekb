package org.gokb.cred

import de.wekb.annotations.RefdataAnnotation
import de.wekb.helper.RCConstants

import javax.persistence.Transient
import grails.converters.JSON
import grails.plugins.orm.auditable.Auditable

import java.sql.Ref

class ReviewRequest implements Auditable {

  @Transient
  def springSecurityService

  def allComboPropertyNames = []

  KBComponent componentToReview
  String descriptionOfCause
  String reviewRequest
  RefdataValue status
  RefdataValue stdDesc
  Boolean needsNotify
  String additionalInfo


  // Timestamps
  Date dateCreated
  Date lastUpdated

  @RefdataAnnotation(cat = RCConstants.CURATORY_GROUP_TYPE)
  RefdataValue type

  static mapping = {
    id column:'rr_id'
    descriptionOfCause column:'rr_cause_txt', type:'text'
    reviewRequest column:'rr_req_txt', type:'text'
    additionalInfo column:'rr_additional_info', type:'text'

  }

  static constraints = {
    componentToReview(nullable:false, blank:false)
    descriptionOfCause(nullable:true, blank:true)
    reviewRequest(nullable:false, blank:false)
    status(nullable:false, blank:false)
    stdDesc(nullable:true, blank:false)
    dateCreated(nullable:true, blank:true)
    lastUpdated(nullable:true, blank:true)
    needsNotify(nullable:true, blank:true)
    additionalInfo(nullable:true, blank:true)
  }

  public static ReviewRequest raise (KBComponent forComponent,
                                     String actionRequired,
                                     String cause = null,
                                     refineProject = null,
                                     additionalInfo = null,
                                     RefdataValue stdDesc = null) {

    // Create a request.
    ReviewRequest req = new ReviewRequest (
        status : RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Open'),
        descriptionOfCause : (cause),
        reviewRequest : (actionRequired),
        stdDesc : (stdDesc),
        additionalInfo : (additionalInfo),
        componentToReview : (forComponent)
        ).save(failOnError:true);

    // Just return the request.

    req
  }

  public static final String restPath = "/reviews"

  static jsonMapping = [
    'ignore'       : [
      'additionalInfo',
      'needsNotify'
    ],
    'defaultEmbeds': [
      'allocatedGroups'
    ]
  ]

  String getLogEntityId() {
      "${this.class.name}:${id}"
  }

  @Transient
  def availableActions() {
    [
      [code:'method::RRTransfer', label:'Transfer To...'],
      [code:'method::RRClose', label:'Close']
    ]
  }

  @Transient
  static def globalActions() {
    [
      [code:'method::RRTransfer', label:'Transfer To...'],
      [code:'method::RRClose', label:'Close']
    ]
  }


  public void RRClose(rrcontext) {
    log.debug("Close review request ${id} (${this.class.name}) - user=${rrcontext.user}");

    setStatus(RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Closed'))
    save(failOnError:true)
    log.debug("Changed status - ${status}")
  }

  public String getNiceName() {
    return "Review Request";
  }

  def getAllocatedGroups() {
    return AllocatedReviewGroup.findAllByReview(this)
  }

  def allocateGroup(group) {
    def result = false
    def existing = AllocatedReviewGroup.findAllByReviewAndGroup(this, group)

    if (!existing) {
      AllocatedReviewGroup.create(group, this)
      result = true
    }

    result
  }

  def claim(group) {
    def result = true
    def existing = this.allocatedGroups

    if (existing.collect {it.group == group}?.size() > 0) {
      existing.each { eg ->
        if (eg.group != group && eg.status != null) {
          result = false
        }
      }
    }
    else {
      result = false
    }

    result
  }

  def afterInsert() {

  }

  def beforeUpdate() {
    if ( isDirty('status') ) {
      log.debug("RR Status changed > ${this.status}")
    }
  }

  def beforeValidate() {
    if ( this.id == null && !isDirty('status') ) {
      setStatus(RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Open'))
    }
  }

  def getAdditional() {
    def result = null
    if (additionalInfo && additionalInfo.length() > 0 ) {
      result = JSON.parse(additionalInfo);
    }
    result;
  }

  def getAllocationLog() {
    def result = ReviewRequestAllocationLog.executeQuery("from ReviewRequestAllocationLog where rr = ?",[this])
    result
  }

  @Transient
  public userAvailableActions() {
    def user = springSecurityService.currentUser
    def allActions = []
    def result = []

    if (this.respondsTo('availableActions')) {
      allActions = this.availableActions()

      allActions.each { ao ->
        if (ao.perm in ["delete", "admin", "su"] && !user.hasRole('ROLE_SUPERUSER')) {
        }
        else {
          result.add(ao)
        }
      }
    }
    result
  }

  @Transient
  public String getDomainName() {
    return "Review Request"
  }
}
