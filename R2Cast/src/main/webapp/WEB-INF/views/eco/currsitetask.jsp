<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/currsitetask/reportCosmo" var="reportCosmoUrl" />

<c:url value="/eco/common/readACRvms" var="readRvmUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!--  Forms -->

<div class="card">
	<hr class="m-0">
	<div class="card-body">
		<div class="pb-3">
			COSMO 서버에 결과 보고
			<span class="small text-muted pl-3">자동으로 이루어지는 일별 일괄 작업 대신 수동으로 결과를 보고합니다.</span>
		</div>
		<div class="form-row">
			<div class="col-sm-6">
				<div class="form-group col">
					<label class="form-label">
						수거일
						<span class="text-danger">*</span>
					</label>
					<input name="cosmo-report-date" type="text" class="form-control">
				</div>
			</div>
			<div class="col-sm-6">
				<label class="form-label">
					RVM명
				</label>
				<input id="cosmo-report-rvm" type="text" class="form-control">
			</div>
		</div>
		<div class="text-right mt-0">
			<button type="button" id="cosmo-report-btn" class="btn btn-primary btn-round">전송</button>
		</div>
	</div>
</div>


<script>

$(document).ready(function() {
	
	$("#cosmo-report-rvm").kendoAutoComplete({
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
        height: 400,
        delay: 500,
        minLength: 1,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
	
	$("input[name='cosmo-report-date']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd", "yyyyMMdd",
		],
		max: new Date(),
		value: "${initReportCosmoDate}",
	});

	$("#cosmo-report-btn").click(function(e) {

		// kendo datepicker validation
		validateKendoDateValue($("input[name='cosmo-report-date']"));
		
		var date = $("input[name='cosmo-report-date']").data("kendoDatePicker").value();
		var rvmName = $.trim($("#cosmo-report-rvm").val());
		
		if (date != null) {
			
			//showWaitModal();

	    	$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${reportCosmoUrl}",
				data: JSON.stringify({ 
					date: date, 
					rvmName: rvmName,
				}),
				success: function (form) {
					//hideWaitModal();
					showOperationSuccessMsg();
				},
				error: function(e) {
					//hideWaitModal();
					ajaxOperationError(e);
				}
			});

		}
	});

});

</script>

<!--  / Forms -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
