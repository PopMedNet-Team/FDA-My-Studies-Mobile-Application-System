<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mt-md mb-md">

	<!-- widgets section-->
	<div class="col-sm-12 col-md-12 col-lg-12 p-none">
		<div class="black-lg-f">
			<span class="mr-xs"><a href="javascript:void(0)"
				class="backOrCancelBttn"><img
					src="/fdahpStudyDesigner/images/icons/back-b.png" /></a> </span>
			<c:if test="${actionPage eq 'ADD_PAGE'}">
              	Add New User
              </c:if>
			<c:if test="${actionPage eq 'EDIT_PAGE'}">
              	Edit User Details
              </c:if>
			<c:if test="${actionPage eq 'VIEW_PAGE'}">
              	User Details
              </c:if>

		</div>
		<c:if test="${actionPage eq 'EDIT_PAGE' || actionPage eq 'VIEW_PAGE'}">
			<div class="dis-line pull-right">
				<div class="form-group mb-none">
					<c:if
						test="${not empty userBO.userPassword && userBO.enabled && not userBO.emailChanged}">
						<div class="dis-inline mt-sm">
							<span class="stat"><span class="black-sm-f">Status:<span
									class="gray-xs-f mb-xs pl-xs"> Active</span></span></span>
						</div>
					</c:if>
					<c:if
						test="${not empty userBO.userPassword &&  not userBO.enabled}">
						<div class="dis-inline mt-sm">
							<span class="black-sm-f">Status:<span
								class="gray-xs-f mb-xs pl-xs"> Deactivated</span></span>
						</div>
					</c:if>
					<c:if test="${empty userBO.userPassword}">
						<div class="dis-inline mt-sm">
							<span class="black-sm-f">Status:<span
								class="gray-xs-f mb-xs pl-xs pr-md"> Invitation Sent,
									Account Activation Pending</span></span><span class="black-sm-f resend pl-md"><a
								href="javascript:void(0)" id="resendLinkId">Re-send
									Activation Link</a></span>
						</div>
					</c:if>
					<c:if test="${userBO.emailChanged}">
						<div class="dis-inline mt-sm">
							<span class="black-sm-f">Status:<span
								class="gray-xs-f mb-xs pl-xs"> Pending Verification</span></span>
						</div>
					</c:if>
				</div>
			</div>
		</c:if>

	</div>

</div>

