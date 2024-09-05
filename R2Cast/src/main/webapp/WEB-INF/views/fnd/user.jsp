<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/user/create" var="createUrl" />
<c:url value="/fnd/user/read" var="readUrl" />
<c:url value="/fnd/user/update" var="updateUrl" />
<c:url value="/fnd/user/destroy" var="destroyUrl" />
<c:url value="/fnd/user/defaultpassword" var="passwordUrl" />


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
<kendo:grid name="grid" pageable="true" groupable="false" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
    <kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">${cmd_add}</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">${cmd_excel}</button>
    		</div>
    		<div class="float-right">
    			<button id="password-btn" type="button" class="btn btn-default d-none d-sm-inline">${cmd_setDefaultPassword}</button>
    			<button id="delete-btn" type="button" class="btn btn-danger">${cmd_delete}</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column title="${cmd_edit}" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="${title_username}" field="username" />
		<kendo:grid-column title="${title_familiarName}" field="familiarName" />
		<kendo:grid-column title="${title_effectiveStartDate}" field="effectiveStartDate" format="{0:yyyy-MM-dd}" width="140" minScreenWidth="500"/>
		<kendo:grid-column title="${title_effectiveEndDate}" field="effectiveEndDate" format="{0:yyyy-MM-dd}" width="140" minScreenWidth="700"/>
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="familiarName" dir="asc"/>
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
		
		initForm2();

		
		$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
		$("#form-modal-2").modal();
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
			}, true, delRows.length);
		}
	});
	// / Delete
	
	// Default password
	$("#password-btn").click(function(e) {
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
				url: "${passwordUrl}",
				data: JSON.stringify({ items: opRows }),
				success: function (data, status, xhr) {
						showOperationSuccessMsg();
				},
				error: ajaxOperationError
			});
		}
	});
	// / Default password
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Forms -->

<script id="template-1" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-1">
	<div class="modal-dialog modal-sm">
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
				<div class="form-group col">
					<label class="form-label">
						${title_username}
						<span class="text-danger">*</span>
					</label>
					<input name="username" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						${title_familiarName}
						<span class="text-danger">*</span>
					</label>
					<input name="familiarName" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						${title_password}
					</label>
					<input name="password" type="password" class="form-control">
				</div>
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
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">${form_cancel}</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>${form_save}</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog modal-sm">
		<form class="modal-content" id="form-2" rowid="-1" url="${createUrl}">
      
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
				<div class="form-group col">
					<label class="form-label">
						${title_username}
						<span class="text-danger">*</span>
					</label>
					<input name="username" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						${title_familiarName}
						<span class="text-danger">*</span>
					</label>
					<input name="familiarName" type="text" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						${title_password}
						<span class="text-danger">*</span>
					</label>
					<input name="password" type="password" class="form-control required">
				</div>
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
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">${form_cancel}</button>
				<button type="button" class="btn btn-primary" onclick='saveForm2()'>${form_save}</button>
			</div>
			
		</form>
	</div>
</div>

</script>

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
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "${form_add}");
	
	$("#form-1").validate({
		rules: {
			username: {
				minlength: 2, maxlength: 50, alphanumeric: true,
			},
			familiarName: {
				minlength: 2, maxlength: 25,
			},
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
		}
	});
}


function saveForm1() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-1 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-1 input[name='effectiveEndDate']"));
	
	var passwordLevel = "${val_pwdLevel}";
	var password = $.trim($("#form-1 input[name='password']").val());
	
	if (passwordLevel == "2" && password != null && password != "") {
		var reg = new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@!?#%^&*])\S{9,}$/g);
		if (!reg.test(password)) {
			showAlertModal("danger", "${msg_wrongLevel2Pwd}");
			return;
		}
	}
	
	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		username: $.trim($("#form-1 input[name='username']").val()),
    		familiarName: $.trim($("#form-1 input[name='familiarName']").val()),
    		newPassword: password,
    		effectiveStartDate: $("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
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


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$("#form-2 input[name='effectiveStartDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
		value: new Date(),
	});
	
	$("#form-2 input[name='effectiveEndDate']").kendoDatePicker({
		format: "yyyy-MM-dd",
		parseFormats: [
			"yyyy-MM-dd",
		],
	});
	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "${form_add}");
	
	$("#form-2").validate({
		rules: {
			username: {
				minlength: 2, maxlength: 50, alphanumeric: true,
			},
			familiarName: {
				minlength: 2, maxlength: 25,
			},
			effectiveStartDate: { date: true },
			effectiveEndDate: { date: true },
		}
	});
}


function saveForm2() {

	// kendo datepicker validation
	validateKendoDateValue($("#form-2 input[name='effectiveStartDate']"));
	validateKendoDateValue($("#form-2 input[name='effectiveEndDate']"));
	
	var passwordLevel = "${val_pwdLevel}";
	var password = $.trim($("#form-2 input[name='password']").val());
	
	if (passwordLevel == "2" && password != null && password != "") {
		var reg = new RegExp(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[$@!?#%^&*])\S{9,}$/g);
		if (!reg.test(password)) {
			showAlertModal("danger", "${msg_wrongLevel2Pwd}");
			return;
		}
	}
	
	if ($("#form-2").valid()) {
    	var data = {
    		id: Number($("#form-2").attr("rowid")),
    		username: $.trim($("#form-2 input[name='username']").val()),
    		familiarName: $.trim($("#form-2 input[name='familiarName']").val()),
    		newPassword: password,
    		effectiveStartDate: $("#form-2 input[name='effectiveStartDate']").data("kendoDatePicker").value(),
    		effectiveEndDate: $("#form-2 input[name='effectiveEndDate']").data("kendoDatePicker").value(),
    	};
    	
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: $("#form-2").attr("url"),
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-2").modal("hide");
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
	
	$("#form-1 input[name='username']").val(dataItem.username);
	$("#form-1 input[name='familiarName']").val(dataItem.familiarName);
	
	$("#form-1 input[name='effectiveStartDate']").data("kendoDatePicker").value(dataItem.effectiveStartDate);
	$("#form-1 input[name='effectiveEndDate']").data("kendoDatePicker").value(dataItem.effectiveEndDate);

	
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