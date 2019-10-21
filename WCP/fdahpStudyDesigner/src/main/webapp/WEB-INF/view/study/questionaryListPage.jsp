<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<style>
.tool-tip {
  display: inline-block;
}

.tool-tip [disabled] {
  pointer-events: none;
}
</style>
 <!-- ============================================================== -->
         <!-- Start right Content here -->
         <!-- ============================================================== --> 
      <div class="col-sm-10 col-rc white-bg p-none">
            <input type="hidden" name="studyId" value="${studyBo.id}" id="studyId">
            <!--  Start top tab section-->
            <div class="right-content-head">        
                <div class="text-right">
                    <div class="black-md-f text-uppercase dis-line pull-left line34">QUESTIONNAIRES</div>
                    
                    <div class="dis-line form-group mb-none">
                         <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
                     </div>
                    
                     <!-- <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn">Save</button>
                     </div> -->
					<c:if test="${empty permission}">
                     <div class="dis-line form-group mb-none ml-sm">
                      <span class="tool-tip" id="markAsTooltipId"data-toggle="tooltip" data-placement="bottom" <c:if test="${!markAsComplete}"> title="${activityMsg}" </c:if> >
                         <button type="button" class="btn btn-primary blue-btn" id="markAsCompleteBtnId" onclick="markAsCompleted();" <c:if test="${!markAsComplete}"> disabled </c:if> >Mark as Completed</button>
                       </span>
                     </div>
                    </c:if>
                 </div>
            </div>
            <!--  End  top tab section-->
            
            
            
            <!--  Start body tab section -->
            <div class="right-content-body pt-none">
                <div>
                    <table id="questionnaire_list" class="display bor-none dragtbl" cellspacing="0" width="100%">
                         <thead>
                            <tr>
                            	<th style="display: none;"></th>
                                <th>TITLE<span class="sort"></span></th>
                                <th>FREQUENCY<span class="sort"></span></th>                                
                                <th>
                                <c:if test="${empty permission}">
                                    <div class="dis-line form-group mb-none">
                                         <button type="button" class="btn btn-primary blue-btn" onclick="addQuestionnaires();">Add Questionnaire</button>
                                     </div>
                                 </c:if>    
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                          <c:forEach items="${questionnaires}" var="questionnaryInfo">
		             	    <tr>
		             	      <td>${questionnaryInfo.createdDate}</td>
			                  <td><div class="dis-ellipsis pr-100" title="${fn:escapeXml(questionnaryInfo.title)}">${questionnaryInfo.title}</div></td>
			                  <td>${questionnaryInfo.frequency == 'Manually Schedule' ? 'Custom Schedule' :questionnaryInfo.frequency}</td>
			                  <td style="width:200px !important;">
			                   	 <span class="sprites_icon preview-g mr-lg" data-toggle="tooltip" data-placement="top" title="View" onclick="viewQuestionnaires(${questionnaryInfo.id});"></span>
			                     <span class="${questionnaryInfo.status?'edit-inc':'edit-inc-draft mr-md'} mr-lg <c:if test="${not empty permission}"> cursor-none </c:if>" data-toggle="tooltip" data-placement="top" title="Edit" onclick="editQuestionnaires(${questionnaryInfo.id});"></span>
			                     <span class="sprites_icon copy  mr-lg<c:if test="${not empty permission}"> cursor-none </c:if>" data-toggle="tooltip" data-placement="top" title="Copy" onclick="copyQuestionnaire(${questionnaryInfo.id});"></span>
			                     <span class="sprites_icon copy delete <c:if test="${not empty permission}"> cursor-none </c:if>" data-toggle="tooltip" data-placement="top" title="Delete" onclick="deleteQuestionnaire(${questionnaryInfo.id});"></span>
			                  </td>
			               </tr>
			             </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
            <!--  End body tab section -->
            
            
            
            
        </div>
        <!-- End right Content here -->
        <form:form action="/fdahpStudyDesigner/adminStudies/viewQuestionnaire.do?_S=${param._S}" name="questionnaireInfoForm" id="questionnaireInfoForm" method="post">
			<input type="hidden" name="questionnaireId" id="questionnaireId" value="">
			<input type="hidden" name="actionType" id="actionType"> 
			<input type="hidden" name="studyId" id="studyId" value="${studyId}" />
		</form:form>
