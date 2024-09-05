<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/currsitesetting/read" var="readUrl" />
<c:url value="/eco/currsitesetting/update" var="updateUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>





<!-- Page body -->


<!-- Page scripts  -->

<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=AIzaSyB9-dRuduoBUGDiW28ByX3Lu4kJX2KtbU8"></script>


<!-- Java(optional)  -->

<%
	String allSiteValue = "<span class='fas fa-times text-light'></span>";
	String currSiteValue = "<span class='fas fa-times text-light'></span>";
	
	Boolean globalCustLogoDisplayed = (Boolean) request.getAttribute("globalCustLogoDisplayed");
	Boolean currCustLogoDisplayed = (Boolean) request.getAttribute("currCustLogoDisplayed");

	if (globalCustLogoDisplayed != null && globalCustLogoDisplayed.booleanValue()) {
		allSiteValue = "<span class='far fa-check text-primary'></span>";
	}
	if (currCustLogoDisplayed != null && currCustLogoDisplayed.booleanValue()) {
		currSiteValue = "<span class='far fa-check text-primary'></span>";
	}
%>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#basic-info">
			<i class="mr-1 far fa-check"></i>
			${tab_general}
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#logo-loc">
			<i class="mr-1 fas fa-location-arrow"></i>
			${tab_location}
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#monitoring">
			<i class="mr-1 far fa-eye"></i>
			${tab_monitoring}
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#asset">
			<i class="mr-1 far fa-building"></i>
			${tab_asset}
		</a>
	</li>
	<li class="nav-item">
		<a class="nav-link" data-toggle="tab" href="#advanced-info">
			<i class="mr-1 fas fa-tasks"></i>
			${tab_advanced}
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="basic-info">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
					${title_editionCode}
					<span class="small text-muted pl-3">${desc_editionCode}</span>
				</div>
				<select name="editionCode" class="selectpicker col-sm-6 px-0" data-style="btn-default" data-none-selected-text="">
<c:forEach var="item" items="${Editions}">
					<option value="${item.value}">${item.text}</option>
</c:forEach>
				</select>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_autoRvmReg}
					<span class="small text-muted pl-3">${desc_autoRvmReg}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="auto-rvm-reg-switch">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_autoSiteUser}
					<span class="small text-muted pl-3">${desc_autoSiteUser}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="auto-site-user-switch" checked="checked">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_autoRvmStatus}
					<span class="small text-muted pl-3">${desc_autoRvmStatus}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="auto-status-calc-switch">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_rvmGroupOrder}
					<span class="small text-muted pl-3">${desc_rvmGroupOrder}</span>
				</div>
				<select name="rvmGroupOrder" class="selectpicker col-sm-6 px-0" data-style="btn-default" data-none-selected-text="">
					<option value="G">${item_orderGroup}</option>
					<option value="C">${item_orderColor}</option>
				</select>
			</div>
		</div>
	</div>
	<div class="tab-pane" id="logo-loc">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
					${title_siteLogo}
					<span class="small text-muted pl-3">${desc_siteLogo}</span>
				</div>
				<hr class="border-light m-0 mt-2">
				<div class="table-responsive">
					<table class="table card-table m-0">
						<tbody>
							<tr>
								<th class="small"></th>
								<th class="small">${label_allSite}</th>
								<th class="small">${label_currSite}</th>
							</tr>
							<tr>
								<td class="small">${label_currSetting}</td>
								<td><%= allSiteValue %></td>
								<td><%= currSiteValue %></td>
							</tr>
							
<c:if test="${requestScope.currCustLogoDisplayed}">

							<tr>
								<td class="small">${label_serviceURL}</td>
								<td></td>
								<td class="small"><samp>${currSiteLogoServiceURL}</samp></td>
							</tr>

</c:if>
							
<c:if test="${requestScope.currCustLogoDisplayed or requestScope.globalCustLogoDisplayed}">
							
							<tr>
								<td class="small">${label_logoFile}</td>
								<td class="small">

<c:if test="${requestScope.globalCustLogoDisplayed}">

									<samp>
										logo_login.png<br>
										logo_top.png
									</samp>

</c:if>
							
								</td>
								<td class="small">

