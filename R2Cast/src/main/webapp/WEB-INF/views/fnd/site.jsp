<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/site/create" var="createUrl" />
<c:url value="/fnd/site/read" var="readUrl" />
<c:url value="/fnd/site/update" var="updateUrl" />
<c:url value="/fnd/site/destroy" var="destroyUrl" />
<c:url value="/fnd/site/checkdir" var="checkUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="single" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">${cmd_add}</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">${cmd_excel}</button>
    		</div>
    		<div class="float-right">
    			<button id="check-btn" type="button" class="btn btn-default d-none d-sm-inline">${cmd_checkDirectory}</button>
    			<button id="delete-btn" type="button" class="btn btn-danger">${cmd_delete}</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column title="${cmd_edit}" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="${title_shortName}" field="shortName" groupHeaderTemplate="${title_shortName} - #= value #, Count: #= count #"/>
		<kendo:grid-column title="${title_siteName}" field="siteName" minScreenWidth="500"/>
		<kendo:grid-column title="${title_effectiveStartDate}" field="effectiveStartDate" format="{0:yyyy-MM-dd}"/>
		<kendo:grid-column title="${title_effectiveEndDate}" field="effectiveEndDate" format="{0:yyyy-MM-dd}" minScreenWidth="700"/>
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="shortName" dir="asc"/>
		</kendo:dataSource-sort>
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
					<kendo:dataSource-schema-model-field name="effectiveStartDate" type="date" />
					<kendo:dataSource-schema-model-field name="effectiveEndDate" type="date" />
				</kendo:dataSource-schema-model-fields>
			</kendo:dataSource-schema-model>
		</kendo:dataSource-schema>
	</kendo:dataSource>
</kendo:grid>
</div>

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
	
	// Check directories
	$("#check-btn").click(function(e) {
		e.preventDefault();
		
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();

		var opRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.id);
		});
		
		if (opRows.length > 0) {
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${checkUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (data, status, xhr) {
					showOperationSuccessMsg();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Check directories
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog">
		<form class="modal-content" id="form-1" rowid="-1" url="${createUrl}">
      
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
					<div class="form-group col">
						<label class="form-label">
							${title_shortName}
							<span class="text-danger">*</span>
						</label>
						<input name="shortName" type="text" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							${title_siteName}
							<span class="text-danger">*</span>
						</label>
						<input name="siteName" type="text" class="form-control required">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							${title_effectiveStartDate}
							<span class="text-danger">*</span>
						</label>
						<input name="effectiveStartDate" type="text" class="form-control required">
					</div>
					<div class="form-group col">
						<label class="form-label">
							${title_effectiveEndDate}
						</label>
						<input name="effectiveEndDate" type="text" class="form-control">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							${title_serverHost}
						</label>
						<input name="serverHost" type="text" class="form-control">
					</div>
					<div class="form-group col">
						<label class="form-label">
							${title_serverPort}
						</label>
						<input name="serverPort" type="text" class="form-control">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							${title_ftpHost}
						</label>
						<input name="ftpHost" type="text" class="form-control">
					</div>
					<div class="form-group col">
						<label class="form-label">
							${title_ftpPort}
						</label>
						<input name="ftpPort" type="text" class="form-control">
					</div>
				</div>
				<div class="form-row">
					<div class="form-group col">
						<label class="form-label">
							${title_ftpUsername}
						</label>
						<input name="ftpUsername" type="text" class="form-control">
					</div>
					<div class="form-group col">
						<label class="form-label">
							${title_ftpPassword}
						</label>
						<input name="ftpPassword" type="password" class="form-control">
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">${form_cancel}</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>${form_save}</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<style>

/* 폼에서 열 사이의 간격을 원래대로 넓게 */
.form-row>.col, .form-row>[class*="col-"] {
	padding-right: .75rem;
	padding-left: .75rem;
}

</style>

<!--  / Forms -->


<!--  Scripts -->

<script>

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
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
	
	// set default values
	$("#form-1 input[name='serverPort']").val(80);
	$("#form-1 input[name='ftpPort']").val(21);
	
	// set modal title
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "${form_add}");
	
	// set validation
	$("#form-1").validate({
		rules: {
			shortName: {
				minlength: 2, maxlength: 50, alphanumeric: true,
			},
			siteName: {
				minlength: 2, maxlength: 25,
			},
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
			serverPort: {
				number: true, min: 1, max: 65535,
			},
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-1 input[name='effectiveEndDate']"));
	
	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		
    		shortName: $.trim($("#form-1 input[name='shortName']").val()),
    		siteName: $.trim($("#form-1 input[name='siteName']").val()),
    		
    		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
    		
    		serverHost: $.trim($("#form-1 input[name='serverHost']").val()),
    		serverPort: Number($("#form-1 input[name='serverPort']").val()),
    		ftpHost: $.trim($("#form-1 input[name='ftpHost']").val()),
    		ftpPort: Number($("#form-1 input[name='ftpPort']").val()),
    		ftpUsername: $.trim($("#form-1 input[name='ftpUsername']").val()),
    		ftpPassword: $.trim($("#form-1 input[name='ftpPassword']").val()),
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
	
	initForm1("${form_edit}");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='shortName']").val(dataItem.shortName);
	$("#form-1 input[name='siteName']").val(dataItem.siteName);
	
	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);
	
	$("#form-1 input[name='serverHost']").val(dataItem.serverHost);
	$("#form-1 input[name='serverPort']").val(dataItem.serverPort);
	$("#form-1 input[name='ftpHost']").val(dataItem.ftpHost);
	$("#form-1 input[name='ftpPort']").val(dataItem.ftpPort);
	$("#form-1 input[name='ftpUsername']").val(dataItem.ftpUsername);
	$("#form-1 input[name='ftpPassword']").val(dataItem.ftpPassword);

	
	$('#form-modal-1 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-1").modal();
}


</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
