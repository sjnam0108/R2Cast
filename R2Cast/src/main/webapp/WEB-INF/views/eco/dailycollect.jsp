<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/dailycollect/read" var="readUrl" />
<c:url value="/eco/dailycollect/readDailyResult" var="readSummaryUrl" />
<c:url value="/eco/dailycollect/excel" var="excelUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<func:ecoRvmStatusFeedBar />





<!-- Page body -->


<!--  HTML tags -->

<div class="card">
	<h6 class="card-header with-elements">
		<span class="fa-duotone fa-calendar-alt fa-lg" style="--fa-primary-color: mediumseagreen;"></span>
		<span id="summaryTitle" class="pl-2"></span>
		<div class="card-header-elements ml-auto">
			<button id="excel-btn" type="button" class="btn btn-round btn-outline-secondary btn-sm">
				<span class="fa-light fa-file-excel"></span>
				<span class="pl-1">엑셀</span>
			</button>
		</div>
	</h6>
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-xl-8">
			<div class="px-3">
				<div class="form-group col pt-2 pb-0 px-3">
					<label class="form-label">
						수거일
					</label>
					<input id="datepicker" class="form-control" />
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-album fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">수거 RVM 기기</div>
					<div class="text-large"><span id="rvmCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-cubes-stacked fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">용기 유형 수</div>
					<div class="text-large"><span id="emptiesTypeCount"></span></div>
				</div>
			</div>
		</div>
	</div>
	<hr class="m-0">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-bottle-water fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">총 수거 용기수</div>
					<div class="text-large"><span id="emptiesCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-bottle-water fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">평균 수거 용기수</div>
					<div class="text-large"><span id="emptiesAvgCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-receipt fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">총 영수증 건수</div>
					<div class="text-large"><span id="receiptCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-receipt fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">평균 영수증 건수</div>
					<div class="text-large"><span id="receiptAvgCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-coin-blank fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">총 영수증 금액</div>
					<div class="text-large"><span id="receiptAmount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-coin-blank fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">평균 영수증 금액</div>
					<div class="text-large"><span id="receiptAvgAmount"></span></div>
				</div>
			</div>
		</div>
	</div>
</div>

<!--  / HTML tags -->


<!--  Scripts -->

<script>

var baseDate = null;


$(document).ready(function() {
	
	$("#datepicker").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd", "yyyyMMdd",
		],
		max: new Date(),
		change: onDatePickerChange,
	});

	$("#excel-btn").click(function(e) {
		e.preventDefault();
		
		var compareCheck = $("#compareCheck").is(':checked');

		var date = $("#datepicker").data("kendoDatePicker").value();
		var data = {
			reqStrValue1: date == null ? new Date() : date,
		};
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${excelUrl}",
			data: JSON.stringify(data),
			complete: function (data, status) {
				location.href = "/export?type=excel";
			},
			error: function(e) {
				showOperationErrorMsg();
			}
		});
	});
	
    readData();
    
});


function readData() {

	// kendo datepicker validation
	validateKendoDateValue($("#datepicker"));
	
	$("#summaryTitle").text("");
	$("#rvmCount").text("");
	$("#emptiesTypeCount").text("");
	
	$("#emptiesCount").text("");
	$("#emptiesAvgCount").text("");
	$("#receiptCount").text("");
	$("#receiptAvgCount").text("");
	$("#receiptAmount").text("");
	$("#receiptAvgAmount").text("");

	
	showWaitModal();
	
	var date = $("#datepicker").data("kendoDatePicker").value();
	var data = {
		reqStrValue1: date == null ? new Date() : date,
	};
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readSummaryUrl}",
		data: JSON.stringify(data),
		success: function (data, status) {
			baseDate = data.date;
			
			$("#datepicker").data("kendoDatePicker").value(baseDate);
			
			$("#summaryTitle").text(data.summaryTitle);
			$("#rvmCount").text(data.summaryRvmCount);
			$("#emptiesTypeCount").text(data.summaryEmptiesTypeCount);
			
			$("#emptiesCount").text(data.summaryEmptiesCount);
			$("#emptiesAvgCount").text(data.summaryEmptiesAvgCount);
			$("#receiptCount").text(data.summaryReceiptCount);
			$("#receiptAvgCount").text(data.summaryReceiptAvgCount);
			$("#receiptAmount").text(data.summaryReceiptAmount);
			$("#receiptAvgAmount").text(data.summaryReceiptAvgAmount);
			
			hideWaitModal();
			
			$("#grid").data("kendoGrid").dataSource.read();
		},
		error: function(e) {
			hideWaitModal();
			showReadErrorMsg();
		}
	});
}


function onDatePickerChange(e) {
	var value = e.sender.value();
	
	if (value == null) {
		e.sender.value(baseDate);
	}
	
	baseDate = e.sender.value();
	
	readData();
}

</script>

<!--  / Scripts -->


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" autoBind="false" >
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-columns>
		<kendo:grid-column title="RVM명" field="rvmName"
				template="<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvmName #</a><a href='javascript:showRvm(#= id #,\x22#= rvmName #\x22)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-search text-info'></span></a></div>" />
		<kendo:grid-column title="영수증 건수" field="receiptCount" filterable="false" template="#= dispReceiptCount #" />
		<kendo:grid-column title="수거 용기수" field="emptiesCount" filterable="false" template="#= dispEmptiesCount #"  />
		<kendo:grid-column title="영수증 총액" field="receiptAmount" filterable="false" template="#= dispReceiptAmount #" />
		<kendo:grid-column title="마지막 영수증" field="lastReceiptNo" filterable="false" template="#= dispLastReceiptNo #" />
		<kendo:grid-column title="유형수" field="emptiesTypeCount" filterable="false" template="#= dispEmptiesTypeCount #" />
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
	<kendo:dataSource serverPaging="false" serverSorting="false" serverFiltering="false" serverGrouping="false" error="kendoReadError">
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json" >
				<kendo:dataSource-transport-read-data>
					<script>
						function additionalData(e) {
							return { reqStrValue1:  baseDate.slice(0, 10) };
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
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model id="id">
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="id" type="number"/>
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


<!-- / Page body -->




<!-- Functional tags -->

<func:ecoRvmOverview />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