<c:if test="${requestScope.currCustLogoDisplayed}">

									<samp>
										logo_login.${siteShortName}.png<br>
										logo_top.${siteShortName}.png
									</samp>

</c:if>
							
								</td>
							</tr>

</c:if>
							
						</tbody>
					</table>
				</div>
				<hr class="border-light m-0">
				<div class="mt-3">
					<div class="d-flex flex-wrap">
						<div class="mb-3 mr-3" style="width: 302px;">
							<div class="mb-1 ml-1">
								<span class="fas fa-map-marker-alt mr-1"></span>${label_login}
							</div>
							<div style="background: #f8f8f8;">
								<img src="${logoFileLogin}" style="border: solid 1px #e5e5e5;" class="my-5">
							</div>
							<div class="d-flex justify-content-center text-muted">
								<span>300px</span>
								<span class="text-body mx-2">x</span>
								<span>[${tip_changeable}]</span>
							</div>
						</div>

						<div class="mb-3 mr-3" style="width: 15.625rem;">
							<div class="mb-1">
								<span class="fas fa-map-marker-alt mr-1"></span>${label_slide}
							</div>
							<div style="background: #3f4853; height: 60px; display: flex; align-items: center;">
									<span class="app-brand-logo logo-frame background-green ml-4">
									<img src="${logoFileTop}" alt>
								</span>
								<span class="app-brand-text logo-text sidenav-text font-weight-normal ml-2 text-white">
									${logoTitleText}
								</span>
							</div>
							<div class="d-flex justify-content-center text-muted">
								<span>30px</span>
								<span class="text-body mx-2">x</span>
								<span>30px</span>
							</div>
						</div>
					
						<div class="mb-3 mr-3" style="width: 250px;">
							<div class="mb-1">
								<span class="fas fa-map-marker-alt mr-1"></span>${label_top}
							</div>
							<div style="background: #be0000; height: 60px; display: flex; align-items: center;">
								<span class="app-brand-logo ml-4">
									<img src="${logoTopPathFile}" alt>
								</span>
								<span class="app-brand-text logo-text sidenav-text font-weight-normal ml-2 text-white">
									${logoTitleText}
								</span>
								<div class="ml-4">
									<i class="fas fa-bars fa-lg text-white"></i>
								</div>
							</div>
							<div class="d-flex justify-content-center text-muted">
								<span>30px</span>
								<span class="text-body mx-2">x</span>
								<span>30px</span>
							</div>
						</div>
					
						<div style="width: 250px;">
							<div class="mb-1">
								<span class="fas fa-map-marker-alt mr-1"></span>${label_logoText}
							</div>
							<input name="logoTitle" type="text" maxlength="15" class="form-control">
						</div>
					</div>
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_siteLoc}
					<span class="small text-muted pl-3">${desc_siteLoc}</span>
				</div>
				<div id="map-canvas" style="height: 250px; border: solid 1px #e5e5e5;"></div>
			</div>
		</div>
	</div>
	<div class="tab-pane" id="monitoring">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
					${title_failureControlMode}
					<span class="small text-muted pl-3">${desc_failureControlMode}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="failure-control-switch">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body pb-2">
				<div class="pb-2">
					${title_failureControlPeriod}
					<span class="small text-muted pl-3">${desc_failureControlPeriod}</span>
				</div>
				<div class="form-row">
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								${label_start}
							</label>
							<input name="failControlStartTime" value="09:00" type="text" class="form-control border-none" />
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								${label_end}
							</label>
							<input name="failControlEndTime" value="18:00" type="text" class="form-control border-none" />
						</div>
					</div>
				</div>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_failureReg}
					<span class="small text-muted pl-3">${desc_failureReg}</span>
				</div>
				<select name="failureRegMin" class="selectpicker col-sm-3 px-0" data-style="btn-default" data-none-selected-text="">
					<option value="3">3</option>
					<option value="5">5</option>
					<option value="7">7</option>
					<option value="10">10</option>
					<option value="15">15</option>
					<option value="30">30</option>
				</select>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_failureRemoval}
					<span class="small text-muted pl-3">${desc_failureRemoval}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="failure-remove-switch" checked="checked">
					<span class="switcher-indicator"></span>
				</label>
			</div>
		</div>
	</div>
	<div class="tab-pane" id="asset">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
					${title_applyAssetModule}
					<span class="small text-muted pl-3">${desc_applyAssetModule}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="asset-module-switch">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_assetIdDigit}
					<span class="small text-muted pl-3">${desc_assetIdDigit}</span>
				</div>
				<select name="assetDigitNumber" class="selectpicker col-sm-3 px-0" data-style="btn-default" data-none-selected-text="">
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
					<option value="7">7</option>
				</select>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_allowedSiteIDList}
					<span class="small text-muted pl-3">${desc_allowedSiteIDList}</span>
				</div>
				<input name="allowedSiteIDList" type="text" class="form-control col-sm-6">
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_assetSiteID}
					<span class="small text-muted pl-3">${desc_assetSiteID}</span>
				</div>
				<input name="assetSiteID" type="text" class="form-control col-sm-6">
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_maxPlayerAsset}
					<span class="small text-muted pl-3">${desc_maxPlayerAsset}</span>
				</div>
				<div class="form-row">
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								${item_rvm}
							</label>
							<select name="maxPlayerAssetRvm" class="selectpicker px-0" data-style="btn-default" data-none-selected-text="">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="1000">${item_noLimit}</option>
							</select>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								${item_display}
							</label>
							<select name="maxPlayerAssetDisplay" class="selectpicker px-0" data-style="btn-default" data-none-selected-text="">
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
								<option value="5">5</option>
								<option value="1000">${item_noLimit}</option>
							</select>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="tab-pane" id="advanced-info">
		<div class="card">
			<div class="card-body">
				<div class="pb-2">
					${title_autoSchdDeploy}
					<span class="small text-muted pl-3">${desc_autoSchdDeploy}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="auto-schd-deploy-switch" checked="checked">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_advFtpServer}
					<span class="small text-muted pl-3">${desc_advFtpServer}</span>
				</div>
				<label class="switcher switcher-lg">
					<input type="checkbox" class="switcher-input" name="adv-ftp-server-switch">
					<span class="switcher-indicator"></span>
				</label>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_routinedSchdPrefix}
					<span class="small text-muted pl-3">${desc_routinedSchdPrefix}</span>
				</div>
				<input name="routineSchedPrefix" type="text" class="form-control col-sm-6" data-toggle="tooltip" 
						data-placement="bottom" title="${desc_routinedSchdPrefixEx} cu_2015.12.20.scd">
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_playerIdPrefix}
					<span class="small text-muted pl-3">${desc_playerIdPrefix}</span>
				</div>
				<input name="playerIdPrefix" type="text" class="form-control col-sm-6">
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_playerIdDigit}
					<span class="small text-muted pl-3">${desc_playerIdDigit}</span>
				</div>
				<select name="digitNumber" class="selectpicker col-sm-3 px-0" data-style="btn-default" data-none-selected-text="">
					<option value="1">1</option>
					<option value="2">2</option>
					<option value="3">3</option>
					<option value="4">4</option>
					<option value="5">5</option>
					<option value="6">6</option>
				</select>
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_rvmModelList}
					<span class="small text-muted pl-3">${desc_rvmModelList}</span>
				</div>
				<input name="rvmModelList" type="text" class="form-control col-sm-9">
			</div>
			<hr class="m-0" />
			<div class="card-body">
				<div class="pb-2">
					${title_maxQuickLinkMenu}
					<span class="small text-muted pl-3">${desc_maxQuickLinkMenu}</span>
				</div>
				<select name="maxMenuCount" class="selectpicker col-sm-3 px-0" data-style="btn-default" data-none-selected-text="">
					<option value="0">0</option>
					<option value="5">5</option>
					<option value="7">7</option>
					<option value="10">10</option>
					<option value="15">15</option>
					<option value="20">20</option>
				</select>
			</div>
		</div>
	</div>
