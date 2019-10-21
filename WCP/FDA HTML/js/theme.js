/*

Name: 			theme.js
Written by: 	BTC - Maari Vanaraj
Version: 		1.0

*/

wow = new WOW(
  {
  boxClass:     'wow',      // default
  animateClass: 'animated', // default
  offset:       0,          // default
  mobile:       true,       // default
  live:         true        // default
}
)
wow.init();

$(document).ready(function(){
  $("#error").hide();  
 //Login left section
  var wht = $(window).height();
  $(".lg-space-left").css("height",wht) 
  
  var lsimg = $(".lg-space-img").innerHeight();
  var lscov = $(".lg-space-cover").innerHeight();
  var lst = $(".lg-space-txt").innerHeight();
  var ls = wht - lsimg - lscov - lst; 
  $(".lg-space-txt").css("margin-top",ls/2).css("margin-bottom",ls/2);
    

//Login right section  
  var lbox = $(".lg-space-container").height();
  var ptb = (wht - lbox - 5)/2;
  $(".lg-space-container").css("margin-top",ptb).css("margin-bottom",ptb);
    
    
  //Login right section    
  var lgb = $(".login-box").height();  
  var bth = (wht - lgb)/2;
  $(".login-box").css("margin-top",bth).css("margin-bottom",bth);
    
 $("#forgot_pwd").click(function(){
  $(".login").addClass("dis-none");
  $(".pwd").removeClass("dis-none");
});


$("#login").click(function(){
  $(".login").removeClass("dis-none");
  $(".pwd").addClass("dis-none");
});
    
    



    
});

$(window).on('load', function(){    
 
//Login left section
var wht = $(window).height();
$(".lg-space-left").css("height",wht) 

var lsimg = $(".lg-space-img").innerHeight();
var lscov = $(".lg-space-cover").innerHeight();
var lst = $(".lg-space-txt").innerHeight();
var ls = wht - lsimg - lscov - lst;console.log(ls);  
$(".lg-space-txt").css("margin-top",ls/2).css("margin-bottom",ls/2);
    
//Login right section  
  var lbox = $(".lg-space-container").height();
  var ptb = (wht - lbox - 5)/2;
  $(".lg-space-container").css("margin-top",ptb).css("margin-bottom",ptb);


//Login right section    
var lgb = $(".login-box").height();  
var bth = (wht - lgb)/2;
$(".login-box").css("margin-top",bth).css("margin-bottom",bth);
    

    
});


$(window).on('resize', function(){    
 
 //Login left section
  var wht = $(window).height();
  $(".lg-space-left").css("height",wht) 
  
  var lsimg = $(".lg-space-img").innerHeight();
  var lscov = $(".lg-space-cover").innerHeight();
  var lst = $(".lg-space-txt").innerHeight();
  var ls = wht - lsimg - lscov - lst;console.log(ls);  
  $(".lg-space-txt").css("margin-top",ls/2).css("margin-bottom",ls/2);
    
    
//Login right section  
  var lbox = $(".lg-space-container").height();
  var ptb = (wht - lbox - 5)/2;
  $(".lg-space-container").css("margin-top",ptb).css("margin-bottom",ptb);
    

  //Login right section    
  var lgb = $(".login-box").height();  
  var bth = (wht - lgb)/2;
  $(".login-box").css("margin-top",bth).css("margin-bottom",bth);
    
    
});
