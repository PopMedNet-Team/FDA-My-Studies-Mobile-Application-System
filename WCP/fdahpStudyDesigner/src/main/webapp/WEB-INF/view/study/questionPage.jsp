<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<style>
.tool-tip {
  display: inline-block;
}
.tool-tip [disabled] {
  pointer-events: none;
}
.tooltip {
  width: 200px;
}
</style>
<script type="text/javascript">
function isNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
    		return false;
    }
    return true;
}
function isOnlyNumber(evt) {
    evt = (evt) ? evt : window.event;
    var charCode = (evt.which) ? evt.which : evt.keyCode;
    if (charCode > 31 && (charCode < 48 || charCode > 57)) {
       if(charCode != 45){
        	return false;
        }
        
    }
    return true;
}
function isNumberKey(evt)
{
   var charCode = (evt.which) ? evt.which : evt.keyCode;
   if (charCode != 46 && charCode > 31 
     && (charCode < 48 || charCode > 57))
	   if(charCode != 45){
       	return false;
       }

   return true;
}
</script>
<!-- Start right Content here -->
<div id="questionPage" class="col-sm-10 col-rc white-bg p-none">
   <!--  Start top tab section-->
   <div class="right-content-head">
      <div class="text-right">
         <div class="black-md-f dis-line pull-left line34">
            <span class="mr-sm cur-pointer" onclick="goToBackPage(this);"><img src="../images/icons/back-b.png"/></span>
            <c:if test="${actionTypeForFormStep == 'edit'}">Edit Question</c:if>
         	<c:if test="${actionTypeForFormStep == 'view'}">View Question <c:set var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</c:if>
         	<c:if test="${actionTypeForFormStep == 'add'}">Add Question</c:if>
         </div>
         <div class="dis-line form-group mb-none mr-sm">
            <button type="button" class="btn btn-default gray-btn" onclick="goToBackPage(this);">Cancel</button>
         </div>
         <c:if test="${actionTypeForFormStep ne 'view'}">
	         <div class="dis-line form-group mb-none mr-sm">
	            <button type="button" class="btn btn-default gray-btn" id="saveId" >Save</button>
	         </div>
	         <div class="dis-line form-group mb-none">
	            <button type="button" class="btn btn-primary blue-btn" id="doneId">Done</button>
	         </div>
         </c:if>
      </div>
   </div>
   <!--  End  top tab section-->
   <!--  Start body tab section -->
   <form:form action="/fdahpStudyDesigner/adminStudies/saveOrUpdateFromQuestion.do?_S=${param._S}&${_csrf.parameterName}=${_csrf.token}" name="questionStepId" id="questionStepId" method="post" data-toggle="validator" role="form" enctype="multipart/form-data" >
   <div class="right-content-body pt-none pl-none pr-none">
      <ul class="nav nav-tabs review-tabs gray-bg">
         <li class="questionLevel active"><a data-toggle="tab" href="#qla">Question-level Attributes</a></li>
         <li class="responseLevel"><a data-toggle="tab" href="#rla">Response-level Attributes</a></li>
      </ul>
      <div class="tab-content pl-xlg pr-xlg">
         <!-- Step-level Attributes--> 
         <input type="hidden" name="stepType" id="stepType" value="Question">
         <input type="hidden" id="type" name="type" value="complete" />
         <input type="hidden" name="id" id="questionId" value="${questionsBo.id}">
         <input type="hidden" id="fromId" name="fromId" value="${formId}" />
         <input type="hidden" name="questionnairesId" id="questionnairesId" value="${questionnaireBo.id}">
         <input type="hidden" id="questionnaireShortId" value="${questionnaireBo.shortTitle}">
         <input type="hidden" id="anchorDateId" name="anchorDateId" value="${questionnairesStepsBo.questionsBo.anchorDateId}" />
         <input type="hidden" id="isShorTitleDuplicate" name="isShorTitleDuplicate" value="${questionsBo.isShorTitleDuplicate}" />
         <!---  Form-level Attributes ---> 
         <div id="qla" class="tab-pane fade active in mt-xlg">
            <div class="col-md-6 pl-none">
                  <div class="gray-xs-f mb-xs">Question Short Title or Key  (1 to 15 characters) <span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="This must be a human-readable question identifier and unique across all steps of the questionnaire and across all questions belonging to various form steps.In other words, no two questions should have the same short title - whether it belongs to a question step or a form step.Note that this field cannot be edited once the study is Launched."></span></div>
                  <div class="form-group mb-none">
                     <input type="text" custAttType="cust" class="form-control" name="shortTitle" id="shortTitle" value="${fn:escapeXml(
                     questionsBo.shortTitle)}" required maxlength="15" <c:if test="${not empty questionsBo.isShorTitleDuplicate && (questionsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>/>
                     <div class="help-block with-errors red-txt"></div>
                     <input  type="hidden"  id="preShortTitleId" value="${fn:escapeXml(
                     questionsBo.shortTitle)}"/>
                  </div>
            </div>
            <div class="col-md-10 p-none mt-md">
               <div class="gray-xs-f mb-xs">Text of the question (1 to 300 characters)<span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The question you wish to ask the participant."></span></div>
               <div class="form-group">
                  <input type="text" class="form-control" name="question" id="questionTextId" placeholder="Type the question you wish to ask the participant" value="${fn:escapeXml(
                  questionsBo.question)}" required maxlength="300"/>
                  <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div>
               <div class="gray-xs-f mb-xs">Is this a Skippable Question?</div>
               <div>
                  <span class="radio radio-info radio-inline p-45">
                     <input type="radio" id="skiappableYes" value="Yes" name="skippable"  ${empty questionsBo.skippable  || questionsBo.skippable =='Yes' ? 'checked':''}>
                     <label for="skiappableYes">Yes</label>
                  </span>
                  <span class="radio radio-inline">
                     <input type="radio" id="skiappableNo" value="No" name="skippable" ${questionsBo.skippable=='No' ?'checked':''}>
                     <label for="skiappableNo">No</label>
                  </span>
             </div>
            </div>
            <div class="mt-lg">
               <div class="gray-xs-f">Response Type <span class="requiredStar">*</span></div>
               <div class="gray-xs-f mb-xs"><small>The type of interface needed to capture the response. Note that this is not editable after Study Launch.</small></div>
               <div class="clearfix"></div>
               <div class="col-md-4 col-lg-3 p-none">
                  <div class="form-group">
                     <select id="responseTypeId" class="selectpicker" name="responseType" required value="${questionsBo.responseType}" <c:if test="${not empty questionsBo.isShorTitleDuplicate && (questionsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>>
                      <option value=''>Select</option>
                      <c:forEach items="${questionResponseTypeMasterInfoList}" var="questionResponseTypeMasterInfo">
                      	<option value="${questionResponseTypeMasterInfo.id}" ${questionsBo.responseType eq questionResponseTypeMasterInfo.id ? 'selected' : ''}>${questionResponseTypeMasterInfo.responseType}</option>
                      </c:forEach>
                     </select>
                     <div class="help-block with-errors red-txt"></div>
                  </div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="row mt-none">
               <div class="col-md-6 pl-none">
                  <div class="gray-xs-f mb-xs">Description of response type </div>
                  <div id="responseTypeDescrption">
                     - NA -
                  </div>
               </div>
               <div class="col-md-6">
                  <div class="gray-xs-f mb-xs">Data Type</div>
                  <div id="responseTypeDataType">- NA - </div>
               </div>
            </div>
            <%-- <div class="mt-lg mb-lg" id="useAnchorDateContainerId" style="display: none">
               <c:choose>
               	<c:when test="${questionsBo.useAnchorDate}">
               		<span class="checkbox checkbox-inline">
		               <input type="checkbox" id="useAnchorDateId" name="useAnchorDate" value="true" ${questionsBo.useAnchorDate ? 'checked':''} <c:if test="${questionnairesStepsBo.repeatable eq'Yes'}">disabled</c:if>>
		               <label for="useAnchorDateId"> Use Anchor Date </label>
		            </span>
               	</c:when>
               	<c:otherwise>
               		<span class="tool-tip" data-toggle="tooltip" data-html="true" data-placement="top" <c:if test="${questionnaireBo.frequency ne 'One time' || isAnchorDate}"> title="This field is disabled for one of the following reasons:<br/>1. Your questionnaire is scheduled for a frequency other than 'one-time'<br/>2. There is already another question in the study that has been marked for anchor date<br/>Please make changes accordingly and try again." </c:if> >
		               <span class="checkbox checkbox-inline">
		               <input type="checkbox" id="useAnchorDateId" name="useAnchorDate" value="true" ${questionsBo.useAnchorDate ? 'checked':''} <c:if test="${questionnairesStepsBo.repeatable eq'Yes' || questionnaireBo.frequency ne 'One time' || isAnchorDate}"> disabled </c:if> >
		               <label for="useAnchorDateId"> Use Anchor Date </label>
		               </span>
	               </span>
               	</c:otherwise>
               </c:choose>
            </div> --%>
            <div class="mt-lg mb-lg" id="useAnchorDateContainerId" style="display: none">
            <c:choose>
            	<c:when test="${questionsBo.useAnchorDate}">
            	    <span class="tool-tip" data-toggle="tooltip" data-html="true" data-placement="top"  title="The date supplied by a participant in response to this question can be used to dictate the schedule for other questionnaires or active tasks in the study, or to determine the Period of Visibility of study resources."  >
            		<span class="checkbox checkbox-inline">
			               <input type="checkbox" id="useAnchorDateId" name="useAnchorDate" value="true" ${questionsBo.useAnchorDate ? 'checked':''} <c:if test="${questionnairesStepsBo.repeatable eq'Yes'}">disabled</c:if> <c:if test="${not empty questionsBo.isShorTitleDuplicate && (questionsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>>
			               <label for="useAnchorDateId"> Use response as Anchor Date </label>
		             </span>
		             </span>
		             <div class="clearfix"></div>
	            	<div class="col-md-6 p-none useAnchorDateName mt-md" style="display: none">
		                <div class="gray-xs-f mb-xs">Define name for Anchor date<span class="requiredStar">*</span></div>
		                <div class="form-group">
		                  <input type="text" class="form-control" name="anchorDateName" id="anchorTextId" value="${questionsBo.anchorDateName}" maxlength="50" <c:if test="${not empty questionsBo.isShorTitleDuplicate && (questionsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>/>
		                  <div class="help-block with-errors red-txt"></div>
		                </div>
                   </div>
            	</c:when>
            	<c:otherwise>
	               <span class="tool-tip" data-toggle="tooltip" data-html="true" data-placement="top"  
	               <c:if test="${questionnaireBo.scheduleType eq 'AnchorDate'}"> title= "This option has been disabled, since this questionnaire has anchor-date based scheduling already."</c:if>
	               <c:if test="${questionnaireBo.frequency ne 'One time' || questionnaireBo.scheduleType eq 'Regular'}"> title= "The date supplied by a participant in response to this question can be used to dictate the schedule for other questionnaires or active tasks in the study, or to determine the Period of Visibility of study resources."</c:if>
	                >
		               <span class="checkbox checkbox-inline">
			               <input type="checkbox" id="useAnchorDateId" name="useAnchorDate" value="true" ${questionsBo.useAnchorDate ? 'checked':''} <c:if test="${questionnairesStepsBo.repeatable eq'Yes'}"> disabled </c:if> 
			                     <c:if test="${questionnaireBo.frequency ne 'One time' || questionnaireBo.scheduleType ne 'Regular'}"> disabled </c:if> <c:if test="${not empty questionsBo.isShorTitleDuplicate && (questionsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>>
			               <label for="useAnchorDateId"> Use response as Anchor Date </label>
		               </span>
	               </span>
	               <div class="clearfix"></div>
	            	<div class="col-md-6 p-none useAnchorDateName mt-md" style="display: none">
		                <div class="gray-xs-f mb-xs">Define name for Anchor date<span class="requiredStar">*</span></div>
		                <div class="form-group">
		                  <input type="text" class="form-control" name="anchorDateName" id="anchorTextId" value="${fn:escapeXml(questionsBo.anchorDateName)}" maxlength="50" <c:if test="${not empty questionsBo.isShorTitleDuplicate && (questionsBo.isShorTitleDuplicate gt 0)}"> disabled</c:if>/>
		                  <div class="help-block with-errors red-txt"></div>
		                </div>
                   </div>
            	</c:otherwise>
            </c:choose>
            </div>
            <c:if test="${fn:contains(studyBo.platform, 'I')}">
            	<div class="clearfix"></div>
            	<div class="mt-lg" id="allowHealthKitId" style="display: none">
	               <span class="checkbox checkbox-inline">
	               <input type="checkbox" id="allowHealthKit" name="allowHealthKit" value="Yes" ${questionsBo.allowHealthKit eq 'Yes' ? 'checked':''}>
	               <label for="allowHealthKit"> Allow participant to optionally use HealthKit to provide answer  <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="If you check this box, participants who are using the app on an iOS device will be presented with an option to provide data from Health as the answer to this question. Participants are allowed to edit  the answer before submitting it."></span></label>
	               </span>
	            </div>
	            <div id="healthKitContainerId" style="display: none">
	            	<div class="col-md-4 p-none">
		               <div class="gray-xs-f mt-lg">Select a HealthKit quantity data type <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-html=true data-toggle="tooltip" title="- Please select the appropriate HealthKit data type as suited to the question<br>- Please note that only the most recent value available in HealthKit would be read by the app<br>- Access to HealthKit data is subject to the user providing permissions for the app to read the data"></span></div>
		               <div class="form-group">
		                  <select class="selectpicker elaborateClass healthkitrequireClass" id="healthkitDatatypeId" name="healthkitDatatype" value="${questionsBo.healthkitDatatype}">
		                       <option value="" selected >Select</option>
			                   <c:forEach items="${healthKitKeysInfo}" var="healthKitKeys">
			                        <option value="${healthKitKeys.key}" ${questionsBo.healthkitDatatype eq healthKitKeys.key ? 'selected':''}>${healthKitKeys.displayName}</option>
			                   </c:forEach>
		                  </select>
		                  <div class="help-block with-errors red-txt"></div>
		               </div>
		            </div>
	            </div>
            </c:if>
            <div class="clearfix"></div>
            <c:if test="${questionnaireBo.frequency ne 'One time'}">
            <div class="bor-dashed mt-md mb-md" id="borderHealthdashId" style="display:none"></div>
            <div class="mt-lg mb-lg" id="addLineChartContainerId" style="display: none">
               <span class="checkbox checkbox-inline">
               <input type="checkbox" id="addLineChart" name="addLineChart" value="Yes" ${questionsBo.addLineChart eq 'Yes' ? 'checked':''} <c:if test="${questionnairesStepsBo.repeatable eq'Yes'}">disabled</c:if>>
               <label for="addLineChart"> Add response data to line chart on app dashboard </label>
               </span>
            </div>
            <div class="clearfix"></div>
            <div id="chartContainer" style="display: none">
            <div class="col-md-6 p-none">
               <div class="gray-xs-f mb-xs">Time range for the chart <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="The options available here depend on the scheduling frequency set for the activity. For multiple-times-a-day and custom- scheduled activities, the chart's X axis divisions will represent runs. For the former case, the chart will display all runs for the day while for the latter, the chart will display a max of 5 runs at a time."></span></div>
               <div class="form-group">
                  <select class="selectpicker elaborateClass chartrequireClass" id="lineChartTimeRangeId" name="lineChartTimeRange" value="${questionsBo.lineChartTimeRange}">
                       <option value="" selected >Select</option>
	                   <c:forEach items="${timeRangeList}" var="timeRangeAttr">
	                        <option value="${timeRangeAttr}" ${questionsBo.lineChartTimeRange eq timeRangeAttr ? 'selected':''}>${timeRangeAttr}</option>
	                   </c:forEach>
                  </select>
                  <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div>
               <div class="gray-xs-f mb-xs">Allow rollback of chart? <span class="sprites_icon info" data-toggle="tooltip" title="If you select Yes, the chart will be allowed for rollback until the date of enrollment into the study."></span></div>
               <div>
                  <span class="radio radio-info radio-inline p-45">
                  <input type="radio" id="allowRollbackChartYes" value="Yes" name="allowRollbackChart" ${questionsBo.allowRollbackChart eq 'Yes' ? 'checked': ''}>
                  <label for="allowRollbackChartYes">Yes</label>
                  </span>
                  <span class="radio radio-inline">
                  <input type="radio" id="allowRollbackChartNo" value="No" name="allowRollbackChart" ${questionsBo.allowRollbackChart eq 'No' ? 'checked': ''}>
                  <label for="allowRollbackChartNo">No</label>
                  </span>
                  <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="col-md-4 col-lg-4 p-none">
               <div class="gray-xs-f mb-xs">Title for the chart (1 to 30 characters)<span class="requiredStar">*</span></div>
               <div class="form-group">
                  <input type="text" class="form-control chartrequireClass" name="chartTitle" id="chartTitleId" value="${fn:escapeXml(
                  questionsBo.chartTitle)}" maxlength="30">
                  <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            </div>
            </c:if>
            <div class="clearfix"></div>
            <div class="bor-dashed mb-md" id="borderdashId" style="display:none"></div>
            <div class="clearfix"></div>
            <div class="mb-lg" id="useStasticDataContainerId" style="display: none">
               <span class="checkbox checkbox-inline">
               <input type="checkbox" id= "useStasticData" value="Yes" name="useStasticData" ${questionsBo.useStasticData eq 'Yes' ? 'checked':''} <c:if test="${questionnairesStepsBo.repeatable eq 'Yes'}">disabled</c:if>>
               <label for="useStasticData"> Use response data for statistic on dashboard</label>
               </span>
            </div>
            <div class="clearfix"></div>
            <div id="statContainer" style="display: none">
            <div class="col-md-6 col-lg-4 p-none">
               <div class="gray-xs-f mb-xs">Short identifier name (1 to 20 characters)<span class="requiredStar">*</span></div>
               <div class="form-group">
                  <input type="text" custAttType="cust" class="form-control requireClass" name="statShortName" id="statShortNameId" value="${fn:escapeXml(questionsBo.statShortName)}" 
                  maxlength="20" <c:if test="${not empty questionsBo.isStatShortNameDuplicate && (questionsBo.isStatShortNameDuplicate gt 0)}"> disabled</c:if>>
               	  <div class="help-block with-errors red-txt"></div>
               	  <input type="hidden" id="prevStatShortNameId" value= "${fn:escapeXml(questionsBo.statShortName)}">
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="col-md-10 p-none">
               <div class="gray-xs-f mb-xs">Display name for the Stat (e.g. Total Hours of Activity Over 6 Months) (1 to 50 characters)<span class="requiredStar">*</span></div>
               <div class="form-group">
                  <input type="text" class="form-control requireClass" name="statDisplayName" id="statDisplayNameId" value="${fn:escapeXml(
                  questionsBo.statDisplayName)}" maxlength="50">
                  <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="col-md-6 col-lg-4 p-none">
               <div class="gray-xs-f mb-xs">Display Units (e.g. hours) (1 to 15 characters)<span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip"  data-toggle="tooltip" title="For Response Types of Time Interval and Height, participant responses are saved in hours and cms respectively. Please enter units accordingly."></span></div>
               <div class="form-group">
                  <input type="text" class="form-control requireClass" name="statDisplayUnits" id="statDisplayUnitsId" value="${fn:escapeXml(
                  questionsBo.statDisplayUnits)}" maxlength="15">
                  <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="col-md-4 col-lg-3 p-none">
               <div class="gray-xs-f mb-xs">Stat Type for image upload <span class="requiredStar">*</span></div>
               <div class="form-group">
                  <select class="selectpicker elaborateClass requireClass" id="statTypeId" title="Select" name="statType">
			         <option value="" selected >Select</option>
			         <c:forEach items="${statisticImageList}" var="statisticImage">
			            <option value="${statisticImage.statisticImageId}" ${questionsBo.statType eq statisticImage.statisticImageId ? 'selected':''}>${statisticImage.value}</option>
			         </c:forEach>
			      </select> 
			      <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="col-md-10 p-none">
               <div class="gray-xs-f mb-xs">Formula for to be applied <span class="requiredStar">*</span></div>
               <div class="form-group">
                  <select class="selectpicker elaborateClass requireClass" id="statFormula" title="Select" name="statFormula">
			         <option value="" selected >Select</option>
			         <c:forEach items="${activetaskFormulaList}" var="activetaskFormula">
			            <option value="${activetaskFormula.activetaskFormulaId}" ${questionsBo.statFormula eq activetaskFormula.activetaskFormulaId ? 'selected':''}>${activetaskFormula.value}</option>
			         </c:forEach>
			      </select>
			      <div class="help-block with-errors red-txt"></div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="col-md-10 p-none">
               <div class="gray-xs-f mb-xs">Time ranges options available to the mobile app user</div>
               <div class="clearfix"></div>
            </div>
            <div class="clearfix"></div>
            <div>
               <div>
                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Day</span></span>
                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Week</span></span>
                  <span class="mr-lg"><span class="mr-sm"><img src="../images/icons/tick.png"/></span><span>Current Month</span></span>
                  <span class="txt-gray">(Rollback option provided for these three options)</span>
               </div>
               
            </div>
		</div>
         </div>
         <!---  Form-level Attributes ---> 
         <div id="rla" class="tab-pane fade mt-xlg">
            <div class="col-md-4 col-lg-4 p-none">
               <div class="gray-xs-f mb-xs">Response Type </div>
               <div class="gray-xs-f mb-xs"><small>The type of interface needed to capture the response</small></div>
               <div class="form-group">
                  <input type="text" class="form-control" id="rlaResonseType" disabled>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="row mt-sm">
               <div class="col-md-6 pl-none">
                  <div class="gray-xs-f mb-xs">Description of response type</div>
                  <div id="rlaResonseTypeDescription">
                     - NA -
                  </div>
               </div>
               <div class="col-md-6">
                  <div class="gray-xs-f mb-xs">Data Type</div>
                  <div id="rlaResonseDataType"> - NA -</div>
               </div>
            </div>
            <div class="clearfix"></div>
            <input type="hidden" class="form-control" name="questionReponseTypeBo.responseTypeId" id="questionResponseTypeId" value="${questionsBo.questionReponseTypeBo.responseTypeId}">
            <input type="hidden" class="form-control" name="questionReponseTypeBo.questionsResponseTypeId" id="responseQuestionId" value="${questionsBo.questionReponseTypeBo.questionsResponseTypeId}">
            <input type="hidden" class="form-control" name="questionReponseTypeBo.placeholder" id="placeholderTextId" />
            <input type="hidden" class="form-control" name="questionReponseTypeBo.step" id="stepValueId" />
            <div id="responseTypeDivId">
            <div id="scaleType" style="display: none">
            	<div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Scale Type <span class="requiredStar">*</span></div>
	               <div>
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="ScaleRequired" id="vertical" value="true" name="questionReponseTypeBo.vertical"  ${questionsBo.questionReponseTypeBo.vertical ? 'checked':''} >
	                  <label for="vertical">Vertical</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="ScaleRequired" id="horizontal" value="false" name="questionReponseTypeBo.vertical" ${empty questionsBo.questionReponseTypeBo.vertical || !questionsBo.questionReponseTypeBo.vertical ? 'checked':''} >
	                  <label for="horizontal">Horizontal</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	            </div>
            </div>
            <div id="Scale" style="display: none">
            <div class="clearfix"></div>
            <div class="row mt-md mb-xs">
               <div class="col-md-6 pl-none">
                  <div class="col-md-9 col-lg-9 p-none">
                     <div class="gray-xs-f mb-xs">Minimum Value <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer number in the range (Min, 10000)."></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control ScaleRequired"  name="questionReponseTypeBo.minValue" id="scaleMinValueId" value="${fn:escapeXml(
                        questionsBo.questionReponseTypeBo.minValue)}" onkeypress="return isOnlyNumber(event)">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
               <div class="col-md-6">
                  <div class="col-md-9 col-lg-9 p-none">
                     <div class="gray-xs-f mb-xs">Maximum Value <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer number in the range (Min+1, 10000)."></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control ScaleRequired" name="questionReponseTypeBo.maxValue" id="scaleMaxValueId" value="${fn:escapeXml(
                        questionsBo.questionReponseTypeBo.maxValue)}" onkeypress="return isOnlyNumber(event)">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="row mb-xs">
            	<div class="col-md-6 pl-none">
                  <div class="col-md-9 col-lg-9 p-none">
                  	<div class="gray-xs-f mb-xs">Description for minimum value (1 to 50 characters)</div>
	                <div class="form-group">
	                  <input type="text" class="form-control" name="questionReponseTypeBo.minDescription" id="scaleMinDescriptionId" value="${fn:escapeXml(
	                  questionsBo.questionReponseTypeBo.minDescription)}" maxlength="50"/>
	                  <div class="help-block with-errors red-txt"></div>
	                </div>
                  </div>
                </div>
            	<div class="col-md-6">
                  <div class="col-md-9 col-lg-9 p-none">
                  	<div class="gray-xs-f mb-xs">Description for maximum value (1 to 50 characters)</div>
	                <div class="form-group">
	                  <input type="text" class="form-control" name="questionReponseTypeBo.maxDescription" id="scaleMaxDescriptionId" value="${fn:escapeXml(
	                  questionsBo.questionReponseTypeBo.maxDescription)}" maxlength="50" />
	                  <div class="help-block with-errors red-txt"></div>
	                </div>
                  </div>
                </div>
            </div>
            <div class="clearfix"></div>
            <div class="row mb-xs">
               <div class="col-md-6 pl-none">
	               <div class="col-md-9 col-lg-9 p-none">
		               <div class="gray-xs-f mb-xs">Step Size  <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter the desired size to be applied to each step in the scale. Note that this value determines the step count or  number of steps in the scale. You will be prompted to enter a different step size if the scale cannot be divided into equal steps. Or if the value you entered results in a step count <1 or >13. "></span></div>
		               <div class="form-group">
		               	  <c:if test="${not empty questionsBo.questionReponseTypeBo.step}">
		               	  	<input type="text" class="form-control ScaleRequired" id="displayStepsCount"  value="<fmt:formatNumber  value="${(questionsBo.questionReponseTypeBo.maxValue-questionsBo.questionReponseTypeBo.minValue)/questionsBo.questionReponseTypeBo.step}"  groupingUsed="false" maxFractionDigits="0" type="number" />" onkeypress="return isNumber(event)" >
		               	  </c:if>
		                  <c:if test="${empty questionsBo.questionReponseTypeBo.step}">
		               	  	<input type="text" class="form-control ScaleRequired" id="displayStepsCount"  value="" onkeypress="return isNumber(event)" >
		               	  </c:if>
		                  <div class="help-block with-errors red-txt"></div>
		               </div>
		           </div>
	           </div>
	           <div class="col-md-6 ">
	           <div class="col-md-9 col-lg-9 p-none">
	           		<div class="gray-xs-f mb-xs">Number of Step <span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="This represents the number of steps the scale is divided into."></span></div>
	           		 <input type="text" class="form-control ScaleRequired"  id="scaleStepId" value="${questionsBo.questionReponseTypeBo.step}" disabled="disabled" >
	           		 <div class="help-block with-errors red-txt"></div>
	           	   </div>
	           </div>
	        </div>
	        <div class="clearfix"></div>
	        <div class="row mb-xs">
	        	<div class="col-md-6 pl-none">
                  <div class="col-md-9 col-lg-9 p-none">
                     <div class="gray-xs-f mb-xs">Default value (slider position) <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer number to indicate the desired default step position for the slider in the scale.  Ensure it is in the range (0,  Numer of  Steps). For example, if you have 6 steps,  0 indicates the minimum value, 1 indicates the first step and so on. 6 indicates the maximum value. "></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control ScaleRequired" name="questionReponseTypeBo.defaultValue" id="scaleDefaultValueId" value="${fn:escapeXml(
                        questionsBo.questionReponseTypeBo.defaultValue)}" onkeypress="return isOnlyNumber(event)">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
	        </div>
	         <div class="clearfix"></div>
	        <div class="row mb-xs">
            	<div class="col-md-6 pl-none">
            	<div class="col-md-8 col-lg-8 pl-none">
				   <div class="gray-xs-f mb-xs">Image for Minimum Value<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="Upload an image that represents the minimum value.<br>JPEG / PNG <br> Recommended Size: <br>Min: 90x90 Pixels<br>Max: 120x120 Pixels<br>(Maintain aspect ratio for the selected size of the image)"></span></div>
				   <div class="form-group col-smthumb-2">
				      <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
				         <div class="thumb-img">
				            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionReponseTypeBo.minImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
				         </div>
				        
				         <c:if test="${empty questionsBo.questionReponseTypeBo.minImage}"><div class="textLabelscaleMinImagePathId" >Upload</div></c:if>
					     <c:if test="${not empty questionsBo.questionReponseTypeBo.minImage}"><div class="textLabelscaleMinImagePathId" >Change</div></c:if>
				      </div>
				      <input class="dis-none upload-image" data-imageId='0' name="questionReponseTypeBo.minImageFile" id="scaleMinImageFileId" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
				      <input type="hidden" name="questionReponseTypeBo.minImage" id="scaleMinImagePathId" value="${questionsBo.questionReponseTypeBo.minImage}">
				      <span id="removeUrl" class="blue-link elaborateHide removeImageId <c:if test="${empty questionsBo.questionReponseTypeBo.minImage}">hide</c:if>"  onclick="removeImage(this);">X<a href="javascript:void(0)" class="blue-link txt-decoration-underline pl-xs">Remove Image</a></span>
				      <div class="help-block with-errors red-txt"></div>
				   </div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="col-md-8 col-lg-8 pl-none">
				<div class="gray-xs-f mb-xs">Image for Maximum Value<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="Upload an image that represents the maximum value.<br>JPEG / PNG <br> Recommended Size: <br>Min: 90x90 Pixels<br>Max: 120x120 Pixels<br>(Maintain aspect ratio for the selected size of the image)"></span></div>
				   <div class="form-group col-smthumb-2">
				      <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
				         <div class="thumb-img">
				            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionReponseTypeBo.maxImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
				         </div>
				        
				         <c:if test="${empty questionsBo.questionReponseTypeBo.maxImage}"><div class="textLabelscaleMaxImagePathId" >Upload</div></c:if>
					     <c:if test="${not empty questionsBo.questionReponseTypeBo.maxImage}"><div class="textLabelscaleMaxImagePathId" >Change</div></c:if>
				      </div>
				      <input class="dis-none upload-image" data-imageId='1' name="questionReponseTypeBo.maxImageFile" id="scaleMaxImageFileId" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
				      <input type="hidden" name="questionReponseTypeBo.maxImage" id="scaleMaxImagePathId" value="${questionsBo.questionReponseTypeBo.maxImage}">
				      <span id="removeUrl " class="blue-link elaborateHide removeImageId <c:if test="${empty questionsBo.questionReponseTypeBo.maxImage}">hide</c:if>"  onclick="removeImage(this);">X<a href="javascript:void(0)" class="blue-link txt-decoration-underline pl-xs">Remove Image</a></span>
				      <div class="help-block with-errors red-txt"></div>
				   </div>
				</div>
			   </div>
            </div>
            </div>
            </div>
            <div id="ContinuousScale" style="display: none">
            <div class="clearfix"></div>
            <div class="row mt-md mb-xs">
               <div class="col-md-6 pl-none">
                  <div class="col-md-9 col-lg-9 p-none">
                     <div class="gray-xs-f mb-xs">Minimum Value <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer number in the range (Min, 10000)."></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control ContinuousScaleRequired"  name="questionReponseTypeBo.minValue" id="continuesScaleMinValueId" value="${questionsBo.questionReponseTypeBo.minValue}" onkeypress="return isNumberKey(event)">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
               <div class="col-md-6">
                  <div class="col-md-9 col-lg-9 p-none">
                     <div class="gray-xs-f mb-xs">Maximum Value <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer number in the range (Min+1, 10000)."></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control ContinuousScaleRequired" name="questionReponseTypeBo.maxValue" id="continuesScaleMaxValueId" value="${questionsBo.questionReponseTypeBo.maxValue}" onkeypress="return isNumberKey(event)">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
            </div>
            <div class="clearfix"></div>
            <div class="row mb-xs">
               <div class="col-md-6  pl-none">
                  <div class="col-md-9 col-lg-9 p-none">
                     <div class="gray-xs-f mb-xs">Default value (slider position) <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer between the minimum and maximum."></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control ContinuousScaleRequired" name="questionReponseTypeBo.defaultValue" id="continuesScaleDefaultValueId" value="${questionsBo.questionReponseTypeBo.defaultValue}" onkeypress="return isNumberKey(event)">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
               <div class="col-md-6">
               <div class="col-md-6 col-lg-4 p-none">
	               <div class="gray-xs-f mb-xs">Max Fraction Digits  <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter the maximum number of decimal places to be shown for the values on the scale. Note that your options  (0,1,2,3,4) are limited by the selected maximum and minimum values."></span></div>
	               <div class="form-group">
	                  <input type="text" class="form-control ContinuousScaleRequired"  name="questionReponseTypeBo.maxFractionDigits" id="continuesScaleFractionDigitsId" value="${questionsBo.questionReponseTypeBo.maxFractionDigits}" onkeypress="return isNumber(event)" maxlength="2" onblur="validateFractionDigits(this);">
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	           </div>
	           </div>
            </div>
            <div class="clearfix"></div>
            <div class="row mb-xs">
            	<div class="col-md-6 pl-none">
                  <div class="col-md-9 col-lg-9 p-none">
                  	<div class="gray-xs-f mb-xs">Description for minimum value (1 to 50 characters)</div>
	                <div class="form-group">
	                  <input type="text" class="form-control" name="questionReponseTypeBo.minDescription" id="continuesScaleMinDescriptionId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.minDescription)}"  maxlength="50"/>
	                  <div class="help-block with-errors red-txt"></div>
	                </div>
                  </div>
                </div>
            	<div class="col-md-6">
                  <div class="col-md-9 col-lg-9 p-none">
                  	<div class="gray-xs-f mb-xs">Description for maximum value (1 to 50 characters)</div>
	                <div class="form-group">
	                  <input type="text" class="form-control" name="questionReponseTypeBo.maxDescription" id="continuesScaleMaxDescriptionId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.maxDescription)}"  maxlength="50" />
	                  <div class="help-block with-errors red-txt"></div>
	                </div>
                  </div>
                </div>
            </div>
            <div class="clearfix"></div>
            <div class="row">
            	<div class="col-md-6 pl-none">
            	<div class="col-md-8 col-lg-8 pl-none">
            	<div class="gray-xs-f mb-xs">Image for Minimum Value <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="Upload an image that represents the minimum value.<br>JPEG / PNG <br> Recommended Size: <br>Min: 90x90 Pixels<br>Max: 120x120 Pixels<br>(Maintain aspect ratio for the selected size of the image)"></span></div>
				   <div class="form-group col-smthumb-2">
				      <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
				         <div class="thumb-img">
				            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionReponseTypeBo.minImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
				         </div>
				       
				         <c:if test="${empty questionsBo.questionReponseTypeBo.minImage}"><div class="textLabelcontinuesScaleMinImagePathId" >Upload</div></c:if>
					     <c:if test="${not empty questionsBo.questionReponseTypeBo.minImage}"><div class="textLabelcontinuesScaleMinImagePathId" >Change</div></c:if>
				      </div>
				      <input class="dis-none upload-image" data-imageId='0' name="questionReponseTypeBo.minImageFile" id="continuesScaleMinImageFileId" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
				      <input type="hidden" name="questionReponseTypeBo.minImage" id="continuesScaleMinImagePathId" value="${questionsBo.questionReponseTypeBo.minImage}">
				      <span id="removeUrl" class="blue-link elaborateHide removeImageId <c:if test="${empty questionsBo.questionReponseTypeBo.minImage}">hide</c:if>"  onclick="removeImage(this);">X<a href="javascript:void(0)" class="blue-link txt-decoration-underline pl-xs">Remove Image</a></span>
				      <div class="help-block with-errors red-txt"></div>
				   </div>
				</div>
				</div>
				<div class="col-md-6">
				<div class="col-md-8 col-lg-8 pl-none">
				<div class="gray-xs-f mb-xs">Image for Maximum Value <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="Upload an image that represents the maximum value.<br>JPEG / PNG <br> Recommended Size: <br>Min: 90x90 Pixels<br>Max: 120x120 Pixels<br>(Maintain aspect ratio for the selected size of the image)"></span></div>
				   <div class="form-group col-smthumb-2">
				      <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
				         <div class="thumb-img">
				            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionReponseTypeBo.maxImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
				         </div>
				       
				         <c:if test="${empty questionsBo.questionReponseTypeBo.maxImage}"><div class="textLabelcontinuesScaleMaxImagePathId" >Upload</div></c:if>
					     <c:if test="${not empty questionsBo.questionReponseTypeBo.maxImage}"><div class="textLabelcontinuesScaleMaxImagePathId" >Change</div></c:if>
				      </div>
				      <input class="dis-none upload-image" data-imageId='1' name="questionReponseTypeBo.maxImageFile" id="continuesScaleMaxImageFileId" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
				      <input type="hidden" name="questionReponseTypeBo.maxImage" id="continuesScaleMaxImagePathId" value="${questionsBo.questionReponseTypeBo.maxImage}">
				       <span id="removeUrl" class="blue-link elaborateHide removeImageId <c:if test="${empty questionsBo.questionReponseTypeBo.maxImage}">hide</c:if>"  onclick="removeImage(this);">X<a href="javascript:void(0)" class="blue-link txt-decoration-underline pl-xs">Remove Image</a></span>
				      <div class="help-block with-errors red-txt"></div>
				   </div>
				</div>
			   </div>
            </div>
            </div>
            <div id="Location" style="display: none">
            	<div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Use Current Location <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Choose Yes if you wish to mark the user's current location on the map used to provide the response."></span></div>
	               <div>
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="LocationRequired" id="useCurrentLocationYes" value="true" name="questionReponseTypeBo.useCurrentLocation"  ${empty questionsBo.questionReponseTypeBo.useCurrentLocation || questionsBo.questionReponseTypeBo.useCurrentLocation eq true ? 'checked':''} >
	                  <label for="useCurrentLocationYes">Yes</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="LocationRequired" id="useCurrentLocationNo" value="false" name="questionReponseTypeBo.useCurrentLocation" ${questionsBo.questionReponseTypeBo.useCurrentLocation eq false ? 'checked':''} >
	                  <label for="useCurrentLocationNo"">No</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	            </div>
            </div>
            <div id="Email" style="display: none">
	            <div class="row mt-sm">
	               <div class="col-md-6 pl-none">
	                  <div class="col-md-12 col-lg-12 p-none">
	                     <div class="gray-xs-f mb-xs">Placeholder Text (1 to 40 characters) <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an input hint to the user"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control" placeholder="1-40 characters"  id="placeholderId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.placeholder)}" maxlength="40">
	                     </div>
	                  </div>
	               </div>
	            </div>
            </div>
           <div id="Text" style="display: none">
           		<div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Allow Multiple Lines? <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Choose Yes if you need the user to enter large text in a text area."></span></div>
	               <div>
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="TextRequired" id="multipleLinesYes" value="true" name="questionReponseTypeBo.multipleLines"  ${questionsBo.questionReponseTypeBo.multipleLines ? 'checked':''} >
	                  <label for="multipleLinesYes">Yes</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="TextRequired" id="multipleLinesNo" value="false" name="questionReponseTypeBo.multipleLines" ${empty questionsBo.questionReponseTypeBo.multipleLines || !questionsBo.questionReponseTypeBo.multipleLines ? 'checked':''} >
	                  <label for="multipleLinesNo">No</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	            </div>
           		<div class="clearfix"></div>
	            <div class="row mt-lg">
	               <div class="col-md-6 pl-none">
	                  <div class="col-md-12 col-lg-12 p-none">
	                     <div class="gray-xs-f mb-xs">Placeholder (1 to 40 characters) <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an input hint to the user"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control"  placeholder="1-50 characters"  id="textPlaceholderId" value="${fn:escapeXml(
	                        questionsBo.questionReponseTypeBo.placeholder)}" maxlength="50">
	                     </div>
	                  </div>
	               </div>
	               <div class="col-md-4">
	                  <div class="col-md-6 col-lg-4 p-none">
	                     <div class="gray-xs-f mb-xs">Max Length  <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer for the maximum length of text allowed. If left empty, there will be no max limit applied."></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control" name="questionReponseTypeBo.maxLength" id="textmaxLengthId" value="${fn:escapeXml(
	                        questionsBo.questionReponseTypeBo.maxLength)}" onkeypress="return isNumber(event)" maxlength="5">
	                     </div>
	                  </div>
	               </div>
	            </div>
	            <div class="clearfix"></div>
	            <div class="row mt-md">
		            <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 pl-none">
		           
		                  <div class="col-md-12 col-lg-12 p-none">
		                     <div class="gray-xs-f mb-xs">Special Validations<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Define any special case rules you wish to be applied for the participant-entered text. If the participant's input does not meet these conditions, an admin-defined error message will be shown asking them to retry. "></span></div>
		                     <div class="col-md-3 pl-none">
			                     <div class="form-group">
			    					<select name="questionReponseTypeBo.validationCondition" id="validationConditionId"  class="selectpicker">
							         <option value=''  selected>Select</option>
							         <option value="allow" ${questionsBo.questionReponseTypeBo.validationCondition eq 'allow' ? 'selected' :''}>Allow</option>
							         <option value="disallow" ${questionsBo.questionReponseTypeBo.validationCondition eq 'disallow' ? 'selected' :''}>Disallow</option>
							       </select>                    
			                     </div>
			                     <div class="help-block with-errors red-txt"></div>
		                     </div>
		                     <div class="col-md-3 pr-none pr-xs">
			                     <div class="form-group">
          			    					<select name="questionReponseTypeBo.validationCharacters" id="validationCharactersId"  class="selectpicker <c:if test="${not empty questionsBo.questionReponseTypeBo.validationCondition}">TextRequired</c:if>" <c:if test="${empty questionsBo.questionReponseTypeBo.validationCondition}">disabled</c:if>>
          							         <option value=''  selected>Select</option>
          							         <option value="allcharacters" ${questionsBo.questionReponseTypeBo.validationCharacters eq 'allcharacters' ? 'selected' :''}>All Characters</option>
          							         <option value="alphabets" ${questionsBo.questionReponseTypeBo.validationCharacters eq 'alphabets' ? 'selected' :''}>alphabets</option>
          							         <option value="numbers" ${questionsBo.questionReponseTypeBo.validationCharacters eq 'numbers' ? 'selected' :''}>numbers</option>
          							         <option value="alphabetsandnumbers" ${questionsBo.questionReponseTypeBo.validationCharacters eq 'alphabetsandnumbers' ? 'selected' :''}>alphabets and numbers</option>
          							         <option value="specialcharacters" ${questionsBo.questionReponseTypeBo.validationCharacters eq 'specialcharacters' ? 'selected' :''}>special characters</option>
          							       </select>                
							                 <div class="help-block with-errors red-txt"></div>    
			                     </div>
			                     
		                     </div>
		                     <div class="col-md-6 pl-none">
			    					        <div class="form-group mr-xs col-md-2 pr-none">except</div>
                            <div class="col-md-9 pl-none pr-none">
                           <div class="form-group">
                            <textarea class="form-control" rows="3" cols="40" name="questionReponseTypeBo.validationExceptText" id="validationExceptTextId" <c:if test="${empty questionsBo.questionReponseTypeBo.validationCondition}">disabled</c:if>>${questionsBo.questionReponseTypeBo.validationExceptText}</textarea>
                           </div>
                           <div class="help-block with-errors red-txt"></div>
                         </div>
                         <span class="ml-xs sprites_v3 filled-tooltip float__left" data-toggle="tooltip" title="Enter text strings separated by the | symbol. E.g. AB | O Note that each of the strings will be individually checked for occurrence in the user input and allowed or disallowed based on how you have defined the rule. "></span>
		                     </div>
			    				
		                  </div>
		            </div>
	            </div>
	            <div class="clearfix"></div>
	            <div class="row">
	            	<div class="col-md-6 p-none">
		               <div class="gray-xs-f mb-xs">Invalid Message  (1 to 200 characters)<span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter text to be presented to the user when invalid input is received."></span></div>
		               <div class="form-group">
		                  <textarea class="form-control <c:if test="${not empty questionsBo.questionReponseTypeBo.validationCondition}">TextRequired</c:if>" rows="4" name="questionReponseTypeBo.invalidMessage" id="invalidMessageId" placeholder="" maxlength="200" >${fn:escapeXml(questionsBo.questionReponseTypeBo.invalidMessage)}</textarea>
		                  <div class="help-block with-errors red-txt"></div>
		               </div>
		            </div>
	            </div>
           </div>
           <div id="Height" style="display: none">
           		<div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Measurement System <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Select a suitable measurement system for height"></span></div>
	               <div>
	                  <span class="radio radio-info radio-inline pr-sm">
	                  <input type="radio" class="HeightRequired" id="measurementSystemLocal" value="Local" name="questionReponseTypeBo.measurementSystem"  ${questionsBo.questionReponseTypeBo.measurementSystem eq 'Local'? 'checked':''} >
	                  <label for="measurementSystemLocal">Local</label>
	                  </span>
	                  <span class="radio radio-inline pr-sm">
	                  <input type="radio" class="HeightRequired" id="measurementSystemMetric" value="Metric" name="questionReponseTypeBo.measurementSystem" ${questionsBo.questionReponseTypeBo.measurementSystem eq 'Metric' ? 'checked':''} >
	                  <label for="measurementSystemMetric">Metric</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="HeightRequired" id="measurementSystemUS" value="US" name="questionReponseTypeBo.measurementSystem" ${empty questionsBo.questionReponseTypeBo.measurementSystem || questionsBo.questionReponseTypeBo.measurementSystem eq 'US' ? 'checked':''} >
	                  <label for="measurementSystemUS">US</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	            </div>
           		<div class="clearfix"></div>
	            <div class="row mt-sm">
	               <div class="col-md-6 pl-none">
	                  <div class="col-md-12 col-lg-12 p-none">
	                     <div class="gray-xs-f mb-xs">Placeholder Text (1 to 20 characters) <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an input hint to the user"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control" placeholder="1-20 characters"  id="heightPlaceholderId" value="${fn:escapeXml(
	                        questionsBo.questionReponseTypeBo.placeholder)}" maxlength="20">
	                     </div>
	                  </div>
	               </div>
	            </div>
           </div>
           <div id="Timeinterval" style="display: none;">
	           <div class="row mt-sm display__flex__center">
	           	<div class="col-md-2 pl-none">
	               <div class="gray-xs-f mb-xs">Step value  <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="This is the step size in the time picker, in minutes. Choose a value from the following set (1,2,3,4,5,6,10,12,15,20 & 30)."></span></div>
	               <div class="form-group">
	                  <input type="text" class="form-control TimeintervalRequired wid90"  id="timeIntervalStepId" value="${questionsBo.questionReponseTypeBo.step}" onkeypress="return isNumber(event)" maxlength="2">
	                  <span class="dis-inline mt-sm ml-sm">Min</span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	            </div>
	            <div class="col-md-2">
	               <div class="gray-xs-f mb-xs">Default Value  <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="The default value to be seen by the participant on the time interval picker widget."></span></div>
	               <div class="form-group">
	                  <input type="text" class="form-control TimeintervalRequired wid90 clock"  name="questionReponseTypeBo.defaultTime" id="timeIntervalDefaultId" value="${questionsBo.questionReponseTypeBo.defaultTime}">
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	            </div>
	         </div>
           <div class="clearfix"></div>
          </div>
          <div id="Numeric" style="display: none;">
          	<div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Style <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Choose the kind of numeric input needed"></span></div>
	               <div class="form-group">
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="NumericRequired" id="styleDecimal" value="Decimal" name="questionReponseTypeBo.style"  ${questionsBo.questionReponseTypeBo.style eq 'Decimal' ? 'checked':''} >
	                  <label for="styleDecimal">Decimal</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="NumericRequired" id="styleInteger" value="Integer" name="questionReponseTypeBo.style" ${questionsBo.questionReponseTypeBo.style eq 'Integer' ? 'checked':''} >
	                  <label for="styleInteger">Integer</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	        </div>
           	<div class="clearfix"></div>
          	<div class="row">
	               <div class="col-md-6 pl-none">
	                  <div class="col-md-8 col-lg-8 p-none">
	                     <div class="gray-xs-f mb-xs">Units (1 to 15 characters)  <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter the applicable units for the numeric input"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control"  name="questionReponseTypeBo.unit" id="numericUnitId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.unit)}" maxlength="15">
	                     </div>
	                  </div>
	               </div>
	               <div class="col-md-6">
	                  <div class="col-md-8 col-lg-8 p-none">
	                     <div class="gray-xs-f mb-xs">Placeholder Text (1 to 30 characters)  <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Provide an input hint to the user"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control"  id="numericPlaceholderId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.placeholder)}" maxlength="30">
	                     </div>
	                  </div>
	               </div>
	        </div>
	        <div class="clearfix"></div>
	        <div class="row mb-xs">
               <div class="col-md-6 pl-none">
                  <div class="col-md-8 col-lg-8 p-none">
                     <div class="gray-xs-f mb-xs">Minimum Value <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter minimum value allowed"></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control"  name="questionReponseTypeBo.minValue" id="numericMinValueId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.minValue)}" onkeypress="return isNumberKey(event)" maxlength="50">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
               <div class="col-md-6">
                  <div class="col-md-8 col-lg-8 p-none">
                     <div class="gray-xs-f mb-xs">Maximum Value <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter maximum value allowed"></span></div>
                     <div class="form-group">
                        <input type="text" class="form-control" name="questionReponseTypeBo.maxValue" id="numericMaxValueId" value="${fn:escapeXml(questionsBo.questionReponseTypeBo.maxValue)}" onkeypress="return isNumberKey(event)" maxlength="50">
                        <div class="help-block with-errors red-txt"></div>
                     </div>
                  </div>
               </div>
            </div>
          </div>
          <div id="Date" style="display: none;">
          	<div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Style <span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Choose the kind of numeric input needed"></span></div>
	               <div class="form-group">
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="DateRequired DateStyleRequired" id="date" value="Date" name="questionReponseTypeBo.style"  ${questionsBo.questionReponseTypeBo.style eq 'Date' ? 'checked':''} >
	                  <label for="date">Date</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="DateRequired DateStyleRequired" id="dateTime" value="Date-Time" name="questionReponseTypeBo.style" ${questionsBo.questionReponseTypeBo.style eq 'Date-Time' ? 'checked':''} >
	                  <label for="dateTime">Date-Time</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	        </div>
	        <div class="mt-lg">
	               <div class="gray-xs-f mb-xs">Set allowed date range<span class="requiredStar">*</span> <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Participants will be allowed to choose a date from the date range you set here. The option 'Until current date' includes the current date as well.Date or date/time will apply as per your selection in the previous field."></span></div>
	               <div class="form-group">
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="DateRequired DateRangeRequired" id="untilCurrentDateId" value="Until current date" name="questionReponseTypeBo.selectionStyle"  ${questionsBo.questionReponseTypeBo.selectionStyle eq 'Until current date' ? 'checked':''} >
	                  <label for="untilCurrentDateId">Until current date</label>
	                  </span>
	                  <span class="radio radio-info radio-inline p-45">
	                  <input type="radio" class="DateRequired DateRangeRequired" id="afterCurrentDateId" value="After current date" name="questionReponseTypeBo.selectionStyle" ${questionsBo.questionReponseTypeBo.selectionStyle eq 'After current date' ? 'checked':''} >
	                  <label for="afterCurrentDateId">After current date</label>
	                  </span>
	                  <span class="radio radio-inline">
	                  <input type="radio" class="DateRequired DateRangeRequired" id="customDateId" value="Custom" name="questionReponseTypeBo.selectionStyle" ${questionsBo.questionReponseTypeBo.selectionStyle eq 'Custom' ? 'checked':''} >
	                  <label for="customDateId">Custom</label>
	                  </span>
	                  <div class="help-block with-errors red-txt"></div>
	               </div>
	        </div>
           	<div class="clearfix"></div>
           	<div id="customDateContainerId" <c:if test="${questionsBo.questionReponseTypeBo.selectionStyle eq 'Until current date' || questionsBo.questionReponseTypeBo.selectionStyle eq 'After current date'}">style="display: none;"</c:if>>
          	<div class="row">
	               <div class="col-md-6 pl-none">
	                  <div class="col-md-8 col-lg-8 p-none">
	                     <div class="gray-xs-f mb-xs">Minimum Date  <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter minimum date allowed."></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control"  name="questionReponseTypeBo.minDate" id="minDateId" value="${questionsBo.questionReponseTypeBo.minDate}" >
	                        <div class="help-block with-errors red-txt"></div>
	                     </div>
	                  </div>
	               </div>
	       </div>
	       <div class="row">
	               <div class="col-md-6  pl-none">
	                  <div class="col-md-8 col-lg-8 p-none">
	                     <div class="gray-xs-f mb-xs">Maximum Date <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter maximum date allowed"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control"  name="questionReponseTypeBo.maxDate"id="maxDateId" value="${questionsBo.questionReponseTypeBo.maxDate}" >
	                        <div class="help-block with-errors red-txt"></div>
	                     </div>
	                  </div>
	               </div>
	        </div>
	        <div class="row">
	               <div class="col-md-6  pl-none">
	                  <div class="col-md-8 col-lg-8 p-none">
	                     <div class="gray-xs-f mb-xs">Default Date <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter default date to be shown as selected"></span></div>
	                     <div class="form-group">
	                        <input type="text" class="form-control"  name="questionReponseTypeBo.defaultDate" id="defaultDate" value="${questionsBo.questionReponseTypeBo.defaultDate}">
	                        <div class="help-block with-errors red-txt"></div>
	                     </div>
	                  </div>
	               </div>
	        </div>
	        </div>
          </div>
          <div id="Boolean" style="display: none;">
          	<div class="clearfix"></div>
          	<div class="mt-lg"><div class="gray-choice-f mb-xs">Choices <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="If there is branching applied to your questionnaire, you can  define destination steps for the Yes and No choices"></span></div></div>
          	<div class="row mt-xs" id="0">
          		<input type="hidden" class="form-control" id="responseSubTypeValueId0" name="questionResponseSubTypeList[0].responseSubTypeValueId" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].responseSubTypeValueId)}">
	          	<div class="col-md-3 pl-none">
				   <div class="gray-xs-f mb-xs">Display Text <span class="requiredStar">*</span> </div>
				   <div class="form-group">
				      <input type="text" class="form-control" id="dispalyText0" name="questionResponseSubTypeList[0].text" value="Yes" readonly="readonly">
				      <div class="help-block with-errors red-txt"></div>
				   </div>
				</div>
				<div class="col-md-3 pl-none">
				   <div class="gray-xs-f mb-xs">Value <span class="requiredStar">*</span> </div>
				   <div class="form-group">
				      <input type="text" class="form-control" id="displayValue0" value="True" name="questionResponseSubTypeList[0].value" readonly="readonly">
				      <div class="help-block with-errors red-txt" ></div>
				   </div>
				</div>
			</div>
			
			<div class="row" id="1">
	          	<div class="col-md-3 pl-none" >
	          	<input type="hidden" class="form-control" id="responseSubTypeValueId1" name="questionResponseSubTypeList[1].responseSubTypeValueId" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].responseSubTypeValueId)}">
				   <div class="form-group">
				      <input type="text" class="form-control" id="dispalyText1" name="questionResponseSubTypeList[1].text" value="No" readonly="readonly">
				      <div class="help-block with-errors red-txt" ></div>
				   </div>
				</div>
				<div class="col-md-3 pl-none">
				   <div class="form-group">
				      <input type="text" class="form-control" id="displayValue1" value="False" name="questionResponseSubTypeList[1].value" readonly="readonly">
				      <div class="help-block with-errors red-txt"></div>
				   </div>
				</div>
			</div>
          </div>
          <div id="ValuePicker" style="display: none;">
           <div class="mt-lg"><div class="gray-choice-f mb-xs">Values for the picker<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter values in the order they must appear in the picker. Each row needs a display text and an associated value that gets captured if that choice is picked by the user."></span></div></div>
           <div class="row mt-sm" id="0">
          	<div class="col-md-3 pl-none">
			   <div class="gray-xs-f mb-xs">Display Text (1 to 20 characters)<span class="requiredStar">*</span></div>
			</div>
			<div class="col-md-4 pl-none">
			   <div class="gray-xs-f mb-xs">Value (1 to 50 characters)<span class="requiredStar">*</span></div>
			</div>
			<div class="clearfix"></div>
			<div class="ValuePickerContainer">
			<c:choose>
			  <c:when test="${questionsBo.responseType eq 4 && fn:length(questionsBo.questionResponseSubTypeList) gt 1}">
			  	<c:forEach items="${questionsBo.questionResponseSubTypeList}" var="questionResponseSubType" varStatus="subtype">
			  		<div class="value-picker row form-group mb-xs" id="${subtype.index}">
			  		<input type="hidden" class="form-control" id="valPickSubTypeValueId${subtype.index}" name="questionResponseSubTypeList[${subtype.index}].responseSubTypeValueId" value="${questionResponseSubType.responseSubTypeValueId}">
						<div class="col-md-3 pl-none">
						   <div class="form-group">
						      <input type="text" class="form-control ValuePickerRequired" name="questionResponseSubTypeList[${subtype.index}].text" id="displayValPickText${subtype.index}" value="${fn:escapeXml(questionResponseSubType.text)}" maxlength="20">
						      <div class="help-block with-errors red-txt"></div>
						   </div>
						</div>
						<div class="col-md-4 pl-none">
						   <div class="form-group">
						      <input type="text" class="form-control ValuePickerRequired valuePickerVal" name="questionResponseSubTypeList[${subtype.index}].value" id="displayValPickValue${subtype.index}" value="${fn:escapeXml(questionResponseSubType.value)}" maxlength="50">
						      <div class="help-block with-errors red-txt"></div>
						   </div>
						</div>
						<div class="col-md-2 pl-none mt__6">
						   <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addValuePicker();'>+</span>
				           <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0" onclick='removeValuePicker(this);'></span>
						</div>
					</div>
			  	</c:forEach>
			  </c:when>
			  <c:otherwise>
			  	<div class="value-picker row form-group mb-xs" id="0">
					<div class="col-md-3 pl-none">
					   <div class="form-group">
					      <input type="text" class="form-control ValuePickerRequired" name="questionResponseSubTypeList[0].text" id="displayValPickText0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].text)}" maxlength="20">
					      <div class="help-block with-errors red-txt"></div>
					   </div>
					</div>
					<div class="col-md-4 pl-none">
					   <div class="form-group">
					      <input type="text" class="form-control ValuePickerRequired valuePickerVal" name="questionResponseSubTypeList[0].value" id="displayValPickValue0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].value)}" maxlength="50">
					      <div class="help-block with-errors red-txt"></div>
					   </div>
					</div>
					<div class="col-md-2 pl-none mt__6">
					   <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addValuePicker();'>+</span>
			           <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0" onclick='removeValuePicker(this);'></span>
					</div>
				</div>
			   <div class="value-picker row form-group mb-xs" id="1">
					<div class="col-md-3 pl-none">
					   <div class="form-group">
					      <input type="text" class="form-control ValuePickerRequired" name="questionResponseSubTypeList[1].text" id="displayValPickText1" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].text)}" maxlength="20">
					      <div class="help-block with-errors red-txt"></div>
					   </div>
					</div>
					<div class="col-md-4 pl-none">
					   <div class="form-group">
					      <input type="text" class="form-control ValuePickerRequired valuePickerVal" name="questionResponseSubTypeList[1].value" id="displayValPickValue1" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].value)}" maxlength="50">
					      <div class="help-block with-errors red-txt"></div>
					   </div>
					</div>
					<div class="col-md-2 pl-none mt__6">
					<span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addValuePicker();'>+</span>
			        <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0" onclick='removeValuePicker(this);'></span>
					</div>
			   </div> 
			  </c:otherwise>
			</c:choose>
          	</div>
          </div>
         <div>
         </div>
         </div>
         <div id="TextScale" style="display: none;">
            <div class="clearfix"></div>
            <div class="gray-choice-f mb-xs mt-md">Text Choices<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter text choices in the order you want them to appear on the slider. You can enter a text that will be displayed for each slider position, and an associated  value to be captured if that position is selected by the user.  You can also select a destination step for each choice, if you have branching enabled for the questionnaire. "></span></div>
            <div class="row">
				   <div class="col-md-3 pl-none">
				      <div class="gray-xs-f mb-xs">Display Text (1 to 15 characters)<span class="requiredStar">*</span> </div>
				   </div>
				   <div class="col-md-4 pl-none">
				      <div class="gray-xs-f mb-xs">Value (1 to 50 characters)<span class="requiredStar">*</span></div>
				   </div>
				</div>
			<div class="TextScaleContainer">
				<c:choose>
					<c:when test="${questionsBo.responseType eq 3 && fn:length(questionsBo.questionResponseSubTypeList) gt 1}">
						<c:forEach items="${questionsBo.questionResponseSubTypeList}" var="questionResponseSubType" varStatus="subtype">
							<div class="text-scale row" id="${subtype.index}">
							<input type="hidden" class="form-control" id="textScaleSubTypeValueId${subtype.index}" name="questionResponseSubTypeList[${subtype.index}].responseSubTypeValueId" value="${questionResponseSubType.responseSubTypeValueId}">
							   <div class="col-md-3 pl-none">
							      <div class="form-group">
							         <input type="text" class="form-control TextScaleRequired" name="questionResponseSubTypeList[${subtype.index}].text" id="displayTextSclText${subtype.index}" value="${fn:escapeXml(questionResponseSubType.text)}" maxlength="15">
							         <div class="help-block with-errors red-txt"></div>
							      </div>
							   </div>
							   <div class="col-md-4 pl-none">
							      <div class="form-group">
							         <input type="text" class="form-control TextScaleRequired textScaleValue" name="questionResponseSubTypeList[${subtype.index}].value" id="displayTextSclValue${subtype.index}" value="${fn:escapeXml(questionResponseSubType.value)}" maxlength="50">
							         <div class="help-block with-errors red-txt"></div>
							      </div>
							   </div>
							   <div class="col-md-2 pl-none mt__8">
								<c:choose>
							     <c:when test="${fn:length(questionsBo.questionResponseSubTypeList) eq 8 }"><span class='tool-tip' data-toggle='tooltip' data-placement='top' title='Only a max of 8 rows are allowed'><span class='addBtnDis addbtn mr-sm align-span-center cursor-none' onclick='addTextScale();' >+</span></span></c:when>
							     <c:otherwise><span class="addBtnDis addbtn mr-sm align-span-center" onclick='addTextScale();'>+</span></c:otherwise>
							    </c:choose>
						        <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center" onclick='removeTextScale(this);'></span>
							   </div>
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div class="text-scale row" id="0">
						   <div class="col-md-3 pl-none">
						      <div class="form-group">
						         <input type="text" class="form-control TextScaleRequired" name="questionResponseSubTypeList[0].text" id="displayTextSclText0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].text)}" maxlength="15">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-4 pl-none">
						      <div class="form-group">
						         <input type="text" class="form-control TextScaleRequired textScaleValue" name="questionResponseSubTypeList[0].value" id="displayTextSclValue0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].value)}" maxlength="50">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-2 pl-none mt__8">
							<span class="addBtnDis addbtn mr-sm align-span-center" onclick='addTextScale();'>+</span>
					        <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center" onclick='removeTextScale(this);'></span>
							</div>
						</div>
		            	<div class="text-scale row" id="1">
						   <div class="col-md-3 pl-none">
						      <div class="form-group">
						         <input type="text" class="form-control TextScaleRequired" name="questionResponseSubTypeList[1].text" id="displayTextSclText1" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].text)}" maxlength="15">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-4 pl-none">
						      <div class="form-group">
						         <input type="text" class="form-control TextScaleRequired textScaleValue" name="questionResponseSubTypeList[1].value" id="displayTextSclValue1" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].value)}" maxlength="50">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-2 pl-none mt__8">
							<span class="addBtnDis addbtn mr-sm align-span-center" onclick='addTextScale();'>+</span>
					        <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center" onclick='removeTextScale(this);'></span>
							</div>
						</div>
					</c:otherwise>
				</c:choose>
            </div>
            <div class="clearfix"></div>
            <div class="row mt-none">
                <div class="col-md-6 pl-none">
                   <div class="col-md-8 col-lg-8 p-none">
                        <div class="gray-xs-f mb-xs">Default slider position  <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter an integer number to indicate the desired default slider position. For example, if you have 6 choices, 5 will indicate the 5th choice."></span></div>
                        <div class="form-group">
                           <input type="text" class="form-control" id="textScalePositionId"  value="${questionsBo.questionReponseTypeBo.step}" onkeypress="return isNumber(event)">
                           <div class="help-block with-errors red-txt"></div>
                        </div>
                        </div>
                   </div>                          
               </div>           
         </div>  
         <div id="TextChoice" style="display: none;">
          <div class="mt-lg">
              <div class="gray-xs-f mb-xs">Selection Style <span class="requiredStar">*</span></div>
              <div class="form-group">
                  <span class="radio radio-info radio-inline p-45">
                  <input type="radio" class="TextChoiceRequired" id="singleSelect" value="Single" name="questionReponseTypeBo.selectionStyle"  ${empty questionsBo.questionReponseTypeBo.selectionStyle || questionsBo.questionReponseTypeBo.selectionStyle eq 'Single' ? 'checked':''} onchange="getSelectionStyle(this);">
                  <label for="singleSelect">Single Select</label>
                  </span>
                  <span class="radio radio-inline">
                  <input type="radio" class="TextChoiceRequired" id="multipleSelect" value="Multiple" name="questionReponseTypeBo.selectionStyle" ${questionsBo.questionReponseTypeBo.selectionStyle eq 'Multiple' ? 'checked':''} onchange="getSelectionStyle(this);">
                  <label for="multipleSelect">Multiple Select</label>
                  </span>
                  <div class="help-block with-errors red-txt"></div>
               </div>
          </div>
         <div class="clearfix"></div>
		 <div class="gray-choice-f mb-xs">Text Choices<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Enter text choices in the order you want them to appear. You can enter a display text, an associated  value to be captured if that choice is selected and mark the choice as exclusive, meaning once it is selected, all other options get deselected and vice-versa. You can also select a destination step for each choice that is exclusive, if you have branching enabled for the questionnaire. "></span></div>
         <div class="TextChoiceContainer">
         	<c:choose>
				<c:when test="${questionsBo.responseType eq 6 && fn:length(questionsBo.questionResponseSubTypeList) gt 1}">
					<c:forEach items="${questionsBo.questionResponseSubTypeList}" var="questionResponseSubType" varStatus="subtype">
						 <!-- Section Start  -->
						   <div class="text-choice mt-xlg" id="${subtype.index}">
						      <input type="hidden" class="form-control" id="textChoiceSubTypeValueId${subtype.index}" name="questionResponseSubTypeList[${subtype.index}].responseSubTypeValueId" value="${questionResponseSubType.responseSubTypeValueId}">
							  <div class="col-md-3 pl-none">
						         <div class="gray-xs-f mb-xs">Display Text (1 to 100 characters)<span class="requiredStar">*</span> </div>
						         <div class="form-group mb-none">
						            <input type="text" class="form-control TextChoiceRequired" name="questionResponseSubTypeList[${subtype.index}].text" id="displayTextChoiceText${subtype.index}" value="${fn:escapeXml(questionResponseSubType.text)}" maxlength="100">
						            <div class="help-block with-errors red-txt"></div>
						         </div>
						      </div>
						      <div class="col-md-3 pl-none">
						         <div class="gray-xs-f mb-xs">Value (1 to 50 characters)<span class="requiredStar">*</span> </div>
						         <div class="form-group mb-none">
						            <input type="text" class="form-control TextChoiceRequired textChoiceVal" name="questionResponseSubTypeList[${subtype.index}].value" id="displayTextChoiceValue${subtype.index}" value="${fn:escapeXml(questionResponseSubType.value)}" maxlength="50">
						            <div class="help-block with-errors red-txt"></div>
						         </div>
						      </div>
						      <div class="col-md-2 pl-none">
						         <div class="gray-xs-f mb-xs">Mark as exclusive ? <span class="requiredStar">*</span> </div>
						         <div class="form-group">
						            <select name="questionResponseSubTypeList[${subtype.index}].exclusive" id="exclusiveId${subtype.index}" index="${subtype.index}" title="select" data-error="Please choose one option" class="selectpicker <c:if test="${questionsBo.questionReponseTypeBo.selectionStyle eq 'Multiple'}">TextChoiceRequired</c:if> textChoiceExclusive" <c:if test="${empty questionsBo.questionReponseTypeBo.selectionStyle || questionsBo.questionReponseTypeBo.selectionStyle eq 'Single'}">disabled</c:if> >
									   <option value="Yes" ${questionResponseSubType.exclusive eq 'Yes' ? 'selected' :''}>Yes</option>
									   <option value="No" ${questionResponseSubType.exclusive eq 'No' ? 'selected' :''}>No</option>
									</select>
						            <div class="help-block with-errors red-txt"></div>
						         </div>
						      </div>      
                  <div class="col-md-12 p-none display__flex__center">
  						      <div class="col-md-10 pl-none">
  						         <div class="gray-xs-f mb-xs">Description(1 to 150 characters) </div>
  						         <div class="form-group">					     
  						            <textarea class="form-control" name="questionResponseSubTypeList[${subtype.index}].description" id="displayTextChoiceDescription${subtype.index}" value="${fn:escapeXml(questionResponseSubType.description)}" maxlength="150">${fn:escapeXml(questionResponseSubType.description)}</textarea>
  						         </div>
  						      </div>
  						      <div class="col-md-2 pl-none">
  								 <span class="addBtnDis addbtn align-span-center top6" onclick='addTextChoice();'>+</span>
  								 <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0 ml-sm" onclick='removeTextChoice(this);'></span>        
  						      </div>
                  </div>
						   </div>
						   <!-- Section End  -->
					</c:forEach>
				</c:when>
				<c:otherwise>
					 <!-- Section Start  -->
					   <div class="text-choice mt-xlg" id="0">
						  <div class="col-md-3 pl-none">
					         <div class="gray-xs-f mb-xs">Display Text (1 to 100 characters)<span class="requiredStar">*</span> </div>
					         <div class="form-group mb-none">
					            <input type="text" class="form-control TextChoiceRequired" name="questionResponseSubTypeList[0].text" id="displayTextChoiceText0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].text)}" maxlength="100">
					            <div class="help-block with-errors red-txt"></div>
					         </div>
					      </div>
					      <div class="col-md-3 pl-none">
					         <div class="gray-xs-f mb-xs">Value (1 to 50 characters)<span class="requiredStar">*</span> </div>
					         <div class="form-group mb-none">
					            <input type="text" class="form-control TextChoiceRequired textChoiceVal" name="questionResponseSubTypeList[0].value" id="displayTextChoiceValue0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].value)}" maxlength="50">
					            <div class="help-block with-errors red-txt"></div>
					         </div>
					      </div>
					      <div class="col-md-2 pl-none">
					         <div class="gray-xs-f mb-xs">Mark as exclusive ? <span class="requiredStar">*</span> </div>
					         <div class="form-group">
					             <select name="questionResponseSubTypeList[0].exclusive" id="exclusiveId0" index="0" title="select" data-error="Please choose one option" class="selectpicker <c:if test="${questionsBo.questionReponseTypeBo.selectionStyle eq 'Multiple'}">TextChoiceRequired</c:if> textChoiceExclusive" <c:if test="${ empty questionsBo.questionReponseTypeBo.selectionStyle || questionsBo.questionReponseTypeBo.selectionStyle eq 'Single'}">disabled</c:if> >
						              <option value="Yes" ${questionsBo.questionResponseSubTypeList[0].exclusive eq 'Yes' ? 'selected' :''}>Yes</option>
						              <option value="No" ${questionsBo.questionResponseSubTypeList[0].exclusive eq 'No' ? 'selected' :''}>No</option>
						          </select>
					            <div class="help-block with-errors red-txt"></div>
					         </div>
					      </div> 
                <div class="col-md-12 p-none display__flex__center">     
  					      <div class="col-md-10 pl-none">
  					         <div class="gray-xs-f mb-xs">Description(1 to 150 characters) <span class="requiredStar">*</span> </div>
  					         <div class="form-group">					     
  					            <textarea class="form-control" name="questionResponseSubTypeList[0].description" id="displayTextChoiceDescription0" value="${fn:escapeXml(questionResponseSubType.questionResponseSubTypeList[0].description)}" maxlength="150">${fn:escapeXml(questionResponseSubType.questionResponseSubTypeList[0].description)}</textarea>
  					         </div>
  					      </div>
  					      <div class="col-md-2 pl-none">
  							 <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addTextChoice();'>+</span>
  							 <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0 ml-sm" onclick='removeTextChoice(this);'></span>       
  					      </div>
                </div>
					   </div>
					   <!-- Section End  -->
					   <div class="text-choice mt-xlg" id="1">
						  <div class="col-md-3 pl-none">
					         <div class="gray-xs-f mb-xs">Display Text (1 to 100 characters)<span class="requiredStar">*</span> </div>
					         <div class="form-group mb-none">
					            <input type="text" class="form-control TextChoiceRequired" name="questionResponseSubTypeList[1].text" id="displayTextChoiceText0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].text)}" maxlength="100">
					            <div class="help-block with-errors red-txt"></div>
					         </div>
					      </div>
					      <div class="col-md-3 pl-none">
					         <div class="gray-xs-f mb-xs">Value (1 to 50 characters)<span class="requiredStar">*</span> </div>
					         <div class="form-group mb-none">
					            <input type="text" class="form-control TextChoiceRequired textChoiceVal" name="questionResponseSubTypeList[1].value" id="displayTextChoiceValue0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].value)}" maxlength="50">
					            <div class="help-block with-errors red-txt"></div>
					         </div>
					      </div>
					      <div class="col-md-2 pl-none">
					         <div class="gray-xs-f mb-xs">Mark as exclusive ? <span class="requiredStar">*</span> </div>
					         <div class="form-group">
					             <select name="questionResponseSubTypeList[1].exclusive" id="exclusiveId1" index="1" title="select" data-error="Please choose one option" class="selectpicker <c:if test="${questionsBo.questionReponseTypeBo.selectionStyle eq 'Multiple'}">TextChoiceRequired</c:if> textChoiceExclusive" <c:if test="${ empty questionsBo.questionReponseTypeBo.selectionStyle || questionsBo.questionReponseTypeBo.selectionStyle eq 'Single'}">disabled</c:if> >
						              <option value="Yes" ${questionsBo.questionResponseSubTypeList[1].exclusive eq 'Yes' ? 'selected' :''}>Yes</option>
						              <option value="No" ${questionsBo.questionResponseSubTypeList[1].exclusive eq 'No' ? 'selected' :''}>No</option>
						          </select>
					            <div class="help-block with-errors red-txt"></div>
					         </div>
					      </div>  
                <div class="col-md-12 p-none display__flex__center">    
  					      <div class="col-md-10 pl-none">
  					         <div class="gray-xs-f mb-xs">Description(1 to 150 characters) <span class="requiredStar">*</span> </div>
  					         <div class="form-group">					     
  					            <textarea class="form-control" name="questionResponseSubTypeList[1].description" id="displayTextChoiceDescription1" value="${fn:escapeXml(questionResponseSubType.questionResponseSubTypeList[1].description)}" maxlength="150">${fn:escapeXml(questionResponseSubType.questionResponseSubTypeList[1].description)}</textarea>
  					         </div>
  					      </div>
  					      <div class="col-md-2 pl-none">
  							 <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addTextChoice();'>+</span>
  							 <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0 ml-sm" onclick='removeTextChoice(this);'></span>        
  					      </div>
                </div>
					   </div>
					   <!-- Section End  -->
				</c:otherwise>
			</c:choose> 
         </div>
         <div class="clearfix"></div>
			<div class="checkbox checkbox-inline gray-xs-f mb-xs">
	               <input type="checkbox" name="questionReponseTypeBo.otherType" id="textchoiceOtherId" disabled>
	               <label for="textchoiceOtherId"> Include 'Other' as an option ? </label> 
	               <span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Fields related to an explicit 'Other' option are not supported for text-choice Question Steps that are within a Form Step."></span>
		     </div>
         </div>
         <div id="ImageChoice" style="display: none;">
         	<div class="mt-lg"><div class="gray-choice-f mb-xs">Image Choices<span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Fill in the different image choices you wish to provide. Upload images for display and selected states and enter display text and value to be captured for each choice. Also, if you have branching enabled for your questionnaire, you can define destination steps for each choice."></span></div></div>
         	<div class="mt-sm row">
			   <div>
			      <div class="col-md-2 pl-none col-smthumb-2">
			         <div class="gray-xs-f mb-xs">Image <span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="JPEG / PNG <br> Recommended Size: <br>Min: 90x90 Pixels<br>Max: 120x120 Pixels<br>(Maintain aspect ratio for the selected size of the image)"></span> </div>
			      </div>
			      <div class="col-md-2 pl-none col-smthumb-2">
			         <div class="gray-xs-f mb-xs">Selected Image <span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="JPEG / PNG <br> Recommended Size: <br>Min: 90x90 Pixels<br>Max: 120x120 Pixels<br>(Maintain aspect ratio for the selected size of the image)"></span> </div>
			      </div>
			      <div class="col-md-3 pl-none">
			         <div class="gray-xs-f mb-xs">Display Text <span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="1 to 100 characters"></span></div>
			      </div>
			      <div class="col-md-3 col-lg-3 pl-none">
			         <div class="gray-xs-f mb-xs">Value <span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" data-html="true" title="1 to 50 characters"></span></div>
			      </div>
			      
			      <div class="col-md-2 pl-none">
			         <div class="gray-xs-f mb-xs">&nbsp;</div>
			      </div>
			   </div>
			</div>
			<div class="ImageChoiceContainer">
				<c:choose>
				<c:when test="${questionsBo.responseType eq 5 && fn:length(questionsBo.questionResponseSubTypeList) gt 1}">
					<c:forEach items="${questionsBo.questionResponseSubTypeList}" var="questionResponseSubType" varStatus="subtype">
						<div class="image-choice row" id="${subtype.index}">
						   <input type="hidden" class="form-control" id="imageChoiceSubTypeValueId${subtype.index}" name="questionResponseSubTypeList[${subtype.index}].responseSubTypeValueId" value="${questionResponseSubType.responseSubTypeValueId}">
						   <div class="col-md-2 pl-none col-smthumb-2">
						      <div class="form-group">
						         <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
						            <div class="thumb-img">
						            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionResponseSubType.image)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
						            </div>
						            <div class="textLabelimagePathId${subtype.index}">Change</div>
						         </div>
						         <input class="dis-none upload-image <c:if test="${empty questionResponseSubType.image}">ImageChoiceRequired</c:if>" data-imageId='${subtype.index}' name="questionResponseSubTypeList[${subtype.index}].imageFile" id="imageFileId${subtype.index}" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);" value="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionResponseSubType.image)}">
						         <input type="hidden" name="questionResponseSubTypeList[${subtype.index}].image" id="imagePathId${subtype.index}" value="${questionResponseSubType.image}">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-2 pl-none col-smthumb-2">
						      <div class="form-group">
						         <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
						            <div class="thumb-img">
						            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionResponseSubType.selectedImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
						            </div>
						            <div class="textLabelselectImagePathId${subtype.index}">Change</div>
						         </div>
						         <input class="dis-none upload-image <c:if test="${empty questionResponseSubType.selectedImage}">ImageChoiceRequired</c:if>" data-imageId='${subtype.index}' name="questionResponseSubTypeList[${subtype.index}].selectImageFile" id="selectImageFileId${subtype.index}" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
						         <input type="hidden" name="questionResponseSubTypeList[${subtype.index}].selectedImage" id="selectImagePathId${subtype.index}" value="${questionResponseSubType.selectedImage}">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-3 pl-none">
						      <div class="form-group">
						         <input type="text" class="form-control ImageChoiceRequired" name="questionResponseSubTypeList[${subtype.index}].text" id="displayImageChoiceText${subtype.index}" value="${fn:escapeXml(questionResponseSubType.text)}" maxlength="100">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   <div class="col-md-3 col-lg-3 pl-none">
						      <div class="form-group">
						         <input type="text" class="form-control ImageChoiceRequired imageChoiceVal" name="questionResponseSubTypeList[${subtype.index}].value" id="displayImageChoiceValue${subtype.index}" value="${fn:escapeXml(questionResponseSubType.value)}"maxlength="50">
						         <div class="help-block with-errors red-txt"></div>
						      </div>
						   </div>
						   
						   <div class="col-md-2 pl-none mt__6">
						      <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addImageChoice();'>+</span>
							  <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0" onclick='removeImageChoice(this);'></span>
						   </div>
						</div>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<div class="image-choice row" id="0">
					   <div class="col-md-2 pl-none col-smthumb-2">
					      <div class="form-group">
					         <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
					            <div class="thumb-img">
					            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].image)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
					            </div>
					            <c:if test="${empty questionsBo.questionResponseSubTypeList[0].image}"><div class="textLabelimagePathId0">Upload</div></c:if>
					            <c:if test="${not empty questionsBo.questionResponseSubTypeList[0].image}"><div class="textLabelimagePathId0">Change</div></c:if>
					         </div>
					         <input class="dis-none upload-image <c:if test="${empty questionsBo.questionResponseSubTypeList[0].image}">ImageChoiceRequired</c:if>" data-imageId='0' name="questionResponseSubTypeList[0].imageFile" id="imageFileId0" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
					         <input type="hidden" name="questionResponseSubTypeList[0].image" id="imagePathId0" value="${questionsBo.questionResponseSubTypeList[0].image}">
					         <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   <div class="col-md-2 pl-none col-smthumb-2">
					      <div class="form-group">
					         <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
					            <div class="thumb-img">
					            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].selectedImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
					            </div>
					            <c:if test="${empty questionsBo.questionResponseSubTypeList[0].selectedImage}"><div class="textLabelselectImagePathId0">Upload</div></c:if>
					            <c:if test="${not empty questionsBo.questionResponseSubTypeList[0].selectedImage}"><div class="textLabelselectImagePathId0">Change</div></c:if>
					         </div>
					         <input class="dis-none upload-image <c:if test="${empty questionsBo.questionResponseSubTypeList[0].selectedImage}">ImageChoiceRequired</c:if>" data-imageId='0' name="questionResponseSubTypeList[0].selectImageFile" id="selectImageFileId0" type="file"  accept=".png, .jpg, .jpeg" onchange="readURL(this);">
					         <input type="hidden" name="questionResponseSubTypeList[0].selectedImage" id="selectImagePathId0" value="${questionsBo.questionResponseSubTypeList[0].selectedImage}">
					         <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   <div class="col-md-3 pl-none">
					      <div class="form-group">
					         <input type="text" class="form-control ImageChoiceRequired" name="questionResponseSubTypeList[0].text" id="displayImageChoiceText0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].text)}" maxlength="100">
					         <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   <div class="col-md-3 col-lg-3 pl-none">
					      <div class="form-group">
					         <input type="text" class="form-control ImageChoiceRequired imageChoiceVal" name="questionResponseSubTypeList[0].value" id="displayImageChoiceValue0" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[0].value)}" maxlength="50">
					         <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   
					   <div class="col-md-2 pl-none mt__6">
					      <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addImageChoice();'>+</span>
						  <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0" onclick='removeImageChoice(this);'></span>
					   </div>
					</div>
					<div class="image-choice row" id="1">
					   <div class="col-md-2 pl-none col-smthumb-2">
					      <div class="form-group">
					         <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
					            <div class="thumb-img">
					             <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].image)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
					            </div>
					            <c:if test="${empty questionsBo.questionResponseSubTypeList[1].image}"><div class="textLabelimagePathId1">Upload</div></c:if>
					            <c:if test="${not empty questionsBo.questionResponseSubTypeList[1].image}"><div class="textLabelimagePathId1">Change</div></c:if>
					         </div>
					          <input  class="dis-none upload-image <c:if test="${empty questionsBo.questionResponseSubTypeList[1].image}">ImageChoiceRequired</c:if>" type="file"   data-imageId='1' accept=".png, .jpg, .jpeg" name="questionResponseSubTypeList[1].imageFile" id="imageFileId1" onchange="readURL(this);">
					          <input type="hidden" name="questionResponseSubTypeList[1].image" id="imagePathId1" value="${questionsBo.questionResponseSubTypeList[1].image}">
					          <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   <div class="col-md-2 pl-none col-smthumb-2">
					      <div class="form-group">
					         <div class="sm-thumb-btn" onclick="openUploadWindow(this);">
					            <div class="thumb-img">
					            <img src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />questionnaire/${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].selectedImage)}" onerror="this.src='/fdahpStudyDesigner/images/icons/sm-thumb.jpg';" class="imageChoiceWidth"/>
					            </div>
					            <c:if test="${empty questionsBo.questionResponseSubTypeList[1].selectedImage}"><div class="textLabelselectImagePathId1">Upload</div></c:if>
					            <c:if test="${not empty questionsBo.questionResponseSubTypeList[1].selectedImage}"><div class="textLabelselectImagePathId1">Change</div></c:if>
					         </div>
					          <input  class="dis-none upload-image <c:if test="${empty questionsBo.questionResponseSubTypeList[1].selectedImage}">ImageChoiceRequired</c:if>" type="file"  data-imageId='1' accept=".png, .jpg, .jpeg" name="questionResponseSubTypeList[1].selectImageFile" id="selectImageFileId1" onchange="readURL(this);">
					          <input type="hidden" name="questionResponseSubTypeList[1].selectedImage" id="selectImagePathId1" value="${questionsBo.questionResponseSubTypeList[1].selectedImage}">
					          <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   <div class="col-md-3 pl-none">
					      <div class="form-group">
					         <input type="text" class="form-control ImageChoiceRequired" name="questionResponseSubTypeList[1].text" id="displayImageChoiceText1" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].text)}" maxlength="100">
					          <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   <div class="col-md-3 col-lg-3 pl-none">
					      <div class="form-group">
					          <input type="text" class="form-control ImageChoiceRequired imageChoiceVal" name="questionResponseSubTypeList[1].value" id="displayImageChoiceValue1" value="${fn:escapeXml(questionsBo.questionResponseSubTypeList[1].value)}" maxlength="50">
					          <div class="help-block with-errors red-txt"></div>
					      </div>
					   </div>
					   
					   <div class="col-md-2 pl-none mt__6">
					      <span class="addBtnDis addbtn mr-sm align-span-center top6" onclick='addImageChoice();'>+</span>
						  <span class="delete vertical-align-middle remBtnDis hide pl-md align-span-center top0" onclick='removeImageChoice(this);'></span>
					   </div>
					</div> 
				</c:otherwise>
				</c:choose>
			</div>
          </div>
          </div>
         </div>
      </div>
   </div>
   </form:form>
