<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/common/readStatusFeed" var="readStatusbarUrl" />


<!--  HTML tags -->

<div class="nav bg-lighter container-p-x py-1 container-m--x mb-3 d-flex align-items-center">
	<span class="ml-1 flag-18 sts-18-6" title="정상 동작"></span><a href="javascript:filterStatus('6')" class="font-size-16 ml-1"><span class="text-blue" id="sb-st6-count"></span></a>
	<span class="ml-3 flag-18 sts-18-5" title="투입 금지"></span><a href="javascript:filterStatus('5')" class="font-size-16 ml-1"><span class="text-green" id="sb-st5-count"></span></a>
	<span class="ml-3 flag-18 sts-18-4" title="장애 보고"></span><a href="javascript:filterStatus('4')" class="font-size-16 ml-1"><span class="text-yellow" id="sb-st4-count"></span></a>
	<span class="ml-3 flag-18 sts-18-3" title="RVM 꺼짐"></span><a href="javascript:filterStatus('3')" class="font-size-16 ml-1"><span class="text-orange" id="sb-st3-count"></span></a>
	<span class="ml-3 flag-18 sts-18-2" title="STB 꺼짐"></span><a href="javascript:filterStatus('2')" class="font-size-16 ml-1"><span class="text-red" id="sb-st2-count"></span></a>
	<span class="ml-3 flag-18 sts-18-0" title="미확인"></span><a href="javascript:filterStatus('0')" class="font-size-16 ml-1"><span class="text-gray" id="sb-st0-count"></span></a>

	<div class="d-none d-sm-inline ml-2">
		<span id="realtime-icon" style="display: none;">
			<span id="realtime-icon-R" class="badge badge-outline-dark font-weight-normal small ml-2">실시간</span>
			<span id="realtime-icon-C" class="badge badge-outline-info font-weight-normal small ml-2">새로고침</span>
		</span>
		<button id="sb-refresh-btn" type="button" class="btn btn-xs btn-default" onclick="sbReadStatusbarStatus()">
			<span class="fas fa-sync-alt fa-sm"></span><span id="sb-refresh-content" class="pl-1"></span>
		</button>
	</div>
</div>

<!--  / HTML tags -->


<!--  Styles -->

<style>

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

.font-size-16 { font-size: 16px; }

</style>

<!--  / Styles -->


<!--  Scripts -->

<script>

var sbTimerHandle;
var sbCount;

var wsMode = false;
var wsTime;
var wsFirst = true;
var wsSocket;


function filterStatus(status) {

	if (status) {
		location.href = "/eco/mongridview?status=" + status;
	}
}


function sbReadStatusbarStatus() {
	
	if (sbTimerHandle) {
		window.clearTimeout(sbTimerHandle);
	}

	if (wsMode && !wsIsValid(wsTime, 1)) {
		wsMode = false;
		wsSocket.close();
		
		$("#realtime-icon").hide();
		$("#sb-refresh-btn").show();
	}

	if (wsMode) {
		startTimer(60);
		return;
	}
	
	$("#sb-st6-count").text("");
	$("#sb-st5-count").text("");
	$("#sb-st4-count").text("");
	$("#sb-st3-count").text("");
	$("#sb-st2-count").text("");
	$("#sb-st0-count").text("");
	$("#sb-refresh-content").text("읽는 중...");
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${readStatusbarUrl}",
		data: JSON.stringify({ }),
		success: function (data, status) {
			$("#sb-refresh-content").text("01:00");
			$("#sb-st6-count").text(data.workingCount);
			$("#sb-st5-count").text(data.storeClosedCount);
			$("#sb-st4-count").text(data.failureReportedCount);
			$("#sb-st3-count").text(data.rvmOffCount);
			$("#sb-st2-count").text(data.stbOffCount);
			$("#sb-st0-count").text(data.noShowCount);
			
			startTimer(60);
			
<c:if test="${not isMobileMode}">

			if (wsFirst) {
				wsConnect();
			}

</c:if>
			
		},
		error: function(e) {
			$("#sb-refresh-content").text("읽기실패");
		}
	});
}


function sbCountdown() {
	
    var minutes = parseInt(sbCount / 60, 10);
    var seconds = parseInt(sbCount % 60, 10);

    minutes = minutes < 10 ? "0" + minutes : minutes;
    seconds = seconds < 10 ? "0" + seconds : seconds;

    $("#sb-refresh-content").text(minutes + ":" + seconds);

    if (--sbCount < 0) {
    	sbReadStatusbarStatus();
    }
}


function startTimer(duration) {
	
	sbCount = duration;
	sbTimerHandle = setInterval("sbCountdown();", 1000);
}


function wsIsValid(date, offsetMins) {
	
	if (!date) {
		return false;
	}
	
	var validTime = new Date(date.getTime() + offsetMins * 60 * 1000);
	
	return (validTime > (new Date()));
}


function wsConnect() {
	
	if ('WebSocket' in window) {
		var siteId = "";
		<c:if test="${isSiteLevelViewMode && not empty sessionScope['currentSiteId']}">
			siteId = ${sessionScope['currentSiteId']};
		</c:if>
		
		var wsProtocol = "ws://";
		if (location.protocol == "https:") {
			wsProtocol = "wss://";
		}
 		wsSocket = new WebSocket(wsProtocol + location.host + "/r/sitervmstatus?siteId=" + siteId);

		wsSocket.onmessage = function (e) {
			var obj = JSON.parse(e.data);

			if (wsIsValid(new Date(obj.createdTime), 3)) {
				$("#sb-st6-count").text(obj.status6);
				$("#sb-st5-count").text(obj.status5);
				$("#sb-st4-count").text(obj.status4);
				$("#sb-st3-count").text(obj.status3);
				$("#sb-st2-count").text(obj.status2);
				$("#sb-st0-count").text(obj.status0);

				wsMode = true;
				wsTime = new Date();
				wsFirst = false;
				
				var el = $("#realtime-icon");
				$("#realtime-icon-C").show();
				$("#realtime-icon-R").hide();

				$("#realtime-icon").show();
				$("#sb-refresh-btn").hide();
				
				var effect = kendo.fx($("#realtime-icon")).fadeOut().duration(1000);
				effect.play().then(function() {
					$("#realtime-icon-C").hide();
					$("#realtime-icon-R").show();
					
					kendo.fx(el).fadeIn().play();
			    });
			}
		}
	}
}	


$(document).ready(function() {
	
	sbReadStatusbarStatus();
	
});

</script>

<!--  / Scripts -->