<script>
$(document).ready(function(){  
			$('[data-toggle="tooltip"]').tooltip();
			$(".menuNav li.active").removeClass('active');
			$(".sixthQuestionnaires").addClass('active');
	
             $('#questionnaire_list').DataTable( {
                 "paging":   true,
                 "abColumns": [
                   { "bSortable": true },
                    { "bSortable": true },
                    { "bSortable": true }
                   ],
                   "order": [[ 0, "desc" ]],
                 "info" : false, 
                 "lengthChange": false, 
                 "searching": false, 
                 "pageLength": 10,
                 "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
        	    	 $('td:eq(0)', nRow).addClass("dis-none");
        	      }
             } );  
             
            if(document.getElementById("markAsCompleteBtnId") != null && document.getElementById("markAsCompleteBtnId").disabled){
         		$('[data-toggle="tooltip"]').tooltip();
         	}

  });
  function editQuestionnaires(questionnaryId){
	console.log("consentInfoId:"+questionnaryId);
	if(questionnaryId != null && questionnaryId != '' && typeof questionnaryId !='undefined'){
		$("#actionType").val('edit');
		$("#questionnaireId").val(questionnaryId);
		$("#questionnaireInfoForm").submit();
    }
  }    
  function viewQuestionnaires(questionnaryId){
		console.log("consentInfoId:"+questionnaryId);
		if(questionnaryId != null && questionnaryId != '' && typeof questionnaryId !='undefined'){
			$("#actionType").val('view');
			$("#questionnaireId").val(questionnaryId);
			$("#questionnaireInfoForm").submit();
	    }
	  }  
  function copyQuestionnaire(questionnaryId){
	  console.log("consentInfoId:"+questionnaryId);
		if(questionnaryId != null && questionnaryId != '' && typeof questionnaryId !='undefined'){
			$("#questionnaireId").val(questionnaryId);
			$("#actionType").val('edit');
			document.questionnaireInfoForm.action="/fdahpStudyDesigner/adminStudies/copyQuestionnaire.do?_S=${param._S}";	 
			document.questionnaireInfoForm.submit();
	    }
  }
  function addQuestionnaires(){
	$("#actionType").val('add');
	$("#questionnaireId").val('');
	$("#questionnaireInfoForm").submit();
  }   
  function deleteQuestionnaire(questionnaireId){
	  var studyId = $("#studyId").val();
	  bootbox.confirm({
		    message: "Are you sure you want to delete this questionnaire item? This item will no longer appear on the mobile app or admin portal. Response data already gathered against this item, if any, will still be available on the response database.",
		    buttons: {
		        confirm: {
		            label: 'Yes',
		        },
		        cancel: {
		            label: 'No',
		        }
		    },
		    callback: function (result) {
		    	if(result){
					if(questionnaireId != null && questionnaireId != '' && typeof questionnaireId !='undefined'){
						$.ajax({
			    			url: "/fdahpStudyDesigner/adminStudies/deleteQuestionnaire.do?_S=${param._S}",
			    			type: "POST",
			    			datatype: "json",
			    			data:{
			    				questionnaireId: questionnaireId,
			    				studyId : studyId,
			    				"${_csrf.parameterName}":"${_csrf.token}",
			    			},
			    			success: function deleteConsentInfo(data){
			    				var jsonobject = eval(data);
			    				var status = jsonobject.message;
			    				var markAsComplete = data.markAsComplete;
			    				var activityMsg = data.activityMsg;
			    				if(status == "SUCCESS"){
			    					$("#alertMsg").removeClass('e-box').addClass('s-box').html("Questionnaire deleted successfully");
			    					$('#alertMsg').show();
			    					var questionnaireList = jsonobject.questionnaireList;
			    					reloadDataTabel(questionnaireList);
			    					if($('.sixthQuestionnaires').find('span').hasClass('sprites-icons-2 tick pull-right mt-xs')){
			    						$('.sixthQuestionnaires').find('span').removeClass('sprites-icons-2 tick pull-right mt-xs');
			    					}
			    					if(!markAsComplete){
			    						$('#markAsCompleteBtnId').prop('disabled',true);
			    						$('#markAsTooltipId').attr("data-original-title", activityMsg);
			    					}else{
			    						$('#markAsCompleteBtnId').prop('disabled',false);
			    						$('#markAsTooltipId').removeAttr('data-original-title');
			    					}
			    				}else{
			    					if(status == 'FAILUREanchorused'){
			    						$("#alertMsg").removeClass('s-box').addClass('e-box').html("Questionnaire already live anchorbased.unable to delete");
			    					}else{
			    						$("#alertMsg").removeClass('s-box').addClass('e-box').html("Unable to delete consent");
				    					
			    					}
			    					$('#alertMsg').show();
			    	            }
			    				setTimeout(hideDisplayMessage, 4000);
			    			},
			    			error: function(xhr, status, error) {
			    			  $("#alertMsg").removeClass('s-box').addClass('e-box').html(error);
			    			  setTimeout(hideDisplayMessage, 4000);
			    			}
			    		});
					}
				}
		    }
	  });
  }
  function reloadDataTabel(questionnaireList){
	  $('#questionnaire_list').DataTable().clear();
	  if (typeof questionnaireList != 'undefined' && questionnaireList != null && questionnaireList.length >0){
		  $.each(questionnaireList, function(i, obj) {
             var datarow = [];
 			 if(typeof obj.createdDate === "undefined" && typeof obj.createdDate === "undefined" ){
 					datarow.push(' ');
 			 }else{
 					datarow.push(obj.createdDate);
 			 }
 			 if(typeof obj.title === "undefined" && typeof obj.title === "undefined" ){
					datarow.push(' ');
			 }else{
					datarow.push('<div class="dis-ellipsis pr-100" title="obj.title">'+obj.title+'</div>');
			 }	
 			 if(typeof obj.frequency === "undefined" && typeof obj.frequency === "undefined" ){
					datarow.push(' ');
			 }else{
					datarow.push(obj.frequency);
			 }	
 			 var actionDiv = "<span class='sprites_icon preview-g mr-lg' data-toggle='tooltip' data-placement='top' title='View' onclick='viewQuestionnaires("+obj.id+");'></span>";
 			 if(obj.status){
 				actionDiv += "<span class='sprites_icon edit-g mr-lg' data-toggle='tooltip' data-placement='top' title='Edit' onclick='editQuestionnaires("+obj.id+");'></span>";
 			 }else{
 				actionDiv += "<span class='edit-inc-draft mr-md mr-lg' data-toggle='tooltip' data-placement='top' title='Edit' onclick='editQuestionnaires("+obj.id+");'></span>";
 			 }
 			 actionDiv += "<span class='sprites_icon copy  mr-lg' data-toggle='tooltip' data-placement='top' title='Copy' onclick='copyQuestionnaire("+obj.id+");'></span>";
 			 
 			 actionDiv += "<span class='sprites_icon copy delete' data-toggle='tooltip' data-placement='top' title='Delete' onclick='deleteQuestionnaire("+obj.id+");'></span>";
             datarow.push(actionDiv);
             $('#questionnaire_list').DataTable().row.add(datarow);
		  });
		  $('#questionnaire_list').DataTable().draw();
	  }
	 else{
		  $('#questionnaire_list').DataTable().draw();
		  $("#markAsCompleteBtnId").prop("disabled",false);
		  $("#markAsTooltipId").removeAttr('data-original-title');
	  }
	  $('[data-toggle="tooltip"]').tooltip();
  }
  function markAsCompleted(){
		document.questionnaireInfoForm.action="/fdahpStudyDesigner/adminStudies/questionnaireMarkAsCompleted.do?_S=${param._S}";	 
		document.questionnaireInfoForm.submit();
	}
</script>     
        
        