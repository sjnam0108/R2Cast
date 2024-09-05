<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/uprawtrx/read" var="readUrl" />
<c:url value="/eco/uprawtrx/destroy" var="destroyUrl" />

<c:url value="/eco/uprawtrx/import" var="importUrl" />

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
	String itemTemplate = 
			"<button type='button' onclick='download(\"#= pathName #\", \"#= rvm #\")' class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
			"<span class='fas fa-download'></span></button>";
	String rvmTemplate =
			"<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvm #</a><a href='javascript:showRtb(#= id #,\"#= rvm #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a></div>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default k-grid-excel">엑셀</button>
    			<span class="d-none d-sm-inline">
					<span class="mx-2">|</span>
					<span class="fa-regular fa-info-circle text-info"></span>
    				<span class="pl-1">최근 임포트: ${lastImportDt}</span>
    			</span>
    		</div>
    		<div class="float-right">
    		    <button id="import-btn" type="button" class="btn btn-default d-none d-sm-inline">임포트</button>
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column width="50" filterable="false" sortable="false" template="<%= itemTemplate %>" />
		<kendo:grid-column title="RVM명" field="rvm" />
        <kendo:grid-column title="날짜" field="date" />
        <kendo:grid-column title="파일명" field="filename" minScreenWidth="1100" />
        <kendo:grid-column title="크기" field="length" template="#= smartLength #" minScreenWidth="650" />
        <kendo:grid-column title="업로드일시" field="lastModified" minScreenWidth="500" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				attachRvmStatusBarPopover();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="lastModified" dir="desc"/>
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
		<kendo:dataSource-schema>
			<kendo:dataSource-schema-model>
				<kendo:dataSource-schema-model-fields>
					<kendo:dataSource-schema-model-field name="length" type="number"/>
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

	// Delete
	$("#delete-btn").click(function(e) {
		e.preventDefault();
			
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();
	
		var delRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			delRows.push(selectedItem.pathName);
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
	
	
	// Import
	$("#import-btn").click(function(e) {
		e.preventDefault();
		
		var grid = $("#grid").data("kendoGrid");
		var rows = grid.select();

		var opRows = [];
		rows.each(function(index, row) {
			var selectedItem = grid.dataItem(row);
			opRows.push(selectedItem.pathName);
		});
		
		if (opRows.length > 0) {
			var msg = "총 {0} 건에 대한 임포트 작업을 수행할 예정입니다. 계속 진행하시겠습니까?".replace("{0}", "<strong>" + opRows.length + "</strong>");
			showConfirmModal(msg, function(result) {
				if (result) {
					showWaitModal();
					
					$.ajax({
						type: "POST",
						contentType: "application/json",
						dataType: "json",
						url: "${importUrl}",
						data: JSON.stringify({ items: opRows }),
						success: function (data, status, xhr) {
							hideWaitModal();
	  						showOperationSuccessMsg();
							grid.dataSource.read();
						},
						error: function(e) {
							hideWaitModal();
							showOperationErrorMsg();
						}
					});
				}
			});
		}
	});
	// / Import
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Scripts -->

<script>

function download(file, prefix) {
	
	var path = "/eco/common/download?type=TRX&file=" + file + "&prefix=" + prefix;
	
	location.href = path;
}


</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:ecoRvmOverview />


<!-- Closing tags -->

<common:base />
<common:pageClosing />