<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/rvmgroup/create" var="createUrl" />
<c:url value="/eco/rvmgroup/read" var="readUrl" />
<c:url value="/eco/rvmgroup/update" var="updateUrl" />
<c:url value="/eco/rvmgroup/destroy" var="destroyUrl" />


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
	String catTemplate =
			"# if (category == 'R') { #" + "<span class='fas fa-square text-red'></span><span class='pl-1'>빨간색" +
			"# } else if (category == 'O') { #" + "<span class='fas fa-square text-orange'></span><span class='pl-1'>주황색" +
			"# } else if (category == 'Y') { #" + "<span class='fas fa-square text-yellow'></span><span class='pl-1'>노란색" +
			"# } else if (category == 'G') { #" + "<span class='fas fa-square text-green'></span><span class='pl-1'>초록색" +
			"# } else if (category == 'B') { #" + "<span class='fas fa-square text-blue'></span><span class='pl-1'>파란색" +
			"# } else if (category == 'P') { #" + "<span class='fas fa-square text-purple'></span><span class='pl-1'>보라색" +
			"# } #" +
			"</span>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="RVM 그룹명" field="rvmGroupName" />
		<kendo:grid-column title="범주" field="category"
				template="<%= catTemplate %>" />
		<kendo:grid-column title="뷰 유형" field="viewType" filterable="true"
				template="#=viewType == 'Y' ? \"<span class='far fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="rvmGroupName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-filter>
			<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}">
			</kendo:dataSource-filterItem>
		</kendo:dataSource-filter>
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
			<kendo:dataSource-schema-model id="id" />
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
			}, true, delRows.length);
		}
	});
	// / Delete
	
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
						RVM 그룹명
						<span class="text-danger">*</span>
					</label>
					<input name="rvmGroupName" type="text" maxlength="25" class="form-control required">
				</div>
				<div class="form-group col">
					<label class="form-label">
						범주
					</label>
					<select name="category" class="selectpicker bg-white" data-style="btn-default" data-icon-base="fas" data-tick-icon="fa-blank" data-none-selected-text="">
						<option value="R" data-icon="fa-square text-red mr-2">빨간색</option>
						<option value="O" data-icon="fa-square text-orange mr-2">주황색</option>
						<option value="Y" data-icon="fa-square text-yellow mr-2">노란색</option>
						<option value="G" data-icon="fa-square text-green mr-2">초록색</option>
						<option value="B" data-icon="fa-square text-blue mr-2">파란색</option>
						<option value="P" data-icon="fa-square text-purple mr-2">보라색</option>
					</select>
				</div>
				<div class="form-group col">
					<label class="form-label">
						뷰 유형
					</label>
					<select name="viewType" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
						<option value="Y">뷰 목록에 포함</option>
						<option value="N">뷰 목록에서 제외</option>
					</select>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm1()'>저장</button>
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
	
	$("#form-1 select[name='category']").selectpicker('render');
	$("#form-1 select[name='viewType']").selectpicker('render');

	bootstrapSelectVal($("#form-1 select[name='category']"), "B");
	bootstrapSelectVal($("#form-1 select[name='viewType']"), "Y");
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			rvmGroupName: {
				minlength: 2, maxlength: 25,
			},
		}
	});
}


function saveForm1() {

	if ($("#form-1").valid()) {
    	var data = {
    		id: Number($("#form-1").attr("rowid")),
    		rvmGroupName: $.trim($("#form-1 input[name='rvmGroupName']").val()),
    		category: $("#form-1 select[name='category']").val(),
    		viewType: $("#form-1 select[name='viewType']").val(),
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
	
	initForm1("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-1").attr("rowid", dataItem.id);
	$("#form-1").attr("url", "${updateUrl}");
	
	$("#form-1 input[name='rvmGroupName']").val(dataItem.rvmGroupName);

	bootstrapSelectVal($("#form-1 select[name='category']"), dataItem.category);
	bootstrapSelectVal($("#form-1 select[name='viewType']"), dataItem.viewType);

	
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
