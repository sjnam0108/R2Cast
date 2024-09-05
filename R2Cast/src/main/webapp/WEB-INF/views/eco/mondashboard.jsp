<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/mondashboard/readServiceResult" var="readUrl" />
<c:url value="/eco/mondashboard/readServiceSimpleResult" var="readStatusbarUrl" />

<c:url value="/eco/moneventreport/read" var="readEventUrl" />
<c:url value="/eco/moneventreport/readCategories" var="readCategoryUrl" />
<c:url value="/eco/moneventreport/readReportTypes" var="readReportTypeUrl" />


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
	String catTemplate = "<span class='fas fa-circle text-" + 
			"# if (category == 'R') { #" + "red" + 
			"# } else if (category == 'O') { #" + "orange" + 
			"# } else if (category == 'Y') { #" + "yellow" + 
			"# } else if (category == 'G') { #" + "green" + 
			"# } else if (category == 'B') { #" + "blue" + 
			"# } else if (category == 'P') { #" + "purple" + 
			"# } #" +
			"'></span>";
	String reportTypeTemplate = "<span class='fas " + 
			"# if (reportType == 'I') { #" + "fa-exclamation fa-fw text-info" + 
			"# } else if (reportType == 'W') { #" + "fa-exclamation-triangle fa-fw text-yellow" + 
			"# } else if (reportType == 'E') { #" + "fa-exclamation-circle fa-fw text-red" + 
			"# } #" +
			"'></span>";
	String rvmTemplate =
			"# if (equipType == 'P') { #" + "<div class='d-flex align-items-center'><a href='javascript:void(0)' class='stb-status-popover' tabindex='0'>#= equipName #</a><a href='javascript:showStb(#= equipId #,\"#= equipName #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a></div>" +
			"# } else { #" + "<span>#= equipName #</span>" +
			"# } #";
	String detailsTemplate = "#:details#";
	String dateTemplate = kr.co.r2cast.utils.Util.getSmartDate();
%>


<!--  HTML tags -->

<div class="row mb-4 mx-0">
	<div class="card">
		<div class="row no-gutters row-bordered row-border-light h-100">
			<div class="d-flex col-sm-6 col-md-4 col-xl-2 align-items-center p-3">
				<span class="flag-34 sts-34-6"></span>
				<span class="media-body d-block ml-3">
					<div class="ellipsis-line-1">정상 동작</div>
					<div class="text-muted small ellipsis-line-2">RVM 기기가 정상적으로 동작중인 상태</div>
					<div class="text-large pt-2"><span id="st6_count">0</span></div>
				</span>
			</div>
			<div class="d-flex col-sm-6 col-md-4 col-xl-2 align-items-center p-3">
				<span class="flag-34 sts-34-5"></span>
				<span class="media-body d-block ml-3">
					<div class="ellipsis-line-1">투입 금지</div>
					<div class="text-muted small ellipsis-line-2">RVM 기기는 켜져있으나, 빈 용기의 투입이 금지된 상태</div>
					<div class="text-large pt-2"><span id="st5_count">0</span></div>
				</span>
			</div>
			<div class="d-flex col-sm-6 col-md-4 col-xl-2 align-items-center p-3">
				<span class="flag-34 sts-34-4"></span>
				<span class="media-body d-block ml-3">
					<div class="ellipsis-line-1">장애 보고</div>
					<div class="text-muted small ellipsis-line-2">RVM 기기에서 특정 오류를 보고하고 있는 상태</div>
					<div class="text-large pt-2"><span id="st4_count">0</span></div>
				</span>
			</div>
			<div class="d-flex col-sm-6 col-md-4 col-xl-2 align-items-center p-3">
				<span class="flag-34 sts-34-3"></span>
				<span class="media-body d-block ml-3">
					<div class="ellipsis-line-1">RVM 꺼짐</div>
					<div class="text-muted small ellipsis-line-2">RVM 기기와 연결이 안되거나, RVM 기기가 꺼져 있는 상태</div>
					<div class="text-large pt-2"><span id="st3_count">0</span></div>
				</span>
			</div>
			<div class="d-flex col-sm-6 col-md-4 col-xl-2 align-items-center p-3">
				<span class="flag-34 sts-34-2"></span>
				<span class="media-body d-block ml-3">
					<div class="ellipsis-line-1">STB 꺼짐</div>
					<div class="text-muted small ellipsis-line-2">금일 동작을 하였으나, 현재는 장비가 꺼진 상태</div>
					<div class="text-large pt-2"><span id="st2_count">0</span></div>
				</span>
			</div>
			<div class="d-flex col-sm-6 col-md-4 col-xl-2 align-items-center p-3">
				<span class="flag-34 sts-34-0"></span>
				<span class="media-body d-block ml-3">
					<div class="ellipsis-line-1">미확인</div>
					<div class="text-muted small ellipsis-line-2">금일 동작을 하지 않았으며, 장비도 꺼져 있는 상태</div>
					<div class="text-large pt-2"><span id="st0_count">0</span></div>
				</span>
			</div>
		</div>
	</div>
