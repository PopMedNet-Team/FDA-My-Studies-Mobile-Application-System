<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@page import="com.fdahpstudydesigner.util.SessionObject"%>
<!DOCTYPE html>
<html class="overflow-hidden">
	<head>
        
    <!-- Basic -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
            
    <title>FDA MSMP</title>	
    
    <meta name="description" content="">
    <meta name="keywords" content="">
    <meta name="author" content="">

    <!-- Favicon -->
    <link rel="shortcut icon" href="/fdahpStudyDesigner/images/icons/fav.png" type="image/x-icon" />
    <link rel="apple-touch-icon" href="/fdahpStudyDesigner/images/icons/fav.png">
        
    <!-- Mobile Metas -->
    <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
        
    <!-- Web Fonts  -->
   <link href="https://fonts.googleapis.com/css?family=Roboto:300,400" rel="stylesheet">
        
    <!-- Vendor CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/boostrap/bootstrap.min.css">
    
     <!-- Your custom styles (optional) -->
    <link href="/fdahpStudyDesigner/css/loader.css" rel="stylesheet">
    
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datatable/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/dragula/dragula.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/magnific-popup/magnific-popup.css">        
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/animation/animate.css">    
        
    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/sprites.css">   
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/layout.css">   
        
    <!-- Theme CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/theme.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/style.css">
        
    <!-- Head Libs -->
    <script src="/fdahpStudyDesigner/vendor/modernizr/modernizr.js"></script>