</div>
<!-- End right Content here -->
<script type="text/javascript">
$(document).ready(function(){
	
	if($('#useAnchorDateId').is(':checked')){
		$("#anchorTextId").attr('required',true);
	}else{
		$('.useAnchorDateName').hide();
		$("#anchorTextId").attr('required',false);
		$("#anchorTextId").parent().removeClass("has-danger").removeClass("has-error");
	    $("#anchorTextId").parent().find(".help-block").html("");
	}
	
	$('#useAnchorDateId').click(function() {	
		if ($(this).is(':checked')){
			$('.useAnchorDateName').show();
			$("#anchorTextId").attr('required',true);
		}else{
			$('.useAnchorDateName').hide();
			$("#anchorTextId").attr('required',false);
			$("#anchorTextId").parent().removeClass("has-danger").removeClass("has-error");
 	        $("#anchorTextId").parent().find(".help-block").html("");
		}
	});
	
	$("#anchorTextId").blur(function(){
		validateAnchorDateText('',function(val){});
    });
	
	<c:if test="${actionTypeForFormStep == 'view'}">
		$('#questionStepId input,textarea ').prop('disabled', true);
		$('#questionStepId select').addClass('linkDis');
		$('.addBtnDis, .remBtnDis').addClass('dis-none');
		$(".removeImageId").css("visibility","hidden");
	</c:if>
	
	$(".menuNav li.active").removeClass('active');
	$(".sixthQuestionnaires").addClass('active');
     $("#doneId").click(function(){
    	 $("#doneId").attr("disabled",true);
    	 var isValid = true;
    	 var isImageValid = true;
		 var resType = $("#rlaResonseType").val();
		 var anchorDateFlag = true;
		 if(resType == 'Text Scale' || resType == 'Image Choice' || resType == 'Value Picker' || resType == 'Text Choice'){
			 validateForUniqueValue('',resType,function(val){if(val){}});
		 }
    	 if(resType == "Scale"){
    		 $("#displayStepsCount").trigger('blur');
    		 $("#scaleMinValueId").trigger('blur');
    		 $("#scaleMaxValueId").trigger('blur');
    		 $("#scaleDefaultValueId").trigger('blur');
    	 }else if(resType == "Continuous Scale"){
    		 $("#continuesScaleMinValueId").trigger('blur');
    		 $("#continuesScaleMaxValueId").trigger('blur');
    		 $("#continuesScaleDefaultValueId").trigger('blur');
    		 validateFractionDigits($("#continuesScaleFractionDigitsId"));
    	 }else if(resType == "Numeric"){
    		 $("#numericMinValueId").trigger('blur');
    		 $("#numericMaxValueId").trigger('blur');
    	 }
    	 if(isFromValid("#questionStepId")){
    	  $("body").addClass("loading");
   		  var placeholderText ='';
   		  var stepText = "";
   		  if(resType == "Email"){
   				 placeholderText = $("#placeholderId").val();	
   		  }else if(resType == "Text"){
   				placeholderText = $("#textPlaceholderId").val(); 
   		  }else if(resType == "Height"){
   				placeholderText = $("#heightPlaceholderId").val();
   		  }else if(resType == "Numeric"){
   				placeholderText = $("#numericPlaceholderId").val(); 
   				var minValue =$("#numericMinValueId").val();
	   			var maxValue = $("#numericMaxValueId").val();
				if((minValue != '' && maxValue != '') || (minValue == '' && maxValue == '')){
	   				isValid = true;
	   			  }else{
	   				  if(maxValue == ''){
	   					$("#numericMaxValueId").parent().addClass("has-danger").addClass("has-error");
                        $("#numericMaxValueId").parent().find(".help-block").empty();
                        $("#numericMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
	   				  }
	     			  if(minValue == ''){
	     				 $("#numericMinValueId").parent().addClass("has-danger").addClass("has-error");
                         $("#numericMinValueId").parent().find(".help-block").empty();
                         $("#numericMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");  
	     			  }
	   				isValid = false;
	   				$("#doneId").attr("disabled",false);
	   				$("body").removeClass("loading");
	   			  }
   		  }else if(resType == "Time interval"){
   			  stepText = $("#timeIntervalStepId").val();
   		  }else if(resType == "Scale" || resType == "Continuous Scale"){
   			  stepText =  $("#scaleStepId").val();
   				var minValue =''
   				var maxValue = ''
	   			if(resType == "Continuous Scale"){
	   				 minValue = $("#continuesScaleMinDescriptionId").val();
	     			 maxValue = $("#continuesScaleMaxDescriptionId").val();
	   			}else{
	   				 minValue = $("#scaleMinDescriptionId").val();
	     			 maxValue = $("#scaleMaxDescriptionId").val();
	   			}
   			  if((minValue != '' && maxValue != '') || (minValue == '' && maxValue == '')){
   				isValid = true;
   			  }else{
   				  if(maxValue == ''){
   					if(resType == "Continuous Scale"){
   					 	$("#continuesScaleMaxDescriptionId").parent().addClass("has-danger").addClass("has-error");
                     	$("#continuesScaleMaxDescriptionId").parent().find(".help-block").empty();
                        $("#continuesScaleMaxDescriptionId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
                    }else{
                    	$("#scaleMaxDescriptionId").parent().addClass("has-danger").addClass("has-error");
                        $("#scaleMaxDescriptionId").parent().find(".help-block").empty();
                        $("#scaleMaxDescriptionId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>"); 
                    }
   				  }
     			  if(minValue == ''){
     				 if(resType == "Continuous Scale"){
       					 $("#continuesScaleMinDescriptionId").parent().addClass("has-danger").addClass("has-error");
                         $("#continuesScaleMinDescriptionId").parent().find(".help-block").empty();
                         $("#continuesScaleMinDescriptionId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
                     }else{
                    	 $("#scaleMinDescriptionId").parent().addClass("has-danger").addClass("has-error");
                         $("#scaleMinDescriptionId").parent().find(".help-block").empty();
                         $("#scaleMinDescriptionId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");   
                     }  
     			  }
   				isValid = false;
   				$("#doneId").attr("disabled",false);
   				$("body").removeClass("loading");
   			  }
	   			var minImagePath = '';
	   			var maxImagePath = '';
	   			var minImageFile='';
	   			var maxImageFile='';
	   			if(resType == "Continuous Scale"){
	   				 minImagePath = $("#continuesScaleMinImagePathId").val();
	   				 maxImagePath = $("#continuesScaleMaxImagePathId").val();
	     			 minImageFile = document.getElementById("continuesScaleMinImageFileId").files[0];
	     			 maxImageFile = document.getElementById("continuesScaleMaxImageFileId").files[0];
	   			}else{
	   				minImagePath = $("#scaleMinImagePathId").val();
	   				maxImagePath = $("#scaleMaxImagePathId").val();
	   				minImageFile = document.getElementById("scaleMinImageFileId").files[0];
	     			maxImageFile = document.getElementById("scaleMaxImageFileId").files[0];
	   			}
	   			if(minImagePath == '' && maxImagePath == '' && ((typeof minImageFile == 'undefined' && typeof maxImageFile == 'undefined') ||  (minImageFile == null && maxImageFile == null))){
	   				isImageValid = true;
	   			}else if (((minImageFile != null && typeof minImageFile != 'undefined') || minImagePath != '') && ((maxImageFile != null && typeof maxImageFile != 'undefined') || maxImagePath != '')){
	   				isImageValid = true;
	   			}else{
	   				if(maxImagePath == '' && (maxImageFile == '' || typeof maxImageFile == 'undefined' || maxImageFile == null)){
	   					if(resType == "Continuous Scale"){
	   					 	$("#continuesScaleMaxImagePathId").parent().addClass("has-danger").addClass("has-error");
	                     	$("#continuesScaleMaxImagePathId").parent().find(".help-block").empty();
	                        $("#continuesScaleMaxImagePathId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
	                    }else{
	                    	$("#scaleMaxImagePathId").parent().addClass("has-danger").addClass("has-error");
	                        $("#scaleMaxImagePathId").parent().find(".help-block").empty();
	                        $("#scaleMaxImagePathId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>"); 
	                    }
	   				  }
	     			  if(minImagePath == '' && (minImageFile == '' || typeof minImageFile == 'undefined' || minImageFile == null)){
	     				 if(resType == "Continuous Scale"){
	       					 $("#continuesScaleMinImagePathId").parent().addClass("has-danger").addClass("has-error");
	                         $("#continuesScaleMinImagePathId").parent().find(".help-block").empty();
	                         $("#continuesScaleMinImagePathId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
	                     }else{
	                    	 $("#scaleMinImagePathId").parent().addClass("has-danger").addClass("has-error");
	                         $("#scaleMinImagePathId").parent().find(".help-block").empty();
	                         $("#scaleMinImagePathId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");   
	                     }  
	     			  }
	     			 isImageValid = false;
	   				$("#doneId").attr("disabled",false);
	   				$("body").removeClass("loading");
	   		 }
   		  }else if(resType == 'Text Scale'){
   			  var count = $('.text-scale').length;
   			  stepText =  $("#textScalePositionId").val();
			  if(stepText != ''){
				  if(stepText != '' && stepText >= 1 && stepText <= count){
	 	    			 isValid = true;
	 	    	  }else{
	 	    			 isValid  = false;
	 	    			$("#textScalePositionId").focus();
	 	    			 stepText="";
	 	    	   }	  
			  }else{
				  isValid = true;
			  }
		  }else if(resType == 'Date'){
			  var skiappable = $('input[name="skippable"]:checked').val();
			  var anchorText = $("#anchorTextId").val();
			  var anchorDateUsed = $('#useAnchorDateId').is(':checked');
			  if(anchorDateUsed && anchorText != '' && anchorText != null && typeof anchorText != 'undefined'){
				    $("#anchorTextId,#useAnchorDateId").attr("disabled",false);
				    validateAnchorDateText('',function(val){});
				    if(skiappable == 'Yes')
						anchorDateFlag = false;
			  }
		  }
   		    $("#placeholderTextId").val(placeholderText);
   		    $("#stepValueId").val(stepText);
	   		if(isValid && isImageValid){
	   			validateTheQuestionshortTitle('',function(val){
	   				if(val){
	   					var statShortName =  $("#statShortNameId").val();
	   			       	if(statShortName != '' && statShortName != null && typeof statShortName != 'undefined'){
	   			       			validateStatsShorTitle('',function(val){
	   			       				if(val){
	   			       					if(resType != '' && resType != null && resType != 'undefined'){
			   						    	 $("#responseTypeId > option").each(function() {
			   							    	 var textVal = this.text.replace(/\s/g, '');
			   						   			 if(resType.replace(/\s/g, '') == textVal){
			   						   			 }else{
			   						   				 $("#"+textVal).empty();
			   						   			 }    
			   						   		 });
			   						 	}
	   			       					document.questionStepId.submit();
	   			       				}else{
		   			       				 $("#doneId").attr("disabled",false);
				    	    			 $("body").removeClass("loading");
	   			       				}
	   			       			});
	   			       	 }else{
				   			   if(resType != '' && resType != null && resType != 'undefined'){
				   			    	 $("#responseTypeId > option").each(function() {
				   				    	 var textVal = this.text.replace(/\s/g, '');
				   			   			 if(resType.replace(/\s/g, '') == textVal){
				   			   			 }else{
				   			   				 $("#"+textVal).empty();
				   			   			 }    
				   			   		 });
				   			 	}
				   			 if(anchorDateFlag){
				    			 document.questionStepId.submit();	
		    				 }else{
		    					 $("body").removeClass("loading");
		    					 $("#doneId").attr("disabled",false);
		    					 bootbox.confirm({
		    							closeButton: false,
		    							message : "This question provides an Anchor Date response element, but has been marked Skippable. Are you sure you wish to proceed?",	
		    						    buttons: {
		    						        'cancel': {
		    						            label: 'Cancel',
		    						        },
		    						        'confirm': {
		    						            label: 'OK',
		    						        },
		    						    },
		    						    callback: function(result) {
		    						        if (result) {
		    						        	   document.questionStepId.submit();	
		    						             }	
		    						        }
		    						    })
		    				 }
	   			       	 }
	   				}else{
		   				 $("#doneId").attr("disabled",false);
		    			 $("body").removeClass("loading");
	   				}
	   			});
	   			
	   		}else{
	   			$("#doneId").attr("disabled",false);
	   		    $("body").removeClass("loading");
	   			var qlaCount = $('#qla').find('.has-error.has-danger').length;
				var rlaCount = $('#rla').find('.has-error.has-danger').length;
				if(parseInt(qlaCount) >= 1){
					 $('.questionLevel a').tab('show');
				}else if(parseInt(rlaCount) >= 1){
					 $('.responseLevel a').tab('show');
				}
				$("#rla").find(".has-error:first").find('input').focus();
	   		}
	   		
		}else{
			$("#doneId").attr("disabled",false);
			var qlaCount = $('#qla').find('.has-error.has-danger').length;
			var rlaCount = $('#rla').find('.has-error.has-danger').length;
			if(parseInt(qlaCount) >= 1){
				 $('.questionLevel a').tab('show');
			}else if(parseInt(rlaCount) >= 1){
				 $('.responseLevel a').tab('show');
			}	
		}
     });
     $("#saveId").on('click',function(e){
    	 $("body").addClass("loading");
    	 validateTheQuestionshortTitle('',function(val){
				if(val){
					var statShortName =  $("#statShortNameId").val();
			       	if(statShortName != '' && statShortName != null && typeof statShortName != 'undefined'){
			       			validateStatsShorTitle('',function(val){
			       				if(val){
			       				 saveQuestionStepQuestionnaire();
			       				}else{
			       					$("body").removeClass("loading");
			       				}
			       			});
			       	 }else{
			       		 var resType = $("#rlaResonseType").val();
	    	    		 if(resType == 'Text Scale' || resType == 'Image Choice' || resType == 'Value Picker' || resType == 'Text Choice'){
	    	    			 validateForUniqueValue('',resType,function(val){
	    	    				if(val){
	    	    					 saveQuestionStepQuestionnaire();
	    	    				 }else{
	    	    					 $("body").removeClass("loading");
	    	    				 }
	    	    			 });
	    	    		 }else{
	    	    			 saveQuestionStepQuestionnaire();
	    	    		 }
			       	 }
				}else{
					$("body").removeClass("loading");
				}
		});
     });
     $("#statShortNameId").blur(function(){
    	 validateStatsShorTitle('',function(val){});
     });
     $(".responseLevel ").on('click',function(){
    	var reponseType = $("#responseTypeId").val();
    	if(reponseType != '' && reponseType !='' && typeof reponseType != 'undefined'){
    		$("#responseTypeDivId").show();
    	}else{
    		$("#responseTypeDivId").hide();
    	}
     });
     $("#continuesScaleMaxDescriptionId,#continuesScaleMinDescriptionId,#scaleMinDescriptionId,#scaleMaxDescriptionId").on("change",function(){
    	 $(this).validator('validate');
         $(this).parent().removeClass("has-danger").removeClass("has-error");
         $(this).parent().find(".help-block").empty();
     });
     $("#scaleMinValueId,#scaleMaxValueId").on("change",function(){
    	if($(this).val() != ''){
    		$("#scaleStepId").val('');
    		$("#scaleDefaultValueId").val('');
    		$("#displayStepsCount").val('');
    	} 
     });
     $("#continuesScaleMinValueId,#continuesScaleMaxValueId").on("change",function(){
      	if($(this).val() != ''){
      		$("#continuesScaleDefaultValueId").val('');
      		$("#continuesScaleFractionDigitsId").val('');
      	} 
      	$("#continuesScaleDefaultValueId").parent().removeClass("has-danger").removeClass("has-error");
        $("#continuesScaleDefaultValueId").parent().find(".help-block").empty();
        $("#continuesScaleFractionDigitsId").parent().removeClass("has-danger").removeClass("has-error");
        $("#continuesScaleFractionDigitsId").parent().find(".help-block").empty();
     });
     $("#displayStepsCount").on("change",function(){
    	 if($(this).val() != ''){
    		 $("#scaleDefaultValueId").val('');
    	 }
         $("#scaleDefaultValueId").parent().removeClass("has-danger").removeClass("has-error");
         $("#scaleDefaultValueId").parent().find(".help-block").empty();
     });
     $("#addLineChart").on('change',function(){
    	if($(this).is(":checked")){
    		$(this).val("Yes");
    		$("#chartContainer").show();
    		$(".chartrequireClass").attr('required',true);
    		$('.selectpicker').selectpicker('refresh');
    	} else{
    		$(this).val("No");
    		$("#chartContainer").hide();
    		$(".chartrequireClass").attr('required',false);
    		$("#lineChartTimeRangeId").val('');
    		$('#chartTitleId').val('');
    		$('.selectpicker').selectpicker('refresh');
    		 document.getElementById("allowRollbackChartNo").checked = true;
    	}
     });
     $("#allowHealthKit").on('change',function(){
    	
    	 if($(this).is(":checked")){
     		$(this).val("Yes");
     		$("#healthKitContainerId").show();
     		$(".healthkitrequireClass").attr('required',true);
     		$('.selectpicker').selectpicker('refresh');
     	} else{
     		$(this).val("No");
     		$("#healthKitContainerId").hide();
     		$(".healthkitrequireClass").attr('required',false);
     		$("#healthkitDatatypeId").val('');
     		$('.selectpicker').selectpicker('refresh');
     	}
     });
    $("#useStasticData").on('change',function(){
    	if($(this).is(":checked")){
    		$(this).val("Yes");
    		$("#statContainer").show();
    		$(".requireClass").attr('required',true);
    		$('.selectpicker').selectpicker('refresh');
    	} else{
    		$(this).val("No");
    		$("#statContainer").hide();
    		$(".requireClass").attr('required',false);
    		$("#statShortNameId").val('');
    		$("#statDisplayNameId").val('');
    		$("#statDisplayUnitsId").val('');
    		$("#statTypeId").val('');
    		$("#statFormula").val('');
    		$('.selectpicker').selectpicker('refresh');
    	}
    });
    $("#scaleMinValueId").blur(function(){
    	var value= $("#scaleMinValueId").val();
    	var maxValue = $("#scaleMaxValueId").val();
    	$("#scaleMinValueId").parent().removeClass("has-danger").removeClass("has-error");
        $("#scaleMinValueId").parent().find(".help-block").empty();
    	if(maxValue != ''){
    		if(value != ''){
	    		if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
	    			if(parseInt(value)+1 > parseInt(maxValue)){
	            		$("#scaleMinValueId").val('');
	           		    $("#scaleMinValueId").parent().addClass("has-danger").addClass("has-error");
	                    $("#scaleMinValueId").parent().find(".help-block").empty();
	                    $("#scaleMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min, 10000)</li></ul>");
	            	}else{
	            		$("#scaleMinValueId").parent().removeClass("has-danger").removeClass("has-error");
	                    $("#scaleMinValueId").parent().find(".help-block").empty();
	            	}
	    		}else{
	        		$("#scaleMinValueId").val('');
	       		    $("#scaleMinValueId").parent().addClass("has-danger").addClass("has-error");
	                $("#scaleMinValueId").parent().find(".help-block").empty();
	                $("#scaleMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min, 10000) </li></ul>");
	        	}
    		}
    	}else{
    		if(value != ''){
    			if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
            		$("#scaleMinValueId").parent().removeClass("has-danger").removeClass("has-error");
                    $("#scaleMinValueId").parent().find(".help-block").empty();
            	}else{
            		$("#scaleMinValueId").val('');
           		    $("#scaleMinValueId").parent().addClass("has-danger").addClass("has-error");
                    $("#scaleMinValueId").parent().find(".help-block").empty();
                    $("#scaleMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min, 10000) </li></ul>");
            	}	
    		}
    	}
    });
    $("#scaleMaxValueId").blur(function(){
    	var value= $("#scaleMaxValueId").val();
    	var minValue = $("#scaleMinValueId").val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
    	if(minValue != ''){
    		if(value != ''){
	    		if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
	    			if(parseInt(value) >= parseInt(minValue)+1 && parseInt(value) <= 10000){
	            		$("#scaleMaxValueId").parent().removeClass("has-danger").removeClass("has-error");
	                    $("#scaleMaxValueId").parent().find(".help-block").empty();
	        		}else if(parseInt(value) < parseInt(minValue)+1){
	        			$("#scaleMaxValueId").val('');
	           		    $("#scaleMaxValueId").parent().addClass("has-danger").addClass("has-error");
	                    $("#scaleMaxValueId").parent().find(".help-block").empty();
	                    $("#scaleMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min+1, 10000)</li></ul>");
	        		}
	        	}else{
	        		$("#scaleMaxValueId").val('');
	       		    $("#scaleMaxValueId").parent().addClass("has-danger").addClass("has-error");
	                $("#scaleMaxValueId").parent().find(".help-block").empty();
	                $("#scaleMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min+1, 10000) </li></ul>");
	        	}
    		}
    	}else{
    		if(value != ''){
	    		if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
	        		$("#scaleMaxValueId").parent().removeClass("has-danger").removeClass("has-error");
	                $("#scaleMaxValueId").parent().find(".help-block").empty();
	        	}else{
	        		$("#scaleMaxValueId").val('');
	       		    $("#scaleMaxValueId").parent().addClass("has-danger").addClass("has-error");
	                $("#scaleMaxValueId").parent().find(".help-block").empty();
	                $("#scaleMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min+1, 10000) </li></ul>");
	        	}
    		}
    	}
    });
    
    $("#displayStepsCount").blur(function(){
    	var value= $("#displayStepsCount").val();
    	var minValue = $("#scaleMinValueId").val();
    	var maxValue = $("#scaleMaxValueId").val();
    	$("#displayStepsCount").parent().removeClass("has-danger").removeClass("has-error");
        $("#displayStepsCount").parent().find(".help-block").empty();
    	if(value != '' && minValue != '' && maxValue != ''){
    			var diff = parseInt(maxValue)-parseInt(minValue);
    			var displayStepsCount = "";
    			var stepsCount = (parseInt(diff)/parseInt(value)); 
    			if((parseInt(diff)%parseInt(value)) == 0){
    	            displayStepsCount = parseInt(stepsCount);
    	           
    	            if(parseInt(stepsCount) >= 1 && parseInt(stepsCount) <= 13){
        	    		$("#displayStepsCount").parent().removeClass("has-danger").removeClass("has-error");
        	            $("#displayStepsCount").parent().find(".help-block").empty();
        	            $("#scaleStepId").val(displayStepsCount);
    	            }else{
    	            	 $("#scaleStepId").val('');
    	            	 $("#displayStepsCount").val('');
        	    		 $("#displayStepsCount").parent().addClass("has-danger").addClass("has-error");
        	             $("#displayStepsCount").parent().find(".help-block").empty();
        	             if(parseInt(stepsCount) < 1){
        	            	 $("#displayStepsCount").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter  a smaller step size.</li></ul>");
        	             }else{
        	            	 $("#displayStepsCount").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter a larger step size.</li></ul>"); 
        	             }
        	             
    	            }
    			}else{
    				 $("#displayStepsCount").val('');
    				 $("#scaleStepId").val('');
    	    		 $("#displayStepsCount").parent().addClass("has-danger").addClass("has-error");
    	             $("#displayStepsCount").parent().find(".help-block").empty();
    	             $("#displayStepsCount").parent().find(".help-block").append("<ul class='list-unstyled'><li>(Max-Min) value should be exactly divisisble by the step size.</li></ul>");
    			}
    	}
    });
    $("#timeIntervalStepId").blur(function(){
    	var value= $(this).val();
    	var selectedValue = [1,2,3,4,5,6,10,12,15,20,30];
    	if(selectedValue.indexOf(parseInt(value)) != -1){
    		$(this).parent().removeClass("has-danger").removeClass("has-error");
            $(this).parent().find(".help-block").empty();
            $(this).validator('validate');
            $('#timeIntervalDefaultId').val('');
            if(parseInt(value) <=6){
            	 $('#timeIntervalDefaultId').val('00:0'+value);
            }else{
            	 $('#timeIntervalDefaultId').val('00:'+value);
            }
            $('#timeIntervalDefaultId').data('DateTimePicker').stepping(parseInt(value));
    	}else{
    	     $(this).val('');
    		 $(this).parent().addClass("has-danger").addClass("has-error");
             $(this).parent().find(".help-block").empty();
             $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Please select a number from the following set (1,2,3,4,5,6,10,12,15,20 & 30).</li></ul>");
    	}
    });
    var dt = new Date();
    $('#timeIntervalDefaultId').datetimepicker({
		format: 'HH:mm',
		stepping: 1,
		useCurrent: false,
		minDate : new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 00, 01),
		maxDate : new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 23, 59)
     }).on("dp.change", function (e) {
		var durationTime = $('#timeIntervalDefaultId').val();
		if(durationTime && durationTime == '00:00'){
			durationFlag = false;
			$('#timeIntervalDefaultId').parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>Please select a non-zero Duration value.</li></ul>');
		}else{
			durationFlag = true;
			$('#timeIntervalDefaultId').parent().find(".help-block").empty();
			var dt = new Date();
			$('#timeIntervalDefaultId').datetimepicker({format: 'HH:mm',stepping: 1,
				useCurrent: false,
		 		minDate : new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 00, 01),
				maxDate : new Date(dt.getFullYear(), dt.getMonth(), dt.getDate(), 23, 59)});
		}
     });
    $("#textScalePositionId").blur(function(){
    	var count = $('.text-scale').length;
    	var value= $(this).val();
    	if(value !=''){
    		if(value >= 1 && value <= count){
        		$("#textScalePositionId").parent().removeClass("has-danger").removeClass("has-error");
                $("#textScalePositionId").parent().find(".help-block").empty();
        	}else{
        	     
        		 $("#textScalePositionId").parent().addClass("has-danger").addClass("has-error");
                 $("#textScalePositionId").parent().find(".help-block").empty();
                 $("#textScalePositionId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter choice from 1 to number of choices </li></ul>");
        	}
    	}
    });
    $("#scaleDefaultValueId").blur(function(){
    	var value= $("#scaleDefaultValueId").val();
		var stepSize = $("#scaleStepId").val();
		$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
		if(value != '' && stepSize != ''){
			if(parseInt(value) >= 0 && parseInt(value) <= parseInt(stepSize)){
	    		$("#scaleDefaultValueId").parent().removeClass("has-danger").removeClass("has-error");
	            $("#scaleDefaultValueId").parent().find(".help-block").empty();
			}else{
				 $("#scaleDefaultValueId").val('');
	    		 $("#scaleDefaultValueId").parent().addClass("has-danger").addClass("has-error");
	             $("#scaleDefaultValueId").parent().find(".help-block").empty();
	             $("#scaleDefaultValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer from 0 to number of steps</li></ul>");
			}
		}else{
			if(value != ''){
				$("#scaleDefaultValueId").val('');
	   		    $("#scaleDefaultValueId").parent().addClass("has-danger").addClass("has-error");
	            $("#scaleDefaultValueId").parent().find(".help-block").empty();
	            $("#scaleDefaultValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an step size first </li></ul>");
			}
		}
    });
    $("#continuesScaleMinValueId").blur(function(){
    	var value= $("#continuesScaleMinValueId").val();
    	var maxValue = $("#continuesScaleMaxValueId").val();
    	$("#continuesScaleMinValueId").parent().removeClass("has-danger").removeClass("has-error");
        $("#continuesScaleMinValueId").parent().find(".help-block").empty();
    	if(maxValue != ''){
    		if(value != ''){
	    		if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
	    			if(parseInt(value)+1 > parseInt(maxValue)){
	            		$("#continuesScaleMinValueId").val('');
	           		    $("#continuesScaleMinValueId").parent().addClass("has-danger").addClass("has-error");
	                    $("#continuesScaleMinValueId").parent().find(".help-block").empty();
	                    $("#continuesScaleMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min, 10000)</li></ul>");
	            	}else{
	            		
	            		$("#continuesScaleMinValueId").parent().removeClass("has-danger").removeClass("has-error");
	                    $("#continuesScaleMinValueId").parent().find(".help-block").empty();
	            	}
	        	}else{
	        		$("#continuesScaleMinValueId").val('');
	       		    $("#continuesScaleMinValueId").parent().addClass("has-danger").addClass("has-error");
	                $("#continuesScaleMinValueId").parent().find(".help-block").empty();
	                $("#continuesScaleMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min, 10000) </li></ul>");
	        	}
    		}
    	}else{
    		if(value != ''){
    			if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
            		$("#continuesScaleMinValueId").parent().removeClass("has-danger").removeClass("has-error");
                    $("#continuesScaleMinValueId").parent().find(".help-block").empty();
            	}else{
            		$("#continuesScaleMinValueId").val('');
           		    $("#continuesScaleMinValueId").parent().addClass("has-danger").addClass("has-error");
                    $("#continuesScaleMinValueId").parent().find(".help-block").empty();
                    $("#continuesScaleMinValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min, 10000) </li></ul>");
            	}
    		}
    	}
    });
    $("#continuesScaleMaxValueId").blur(function(){
    	var value= $("#continuesScaleMaxValueId").val();
    	var minValue = $("#continuesScaleMinValueId").val();
    	$("#continuesScaleMaxValueId").parent().removeClass("has-danger").removeClass("has-error");
        $("#continuesScaleMaxValueId").parent().find(".help-block").empty();
    	if(minValue != ''){
    		if(value != ''){
    			if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
        			if(parseInt(value) >= parseInt(minValue)+1 && parseInt(value) <= 10000){
                		$("#continuesScaleMaxValueId").parent().removeClass("has-danger").removeClass("has-error");
                        $("#continuesScaleMaxValueId").parent().find(".help-block").empty();
            		}else if(parseInt(value) < parseInt(minValue)+1){
            			$("#continuesScaleMaxValueId").val('');
               		    $("#continuesScaleMaxValueId").parent().addClass("has-danger").addClass("has-error");
                        $("#continuesScaleMaxValueId").parent().find(".help-block").empty();
                        $("#continuesScaleMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min+1, 10000)</li></ul>");
            		}
            	}else{
            		$("#continuesScaleMaxValueId").val('');
           		    $("#continuesScaleMaxValueId").parent().addClass("has-danger").addClass("has-error");
                    $("#continuesScaleMaxValueId").parent().find(".help-block").empty();
                    $("#continuesScaleMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min+1, 10000) </li></ul>");
            	}
    		}
    	}else{
    		if(value != ''){
    			if(parseInt(value) >= -10000 && parseInt(value) <= 10000){
            		$("#continuesScaleMaxValueId").parent().removeClass("has-danger").removeClass("has-error");
                    $("#continuesScaleMaxValueId").parent().find(".help-block").empty();
            	}else{
            		$("#continuesScaleMaxValueId").val('');
           		    $("#continuesScaleMaxValueId").parent().addClass("has-danger").addClass("has-error");
                    $("#continuesScaleMaxValueId").parent().find(".help-block").empty();
                    $("#continuesScaleMaxValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer number in the range (Min+1, 10000) </li></ul>");
            	}
    		}
    	}
    });
    $("#continuesScaleDefaultValueId").blur(function(){
    	var value= $("#continuesScaleDefaultValueId").val();
    	var minValue = $("#continuesScaleMinValueId").val();
		var maxValue = $("#continuesScaleMaxValueId").val();
		if(value != ''){
			if(parseInt(value) >= parseInt(minValue) && parseInt(value) <= parseInt(maxValue)){
				$("#continuesScaleDefaultValueId").validator('validate');
	    		$("#continuesScaleDefaultValueId").parent().removeClass("has-danger").removeClass("has-error");
	            $("#continuesScaleDefaultValueId").parent().find(".help-block").empty();
			}else{
				 $("#continuesScaleDefaultValueId").val('');
	    		 $("#continuesScaleDefaultValueId").parent().addClass("has-danger").addClass("has-error");
	             $("#continuesScaleDefaultValueId").parent().find(".help-block").empty();
	             $("#continuesScaleDefaultValueId").parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an integer between the minimum and maximum  </li></ul>");
			}
		}
    });
    $("#numericMinValueId").blur(function(){
    	var value= $(this).val();
    	var maxValue = $("#numericMaxValueId").val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
        if(maxValue != ''){
        	if(parseInt(value) >= parseInt(maxValue)){
        		$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an value number less than Maximum</li></ul>");
        	}else{
        		$(this).parent().removeClass("has-danger").removeClass("has-error");
                $(this).parent().find(".help-block").empty();
        	}
        }
    });
    $("#numericMaxValueId").blur(function(){
    	var value= $(this).val();
    	var minValue = $("#numericMinValueId").val();
    	$(this).parent().removeClass("has-danger").removeClass("has-error");
        $(this).parent().find(".help-block").empty();
        if(minValue != ''){
        	if(parseInt(value) <= parseInt(minValue)){
        		$(this).val('');
       		    $(this).parent().addClass("has-danger").addClass("has-error");
                $(this).parent().find(".help-block").empty();
                $(this).parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an value number greater than Minimum</li></ul>");
        	}else{
        		$(this).parent().removeClass("has-danger").removeClass("has-error");
                $(this).parent().find(".help-block").empty();
        	}
        }
    });
    var responseTypeId= '${questionsBo.responseType}';
    if(responseTypeId != null && responseTypeId !='' && typeof responseTypeId != 'undefined'){
    	 getResponseType(responseTypeId);
    }
    $("#responseTypeId").on("change",function(){
    	var value= $(this).val();
    	
    	 getResponseType(value);
    });
    $('.DateStyleRequired').on("change",function(){
    	var value= $(this).val();
    	setResponseDate(value);
    	
    });
    $('.DateRangeRequired').on("change",function(){
    	var value= $(this).val();
    	if(value == 'Custom'){
    		$("#customDateContainerId").show();
    	}else{
    		$("#customDateContainerId").hide();
    		$("#defaultDate").data("DateTimePicker").clear();
            $('#maxDateId').data("DateTimePicker").clear();
    		$('#minDateId').data("DateTimePicker").clear();
    	}
    });
    $("#minDateId").on('dp.change',function(e){
        $("#defaultDate").data("DateTimePicker").clear();
        $('#maxDateId').data("DateTimePicker").clear()
    });
    $("#maxDateId").on('dp.change',function(){
    	var minDate = $("#minDateId").val();
        var maxDate = $('#maxDateId').val();
        $("#defaultDate").data("DateTimePicker").clear();
        if(minDate!='' && maxDate!='' && new Date(minDate) >= new Date(maxDate)){
       	    $('#maxDateId').data("DateTimePicker").clear();
        	$('#maxDateId').parent().addClass("has-danger").addClass("has-error");
       	    $('#maxDateId').parent().find(".help-block").html('<ul class="list-unstyled"><li>Max Date and Time Should not be less than or equal Min Date and Time</li></ul>');
        }else{
        	$('#maxDateId').parent().removeClass("has-danger").removeClass("has-error");
            $('#maxDateId').parent().find(".help-block").empty();
            $("#minDateId").parent().removeClass("has-danger").removeClass("has-error");
            $("#minDateId").parent().find(".help-block").empty();
        }
    });
    $("#defaultDate").on('dp.change',function(){
    	var minDate = $("#minDateId").val();
        var maxDate = $('#maxDateId').val();
        var defaultDate = $("#defaultDate").val();
        if(minDate!='' && maxDate!='' && defaultDate != ''){
        	if(new Date(defaultDate) >= new Date(minDate) && new Date(defaultDate) <= new Date(maxDate)){
        		$('#defaultDate').parent().removeClass("has-danger").removeClass("has-error");
                $('#defaultDate').parent().find(".help-block").empty();
        	}else{
        		$("#defaultDate").data("DateTimePicker").clear();
        		$('#defaultDate').parent().addClass("has-danger").addClass("has-error");
           	    $('#defaultDate').parent().find(".help-block").html('<ul class="list-unstyled"><li>Enter default date to be shown as selected as per availability of Min and Max</li></ul>');
        	}
        }
    });
    $('#scaleMinValueId,#scaleMaxValueId,#scaleDefaultValueId,#textmaxLengthId').bind('input', function(e) {
        var id= $(this).attr('id');
       
    	var str = $("#"+id).val();
        var dec = str.indexOf(".");
        var first_char = str.charAt(0);
        var isNumber = true;
        if (first_char == '-' || !isNaN(first_char)) {
    		    for (i=1; i<str.length; i++) {
        				if(isNaN(str.charAt(i)) && str.charAt(i) != '.') {
            				isNumber = false;
                		break;
            		}
            }
        }
        else {
        		isNumber = false;
        }
        if(dec != -1 && isNumber) {
            str = str.substring(0, str.indexOf("."));
        }
        if (isNumber) {
            $("#"+id).val(str);
        }
        else {
            $("#"+id).val("");
        }
    });
    $("#shortTitle").blur(function(){
    	validateTheQuestionshortTitle(this,function(val){});
    })
    if($('.value-picker').length > 2){
		$('.ValuePickerContainer').find(".remBtnDis").removeClass("hide");
	}else{
		$('.ValuePickerContainer').find(".remBtnDis").addClass("hide");
	}
    if($('.text-scale').length > 2){
		$('.TextScaleContainer').find(".remBtnDis").removeClass("hide");
	}else{
		$('.TextScaleContainer').find(".remBtnDis").addClass("hide");
	}
    if($('.text-choice').length > 2){
		$('.TextChoiceContainer').find(".remBtnDis").removeClass("hide");
	}else{
		$('.TextChoiceContainer').find(".remBtnDis").addClass("hide");
	}
    if($('.image-choice').length > 2){
		$('.ImageChoiceContainer').find(".remBtnDis").removeClass("hide");
	}else{
		$('.ImageChoiceContainer').find(".remBtnDis").addClass("hide");
	}
    $('[data-toggle="tooltip"]').tooltip();
 // File Upload    
    
    openUploadWindow=function(item){
    	$(item).siblings('.upload-image').click();
    }
    var _URL = window.URL || window.webkitURL;

    $(document).on('change', '.upload-image', function(e) {
        var file, img;
        var thisAttr = this;
        var response_type = $("#rlaResonseType").val();
        if ((file = this.files[0])) {
            img = new Image();
            img.onload = function() {
                var ht = this.height;
                var wds = this.width;
                if ((parseInt(ht) == parseInt(wds)) && (parseInt(ht) >= 90 && parseInt(ht) <= 120 ) && (parseInt(wds) >=90 && parseInt(wds) <= 120)) {
                    $(thisAttr).parent().find('.form-group').removeClass('has-error has-danger');
                    $(thisAttr).parent().find(".help-block").empty();
                    var id= $(thisAttr).next().attr("id");
                    $("#"+id).val('');
                    if(response_type == "Scale" || response_type == "Continuous Scale"){
                    	$("#"+id).next().removeClass("hide");
                    }
                    $('.textLabel'+id).text("Change");
                } else {
                    $(thisAttr).parent().find('img').attr("src","../images/icons/sm-thumb.jpg");
                    $(thisAttr).parent().find('.form-group').addClass('has-error has-danger');
                    $(thisAttr).parent().find(".help-block").empty().append('<ul class="list-unstyled"><li>File incorrect.</li></ul>');
                    $(thisAttr).parent().parent().parent().find(".removeUrl").click();
                    var id= $(thisAttr).next().attr("id");
                    $("#"+id).val('');
                    $("#"+$(thisAttr).attr("id")).val('');
                    $('.textLabel'+id).text("Upload");
                    if(response_type == "Scale" || response_type == "Continuous Scale"){
                    	$("#"+id).next().addClass("hide");
                    }
                }
            };
            img.onerror = function() {
                $(thisAttr).parent().find('img').attr("src","../images/icons/sm-thumb.jpg");
                $(thisAttr).parent().find('.form-group').addClass('has-error has-danger');
                $(thisAttr).parent().find(".help-block").empty().append('<ul class="list-unstyled"><li>File incorrect.</li></ul>');
                $(thisAttr).parent().parent().parent().find(".removeUrl").click();
            };
            img.src = _URL.createObjectURL(file);
        }
    });
    $('.textScaleValue').on('blur',function(){
		validateForUniqueValue(this,"Text Scale",function(){});
	});
    $('.valuePickerVal').on('blur',function(){
		validateForUniqueValue(this,"Value Picker",function(){});
	});
    $('.imageChoiceVal').on('blur',function(){
		validateForUniqueValue(this,"Image Choice",function(){});
	});
    $('.textChoiceVal').on('blur',function(){
		validateForUniqueValue(this,"Text Choice",function(){});
	});
    $("#numericUnitId").keypress(function(event){
        var inputValue = event.charCode;
        if(!(inputValue >= 65 && inputValue <= 122) && (inputValue != 32 && inputValue != 0)){
            event.preventDefault();
        }
    });
    $("#validationConditionId").change(function(e){
    	var value = $(this).val();
    	if(value != '' && value != null && typeof value!='undefined'){
    		$("#validationCharactersId").val('');
    		$("#validationCharactersId").attr("disabled",false);
    		$("#validationCharactersId").attr("required",true);
    		$("#validationExceptTextId").val('');
    		$("#validationExceptTextId").attr("disabled",false);
    		$('.selectpicker').selectpicker('refresh');
    		$("#invalidMessageId").attr("required",true);
    		$("#invalidMessageId").val("Invalid Input. Please try again.");
    	}else{
    		$("#validationCharactersId").val('');
    		$("#validationExceptTextId").val('');
    		$("#validationCharactersId").attr("disabled",true);
    		$("#validationExceptTextId").attr("disabled",true);
    		$("#validationCharactersId").attr("required",false);
    		$('.selectpicker').selectpicker('refresh');
    		$("#validationCharactersId").validator('validate');
    		$('#validationCharactersId').parent().removeClass("has-danger").removeClass("has-error");
            $('#validationCharactersId').parent().find(".help-block").empty();
    		$("#invalidMessageId").attr("required",false);
    		$("#invalidMessageId").val('');
    	}
    })
    $("#validationCharactersId").change(function(e){
    	var value = $(this).val();
    	$("#validationExceptTextId").val('');
    	addRegEx(value)
    });
    var valicationCharacterValue = "${questionsBo.questionReponseTypeBo.validationCharacters}";
    if(valicationCharacterValue != '' && valicationCharacterValue != null && typeof valicationCharacterValue !='undefined'){
    	addRegEx(valicationCharacterValue);
    }
});
function addRegEx(value){
	$("#validationExceptTextId").unbind("keyup blur");
	if(value == "alphabets"){
		$("#validationExceptTextId" ).bind('keyup blur',function(){ 
			var node = $(this);
		    node.val(node.val().replace(/[^a-zA-Z|\s]/g,'')); 
        });
	}else if(value == "numbers"){
		$("#validationExceptTextId" ).bind('keyup blur',function(){ 
			var node = $(this);
		    node.val(node.val().replace(/[^0-9|\s]+$/,'')); 
        });
	}else if(value == "alphabetsandnumbers"){
		$("#validationExceptTextId" ).bind('keyup blur',function(){ 
			var node = $(this);
			node.val(node.val().replace(/[^a-zA-Z0-9|\s]/g,'')); 
        });
	}else if(value == "specialcharacters"){
		$("#validationExceptTextId" ).bind('keyup blur',function(){ 
			var node = $(this);
		    node.val(node.val().replace(/[a-zA-Z0-9\s]/g,'')); 
        });
	}
}
//Displaying images from file upload 
function readURL(input) {
    
    if (input.files && input.files[0]) {
            var reader = new FileReader();            
             
            reader.onload = function (e) {
                 var a = input.getAttribute("id");
                 $("#" + a).prev().children().children()
                    .attr('src', e.target.result)
                    .width(32)
                    .height(32); 
                var  sr = $("#" + a).prev().children().children().attr('src');
            };

            reader.readAsDataURL(input.files[0]);
        }
}
function toJSDate( dateTime ) {
	if(dateTime != null && dateTime !='' && typeof dateTime != 'undefined'){
		var date = dateTime.split("/");
		return new Date(date[2], (date[0]-1), date[1]);
	}
}
function setResponseDate(type){
	
	
	if(type == 'Date-Time'){
		
		$("#minDateId").datetimepicker().data('DateTimePicker').format('MM/DD/YYYY HH:mm');
	    $("#maxDateId").datetimepicker().data('DateTimePicker').format('MM/DD/YYYY HH:mm');
	    $("#defaultDate").datetimepicker().data('DateTimePicker').format('MM/DD/YYYY HH:mm');
	    
	}else{
		
		$("#minDateId").datetimepicker().data('DateTimePicker').format('MM/DD/YYYY');
	    $("#maxDateId").datetimepicker().data('DateTimePicker').format('MM/DD/YYYY');
	    $("#defaultDate").datetimepicker().data('DateTimePicker').format('MM/DD/YYYY');
	   
	}
}
function resetTheLineStatData(){
	 $("#chartContainer").find('input:text').val(''); 
	 $("#statContainer").find('input:text').val(''); 
	 $("#chartContainer").find('input:text').val(''); 
	 $("#statContainer").find('input:text').val(''); 
	 $("#addLineChart").prop("checked", false);
	 $("#useStasticData").prop("checked", false);
	 $("#chartContainer").hide();
     $("#statContainer").hide();
     $(".chartrequireClass").attr('required',false);
     $(".requireClass").attr('required',false);
	 var container = document.getElementById('chartContainer');
	 if(container != null){
		 var children = container.getElementsByTagName('select');
		 for (var i = 0; i < children.length; i++) {
		        children[i].selectedIndex = 0;
		 }
	 }
	  
	 var statcontainer = document.getElementById('statContainer');
	 if(statcontainer != null){
		 var statchildren = statcontainer.getElementsByTagName('select');
		 for (var i = 0; i < statchildren.length; i++) {
		        statchildren[i].selectedIndex = 0;
		 } 
	 }
	 $("#allowHealthKit").prop("checked", false);
	 $(".healthkitrequireClass").attr('required',false);
	 $("#healthkitDatatypeId").val('');
	 $('.selectpicker').selectpicker('refresh');
}
function getResponseType(id){
	if(id != null && id !='' && typeof id != 'undefined'){
		var previousResponseType = '${questionsBo.responseType}';
		if(Number(id) != Number(previousResponseType)){
			 var responseType = $("#responseTypeId>option:selected").html();
			 resetTheLineStatData();
			 if(responseType != 'Boolean'){
				 $("#"+responseType.replace(/\s/g, '')).find('input:text').val(''); 
				 $("#"+responseType.replace(/\s/g, '')).find('img').attr("src",''); 
				 if(responseType == "Date"){
					 var datePicker = $("#"+responseType.replace(/\s/g, '')).find('input:text').data("DateTimePicker");
					 if(typeof datePicker != 'undefined'){
						 $("#minDateId").datetimepicker().data('DateTimePicker').clear();
						 $("#maxDateId").datetimepicker().data('DateTimePicker').clear();
						 $("#defaultDate").datetimepicker().data('DateTimePicker').clear();
					 }
				 }
			 }
			 if(responseType == 'Image Choice'){
				 $("#"+responseType.replace(/\s/g, '')).find('input:file').val(''); 
				 $("#"+responseType.replace(/\s/g, '')).find('img').attr("src","../images/icons/sm-thumb.jpg");
				 $("#"+responseType.replace(/\s/g, '')).find("input:hidden").each(function(){
					 $("#"+this.id).val('');
				 });
			 }
			 if(responseType == 'Text Scale' && responseType == 'Text Choice' && responseType == 'Boolean'){
				 	var container = document.getElementById(responseType.replace(/\s/g, ''));
				    var children = container.getElementsByTagName('select');
				    
				    for (var i = 0; i < children.length; i++) {
				        children[i].selectedIndex = 0;
				    }
				    $('.selectpicker').selectpicker('refresh');
			}
			$("#timeIntervalStepId").val(1);
			$("#timeIntervalDefaultId").val("00:01");
			
			$("#textScalePositionId").val(2);
			$("#scaleDefaultValueId").val(1);
			if(responseType == 'Text Scale'){
	    		 $("#vertical").attr("checked",true);
	    	}
			if(responseType == 'Scale' || responseType == 'Continuous Scale'){
	    		 $("#horizontal").attr("checked",true);
	    		 if(responseType == 'Scale'){
	    			 $("#scaleMinImagePathId").val('');
	    			 $("#scaleMaxImagePathId").val('');
	    		 }else{
	    			 $("#continuesScaleMinImagePathId").val('');
	    			 $("#continuesScaleMaxImagePathId").val('');
	    		 } 
	    	}
		    if(responseType == 'Numeric'){
		    	 $('input[name="questionReponseTypeBo.style"]').attr("checked",false);
		    	 $("#styleDecimal").attr("checked",true);
		    }
		    if(responseType == 'Date'){
		    	$('input[name="questionReponseTypeBo.style"]').attr("checked",false); 
		    	$("#date").attr("checked",true);
		    	$("#customDateId").attr("checked",true);
		    }
		    $("#useAnchorDateId").attr("checked",false);
		 }
		<c:forEach items="${questionResponseTypeMasterInfoList}" var="questionResponseTypeMasterInfo">
		 var infoId = Number('${questionResponseTypeMasterInfo.id}'); 
		 var responseType = '${questionResponseTypeMasterInfo.responseType}';
		 $("#"+responseType.replace(/\s/g, '')).hide();
		 if(responseType == 'Date'){
			 var style = "${questionsBo.questionReponseTypeBo.style}";
			
			 setResponseDate(style);
		 }
		 $("."+responseType.replace(/\s/g, '')+"Required").attr("required",false);
		 if(id == infoId){
    		var description = '${questionResponseTypeMasterInfo.description}';
    		var dataType = "${questionResponseTypeMasterInfo.dataType}";
    		var dashboard = '${questionResponseTypeMasterInfo.dashBoardAllowed}';
    		$("#responseTypeDataType").text(dataType);
    		$("#responseTypeDescrption").text(description);
    		$("#rlaResonseType").val(responseType);
    		$("#rlaResonseDataType").text(dataType);
    		$("#rlaResonseTypeDescription").text(description);
    		$("#"+responseType.replace(/\s/g, '')).show();
    		$("."+responseType.replace(/\s/g, '')+"Required").attr("required",true);
    		if(dashboard == 'true'){
    			$("#useStasticDataContainerId").show();
        		$("#addLineChartContainerId").show();	
        		$("#borderdashId").show();
        		 if($("#addLineChart").is(":checked")){
        			 $("#chartContainer").show();
        			 $(".chartrequireClass").attr('required',true);
        		 }else{
        			 $("#lineChartTimeRangeId").val('');
        			 if(document.getElementById("allowRollbackChartNo") != null && typeof document.getElementById("allowRollbackChartNo") != 'undefined'){
        				 document.getElementById("allowRollbackChartNo").checked = true;	 
        			 }
        	    	 $('#chartTitleId').val('');
        	    	 $('.selectpicker').selectpicker('refresh');
        		 }
        		 if($("#useStasticData").is(":checked")){
        			 $("#statContainer").show();
        			 $(".requireClass").attr('required',true);
        		 }else{
        			 $("#statShortNameId").val('');
        	    	 $("#statDisplayNameId").val('');
        	    	 $("#statDisplayUnitsId").val('');
        	    	 $("#statTypeId").val('');
        	    	 $("#statFormula").val('');
        	    	 $('.selectpicker').selectpicker('refresh');
        		 }
    		}else{
    			$("#useStasticDataContainerId").hide();
        		$("#addLineChartContainerId").hide();
        		$("#borderdashId").hide();
    		}
    		if(responseType == 'Height' || responseType == 'Numeric'){
    			$("#borderHealthdashId").show();
    			$("#allowHealthKitId").show();
    			if($("#allowHealthKit").is(":checked")){
        			$("#healthKitContainerId").show();
             		$(".healthkitrequireClass").attr('required',true);
        		}else{
        			$("#healthKitContainerId").hide();
             		$(".healthkitrequireClass").attr('required',false);
             		$("#healthkitDatatypeId").val('');
             		$('.selectpicker').selectpicker('refresh');
        		}
    		}else{
    			$("#allowHealthKitId").hide();
    			$("#healthKitContainerId").hide();
    			$("#borderHealthdashId").hide();
    		}
    		if(responseType == 'Date'){
   			 	$("#useAnchorDateContainerId").show();
   			 	var anchorDate = "${questionsBo.useAnchorDate}";
			 	if(anchorDate == "true"){
	   			 	$('.useAnchorDateName').show();
			 		$("#useAnchorDateId").attr("checked",true);
			 	}
	   		}else{
	   			$("#useAnchorDateContainerId").hide();
	   		}
    		if(responseType == 'Scale' || responseType == 'Continuous Scale' || responseType == 'Text Scale'){
    			$("#scaleType").show();
	   		}else{
	   			$("#scaleType").hide();
	   		}
    	 }
    	</c:forEach>
	}else{
		$("#responseTypeDataType").text("- NA -");
		$("#responseTypeDescrption").text("- NA -");
		$("#rlaResonseType").val('');
		$("#rlaResonseDataType").text("- NA -");
		$("#rlaResonseTypeDescription").text("- NA -");
	}
}
function saveQuestionStepQuestionnaire(item,callback){
	
	var fromId = $("#fromId").val();
	
	var questionsBo = new Object();
	
	var short_title = $("#shortTitle").val();
	var questionText = $("#questionTextId").val();
	
	var responseType = $("#responseTypeId").val();
	var addLinceChart = $('input[name="addLineChart"]:checked').val();
	var lineChartTimeRange = $("#lineChartTimeRangeId").val();
	var allowRollbackChart = $('input[name="allowRollbackChart"]:checked').val();
	var chartTitle = $('#chartTitleId').val();
	var useStasticData = $('input[name="useStasticData"]:checked').val();
	var statShortName = $("#statShortNameId").val();
	var statDisplayName = $("#statDisplayNameId").val();
	var statDisplayUnits = $("#statDisplayUnitsId").val();
	var statType=$("#statTypeId").val();
	var statFormula=$("#statFormula").val();
	var questionid = $("#questionId").val();
	var skippableText = $('input[name="skippable"]:checked').val();
	var anchor_date = $('input[name="questionsBo.useAnchorDate"]:checked').val();
	var questionnaireId = $("#questionnairesId").val();
	var anchor_date_id = $("#anchorDateId").val();
	var anchor_text = $('#anchorTextId').val();
	
	
	questionsBo.shortTitle=short_title;
	questionsBo.question=questionText;
	questionsBo.responseType=responseType;
	questionsBo.lineChartTimeRange=lineChartTimeRange;
	questionsBo.addLineChart=addLinceChart;
	questionsBo.allowRollbackChart=allowRollbackChart;
	questionsBo.chartTitle=chartTitle;
	questionsBo.useStasticData=useStasticData;
	questionsBo.statShortName=statShortName;
	questionsBo.statDisplayName=statDisplayName;
	questionsBo.statDisplayUnits=statDisplayUnits;
	questionsBo.statType=statType;
	questionsBo.statFormula=statFormula;
	questionsBo.type="save";
	questionsBo.fromId=fromId;
	questionsBo.id = questionid;
	questionsBo.skippable=skippableText;
	questionsBo.useAnchorDate=anchor_date;
	questionsBo.anchorDateName=anchor_text;
	questionsBo.anchorDateId=anchor_date_id;
	questionsBo.questionnaireId=questionnaireId;
	var questionReponseTypeBo = new  Object();
	var minValue='';
	var maxValue='';
	var defaultValue='';
	var maxdescription='';
	var mindescrption='';
	var step='';
	var resType = $("#rlaResonseType").val();
	var verticalText = '';
	
	var formData = new FormData();
	
	if(resType == "Scale"){
		minValue = $("#scaleMinValueId").val();
		maxValue = $("#scaleMaxValueId").val();
		defaultValue = $("#scaleDefaultValueId").val();
		mindescrption = $("#scaleMinDescriptionId").val();
		maxdescription = $("#scaleMaxDescriptionId").val();
		step = $("#scaleStepId").val();
		verticalText = $('input[name="questionReponseTypeBo.vertical"]:checked').val();	
		
		var minImagePath = $("#scaleMinImagePathId").val();
		var maxImagePath = $("#scaleMaxImagePathId").val();
	    
		formData.append('minImageFile', document.getElementById("scaleMinImageFileId").files[0]);
	    formData.append('maxImageFile', document.getElementById("scaleMaxImageFileId").files[0]);
		
		questionReponseTypeBo.vertical=verticalText;
		questionReponseTypeBo.minValue=minValue;
		questionReponseTypeBo.maxValue=maxValue;
		questionReponseTypeBo.defaultValue=defaultValue;
		questionReponseTypeBo.minDescription=mindescrption;
		questionReponseTypeBo.maxDescription=maxdescription;
		questionReponseTypeBo.step=step;
		
		questionReponseTypeBo.minImage=minImagePath;
		questionReponseTypeBo.maxImage=maxImagePath;
		
	}else if(resType == "Continuous Scale"){
		
		minValue = $("#continuesScaleMinValueId").val();
		maxValue = $("#continuesScaleMaxValueId").val();
		defaultValue = $("#continuesScaleDefaultValueId").val();
		mindescrption = $("#continuesScaleMinDescriptionId").val();
		maxdescription = $("#continuesScaleMaxDescriptionId").val();
		vertical = $('input[name="questionReponseTypeBo.vertical"]:checked').val();	
		var fractionDigits = $("#continuesScaleFractionDigitsId").val();
		
		var minImagePath = $("#continuesScaleMinImagePathId").val();
		var maxImagePath = $("#continuesScaleMaxImagePathId").val();
	    
		formData.append('minImageFile', document.getElementById("continuesScaleMinImageFileId").files[0]);
	    formData.append('maxImageFile', document.getElementById("continuesScaleMaxImageFileId").files[0]);
		
		questionReponseTypeBo.vertical=verticalText;
		questionReponseTypeBo.minValue=minValue;
		questionReponseTypeBo.maxValue=maxValue;
		questionReponseTypeBo.defaultValue=defaultValue;
		questionReponseTypeBo.minDescription=mindescrption;
		questionReponseTypeBo.maxDescription=maxdescription;
		questionReponseTypeBo.maxFractionDigits=fractionDigits;
		
		questionReponseTypeBo.minImage=minImagePath;
		questionReponseTypeBo.maxImage=maxImagePath;
		
	}else if(resType == "Location"){
		var usecurrentlocation = $('input[name="questionReponseTypeBo.useCurrentLocation"]:checked').val();	
		questionReponseTypeBo.useCurrentLocation=usecurrentlocation;
	}else if(resType == "Email"){
		var placeholderText = $("#placeholderId").val();	
		questionReponseTypeBo.placeholder=placeholderText;
	}else if(resType == "Text"){
		var max_length = $("#textmaxLengthId").val();
		var placeholderText = $("#textPlaceholderId").val(); 
	    var multiple_lines = $('input[name="questionReponseTypeBo.multipleLines"]:checked').val();	
	    
	    var validation_condition = $("#validationConditionId").val();
	    var validation_characters = $("#validationCharactersId").val();
	    var validation_except_text =$("#validationExceptTextId").val();
	    var validation_regex = $("#validationRegexId").val();
		var invalid_message = $("#invalidMessageId").val();
		
	    questionReponseTypeBo.maxLength = max_length;
	    questionReponseTypeBo.placeholder = placeholderText;
	    questionReponseTypeBo.multipleLines = multiple_lines;
	    
	    questionReponseTypeBo.validationCondition = validation_condition;
	    questionReponseTypeBo.validationCharacters = validation_characters;
	    questionReponseTypeBo.validationExceptText = validation_except_text;
	    questionReponseTypeBo.validationRegex = validation_regex;
	    questionReponseTypeBo.invalidMessage = invalid_message;
	    
	}else if(resType == "Height"){
		var measurement_system = $('input[name="questionReponseTypeBo.measurementSystem"]:checked').val();
		var placeholder_text = $("#heightPlaceholderId").val();
		var healthkitinfo = $('input[name="allowHealthKit"]:checked').val();
		var healthkitdatatype = $("#healthkitDatatypeId").val();
		
		questionReponseTypeBo.measurementSystem = measurement_system;
		questionReponseTypeBo.placeholder = placeholder_text;
		questionsBo.allowHealthKit=healthkitinfo;
		questionsBo.healthkitDatatype=healthkitdatatype;
	}else if(resType == "Time interval"){
		 var stepValue = $("#timeIntervalStepId").val();
		 var default_time = $("#timeIntervalDefaultId").val();
		 questionReponseTypeBo.step=stepValue;
		 questionReponseTypeBo.defaultTime=default_time;
	}else if(resType == "Numeric"){
		var styletext = $('input[name="questionReponseTypeBo.style"]:checked').val();
		var unitText = $("#numericUnitId").val();
		var palceholder_text = $("#numericPlaceholderId").val(); 
		
		var healthkitinfo = $('input[name="allowHealthKit"]:checked').val();
		var healthkitdatatype = $("#healthkitDatatypeId").val();
		
		questionReponseTypeBo.style = styletext;
		questionReponseTypeBo.placeholder = palceholder_text;
		questionReponseTypeBo.unit=unitText;
		questionsBo.allowHealthKit=healthkitinfo;
		questionsBo.healthkitDatatype=healthkitdatatype;
	}else if(resType == "Date"){
		
		var min_date = $("#minDateId").val(); 
		var max_date = $("#maxDateId").val(); 
		var default_date = $("#defaultDate").val(); 
		var style=$('input[name="questionReponseTypeBo.style"]:checked').val();
		var allowedDateRange = $('input[name="questionReponseTypeBo.selectionStyle"]:checked').val();
		questionReponseTypeBo.minDate = min_date;
		questionReponseTypeBo.maxDate = max_date;
		questionReponseTypeBo.defaultDate = default_date;
		questionReponseTypeBo.style=style;
		questionReponseTypeBo.selectionStyle=allowedDateRange;
	}else if(resType == "Boolean"){
		var questionSubResponseArray  = new Array();
		$('#Boolean .row').each(function(){
			var questionSubResponseType = new Object();
			var id = $(this).attr("id");
			var response_sub_type_id = $("#responseSubTypeValueId"+id).val();
			var diasplay_text = $("#dispalyText"+id).val();
			var diaplay_value = $("#displayValue"+id).val();
			
			
			questionSubResponseType.responseSubTypeValueId=response_sub_type_id;
			questionSubResponseType.text=diasplay_text;
			questionSubResponseType.value=diaplay_value;
			
			
			questionSubResponseArray.push(questionSubResponseType);
		});
		questionsBo.questionResponseSubTypeList = questionSubResponseArray;
		
		
	}else if(resType == "Value Picker"){
		var questionSubResponseArray  = new Array();
		$('.value-picker').each(function(){
			var questionSubResponseType = new Object();
			var id = $(this).attr("id");
			
			var response_sub_type_id = $("#valPickSubTypeValueId"+id).val();
			var diasplay_text = $("#displayValPickText"+id).val();
			var diaplay_value = $("#displayValPickValue"+id).val();
			
			questionSubResponseType.responseSubTypeValueId=response_sub_type_id;
			questionSubResponseType.text=diasplay_text;
			questionSubResponseType.value=diaplay_value;
			questionSubResponseArray.push(questionSubResponseType);
		});
		questionsBo.questionResponseSubTypeList = questionSubResponseArray;
	} else if(resType == "Text Scale"){
		var questionSubResponseArray  = new Array();
		$('.text-scale').each(function(){
			
			var questionSubResponseType = new Object();
			var id = $(this).attr("id");
			
			
			var response_sub_type_id = $("#textScaleSubTypeValueId"+id).val();
			var diasplay_text = $("#displayTextSclText"+id).val();
			var diaplay_value = $("#displayTextSclValue"+id).val();
			
			questionSubResponseType.responseSubTypeValueId=response_sub_type_id;
			questionSubResponseType.text=diasplay_text;
			questionSubResponseType.value=diaplay_value;
			
			questionSubResponseArray.push(questionSubResponseType);
			
		});
		questionsBo.questionResponseSubTypeList = questionSubResponseArray;
	}else if(resType == "Text Choice"){
		
		var questionSubResponseArray  = new Array();
		var selectionStyel = $('input[name="questionReponseTypeBo.selectionStyle"]:checked').val();
		questionReponseTypeBo.selectionStyle = selectionStyel;
		$('.text-choice').each(function(){
			var questionSubResponseType = new Object();
			var id = $(this).attr("id");
			
			
			var response_sub_type_id = $("#textChoiceSubTypeValueId"+id).val();
			var diasplay_text = $("#displayTextChoiceText"+id).val();
			var diaplay_value = $("#displayTextChoiceValue"+id).val();
			var exclusioveText = $("#exclusiveId"+id).val();
			var display_description = $("#displayTextChoiceDescription"+id).val();
			
			questionSubResponseType.responseSubTypeValueId=response_sub_type_id;
			questionSubResponseType.text=diasplay_text;
			questionSubResponseType.value=diaplay_value;
			questionSubResponseType.exclusive=exclusioveText;
			questionSubResponseType.description=display_description;
			questionSubResponseArray.push(questionSubResponseType);
			
		});
		questionsBo.questionResponseSubTypeList = questionSubResponseArray;
	}else if(resType == "Image Choice"){
		var questionSubResponseArray  = new Array();
		var i=0;
		$('.image-choice').each(function(){
			var questionSubResponseType = new Object();
			var id = $(this).attr("id");
			
			var response_sub_type_id = $("#imageChoiceSubTypeValueId"+id).val();
			var diasplay_text = $("#displayImageChoiceText"+id).val();
			var diaplay_value = $("#displayImageChoiceValue"+id).val();
			
			
			var imagePath = $("#imagePathId"+id).val();
			var selectedImagePath = $("#selectImagePathId"+id).val();
		    
			formData.append('imageFile[' + id + ']', document.getElementById("imageFileId"+id).files[0]);
		    formData.append('selectImageFile[' + id + ']', document.getElementById("selectImageFileId"+id).files[0]);
			
			questionSubResponseType.responseSubTypeValueId=response_sub_type_id;
			questionSubResponseType.text=diasplay_text;
			questionSubResponseType.value=diaplay_value;
			questionSubResponseType.imageId=id;
			questionSubResponseType.image=imagePath;
			questionSubResponseType.selectedImage=selectedImagePath;
			
			questionSubResponseArray.push(questionSubResponseType);
			
			i=i+1;
		});
		questionsBo.questionResponseSubTypeList = questionSubResponseArray;
	}
	
	var response_type_id = $("#questionResponseTypeId").val();
	var question_response_type_id = $("#responseQuestionId").val();
	
	questionReponseTypeBo.responseTypeId=response_type_id;
	questionReponseTypeBo.questionsResponseTypeId=question_response_type_id;
	
	
	questionsBo.questionReponseTypeBo=questionReponseTypeBo;
	if(fromId != null && fromId!= '' && typeof fromId !='undefined' && 
			questionText != null && questionText!= '' && typeof questionText !='undefined' ||
			short_title != null && short_title!= '' && typeof short_title !='undefined'){
		formData.append("questionInfo", JSON.stringify(questionsBo)); 
		
		var data = JSON.stringify(questionsBo);
		$.ajax({ 
	         url: "/fdahpStudyDesigner/adminStudies/saveQuestion.do?_S=${param._S}",
	          type: "POST",
	          datatype: "json",
	          data: formData,
	          processData: false,
           	  contentType: false,
	          beforeSend: function(xhr, settings){
	              xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
	          },
	          success:function(data){
	        	var jsonobject = eval(data);			                       
				var message = jsonobject.message;
				if(message == "SUCCESS"){
					$("body").removeClass("loading");
					$("#preShortTitleId").val(short_title);
					var questionId = jsonobject.questionId;
					var questionResponseId = jsonobject.questionResponseId;
					
					$("#questionId").val(questionId);
					$("#questionResponseTypeId").val(questionResponseId);
					$("#responseQuestionId").val(questionId);
					
					if(statShortName != null && statShortName != '' && typeof statShortName != 'undefined'){
						$("#prevStatShortNameId").val(statShortName);
					}
					
					$("#alertMsg").removeClass('e-box').addClass('s-box').html("Content saved as draft.");
					$(item).prop('disabled', false);
					$('#alertMsg').show();
					if (callback)
						callback(true);
					if($('.sixthQuestionnaires').find('span').hasClass('sprites-icons-2 tick pull-right mt-xs')){
						$('.sixthQuestionnaires').find('span').removeClass('sprites-icons-2 tick pull-right mt-xs');
					}
				}else{
					var errMsg = jsonobject.errMsg;
					if(errMsg != '' && errMsg != null && typeof errMsg != 'undefined'){
						$("#alertMsg").removeClass('s-box').addClass('e-box').html(errMsg);
					}else{
						$("#alertMsg").removeClass('s-box').addClass('e-box').html("Something went Wrong");
					}
					if (callback)
  						callback(false);
				}
				setTimeout(hideDisplayMessage, 4000);
	          },
	          error: function(xhr, status, error) {
    			  $(item).prop('disabled', false);
    			  $('#alertMsg').show();
    			  $("#alertMsg").removeClass('s-box').addClass('e-box').html("Something went Wrong");
    			  setTimeout(hideDisplayMessage, 4000);
    		  }
	   }); 
	}else{
		if(questionText == null || questionText == '' || typeof questionText =='undefined' ){
			$('#questionTextId').validator('destroy').validator();
			if(!$('#questionTextId')[0].checkValidity()) {
				$("#questionTextId").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
				$('.questionLevel a').tab('show');
			}
		}
		if(short_title == null || short_title== '' || typeof short_title =='undefined' ){
			$('#shortTitle').validator('destroy').validator();
			if(!$('#shortTitle')[0].checkValidity()) {
				$("#shortTitle").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
				$('.questionLevel a').tab('show');
			}
		}
	}
}
function goToBackPage(item){
	
	$(item).prop('disabled', true);
	<c:if test="${actionTypeForFormStep ne 'view'}">
		bootbox.confirm({
				closeButton: false,
				message : 'You are about to leave the page and any unsaved changes will be lost. Are you sure you want to proceed?',	
			    buttons: {
			        'cancel': {
			            label: 'Cancel',
			        },
			        'confirm': {
			            label: 'OK',
			        },
			    },
			    callback: function(result) {
			        if (result) {
			        	var a = document.createElement('a');
			        	a.href = "/fdahpStudyDesigner/adminStudies/formStep.do?_S=${param._S}";
			        	document.body.appendChild(a).click();
			        }else{
			        	$(item).prop('disabled', false);
			        }
			    }
		});
	</c:if>
	<c:if test="${actionTypeForFormStep eq 'view'}">
		var a = document.createElement('a');
		a.href = "/fdahpStudyDesigner/adminStudies/formStep.do?_S=${param._S}";
		document.body.appendChild(a).click();
	</c:if>
}
function getSelectionStyle(item){
	var value= $(item).val();
	if(value == 'Single'){
		$('.textChoiceExclusive').attr("disabled",true);
		$('.textChoiceExclusive').attr("required",false);
		$('.textChoiceExclusive').val('');
		
		$('.selectpicker').selectpicker('refresh');
		$(".textChoiceExclusive").validator('validate');
	}else{
		$('.textChoiceExclusive').attr("disabled",false);
		$('.textChoiceExclusive').attr("required",true);
		$('.selectpicker').selectpicker('refresh');
	}
}

