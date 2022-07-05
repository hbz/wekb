package gokbg3

import org.springframework.security.access.AccessDeniedException
import org.springframework.security.acls.model.NotFoundException

class UrlMappings {

  def springSecurityService

  static mappings = {
    // "/$controller/$action?/$id?(.$format)?"{
    "/resource/show/$type/$id"(controller: 'resource', action: 'show')
    "/package"(controller: 'packages')

    group "/rest", {
      "/packages/$id/$action"(controller: 'package', namespace: 'rest')
      get "/packages/$id"(controller: 'package', namespace: 'rest', action: 'show')
      get "/packages"(controller: 'package', namespace: 'rest', action: 'index')

      get "/platforms/$id/$action"(controller: 'platform', namespace: 'rest')
      get "/platforms/$id"(controller: 'platform', namespace: 'rest', action: 'show')
      get "/platforms"(controller: 'platform', namespace: 'rest', action: 'index')

    }
    "/$controller/$action?/$id?" {
      constraints {
        // apply constraints here
      }
    }

    "/"(controller: 'public', action: 'index')
    "500"(controller: 'error', action: 'forbidden', exception: NotFoundException)
    "500"(controller: 'error', action: 'unauthorized', exception: AccessDeniedException)
    "500"(controller: 'error', action: 'serverError')
    "405"(controller: 'error', action: 'wrongMethod')
    "404"(controller: 'error', action: 'notFound')
    "403"(controller: 'error', action: 'forbidden', params: params)
    "401"(controller: 'error', action: 'unauthorized')
    "400"(controller: 'error', action: 'badRequest')
  }
}
