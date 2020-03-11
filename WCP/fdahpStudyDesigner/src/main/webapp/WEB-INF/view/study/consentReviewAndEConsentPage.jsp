<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->
 <div class="col-sm-10 col-rc white-bg p-none">
	<!--  Start top tab section-->
	<form:form action="/fdahpStudyDesigner/adminStudies/studyList.do?_S=${param._S}" name="cancelConsentReviewFormId" id="cancelConsentReviewFormId" method="POST" role="form">
		<input type="hidden" id="studyId" name="studyId" value="${studyId}">
		<input type="hidden" id="consentId" name="consentId" value="${consentBo.id}">
	</form:form>
	<form:form action="/fdahpStudyDesigner/adminStudies/saveConsentReviewAndEConsentInfo.do?_S=${param._S}" name="consentReviewFormId" id="consentReviewFormId" method="post" role="form">
		<input type="hidden" id="studyId" name="studyId" value="${studyId}">
		<input type="hidden" id="consentId" name="consentId" value="${consentBo.id}">
		<input type="hidden" id="consentBo" name="consentBo" value="${consentBo}">
		<input type="hidden" id="typeOfCensent" name="typeOfCensent" value="${consentBo.consentDocType}">
		<!--  End body tab section -->
		<div>
            <!--  Start top tab section-->
            <div class="right-content-head" style="z-index:999;">
                <div class="text-right">
                    <div class="black-md-f text-uppercase dis-line pull-left line34">Review and E-Consent Steps <c:set var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>
                    <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn" onclick="goToBackPage(this);">Cancel</button>
                     </div>
                     <div class="dis-line form-group mb-none mr-sm">
                         <button type="button" class="btn btn-default gray-btn" id="saveId">Save</button>
                     </div>
                     <div class="dis-line form-group mb-none">
                        <button type="button" class="btn btn-primary blue-btn" id="doneId">Mark as Completed</button>
                     </div>
                 </div>
            </div>
            <!--  End  top tab section-->
            <!--  Start body tab section -->
            <div class="right-content-body pt-none pl-none">
             <ul class="nav nav-tabs review-tabs">
                <li class="shareData active"><a data-toggle="tab" href="#menu1" >Share Data Permissions</a></li>
                <li class="consentReview"><a data-toggle="tab" href="#menu2" >Consent Document for Review</a></li>
                <li class="econsentForm"><a data-toggle="tab" href="#menu3" >E-Consent Form	</a></li>               
              </ul>
              <div class="tab-content pl-xlg pr-xlg">
                <input type="hidden" id="version" name="version" value="${consentBo.version}">
                <div id="menu1" class="tab-pane fade in active">
                 <!--   <div class="mt-xlg text-weight-semibold" style="text-align: center;">This feature is work in progress and coming soon.</div> -->
                   <div class="mt-lg" >
                   		<div class="gray-xs-f mb-sm">Enable share data permissions step for this study? <span class="gray-xs-f mb-xs">(Please ensure you have the IRB mandate to allow this)</span></div>
                   		<div class="col-md-12 pl-none">
	                        <div class="form-group">
	                            <span class="radio radio-info radio-inline p-45">
	                                <input type="radio" id="shareDataPermissionsYes" value="Yes" name="shareDataPermissions" ${consentBo.shareDataPermissions eq 'Yes' ? 'checked' : ''}>
	                                <label for="shareDataPermissionsYes">Yes</label>
	                            </span>
	                            <span class="radio radio-inline">
	                                <input type="radio" id="shareDataPermissionsNo" value="No" name="shareDataPermissions" ${empty consentBo.shareDataPermissions || consentBo.shareDataPermissions eq 'No' ? 'checked' : ''}>
	                                <label for="shareDataPermissionsNo">No</label>
	                            </span>
	                        </div>
                    	</div>
                    	<div class="<c:if test="${consentBo.shareDataPermissions eq 'No'}">ct_panel</c:if>" id="rootContainer">
                    	<div class="col-md-12 p-none">
		                    <div class="gray-xs-f mb-xs">Title <small>(250 characters max)</small><span class="requiredStar">*</span></div>
		                    <div class="form-group">
		                        <input type="text" class="form-control requiredClass" placeholde="" id="titleId" name="title" value="${consentBo.title}" maxlength="250"/>
		                        <div class="help-block with-errors red-txt"></div>
		                    </div>
		                </div>
		                <div class="col-md-12 p-none">
		                    <div class="gray-xs-f mb-xs">1 line description or tagline <small>(250 characters max)</small><span class="requiredStar">*</span></div>
		                    <div class="form-group">
		                        <input type="text" class="form-control requiredClass" placeholder="" maxlength="250" name="taglineDescription" id="taglineDescriptionId" value="${consentBo.taglineDescription}" />
		                        <div class="help-block with-errors red-txt"></div>
		                    </div>
		                </div>
		                <div class="col-md-12 p-none">
		                    <div class="gray-xs-f mb-xs">Short Description for RO (Research Organization or Data Partner) <small>(250 characters max)</small><span class="requiredStar">*</span></div>
		                    <div class="form-group">
		                        <input type="text" class="form-control requiredClass" placeholder=""  maxlength="250" name="shortDescription" id="shortDescriptionId" value="${consentBo.shortDescription}"/>
		                        <div class="help-block with-errors red-txt"></div>
		                    </div>
		                </div>
		                <div class="col-md-12 p-none">
		                    <div class="gray-xs-f mb-xs">Long Description for RO (Research Organization or Data Partner) <small>(500 characters max)</small><span class="requiredStar">*</span></div>
		                    <div class="form-group">
		                        <textarea class="form-control requiredClass"  rows="5" maxlength="500"  placeholder="" name="longDescription" id="longDescriptionId">${consentBo.longDescription}</textarea>
		                        <div class="help-block with-errors red-txt"></div>
		                    </div>
		                </div>
		                <div class="col-md-12 p-none">
		                     <div class="gray-xs-f mb-xs">Learn More text (Elaborated content that explains how data will be shared)<span class="requiredStar">*</span></div>
		                     <div class="form-group">
		                        <textarea  id="learnMoreTextId" name="learnMoreText" required>${consentBo.learnMoreText}</textarea>
		                        <div class="help-block with-errors red-txt"></div>
		                     </div>
		                </div>
		                <%-- <div class="col-md-12 pl-none mt-lg">
		                	<div class="gray-xs-f mb-sm">Allow user to proceed if permission not provided</div>
	                        <div class="form-group">
	                            <span class="radio radio-info radio-inline p-45">
	                                <input type="radio" id="allowWithoutPermissionYes"  name="allowWithoutPermission" ${empty consentBo.allowWithoutPermission || consentBo.allowWithoutPermission eq 'Yes' ? 'checked' : ''} value="Yes">
	                                <label for="allowWithoutPermissionYes">Yes</label>
	                            </span>
	                            <span class="radio radio-inline">
	                                <input type="radio" id="allowWithoutPermissionNo"  name="allowWithoutPermission" ${consentBo.allowWithoutPermission eq 'No' ? 'checked' : ''} value="No">
	                                <label for="allowWithoutPermissionNo">No</label>
	                            </span>
	                        </div>
                    	</div> --%>
                    	 <div class="col-md-12 pl-none mt-lg mb-xlg">
                    	<!--  <div class="mt-lg"> -->
