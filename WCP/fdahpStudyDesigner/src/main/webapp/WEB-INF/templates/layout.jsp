<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/fmt" prefix = "fmt" %>
<jsp:useBean id="date" class="java.util.Date" />
<c:set var="tz" value="America/Los_Angeles"/>
<!DOCTYPE html>
<html lang="en">
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
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datetimepicker/css/bootstrap-timepicker.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/scrollbar/jquery.mCustomScrollbar.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datetimepicker/css/bootstrap-datetimepicker.min.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datatable/css/dataTables.bootstrap.min.css">
    
     <!-- Your custom styles (optional) -->
    <link href="/fdahpStudyDesigner/css/loader.css" rel="stylesheet">
    <link rel="stylesheet" href="/fdahpStudyDesigner/vendor/datatable/css/jquery.dataTables.min.css">
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
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/sprites-icons-2.css"> 
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/sprites_v3.css">
    <link rel="stylesheet" href="/fdahpStudyDesigner/css/jquery-password-validator.css">
   
   
        
    <!-- Head Libs -->
    <script src="/fdahpStudyDesigner/vendor/modernizr/modernizr.js"></script>
    
    
    <!-- Vendor -->
    <script src="/fdahpStudyDesigner/vendor/jquery/jquery-3.1.1.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/boostrap/bootstrap.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/scrollbar/jquery.mCustomScrollbar.concat.min.js"></script>
     <script src="/fdahpStudyDesigner/vendor/datetimepicker/js/moment.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/datetimepicker/js/bootstrap-timepicker.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/datetimepicker/js/bootstrap-datetimepicker.min.js"></script>
    <script src="/fdahpStudyDesigner/js/validator.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/animation/wow.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/datatable/js/jquery.dataTables.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/datatable/js/dataTables.rowReorder.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/dragula/react-dragula.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/magnific-popup/jquery.magnific-popup.min.js"></script>    
	<!-- <script src="/fdahpStudyDesigner/vendor/slimscroll/jquery.slimscroll.min.js"></script> -->
    <script src="/fdahpStudyDesigner/vendor/select2/bootstrap-select.min.js"></script>
    
    <script src="/fdahpStudyDesigner/js/jquery.password-validator.js"></script>
    
    <script src="/fdahpStudyDesigner/js/underscore-min.js"></script>
    <script src="/fdahpStudyDesigner/js/ajaxRequestInterceptor.js"></script>
    <script type="text/javascript" src="/fdahpStudyDesigner/js/loader.js"></script>
    <script>
//       (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
//       (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
//       m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
//       })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

//       ga('create', 'UA-71064806-1', 'auto');
//       ga('send', 'pageview');
      $(window).on('keydown keypress mousedown',function(event){
    		 event = (event || window.event);
    	   if(event.keyCode == 13) {
    		   if(!(event.target.nodeName == 'TEXTAREA')){
    			  (event).preventDefault(); // Disable the " Entry " key
    		      return false;
    		   }
    		   return true;
    	   }
   	 });
    </script>
        
</head>
<body class="loading" onload="noBack();" onpageshow="if (event.persisted) noBack();" onunload="">
<div id="loader"><span></span></div>
			<tiles:insertAttribute name="header" />
 			<tiles:insertAttribute name="subheader" /> 
						 	
			<!-- content start  -->
 			<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 p-none">
 			    <div class="md-container white-bg "> 			    	
 			    	<div class="row" id="rowId">
					    <tiles:insertAttribute name="menu" />
					    <tiles:insertAttribute name="body" />
				    </div>
 			    </div>
			</div>  
			 	
			<!-- /End content   -->
			<tiles:insertAttribute name="footer" />
		<input type="hidden" id="csrfDet" csrfParamName="${_csrf.parameterName}" csrfToken="${_csrf.token}" />
		
	<!-- Return to Top -->
	<a href="javascript:void(0)" id="return-to-top"><i class="fa fa-angle-up" aria-hidden="true"></i></a>
	
	<!-- Theme Custom JS-->
    <script src="/fdahpStudyDesigner/js/theme.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery.mask.min.js"></script>
    <script src="/fdahpStudyDesigner/js/jquery-scrollto.js" ></script>
    <script src="/fdahpStudyDesigner/js/jquery.nicescroll.min.js"></script>
    <script src="/fdahpStudyDesigner/vendor/tinymce/tinymce.min.js"></script>
    <script src="/fdahpStudyDesigner/js/bootbox.min.js"></script>
    
    
    <script src="/fdahpStudyDesigner/js/common.js"></script>
    
    <script>
    window.history.forward();
    function noBack() { 
         window.history.forward(); 
    }
//     $(document).ready(function(){
//     	var a = $(".col-lc").height();
// 		$(".col-rc").css("min-height", a);
//     });
    
    // Fancy Scroll Bar
		(function($){
			$(window).on("load",function(){				
				$(".scrollbars").mCustomScrollbar({					
					theme:"minimal-dark"
				});
			});
		 	if('${sessionScope.sessionObject}' != ''){
		   	  	setTimeout(function(){
			 		 window.location.href = '/fdahpStudyDesigner/errorRedirect.do?error=timeOut';
			 	}, 1000 * 60 * 31);
   	  	 	}
   	  	 	setInterval('clock()', 1000);
		})(jQuery);
    
    
		// ===== Scroll to Top ==== 
		$(window).scroll(function() {
		    if ($(this).scrollTop() >= 50) {        // If page is scrolled more than 50px
		        $('#return-to-top').fadeIn(200);    // Fade in the arrow
		    } else {
		        $('#return-to-top').fadeOut(200);   // Else fade out the arrow
		    }
		});
		$('#return-to-top').click(function() {      // When arrow is clicked
		    $('body,html').animate({
		        scrollTop : 0                       // Scroll to top of body
		    }, 100);
		});
		var startDate = new Date();
		var mytime = moment('<fmt:formatDate value ="${date}"  type = "both"  pattern="yyyy-MM-dd HH:mm:ss"/>').toDate();
		function clock() {
			var diff = new Date().getTime() - startDate.getTime();
			mytime = moment('<fmt:formatDate value ="${date}"  type = "both"  pattern="yyyy-MM-dd HH:mm:ss"/>').toDate();
			mytime.setMilliseconds(mytime.getMilliseconds() + diff);
// 			var seconds = mytime.getSeconds();
// 			var minutes = mytime.getMinutes();
// 			var hours = mytime.getHours();
// 			var currentTime = mytime.get hours + ":" + minutes + ":" + seconds;
// 		  	console.log(mytime);
		}
		var serverDateTime = function() {
			return moment(moment(mytime).format("YYYY-MM-DD HH:mm")).toDate();
		}
		var serverDate = function() {
			return new Date(mytime.getFullYear(), mytime.getMonth(), mytime.getDate());
		}
    </script>
</body>
	
</html>
