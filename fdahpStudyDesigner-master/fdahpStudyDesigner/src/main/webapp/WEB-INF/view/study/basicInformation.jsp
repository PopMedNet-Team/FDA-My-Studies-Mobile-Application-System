<!-- 
  Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
  Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
  associated documentation files (the "Software"), to deal in the Software without restriction, including
  without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
  of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
  following conditions:
 
  The above copyright notice and this permission notice shall be included in all copies or substantial
  portions of the Software.
 
  Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
  HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 
  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
  THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
  OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
  ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
  OTHER DEALINGS IN THE SOFTWARE. 
-->
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- ============================================================== -->
<!-- Start right Content here -->
<!-- ============================================================== -->

<div class="col-sm-10 col-rc white-bg p-none">

	<form:form
		action="/fdahpStudyDesigner/adminStudies/saveOrUpdateBasicInfo.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}"
		data-toggle="validator" role="form" id="basicInfoFormId" method="post"
		autocomplete="off" enctype="multipart/form-data">
		<!--  Start top tab section-->
		<div class="right-content-head">
			<div class="text-right">
				<div class="black-md-f text-uppercase dis-line pull-left line34">
					Basic Information
					<c:set var="isLive">${_S}isLive</c:set>
					${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}
				</div>


				<div class="dis-line form-group mb-none mr-sm">
					<button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
				</div>
				<c:if test="${empty permission}">
					<div class="dis-line form-group mb-none mr-sm">
						<button type="button" class="btn btn-default gray-btn actBut"
							id="saveId"
							<c:if test="${not studyBo.viewPermission }">disabled</c:if>>Save</button>
					</div>

					<div class="dis-line form-group mb-none">
						<button type="button" class="btn btn-primary blue-btn actBut"
							id="completedId"
							<c:if test="${not studyBo.viewPermission }">disabled</c:if>>Mark
							as Completed</button>
					</div>
				</c:if>
			</div>
		</div>
		<!--  End  top tab section-->
		<input type="hidden" id="sId" value="${studyBo.id}" name="id" />
		<input type="hidden" value="" id="buttonText" name="buttonText">
		<!--  Start body tab section -->
		<div class="right-content-body col-xs-12">

			<div class="col-md-12 p-none">
				<div class="col-md-6 pl-none">
					<div class="gray-xs-f mb-xs">
						Study ID <small>(15 characters max)</small><span
							class="requiredStar"> *</span>
					</div>
					<div class="form-group customStudyClass">
						<input type="text" custAttType="cust" autofocus="autofocus"
							class="form-control aq-inp studyIdCls" name="customStudyId"
							id="customStudyId" maxlength="15"
							value="${studyBo.customStudyId}"
							<c:if test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}"> disabled</c:if>
							required />
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
				<div class="col-md-6 pr-none">
					<div class="gray-xs-f mb-xs">
						Study Name <small>(50 characters max)</small><span
							class="requiredStar"> *</span>
					</div>
					<div class="form-group">
						<input type="text" class="form-control" name="name"
							id="customStudyName" value="${fn:escapeXml(studyBo.name)}"
							maxlength="50" required />
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
			</div>

			<div class="col-md-12 p-none">
				<div class="gray-xs-f mb-xs">
					Study full name <small>(150 characters max)</small><span
						class="requiredStar"> *</span>
				</div>
				<div class="form-group">
					<input type="text" class="form-control" name="fullName"
						value="${fn:escapeXml(studyBo.fullName)}" maxlength="150" required />
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>

			<div class="col-md-12 p-none">
				<div class="col-md-6 pl-none">
					<div class="gray-xs-f mb-xs">
						Study Category<span class="requiredStar"> *</span>
					</div>
					<div class="form-group">
						<select
							class="selectpicker aq-select aq-select-form elaborateClass"
							id="category" name="category" required title="Select">
							<c:forEach items="${categoryList}" var="category">
								<option value="${category.id}"
									${studyBo.category eq category.id ?'selected':''}>${category.value}</option>
							</c:forEach>
						</select>
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
				<div class="col-md-6 pr-none">
					<div class="gray-xs-f mb-xs">
						Research Sponsor<span class="requiredStar"> *</span>
					</div>
					<div class="form-group">
						<select
							class="selectpicker aq-select aq-select-form elaborateClass"
							required title="Select" name="researchSponsor">
							<c:forEach items="${researchSponserList}" var="research">
								<option value="${research.id}"
									${studyBo.researchSponsor eq research.id ?'selected':''}>${research.value}</option>
							</c:forEach>
						</select>
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
			</div>

			<div class="col-md-12 p-none">
				<div class="col-md-6 pl-none">
					<div class="gray-xs-f mb-xs">
						Data Partner<span class="requiredStar"> *</span>
					</div>
					<div class="form-group">
						<select class="selectpicker elaborateClass" id="dataPartnerId"
							multiple="multiple" title="Select"
							data-none-selected-text="Select" name="dataPartner" required>
							<c:forEach items="${dataPartnerList}" var="datapartner">
								<option value="${datapartner.id}"
									${fn:contains(studyBo.dataPartner , datapartner.id ) ? 'selected' : ''}>${datapartner.value}</option>
							</c:forEach>
						</select>
						<div class="help-block with-errors red-txt"></div>
					</div>

				</div>
				<div class="col-md-6 pr-none">
					<div class="gray-xs-f mb-xs">
						Tentative Duration <small>(3 numbers max)</small><span
							class="requiredStar"> *</span>
					</div>
					<div class="form-group col-md-4 p-none mr-md mb-none">
						<input type="text" class="form-control" name="tentativeDuration"
							value="${studyBo.tentativeDuration}" maxlength="3" required
							pattern="^(0{0,2}[1-9]|0?[1-9][0-9]|[1-9][0-9][0-9])$"
							data-pattern-error="Please enter valid number." />
						<div class="help-block with-errors red-txt"></div>
					</div>
					<div class="form-group col-md-4 p-none mb-none">
						<select class="selectpicker elaborateClass" required
							title="Select" name="tentativeDurationWeekmonth">
							<option value="Days"
								${studyBo.tentativeDurationWeekmonth eq 'Days'?'selected':''}>Days</option>
							<option value="Weeks"
								${studyBo.tentativeDurationWeekmonth eq 'Weeks'?'selected':''}>Weeks</option>
							<option value="Months"
								${studyBo.tentativeDurationWeekmonth eq 'Months'?'selected':''}>Months</option>
							<option value="Years"
								${studyBo.tentativeDurationWeekmonth eq 'Years'?'selected':''}>Years</option>
						</select>
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
			</div>

			<div class="col-md-12 p-none">
				<div class="gray-xs-f mb-xs">
					Study Tagline <small>(100 characters max) </small><span
						class="requiredStar"> *</span>
				</div>
				<div class="form-group">
					<input type="text" class="form-control" name="studyTagLine"
						value="${fn:escapeXml(studyBo.studyTagLine)}" maxlength="100"
						required />
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>

			<div class="col-md-12 p-none">
				<div class="gray-xs-f mb-xs">
					Description<span class="requiredStar"> *</span>
				</div>
				<div class="form-group">
					<textarea class="form-control" id="editor" name="description"
						required>${studyBo.description}</textarea>
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>
			<div class="col-md-12 p-none">
				<div class="col-md-6 pl-none">
					<div class="gray-xs-f mb-xs">
						Study website <span>(e.g: http://www.google.com) </span> <small>(100
							characters max)</small>
					</div>
					<div class="form-group">
						<input type="text" class="form-control" id="studyWebsiteId"
							name="studyWebsite" value="${studyBo.studyWebsite}"
							pattern="^(http(s)?:\/\/)?(www\.)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
							title="Include http://" maxlength="100"
							data-pattern-error="Please enter a valid URL" />
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
				<div class="col-md-6 pr-none">
					<div class="gray-xs-f mb-xs">
						Study feedback destination inbox email address <small>(100
							characters max) </small><span class="requiredStar"> *</span>
					</div>
					<div class="form-group">
						<input type="text" class="form-control" name="inboxEmailAddress"
							value="${studyBo.inboxEmailAddress}" required maxlength="100"
							pattern="[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,3}$"
							autocomplete="off" data-pattern-error="Email address is invalid" />
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
			</div>

			<div class="col-md-12 p-none mb-xxlg">
				<div class="col-md-6 pl-none">
					<div class="gray-xs-f mb-xs">
						Study type<span class="requiredStar"> *</span>
					</div>
					<div class="form-group">
						<span class="radio radio-info radio-inline p-45"> <input
							type="radio" id="inlineRadio5"
							class="rejoin_radio studyTypeClass" name="type" value="GT"
							${studyBo.type eq 'GT'?'checked':""} required
							<c:if test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}"> disabled </c:if>>
							<label for="inlineRadio5">Gateway</label>
						</span> <span class="radio radio-inline"> <input type="radio"
							id="inlineRadio6" class="rejoin_radio studyTypeClass" name="type"
							value="SD" ${studyBo.type eq 'SD'?'checked':""} required
							<c:if test="${not empty studyBo.status && (studyBo.status == 'Active' || studyBo.status == 'Published' || studyBo.status == 'Paused' || studyBo.status == 'Deactivated')}"> disabled </c:if>>
							<label for="inlineRadio6">Standalone</label>
						</span>
						<div class="help-block with-errors red-txt"></div>
					</div>
				</div>
				<div class="col-md-6 pr-none thumbImageDIv">
					<div class="gray-xs-f mb-sm">
						Study Thumbnail Image <span> <span class="filled-tooltip"
							data-toggle="tooltip" data-placement="top" data-html="true"
							title="<span class='font24 text-weight-light pull-left'></span> JPEG / PNG<br><span class='font20'></span> Recommended Size: 225x225 pixels" /></span></span>
						<span class="requiredStar thumbDivClass"
							style="color: red; display: none"> *</span>
					</div>
					<div>
						<div class="thumb">
							<img
								<c:if test="${not empty studyBo.thumbnailImage}">src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />studylogo/${studyBo.thumbnailImage}" </c:if>
								<c:if test="${empty studyBo.thumbnailImage}">src="/fdahpStudyDesigner/images/dummy-img.jpg" </c:if>
								onerror="this.src='/fdahpStudyDesigner/images/dummy-img.jpg';"
								class="wid100" />
						</div>
						<div class="dis-inline ">
							<span id="removeUrl" class="blue-link elaborateHide">X<a
								href="javascript:void(0)"
								class="blue-link txt-decoration-underline pl-xs">Remove
									Image</a></span>
							<div class="form-group mb-none mt-sm">
								<button id="uploadImgbtn" type="button"
									class="btn btn-default gray-btn imageButtonDis">Upload
									Image</button>
								<span><span
									class="help-block with-errors red-txt pos-inherit"></span></span> <input
									id="uploadImg" class="dis-none" type="file" name="file"
									accept=".png, .jpg, .jpeg" onchange="readURL(this);" required>
								<input type="hidden" value="${studyBo.thumbnailImage}"
									id="thumbnailImageId" name="thumbnailImage" />

							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<!--  End body tab section -->
	</form:form>
