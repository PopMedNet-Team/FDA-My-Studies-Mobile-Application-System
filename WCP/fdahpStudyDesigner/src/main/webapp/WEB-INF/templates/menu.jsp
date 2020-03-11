<script type="text/javascript">
$(document).ready(function() {
	getAdminNotificationCount();
});

//Refreshing to get the new notifications count
window.setInterval('getNotificationCount()', 300000); 
var getNotificationCount = function() {
	getAdminNotificationCount();
}

//Get the AdminNotificationCount
function getAdminNotificationCount() {
	var mdiv = document.getElementById("rcount");
	$.ajax({
		url : 'getAdminNotificationCount.do',
		type : "POST",
		datatype : "json",
		data : {},
		success : function test(data) {
			data = eval(data);
			var count = data.count;
			if (parseInt(count) > 0) {
				$("#notifSpanId").html('NOTIFICATIONS <B><font color="#42963b">('+data.count+')</font></B>');
			}
		}
	});
} 

</script>

<div class="left-sidebar">
      <div class="sidebar-holder" data-original-title="" title="">
        <div class="accordion" id="leftMenu">
          
          <div class="accordion-group">
            <div class="accordion-heading"  id="todayDeliveryDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getTodayDeliveryList.do"> <span class="hidden-minibar"> TODAY'S DELIVERY</span> </a> 
            </div>
            <div class="accordion-heading"  id="tomorrowDeliveryDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getTomorrowDeliveryList.do"> <span class="hidden-minibar"> TOMORROW'S DELIVERY</span> </a> 
            </div>
            <div class="accordion-heading"  id="pastDeliveryDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getPastOrderList.do"> <span class="hidden-minibar"> PAST ORDERS</span> </a> 
            </div>
            <div class="accordion-heading"  id="futureDeliveryDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getFutureOrderList.do"> <span class="hidden-minibar"> FUTURE ORDERS</span> </a> 
            </div>
            <div class="accordion-heading"  id="productCatalogDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getProductCatalogList.do"> <span class="hidden-minibar"> PRODUCT CATALOG</span> </a> 
            </div>
            
            <div class="accordion-heading"  id="notificationDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getAdminNotificationList.do"> <span class="hidden-minibar" id="notifSpanId"> NOTIFICATIONS </span> </a> 
            </div>
            
            <div class="accordion-heading"  id="farmerBOlDiv"> 
            	<a class="accordion-toggle"  data-parent="#leftMenu" href="getFarmerBOLPage.do"> <span class="hidden-minibar" id="notifSpanId"> FARMER BOL </span> </a> 
            </div>
          
          <div class="accordion-group"   id="f2tSettingDiv">
            <div class="accordion-heading "> <a class="accordion-toggle" data-toggle="collapse" data-parent="#leftMenu" href="#collapseOne"> <span class="hidden-minibar"> SETTINGS</span><span class="fa arrow"></span> </a> </div>
            <div id="collapseOne" class="accordion-body collapse" style="height: 0px; ">
              <div class="accordion-inner">
                <ul>
                  <li><a href="listFarmDetails.do"><span class="fa arrow"></span> <span class="hidden-minibar"> FARM</span></a> </li>
                  <li><a href="listResDetails.do"><span class="fa arrow"></span> <span class="hidden-minibar"> RESTAURANT</span></a> </li>
                  <li><a href="listDriverDetails.do"><span class="fa arrow"></span> <span class="hidden-minibar"> DRIVER</span></a> </li>
                  <li><a href="listPickUpLocationDetails.do"><span class="fa arrow"></span> <span class="hidden-minibar"> PICKUP LOCATION</span></a> </li>
                  <li><a href="listDeliveryLocationDetails.do"><span class="fa arrow"></span> <span class="hidden-minibar"> DELIVERY LOCATION</span></a> </li>
                  <li><a href="getVariableSettingPage.do"><span class="fa arrow"></span> <span class="hidden-minibar"> VARIABLE</span></a> </li>
                  <li id="termsDiv"><a href="getTermsDetailsList.do"><span class="fa arrow"></span> <span class="hidden-minibar"> TERMS</span></a> </li>
                  <li><a href="getProductRequestList.do"><span class="fa arrow"></span> <span class="hidden-minibar"> NEW PRODUCT REQUEST</span></a> </li>
                  <li><a href="getContentList.do"><span class="fa arrow"></span> <span class="hidden-minibar"> CONTENT</span></a> </li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
