<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style>
<!--
.sorting, .sorting_asc, .sorting_desc {
    background : none !important;
}
-->
</style>
        <div class="col-sm-10 col-rc white-bg p-none">
            <!--  Start top tab section-->
            <div class="right-content-head">        
                <div class="text-right">
                    <div class="black-md-f text-uppercase dis-line pull-left line34">Notifications</div>
                    
                    <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
                     </div>
                     <c:if test="${empty permission}">
                     <div class="dis-line form-group mb-none" <c:if test="${not empty notificationSavedList}">data-toggle="tooltip" data-placement="bottom" title="Please ensure individual list items are marked Done, before marking the section as Complete"</c:if>>
                         <button type="button" class="btn btn-primary blue-btn markCompleted <c:if test="${not empty notificationSavedList}">linkDis</c:if>" onclick="markAsCompleted();"
                         >Mark as Completed</button>
                     </div>
                     </c:if>
                 </div>
            </div>
            <!--  End  top tab section-->
            <!--  Start body tab section -->
            <div class="right-content-body pt-none">
                <div>
                    <table id="notification_list" class="display bor-none tbl_rightalign" cellspacing="0" width="100%">
                         <thead>
                            <tr>
                                <th>Title</th>  
                                <th class="linkDis">Status</th>                             
                                <th class="text-right">
                                    <c:if test="${empty permission}">
                                    <div class="dis-line form-group mb-none">
                                         <button type="button" class="btn btn-primary blue-btn hideButtonIfPaused studyNotificationDetails">Add Notification</button>
                                     </div>
                                     </c:if>
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                        	<c:forEach items="${notificationList}" var="studyNotification">
	                            <tr id="${studyNotification.notificationId}">
	                                <td width="60%"><div class="dis-ellipsis" title="${fn:escapeXml(studyNotification.notificationText)}">${fn:escapeXml(studyNotification.notificationText)}</div></td>
	                                <td class="wid20">${studyNotification.checkNotificationSendingStatus}</td>
	                                <td class="wid20 text-right">
	                                	<span class="sprites_icon preview-g mr-lg studyNotificationDetails" actionType="view" notificationId="${studyNotification.notificationId}" data-toggle="tooltip" data-placement="top" title="view"></span>
	                                	<c:if test="${studyNotification.notificationSent}">
	                                    	<span class="sprites-icons-2 send mr-lg hideButtonIfPaused studyNotificationDetails <c:if test="${not empty permission}"> cursor-none </c:if>" actionType="resend" notificationId="${studyNotification.notificationId}" data-toggle="tooltip" data-placement="top" title="Resend"></span>
	                                    </c:if>
	                                    <c:if test="${not studyNotification.notificationSent}">
	                                    	<span class="${studyNotification.notificationDone?'edit-inc':'edit-inc-draft'} mr-lg hideButtonIfPaused studyNotificationDetails <c:if test="${not empty permission}"> cursor-none </c:if>" actionType="edit" notificationId="${studyNotification.notificationId}" data-toggle="tooltip" data-placement="top" title="Edit"></span>
	                                    </c:if>
	                                    <span class="sprites_icon copy hideButtonIfPaused studyNotificationDetails <c:if test="${not empty permission}"> cursor-none </c:if>" actionType="addOrEdit" notificationText="${fn:escapeXml(studyNotification.notificationText)}" data-toggle="tooltip" data-placement="top" title="Copy"></span>   
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
<form:form action="/fdahpStudyDesigner/adminStudies/getStudyNotification.do?_S=${param._S}" id="getStudyNotificationEditPage" name="getNotificationEditPage" method="post">
		<input type="hidden" id="notificationId" name="notificationId">
		<input type="hidden" id="notificationText" name="notificationText">
		<input type="hidden" id="actionType" name="actionType">
		 <input type="hidden" id="appId" name="appId">
		<input type="hidden" name="chkRefreshflag" value="y">
</form:form>

<form:form action="/fdahpStudyDesigner/adminStudies/studyList.do?_S=${param._S}" name="studyListPage" id="studyListPage" method="post">
</form:form>        

<form:form action="/fdahpStudyDesigner/adminStudies/notificationMarkAsCompleted.do?_S=${param._S}" name="notificationMarkAsCompletedForm" id="notificationMarkAsCompletedForm" method="post">
</form:form>
    <script>
        $(document).ready(function(){ 
        	$('[data-toggle="tooltip"]').tooltip();
            $(".menuNav li").removeClass('active');
            $(".eigthNotification").addClass('active'); 
            $("#createStudyId").show();
            $('.eigthNotification').removeClass('cursor-none');
            
             <c:if test="${studyLive.status eq 'Paused'}">
             	$('.hideButtonIfPaused').addClass('dis-none');
             </c:if>
             
        	$('.studyNotificationDetails').on('click',function(){
        		var appId = '${appId}';
        		$('.studyNotificationDetails').addClass('cursor-none');
    			$('#notificationId').val($(this).attr('notificationId'));
    			$('#notificationText').val($(this).attr('notificationText'));
    			$('#actionType').val($(this).attr('actionType'));
    			$('#appId').val(appId);
    			$('#getStudyNotificationEditPage').submit();
    			
    		});
        	
             var table = $('#notification_list').DataTable({              
              "paging":   false, 
              "order": [],
      		  "columnDefs": [ { orderable: false, orderable: false, targets: [0] } ],
              "info" : false, 
              "lengthChange": false, 
              "searching": false,
           });
            
     });
        
        function markAsCompleted(){
    			$('.markCompleted').prop('disabled', true);
    			$("#notificationMarkAsCompletedForm").submit();
    	}         
    </script>
