<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/rvm/read" var="readUrl" />
<c:url value="/eco/rvm/destroy" var="destroyUrl" />
<c:url value="/eco/rvm/edit" var="editUrl" />
<c:url value="/eco/rvm/readRegions" var="readRegionUrl" />

<c:url value="/eco/rvm/readReportIntervals" var="readReportIntervalUrl" />

<c:url value="/eco/common/readRvmServiceTypes" var="readCmnServiceTypeUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<script type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=AIzaSyB9-dRuduoBUGDiW28ByX3Lu4kJX2KtbU8"></script>


<!-- Java(optional)  -->

<%
	String editTemplate	 = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
	String customerIdRequiredTemplate =
			"# if (customerIdRequired) { #" + "<span class='fa-regular fa-user-check fa-fw'></span><span class='pl-2'>확인 후 수거</span>" +
			"# } else { #" + "<span class='fa-regular fa-question fa-fw'></span><span class='pl-2'>확인하지 않음</span>" +
			"# } #";
	String importRequiredTemplate =
			"# if (importRequired) { #" + "<span class='fa-regular fa-check'></span>" +
			"# } else { #" +
			"# } #";
	String resultTypeTemplate =
			"# if (resultType == 'c1') { #" + "<span>c1 - COSMO 일별 일괄</span>" +
			"# } else { #" +
			"# } #";
			
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="d-flex align-items-center">
					<div class="btn-group d-none d-sm-inline pr-1">
						<button class="btn btn-secondary dropdown-toggle" type="button" data-toggle="dropdown">
							<span class="fa-regular fa-eyes"></span>
							<span class="pl-1" id="curr-tab-name">일반</span>
						</button>
						<div class="dropdown-menu">
							<a class="dropdown-item" href="javascript:changeTab(1)">
								<span class="far fa-check" id="view-tab-1"></span>
								<span class="pl-1">일반</span>
							</a>
							<a class="dropdown-item" href="javascript:changeTab(2)">
								<span class="fas fa-blank" id="view-tab-2"></span>
								<span class="pl-1">추가</span>
							</a>
							<a class="dropdown-item" href="javascript:changeTab(3)">
								<span class="fas fa-blank" id="view-tab-3"></span>
								<span class="pl-1">옵션</span>
							</a>
	
							<c:if test="${not isMobileMode}">
	
							<a class="dropdown-item" href="javascript:changeTab(9)">
								<span class="fas fa-blank" id="view-tab-9"></span>
								<span class="pl-1">전체</span>
							</a>
	
	</c:if>
	
						</div>
					</div>
					<div class="pr-1">
		    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
					</div>
	    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    			</div>
    		</div>
    		<div class="float-right">
    			<span class="d-none d-sm-inline">
					유효 RVM으로 제한
					<label class="switcher switcher-lg ml-2">
						<input type="checkbox" class="switcher-input" name="view-mode-switch">
						<span class="switcher-indicator"></span>
					</label>
					<span class="mr-3">|</span>
    			</span>
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>

		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />

		<kendo:grid-column title="RVM명" field="rvmName"
				template="<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvmName #</a><a href='javascript:showRvm(#= id #,\x22#= rvmName #\x22)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-search text-info'></span></a></div>" />
		
		<kendo:grid-column title="기기ID" field="deviceID" />
		<kendo:grid-column title="유효시작일" field="effectiveStartDate" format="{0:yyyy-MM-dd}" />
		<kendo:grid-column title="유효종료일" field="effectiveEndDate" format="{0:yyyy-MM-dd}" />
		<kendo:grid-column title="서비스유형" field="serviceType"
				template="#=(serviceType == 'S' || serviceType == 'M')  ? \"<span class='fa-regular fa-check'>\" : \"\"#" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCmnServiceTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		
		<kendo:grid-column hidden="true" title="기기일련번호" field="serialNo" />
		<kendo:grid-column hidden="true" title="지역 코드" field="areaCode" />
		<kendo:grid-column hidden="true" title="지점 코드" field="branchCode" />
		<kendo:grid-column hidden="true" title="지점명" field="branchName" />
		
		<kendo:grid-column hidden="true" title="보고 주기(분)" field="reportInterval" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcTextOnly">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readReportIntervalUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column hidden="true" title="사용자 정보" field="customerIdRequired" template="<%= customerIdRequiredTemplate %>" />
		<kendo:grid-column hidden="true" title="서버 임포트" field="importRequired" template="<%= importRequiredTemplate %>" />
		<kendo:grid-column hidden="true" title="결과 보고 유형" field="resultType" template="<%= resultTypeTemplate %>" />

	</kendo:grid-columns>
	<kendo:grid-filterable extra="false">
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				attachRvmStatusBarPopover();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="rvmName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-filter>
			<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}">
			</kendo:dataSource-filterItem>
		</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json" >
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							var viewMode = $("input[name='view-mode-switch']").is(":checked") ? "E" : "A";
							if (isFirstRead) {
								viewMode = "${sessionScope['userCookie'].viewCodeRvm}";
								isFirstRead = false;
							}
						
							return { reqStrValue1:  viewMode };
						}
					</script>
				</kendo:dataSource-transport-read-data>
			</kendo:dataSource-transport-read>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="effectiveStartDate" type="date" />
					<kendo:dataSource-schema-model-field name="effectiveEndDate" type="date" />
					<kendo:dataSource-schema-model-field name="customerIdRequired" type="boolean" />
					<kendo:dataSource-schema-model-field name="importRequired" type="boolean" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<style>

