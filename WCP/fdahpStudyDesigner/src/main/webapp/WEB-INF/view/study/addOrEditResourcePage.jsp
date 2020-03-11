<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
  <!-- ============================================================== -->
         <!-- Start right Content here -->
         <!-- ============================================================== --> 
        <div class="col-sm-10 col-rc white-bg p-none">
        <form:form action="/fdahpStudyDesigner/adminStudies/saveOrUpdateResource.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}" data-toggle="validator" id="resourceForm" role="form" method="post" autocomplete="off" enctype="multipart/form-data">    
            <!--  Start top tab section-->
            <div class="right-content-head">        
                <div class="text-right">
                    <div class="black-md-f dis-line pull-left line34"><span class="pr-sm"><a href="javascript:void(0)" class="goToResourceListForm" id="goToResourceListForm"><img src="/fdahpStudyDesigner/images/icons/back-b.png"/></a></span>
                    <c:if test="${isstudyProtocol ne 'isstudyProtocol'}">
                    <c:if test="${actionOn eq 'add'}">Add Resource</c:if>
                    <c:if test="${actionOn eq 'edit'}">Edit Resource</c:if>
                    <c:if test="${not empty resourceBO && actionOn eq 'view'}">View Resource <c:set var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</c:if>
                    </c:if>
                    <c:if test="${isstudyProtocol eq 'isstudyProtocol'}">
                    <c:if test="${actionOn eq 'add'}">Add Study Protocol</c:if>
                    <c:if test="${actionOn eq 'edit'}">Edit Study Protocol</c:if>
                    </c:if></div>
                     
                    <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn goToResourceListForm" id="goToStudyListPage">Cancel</button>
                     </div>
                    
                     <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn viewAct" id="saveResourceId">Save</button>
                     </div>

                     <div class="dis-line form-group mb-none">
                         <button type="button" class="btn btn-primary blue-btn viewAct" id="doneResourceId">Done</button>
                     </div>
                 </div>
            </div>
            <!--  End  top tab section-->
            <input type="hidden" name="id" value="${resourceBO.id}"/>
            <input type="hidden" id="buttonText" name="buttonText">
            <input type="hidden" id="actionOn" name="actionOn">
            <c:if test="${isstudyProtocol eq 'isstudyProtocol'}">
            	<input type="hidden" name="isstudyProtocol" value="isstudyProtocol"/>
            </c:if>
            
            
            <!--  Start body tab section -->
            <div class="right-content-body">
                
             <div class="mt-none">
                <!-- form- input-->
                <div>
                   <div class="gray-xs-f mb-xs">Title <c:if test="${isstudyProtocol ne 'isstudyProtocol'}">&nbsp;<small class="viewAct">(50 characters max)</small></c:if><span class="requiredStar"> *</span></div>
                   <div class="form-group">
                        <input autofocus="autofocus" type="text" class="form-control" id="resourceTitle" name="title" value="${fn:escapeXml(resourceBO.title)}" maxlength="50" required  <c:if test="${isstudyProtocol eq 'isstudyProtocol'}">readonly</c:if>/>
                   		<div class="help-block with-errors red-txt"></div>
                   </div>
                </div>
             </div>
                
            <div class="clearfix"></div>
                
            <div>
            <div class="gray-xs-f mb-xs">Content Type<span class="requiredStar"> *</span></div>
                 <span class="radio radio-info radio-inline p-45">
                    <input type="radio" class="addResource" id="inlineRadio1"  name="textOrPdfParam" value="0" <c:if test="${not resourceBO.textOrPdf}">checked</c:if>>
                    <label for="inlineRadio1">Rich Text editor</label>
                </span>
                <span class="radio radio-inline">
                    <input type="radio" id="inlineRadio2" class="addResource"  name="textOrPdfParam" value="1" <c:if test="${resourceBO.textOrPdf}">checked</c:if>>
                    <label for="inlineRadio2">Upload PDF</label>
                </span>  
            </div>
                
            <div class="clearfix"></div>
             
            <div id="richEditor" class="mt-lg form-group resetContentType <c:if test="${resourceBO.textOrPdf}">dis-none</c:if>">
              <textarea class="remReqOnSave" id="richText" name="richText" required>${resourceBO.richText}</textarea>
               <div class="help-block with-errors red-txt"></div>      
            </div>
            
            
            <div id="pdf_file" class="mt-lg form-group resetContentType <c:if test="${empty resourceBO || not resourceBO.textOrPdf}">dis-none</c:if>">
                <button id="uploadPdf" type="button" class="btn btn-default gray-btn uploadPdf viewAct">Upload PDF</button>
                <input id="uploadImg" class="dis-none remReqOnSave" type="file" name="pdfFile" accept=".pdf" data-error="Please select a pdf file" required>
                <input type="hidden" class="remReqOnSave" value="${resourceBO.pdfUrl}" required id="pdfUrl" name="pdfUrl">
                <input type="hidden" value="${resourceBO.pdfName}" id="pdfName" name="pdfName">
             <span class="alert customalert pdfDiv">
                <a href="javascript:void(0)" id="pdf_name" class="pdfClass" >${resourceBO.pdfName}</a>
				<span id="delete" class="blue-link dis-none viewAct borr">&nbsp;X<a href="javascript:void(0)" class="blue-link pl-xs mr-sm">Remove PDF</a></span>
			</span>
            <div class="help-block with-errors red-txt"></div>  
            </div>
             
            <c:if test="${isstudyProtocol ne 'isstudyProtocol'}">   
            <div class="clearfix"></div>
                
            <div class="mt-xs">
                <div class="gray-xs-f mb-sm">Set a Period of Visibility for this resource? <span class="requiredStar">*</span> <span data-toggle="tooltip" data-placement="top" title="If you choose Yes, the resource will be made available in the app for the selected time period. If you choose No, the Resource is available for the entire duration of the study." class="filled-tooltip"></span></div>
                 <span class="radio radio-info radio-inline p-45">
                    <input type="radio" id="inlineRadio3" name="resourceVisibilityParam" value="0"
                     <c:if test="${not resourceBO.resourceVisibility}">checked</c:if>>
                      <label for="inlineRadio3">Yes</label>
                </span>
                <span class="radio radio-inline">
                    <input type="radio" id="inlineRadio4" name="resourceVisibilityParam" value="1" <c:if test="${resourceBO.resourceVisibility  || empty resourceBO}">checked</c:if>>
                    <label for="inlineRadio4">No</label>
                </span>    
                <div class="help-block with-errors red-txt"></div>
            </div>
                
            <div class="clearfix"></div>
               
             <div class="mt-lg resetDate">
                <div class="gray-xs-f mb-xs">Select Time Period <span class="requiredStar">*</span></div>
                <div id="selectTime">
                 <span class="radio radio-info radio-inline pr-md">
                    <input type="radio" id="inlineRadio5" class="disRadBtn1" value="1" name="resourceTypeParm">
                    <label for="inlineRadio5">Anchor Date-based Period</label><br/>
                </span>
                <%-- <c:if test="${fn:length(anchorTypeList) gt 0}">
                	<div>
	            	  <div class="gray-xs-f col-md-3 col-lg-3 p-none mt-sm">Select Anchor Date Type<span class="requiredStar">*</span></div>
	                  <div class="col-md-3 col-lg-3 p-none">
		                  <div class="form-group">
		                     <select id="anchorDateId" class="selectpicker disRadBtn1 disBtn1" required name="anchorDateId">
		                      <option value='' >Select</option>
		                      <c:forEach items="${anchorTypeList}" var="anchorTypeInfo">
		                      	<option value="${anchorTypeInfo.id}" ${resourceBO.anchorDateId eq anchorTypeInfo.id ? 'selected' : ''}>${anchorTypeInfo.name}</option>
		                      </c:forEach>
		                     </select>
		                     <div class="help-block with-errors red-txt"></div>
		                  </div>
	                  </div>
	                  <div class="clearfix"></div>
	                 </div>
                </c:if> --%>
                	<div>
	            	  <div class="gray-xs-f col-md-3 col-lg-3 p-none mt-sm">Select Anchor Date Type<span class="requiredStar">*</span></div>
	                  <div class="col-md-3 col-lg-3 p-none">
		                  <div class="form-group">
		                     <select id="anchorDateId" class="selectpicker disBtn1" required name="anchorDateId">
		                      <option value='' >Select</option>
		                      <c:forEach items="${anchorTypeList}" var="anchorTypeInfo">
		                      <option value="${anchorTypeInfo.id}" ${resourceBO.anchorDateId eq anchorTypeInfo.id ? 'selected' : ''}>${anchorTypeInfo.name}</option>
		                      </c:forEach>
		                     </select>
		                     <div class="help-block with-errors red-txt"></div>
		                  </div>
	                  </div>
	                  <div class="clearfix"></div>
	                 </div>
                <span class="mb-sm pr-md">
                    <span class="light-txt opacity06">Anchor Date </span>                   
                 </span>
                <span>
	                 <select class="signDropDown selectpicker sign-box" title="Select" name="xDaysSign" id="xSign">
	                          <option value="0" ${not resourceBO.xDaysSign ?'selected':''}>+</option>
	                          <option value="1" ${resourceBO.xDaysSign ?'selected':''}>-</option>
	                 </select>
                </span>
                <!--  selectpicker -->
                 <span class="form-group m-none dis-inline vertical-align-middle">
                 	
                     <input id="xdays" type="text" class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm resetAncDate" 
                     placeholder="X" name="timePeriodFromDays" value="${resourceBO.timePeriodFromDays}" oldxDaysVal="${resourceBO.timePeriodFromDays}" 
                     maxlength="3" required pattern="[0-9]+" data-pattern-error="Please enter valid number."/>
                 	 <span class="help-block with-errors red-txt"></span>
                 </span>
                 <span class="mb-sm pr-md">
                    <span class="light-txt opacity06"> days <span style="padding-right:5px;padding-left:5px">to </span>  Anchor Date </span>                   
                 </span>
                 
                 <span>
                 	 <select class="signDropDown selectpicker sign-box" title="Select" name="yDaysSign" id="ySign">
                              <option value="0" ${not resourceBO.yDaysSign ?'selected':''}>+</option>
                              <option value="1" ${resourceBO.yDaysSign ?'selected':''}>-</option>
                     </select>
                 </span>
                 
                  <span class="form-group m-none dis-inline vertical-align-middle">
                    
                     <input id="ydays" type="text" class="form-control wid70 disRadBtn1 disBtn1 remReqOnSave daysMask mt-sm resetAncDate" placeholder="Y" 
                     	name="timePeriodToDays" value="${resourceBO.timePeriodToDays}" oldyDaysVal="${resourceBO.timePeriodToDays}" maxlength="3" required />
                     	
                 	 <span class="help-block with-errors red-txt"></span>
                 </span>
                 <span class="mb-sm pr-md">
	                    <span class="light-txt opacity06 disBtn1"> days </span>                   
	                 </span>
                 </div>
             </div>
                
             <div class="mt-lg resetDate">
                 <div class="mb-none">
                     <span class="radio radio-info radio-inline pr-md">
                        <input type="radio" class="disRadBtn1" id="inlineRadio6" value="0" name="resourceTypeParm">
                        <label for="inlineRadio6">Custom Date Range</label>
                    </span>
                </div>
                 <div>
                     <span class="form-group m-none dis-inline vertical-align-middle pr-md">
                         <input id="StartDate" type="text" class="form-control disRadBtn1 disBtn2 datepicker remReqOnSave mt-md" placeholder="Start Date" name="startDate" value="${resourceBO.startDate}" oldStartDateVal="${resourceBO.startDate}" required/>
                         <span class="help-block with-errors red-txt"></span>
                     </span>
                     <span class="gray-xs-f mb-sm pr-md">
                        to 
                     </span>
                      <span class="form-group m-none dis-inline vertical-align-middle">
                         <input id="EndDate" type="text" class="form-control disRadBtn1 disBtn2 datepicker remReqOnSave mt-md" placeholder="End Date" name="endDate" value="${resourceBO.endDate}" oldEndDateVal="${resourceBO.endDate}" required/>
                    	 <span class="help-block with-errors red-txt"></span>
                     </span>
                     <div class="help-block with-errors red-txt"></div>
                 </div>
             </div>
            
              <div class="clearfix"></div>
                
             <div class="mt-sm">
                <div class="gray-xs-f mb-xs">Text for notifying participants about the new resource being available&nbsp;<small class="viewAct">(250 characters max)</small> <span class="requiredStar">*</span> </div>
                 
                 <div class="form-group">
                  <textarea class="form-control remReqOnSave" rows="4" id="comment" name="resourceText" data-error="Please enter plain text of up to 250 characters max." maxlength="250" required>${resourceBO.resourceText}</textarea>
                  <div class="help-block with-errors red-txt"></div>
                 </div>
             </div>
            </c:if>
            
                
            </div>
            <!--  End body tab section -->
            <!-- Modal -->
			<div class="modal fade" id="myModal" role="dialog">
			   <div class="modal-dialog modal-lg" style="width:98%;">
			      <!-- Modal content-->
			      <div class="modal-content">
			      <div class="modal-header">
			          <button type="button" class="close" data-dismiss="modal">&times;</button>
			       </div>
			         <div class="modal-body pt-xs pb-lg pl-xlg pr-xlg" >
			            <embed id="embedPdfId" src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />studyResources/${resourceBO.pdfUrl}" oncontextmenu="return false;" onkeydown="return false;" onmousedown="return false;" width="100%" height="500px" />
			         </div>
			      </div>
			   </div>
			</div>
        </form:form>   
        </div>
        <!-- End right Content here -->
