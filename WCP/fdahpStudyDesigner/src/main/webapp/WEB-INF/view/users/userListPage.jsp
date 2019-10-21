<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%> 
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 grayeef2f5-bg p-none">
     <div>
         <!-- widgets section-->
         <div class="col-sm-12 col-md-12 col-lg-12 p-none mb-md">
         
            <div class="black-lg-f" style="margin-top: 0px">
                Manage Users
            </div>
             
             <div class="dis-inline" style="margin-top: 8px">
              <form class="navbar-form" role="search">
              <div class="form-group mb-none mt-xs">
                <div class="input-group add-on">
                  <input placeholder="Search" class="c__search"  type="text" style="height: auto;
						 padding: 4px 20px;
						 background-position: left 7px top 6px;color: #000;"> 
                </div>
                </div>
              </form>
             </div>
             
             <div class="dis-line pull-right ml-md">
	             <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_USERS_EDIT')}">
	                 <div class="form-group mb-none mt-xs">
	                 	 <button type="button" class="btn btn-default gray-btn mr-sm" id="enforcePasswordId" style="margin-top: 12px;">Enforce Password Change</button>                 	
	                     <button type="button" class="btn btn-primary blue-btn addOrEditUser" style="margin-top: 12px;">Add User</button>
	                 </div>
	             </c:if>
             </div>
             <div class="dis-line pull-right" style="margin-top: 10px;height: auto;">
              	<div class="mb-none mt-xs">
                  <select class="selectpicker btn-md" id="filterRole">
                    <option value="" selected>Filter by Role</option>
                    <c:forEach items="${roleList}" var="role">
                        <option value="${role.roleName}">${role.roleName}</option>
                     </c:forEach>
                    </select>
                </div>
             </div>
                      
         </div>         
    </div>
    <div  class="clearfix"></div>
</div>
<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none"> 
    <div class="white-bg">
        <div>
            <table id="user_list" class="table tbl_rightalign tbl">
            <thead>
              <tr>
                <th>Name <span class="sort"></span></th>
                <th>Email  address <span class="sort"></span></th>
                <th>ROLE <span class="sort"></span></th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
            <c:forEach items="${userList}" var="user">
              <tr <c:if test="${fn:contains(aspList.capability , 'Project Lead' )}"> plRow </c:if>
              <c:if test="${fn:contains(aspList.capability , 'Coordinator' )}"> cRow </c:if>>
                <td><div class="dis-ellipsis" title="${fn:escapeXml(user.userFullName)}">${fn:escapeXml(user.userFullName)}</div></td>
                <td>
                	<div class="dis-ellipsis" title="${user.userEmail}">${user.userEmail}</div>
                </td>
                <td>${user.roleName}</td>
                <td>
                	<span class="sprites_icon preview-g mr-lg viewUser" userId="${user.userId}" data-toggle="tooltip" data-placement="top" title="View"></span>
                	<c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_USERS_EDIT')}">
	                    <span class="sprites_icon edit-g addOrEditUser <c:if test='${not empty user.userPassword &&  not user.enabled}'>cursor-none</c:if>" 
	                    	userId="${user.userId}" data-toggle="tooltip" data-placement="top" title="Edit"  id="editIcon${user.userId}">
                    	</span>
	                    <span class="ml-lg">
	                        <label class="switch" data-toggle="tooltip" id="label${user.userId}" data-placement="top"
	                        <c:if test="${empty user.userPassword}">title="Status: Invitation Sent, Account Activation Pending"</c:if>
	                        <c:if test="${user.emailChanged}">title="Status: Pending Verification"</c:if>
	                        <c:if test="${!user.emailChanged && not empty user.userPassword && user.enabled}">title="Status: Active"</c:if>
	                        <c:if test="${!user.emailChanged && not empty user.userPassword &&  not user.enabled}">title="Status: Deactivated"</c:if> >
	                          <input type="checkbox" class="switch-input" value="${user.enabled ? 1 : 0}" id="${user.userId}"
	                          <c:if test="${user.enabled}">checked</c:if> onchange="activateOrDeactivateUser(${user.userId})" 
	                          <c:if test="${empty user.userPassword || user.emailChanged}">disabled</c:if> >
	                          <span class="switch-label" data-on="On" data-off="Off"></span>
	                          <span class="switch-handle"></span>
	                        </label>
	                    </span>
                    </c:if>
                </td> 
              </tr>
              </c:forEach>
            </tbody>
          </table>
        </div>
  </div>
</div>

