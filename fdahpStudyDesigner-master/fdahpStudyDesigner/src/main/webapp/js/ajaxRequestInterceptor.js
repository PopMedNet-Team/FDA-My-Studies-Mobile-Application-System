/**
 * @author Vivek
 * 
 * @see Intercepts the ajax request.
 */
function ajaxSessionTimeout() {
	window.location.href = '/fdahpStudyDesigner/errorRedirect.do?error=timeOut';
}

!function($) {
	$.ajaxSetup({
		statusCode : {
			901 : ajaxSessionTimeout
		},
		cache : false
	});
}(window.jQuery);