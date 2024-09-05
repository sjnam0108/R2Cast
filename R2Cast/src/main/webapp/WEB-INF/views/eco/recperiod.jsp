<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/recperiod/readTrxResult" var="readUrl" />
<c:url value="/eco/recperiod/excel" var="excelUrl" />

<c:url value="/eco/common/readACRvms" var="readRvmUrl" />


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

<div class="card mb-4">
	<h6 class="card-header with-elements">
		<span class="fa-duotone fa-calendar-alt fa-lg" style="--fa-primary-color: mediumseagreen;"></span>
		<span id="period" class="pl-2"></span>
		<div class="card-header-elements ml-auto">
			<button id="excel-btn" type="button" class="btn btn-round btn-outline-secondary btn-sm">
				<span class="fa-light fa-file-excel"></span>
				<span class="pl-1">엑셀</span>
			</button>
		</div>
	</h6>
	<div class="card-body">
			<div class="form-row">
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							종료일
						</label>
						<input id="datepicker" class="form-control" />
					</div>
				</div>
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="custom-control custom-checkbox mb-1">
							<input type="checkbox" id="compareCheck" class="custom-control-input">
							<span class="custom-control-label form-label">아래 RVM과 비교:</span>
						</label>
						<input id="compareRvm" type="text" class="form-control">
					</div>
				</div>
			</div>
	</div>
	<hr class="m-0">
	<div class="row no-gutters row-bordered row-border-light">
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-album fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">총 RVM 기기수</div>
					<div class="text-large"><span id="maxRvmCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-bottle-water fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">총 수거 용기수</div>
					<div class="text-large"><span id="totalEmptiesCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-bottle-water fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">평균 수거 용기수</div>
					<div class="text-large"><span id="avgEmptiesCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-receipt fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">총 영수증 건수</div>
					<div class="text-large"><span id="totalReceiptCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-receipt fa-3x text-gray"></span>
				<div class="ml-3">
					<div class="text-muted small">평균 영수증 건수</div>
					<div class="text-large"><span id="avgReceiptCount"></span></div>
				</div>
			</div>
		</div>
		<div class="col-sm-6 col-md-4 col-xl-2">
			<div class="d-flex align-items-center container-p-x py-4">
				<span class="fa-thin fa-receipt fa-3x text-gray" id="compare-rvm"></span>
				<div class="ml-3">
					<div class="text-muted small">대상 기기 영수증 건수</div>
					<div class="text-large"><span id="rvmAvgReceiptCount"></span></div>
				</div>
			</div>
		</div>
	</div>
	<hr class="m-0">
	<div class="card-body">
		<div id="chart-container">
			<div id="chart" style="height: 350px;"></div>
		</div>
	</div>
</div>

<!--  / HTML tags -->


<!--  Scripts -->

<script>

var baseDate = null;

var collectDates = new Array();
var receiptAvgCounts = new Array();
var emptiesAvgCounts = new Array();
var rvmReceiptCounts = new Array();

var prevWidth = null;


