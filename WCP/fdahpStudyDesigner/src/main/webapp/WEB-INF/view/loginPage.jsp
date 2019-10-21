<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html class="overflow-hidden" lang="en">
	<head>
    <!-- Basic -->
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="refresh" content="1700">        
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
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datatable/css/dataTables.bootstrap.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datatable/css/jquery.dataTables.min.css">
    
     <!-- Your custom styles (optional) -->
    <link href="/fdahpStudyDesigner/css/loader.css" rel="stylesheet">
    
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datatable/css/rowReorder.dataTables.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/dragula/dragula.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/magnific-popup/magnific-popup.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/font-awesome/font-awesome.min.css"> 
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/select2/bootstrap-select.min.css">  
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/animation/animate.css">
        
    <!-- Theme Responsive CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/layout.css">   
        
    <!-- Theme CSS -->
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/theme.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/style.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/sprites_icon.css">
        
    <!-- Head Libs -->
    <script src="/fdahpStudyDesigner/vendor/modernizr/modernizr.js"></script>
    
    <!-- Vendor -->
    <script src="/fdahpStudyDesigner/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/fdahpStudyDesigner/js/validator.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/animation/wow.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/datatable/js/jquery.dataTables.min.js"></script>
     <script src="/fdahpStudyDesigner/vendor/datatable/js/dataTables.rowReorder.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/dragula/react-dragula.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/magnific-popup/jquery.magnific-popup.min.js"></script>    
    <script src="/fdahpStudyDesigner/vendor/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/select2/bootstrap-select.min.js"></script>
     <script type="text/javascript" src="/fdahpStudyDesigner/js/loader.js"></script>
    
    
    
    
    
    <script>
      (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
      (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
      m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
      })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

      ga('create', 'UA-71064806-1', 'auto');
      ga('send', 'pageview');
    </script>
        
