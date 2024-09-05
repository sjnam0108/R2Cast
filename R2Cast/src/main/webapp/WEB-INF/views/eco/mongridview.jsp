<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/mongridview/read" var="readUrl" />
<c:url value="/eco/mongridview/create" var="createUrl" />

<c:url value="/eco/common/readRvmModels" var="readCmnRvmModelUrl" />
<c:url value="/eco/common/readRvmServiceTypes" var="readCmnServiceTypeUrl" />
<c:url value="/eco/common/readRvmStatusTypes" var="readCmnStatusTypeUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<func:ecoRvmStatusFeedBar />





<!-- Page body -->




<!-- Java(optional)  -->

<%
%>

<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
		<div class="d-flex">
			<div>
				<div class="btn-group d-none d-sm-inline">
					<button class="btn btn-secondary dropdown-toggle" type="button" data-toggle="dropdown">
						<span class="fas fa-eyes"></span>
						<span class="pl-1" id="curr-tab-name">일반</span>
					</button>
					<div class="dropdown-menu">
						<a class="dropdown-item" href="javascript:changeTab(1)">
							<span class="far fa-check" id="view-tab-1"></span>
							<span class="pl-1">일반</span>
						</a>
						<a class="dropdown-item" href="javascript:changeTab(2)">
							<span class="fas fa-blank" id="view-tab-2"></span>
							<span class="pl-1">수거</span>
						</a>
						<a class="dropdown-item" href="javascript:changeTab(3)">
							<span class="fas fa-blank" id="view-tab-2"></span>
							<span class="pl-1">추가</span>
						</a>
	
	<c:if test="${not isMobileMode}">
	
						<a class="dropdown-item" href="javascript:changeTab(9)">
							<span class="fas fa-blank" id="view-tab-9"></span>
							<span class="pl-1">전체</span>
						</a>
	
	</c:if>
	
					</div>
				</div>
				<button type="button" class="btn btn-default d-none d-md-inline k-grid-excel">엑셀</button>
			</div>
			<div class="ml-auto">
				<div class="d-flex align-items-center">
					<span class="d-none d-md-inline">선택 항목에 대해</span>
					<span style="width: 200px;">
						<select name="cmdItem" class="selectpicker pl-2 pr-1" data-style="btn-default" data-none-selected-text="">
							<optgroup label="동작">
								<option value="Reboot.bbmc">STB 재시작(리부팅)</option>
								<option value="RestartAgent.bbmc">에이전트 재시작</option>
								<option value="UpdateAgent.bbmc">에이전트 업데이트</option>
							</optgroup>
							<optgroup label="업로드">
								<option value="UploadDebugFile.bbmc">디버그 로그 업로드</option>
								<option value="UploadTrxFile.bbmc">원시 트랜잭션 업로드</option>
							</optgroup>
						</select>
					</span>
	    			<button id="proc-btn" type="button" class="btn btn-default">진행</button>
				</div>
				
			</div>
		
    				
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column width="80" title="상태" field="lastStatus"
				template="<div class='d-flex align-items-center'><span class='flag-18 sts-18-#= lastStatus #'></span></div>" >
			<kendo:grid-column-filterable multi="true">
				<kendo:grid-column-filterable-itemTemplate>
					<script>
						function (e) {
							if (e.field == "all") {
								return "<div class='mt-3 mx-3' style='min-width:150px;'>" +
										"  <label class='custom-control custom-checkbox'>" +
										"    <input type='checkbox' class='custom-control-input'>" +
										"    <span class='custom-control-label'>#= all #</span>" +
										"  </label>" +
										"</div>";
							} else {
								return "<div class='mx-3'>" +
										"  <label class='custom-control custom-checkbox'>" +
										"    <input type='checkbox' class='custom-control-input' value='#= value #'>" +
										"    <span class='custom-control-label d-flex align-items-center'><span class='#= icon #'></span> <span class='pl-1'>#= text #</span></span>" +
										"  </label>" +
										"</div>";
							}
						}
					</script>
				</kendo:grid-column-filterable-itemTemplate>
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCmnStatusTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="RVM" field="rvmName"
				template="<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvmName #</a><a href='javascript:showRvm(#= id #,\x22#= rvmName #\x22)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a></div>" />
		<kendo:grid-column title="운행시간(분)" field="runningMinCount" format="{0:n0}"
				template="#= kendo.toString(runningMinCount, '\\#\\#,\\#') #"/>
		<kendo:grid-column title="최근 보고" field="rvmLastReport.whoLastUpdateDate"
				template="#=rvmLastReport == null ? \"\" : rvmLastReport.lastReportTime #" />
		<kendo:grid-column title="에이전트 시작" field="rvmLastReport.agentStartDate"
				template="#=rvmLastReport == null ? \"\" : rvmLastReport.agentStart #" />
		<kendo:grid-column title="버전" field="rvmLastReport.appVersion"
				template="#=rvmLastReport == null ? \"\" : rvmLastReport.appVersion #" />

		<kendo:grid-column title="서비스?" field="serviceType"
				template="#=(serviceType == 'S' || serviceType == 'M') ? \"<span class='far fa-check'>\" : \"\"#" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCmnServiceTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>

		<kendo:grid-column hidden="true" filterable="false" title="최근 Trx번호" field="lastTrxNo" />
		<kendo:grid-column hidden="true" filterable="false" title="최근 영수증번호" field="lastReceiptNo" />
		<kendo:grid-column hidden="true" filterable="false" title="최근 수거" field="lastOpDt" template="#= lastOpDt #" />
		<kendo:grid-column hidden="true" filterable="false" title="누적 Trx수" field="cumTrxCount" />
		<kendo:grid-column hidden="true" filterable="false" title="누적 용기수" field="cumEmptiesCount" />
		<kendo:grid-column hidden="true" filterable="false" title="누적 영수증총액" field="cumReceiptAmount" />

		<kendo:grid-column hidden="true" title="보고IP" field="rvmLastReport.reportIp"
				template="#=rvmLastReport == null ? \"\" : rvmLastReport.reportIp #" />
		<kendo:grid-column hidden="true" title="디스크크기(GB)" field="rvmLastReport.diskSize"
				template="#=rvmLastReport == null || rvmLastReport.diskSize == null ? \"\" : rvmLastReport.diskSize #" />
		<kendo:grid-column hidden="true" title="디스크여유(GB)" field="rvmLastReport.diskFree"
				template="#=rvmLastReport == null || rvmLastReport.diskFree == null ? \"\" : rvmLastReport.diskFree #" />
		<kendo:grid-column hidden="true" title="디스크사용율(%)" field="rvmLastReport.diskUsedRatio"
				template="#=rvmLastReport == null || rvmLastReport.diskUsedRatio == null ? \"\" : rvmLastReport.diskUsedRatio #" />
                
		<kendo:grid-column hidden="true" title="모델" field="model" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcTextOnly">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCmnRvmModelUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
        
	</kendo:grid-columns>
	<kendo:grid-filterable extra="false">
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				attachRvmStatusBarPopover();
				$('[data-toggle="tooltip"]').tooltip();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="rvmName" dir="asc"/>
		</kendo:dataSource-sort>
		
