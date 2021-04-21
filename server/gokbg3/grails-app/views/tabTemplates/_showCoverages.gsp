<g:if test="${d.publicationType?.value == 'Serial'}">
	<div class="tab-pane active" id="tippcoverage">
		<dl class="dl-horizontal">
			<dt>
				<gokb:annotatedLabel owner="${d}" property="coverage">Coverage</gokb:annotatedLabel>
			</dt>
			<dd>
				<table class="table table-striped">
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
						<th>Actions</th>
					</tr>
					</thead>
					<tbody>
					<g:if test="${d.coverageStatements?.size() > 0}">
						<g:each var="cs" in="${d.coverageStatements.sort { it.startDate }}">
							<tr>
								<td><gokb:xEditable  owner="${cs}" type="date"
													 field="startDate"/></td>
								<td><gokb:xEditable  owner="${cs}"
													 field="startVolume"/></td>
								<td><gokb:xEditable  owner="${cs}"
													 field="startIssue"/></td>
								<td><gokb:xEditable  owner="${cs}" type="date"
													 field="endDate"/></td>
								<td><gokb:xEditable  owner="${cs}" field="endVolume"/></td>
								<td><gokb:xEditable  owner="${cs}" field="endIssue"/></td>
								<td><gokb:xEditable  owner="${cs}" field="embargo"/></td>
								<td><gokb:xEditable  owner="${cs}" field="coverageNote"/></td>
								<td><gokb:xEditableRefData owner="${cs}" field="coverageDepth"
														   config="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
								</td>
								<td><g:if test="${editable}"><g:link controller="ajaxSupport"
																	 action="deleteCoverageStatement"
																	 params="[id: cs.id, fragment: 'tippcoverage']">Delete</g:link></g:if></td>
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
					<button
							class="hidden-license-details btn btn-default btn-sm btn-primary "
							data-toggle="collapse" data-target="#collapseableAddCoverageStatement">
						Add new <i class="fas fa-plus"></i>
					</button>
					<dl id="collapseableAddCoverageStatement" class="dl-horizontal collapse">
						<g:form controller="ajaxSupport" action="addToCollection"
								class="form-inline" params="[fragment: 'tippcoverage']">
							<input type="hidden" name="__context"
								   value="${d.class.name}:${d.id}"/>
							<input type="hidden" name="__newObjectClass"
								   value="org.gokb.cred.TIPPCoverageStatement"/>
							<input type="hidden" name="__recip" value="owner"/>
							<dt class="dt-label">Start Date</dt>
							<dd>
								<input class="form-control" type="date" name="startDate"/>
							</dd>
							<dt class="dt-label">Start Volume</dt>
							<dd>
								<input class="form-control" type="text" name="startVolume"/>
							</dd>
							<dt class="dt-label">Start Issue</dt>
							<dd>
								<input class="form-control" type="text" name="startIssue"/>
							</dd>
							<dt class="dt-label">End Date</dt>
							<dd>
								<input class="form-control" type="date" name="endDate"/>
							</dd>
							<dt class="dt-label">End Volume</dt>
							<dd>
								<input class="form-control" type="text" name="endVolume"/>
							</dd>
							<dt class="dt-label">End Issue</dt>
							<dd>
								<input class="form-control" type="text" name="endIssue"/>
							</dd>
							<dt class="dt-label">Embargo</dt>
							<dd>
								<input class="form-control" type="text" name="embargo"/>
							</dd>
							<dt class="dt-label">Coverage Depth</dt>
							<dd>
								<gokb:simpleReferenceTypedown name="coverageDepth"
															  baseClass="org.gokb.cred.RefdataValue"
															  filter1="${RCConstants.TIPPCOVERAGESTATEMENT_COVERAGE_DEPTH}"/>
							</dd>
							<dt class="dt-label">Coverage Note</dt>
							<dd>
								<input class="form-control" type="text" name="coverageNote"/>
							</dd>
							<dt></dt>
							<dd>
								<button type="submit"
										class="btn btn-default btn-primary btn-sm ">Add</button>
							</dd>
						</g:form>
					</dl>
				</g:if>
			</dd>
			<dt>
				<gokb:annotatedLabel owner="${d}" property="coverageNote">Coverage Note</gokb:annotatedLabel>
			</dt>
			<dd>
				<gokb:xEditable  owner="${d}" field="coverageNote"/>
			</dd>
			<dt>
				<gokb:annotatedLabel owner="${d}" property="coverageDepth">Coverage Depth</gokb:annotatedLabel>
			</dt>
			<dd>
				<gokb:xEditableRefData owner="${d}" field="coverageDepth"
									   config="${RCConstants.TIPP_COVERAGE_DEPTH}"/>
			</dd>
		</dl>
	</div>
</g:if>