</head>
<body class="loading background__img" onload="noBack();" onpageshow="if (event.persisted) noBack();" onunload="">
    <div id="loader"><span></span></div>
    <div id="lg-container" class="lg-container">
       <!--  <div class="lg-space-left">
            <div class="lg-space-img">
                <img src="images/logo/fda-logo-w.png"/>
            </div>
            <div class="lg-space-txt">
               MyStudies <br>Management Portal
            </div>
             <div class="lg-space-cover">
                <img src="images/icons/web.png"/>
            </div>
        </div> -->
        <!-- <div class="lg-space-right">
        
	        <div class="cs-model-box hide askSignInCls">        
		        <div></div>
		        <div>
		       		<div>Important Note</div>
		       		<ul>
		       			<li>You Are Accessing a U.S. Government Information System </li>
		       			<li>Usage Of This Information System May Be Monitored, Recorded, And Subject To Audit</li>
		       			<li>Unauthorized Use Of This Information System Is Prohibited And Subject To Criminal And Civil Penalties</li>
		       			<li>Use Of This Information System Indicates Consent To Monitoring And Recording</li>
		       		</ul>
		       		
		       		<div>
		       			By clicking Sign In, you agree to the the above-mentioned points as well as to the US FDA MyStudies Management Portal <a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" class="" target="_blank">Terms</a> and <a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" class="" target="_blank">Privacy Policy</a>
		       		</div>
		       		
		       		<div class="mt-lg">
		       			<button id="loginBtnId" type="button" class="btn btn-primary blue-btn float__left" >Sign In</button>
		       			<button id="cancelbtn" type="button" class="btn btn-default gray-btn ml-sm float__left">Cancel</button>
		       		</div>
		       	</div>
	       	</div>
           
            <div class="login-box">
             <c:url value='/j_spring_security_check' var="fdaLink"/>
             <input type="hidden" id="fdaLink" value="${fdaLink}" >
             <form:form id="loginForm" data-toggle="validator" role="form" action="#"  name="loginForm" method="post" autocomplete="off">  
                    <div id="errMsg" class="error_msg">${errMsg}</div>
                    <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
                    <div class="login">
                        <div class="mb-lg form-group">
                            <input type="text" class="input-field wow_input" id="email" name="username" data-pattern-error="Email address is invalid" 
                            	placeholder="Email Address" required maxlength="100" pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$" autofocus autocomplete="off">
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg form-group">
                            <input type="password" class="input-field wow_input" id="password" 
                        		placeholder="Password"  required maxlength="64" data-error="This field shouldn't be empty" autocomplete="off"  readonly onfocus="$(this).removeAttr('readonly');">
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg form-group">
                            <button type="button" class="btn lg-btn" id="siginNoteBtnId">Submit</button>
                        </div>
                        <div class="pb-md">
                            <a id="forgot_pwd" class="gray-link" href="javascript:void(0)">Forgot Password?</a>
                        </div>
                   </div>
                   <input type="password" name="password" id="hidePass" style="display: none;"/>
                </form:form>
                <form:form id="forgotForm" data-toggle="validator" role="form" action="forgotPassword.do" method="post" autocomplete="off">
                   <div class="pwd dis-none">
                     <div class="mb-lg">
                         <h3 class="mt-none text-weight-bold">Forgot Password?</h3>
                        <div class="gray-xs-f mt-md">Enter your Email address to get  a link to reset password</div>
                        </div>
                        <div class="mb-lg form-group">
                            <input type="text" class="input-field wow_input" id="emailReg" name="email" maxlength="100" placeholder="Email Address" 
                            data-pattern-error = "Email address is invalid"  required maxlength="100" 
                               pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$">
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg">
                            <button type="submit" class="btn lg-btn" id="log-btn">Submit</button>
                        </div>
                        <div>
                            <a id="login" class="gray-link" href="javascript:void(0)">Back to Sign in</a>
                        </div>
                   </div>
              </form:form>   
            </div>
            
            
            <div class="clearfix"></div>
            
             <div class="footer">
                    <div><span>Copyright © 2017 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" class="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" class="" target="_blank">Privacy Policy</a></span></div>
              </div>
             
        </div> -->

        <!-- new login -->
        <!-- Logo-->
        <div class="logo__ll">
            <img src="images/logo/fda-logo-w.png"/>
        </div>
        <div class="clearfix"></div>
        <div class="login__container">
            <div class="cs-model-box hide askSignInCls">        
                <div></div>
                <div>
                    <div>Important Note</div>
                    <ul>
                        <li>You Are Accessing a U.S. Government Information System </li>
                        <li>Usage Of This Information System May Be Monitored, Recorded, And Subject To Audit</li>
                        <li>Unauthorized Use Of This Information System Is Prohibited And Subject To Criminal And Civil Penalties</li>
                        <li>Use Of This Information System Indicates Consent To Monitoring And Recording</li>
                    </ul>
                    
                    <div>
                        By clicking Sign In, you agree to the the above-mentioned points as well as to the US FDA MyStudies Management Portal <a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" class="" target="_blank">Terms</a> and <a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" class="" target="_blank">Privacy Policy</a>
                    </div>
                    
                    <div class="mt-lg">
                        <button id="loginBtnId" type="button" class="btn btn-primary blue-btn float__left" >Sign In</button>
                        <button id="cancelbtn" type="button" class="btn btn-default gray-btn ml-sm float__left">Cancel</button>
                    </div>
                </div>
            </div>
           
            <div class="login-box">
                <div class="lg-space-txt">
                    FDA MyStudies Management Portal
                </div>
                <div class="ll__border__bottom"></div>
             <c:url value='/j_spring_security_check' var="fdaLink"/>
             <input type="hidden" id="fdaLink" value="${fdaLink}" >
             <form:form id="loginForm" data-toggle="validator" role="form" action="#"  name="loginForm" method="post" autocomplete="off">  
                    <div id="errMsg" class="error_msg">${errMsg}</div>
                    <div id="sucMsg" class="suceess_msg">${sucMsg}</div>
                    <div class="login pt-xlg">
                        <div class="mb-lg form-group">
                            <input type="text" class="input-field wow_input" id="email" name="username" data-pattern-error="Email address is invalid" 
                                placeholder="Email Address" required maxlength="100" pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$" autofocus autocomplete="off">
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg form-group">
                            <input type="password" class="input-field wow_input" id="password" 
                                placeholder="Password"  required maxlength="64" data-error="This field shouldn't be empty" autocomplete="off"  readonly onfocus="$(this).removeAttr('readonly');">
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg form-group">
                            <button type="button" class="btn lg-btn" id="siginNoteBtnId">SIGN IN</button>
                        </div>
                        <div class="pb-md pt-xs">
                            <a id="forgot_pwd" class="gray-link white__text" href="javascript:void(0)">Forgot Password?</a>
                        </div>
                   </div>
                   <input type="password" name="password" id="hidePass" style="display: none;"/>
                </form:form>
                <form:form id="forgotForm" data-toggle="validator" role="form" action="forgotPassword.do" method="post" autocomplete="off">
                   <div class="pwd dis-none">
                     <div class="mb-lg">
                         <h3 style="    color: #fff; padding-top: 20px;" class="mt-none">Forgot Password?</h3>
                        <div class="mt-md white__text">Enter your Email address to get  a link to reset password</div>
                        </div>
                        <div class="mb-lg form-group">
                            <input type="text" class="input-field wow_input" id="emailReg" name="email" maxlength="100" placeholder="Email Address" 
                            data-pattern-error = "Email address is invalid"  required maxlength="100" 
                               pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$">
                            <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mb-lg">
                            <button type="submit" class="btn lg-btn" id="log-btn">SUBMIT</button>
                        </div>
                        <div class="pt-xs">
                            <a id="login" class="gray-link white__text" href="javascript:void(0)">Back to Sign in</a>
                        </div>
                   </div>
              </form:form>   
            </div> 
            <div class="clearfix"></div>
        </div>
        <div class="clearfix"></div>
        <div class="footer">
            <div><span>Copyright © 2017 FDA</span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/" class="" target="_blank">Terms</a></span><span><a href="https://www.fda.gov/AboutFDA/AboutThisWebsite/WebsitePolicies/#privacy" class="" target="_blank">Privacy Policy</a></span></div>
        </div>
        <!-- new login --> 
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
		               <span>${masterDataBO.termsText}</span>
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
		               <span>${masterDataBO.privacyPolicyText}</span>
            </div>
      </div>
      </div>
   </div>