<c:if test="${not empty requestScope['value_defaultStatus']}">

		<kendo:dataSource-filter>
			<kendo:dataSource-filterItem field="lastStatus" operator="startswith" logic="and" value="${value_defaultStatus}" />
		</kendo:dataSource-filter>

</c:if>
		
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json"/>
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
					<kendo:dataSource-schema-model-field name="runningMinCount" type="number" />
					<kendo:dataSource-schema-model-field name="lastStatus" type="string" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

<!-- / Kendo grid  -->


<!--  Styles -->

<style>

/* Kendo 그리드에서 bootstrap dropdownmenu 모두 보이게 */
.k-grid td {
	overflow: visible;
}


/* 상태별 아이콘 */
.flag-18 {
	background-image: url('/resources/shared/images/color_flags.png');
	background-color: transparent;
	opacity: 1;
	display: inline-block;
	width: 18px;
	height: 18px;
	overflow: hidden;
	background-repeat: no-repeat;
}
.sts-18-0 { background-position: 0px -40px; }
.sts-18-2 { background-position: -18px -40px; }
.sts-18-3 { background-position: -36px -40px; }
.sts-18-4 { background-position: -54px -40px; }
.sts-18-5 { background-position: -72px -40px; }
.sts-18-6 { background-position: -90px -40px; }
.sts-18-9 { background-position: -108px -40px; }


