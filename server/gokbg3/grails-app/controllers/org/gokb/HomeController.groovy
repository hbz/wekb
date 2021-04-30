package org.gokb

import de.wekb.helper.RCConstants
import org.hibernate.SessionFactory
import org.springframework.security.access.annotation.Secured
import grails.util.GrailsNameUtils
import grails.converters.JSON
import org.gokb.cred.*
import org.hibernate.transform.AliasToEntityMapResultTransformer


class HomeController {

  def springSecurityService
  def userAlertingService
  def passwordEncoder
  SessionFactory sessionFactory

  static stats_cache = null;
  static stats_timestamp = null;

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def statistic() {
    if ( ( stats_timestamp == null )|| ( System.currentTimeMillis() - stats_timestamp > 3600000 ) || params.reset) {
      stats_timestamp = System.currentTimeMillis()
      // Initialise
      stats_cache = [widgets:[:]];
      stats_cache = calculate();
    }
    else {
      log.debug("stats from cache (${System.currentTimeMillis() - stats_timestamp})...");
    }

    return stats_cache
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def index () {
    log.debug("Home::index -- ${params}")

    redirect(action:'statistic')
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def userdash() {
    def result = [:]
    result.user = springSecurityService.currentUser

    result.saved_items = SavedSearch.executeQuery('Select ss from SavedSearch as ss where ss.owner = :user order by name',[user: result.user])

    result
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def dash() {
    def result=[:]

    User user = springSecurityService.currentUser
    def active_status = RefdataCategory.lookupOrCreate(RCConstants.ACTIVITY_STATUS, 'Active')
    def needs_review_status = RefdataCategory.lookupOrCreate(RCConstants.REVIEW_REQUEST_STATUS, 'Needs Review')

    result.openActivities = Activity.findAllByOwnerAndStatus(user,active_status)
    result.recentlyClosedActivities = Activity.findAllByOwnerAndStatusNotEqual(user,active_status,[max: 10, sort: "lastUpdated", order: "desc"])

    result
  }

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def about() {
    def result = [:]
    def dbmQuery = (sessionFactory.currentSession.createSQLQuery(
            'SELECT filename, id, dateexecuted from databasechangelog order by orderexecuted desc limit 1'
    )).list()
    result.dbmVersion = dbmQuery.size() > 0 ? dbmQuery.first() : ['unkown', 'unkown', 'unkown']
    result

  }

  def releaseNotes() {
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def profile() {
    def result = [:]
    User user = springSecurityService.currentUser

    withFormat {
      html {
        result.user = user
        result.curator = [user]
        result.editable = true

        result
      }
      json {
        def cur_groups = []

        user.curatoryGroups?.each { cg ->
          cur_groups.add([name: cg.name, id: cg.id, uuid: cg.uuid])
        }

        result = ['id': user.id, 'username': user.username, 'displayName': user.displayName, 'email': user.email, 'curatoryGroups': cur_groups]
        render result as JSON
      }
    }
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def preferences() {
    def result = [:]
    User user = springSecurityService.currentUser
    result.user = user
    result.editable = true

    result
  }

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def changePass() {
    if ( params.newpass == params.repeatpass ) {
      User user = springSecurityService.currentUser
      if ( passwordEncoder.isPasswordValid(user.password, params.origpass, null) ) {
        user.password = params.newpass
        user.save(flush:true, failOnError:true);
        flash.success = "Password Changed!"
      }
      else {
        flash.error = "Existing password does not match: not changing"
      }
    }
    else {
      flash.error = "New password does not match repeat password: not changing"
    }
    redirect(action:'profile')
  }


  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def sendAlerts() {
    flash.message ="Alerts email sent, please check your email shortly";
    User user = springSecurityService.currentUser
    userAlertingService.sendAlertingEmail(user);
    redirect(action:'profile')
  }

  private def calculate() {
    log.debug("Calculating stats...");

    // The defaults for these widgets.
    def colors=[numNew: '#FF0000', numTotal: '#0000FF']
    def result=[:].withDefault {[

            xkey:'month',
            hideHover: 'auto',
            resize: true,
            type: 'line'

    ].withDefault {[]}}

    def widgets = [
            'Titles' : [
                    'componentName' : 'TitleInstancePackagePlatform',
            ],
            'Organizations' : [
                    'componentName' : 'Org',
            ],
            'Packages' : [
                    'componentName' : 'Package'
            ],
            'Platform' : [
                    'componentName' : 'Platform',
            ],
    ]

    // The calendar for the queries.
    Calendar calendar = Calendar.getInstance()
    int start_year = calendar.get(Calendar.YEAR) - 1
    int start_month = calendar.get(Calendar.MONTH) + 1
    if ( start_month == 12 ) {
      start_month = 0
      start_year++
    }

    // For each month in the past 12 months, execute each stat query defined in the month_queries array and stuff
    // the count for that stat in the matrix (Rows = stats, cols = months)
    widgets.each {widget_name, widget_data ->

      log.debug("Processing counts for ${widget_name}");

      // Widget data.
      def wData = [:].withDefault {
        [:]
      }

      // The datasets.
      def component_name = widget_data.remove("componentName")

      result."${widget_name}" << (widget_data + [
              'element' : GrailsNameUtils.getPropertyName(widget_name),
      ])

      def widget_element = GrailsNameUtils.getPropertyName(widget_name)

      // Clear the calendar.
      calendar.clear();
      calendar.set(Calendar.MONTH, start_month);
      calendar.set(Calendar.YEAR, start_year);

      for ( int i=0; i<12; i++ ) {

        log.debug("Period ${i}")

        // X-axis key and val.
        String xkey = result."${widget_name}"."xkey"

        def comp_stats = ComponentStatistic.executeQuery("from ComponentStatistic where componentType = ? and year = ? and month = ?", [component_name, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)], [readOnly: true])[0]
        def ykeyAll = "${widget_element}all"
        def ykeyNew = "${widget_element}new"
        def cur_month = calendar.get(Calendar.MONTH) + 1

        def xVal = "${calendar.get(Calendar.YEAR)}-${cur_month < 10 ? '0'+ cur_month : cur_month}"

        result."${widget_name}".'ykeys' = [ykeyAll, ykeyNew]
        result."${widget_name}".'labels' = ["Total ${widget_name}", "New ${widget_name}"]
        result."${widget_name}".'lineColors' = [colors.numTotal, colors.numNew]

        // Construct an entry for this xValue
        def entry = [
                "${xkey}" : "${xVal}",
                "${ykeyAll}" : comp_stats?.numTotal,
                "${ykeyNew}" : comp_stats?.numNew
        ]

        calendar.add(Calendar.MONTH, 1)

        // Add to the data.
        wData."${xVal}".putAll(entry)
      }

      log.debug("Completed Processing counts for ${widget_name}");

      // Add the results.
      result."${widget_name}"."data" = wData.values()
    }

    //log.debug("${result}")
    ["widgets" : result]
  }

}
