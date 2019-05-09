<!-- 
  Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
  associated documentation files (the "Software"), to deal in the Software without restriction, including
  without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
  of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
  following conditions:
 
  The above copyright notice and this permission notice shall be included in all copies or substantial
  portions of the Software.
 
  Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
  HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE. 
-->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.hphc.mystudies.util.SessionObject"%>

<!-- create Study Section Start -->
<div id="" class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none mt-md tit_con" >
     <div class="md-container">
         <!-- <div class="col-sm-12 col-md-12 col-lg-12 p-none">
            <div class="black-lg-f">
              <span class="mr-xs"><a href="javascript:void(0)" class="backOrCancelBtn"><img src="/fdahpStudyDesigner/images/icons/back-b.png"/></a></span> Create Study
            </div>
         </div> -->
         <div class="text-center"> 
       		<div class="" id="alertMsg"></div>
        </div>
    </div>
</div>
<!-- create Study Section End -->

<!-- StudyList Section Start-->

<div id="studyListId" class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none" style="display: none;">
     <div class="md-container">
        <div class="col-sm-12 col-md-12 col-lg-12 p-none mb-md">
           <div class="black-lg-f">
               Manage Studies
           </div>          
            <c:if test="${fn:contains(sessionObject.userPermissions,'ROLE_CREATE_MANAGE_STUDIES')}">
            <div class="dis-line pull-right ml-md mt-xs">
                <div class="form-group mb-none">
                    <button type="button" class="btn btn-primary blue-btn addEditStudy"> Create Study</button>
                </div>
		</div>
           </c:if>
</div>
</div>
</div>
<!-- StudyList Section End-->

<form:form action="/fdahpStudyDesigner/adminStudies/viewStudyDetails.do" id="addEditStudyForm" name="addEditStudyForm" method="post">
</form:form> 
<form:form action="/fdahpStudyDesigner/adminStudies/studyList.do" id="backOrCancelForm" name="backOrCancelForm" method="post">
</form:form>

<script type="text/javascript">
$(document).ready(function(){
	
$('.addEditStudy').on('click',function(){
	$('#addEditStudyForm').submit();
 });
 
//cancel or back click
$('.backOrCancelBtn').on('click',function(){
	$('#backOrCancelForm').submit();
});

<c:if test="${studyListId eq true}">
   $('#studyListId').show();
</c:if>
// 	<c:if test="${createStudyId eq true}">
// 	$('#createStudyId').show();
// 	</c:if>
	var sucMsg = '${sucMsg}';
	if(sucMsg.length > 0){
		showSucMsg(sucMsg);
	}
	var errMsg = '${errMsg}';
	if(errMsg.length > 0){
		showErrMsg(errMsg);
	}
	
	var resourceErrMsg = '${resourceErrMsg}';
	if(resourceErrMsg){
		bootbox.alert(resourceErrMsg);
	}
	
	var actionSucMsg = '${actionSucMsg}';
	if(actionSucMsg){
		//bootbox.alert(actionSucMsg);
		
		bootbox.alert({
		    message: actionSucMsg,
		    className: 'green-txt',
		    closeButton: false
		   
		});
	}
});
function showSucMsg(message) {
	$("#alertMsg").removeClass('e-box').addClass('s-box').html(message);
	$('#alertMsg').show('5000');
	setTimeout(hideDisplayMessage, 5000);
}

function showErrMsg(message){
	$("#alertMsg").removeClass('s-box').addClass('e-box').html(message);
	$('#alertMsg').show('5000');
	setTimeout(hideDisplayMessage, 5000);
}

function hideDisplayMessage(){
	$('#alertMsg').slideUp('5000');
}
</script>