</head>
<body class="loading background__img" onload="noBack();" onpageshow="if (event.persisted) noBack();" onunload="">
	<div id="loader"><span></span></div>
     <form:form action="" name="studyListForm" id="studyListForm" method="post">
     </form:form>
     <c:url value="/j_spring_security_logout" var="logoutUrl" />
	<form action="${logoutUrl}" method="post" id="logoutForm">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
	</form>
    <div id="lg-container" class="lg-container">
        
        <!-- Login Left Section-->
        <!-- <div class="lg-space-left">
            <div class="lg-space-img">
                <img src="/fdahpStudyDesigner/images/logo/fda-logo-w.png"/>
            </div>
            <div class="lg-space-txt">
               MyStudies <br>Management Portal
            </div>
             <div class="lg-space-cover">
                <img src="/fdahpStudyDesigner/images/icons/web.png"/>
            </div>
        </div> -->
        <!-- End Login Left Section-->
        <!-- <div>
          <a href="javascript:formSubmit();">Logout</a>
        </div> -->
        <!-- Login Right Section-->
        <!-- <div class="lg-space-right">
        	<div class="logout">
               <div class="dis-line pull-right ml-md line34">
                 <a href="/fdahpStudyDesigner/sessionOut.do" class="blue-link text-weight-normal text-uppercase"><span>sign Out</span> <span class="ml-xs"><img src="/fdahpStudyDesigner/images/icons/logout.png"/></span></a>  
               </div>
           	</div>
            <div class="lg-space-container wd">
                <div class="lg-space-center">
	                <div class="lg-space-title">
	                    <span>Welcome,</span><span>${sessionObject.firstName}</span>
	                </div>
	                <div class='lg-icons'> 
	                   <ul class="lg-icons-list"> 
	                    <li class="studyListId">
	                        <a class='studies-g' href='javascript:void(0)'></a>
	                        <div class='studyList'>Studies<br><span>&nbsp;</span></div>
	                    </li>
	                    <li class="linkDis hide">
	                        <a class='repository-g' href='javascript:void(0)'></a>
	                        <div>Repository</div>
	                    </li> 
	                    <li class="notificationListId">
	                        <a class='notifications-g' href='javascript:void(0)'></a>
	                        <div>Notifications<br><span>&nbsp;</span></div>
	                    </li> 
	                   <li class="userListId">
	                        <a class='user-g' href='javascript:void(0)'></a>
	                        <div>Users<br><span>&nbsp;</span></div>
	                    </li> 
	                    <li class="myAccountId">
	                        <a class='account-g' href='javascript:void(0)'></a>
	                        <div>My Account<br><span>&nbsp;</span></div>
	                    </li>
	                 </ul> 
	                </div>
                </div>
               <div class="clearfix"></div>
               <div class="footer">
                    <span>Copyright © 2017 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" id="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" id="" target="_blank">Privacy Policy</a></span>
              </div>
            </div>
            
             
            
             
        </div> -->
        <!-- End Login Right Section-->
        <div class="logout">
               <div class="dis-line pull-right ml-md line34">
                 <a href="/fdahpStudyDesigner/sessionOut.do" class="blue-link text-weight-normal text-uppercase"><span class="white__text">sign Out</span> <!-- <span class="ml-xs"><img src="/fdahpStudyDesigner/images/icons/logout.png"/></span> --></a>  
               </div>
           	</div>
        <div class="logo__ll">
            <img src="../images/logo/fda-logo-w.png"/>
        </div>
        <div class="landing__container">
        <!--container-->
        <div class="landing__content">
            <div class="manage-content-parent">
                <div class="lg-space-center">
                    <div class="lg-space-txt">
                        FDA MyStudies Management Portal
                    </div>
                    <div class="ll__border__bottom"></div>
                    <div class="lg-space-title">
                        <span>Welcome,</span><span>${sessionObject.firstName}</span>
                    </div>
                    <div class='lg-icons'> 
                       <ul class="lg-icons-list"> 
                        <li class="studyListId">
                            <a class='' href='javascript:void(0)'>
                                <img class="mt-xlg" src="../images/icons/studies-w.png">
                            </a>
                            <div class='studyList'>Studies<br><span>&nbsp;</span></div>
                        </li>
                        <li class="linkDis hide">
                            <a class='repository-g' href='javascript:void(0)'></a>
                            <div>Repository</div>
                        </li> 
                        <li class="notificationListId">
                             <a class='' href='javascript:void(0)'>
                                <img class="mt-xlg" src="../images/icons/notifications-w.png">
                            </a>
                            <div class='studyList'>Notifications<br><span>&nbsp;</span></div>
                        </li> 
                       <li class="userListId">
                             <a class='' href='javascript:void(0)'>
                                <img class="mt-xlg" src="../images/icons/user-w.png">
                            </a>
                            <div>Users<br><span>&nbsp;</span></div>
                        </li> 
                        <li class="myAccountId">
                            <a class='' href='javascript:void(0)'>
                                <img class="mt-xlg" src="../images/icons/account-w.png">
                            </a>
                            <div>My Account<br><span>&nbsp;</span></div>
                        </li>
                     </ul> 
                    </div>
                </div>
              <div class="clearfix"></div>
          </div>
        </div> 
        <div class="footer">
            <span>Copyright © 2017 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" id="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" id="" target="_blank">Privacy Policy</a></span>
      </div>  
        <!--container-->
        </div>
        
    </div>
    
    <!-- Modal -->
<div class="modal fade" id="termsModal" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
      
      <div class="modal-header cust-hdr">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>       
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
      		 <div>
      			<div class="mt-md mb-md"><u><b>Terms</b></u></div>
		               <span>${sessionObject.termsText}</span>
            </div>
      </div>
      </div>
   </div>
</div>

<div class="modal fade" id="privacyModal" role="dialog">
   <div class="modal-dialog modal-lg">
      <!-- Modal content-->
      <div class="modal-content">
      
      <div class="modal-header cust-hdr">
        <button type="button" class="close pull-right" data-dismiss="modal">&times;</button>       
      </div>
      <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg">
      		 <div>
      			<div class="mt-md mb-md"><u><b>Privacy Policy</b></u></div>
		               <span>${sessionObject.privacyPolicyText}</span>
            </div>
      </div>
      </div>
   </div>