/* 선택 체크박스를 포함하는 필터 패널을 보기 좋게 */
.k-filter-selected-items {
	font-weight: 500;
	margin: 0.5em 0;
}
.k-filter-menu .k-button {
	width: 47%;
	margin: 0.5em 1% 0.25em;
}


/* Kendo 그리드에서 bootstrap dropdownmenu 모두 보이게 */
.k-grid td {
	overflow: visible;
}

</style>

<!-- / Kendo grid  -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		initForm1();


		$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-1").modal();
	});
	// / Add
	
	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var delRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			delRows.push(selectedItem.id);
		});
			
		if (delRows.length > 0) {
			showDelConfirmModal(function(result) {
				if (result) {
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${destroyUrl}",
						data: JSON.stringify({ items: delRows }),
						success: function (form) {
        					showDeleteSuccessMsg();
							grid.dataSource.read();
						},
						error: ajaxDeleteError
					});
				}
			});
		}
	});
	// / Delete
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog">
		<form class="modal-content" id="form-1" rowid="-1" url="${editUrl}">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle"></span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">
				<div class="form-row">
					<div class="col-12">
						<div class="form-group col">
							<label class="form-label">
								RVM명
								<span class="text-danger">*</span>
							</label>
							<input name="rvmName" type="text" maxlength="50" class="form-control required">
						</div>
					</div>
				</div>
				
				<div class="nav-tabs-top mt-1 px-2">
					<ul class="nav nav-tabs px-1">
						<li class="nav-item">
							<a class="nav-link active" data-toggle="tab" id="rvm-basic" href="\\\#rvm-general-ctnt">
								<span class="d-none d-sm-inline fa-duotone fa-check"  style="--fa-primary-color: mediumseagreen;"></span>
								<span class="pl-1">일반</span>
							</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" data-toggle="tab" id="rvm-option" href="\\\#rvm-plus-info-ctnt">
								<span class="d-none d-sm-inline fa-duotone fa-memo-circle-info"  style="--fa-primary-color: mediumseagreen;"></span>
								<span class="pl-1">추가</span>
							</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" data-toggle="tab" id="rvm-option" href="\\\#rvm-option-ctnt">
								<span class="d-none d-sm-inline fa-duotone fa-toggle-off"  style="--fa-primary-color: mediumseagreen;"></span>
								<span class="pl-1">옵션</span>
							</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" data-toggle="tab" id="rvm-support" href="\\\#rvm-support-ctnt">
								<span class="d-none d-sm-inline fa-duotone fa-user-headset"  style="--fa-primary-color: mediumseagreen;"></span>
								<span class="pl-1">지원</span>
							</a>
						</li>
					</ul>
					<div class="tab-content mx-1">
						<div class="tab-pane p-2 active" id="rvm-general-ctnt">
							<div class="form-row mt-3">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											기기ID
										</label>
										<input name="deviceID" type="text" style="text-transform: uppercase;" maxlength="8" class="form-control">
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											서비스 유형
										</label>
										<select name="serviceType" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
											<option value="M" data-icon="fa-regular fa-monitor-waveform fa-fw text-blue mr-1">집중 감시</option>
											<option value="S" data-icon="fa-regular fa-lightbulb-on fa-fw text-blue mr-1">서비스</option>
											<option value="I" data-icon="fa-regular fa-truck fa-fw mr-1">설치</option>
											<option value="T" data-icon="fa-regular fa-vial fa-fw mr-1">테스트</option>
											<option value="P" data-icon="fa-regular fa-bullhorn fa-fw mr-1">홍보</option>
											<option value="C" data-icon="fa-regular fa-pause-circle fa-fw text-danger mr-1">임시중단</option>
											<option value="R" data-icon="fa-regular fa-truck fa-fw text-danger mr-1">철거</option>
										</select>
									</div>
								</div>
							</div>
							<div class="form-row">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											유효시작일
											<span class="text-danger">*</span>
										</label>
										<input name="effectiveStartDate" type="text" class="form-control required">
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											유효종료일
										</label>
										<input name="effectiveEndDate" type="text" class="form-control">
									</div>
								</div>
							</div>
							<div class="form-row">
								<div class="col-sm-12">
									<div class="form-group col">
										<label class="form-label">
											메모
										</label>
										<input name="memo" type="text" class="form-control">
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane p-2 fade" id="rvm-plus-info-ctnt">
							<div class="form-row mt-3">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											기기일련번호
										</label>
										<input name="serialNo" type="text" class="form-control">
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											지역 코드
										</label>
										<input name="areaCode" type="text" class="form-control">
										</select>
									</div>
								</div>
							</div>
							<div class="form-row">

								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											지점 코드
										</label>
										<input name="branchCode" type="text" class="form-control">
									</div>
								</div>
																<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											지점명
										</label>
										<input name="branchName" type="text" class="form-control">
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane p-2 fade" id="rvm-option-ctnt">
							<div class="form-row mt-3">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											보고 주기
										</label>
										<select name="reportInterval" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
											<option value="1">1분</option>
											<option value="5">5분</option>
											<option value="30">30분</option>
											<option value="60">1시간</option>
										</select>
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											사용자 정보
										</label>
										<select name="customerIdRequired" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
											<option value="Y" data-icon="fa-regular fa-user-check fa-fw mr-1">확인 후 수거</option>
											<option value="N" data-icon="fa-regular fa-question fa-fw mr-1">확인하지 않음</option>
										</select>
									</div>
								</div>
							</div>
							
							<div class="form-row">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											서버 임포트
										</label>
										<select name="importRequired" class="selectpicker bg-white" data-style="btn-default" data-tick-icon="fa-blank" data-none-selected-text="">
											<option value="Y" data-icon="fa-regular fa-check fa-fw mr-1">수행</option>
											<option value="N" data-icon="fa-regular fa-blank fa-fw mr-1">수행 안함</option>
										</select>
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											결과 보고 유형
										</label>
										<select name="resultType" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
											<option value=""></option>
											<option value="c1">c1 - COSMO 일별 일괄</option>
										</select>
									</div>
								</div>
							</div>
						</div>
						<div class="tab-pane p-2 fade" id="rvm-support-ctnt">
							<div class="form-row mt-3">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											장소 연락처
										</label>
										<input name="storeContact" type="text" class="form-control">
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											영업 연락처
										</label>
										<input name="salesContact" type="text" class="form-control">
									</div>
								</div>
							</div>
							<div class="form-row">
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											지도 위도
										</label>
										<input name="rvmLatitude" type="text" class="form-control">
									</div>
								</div>
								<div class="col-sm-6">
									<div class="form-group col">
										<label class="form-label">
											지도 경도
										</label>
										<input name="rvmLongitude" type="text" class="form-control">
									</div>
								</div>
							</div>
						</div>

					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-round btn-outline-secondary mr-auto" onClick="openMap()">
                	<span class="fal fa-map"></span>
                	<span class="ml-1">지도</span>
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>