<form:form
	action="/fdahpStudyDesigner/adminUsersEdit/addOrUpdateUserDetails.do"
	data-toggle="validator" id="userForm" role="form" method="post"
	autocomplete="off">
	<input type="hidden" name="userId" value="${userBO.userId}">
	<input type="hidden" id="userStatus" name="enabled"
		value="${userBO.enabled}">
	<input type="hidden" id="selectedStudies" name="selectedStudies">
	<input type="hidden" id="permissionValues" name="permissionValues">
	<input type="hidden" name="ownUser" id="ownUser">
	<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
		<div class="white-bg box-space">
			<c:if
				test="${actionPage eq 'EDIT_PAGE' && not empty userBO.userPassword && not userBO.emailChanged}">
				<div class="gray-xs-f text-weight-semibold pull-right">
					<button type="button" class="btn btn-default gray-btn"
						id="enforcePasswordId">Enforce Password Change</button>
				</div>
			</c:if>
			<div class="ed-user-layout row">
				<!-- Edit User Layout-->

				<div class="blue-md-f text-uppercase mb-md">Basic Information</div>
				<div class="col-md-12 p-none">
					<!-- form- input-->
					<div class="col-md-6 pl-none">
						<div class="gray-xs-f mb-xs">
							First Name
							<c:if test="${actionPage ne 'VIEW_PAGE'}">&nbsp;<small>(50
									characters max)</small>
							</c:if>
							<span class="requiredStar"> *</span>
						</div>
						<div class="form-group">
							<input autofocus="autofocus" type="text" class="form-control"
								name="firstName" value="${fn:escapeXml(userBO.firstName)}"
								maxlength="50" required
								<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if> />
							<div class="help-block with-errors red-txt"></div>
						</div>
					</div>
					<!-- form- input-->
					<div class="col-md-6 pr-none">
						<div class="gray-xs-f mb-xs">
							Last Name
							<c:if test="${actionPage ne 'VIEW_PAGE'}">&nbsp;<small>(50
									characters max)</small>
							</c:if>
							<span class="requiredStar"> *</span>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="lastName"
								value="${fn:escapeXml(userBO.lastName)}" maxlength="50" required
								<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if> />
							<div class="help-block with-errors red-txt"></div>
						</div>
					</div>
				</div>


				<div class="col-md-12 p-none">
					<!-- form- input-->
					<div class="col-md-6 pl-none">
						<div class="gray-xs-f mb-xs">
							Email Address
							<c:if test="${actionPage ne 'VIEW_PAGE'}">&nbsp;<small>(100
									characters max)</small>
							</c:if>
							<span class="requiredStar"> *</span>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" id="emailId"
								name="userEmail" value="${userBO.userEmail}"
								oldVal="${userBO.userEmail}"
								pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$"
								data-pattern-error="Email address is invalid" maxlength="100"
								required 
                                <%-- <c:if test="${actionPage eq 'VIEW_PAGE' || (empty userBO.userPassword && not empty userBO) || not empty userBO}">disabled</c:if>/> --%>
                                <c:if test="${actionPage eq 'VIEW_PAGE' || (empty userBO.userPassword && not empty userBO)}">disabled</c:if> />
							<div class="help-block with-errors red-txt"></div>
						</div>
					</div>
					<!-- form- input-->
					<div class="col-md-6 pr-none">
						<div class="gray-xs-f mb-xs">
							Phone (XXX - XXX - XXXX)<span class="requiredStar"> *</span>
						</div>
						<div class="form-group">
							<input type="text" class="form-control phoneMask"
								name="phoneNumber" value="${userBO.phoneNumber}"
								data-minlength="12" maxlength="12" required
								<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if> />
							<div class="help-block with-errors red-txt"></div>
						</div>
					</div>
				</div>

				<div class="clearfix"></div>
				<!-- Assign Role Section -->
				<div class="col-md-12 p-none">
					<!-- form- input-->
					<div class="col-md-6 pl-none">
						<div class="blue-md-f text-uppercase mt-lg mb-md">
							Assign Role<span class="requiredStar"> *</span>
						</div>
						<div class="form-group">
							<!-- <input type="text" class="form-control"/> -->
							<select id="roleId"
								class="selectpicker <c:if test="${actionPage eq 'VIEW_PAGE'}">linkDis</c:if>"
								name="roleId" required>
								<option value="" selected disabled>- Select Role -</option>
								<c:forEach items="${roleBOList}" var="role">
									<option ${role.roleId eq userBO.roleId ? 'selected' : ''}
										value="${role.roleId}">${role.roleName}</option>
								</c:forEach>
							</select>
							<div class="help-block with-errors red-txt"></div>
						</div>
					</div>
					<c:if test="${actionPage ne 'ADD_PAGE'}">
						<div class="col-md-6 pl-none">
							<div class="blue-md-f text-uppercase mt-lg mb-md">&nbsp;&nbsp;&nbsp;&nbsp;Activate
								/ Deactivate</div>
							<div class="form-group mb-none">
								<c:if
									test="${actionPage eq 'EDIT_PAGE' || actionPage eq 'VIEW_PAGE'}">
									<span class="ml-xs">&nbsp; <label
										class="switch bg-transparent mt-xs"> <input
											type="checkbox" class="switch-input"
											value="${userBO.enabled}" id="change${userBO.userId}"
											<c:if test="${userBO.enabled}">checked</c:if>
											<c:if test="${empty userBO.userPassword || actionPage eq 'VIEW_PAGE' || userBO.emailChanged}">disabled</c:if>
											onclick="activateOrDeactivateUser(${userBO.userId});">
											<span class="switch-label bg-transparent" data-on="On"
											data-off="Off"></span> <span class="switch-handle"></span>
									</label>
									</span>
								</c:if>
							</div>
						</div>
					</c:if>
				</div>

				<div class="clearfix"></div>
				<!-- Assign Permissions -->
				<div class="blue-md-f text-uppercase mt-lg">Assign Permissions</div>
				<div class="pull-right mb-xs">
					<span class="gray-xs-f">View Only</span> <span
						class="gray-xs-f ml-lg">View & Edit</span>
				</div>
				<div class="clearfix"></div>

				<!-- Gray Widget-->
				<div class="edit-user-list-widget">
					<span class="checkbox checkbox-inline"> <input
						type="checkbox" class="chk" id="inlineCheckbox1" value="option1"
						<c:if test="${fn:contains(permissions,7)}">checked</c:if>
						<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
						<label for="inlineCheckbox1"> Manage Users </label>
					</span> <span class="pull-right"> <span
						class="radio radio-info radio-inline p-45"> <input
							type="radio" class="musr" id="inlineRadio1" value="0"
							name="manageUsers"
							<c:if test="${fn:contains(permissions,7)}">checked</c:if>
							<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
							<label for="inlineRadio1"></label>
					</span> <span class="radio radio-inline"> <input type="radio"
							class="musr" id="inlineRadio2" value="1" name="manageUsers"
							<c:if test="${fn:contains(permissions,5)}">checked</c:if>
							<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
							<label for="inlineRadio2"></label>
					</span>
					</span>
				</div>

				<!-- Gray Widget-->
				<!--  <div class="edit-user-list-widget">
                     <span class="checkbox checkbox-inline">
                        <input type="checkbox" id="inlineCheckbox2" value="option1">
                        <label for="inlineCheckbox2"> Manage Repository </label>
                    </span>
                    <span class="pull-right">
                        <span class="radio radio-info radio-inline p-45">
                            <input type="radio" id="inlineRadio3" value="option1" name="radioInline2">
                            <label for="inlineRadio3"></label>
                        </span>
                        <span class="radio radio-inline">
                            <input type="radio" id="inlineRadio4" value="option1" name="radioInline2">
                            <label for="inlineRadio4"></label>
                        </span>
                    </span>
                </div> -->

				<!-- Gray Widget-->
				<div class="edit-user-list-widget">
					<span class="checkbox checkbox-inline"> <input
						type="checkbox" id="inlineCheckbox3" class="chk" value="option1"
						<c:if test="${fn:contains(permissions,4)}">checked</c:if>
						<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
						<label for="inlineCheckbox3"> Manage App-Wide
							Notifications </label>
					</span> <span class="pull-right"> <span
						class="radio radio-info radio-inline p-45"> <input
							type="radio" id="inlineRadio5" class="mnotf" value="0"
							name="manageNotifications"
							<c:if test="${fn:contains(permissions,4)}">checked</c:if>
							<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
							<label for="inlineRadio5"></label>
					</span> <span class="radio radio-inline"> <input type="radio"
							id="inlineRadio6" class="mnotf" value="1"
							name="manageNotifications"
							<c:if test="${fn:contains(permissions,6)}">checked</c:if>
							<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
							<label for="inlineRadio6"></label>
					</span>
					</span>
				</div>

				<!-- Gray Widget-->
				<div class="edit-user-list-widget">
					<span class="checkbox checkbox-inline"> <input
						type="checkbox" id="inlineCheckbox4" name="manageStudies"
						<c:if test="${fn:contains(permissions,2)}">value="1" checked</c:if>
						<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
						<label for="inlineCheckbox4"> Manage Studies </label>
					</span>
					<div class="mt-lg pl-lg">
						<div class="pb-md bor-dashed">
							<span class="checkbox checkbox-inline"> <input
								type="checkbox" id="inlineCheckbox5" class="changeView1"
								name="addingNewStudy"
								value="${fn:contains(permissions,8)?'1':''}"
								<c:if test="${fn:contains(permissions,8)}"> checked</c:if>
								<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
								<label for="inlineCheckbox5"> Create New Studies </label>
							</span>
						</div>
						<div class="mt-md study-list mb-md addHide">
							<c:if test="${actionPage ne 'VIEW_PAGE'}">
								<select
									class="selectpicker col-md-6 p-none changeView <c:if test="${actionPage eq 'VIEW_PAGE'}">linkDis</c:if>"
									title="- Select and Add Studies -" multiple id="multiple">
									<c:forEach items="${studyBOList}" var="study">
										<option value="${study.id}" id="selectStudies${study.id}">${study.name}&nbsp;(${study.customStudyId})</option>
									</c:forEach>
								</select>
								<span class="study-addbtn changeView">+</span>
							</c:if>
						</div>
						<div class="addHide">
							<span class="mr-lg text-weight-semibold text-uppercase">Existing
								Studies</span>
							<c:if test="${actionPage ne 'VIEW_PAGE'}">
								<span class="ablue removeAll changeView">x Remove all</span>
							</c:if>
						</div>
						<!-- Selected Study items -->
						<div class="study-selected mt-md">
							<c:forEach items="${studyBOs}" var="study">
								<div class="study-selected-item selStd" id="std${study.id}">
									<input type="hidden" class="stdCls" id="${study.id}" name=""
										value="${study.id}"
										stdTxt="${study.name}&nbsp;(${study.customStudyId})"
										<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
									<c:if test="${actionPage ne 'VIEW_PAGE'}">
										<span class="mr-md"><img
											src="/fdahpStudyDesigner/images/icons/close.png"
											onclick="del(${study.id});" /></span>
									</c:if>
									<span>${study.name}&nbsp;(${study.customStudyId})</span> <span
										class="pull-right"> <span
										class="radio radio-info radio-inline p-45 mr-xs"> <input
											type="radio" class="v${study.id} changeView"
											id="v1${study.id}" name="radio${study.id}" value="0"
											<c:if test="${not study.viewPermission}">checked</c:if>
											<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
											<label for="v1${study.id}"></label></span> <span
										class="radio radio-inline"> <input type="radio"
											class="v${study.id} changeView" id="v2${study.id}"
											name="radio${study.id}" value="1"
											<c:if test="${study.viewPermission}">checked</c:if>
											<c:if test="${actionPage eq 'VIEW_PAGE'}">disabled</c:if>>
											<label for="v2${study.id}"></label>
									</span>
									</span>
								</div>
							</c:forEach>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${actionPage ne 'VIEW_PAGE'}">
		<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
			<div class="white-bg box-space t-bor text-right">
				<div class="dis-line text-right ml-md">
					<div class="dis-line form-group mb-none mr-sm">
						<button type="button"
							class="btn btn-default gray-btn backOrCancelBttn">Cancel</button>
					</div>
					<c:if test="${actionPage eq 'ADD_PAGE'}">
						<div class="dis-line form-group mb-none">
							<button type="button" class="btn btn-primary blue-btn addUpdate">Add</button>
						</div>
					</c:if>
					<c:if test="${actionPage eq 'EDIT_PAGE'}">
						<div class="dis-line form-group mb-none">
							<button type="button" class="btn btn-primary blue-btn addUpdate">Update</button>
						</div>
					</c:if>
				</div>
			</div>
		</div>
	</c:if>