<form:form action="/fdahpStudyDesigner/adminStudies/getResourceList.do?_S=${param._S}" name="resourceListForm" id="resourceListForm" method="post">
</form:form>
<form:form action="/fdahpStudyDesigner/downloadPdf.do?_S=${param._S}"  id="pdfDownloadFormId" method="post" target="_blank" >
	<input type="hidden" value="studyResources"  name="fileFolder"/>
	<input type="hidden" value="${resourceBO.pdfUrl}"  name="fileName"/>
</form:form>
<script type="text/javascript">
$(document).ready(function(){
	
	//$('[data-id="anchorDateId"]').prop('disabled', true);
	
	<c:if test="${isstudyProtocol eq 'isstudyProtocol' && empty resourceBO.title}">
		$('#resourceTitle').val('Study Protocol');
	</c:if>
	
	$('#embedPdfId').bind('contextmenu', function(e) {
		alert("Right click has been disabled.");
	    return false;
	}); 
	
	    $('.daysMask').mask('000');
	
	    $(".menuNav li").removeClass('active');
	    $(".eighthResources").addClass('active'); 
		$("#createStudyId").show();
        
	 $("#doneResourceId").on('click', function(){
		 $('#doneResourceId').prop('disabled',true);
          if( chkDaysValid(true) && isFromValid('#resourceForm')){
        	  if($('#inlineRadio5').is(':checked')){
        		  var text = "You have chosen to use a period of visibility based on an Anchor Date. Please ensure that the Source Questionnaire providing the Anchor Date response is scheduled appropriately.";
              	  bootbox.confirm({
              		closeButton: false,
              		message: text,
              		buttons: {
      			        'cancel': {
      			            label: 'Cancel',
      			        },
      			        'confirm': {
      			            label: 'OK',
      			        },
      			    },
      			    callback: function(valid) {
      			    	if (valid) {
      			    		console.log(1);
      			    		$('#buttonText').val('done');
      	   		   		    $('#resourceForm').submit(); 
      			    	}else{
      			    		console.log(2);
      			    		$('#doneResourceId').prop('disabled',false);
      			    	}
      			      }
              	   });
        	  }else{
        		  $('#buttonText').val('done');
   		   		  $('#resourceForm').submit(); 
        	  }
 		   }else{
 			  $('#doneResourceId').prop('disabled',false);
 		   }
	    });
	 
	 if($('#inlineRadio1').prop('checked')){
		 $('#uploadImg').removeAttr('required');
		 $('#pdfUrl').removeAttr('required');
		 $('#richText').attr('required','required');
		 resetValidation('#resourceForm');
	 }else{
		 $('#richText').removeAttr('required');
		  var file = $('#uploadImg').val();
          var pdfId = $('#pdfUrl').val();
          $('#richText').removeAttr('required');
		  if(pdfId){
			  $('#pdfUrl').attr('required','required');
			  $('#uploadImg').removeAttr('required');
		  }else{
			  $('#uploadImg').attr('required','required');
			  $('#pdfUrl').removeAttr('required');
		  }
		  resetValidation('#resourceForm');
	 }
	 
	 $('#inlineRadio1','#inlineRadio2').on('change',function(){
		 if($('#inlineRadio1').prop('checked')){
			 $('#uploadImg').removeAttr('required');
			 $('#pdfUrl').removeAttr('required');
		 }else{
			 $('#richText').removeAttr('required');
			  var file = $('#uploadImg').val();
	          var pdfId = $('#pdfUrl').val();
	          $('#richText').removeAttr('required');
			  if(pdfId){
				  $('#pdfUrl').attr('required','required');
			  }else{
				  $('#uploadImg').attr('required','required');
			  }
		 }
		  resetValidation('#resourceForm');
	 });
	  
	$('#saveResourceId').click(function() {
		 $('#saveResourceId').prop('disabled',true);
		   	$("#resourceTitle").parent().find(".help-block").empty();
	   		$('#resourceForm').validator('destroy').validator();
	   		var isValid = true;
	   		var anchorList = "${anchorTypeList}";
			 var length = anchorList.length;
	   if($('#inlineRadio5').prop('checked') && ($('#xdays').val() || $('#ydays').val())) {
		   isValid = chkDaysValid(false);
	   }
       if(!$('#resourceTitle')[0].checkValidity()){
    	if($("#resourceTitle").parent().addClass('has-error has-danger').find(".help-block").text() == ''){
    		$("#resourceTitle").parent().addClass('has-error has-danger').find(".help-block").append('<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');
    	}
       	$('#saveResourceId').prop('disabled',false);
    	  return false;
       }else if(isValid){
    	   	var actionOn = '${actionOn}';
	       	$('#resourceForm').validator('destroy');
	       	$("#actionOn").val(actionOn);
	       	$("#buttonText").val('save');
	       	$('#resourceForm').submit();
       }
      $('#saveResourceId').prop('disabled',false);
	});
	
	 pdfUrlName = $('#pdfUrl').val();
     if(pdfUrlName != ""){
       $("#uploadPdf").text("Change PDF");
       $("#delete").removeClass("dis-none");
     }else{
    	 $('.pdfDiv').hide();
     }
     
     
 	$('.goToResourceListForm').on('click',function(){
        <c:if test="${actionOn ne 'view'}">
 		bootbox.confirm({
			closeButton: false,
			message : 'You are about to leave the page and any unsaved changes will be lost. Are you sure you want to proceed?',	
		    buttons: {
		        'cancel': {
		            label: 'Cancel',
		        },
		        'confirm': {
		            label: 'OK',
		        },
		    },
		    callback: function(result) {
		        if (result) {
		        	$('#resourceListForm').submit();
		        }
		    }
	    });
 		</c:if>
 		<c:if test="${actionOn eq 'view'}">
 			$('#resourceListForm').submit();
 		</c:if>
	});
	
	 // File Upload    
    $(".uploadPdf,.changePdf").click(function(){               
       $("#uploadImg").click();
    });
	 
    if($("#richText").length > 0){
    tinymce.init({
        selector: "#richText",
        theme: "modern",
        skin: "lightgray",
        height:150,
        plugins: [
            "advlist autolink link image lists charmap hr anchor pagebreak spellchecker",
            "save contextmenu directionality paste"
        ],
        toolbar: "anchor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | underline link | hr removeformat | cut undo redo | fontsizeselect fontselect",
        menubar: false,
        toolbar_items_size: 'small',
        content_style: "div, p { font-size: 13px;letter-spacing: 1px;}",
        setup : function(ed) {
            ed.on('keypress change', function(ed) {
            	resetValidation('.resetContentType');
            	resetValidation($('#'+ed.target.id).val(tinyMCE.get(ed.target.id).getContent()).parents('form #richText'));
            });
     	  },
     	 <c:if test="${actionOn eq 'view'}">readonly:1</c:if>
    });
	}
  
    //Toggling Rich richText and Upload Button    
    $(".addResource").click(function(){
        var a = $(this).val();
        if(a == '0'){
            $("#richEditor").removeClass("dis-none");
            $("#pdf_file").addClass("dis-none");
            $('#richText').attr('required','required');
  		  	$('#uploadImg').removeAttr('required');
  		  	$('#pdfUrl').removeAttr('required');
        }else if(a == '1'){
            $("#richEditor").addClass("dis-none");
            $("#pdf_file").removeClass("dis-none");
            $('#richText').removeAttr('required');
            $('#uploadImg').attr('required','required');
            $('#pdfUrl').attr('required','required');
        }
        resetValidation($(this).parents('form'));
    });
    
    
  //Changing & Displaying upload button text & file name
  
    $('#uploadImg').on('change',function (){
    
    	var fileExtension = ['pdf'];
		var file, reader;
		var thisAttr = this;
		var thisId = $(this).attr("data-imageId");
		if ((file = this.files[0])) {
			reader = new FileReader();
			reader.onload = function() {
		        if ($.inArray($(thisAttr).val().split('.').pop().toLowerCase(), fileExtension) == -1) {
		        	$("#uploadImg").parent().addClass('has-error has-danger').find(".help-block").html('<ul class="list-unstyled"><li>Please select a pdf file</li></ul>');
		        	$("#delete").click();
		        }else if($('input[type=file]').val()){
		        	$('#pdfClk').attr('href','javascript:void(0)').css('cursor', 'default');
		        	$('.pdfDiv').show();
			        var filename = $(thisAttr).val().replace(/C:\\fakepath\\/i, '').split(/\\/i)[$(thisAttr).val().replace(/C:\\fakepath\\/i, '').split(/\\/i).length - 1];
			        $("#pdf_name").prop('title',filename).text(filename);
			        
			        var a = $("#uploadPdf").text();
			        if(a == "Upload PDF"){
			          $("#uploadPdf").text("Change PDF");
			        }
		       		$("#delete").removeClass("dis-none");
		       		$("#uploadImg").parent().removeClass('has-error has-danger').find(".help-block").html('');
		       		$('.pdfClass').off( "click");
		    	}
    		};
    		reader.onerror = function() {
    			$("#uploadImg").parent().addClass('has-error has-danger').find(".help-block").html('<ul class="list-unstyled"><li>Please select a pdf file</li></ul>');
		        $("#delete").click();
    		}
    		reader.readAsDataURL(file)
    	}
        resetValidation($("#uploadImg").parents('form'));
   });
  
  //Deleting Uploaded pdf
    $("#delete").click(function(){
       $("#uploadPdf").text("Upload PDF");
       $("#pdf_name").prop('title','').text(""); 
       $(this).addClass("dis-none");
       $('input[type=file]').val('');
       $('#pdfUrl').val('');
       $('#pdfName').val('');
       $("#uploadImg").attr('required','required');
       $('.pdfDiv').hide();
       resetValidation($("#uploadImg").parents('form'));
    });
	
	<c:if test="${isstudyProtocol ne 'isstudyProtocol'}">
	<c:if test="${not empty resourceBO.timePeriodFromDays || not empty resourceBO.timePeriodToDays}">
		$('.disBtn1').attr('required','required');
		$('.disBtn2').removeAttr('required');
		$('.disBtn2').prop('disabled',true);
		$('#inlineRadio5').prop('checked',true);
		$('#inlineRadio6').prop('checked',false);
		resetValidation($(this).parents('form'));
	</c:if>
		<c:if test="${empty resourceBO || not empty resourceBO.startDate || not empty resourceBO.endDate}">
		$('.disBtn2').attr('required','required');
		$('.disBtn1').removeAttr('required');
		$('.disBtn1').prop('disabled',true);
		$('#inlineRadio6').prop('checked',true);
		$('#inlineRadio5').prop('checked',false);
		resetValidation($(this).parents('form'));
		</c:if>
	


		$("#xdays, #ydays").on('blur',function(){
			chkDaysValid(false);
		});
	 $('#StartDate').datetimepicker({
        format: 'MM/DD/YYYY',
        ignoreReadonly: true,
        useCurrent :false,
     });
    $('#EndDate').datetimepicker({
         format: 'MM/DD/YYYY',
         ignoreReadonly: true,
         useCurrent: false,
     }); 
     
     $(".datepicker").on("click", function (e) {
         $('#StartDate').data("DateTimePicker").minDate(new Date(new Date().getFullYear(),new Date().getMonth(), new Date().getDate()));
         var startDate = $("#StartDate").data("DateTimePicker").date();
         if(startDate != null && startDate != '' && typeof startDate != 'undefined'){
        	 if(new Date(new Date().getFullYear(),new Date().getMonth(), new Date().getDate()) > startDate){
        		 $('#EndDate').data("DateTimePicker").minDate(new Date(new Date().getFullYear(),new Date().getMonth(), new Date().getDate())); 
        	 }else{
        		 $('#EndDate').data("DateTimePicker").minDate(new Date(startDate));
        	 }
         }else{
        	 $('#EndDate').data("DateTimePicker").minDate(new Date(new Date().getFullYear(),new Date().getMonth(), new Date().getDate()));
         }
     });
     
     $("#StartDate").on("dp.change", function (e) {
			if($("#EndDate").data("DateTimePicker").date() < $(this).data("DateTimePicker").date()) {
				$("#EndDate").val('');
			}
        	$("#EndDate").data("DateTimePicker").minDate(new Date(e.date._d));
     });
        
		$('#inlineRadio5').on('click',function(){
			if($('#inlineRadio5').prop('checked') == true){
			$('.disBtn1').prop('disabled',false);
			$('.disRadBtn1').prop('disabled',false);
			$('.disBtn1').selectpicker('refresh');
			$('.disBtn2').prop('disabled',true);
			$('.disBtn2').val('');
			$('.disBtn1').attr('required','required');
			$('.disBtn2').removeAttr('required');
			//$('[data-id="anchorDateId"]').prop('disabled', false);
			if($('#xdays').attr('oldxDaysVal') != ''){
				$('#inlineRadio5').prop('checked',true);
				//$('#xdays').val($('#xdays').attr('oldxDaysVal'));
				$('.disBtn1').prop('disabled',false);
				$('.disBtn2').prop('disabled',true);
				$('.disBtn1').selectpicker('refresh');
			}
			if($('#ydays').attr('oldyDaysVal') != ''){
				$('#inlineRadio5').prop('checked',true);
				//$('#ydays').val($('#ydays').attr('oldyDaysVal'));
				$('.disBtn1').prop('disabled',false);
				$('.disBtn2').prop('disabled',true);
				$('.disBtn1').selectpicker('refresh');
			}
			resetValidation('.resetDate');
			}
		});
		
		$('#inlineRadio6').on('click',function(){
			if($('#inlineRadio6').prop('checked') == true){
			$('.disBtn2').prop('disabled',false);
			$('.disBtn1').prop('disabled',true);
			$('.disBtn1').val('');
			$('.disBtn2').attr('required','required');
			$('.disBtn1').removeAttr('required');
			$('.disBtn1').selectpicker('refresh');
			//$('[data-id="anchorDateId"]').prop('disabled', true);
			$('#ydays').parent().removeClass('has-error has-danger').find(".help-block").html("");
			if($('#StartDate').attr('oldStartDateVal') != ''){
				$('#inlineRadio6').prop('checked',true);
				$('#StartDate').val($('#StartDate').attr('oldStartDateVal'));
				$('.disBtn1').prop('disabled',true);
				$('.disBtn2').prop('disabled',false);
			}
			if($('#EndDate').attr('oldEndDateVal') != ''){
				$('#inlineRadio6').prop('checked',true);
				$('#EndDate').val($('#EndDate').attr('oldEndDateVal'));
				$('.disBtn1').prop('disabled',true);
				$('.disBtn2').prop('disabled',false);
			}
			resetValidation('.resetDate');
			}
		});
		
	
		if($('#inlineRadio3').prop('checked') == false){
			$('#inlineRadio5').prop('checked',false);
			$('#inlineRadio6').prop('checked',false);
			$('.disRadBtn1').prop('disabled',true);
			$('.disBtn1').removeAttr('required');
			$('.disBtn2').removeAttr('required');
			resetValidation($(this).parents('form'));
		}
		
		
		
		$('#inlineRadio3').on('click',function(){
			if($('#inlineRadio3').prop('checked') == true){
				
			var anchorTypeList = "${anchorTypeList}";
			var length = anchorTypeList.length;
				 if(length < 3){
					 $('#inlineRadio5').prop('disabled',true);
						$('.disRadBtn1').prop('disabled',true);	
						$('.disRadBtn1').prop('checked',false);
						$('.disRadBtn1').val('');	
						$('.disBtn1').removeAttr('required');
						$('.disBtn1').val('');
						
				 }
			$('.disBtn2').prop('disabled',true);
			$('#inlineRadio6').prop('disabled',false);
			$('.disBtn2').val('');
				if($('#xdays').attr('oldxDaysVal') != ''){
					$('#inlineRadio5').prop('checked',true);
					$('#xdays').val($('#xdays').attr('oldxDaysVal'));
					$('.disBtn1').prop('disabled',false);
					$('.disBtn2').prop('disabled',true);
					$('.disBtn1').attr('required','required');
					$('.disBtn2').removeAttr('required');
					$('#inlineRadio5').prop('disabled',false);	
					$('#inlineRadio5').val('');	
					//$('.disRadBtn1').prop('checked',true);
					$('.disBtn1').val('');
					$('.disBtn1').selectpicker('refresh');
					resetValidation($('.resetDate'));
				}
				if($('#ydays').attr('oldyDaysVal') != ''){
					$('#inlineRadio5').prop('checked',true);
					$('#ydays').val($('#ydays').attr('oldyDaysVal'));
					$('.disBtn1').prop('disabled',false);
					$('.disBtn2').prop('disabled',true);
					$('.disBtn1').attr('required','required');
					$('.disBtn2').removeAttr('required');
					$('.disBtn1').val('');
					$('#inlineRadio5').prop('disabled',false);	
					$('#inlineRadio5').val('');	
					//$('.disRadBtn1').prop('checked',true);
					$('.disBtn1').selectpicker('refresh');
					resetValidation($('.resetDate'));
				}
				if($('#StartDate').attr('oldStartDateVal') != ''){
					$('#inlineRadio6').prop('checked',true);
					$('#StartDate').val($('#StartDate').attr('oldStartDateVal'));
					$('.disBtn1').prop('disabled',true);
					$('.disBtn2').prop('disabled',false);
					$('.disBtn2').attr('required','required');
					$('.disBtn1').removeAttr('required');
					$('.disRadBtn1').prop('checked',false);
					 if(length < 3){
						 $('#inlineRadio5').prop('disabled',true);
							$('.disRadBtn1').prop('disabled',true);	
							$('.disRadBtn1').prop('checked',false);
							$('.disRadBtn1').val('');	
							$('.disBtn1').removeAttr('required');
							$('.disBtn1').val('');
							
					 }else{
						 $('#inlineRadio5').prop('disabled',false);
					 }
					resetValidation($('.resetDate'));
				}
				if($('#EndDate').attr('oldEndDateVal') != ''){
					$('#inlineRadio6').prop('checked',true);
					$('#EndDate').val($('#EndDate').attr('oldEndDateVal'));
					$('.disBtn1').prop('disabled',true);
					$('.disBtn2').prop('disabled',false);
					$('.disBtn2').attr('required','required');
					$('.disBtn1').removeAttr('required');
					$('.disBtn1').selectpicker('refresh');
					 if(length < 3){
						 $('#inlineRadio5').prop('disabled',true);
							$('.disRadBtn1').prop('disabled',true);	
							$('.disRadBtn1').prop('checked',false);
							$('.disRadBtn1').val('');	
							$('.disBtn1').removeAttr('required');
							$('.disBtn1').val('');
							
					 }else{
						 $('#inlineRadio5').prop('disabled',false);
					 }
					resetValidation($('.resetDate'));
				}
				if($('#xdays').attr('oldxDaysVal') == '' && $('#ydays').attr('oldyDaysVal') == '' && $('#StartDate').attr('oldStartDateVal') == '' && $('#EndDate').attr('oldEndDateVal') == ''){
					 $('#inlineRadio6').prop('checked',true);
					$('.disBtn2').prop('disabled',false);
					$('.disBtn1').prop('disabled',true);
					$('.disBtn2').attr('required','required');
					$('.disBtn1').removeAttr('required');
					
					//added by sweta
				 	 if(length > 3 || $('#ydays').attr('oldyDaysVal') != '' || $('#ydays').attr('oldyDaysVal') != ''){
				 		 $('#inlineRadio5').prop('disabled',false);
				 		 //added by sweta
				 		resetValidation($('.resetDate'));
					 } 
					
				}
			}
			 var a = $("#inlineRadio3").val();
			if(a ==0){
			   $(".light-txt").removeClass("opacity06");
			}else{
			  $(".light-txt").addClass("opacity06");
			} 
			resetValidation($('.resetDate'));
		});
		
		if($('#inlineRadio3').prop('checked') == true){
			var anchorTypeList = "${anchorTypeList}";
			var length = anchorTypeList.length;
				
		if($('#xdays').attr('oldxDaysVal') == '' && $('#ydays').attr('oldyDaysVal') == '' && $('#StartDate').attr('oldStartDateVal') == '' && $('#EndDate').attr('oldEndDateVal') == ''){
			$('#inlineRadio6').prop('checked',true);
			$('.disBtn2').prop('disabled',false);
			$('.disBtn1').prop('disabled',true);
			$('.disBtn2').attr('required','required');
			$('.disBtn1').removeAttr('required');
			 if(length < 3){
				 $('#inlineRadio5').prop('checked',false);
				 $('.disBtn1').prop('disabled',true);
				 $('.disBtn1').prop('disabled',true);
				 $('#inlineRadio5').prop('disabled',true);
			 }
		}else if($('#xdays').attr('oldxDaysVal') || $('#ydays').attr('oldyDaysVal')){
			$('#inlineRadio5').prop('checked',true);
			$('.disBtn1').prop('disabled',false);
			$('.disBtn2').prop('disabled',true);
			$('.disBtn1').attr('required','required');
			$('.disBtn2').removeAttr('required');
		}else if($('#StartDate').attr('oldStartDateVal') || $('#EndDate').attr('oldEndDateVal')){
			$('#inlineRadio6').prop('checked',true);
			$('.disBtn2').prop('disabled',false);
			$('.disBtn1').prop('disabled',true);
			$('.disBtn2').attr('required','required');
			$('.disBtn1').removeAttr('required');
			 if(length < 3){
				 $('#inlineRadio5').prop('checked',false);
				 $('.disBtn1').prop('disabled',true);
				 $('.disBtn1').prop('disabled',true);
				 $('#inlineRadio5').prop('disabled',true);
			 }
		}
		var a = $("#inlineRadio3").val();
		if(a ==0){
		   $(".light-txt").removeClass("opacity06");
		}else{
		  $(".light-txt").addClass("opacity06");
		}
		resetValidation($(this).parents('form'));
		}
		
		$('#inlineRadio4').on('click',function(){
			if($('#inlineRadio4').prop('checked') == true){
			$('.disRadBtn1').prop('disabled',true);	
			$('.disRadBtn1').val('');	
			$('.disRadBtn1').prop('checked',false);
			$('.disBtn1').prop('disabled',true);	
			$('.disBtn1').val('');
			$('.disBtn1').removeAttr('required');
			$('.disBtn2').removeAttr('required');
			$('.disBtn1').selectpicker('refresh');
			//$('[data-id="anchorDateId"]').prop('disabled', true);
			resetValidation($('.resetDate'));
			}
			
			var a = $("#inlineRadio4").val();
			if(a ==1){
			   $(".light-txt").addClass("opacity06");
			}else{
			  $(".light-txt").removeClass("opacity06");
			}
		});
		
	</c:if>
	
	<c:if test="${actionOn eq 'view'}">
	 	$('#resourceForm input,textarea,select').prop('disabled', true);
    	$('.viewAct').hide();
	</c:if>
	
	$('.signDropDown').on('change',function(){
		chkDaysValid(false);
	});
	
	 $('.pdfClass').on('click',function(){
		$('#pdfDownloadFormId').submit();
		$("body").removeClass("loading");
 	});
	 
	 $('#anchorDateId').change(function(){ 
			var element = $(this).find('option:selected').text(); 
			if(element == 'Enrollment Date'){
				$('#xSign').children('option').remove();
				$('#xSign').append("<option value='0' selected>+</option>");
				$('#ySign').children('option').remove();
			    $('#ySign').append("<option value='0' selected>+</option>");
			} else{
				$('#xSign').children('option').remove();
				$('#xSign').append("<option value='0' selected>+</option><option value='1' selected>-</option>");
				$('#ySign').children('option').remove();
			    $('#ySign').append("<option value='0' selected>+</option><option value='1' selected>-</option>");
			}
			$('.selectpicker').selectpicker('refresh');
	}); 
});
function chkDaysValid(clickDone){
	var x = $("#xdays").val();
	var y = $("#ydays").val();
	var xSign = $('#xSign').val();
	var ySign = $('#ySign').val();
	if(xSign === '0'){
		x = "+"+x;
	}else if(xSign === '1'){
		x = "-"+x;
	}
	if(ySign === '0'){
		y = "+"+y;
	}else if(ySign === '1'){
		y = "-"+y;
	}
	var valid = true;
	if(y && x){
		if(parseInt(x) > parseInt(y)){
			if(clickDone && isFromValid($('#ydays').parents('form')))
				$('#ydays').focus();
			$('#ydays').parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>Y days should be greater than X days.</li></ul>');
			valid = false;
		}else{
			$('#ydays').parent().removeClass('has-error has-danger').find(".help-block").html("");
			resetValidation($('#ydays').parents('form'));
		}
	}
	return valid;
}
<c:if test="${isstudyProtocol ne 'isstudyProtocol'}">
function toJSDate( dateTime ) {
    var dateTime = dateTime.split(" ");
    var date = dateTime[0].split("/");
    return new Date(date[2], (date[0]-1), date[1]);
}
</c:if>
</script>
