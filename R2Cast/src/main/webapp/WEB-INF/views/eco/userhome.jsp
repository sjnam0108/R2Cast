<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->


<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}

<c:if test="${not empty LastLogin}">

	<div class="text-muted text-tiny mt-1">
		<small class="font-weight-normal">${LastLogin}</small>
	</div>

</c:if>

</h4>





<!-- Page body -->


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#quick-links">
			<i class="mr-1 fa-light fa-link"></i>
			${tip_quickLink}
		</a>
	</li>

<c:if test="${fireAlarmSwitchDisplayed}">

	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#fire-alarm">
			<i class="mr-1 fal fa-fire"></i>
			${dfFireAlarm}
		</a>
	</li>

</c:if>

</ul>

<div class="tab-content">
	<div class="tab-pane active" id="quick-links">
		<div class="row">
       
<c:forEach var="item" items="${QuickLinkItems}">

			<div class="col-md-6 col-xl-3">
				<div class="card card-condenced mb-4" onclick="navigateTo('${item.link}')" style="cursor: pointer;">
					<div class="card-body media align-items-center">
						<span class="${item.iconStyle} fa-fw fa-4x text-muted"></span>
						<div class="media-body ml-3">
							<a href="${item.link}" class="text-dark mb-3">${item.title}</a>
							<div class="text-muted small mt-2">${item.subtitle}</div>
						</div>
					</div>
				</div>
			</div>
			
</c:forEach>
		
		</div>
	</div>

<c:if test="${fireAlarmSwitchDisplayed}">

	<div class="tab-pane" id="fire-alarm">
		<div class="card">
			<div class="text-center pt-5 pb-3 px-3">
				<span class="text-large">${dfFireAlarmDesc}</span>
			
				<div class="d-flex justify-content-center pt-4">
					<label class="custom-control custom-radio text-large font-weight-light mr-5">
						<input type="radio" name="alarmType" value="D" class="custom-control-input">
						<span class="custom-control-label big-custom-control-label">${dfDrill}</span>
					</label>
					<label class="custom-control custom-radio text-large font-weight-light">
						<input type="radio" name="alarmType" value="E" class="custom-control-input align-self-center">
						<span class="custom-control-label big-custom-control-label">${dfEmergency}</span>
					</label>
				</div>
			</div>

			<div class="text-center pb-4">
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="control-switch">
					<span class="switcher-indicator"></span>
				</label>
			</div>
		</div>
	</div>

<style>

/* 큰 크기의 라디오 박스 라벨(훈련상황, 긴급상황)을 라디오 버튼과 어울리게 */
.big-custom-control-label::before, .big-custom-control-label::after {
	top: .3rem;  width: 1.25rem;  height: 1.25rem;
}

</style>

<script>
$(document).ready(function() {
	
	$("input[name='control-switch']").change(function() {
		$("input[name='alarmType']").prop("disabled", $(this).is(":checked"));
		changeValue($("input[name=alarmType]:checked").val(), 
				$(this).is(":checked"));
	});
	
	$('input:radio[name=alarmType]:input[value="${fireAlarmType}"]').attr("checked", true);
	
	<c:if test="${fireAlarmActive}">
		$("input[name='alarmType']").prop("disabled", true);
		$("input[name='control-switch']").prop("checked", true);
	</c:if>

	function changeValue(typeCode, value) {
    	$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "/eco/common/datafeed/chgfirealarm",
			data: JSON.stringify( { alertType: typeCode, value: value == true ? "Y" : "N" }),
			success: function (form) {
				showOperationSuccessMsg();
			},
			error: ajaxOperationError
		});
	}
	
});
</script>

</c:if>

</div>

  <script>
  
  function navigateTo(url) {
  	
  	if (url) {
  		location.href = url;		
  	}
 	
  }
  
 </script>

<!--  / Forms -->


<!-- / Page body -->





<!-- Closing tags -->

<common:base />
<common:pageClosing />
