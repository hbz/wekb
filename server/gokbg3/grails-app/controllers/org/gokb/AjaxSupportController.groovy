package org.gokb

import de.wekb.helper.DateUtils
import de.wekb.helper.RCConstants
import de.wekb.helper.RDStore
import grails.converters.JSON
import com.k_int.ClassUtils

import org.gokb.cred.*

import org.springframework.security.access.annotation.Secured;
import grails.gorm.transactions.Transactional
import grails.core.GrailsClass
import org.grails.datastore.mapping.model.*
import org.grails.datastore.mapping.model.types.*
import wekb.AccessService
import wekb.KBComponentLanguage
import wekb.Contact

import java.text.SimpleDateFormat

class AjaxSupportController {

  def genericOIDService
  def springSecurityService
  def messageService
  AccessService accessService


  @Deprecated
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def edit() {
    // edit [name:name, value:project:12, pk:org.gokb.cred.Package:2950, action:edit, controller:ajaxSupport]
    log.debug("edit ${params}");
    def result = [:]

    try {
      if ( params.pk ) {
        def target = genericOIDService.resolveOID(params.pk)
        def user = springSecurityService.currentUser

        if (target) {
          def editable = checkEditable(target)

          if (editable) {
            target[params.name] = params.value
            target.save(flush:true)
          }
        }

        pk_components = pk.split(':')
        if ( pk_components.length == 2 ) {
        }
      }
    }
    catch ( Exception e ) {
      log.error(e)
    }

    render result as JSON
  }

  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def setRef() {
    def result = [:]
    render result as JSON
  }

  /**
   *  getRefdata : Used to retrieve a list of all RefdataValues for a specific category.
   * @param id : The label of the RefdataCategory
   */

  @Secured(['ROLE_USER', 'IS_AUTHENTICATED_FULLY'])
  def getRefdata() {
    log.debug("AjaxController::getRefdata ${params}")

    def result = []

    def config = refdata_config[params.id]

    if (!config) {
      log.debug("Use generic config.")

      config = [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=? order by rdv.value asc, rdv.description asc",
      rdvCat: "${params.id}",
      qryParams:[],
      cols:['value'],
      format:'simple'
      ]
    }

    if ( params.id == 'boolean' ) {
      result.add([text:'Yes', value: 1])
      result.add([text:'No', value: 0])
    } else {
      def query_params = [config.rdvCat]

      config.qryParams.each { qp ->
        if ( qp.clos ) {
          query_params.add(qp.clos(params[qp.param]?:''));
        }
        else {
          query_params.add(params[qp.param] ?: qp.cat);
        }
      }

      log.debug("Params: ${query_params}");
      log.debug("Count qry: ${config.countQry}");
      log.debug("Row qry: ${config.rowQry}");
      log.debug("DOMAIN: ${config.domain}");

      GrailsClass dc = grailsApplication.getArtefact("Domain", 'org.gokb.cred.'+ config.domain)

      if (dc) {
        def cq = dc.getClazz().executeQuery(config.countQry,query_params);
        def rq = dc.getClazz().executeQuery(config.rowQry,
                                  query_params,
                                  [max:params.iDisplayLength?:400,offset:params.iDisplayStart?:0]);

        if (!config.required) {
          result.add([id:'', text:'', value:'']);
        }

        rq.each { it ->
          def o = ClassUtils.deproxy(it)
          result.add([id:"${o.class.name}:${o.id}", text: o[config.cols[0]], value:"${o.class.name}:${o.id}"]);
        }
      }
    }

    render result as JSON
  }


