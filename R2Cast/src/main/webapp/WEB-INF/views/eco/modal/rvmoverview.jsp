    <%@ page language="java" contentType="text/html; charset=UTF-8"
        pageEncoding="UTF-8"%>
    
    
    <!-- Taglib -->
    
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
    <%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>
    
    
    <!-- URL -->
    
    <c:url value="/eco/common/readRvmOverview" var="readRvmOverviewUrl" />
    
    
    
    
    
    <!-- Page body -->
    
    
    <!--  Forms -->
    
    <ul class="nav nav-tabs tabs-alt mb-3 pt-0" id="rvm-overview-tabs">
    	<li class="nav-item">
    		<a class="nav-link" data-toggle="tab" id="ovw-basic" href="#overview-basic">
    			<span class="d-none d-sm-inline mr-1 fa-duotone fa-album" style="--fa-primary-color: mediumseagreen;"></span>
    			일반
    		</a>
    	</li>
    	<li class="nav-item">
    		<a class="nav-link active" data-toggle="tab" id="ovw-chart" href="#overview-chart">
    			<i class="d-none d-sm-inline mr-1 fa-duotone fa-chart-pie" style="--fa-primary-color: mediumseagreen;"></i>
    			상태
    		</a>
    	</li>
    </ul>
    <div class="tab-content">
    	<div class="tab-pane p-2" id="overview-basic">
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>기기ID</div>
    			<div class="col-sm-8"><span id="overview-deviceID"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>유효시작일</div>
    			<div class="col-sm-8" id="overview-eff-start-date"></div>
    		</div>
    
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>기기일련번호</div>
    			<div class="col-sm-8"><span id="overview-serialNo"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>지점코드</div>
    			<div class="col-sm-8"><span id="overview-branchCode"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>지점명</div>
    			<div class="col-sm-8"><span id="overview-branchName"></span></div>
    		</div>
    
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>보고 주기</div>
    			<div class="col-sm-8"><span id="overview-reportInterval"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>사용자 정보</div>
    			<div class="col-sm-8"><span id="overview-customerId"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>서버 임포트</div>
    			<div class="col-sm-8"><span id="overview-import"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-check mr-2"></span>결과 보고 유형</div>
    			<div class="col-sm-8"><span id="overview-resultType"></span></div>
    		</div>
    
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-phone mr-2"></span>장소 연락처</div>
    			<div class="col-sm-8"><span id="overview-storeContact"></span></div>
    		</div>
    		<div class="row mb-3">
    			<div class="col-sm-4 text-muted"><span class="fa-regular fa-phone mr-2"></span>영업 연락처</div>
    			<div class="col-sm-8"><span id="overview-salesContact"></span></div>
    		</div>
    	</div>
    	<div class="tab-pane active" id="overview-chart">
    		<div>
    			<input id="overview-svcdate" type="text" class="form-control">
    			
    			<div class="d-flex mt-2">
    				<div class="chart-container mr-auto">
    					<div id="overview-chart-control" style="width: 250px; height: 250px; top: 25px; left: 25px;"></div>
    					<div style="width: 1px; height: 50px"></div>
    					<div class="mt-1">
    						<div class="d-flex justify-content-between">
    							<div>
    								<button type="button" class="btn btn-sm btn-outline-secondary" onclick="goPrevDate()">
    									<span class="fal fa-arrow-left"></span>
										<span class="pl-1">하루 전</span>
    								</button>		
    							</div>
    							<div class="d-flex align-items-center">
    								<span id="curr-date-day"><span id="curr-date-day-str"></span></span>
    							</div>
    							<div>
        							<button type="button" class="btn btn-sm btn-outline-secondary" onclick="goNextDate()">
    									<span class="fal fa-arrow-right"></span>
	 									<span class="pl-1">다음 날</span>
    								</button>		
    							</div>
    						</div>
    					</div>					
    				</div>
    				<div class="d-none d-sm-block" style="min-width: 200px;">
    					<div class="d-flex align-items-center mb-4 mt-2">
    						<span class="flag-34 sts-34-6" id="overview-st6i" title='정상 동작'></span>
    						<span class="ml-1">정상 동작</span>
    						<span class="numbers ml-auto"><span id="overview-st6-count"></span></span><span class="text-muted">분</span>
    					</div>
    					<div class="d-flex align-items-center my-4">
    						<span class="flag-34 sts-34-5" id="overview-st5i" title='투입 금지'></span>
    						<span class="ml-1">투입 금지</span>
    						<span class="numbers ml-auto"><span id="overview-st5-count"></span></span><span class="text-muted">분</span>
    					</div>
    					<div class="d-flex align-items-center my-4">
    						<span class="flag-34 sts-34-4" id="overview-st4i" title='장애 보고'></span>
    						<span class="ml-1">장애 보고</span>
    						<span class="numbers ml-auto"><span id="overview-st4-count"></span></span><span class="text-muted">분</span>
    					</div>
    					<div class="d-flex align-items-center my-4">
    						<span class="flag-34 sts-34-3" id="overview-st3i" title='RVM 꺼짐'></span>
    						<span class="ml-1">RVM 꺼짐</span>
    						<span class="numbers ml-auto"><span id="overview-st3-count"></span></span><span class="text-muted">분</span>
    					</div>
    					<div class="d-flex align-items-center mt-3 mb-2">
    						<span class="flag-34 sts-34-2" id="overview-st2i" title='STB 꺼짐'></span>
    						<span class="ml-1">STB 꺼짐</span>
    						<span class="numbers ml-auto"><span id="overview-st2-count"></span></span><span class="text-muted">분</span>
    					</div>
    					<hr class="border-light mt-2 mb-1">
    					<div class="d-flex align-items-center my-3">
    						<span class="ml-1"></span>
    						<span class="numbers ml-auto"><span id="overview-running-count"></span></span><span class="text-muted">분</span>
    					</div>
    				</div>
    			</div>
    		</div>
    	</div>
    </div>
    
    <style>
    
    /* 기기의 하루 운행 상태 파이 차트의 배경에 시간이 포함된 배경 이미지 출력  */
    .chart-container {
    	background:url(/resources/shared/styles/bg_time.png) left top no-repeat; 
    	width:300px; 
    	height:300px; 
    	text-align:center;
    }
    
    
    /* 기기 운행 시간(분) 문자열 데코 */
    span.numbers {
    	font-size: 16px;
    	font-weight:500; 
    	color:#000; 
    	padding-left:5px;
    	padding-right: 10px;
    }
    
    
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
    
    
    /* 도우넛 차트에 텍스트 출력 */
    .donut-wrapper {
    	position: relative;
    	width: 250px;
    	height: 250px;
    }
    
    .inner-content {
    	position: absolute;
    	top: 56%;
    	left: 0%;
    	width: 300px;
    	height: 300px;
    	vertical-align: middle;
    	text-align: center;
    	font-size: 16px;
    }
    
    .prev-date-button {
    	position: absolute;
    	top: 0;
    	left: 0;
    	width: 300px;
    	height: 300px;
    	text-align: left;
    	margin-left: 3px;
    	margin-top: 3px;
    }
    
    .next-date-button {
    	position: absolute;
    	top: 0;
    	left: 0;
    	width: 300px;
    	height: 300px;
    	text-align: right;
    	margin-right: 3px;
    	margin-top: 3px;
    }
    
    </style>
    
    <!--  / Forms -->
    
    
    <!--  Scripts -->
    
    <script>
    
    var ovwChartData = [];
    
    var ovwPrevGroup = "";
    var ovwCurrExplode = false;
    
    var ovwOldOpTag = null;
    
    var currDate = null;
    var prevDateStr = "", nextDateStr = "";
    var maxDate = new Date();
    
    $(document).ready(function() {
    	$("#overview-svcdate").kendoDatePicker({
    		dates: ${dates},
    		format: "yyyy-MM-dd",
    		parseFormats: [
    			"yyyy-MM-dd", "yyyyMMdd",
    		],
    		max: maxDate,
    		month: {
                content: '# if ($.inArray(+data.date, ${dates}) != -1) { #' +
                		 "<span style=\"position: relative;\"><span style=\"position:absolute;right:0px;margin-top:-11px;\">&bull;</span>#= data.value #</span>" +
             			 '# } else { #' +
             			 '#= data.value #' +
             			 '# } #',
    		},
    		change: function(e) {
    			var url = "/eco/common/rvmoverview?id=${value_rvmId}&date=" + 
    					kendo.toString($("#overview-svcdate").data("kendoDatePicker").value(), "yyyy'-'MM'-'dd");
    		    $("#deviceOverviewModal .modal-body").load(url);
    		}
    	});
    	
    	$('#rvm-overview-tabs a').on('shown.bs.tab', function(e){
    		var id = $(e.target).attr("id");
    		
    		if (parent) {
    			if (id == "ovw-tags") {
    				parent.changeModalButton("save");
    			} else {
    				parent.changeModalButton("");
    			}
    		}
    	});
    	
    	$("#overview-st6i").click(function() {
    		clickGroup("6");
    	});
    
    	$("#overview-st5i").click(function() {
    		clickGroup("5");
    	});
    
    	$("#overview-st4i").click(function() {
    		clickGroup("4");
    	});
    
    	$("#overview-st3i").click(function() {
    		clickGroup("3");
    	});
    
    	$("#overview-st2i").click(function() {
    		clickGroup("2");
    	});
    
    	if (parent) {
    		parent.childDoc = window;
    		parent.enableSaveButton(false);
    	}
    	
    	
    	$.ajax({
    		type: "POST",
    		contentType: "application/json",
    		dataType: "json",
    		url: "${readRvmOverviewUrl}",
    		data: JSON.stringify({ rvmId: "${value_rvmId}", stateDate: "${value_date}" }),
    		success: function (data, status) {
    			refreshData(data, status);
    		},
    		error: function(e) {
    			if (parent) {
    				parent.showReadErrorMsg();
    			}
    		}
    	});
    });
    
    function clickGroup(group) {
    	if (ovwPrevGroup != group) {
    		ovwCurrExplode = true;
    	} else {
    		ovwCurrExplode = !ovwCurrExplode;
    	}
    	
    	for(var i in ovwChartData) {
    		if (ovwChartData[i].group == group) {
    			ovwChartData[i].explode = ovwCurrExplode;
    		} else {
    			ovwChartData[i].explode = false;
    		}
    	}
    	
    	ovwPrevGroup = group;
    	
    	$("#overview-chart-control").data("kendoChart").refresh();
    }
    
    function refreshData(data, status) {
    
    	//
    	// [일반] 탭
    	//
    	var reportInterval = "";
    	if (data.rvm.reportInterval == 1) {
    		reportInterval = "1분";
    	} else if (data.rvm.reportInterval == 5) {
    		reportInterval = "5분";
    	} else if (data.rvm.reportInterval == 30) {
    		reportInterval = "30분";
    	} else if (data.rvm.reportInterval == 60) {
    		reportInterval = "1시간";
    	}
    	
    	var customerIdRequired = "";
    	if (data.rvm.customerIdRequired) {
    		customerIdRequired = "<span class='fa-regular fa-user-check'></span><span class='ml-2'>확인 후 수거</span>"
    	} else {
    		customerIdRequired = "<span class='fa-regular fa-question'></span><span class='ml-2'>확인하지 않음</span>"
    	}
    	
    	var importRequired = "";
    	if (data.rvm.importRequired) {
    		importRequired = "수행";
    	} else {
    		importRequired = "수행 안함";
    	}
    	
    	var resultType = "";
    	if (data.rvm.resultType == "c1") {
    		resultType = "COSMO 일별 일괄";
    	}
    
    	
    	$("#overview-deviceID").text(data.rvm.deviceID);
    	$("#overview-eff-start-date").text(data.rvm.effectiveStartDate.slice(0,10));
    	$("#overview-serialNo").text(data.rvm.serialNo);
    	$("#overview-branchCode").text(data.rvm.branchCode);
    	$("#overview-branchName").text(data.rvm.branchName);
    
    	$("#overview-reportInterval").html(reportInterval);
    	$("#overview-customerId").html(customerIdRequired);
    	$("#overview-import").text(importRequired);
    	$("#overview-resultType").text(resultType);
    
    
    	$("#overview-storeContact").text(data.rvm.storeContact);
    	$("#overview-salesContact").text(data.rvm.salesContact);
    	
    	
    	//
    	// [상태] 탭
    	//
    	$("#overview-svcdate").data("kendoDatePicker").value(data.service.stateDate);
    
    	$("#overview-st6-count").text(data.service.workingCount);
    	$("#overview-st5-count").text(data.service.storeClosedCount);
    	$("#overview-st4-count").text(data.service.failureReportedCount);
    	$("#overview-st3-count").text(data.service.rvmOffCount);
    	$("#overview-st2-count").text(data.service.stbOffCount);
    
    	$("#overview-running-count").text(data.service.totalCount);
    	
    	for(var i in data.service.runningTimeItems) {
    		var color = "#f3f3f4";
    		if (data.service.runningTimeItems[i].group == "6") {
    			color = "#0276bd";
    		} else if (data.service.runningTimeItems[i].group == "5") {
    			color = "#69bd44";
    		} else if (data.service.runningTimeItems[i].group == "4") {
    			color = "#fed235";
    		} else if (data.service.runningTimeItems[i].group == "3") {
    			color = "#f67c20";
    		} else if (data.service.runningTimeItems[i].group == "2") {
    			color = "#ef262e";
    		}
    		
    		var item = {
    				category: data.service.runningTimeItems[i].title,
    				value: data.service.runningTimeItems[i].value,
    				explode: false,
    				group: data.service.runningTimeItems[i].group,
    				color: color,
    			};
    		
    		ovwChartData.push(item);
    	}
    	
    	createOverviewChart(data.service.stateDate);
    	
    }
    
    function createOverviewChart(currDate) {
    	
        $("#overview-chart-control").kendoChart({
            legend: {
                visible: false,
            },
            chartArea: {
                background: ""
            },
            seriesDefaults: {
                type: "donut",
                holeSize: 40,
                startAngle: 90,
                explodeField: "explode",
                tooltip: {
                	font: "'Roboto', 'Noto Sans', 'Noto Sans CJK KR', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Oxygen', 'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue'",
                }
            },
            series: [{
                name: "Service Time",
                data: ovwChartData,
                size: 65,
            }],
            tooltip: {
                visible: true,
                template: "#= category #"
            },
        });
    
        
        // 선택 날짜의 요일 표시
    	$("#curr-date-day-str").text(kendo.toString($("#overview-svcdate").data("kendoDatePicker").value(), "dddd"));
    
        // 하루 전, 다음 날 처리를 위한 계산
        const [y, m, d] = currDate.split("-");
        
        var nextDate = new Date(Number(y), Number(m) - 1, Number(d) + 1);
        var prevDate = new Date(Number(y), Number(m) - 1, Number(d) - 1);
        
        prevDateStr = toStringByFormatting(prevDate, "-");
        
        if (nextDate.getTime() > maxDate.getTime()) {
            nextDateStr = toStringByFormatting(maxDate, "-");
        } else {
            nextDateStr = toStringByFormatting(nextDate, "-");
        }
    }
    
    function leftPad(value) {
        if (value >= 10) {
            return value;
        }
    
        return "0" + value;
    }
    
    function toStringByFormatting(source, delimiter = '-') {
        const year = source.getFullYear();
        const month = leftPad(source.getMonth() + 1);
        const day = leftPad(source.getDate());
        
        return [year, month, day].join(delimiter);
    }
    
    function goPrevDate() {
    	$("#deviceOverviewModal .modal-body").load("/eco/common/rvmoverview?id=${value_rvmId}&date=" + prevDateStr);
    }
    
    function goNextDate() {
    	$("#deviceOverviewModal .modal-body").load("/eco/common/rvmoverview?id=${value_rvmId}&date=" + nextDateStr);
    }
    
    </script>
    
    <!--  / Scripts -->
    
    
    <!-- / Page body -->
