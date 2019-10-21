<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<div class="changeContent">
	<form:form
		action="/fdahpStudyDesigner/adminStudies/saveOrUpdateActiveTaskContent.do?_S=${param._S}"
		name="activeContentFormId" id="activeContentFormId" method="post"
		role="form">
		<input type="hidden" name="id" id="taskContentId"
			value="${activeTaskBo.id}">
		<input type="hidden" name="taskTypeId"
			value="${activeTaskBo.taskTypeId}">
		<input type="hidden" name="studyId" value="${activeTaskBo.studyId}">
		<input type="hidden" value="" id="buttonText" name="buttonText">
		<input type="hidden" value="${actionPage}" id="actionPage"
			name="actionPage">
		<input type="hidden" value="${currentPage}" id="currentPageId"
			name="currentPage">
		<div class="pt-lg">
			<div class="gray-xs-f mb-sm">
				Activity Short Title or Key <small>(50 characters max)</small><span
					class="requiredStar"> *</span><span
					class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					title="This must be a human-readable activity identifier and unique across all activities of the study.Note that this field cannot be edited once the study is Launched."></span>
			</div>
			<div class="add_notify_option">
				<div class="form-group shortTitleClass">
					<input autofocus="autofocus" type="text" custAttType="cust"
						class="form-control shortTitleCls" id="shortTitleId" 
						name="shortTitle" value="${fn:escapeXml(activeTaskBo.shortTitle)}"
						<c:if test="${not empty activeTaskBo.isDuplicate && (activeTaskBo.isDuplicate gt 0)}"> disabled</c:if>
						maxlength="50" required />
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>
		</div>
		<div>
			<div class="gray-xs-f mb-sm">
				Display name <small>(150 characters max)</small><span
					class="requiredStar"> *</span><span
					class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					title="A name that gets displayed for the task in the app."></span>
			</div>
			<div>
				<div class="form-group">
					<input type="text" class="form-control" name="displayName"
						value="${fn:escapeXml(activeTaskBo.displayName)}" maxlength="150"
						required />
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>
		</div>
		<div class="mt-lg blue-md-f text-uppercase">Configurable parameters</div>
                    <div class="gray-xs-f mt-md mb-sm">Instructions <small>(150 characters max)</small><span class="requiredStar"> *</span></div>
                    <div class="form-group">                     
                      <textarea class="form-control" rows="5" id="comment" name="instruction" maxlength="150" required>${activeTaskBo.instruction}</textarea>
                      <div class="help-block with-errors red-txt"></div>
        </div>
		<c:if test="${fn:length(activeTaskBo.taskAttributeValueBos) eq 0}">
			<c:forEach items="${activeTaskBo.taskMasterAttributeBos}" var="taskMasterAttributeBo">
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 1}">
					<div class="col-md-3 col-lg-3 p-none mr-lg ">
					  <div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The sequence length of the initial memory pattern"></span>
					  </div>
					  <input type="hidden" name="taskAttributeValueBos[0].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[0].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[0].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="initialspanId" class="form-control" name="taskAttributeValueBos[0].attributeVal" maxlength="2" onkeypress="return isNumber(event)" required/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 2}">
					<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
					  <div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The minimum pattern sequence length"></span>
					  </div>
					  <input type="hidden" name="taskAttributeValueBos[1].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[1].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[1].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="minimumspanId" class="form-control" name="taskAttributeValueBos[1].attributeVal" maxlength="2" onkeypress="return isNumber(event)" required />
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 3}">
					<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
					  <div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
					   class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					   title="The maximum pattern sequence length"></span>
					  </div>
					  <input type="hidden" name="taskAttributeValueBos[2].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[2].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[2].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="maximumspanId" class="form-control" name="taskAttributeValueBos[2].attributeVal" maxlength="2" onkeypress="return isNumber(event)" required />
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 4}">
					<div class="col-md-3 col-lg-3 p-none mr-lg">
					  <div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
					  class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					  title="The time per sequence item; a smaller value means faster sequence play."></span>
					 </div>
					  <input type="hidden" name="taskAttributeValueBos[3].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[3].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[3].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="playspeedId"  class="form-control" name="taskAttributeValueBos[3].attributeVal" maxlength="5" required onkeypress="return isNumberFloat(event)"/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 5}">
					<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
					  <div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
					    class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					    title="The maximum number of rounds to conduct"></span>
					  </div>
					  <input type="hidden" name="taskAttributeValueBos[4].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[4].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[4].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="maximumtestId" class="form-control" name="taskAttributeValueBos[4].attributeVal" maxlength="3" onkeypress="return isNumber(event)" required />
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 6}">
				<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
								class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The maximum number of consecutive failures the user can make before the task is terminated."></span>
						</div>
					  <input type="hidden" name="taskAttributeValueBos[5].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[5].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[5].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="maximumFailureId" class="form-control" name="taskAttributeValueBos[5].attributeVal" maxlength="3" onkeypress="return isNumber(event)" required />
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 7}">
					<div class="col-md-3 col-lg-3 p-none">
					  <div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="Boolean value that indicates whether to require the user to tap the sequence in reverse order."></span>
					   </div>
					  <input type="hidden" name="taskAttributeValueBos[6].attributeValueId" value="">
					  <input type="hidden" name="taskAttributeValueBos[6].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[6].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                            <span class="radio radio-info radio-inline p-45">
                                <input type="radio" id="inlineRadio7" class="rejoin_radio studyTypeClass" name="taskAttributeValueBos[6].attributeVal" value="Y" required>
                                <label for="inlineRadio7">Yes</label>
                            </span>
                            <span class="radio radio-inline">
                                <input type="radio" id="inlineRadio8" class="rejoin_radio studyTypeClass" name="taskAttributeValueBos[6].attributeVal" value="N" checked required>
                                <label for="inlineRadio8">No</label>
                            </span>
                            <div class="help-block with-errors red-txt"></div>
                      </div>
	               </div>
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 7}">
                    <div class="blue-md-f text-uppercase">Results captured from the task</div>
               </c:if>
               <c:if test="${taskMasterAttributeBo.orderByTaskType eq 8}">
                   <input type="hidden" name="taskAttributeValueBos[7].attributeValueId" value="">
                   <input type="hidden" name="taskAttributeValueBos[7].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
                   <input type="hidden" name="taskAttributeValueBos[7].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">    
                   <div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>
					<div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
						<div class="chartSection" style="display: none">
							<div class="mb-lg">
								<span class="checkbox checkbox-inline"> <input
									type="checkbox"
									id="${taskMasterAttributeBo.attributeName}_chart_id"
									name="taskAttributeValueBos[7].addToLineChart" value="option1">
									<label for="${taskMasterAttributeBo.attributeName}_chart_id">Add to line chart</label>
								</span>
							</div>

							<div
								class="addLineChartBlock_${taskMasterAttributeBo.attributeName}"
								style="display: none">
								<div class="pb-lg">
									<div class="gray-xs-f mt-md mb-sm">
										Time range for the chart<span class="requiredStar">*</span> <span
											class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
											title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span>
									</div>
									<div class="add_notify_option form-group">
										<select
											class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList elaborateClass requireClass"
											id="chartId" name="taskAttributeValueBos[7].timeRangeChart"
											title="Select">
											<option value="" selected disabled>Select</option>
											<c:forEach items="${timeRangeList}" var="timeRangeAttr">
												<option value="${timeRangeAttr}">${timeRangeAttr}</option>
											</c:forEach>
										</select>
										<div class="mt-sm black-xs-f italic-txt activeaddToChartText" style="display: none;"></div>
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>

								<div class="pb-lg">
									<div class="gray-xs-f mb-sm">
										Allow rollback of chart? <span
											class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
											title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span>
									</div>
									<div class="form-group">
										<span class="radio radio-info radio-inline p-45"> <input
											type="radio" id="inlineRadio1" value="Yes"
											name="taskAttributeValueBos[7].rollbackChat"> <label
											for="inlineRadio1">Yes</label>
										</span> <span class="radio radio-inline"> <input
											class="rollbackRadioClass" type="radio" id="inlineRadio2"
											value="No" name="taskAttributeValueBos[7].rollbackChat">
											<label for="inlineRadio2">No</label>
										</span>
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>

								<div class="bor-b-dash">
									<div class="gray-xs-f mb-sm">
										Title for the chart <small>(30 characters max)</small><span
											class="requiredStar"> *</span>
									</div>
									<div class="add_notify_option">
										<div class="form-group">
											<input type="text" class="form-control requireClass"
												name="taskAttributeValueBos[7].titleChat" maxlength="30" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="pt-lg mt-xs pb-lg">
							<span class="checkbox checkbox-inline"> <input
								type="checkbox"
								id="${taskMasterAttributeBo.attributeName}_stat_id" class="statisticsBlock"
								name="taskAttributeValueBos[7].useForStatistic" value="option1">
								<label for="${taskMasterAttributeBo.attributeName}_stat_id">Use
									for Statistic</label>
							</span>
						</div>
						<div
							class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}"
							style="display: none">
							<div>
								<div class="gray-xs-f mb-sm">
									Short name <small>(20 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option">
									<div class="form-group statShortTitleClass">
										<input autofocus="autofocus" type="text" custAttType="cust"
											class="form-control requireClass shortTitleStatCls"
											id="static1" exist="" dbid=""
											name="taskAttributeValueBos[7].identifierNameStat"
											maxlength="20" />
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Display name for the Stat (e.g. Total Hours of Activity Over 6
									Months) <small>(50 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="form-group">
									<input type="text" class="form-control requireClass"
										name="taskAttributeValueBos[7].displayNameStat" maxlength="50" />
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Display Units (e.g. hours) <small>(15 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option">
									<div class="form-group">
										<input type="text" class="form-control requireClass"
											name="taskAttributeValueBos[7].displayUnitStat"
											maxlength="15" />
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Stat Type for image display<span class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option form-group">
									<select
										class="selectpicker aq-select aq-select-form elaborateClass requireClass"
										title="Select" name="taskAttributeValueBos[7].uploadTypeStat">
										<c:forEach items="${statisticImageList}" var="statisticImage">
											<option value="${statisticImage.statisticImageId}">${statisticImage.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Formula for to be applied<span class="requiredStar"> *</span>
								</div>
								<div class="form-group">
									<select class="selectpicker aq-select aq-select-form elaborateClass requireClass" title="Select"
										       name="taskAttributeValueBos[7].formulaAppliedStat">
										<c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
											<option value="${activetaskFormula.activetaskFormulaId}">${activetaskFormula.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>
							<!-- <div>
								<div class="gray-xs-f mb-sm">Time ranges options available
									to the mobile app user</div>
								<div>
									<span class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Day</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Week</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Month</span></span> <span
										class="txt-gray">(Rollback option provided for these
										three options)</span>
								</div>
							</div> -->
						</div>
					</div>
					<div class="clearfix"></div>
			   </c:if>
			   <c:if test="${taskMasterAttributeBo.orderByTaskType eq 9}">
                   <input type="hidden" name="taskAttributeValueBos[8].attributeValueId" value="">
                   <input type="hidden" name="taskAttributeValueBos[8].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
                   <input type="hidden" name="taskAttributeValueBos[8].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">    
                   <div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>
					<div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
						<div class="chartSection" style="display: none">
							<div class="mb-lg">
								<span class="checkbox checkbox-inline"> <input
									type="checkbox"
									id="${taskMasterAttributeBo.attributeName}_chart_id"
									name="taskAttributeValueBos[8].addToLineChart" value="option1">
									<label for="${taskMasterAttributeBo.attributeName}_chart_id">Add
										to line chart</label>
								</span>
							</div>

							<div
								class="addLineChartBlock_${taskMasterAttributeBo.attributeName}"
								style="display: none">
								<div class="pb-lg">
									<div class="gray-xs-f mt-md mb-sm">
										Time range for the chart<span class="requiredStar"> *</span> <span
											class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
											title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span>
									</div>
									<div class="add_notify_option form-group">
										<select
											class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList elaborateClass requireClass"
											id="chartId1" name="taskAttributeValueBos[8].timeRangeChart"
											title="Select">
											<option value="" selected disabled>Select</option>
											<c:forEach items="${timeRangeList}" var="timeRangeAttr">
												<option value="${timeRangeAttr}">${timeRangeAttr}</option>
											</c:forEach>
										</select>
										<div class="mt-sm black-xs-f italic-txt activeaddToChartText" style="display: none;"></div>
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>

								<div class="pb-lg">
									<div class="gray-xs-f mb-sm">
										Allow rollback of chart? <span
											class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
											title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span>
									</div>
									<div class="form-group">
										<span class="radio radio-info radio-inline p-45"> <input
											type="radio" id="inlineRadio3" value="Yes"
											name="taskAttributeValueBos[8].rollbackChat"> <label
											for="inlineRadio3">Yes</label>
										</span> <span class="radio radio-inline"> <input
											class="rollbackRadioClass" type="radio" id="inlineRadio4"
											value="No" name="taskAttributeValueBos[8].rollbackChat">
											<label for="inlineRadio4">No</label>
										</span>
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>

								<div class="bor-b-dash">
									<div class="gray-xs-f mb-sm">
										Title for the chart <small>(30 characters max)</small><span
											class="requiredStar"> *</span>
									</div>
									<div class="add_notify_option">
										<div class="form-group">
											<input type="text" class="form-control requireClass"
												name="taskAttributeValueBos[8].titleChat" maxlength="30" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="pt-lg mt-xs pb-lg">
							<span class="checkbox checkbox-inline"> <input
								type="checkbox"
								id="${taskMasterAttributeBo.attributeName}_stat_id" class="statisticsBlock"
								name="taskAttributeValueBos[8].useForStatistic" value="option1">
								<label for="${taskMasterAttributeBo.attributeName}_stat_id">Use
									for Statistic</label>
							</span>
						</div>
						<div
							class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}"
							style="display: none">
							<div>
								<div class="gray-xs-f mb-sm">
									Short name <small>(20 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option">
									<div class="form-group statShortTitleClass">
										<input autofocus="autofocus" type="text" custAttType="cust"
											class="form-control requireClass shortTitleStatCls"
											id="static2" exist="" dbid=""
											name="taskAttributeValueBos[8].identifierNameStat"
											maxlength="20" />
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Display name for the Stat (e.g. Total Hours of Activity Over 6
									Months) <small>(50 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="form-group">
									<input type="text" class="form-control requireClass"
										name="taskAttributeValueBos[8].displayNameStat" maxlength="50" />
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Display Units (e.g. hours) <small>(15 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option">
									<div class="form-group">
										<input type="text" class="form-control requireClass"
											name="taskAttributeValueBos[8].displayUnitStat"
											maxlength="15" />
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Stat Type for image display<span class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option form-group">
									<select
										class="selectpicker aq-select aq-select-form elaborateClass requireClass"
										title="Select" name="taskAttributeValueBos[8].uploadTypeStat">
										<c:forEach items="${statisticImageList}" var="statisticImage">
											<option value="${statisticImage.statisticImageId}">${statisticImage.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Formula for to be applied<span class="requiredStar"> *</span>
								</div>
								<div class="form-group">
									<select class="selectpicker aq-select aq-select-form elaborateClass requireClass" title="Select"
										       name="taskAttributeValueBos[8].formulaAppliedStat">
										<c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
											<option value="${activetaskFormula.activetaskFormulaId}">${activetaskFormula.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>
							<!-- <div>
								<div class="gray-xs-f mb-sm">Time ranges options available
									to the mobile app user</div>
								<div>
									<span class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Day</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Week</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Month</span></span> <span
										class="txt-gray">(Rollback option provided for these
										three options)</span>
								</div>
							</div> -->
						</div>
					</div>
					<div class="clearfix"></div>
			   </c:if>
			   <c:if test="${taskMasterAttributeBo.orderByTaskType eq 10}">
                   <input type="hidden" name="taskAttributeValueBos[9].attributeValueId" value="">
                   <input type="hidden" name="taskAttributeValueBos[9].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
                   <input type="hidden" name="taskAttributeValueBos[9].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">    
                   <div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>
					<div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
						<div class="chartSection" style="display: none">
							<div class="mb-lg">
								<span class="checkbox checkbox-inline"> <input
									type="checkbox"
									id="${taskMasterAttributeBo.attributeName}_chart_id"
									name="taskAttributeValueBos[9].addToLineChart" value="option1">
									<label for="${taskMasterAttributeBo.attributeName}_chart_id">Add
										to line chart</label>
								</span>
							</div>

							<div
								class="addLineChartBlock_${taskMasterAttributeBo.attributeName}"
								style="display: none">
								<div class="pb-lg">
									<div class="gray-xs-f mt-md mb-sm">
										Time range for the chart<span class="requiredStar"> *</span> <span
											class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
											title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span>
									</div>
									<div class="add_notify_option form-group">
										<select
											class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList elaborateClass requireClass"
											id="chartId2" name="taskAttributeValueBos[9].timeRangeChart"
											title="Select">
											<option value="" selected disabled>Select</option>
											<c:forEach items="${timeRangeList}" var="timeRangeAttr">
												<option value="${timeRangeAttr}">${timeRangeAttr}</option>
											</c:forEach>
										</select>
										<div class="mt-sm black-xs-f italic-txt activeaddToChartText" style="display: none;"></div>
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>

								<div class="pb-lg">
									<div class="gray-xs-f mb-sm">
										Allow rollback of chart? <span
											class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
											title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span>
									</div>
									<div class="form-group">
										<span class="radio radio-info radio-inline p-45"> <input
											type="radio" id="inlineRadio5" value="Yes"
											name="taskAttributeValueBos[9].rollbackChat"> <label
											for="inlineRadio5">Yes</label>
										</span> <span class="radio radio-inline"> <input
											class="rollbackRadioClass" type="radio" id="inlineRadio6"
											value="No" name="taskAttributeValueBos[9].rollbackChat">
											<label for="inlineRadio6">No</label>
										</span>
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>

								<div class="bor-b-dash">
									<div class="gray-xs-f mb-sm">
										Title for the chart <small>(30 characters max)</small><span
											class="requiredStar"> *</span>
									</div>
									<div class="add_notify_option">
										<div class="form-group">
											<input type="text" class="form-control requireClass"
												name="taskAttributeValueBos[9].titleChat" maxlength="30" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="pt-lg mt-xs pb-lg">
							<span class="checkbox checkbox-inline"> <input
								type="checkbox"
								id="${taskMasterAttributeBo.attributeName}_stat_id" class="statisticsBlock"
								name="taskAttributeValueBos[9].useForStatistic" value="option1">
								<label for="${taskMasterAttributeBo.attributeName}_stat_id">Use
									for Statistic</label>
							</span>
						</div>
						<div
							class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}"
							style="display: none">
							<div>
								<div class="gray-xs-f mb-sm">
									Short name <small>(20 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option">
									<div class="form-group statShortTitleClass">
										<input autofocus="autofocus" type="text" custAttType="cust"
											class="form-control requireClass shortTitleStatCls"
											id="static3" exist="" dbid=""
											name="taskAttributeValueBos[9].identifierNameStat"
											maxlength="20" />
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Display name for the Stat (e.g. Total Hours of Activity Over 6
									Months) <small>(50 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="form-group">
									<input type="text" class="form-control requireClass"
										name="taskAttributeValueBos[9].displayNameStat" maxlength="50" />
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Display Units (e.g. hours) <small>(15 characters max)</small><span
										class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option">
									<div class="form-group">
										<input type="text" class="form-control requireClass"
											name="taskAttributeValueBos[9].displayUnitStat"
											maxlength="15" />
										<div class="help-block with-errors red-txt"></div>
									</div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Stat Type for image display<span class="requiredStar"> *</span>
								</div>
								<div class="add_notify_option form-group">
									<select
										class="selectpicker aq-select aq-select-form elaborateClass requireClass"
										title="Select" name="taskAttributeValueBos[9].uploadTypeStat">
										<c:forEach items="${statisticImageList}" var="statisticImage">
											<option value="${statisticImage.statisticImageId}">${statisticImage.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>

							<div>
								<div class="gray-xs-f mb-sm">
									Formula for to be applied<span class="requiredStar"> *</span>
								</div>
								<div class="form-group">
									<select class="selectpicker aq-select aq-select-form elaborateClass requireClass" title="Select"
										       name="taskAttributeValueBos[9].formulaAppliedStat">
										<c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
											<option value="${activetaskFormula.activetaskFormulaId}">${activetaskFormula.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>
						<!-- 	<div>
								<div class="gray-xs-f mb-sm">Time ranges options available
									to the mobile app user</div>
								<div>
									<span class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Day</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Week</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Month</span></span> <span
										class="txt-gray">(Rollback option provided for these
										three options)</span>
								</div>
							</div> -->
						</div>
					</div>
					<div class="clearfix"></div>
			   </c:if>
			</c:forEach>
		</c:if>
		<c:if test="${fn:length(activeTaskBo.taskAttributeValueBos) gt 0}">
			<c:set var="count" value="0"/>
			<c:forEach items="${activeTaskBo.taskMasterAttributeBos}" var ="taskMasterAttributeBo">
            <c:forEach items="${activeTaskBo.taskAttributeValueBos}" var ="taskValueAttributeBo">
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 1 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
					
					<div class="col-md-3 col-lg-3 p-none mr-lg">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
								class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The sequence length of the initial memory pattern"></span>
						</div>
					  <input type="hidden" name="taskAttributeValueBos[0].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[0].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[0].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="initialspanId" class="form-control" name="taskAttributeValueBos[0].attributeVal" maxlength="2" required value="${taskValueAttributeBo.attributeVal}" onkeypress="return isNumber(event)" pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$" data-pattern-error="Please enter valid number."/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 2 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
					
					<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
								class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The minimum pattern sequence length"></span>
						</div>
					  <input type="hidden" name="taskAttributeValueBos[1].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[1].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[1].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="minimumspanId" class="form-control" name="taskAttributeValueBos[1].attributeVal" maxlength="2" required value="${taskValueAttributeBo.attributeVal}" onkeypress="return isNumber(event)" pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$" data-pattern-error="Please enter valid number."/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 3 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
					
					<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
								class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The maximum pattern sequence length"></span>
						</div>
					  <input type="hidden" name="taskAttributeValueBos[2].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[2].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[2].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="maximumspanId" class="form-control" name="taskAttributeValueBos[2].attributeVal" maxlength="2" onkeypress="return isNumber(event)" required value="${taskValueAttributeBo.attributeVal}" pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$" data-pattern-error="Please enter valid number."/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 4 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
					
					<div class="col-md-3 col-lg-3 p-none mr-lg">
					<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
					class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					title="The time per sequence item; a smaller value means faster sequence play."></span>
					</div>
					  <input type="hidden" name="taskAttributeValueBos[3].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[3].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[3].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="playspeedId" class="form-control" name="taskAttributeValueBos[3].attributeVal" maxlength="5" onkeypress="return isNumberFloat(event)" required value="${taskValueAttributeBo.attributeVal}"/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 5 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
					
					<div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span><span
					class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
					title="The maximum number of rounds to conduct"></span>
					</div>
					  <input type="hidden" name="taskAttributeValueBos[4].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[4].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[4].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="maximumtestId" class="form-control" name="taskAttributeValueBos[4].attributeVal" maxlength="3" onkeypress="return isNumber(event)" required value="${taskValueAttributeBo.attributeVal}"/>
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
					
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 6 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
	                <div class="col-md-3 col-lg-3 p-none mr-lg ml-lg">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
								class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="The maximum number of consecutive failures the user can make before the task is terminated."></span>
						</div>
					  <input type="hidden" name="taskAttributeValueBos[5].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[5].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[5].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                           <input type="text" id="maximumFailureId" class="form-control" name="taskAttributeValueBos[5].attributeVal" maxlength="3" onkeypress="return isNumber(event)" required value="${taskValueAttributeBo.attributeVal}" />
	                     <div class="help-block with-errors red-txt"></div>
	                  </div>
	               </div>
	               
	               
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 7 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
					
					<div class="col-md-3 col-lg-3 p-none">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
								class="requiredStar"> *</span><span
						class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip"
						title="Boolean value that indicates whether to require the user to tap the sequence in reverse order."></span>
						</div>
					  <input type="hidden" name="taskAttributeValueBos[6].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
					  <input type="hidden" name="taskAttributeValueBos[6].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
					  <input type="hidden" name="taskAttributeValueBos[6].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                  <div class="form-group">
                            <span class="radio radio-info radio-inline p-45">
                                <input type="radio" id="inlineRadio7" class="rejoin_radio studyTypeClass" name="taskAttributeValueBos[6].attributeVal" value="Y" required ${taskValueAttributeBo.attributeVal eq 'Y'?'checked':""}>
                                <label for="inlineRadio7">Yes</label>
                            </span>
                            <span class="radio radio-inline">
                                <input type="radio" id="inlineRadio8" class="rejoin_radio studyTypeClass" name="taskAttributeValueBos[6].attributeVal" value="N" required <c:if test="${empty taskValueAttributeBo.attributeVal  || empty taskValueAttributeBo}">checked</c:if> ${taskValueAttributeBo.attributeVal eq 'N'?'checked':""}>
                                <label for="inlineRadio8">No</label>
                            </span>
                            <div class="help-block with-errors red-txt"></div>
                      </div>
	               </div>
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 8 && count == 0}">
	             <c:set var="count" value="${count+1}"/>
                    <div class="blue-md-f text-uppercase">Results captured from the task</div>
               </c:if>
               <c:if test="${taskMasterAttributeBo.orderByTaskType eq 8 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
	                        <input type="hidden" name="taskAttributeValueBos[7].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
	                        <input type="hidden" name="taskAttributeValueBos[7].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
	                        <input type="hidden" name="taskAttributeValueBos[7].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                        <div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>
	                        
	                        <div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
	                        <div class="chartSection" style="display:none">
	                          <div class="mb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" id="${taskMasterAttributeBo.attributeName}_chart_id" name="taskAttributeValueBos[7].addToLineChart" <c:if test="${taskValueAttributeBo.addToLineChart==true}">checked</c:if> value="${taskValueAttributeBo.addToLineChart}">
	                                <label for="${taskMasterAttributeBo.attributeName}_chart_id">Add to line chart</label>
	                            </span>  
	                          </div>   
	                           
	                          <div class="addLineChartBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.addToLineChart==true?'':'display:none'}">  
	                          <div class="pb-lg">
	                            <div class="gray-xs-f mt-md mb-sm">Time range for the chart<span class="requiredStar"> *</span> <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span></div>
	                              <div class="add_notify_option form-group mb-none">
		                           <select class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList requireClass" id="chartId" name="taskAttributeValueBos[7].timeRangeChart" title="Select" >
		                              <c:forEach items="${timeRangeList}" var="timeRangeAttr">
		                                 <option value="${timeRangeAttr}" ${fn:escapeXml(taskValueAttributeBo.timeRangeChart) eq fn:escapeXml(timeRangeAttr)?'selected':''}>${timeRangeAttr}</option>
		                              </c:forEach>
		                            </select>
		                            <div class="mt-sm black-xs-f italic-txt activeaddToChartText" style="display: none;"></div>
		                            <div class="help-block with-errors red-txt"></div>
		                         </div>
	                           
	                          </div>
	                          
	                            
	                          <div class="pb-lg">
	                           <div class="gray-xs-f mb-sm">Allow rollback of chart?
	                           <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span>
                                </div>
	                              <div class="form-group">
	                                <span class="radio radio-info radio-inline p-45">
	                                    <input class="" type="radio" id="inlineRadio1" value="Yes" name="taskAttributeValueBos[7].rollbackChat" ${taskValueAttributeBo.rollbackChat eq 'Yes'?'checked':""}>
	                                    <label for="inlineRadio1">Yes</label>
	                                </span>
	                                <span class="radio radio-inline">
	                                    <input class="rollbackRadioClass" type="radio" id="inlineRadio2" value="No" name="taskAttributeValueBos[7].rollbackChat" <c:if test="${empty taskValueAttributeBo.rollbackChat  || empty taskValueAttributeBo}">checked</c:if> ${taskValueAttributeBo.rollbackChat eq 'No'?'checked':""}>
	                                    <label for="inlineRadio2">No</label>
	                                </span>
	                                <div class="help-block with-errors red-txt"></div>
	                              </div>
	                          </div>
	                           
	                        <div class="bor-b-dash">
	                         <div class="gray-xs-f mb-sm">Title for the chart <small>(30 characters max)</small><span class="requiredStar"> *</span>
                             </div>
	                             <div class="add_notify_option">
	                                 <div class="form-group">
	                                     <input type="text" class="form-control requireClass" id="lineChartId" name="taskAttributeValueBos[7].titleChat" maxlength="30" value="${fn:escapeXml(taskValueAttributeBo.titleChat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                        </div>
	                        </div>
	                        </div>
	                        <div>    
	                         <div class="pt-lg mt-xs pb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" class="statisticsBlock" id="${taskMasterAttributeBo.attributeName}_stat_id" name="taskAttributeValueBos[7].useForStatistic" <c:if test="${taskValueAttributeBo.useForStatistic==true}">checked</c:if> value="${taskValueAttributeBo.useForStatistic}">
	                                <label for="${taskMasterAttributeBo.attributeName}_stat_id">Use for Statistic</label>
	                            </span>  
	                          </div>
	                          <div class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.useForStatistic==true?'':'display:none'}">  
	                          <div>
	                            <div class="gray-xs-f mb-sm">Short name <small>(20 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option">
	                                 <div class="form-group statShortTitleClass">
	                                     <input type="hidden" id="dbidentifierId1" title="${taskValueAttributeBo.attributeValueId}" value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}">
	                                     <input autofocus="autofocus" type="text" class="form-control requireClass shortTitleStatCls" exist="${taskValueAttributeBo.useForStatistic==true?'Y':'N'}" dbid="${taskValueAttributeBo.attributeValueId}" custAttType="cust" id="identifierId1" name="taskAttributeValueBos[7].identifierNameStat" 
	                                     maxlength="20" value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}" <c:if test="${not empty taskValueAttributeBo.isIdentifierNameStatDuplicate && (taskValueAttributeBo.isIdentifierNameStatDuplicate gt 0)}"> disabled</c:if>/>
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                         </div>
	                            
	                         <div>
	                            <div class="gray-xs-f mb-sm">Display name for the Stat (e.g. Total Hours of Activity Over 6 Months) <small> (50 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="form-group">
	                                 <input type="text" class="form-control requireClass" name="taskAttributeValueBos[7].displayNameStat" maxlength="50" value="${fn:escapeXml(taskValueAttributeBo.displayNameStat)}"/>  
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                         </div>
	                         
	                            
	                         <div>
	                            <div class="gray-xs-f mb-sm">Display Units (e.g. hours) <small>(15 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option">
	                                 <div class="form-group">
	                                     <input type="text" class="form-control requireClass" name="taskAttributeValueBos[7].displayUnitStat" maxlength="15" value="${fn:escapeXml(taskValueAttributeBo.displayUnitStat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>
	                         </div>
	                         <div>
	                         <div>
	                            <div class="gray-xs-f mb-sm">Stat Type for image display<span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option form-group">
	                                  <select class="selectpicker  aq-select aq-select-form elaborateClass requireClass" title="Select" name="taskAttributeValueBos[7].uploadTypeStat">
                                      <c:forEach items="${statisticImageList}" var="statisticImage">
	                                    <option value="${statisticImage.statisticImageId}" ${taskValueAttributeBo.uploadTypeStat eq statisticImage.statisticImageId?'selected':''}>${statisticImage.value}</option>
	                                </c:forEach>
                                    </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                             </div>
	                         </div>
	                         <div>
	                            <div class="gray-xs-f mb-sm">Formula for to be applied<span class="requiredStar"> *</span></div>
	                             <div class="form-group">
	                                  <select class="selectpicker aq-select aq-select-form elaborateClass requireClass" title="Select" name="taskAttributeValueBos[7].formulaAppliedStat">
                                      <c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
	                                    <option value="${activetaskFormula.activetaskFormulaId}" ${taskValueAttributeBo.formulaAppliedStat eq activetaskFormula.activetaskFormulaId?'selected':""}>${activetaskFormula.value}</option>
	                                  </c:forEach>
                                      </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                         </div>
	                        <!--  <div>
	                           <div class="gray-xs-f mb-sm">Time ranges options available to the mobile app user</div>
				               <div>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Day</span></span>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Week</span></span>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Month</span></span>
				                  <span class="txt-gray">(Rollback option provided for these three options)</span>
				               </div>
				            </div> -->
	                        </div>
	                            
	                         </div>
	                         </div>
	                    </div>
	                    </c:if>
	                    <c:if test="${taskMasterAttributeBo.orderByTaskType eq 9 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
	                        <input type="hidden" name="taskAttributeValueBos[8].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
	                        <input type="hidden" name="taskAttributeValueBos[8].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
	                        <input type="hidden" name="taskAttributeValueBos[8].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                        <div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>
	                        
	                        <div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
	                        <div class="chartSection" style="display:none">
	                          <div class="mb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" id="${taskMasterAttributeBo.attributeName}_chart_id" name="taskAttributeValueBos[8].addToLineChart" <c:if test="${taskValueAttributeBo.addToLineChart==true}">checked</c:if> value="${taskValueAttributeBo.addToLineChart}">
	                                <label for="${taskMasterAttributeBo.attributeName}_chart_id">Add to line chart</label>
	                            </span>  
	                          </div>   
	                           
	                          <div class="addLineChartBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.addToLineChart==true?'':'display:none'}">  
	                          <div class="pb-lg">
	                            <div class="gray-xs-f mt-md mb-sm">Time range for the chart<span class="requiredStar"> *</span> <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span></div>
	                              <div class="add_notify_option form-group mb-none">
		                           <select class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList requireClass" id="chartId1" name="taskAttributeValueBos[8].timeRangeChart" title="Select" >
		                              <c:forEach items="${timeRangeList}" var="timeRangeAttr">
		                                 <option value="${timeRangeAttr}" ${fn:escapeXml(taskValueAttributeBo.timeRangeChart) eq fn:escapeXml(timeRangeAttr)?'selected':''}>${timeRangeAttr}</option>
		                              </c:forEach>
		                            </select>
		                            <div class="mt-sm black-xs-f italic-txt activeaddToChartText" style="display: none;"></div>
		                            <div class="help-block with-errors red-txt"></div>
		                         </div>
	                           
	                          </div>
	                          
	                            
	                          <div class="pb-lg">
	                           <div class="gray-xs-f mb-sm">Allow rollback of chart?
	                           <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span>
                                </div>
	                              <div class="form-group">
	                                <span class="radio radio-info radio-inline p-45">
	                                    <input class="" type="radio" id="inlineRadio3" value="Yes" name="taskAttributeValueBos[8].rollbackChat" ${taskValueAttributeBo.rollbackChat eq 'Yes'?'checked':""}>
	                                    <label for="inlineRadio3">Yes</label>
	                                </span>
	                                <span class="radio radio-inline">
	                                    <input class="rollbackRadioClass" type="radio" id="inlineRadio4" value="No" name="taskAttributeValueBos[8].rollbackChat" <c:if test="${empty taskValueAttributeBo.rollbackChat  || empty taskValueAttributeBo}">checked</c:if> ${taskValueAttributeBo.rollbackChat eq 'No'?'checked':""}>
	                                    <label for="inlineRadio4">No</label>
	                                </span>
	                                <div class="help-block with-errors red-txt"></div>
	                              </div>
	                          </div>
	                           
	                        <div class="bor-b-dash">
	                         <div class="gray-xs-f mb-sm">Title for the chart <small>(30 characters max)</small><span class="requiredStar"> *</span>
                             </div>
	                             <div class="add_notify_option">
	                                 <div class="form-group">
	                                     <input type="text" class="form-control requireClass" id="lineChartId1" name="taskAttributeValueBos[8].titleChat" maxlength="30" value="${fn:escapeXml(taskValueAttributeBo.titleChat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                        </div>
	                        </div>
	                        </div>    
	                         <div class="pt-lg mt-xs pb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" class="statisticsBlock" id="${taskMasterAttributeBo.attributeName}_stat_id" name="taskAttributeValueBos[8].useForStatistic" <c:if test="${taskValueAttributeBo.useForStatistic==true}">checked</c:if> value="${taskValueAttributeBo.useForStatistic}">
	                                <label for="${taskMasterAttributeBo.attributeName}_stat_id">Use for Statistic</label>
	                            </span>  
	                          </div>
	                          <div class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.useForStatistic==true?'':'display:none'}">  
	                          <div>
	                            <div class="gray-xs-f mb-sm">Short name <small>(20 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option">
	                                 <div class="form-group statShortTitleClass">
	                                     <input type="hidden" id="dbidentifierId2" title="${taskValueAttributeBo.attributeValueId}" value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}">
	                                     <input autofocus="autofocus" type="text" class="form-control requireClass shortTitleStatCls" exist="${taskValueAttributeBo.useForStatistic==true?'Y':'N'}" dbid="${taskValueAttributeBo.attributeValueId}" custAttType="cust" id="identifierId2" name="taskAttributeValueBos[8].identifierNameStat" 
	                                     maxlength="20" value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}" <c:if test="${not empty taskValueAttributeBo.isIdentifierNameStatDuplicate && (taskValueAttributeBo.isIdentifierNameStatDuplicate gt 0)}"> disabled</c:if>/>
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                         </div>
	                            
	                         <div>
	                            <div class="gray-xs-f mb-sm">Display name for the Stat (e.g. Total Hours of Activity Over 6 Months) <small> (50 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="form-group">
	                                 <input type="text" class="form-control requireClass" name="taskAttributeValueBos[8].displayNameStat" maxlength="50" value="${fn:escapeXml(taskValueAttributeBo.displayNameStat)}"/>  
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                         </div>
	                         
	                            
	                         <div>
	                            <div class="gray-xs-f mb-sm">Display Units (e.g. hours) <small>(15 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option">
	                                 <div class="form-group">
	                                     <input type="text" class="form-control requireClass" name="taskAttributeValueBos[8].displayUnitStat" maxlength="15" value="${fn:escapeXml(taskValueAttributeBo.displayUnitStat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>
	                         </div>
	                         <div>
	                         <div>
	                            <div class="gray-xs-f mb-sm">Stat Type for image display<span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option form-group">
	                                  <select class="selectpicker  aq-select aq-select-form elaborateClass requireClass" title="Select" name="taskAttributeValueBos[8].uploadTypeStat">
                                      <c:forEach items="${statisticImageList}" var="statisticImage">
	                                    <option value="${statisticImage.statisticImageId}" ${taskValueAttributeBo.uploadTypeStat eq statisticImage.statisticImageId?'selected':''}>${statisticImage.value}</option>
	                                </c:forEach>
                                    </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                             </div>
	                         </div>
	                         <div>
	                            <div class="gray-xs-f mb-sm">Formula for to be applied<span class="requiredStar"> *</span></div>
	                             <div class="form-group">
	                                  <select class="selectpicker aq-select aq-select-form elaborateClass requireClass" title="Select" name="taskAttributeValueBos[8].formulaAppliedStat">
                                      <c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
	                                    <option value="${activetaskFormula.activetaskFormulaId}" ${taskValueAttributeBo.formulaAppliedStat eq activetaskFormula.activetaskFormulaId?'selected':""}>${activetaskFormula.value}</option>
	                                  </c:forEach>
                                      </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                         </div>
	                         <!-- <div>
	                           <div class="gray-xs-f mb-sm">Time ranges options available to the mobile app user</div>
				               <div>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Day</span></span>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Week</span></span>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Month</span></span>
				                  <span class="txt-gray">(Rollback option provided for these three options)</span>
				               </div>
				            </div> -->
	                        </div>
	                            
	                         </div>
	                    </div>
	                    </c:if>
	                    <c:if test="${taskMasterAttributeBo.orderByTaskType eq 10 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
	                        <input type="hidden" name="taskAttributeValueBos[9].attributeValueId" value="${taskValueAttributeBo.attributeValueId}">
	                        <input type="hidden" name="taskAttributeValueBos[9].activeTaskMasterAttrId" value="${taskMasterAttributeBo.masterId}">
	                        <input type="hidden" name="taskAttributeValueBos[9].addToDashboard" value="${taskMasterAttributeBo.addToDashboard}">
	                        <div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>
	                        <div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
	                        <div class="chartSection" style="display:none">
	                          <div class="mb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" id="${taskMasterAttributeBo.attributeName}_chart_id" name="taskAttributeValueBos[9].addToLineChart" <c:if test="${taskValueAttributeBo.addToLineChart==true}">checked</c:if> value="${taskValueAttributeBo.addToLineChart}">
	                                <label for="${taskMasterAttributeBo.attributeName}_chart_id">Add to line chart</label>
	                            </span>  
	                          </div>   
	                           
	                          <div class="addLineChartBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.addToLineChart==true?'':'display:none'}">  
	                          <div class="pb-lg">
	                            <div class="gray-xs-f mt-md mb-sm">Time range for the chart<span class="requiredStar"> *</span> <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span></div>
	                              <div class="add_notify_option form-group mb-none">
		                           <select class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList requireClass" id="chartId2" name="taskAttributeValueBos[9].timeRangeChart" title="Select" >
		                              <c:forEach items="${timeRangeList}" var="timeRangeAttr">
		                                 <option value="${timeRangeAttr}" ${fn:escapeXml(taskValueAttributeBo.timeRangeChart) eq fn:escapeXml(timeRangeAttr)?'selected':''}>${timeRangeAttr}</option>
		                              </c:forEach>
		                            </select>
		                            <div class="mt-sm black-xs-f italic-txt activeaddToChartText" style="display: none;"></div>
		                            <div class="help-block with-errors red-txt"></div>
		                         </div>
	                           
	                          </div>
	                          
	                            
	                          <div class="pb-lg">
	                           <div class="gray-xs-f mb-sm">Allow rollback of chart?
	                           <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span>
                                </div>
	                              <div class="form-group">
	                                <span class="radio radio-info radio-inline p-45">
	                                    <input class="" type="radio" id="inlineRadio5" value="Yes" name="taskAttributeValueBos[9].rollbackChat" ${taskValueAttributeBo.rollbackChat eq 'Yes'?'checked':""}>
	                                    <label for="inlineRadio5">Yes</label>
	                                </span>
	                                <span class="radio radio-inline">
	                                    <input class="rollbackRadioClass" type="radio" id="inlineRadio6" value="No" name="taskAttributeValueBos[9].rollbackChat" <c:if test="${empty taskValueAttributeBo.rollbackChat  || empty taskValueAttributeBo}">checked</c:if> ${taskValueAttributeBo.rollbackChat eq 'No'?'checked':""}>
	                                    <label for="inlineRadio6">No</label>
	                                </span>
	                                <div class="help-block with-errors red-txt"></div>
	                              </div>
	                          </div>
	                           
	                        <div class="bor-b-dash">
	                         <div class="gray-xs-f mb-sm">Title for the chart <small>(30 characters max)</small><span class="requiredStar"> *</span>
                             </div>
	                             <div class="add_notify_option">
	                                 <div class="form-group">
	                                     <input type="text" class="form-control requireClass" id="lineChartId2" name="taskAttributeValueBos[9].titleChat" maxlength="30" value="${fn:escapeXml(taskValueAttributeBo.titleChat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                        </div>
	                        </div>
	                        </div>    
	                         <div class="pt-lg mt-xs pb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" class="statisticsBlock" id="${taskMasterAttributeBo.attributeName}_stat_id" name="taskAttributeValueBos[9].useForStatistic" <c:if test="${taskValueAttributeBo.useForStatistic==true}">checked</c:if> value="${taskValueAttributeBo.useForStatistic}">
	                                <label for="${taskMasterAttributeBo.attributeName}_stat_id">Use for Statistic</label>
	                            </span>  
	                          </div>
	                          <div class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.useForStatistic==true?'':'display:none'}">  
	                          <div>
	                            <div class="gray-xs-f mb-sm">Short name <small>(20 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option">
	                                <div class="form-group statShortTitleClass">
	                                     <input type="hidden" id="dbidentifierId3" title="${taskValueAttributeBo.attributeValueId}" value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}">
	                                     <input autofocus="autofocus" type="text" class="form-control requireClass shortTitleStatCls" exist="${taskValueAttributeBo.useForStatistic==true?'Y':'N'}" dbid="${taskValueAttributeBo.attributeValueId}" custAttType="cust" id="identifierId3" name="taskAttributeValueBos[9].identifierNameStat" 
	                                     maxlength="20" value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}" <c:if test="${not empty taskValueAttributeBo.isIdentifierNameStatDuplicate && (taskValueAttributeBo.isIdentifierNameStatDuplicate gt 0)}"> disabled</c:if>/>
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                         </div>
	                            
	                         <div>
	                            <div class="gray-xs-f mb-sm">Display name for the Stat (e.g. Total Hours of Activity Over 6 Months) <small> (50 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="form-group">
	                                 <input type="text" class="form-control requireClass" name="taskAttributeValueBos[9].displayNameStat" maxlength="50" value="${fn:escapeXml(taskValueAttributeBo.displayNameStat)}"/>  
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                         </div>
	                         
	                            
	                         <div>
	                            <div class="gray-xs-f mb-sm">Display Units (e.g. hours) <small>(15 characters max)</small><span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option">
	                                 <div class="form-group">
	                                     <input type="text" class="form-control requireClass" name="taskAttributeValueBos[9].displayUnitStat" maxlength="15" value="${fn:escapeXml(taskValueAttributeBo.displayUnitStat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>
	                         </div>
	                         <div>
	                         <div>
	                            <div class="gray-xs-f mb-sm">Stat Type for image display<span class="requiredStar"> *</span></div>
	                             <div class="add_notify_option form-group">
	                                  <select class="selectpicker  aq-select aq-select-form elaborateClass requireClass" title="Select" name="taskAttributeValueBos[9].uploadTypeStat">
                                      <c:forEach items="${statisticImageList}" var="statisticImage">
	                                    <option value="${statisticImage.statisticImageId}" ${taskValueAttributeBo.uploadTypeStat eq statisticImage.statisticImageId?'selected':''}>${statisticImage.value}</option>
	                                </c:forEach>
                                    </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                             </div>
	                         </div>
	                         <div>
	                            <div class="gray-xs-f mb-sm">Formula for to be applied<span class="requiredStar"> *</span></div>
	                             <div class="form-group">
	                                  <select class="selectpicker aq-select aq-select-form elaborateClass requireClass" title="Select" name="taskAttributeValueBos[9].formulaAppliedStat">
                                      <c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
	                                    <option value="${activetaskFormula.activetaskFormulaId}" ${taskValueAttributeBo.formulaAppliedStat eq activetaskFormula.activetaskFormulaId?'selected':""}>${activetaskFormula.value}</option>
	                                  </c:forEach>
                                      </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                         </div>
	                         <!-- <div>
	                           <div class="gray-xs-f mb-sm">Time ranges options available to the mobile app user</div>
				               <div>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Day</span></span>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Week</span></span>
				                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Month</span></span>
				                  <span class="txt-gray">(Rollback option provided for these three options)</span>
				               </div>
				            </div> -->
	                        </div>
	                            
	                         </div>
	                    </div>
	                    </c:if>
			</c:forEach>	
			</c:forEach>
							
		</c:if>
		<div class="mt-md ml-xs">
			<div class="gray-xs-f mb-sm">Time ranges options available to the mobile app user</div>
			<div>
				<span class="mr-lg"><span class="mr-sm"><img
						src="../images/icons/tick.png" /></span><span>Current Day</span></span> <span
					class="mr-lg"><span class="mr-sm"><img
						src="../images/icons/tick.png" /></span><span>Current Week</span></span> <span
					class="mr-lg"><span class="mr-sm"><img
						src="../images/icons/tick.png" /></span><span>Current Month</span></span> <span
					class="txt-gray">(Rollback option provided for these
					three options)</span>
			</div>
		</div>
	</form:form>
</div>
<script>
$(document).ready(function(){
	var taskId = $('#taskContentId').val();
    if(taskId){
 	   var frequencyType = '${activeTaskBo.frequency}';
 	   if(frequencyType && frequencyType != 'One time')
 	      $('.chartSection').show();
 	   if(frequencyType && frequencyType == 'Manually Schedule'){
 		   $('.activeaddToChartText').show();
			   $('.activeaddToChartText').html('A max of x runs will be displayed in each view of the chart.');
 	   }
    }
    $('#initialspanId').blur(function(){	
    	var value= $(this).val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
        if(value){
        	if(parseInt($(this).val()) < 2){
            	$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Initial Span must be >= 2</li></ul>");
            }
        	if(parseInt($(this).val()) > 20){
    			$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Initial Span should be <= 20  </li></ul>");
    		}
        	var minimumSpanVal = $('#minimumspanId').val();
        	if(minimumSpanVal && (parseInt(minimumSpanVal) > parseInt($(this).val()))){
        		$('#minimumspanId').val('');
       		    $('#minimumspanId').parent().addClass("has-danger").addClass("has-error");
                $('#minimumspanId').parent().find(".help-block").empty();
                $('#minimumspanId').parent().find(".help-block").append("<ul class='list-unstyled'><li>Minimum Span should be always <= Initial Span</li></ul>");
        	}
        	var maxmimumSpanVal = $('#maximumspanId').val();
        	if(maxmimumSpanVal && (parseInt(maxmimumSpanVal) < parseInt($(this).val()))){
        		$('#maximumspanId').val('');
       		    $('#maximumspanId').parent().addClass("has-danger").addClass("has-error");
                $('#maximumspanId').parent().find(".help-block").empty();
                $('#maximumspanId').parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Span should be always >= Initial Span</li></ul>");
        	}
        }else{
        	/* $(this).val('');
   		    $(this).parent().addClass("has-danger").addClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Initial Span must be >= 2</li></ul>"); */
        }
    });
    $("#minimumspanId").blur(function(){	
    	var value= $(this).val();
    	var initialSpanVal = $('#initialspanId').val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
    	if(initialSpanVal){
    		if(parseInt($(this).val()) > parseInt(initialSpanVal)){
    			$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Minimum Span should be always <= Initial Span  </li></ul>");
    		}
    	}else{
    		/* $('#initialspanId').val('');
   		    $('#initialspanId').parent().addClass("has-danger").addClass("has-error");
            $('#initialspanId').parent().find(".help-block").empty();
            $('#initialspanId').parent().find(".help-block").append("<ul class='list-unstyled'><li>Initial Span must be >= 2</li></ul>");
            
            $(this).val('');
   		    $(this).parent().addClass("has-danger").addClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Minimum Span should be always <= Initial Span  </li></ul>"); */
    	}
    });
    
    $("#maximumspanId").blur(function(){	
    	var value= $(this).val();
    	var initialSpanVal = $('#initialspanId').val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
    	if(initialSpanVal){
    		if(parseInt($(this).val()) > 20){
    			$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Span should be <= 20  </li></ul>");
    		}else if(parseInt($(this).val()) < parseInt(initialSpanVal)){
    			$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Span should be always >= Initial Span</li></ul>");
    		}
    	}else{
    		/* $('#initialspanId').val('');
   		    $('#initialspanId').parent().addClass("has-danger").addClass("has-error");
            $('#initialspanId').parent().find(".help-block").empty();
            $('#initialspanId').parent().find(".help-block").append("<ul class='list-unstyled'><li>Initial Span must be >= 2</li></ul>");
            
            $(this).val('');
   		    $(this).parent().addClass("has-danger").addClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Span should be always >= Initial Span</li></ul>"); */
    	}
    });
    
    $("#playspeedId").blur(function(){
    	var value= $(this).val();
    	$("#playspeedId").parent().removeClass("has-danger").removeClass("has-error");
        $("#playspeedId").parent().find(".help-block").empty();
        console.log("playspeedId value:"+value);
        if(value){
        	if(value == '.'){
        		$("#playspeedId").val('');
       		    $("#playspeedId").parent().addClass("has-danger").addClass("has-error");
                $("#playspeedId").parent().find(".help-block").empty();
                $("#playspeedId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter a valid number</li></ul>");
        	}else if(parseFloat(value) < 0.5){
            	$("#playspeedId").val('');
       		    $("#playspeedId").parent().addClass("has-danger").addClass("has-error");
                $("#playspeedId").parent().find(".help-block").empty();
                $("#playspeedId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Play Speed should be >= 0.5 seconds  </li></ul>");
            }
        	if(parseFloat(value) > parseFloat(20)){
    			$("#playspeedId").val('');
       		    $("#playspeedId").parent().addClass("has-danger").addClass("has-error");
                $("#playspeedId").parent().find(".help-block").empty();
                $("#playspeedId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Play Speed should be <= 20 seconds</li></ul>");
    		}
        }else{
        	//$("#playspeedId").val('');
   		    //$("#playspeedId").parent().addClass("has-danger").addClass("has-error");
            //$("#playspeedId").parent().find(".help-block").empty();
            //$("#playspeedId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Play Speed should be >= 0.5 seconds  </li></ul>");
        }
    });
    $("#maximumtestId").blur(function(){	
    	var value= $(this).val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
    	if(parseInt($(this).val()) < 1){
    		$(this).val('');
   		    $(this).parent().addClass("has-danger").addClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Tests should be >= 1</li></ul>");
    	}
    	var maximumFailure = $('#maximumFailureId').val();
    	if(value && parseInt(maximumFailure) >= parseInt($(this).val())){
    		$('#maximumFailureId').val('');
   		    $('#maximumFailureId').parent().addClass("has-danger").addClass("has-error");
            $('#maximumFailureId').parent().find(".help-block").empty();
            $('#maximumFailureId').parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Consecutive Failures should be always < Maximum tests</li></ul>");
    	}
    });
    $("#maximumFailureId").blur(function(){	
    	var value= $(this).val();
    	var maxmimunTestVal = $('#maximumtestId').val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
    	if(parseInt($(this).val()) < 1){
    		$(this).val('');
   		    $(this).parent().addClass("has-danger").addClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Tests should be >= 1</li></ul>");
    	}
    	if(maxmimunTestVal && parseInt($(this).val()) >= parseInt(maxmimunTestVal)){
    		$(this).val('');
   		    $(this).parent().addClass("has-danger").addClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Maximum Consecutive Failures should be always < Maximum tests</li></ul>");
    	}
    });
    
	$("#shortTitleId").blur(function(){
    	  validateShortTitleId('',function(val){});
    })
    $('#static1, #static2, #static3').on('keyup',function(){
   	  $(this).parent().find(".help-block").empty();
	  $('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
    });
    $('#static1, #static2, #static3').blur(function(){
    	validateShortTitleStatId('', this, function(val){});
    })
     $('#identifierId1').blur(function(){
    	validateShortTitleStatId('', this, function(val){});
    })
     $('#identifierId2').blur(function(){
    	validateShortTitleStatId('', this, function(val){});
    })
     $('#identifierId3').blur(function(){
    	 //alert("1");
    	validateShortTitleStatId('', this, function(val){});
    })
    $('#identifierId1, #identifierId2, #identifierId3').on('keyup',function(){
   	  $(this).parent().find(".help-block").empty();
	  $('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
    });
    setLineChatStatCheckedVal();
    $('#Score_spatial_chart_id').on('click',function(){
	        	   if($(this).is(":checked")){
	        			$('.addLineChartBlock_Score_spatial').css("display","");
	        			$('.addLineChartBlock_Score_spatial').find('.requireClass').attr('required', true);
	        			$('#Score_spatial_chart_id').val(true);
	        			$('.selectpicker').selectpicker('refresh');
	        	   }else{
	        	   	 $('.addLineChartBlock_Score_spatial').css("display","none");
	        	   	 $('.addLineChartBlock_Score_spatial').find('.requireClass').attr('required', false);
	        	   	 $('#Score_spatial_chart_id').val(false);
	        	   }
	        	   resetValidation($(this).parents('form'));
   });
   $('#Score_spatial_stat_id').on('click',function(){
	        	   if($(this).is(":checked")){
	        		    //alert("checked ...");
	        			$('.addLineStaticBlock_Score_spatial').css("display","");
	        			$('.addLineStaticBlock_Score_spatial').find('.requireClass').attr('required', true);
	        			$('#Score_spatial_stat_id').val(true);
	        			$('.addLineStaticBlock_Score_spatial').find('.shortTitleStatCls').attr('exist','Y');
	        			//alert("attr value::"+ $('.addLineStaticBlock_Score_spatial').find('.shortTitleStatCls').attr('exist'));
	        			$('.selectpicker').selectpicker('refresh');
	        	   }else{
	        		// alert("Not checked ...");  
	        	   	 $('.addLineStaticBlock_Score_spatial').css("display","none");
	        	   	 $('.addLineStaticBlock_Score_spatial').find('.requireClass').attr('required', false);
	        	   	 $('.addLineStaticBlock_Score_spatial').find('.shortTitleStatCls').attr('exist','N');
	        	   	// alert("attr value::"+ $('.addLineStaticBlock_Score_spatial').find('.shortTitleStatCls').attr('exist'));
	        	   	 $('#Score_spatial_stat_id').val(false);
	        	   }
    });
   $('#Number_of_Games_spatial_chart_id').on('click',function(){
	   if($(this).is(":checked")){
			$('.addLineChartBlock_Number_of_Games_spatial').css("display","");
			$('.addLineChartBlock_Number_of_Games_spatial').find('.requireClass').attr('required', true);
			$('#Number_of_Games_spatial_chart_id').val(true);
			$('.selectpicker').selectpicker('refresh');
	   }else{
	   	 $('.addLineChartBlock_Number_of_Games_spatial').css("display","none");
	   	 $('.addLineChartBlock_Number_of_Games_spatial').find('.requireClass').attr('required', false);
	   	 $('#Number_of_Games_spatial_chart_id').val(false);
	   }
	   resetValidation($(this).parents('form'));
	});
	$('#Number_of_Games_spatial_stat_id').on('click',function(){
		   if($(this).is(":checked")){
				$('.addLineStaticBlock_Number_of_Games_spatial').css("display","");
				$('.addLineStaticBlock_Number_of_Games_spatial').find('.requireClass').attr('required', true);
				$('#Number_of_Games_spatial_stat_id').val(true);
				$('.addLineStaticBlock_Number_of_Games_spatial').find('.shortTitleStatCls').attr('exist','Y');
				$('.selectpicker').selectpicker('refresh');
		   }else{
		   	 $('.addLineStaticBlock_Number_of_Games_spatial').css("display","none");
		   	 $('.addLineStaticBlock_Number_of_Games_spatial').find('.requireClass').attr('required', false);
		   	 $('.addLineStaticBlock_Number_of_Games_spatial').find('.shortTitleStatCls').attr('exist','N');
		   	 $('#Number_of_Games_spatial_stat_id').val(false);
		   }
	});
	$('#Number_of_Failures_spatial_chart_id').on('click',function(){
		   if($(this).is(":checked")){
				$('.addLineChartBlock_Number_of_Failures_spatial').css("display","");
				$('.addLineChartBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', true);
				$('#Number_of_Failures_spatial_chart_id').val(true);
				$('.selectpicker').selectpicker('refresh');
		   }else{
		   	 $('.addLineChartBlock_Number_of_Failures_spatial').css("display","none");
		   	 $('.addLineChartBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', false);
		   	 $('#Number_of_Failures_spatial_chart_id').val(false);
		   }
		   resetValidation($(this).parents('form'));
		});
		$('#Number_of_Failures_spatial_stat_id').on('click',function(){
			   if($(this).is(":checked")){
					$('.addLineStaticBlock_Number_of_Failures_spatial').css("display","");
					$('.addLineStaticBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', true);
					$('#Number_of_Failures_spatial_stat_id').val(true);
					$('.addLineStaticBlock_Number_of_Failures_spatial').find('.shortTitleStatCls').attr('exist','Y');
					$('.selectpicker').selectpicker('refresh');
			   }else{
			   	 $('.addLineStaticBlock_Number_of_Failures_spatial').css("display","none");
			   	 $('.addLineStaticBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', false);
			   	 $('.addLineStaticBlock_Number_of_Failures_spatial').find('.shortTitleStatCls').attr('exist','N');
			   	 $('#Number_of_Failures_spatial_stat_id').val(false);
			   }
		});
	    $(document).on('click', '#doneId', function(e){
		  	$("body").addClass('loading');
		  	$("#doneId").attr("disabled",true);
	        if($('#pickStartDate').val() == ''){
			    $('#pickStartDate').attr("readonly",false);	
			}
			if($('#startWeeklyDate').val() == ''){
				$('#startWeeklyDate').attr("readonly",false);	
			}
			$("#initialspanId").trigger('blur');
			$("#minimumspanId").trigger('blur');
			$("#maximumspanId").trigger('blur');
			$("#playspeedId").trigger('blur');
			$("#maximumtestId").trigger('blur');
			$("#maximumFailureId").trigger('blur');
			if(isFromValid("#activeContentFormId")){
				  $('.scheduleTaskClass').removeAttr('disabled');
			      $('.scheduleTaskClass').removeClass('linkDis');
			      var shortTitle = $('#shortTitleId').val();
			      if(shortTitle){
			    validateShortTitleId('', function(st){
				  if(st){
			      var scoreStat = $('#Score_spatial_stat_id').is(":checked");
			      var gameStat = $('#Number_of_Games_spatial_stat_id').is(":checked");
			      var failureStat = $('#Number_of_Failures_spatial_stat_id').is(":checked");
			      var dbStatExist = true;
			      var statShortVal1 = '', statShortVal2 = '', statShortVal3 = '';
			      var statShortId1 = '', statShortId2 = '', statShortId3 = '';
			      var dbShortVal1 = '', dbShortVal2 = '', dbShortVal3 = '';
			      var dbShortId1 = '', dbShortId2 = '', dbShortId3 = '';
			      var statisticsData = $('.shortTitleStatCls').attr('id');
			      if(statisticsData){
			    	  var count = statisticsData.indexOf('identifier');
			    	  if(count == -1){
			    		  dbStatExist = false; 
			    	  }
			      }
			      if(dbStatExist){
			    	  if(scoreStat){
			    		  statShortId1 = "identifierId1";
			    		  dbShortVal1 = $('#dbidentifierId1').val();
			    		  dbShortId1 =  $('#dbidentifierId1').attr("title");
			    		  statShortVal1 = $('#identifierId1').val();
			    	  }
			    	  if(gameStat){
			    		  statShortId2 = "identifierId2";
			    		  dbShortVal2 = $('#dbidentifierId2').val();
			    		  dbShortId2 =  $('#dbidentifierId2').attr("title");
			    		  statShortVal2 = $('#identifierId2').val();
			    	  }
			    	  if(failureStat){
			    		  statShortId3 = "identifierId3";
			    		  dbShortVal3 = $('#dbidentifierId3').val();
			    		  dbShortId3 =  $('#dbidentifierId3').attr("title");
			    		  //alert("dbShortId3"+dbShortId3);
			    		  statShortVal3 = $('#identifierId3').val(); 
			    	  } 
			      }else{
			    	  if(scoreStat){
			    		  statShortId1 = "static1";
			    		  statShortVal1 = $('#static1').val();
			    	  }
			    	  if(gameStat){
			    		  statShortId2 = "static2";
			    		  statShortVal2 = $('#static2').val();
			    	  }
			    	  if(failureStat){
			    		  //alert("1");
			    		  statShortId3 = "static3";
			    		  statShortVal3 = $('#static3').val(); 
			    	  }
			      }
			      var jsonArray  = new Array();
			      if(scoreStat){
			    	  var statObj = new Object();
			    	  statObj.id = statShortId1;
			    	  statObj.dbVal = dbShortVal1;
			    	  statObj.idVal = statShortVal1;
			    	  if(dbShortId1)
			    	    statObj.idname = dbShortId1; 
			    	  jsonArray.push(statObj);
			      }
			      if(gameStat){
			    	  var statObj = new Object();
			    	  statObj.id = statShortId2;
			    	  statObj.dbVal = dbShortVal2;
			    	  statObj.idVal = statShortVal2;
			    	  if(dbShortId2)
				    	    statObj.idname = dbShortId2; 
			    	  jsonArray.push(statObj);
			      }
			      if(failureStat){
			    	  var statObj = new Object();
			    	  statObj.id = statShortId3;
			    	  statObj.dbVal = dbShortVal3;
			    	  statObj.idVal = statShortVal3;
			    	  if(dbShortId3)
				    	  statObj.idname = dbShortId3; 
			    	  jsonArray.push(statObj);
			      }
			      if(jsonArray.length>0){
			    	  validateStatisticsIds(jsonArray, function(val){
			    		  if(val){
			    			  $("#doneId").attr("disabled",false);
							  $("body").removeClass('loading');
			    			  doneActiveTask(this, 'done', function(val) {
				 					if(val) {
				 						    $('.shortTitleCls,.shortTitleStatCls').prop('disabled', false);
				 	                      	$("#buttonText").val('completed');
				 	                      	document.activeContentFormId.submit();
				 					}
				 			      }) 
			    		  }else{
			    			  //alert("Not");
			    			  $("#doneId").attr("disabled",false);
							  $("body").removeClass('loading');
							  showErrMsg("Please fill in all mandatory fields.");
				          	  $('.contentClass a').tab('show');
			    		  }
			    	  });
			      }else{
			    	  $("#doneId").attr("disabled",false);
					  $("body").removeClass('loading');
	 				  doneActiveTask(this, 'done', function(val) {
	 					if(val) {
	 						    $('.shortTitleCls,.shortTitleStatCls').prop('disabled', false);
	 	                      	$("#buttonText").val('completed');
	 	                      	document.activeContentFormId.submit();
	 					}
	 			      }) 
			     }
				 }else{
						$("#doneId").attr("disabled",false);
						$("body").removeClass('loading');
				 }
			   }); 
			   }else{
				   $("#doneId").attr("disabled",false);
				   $("body").removeClass('loading');
				   $('.contentClass a').tab('show');
			   }   
			}else{
					console.log("else of Done");
					$("body").removeClass('loading');
					$("#doneId").attr("disabled",false);
					$('.contentClass a').tab('show');
					showErrMsg("Please fill in all mandatory fields.");
			}
	    });
	    
        $('#saveId').click(function(e) {
       	 $("body").addClass('loading');
       	 $("#saveId").attr("disabled",true);
       	 var shortTitleCount = $('.shortTitleClass').find('.help-block').children().length;
       	 if(shortTitleCount >=1){
       		 showErrMsg("Please fill in all mandatory fields.");
       		 $('.contentClass a').tab('show');
       		 $("body").removeClass('loading');
       		 $("#saveId").attr("disabled",false);
                return false;
       	 }else if(!$('#shortTitleId')[0].checkValidity()){
            	 $("#shortTitleId").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
            	 showErrMsg("Please fill in all mandatory fields.");
            	 $('.contentClass a').tab('show');
                $("body").removeClass('loading');
                $("#saveId").attr("disabled",false);
                return false;
         } else {
       	        validateShortTitleId('', function(st){
        		if(st){
        		  var scoreStat = $('#Score_spatial_stat_id').is(":checked");
  			      var gameStat = $('#Number_of_Games_spatial_stat_id').is(":checked");
  			      var failureStat = $('#Number_of_Failures_spatial_stat_id').is(":checked");
  			      var dbStatExist = true;
  			      var statShortVal1 = '', statShortVal2 = '', statShortVal3 = '';
  			      var statShortId1 = '', statShortId2 = '', statShortId3 = '';
  			      var dbShortVal1 = '', dbShortVal2 = '', dbShortVal3 = '';
  			      var dbShortId1 = '', dbShortId2 = '', dbShortId3 = '';
  			      var statisticsData = $('.shortTitleStatCls').attr('id');
  			      if(statisticsData){
  			    	  var count = statisticsData.indexOf('identifier');
  			    	  if(count == -1){
  			    		  dbStatExist = false; 
  			    	  }
  			      }
  			      if(dbStatExist){
  			    	  if(scoreStat){
  			    		  statShortId1 = "identifierId1";
  			    		  dbShortVal1 = $('#dbidentifierId1').val();
  			    		  dbShortId1 =  $('#dbidentifierId1').attr("title");
  			    		  statShortVal1 = $('#identifierId1').val();
  			    	  }
  			    	  if(gameStat){
  			    		  statShortId2 = "identifierId2";
  			    		  dbShortVal2 = $('#dbidentifierId2').val();
  			    		  dbShortId2 =  $('#dbidentifierId2').attr("title");
  			    		  statShortVal2 = $('#identifierId2').val();
  			    	  }
  			    	  if(failureStat){
  			    		  statShortId3 = "identifierId3";
  			    		  dbShortVal3 = $('#dbidentifierId3').val();
  			    		  dbShortId3 =  $('#dbidentifierId3').attr("title");
  			    		  //alert("dbShortId3"+dbShortId3);
  			    		  statShortVal3 = $('#identifierId3').val(); 
  			    	  } 
  			      }else{
  			    	  if(scoreStat){
  			    		  statShortId1 = "static1";
  			    		  statShortVal1 = $('#static1').val();
  			    	  }
  			    	  if(gameStat){
  			    		  statShortId2 = "static2";
  			    		  statShortVal2 = $('#static2').val();
  			    	  }
  			    	  if(failureStat){
  			    		  //alert("1");
  			    		  statShortId3 = "static3";
  			    		  statShortVal3 = $('#static3').val(); 
  			    	  }
  			      }
  			      var jsonArray  = new Array();
  			      if(scoreStat){
  			    	  var statObj = new Object();
  			    	  statObj.id = statShortId1;
  			    	  statObj.dbVal = dbShortVal1;
  			    	  statObj.idVal = statShortVal1;
  			    	  if(dbShortId1)
  			    	    statObj.idname = dbShortId1; 
  			    	  jsonArray.push(statObj);
  			      }
  			      if(gameStat){
  			    	  var statObj = new Object();
  			    	  statObj.id = statShortId2;
  			    	  statObj.dbVal = dbShortVal2;
  			    	  statObj.idVal = statShortVal2;
  			    	  if(dbShortId2)
  				    	    statObj.idname = dbShortId2; 
  			    	  jsonArray.push(statObj);
  			      }
  			      if(failureStat){
  			    	  var statObj = new Object();
  			    	  statObj.id = statShortId3;
  			    	  statObj.dbVal = dbShortVal3;
  			    	  statObj.idVal = statShortVal3;
  			    	  if(dbShortId3)
  				    	  statObj.idname = dbShortId3; 
  			    	  jsonArray.push(statObj);
  			      }
  			      if(jsonArray.length>0){
  			    	saveValidateStatisticsIds(jsonArray, function(val){
  			    		  if(val){
  			    			  $("#saveId").attr("disabled",false);
  							  $("body").removeClass('loading');
  			    			  doneActiveTask(this, 'save', function(val) {
  				 					if(val) {
  				 						    $('.shortTitleCls,.shortTitleStatCls').prop('disabled', false);
  				 	                      	$("#buttonText").val('save');
  				 	                      	document.activeContentFormId.submit();
  				 					}
  				 			      }) 
  			    		  }else{
  			    			 // alert("Not");
  			    			  $("#saveId").attr("disabled",false);
  							  $("body").removeClass('loading');
  							  showErrMsg("Please fill in all mandatory fields.");
  				          	  $('.contentClass a').tab('show');
  			    		  }
  			    	  });
  			      }else{
  			    	  $("#saveId").attr("disabled",false);
  					  $("body").removeClass('loading');
  	 				  doneActiveTask(this, 'save', function(val) {
  	 					if(val) {
  	 						    $('.shortTitleCls,.shortTitleStatCls').prop('disabled', false);
  	 	                      	$("#buttonText").val('save');
  	 	                      	document.activeContentFormId.submit();
  	 					}
  	 			      }) 
  			     }	  
        		}else{
        			$("body").removeClass('loading');
        			$("#saveId").attr("disabled",false);
        		}
        	   });
           }
		});
	    
	    
	    
	$('.selectpicker').selectpicker('refresh');
	$('[data-toggle="tooltip"]').tooltip();
	$('input').on('drop', function() {
	    return false;
	});
	$(document).find('input[type = text][custAttType != cust]').keyup(function(e){
		var evt = (e) ? e : window.event;
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if(charCode == 16)
	    	isShift = false;
	    if(!isShift && $(this).val()) {
			var regularExpression = /^[ A-Za-z0-9!\$%&\*\(\)_+|:"?,.\/;'\[\]=\-><@]*$/;
			if(!regularExpression.test($(this).val())) {
				var newVal = $(this).val().replace(/[^ A-Za-z0-9!\$%&\*\(\)_+|:"?,.\/;'\[\]=\-><@]/g, '');
				e.preventDefault();
				$(this).val(newVal);
				$(this).parent().addClass("has-danger has-error");
				$(this).parent().find(".help-block").empty().html("<ul class='list-unstyled'><li>Special characters such as #^}{ are not allowed.</li></ul>");
			}
	    }
	});
	$(document).find('input[type = text][custAttType = cust]').keyup(function(e) {
		var evt = (e) ? e : window.event;
	    var charCode = (evt.which) ? evt.which : evt.keyCode;
	    if(charCode == 16)
	    	isShift = false;
	    if(!isShift && $(this).val()) {
	    	var regularExpression = /^[A-Za-z0-9*()_+|:.-]*$/;
			if(!regularExpression.test($(this).val())) {
				var newVal = $(this).val().replace(/[^A-Za-z0-9\*\(\)_+|:.\-]/g, '');
				e.preventDefault();
				$(this).val(newVal);
				$(this).parent().addClass("has-danger has-error");
				$(this).parent().find(".help-block").empty().html("<ul class='list-unstyled'><li>The characters like (< >) are not allowed.</li></ul>");
			}
	    }
	});
});
function validateShortTitleId(item,callback){
	   console.log("validateShortTitleId");
		var shortTitle = $("#shortTitleId").val();
	 	var thisAttr= $("#shortTitleId");
	 	var existedKey = '${activeTaskBo.shortTitle}';
	 	var activeTaskAttName = 'shortTitle';
 	    var activeTaskAttIdVal = shortTitle;
	    var activeTaskAttIdName = "not";
	 	if(shortTitle != null && shortTitle !='' && typeof shortTitle!= 'undefined'){
	 		$(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
	        $(thisAttr).parent().find(".help-block").empty();
	        $('.shortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
		    $('.shortTitleClass').parent().find(".help-block").empty();
		     if(existedKey !=shortTitle){
	 			$.ajax({
	 				url: "/fdahpStudyDesigner/adminStudies/validateActiveTaskShortTitleId.do?_S=${param._S}",
	                type: "POST",
	                datatype: "json",
	                data: {
	             	   activeTaskAttName:activeTaskAttName,
	             	   activeTaskAttIdVal:activeTaskAttIdVal,
	             	   activeTaskAttIdName:activeTaskAttIdName,
	                   "${_csrf.parameterName}":"${_csrf.token}",
	                 },
	                 success:  function getResponse(data){
	                     var message = data.message;
	                     console.log(message);
	                     if('SUCCESS' != message){
	                         $(thisAttr).validator('validate');
	                         $('.shortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
	                         $('.shortTitleClass').parent().find(".help-block").empty();
	                         callback(true);
	                     }else{
	                         $(thisAttr).val('');
	                         $('.shortTitleClass').parent().addClass("has-danger").addClass("has-error");
	                         $('.shortTitleClass').parent().find(".help-block").empty();
	                         $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + shortTitle + "' has already been used in the past.</li></ul>");
	                        // $('#shortTitleId').focus();
	                         callback(false);
	                     }
	                 },
	                 global : false
	           });
		     }else{
		 			callback(true);
		 			$('.shortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
		 	        $('.shortTitleClass').parent().find(".help-block").html("");
		 	}
	 	}else{
	 		callback(false);
	 	}
	}
	
function validateShortTitleStatId(event, thisAttr, callback){
	   var activeTaskAttName = 'identifierNameStat';
	   var activeTaskAttIdVal = $(thisAttr).val();
	   var activeTaskAttIdName = $(thisAttr).attr('id');
	   var dbId = $(thisAttr).attr('dbid');
	   $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
	   $(thisAttr).parent().find(".help-block").empty();
	   var statIds = "";
	   if(dbId)
		   statIds = dbId;
	   //validation with other statistics if short  title is there .
	   //if not valid then display duplicate data 
	   if(activeTaskAttIdVal){
		   var count = 0;
		   $(".shortTitleStatCls").each(function() {
			   var flag = $(this).attr('exist');
			   var statAttId = this.id;
			   if(flag && flag=='Y'){
				   var statAttId = this.id;
				   var dbStatAttId = $(this).attr('dbid');
				   var val = $(this).val();
				   if(val && statAttId!=activeTaskAttIdName){
					   if(dbStatAttId)
					    statIds = statIds +","+ dbStatAttId;
					   if(val.toLowerCase()  == activeTaskAttIdVal.toLowerCase()){
						   count = count + 1;
					   }
				   }  
			   }
		   });
		   if(count>0){
			  // alert("count");
			   $(thisAttr).val('');
			   $(thisAttr).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error");
			   $(thisAttr).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
          	   $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + activeTaskAttIdVal + "' has already been used in the past.</li></ul>");
          	   showErrMsg("Please fill in all mandatory fields.");
          	   $('.contentClass a').tab('show');
          	   shortTitleStatFlag = false;
			   callback(false);
		   }else{
			  // alert("count0");
			   var staticShortTitleId = activeTaskAttIdName;
			   if(activeTaskAttIdName == 'static1' || activeTaskAttIdName == 'static2' || activeTaskAttIdName == 'static3'){
				  // alert("static data");
				   activeTaskAttIdName = 'static'; 
			    	 $.ajax({
			               url: "/fdahpStudyDesigner/adminStudies/validateActiveTaskShortTitleId.do?_S=${param._S}",
			               type: "POST",
			               datatype: "json",
			               data: {
			            	   activeTaskAttName:activeTaskAttName,
			            	   activeTaskAttIdVal:activeTaskAttIdVal,
			            	   activeTaskAttIdName:activeTaskAttIdName,
			                   "${_csrf.parameterName}":"${_csrf.token}",
			               },
			               success: function emailValid(data, status) {
			            	   var jsonobject = eval(data);
			                   var message = jsonobject.message;
			                   if('SUCCESS' != message){
			                	     $(thisAttr).validator('validate');
			                	     $('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
			                	     $('.statShortTitleClass').parent().find(".help-block").empty();
			                         if (callback)
			     						callback(true);
			                     }else{
			                    	 $(thisAttr).val('');
			                    	 $(thisAttr).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error");
			          			     $(thisAttr).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
			                    	 $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + activeTaskAttIdVal + "' has already been used in the past.</li></ul>");
			                    	 showErrMsg("Please fill in all mandatory fields.");
			                    	 $('.contentClass a').tab('show');
			                    	 if (callback)
			     						callback(false);
			                         
			                     }
			               },
			               error:function status(data, status) {
			            	   callback(false);
			               },
			               global : false
			           });
			   }else{
			    	//alert("not static");
			    	 var dbIdentifierVal = '';
			    	 if(activeTaskAttIdName == 'identifierId1'){
			    		 dbIdentifierVal = $('#dbidentifierId1').val();
			    	 }else if(activeTaskAttIdName == 'identifierId2'){
			    		 dbIdentifierVal = $('#dbidentifierId2').val(); 
			    	 }else if(activeTaskAttIdName == 'identifierId3'){
			    		 dbIdentifierVal = $('#dbidentifierId3').val();
			    	 }
			    	if(dbIdentifierVal!=activeTaskAttIdVal){
			    		// alert("statIds:::"+statIds);
				    	 if(statIds){
				    		 activeTaskAttIdName = statIds; 
				    	 }else{
				    		 activeTaskAttIdName = 'static'; 
				    	 }
			    		 //alert("ajax");
			    		  $.ajax({
				               url: "/fdahpStudyDesigner/adminStudies/validateActiveTaskShortTitleId.do?_S=${param._S}",
				               type: "POST",
				               datatype: "json",
				               data: {
				            	   activeTaskAttName:activeTaskAttName,
				            	   activeTaskAttIdVal:activeTaskAttIdVal,
				            	   activeTaskAttIdName:activeTaskAttIdName,
				                   "${_csrf.parameterName}":"${_csrf.token}",
				               },
				               success: function emailValid(data, status) {
				            	   var jsonobject = eval(data);
				                   var message = jsonobject.message;
				                   if('SUCCESS' != message){
				                	     $(thisAttr).validator('validate');
				                	     $(thisAttr).parent().find('.statShortTitleClass').removeClass("has-danger").removeClass("has-error");
				          			     $(thisAttr).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
				                	     shortTitleStatFlag = true;
				                	     callback(true);
				                     }else{
				                    	 $(thisAttr).val('');
				                    	 $(thisAttr).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error");
				          			     $(thisAttr).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
				                    	 $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + activeTaskAttIdVal + "' has already been used in the past.</li></ul>");
				                    	 $(thisAttr).focus();
				                    	 showErrMsg("Please fill in all mandatory fields.");
				                    	 $('.contentClass a').tab('show');
				                    	 shortTitleStatFlag = false;
				     					 callback(false);
				                     }
				               },
				               error:function status(data, status) {
				            	   callback(false);
				               },
				               global : false
				           });
			    	 }else{
				 			callback(true);
				 			$(thisAttr).parent().find('.statShortTitleClass').removeClass("has-danger").removeClass("has-error");
	          			    $(thisAttr).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
				 	  }
			     }
		  }
	   }else{
	 		callback(false);
	   }
}
function validateStatisticsIds(jsonDatas, callback){
	var flag = true;
	var arrayLength = jsonDatas.length; //cache the array length
	var shortSatId = '';
	var shortSatIdVal = '';
	//alert("inside stat");
	 if (arrayLength > 1) { 
		for(var i=0;i<arrayLength ; i++){
			   var existId = jsonDatas[i].id; 
			   var existVal = jsonDatas[i].idVal;  
			   if(existVal){
				   for(var j = 0; j<arrayLength ; j++) {
					   var statId = jsonDatas[j].id;
					   var statVal = jsonDatas[j].idVal; 
				       if (existId!=statId && existVal.toLowerCase() == statVal.toLowerCase()) {
				    	   flag = false;
				    	   shortSatId = jsonDatas[j].id;
				    	   shortSatIdVal = jsonDatas[j].idVal;
				    	   break;
				       }
				   }   
			   }else{
				   shortSatId = existId;
		    	   flag = false;
			   }
	   }
	 }
	 //alert(flag);
	 if(!flag){ 
		   if(shortSatId){
          	 if(shortSatIdVal === ""){
          		$("#"+shortSatId).val('');
   			    $("#"+shortSatId).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
             	$("#"+shortSatId).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error"); 
          		$("#"+shortSatId).parent().find(".help-block").empty().append("<ul class='list-unstyled'><li>Please fill out this field.</li></ul>");
          	 }else{
          		$("#"+shortSatId).val('');
   			    $("#"+shortSatId).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
             	$("#"+shortSatId).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error"); 
             	$("#"+shortSatId).parent().find(".help-block").empty().append("<ul class='list-unstyled'><li>'" + shortSatIdVal + "' has already been used in the past.</li></ul>");
          	 }
		   }
		   callback(false); 
	 }else{
		//alert("else..");
// 		 for(var i=0;i<arrayLength ; i++){
// 			 var activeStatisticsBean =  new Object();
			 
// 		 }
		 //do ajax call and check the db validation
		 var data = JSON.stringify(jsonDatas);
		 $.ajax({
			      url: "/fdahpStudyDesigner/adminStudies/validateActiveTaskStatShortTitleIds.do?_S=${param._S}",
			      type: "POST",
			      datatype: "json",
		          data: {activeStatisticsBean:data},
		          beforeSend: function(xhr, settings){
		              xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
		          },
		          success: function emailValid(data, status) {
		          var jsonobject = eval(data);
		          var message = jsonobject.message;
		          var staticInfoList = jsonobject.statisticsInfoList;
		          if('SUCCESS' == message){
		        	  if (typeof staticInfoList != 'undefined' && staticInfoList != null && staticInfoList.length >0){
		        		  $.each(staticInfoList, function(i, obj) {
		        				 if(obj.type){
		        						 $("#"+obj.id).val('');
		        						 $("#"+obj.id).focus();
		        						 $("#"+obj.id).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
		        			          	 $("#"+obj.id).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error");
		        			          	 $("#"+obj.id).parent().find(".help-block").empty().append("<ul class='list-unstyled'><li>'" + obj.idVal + "' has already been used in the past.</li></ul>"); 
		        				 }
		        		 });
		        		  
		        	  }
		        	   callback(false);
		             }else{
		        	   callback(true);
		             }
		           },
		           error:function status(data, status) {
		            	   callback(false);
		           },
		           global : false
		     });
	 }
}
function saveValidateStatisticsIds(jsonDatas, callback){
	var flag = true;
	var arrayLength = jsonDatas.length; //cache the array length
	var shortSatId = '';
	var shortSatIdVal = '';
	//alert("inside stat");
	 if (arrayLength > 1) { 
		for(var i=0;i<arrayLength ; i++){
			   var existId = jsonDatas[i].id; 
			   var existVal = jsonDatas[i].idVal;  
			   if(existVal){
				   for(var j = 0; j<arrayLength ; j++) {
					   var statId = jsonDatas[j].id;
					   var statVal = jsonDatas[j].idVal;
				       if (existId!=statId && existVal.toLowerCase() == statVal.toLowerCase()) {
				    	   flag = false;
				    	   shortSatId = jsonDatas[j].id;
				    	   shortSatIdVal = jsonDatas[j].idVal;
				    	   break;
				       }
				   }   
			   }
	   }
	 }
	 if(!flag){ 
		   if(shortSatId){
          	 if(shortSatIdVal === ""){
          		$("#"+shortSatId).val('');
   			    $("#"+shortSatId).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
             	$("#"+shortSatId).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error"); 
          		$("#"+shortSatId).parent().find(".help-block").empty().append("<ul class='list-unstyled'><li>Please fill out this field.</li></ul>");
          	 }else{
          		$("#"+shortSatId).val('');
   			    $("#"+shortSatId).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
             	$("#"+shortSatId).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error"); 
             	$("#"+shortSatId).parent().find(".help-block").empty().append("<ul class='list-unstyled'><li>'" + shortSatIdVal + "' has already been used in the past.</li></ul>");
          	 }
		   }
		   callback(false); 
	 }else{
		 var jsonArray  = new Array();
		 for(var j = 0; j<arrayLength ; j++) {
			   var statVal = jsonDatas[j].idVal;
		       if(statVal) {
		    	   var statObj = new Object();
		    	   statObj.id = jsonDatas[j].id;
		    	   statObj.dbVal = jsonDatas[j].dbVal;
		    	   statObj.idVal = jsonDatas[j].idVal;
		    	   if(jsonDatas[j].hasOwnProperty("idname"))
		    	     statObj.idname = jsonDatas[j].idname; 
		    	   jsonArray.push(statObj);
		       }
		 } 
		 //do ajax call and check the db validation
		 var jsonArrayLength = jsonArray.length;
		 if(jsonArrayLength >0){
			 var data = JSON.stringify(jsonArray);
			 $.ajax({
				      url: "/fdahpStudyDesigner/adminStudies/validateActiveTaskStatShortTitleIds.do?_S=${param._S}",
				      type: "POST",
				      datatype: "json",
			          data: {activeStatisticsBean:data},
			          beforeSend: function(xhr, settings){
			              xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
			          },
			          success: function emailValid(data, status) {
			          var jsonobject = eval(data);
			          var message = jsonobject.message;
			          var staticInfoList = jsonobject.statisticsInfoList;
			          if('SUCCESS' == message){
			        	  if (typeof staticInfoList != 'undefined' && staticInfoList != null && staticInfoList.length >0){
			        		  $.each(staticInfoList, function(i, obj) {
			        				 if(obj.type){
			        						 $("#"+obj.id).val('');
			        						 $("#"+obj.id).focus();
			        						 $("#"+obj.id).parent().find('.statShortTitleClass').parent().find(".help-block").empty();
			        			          	 $("#"+obj.id).parent().find('.statShortTitleClass').addClass("has-danger").addClass("has-error");
			        			          	 $("#"+obj.id).parent().find(".help-block").empty().append("<ul class='list-unstyled'><li>'" + obj.idVal + "' has already been used in the past.</li></ul>"); 
			        				 }
			        		 });
			        		  
			        	  }
			        	   callback(false);
			             }else{
			        	   callback(true);
			             }
			           },
			           error:function status(data, status) {
			            	   callback(false);
			           },
			           global : false
			     });
		 }else{
			 callback(true);
		 }
	 }
}
function setLineChatStatCheckedVal(){
	   if($('#Score_spatial_chart_id').is(":checked")){
			$('.addLineChartBlock_Score_spatial').css("display","");
			$('.addLineChartBlock_Score_spatial').find('.requireClass').attr('required', true);
			$('#Score_spatial_chart_id').val(true);
			$('.selectpicker').selectpicker('refresh');
	   }else{
		   $('.addLineChartBlock_Score_spatial').css("display","none");
  	   	   $('.addLineChartBlock_Score_spatial').find('.requireClass').attr('required', false);
  	   	   $('#Score_spatial_chart_id').val(false);
	   }
	   if($('#Score_spatial_stat_id').is(":checked")){
		    $('.addLineStaticBlock_Score_spatial').css("display","");
		    $('.addLineStaticBlock_Score_spatial').find('.requireClass').attr('required', true);
			$('#Score_spatial_stat_id').val(true);
			$('.addLineStaticBlock_Score_spatial').find('.shortTitleStatCls').attr('exist','Y');
			$('.selectpicker').selectpicker('refresh');
	   }else{
		   $('.addLineStaticBlock_Score_spatial').css("display","none");
  	   	   $('.addLineStaticBlock_Score_spatial').find('.requireClass').attr('required', false);
  	       $('.addLineStaticBlock_Score_spatial').find('.shortTitleStatCls').attr('exist','N');
  	   	   $('#Score_spatial_stat_id').val(false);
        }
	   
	   if($('#Number_of_Games_spatial_chart_id').is(":checked")){
		    $('.addLineChartBlock_Number_of_Games_spatial').css("display","");
			$('.addLineChartBlock_Number_of_Games_spatial').find('.requireClass').attr('required', true);
			$('#Number_of_Games_spatial_chart_id').val(true);
			$('.selectpicker').selectpicker('refresh');
	   }else{
		     $('.addLineChartBlock_Number_of_Games_spatial').css("display","none");
		   	 $('.addLineChartBlock_Number_of_Games_spatial').find('.requireClass').attr('required', false);
		   	 $('#Number_of_Games_spatial_chart_id').val(false);
	   }
	   if($('#Number_of_Games_spatial_stat_id').is(":checked")){
		    $('.addLineStaticBlock_Number_of_Games_spatial').css("display","");
			$('.addLineStaticBlock_Number_of_Games_spatial').find('.requireClass').attr('required', true);
			$('#Number_of_Games_spatial_stat_id').val(true);
			$('.addLineStaticBlock_Number_of_Games_spatial').find('.shortTitleStatCls').attr('exist','Y');
			$('.selectpicker').selectpicker('refresh');
	   }else{
		     $('.addLineStaticBlock_Number_of_Games_spatial').css("display","none");
		   	 $('.addLineStaticBlock_Number_of_Games_spatial').find('.requireClass').attr('required', false);
		   	 $('.addLineStaticBlock_Number_of_Games_spatial').find('.shortTitleStatCls').attr('exist','N');
		   	 $('#Number_of_Games_spatial_stat_id').val(false);
       }
	   
	   if($('#Number_of_Failures_spatial_chart_id').is(":checked")){
		    $('.addLineChartBlock_Number_of_Failures_spatial').css("display","");
			$('.addLineChartBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', true);
			$('#Number_of_Failures_spatial_chart_id').val(true);
			$('.selectpicker').selectpicker('refresh');
	   }else{
		     $('.addLineChartBlock_Number_of_Failures_spatial').css("display","none");
		   	 $('.addLineChartBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', false);
		   	 $('#Number_of_Failures_spatial_chart_id').val(false);
	   }
	   if($('#Number_of_Failures_spatial_stat_id').is(":checked")){
		    $('.addLineStaticBlock_Number_of_Failures_spatial').css("display","");
			$('.addLineStaticBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', true);
			$('#Number_of_Failures_spatial_stat_id').val(true);
			$('.addLineStaticBlock_Number_of_Failures_spatial').find('.shortTitleStatCls').attr('exist','Y');
			$('.selectpicker').selectpicker('refresh');
	   }else{
		     $('.addLineStaticBlock_Number_of_Failures_spatial').css("display","none");
		   	 $('.addLineStaticBlock_Number_of_Failures_spatial').find('.requireClass').attr('required', false);
		   	$('.addLineStaticBlock_Number_of_Failures_spatial').find('.shortTitleStatCls').attr('exist','N');
		   	 $('#Number_of_Failures_spatial_stat_id').val(false);
    }   
}
var updateLogoutCsrf = function() {
	$('#logoutCsrf').val('${_csrf.token}');
	$('#logoutCsrf').prop('name', '${_csrf.parameterName}');
}
function isNumber(evt) {
	evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)){
    	 return false;
    }
    return true;
}
function isNumberFloat(e) {
	var keyCode = (e.which) ? e.which : e.keyCode;
    if ((keyCode >= 48 && keyCode <= 57) || (keyCode == 8))
        return true;
    else if (keyCode == 46) {
        var curVal = document.activeElement.value;
        if (curVal != null && curVal.trim().indexOf('.') == -1)
            return true;
        else
            return false;
    }
    else
        return false;
}
//# sourceURL=filename3.js
</script>