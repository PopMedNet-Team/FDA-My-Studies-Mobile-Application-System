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
		action="/fdahpStudyDesigner/adminStudies/saveOrUpdateStudyOverviewPage.do?${_csrf.parameterName}=${_csrf.token}&_S=${param._S}"
		data-toggle="validator" role="form" id="overViewFormId" method="post"
		autocomplete="off" enctype="multipart/form-data">
		<!--  Start top tab section-->
		<div class="right-content-head">
			<div class="text-right">
				<div class="black-md-f text-uppercase dis-line pull-left line34">
					Overview
					<c:set var="isLive">${_S}isLive</c:set>${not empty  sessionScope[isLive]?'<span class="eye-inc ml-sm vertical-align-text-top"></span>':''}</div>

				<div class="dis-line form-group mb-none mr-sm">
					<button type="button" class="btn btn-default gray-btn cancelBut">Cancel</button>
				</div>
				<c:if test="${empty permission}">
					<div class="dis-line form-group mb-none mr-sm">
						<button type="button" class="btn btn-default gray-btn submitEle"
							actType="save">Save</button>
					</div>

					<div class="dis-line form-group mb-none">
						<button type="button" class="btn btn-primary blue-btn submitEle"
							id="completedId" actType="completed">Mark as Completed</button>
					</div>
				</c:if>
			</div>
		</div>
		<!--  End  top tab section-->
		<input type="hidden" value="${studyBo.id}" name="studyId" />
		<input type="hidden" value="" id="buttonText" name="buttonText">


		<!--  Start body tab section -->
		<div class="right-content-body">

			<div>
				<div class="gray-xs-f mb-xs">
					Study Video URL (if available <span>e.g:
						http://www.google.com</span>) <small>(300 characters max) </small>
				</div>
				<div class="form-group">
					<input autofocus="autofocus" type="text" class="form-control"
						id="studyMediaLinkId" name="mediaLink"
						value="${studyBo.mediaLink}" maxlength="300"
						pattern="^(http(s)?:\/\/)?(www\.)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"
						title="Include http://"
						data-pattern-error="Please enter a valid URL">
					<div class="help-block with-errors red-txt"></div>
				</div>
			</div>

			<!-- Study Section-->
			<div class="overview_section">
				<div class="panel-group overview-panel" id="accordion">
					<div class="black-md-f mb-md">
						Manage Overview Pages <span><span class="filled-tooltip"
							data-toggle="tooltip" data-placement="right" data-html="true"
							title=""
							data-original-title="
	                 	<p class='text-left'>These pages are meant for the introductory, 'Quick Overview' section of your study in the mobile app. It is intended to help users get a quick snapshot summary of what the study is about and how it may benefit them and others.</p>
						<p class='text-left'>Each page has an image, title and about 180-200 characters of description allowed. Given below are some suggested topics you can touch upon in these pages (remember to key in lines that can best describe your study in a way that people will be able to relate to and understand):</p>	
						<div class='text-left'>o Study Purpose and Goals</div>
						<div class='text-left'>o Target Audience</div>
						<div class='text-left'>o Usage of Participant Data</div>
						<div class='text-left'>o Benefits / Why one must participate?</div>
	                 "></span></span>
					</div>
					<c:if test="${empty studyPageBos}">
						<!-- Start panel-->
						<div class="panel panel-default">
							<input type="hidden" name="pageId">
							<div class="panel-heading">
								<div class="panel-title">
									<a data-toggle="collapse" data-parent="#accordion"
										href="#collapse1" aria-expanded="true">
										<div class="text-left dis-inline">
											<div
												class="gray-xs-f mb-xs text-uppercase text-weight-bold pageCount">Page
												- 1</div>
											<div class="studyCount">${studyBo.name}</div>
										</div>
										<div class="text-right dis-inline pull-right">
											<!-- <span class="sprites_icon delete"></span> -->
											<span class="ml-lg imageBg"><img class="arrow"
												src="/fdahpStudyDesigner/images/icons/slide-down.png" /></span>
										</div>
									</a>
								</div>
							</div>
							<div id="collapse1" class="panel-collapse collapse in">
								<div class="panel-body pt-none">

									<div class="gray-xs-f mb-sm">
										Image <span><span class="filled-tooltip"
											data-toggle="tooltip" data-placement="top" data-html="true"
											title=""
											data-original-title=" JPEG / PNG <br> Recommended Size: 750x1334 pixels"></span></span>
										<span class="requiredStar"> *</span>
									</div>
									<div>
										<div class="thumb">
											<img src="/fdahpStudyDesigner/images/dummy-img.jpg"
												class="wid100" />
										</div>
										<div class="dis-inline imgCls">
											<span id="" class="blue-link removeUrl elaborateHide">X<a
												href="javascript:void(0)"
												class="blue-link txt-decoration-underline pl-xs">Remove
													Image</a></span>
											<div class="form-group mb-none mt-sm">
												<button id="" type="button"
													class="btn btn-default gray-btn uploadImgbtn">Upload
													Image</button>
												<input id="1" class="dis-none uploadImg" data-imageId='1'
													type="file" name="multipartFiles"
													accept=".png, .jpg, .jpeg" onchange="readURL(this);"
													required data-error="Please select an image."> <input
													type="hidden" class="imagePathCls" name="imagePath" />
												<div class="help-block with-errors red-txt wid180"></div>
											</div>
										</div>
									</div>

									<div class="mt-xlg">
										<div class="gray-xs-f mb-xs">
											Title <small>(50 characters max) </small><span
												class="requiredStar">*</span>
										</div>
										<div class="form-group">
											<input type="text" class="form-control updateInput"
												name="title" required maxlength="50"
												value="${fn:escapeXml(studyBo.name)}" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
									<div class="mt-xlg">
										<div class="gray-xs-f mb-xs">
											Description <small>(200 characters max) </small><span
												class="requiredStar">*</span>
										</div>
										<div class="form-group elaborateClass">
											<textarea class=" form-control updateInput" rows="5"
												id="editor1" name="description" required
												data-error="Please enter plain text of up to 200 characters max."
												maxlength="200"></textarea>

											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<!-- End panel-->
					</c:if>
					<c:forEach items="${studyPageBos}" var="studyPageBo"
						varStatus="spbSt">
						<!-- Start panel-->
						<div class="panel panel-default">
							<input type="hidden" value="${studyPageBo.pageId}" name="pageId">
							<div class="panel-heading">
								<div class="panel-title">
									<a data-toggle="collapse" data-parent="#accordion"
										href="#collapse${spbSt.count}"
										aria-expanded=<c:if test='${spbSt.last}'>"true"</c:if>
										<c:if test='${not spbSt.last}'>"false"</c:if>>
										<div class="text-left dis-inline">
											<div
												class="gray-xs-f mb-xs text-uppercase text-weight-bold pageCount">Page
												- ${spbSt.count}</div>
											<div class="studyCount">${fn:escapeXml(studyPageBo.title)}</div>
										</div>
										<div class="text-right dis-inline pull-right">
											<c:if test="${not spbSt.first}">
												<span class="sprites_icon delete elaborateHide"></span>
											</c:if>
											<span class="ml-lg imageBg"><img class="arrow"
												src="/fdahpStudyDesigner/images/icons/slide-down.png" /></span>
										</div>
									</a>
								</div>
							</div>
							<div id="collapse${spbSt.count}"
								class="panel-collapse collapse <c:if test='${spbSt.last}'>in</c:if>">
								<div class="panel-body  pt-none">
									<div>
										<div class="gray-xs-f mb-sm">
											Image <span><span class="filled-tooltip"
												data-toggle="tooltip" data-placement="top" data-html="true"
												title="" src="/fdahpStudyDesigner/images/icons/tooltip.png"
												data-original-title="<span class='font24'>.</span></span> JPEG/PNG<br><span class='font24'>.</span> Recommended Size: <c:if test='${spbSt.first}'>750x1334</c:if><c:if test='${not spbSt.first}'>750x570</c:if> pixels"></span>
												<span class="requiredStar"> *</span>
										</div>
										<div>
											<div class="thumb">
												<img
													src="<spring:eval expression="@propertyConfigurer.getProperty('fda.imgDisplaydPath')" />studypages/${fn:escapeXml(studyPageBo.imagePath)}"
													onerror="this.src='/fdahpStudyDesigner/images/dummy-img.jpg';"
													class="wid100" />
											</div>
											<div class="dis-inline imgCls">
												<span id="remUrl${spbSt.count}"
													class="blue-link removeUrl elaborateHide">X<a
													href="javascript:void(0)"
													class="blue-link txt-decoration-underline pl-xs">Remove
														Image</a></span>
												<div class="form-group mb-none mt-sm"
													style="vertical-align: bottom;">
													<button id="" type="button"
														class="btn btn-default gray-btn uploadImgbtn">Upload
														Image</button>
													<input id="" class="dis-none uploadImg"
														data-imageId='${spbSt.count}' type="file"
														name="multipartFiles" accept=".png, .jpg, .jpeg"
														onchange="readURL(this);"
														<c:if test="${empty studyPageBo.imagePath}">required</c:if>
														data-error="Please select an image."> <input
														type="hidden" class="imagePathCls" name="imagePath"
														value="${studyPageBo.imagePath}" />
													<div class="help-block with-errors red-txt wid180"></div>
												</div>
											</div>
										</div>
									</div>
									<div class="mt-lg">
										<div class="gray-xs-f mb-xs">
											Title <small>(50 characters max) </small><span
												class="requiredStar">*</span>
										</div>
										<div class="form-group">
											<input type="text" class="form-control updateInput"
												name="title" value="${fn:escapeXml(studyPageBo.title)}"
												required maxlength="50" />
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
									<div class="mt-md">
										<div class="gray-xs-f mb-xs">
											Description <small>(200 characters max) </small><span
												class="requiredStar">*</span>
										</div>
										<div class="form-group elaborateClass">
											<textarea class="form-control updateInput" rows="5"
												name="description" id="editor${spbSt.count}" required
												data-error="Please enter plain text of up to 200 characters max."
												maxlength="200">${studyPageBo.description}</textarea>
											<div class="help-block with-errors red-txt"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<!-- End panel-->
					</c:forEach>
				</div>
			</div>
			<c:if test="${empty permission}">
				<div class="dis-line mt-lg">
					<div class="form-group mb-none">
						<button id="addpage" type="button"
							class="btn btn-primary blue-btn">
							<span class="mr-xs">+</span> Add page
						</button>
					</div>
				</div>
			</c:if>
		</div>
		<!-- End Study Section-->
	</form:form>
</div>
<!-- End right Content here -->




<script>

    $(document).ready(function(){
    
    	<c:if test="${user eq 'logout_login_user'}">
			bootbox.alert({
				closeButton: false,
				message : 'Your user account details have been updated. Please sign in again to continue using the portal.',	
			    callback: function(result) {
			    	var a = document.createElement('a');
			    	a.href = "/fdahpStudyDesigner/sessionOut.do";
					document.body.appendChild(a).click();
			    }
		    });
		</c:if>
		
    	$('body').find('a[aria-expanded=true]').find('.imageBg').html('<img class="arrow" src="/fdahpStudyDesigner/images/icons/slide-up.png" />');
      	$(".menuNav li.active").removeClass('active');
	   	$(".menuNav li.third").addClass('active');
	   	
	    $('.imgCls').each(function(){
        	var imagePathCls =  $(this).find('.imagePathCls').val();
        	if(imagePathCls){
            	$(this).find('.removeUrl').css("visibility","visible");
            }else{
            	$(this).find('.removeUrl').css("visibility","hidden");
            }
        });
	   	
	   	<c:if test="${not empty permission}">
        $('#overViewFormId input,textarea,select').prop('disabled', true);
        $('.uploadImgbtn').prop('disabled', true);
        $('.elaborateHide').css('visibility','hidden');
       </c:if>
      	$("[data-toggle=tooltip]").tooltip();
      	var countId = ${fn:length(studyPageBos)+ 2};
       	// File Upload    
		$(document).on("click",".uploadImgbtn", function(){
		   $(this).parent().find(".uploadImg").click();
		});
          
		// Removing selected file upload image
		$(document).on("click",".removeUrl", function(){
			$(this).css("visibility","hidden");
    	  	$(this).parent().parent().find(".thumb img").attr("src","/fdahpStudyDesigner/images/dummy-img.jpg");
    	  	$(this).parent().parent().find(".uploadImg").val('').attr('required', 'required');
    	  	$(this).parent().parent().find(".imagePathCls").val('');
       	});
      
      //wysiwyg editor
          if($(".editor").length > 0){
          tinymce.init({
              selector: ".editor",
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
              setup : function(ed) {
                  ed.on('change', function(ed) {
                		  resetValidation($('#'+ed.target.id).val(tinyMCE.get(ed.target.id).getContent()).parents('form'));
                		  $('#'+ed.target.id).trigger('change');
                  });
           	  },
           	<c:if test="${not empty permission}">readonly:1</c:if>
          });
      }
                 
      //deleting panel 
	      var b = $("#accordion").find(".panel-default").length; 
	      if(b==1){            
	        $(".delete").hide();  
	      }else if(b > 4){
	         $("#addpage").hide(); 
	      }
       $(document).on("click", ".delete", function(){
	        var a = $(".overview-panel > div").length;           
	        if(a > 1){
	            $(".delete").show();
	            $(this).parents(".panel-default").remove();
	        }
	        var b = $(".overview-panel > div").length; 
	        if(b==1){            
	          $(".delete").hide();  
	        }else if(b>=4){
	           $("#addpage").show(); 
	        }
	        var a = 1;
	        var b = 1;
	      	$('#accordion').find('.pageCount').each(function() {
				$(this).text('PAGE - '+ a++);	
			});
			resetValidation($("#accordion").parents('form'));
			if($('body').find('.panel-collapse.in').length == 0)
				$('body').find('.panel-collapse:last').collapse('show');
        });
          
      
       $("#addpage").click(function(){   
           
          $(".panel-collapse").collapse('hide').removeClass('in');
          $(".delete").show();
          var count = $("#accordion").find('.panel-default').length +1;
          $("#accordion").append("<!-- Start panel-->"+
        		  "<div class='panel panel-default'> <input type='hidden' name='pageId'>"+
        		  "<div class='panel-heading'>"+
        		  "<div class='panel-title'>"+
        		  "<a href='#collapse"+count+"' data-parent=#accordion data-toggle=collapse aria-expanded='true'>"+
        		  "<div class='dis-inline text-left'>"+
        		  "<div class='gray-xs-f mb-xs text-uppercase text-weight-bold pageCount'>PAGE - "+count+"</div>"+
        		  "<div class='studyCount'></div>"+
        		  "</div>"+
        		  "<div class='dis-inline pull-right text-right'>"+
        		  "<span class='delete mr-lg sprites_icon'></span> "+
        		  "<span class='imageBg'><img src='/fdahpStudyDesigner/images/icons/slide-down.png'></span>"+
        		  "</div>"+
        		  "</a>"+
        		  "</div>"+
        		  "</div>"+
        		  "<div class='collapse panel-collapse' id='collapse"+count+"'>"+
        		  "<div class=panel-body  pt-none>"+
        		  "<div>"+
        		  "<div class='gray-xs-f mb-sm'>Image <span><span class='filled-tooltip' data-toggle='tooltip' data-placement='top' data-html='true' title='' src='/fdahpStudyDesigner/images/icons/tooltip.png' data-original-title='<span class= font24>.</span></span> JPEG/PNG<br><span class=font24>.</span> Recommended Size: 750x570 pixels'></span><span class='requiredStar'> *</span> </div>"+
        		  "<div>"+
        		  "<div class=thumb><img src=/fdahpStudyDesigner/images/dummy-img.jpg class=wid100></div>"+
        		  "<div class=dis-inline>"+
        		  "<span class='blue-link removeUrl elaborateHide' id='hideRemoveUrl"+count+"'>X<a href='javascript:void(0)' class='blue-link pl-xs txt-decoration-underline'>Remove Image</a></span>"+
        		  "<div class='form-group mb-none mt-sm'>"+
        		  "<button class='btn btn-default gray-btn uploadImgbtn' type=button>Upload Image</button>"+ 
        		  "<input class='dis-none uploadImg' data-imageId='"+count+"' accept='.png, .jpg, .jpeg' name='multipartFiles' onchange=readURL(this) type=file required data-error='Please select an image.'>"+
        		  "<input type='hidden' class='imagePathCls' name='imagePath' /><div class='help-block with-errors red-txt wid180'></div>"+
        		  "</div>"+
        		  "</div>"+
        		  "</div>"+
        		  "</div>"+
        		  "<div class=mt-lg>"+
        		  "<div class='gray-xs-f mb-xs'>Title <small>(50 characters max) </small><span class='requiredStar'>*</span></div>"+
        		  "<div class=form-group>"+
        		  "<input type='text' class='form-control updateInput'  name='title' required maxlength='50'>"+
        		  "<div class='help-block with-errors red-txt'></div>"+
        		  "</div>"+
        		  "</div>"+
        		  "<div class=mt-lg>"+
        		  "<div class='gray-xs-f mb-xs'>Description <small>(200 characters max) </small><span class='requiredStar'>*</span></div>"+
        		  "<div class='form-group elaborateClass'><textarea class='form-control updateInput' name='description' id='editor"+countId+"' rows='5' required data-error='Please enter plain text of up to 200 characters max.' maxlength='200'></textarea>"+
        		  "<div class='help-block with-errors red-txt'></div></div>"+
        		  "</div>"+
        		  "</div>"+
        		  "</div>"+
        		  "</div>"+
        		  "<!-- End panel-->");
          $('#hideRemoveUrl'+count).css("visibility","hidden");
          var c = $(".overview-panel > div").length;
          if(c > 5){
              $("#addpage").hide();
          }

          tinymce.init({
              selector: ".editor",
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
              charLimit : 1000,
              setup : function(ed) {
                  ed.on('change', function(ed) {
                		  resetValidation($('#'+ed.target.id).val(tinyMCE.get(ed.target.id).getContent()).parents('form'));
                		  $('#'+ed.target.id).trigger('change');
                  });
           	  }
          });
          resetValidation($("#accordion").parents('form'));
          countId++;
          $("[data-toggle=tooltip]").tooltip();
          $('body').find('.panel-collapse:last').collapse('show').addClass('in');
       });
       $(document).on('show.bs.collapse','.panel-collapse', function(){
       		$('.panel-collapse').not(this).collapse('hide').removeClass('in');
       		$('body').not(this).find('.imageBg').html('<img class="arrow" src="/fdahpStudyDesigner/images/icons/slide-down.png" />');
       		
       });
       $(document).on('hide.bs.collapse','.panel-collapse', function(){
       		$('body').not('a[aria-expanded=true]').find('.imageBg').html('<img class="arrow" src="/fdahpStudyDesigner/images/icons/slide-down.png" />');
       		
       });
       $(document).on('shown.bs.collapse','.panel-collapse', function(){
       		var $panel = $(this).parent().ScrollTo();
       		$('body').find('a[aria-expanded=true]').find('.imageBg').html('<img class="arrow" src="/fdahpStudyDesigner/images/icons/slide-up.png" />');
       });
       $('.submitEle').click(function(e) {
		   $('#actTy').remove();
		   $('<input />').attr('type', 'hidden').attr('name', "actionType").attr('value', $(this).attr('actType')).attr('id', 'actTy') .appendTo('#overViewFormId');
	   		if($(this).attr('actType') == 'save'){
	   			 e.preventDefault();
	   			$('#overViewFormId').validator('destroy');
	   			$('#overViewFormId').submit();
	   		} 
		});
		$("#completedId").on('click', function(e){
			e.preventDefault();
			var formValid = true;
      		$('#accordion').find('.panel-default').each(function() {
				var file = $(this).find('input[type=file]').val();
	            var thumbnailImageId = $(this).find('input[type=file]').parent().find('input[name="imagePath"]').val();
	            if(file || thumbnailImageId){
				   $(this).find('input[type=file]').removeAttr('required');
	            } else {
					formValid = false;
	            }
			});
			if((!isFromValid($(this).parents('form')))) {
				if(!($(this).parents('body').find('.panel-collapse.in').find('.has-error:first').length > 0)){
					$(this).parents('body').find('.panel-collapse.in').collapse('hide').removeClass('in');
				} 
		    	$(this).parents('body').find(".has-error:first").parents('.panel-collapse').not('.in').collapse('show');
			} else {
				if(!($(this).parents('body').find('.panel-collapse.in').find('.has-error-cust:first').length > 0)){
						$(this).parents('body').find('.panel-collapse.in').collapse('hide').removeClass('in');
					} 
			    	$(this).parents('body').find(".has-error-cust:first").parents('.panel-collapse').not('.in').collapse('show');
			    	$(this).parents('body').find(".has-error-cust:first").ScrollTo();
			}
			if(isFromValid($(this).parents('form')) && formValid){
				$(this).attr('disabled','disabled')
		   		$(this).parents('form').submit();
		    } else {
		    	e.preventDefault();
		    }
        });
		var _URL = window.URL || window.webkitURL;
		
		  $(document).on('change','.uploadImg',function(e) {
		      var file, img;
		      var thisAttr = this;
		      var thisId = $(this).attr("data-imageId");
		      if ((file = this.files[0])) {
		          img = new Image();
		          img.onload = function() {
		              var ht = this.height;
		              var wds = this.width;
		              if(thisId!='' && thisId == 1){
		            	  if(ht == 1334 && wds == 750){
		            		  $(thisAttr).parent().parent().find('.removeUrl').css("visibility","visible");
		            		  $(thisAttr).parent().parent().parent().find(".thumb img")
			                  .attr('src', img.src)
			                  .width(66)
			                  .height(66);
			                  $(thisAttr).parent().find('.form-group').removeClass('has-error has-danger');
			                  $(thisAttr).parent().find(".help-block").empty();
		            	  }else{
		            		  $(thisAttr).val();
			                  $(thisAttr).parent().find('.form-group').addClass('has-error has-danger');
			                  $(thisAttr).parent().find(".help-block").empty().append('<ul class="list-unstyled"><li>Please upload image as per provided guidelines.</li></ul>');
			                  $(thisAttr).parent().parent().parent().find(".removeUrl").click();
		            	  }
		              }else{
		            	  if(ht == 570 && wds == 750){
			                  $(thisAttr).parent().parent().find('.removeUrl').css("visibility","visible");
			                  $(thisAttr).parent().parent().parent().find(".thumb img")
			                  .attr('src', img.src)
			                  .width(66)
			                  .height(66);
			                  $(thisAttr).parent().find('.form-group').removeClass('has-error has-danger');
			                  $(thisAttr).parent().find(".help-block").empty();
			              }else{
			                  $(thisAttr).val();
			                  $(thisAttr).parent().find('.form-group').addClass('has-error has-danger');
			                  $(thisAttr).parent().find(".help-block").empty().append('<ul class="list-unstyled"><li>Please upload image as per provided guidelines.</li></ul>');
			                  $(thisAttr).parent().parent().parent().find(".removeUrl").click();
			              }
		              }
		              
		          };
		          img.onerror = function() {
		        	  $(thisAttr).val();
	                  $(thisAttr).parent().find('.form-group').addClass('has-error has-danger');
	                  $(thisAttr).parent().find(".help-block").empty().append('<ul class="list-unstyled"><li>Please upload image as per provided guidelines.</li></ul>');
	                  $(thisAttr).parent().parent().parent().find(".removeUrl").click();
		          };
		          img.src = _URL.createObjectURL(file);
		
		
		      }
				var file = $(this).find('input[type=file]').val();
	            var thumbnailImageId = $(this).find('input[type=file]').parent().find('input[name="imagePath"]').val();
	            if(file || thumbnailImageId){
				   $(this).removeAttr('required');
	            } else {
					$(this).removeAttr('required','required');
	            }
		  });
     });
      
      // Displaying images from file upload 
      function readURL(input) {
      if (input.files && input.files[0]) {
          var reader = new FileReader();  
          reader.onload = function (e) {
          };
          reader.readAsDataURL(input.files[0]);
      }
  	}
</script>
