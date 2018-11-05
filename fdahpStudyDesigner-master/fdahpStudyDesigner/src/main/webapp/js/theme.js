/*

Name: 			theme.js
Written by: 	BTC - Maari Vanaraj
Version: 		1.0

 */

wow = new WOW({
	boxClass : 'wow', // default
	animateClass : 'animated', // default
	offset : 0, // default
	mobile : true, // default
	live : true
// default
})
wow.init();

$(document).ready(
		function() {
			$("#error").hide();
			// Login left section
			var wht = $(window).height();
			var wdt = $(window).width();

			$(".lg-space-left").css("min-height", wht);

			var lsimg = $(".lg-space-img").innerHeight();
			var lscov = $(".lg-space-cover").innerHeight();
			var lst = $(".lg-space-txt").innerHeight();
			var ls = wht - lsimg - lscov - lst;
			$(".lg-space-txt").css("margin-top", ls / 2).css("margin-bottom",
					ls / 2);

			// While clicking submit button

			$("#log-btn").click(function() {
				var wht = $(window).height();
				$(".lg-space-left").css("min-height", wht);
			});

			// Landing page right section
			var mlbox = $(".lg-space-center").height();
			var mtb = (wht - mlbox - 5) / 2;
			$(".lg-space-center").css("margin-top", mtb).css("margin-bottom",
					mtb);

			// Login right section
			var lgb = $(".login-box").height();
			var bth = (wht - lgb) / 2;
			$(".login-box").css("margin-top", bth).css("margin-bottom", bth);

			// Password Reset Form section
			var prfs = $("#passwordResetForm").height();
			bth = (wht - prfs) / 2;
			$("#passwordResetForm").css("margin-top", bth).css("margin-bottom",
					bth);

			// Register page
			var rlbox = $(".lg-register-center").height();
			var rptb = (wht - rlbox - 40) / 2;
			$(".lg-register-center").css("margin-top", rptb).css(
					"margin-bottom", rptb);

			$("#forgot_pwd").click(function() {
				$('#sucMsg').hide();
				$('#errMsg').hide();
				$(".login").addClass("dis-none");
				$(".pwd").removeClass("dis-none");
				resetValidation('#loginForm');
				resetValidation('#forgotForm');
				$('#loginForm input').val('');
				$('#emailReg').focus();
			});

			$("#login").click(function() {
				$('#sucMsg').hide();
				$('#errMsg').hide();
				$(".login").removeClass("dis-none");
				$(".pwd").addClass("dis-none");
				resetValidation('#forgotForm');
				resetValidation('#loginForm');
				$('#forgotForm input').val('');
				$('#email').focus();
			});

		});
var rtime;
var timeout = false;
var delta = 200;

$(window).on('load resize', function() {

	rtime = new Date();
	if (timeout === false) {
		timeout = true;
		setTimeout(resizeend, delta);
	}

});
function resizeend() {
	if (new Date() - rtime < delta) {
		setTimeout(resizeend, delta);
	} else {
		timeout = false;
		responsiveScreen();
	}
}

function responsiveScreen() {
	// Login left section
	var wht = $(window).height();
	var wdt = $(window).width();
	$(".lg-space-left").width();

	$(".lg-space-left").css("min-height", wht);
	$(".lg-space-left").css("width", "410px");

	var lsimg = $(".lg-space-img").innerHeight();
	var lscov = $(".lg-space-cover").innerHeight();
	var lst = $(".lg-space-txt").innerHeight();
	var ls = wht - lsimg - lscov - lst;
	$(".lg-space-txt").css("margin-top", ls / 2).css("margin-bottom", ls / 2);

	// Landing page right section
	var mlbox = $(".lg-space-center").height();
	var mtb = (wht - mlbox - 5) / 2;
	$(".lg-space-center").css("margin-top", mtb).css("margin-bottom", mtb);

	// Login right section
	var lgb = $(".login-box").height();
	var bth = (wht - lgb) / 2;
	$(".login-box").css("margin-top", bth).css("margin-bottom", bth);

	// Register page
	var rlbox = $(".lg-register-center").height();
	var rptb = (wht - rlbox - 40) / 2;
	$(".lg-register-center").css("margin-top", rptb).css("margin-bottom", rptb);

}