</div>


<div class="row">
	<div class="col-md-6 mb-4">
		<div class="card">
			<h6 class="card-header with-elements">
				<div class="card-header-title">
					<span class="fa-duotone fa-signs-post fa-lg" style="--fa-primary-color: mediumseagreen;"></span>
					<span class="pl-1">주요 관심 지표</span>
				</div>
			</h6>
			<div class="table-responsive">
				<table class="table card-table m-0">
					<tbody>
						<tr>
							<td class="text-nowrap align-middle">
								<span class="text-dark">총 기기</span>
							</td>
							<td class="w-100 align-middle">
							</td>
							<td class="text-nowrap align-middle">
								<span id="running_count" class="pr-2">0</span>
								<span class="text-muted small">100</span> %
							</td>
						</tr>
                        <tr>
							<td class="text-nowrap align-middle">
								<span class="text-dark">현재 동작 중</span>
							</td>
							<td class="align-middle">
								<div class="progress" style="height: 7px;">
									<div class="progress-bar" id="playing-prog" style="background-color: #8897AA;"></div>
								</div>
							</td>
							<td class="text-nowrap align-middle">
								<span id="playing-count" class="pr-2">0</span>
								<span id="playing-pct" class="text-muted small"></span> %
							</td>
                        </tr>
                        <tr>
							<td class="text-nowrap align-middle">
								<span class="text-dark">금일 동작 수행</span>
							</td>
							<td class="align-middle">
								<div class="progress" style="height: 7px;">
									<div class="progress-bar" id="today-playing-prog" style="background-color: #8897AA;"></div>
								</div>
							</td>
							<td class="text-nowrap align-middle">
								<span id="today-playing-count" class="pr-2">0</span>
								<span id="today-playing-pct" class="text-muted small"></span> %
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
	<div class="col-md-6 mb-4">
		<div class="card">
			<h6 class="card-header with-elements">
				<div class="card-header-title">
					<span class="fa-duotone fa-monitor-waveform fa-lg" style="--fa-primary-color: mediumseagreen;"></span>
					<span class="pl-1">주요 기기 상태</span>
				</div>
			</h6>
			<div class="table-responsive">
				<table class="table card-table m-0">
					<tbody>
						<tr>
							<td class="text-nowrap align-middle">
								<span id="status-title-1" class="text-dark"></span>
							</td>
							<td class="w-100 align-middle">
								<div class="progress" style="height: 7px;">
									<div class="progress-bar" id="status-prog-1"></div>
								</div>
							</td>
							<td class="text-nowrap align-middle">
								<span id="status-count-1" class="pr-2">0</span>
								<span id="status-pct-1" class="text-muted small"></span> %
							</td>
						</tr>
                        <tr>
							<td class="text-nowrap align-middle">
								<span id="status-title-2" class="text-dark"></span>
							</td>
							<td class="w-100 align-middle">
								<div class="progress" style="height: 7px;">
									<div class="progress-bar" id="status-prog-2"></div>
								</div>
							</td>
							<td class="text-nowrap align-middle">
								<span id="status-count-2" class="pr-2">0</span>
								<span id="status-pct-2" class="text-muted small"></span> %
							</td>
                        </tr>
                        <tr>
							<td class="text-nowrap align-middle">
								<span id="status-title-3" class="text-dark"></span>
							</td>
							<td class="w-100 align-middle">
								<div class="progress" style="height: 7px;">
									<div class="progress-bar" id="status-prog-3"></div>
								</div>
							</td>
							<td class="text-nowrap align-middle">
								<span id="status-count-3" class="pr-2">0</span>
								<span id="status-pct-3" class="text-muted small"></span> %
							</td>
						</tr>
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>


<style>

/* 상태별 아이콘 */
.flag-34 {
	background-image: url('/resources/shared/images/color_flags.png');
	background-color: transparent;
	opacity: 1;
	display: inline-block;
	width: 34px;
	height: 34px;
	overflow: hidden;
	background-repeat: no-repeat;
}
.sts-34-0 { background-position: 0px 0px; }
.sts-34-2 { background-position: -34px 0px; }
.sts-34-3 { background-position: -68px 0px; }
.sts-34-4 { background-position: -102px 0px; }
.sts-34-5 { background-position: -136px 0px; }
.sts-34-6 { background-position: -170px 0px; }


/* 라인 문자 생략 */
.ellipsis-line-1 {
	display: -webkit-box;
	-webkit-line-clamp: 1;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
}
.ellipsis-line-2 {
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
	overflow: hidden;
	text-overflow: ellipsis;
}

</style>


<div class="mb-4" id="event-grid-div">

<div class="card" style="border-bottom: transparent;">
	<h6 class="card-header with-elements">
		<div class="card-header-title">
			<span class="fa-duotone fa-balloons fa-lg" style="--fa-primary-color: mediumseagreen;"></span>
			<span class="pl-1">장비 이벤트 보고</span>
		</div>
	</h6>