</form:form>

<form:form action="/fdahpStudyDesigner/adminUsersView/getUserList.do"
	id="backOrCancelBtnForm" name="backOrCancelBtnForm" method="post">
</form:form>
<script>


    $(document).ready(function(){
    	$('#rowId').parent().removeClass('white-bg');
    	
    	<c:if test="${empty studyBOList && empty studyBOs}">
    		$('.addHide').hide();
    	</c:if>
    	
    	$('#users').addClass('active');
    	
    	$('[data-toggle="tooltip"]').tooltip();	
    	
    	var isManageStudyChecked =$("#inlineCheckbox4").is(":checked");
    	if(isManageStudyChecked){
    	
    	}else{
    	   $('#inlineCheckbox5').val('');
           $('#inlineCheckbox5').prop('checked',false);
           $('.changeView').prop('disabled',true);
           $('.changeView1').prop('disabled',true);
    	}
    	var role = '${userBO.roleName}';
    	if(role){
    	   setStudySettingByRole(role);
    	}
    	
    	$('#roleId').on('change', function() {
		  var element = $(this).find('option:selected').text(); 
		  setStudySettingByRole(element);
		});
    	
    	
   var countCall = 0;
   $(window).on('load',function(){
	   countCall = 1;
    	   	$('.selStd').each(function(){
        		var stdTxt = $(this).find('.stdCls').attr('stdTxt');
        		 $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(function(){
       	  		  var ltxt = $(this).text();
       	  		  var a = $.trim(ltxt);
       	  		  var b = $.trim(stdTxt);
       	          if(a == b){
       	        	 $(this).parent().parent().hide();
       	          }
       	      });
        	});
   }); 
   
   if(countCall == 0){
		$('.selStd').each(function(){
    		var stdTxt = $(this).find('.stdCls').attr('stdTxt');
    		 $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(function(){
   	  		  var ltxt = $(this).text();
   	  		  var a = $.trim(ltxt);
   	  		  var b = $.trim(stdTxt);
   	          if(a == b){
   	        	 $(this).parent().parent().hide();
   	          }
   	      });
    	});
   }
      $("#emailId").blur(function(){  
    	   var email = $('#emailId').val().toLowerCase();
    	   var emailCopy = $('#emailId').val();
    	   var oldEmail = $('#emailId').attr('oldVal');
    	   var isEmail;
    	   var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
    	   isEmail = regEX.test(email);
    	   if(isEmail && ('' === oldEmail || ('' !== oldEmail && oldEmail !== email))){
    	   	var csrfDetcsrfParamName = $('#csrfDet').attr('csrfParamName');
    	       var csrfToken = $('#csrfDet').attr('csrfToken');
    	       $('#emailId').parent().find(".help-block").html("<ul class='list-unstyled'><li></li></ul>");
    	       if(email !== ''){
    	           $.ajax({
    	               url: "/fdahpStudyDesigner/isEmailValid.do?"+csrfDetcsrfParamName+"="+csrfToken,
    	               type: "POST",
    	               datatype: "json",
    	               global : false,
    	               data: {
    	                   email : email,
    	               },
    	               success:  function getResponse(data){
    	                   var message = data.message;
    	                   if('SUCCESS' !== message){
                               $('#emailId').validator('validate');
                               $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
                               $('#emailId').parent().find(".help-block").html("");
                           }else{
                         	   $("body").removeClass("loading");
                               $('#emailId').val('');
                               $('#emailId').parent().addClass("has-danger").addClass("has-error");
                               $('#emailId').parent().find(".help-block").empty();
                               $('#emailId').parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + emailCopy + "' already exists.</li></ul>");
                           }
    	               }
    	         });
    	     }
    	   }else{
    	     $('#emailId').validator('validate');
    	     $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
    	     $('#emailId').parent().find(".help-block").html("");
    	   	}
    	   });
      
    	//cancel or back click
    	$('.backOrCancelBttn').on('click',function(){
    		$('#backOrCancelBtnForm').submit();
    	});
    	
    	
    	if($('#inlineCheckbox1').prop("checked") == false){
    		$('.musr').prop('checked',false);
    		$('.musr').prop('disabled',true);
    	}
    	
    	if($('#inlineCheckbox3').prop("checked") == false){
    		$('.mnotf').prop('checked',false);
    		$('.mnotf').prop('disabled',true);
    	}
    	
    	if($('#inlineCheckbox4').prop("checked") == false){
    		$('#inlineCheckbox5').prop('checked',false);
    		$('#inlineCheckbox5').prop('disabled',true);
    	}
    	
    	$('#inlineCheckbox1').on('click',function(){
    		if($(this).prop("checked") == true){
    			$('.musr').prop('disabled',false);
    			$('#inlineRadio1').prop('checked',true);
    		}
    		else if($(this).prop("checked") == false){
    			$('.musr').prop('checked',false);
        		$('.musr').prop('disabled',true);
    		}
    	});
    	
    	$('#inlineCheckbox3').on('click',function(){
    		if($(this).prop("checked") == true){
    			$('.mnotf').prop('disabled',false);
    			$('#inlineRadio5').prop('checked',true);
    		}
    		else if($(this).prop("checked") == false){
    			$('.mnotf').prop('checked',false);
        		$('.mnotf').prop('disabled',true);
    		}
    	});
    	
    	$('#inlineCheckbox4').on('click',function(){
   		   if($(this).prop("checked") == true){
               $(this).val(1);
               $('.changeView').prop('disabled',false);
               var element = $("#roleId option:selected").text();
               if(element == 'Org-level Admin'){
		         $('.changeView1').prop('disabled',true);
			   }else{
			       $('.changeView1').prop('disabled',false);
			   }
           }else if($(this).prop("checked") == false){
               $(this).val('');
               $('#inlineCheckbox5').val('');
               $('#inlineCheckbox5').prop('checked',false);
               $('.changeView').prop('disabled',true);
               $('.changeView1').prop('disabled',true);
           }
    	});
    	
    	$('#inlineCheckbox5').on('click',function(){
    		if($(this).prop("checked") == true){
                $(this).val(1);
            }
            else if($(this).prop("checked") == false){
                $(this).val('');
            }
    	});
     // Adding selected study items    
  $(".study-addbtn").click(function(){
	  
		  $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li.selected").hide();
	      
	      $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function(){
	         if($(this).text()=="- All items are already selected -"){
	           $(this).hide();  
	         }
	       });
	  
		$('#multiple :selected').each(function(i, sel){ 
								    var selVal = $(sel).val(); 
								    var selTxt = $(sel).text(); 
								    var existingStudyDiv = "<div class='study-selected-item selStd' id='std"+selVal+"'>"
									+"<input type='hidden' class='stdCls' id='"+selVal+"' name='' value='"+selVal+"'>"
						            +"<span class='mr-md cls cur-pointer'><img src='/fdahpStudyDesigner/images/icons/close.png' onclick='del("+selVal+");'/></span>"
						            +"<span>"+selTxt+"</span>"
						            +"<span class='pull-right'>"
						            +"<span class='radio radio-info radio-inline p-45 mr-xs'>"
						            +" <input type='radio' class='v"+selVal+" changeView' id='v1"+selVal+"' name='radio"+selVal+"' value='0' checked='checked'>"
						            +"<label for='v1"+selVal+"'></label></span>"
						            +"<span class='radio radio-inline'>"
						            +"<input type='radio' class='v"+selVal+" changeView' id='v2"+selVal+"' name='radio"+selVal+"' value='1'>"
						            +" <label for='v2"+selVal+"'></label>"
						            +"</span>"
						            +"</span>"
						            +"</div>";
						            
						            $('.study-selected').append(existingStudyDiv); 
		});
          
		 $(".selectpicker").selectpicker('deselectAll');
         var tot_items = $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").length;
         var count = $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li[style]").length;    
         if(count == tot_items){
             $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu").append('<li class="text-center">- All items are already selected -</li>');
         }
          
        });
     