<div class="modal fade modal-level-plus-1" id="modal-rvm-map" tabindex="-1" role="dialog">
	<div class="modal-dialog modal-dialog-centered modal-lg" role="document">
		<div class="modal-content">
      
			<!-- Modal body -->
			<div class="modal-body mb-0 p-0">
				<div id="map-canvas" style="height:300px;"></div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer d-flex">
				<button type="button" class="btn btn-round btn-outline-secondary mr-auto" onClick="moveToAddressPoint()">
                	<span class="fal fa-map-marked-alt"></span>
                	<span class="ml-1">주소로 이동</span>
				</button>
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='setLatLng()'>현재 위치 설정</button>
			</div>
			
		</div>
	</div>
</div>

<!--  / Forms -->


<!--  Scripts -->

<script>

var currBrowserWidth = 0;
var currTabIdx = 1;

$(document).ready(function() {

	// fire resize event
	window.dispatchEvent(new Event('resize'));

});

window.onresize = function() {

	var browserWidth = window.innerWidth || document.body.clientWidth;
	
	if (browserWidth != currBrowserWidth) {
		currBrowserWidth = browserWidth;
		reRenderGrid();
	}
}

function reRenderGrid() {

	checkColumnView(1, 2, 500);		// 기기ID
	checkColumnView(1, 3, 600);		// 유효시작일
	checkColumnView(1, 4, 1000);	// 유효종료일
	checkColumnView(1, 5, 800);		// 서비스유형
	
	checkColumnView(2, 6, 500);		// 기기일련번호
	checkColumnView(2, 7, 1000);	// 지역 코드
	checkColumnView(2, 8, 700);		// 지점 코드
	checkColumnView(2, 9, 600);		// 지점명
	
	checkColumnView(3, 10, 500);	// 보고 주기
	checkColumnView(3, 11, 600);	// 사용자 정보
	checkColumnView(3, 12, 700);	// 서버 임포트
	checkColumnView(3, 13, 1000);	// 결과 보고 유형

}


