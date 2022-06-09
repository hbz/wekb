<%@ page import="de.wekb.helper.RCConstants;" %>
<g:if test="${d.publicationType?.value == 'Serial'}">
	<semui:tabsItemContent tab="tippcoverage">
		<div class="content we-inline-lists">
			<dl>
				<dt class="control-label">
					Coverage
				</dt>
				<dd>
					<table class="ui selectable striped sortable celled table">
						<thead>
						<tr>
							<th>Start Date</th>
							<th>Start Volume</th>
							<th>Start Issue</th>
							<th>End Date</th>
							<th>End Volume</th>
							<th>End Issue</th>
							<th>Embargo</th>
							<th>Note</th>
							<th>Depth</th>
							<g:if test="${editable}">
								<th>Actions</th>
							</g:if>
						</tr>
						</thead>
						<tbody>
						<g:if test="${d.coverageStatements?.size() > 0}">
							<g:each var="cs" in="${d.coverageStatements.sort { it.startDate }}">
								<tr>
									<td><semui:xEditable owner="${cs}" type="date"
														 field="startDate"/></td>
									<td><semui:xEditable owner="${cs}"
														 field="startVolume"/></td>
									<td><semui:xEditable owner="${cs}"
														 field="startIssue"/></td>
									<td><semui:xEditable owner="${cs}" type="date"
														 field="endDate"/></td>
									<td><semui:xEditable owner="${cs}" field="endVolume"/></td>
									<td><semui:xEditable owner="${cs}" field="endIssue"/></td>
									<td><semui:xEditable owner="${cs}" field="embargo"/></td>
									<td><semui:xEditable owner="${cs}" field="coverageNote"/></td>
									<td><semui:xEditableRefData owner="${cs}" field="coverageDepth"
																config="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
									</td>
									<g:if test="${editable}">
										<td>
											<g:link controller="ajaxSupport"
													action="deleteCoverageStatement"
													params="[id: cs.id, fragment: 'tippcoverage']">Delete</g:link>
										</td>
									</g:if>
								</tr>
							</g:each>
						</g:if>
						<g:else>
							<tr><td colspan="8"
									style="text-align:center">${message(code: 'tipp.coverage.empty', default: 'No coverage defined')}</td>
							</tr>
						</g:else>
						</tbody>
					</table>
					<g:if test="${editable}">
						<dl>
							<a class="ui right floated black button" href="#"
							   onclick="$('#coverageStatementsModal').modal('show');">Add Coverage Statement</a>

							<br>
							<br>

						</dl>
					</g:if>
				</dd>
			</dl>
		</div>

	<g:if test="${editable}">
	<semui:modal id="coverageStatementsModal" title="Add Coverage Statement">

		<g:form controller="ajaxSupport" action="addToCollection"
				class="form-inline" params="[fragment: 'tippcoverage']">
			<input type="hidden" name="__context"
				   value="${d.class.name}:${d.id}"/>
			<input type="hidden" name="__newObjectClass"
				   value="org.gokb.cred.TIPPCoverageStatement"/>
			<input type="hidden" name="__recip" value="owner"/>
			<dt class="control-label">Start Date</dt>
			<dd>
				<input class="form-control" type="date" name="startDate"/>
			</dd>
			<dt class="control-label">Start Volume</dt>
			<dd>
				<input class="form-control" type="text" name="startVolume"/>
			</dd>
			<dt class="control-label">Start Issue</dt>
			<dd>
				<input class="form-control" type="text" name="startIssue"/>
			</dd>
			<dt class="control-label">End Date</dt>
			<dd>
				<input class="form-control" type="date" name="endDate"/>
			</dd>
			<dt class="control-label">End Volume</dt>
			<dd>
				<input class="form-control" type="text" name="endVolume"/>
			</dd>
			<dt class="control-label">End Issue</dt>
			<dd>
				<input class="form-control" type="text" name="endIssue"/>
			</dd>
			<dt class="control-label">Embargo</dt>
			<dd>
				<input class="form-control" type="text" name="embargo"/>
			</dd>
			<dt class="control-label">Coverage Depth</dt>
			<dd>
				<semui:simpleReferenceTypedown name="coverageDepth"
											  baseClass="org.gokb.cred.RefdataValue"
											  filter1="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
			</dd>
			<dt class="control-label">Coverage Note</dt>
			<dd>
				<input class="form-control" type="text" name="coverageNote"/>
			</dd>
		</g:form>
	</semui:modal>
		</g:if>

	</semui:tabsItemContent>
</g:if>