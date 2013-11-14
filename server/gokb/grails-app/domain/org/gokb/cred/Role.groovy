package org.gokb.cred

class Role {

	String authority

	static mapping = {
		cache true
		id column:'role_id'
	}

	static constraints = {
		authority blank: false, unique: true
	}

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    // ql = RefdataValue.findAllByValueIlikeOrDescriptionIlike("%${params.q}%","%${params.q}%",params)
    // ql = RefdataValue.findWhere("%${params.q}%","%${params.q}%",params)

    def query = "from Role as r where lower(r.authority) like ?"
    def query_params = ["%${params.q.toLowerCase()}%"]

    ql = Role.findAll(query, query_params, params)

    if ( ql ) {
      ql.each { id ->
        result.add([id:"${id.class.name}:${id.id}",text:"${id.authority}"])
      }
    }

    result
  }

}