function checkColumnView(tab, idx, minWidth) {

	var goAhead = currTabIdx == 9 || currTabIdx == tab;
	
	if (goAhead) {
		if (currBrowserWidth >= minWidth) showColumns(idx); else hideColumns(idx);
	}
}


function changeTab(idx) {

	currTabIdx = idx;
	
	setTabItemChecked($("#view-tab-1"), false);
	setTabItemChecked($("#view-tab-2"), false);
	setTabItemChecked($("#view-tab-3"), false);
	setTabItemChecked($("#view-tab-9"), false);

	hideRangeColumns(2, 13);
	
	var title = "";
	if (idx == 1) {
		title = "일반";
		setTabItemChecked($("#view-tab-1"), true);
		
		showRangeColumns(2, 5);
	} else if (idx == 2) {
		title = "추가";
		setTabItemChecked($("#view-tab-2"), true);
		
		showRangeColumns(6, 9);
	} else if (idx == 3) {
		title = "옵션";
		setTabItemChecked($("#view-tab-3"), true);
		
		showRangeColumns(10, 13);
	} else if (idx == 9) {
		title = "전체";
		setTabItemChecked($("#view-tab-9"), true);
		
		showRangeColumns(2, 13);
	}
	
	$("#curr-tab-name").text(title);

	reRenderGrid();
}

</script>


<script>

var isFirstRead = true;


$(document).ready(function() {

	$("input[name='view-mode-switch']").prop("checked", "${sessionScope['userCookie'].viewCodeRvm}" == "E");
	
	$("input[name='view-mode-switch']").change(function() {
		$("#grid").data("kendoGrid").dataSource.read();
	});
	
	$("#modal-rvm-map").on('show.bs.modal', function (e) {
		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-plus-1');
		});
	});
});


