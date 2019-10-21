<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- ============================================================== -->
         <!-- Start right Content here -->
         <!-- ============================================================== --> 
<style>
table.dataTable thead th:last-child{
    width: 100px !important;
}
</style>
       <div class="col-sm-10 col-rc white-bg p-none" id="settingId">
            <form:form action="/fdahpStudyDesigner/adminStudies/saveOrUpdateSettingAndAdmins.do?_S=${param._S}" data-toggle="validator" role="form" id="settingfoFormId"  method="post" autocomplete="off">
            <input type="hidden" name="buttonText" id="buttonText">
            <input type="hidden" id="settingsstudyId" name="id" value="${studyBo.id}">
            <input type="hidden" id="userIds" name="userIds">
            <input type="hidden" id="permissions" name="permissions">
            <input type="hidden" id="projectLead" name="projectLead">
            <!--  Start top tab section-->
            <div class="right-content-head">        
                <div class="text-right">
                    <div class="black-md-f text-uppercase dis-line pull-left line34">SETTINGS AND ADMINS <c:set var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>
                    
                    <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn cancelBut" id="cancelId">Cancel</button>
                     </div>
                     <c:if test="${(empty permission) && (sessionObject.role ne 'Org-level Admin')}">
                     <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn" id="saveId">Save</button>
                     </div>

                     <div class="dis-line form-group mb-none">
                         <button type="button" class="btn btn-primary blue-btn" id="completedId">Mark as Completed</button>
                     </div>
                     </c:if>
                 </div>
            </div>
            <!--  End  top tab section-->
            
            
            
            <!--  Start body tab section -->
            <div class="right-content-body col-xs-12">
                <!-- Start Section-->
                <div class="col-md-12 p-none">
                     <div class="gray-xs-f mb-sm">Platform(s) Supported<span class="requiredStar"> *</span> <span class="sprites_v3 filled-tooltip" id="infoIconId"></span></div>
                     <div class="form-group">
                       <span class="checkbox checkbox-inline p-45">
                            <input class="platformClass" type="checkbox" id="inlineCheckbox1" name="platform" value="I" <c:if test="${fn:contains(studyBo.platform,'I')}">checked</c:if> <c:if test="${not empty studyBo.liveStudyBo && fn:contains(studyBo.liveStudyBo.platform,'I')}">disabled</c:if> data-error="Please check these box if you want to proceed." required >
                            <label for="inlineCheckbox1"> iOS </label>
                      </span>

                      <span class="checkbox checkbox-inline">
                            <input type="checkbox" class="platformClass" id="inlineCheckbox2" name="platform" value="A" <c:if test="${fn:contains(studyBo.platform,'A')}">checked</c:if> <c:if test="${not empty studyBo.liveStudyBo && fn:contains(studyBo.liveStudyBo.platform,'A')}">disabled</c:if> data-error="Please check these box if you want to proceed." required>
                            <label for="inlineCheckbox2"> Android </label>
                      </span>
                      <div class="help-block with-errors red-txt"></div>
                    </div>
                </div>
                <!-- End Section-->
                
                <!-- Start Section-->
                <div class="col-md-12 p-none">
                     <div class="gray-xs-f mb-sm">Allow participants to enroll?<span class="requiredStar"> *</span></div>

                     <div class="form-group">
                      <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="inlineRadio1" value="Yes" name="enrollingParticipants" <c:if test="${studyBo.enrollingParticipants eq 'Yes'}">checked</c:if> required>
                            <label for="inlineRadio1">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="inlineRadio2" value="No" name="enrollingParticipants" <c:if test="${studyBo.enrollingParticipants eq null}">checked</c:if> <c:if test="${studyBo.enrollingParticipants eq 'No'}">checked</c:if> required>
                            <label for="inlineRadio2">No</label>
                        </span>
                        <div class="help-block with-errors red-txt"></div>
                    </div>
                </div>
                <!-- End Section-->
                
                 <!-- Start Section-->
                <div class="col-md-12 p-none">
                     <div class="gray-xs-f mb-sm">Use Enrollment Date as Anchor Date in study activity scheduling?<span class="requiredStar"> *</span><span><span data-toggle="tooltip" data-placement="top" title="Select this option to distribute a questionnaire, active task or resource, N number of days after participant enrollment. N is configured in the schedule settings of that study activity or resource." class="filled-tooltip"></span></span></div>

                     <div class="form-group">
                      <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="inlineRadio11" value="Yes" name="enrollmentdateAsAnchordate" <c:if test="${studyBo.enrollmentdateAsAnchordate}">checked</c:if> required>
                            <label for="inlineRadio11">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="inlineRadio22" value="No" name="enrollmentdateAsAnchordate" ${isAnchorForEnrollmentLive?'disabled':''} <c:if test="${studyBo.enrollmentdateAsAnchordate eq false}">checked</c:if> required>
                            <label for="inlineRadio22">No</label>
                        </span>
                        <div class="help-block with-errors red-txt"></div>
                    </div>
                </div>
                <!-- End Section-->
                
                 <!-- Start Section-->
                 <div class="col-md-12 p-none">
                     <div class="gray-xs-f mb-sm">Retain participant data when they leave a study? <span class="requiredStar">*</span></div>

                     <div class="form-group">
                      <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="inlineRadio3" value="Yes" name="retainParticipant" <c:if test="${studyBo.retainParticipant eq 'Yes'}">checked</c:if> required>
                            <label for="inlineRadio3">Yes</label>
                        </span>
                        <span class="radio radio-inline p-45">
                            <input type="radio" id="inlineRadio4" value="No" name="retainParticipant" <c:if test="${studyBo.retainParticipant eq 'No'}">checked</c:if> required>
                            <label for="inlineRadio4">No</label>
                        </span>
                         <span class="radio radio-inline">
                            <input type="radio" id="inlineRadio5" value="All" name="retainParticipant" <c:if test="${studyBo.retainParticipant eq 'All'}">checked</c:if> required>
                            <label for="inlineRadio5">Allow participant to choose to have their data retained or deleted</label>
                        </span>
                        <div class="help-block with-errors red-txt"></div>
                    </div>
                </div>
                <!-- End Section-->
                
                 <!-- Start Section-->
                <div class="col-md-12 p-none">
                     <div class="gray-xs-f mb-sm">Allow users to rejoin a Study once they leave it? <span class="requiredStar">*</span></div>

                     <div class="form-group">
                      <span class="radio radio-info radio-inline p-45">
                            <input type="radio" class="rejoin_radio" id="inlineRadio6" value="Yes" name="allowRejoin" <c:if test="${studyBo.allowRejoin eq null}">checked</c:if> <c:if test="${studyBo.allowRejoin eq 'Yes'}">checked</c:if> required>
                            <label for="inlineRadio6">Yes</label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" class="rejoin_radio" id="inlineRadio7" value="No" name="allowRejoin" <c:if test="${studyBo.allowRejoin eq 'No'}">checked</c:if> required>
                            <label for="inlineRadio7">No</label>
                        </span>
                        <div class="help-block with-errors red-txt"></div>
                    </div>
                    
                    <div class="gray-xs-f ">Alert text for participants attempting to leave a study <span><span data-toggle="tooltip" data-placement="top" title="Enter a message that should be shown to participants when they attempt to leave the study indicating whether or not they have the option to re-join the study." class="filled-tooltip"></span></span></div>
                    
                    <div class="col-md-7 p-none mt-sm rejointextclassYes" style="display:none;">
                       <div class="form-group m-none elaborateClass">
                          <textarea class="form-control"  maxlength="250" rows="5" id="rejoin_comment_yes" data-error="Please enter plain text of up to 250 characters max." placeholder="Please enter text that the user should see when they leave a study to let them know whether they can or cannot Rejoin the study" >${fn:escapeXml(studyBo.allowRejoinText)}</textarea>
                          <div><small>(250 characters max)</small></div>
                          <div class="help-block with-errors red-txt"></div>
                        </div>
                    </div>
                    <div class="col-md-7 p-none mt-sm rejointextclassNo" style="display:none;">
                       <div class="form-group m-none elaborateClass">
                          <textarea class="form-control"  maxlength="250" rows="5" id="rejoin_comment_no" data-error="Please enter plain text of up to 250 characters max." placeholder="Please enter text that the user should see when they leave a study to let them know whether they can or cannot Rejoin the study" >${fn:escapeXml(studyBo.allowRejoinText)}</textarea>
                          <div><small>(250 characters max)</small></div>
                          <div class="help-block with-errors red-txt"></div>
                        </div>
                    </div>
                </div>
                 <!-- End Section-->
                
                 <!-- Start Section-->
                <c:if test="${fn:contains(permissions,7)}">
                <div>
                 <div class="black-md-f text-uppercase line34">MANAGE LIST OF ADMINS ASSIGNED TO THE STUDY</div>
                 <c:if test="${empty permission && fn:contains(permissions,5)}">
		            <div class="dis-line form-group mb-none">
							<button type="button" class="btn btn-primary blue-btn mb-sm mt-xs" onclick="addAdmin();">+ Add Admin</button>
					</div>
				</c:if>	
            
			<table id="studyAdminsTable" class="display bor-none" cellspacing="0" width="80%">
				<thead>
					<tr>
						<th>&nbsp;&nbsp;&nbsp;Admins</th>
						<th>View</th>
						<th>View & Edit</th>
						<th>Project Lead</th>
						<th></th>

					</tr>
				</thead>
				<tbody id="studyAdminId">
					<c:forEach items="${studyPermissionList}" var="perm">
							<tr id="studyAdminRowId${perm.userId}" class="studyAdminRowCls" studyUserId="${perm.userId}">
								<td><span class="dis-ellipsis" title="${fn:escapeXml(perm.userFullName)}">${perm.userFullName}</span></td>
								<td>
									<span class="radio radio-info radio-inline p-45">
	                            		<input type="radio" id="inlineRadio1${perm.userId}" class="radcls" value="0" name="view${perm.userId}" <c:if test="${not perm.viewPermission}">checked</c:if>>
	                            		<label for="inlineRadio1${perm.userId}"></label>
                        			</span>
								</td>
								<td align="center">
									<span class="radio radio-info radio-inline p-45">
	                            		<input type="radio" id="inlineRadio2${perm.userId}" class="radcls" value="1" name="view${perm.userId}" <c:if test="${perm.viewPermission}">checked</c:if>>
	                            		<label for="inlineRadio2${perm.userId}"></label>
                        			</span>
								</td>
								<td align="center">
									<span class="radio radio-info radio-inline p-45">
	                            		<input type="radio" id="inlineRadio3${perm.userId}" class="radcls leadCls" value="" name="projectLead" <c:if test="${perm.projectLead eq 1}">checked</c:if>>
	                            		<label for="inlineRadio3${perm.userId}"></label>
                        			</span>
								</td>
								<td align="center">
									<span class="sprites_icon copy delete <c:if test="${not empty permission || !fn:contains(permissions,5)}"> cursor-none </c:if>" 
									 onclick="removeUser(${perm.userId})" data-toggle="tooltip" data-placement="top" title="Delete"></span>
								</td>
							</tr>
					</c:forEach>
				</tbody>
			</table>
		<!-- </div> -->
	</div>
	</c:if>
                 <!-- End Section-->
                
              
            </div>
            <!--  End body tab section -->
           
            </form:form>
            
            
            
        </div>
        <!-- End right Content here -->
        
        <c:if test="${empty permission && fn:contains(permissions,5)}">
        <div class="col-sm-10 col-rc white-bg p-none" id="adminsId">
            <div class="right-content-head">        
                <div class="text-right">
                    <div class="black-md-f dis-line pull-left line34"><span class="pr-sm"><a href="javascript:void(0)" onclick="cancelAddAdmin();"><img src="/fdahpStudyDesigner/images/icons/back-b.png"/></a></span>Add Admins</div>
                    
                    <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn" onclick="cancelAddAdmin();">Cancel</button>
                     </div>
                     <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-primary blue-btn" id="addAdminsToStudyId" onclick="addAdminsToStudy()">Add</button>
                     </div>
                 </div>
            </div>
            <div class="right-content-body col-xs-12">
            	<!-- <div class="right-content-body pt-none pb-none"> -->
		<div>
			<table id="userListTable" class="display bor-none tbl_rightalign" cellspacing="0" width="100%">
				<thead>
					<tr>
						<th></th>
						<th>USERS<span class="sort"></span></th>
						<th>E-MAIL ADDRESS</th>
						<th style="width:100px !important">ROLE</th>

					</tr>
				</thead>
				<tbody>
					<c:forEach items="${userList}" var="user">
							<tr id="user${user.userId}" class="checkCount">
								<td style="position: inherit;">
									<span class="checkbox checkbox-inline" style="padding-top: 5px;">
                            			<input type="checkbox" class="addAdminCheckbox" id="inlineCheckboxNew${user.userId}" name="case" value="${fn:escapeXml(user.userFullName)}" userId="${user.userId}">
                            			<label for="inlineCheckboxNew${user.userId}"></label>
                      				</span>
								</td>
								<td><span class="dis-ellipsis" title="${fn:escapeXml(user.userFullName)}">${user.userFullName}</span></td>
								<td><span class="dis-ellipsis" title="${fn:escapeXml(user.userEmail)}">${user.userEmail}</span></td>
								<td>${user.roleName}</td>
							</tr>
					</c:forEach>
				</tbody>
			</table>
		<!-- </div> -->
	</div>
            </div>
        </div>
        </c:if>