<!-- 		                   	<a  class="preview__text" href="javascript:void()" data-toggle="modal" data-target="#myModal"> -->
		                   	<a  class="preview__text" href="javascript:void()" data-toggle="modal" onclick="previewDataSharing();">
							   <img class="mr-xs" src="../images/icons/eye-icn.png"> <span>Preview</span>
							</a>
						 <!-- </div> -->
                    	 	<!-- <span data-toggle="modal" data-target="#myModal" class="eye__preview"><span class="sprites_icon preview-g mr-sm" data-toggle="tooltip" data-placement="top" title="" data-original-title="View"></span><span class="gray-xs-f">Preview</span></span> -->
                    	 </div>
                    	 </div>
                    	 <!-- <div class="container">
							  <div class="modal fade" id="myModal" role="dialog">
							    <div class="modal-dialog" id="i__phone">
							    	<button type="button" class="close" data-dismiss="modal">&times;</button>
							    	<div class="i__phone__bg">
							    		<div class="i__phone__content">
							    			<div class=" ">
							    				<div class="black-md-f text-uppercase dis-line line34 mb-sm mt-xlg">Title</div>
							    				<div class="gray-xs-f mb-sm">Tagline Description</div>
							    				<div classs=""mb-xlg><a href="#">Learn More</a></div>
							    			</div>
							    			<div>
							    				<ul>
							    					<li>List</li>
							    					<li>List</li>
							    					<li>List</li>
							    				</ul>
							    			</div>
							    		</div>
							    	</div>
							    </div>
							  </div>
							  
							</div> -->
                   </div>
                   <div class="clearfix"></div>
					
					
					
                </div>
	                <div id="menu2" class="tab-pane fade">
	                    <div class="mt-lg">
	                    <div class="gray-xs-f mb-sm">Select a method of creation for the Consent Document </div>
		                	<div class="form-group mb-none">
			                	<div id="consentDocTypeDivId">
			                         <span class="radio radio-info radio-inline p-45">
			                            <input type="radio" id="inlineRadio1" value="Auto" name="consentDocType" required data-error="Please choose consent document type" ${consentBo.consentDocType=='Auto'?'checked':''}>
			                            <label for="inlineRadio1">Use auto-created Consent Document</label>
			                        </span>
			                        <span class="radio radio-inline">
			                            <input type="radio" id="inlineRadio2" value="New" name="consentDocType" required data-error="Please choose consent document type" ${consentBo.consentDocType=='New'?'checked':''}>
			                            <label for="inlineRadio2">Create New Consent Document</label>
			                        </span>
			                        <div class="help-block with-errors red-txt"></div>
			                    </div>
		                    </div>
	                    </div>
	                    <div class="italic-txt">
	                        <div id="autoCreateHelpTextDiv" style="display:block;">
	                        	<small class="inst">This is a preview of the Consent Document to depict how it gets created by the ResearchKit / ResearchStack frameworks on the mobile app. Consent Items (title and long description portions) are concatenated to automatically create the Consent Document. The mobile app also generates a Consent Document PDF with participant first name, last name, signature and date, time of providing consent, as captured on the app.</small>
	                        </div>
	                         <div id="newDocumentHelpTextDiv" style="display:none;">
	                        	<small class="inst">Choose this option if you wish to provide separate content for the Consent Document instead of using the auto-generated Consent Document. Note that in this case, the mobile app will not be able to add user-specific details such as first name, last name, signature and date/time of providing consent, to the PDF that it generates for the Consent Document.</small>
	                        </div>
	                    </div>
	                   <div class="mt-xlg">
	                        <div class="blue-lg-f text-uppercase">CONSENT DOCUMENT <span id="requiredStarId" class="requiredStar">*</span></div>
							<div class="mt-lg">
	                        <div class="cont_doc" id="autoCreateDivId" style="display:block;">
	                           <div style="height:900px;">
									<div id="autoConsentDocumentDivId"></div>
							   </div>
	                        </div>
	                        <div class="cont_editor">
			                    <div id="newDivId" style="display:none;">
									<div class="form-group ">
							            <textarea class="" rows="8" id="newDocumentDivId" name="newDocumentDivId" >${consentBo.consentDocContent}</textarea>
							            <div class="help-block with-errors red-txt"></div>
							         </div>
								</div>
	                        </div>
	                    </div>
	                    </div>
	                    <div class="mt-xlg dis-inline" id="autoCreateDivId01" style="display:block;">
	                        <div class="sign">Participant's First Name</div>
	                        <div class="sign">Last Name</div>
	                        <div class="sign">Signature</div>
	                        <div class="sign">Date</div>
	                        <div class="sign">Time</div>
	                    </div>
	                </div>
                <div id="menu3" class="tab-pane fade">
                    <div class="mt-xlg text-weight-semibold">The mobile app captures the following from the user as  part of Consent to the study : </div>
                    <div class="mt-lg">
	                    <ul class="list-style-image">
	                    	<li>Agreement to the content in the Consent Document <small>(250 characters max)</small><span class="requiredStar">*</span><span class="ml-xs sprites_v3 filled-tooltip" data-toggle="tooltip" title="Text message shown to the prospect participant on the app, to confirm Review of and Agreement to the Consent Document."></span>
			                    <div class="form-group mt-sm mb-none">
			                        <input type="text" class="form-control" placeholder="" required  name="aggrementOfTheConsent" id="aggrementOfTheConsentId" value="${consentBo.aggrementOfTheConsent}" maxlength="250"/>
			                        <div class="help-block with-errors red-txt"></div>
			                    </div>
				            </li>
	                    	
	                    	<li>First Name</li>
	                    	<li>Last Name</li>
	                    	<li>E-signature</li>
	                    	<li>Date and Time of providing Consent</li>
	                    </ul>
                        <%-- <div class="mt-lg form-group">
                            <span class="checkbox checkbox-inline">
                                <input type="checkbox" id="agreementCB" value="No" name="eConsentAgree" ${consentBo.eConsentAgree=='Yes'?'checked':''}>
                                <label for="agreementCB"> Agreement to the content in the Consent Document</label>
                            </span>
                        </div>
                        <div class="mt-md form-group">
                            <span class="checkbox checkbox-inline">
                                <input type="checkbox" id="fNameCB" value="Yes" name="eConsentFirstName" checked disabled>
                                <label for="fNameCB"> First Name</label>
                            </span> 
                             <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mt-md form-group">
                            <span class="checkbox checkbox-inline">
                                <input type="checkbox" id="lNameCB" value="Yes" name="eConsentLastName" checked disabled>
                                <label for="lNameCB"> Last Name</label>
                            </span> 
                             <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mt-md form-group">
                            <span class="checkbox checkbox-inline">
                                <input type="checkbox" id="eSignCB" value="Yes" name="eConsentSignature" checked disabled>
                                <label for="eSignCB"> E-signature</label>
                            </span> 
                             <div class="help-block with-errors red-txt"></div>
                        </div>
                        <div class="mt-md form-group">
                            <span class="checkbox checkbox-inline">
                                <input type="checkbox" id="dateTimeCB" value="Yes" name="eConsentDatetime" checked disabled>
                                <label for="dateTimeCB"> Date and Time of providing Consent</label>
                            </span> 
                             <div class="help-block with-errors red-txt"></div> --%>
                        </div>
                    </div>
                </div>              
              </div>
            </div>
            <!--  End body tab section -->
       
        <!-- End right Content here -->
	</form:form>
	              <div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
					  <div class="modal-dialog modal-sm">
					    <div class="">              
					     	<!-- <button style="position: absolute;left: 0;right: 0;margin: auto;top: -10px;" type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button> -->
					         <div class="pp__img__container">
								  <img src="../images/preview_phone.png" alt="Norway" style="width:100%;">
								  <div class="pp__top">
								  	<div id="cancelButtonId" class="pl-lg pr-lg" style="display:none;"><button class="float__right cancel__close mb-sm"  data-dismiss="modal">Cancel</button></div>
								  	<div id="doneButtonId" class="pl-lg pr-lg" style="display:none;"><button class="float__right cancel__close"  onclick="previewDataSharing();">Done</button></div>
								  	 <div class="clearfix"></div>
								  	<div class="pl-md pr-md">
								  		<div id="wrapper__">  
										   <div class="scrollbar__" id="style-2">
										    <div class="force-overflow__">
												   <!--1st modal Start -->
												   <div class="pp__title" id="titleModalId">- NA -</div>
												    <div class="pp__tagline" id="tagLineDescriptionModalId">- NA -</div>
												    <div class="pp__learnmore"><a href="javascript:void(0)" data-toggle="modal" onclick="previewLearnMore();">Learn more</a>
												    </div>
												    <div class="pp__ul mt-xlg">
												    	<div class="panel-group overview-panel" id="accordion">
												    		<div class="panel panel-default">
																<div class="panel-heading">
																	<div class="panel-title">
																		<a data-toggle="collapse" data-parent="#accordion"
																			href="#collapse1" aria-expanded="true">
																			<div class="text-left dis-inline pull-left">
																				<span class="ellipsis__">dis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsisdis-ellipsis dis-ellipsis dis-ellipsis dis-ellipsis</span>
																			</div>
																			<div class="text-right dis-inline pull-right">
																				<span class="glyphicon glyphicon-chevron-right"></span>
																			</div>
																			<div class="clearfix"></div>
																		</a>
																	</div>
																</div>
																<div id="collapse1" class="panel-collapse collapse">
																	<div class="panel-body">
																		kfjdf;ljhdlfhjd;lhjb
																		dskfdsjfnhslkdnghlkdsfglkd bfdskjfbkjd
																	</div>
																</div>
															</div>
												    	</div>
												    	<div class="panel-group overview-panel" id="accordion1">
												    		<div class="panel panel-default">
																<div class="panel-heading">
																	<div class="panel-title">
																		<a data-toggle="collapse" data-parent="#accordion1"
																			href="#collapse2" aria-expanded="true">
																			<div class="text-left dis-inline pull-left">
																				<span class="ellipsis__">ronalin sahoo ejrerhewuirew ronalinefewf</span>
																			</div>
																			<div class="text-right dis-inline pull-right">
																				<span class="glyphicon glyphicon-chevron-right"></span>
																			</div>
																			<div class="clearfix"></div>
																		</a>
																	</div>
																</div>
																<div id="collapse2" class="panel-collapse collapse">
																	<div class="panel-body">
																		kfjdf;ljhdlfhjd;lhjb
																		dskfdsjfnhslkdnghlkdsfglkd bfdskjfbkjd
																	</div>
																</div>
															</div>
												    	</div>
													</div>	
													<!-- 1st Modal  End-->
											  </div>
											</div>
										</div>
								  	</div>
								  </div>
							</div>
					    </div>
					  </div>
					</div>