  def refdata_config = [
    'ContentProvider' : [
      domain:'Org',
      countQry:'select count(o) from Org as o where lower(o.name) like ?',
      rowQry:'select o from Org as o where lower(o.name) like ? order by o.name asc',
      qryParams:[
    [
      param:'sSearch',
      clos:{ value ->
      def result = '%'
      if ( value && ( value.length() > 0 ) )
        result = "%${value.trim().toLowerCase()}%"
      result
      }
    ]
    ],
      cols:['name'],
      format:'map'
    ],
    'PackageType' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      qryParams:[['cat': "Package Type"]],
      rdvCat: RCConstants.PACKAGE_SCOPE,
      cols:['value'],
      format:'simple'
    ],
    'KBComponent.Status' : [
      domain:'RefdataValue',
      // countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='KBComponent.Status' and rdv.value !='${KBComponent.STATUS_DELETED}'",
      // rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='KBComponent.Status' and rdv.value !='${KBComponent.STATUS_DELETED}'",
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      required:true,
      qryParams:[],
      rdvCat: RCConstants.KBCOMPONENT_STATUS,
      cols:['value'],
      format:'simple'
    ],
    'VariantNameType' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      qryParams:[],
      rdvCat: RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE,
      cols:['value'],
      format:'simple'
    ],
    'KBComponentVariantName.VariantType' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      qryParams:[],
      rdvCat: RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE,
      cols:['value'],
      format:'simple'
    ],
    'Locale' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      qryParams:[],
      rdvCat: RCConstants.KBCOMPONENT_VARIANTNAME_LOCAL,
      cols:['value'],
      format:'simple'
    ],
    'ReviewRequest.Status' : [
      domain:'RefdataValue',
      // countQry:"select count(rdv) from RefdataValue as rdv where rdv.owner.desc='KBComponent.Status' and rdv.value !='${KBComponent.STATUS_DELETED}'",
      // rowQry:"select rdv from RefdataValue as rdv where rdv.owner.desc='KBComponent.Status' and rdv.value !='${KBComponent.STATUS_DELETED}'",
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      qryParams:[],
      rdvCat: RCConstants.REVIEW_REQUEST_STATUS,
      cols:['value'],
      format:'simple'
    ],
    'TitleInstancePackagePlatform.CoverageDepth' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      required:true,
      qryParams:[],
      rdvCat: RCConstants.TIPP_COVERAGE_DEPTH,
      cols:['value'],
      format:'simple'
    ],
    'TIPPCoverageStatement.CoverageDepth' : [
      domain:'RefdataValue',
      countQry:"select count(rdv) from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      rowQry:"select rdv from RefdataValue as rdv where rdv.useInstead is null and rdv.owner.desc=?",
      required:true,
      qryParams:[],
      rdvCat: RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH,
      cols:['value'],
      format:'simple'
    ],
  ]



  /**
   *  addToCollection : Used to create a form which will add a new object to a named collection within the target object.
   * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) Of the context object
   * @param __newObjectClass : The fully qualified class name of the instance to create
   * @param __recip : Optional - If set, then new_object.recip will point to __context
   * @param __addToColl : The name of the local set to which the new object should be added
   * @param All other parameters are taken to be property names on newObjectClass and used to init the new instance.
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def addToCollection() {
    log.debug("AjaxController::addToCollection ${params}");
    User user = springSecurityService.currentUser
    def contextObj = resolveOID2(params.__context)
    def new_obj = null
    def errors = []
    GrailsClass domain_class = grailsApplication.getArtefact('Domain',params.__newObjectClass)

    if (domain_class) {
      if (contextObj) {
        def editable = accessService.checkEditableObject(contextObj, params)//checkEditable(contextObj)

        if (editable || contextObj.id == user.id) {
          log.debug("Create a new instance of ${params.__newObjectClass}");

          if (params.__newObjectClass == "org.gokb.cred.KBComponentVariantName"){

            def norm_variant = GOKbTextUtils.normaliseString(params.variantName)
            def existing_variants = KBComponentVariantName.findByNormVariantNameAndOwner(norm_variant, contextObj)

            if (existing_variants){
              log.debug("found dupes!")
              errors.add(message(code:'variantName.value.notUnique', default:'This variant is already present in this list'))
            }
            else {
              log.debug("create new variantName")
            }
          }

          if (params.__newObjectClass == "org.gokb.cred.TitleInstancePackagePlatform") {

            if (!params.title || params.title.size() == 0) {
              log.debug("missing title for TIPP creation")
              errors.add(message(code:'tipp.title.nullable', default:'Please provide a title for the TIPP'))
            }

            if (!params.hostPlatform || params.hostPlatform.size() == 0) {
              log.debug("missing platform for TIPP creation")
              errors.add(message(code:'tipp.hostPlatform.nullable', default:'Please provide a platform for the TIPP'))
            }

            if(!params.url || params.url.size() == 0) {
              log.debug("missing url for TIPP creation")
              errors.add(message(code:'tipp.url.nullable', default:'Please provide an url for the TIPP'))
            }
          }

          if (errors.size() == 0) {
            new_obj = domain_class.getClazz().newInstance();
            PersistentEntity pent = grailsApplication.mappingContext.getPersistentEntity(domain_class.fullName)

            pent.getPersistentProperties().each { p -> // list of PersistentProperties
              log.debug("${p.name} (assoc=${p instanceof Association}) (oneToMany=${p instanceof OneToMany}) (ManyToOne=${p instanceof ManyToOne}) (OneToOne=${p instanceof OneToOne})");
              if ( params[p.name] && p.name != 'format' ) {
                if ( p instanceof Association ) {
                  if ( p instanceof ManyToOne || p instanceof OneToOne ) {
                    // Set ref property
                    log.debug("set assoc ${p.name} to lookup of OID ${params[p.name]}");
                    // if ( key == __new__ then we need to create a new instance )
                    new_obj[p.name] = resolveOID2(params[p.name])
                  }
                  else {
                    // Add to collection
                    log.debug("add to collection ${p.name} for OID ${params[p.name]}");
                    new_obj[p.name].add(resolveOID2(params[p.name]))
                  }
                }
                else {
                  log.debug("checking for type of property -> ${p.type}")
                  switch ( p.type ) {
                    case Long.class:
                      log.debug("Set simple prop ${p.name} = ${params[p.name]} (as long=${Long.parseLong(params[p.name])})");
                      new_obj[p.name] = Long.parseLong(params[p.name]);
                      break;

                    case Date.class:
                      def dateObj = params.date(p.name, 'yyyy-MM-dd')
                      new_obj[p.name] = dateObj
                      log.debug("Set simple prop ${p.name} = ${params[p.name]} (as date ${dateObj}))");
                      break;

                    case Float.class:
                      log.debug("Set simple prop ${p.name} = ${params[p.name]} (as float=${Float.valueOf(params[p.name])})");
                      new_obj[p.name] = Float.valueOf(params[p.name]);
                      break;

                    default:
                      log.debug("Default for type ${p.type}")
                      log.debug("Set simple prop ${p.name} = ${params[p.name]}");
                      new_obj[p.name] = params[p.name]
                      break;
                  }
                }
              }
            }

            if (params.__refdataName && params.__refdataValue) {
              log.debug("set refdata "+ params.__refdataName +" for component ${contextObj}")
              def refdata = resolveOID2(params.__refdataValue)
              new_obj[params.__refdataName] = refdata
            }

            // Need to do the right thing depending on who owns the relationship. If new obj
            // BelongsTo other, should be added to recip collection.
            if ( params.__recip ) {
              log.debug("Set reciprocal property ${params.__recip} to ${contextObj}");
              new_obj[params.__recip] = contextObj
              log.debug("Saving ${new_obj}");
              if ( new_obj.validate() ) {
                new_obj.save(flush:true)
                log.debug("Saved OK");
                if (contextObj.respondsTo("lastUpdateComment")){
                  contextObj.lastUpdateComment = "Added new connected ${new_obj.class.simpleName}(ID: ${new_obj.id})."
                }
                contextObj.save(flush: true)
              }
              else {
                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
              }
            }
            else if ( params.__addToColl ) {
              contextObj[params.__addToColl].add(new_obj)
              log.debug("Saving ${new_obj}");

              if ( new_obj.validate() ) {
                new_obj.save(flush:true)
                log.debug("New Object Saved OK");
              }
              else {
                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
              }

              if ( contextObj.validate() ) {
                  contextObj.save(flush:true)
                log.debug("Context Object Saved OK");
              }
              else {
                errors.addAll(messageService.processValidationErrorsToListForFlashError(contextObj.errors, request.locale))
              }
            }
            else {
              // Stand alone object.. Save it!
              log.debug("Saving stand alone reference object");
              if ( new_obj.validate() ) {
                new_obj.save(flush:true, failOnError:true)
                log.debug("Saved OK (${new_obj.class.name} ${new_obj.id})");
              }
              else {
                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
              }
            }

            // Special combo processing
            if ( ( new_obj != null ) &&
                ( new_obj.hasProperty('hasByCombo') ) && ( new_obj.hasByCombo != null ) ) {
              log.debug("Processing hasByCombo properties...${new_obj.hasByCombo}");
              new_obj.hasByCombo.keySet().each { hbc ->
                log.debug("Testing ${hbc} -> ${params[hbc]}");
                if ( params[hbc] ) {
                  log.debug("Setting ${hbc} to ${params[hbc]}");
                  new_obj[hbc] = resolveOID2(params[hbc])
                }
              }
              if( new_obj.validate() ) {
                new_obj.save(flush:true, failOnError:true)
              }
              else {
                errors.addAll(messageService.processValidationErrorsToListForFlashError(new_obj.errors, request.locale))
              }
            }
          }
        }
        else {
          log.debug("Located instance of context class with oid ${params.__context} is not editable.");
          flash.error = message(code:'component.addToList.denied.label')
        }
      }
      else if (!contextObj) {
        log.debug("Unable to locate instance of context class with oid ${params.__context}");
        flash.error = message(code:'component.context.notFound.label')
      }
    }
    else {
      if(!domain_class) {
        log.error("Unable to lookup domain class ${params.__newObjectClass}");
        flash.error = message(code:'component.classNotFound.label', args:[params.__newObjectClass])
      }else{
        flash.error = message(code:'component.create.denied.label', args:[params.__newObjectClass])
        log.error("No permission to create an object of domain class ${params.__newObjectClass}");
      }
    }

    if (errors.size() > 0) {
      flash.error = errors
    }

    withFormat {
      html {
        if( new_obj && params.__showNew && errors.size() == 0) {
          redirect(controller:'resource', action:'show', id:"${new_obj.class.name}:${new_obj.id}");
        }
        else {
          def redirect_to = request.getHeader('referer')

          if ( params.fragment && params.fragment.length() > 0 ) {
            redirect_to = "${redirect_to}#${params.fragment}"
          }
          redirect(url: redirect_to)
        }
      }
      json {
        def result = ['result': 'OK', 'params': params]

        if (flash.error) {
          result.result = 'ERROR'
          result.errors = flash.error
        }
        else {
          result.new_object = new_obj
          result.new_oid = "${new_obj.class.name}:${new_obj.id}"
        }

        render result as JSON
      }
    }
  }

  /**
   *  addToStdCollection : Used to add an existing object to a named collection that is not mapped through a join object.
   * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
   * @param __relatedObject : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the object to be added to the list
   * @param __property : The property name of the collection to which the object should be added
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def addToStdCollection() {
    log.debug("addToStdCollection(${params})");
    // Adds a link to a collection that is not mapped through a join object
    def contextObj = resolveOID2(params.__context)
    def user = springSecurityService.currentUser
    def relatedObj = resolveOID2(params.__relatedObject)
    def result = ['result': 'OK', 'params': params]
    if (relatedObj != null && contextObj != null) {
      def editable = checkEditable(contextObj)

      if (editable || user.id == contextObj.id) {
        if (!contextObj["${params.__property}"].contains(relatedObj)) {
          contextObj["${params.__property}"].add (relatedObj)
          contextObj.save(flush:true, failOnError:true)
          log.debug("Saved: ${contextObj.id}");
          result.context = contextObj
        }else{
          flash.error = "Object is already present in this list!"
          log.debug("Tried to add the same object twice!")
          result.result = 'ERROR'
          result.error = "Object is already present in this list!"
        }
      }
      else {
        flash.error = message(code:'component.list.add.denied.label')
        log.debug("context object not editable.")
        result.result = 'ERROR'
        result.error = "Permission to add to this list was denied."
      }
    }
    else if (!contextObj) {
      flash.error = message(code:'component.context.notFound.label')
      result.result = 'ERROR'
      result.error = "Context object could not be found!"
    }
    else if (!relatedObj) {
      flash.error = message(code:'component.listItem.notFound.label')
      result.result = 'ERROR'
      result.error = "List item not found!"
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.fragment && params.fragment.length() > 0 ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }
        redirect(url: redirect_to)
      }
      json {
        render result as JSON
      }
    }
  }

  /**
   *  unlinkManyToMany : Used to remove an object from a named collection.
   * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
   * @param __itemToRemove : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the object to be removed from the list
   * @param __property : The property name of the collection from which the object should be removed from
   * @param __otherEnd : The property name from the side of the object to be removed
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def unlinkManyToMany() {
    log.debug("unlinkManyToMany(${params})");
    def contextObj = resolveOID2(params.__context)
    def user = springSecurityService.currentUser
    def result = ['result': 'OK', 'params': params]
    if (contextObj) {
      def editable = checkEditable(contextObj)

      if (editable || contextObj.id == user.id) {
        def item_to_remove = resolveOID2(params.__itemToRemove)
        if ( item_to_remove ) {
          if ( ( item_to_remove != null ) && ( item_to_remove.hasProperty('hasByCombo') ) && ( item_to_remove.hasByCombo != null ) ) {
            item_to_remove.hasByCombo.keySet().each { hbc ->
              log.debug("Testing ${hbc}");
              log.debug("here's the data: "+ item_to_remove[hbc])
              if (item_to_remove[hbc]==contextObj) {
                log.debug("context found");
                //item_to_remove[hbc]=resolveOID2(null)
                if(item_to_remove.respondsTo('deleteParent')) {
                  log.debug("deleteParent()")
                  item_to_remove.deleteParent();
                }
                log.debug("tried removal: ${item_to_remove[hbc]}");
              }
            }
          }
          log.debug("${params}");
          log.debug("removing: ${item_to_remove} from ${params.__property} for ${contextObj}");

          def remove_result = contextObj[params.__property].remove(item_to_remove);

          log.debug("remove successful?: ${remove_result}")
          log.debug("child ${item_to_remove} removed: "+ contextObj[params.__property]);

          if ( params.propagate == "true" && KBComponent.isAssignableFrom(contextObj.class)) {
            contextObj.lastSeen = new Date().getTime()
          }

          if (contextObj.save(flush: true, failOnError: true)) {
            log.debug("Saved context object ${contextObj.class.name}")
          }
          else {
            flash.error = messageService.processValidationErrorsToListForFlashError(contextObj.errors, request.locale)
            result.result = 'ERROR'
            result.code = 400
          }

          if (item_to_remove.hasProperty('fromComponent') && item_to_remove.fromComponent == contextObj) {
            item_to_remove.delete(flush:true)
          }
          else {

            if (params.__otherEnd && item_to_remove[params.__otherEnd]!=null) {
              log.debug("remove parent: "+item_to_remove[params.__otherEnd])
              //item_to_remove.setParent(null);
              item_to_remove[params.__otherEnd]=null; //this seems to fail
              log.debug("parent removed: "+item_to_remove[params.__otherEnd]);
            }
            if (!item_to_remove.validate()) {
              flash.error = messageService.processValidationErrorsToListForFlashError(item_to_remove.errors, request.locale)
            }
            else {
              item_to_remove.save(flush:true)
            }
          }
        } else {
          log.error("Unable to resolve item to remove : ${params.__itemToRemove}");
          flash.error(code:'component.listItem.notFound.label')
        }
      }
      else {
        flash.error = message(code:'component.list.remove.denied.label')
        log.debug("Located instance of context class with oid ${params.__context} is not editable.");
        result.result = 'ERROR'
        result.code = 403
      }
    }
    else {
      flash.error = message(code:'component.context.notFound.label')
      log.debug("Unable to locate instance of context class with oid ${params.__context}");
      result.result = 'ERROR'
      result.code = 404
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.fragment && params.fragment.length() > 0 ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }
        redirect(url: redirect_to)
      }
      json {
        if (flash.error) {
          result.errors = flash.error
        }

        render result as JSON
      }
    }
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def unlinkManyToOne() {
    log.debug("unlinkManyToOne(${params})");
    def contextObj = resolveOID2(params.__context)
    def result = ['result': 'OK', 'params': params]
    if (contextObj) {
      def editable = checkEditable(contextObj)

      if (editable) {
          contextObj[params.__property] = null

          if (contextObj.save(flush: true, failOnError: true)) {
            log.debug("Saved context object ${contextObj.class.name}")
          }
          else {
            flash.error = messageService.processValidationErrorsToListForFlashError(contextObj.errors, request.locale)
            result.result = 'ERROR'
            result.code = 400
          }
      }
      else {
        flash.error = message(code:'component.delete.denied.label')
        log.debug("Located instance of context class with oid ${params.__context} is not editable.");
        result.result = 'ERROR'
        result.code = 403
      }
    }
    else {
      flash.error = message(code:'component.context.notFound.label')
      log.debug("Unable to locate instance of context class with oid ${params.__context}");
      result.result = 'ERROR'
      result.code = 404
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.fragment && params.fragment.length() > 0 ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }
        redirect(url: redirect_to)
      }
      json {
        if (flash.error) {
          result.errors = flash.error
        }

        render result as JSON
      }
    }
  }

  /**
   *  delete : Used to delete a domain class object.
   * @param __context : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def delete() {
    log.debug("delete(${params}), referer: ${request.getHeader('referer')}");
    // Adds a link to a collection that is not mapped through a join object
    def contextObj = resolveOID2(params.__context)
    def user = springSecurityService.currentUser
    def result = ['result': 'OK', 'params': params]

    if ( contextObj ) {
      def editable = checkEditable(contextObj)

      if (editable) {
        if(contextObj.respondsTo('deleteSoft')) {
          contextObj.deleteSoft()
        }
        else {
          contextObj.delete(flush:true)
        }
        log.debug("Item deleted.")
      }
      else {
        flash.error = message(code:'component.delete.denied.label')
        log.debug("Located instance of context class with oid ${params.__context} is not editable.");
      }
    }
    else {
      flash.error = message(code:'component.notFound.label', args:[params.__context])
      log.debug("Unable to locate instance of context class with oid ${params.__context}");
    }

    def redirect_to = request.getHeader('referer')

    if ( params.redirect ) {
      redirect_to = params.redirect
    }
    else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
      redirect_to = "${redirect_to}#${params.fragment}"
    }

    withFormat {
      html {
        redirect(url: redirect_to)
      }
      json {
        if (flash.error) {
          result.errors = flash.error
          result.result = 'ERROR'
        }

        render result as JSON
      }
    }
  }

  private def resolveOID2(oid) {
    def oid_components = oid.split(':');
    def result = null;
    def domain_class=null;
    domain_class = grailsApplication.getArtefact('Domain',oid_components[0])
    if ( domain_class ) {
      if (oid_components.size() == 2 ) {
        if ( oid_components[1]=='__new__' ) {
          result = domain_class.getClazz().refdataCreate(oid_components)
          log.debug("Result of create ${oid} is ${result}");
        }
        else {
          result = domain_class.getClazz().get(oid_components[1])
        }
      }
      else {
        log.debug("Could not retrieve object. No ID provided.")
      }
    }
    else {
      log.debug("resolve OID failed to identify a domain class. Input was ${oid_components}");
    }
    result
  }

  /**
   *  editableSetValue : Used to set a primitive property value.
   * @param pk : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
   * @param type : Used for date parsing with value 'date'
   * @param dateFormat : Used for overriding the default date format ('yyyy-MM-dd')
   * @param name : The name of the property to be changed
   * @param value : The new value for the property
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def editableSetValue() {
    log.debug("editableSetValue ${params}");
    def user = springSecurityService.currentUser
    def target_object = genericOIDService.resolveOID(params.pk)

    def result = ['result': 'OK', 'params': params]
    def errors = [:]
    if (target_object) {
      def editable = checkEditable(target_object)

      if (editable || target_object == user) {
        if (params.type == 'date') {
          SimpleDateFormat sdf = DateUtils.getSDF_NoTime()
          def backup = target_object."${params.name}"

          try {
            if (params.value && params.value.size() > 0) {
              // parse new date
              def parsed_date = sdf.parse(params.value)
              target_object."${params.name}" = parsed_date
            } else {
              // delete existing date
              target_object."${params.name}" = null
            }
            target_object.save(failOnError: true)
          }
          catch (Exception e) {
            target_object."${params.name}" = backup
            log.error(e.toString())
          }

          //target_object."${params.name}" = params.date('value',params.dateFormat ?: 'yyyy-MM-dd')
        }
        else if (params.type == 'boolean') {
          target_object."${params.name}" = params.boolean('value')
        }
        else if (params.name == 'uuid' || params.name == 'password') {
          errors[params.name] = "This property is not editable."
        }
        else {
          def binding_properties = [:]
          def new_val = params.value?.trim() ?: null

          binding_properties[ params.name ] = new_val
          bindData(target_object, binding_properties)
        }

        if (target_object.validate()) {
          target_object.save(flush:true);
        }
        else {
          errors = messageService.processValidationErrors(target_object.errors, request.locale)
        }
      }
      else {
        errors['global'] = [[message:"Object ${target_object} is not editable.".toString()]]
        log.debug("Object ${target_object} is not editable.");
      }
    }
    else {
      errors['global'] = [[message:"Not able to resolve object from ${params.pk}.".toString()]]
      log.debug("Object ${target_object} could not be resolved.");
    }

    withFormat {
      html {
        def resp = null
        if (errors.size() == 0) {
          resp = params.value
        }
        else {
          def error_obj = errors[params.name] ? errors[params.name][0] : errors['global'][0]
          log.debug("Error msg: ${error_obj} (${error_obj.message})")

          resp = error_obj.message
          response.setContentType('text/plain;charset=UTF-8')
          response.status = 400
          render resp
        }
      }
      json {
        if (errors.size() > 0) {
          result.errors = errors
          result.result = 'ERROR'
        }

        render result as JSON
      }
    }
  }

  private boolean checkEditable(obj) {
    def editable = accessService.checkEditableObject(obj, params)

    editable
  }

  /**
   *  genericSetRel : Used to set a complex property value.
   * @param pk : the OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
   * @param name : The name of the property to be changed
   * @param value : The OID ([FullyQualifiedClassName]:[PrimaryKey]) of the object to link
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def genericSetRel() {
    // [id:1, value:JISC_Collections_NESLi2_Lic_IOP_Institute_of_Physics_NESLi2_2011-2012_01012011-31122012.., type:License, action:inPlaceSave, controller:ajax
    // def clazz=grailsApplication.domainClasses.findByFullName(params.type)
    log.debug("genericSetRel ${params}");
    def user = springSecurityService.currentUser
    def target = genericOIDService.resolveOID(params.pk)
    def value = null

    if (params.type == 'boolean') {
      value = params.boolean('value')
    }
    else {
      value = genericOIDService.resolveOID(params.value)
    }

    def result = ['result':'OK']

    if ( target != null) {
      def editable = checkEditable(target)

      if (editable) {
        // def binding_properties = [ "${params.name}":value ]
        log.debug("Binding: ${params.name} into ${target} - a ${target.class.name}");
        // bindData(target, binding_properties)
        target[params.name] = value
        log.debug("Saving... after assignment ${params.name} = ${target[params.name]}");

        if ( target.validate() ) {
          target = target.merge(flush: true, failOnError: true)

          if ( params.resultProp ) {
            result = value ? value[params.resultProp] : ''
          }

          // We should clear the session values for a user if this is a user to force reload of the,
          // parameters.
          if (target instanceof User) {
            //session.userPereferences = null
          }
          else {
            if ( value ) {
              result.objVal = renderObjectValue(value);
              // result = value.toString()
            }
          }
        }
        else {
          log.debug("Problem saving.. ${target.errors}");
          result.errors = messageService.processValidationErrors(target.errors, request.locale)
          result.result = "ERROR"
        }
      }
      else {
        log.debug("Target is not editable!");
        result.result = "ERROR"
        result.errors = ["Not able to edit this property!"]
      }
    }
    else {
      log.debug("Target not found!");
      result.result = "ERROR"
      result.errors = ["Unable to locate intended target object!"]
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        result.newValue = target[params.name]
        log.debug("return ${result}");
        render result as JSON
      }
    }
  }

  def renderObjectValue(value) {
    def result=''
    if ( value ) {
      switch ( value.class ) {
        case org.gokb.cred.RefdataValue.class:
          if ( value.icon != null ) {
            result="<span class=\"select-icon ${value.icon}\"></span>${value.value}"
          }
          else {
            result=value.value
          }
          break;
        case Boolean.class:
          result = value ? 'Yes' : 'No'
        default:
          result=value.toString();
      }
    }
    result;
  }

  /**
   *  addIdentifier : Used to add an identifier to a list.
   * @param __context : The OID ([FullyQualifiedClassName]:[PrimaryKey]) of the context object
   * @param identifierNamespace : The OID ([FullyQualifiedClassName]:[PrimaryKey]) of the identifier namespace
   * @param identifierValue : The value of the identifier to link
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def addIdentifier() {
    log.debug("addIdentifier - ${params}");
    def result = ['result': 'OK', 'params': params]
        def user = springSecurityService.currentUser
    def identifier_instance = null
    // Check identifier namespace present, and identifier value valid for that namespace
    if ( ( params.identifierNamespace?.trim() ) &&
         ( params.identifierValue?.trim() ) &&
         ( params.__context?.trim() ) ) {
      def ns = genericOIDService.resolveOID(params.identifierNamespace)
      def owner = genericOIDService.resolveOID(params.__context)
      if ( ( ns != null ) && ( owner != null ) ) {
        def editable = checkEditable(owner)

        if (editable) {
          // Lookup or create Identifier
          try {
            String attr = Identifier.getAttributeName(owner)
            def ident = Identifier.executeQuery(
                    'select ident from Identifier ident where ident.value = :val and ident.namespace = :namespace and ident.' + attr +' = :owner order by ident.id',
                    [val: params.identifierValue, namespace: ns, owner: owner])

            if (ident){
              flash.error = message(code:'identifier.no.unique.by.component')
            }else if (!ident) {
                ident = new Identifier(namespace: ns, value: params.identifierValue)
                ident.setReference(owner)
                boolean success = ident.save(flush: true) //needed to trigger afterInsert(); temp solution
                if (success){
                  flash.success = message(code:'identifier.create.success')
                } else {
                  flash.error = message(code:'identifier.create.fail')
                }
              }
          }
          catch (grails.validation.ValidationException ve) {

            log.debug("${ve}")
            flash.error = message(code:'identifier.value.illegalIdForm')
          }
        }
        else {
          flash.error = message(code:'component.addToList.denied.label')
        }
      }else{
        flash.error = message(code:'identifier.create.error')
        log.debug("could not create identifier!")
      }
    }
    log.debug("Redirecting to referer: ${request.getHeader('referer')}");

    withFormat {
      html {
        if(params.hash){
          redirect(url: (request.getHeader('referer')+params.hash))
        }else if(params.activeTab){
          redirect(url: (request.getHeader('referer')+'?activeTab='+params.activeTab))
        }else {
          redirect(url: (request.getHeader('referer')))
        }
      }
      json {
        if (flash.error) {
          result.result = 'ERROR'
          result.error = flash.error
        }
        else {
          result.new_obj = identifier_instance
          result.new_oid = "${identifier_instance.class.name}:${identifier_instance.id}"
        }

        render result as JSON
      }
    }
  }




  /**
   *  authorizeVariant : Used to replace the name of a component by one of its existing variant names.
   * @param id : The id of the variant name
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def authorizeVariant() {
    log.debug("${params}");
    def result = ['result':'OK', 'params':params]
    def variant = KBComponentVariantName.get(params.id)
    def user = springSecurityService.currentUser

    if ( variant != null) {
      def owner = variant.owner
      def editable = checkEditable(owner)

      if (editable) {
        // Does the current owner.name exist in a variant? If not, we should create one so we don't loose the info
        def current_name_as_variant = owner.variantNames.find { it.variantName == owner.name }

        result.owner = "${owner.class.name}:${owner.id}"

        if ( current_name_as_variant == null ) {
          log.debug("No variant name found for current name: ${owner.name} ")
          def variant_name = owner.getId();

          if(variant.owner.name){
            variant_name = owner.name
          }
          else if (owner?.respondsTo('getDisplayName') && owner.getDisplayName()){
            variant_name = owner.getDisplayName()?.trim()
          }
          else if(owner?.respondsTo('getName') ) {
            variant_name = owner?.getName()?.trim()
          }

          def new_variant = new KBComponentVariantName(owner:owner,variantName:variant_name).save(flush:true);

        }else{
            log.debug("Found existing variant name: ${current_name_as_variant}")
        }

        variant.variantType = RefdataCategory.lookupOrCreate(RCConstants.KBCOMPONENT_VARIANTNAME_VARIANT_TYPE, 'Authorized')
        owner.name = variant.variantName

        if (owner.validate()) {
          owner.save(flush:true);
          result.new_name = variant.owner.name
        }
        else {
          result.result = 'ERROR'
          result.code = 400
          result.message = "This name already belongs to another component of the same type!"
          flash.error = message(code:'variantName.authorize.notUnique')
        }
      }
      else {
        result.result = 'ERROR'
        result.code = 403
        result.message = "No permission to edit variants for this object!"
        flash.error = message(code:'variantName.owner.denied')
      }
    }
    else if (!variant) {
      result.result = 'ERROR'
      result.code = 404
      result.message = "Variant with id ${params.id} not found!".toString()
      def vname = message(code:'variantName.label')
      flash.message = message(code:'default.not.found.message', args:[vname, params.id])
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        render result as JSON
      }
    }
  }

  /**
   *  deleteVariant : Used to delete a variant name of a component.
   * @param id : The id of the variant name
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deleteVariant() {
    log.debug("${params}");
    def result = ['result':'OK', 'params': params]
    def variant = KBComponentVariantName.get(params.id)
    def user = springSecurityService.currentUser
    def variantOwner = variant?.owner ?: null

    if ( variant != null ) {
      def editable = checkEditable(variantOwner)

      if (editable) {
        def variantName = variant.variantName

        variant.delete()
        variantOwner.lastUpdateComment = "Deleted Alternate Name ${variantName}."
        variantOwner.save(flush: true)

        result.owner_oid = "${variantOwner.class.name}:${variantOwner.id}"
        result.deleted_variant = "${variantName}"
      }
      else {
        result.result = 'ERROR'
        result.code = 403
        result.message = "No permission to edit variants for this object!"
        flash.error = message(code:'variantName.owner.denied')
      }
    }
    else if (!variant) {
      result.result = 'ERROR'
      result.code = 404
      def vname = message(code:'variantName.label')
      flash.error = message(code:'default.not.found.message', args:[vname, params.id])
      result.message = "Variant with id ${params.id} not found!".toString()
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        render result as JSON
      }
    }
  }

  /**
   *  deleteCoverageStatement : Used to delete a TIPPCoverageStatement.
   * @param id : The id of the coverage statement object
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deleteCoverageStatement() {
    log.debug("${params}");
    def result = ['result':'OK', 'params': params]
    def user = springSecurityService.currentUser
    def tcs = TIPPCoverageStatement.get(params.id)
    def tipp = tcs.owner

    if ( tcs != null) {
      def editable = checkEditable(tipp)

      if (editable) {
        tcs.delete()
        tipp.lastUpdateComment = "Deleted Coverage Statement."
        tipp.save(flush: true)
      }
      else {
        result.result = 'ERROR'
        result.code = 403
        result.message = "This TIPP is not editable!"
        flash.error = message(code:'tipp.coverage.denied.label')
      }
    }
    else if (!tcs) {
      result.result = 'ERROR'
      result.code = 404
      def vname = message(code:'TIPPCoverageStatement.label')
      result.message = "TIPPCoverageStatement with id ${params.id} not found!".toString()
      flash.error = message(code:'default.not.found.message', args:[vname, params.id])
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        render result as JSON
      }
    }
  }

  /**
   *  deleteCombo : Used to delete a combo object.
   * @param id : The id of the combo object
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deleteCombo() {
    def result = ['result': "OK", 'params': params]
    Combo c = Combo.get(params.id);
    def user = springSecurityService.currentUser

    if (c && c.fromComponent) {
      def owner = c.fromComponent
      def editable = checkEditable(owner)

      if (editable) {
        log.debug("Delete combo..")

        if ( params.propagate == "true") {
          c.fromComponent.lastSeen = new Date().getTime()
        }

        if (params.keepLink) {
          c.status = RefdataCategory.lookup(RCConstants.COMBO_STATUS, Combo.STATUS_DELETED)
        }
        else{
          c.delete(flush:true);
        }
      }
      else {
        def fcomp = (c.fromComponent?.logEntityId ?: "Combo ${params.id}")
        result.code = 403
        result.message = "Not deleting combo.. no edit permissions on ${fcomp}!".toString()
        result.result = 'ERROR'
        flash.error = message(code:'combo.fromComponent.denied.label', args:[fcomp])
        log.debug("Not deleting combo.. no edit permissions on fromComponent!")
      }
    }
    else {
      result.code = 404
      result.message = "Unable to reference Combo!"
      def vname = message(code:'combo.label')
      flash.error = message(code:'default.not.found.message', args:[vname, params.id])
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        render result as JSON
      }
    }
  }

  /**
   *  deletePrice : Used to delete a ComponentPrice from a TitleInstance.
   * @param id : The id of the ComponentPrice
   */

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deletePrice() {
    def result = ['result': "OK", 'params': params]
    ComponentPrice c = ComponentPrice.get(params.id);
    def user = springSecurityService.currentUser

    if (c) {
      def editable = checkEditable(c.owner)

      if (editable) {
        log.debug("Delete Price..")
        c.delete(flush: true);
      }
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        render result as JSON
      }
    }
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def applyForUserorg() {
    def result = ['result': 'OK', 'params': params]
    def user_org = UserOrganisation.get(params.id ?: params.userOrg)
    def user = springSecurityService.currentUser
    def pending_status = RefdataCategory.lookup(RCConstants.MEMBERSHIP_STATUS, 'Pending')
    def role_type = RefdataCategory.lookup(RCConstants.MEMBERSHIP_ROLE, 'Member')

    if ( user_org && !user_org.members?.party?.contains(user) ) {
      new UserOrganisationMembership(memberOf: user_org, party: user, role: role_type, status: pending_status).save(flush:true, failOnError:true)

      result.item = [user:user.username, status: pending_status.value, role: role_type.value]
    }
    else {
      result.result = 'ERROR'

      if ( !user_org ) {
        result.message = 'Could not find User Organisation with id ${params.userOrg}!'
        result.code = 404
      }
      else {
        result.message = 'This user is already a member of this group'
      }
    }

    render result as JSON
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def deleteLanguage() {
    log.debug("${params}");
    def result = ['result':'OK', 'params': params]
    KBComponentLanguage kbComponentLanguage = KBComponentLanguage.get(params.id)
    def user = springSecurityService.currentUser

    if ( kbComponentLanguage != null ) {
      def editable = checkEditable(kbComponentLanguage.kbcomponent)

      if (editable) {
        kbComponentLanguage.delete()
      }
      else {
        result.result = 'ERROR'
        result.code = 403
        result.message = "No permission to edit language for this object!"
        flash.error = "No permission to edit language for this object!"
      }
    }
    else if (!kbComponentLanguage) {
      result.result = 'ERROR'
      result.code = 404
      flash.error = message(code:'default.not.found.message', args:["Language", params.id])
      result.message = "Language with id ${params.id} not found!".toString()
    }

    withFormat {
      html {
        def redirect_to = request.getHeader('referer')

        if ( params.redirect ) {
          redirect_to = params.redirect
        }
        else if ( ( params.fragment ) && ( params.fragment.length() > 0 ) ) {
          redirect_to = "${redirect_to}#${params.fragment}"
        }

        redirect(url: redirect_to);
      }
      json {
        render result as JSON
      }
    }
  }

  @Transactional
  @Secured(['ROLE_EDITOR', 'IS_AUTHENTICATED_FULLY'])
  def addContact() {
    log.debug("addContact - ${params}");
    def result = ['result': 'OK', 'params': params]
    Contact contact = null
    if (params.content?.trim()) {
      String content = params.content
      RefdataValue contentType = genericOIDService.resolveOID(params.contentType)
      RefdataValue language = genericOIDService.resolveOID(params.language)
      RefdataValue type = genericOIDService.resolveOID(params.type)
      Org owner = genericOIDService.resolveOID(params.__context)
        def editable = checkEditable(owner)
        if (editable) {
            if (contentType == RDStore.CONTACT_CONTENT_TYPE_EMAIL) {
                if (content ==~ /[_A-Za-z0-9-]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})/) {
                    contact = new Contact(org: owner, content: content, contentType: contentType, language: language, type: type)
                    contact.save(flush: true)
                } else {
                    flash.error = message(code: 'contact.email.validation.fail')
                }
            } else {
                contact = new Contact(org: owner, content: content, contentType: contentType, language: language, type: type)
                contact.save(flush: true)
            }
        }
        else {
          flash.error = message(code:'component.addToList.denied.label')
        }
    }
    log.debug("Redirecting to referer: ${request.getHeader('referer')}")

    withFormat {
      html {
        redirect(url: (request.getHeader('referer')))
      }
    }
  }

}
