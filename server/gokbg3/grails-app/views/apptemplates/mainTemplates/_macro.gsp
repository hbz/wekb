<%@ page import="de.wekb.helper.RCConstants" %>
<dl class="dl-horizontal">
	<dt>
		<gokb:annotatedLabel owner="${d}" property="name">Name</gokb:annotatedLabel>
	</dt>
	<dd>
		<gokb:xEditable class="ipe" owner="${d}" field="name" />
	</dd>
	<g:if test="${d?.id != null}">
	  <dt>
	    <gokb:annotatedLabel owner="${d}" property="description">Description</gokb:annotatedLabel>
	  </dt>
	  <dd class="multiline" >
	    <gokb:xEditable class="ipe" owner="${d}" field="description" />
	  </dd>
		<dt>
			<gokb:annotatedLabel owner="${d}" property="tags">Tags</gokb:annotatedLabel>
		</dt>
		<dd>
			<ul>
				<g:each in="${d.tags?.sort({"${it.value}"})}" var="t">
					<li>
						${t.value} (<g:link controller="ajaxSupport" action="unlinkManyToMany"
						  params="${ ["__context" : "${d.class.name}:${d.id}", "__property" : "tags", "__itemToRemove" : "${t.getClassName()}:${t.id}" ] }" 
						  class='confirm-click' data-confirm-message="Are you sure you wish to remove this tag?"
						  >delete</g:link>)
					</li>
				</g:each>
			</ul>
			<g:if test="${d.isEditable()}">
				<g:form controller="ajaxSupport" action="addToStdCollection"
					class="form-inline">
					<input type="hidden" name="__context"
						value="${d.class.name}:${d.id}" />
					<input type="hidden" name="__property" value="tags" />
					<gokb:simpleReferenceTypedown class="form-control allow-add"
						name="__relatedObject" baseClass="org.gokb.cred.RefdataValue"
						filter1="Macro.Tags" />
					<input type="submit" value="Add..."
						class="btn btn-default btn-primary btn-sm " />
				</g:form>
			</g:if>
		</dd>
	  <dt>
	    <gokb:annotatedLabel owner="${d}" property="status">Status</gokb:annotatedLabel>
	  </dt>
	  <dd>
	    <gokb:xEditableRefData owner="${d}" field="status"
	      config="${RCConstants.KBCOMPONENT_STATUS}" />
	  </dd>

		<dt>
			<gokb:annotatedLabel owner="${d}" property="refineTransformations">Refine Transformations</gokb:annotatedLabel>
		</dt>
		<dd class="multiline json refine-transform preformatted" >
			<gokb:xEditable class="ipe" owner="${d}" field="refineTransformations" data-tpl="tpl" />
		</dd>
	</g:if>
</dl>