var count = $('.value-picker').length;
function addValuePicker(){
	count = count+1;
	var newValuePicker ="<div class='value-picker row form-group mb-xs' id="+count+">"+
						"	<div class='col-md-3 pl-none'>"+
						"   <div class='form-group'>"+
						"      <input type='text' class='form-control' name='questionResponseSubTypeList["+count+"].text' id='displayValPickText"+count+"' required maxlength='20'>"+
						"      <div class='help-block with-errors red-txt'></div>"+
						"   </div>"+
						"</div>"+
						"<div class='col-md-4 pl-none'>"+
						"   <div class='form-group'>"+
						"      <input type='text' class='form-control valuePickerVal' name='questionResponseSubTypeList["+count+"].value' id='displayValPickValue"+count+"' required maxlength='50'"+ 
						" onblur='validateForUniqueValue(this,&#34;Value Picker&#34;,function(){});'>"+
						"      <div class='help-block with-errors red-txt'></div>"+
						"   </div>"+
						"</div>"+
						"<div class='col-md-2 pl-none mt__6'>"+
						"   <span class='addBtnDis addbtn mr-sm align-span-center top6' onclick='addValuePicker();'>+</span>"+
					    "<span class='delete vertical-align-middle remBtnDis hide pl-md align-span-center top0' onclick='removeValuePicker(this);'></span>"+
						"</div>"+
					"</div>";	
	$(".value-picker:last").after(newValuePicker);
	$(".value-picker").parent().removeClass("has-danger").removeClass("has-error");
    $(".value-picker").parent().find(".help-block").html('');
	$(".value-picker").parents("form").validator("destroy");
    $(".value-picker").parents("form").validator();
   
	if($('.value-picker').length > 2){
		$(".remBtnDis").removeClass("hide");
	}else{
		$(".remBtnDis").addClass("hide");
	}
	$('#'+count).find('input:first').focus();
}
function removeValuePicker(param){
	if($('.value-picker').length > 2){
		
		$(param).parents(".value-picker").remove();
		$(".value-picker").parent().removeClass("has-danger").removeClass("has-error");
	    $(".value-picker").parent().find(".help-block").html('');
	    $(".value-picker").parents("form").validator("destroy");
		$(".value-picker").parents("form").validator();
		if($('.value-picker').length > 2){
			$(".remBtnDis").removeClass("hide");
		}else{
			$(".remBtnDis").addClass("hide");
		}
	}
}
var scaleCount = $('.text-scale').length;
function addTextScale(){
	scaleCount = scaleCount+1;
	if($('.text-scale').length < 8){
	var newTextScale = "<div class='text-scale row' id="+scaleCount+">"+
					    "	<div class='col-md-3 pl-none'>"+
					    "    <div class='form-group'>"+
				        "      <input type='text' class='form-control TextScaleRequired' name='questionResponseSubTypeList["+scaleCount+"].text' id='displayTextSclText"+scaleCount+"'+  maxlength='15' required>"+
					    "      <div class='help-block with-errors red-txt'></div>"+
					    "   </div>"+
						"</div>"+
						" <div class='col-md-4 pl-none'>"+
						"    <div class='form-group'>"+
						"       <input type='text' class='form-control TextScaleRequired textScaleValue' class='form-control' name='questionResponseSubTypeList["+scaleCount+"].value' id='displayTextSclValue"+scaleCount+"' maxlength='50' required onblur='validateForUniqueValue(this,&#34;Text Scale&#34;,function(){});'>"+
						"       <div class='help-block with-errors red-txt'></div>"+
						"    </div>"+
						" </div>";
						newTextScale+="<div class='col-md-2 pl-none mt__8'>"+
						"	<span class='addBtnDis addbtn mr-sm align-span-center' onclick='addTextScale();'>+</span>"+
						"  <span class='delete vertical-align-middle remBtnDis hide pl-md align-span-center' onclick='removeTextScale(this);'></span>"+
						"	</div>"+
						"</div>";
	$(".text-scale:last").after(newTextScale);
	$('.selectpicker').selectpicker('refresh');
	$(".text-scale").parent().removeClass("has-danger").removeClass("has-error");
    $(".text-scale").parent().find(".help-block").empty();
	$(".text-scale").parents("form").validator("destroy");
    $(".text-scale").parents("form").validator();
	if($('.text-scale').length > 2){
		$(".remBtnDis").removeClass("hide");
	}else{
		$(".remBtnDis").addClass("hide");
	}
	if($('.text-scale').length == 8){
		$(".text-scale:last").find('span.addBtnDis').remove();
		$(".text-scale:last").find('span.delete').before("<span class='tool-tip' data-toggle='tooltip' data-placement='top' title='Only a max of 8 rows are allowed'><span class='addBtnDis addbtn mr-sm align-span-center cursor-none' onclick='addTextScale();'>+</span></span>");
		$('[data-toggle="tooltip"]').tooltip();
	}else{
		$(".text-scale:last").find('span.addBtnDis').remove();
		$(".text-scale:last").find('span.delete').before("<span class='addBtnDis addbtn mr-sm align-span-center' onclick='addTextScale();'>+</span>");
	}
	
	}
	$('#'+scaleCount).find('input:first').focus();
}
function removeTextScale(param){
	if($('.text-scale').length > 2){
		$(param).parents(".text-scale").remove();
		$(".text-scale").parent().removeClass("has-danger").removeClass("has-error");
	    $(".text-scale").parent().find(".help-block").empty();
	    $(".text-scale").parents("form").validator("destroy");
		$(".text-scale").parents("form").validator();
		if($('.text-scale').length > 2){
			$(".remBtnDis").removeClass("hide");
		}else{
			$(".remBtnDis").addClass("hide");
		}
		$("#textScalePositionId").val($('.text-scale').length);
		if($('.text-scale').length == 8){
			$(".text-scale:last").find('span.addBtnDis').remove();
			$(".text-scale:last").find('span.delete').before("<span class='tool-tip' data-toggle='tooltip' data-placement='top' title='Only a max of 8 rows are allowed'><span class='addBtnDis addbtn mr-sm align-span-center cursor-none' onclick='addTextScale();'>+</span></span>");
			$('[data-toggle="tooltip"]').tooltip();
		}else{
			$(".text-scale:last").find('span.addBtnDis').remove();
			$(".text-scale:last").find('span.delete').before("<span class='addBtnDis addbtn mr-sm align-span-center' onclick='addTextScale();'>+</span>");
		}
	}
}
var choiceCount = $('.text-choice').length;
function addTextChoice(){
	choiceCount = choiceCount+1;
	var selectionStyle = $('input[name="questionReponseTypeBo.selectionStyle"]:checked').val();
	var newTextChoice = "<div class='text-choice mt-xlg' id='"+choiceCount+"'>"+     
						  "<div class='col-md-3 pl-none'>"+
					    "   <div class='gray-xs-f mb-xs'>Display Text (1 to 100 characters)<span class='requiredStar'>*</span> </div>"+
					    "   <div class='form-group mb-none'>"+
					    "   <input type='text' class='form-control TextChoiceRequired' name='questionResponseSubTypeList["+choiceCount+"].text' id='displayTextChoiceText"+choiceCount+"'  maxlength='100' required>"+
					    "      <div class='help-block with-errors red-txt'></div>"+
					    "   </div>"+
					    "</div>"+
					    "<div class='col-md-3 pl-none'>"+
					    "   <div class='gray-xs-f mb-xs'>Value (1 to 50 characters)<span class='requiredStar'>*</span> </div>"+
					    "   <div class='form-group mb-none'>"+
					    "   <input type='text' class='form-control TextChoiceRequired textChoiceVal' name='questionResponseSubTypeList["+choiceCount+"].value' id='displayTextChoiceValue"+choiceCount+"'  maxlength='50' required onblur='validateForUniqueValue(this,&#34;Text Choice&#34;,function(){});'>"+
					    "      <div class='help-block with-errors red-txt'></div>"+
					    "   </div>"+
					    "</div>"+
					    "<div class='col-md-2 pl-none'>"+
					    "   <div class='gray-xs-f mb-xs'>Mark as exclusive ? <span class='requiredStar'>*</span> </div>"+
					    "   <div class='form-group'>";
						if(selectionStyle == 'Single'){
					    	newTextChoice += "<select name='questionResponseSubTypeList["+choiceCount+"].exclusive' id='exclusiveId"+choiceCount+"' index="+choiceCount+" title='select' data-error='Please choose one option' class='selectpicker TextChoiceRequired textChoiceExclusive' disabled >";
					    }else{
					    	newTextChoice += "<select name='questionResponseSubTypeList["+choiceCount+"].exclusive' id='exclusiveId"+choiceCount+"' index="+choiceCount+" title='select' data-error='Please choose one option' class='selectpicker TextChoiceRequired textChoiceExclusive' required >";
					    }
						newTextChoice +="<option value='Yes'>Yes</option>"+
						  "	   <option value='No'>No</option>"+
						  "	</select>"+
					      "      <div class='help-block with-errors red-txt'></div>"+
					    "   </div>"+
					    "</div> "; 
						  newTextChoice += 	  
					    "<div class='col-md-12 p-none display__flex__center'><div class='col-md-10 pl-none'>"+
					    "   <div class='gray-xs-f mb-xs'>Description(1 to 150 characters) <span class='requiredStar'>*</span> </div>"+
					    "   <div class='form-group'>					     "+
					    "      <textarea class='form-control' name='questionResponseSubTypeList["+choiceCount+"].description' id='displayTextChoiceDescription"+choiceCount+"' maxlength='150'></textarea>"+
					    "   </div>"+
					    "</div>"+
					    "<div class='col-md-2 pl-none'>"+
						  "	 <span class='addBtnDis addbtn align-span-center top6' onclick='addTextChoice();'>+</span>"+
						  "	 <span class='delete vertical-align-middle remBtnDis hide pl-md align-span-center top0 ml-sm' onclick='removeTextChoice(this);'></span>        "+
					    " </div></div>"+
					   "</div>";
	$(".text-choice:last").after(newTextChoice);
	$('.selectpicker').selectpicker('refresh');
	$(".text-choice").parent().removeClass("has-danger").removeClass("has-error");
    $(".text-choice").parent().find(".help-block").empty();
	$(".text-choice").parents("form").validator("destroy");
	$(".text-choice").parents("form").validator();
	if($('.text-choice').length > 2){
		$(".remBtnDis").removeClass("hide");
	}else{
		$(".remBtnDis").addClass("hide");
	}
	$('#'+choiceCount).find('input:first').focus();
}
function removeTextChoice(param){
	if($('.text-choice').length > 2){
		$(param).parents(".text-choice").remove();
		$(".text-choice").parent().removeClass("has-danger").removeClass("has-error");
	    $(".text-choice").parent().find(".help-block").empty();
	    $(".text-choice").parents("form").validator("destroy");
		$(".text-choice").parents("form").validator();
		if($('.text-choice').length > 2){
			$(".remBtnDis").removeClass("hide");
		}else{
			$(".remBtnDis").addClass("hide");
		}
	}
}
var imageCount = $('.image-choice').length;
function addImageChoice(){
	imageCount = imageCount+1;
	var newImageChoice = "<div class='image-choice row' id='"+imageCount+"'>"+
						 "	   <div class='col-md-2 pl-none col-smthumb-2'>"+
						 "   <div class='form-group'>"+
						 "      <div class='sm-thumb-btn' onclick='openUploadWindow(this);'>"+
						 "         <div class='thumb-img'><img src='../images/icons/sm-thumb.jpg'/></div>"+
						 "         <div class='textLabelimagePathId"+imageCount+"'>Upload</div>"+
						 "      </div>"+
						 "      <input class='dis-none ImageChoiceRequired upload-image' data-imageId='"+imageCount+"' name='questionResponseSubTypeList["+imageCount+"].imageFile' id='imageFileId"+imageCount+"' type='file'  accept='.png, .jpg, .jpeg' onchange='readURL(this);' required>"+
						 "		<input type='hidden' name='questionResponseSubTypeList["+imageCount+"].image' id='imagePathId"+imageCount+"' >"+
						 "      <div class='help-block with-errors red-txt'></div>"+
						 "   </div>"+
						 "</div>"+
						 "<div class='col-md-2 pl-none col-smthumb-2'>"+
						 "   <div class='form-group'>"+
						 "      <div class='sm-thumb-btn' onclick='openUploadWindow(this);'>"+
						 "         <div class='thumb-img'><img src='../images/icons/sm-thumb.jpg'/></div>"+
						 "         <div class='textLabelselectImagePathId"+imageCount+"'>Upload</div>"+
						 "      </div>"+
						 "      <input class='dis-none ImageChoiceRequired upload-image' data-imageId='"+imageCount+"' name='questionResponseSubTypeList["+imageCount+"].selectImageFile' id='selectImageFileId"+imageCount+"' type='file'  accept='.png, .jpg, .jpeg' onchange='readURL(this);' required>"+
						 "		<input type='hidden' name='questionResponseSubTypeList["+imageCount+"].selectedImage' id='selectImagePathId"+imageCount+"'>"+
						 "      <div class='help-block with-errors red-txt'></div>"+
						 "   </div>"+
						 "</div>"+
						 "<div class='col-md-3 pl-none'>"+
						 "   <div class='form-group'>"+
						 "      <input type='text' class='form-control ImageChoiceRequired' name='questionResponseSubTypeList["+imageCount+"].text' id='displayImageChoiceText"+imageCount+"' required maxlength='100'>"+
						 "      <div class='help-block with-errors red-txt'></div>"+
						 "   </div>"+
						 "</div>"+
						 "<div class='col-md-3 col-lg-3 pl-none'>"+
						 "   <div class='form-group'>"+
						 "      <input type='text' class='form-control ImageChoiceRequired imageChoiceVal' name='questionResponseSubTypeList["+imageCount+"].value' id='displayImageChoiceValue"+imageCount+"' required maxlength='50' onblur='validateForUniqueValue(this,&#34;Image Choice&#34;,function(){});'>"+
						 "      <div class='help-block with-errors red-txt'></div>"+
						 "   </div>"+
						 "</div>";
						 newImageChoice +="<div class='col-md-2 pl-none mt__6'>"+
						 "   <span class='addBtnDis addbtn mr-sm align-span-center top6' onclick='addImageChoice();'>+</span>"+
						 "	  <span class='delete vertical-align-middle remBtnDis hide pl-md align-span-center top0' onclick='removeImageChoice(this);'></span>"+
						 "</div>"+
						"</div> ";
	$(".image-choice:last").after(newImageChoice);
	$('.selectpicker').selectpicker('refresh');
	$(".image-choice").parent().removeClass("has-danger").removeClass("has-error");
    $(".image-choice").parent().find(".help-block").empty();
	$(".image-choice").parents("form").validator("destroy");
	$(".image-choice").parents("form").validator();
	
	if($('.image-choice').length > 2){
		$(".remBtnDis").removeClass("hide");
	}else{
		$(".remBtnDis").addClass("hide");
	}
	$('#'+imageCount).find('input:first').focus();
}
function removeImageChoice(param){
	if($('.image-choice').length > 2){
		$(param).parents(".image-choice").remove();
		$(".image-choice").parent().removeClass("has-danger").removeClass("has-error");
	    $(".image-choice").parent().find(".help-block").empty();
	    $(".image-choice").parents("form").validator("destroy");
		$(".image-choice").parents("form").validator();
		if($('.image-choice').length > 2){
			$(".remBtnDis").removeClass("hide");
		}else{
			$(".remBtnDis").addClass("hide");
		}
	}
}
function validateStatsShorTitle(event,callback){
	var short_title = $("#statShortNameId").val();
	var prev_short_title =$("#prevStatShortNameId").val();
	if(short_title != null && short_title !='' && typeof short_title!= 'undefined'){
		 $("#statShortNameId").parent().removeClass("has-danger").removeClass("has-error");
         $("#statShortNameId").parent().find(".help-block").empty();
 		if(prev_short_title !=short_title){
 			$.ajax({
                 url: "/fdahpStudyDesigner/adminStudies/validateStatsShortName.do?_S=${param._S}",
                 type: "POST",
                 datatype: "json",
                 data: {
                	 shortTitle : short_title
                 },
                 beforeSend: function(xhr, settings){
                     xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
                 },
                 success:  function getResponse(data){
                     var message = data.message;
                    
                     if('SUCCESS' != message){
                         $("#statShortNameId").validator('validate');
                         $("#statShortNameId").parent().removeClass("has-danger").removeClass("has-error");
                         $("#statShortNameId").parent().find(".help-block").empty();
                         if (callback)
     						callback(true);
                     }else{
                         $("#statShortNameId").val('');
                         $("#statShortNameId").parent().addClass("has-danger").addClass("has-error");
                         $("#statShortNameId").parent().find(".help-block").empty();
                         $("#statShortNameId").parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + short_title + "' has already been used in the past.</li></ul>");
                         if (callback)
     						callback(false);
                         
                     }
                 },
                 global : false
           });
 		}else{
 			if (callback)
				callback(true);
 			$("#statShortNameId").parent().removeClass("has-danger").removeClass("has-error");
 	        $("#statShortNameId").parent().find(".help-block").empty();
 				
 		}
 	}else{
 		if (callback)
			callback(true);
			
 	}
}
function validateTheQuestionshortTitle(item,callback){
	var shortTitle = $("#shortTitle").val();
 	var questionnaireId = $("#questionnairesId").val();
 	var stepType="Question";
 	var thisAttr=  $("#shortTitle");
 	var existedKey = $("#preShortTitleId").val();
 	var questionnaireShortTitle = $("#questionnaireShortId").val();
 	if(shortTitle != null && shortTitle !='' && typeof shortTitle!= 'undefined'){
 		$(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
        $(thisAttr).parent().find(".help-block").empty();
 		if(existedKey !=shortTitle){
 			$.ajax({
                 url: "/fdahpStudyDesigner/adminStudies/validateQuestionKey.do?_S=${param._S}",
                 type: "POST",
                 datatype: "json",
                 data: {
                 	shortTitle : shortTitle,
                 	questionnaireId : questionnaireId,
                 	questionnaireShortTitle : questionnaireShortTitle
                 },
                 beforeSend: function(xhr, settings){
                     xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
                 },
                 success:  function getResponse(data){
                     var message = data.message;
                     
                     if('SUCCESS' != message){
                         $(thisAttr).validator('validate');
                         $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
                         $(thisAttr).parent().find(".help-block").empty();
                         callback(true);
                     }else{
                         $(thisAttr).val('');
                         $(thisAttr).parent().addClass("has-danger").addClass("has-error");
                         $(thisAttr).parent().find(".help-block").empty();
                         $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + shortTitle + "' has already been used in the past.</li></ul>");
                         callback(false);
                     }
                 },
                 global : false
           });
 		}else{
 			callback(true);
 			$(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
 	        $(thisAttr).parent().find(".help-block").html("");
 		}
 	}else{
 		callback(false);
 	}
}
function validateFractionDigits(item){
	var value = $(item).val();
	var minValue = $("#continuesScaleMinValueId").val();
	var maxValue = $("#continuesScaleMaxValueId").val();
	var defaultValue = $("#continuesScaleDefaultValueId").val();
	$(item).parent().addClass("has-danger").addClass("has-error");
    $(item).parent().find(".help-block").empty();
	if(value != ''){
		if(minValue !='' && maxValue != ''){
			var maxFracDigits=0;
			var minTemp=0;
			var maxTemp=0;
			//max value check
			if(parseFloat(maxValue)>0&&parseFloat(maxValue)<=1){
				maxTemp = 4;
			}else if(parseFloat(maxValue)>1&&parseFloat(maxValue)<=10){
				maxTemp = 3;
			}else if(parseFloat(maxValue)>10&&parseFloat(maxValue)<=100){
				maxTemp = 2;
			}else if(parseFloat(maxValue)>100&&parseFloat(maxValue)<=1000){
				maxTemp = 1;
			}else if(parseFloat(maxValue)>1000&&parseFloat(maxValue)<=10000){
				maxTemp = 0;
			}
			
			
			
			//min value check
			if(parseFloat(minValue)>=-10000&&parseFloat(minValue)<-1000){
				minTemp = 0;
			}else if(parseFloat(minValue)>=-1000&&parseFloat(minValue)<-100){
				minTemp = 1;
			}else if(parseFloat(minValue)>=-100&&parseFloat(minValue)<-10){
				minTemp = 2;
			}else if(parseFloat(minValue)>=-10&&parseFloat(minValue)<-1){
				minTemp = 3;
			}else if(parseFloat(minValue)>=-1){
				minTemp = 4;
			}
			
			
			
			maxFracDigits = (parseInt(maxTemp)>parseInt(minTemp)) ? parseInt(minTemp):parseInt(maxTemp);
			
			
			if(parseInt(value) <= parseInt(maxFracDigits)){
				$(item).validator('validate');
	    		$(item).parent().removeClass("has-danger").removeClass("has-error");
	            $(item).parent().find(".help-block").empty();
	            
	            $("#continuesScaleMinValueId").val(parseFloat(minValue).toFixed(value));
	            $("#continuesScaleMaxValueId").val(parseFloat(maxValue).toFixed(value));
	            if(defaultValue != ''){
	            	 $("#continuesScaleDefaultValueId").val(parseFloat(defaultValue).toFixed(value));
	            }
			}else{
				$(item).val('');
	    		$(item).parent().addClass("has-danger").addClass("has-error");
	            $(item).parent().find(".help-block").empty();
	            $(item).parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter a value in the range (0,x).</li></ul>");
			}
		}else{
			$(item).val('');
    		$(item).parent().addClass("has-danger").addClass("has-error");
            $(item).parent().find(".help-block").empty();
            $(item).parent().find(".help-block").append("<ul class='list-unstyled'><li>Please enter an minimum and maximum values </li></ul>");
		}
	}
}
function validateForUniqueValue(item,responsetype,callback){
	var isValid = true;
	if(responsetype == 'Text Scale'){
		var valueArray = new Array();
		$('.text-scale').each(function(){
			var id = $(this).attr("id");
			var diaplay_value = $("#displayTextSclValue"+id).val();
			$("#displayTextSclValue"+id).parent().removeClass("has-danger").removeClass("has-error");
            $("#displayTextSclValue"+id).parent().find(".help-block").empty();
			if(diaplay_value != ''){
				if(valueArray.indexOf(diaplay_value.toLowerCase()) != -1) {
					isValid=false;
					$("#displayTextSclValue"+id).val('');
		    		$("#displayTextSclValue"+id).parent().addClass("has-danger").addClass("has-error");
		            $("#displayTextSclValue"+id).parent().find(".help-block").empty();
		            $("#displayTextSclValue"+id).parent().find(".help-block").append("<ul class='list-unstyled'><li>The value should be unique </li></ul>");
		        }
		        else
		        valueArray.push(diaplay_value.toLowerCase());
			}else{
				
			}
			
		});
		callback(isValid);
	}else if(responsetype == "Value Picker"){
		var valueArray = new Array();
		$('.value-picker').each(function(){
			var id = $(this).attr("id");
			var diaplay_value = $("#displayValPickValue"+id).val();
			$("#displayValPickValue"+id).parent().removeClass("has-danger").removeClass("has-error");
            $("#displayValPickValue"+id).parent().find(".help-block").empty();
			if(diaplay_value != ''){
				
				if(valueArray.indexOf(diaplay_value.toLowerCase()) != -1) {
					isValid=false;
					$("#displayValPickValue"+id).val('');
		    		$("#displayValPickValue"+id).parent().addClass("has-danger").addClass("has-error");
		            $("#displayValPickValue"+id).parent().find(".help-block").empty();
		            $("#displayValPickValue"+id).parent().find(".help-block").append("<ul class='list-unstyled'><li>The value should be unique </li></ul>");
		            
		        }
		        else
		        valueArray.push(diaplay_value.toLowerCase());
			}else{
				
			}
			
		});
		callback(isValid);
	}else if(responsetype == "Image Choice"){
		var valueArray = new Array();
		$('.image-choice').each(function(){
			var id = $(this).attr("id");
			var diaplay_value = $("#displayImageChoiceValue"+id).val();
			$("#displayImageChoiceValue"+id).parent().removeClass("has-danger").removeClass("has-error");
            $("#displayImageChoiceValue"+id).parent().find(".help-block").empty();
			if(diaplay_value != ''){
				if(valueArray.indexOf(diaplay_value.toLowerCase()) != -1) {
					isValid=false;
					$("#displayImageChoiceValue"+id).val('');
		    		$("#displayImageChoiceValue"+id).parent().addClass("has-danger").addClass("has-error");
		            $("#displayImageChoiceValue"+id).parent().find(".help-block").empty();
		            $("#displayImageChoiceValue"+id).parent().find(".help-block").append("<ul class='list-unstyled'><li>The value should be unique </li></ul>");
		        }
		        else
		        valueArray.push(diaplay_value.toLowerCase());
			}else{
				
			}
			
		});
		callback(isValid);
	}else if(responsetype == "Text Choice"){
		var valueArray = new Array();
		$('.text-choice').each(function(){
			var id = $(this).attr("id");
			var diaplay_value = $("#displayTextChoiceValue"+id).val();
			$("#displayTextChoiceValue"+id).parent().removeClass("has-danger").removeClass("has-error");
            $("#displayTextChoiceValue"+id).parent().find(".help-block").empty();
			if(typeof diaplay_value != 'undefined' && diaplay_value != null && diaplay_value != ''){
				console.log(valueArray.indexOf(diaplay_value.toLowerCase()));
				if(valueArray.indexOf(diaplay_value.toLowerCase()) != -1) {
					isValid=false;
					$("#displayTextChoiceValue"+id).val('');
		    		$("#displayTextChoiceValue"+id).parent().addClass("has-danger").addClass("has-error");
		            $("#displayTextChoiceValue"+id).parent().find(".help-block").empty();
		            $("#displayTextChoiceValue"+id).parent().find(".help-block").append("<ul class='list-unstyled'><li>The value should be unique </li></ul>");
		        }
		        else
		        valueArray.push(diaplay_value.toLowerCase());
			}else{
				
			}
			
		});
		callback(isValid);
	}
}
function removeImage(item){
	var id = $(item).parent().find('input').attr('id');
	var id2 = $(item).parent().find('input[type="hidden"]').attr('id')
	
	$("#"+id).val('');
	$("#"+id2).val('');
	$('.textLabel'+id2).text("Upload");
	$(item).parent().find('img').attr("src","../images/icons/sm-thumb.jpg");
	$(item).addClass("hide");
}
function validateAnchorDateText(item,callback){
 	var anchordateText = $("#anchorTextId").val();
 	var thisAttr= $("#anchorTextId");
 	var anchorDateId = '${questionsBo.anchorDateId}';
 	if(anchordateText != null && anchordateText !='' && typeof anchordateText!= 'undefined'){
 		var staticText ="Enrollment Date";
 		if(anchordateText.toUpperCase() === staticText.toUpperCase()){
 			$(thisAttr).val('');
            $(thisAttr).parent().addClass("has-danger").addClass("has-error");
            $(thisAttr).parent().find(".help-block").empty();
            $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + anchordateText + "' has already been used in the past.</li></ul>");
            callback(false);
 		}else{
 			$(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
 	        $(thisAttr).parent().find(".help-block").empty();
 	 			$.ajax({
 	                 url: "/fdahpStudyDesigner/adminStudies/validateAnchorDateName.do?_S=${param._S}",
 	                 type: "POST",
 	                 datatype: "json",
 	                 data: {
 	                	 anchordateText : anchordateText,
 	                	 anchorDateId : anchorDateId
 	                	 
 	                 },
 	                 beforeSend: function(xhr, settings){
 	                     xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
 	                 },
 	                 success:  function getResponse(data){
 	                     var message = data.message;
 	                     console.log(message);
 	                     if('SUCCESS' != message){
 	                         $(thisAttr).validator('validate');
 	                         $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
 	                         $(thisAttr).parent().find(".help-block").empty();
 	                         callback(true);
 	                     }else{
 	                         $(thisAttr).val('');
 	                         $(thisAttr).parent().addClass("has-danger").addClass("has-error");
 	                         $(thisAttr).parent().find(".help-block").empty();
 	                         $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + anchordateText + "' has already been used in the past.</li></ul>");
 	                         callback(false);
 	                     }
 	                 },
 	                 global : false
 	           });
 		}
 	}else{
 			callback(true);
 			$(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
 	        $(thisAttr).parent().find(".help-block").html("");
 	}
}
</script>