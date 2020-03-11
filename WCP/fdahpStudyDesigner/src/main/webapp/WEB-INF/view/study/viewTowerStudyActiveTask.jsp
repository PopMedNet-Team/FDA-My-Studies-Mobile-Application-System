<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
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
		<div class="mt-lg blue-md-f text-uppercase">Configurable
			parameters</div>
		<div class="gray-xs-f mt-md mb-sm">
			Instructions <small>(150 characters max)</small><span
				class="requiredStar"> *</span>
		</div>
		<div class="form-group">
			<textarea class="form-control" rows="5" id="comment"
				name="instruction" maxlength="150" required>${activeTaskBo.instruction}</textarea>
			<div class="help-block with-errors red-txt"></div>
		</div>
		<c:if test="${fn:length(activeTaskBo.taskAttributeValueBos) eq 0}">
			<c:forEach items="${activeTaskBo.taskMasterAttributeBos}"
				var="taskMasterAttributeBo">
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 1}">
					<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}<span
							class="requiredStar"> *</span>
					</div>
					<div class="col-md-3 col-lg-3 p-none">
					  <input type="hidden"
							name="taskAttributeValueBos[0].attributeValueId" value="">
						<input type="hidden"
							name="taskAttributeValueBos[0].activeTaskMasterAttrId"
							value="${taskMasterAttributeBo.masterId}"> <input
							type="hidden" name="taskAttributeValueBos[0].addToDashboard"
							value="${taskMasterAttributeBo.addToDashboard}">
	                             <div class="form-group">
	                                  <select class="selectpicker aq-select aq-select-form elaborateClass  requireClass" required name="taskAttributeValueBos[0].attributeVal">
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3" selected>3</option>
										<option value="4">4</option>
										<option value="5">5</option>
										<option value="6">6</option>
										<option value="7">7</option>
										<option value="8">8</option>
                                      </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
	                </div>
					<div class="clearfix"></div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 2}">
					<div class="blue-md-f text-uppercase">Results captured from
						the task</div>
					<div class="pt-xs">
						<div class="bullets bor-b-2-gray black-md-f pt-md pb-md">${taskMasterAttributeBo.displayName}</div>
				</c:if>
				<c:if test="${taskMasterAttributeBo.orderByTaskType eq 3}">
					<input type="hidden"
						name="taskAttributeValueBos[1].attributeValueId" value="">
					<input type="hidden"
						name="taskAttributeValueBos[1].activeTaskMasterAttrId"
						value="${taskMasterAttributeBo.masterId}">
					<input type="hidden" name="taskAttributeValueBos[1].addToDashboard"
						value="${taskMasterAttributeBo.addToDashboard}">
					<div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>

					<div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
						<div class="chartSection" style="display: none">
							<div class="mb-lg">
								<span class="checkbox checkbox-inline"> <input
									type="checkbox"
									id="${taskMasterAttributeBo.attributeName}_chart_id"
									name="taskAttributeValueBos[1].addToLineChart" value="option1">
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
											id="chartId" name="taskAttributeValueBos[1].timeRangeChart"
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
											name="taskAttributeValueBos[1].rollbackChat"> <label
											for="inlineRadio1">Yes</label>
										</span> <span class="radio radio-inline"> <input
											class="rollbackRadioClass" type="radio" id="inlineRadio2"
											value="No" name="taskAttributeValueBos[1].rollbackChat">
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
												name="taskAttributeValueBos[1].titleChat" maxlength="30" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="pt-lg mt-xs pb-lg">
							<span class="checkbox checkbox-inline"> <input
								type="checkbox"
								id="${taskMasterAttributeBo.attributeName}_stat_id"
								name="taskAttributeValueBos[1].useForStatistic" value="option1">
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
									<div class="form-group">
										<input autofocus="autofocus" type="text" custAttType="cust"
											class="form-control requireClass shortTitleStatCls"
											id="static"
											name="taskAttributeValueBos[1].identifierNameStat"
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
										name="taskAttributeValueBos[1].displayNameStat" maxlength="50" />
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
											name="taskAttributeValueBos[1].displayUnitStat"
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
										title="Select" name="taskAttributeValueBos[1].uploadTypeStat">
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
									<select
										class="selectpicker aq-select aq-select-form elaborateClass requireClass"
										title="Select"
										name="taskAttributeValueBos[1].formulaAppliedStat">
										<c:forEach items="${activetaskFormulaList}"
											var="activetaskFormula">
											<option value="${activetaskFormula.activetaskFormulaId}">${activetaskFormula.value}</option>
										</c:forEach>
									</select>
									<div class="help-block with-errors red-txt"></div>
								</div>
							</div>
							<div>
								<div class="gray-xs-f mb-sm">Time ranges options available
									to the mobile app user</div>
								<div>
									<span class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Day</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Week</span></span> <span
										class="mr-lg"><span class="mr-sm"><img
											src="../images/icons/tick.png" /></span><span>Current Month</span></span>
									<span class="txt-gray">(Rollback option provided for
										these three options)</span>
								</div>
							</div>
						</div>

					</div>
				</c:if>
			</c:forEach>
		</c:if>
		<c:if test="${fn:length(activeTaskBo.taskAttributeValueBos) gt 0}">
			<c:set var="count" value="0" />
			<c:forEach items="${activeTaskBo.taskMasterAttributeBos}"
				var="taskMasterAttributeBo">
				<c:forEach items="${activeTaskBo.taskAttributeValueBos}"
					var="taskValueAttributeBo">
					<c:if
						test="${taskMasterAttributeBo.orderByTaskType eq 1 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
						<div class="gray-xs-f mt-md mb-sm">${taskMasterAttributeBo.displayName}</div>
						<div class="col-md-3 col-lg-3 p-none">
							<input type="hidden"
								name="taskAttributeValueBos[0].attributeValueId"
								value="${taskValueAttributeBo.attributeValueId}"> 
								<input type="hidden"
								name="taskAttributeValueBos[0].activeTaskMasterAttrId"
								value="${taskMasterAttributeBo.masterId}"> 
								<input type="hidden" name="taskAttributeValueBos[0].addToDashboard"
								  value="${taskMasterAttributeBo.addToDashboard}"> 
								<div class="form-group">
	                                  <select class="selectpicker aq-select aq-select-form elaborateClass  requireClass" required name="taskAttributeValueBos[0].attributeVal">
										<option value="1" ${taskValueAttributeBo.attributeVal eq '1'?'selected':''}>1</option>
										<option value="2" ${taskValueAttributeBo.attributeVal eq '2'?'selected':''}>2</option>
										<option value="3" ${taskValueAttributeBo.attributeVal eq '3'?'selected':''}>3</option>
										<option value="4" ${taskValueAttributeBo.attributeVal eq '4'?'selected':''}>4</option>
										<option value="5" ${taskValueAttributeBo.attributeVal eq '5'?'selected':''}>5</option>
										<option value="6" ${taskValueAttributeBo.attributeVal eq '6'?'selected':''}>6</option>
										<option value="7" ${taskValueAttributeBo.attributeVal eq '7'?'selected':''}>7</option>
										<option value="8" ${taskValueAttributeBo.attributeVal eq '8'?'selected':''}>8</option>
                                      </select>
	                                 <div class="help-block with-errors red-txt"></div>
	                            </div>
						</div>
						<div class="clearfix"></div>
					</c:if>
					<c:if
						test="${taskMasterAttributeBo.orderByTaskType eq 2 && count == 0}">
						<c:set var="count" value="${count+1}" />
						<div class="blue-md-f text-uppercase">Results captured from
							the task</div>
						<div class="pt-xs">
							<div class="bullets bor-b-2-gray black-md-f pt-md pb-md">${taskMasterAttributeBo.displayName}</div>
					</c:if>
					<c:if
						test="${taskMasterAttributeBo.orderByTaskType eq 3 && taskMasterAttributeBo.masterId eq taskValueAttributeBo.activeTaskMasterAttrId}">
						<input type="hidden"
							name="taskAttributeValueBos[1].attributeValueId"
							value="${taskValueAttributeBo.attributeValueId}">
						<input type="hidden"
							name="taskAttributeValueBos[1].activeTaskMasterAttrId"
							value="${taskMasterAttributeBo.masterId}">
						<input type="hidden"
							name="taskAttributeValueBos[1].addToDashboard"
							value="${taskMasterAttributeBo.addToDashboard}">
						<div class="bullets black-md-f pt-md">${taskMasterAttributeBo.displayName}</div>

						<div class="pl-xlg ml-xs bor-l-1-gray mt-lg">
							<div class="chartSection" style="display:none">
	                          <div class="mb-lg">
	                            <span class="checkbox checkbox-inline">
	                                <input type="checkbox" id="${taskMasterAttributeBo.attributeName}_chart_id" name="taskAttributeValueBos[1].addToLineChart" <c:if test="${taskValueAttributeBo.addToLineChart==true}">checked</c:if> value="${taskValueAttributeBo.addToLineChart}">
	                                <label for="${taskMasterAttributeBo.attributeName}_chart_id">Add to line chart</label>
	                            </span>  
	                          </div>   
	                           
	                          <div class="addLineChartBlock_${taskMasterAttributeBo.attributeName}" style="${taskValueAttributeBo.addToLineChart==true?'':'display:none'}">  
	                          <div class="pb-lg">
	                            <div class="gray-xs-f mt-md mb-sm">Time range for the chart<span class="requiredStar"> *</span> <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span></div>
	                              <div class="add_notify_option form-group mb-none">
		                           <select class="selectpicker aq-select aq-select-form elaborateClass frequencyIdList requireClass" id="chartId" name="taskAttributeValueBos[1].timeRangeChart" title="Select" >
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
	                                    <input class="" type="radio" id="inlineRadio1" value="Yes" name="taskAttributeValueBos[1].rollbackChat" ${taskValueAttributeBo.rollbackChat eq 'Yes'?'checked':""}>
	                                    <label for="inlineRadio1">Yes</label>
	                                </span>
	                                <span class="radio radio-inline">
	                                    <input class="rollbackRadioClass" type="radio" id="inlineRadio2" value="No" name="taskAttributeValueBos[1].rollbackChat" <c:if test="${empty taskValueAttributeBo.rollbackChat  || empty taskValueAttributeBo}">checked</c:if> ${taskValueAttributeBo.rollbackChat eq 'No'?'checked':""}>
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
	                                     <input type="text" class="form-control requireClass" id="lineChartId" name="taskAttributeValueBos[1].titleChat" maxlength="30" value="${fn:escapeXml(taskValueAttributeBo.titleChat)}"/>  
	                                     <div class="help-block with-errors red-txt"></div>
	                                </div>
	                            </div>                            
	                        </div>
	                        </div>
	                        </div>
							<div class="pt-lg mt-xs pb-lg">
								<span class="checkbox checkbox-inline"> <input
									type="checkbox"
									id="${taskMasterAttributeBo.attributeName}_stat_id"
									name="taskAttributeValueBos[1].useForStatistic"
									<c:if test="${taskValueAttributeBo.useForStatistic==true}">checked</c:if>
									value="${taskValueAttributeBo.useForStatistic}"> <label
									for="${taskMasterAttributeBo.attributeName}_stat_id">Use
										for Statistic</label>
								</span>
							</div>
							<div
								class="addLineStaticBlock_${taskMasterAttributeBo.attributeName}"
								style="${taskValueAttributeBo.useForStatistic==true?'':'display:none'}">
								<div>
									<div class="gray-xs-f mb-sm">
										Short name <small>(20 characters max)</small><span
											class="requiredStar"> *</span>
									</div>
									<div class="add_notify_option">
										<div class="form-group statShortTitleClass">
											<input type="hidden" id="dbIdentifierId"
												value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}">
											<input autofocus="autofocus" type="text"
												class="form-control requireClass shortTitleStatCls"
												custAttType="cust" id="identifierId"
												name="taskAttributeValueBos[1].identifierNameStat"
												maxlength="20"
												value="${fn:escapeXml(taskValueAttributeBo.identifierNameStat)}"
												<c:if test="${not empty taskValueAttributeBo.isIdentifierNameStatDuplicate && (taskValueAttributeBo.isIdentifierNameStatDuplicate gt 0)}"> disabled</c:if> />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>

								<div>
									<div class="gray-xs-f mb-sm">
										Display name for the Stat (e.g. Total Hours of Activity Over 6
										Months) <small> (50 characters max)</small><span
											class="requiredStar"> *</span>
									</div>
									<div class="form-group">
										<input type="text" class="form-control requireClass"
											name="taskAttributeValueBos[1].displayNameStat"
											maxlength="50"
											value="${fn:escapeXml(taskValueAttributeBo.displayNameStat)}" />
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
												name="taskAttributeValueBos[1].displayUnitStat"
												maxlength="15"
												value="${fn:escapeXml(taskValueAttributeBo.displayUnitStat)}" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>

								<div>


									<div>
										<div class="gray-xs-f mb-sm">
											Stat Type for image display<span class="requiredStar">
												*</span>
										</div>
										<div class="add_notify_option form-group">
											<select
												class="selectpicker  aq-select aq-select-form elaborateClass requireClass"
												title="Select"
												name="taskAttributeValueBos[1].uploadTypeStat">
												<c:forEach items="${statisticImageList}"
													var="statisticImage">
													<option value="${statisticImage.statisticImageId}"
														${taskValueAttributeBo.uploadTypeStat eq statisticImage.statisticImageId?'selected':''}>${statisticImage.value}</option>
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
											<select
												class="selectpicker aq-select aq-select-form elaborateClass requireClass"
												title="Select"
												name="taskAttributeValueBos[1].formulaAppliedStat">
												<c:forEach items="${activetaskFormulaList}"
													var="activetaskFormula">
													<option value="${activetaskFormula.activetaskFormulaId}"
														${taskValueAttributeBo.formulaAppliedStat eq activetaskFormula.activetaskFormulaId?'selected':""}>${activetaskFormula.value}</option>
												</c:forEach>
											</select>
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
									<div>
										<div class="gray-xs-f mb-sm">Time ranges options
											available to the mobile app user</div>
										<div>
											<span class="mr-lg"><span class="mr-sm"><img
													src="../images/icons/tick.png" /></span><span>Current Day</span></span> <span
												class="mr-lg"><span class="mr-sm"><img
													src="../images/icons/tick.png" /></span><span>Current Week</span></span>
											<span class="mr-lg"><span class="mr-sm"><img
													src="../images/icons/tick.png" /></span><span>Current Month</span></span>
											<span class="txt-gray">(Rollback option provided for
												these three options)</span>
										</div>
									</div>
								</div>

							</div>
						</div> 
					</c:if> 
				</c:forEach>
			</c:forEach>
		</c:if>
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
	setLineChatStatCheckedVal();
	$('#number_of_moves_tower_chart_id').on('click',function(){
 	   if($(this).is(":checked")){
 			$('.addLineChartBlock_number_of_moves_tower').css("display","");
 			$('.addLineChartBlock_number_of_moves_tower').find('.requireClass').attr('required', true);
 			$('#number_of_moves_tower_chart_id').val(true);
 			$('.selectpicker').selectpicker('refresh');
 	   }else{
 	   	 $('.addLineChartBlock_number_of_moves_tower').css("display","none");
 	   	 $('.addLineChartBlock_number_of_moves_tower').find('.requireClass').attr('required', false);
 	   	 $('#number_of_moves_tower_chart_id').val(false);
 	   }
 	   resetValidation($(this).parents('form'));
   });
   $('#number_of_moves_tower_stat_id').on('click',function(){
 	   if($(this).is(":checked")){
 			$('.addLineStaticBlock_number_of_moves_tower').css("display","");
 			$('.addLineStaticBlock_number_of_moves_tower').find('.requireClass').attr('required', true);
 			$('#number_of_moves_tower_stat_id').val(true);
 			$('.selectpicker').selectpicker('refresh');
 	   }else{
 	   	 $('.addLineStaticBlock_number_of_moves_tower').css("display","none");
 	   	$('.addLineStaticBlock_number_of_moves_tower').find('.requireClass').attr('required', false);
 	   	$('#number_of_moves_tower_stat_id').val(false);
 	   }
	});
    $("#shortTitleId").blur(function(){
   	validateShortTitleId('',function(val){});
   })
   $('#static').blur(function(){
   	validateShortTitleStatId('', this, function(val){});
   })
    $('#identifierId').blur(function(){
   	validateShortTitleStatId('', this, function(val){});
   })
   
    $('#shortTitleId').on('keyup',function(){
   	 $(this).parent().find(".help-block").empty();
   	 $('.shortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
    });
    $('#static').on('keyup',function(){
   	 $(this).parent().find(".help-block").empty();
   	 $('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
    });
    $('#identifierId').on('keyup',function(){
   	 $(this).parent().find(".help-block").empty();	
   	 $('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
    });
    $(document).on('click', '#doneId', function(e){
   	 console.log("done method");
   	 $("body").addClass('loading');
   	 $("#doneId").attr("disabled",true);
         if($('#pickStartDate').val() == ''){
		    $('#pickStartDate').attr("readonly",false);	
		  }
		  if($('#startWeeklyDate').val() == ''){
			$('#startWeeklyDate').attr("readonly",false);	
		  }
		  var shortFlag = true;
		  var statFlag = true;
		  if(isFromValid("#activeContentFormId")){
			  $('.scheduleTaskClass').removeAttr('disabled');
			      $('.scheduleTaskClass').removeClass('linkDis');
			      //alert("statFlag"+ statFlag);
			      var shortTitle = $('#shortTitleId').val();
			      var shortTitleCount = $('.shortTitleClass').find('.help-block').children().length;
				  if(shortTitle){
						  validateShortTitleId('', function(st){
							  if(st){
 									if($('#number_of_moves_tower_stat_id').is(":checked")){
 									  var statShort = '';
 	  					        	  var statShortVal = '';
 	  					        	  var staticShortStat = $('#static').val();
 	  					        	  var dynaminShortStat = $('#identifierId').val();
 	  					        	  if(staticShortStat){
 	  					        		  statShort = '#static';
 	  					        		  statShortVal = staticShortStat;
 	  					        	  }
 	  					        	  if(dynaminShortStat){
 	  					        		  statShort = '#identifierId';
 	  					        		  statShortVal = dynaminShortStat;
 	  					        	  }
 	  								  if(statShort && statShortVal){
 	  										  validateShortTitleStatId('', statShort , function(st){
 	  											  if(st){
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
 	  												$("#doneId").attr("disabled",false);
 	  											    $("body").removeClass('loading');  
 	  											  }
 	  										  }); 
 	  									  }else{
 	  										  var statId = $('.shortTitleStatCls').attr('id');
 	  						      			  if(statId && statId == 'identifierId'){
 	  						      				$("#identifierId").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
 	  						      				//$('#identifierId').focus();
 	  						      			  }else{
 	  											  $("#static").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>'); 
 	  											  //$('#static').focus();
 	  						      			  }
 	  						      			  $("#doneId").attr("disabled",false);
 											  $("body").removeClass('loading');
 	  									  }
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
					  }
			}else{
				console.log("else of Done");
				$("body").removeClass('loading');
				$("#doneId").attr("disabled",false);
				showErrMsg("Please fill in all mandatory fields.");
				$('.contentClass a').tab('show');
			}
    });
    $('#saveId').click(function(e) {
   	 $("body").addClass('loading');
   	 var shortTitleCount = $('.shortTitleClass').find('.help-block').children().length;
   	 if(shortTitleCount >=1){
   		 showErrMsg("Please fill in all mandatory fields.");
   		 $('.contentClass a').tab('show');
   		 $("body").removeClass('loading');
            return false;
   	 }else if(!$('#shortTitleId')[0].checkValidity()){
        	 $("#shortTitleId").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
        	 showErrMsg("Please fill in all mandatory fields.");
        	 $('.contentClass a').tab('show');
            $("body").removeClass('loading');
            return false;
     } else {
   	 validateShortTitleId('', function(st){
    		if(st){
	      			  var statShortTitleCount = $('.statShortTitleClass').find('.help-block').children().length;
	      			  var errorstatShortTitle = $('.statShortTitleClass').find('.help-block').children().text();
	      			  if(statShortTitleCount >= 1 && errorstatShortTitle != "Please fill out this field."){
	      				  var statId = $('.shortTitleStatCls').attr('id');
	      				  if(statId && statId == 'identifierId')
	      					  $('#identifierId').focus();
	      				  else
	      					$('#static').focus();
	      				  
	      				  showErrMsg("Please fill in all mandatory fields.");
	      				  $('.contentClass a').tab('show');
	      				  $("body").removeClass('loading');
	      				  return false;
	      			  }else{
	      				var statShort = '';
  	              	    var staticShortStat = $('#static').val();
  	              	    var dynaminShortStat = $('#identifierId').val();
  	              	    if(staticShortStat)
  	              		  statShort = '#static';
  	              	    if(dynaminShortStat)
  	              		  statShort = '#identifierId';
      	              	if(statShort){
    	      		    	validateShortTitleStatId('', statShort , function(st){
    	    					  if(st){
    	         					doneActiveTask(this, 'save', function(val) {
    	         							if(val) {
    	         								$('.shortTitleCls,.shortTitleStatCls').prop('disabled', false);
    	         								$('#activeContentFormId').validator('destroy');
    	         	                     	    $("#buttonText").val('save');
    	         	                     	    document.activeContentFormId.submit();
    	         							}
    	         					 }); 
    	    					  }else{
    	    						 $("body").removeClass('loading');
    	    					  }
    	    				  });  
    	      		      }else{
    	      		    	$("body").removeClass('loading');  
    	      		    	$('#inputClockId').parent().find(".help-block").empty();
        					var dt = new Date();
        					$('#inputClockId').datetimepicker({format: 'HH:mm',
        					minDate : new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 00, 00),
        					maxDate : new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 23, 59)});
        					doneActiveTask(this, 'save', function(val) {
        							if(val) {
        								$('.shortTitleCls,.shortTitleStatCls').prop('disabled', false);
        								$('#activeContentFormId').validator('destroy');
        	                     	    $("#buttonText").val('save');
        	                     	    document.activeContentFormId.submit();
        							}else{
        								$("body").removeClass('loading');
        							}
        					 }); 
    	      		      }
    	      	   }
    		}else{
    			$("body").removeClass('loading');
    		}
    	   });
       }
	});
   $('.selectpicker').selectpicker('refresh');
   $('[data-toggle="tooltip"]').tooltip();
   $('input').on('drop', function() {
	    return false;
	});
   $(document).find('input[type = text][custAttType != cust]').keyup(function(e) {
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
function setLineChatStatCheckedVal(){
	   if($('#number_of_moves_tower_chart_id').is(":checked")){
			$('.addLineChartBlock_number_of_moves_tower').css("display","");
			$('.addLineChartBlock_number_of_moves_tower').find('.requireClass').attr('required', true);
			$('#number_of_moves_tower_chart_id').val(true);
			$('.selectpicker').selectpicker('refresh');
	   }else{
	   	 $('.addLineChartBlock_number_of_moves_tower').css("display","none");
	   	 $('.addLineChartBlock_number_of_moves_tower').find('.requireClass').attr('required', false);
	   	 $('#number_of_moves_tower_chart_id').val(false);
	   }
	   if($('#number_of_moves_tower_stat_id').is(":checked")){
			$('.addLineStaticBlock_number_of_moves_tower').css("display","");
			$('.addLineStaticBlock_number_of_moves_tower').find('.requireClass').attr('required', true);
			$('#number_of_moves_tower_stat_id').val(true);
			$('.selectpicker').selectpicker('refresh');
	   }else{
	   	 $('.addLineStaticBlock_number_of_moves_tower').css("display","none");
	   	$('.addLineStaticBlock_number_of_moves_tower').find('.requireClass').attr('required', false);
	   	$('#number_of_moves_tower_stat_id').val(false);
     }
}
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
	   //alert("validate");
	   var activeTaskAttName = 'identifierNameStat';
	   var activeTaskAttIdVal = $(thisAttr).val();
	   var activeTaskAttIdName = $(thisAttr).attr('id');
	  if(activeTaskAttIdVal && activeTaskAttIdName){
		$('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
	     $('.statShortTitleClass').parent().find(".help-block").empty();
	     $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
	     $(thisAttr).parent().find(".help-block").empty();
	     if(activeTaskAttIdName != 'static'){
	    	 activeTaskAttIdName = 'static';
	    	 var dbIdentifierVal = $('#dbIdentifierId').val();
	    	 //alert("dbIdentifierVal"+dbIdentifierVal);
	    	 if(dbIdentifierVal!=activeTaskAttIdVal){
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
		                	    // $(thisAttr).validator('validate');
		                	    // $('.statShortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
		                	    // $('.statShortTitleClass').parent().find(".help-block").empty();
		                	     $("#identifierId").validator('validate');
		                         $("#identifierId").parent().removeClass("has-danger").removeClass("has-error");
		                         $("#identifierId").parent().find(".help-block").empty();
		                	     shortTitleStatFlag = true;
		                	     //$("#doneId").attr("disabled",false);
		                	     callback(true);
		                     }else{
		                    	 $(thisAttr).val('');
		                    	 $('#identifierId').parent().addClass("has-danger").addClass("has-error");
		                    	 $('#identifierId').parent().find(".help-block").empty();
		                    	 $('#identifierId').parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + activeTaskAttIdVal + "' has already been used in the past.</li></ul>");
		                    	 $('#identifierId').focus();
		                    	 showErrMsg("Please fill in all mandatory fields.");
		      					 $('.contentClass a').tab('show');
		                    	 shortTitleStatFlag = false;
		                    	 //$("#doneId").attr("disabled",true);
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
		 			$('.shortTitleClass').parent().removeClass("has-danger").removeClass("has-error");
		 	        $('.shortTitleClass').parent().find(".help-block").html("");
		 	}
	     }else{
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
	                    	 $('.statShortTitleClass').parent().addClass("has-danger").addClass("has-error");
	                    	 $('.statShortTitleClass').parent().find(".help-block").empty();
	                    	 $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + activeTaskAttIdVal + "' has already been used in the past.</li></ul>");
	                         if (callback)
	     						callback(false);
	                         
	                     }
	               },
	               error:function status(data, status) {
	            	   callback(false);
	               },
	               global : false
	           });
	     }
	     } else {
	    	 if (callback)
	 			callback(true);
	     }
}
</script>