</div>
<!-- End right Content here -->
<script type="text/javascript">
$(document).ready(function(){  
	//check the type of page action(view/edit)
	if('${permission}' == 'view'){
		$('input[name="consentDocType"]').attr('disabled', 'disabled');
		$('#consentReviewFormId input').prop('disabled', true);
	    $('#longDescriptionId').prop('disabled', true);
		$('#newDivId .elaborateClass').addClass('linkDis');
	    $('#saveId,#doneId').hide();
	}
	
	//auto select if consent Id is empty
	//var consentId = "${consentBo.id}";
	
	var consentId = "${consentBo.consentDocType}";
	console.log(consentId);
	if( consentId == null || consentId == '' || typeof consentId === 'undefined'){
		if( null != "${consentInfoList}" && "${consentInfoList}" != '' && "${consentInfoList}" !== undefined){
			$("#inlineRadio1").attr('checked', true);
			$("#version").val('1.0');
		}else{
			$("#inlineRadio2").attr('checked', true);
		}
	}
	
	//active li
    $(".menuNav li").removeClass('active');
    $(".fifthConsentReview").addClass('active');
	$("#createStudyId").show();
	consentDocumentDivType();
	//check the consent type
	$("#consentDocTypeDivId").on('change', function(){
		consentDocumentDivType();
    });
	var shareDataPermissions = '${consentBo.shareDataPermissions}';
	resetValues(shareDataPermissions);
	$('input[name="shareDataPermissions"]').change(function(){
		var shareDataPermissions = '${consentBo.shareDataPermissions}';
		var value= $(this).val();
		console.log("value:"+value);
		if(value == 'Yes'){
			$('#rootContainer input').attr('required',true);
			$('#learnMoreTextId').attr('required',true);
			$('.requiredClass').attr('required',true);
			$('#longDescriptionId').attr('required',true);
			newLearnMoreConsentDocument();
			$("#rootContainer").show();
		}else{
			$('#rootContainer input').attr('required',false);
			$('#longDescriptionId').attr('required',false);
			$('.requiredClass').attr('required',false);
			$('#learnMoreTextId').attr('required',false);
			newLearnMoreConsentDocument();
			$("#rootContainer").hide();
		}
	});
	/* var isChek = "${consentBo.consentDocType}";
	if(isChek != null && isChek !='' && typeof isChek !=undefined){
		if(isChek == 'New'){
			$("#newDivId").show();
			$("#autoCreateDivId").hide();
			$("#autoCreateDivId01").hide();
			$("#inlineRadio2").prop("checked", true);
			$("#typeOfCensent").val("New");
			createNewConsentDocument();
		}else{
			$("#autoCreateDivId").show();
			$("#autoCreateDivId01").show();
	        $("#newDivId").hide();
	        $("#inlineRadio1").prop("checked", true);
	        $("#typeOfCensent").val("Auto");
	        autoCreateConsentDocument();
		}
	} */
	//go back to consentList page
	$("#saveId,#doneId").on('click', function(){
		var id = this.id;
		var valid= true;
		if($("#typeOfCensent").val() == "New"){
			valid =  maxLenValEditor();
		}
		tinyMCE.triggerSave();
		if(valid){
			if( id == "saveId"){
				$("#consentReviewFormId").parents("form").validator("destroy");
			    $("#consentReviewFormId").parents("form").validator();
				saveConsentReviewAndEConsentInfo("saveId");				
			}else if(id == "doneId"){
				var retainTxt = '${studyBo.retainParticipant}';
				if(isFromValid("#consentReviewFormId") && maxLenLearnMoreEditor()){
					var message = "";
					var alertType = "";
					if(retainTxt != null && retainTxt != '' && typeof retainTxt != 'undefined'){
						if(retainTxt == 'Yes'){
							alertType = "retained";
						}else if(retainTxt == 'No'){
							alertType = "deleted";
						}else{
							alertType = "retained or deleted as per participant choice";
						}
						message = "You have a setting that needs study data to be "+alertType+" if the participant withdraws from the study. Please ensure you have worded Consent Terms in accordance with this. Click OK to proceed with completing this section or Cancel if you wish to make changes.";
					}
					console.log(message);
					bootbox.confirm({
						closeButton: false,
						message : message,
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
					        	var consentDocumentType = $('input[name="consentDocType"]:checked').val();
						    	if(consentDocumentType == "Auto"){
						    		saveConsentReviewAndEConsentInfo("doneId");
						    	}else{
						    		var content = tinymce.get('newDocumentDivId').getContent();
						    		if(content != null && content !='' && typeof content != 'undefined'){
						    			saveConsentReviewAndEConsentInfo("doneId");
						    		}else{
						    			$("#newDocumentDivId").parent().find(".help-block").empty();
							    		$("#newDocumentDivId").parent().find(".help-block").append('<ul class="list-unstyled"><li>Please fill out this field.</li></ul>');
						    		}
						    	}
					        }else{
					        	$("#doneId").prop('disabled', false);
					        }
					    }
		    		});
				}else{
					console.log("else....");
			   		var slaCount = $('#menu1').find('.has-error.has-danger').length;
					var qlaCount = $('#menu2').find('.has-error.has-danger').length;
					var rlaCount = $('#menu3').find('.has-error.has-danger').length;
					console.log("slaCount:"+slaCount);
					console.log("qlaCount:"+qlaCount);
					console.log("rlaCount:"+rlaCount);
					if(parseInt(slaCount) >= 1){
						 $('.shareData a').tab('show');
					}else if(parseInt(qlaCount) >= 1){
						 $('.consentReview a').tab('show');
					}else if(parseInt(rlaCount) >= 1){
						 $('.econsentForm a').tab('show');
					}
				}
			}	
		}
	});
	function resetValues(shareDataPermissions){
		console.log("shareDataPermissions:"+shareDataPermissions);
		if(shareDataPermissions == '' || shareDataPermissions == 'No'){
			console.log("ifff");
			$('#rootContainer input').val('');
			$('#allowWithoutPermissionYes').val("Yes");
			$('#allowWithoutPermissionNo').val("No");
			$('#allowWithoutPermissionYes').prop("checked",true);
			$('#learnMoreTextId').val('');
			$('#longDescriptionId').val('');
			$('#learnMoreTextId').attr('required',false);
			$('.requiredClass').attr('required',false);
			newLearnMoreConsentDocument();
			$("#rootContainer").hide();
			//tinymce.get('learnMoreTextId').setContent('');
		}else{
			$('.requiredClass').attr('required',true);
			$('#learnMoreTextId').attr('required',true);
			newLearnMoreConsentDocument();
		} 
	}
	//consent doc type div
	function consentDocumentDivType(){
		//fancyToolbar();
		
		if($("#inlineRadio1").is(":checked")){
			console.log("consentDocumentDivType if");
    		$("#autoCreateDivId").show();
    		$("#autoCreateDivId01").show();
    		$('#newDocumentDivId').attr("required",false);
	        $("#newDivId").hide();
	        $("#typeOfCensent").val("Auto");
	        $("#autoCreateHelpTextDiv").show();
	        $("#newDocumentHelpTextDiv").hide();
	        $('#requiredStarId').hide();
	        autoCreateConsentDocument();
    	}else{
    		console.log("consentDocumentDivType else");
    		$("#newDivId").show();
    		$("#autoCreateDivId").hide();
    		$("#autoCreateDivId01").hide();
    		$("#typeOfCensent").val("New");
    		$("#autoCreateHelpTextDiv").hide();
	        $("#newDocumentHelpTextDiv").show();
	        $('#requiredStarId').show();
	        $('#newDocumentDivId').attr("required",true);
    		createNewConsentDocument();
    	}
	}
	
	// Fancy Scroll Bar
    /* function fancyToolbar(){
    	$(".left-content").niceScroll({cursorcolor:"#95a2ab",cursorborder:"1px solid #95a2ab"});
        $(".right-content-body").niceScroll({cursorcolor:"#d5dee3",cursorborder:"1px solid #d5dee3"});
	} */
    //check the consentinfo list
    function autoCreateConsentDocument(){
    	var consentDocumentDivContent = "";
        $("#autoConsentDocumentDivId").empty();
        if( null != "${consentInfoList}" && "${consentInfoList}" != '' && "${consentInfoList}" !== undefined){
        	if($("#inlineRadio1").is(":checked")){
        		<c:forEach items="${consentInfoList}" varStatus="i" var="consentInfo">
	        		consentDocumentDivContent += "<span style='font-size:18px;'><strong>"
						+"${consentInfo.displayTitle}"
						+"</strong></span><br/>"
						+"<span style='display: block; overflow-wrap: break-word; width: 100%;'>"
						+"${consentInfo.elaborated}"
						+"</span><br/>";
            	</c:forEach>
            	
        	}
        }
        $("#autoConsentDocumentDivId").append(consentDocumentDivContent);
        $("#newDocumentDivId").val('');
        //apply custom scroll bar to the auto consent document type
       // $("#autoCreateDivId").niceScroll({cursorcolor:"#d5dee3",cursorborder:"1px solid #d5dee3"});
    }
    //createNewConsentDocument
    function createNewConsentDocument(){
    	tinymce.init({
             selector: "#newDocumentDivId",
             theme: "modern",
             skin: "lightgray",
             height:180,
             plugins: [
                 "advlist autolink link image lists charmap hr anchor pagebreak spellchecker",
                 "save contextmenu directionality paste"
             ],
             toolbar: "anchor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | underline link | hr removeformat | cut undo redo | fontsizeselect fontselect",
             menubar: false,
             toolbar_items_size: 'small',
             content_style: "div, p { font-size: 13px;letter-spacing: 1px;}",
             entity_encoding : "raw",
             setup : function(ed) {
                 ed.on('change', function(ed) {
               		  if(tinyMCE.get(ed.target.id).getContent() != ''){
               			$('#newDocumentDivId').parent().removeClass("has-danger").removeClass("has-error");
               	        $('#newDocumentDivId').parent().find(".help-block").html("");
               		  }
                 });
        	  	},
             <c:if test="${permission eq 'view'}">readonly:1</c:if>
         });
    	
    	/* tinymce.get('newDocumentDivId').setContent('');
    	tinymce.get('newDocumentDivId').setContent('${consentBo.consentDocContent}'); */ 
    }
    function newLearnMoreConsentDocument(){
    	tinymce.init({
            selector: "#learnMoreTextId",
            theme: "modern",
            skin: "lightgray",
            height:180,
            plugins: [
                "advlist autolink link image lists charmap hr anchor pagebreak spellchecker",
                "save contextmenu directionality paste"
            ],
            toolbar: "anchor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | underline link | hr removeformat | cut undo redo | fontsizeselect fontselect",
            menubar: false,
            toolbar_items_size: 'small',
            content_style: "div, p { font-size: 13px;letter-spacing: 1px;}",
            entity_encoding : "raw",
            setup : function(ed) {
                ed.on('change', function(ed) {
              		  if(tinyMCE.get(ed.target.id).getContent() != ''){
              			  console.log(tinyMCE.get(ed.target.id).getContent());
              			$('#learnMoreTextId').parent().removeClass("has-danger").removeClass("has-error");
              	        $('#learnMoreTextId').parent().find(".help-block").html("");
              		  }
                });
       	  	},
            <c:if test="${permission eq 'view'}">readonly:1</c:if>
        });
    }
    //save review and E-consent data
    function saveConsentReviewAndEConsentInfo(item){
	   //	if(item == "doneId"){
			//$("#consentValidatorDiv").validator('validate');
	   	 	//var customErrorLength = $("#consentValidatorDiv").find(".has-danger").length;
	   	 
		   	/* if(customErrorLength == 1){
		   		resetValidation($("#consentValidatorDiv"));
		   	} */
	   		/* if(isFromValid("#consentReviewFormId")){
		   		customErrorLength = 0;
			}else{
		   		customErrorLength = 1;
		   	} */
	   /* 	}else{
	   		customErrorLength = 0;
	   	} */
	   	
		//console.log("customErrorLength:"+customErrorLength);
   	 	//if(customErrorLength == 0){
	   		var consentInfo = new Object();
	    	var consentId = $("#consentId").val();
	    	var studyId = $("#studyId").val();
	    	var agreementCB = $("#agreementCB").val();
	    	var fNameCB = $("#fNameCB").val();
	    	var lNameCB = $("#lNameCB").val();
	    	var eSignCB = $("#eSignCB").val();
	    	var dateTimeCB = $("#dateTimeCB").val();
	    	var consentDocumentContent = "";
	    	var consentDocType = $('input[name="consentDocType"]:checked').val();
	    	
	    	var shareDataPermissionsTxt = $('input[name="shareDataPermissions"]:checked').val(); 
	    	var title_txt = $("#titleId").val();
	    	var tagline_description = $("#taglineDescriptionId").val();
	    	var short_description = $("#shortDescriptionId").val();
	    	var long_description = $("#longDescriptionId").val();
	    	var learn_more_text = tinymce.get('learnMoreTextId').getContent({ format: 'raw' });
	    	learn_more_text = replaceSpecialCharacters(learn_more_text);
	    	var allow_Permission = $('input[name="allowWithoutPermission"]:checked').val();	
	    	var aggrement_of_theconsent = $("#aggrementOfTheConsentId").val();	
	    		
	    		
	    	if(consentDocType == "New"){
	    		consentDocumentContent = tinymce.get('newDocumentDivId').getContent({ format: 'raw' });
	    		consentDocumentContent = replaceSpecialCharacters(consentDocumentContent);
	    	}
	    	
	    	if(item == "doneId"){
	    		consentInfo.type="completed";
	    	}else{
	    		consentInfo.type="save";
	    	}
	    	if(null != consentId){consentInfo.id = consentId;}
	    	if(null != studyId){consentInfo.studyId = studyId;}
	    	if(null != consentDocType){consentInfo.consentDocType = consentDocType;}
	    	if(null != consentDocumentContent){consentInfo.consentDocContent = consentDocumentContent;}
	    	if(null != agreementCB){consentInfo.eConsentAgree = agreementCB;} 
	    	if(null != fNameCB){consentInfo.eConsentFirstName = fNameCB;}
	    	if(null != lNameCB){consentInfo.eConsentLastName = lNameCB;}
	    	if(null != eSignCB){consentInfo.eConsentSignature = eSignCB;}
	    	if(null != dateTimeCB){consentInfo.eConsentDatetime = dateTimeCB;}
	    	
	    	if(null != shareDataPermissionsTxt){consentInfo.shareDataPermissions = shareDataPermissionsTxt;}
	    	if(null != title_txt){consentInfo.title = title_txt;}
	    	if(null != tagline_description){consentInfo.taglineDescription = tagline_description;}
	    	if(null != short_description){consentInfo.shortDescription = short_description;}
	    	if(null != long_description){consentInfo.longDescription = long_description;}
	    	if(null != learn_more_text){consentInfo.learnMoreText = learn_more_text;}
	    	if(null != allow_Permission){consentInfo.allowWithoutPermission = allow_Permission;}
	    	if(null != aggrement_of_theconsent){consentInfo.aggrementOfTheConsent = aggrement_of_theconsent;}
	    	var data = JSON.stringify(consentInfo);
	    	$.ajax({ 
		          url: "/fdahpStudyDesigner/adminStudies/saveConsentReviewAndEConsentInfo.do?_S=${param._S}",
		          type: "POST",
		          datatype: "json",
		          data: {consentInfo:data},
		          beforeSend: function(xhr, settings){
		              xhr.setRequestHeader("X-CSRF-TOKEN", "${_csrf.token}");
		          },
		          success:function(data){
		        	var jsonobj = eval(data);                 
					var message = jsonobj.message;
					$("#alertMsg").html('');
					if(message == "SUCCESS"){
						var consentId = jsonobj.consentId;
						var studyId = jsonobj.studyId;
						$("#consentId").val(consentId);
						$("#studyId").val(studyId);
						var consentDocumentType = $('input[name="consentDocType"]:checked').val();
						$("#newDocumentDivId").val('');
						if(consentDocumentType == "New"){
					    	$("#newDocumentDivId").val(consentDocumentContent);
					    	tinymce.get('newDocumentDivId').setContent('');
					    	tinymce.get('newDocumentDivId').setContent(consentDocumentContent);
						}
						if(item == "doneId"){
							var a = document.createElement('a');
							a.href = "/fdahpStudyDesigner/adminStudies/consentReviewMarkAsCompleted.do?_S=${param._S}";
							document.body.appendChild(a).click();
						}else{
							$("#alertMsg").removeClass('e-box').addClass('s-box').html("Content saved as draft.");
							$(item).prop('disabled', false);
							$('#alertMsg').show();
							if ($('.fifthConsentReview').find('span').hasClass('sprites-icons-2 tick pull-right mt-xs')) {
							    $('.fifthConsentReview').find('span').removeClass('sprites-icons-2 tick pull-right mt-xs');
							}
						}
					}else{
						$("#alertMsg").removeClass('s-box').addClass('e-box').html("Something went Wrong");
						$('#alertMsg').show();
					}
					setTimeout(hideDisplayMessage, 4000);
		          },
		          global : false
		   });
	   	 /* }else{
	   		console.log("else....");
	   		var slaCount = $('#menu1').find('.has-error.has-danger').length;
			var qlaCount = $('#menu2').find('.has-error.has-danger').length;
			var rlaCount = $('#menu3').find('.has-error.has-danger').length;
			console.log("slaCount:"+slaCount);
			console.log("qlaCount:"+qlaCount);
			console.log("rlaCount:"+rlaCount);
			if(parseInt(slaCount) >= 1){
				 $('.shareData a').tab('show');
			}else if(parseInt(qlaCount) >= 1){
				 $('.consentReview a').tab('show');
			}else if(parseInt(rlaCount) >= 1){
				 $('.econsentForm a').tab('show');
			}
	   	 } */
    }
});