</div>
<!-- End right Content here -->

<script>
        $(document).ready(function(){
        	 $('#removeUrl').css("visibility","hidden");
             var file = $('#uploadImg').val();
             var thumbnailImageId = $('#thumbnailImageId').val();
             if(file || thumbnailImageId){
          	   $('#removeUrl').css("visibility","visible");
             }
             
        	<c:if test="${not empty permission}">
            $('#basicInfoFormId input,textarea,select').prop('disabled', true);
            $('#basicInfoFormId').find('.elaborateClass').addClass('linkDis');
            $('.elaborateHide').css('visibility','hidden');
            $('.imageButtonDis').prop('disabled', true);
           </c:if>
           
        	var studyType = '${studyBo.type}';
            if (studyType) {
            	if(studyType === 'GT'){
            		$('.thumbDivClass').show();
            	}else{
            		$('.thumbDivClass').hide();
            	}
            }
        	
        	checkRadioRequired();
    		$(".rejoin_radio").click(function(){
    			checkRadioRequired();
    		})
        	
        	$("[data-toggle=tooltip]").tooltip();

//             //wysiwyg editor
            if($("#editor").length > 0){
            tinymce.init({
                selector: "#editor",
                theme: "modern",
                skin: "lightgray",
                height:180,
                plugins: [
                    "advlist autolink link image lists charmap hr anchor pagebreak spellchecker",
                    "save contextmenu directionality paste"
                ],
                toolbar: "anchor bold italic | alignleft aligncenter alignright alignjustify | bullist numlist outdent indent | underline link | hr removeformat | cut undo redo",
                menubar: false,
                toolbar_items_size: 'small',
                content_style: "div, p { font-size: 13px;letter-spacing: 1px;}",
                <c:if test="${not empty permission}">readonly:1,</c:if>
                setup : function(ed) {
                    ed.on('keypress change', function(ed) {
                    	resetValidation($('#editor').val(tinyMCE.get("editor").getContent()).parents('form'));
                    });
           	  	}
            });
        }
        // File Upload    
         $("#uploadImgbtn").click(function(){
            $("#uploadImg").click();
         });
            
        // Removing selected file upload image
        $("#removeUrl").click(function(){
            $(".thumb img").attr("src","/fdahpStudyDesigner/images/dummy-img.jpg");
            $('#uploadImg').val('');
            $('#thumbnailImageId').val('');
            var file = $('#uploadImg').val();
            var thumbnailImageId = $('#thumbnailImageId').val();
            if(file || thumbnailImageId){
         		$("#uploadImg").removeAttr('required');
         		resetValidation($("#uploadImg").parents('form'));
         		$('#removeUrl').css("visibility","visible");
         	} else {
         		$("#uploadImg").attr('required', 'required');
         		resetValidation($("#uploadImg").parents('form'));
         		$('#removeUrl').css("visibility","hidden");
         	}
         });
        
        $("#completedId").on('click', function(e){
        		e.preventDefault();
        		var type = $("input[name='type']:checked").val();
        		if(type && type == 'GT'){
        			validateStudyId('',function(st){
                		if(st){
                       		var studyCount = $('.customStudyClass').find('.help-block').children().length;
    						if(parseInt(studyCount) >= 1){
    							return false;
						    }else{
						    	$('.studyTypeClass,.studyIdCls').prop('disabled', false);
		      	               	  if(isFromValid("#basicInfoFormId"))
		      	                	var file = $('#uploadImg').val();
		      	                    var thumbnailImageId = $('#thumbnailImageId').val();
		      	                   if(file || thumbnailImageId){
		      	                	   $("#uploadImg").parent().find(".help-block").empty();
		      	                	   $("#uploadImg").removeAttr('required');
		      	                	   validateStudyId('',function(st){
		      	                		   if(st){
		      		                       		var studyCount = $('.customStudyClass').find('.help-block').children().length;
		      		    						if(parseInt(studyCount) >= 1){
		      		    							return false;
		      	    						    }else{
		      		                       		$('.studyTypeClass,.studyIdCls').prop('disabled', false);
		      		                       		if(isFromValid("#basicInfoFormId")){
		      		                       			 $("#buttonText").val('completed');
		      		                        	  	 $("#basicInfoFormId").submit();
		      		                        	  }
		      	                                }
		      	                       	 }
		      	                  		}); 
		      				       }
						    }
                       	}
                	}); 
                } else {
                	$("#uploadImg").parent().find(".help-block").empty();
                	validateStudyId('',function(st){
                   	if(st){
                   		var studyCount = $('.customStudyClass').find('.help-block').children().length;
						if(parseInt(studyCount) >= 1){
							return false;
						}else{
							$('.studyTypeClass,.studyIdCls').prop('disabled', false);
	                   		if(isFromValid("#basicInfoFormId")){
	                   			 $("#buttonText").val('completed');
	                    	  	 $("#basicInfoFormId").submit();
	                    	  }
						}
                      }
              		});
                }
         });
        $("#uploadImg").on('change', function(e){
        	var type = $("input[name='type']:checked").val();
            if(null != type && type !='' && typeof type != 'undefined' && type == 'GT'){
               var file = $('#uploadImg').val();
               var thumbnailImageId = $('#thumbnailImageId').val();
               if(file || thumbnailImageId){
            	   $('#removeUrl').css("visibility","visible");
            	   $("#uploadImg").parent().find(".help-block").empty();
               }
            } else {
            	$('#removeUrl').css("visibility","visible");
            	$("#uploadImg").parent().find(".help-block").empty();
            }
        });
        $('#saveId').click(function(e) {
        	$('#basicInfoFormId').validator('destroy').validator();
            	validateStudyId('',function(st){
            		if(st){
            			var studyCount = $('.customStudyClass').find('.help-block').children().length;
						if(parseInt(studyCount) >= 1){
							return false;
						}else if(!$('#customStudyName')[0].checkValidity()){
							$("#customStudyName").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
                        	return false;
                        }else{
                        	$('.studyTypeClass,.studyIdCls').prop('disabled', false);
                			$('#basicInfoFormId').validator('destroy');
                        	$("#buttonText").val('save');
                        	$('#basicInfoFormId').submit();
                        }
            		}else{
            			var studyCount = $('.customStudyClass').find('.help-block').children().length;
            			if(parseInt(studyCount) >= 1){
            				return false;
            			}else{
            			 $('#basicInfoFormId').validator('destroy').validator();
               			 if(!$('#customStudyId')[0].checkValidity()) {
               				 $("#customStudyId").parent().addClass('has-error has-danger').find(".help-block").empty().append('<ul class="list-unstyled"><li>This is a required field.</li></ul>');
               			     return false; 	
               			 }
            			}
            		}
            	});
		});
        $(".studyIdCls").blur(function(){
        	validateStudyId('',function(val){});
        });
  });
        // Displaying images from file upload 
        function readURL(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function (e) {
                $('.thumb img')
                    .attr('src', e.target.result)
                    .width(66)
                    .height(66);
            };

            reader.readAsDataURL(input.files[0]);
        }
      }
        
     //Added for image height and width
      var _URL = window.URL || window.webkitURL;

    $("#uploadImg").change(function(e) {
    	    var file, img;
	        if ((file = this.files[0])) {
	            img = new Image();
	            img.onload = function() {
	                var ht = this.height;
	                var wds = this.width;
	                if(ht == 225 && wds ==225){
	                	$("#uploadImg").parent().find(".help-block").append('');
	                	$('#removeUrl').css("visibility","visible");
	                }else{
	                	$("#uploadImg").parent().find(".help-block").append('<ul class="list-unstyled"><li>Please upload image as per provided guidelines.</li></ul>');
	                	$(".thumb img").attr("src","/fdahpStudyDesigner/images/dummy-img.jpg");
	                	$('#uploadImg, #thumbnailImageId').val('');
	                	$('#removeUrl').css("visibility","hidden");
	                }
	                var file = $('#uploadImg').val();
		   	        var thumbnailImageId = $('#thumbnailImageId').val();
		   	     	if(file || thumbnailImageId){
		   	     		$("#uploadImg").removeAttr('required');
		   	     		resetValidation($("#uploadImg").parents('form'));
		   	     	} else {
		   	     		$("#uploadImg").attr('required', 'required');
		   	     		resetValidation($("#uploadImg").parents('form'));
		   	     	}
	            };
	            img.onerror = function() {
	                $("#uploadImg").parent().find(".help-block").append('<ul class="list-unstyled"><li>Please upload image as per provided guidelines.</li></ul>');
	                $('#removeUrl').css("visibility","hidden");
	                $(".thumb img").attr("src","/fdahpStudyDesigner/images/dummy-img.jpg");
	                $('#uploadImg, #thumbnailImageId').val('');
	                var file = $('#uploadImg').val();
			         var thumbnailImageId = $('#thumbnailImageId').val();
			     	if(file || thumbnailImageId){
			     		$("#uploadImg").removeAttr('required');
			     		resetValidation($("#uploadImg").parents('form'));
			     	} else {
			     		$("#uploadImg").attr('required', 'required');
			     		resetValidation($("#uploadImg").parents('form'));
			     	}
	            };
	            img.src = _URL.createObjectURL(file);
	        }
    });
    $("#uploadImg, #thumbnailImageId").change(function() {
    	 var file = $('#uploadImg').val();
         var thumbnailImageId = $('#thumbnailImageId').val();
     	if(file || thumbnailImageId){
     		$("#uploadImg").removeAttr('required');
     		resetValidation($("#uploadImg").parents('form'));
     	} else {
     		$("#uploadImg").attr('required', 'required');
     		resetValidation($("#uploadImg").parents('form'));
     	}
	});
  function validateStudyId(item,callback){
	var customStudyId = $("#customStudyId").val();
	var thisAttr= $("#customStudyId");
	var dbcustomStudyId = '${studyBo.customStudyId}';
	if(customStudyId != null && customStudyId !='' && typeof customStudyId!= 'undefined'){
		if( dbcustomStudyId !=customStudyId){
			$.ajax({
				url: "/fdahpStudyDesigner/adminStudies/validateStudyId.do?_S=${param._S}",
                type: "POST",
                datatype: "json",
                data: {
                	customStudyId:customStudyId,
                    "${_csrf.parameterName}":"${_csrf.token}",
                },
                success:  function getResponse(data){
                    var message = data.message;
                    if('SUCCESS' != message){
                        $(thisAttr).validator('validate');
                        $(thisAttr).parent().removeClass("has-danger").removeClass("has-error");
                        $(thisAttr).parent().find(".help-block").html("");
                        callback(true);
                    }else{
                        $(thisAttr).val('');
                        $(thisAttr).parent().addClass("has-danger").addClass("has-error");
                        $(thisAttr).parent().find(".help-block").empty();
                        $(thisAttr).parent().find(".help-block").append("<ul class='list-unstyled'><li>'" + customStudyId + "' has already been used in the past.</li></ul>");
                        callback(false);
                    }
                },
                global : false
          });
		}else{
			callback(true);
		}
	}else{
		 callback(false);
	}
}
  function checkRadioRequired() {
        	var rejoinRadioVal = $('input[name=type]:checked').val();
        	if(rejoinRadioVal=='GT'){
        		$('.thumbDivClass').show();
        		$('.thumbImageDIv').show();
        		$('#uploadImg').attr('required','required');
        		var file = $('#uploadImg').val();
                var thumbnailImageId = $('#thumbnailImageId').val();
            	if(file || thumbnailImageId){
            		$("#uploadImg").removeAttr('required');
            		resetValidation($("#uploadImg").parents('form'));
            	} else {
            		$("#uploadImg").attr('required', 'required');
            		resetValidation($("#uploadImg").parents('form'));
            	}
        	}else{
        		$('.thumbDivClass').hide();
        		$('.thumbImageDIv').hide();
        		$('#uploadImg').removeAttr('required','');
        		resetValidation($("#uploadImg").parents('form'));
        	}
        }
                 
</script>