<form:form action="/fdahpStudyDesigner/adminUsersEdit/addOrEditUserDetails.do" id="addOrEditUserForm" name="addOrEditUserForm" method="post">
	<input type="hidden" id="userId" name="userId" value="">
	<input type="hidden" id="checkRefreshFlag" name="checkRefreshFlag">
</form:form>

<form:form action="/fdahpStudyDesigner/adminUsersView/viewUserDetails.do" id="viewUserForm" name="viewUserForm" method="post">
	<input type="hidden" id="usrId" name="userId" value="">
	<input type="hidden" id="checkViewRefreshFlag" name="checkViewRefreshFlag">
</form:form>

<script type="text/javascript">
$(document).ready(function(){
	$('#rowId').parent().removeClass('#white-bg');
	
	$('#users').addClass('active');
	
	$('[data-toggle="tooltip"]').tooltip();		
	
	<c:if test="${ownUser eq '1'}">
		bootbox.alert({
			closeButton: false,
			message : 'Your user account details have been updated. Please sign in again to continue using the portal.',	
		    callback: function(result) {
		    	var a = document.createElement('a');
		    	a.href = "/fdahpStudyDesigner/sessionOut.do";
				document.body.appendChild(a).click();
// 		        $('#logoutForm').submit();
		    }
	    });
	</c:if>
	
	$('.addOrEditUser').on('click',function(){
			$('#userId').val($(this).attr('userId'));
			$('#checkRefreshFlag').val('Y');
			$('#addOrEditUserForm').submit();
	});
	
	$('.viewUser').on('click',function(){
			$('#usrId').val($(this).attr('userId'));
			$('#checkViewRefreshFlag').val('Y');
			$('#viewUserForm').submit();
	});
    
	    $('#enforcePasswordId').on('click',function(){
	    	bootbox.confirm({
					closeButton: false,
					message : "Are you sure you wish to enforce a password change for all users?",	
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
			        		input.value= '';
			        		form.appendChild(input);
			        		
			        		var input= document.createElement('input');
			            	input.type= 'hidden';
			        		input.name= 'emailId';
			        		input.value= '';
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
	
  //User_List page Datatable
    table = $('#user_list').DataTable({
        "paging":   true,
        "searching": true,
        "filter": true,
        "info" : false, 
        "sDom": '"top"i',
        "aoColumns": [
           { "bSortable": true },
           { "bSortable": true },
           { "bSortable": true },
           { "bSortable": false }
          ],  
        "lengthChange": false, 
        "pageLength": 15 
    });
  
    $('.c__search').on('keyup',function(){
	      table.search($(this).val().replace(/(["])/g, "\ $1")).draw() ;
	}); 
 
     $('#filterRole').on('change',function(){
		var selected = $(this).find("option:selected").val();
		table.column(2).search(selected).draw();
     });
});



function activateOrDeactivateUser(userId){
	var status = $('#'+userId).val();
	var msgPart = "";
	if("0" == status){
		msgPart = "activate";
	} else if("1" == status){
		msgPart = "deactivate";
	}
  	 bootbox.confirm("Are you sure you want to " + msgPart + " this user?", function(result){
		 if(result){
			$.ajax({
				url : "/fdahpStudyDesigner/adminUsersEdit/activateOrDeactivateUser.do",
				type : "POST",
				datatype : "json",
				data : {
					userId : userId,
					userStatus : status,
					"${_csrf.parameterName}":"${_csrf.token}"
				},
				success : function(data) {
					var jsonObj = eval(data);
					var message = jsonObj.message;
					if(message == 'SUCCESS'){
						if(status == 1){
							showSucMsg('User successfully deactivated.');
							$('#'+userId).val("0");
							$('#label'+userId).attr('data-original-title','Status: Deactivated');
							$('#editIcon'+userId).addClass('cursor-none');
						}else{
							showSucMsg('User successfully activated.');
							$('#'+userId).val("1");
							$('#label'+userId).attr('data-original-title','Status: Active');
							$('#editIcon'+userId).removeClass('cursor-none');
						}
					}else {
						showErrMsg('Failed to update. Please try again.');
						if("0" == status){
							$('#'+userId).prop('checked', false);
						} else if("1" == checked){
							$('#'+userId).prop('checked', true);
						}
					}
				}
			});
	 	} else {
			if("0" == status){
				$('#'+userId).prop('checked', false);
			} else if("1" == status){
				$('#'+userId).prop('checked', true);
			}
			return;
		}
 	});
}
</script>