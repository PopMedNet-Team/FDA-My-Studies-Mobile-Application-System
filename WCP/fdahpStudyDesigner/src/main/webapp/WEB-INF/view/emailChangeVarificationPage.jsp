#-------------------------------------------------------------------------------
# Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. 
# Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in all copies or substantial
# portions of the Software.
# 
# Funding Source: Food and Drug Administration (?Funding Agency?) effective 18 September 2014 as
# Contract no. HHSF22320140030I/HHSF22301006T (the ?Prime Contract?).
# 
# THE SOFTWARE IS PROVIDED "AS IS" ,WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
# INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
# PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
# LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
# OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
# OTHER DEALINGS IN THE SOFTWARE.
#-------------------------------------------------------------------------------
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@page session="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
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
    <link rel="stylesheet" href="vendor/boostrap/bootstrap.min.css">
    <link rel="stylesheet" href="vendor/datatable/css/dataTables.bootstrap.min.css">
    
     <!-- Your custom styles (optional) -->
    <link href="/fdahpStudyDesigner/css/loader.css" rel="stylesheet">
    
    <link rel="stylesheet" href="vendor/dragula/dragula.min.css">
    <link rel="stylesheet" href="vendor/magnific-popup/magnific-popup.css">        
    <link rel="stylesheet" href="vendor/animation/animate.css">    
        
    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="css/layout.css">   
        
    <!-- Theme CSS -->
    <link rel="stylesheet" href="css/theme.css">
    <link rel="stylesheet" href="css/style.css">
    <link rel="stylesheet" href="css/jquery-password-validator.css"></link>
        
    <!-- Head Libs -->
    <script src="vendor/modernizr/modernizr.js"></script>
    
   
</head>
<body class="loading background__img">
    <div id="loader"><span></span></div>
    <div id="lg-container" class="lg-container">
        
              <div class="logo__ll">
            <img src="images/logo/fda-logo-w.png"/>
        </div>
        
        <div class="login__container">
            <div class="">
             <input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}" csrfToken="${_csrf.token}" />
             <!-- <div class="lg-space-txt text-center">
                    FDA My Studies Management Portal
                </div>
                <div class="ll__border__bottom"></div> -->
             <form:form id="accessCodeForm" data-toggle="validator" role="form" action="validateAccessCode.do" method="post" autocomplete="off">

                    <div id="errMsg" class="error_msg">${errMsg}</div>
                    <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
                    <c:if test="${isValidToken}">
                    <p class="white__text">To complete your email verification process, kindly use the access code provided on your email.</p>
                        <div class="mb-lg form-group">
                             <input autofocus="autofocus" type="text" class="input-field wow_input" id="" tabindex="1" name="accessCode" maxlength="6" placeholder="Access Code" data-error="Access Code is invalid" required autocomplete="off"/>
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg form-group">
                            <button type="submit" class="btn lg-btn">Submit</button>
                        </div>
                        </c:if>
                        <c:if test="${not isValidToken}"><p class="passwordExp"><i class="fa fa-exclamation-circle" aria-hidden="true"></i>The Activation Link is either expired or invalid.</p></c:if>
                        <div>
                            <a id="login" class="gray-link backToLogin white__text" href="javascript:void(0)">Back to Sign in</a>
                        </div>
                        <input type="hidden" name="securityToken" value="${securityToken}" />
                </form:form>
            </div>
        </div>

        <div class="footer">
            <div><span>Copyright © 2019 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" class="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" class="" target="_blank">Privacy Policy</a></span></div>
        </div>
             
        </div>
        <!-- End Login Right Section-->
    <form:form action="/fdahpStudyDesigner/login.do" id="backToLoginForm" name="backToLoginForm" method="post">
	</form:form>
    
    <!-- Vendor -->
    <script src="vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="vendor/boostrap/bootstrap.min.js"></script>
    <script src="vendor/animation/wow.min.js"></script>
    <script src="vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script src="vendor/dragula/react-dragula.min.js"></script>
    <script src="vendor/magnific-popup/jquery.magnific-popup.min.js"></script>    
    <script src="vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="js/validator.min.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery.mask.min.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery.password-validator.js"></script>
    <script type="text/javascript" src="/fdahpStudyDesigner/js/loader.js"></script>   
    <script type="text/javascript" src="/fdahpStudyDesigner/js/jquery.password-validator.js"></script>
	<script src="/fdahpStudyDesigner/js/underscore-min.js"></script>
    
    <!-- Theme Custom JS-->
    <script src="/fdahpStudyDesigner/js/theme.js"></script>
    <script src="/fdahpStudyDesigner/js/common.js"></script>
    <!--common js-->
    
   
   <script>
    	$(document).ready(function(e) {
    		
    		$('#termsId').on('click',function(){
    			$('#termsModal').modal('show');
    		});
    		
    		$('#privacyId').on('click',function(){
    			$('#privacyModal').modal('show');
    		});
    		
    		addPasswordPopup();
    		$('.backToLogin').on('click',function(){
				$('#backToLoginForm').submit();
			});
    		
    		var errMsg = '${errMsg}';
			var isValidToken = '${isValidToken}';
    		if(isValidToken){
				if(errMsg.length > 0){
					$("#errMsg").html(errMsg);
				   	$("#errMsg").show("fast");
				   	setTimeout(hideDisplayMessage, 4000);
				}
    		}
			var sucMsg = '${sucMsg}';
			if(isValidToken){
				if(sucMsg.length > 0){
					$("#sucMsg").html(sucMsg);
			    	$("#sucMsg").show("fast");
			    	$("#errMsg").hide("fast");
			    	setTimeout(hideDisplayMessage, 4000);
				}
			}
			$("#password").passwordValidator({
				require: ['length', 'lower', 'upper', 'digit','spacial'],
				length: 8
			}); 
			
    	});
    	function hideDisplayMessage(){
			$('#sucMsg').hide();
			$('#errMsg').hide();
		}
    	window.onload = function () {
		    if (typeof history.pushState === "function") {
		        history.pushState("jibberish", null, null);
		        window.onpopstate = function () {
		            history.pushState('newjibberish', null, null);
		        };
		    }
		    else {
		        var ignoreHashChange = true;
		        window.onhashchange = function () {
		            if (!ignoreHashChange) {
		                ignoreHashChange = true;
		                window.location.hash = Math.random();
		            }
		            else {
		                ignoreHashChange = false;   
		            }
		        };
		    }
		    $(document).find('.md-container.white-bg ')
    			.removeClass('md-container');
		}
    	
    	var addPasswordPopup = function() {
   		 $("#password").passwordValidator({
   				require: ['length', 'lower', 'upper', 'digit','spacial'],
   				length: 8
   			});
   		}
    </script>

</body>
</html>