function goToBackPage(item){
	//window.history.back();
	<c:if test="${permission ne 'view'}">
	$(item).prop('disabled', true);
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
		        	var a = document.createElement('a');
		        	a.href = "/fdahpStudyDesigner/adminStudies/consentListPage.do?_S=${param._S}";
		        	document.body.appendChild(a).click();
		        }else{
		        	$(item).prop('disabled', false);
		        }
		    }
	});
	</c:if>
	<c:if test="${permission eq 'view'}">
   	var a = document.createElement('a');
	a.href = "/fdahpStudyDesigner/adminStudies/consentListPage.do?_S=${param._S}";
	document.body.appendChild(a).click();
  </c:if>
}
function maxLenValEditor() {
	var isValid = true; 
	var value = tinymce.get('newDocumentDivId').getContent({ format: 'raw' });
	console.log("length:"+$.trim(value.replace(/(<([^>]+)>)/ig, "")).length);
	if(value != '' && $.trim(value.replace(/(<([^>]+)>)/ig, "")).length > 70000){
		if(isValid){
			isValid = false;
		}
		$('#newDocumentDivId').parent().addClass('has-error-cust').find(".help-block").empty().append('<ul class="list-unstyled"><li>Maximum 70000 characters are allowed.</li></ul>');
	} else {
		 $('#newDocumentDivId').parent().removeClass("has-danger").removeClass("has-error");
	     $('#newDocumentDivId').parent().find(".help-block").html(""); 
	}
	return isValid;
}
function maxLenLearnMoreEditor() {
	var isValid = true; 
	var value = tinymce.get('learnMoreTextId').getContent({ format: 'raw' });
	if(value != '' && $.trim(value.replace(/(<([^>]+)>)/ig, "")).length > 70000){
		if(isValid){
			isValid = false;
		}
		$('#learnMoreTextId').parent().addClass('has-error-cust').find(".help-block").empty().append('<ul class="list-unstyled"><li>Maximum 70000 characters are allowed.</li></ul>');
	} else {
		 $('#learnMoreTextId').parent().removeClass("has-danger").removeClass("has-error");
	     $('#learnMoreTextId').parent().find(".help-block").html(""); 
	}
	return isValid;
}
function previewDataSharing(){
	var titleText = $("#titleId").val();
	var tagline_description = $("#taglineDescriptionId").val();
	var short_description = $("#shortDescriptionId").val();
	var long_descriptionId = $("#longDescriptionId").val();
	$('.force-overflow__').html('');
	var data = '<div class="pp__title" id="titleModalId">';
	if(titleText != '' && titleText!= null && typeof titleText != 'undefined'){
		data += titleText + '</div>';
	}else{
		data += ' -NA-</div>';
	}
	data += '<div class="pp__tagline" id="tagLineDescriptionModalId">';
	if(tagline_description != '' && tagline_description!= null && typeof tagline_description != 'undefined'){
		data += tagline_description + '</div>';
	}else{
		data += ' -NA-</div>';		
	}
	
	data += '<div class="pp__learnMore"><a href="javascript:void(0)" data-toggle="modal" onclick="previewLearnMore();">Learn more</a>'
            +'</div>'
            +'<div class="pp__ul mt-xlg">';
	if(short_description != '' && short_description!= null && typeof short_description != 'undefined'){
		data += '<div class="panel-group overview-panel" id="accordion">'
		        + '<div class="panel panel-default">'
		        + '<div class="panel-heading">'
		        + '<div class="panel-title" style="font-weight: bold;">'
		        + '<a data-toggle="collapse" data-parent="#accordion" href="#collapse1" aria-expanded="true">'
		        + '<div class="text-left dis-inline pull-left">'
		        + '<span class="ellipsis__">'+'Share my data with '+short_description+' and qualified researchers worldwide'+'</span>'
		        + '</div>'
		        + '<div class="text-right dis-inline pull-right"><span class="glyphicon glyphicon-chevron-right"></span>'
				+ '</div><div class="clearfix"></div></a></div></div>'
				+ '<div id="collapse1" class="panel-collapse collapse"><div class="panel-body">'
		        + 'Share my data with '+short_description+' and qualified researchers worldwide' + '</div></div></div></div>';
				
	}else{
		data += '<ul class=""><li id="shortDescriptionModalId" style="font-weight: bold;"> - NA - </li></ul>';
	}
	
	if(long_descriptionId != '' && long_descriptionId!= null && typeof long_descriptionId != 'undefined'){
		data += '<div class="panel-group overview-panel" id="accordion1">'
	        + '<div class="panel panel-default">'
	        + '<div class="panel-heading">'
	        + '<div class="panel-title" style="font-weight: bold;">'
	        + '<a data-toggle="collapse" data-parent="#accordion1" href="#collapse2" aria-expanded="true">'
	        + '<div class="text-left dis-inline pull-left">'
	        + '<span class="ellipsis__">'+'Only share my data with '+long_descriptionId+'</span>'
	        + '</div>'
	        + '<div class="text-right dis-inline pull-right"><span class="glyphicon glyphicon-chevron-right"></span>'
			+ '</div><div class="clearfix"></div></a></div></div>'
			+ '<div id="collapse2" class="panel-collapse collapse"><div class="panel-body">'
	        + 'Only share my data with '+long_descriptionId+'</div></div></div></div>';
	}else{
		data += '<ul class=""><li id="longDescriptionModalId" style="font-weight: bold;"> - NA - </li></ul>';
	}
	data += '</div>' ;

	$('.force-overflow__').html(data);
	$('.scrollbar__').scrollTop(0);
	colapseUpAndDown();
	$('#cancelButtonId').show();
	$('#doneButtonId').hide();
	$("#myModal").modal('show');
}
function previewLearnMore(){
	$('#cancelButtonId').hide();
	$('#doneButtonId').show();
	$('.force-overflow__').html('');
    var  learn_more_desc = tinymce.get('learnMoreTextId').getContent({format : 'text'}).trim();
    var data = '<div class="pp__title">Learn more</div>'
	           +'<div class="pp__ul mt-xlg">';
	if(learn_more_desc != ' ' && learn_more_desc != '' && learn_more_desc!= null && typeof learn_more_desc != 'undefined'){
		data += '<div class="panel-group overview-panel" id="accordion1">'
	        + '<div class="panel panel-default">'
	        + '<div class="panel-heading">'
	        + '<div class="panel-title" style="font-weight: bold;">'
	        + '<a data-toggle="collapse" data-parent="#accordion1" href="#collapse2" aria-expanded="true">'
	        + '<div class="text-left dis-inline pull-left">'
	        + '<span class="ellipsis__">'+learn_more_desc+'</span>'
	        + '</div>'
	        + '<div class="text-right dis-inline pull-right"><span class="glyphicon glyphicon-chevron-right"></span>'
			+ '</div><div class="clearfix"></div></a></div></div>'
			+ '<div id="collapse2" class="panel-collapse collapse"><div class="panel-body">'
	        + learn_more_desc+'</div></div></div></div>';
	}else{
		data += '<ul class=""><li id="learnMoreDescId" style="font-weight: bold;"> - NA - </li></ul>';
	}
	data += '</div>' ;
	$('.force-overflow__').html(data);
	$('.scrollbar__').scrollTop(0);
	colapseUpAndDown();
  }


$(document).on('show.bs.collapse','.collapse', function(){
    $('.collapse').not(this).collapse('hide').removeClass('in');
});

function colapseUpAndDown(){
	$('.collapse').on('shown.bs.collapse', function(){ 
	    console.log("shown");   
	    $(this).parent().find(".glyphicon-chevron-right").removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
	   }).on('hidden.bs.collapse', function(){
	    console.log("hidden");
	    $(this).parent().find(".glyphicon-chevron-down").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
	  });
}
</script>