<!-- Modal -->
<div class="modal fade" id="myModal" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
      
      <div class="modal-header cust-hdr pt-lg">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>
        <h4 class="modal-title pl-lg"><b>Platform and Feature Support</b></h4>       
      </div>
         <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
            <div>               
               <div>
                   <ul class="no-disc">
                     <li><strong>1. Platform Support: </strong><br />
                    <ul class="no-disc"><li> Note that once the study is Launched, platform support cannot be revoked. However, adding support for a platform not previously selected will still be possible.</li></ul></li>
                     <li>&nbsp;</li>
                     <li><strong>2. Feature Support on iOS and Android:</strong><br />
                     
	                      <ul class="no-disc">
	                      <li>Given below is a list of features currently NOT available for Android as compared to iOS. Please note the same in your creation of study questionnaires and active tasks.</li>
	                      <li>i. Activetasks: Activetask with type Tower Of Hanoi, Spatial Span Memory</li>
	                     </ul>
                     </li>
                    
                  </ul>
               </div>
            </div>
         </div>
      </div>
   </div>
</div>
<script>
$(document).ready(function(){
	
	<c:if test="${empty permission && fn:contains(permissions,5)}">
	
		<c:if test="${user eq 'logout_login_user'}">
			bootbox.alert({
				closeButton: false,
				message : 'Your user account details have been updated. Please sign in again to continue using the portal.',	
			    callback: function(result) {
			    	var a = document.createElement('a');
			    	a.href = "/fdahpStudyDesigner/sessionOut.do";
					document.body.appendChild(a).click();
			    }
		    });
		</c:if>
	
		$('[data-toggle="tooltip"]').tooltip();
	
		$('#adminsId').hide();
		
		$('.studyAdminRowCls').each(function(){
			  var userId = $(this).attr('studyUserId');
			  $('#user'+userId).removeClass('checkCount').hide();
		});

        $('#userListTable').DataTable({
            "columnDefs": [
                { "width": "100px", "targets": 3 }
            ],
         "paging":   false,
         "emptyTable": "No data available",
         "info" : false, 
         "lengthChange": true, 
         "searching": false, 
     });
		
		$('.addAdminCheckbox').on('click',function(){
			 var count = 0;
			 $('[name=case]:checked').each(function() {
				 count++;
			 });
			 if(count > 0){
				$('#addAdminsToStudyId').prop('disabled',false);
			 }else{
				$('#addAdminsToStudyId').prop('disabled',true);
			 }
		});
	</c:if>
	
		 table =  $('#studyAdminsTable').DataTable({
			        "paging":   false,
			        "aoColumns": [
					   { "width":'40%',"bSortable": false },
			           { "width":'10%',"bSortable": false },
			           { "width":'10%',"bSortable": false },
			           { "width":'10%',"bSortable": false },
			           { "width":'10%',"bSortable": false }
			          ],  
			        "info" : false, 
			        "lengthChange": true, 
			        "searching": false, 
		         });
	
		$(".menuNav li.active").removeClass('active');
	    $(".menuNav li.second").addClass('active');  
	    checkRadioRequired();
		$(".rejoin_radio").click(function(){
			checkRadioRequired();
		})
		<c:if test="${(not empty permission) || (sessionObject.role eq 'Org-level Admin')}">
            $('#settingfoFormId input,textarea,select').prop('disabled', true);
            $('#settingfoFormId').find('.elaborateClass').addClass('linkDis');
        </c:if>
        
        <c:if test="${!fn:contains(permissions,5)}">
	        $('.radcls').prop('disabled', true);
    	</c:if>
		
		$("#completedId").on('click', function(e){
			var rowCount = 0;
			if(isFromValid("#settingfoFormId")) {
				rowCount = $('.leadCls').length;
				if (rowCount != 0) {
				if ($("#studyAdminsTable .leadCls:checked").length > 0)
				{
					$('#completedId').prop('disabled',true);
					platformTypeValidation('completed');
				}
				else
				{
					bootbox.alert({
						closeButton: false,
						message : 'Please select one of the admin as a project lead',	
		    		});
				}
				}else{
					$('#completedId').prop('disabled',true);
					platformTypeValidation('completed');
				}
 			}
         });
         
         $("#saveId").click(function(){
        	 platformTypeValidation('save');
         });
         
         var allowRejoin = '${studyBo.allowRejoin}';
         if (allowRejoin != "") {
        	 if(allowRejoin == 'Yes'){
        	  $('.rejointextclassYes').show();
        	  $('#rejoin_comment_no').text('');
        	  $('.rejointextclassNo').hide();
        	 }else{
        	  $('.rejointextclassNo').show(); 
        	  $('.rejointextclassYes').hide();
        	  $('#rejoin_comment_yes').text(''); 
        	 }
         }
         $("[data-toggle=tooltip]").tooltip();
         $("#infoIconId").hover(function(){
         	$('#myModal').modal('show');
         });
});
function checkRadioRequired() {
	var rejoinRadioVal = $('input[name=allowRejoin]:checked').val();
	if(rejoinRadioVal=='Yes'){
		$('.rejointextclassYes').show();
		$('#rejoin_comment_yes').attr("required","required");
		$('#rejoin_comment_no').removeAttr("required");
		$('.rejointextclassNo').hide();
	}else{
		$('.rejointextclassNo').show();
		$('#rejoin_comment_no').attr("required","required");
		$('#rejoin_comment_yes').removeAttr("required");
		$('.rejointextclassYes').hide();
	}
}
function setAllowRejoinText(){
	var allowRejoin = $('input[name=allowRejoin]:checked').val();
	if(allowRejoin){
		if(allowRejoin =='Yes'){
			$('#rejoin_comment_yes').attr("name","allowRejoinText");
			$('#rejoin_comment_no').removeAttr("name","allowRejoinText");
		}else{
			$('#rejoin_comment_no').attr("name","allowRejoinText");
			$('#rejoin_comment_yes').removeAttr("name","allowRejoinText");
		}
	}
}
function platformTypeValidation(buttonText){
	var platformNames = '';
	$("input:checkbox[name=platform]:checked").each(function() {
		platformNames = platformNames + $(this).val();
	});
	var liveStudy = "${studyBo.liveStudyBo}";
	if(liveStudy){
		var platform = "${studyBo.liveStudyBo.platform}";
		if(platform.includes('A')){
			platformNames = '';
		}
	}
	if(platformNames!='' && platformNames.includes('A')){
		$('.actBut').prop('disabled',true);
		$("body").addClass("loading");
		$.ajax({
            url: "/fdahpStudyDesigner/adminStudies/studyPlatformValidationforActiveTask.do?_S=${param._S}",		
            type: "POST",
            datatype: "json",
            data: {
            	studyId : $('#settingsstudyId').val(),
                "${_csrf.parameterName}":"${_csrf.token}",
            },
            success: function platformValid(data, status) {
                var jsonobject = eval(data);
                var message = jsonobject.message;
                var errorMessage = jsonobject.errorMessage;
                $("body").removeClass("loading");
                if (message == "SUCCESS") {
                	$('#completedId').removeAttr('disabled');
                	bootbox.alert(errorMessage);
                }else{
                	submitButton(buttonText);
                }
            },
            error:function status(data, status) {
            	$("body").removeClass("loading");
            },
            complete : function(){ $('.actBut').removeAttr('disabled'); },
            global : false
        });
    }else{
    	submitButton(buttonText);
    }
}
function submitButton(buttonText){
	setAllowRejoinText();
	admins() //Pradyumn
	var isAnchorForEnrollmentDraft = '${isAnchorForEnrollmentDraft}';
	if(buttonText === 'save'){
		$('#settingfoFormId').validator('destroy');
		$("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
    	$("#buttonText").val('save');
        $("#settingfoFormId").submit();
	}else{
		var retainParticipant = $('input[name=retainParticipant]:checked').val();
		var enrollmentdateAsAnchordate = $('input[name=enrollmentdateAsAnchordate]:checked').val();
        if(retainParticipant){
        	if(retainParticipant=='All')
        		retainParticipant = 'Participant Choice';
			   bootbox.confirm({
				closeButton: false,
				message : 'You have selected "'+retainParticipant+'" for the retention of participant response data when they leave a study.'
				         +' Your Consent content must be worded to convey the same.'
				         +' Click OK to proceed with completing this section or Cancel if you wish to make changes.',	
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
// 			        	$("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
// 			        	$("#buttonText").val('completed');
// 	                    $("#settingfoFormId").submit();
	                    //phase2a anchor
	                    showWarningForAnchor(isAnchorForEnrollmentDraft, enrollmentdateAsAnchordate);
	                   //phase 2a anchor  
			        }else{
			        	$('#completedId').removeAttr('disabled');
			        }
			     }
				});
        }else{
        	$("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
        	$("#buttonText").val('completed');
	        $("#settingfoFormId").submit();
        }
	}
}