$(document).ready(function() {
	
	$("#datepicker").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd", "yyyyMMdd",
		],
		max: new Date(),
		change: onDatePickerChange,
	});

	$("#compareRvm").kendoAutoComplete({
        dataTextField: "rvmName",
        dataSource: {
		    serverFiltering: true,
			transport: {
				read: {
				    dataType: "json",
				    url: "${readRvmUrl}",
				    type: "POST",
				    contentType: "application/json",
					data: JSON.stringify({}),
				},
				parameterMap: function (options) {
                	return JSON.stringify(options);
				},
			},
			error: function(e) {
      			showReadErrorMsg();
			}
        },
        change: function(e) {
        	readData();
        },
        enable: false,
        height: 400,
        delay: 500,
        minLength: 1,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
    
	$("#excel-btn").click(function(e) {
		e.preventDefault();
		
		var compareCheck = $("#compareCheck").is(':checked');

		var date = $("#datepicker").data("kendoDatePicker").value();
		var data = {
			reqStrValue1: date == null ? new Date() : date,
			reqStrValue2: compareCheck ? $.trim($("#compareRvm").val()) : ""
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
	
    $("#compareCheck").click(function() {
    	
    	$("#compareRvm").data("kendoAutoComplete").enable($(this).is(":checked"));
    	
    	if($.trim($("#compareRvm").val())) {
    		readData();
    	}
    });
	
    readData();
    
});


$(window).resize(function(){
	
	var chart = $("#chart").data("kendoChart");
	if (chart != null) {
		if ($("#chart-container").width() != prevWidth) {
			prevWidth = $("#chart-container").width();
			
			chart.setOptions({chartArea: {width: prevWidth}});
		}
	}
	
});


function readData() {

	// kendo datepicker validation
	validateKendoDateValue($("#datepicker"));
	
	var compareCheck = $("#compareCheck").is(':checked');
	
	$("#period").text("");
	$("#maxRvmCount").text("");
	$("#totalEmptiesCount").text("");
	$("#avgEmptiesCount").text("");
	$("#totalReceiptCount").text("");
	$("#avgReceiptCount").text("");
	$("#rvmAvgReceiptCount").text("");
	
	$("#compare-rvm").removeClass("fa-thin fa-receipt fa-3x text-red").addClass("fa-thin fa-receipt fa-3x text-gray");
	
	showWaitModal();
	
	var date = $("#datepicker").data("kendoDatePicker").value();
	var data = {
		reqStrValue1: date == null ? new Date() : date,
		reqStrValue2: compareCheck ? $.trim($("#compareRvm").val()) : ""
	};
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readUrl}",
		data: JSON.stringify(data),
		success: function (data, status) {
			collectDates = new Array();
			receiptAvgCounts = new Array();
			emptiesAvgCounts = new Array();
			rvmReceiptCounts = new Array();
			
			for(var i in data.dates) {
				collectDates.push(data.dates[i]);
				receiptAvgCounts.push(data.receiptAvgCounts[i]);
				emptiesAvgCounts.push(data.emptiesAvgCounts[i]);
				rvmReceiptCounts.push(data.rvmReceiptCounts[i]);
				
				baseDate = data.dates[i];
			}
			
			$("#datepicker").data("kendoDatePicker").value(baseDate);
			
			if (data.rvmCompareMode) {
				$("#rvmAvgReceiptCount").text(data.summaryRvmReceiptAvgCount);
				$("#compare-rvm").removeClass("fa-thin fa-receipt fa-3x text-gray").addClass("fa-thin fa-receipt fa-3x text-red");
				createCompareChart();
			} else {
				$("#rvmAvgReceiptCount").text("N/A");
				createChart();
			}
			
			$("#period").text(data.summaryPeriod);
			$("#maxRvmCount").text(data.maxRvmCount);
			$("#totalEmptiesCount").text(data.summaryEmptiesCount);
			$("#avgEmptiesCount").text(data.summaryEmptiesAvgCount);
			$("#totalReceiptCount").text(data.summaryReceiptCount);
			$("#avgReceiptCount").text(data.summaryReceiptAvgCount);
			
			hideWaitModal();
		},
		error: function(e) {
			hideWaitModal();
			showReadErrorMsg();
		}
	});
}


function createChart() {
	
    $("#chart").kendoChart({
    	chartArea: {
    		background: "transparent",
    	},
        legend: {
            position: "bottom",
            labels: {
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        },
        series: [{
            type: "line",
            data: receiptAvgCounts,
            name: "평균 영수증 건수",
            color: "#007eff",
            axis: "receiptCount",
        }, {
            type: "area",
            data: emptiesAvgCounts,
            name: "평균 수거 용기수",
            color: "#73c100",
            axis: "emptiesCount",
        }],
        valueAxes: [{
            name: "receiptCount",
            color: "#007eff",
            labels: {
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        }, {
            name: "emptiesCount",
            color: "#73c100",
            labels: {
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        }],
        categoryAxis: {
            categories: collectDates,
            axisCrossingValues: [0, 35],
            justified: true,
            type: "date",
            baseUnit: "days",
            labels: {
            	step: 4,
            	skip: 2,
            	dateFormats: {
            		days: "yyyy-MM-dd",
            	},
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            },
            majorGridLines: {
            	step: 4,
            	skip: 2,
            },
        },
        tooltip: {
            visible: true,
            format: "{0}",
            template: "#= kendo.toString(category, 'yyyy-MM-dd') #:<br/> #= value #",
			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
        },
    });
}


function createCompareChart() {
    $("#chart").kendoChart({
    	chartArea: {
    		background: "transparent",
    	},
        legend: {
            position: "bottom",
            labels: {
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        },
        series: [{
            type: "line",
            data: receiptAvgCounts,
            name: "평균 영수증 건수",
            color: "#007eff",
            axis: "receiptCount"
        }, {
            type: "line",
            data: rvmReceiptCounts,
            color: "#ff1c1c",
            axis: "receiptCount",
            visibleInLegend: false,
        }, {
            type: "area",
            data: emptiesAvgCounts,
            name: "평균 수거 용기수",
            color: "#73c100",
            axis: "emptiesCount"
        }],
        valueAxes: [{
            name: "receiptCount",
            color: "#007eff",
            labels: {
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        }, {
            name: "emptiesCount",
            color: "#73c100",
            labels: {
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            }
        }],
        categoryAxis: {
            categories: collectDates,
            axisCrossingValues: [0, 35],
            justified: true,
            type: "date",
            baseUnit: "days",
            labels: {
            	step: 4,
            	skip: 2,
            	dateFormats: {
            		days: "yyyy-MM-dd",
            	},
    			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
            },
            majorGridLines: {
            	step: 4,
            	skip: 2,
            }
        },
        tooltip: {
            visible: true,
            format: "{0}",
            template: "#= kendo.toString(category, 'yyyy-MM-dd') #:<br/> #= value #",
			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
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


<!-- / Page body -->



<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