function initForm1(subtitle, rvmId) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 input[name='rvmLatitude']").val(${value_lat});
	$("#form-1 input[name='rvmLongitude']").val(${value_lng});
	
	$("#form-1 select[name='serviceType']").selectpicker('render');
	$("#form-1 select[name='reportInterval']").selectpicker('render');
	$("#form-1 select[name='customerIdRequired']").selectpicker('render');
	$("#form-1 select[name='importRequired']").selectpicker('render');
	$("#form-1 select[name='resultType']").selectpicker('render');

	bootstrapSelectVal($("#form-1 select[name='serviceType']"), "I");
	bootstrapSelectVal($("#form-1 select[name='reportInterval']"), "1");
	bootstrapSelectVal($("#form-1 select[name='customerIdRequired']"), "N");
	bootstrapSelectVal($("#form-1 select[name='importRequired']"), "Y");
	bootstrapSelectVal($("#form-1 select[name='resultType']"), "");
	
	
	$("#form-1 input[name='effectiveStartDate']").kendoDatePicker({	
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(),
	});
	
	$("#form-1 input[name='effectiveEndDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
	});
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	
	$("#form-1").validate({
		ignore: "",
		rules: {
			rvmName: {
				minlength: 2, maxlength: 50,
			},
			deviceID: { alphanumeric: true },
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
		}
	});

}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-1 input[name='effectiveEndDate']"));

	if ($("#form-1").valid()) {
    	var data = {
    		//
    		id: Number($("#form-1").attr("rowid")),
    		rvmName: $.trim($("#form-1 input[name='rvmName']").val()),
    		//
    		deviceID: $.trim($("#form-1 input[name='deviceID']").val()).toUpperCase(),
    		memo: $.trim($("#form-1 input[name='memo']").val()),
    		//
    		serialNo: $.trim($("#form-1 input[name='serialNo']").val()),
    		areaCode: $.trim($("#form-1 input[name='areaCode']").val()),
    		branchCode: $.trim($("#form-1 input[name='branchCode']").val()),
    		branchName: $.trim($("#form-1 input[name='branchName']").val()),
    		//
    		storeContact: $.trim($("#form-1 input[name='storeContact']").val()),
    		salesContact: $.trim($("#form-1 input[name='salesContact']").val()),
    		rvmLatitude: $.trim($("#form-1 input[name='rvmLatitude']").val()),
    		rvmLongitude: $.trim($("#form-1 input[name='rvmLongitude']").val()),
    		//
    		//
    		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
    		//
    		//
    		serviceType: $("#form-1 select[name='serviceType']").val(),
    		reportInterval: $("#form-1 select[name='reportInterval']").val(),
    		customerIdRequired: $("#form-1 select[name='customerIdRequired']").val(),
    		importRequired: $("#form-1 select[name='importRequired']").val(),
    		resultType: $("#form-1 select[name='resultType']").val(),
    	};

		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-1").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-1").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function edit(id) {
	
	initForm1("변경", id);

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	
	$("#form-1 input[name='rvmName']").val(dataItem.rvmName);
	$("#form-1 input[name='deviceID']").val(dataItem.deviceID);
	$("#form-1 input[name='memo']").val(dataItem.memo);
	//
	$("#form-1 input[name='serialNo']").val(dataItem.serialNo);
	$("#form-1 input[name='areaCode']").val(dataItem.areaCode);
	$("#form-1 input[name='branchCode']").val(dataItem.branchCode);
	$("#form-1 input[name='branchName']").val(dataItem.branchName);
	//
	$("#form-1 input[name='storeContact']").val(dataItem.storeContact);
	$("#form-1 input[name='salesContact']").val(dataItem.salesContact);
	$("#form-1 input[name='rvmLatitude']").val(dataItem.rvmLatitude);
	$("#form-1 input[name='rvmLongitude']").val(dataItem.rvmLongitude);

	
	bootstrapSelectVal($("#form-1 select[name='serviceType']"), dataItem.serviceType);
	bootstrapSelectVal($("#form-1 select[name='reportInterval']"), dataItem.reportInterval);
	bootstrapSelectVal($("#form-1 select[name='customerIdRequired']"), dataItem.customerIdRequired ? "Y" : "N");
	bootstrapSelectVal($("#form-1 select[name='importRequired']"), dataItem.importRequired ? "Y" : "N");
	bootstrapSelectVal($("#form-1 select[name='resultType']"), dataItem.resultType);

	
	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


var marker;
var map;
var geocoder;

function openMap() {
	
	var latitude = $.trim($("#form-1 input[name='rvmLatitude']").val());
	var longitude = $.trim($("#form-1 input[name='rvmLongitude']").val());
	
	var lat = ${value_lat};
	var lng = ${value_lng};
	
	if (latitude && $.isNumeric(latitude)) {
		lat = Number(latitude);
	}

	if (longitude && $.isNumeric(longitude)) {
		lng = Number(longitude);
	}
	
	map = new google.maps.Map(
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
        
	geocoder = new google.maps.Geocoder();
	
	$("#modal-rvm-map").modal();
}


function moveToAddressPoint() {
	
// 	var localCode = $("#form-1 select[name='localName']").val();
	var localName = $("#form-1 select[name='localName']>option:selected").html();
	var address = $.trim($("#form-1 input[name='address']").val());
	
	var fullAddr = "";
	
// 	if (localCode != "-1") {
// 		fullAddr = address ? localName + " " + address : localName;
// 	}
	
	if (fullAddr && geocoder) {
	    geocoder.geocode( { 'address': fullAddr}, function(results, status) {
	        if (status == google.maps.GeocoderStatus.OK) {
	        	map.setCenter(results[0].geometry.location);
	    		marker.setPosition(results[0].geometry.location);
	        }
	    });
	}
}


function setLatLng() {
	
	if (marker) {
		$("#form-1 input[name='rvmLatitude']").val(marker.position.lat());
		$("#form-1 input[name='rvmLongitude']").val(marker.position.lng());
		
		$("#modal-rvm-map").modal("hide");
	}
}

function getRvmTagRows(allTags) {

	var ret = "";
	
	if (allTags.length > 0) {
		for(var i in allTags) {
			ret = ret + "<tr>";
			ret = ret + "	<td class='text-nowrap align-middle p-1'>";
			ret = ret + "		<label class='custom-control custom-checkbox mb-0'>";
			ret = ret + "			<input type='checkbox' class='custom-control-input' name='chkRvmOpTag' value='" + allTags[i].tagValue + "'>";
			ret = ret + "			<span class='custom-control-label'>";
			ret = ret + "				" + getRvmTagHtml(allTags[i].color, null, allTags[i].ukid);
			ret = ret + "			</span>";
			ret = ret + "		</label>";
			ret = ret + "	</td>";
			ret = ret + "	<td class='w-100 align-middle small p-1'>";
			ret = ret + "		" + allTags[i].description;
			ret = ret + "	</td>";
			ret = ret + "</tr>";
		}
	} else {
		ret = "<div class='d-flex align-items-center justify-content-center py-4'>표시할 자료가 없습니다.</div>";
	}
	
	return ret;
}

function getRvmTagHtml(color, title, text) {
	var ret = "<span class=\"badge tag mr-1 ";
	
	if (color == "B") { ret += "bg-blue"; }
	else if (color == "R") { ret += "bg-red"; }
	else if (color == "O") { ret += "bg-orange"; }
	else if (color == "Y") { ret += "bg-yellow"; }
	else if (color == "G") { ret += "bg-green"; }
	else if (color == "P") { ret += "bg-purple"; }
	else if (color == "A") { ret += "bg-gray"; }
	else if (color == "E") { ret += "bg-emerald"; }
	else if (color == "N") { ret += "bg-navy"; }
	
	if (title) {
		ret = ret + "\" data-toggle=\"tooltip\" title=\"" + title;
	}
	
	ret = ret + "\">" + text + "</span>";
	
	return ret;
}

function getRvmOpTagValues() {
	
	var ret = "|";
    $("#rvm-tag-ctnt input[name='chkRvmOpTag']:checked:enabled").each(function () {
    	ret = ret + $(this).val() + "|";
    });
    
    if (ret.length < 3) {
    	ret = "";
    } else {
    	ret = ret.substring(1, ret.length - 1);
    }
    
    return ret;
}

</script>


<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:ecoRvmOverview />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
