import org.gokb.cred.*
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.codehaus.groovy.grails.plugins.springsecurity.SpringSecurityUtils

class UserDetailsFilters {

  def springSecurityService
  def aclUtilService

  // grailsApplication.config.appDefaultPrefs

  def filters = {
    
    // DO not allow any pages to be served from browser cache.
    noCacheFilter(controller:'*', action:'*') {
       after = {
           response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate") // HTTP 1.1.
           response.setHeader("Pragma", "no-cache") // HTTP 1.0.
           response.setDateHeader("Expires", -1)
       }
    }
    
    setUsetFilter(controller:'*', action:'*') {
      before = {
        // if ( session.sessionPreferences == null ) {
        //   session.sessionPreferences = grailsApplication.config.appDefaultPrefs
        // }
        // else {
        // }
        if ( springSecurityService.principal instanceof String ) {
          //log.debug("User is string: ${springSecurityService.principal}");
        }
        else if (springSecurityService.principal?.id != null ) {
          request.user = User.get(springSecurityService.principal.id);
          request.userOptions = request.user.getUserOptions();

          if ( session.userPereferences == null ) {
            //log.debug("Set up user prefs");
            session.userPereferences = request.user.getUserPreferences()
            // Generate Menu for this user.
            session.userPereferences.mainMenuSections = [:]
            def current_type = null
            def current_list = null;
            // Step 1 : List all domains available to this user order by type, grouped into type
            
            def domains = KBDomainInfo.createCriteria().list {
              createAlias ("type", "menueType")
              
              order ('menueType.sortKey')
              order ('menueType.id')
              order ('displayName')
            }
            
            domains.each { d ->
              //log.debug("Process ${d.displayName} - ${d.type.id}");
              if ( d.type.id != current_type ) {
                current_type = d.type.id
                current_list = [:]
                session.userPereferences.mainMenuSections.put(d.type.value, current_list)
                //log.debug("Added new menu section for ${d.type.value}");
              }

              // Test permissions(Admin sees all anyway)
              // boolean hasPermission(Authentication authentication, domainObject, Permission… permissions)
              if ( ( aclUtilService.hasPermission(SCH.context.authentication, d, org.springframework.security.acls.domain.BasePermission.READ ) ) ||
                   ( SpringSecurityUtils.ifAnyGranted('ROLE_ADMIN') ) ) {
 
              // Find any searches for that domain that the user has access to and add them to the menu section
                def searches_for_this_domain = grailsApplication.config.globalSearchTemplates.findAll{it.value.baseclass==d.dcName}
                searches_for_this_domain.each {
                  //log.debug("Adding search for ${it.key} - ${it.value.baseclass}");
                  current_list[it.key] = it.value
                }
              }
            }
          }
        }
      }
    }
  }
}
