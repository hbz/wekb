package gokbg3

import org.gokb.cred.*
import wekb.GlobalSearchTemplatesService;

class UserDetailsInterceptor {

  GlobalSearchTemplatesService globalSearchTemplatesService

  public UserDetailsInterceptor() {
    match(controller: 'admin')
    match(controller: 'component')
    match(controller: 'coreference')
    match(controller: 'create')
    match(controller: 'decisionSupport')
    match(controller: 'file')
    match(controller: 'folderUpload')
    match(controller: 'fwk')
    match(controller: 'globalSearch')
    match(controller: 'group')
    match(controller: 'home')
    match(controller: 'ingest')
    match(controller: 'packages')
    match(controller: 'resource')
    match(controller: 'savedItems')
    match(controller: 'search')
    match(controller: 'security')
    match(controller: 'titleList')
    match(controller: 'upload')
    match(controller: 'user')
    match(controller: 'workflow')
  }


  def springSecurityService
  def displayTemplateService

    boolean before() { 

      log.debug("User details filter...");

      def user = springSecurityService.getCurrentUser()

      log.debug("UserDetailsInterceptor::before(${params}) ${request.getRequestURL()}")

      if (user) {
        log.debug("User details filter... User present");
        request.user = user
        
        if (!session.menus) {
        
          // Map to hold the menus.
          final Map <String, LinkedHashMap<String, Map>> menus = new HashMap <String, LinkedHashMap<String, Map>>().withDefault {
            new LinkedHashMap<String, Map>().withDefault {
              new ArrayList()
            }
          }
          
          // Add to the session.
          session.menus = menus

         /* def default_dcs = ["org.gokb.cred.Package", "org.gokb.cred.Platform", "org.gokb.cred.TitleInstancePackagePlatform", "org.gokb.cred.Org", "org.gokb.cred.Source"]
*/
          boolean userIsAdmin = user.isAdmin()

          // Step 1 : List all domains available to this user order by type, grouped into type
          def domains = KBDomainInfo.createCriteria().list {

            if(userIsAdmin) {
              ilike('dcName', 'org.gokb%')
            }/*else{
              inList('dcName', default_dcs)
            }*/
            
            createAlias ("type", "menueType")
            
            /*order ('menueType.sortKey','asc')
            order ('menueType.value','asc')
            order ('dcSortOrder','asc')*/
            order ('displayName','asc')
          }
          
          domains.each { d ->
    
            // Get the target class.
            Class tc = Class.forName(d.dcName)
            
            if ( tc ) {
            
              if ( tc.isTypeReadable() ) {
      
                // Find any searches for that domain that the user has access to and add them to the menu section
                def searches_for_this_domain = globalSearchTemplatesService.findAllByBaseClass(d.dcName)
                
                searches_for_this_domain.each { key, val ->
                  
                  // Add a menu item.
                  if(userIsAdmin){
                    menus["admin"]["search"] << [
                            text : val.title,
                            link : ['controller' : 'search', 'action' : 'index', 'params' : [qbe:'g:'+ key]],
                            attr : ['title' : "Search ${val.title}"]
                    ]
                  }

                }
              }
              // Add if creatable.
              if ( tc.isTypeCreatable() ) {
                def display_template = displayTemplateService.getTemplateInfo(d.dcName)

                if ( display_template  && !display_template.noCreate) {

                  if(userIsAdmin){
                    menus["admin"]["create"] << [
                            text: d.displayName,
                            link: ['controller': 'create', 'action': 'index', 'params': [tmpl: d.dcName]],
                            attr: ['title': "New ${d.displayName}"]
                    ]
                  }
                }
              }
            }
          }
        }

        // Get user curatorial groups
        if ( ! session.curatorialGroups ) {
          session.curatorialGroups = [];
          request.user.curatoryGroups.each { cg ->
            session.curatorialGroups.add([id:cg.id, name:cg.name]);
          }
        }

        if ( session.userPereferences == null ) {
          //log.debug("Set up user prefs");
          session.userPereferences = request.user.getUserPreferences()
        }
        else {
        }
      }
      else {
        log.debug("No user present..")
      }
      log.debug("Return true");
      true 
    }

    boolean after() { true }

    void afterView() {
        // no-op
    }
}
