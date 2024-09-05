<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/currdispmenu/update" var="updateUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>






<!-- Page body -->


<!-- Page scripts  -->

<link rel="stylesheet" href="/resources/vendor/lib/dragula/dragula.css">

<script src="/resources/vendor/lib/dragula/dragula.js"></script>


<!--  Forms -->

<ul class="nav nav-tabs tabs-alt container-p-x container-m--x mb-4">
	<li class="nav-item">
		<a class="nav-link active" data-toggle="tab" href="#quick-links">
			<i class="mr-1 fa-light fa-link"></i>
			빠른 링크
		</a>
	</li>
</ul>

<div class="tab-content">
	<div class="tab-pane active" id="quick-links">

		<div class="row">
			<div class="card col px-0 mx-1">
				<h6 class="card-header with-elements bg-secondary text-white">
					<div class="card-header-title">표시 가능한 메뉴</div>
				</h6>
				<div id="dragula-left" class="card-body px-2 pb-2 pt-3 menu-container">
			
<c:forEach var="item" items="${OptMenus}">
	       	
					<div class="menu-item card card-condenced pc-opt" ukid="${item.ukid}">
						<div class="card-body media align-items-center">
							<span class="d-none d-sm-block ${item.style} fa-fw fa-2x text-muted mr-3"></span>
							<div class="media-body">
								<span class="text-dark font-weight-semibold mb-2">${item.title}</span>
								<div class="text-muted small">${item.dispGroup}</div>
							</div>
							<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
						</div>
					</div>

</c:forEach>
				
				</div>
			</div>
			
			<div class="card col px-0 mx-1">
				<h6 class="card-header with-elements bg-success text-white">
					<div class="card-header-title">표시 메뉴</div>
					<div class="card-header-elements ml-auto small opacity-75">
						<span id="sel-menu-count"></span>
						<span>항목</span>
					</div>
				</h6>
				<div id="dragula-right" class="card-body px-2 pb-2 pt-3 menu-container">
			
<c:forEach var="item" items="${SelMenus}">
	       	
					<div class="menu-item card card-condenced pc-opt" ukid="${item.ukid}">
						<div class="card-body media align-items-center">
							<span class="d-none d-sm-block ${item.style} fa-fw fa-2x text-muted mr-3"></span>
							<div class="media-body">
								<span class="text-dark font-weight-semibold mb-2">${item.title}</span>
								<div class="text-muted small">${item.dispGroup}</div>
							</div>
							<div><span class="fas fa-arrows-alt fa-lg text-secondary mobile-opt"></span></div>
						</div>
					</div>

</c:forEach>
                
				</div>
			</div>
		</div>


		<div class="text-right mt-3" style="margin-right: -0.5rem;">
			<button id="save-btn" type="button" class="btn btn-primary">저장</button>
		</div>
		
	</div>
</div>

<style>

/* 메뉴들을 포함하는 컨테이너 박스가 많은 메뉴일 경우 스크롤 되도록 */
.menu-container {
	overflow-y: auto; height: 500px;
}


/* 메뉴 항목과 항목간 간격 조정 */
.menu-item {
	margin-bottom: 0.4rem;
}


/* 메뉴 오버 마우스 모습이 포인트에서 이동십자 포인트로 표시(PC 환경) */
.pc-opt {

<c:if test="${not isMobileMode}">
	cursor: move;
</c:if>

}


/* 끌기 가능한 아이콘 버튼 표시(모바일 환경) */
.mobile-opt {

<c:if test="${not isMobileMode}">
	display: none;
</c:if>

}

</style>
        
<!--  / Forms -->


<!--  Scripts -->

<script>
$(document).ready(function() {

	dragula([$('#dragula-left')[0], $('#dragula-right')[0]], {
		revertOnSpill: true,
		moves: function (el, container, handle) {

<c:if test="${isMobileMode}">
			var iconHandler = handle;
			if (handle.tagName == "path") {
				iconHandler = handle.parentElement;
			}

			return iconHandler.classList.contains('fa-arrows-alt');
</c:if>

<c:if test="${not isMobileMode}">
			return true;
</c:if>

		},
	}).on('drop', function (el) {
		displaySelCount();
	});
	
	displaySelCount();
	
	$("#save-btn").click(function(e) {
		
		var ukids = "";
		$("#dragula-right .menu-item").each(function(index) {
			ukids = ukids + $(this).attr("ukid") + "|";
		});

    	var data = {
    		ukids: ukids,	
    	};

    	$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${updateUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showAlertModal("success", "저장이 완료되었습니다.");
			},
			error: ajaxSaveError
		});
    });
	
	function displaySelCount() {
		$("#sel-menu-count").text($("#dragula-right .menu-item").length);
	}
	
});
</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