</div>
    
    
    <!-- Vendor -->
    <script src="/fdahpStudyDesigner/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/animation/wow.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/dragula/react-dragula.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/magnific-popup/jquery.magnific-popup.min.js"></script>    
    <script src="/fdahpStudyDesigner/vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery.mask.min.js"></script>
    
    <script type="text/javascript" src="/fdahpStudyDesigner/js/loader.js"></script>    
    
    <!-- Theme Custom JS-->
    <script src="/fdahpStudyDesigner/js/theme.js"></script>
    <script src="/fdahpStudyDesigner/js/common.js"></script>
    
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-71064806-1', 'auto');
      ga('send', 'pageview');
	function formSubmit() {
		document.getElementById("logoutForm").submit();
	}
    $(document).ready(function(e) {
    	
    	$('#termsId').on('click',function(){
    		$('#termsModal').modal('show');
    	});
    		
    	$('#privacyId').on('click',function(){
    		$('#privacyModal').modal('show');
    	});
    	
    	<c:if test="${not fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_STUDIES')}">
    	 $(".studyListId").addClass('cursor-none');
    	 $(".studyListId").unbind();
    	</c:if>
    	<c:if test="${not fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_USERS_VIEW')}"> 
    	 $(".userListId").addClass('cursor-none');
    	 $(".userListId").unbind();
    	</c:if>
    	 <c:if test="${not fn:contains(sessionObject.userPermissions,'ROLE_MANAGE_APP_WIDE_NOTIFICATION_VIEW')}">
    	 $(".notificationListId").addClass('cursor-none');
    	 $(".notificationListId").unbind();
    	 </c:if>
    	 
    	 
    	 $(".studyListId").click(function(){	
    		document.studyListForm.action="/fdahpStudyDesigner/adminStudies/studyList.do";
    		document.studyListForm.submit();
    	 });
    	 
    	 $(".userListId").click(function(){	
    		document.studyListForm.action="/fdahpStudyDesigner/adminUsersView/getUserList.do";
    		document.studyListForm.submit();
    	 });
    	 
    	 $(".notificationListId").click(function(){	
     		document.studyListForm.action="/fdahpStudyDesigner/adminNotificationView/viewNotificationList.do";
     		document.studyListForm.submit();
     	 });
    	 $(".myAccountId").click(function(){	
      		document.studyListForm.action="/fdahpStudyDesigner/adminDashboard/viewUserDetails.do";
      		document.studyListForm.submit();
      	 });
      	 if('${sessionScope.sessionObject}' != ''){
	   	  	setTimeout(function(){
		 		 window.location.href = '/fdahpStudyDesigner/errorRedirect.do?error=timeOut';
		 	}, 1000 * 60 * 31);
   	  	 }
    });
    <c:if test="${param.action eq 'landing'}">
    /* function noBack() { 
  	  history.pushState(null, null, 'login.do');
  	   window.addEventListener('popstate', function(event) {
  	     history.pushState(null, null, 'login.do');
  	  }); 
    } */
//     window.onload = function () {
//       if (typeof history.pushState === "function") {
//           history.pushState("jibberish", null, null);
//           window.onpopstate = function () {
//               history.pushState('newjibberish', null, null);
//               // Handle the back (or forward) buttons here
//               // Will NOT handle refresh, use onbeforeunload for this.
//           };
//       }
//       else {
//           var ignoreHashChange = true;
//           window.onhashchange = function () {
//               if (!ignoreHashChange) {
//                   ignoreHashChange = true;
//                   window.location.hash = Math.random();
//                   // Detect and redirect change here
//                   // Works in older FF and IE9
//                   // * it does mess with your hash symbol (anchor?) pound sign
//                   // delimiter on the end of the URL
//               }
//               else {
//                   ignoreHashChange = false;   
//               }
//           };
//       }
//   	}
  </c:if>
  	window.history.forward();
    function noBack() { 
         window.history.forward(); 
    }
    </script>
</body>
</html>