function admins(){
	var userIds = "";
	var permissions = "";
	var projectLead = "";
	$('.studyAdminRowCls').each(function(){
		var userId = $(this).attr('studyUserId');
		if(userIds == ""){
			userIds = userId;
		}else{
			userIds += ","+userId;
		}
		var permission = $(this).find('input[type=radio]:checked').val();
		if(permissions == ""){
			permissions = permission;
		}else{
			permissions += ","+permission;
		}
		if($(this).find('#inlineRadio3'+userId).prop('checked')){
			projectLead = userId;
		}
	});
	$('#userIds').val(userIds);
	$('#permissions').val(permissions);
	$('#projectLead').val(projectLead);
}

	<c:if test="${empty permission && fn:contains(permissions,5)}">
		function addAdmin(){
			 var userListTableRowCount = $('.checkCount').length;
			 if(userListTableRowCount == 0){
				 bootbox.alert({
								closeButton: false,
								message : 'There are currently no other admin users available to add to this study.',	
				    		  });
			 }else{
					 $('#settingId').hide();	
					 $('#adminsId').show();
					 $('#addAdminsToStudyId').prop('disabled',true);
			 }
		}

		function cancelAddAdmin(){
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
			        	$('#settingId').show();	
						$('#adminsId').hide();
						$('[name=case]:checked').each(function() {
							$(this).prop('checked',false);
						});
			        }
			    }
		    });
		}
		
		function addAdminsToStudy(){
			 $('#addAdminsToStudyId').attr('disabled',true);
			 $('[name=case]:checked').each(function() {
				 var name = escapeXml($(this).val());
				 var userId = $(this).attr('userId');
				 $('#user'+userId).removeClass('checkCount').hide();
				 $('#settingId').show();
				 $(this).prop('checked',false);
				 $('#adminsId').hide();
				 var domStr = '';
				      domStr = domStr + '<tr id="studyAdminRowId'+userId+'" role="row" class="studyAdminRowCls" studyUserId="'+userId+'">';
				      domStr = domStr + '<td><span class="dis-ellipsis" title="'+name+'">' + name +'</span></td>';
			          domStr = domStr + '<td><span class="radio radio-info radio-inline p-45">'+
			          					'<input type="radio" id="inlineRadio1'+userId+'" value="0" name="view'+userId+'" checked>'+
		    							'<label for="inlineRadio1'+userId+'"></label>'+
										'</span></td>';
			          domStr = domStr + '<td align="center"><span class="radio radio-info radio-inline p-45">'+
			          					'<input type="radio" id="inlineRadio2'+userId+'" value="1" name="view'+userId+'">'+
			          					'<label for="inlineRadio2'+userId+'"></label>'+
			          					'</span></td>';
					  domStr = domStr + '<td align="center"><span class="radio radio-info radio-inline p-45">'+
										'<input type="radio" id="inlineRadio3'+userId+'" class="leadCls" name="projectLead">'+
										'<label for="inlineRadio3'+userId+'"></label>'+
										'</span></td>';
					 domStr = domStr + '<td align="center"><span class="sprites_icon copy delete" onclick="removeUser('+userId+')" data-toggle="tooltip" data-placement="top" title="Delete"></span></td>';
			         domStr = domStr + '</tr>';
				 $('#studyAdminId').append(domStr);
				 $('[data-toggle="tooltip"]').tooltip();
				 $('.dataTables_empty').remove();
			 });
			 $('#addAdminsToStudyId').attr('disabled',false); 
		}
		
		function removeUser(userId){
			var userId = userId;
			var count = 0;
			$('.studyAdminRowCls').each(function(){
				count++;
			});
			if(count == 1){
				table.clear().draw();
			}
			$('#studyAdminRowId'+userId).remove();
			$('#user'+userId).addClass('checkCount').show();
		}
		
		function escapeXml(unsafe) {
		    return unsafe.replace(/[<>&'"]/g, function (c) {
		        switch (c) {
		            case '<': return '&lt;';
		            case '>': return '&gt;';
		            case '&': return '&amp;';
		            case '\'': return '&apos;';
		            case '"': return '&quot;';
		        }
		    });
		}
	</c:if>
	function showWarningForAnchor(isAnchorForEnrollmentDraft, enrollmentdateAsAnchordate){
		if(isAnchorForEnrollmentDraft == 'true' && enrollmentdateAsAnchordate=='No'){
        	var text = "You have chosen not to use Enrollment Date as an Anchor Date.You will need to revise the schedules of Target Activities or Resources,if any, that were set based on the Enrollment Date.Buttons: OK, Cancel.";
        	bootbox.confirm({
        		closeButton: false,
        		message: text,
        		buttons: {
			        'cancel': {
			            label: 'Cancel',
			        },
			        'confirm': {
			            label: 'OK',
			        },
			    },
			    callback: function(valid) {
			    	if (valid) {
			    		console.log(1);
			    		$("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
			        	$("#buttonText").val('completed');
	                    $("#settingfoFormId").submit();
			    	}else{
			    		console.log(2);
			    		$('#completedId').removeAttr('disabled');
			    	}
			    }
        	});
        }else{
        	$("#inlineCheckbox1,#inlineCheckbox2").prop('disabled', false);
        	$("#buttonText").val('completed');
        	$("#settingfoFormId").submit();
        }
	}
</script>