</div>
<input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}" csrfToken="${_csrf.token}" />    
    <script src="/fdahpStudyDesigner/js/theme.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery.mask.min.js"></script>
    <script src="/fdahpStudyDesigner/js/common.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery.nicescroll.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/tinymce/tinymce.min.js"></script>
    <script src="/fdahpStudyDesigner/js/bootbox.min.js"></script>
    <script src="/fdahpStudyDesigner/js/autofill-event.js"></script>
   	<script src="/fdahpStudyDesigner/js/ajaxRequestInterceptor.js"></script>
   
   <script>
   		var isChanged = true;
    	$(document).ready(function(e) {
	    	// Internet Explorer 6-11
			var isIE = /*@cc_on!@*/false || !!document.documentMode;
			
			// Edge 20+
			var isEdge = !isIE && !!window.StyleMedia;
    		if(isIE || isEdge) {
    			$('#password').prop('readonly', false);
    		}
	        $.ajaxSetup({
				beforeSend: function(xhr, settings){
	            	xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
	        	}
			});
    		$('#siginNoteBtnId').click(function() {
    			$('#password').removeAttr('readonly');
				if(isFromValid($(this).parents('form'))) {
					$(".askSignInCls").removeClass('hide');
				}
			});
			$('#loginForm').keypress(function (e) {
				$('#password').removeAttr('readonly');
				 if (e.which == 13) {
				 	if(isFromValid($("#loginForm"))) {
				 		e.target.blur();
						$(".askSignInCls").removeClass('hide');
					}
				 }
			});
			$("#cancelbtn").click(function(){
			 	$(".cs-model-box").addClass('hide');
			});
    		$('.termsCls').on('click',function(){
    			$('#termsModal').modal('show');
    		});
    		
    		$('.privacyCls').on('click',function(){
    			$('#privacyModal').modal('show');
    		});
			
// 			$('input:last').change(function() {
// 				if(isChanged) {
// 					if($('#email').val()){
// 						setTimeout(function(){$('button').removeClass('disabled');}, 200);
// 					}
// 					isChanged = false;
// 				}
// 			});
    		
    		var errMsg = '${errMsg}';
			if(errMsg.length > 0){
				$("#errMsg").html(errMsg);
			   	$("#errMsg").show("fast");
			   	//$("#sucMsg").hide("fast");
			   	setTimeout(hideDisplayMessage, 4000);
			}
			var sucMsg = '${sucMsg}';
			if(sucMsg.length > 0){
				$("#sucMsg").html(sucMsg);
		    	$("#sucMsg").show("fast");
		    	$("#errMsg").hide("fast");
		    	setTimeout(hideDisplayMessage, 4000);
			}
			

		    // Internet Explorer 6-11
		    var isIE = /*@cc_on!@*/false || !!document.documentMode;
		    // Edge 20+
		    var isEdge = !isIE && !!window.StyleMedia;
		    
			$('#email').keyup(function(event){
				event = (event || window.event);
		    	if(event.keyCode == 13) {
					var isEmail = false;
					var emailAdd = $('#email').val();
					var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
			        isEmail = regEX.test(emailAdd);
			        if(emailAdd == ''){
			        	if(isIE || isEdge){
			        		$('#email').parent().find(".help-block").html("<ul class='list-unstyled'><li>This is a required field</li></ul>");
			        	} else {
			        		$('#email').parent().find(".help-block").html("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
			        	}
					} else if(!isEmail){
						$('#email').parent().find(".help-block").html("<ul class='list-unstyled'><li>Email address is invalid</li></ul>");
					}
		    	}
			});
			
			var wh = $(window).height();
		    $('.cs-model-box>div:first-child').css('height',wh);
		    
			/* $('#emailReg').keyup(function(event){
				event = (event || window.event);
		    	if(event.keyCode == 13) {
					var isEmail = false;
					var emailAdd = $('#emailReg').val();
					var regEX = /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$/;
			        isEmail = regEX.test(emailAdd);
					if(emailAdd == ''){
						if(isIE || isEdge){
			        		$('#emailReg').parent().find(".help-block").html("<ul class='list-unstyled'><li>This is a required field</li></ul>");
			        	} else {
			        		$('#emailReg').parent().find(".help-block").html("<ul class='list-unstyled'><li>Please fill out this field</li></ul>");
			        	}
					} else if(!isEmail){
						$('#emailReg').parent().find(".help-block").html("<ul class='list-unstyled'><li>Email address is invalid</li></ul>");
					}
		    	}
			}); */
			
			/* $('form').bind("keypress", function(e) {
			    if ($('input:text').is(":empty")) {
			       if (e.keyCode == 13) {               
			        e.preventDefault();
			        return false;
			      } 
			    }
			}); */
			
    	});
    	function hideDisplayMessage(){
			$('#sucMsg').hide();
			$('#errMsg').hide();
		}
//     	window.onload = function () {
// 		    if (typeof history.pushState === "function") {
// 		        history.pushState("jibberish", null, null);
// 		        window.onpopstate = function () {
// 		            history.pushState('newjibberish', null, null);
// 		            // Handle the back (or forward) buttons here
// 		            // Will NOT handle refresh, use onbeforeunload for this.
// 		        };
// 		    }
// 		    else {
// 		        var ignoreHashChange = true;
// 		        window.onhashchange = function () {
// 		            if (!ignoreHashChange) {
// 		                ignoreHashChange = true;
// 		                window.location.hash = Math.random();
// 		                // Detect and redirect change here
// 		                // Works in older FF and IE9
// 		                // * it does mess with your hash symbol (anchor?) pound sign
// 		                // delimiter on the end of the URL
// 		            }
// 		            else {
// 		                ignoreHashChange = false;   
// 		            }
// 		        };
// 		    }
// 		}
	 window.history.forward();
    function noBack() { 
         window.history.forward(); 
    }
    </script>

</body>
</html>