</div>


<div class="text-right mt-3">
	<button id="save-btn" type="button" class="btn btn-primary">${form_save}</button>
</div>

<!--  / Forms -->


<!--  Scripts -->

<script>

var marker;

function initialize(lat, lng) {
	try {
		var map = new google.maps.Map(
			document.getElementById('map-canvas'), {
				center: new google.maps.LatLng(lat, lng),
				zoom: 13,
				mapTypeControl: false,
				streetViewControl: false,
				mapTypeId: google.maps.MapTypeId.ROADMAP
		});

		marker = new google.maps.Marker({
			position: new google.maps.LatLng(lat, lng),
			draggable: true,
			map: map,
			animation: google.maps.Animation.DROP,
			icon: '/resources/shared/icons/pin_default.png',
		});
	}
	catch (err) {}
}


$(document).ready(function() {
	
	$('[data-toggle="tooltip"]').tooltip(); 
	
	$("select[name='assetDigitNumber']").selectpicker('render');
	$("select[name='digitNumber']").selectpicker('render');
	$("select[name='maxMenuCount']").selectpicker('render');
	$("select[name='failureRegMin']").selectpicker('render');
	
	bootstrapSelectVal($("select[name='assetDigitNumber']"), "3");
	bootstrapSelectVal($("select[name='digitNumber']"), "1");
	bootstrapSelectVal($("select[name='rvmGroupOrder']"), "G");
	bootstrapSelectVal($("select[name='editionCode']"), "EE");
	bootstrapSelectVal($("select[name='maxPlayerAssetRvm']"), "1");
	bootstrapSelectVal($("select[name='maxPlayerAssetDisplay']"), "1");
	bootstrapSelectVal($("select[name='maxMenuCount']"), "5");
	bootstrapSelectVal($("select[name='failureRegMin']"), "5");
	
	$("input[name='failControlStartTime']").kendoTimePicker({ format: "HH:mm", parseFormats: ["HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerStartChange, });
	$("input[name='failControlEndTime']").kendoTimePicker({ format: "HH:mm", parseFormats: ["HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerEndChange, });
	
	
	var initLocLat, initLocLng;
	
	var maxPlayerAssetDisplayData = [
		{ text: "1", value: "1" },
		{ text: "2", value: "2" },
		{ text: "3", value: "3" },
		{ text: "4", value: "4" },
		{ text: "5", value: "5" },
		{ text: "${item_noLimit}", value: "1000" },
	];


	$("#save-btn").click(function(e) {
		var mapLat = initLocLat;
		var mapLng = initLocLng;
		
		try {
			mapLat = marker.position.lat();
			mapLng = marker.position.lng();
		}
		catch (err) {}

    	var data = {
			editionCode: $("select[name='editionCode']").val(),
			autoRvmReg: $("input[name='auto-rvm-reg-switch']").is(":checked") ? "Y" : "N",
			autoSiteUser: $("input[name='auto-site-user-switch']").is(":checked") ? "Y" : "N",
			mapLat: mapLat.toString(),
			mapLng: mapLng.toString(),
			rvmGroupOrder: $("select[name='rvmGroupOrder']").val(),
			routineSchedPrefix: $.trim($("input[name='routineSchedPrefix']").val()),
			autoRvmStatus: $("input[name='auto-status-calc-switch']").is(":checked") ? "Y" : "N",
			playerIdPrefix: $.trim($("input[name='playerIdPrefix']").val()),
			playerIdDigit: $("select[name='digitNumber']").val(),
			rvmModelList: $.trim($("input[name='rvmModelList']").val()),
			applyAssetModule: $("input[name='asset-module-switch']").is(":checked") ? "Y" : "N",
			assetIdDigit: $("select[name='assetDigitNumber']").val(),
			allowedSiteIDList: $.trim($("input[name='allowedSiteIDList']").val()),
			assetSiteID: $.trim($("input[name='assetSiteID']").val()),
			maxPlayerAssetRvm: $("select[name='maxPlayerAssetRvm']").val(),
			maxPlayerAssetDisplay: $("select[name='maxPlayerAssetDisplay']").val(),
			maxMenuCount: $("select[name='maxMenuCount']").val(),
			logoTitle: $.trim($("input[name='logoTitle']").val()),
			failureControlMode: $("input[name='failure-control-switch']").is(":checked") ? "Y" : "N",
			failureControlStart: kendo.toString($("input[name='failControlStartTime']").data("kendoTimePicker").value(), "HH:mm"),
			failureControlEnd: kendo.toString($("input[name='failControlEndTime']").data("kendoTimePicker").value(), "HH:mm"),
			failureControlRegMins: $("select[name='failureRegMin']").val(),
			failureControlRemovable: $("input[name='failure-remove-switch']").is(":checked") ? "Y" : "N",
			autoSchdDeploy: $("input[name='auto-schd-deploy-switch']").is(":checked") ? "Y" : "N",
			advFtpServer: $("input[name='adv-ftp-server-switch']").is(":checked") ? "Y" : "N",
		};

    	$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${updateUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showAlertModal("success", "${msg_updateComplete}");
			},
			error: ajaxSaveError
		});
	});

	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			var locLat = null;
			var locLng = null;
			
			for(var i in data) {
				if (data[i].name == "edition.code") {
					bootstrapSelectVal($("select[name='editionCode']"), data[i].value);
				} else if (data[i].name == "auto.rvmReg") {
					$("input[name='auto-rvm-reg-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "auto.siteUser") {
					$("input[name='auto-site-user-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "map.init.latitude") {
					locLat = data[i].value;
				} else if (data[i].name == "map.init.longitude") {
					locLng = data[i].value;
				} else if (data[i].name == "rvmGroup.order") {
					bootstrapSelectVal($("select[name='rvmGroupOrder']"), data[i].value);
				} else if (data[i].name == "routineSched.prefix") {
					$("input[name='routineSchedPrefix']").val(data[i].value);
				} else if (data[i].name == "auto.rvmStatus") {
					$("input[name='auto-status-calc-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "playerID.prefix") {
					$("input[name='playerIdPrefix']").val(data[i].value);
				} else if (data[i].name == "playerID.digit") {
					bootstrapSelectVal($("select[name='digitNumber']"), data[i].value);
				} else if (data[i].name == "rvm.model.list") {
					$("input[name='rvmModelList']").val(data[i].value);
				} else if (data[i].name == "asset.enabled") {
					$("input[name='asset-module-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "asset.siteID") {
					$("input[name='assetSiteID']").val(data[i].value);
				} else if (data[i].name == "assetID.digit") {
					bootstrapSelectVal($("select[name='assetDigitNumber']"), data[i].value);
				} else if (data[i].name == "asset.allowed.siteID.list") {
					$("input[name='allowedSiteIDList']").val(data[i].value);
				} else if (data[i].name == "playerasset.max.rvm") {
					bootstrapSelectVal($("select[name='maxPlayerAssetRvm']"), data[i].value);
				} else if (data[i].name == "playerasset.max.display") {
					bootstrapSelectVal($("select[name='maxPlayerAssetDisplay']"), data[i].value);
				} else if (data[i].name == "quicklink.max.menu") {
					bootstrapSelectVal($("select[name='maxMenuCount']"), data[i].value);
				} else if (data[i].name == "logo.title") {
					$("input[name='logoTitle']").val(data[i].value);
				} else if (data[i].name == "failureControl.enabled") {
					$("input[name='failure-control-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "failureControl.startTime") {
					$("input[name='failControlStartTime']").data("kendoTimePicker").value(data[i].value);
				} else if (data[i].name == "failureControl.endTime") {
					$("input[name='failControlEndTime']").data("kendoTimePicker").value(data[i].value);
				} else if (data[i].name == "failureControl.regMins") {
					bootstrapSelectVal($("select[name='failureRegMin']"), data[i].value);
				} else if (data[i].name == "failureControl.removalAllowed") {
					$("input[name='failure-remove-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "auto.schdDeploy") {
					$("input[name='auto-schd-deploy-switch']").prop("checked", data[i].value == "Y");
				} else if (data[i].name == "ftpServer.advConfig") {
					$("input[name='adv-ftp-server-switch']").prop("checked", data[i].value == "Y");
				}
			}
			
			if (locLat && locLng) {
				initLocLat = parseFloat(locLat);
				initLocLng = parseFloat(locLng);
				
				initialize(parseFloat(locLat), parseFloat(locLng));
			}
		},
		error: ajaxReadError
	});
});


function onKendoPickerStartChange(e) {
	
	var value = e.sender.value();
	
	if (value == null) {
		e.sender.value("09:00");
	}
}


function onKendoPickerEndChange(e) {
	
	var value = e.sender.value();
	
	if (value == null) {
		e.sender.value("18:00");
	}
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