/* 선택 체크박스를 포함하는 필터 패널을 보기 좋게 */
.k-filter-selected-items {
	font-weight: 500;
	margin: 0.5em 0;
}
.k-filter-menu .k-button {
	width: 47%;
	margin: 0.5em 1% 0.25em;
}

</style>

<!--  / Styles -->


<!-- Grid button actions  -->

<script>
$(document).ready(function() {

	// Proceed
	$("#proc-btn").click(function(e) {
		e.preventDefault();
		
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();

		var selRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			selRows.push(selectedItem.id);
		});
		
		var val = $("select[name='cmdItem']").val();

		if (selRows.length > 0 && val != "") {
			
   			var cancelDate = new Date();
	   		cancelDate.setHours(24, 0, 0, 0);
	    		
	    	var data = {
	    		command: val,
	    		execTime: "",
	    		destDate: new Date(),
	    		cancelDate: cancelDate,
	    		rvmIds: selRows,
	    	};
	    		
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${createUrl}",
				data: JSON.stringify(data),
				success: function (form) {
					showSaveSuccessMsg();
				},
				error: ajaxSaveError
			});

   		}
	});
	// / Proceed
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Scripts -->

<script>

var currBrowserWidth = 0;
var currTabIdx = 1;

$(document).ready(function() {

	// fire resize event
	window.dispatchEvent(new Event('resize'));
	
	$("select[name='cmdItem']").selectpicker('render');
	bootstrapSelectVal($("select[name='cmdItem']"), "RestartAgent.bbmc");

});

window.onresize = function() {

	var browserWidth = window.innerWidth || document.body.clientWidth;
	
	if (browserWidth != currBrowserWidth) {
		currBrowserWidth = browserWidth;
		reRenderGrid();
	}
}


function reRenderGrid() {

	checkColumnView(1, 2, 500);		// 운행시간(분)
	checkColumnView(1, 3, 800);		// 최근 보고
	checkColumnView(1, 4, 1050);	// 에이전트 시작
	checkColumnView(1, 5, 1350);	// 버전
	checkColumnView(1, 6, 1200);	// 서비스?
	
	checkColumnView(2, 7, 500);		// 최근 트랜잭션 번호
	checkColumnView(2, 8, 750);		// 최근 영수증 번호
	checkColumnView(2, 9, 1200);	// 최근 수거일시
	checkColumnView(2, 10, 1300);	// 누적 트랜잭션 건수
	checkColumnView(2, 11, 1000);	// 누적 수거용기 수
	checkColumnView(2, 12, 1000);	// 누적 영수증 총액
	
	checkColumnView(3, 13, 500);	// 보고IP
	checkColumnView(3, 14, 750);	// 디스크크기(GB)
	checkColumnView(3, 15, 1200);	// 디스크여유(GB)
	checkColumnView(3, 16, 1300);	// 디스크사용율(%)
	checkColumnView(3, 17, 1000);	// 모델

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

	hideRangeColumns(2, 20);
	
	var title = "";
	if (idx == 1) {
		title = "일반";
		setTabItemChecked($("#view-tab-1"), true);
		
		showRangeColumns(2, 6);
	} else if (idx == 2) {
		title = "수거";
		setTabItemChecked($("#view-tab-2"), true);
		
		showRangeColumns(7, 12);
	} else if (idx == 3) {
		title = "추가";
		setTabItemChecked($("#view-tab-3"), true);
		
		showRangeColumns(13, 17);
	} else if (idx == 9) {
		title = "전체";
		setTabItemChecked($("#view-tab-9"), true);
		
		showRangeColumns(2, 17);
	}
	
	$("#curr-tab-name").text(title);

	reRenderGrid();
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