//Removing selected study items
	$(".removeAll").click(function(){
		$(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li[style],.study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").show();
      $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function(){
          if($(this).text()=="- All items are already selected -"){
            $(this).hide();  
          }
      });
      $(".study-selected-item").remove();
  });
  
  $('.addUpdate').on('click',function(){
	  var email = $('#emailId').val();
      var oldEmail = $('#emailId').attr('oldVal');
      var isEmail;
      var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
      isEmail = regEX.test(email);
      if(isEmail && ('' === oldEmail || ('' !== oldEmail && oldEmail !== email))){
      	var csrfDetcsrfParamName = $('#csrfDet').attr('csrfParamName');
          var csrfToken = $('#csrfDet').attr('csrfToken');
          //var thisAttr= this;
          $('#emailId').parent().find(".help-block").html("<ul class='list-unstyled'><li></li></ul>");
              if(email !== ''){
            	  $("body").addClass("loading");
                  $.ajax({
                      url: "/fdahpStudyDesigner/isEmailValid.do?"+csrfDetcsrfParamName+"="+csrfToken,
                      type: "POST",
                      datatype: "json",
                      global : false,
                      data: {
                          email : email,
                      },
                      success:  function getResponse(data){
                          var message = data.message;
                          if('SUCCESS' !== message){
                              $('#emailId').validator('validate');
                              $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
                              $('#emailId').parent().find(".help-block").html("");
                              saveUser();
                          }else{
                        	  $("body").removeClass("loading");
                        	  isFromValid($('.addUpdate').parents('form'));
                              $('#emailId').val('');
                              $('#emailId').parent().addClass("has-danger").addClass("has-error");
                              $('#emailId').parent().find(".help-block").empty();
                              $('#emailId').parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + email + "' already exists.</li></ul>");
                          }
                      }
                });
            }
      }else{
    	  $('#emailId').validator('validate');
          $('#emailId').parent().removeClass("has-danger").removeClass("has-error");
          $('#emailId').parent().find(".help-block").html("");
    	  saveUser();
      }
  });
  
      $('#resendLinkId').on('click',function(){
    	    var form= document.createElement('form');
	    	form.method= 'post';
	    	var input= document.createElement('input');
	    	input.type= 'hidden';
			input.name= 'userId';
			input.value= '${userBO.userId}';
			form.appendChild(input);
			
			input= document.createElement('input');
	    	input.type= 'hidden';
			input.name= '${_csrf.parameterName}';
			input.value= '${_csrf.token}';
			form.appendChild(input);
			
	    	form.action= '/fdahpStudyDesigner/adminUsersEdit/resendActivateDetailsLink.do';
	    	document.body.appendChild(form);
	    	form.submit();
     });
      
     $('#enforcePasswordId').on('click',function(){
    	bootbox.confirm({
				closeButton: false,
				message : "Are you sure you wish to enforce a password change for this user?",	
			    buttons: {
			        'cancel': {
			            label: 'No',
			        },
			        'confirm': {
			            label: 'Yes',
			        },
			    },
			    callback: function(result) {
			        if (result) {
			        	var form= document.createElement('form');
		            	form.method= 'post';
		            	var input= document.createElement('input');
		            	input.type= 'hidden';
		        		input.name= 'changePassworduserId';
		        		input.value= '${userBO.userId}';
		        		form.appendChild(input);
		        		
		        		var input= document.createElement('input');
		            	input.type= 'hidden';
		        		input.name= 'emailId';
		        		input.value= '${userBO.userEmail}';
		        		form.appendChild(input);
		        		
		        		input= document.createElement('input');
		            	input.type= 'hidden';
		        		input.name= '${_csrf.parameterName}';
		        		input.value= '${_csrf.token}';
		        		form.appendChild(input);
		        		
		            	form.action= '/fdahpStudyDesigner/adminUsersEdit/enforcePasswordChange.do';
		            	document.body.appendChild(form);
		            	form.submit();
			             }	
			        }
		}) 
    	
     });
        
   });
    
    function del(id){
  	 	var atxt = $('#std'+id).children().text();
  	 	
	  	  $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li a span:first-child").each(function(){
	  		  var ltxt = $(this).text();
	  		  var a = $.trim(ltxt);
	  		  var b = $.trim(atxt);	  		  
	          if(a == b){
	        	 $(this).parent().parent().show();
	          }
	      });
	  	  
	  	 $(".study-list .bootstrap-select .dropdown-menu ul.dropdown-menu li").each(function(){
            if($(this).text()=="- All items are already selected -"){
              $(this).hide();  
            }
        });
	  	  
  	 	 $('#std'+id).remove();
  	 	
    }
    
    function activateOrDeactivateUser(userId){
    	var status = $('#change'+userId).val();
    	var msgPart = "";
    	if('false' == status){
    		msgPart = "activate";
    	} else {
    		msgPart = "deactivate";
    	}
    	bootbox.confirm("Are you sure you want to " + msgPart + " this user?", function(result){
    	if(result){
    	if(status == 'true'){
    		$('#change'+userId).val(false);
    		$('#userStatus').val(false);
    	} else {
    		$('#change'+userId).val(true);
    		$('#userStatus').val(true);
    	}
    	}else {
    		if(status == 'true'){
    			$('#change'+userId).prop('checked', true);
    			$('#userStatus').val(true);
    			
    		} else if(status == 'false'){
    			$('#change'+userId).prop('checked', false);
    			$('#userStatus').val(false);
    		}
    		return;
    	}
    	});
    }
    
    function saveUser(){
    	$('#emailId').prop('disabled',false);
    	var selectedStudies = "";
      	var permissionValues = "";
      	if(isFromValid($('.addUpdate').parents('form'))){
    	  	$('.selStd').each(function(){
    	  		var studyId = $(this).find('.stdCls').val();
    	  		var permissionValue = $('#std'+studyId).find('input[type=radio]:checked').val();
    	  		if(selectedStudies == ""){
    	  			selectedStudies = studyId;
    	  		}else{
    	  			selectedStudies += ","+studyId;
    	  		}
    	  		if(permissionValues == ""){
    	  			permissionValues = permissionValue;
    	  		}else{
    	  			permissionValues += ","+permissionValue;
    	  		}
    	  	});
    	  	$('#selectedStudies').val(selectedStudies);
    	  	$('#permissionValues').val(permissionValues);
    	  	<c:if test="${sessionObject.userId eq userBO.userId}">
    	  		$('#ownUser').val('1');
    	  	</c:if>
      		$('.addUpdate').parents('form').submit();	
      	}
    }
    function setStudySettingByRole(element){
      if(element == 'Org-level Admin'){
		     $('#inlineCheckbox1').prop('checked', false);
		     $('#inlineCheckbox3').prop('checked', false);
		     $('.changeView1').prop('checked', false);
		     $('.musr').prop('checked',false);
             $('.mnotf').prop('checked',false);
             $('.musr').prop('disabled',true);
             $('.mnotf').prop('disabled',true);
		     $('#inlineCheckbox1').attr('disabled',true);
		     $('#inlineCheckbox3').attr('disabled',true);
		     $('.changeView1').prop('disabled',true);
		     $('#inlineCheckbox5').prop('checked',false);
		     $('#inlineCheckbox5').attr('disabled',true);
		  }else{
		     $('#inlineCheckbox1').attr('disabled',false);
		     $('#inlineCheckbox3').attr('disabled',false);
		     $('#inlineCheckbox5').attr('disabled',false);
		  }
		  
    }
</script>

