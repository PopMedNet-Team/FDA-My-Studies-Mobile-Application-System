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
    
    <!-- <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/animation/animate.css">     -->
        
    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/sprites.css">   
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/layout.css">   
        
    <!-- Theme CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/theme.css">
     <link rel="stylesheet" href="/fdahpStudyDesigner/css/jquery-password-validator.css"></link>
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/style.css">
        
    <!-- Head Libs -->
    <script src="/fdahpStudyDesigner/vendor/modernizr/modernizr.js"></script>
    
    
    <!-- Vendor -->
    <script src="/fdahpStudyDesigner/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/fdahpStudyDesigner/js/validator.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/animation/wow.min.js"></script>
	<!-- <script src="/fdahpStudyDesigner/vendor/slimscroll/jquery.slimscroll.min.js"></script> -->
    <script src="/fdahpStudyDesigner/vendor/select2/bootstrap-select.min.js"></script>
    
    <script src="/fdahpStudyDesigner/js/jquery.password-validator.js"></script>
    
    <script src="/fdahpStudyDesigner/js/underscore-min.js"></script>
    <script src="/fdahpStudyDesigner/js/ajaxRequestInterceptor.js"></script>
    <script type="text/javascript" src="/fdahpStudyDesigner/js/loader.js"></script>
    <style type="text/css">
    .has-error .checkbox, .has-error .checkbox-inline, .has-error .control-label, .has-error .help-block, .has-error .radio, .has-error .radio-inline, .has-error.checkbox label, .has-error.checkbox-inline label, .has-error.radio label, .has-error.radio-inline label{
	    color:#fff !important;
	}
    </style>
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
               FDA My Studies <br>Management Portal
            </div>
             <div class="lg-space-cover">
                <img src="/fdahpStudyDesigner/images/icons/web.png"/>
            </div>
        </div> -->
		<!-- <div class="lg-space-right">
			<div>
			    <input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}" csrfToken="${_csrf.token}" />
				<form:form id="passwordResetForm" data-toggle="validator" role="form" action="/fdahpStudyDesigner/changePassword.do" method="post" autocomplete="off">
					<div>
						<p>Your password has expired. You need to reset your password to proceed further.</p>
			        	<div class="mb-lg form-group">
			                <input type="password" class="form-control input-field wow_input" id="oldPassword" name="" maxlength="14"  data-minlength="8" placeholder="Old Password" data-error="Invalid old password." required
			                autocomplete="off"/>
			                <div class="help-block with-errors"></div>
			                <input type="hidden" name="oldPassword" id="hideOldPass" />
			            </div>
			            <div class="mb-lg form-group">
			                <input type="password" class="form-control input-field wow_input" id="password" name="" maxlength="14"  data-minlength="8" placeholder="Password" data-error="Password is invalid" required
			                pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~])[A-Za-z\d!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~]{8,14}" autocomplete="off"/>
			                <div class="help-block with-errors"></div>
			                <span class="arrowLeftSugg"></span>
			            </div>
						<div class="mb-lg form-group">
			                <input type="password" class="form-control input-field wow_input" id="cfnPassword" name="" maxlength="14" data-match="#password" data-match-error="Whoops, these don't match" placeholder="Confirm password" 
			                 required  autocomplete="off"/>
			                <div class="help-block with-errors"></div>
			            </div>
			            <div class="mb-lg form-group">
			  				<button type="button" class="btn lg-btn" id="resetPasswordBut">Submit</button>
			            </div>
					</div>
					<input type="password" name="newPassword" id="hidePass" style="display: none;"/>
				</form:form>
	  		</div>
	  		<div class="clearfix"></div>
            <div class="footer">
                <div><span>Copyright © 2017 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" class="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" class="" target="_blank">Privacy Policy</a></span></div>
          	</div>
	  	</div> -->
	<div class="logout">
	        <div class="dis-line pull-right ml-md line34">
	          <a href="/fdahpStudyDesigner/sessionOut.do" class="blue-link text-weight-normal text-uppercase"><span class="white__text">sign Out</span> <!-- <span class="ml-xs"><img src="/fdahpStudyDesigner/images/icons/logout.png"/></span> --></a>  
	        </div>
    </div>
	<div class="logo__ll">
        <img src="../images/logo/fda-logo-w.png"/>
    </div>
    <div class="pwdexp__container">
      	<!--container-->
      	<div>
		    <!-- change password box-->
		    <input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}" csrfToken="${_csrf.token}" />
			<form:form id="passwordResetForm" data-toggle="validator" role="form" action="/fdahpStudyDesigner/changePassword.do" method="post" autocomplete="off">
				<div>
					<div id="errMsg" class="error_msg">${errMsg}</div>
                    <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
					<p class="white__text">Your password has expired. You need to reset your password to proceed further.</p>
		        	<div class="mb-lg form-group">
		                <input type="password" class="form-control input-field wow_input" id="oldPassword" name="" maxlength="14"  data-minlength="8" placeholder="Old Password" data-error="Invalid old password." required
		                autocomplete="off"/>
		                <div class="help-block with-errors"></div>
		                <input type="hidden" name="oldPassword" id="hideOldPass" />
		            </div>
		            <div class="mb-lg form-group">
		                <input type="password" class="form-control input-field wow_input" id="password" name="" maxlength="14"  data-minlength="8" placeholder="Password" data-error="Password is invalid" required
		                pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~])[A-Za-z\d!&quot;#$%&amp;'()*+,-.:;&lt;=&gt;?@[\]^_`{|}~]{8,14}" autocomplete="off"/>
		                <div class="help-block with-errors"></div>
		                <span class="arrowLeftSugg"></span>
		            </div>
					<div class="mb-lg form-group">
		                <input type="password" class="form-control input-field wow_input" id="cfnPassword" name="" maxlength="14" data-match="#password" data-match-error="Whoops, these don't match" placeholder="Confirm password" 
		                 required  autocomplete="off"/>
		                <div class="help-block with-errors"></div>
		            </div>
		            <div class="mb-lg form-group">
		  				<button type="button" class="btn lg-btn" id="resetPasswordBut">Submit</button>
		            </div>
				</div>
				<input type="password" name="newPassword" id="hidePass" style="display: none;"/>
			</form:form>
		         <!-- change password box ends-->
  		</div>
        <!--container-->
        <div class="footer">
            <span>Copyright © 2017 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" id="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" id="" target="_blank">Privacy Policy</a></span>
        </div>  
    </div> 
</div>
	<script src="/fdahpStudyDesigner/js/theme.js"></script>
    <script src="/fdahpStudyDesigner/js/common.js"></script>
<script>
	$(document).ready(function(e) {
		addPasswordPopup();
		var errMsg = '${errMsg}';
		if(errMsg.length > 0){
			$("#errMsg").html(errMsg);
		   	$("#errMsg").show("fast");
		   	//$("#sucMsg").hide("fast");
		   	setTimeout(hideDisplayMessage, 4000);
		}
	});
// 	 window.onload = function () {
// 	    if (typeof history.pushState === "function") {
// 	        history.pushState("jibberish", null, null);
// 	        window.onpopstate = function () {
// 	            history.pushState('newjibberish', null, null);
// 	            // Handle the back (or forward) buttons here
// 	            // Will NOT handle refresh, use onbeforeunload for this.
// 	        };
// 	    }
// 	    else {
// 	        var ignoreHashChange = true;
// 	        window.onhashchange = function () {
// 	            if (!ignoreHashChange) {
// 	                ignoreHashChange = true;
// 	                window.location.hash = Math.random();
// 	                // Detect and redirect change here
// 	                // Works in older FF and IE9
// 	                // * it does mess with your hash symbol (anchor?) pound sign
// 	                // delimiter on the end of the URL
// 	            }
// 	            else {
// 	                ignoreHashChange = false;   
// 	            }
// 	        };
// 	    }
// 	    $(document).find('.md-container.white-bg ')
// 			.removeClass('md-container');
// 	}
	
	var addPasswordPopup = function() {
		 $("#password").passwordValidator({
				// list of qualities to require
				require: ['length', 'lower', 'upper', 'digit','spacial'],
				// minimum length requirement
				length: 8
			});
		}
	function formSubmit() {
		document.getElementById("logoutForm").submit();
	}
  	window.history.forward();
    function noBack() { 
         window.history.forward(); 
    }
    function hideDisplayMessage(){
		$('#errMsg').hide();
	}
    </script>
</body>
</html>