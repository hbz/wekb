package org.gokb

import org.springframework.security.access.annotation.Secured

class UserController extends grails.plugin.springsecurity.ui.UserController {

  UserProfileService userProfileService

  @Secured(['ROLE_ADMIN', 'IS_AUTHENTICATED_FULLY'])
  def delete() {
    log.debug("Deleting user ${params.id} ..")
    userProfileService.delete(User.get(params.id))
    redirect(controller: 'user', action: 'search')
  }
}