</div>
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="true" reorderable="true" resizable="true" selectable="single" height="250px" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-columns>
		<kendo:grid-column title="범주" field="category" template="<%= catTemplate %>" minScreenWidth="1200" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCategoryUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="유형" field="reportType" template="<%= reportTypeTemplate %>" minScreenWidth="500" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readReportTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="등록일시" field="whoCreationDate" template="<%= dateTemplate %>" />
		<kendo:grid-column title="이벤트" field="event" />
		<kendo:grid-column title="이름" field="equipName" template="<%= rvmTemplate %>" />
		<kendo:grid-column title="상세내용" field="details" template="<%= detailsTemplate %>" minScreenWidth="650" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
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
			<kendo:dataSource-sortItem field="id" dir="desc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-filter>
			<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}">
			</kendo:dataSource-filterItem>
		</kendo:dataSource-filter>
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readEventUrl}" dataType="json" type="POST" contentType="application/json"/>
			<kendo:dataSource-transport-parameterMap>
				<script>
					function parameterMap(options,type) {
						return JSON.stringify(options);	
					}
				</script>
			</kendo:dataSource-transport-parameterMap>
		</kendo:dataSource-transport>
		<kendo:dataSource-schema data="data" total="total" groups="data">
			<kendo:dataSource-schema-model id="id" >
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date"/>
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


/* 그리드의 최소 높이 지정 */
#aaagrid .k-grid-content {
	min-height: 100px;
}

</style>

<!--  / HTML tags -->


<!--  Scripts -->

<script>

$(document).ready(function() {
	
	showWaitModal();
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			$("#st6_count").text(data.workingCount);
			$("#st5_count").text(data.storeClosedCount);
			$("#st4_count").text(data.failureReportedCount);
			$("#st3_count").text(data.rvmOffCount);
			$("#st2_count").text(data.stbOffCount);
			$("#st0_count").text(data.noShowCount);

			$("#running_count").text(data.totalCount);
			
			$("#playing-pct").text(data.playingPct);
			$("#playing-count").text(data.playingCount);
			$("#playing-prog").css("width", data.playingPct + "%");
			
			$("#today-playing-pct").text(data.todayPlayingPct);
			$("#today-playing-count").text(data.todayPlayingCount);
			$("#today-playing-prog").css("width", data.todayPlayingPct + "%");
			
			$("#status-title-1").text(data.statusItemNo1.title);
			$("#status-count-1").text(data.statusItemNo1.countDisp);
			$("#status-pct-1").text(data.statusItemNo1.percent);
			$("#status-prog-1").css("width",data.statusItemNo1.percent + "%");
			$("#status-prog-1").addClass(data.statusItemNo1.color);
			
			$("#status-title-2").text(data.statusItemNo2.title);
			$("#status-count-2").text(data.statusItemNo2.countDisp);
			$("#status-pct-2").text(data.statusItemNo2.percent);
			$("#status-prog-2").css("width",data.statusItemNo2.percent + "%");
			$("#status-prog-2").addClass(data.statusItemNo2.color);
			
			$("#status-title-3").text(data.statusItemNo3.title);
			$("#status-count-3").text(data.statusItemNo3.countDisp);
			$("#status-pct-3").text(data.statusItemNo3.percent);
			$("#status-prog-3").css("width",data.statusItemNo3.percent + "%");
			$("#status-prog-3").addClass(data.statusItemNo3.color);
			
			createSparklineChart1(data.dates, data.stbCounts);
			createSparklineChart2(data.dates, data.runningMins);
			
			hideWaitModal();
		},
		error: function(e) {
			hideWaitModal();
			showReadErrorMsg();
		}
	});
	
});


function createSparklineChart1(dates, data) {
	
	$("#stb-count-chart").kendoSparkline({
		type: "column",
		data: data,
		chartArea: {
			background: "transparent",
			margin: 0,
		},
		categoryAxis: {
			categories: dates,
			type: "date",
			baseUnit: "days",
			labels: {
				dateFormats: {
					days: "yyyy-MM-dd",
				},
			},
		},
		seriesColors: ["#8897AA"],
		tooltip: {
			format: "{0:N0}",
			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
		}
	});		
}


function createSparklineChart2(dates, data) {
	
	$("#running-min-chart").kendoSparkline({
		type: "column",
		data: data,
		chartArea: {
			background: "transparent",
			margin: 0,
		},
		categoryAxis: {
			categories: dates,
			type: "date",
			baseUnit: "days",
			labels: {
				dateFormats: {
					days: "yyyy-MM-dd",
				},
			},
		},
		seriesColors: ["#8897AA"],
		tooltip: {
			format: "{0:N1}",
			font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
		}
	});		
}


$(window).resize(function(){
	var sparkline1 = $("#stb-count-chart").data("kendoSparkline");
	if (sparkline1 != null) {
		sparkline1.setOptions({chartArea: {width: $("#stb-count-chart").width()}});
	}
	
	var sparkline2 = $("#running-min-chart").data("kendoSparkline");
	if (sparkline2 != null) {
		sparkline2.setOptions({chartArea: {width: $("#running-min-chart").width()}});
	}
});

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:ecoRvmOverview />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
