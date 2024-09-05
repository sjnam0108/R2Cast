<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/moneventreport/read" var="readUrl" />
<c:url value="/eco/moneventreport/destroy" var="destroyUrl" />

<c:url value="/eco/moneventreport/readCategories" var="readCategoryUrl" />
<c:url value="/eco/moneventreport/readEquipTypes" var="readEquipTypeUrl" />
<c:url value="/eco/moneventreport/readReportTypes" var="readReportTypeUrl" />
<c:url value="/eco/moneventreport/shotfile" var="shotFileUrl" />


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
	String stbTemplate =
			"# if (equipType == 'P') { #" + "<div class='d-flex align-items-center'><a href='javascript:void(0)' class='stb-status-popover' tabindex='0'>#= equipName #</a><a href='javascript:showStb(#= equipId #,\"#= equipName #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a></div>" +
			"# } else { #" + "<span>#= equipName #</span>" +
			"# } #";
	String equipTypeTemplate = "<span>" + 
			"# if (equipType == 'B') { #" + request.getAttribute("item_BackupServer") + 
			"# } else if (equipType == 'D') { #" + request.getAttribute("item_DbServer") + 
			"# } else if (equipType == 'F') { #" + request.getAttribute("item_FtpServer") + 
			"# } else if (equipType == 'M') { #" + request.getAttribute("item_Manager") + 
			"# } else if (equipType == 'P') { #" + request.getAttribute("item_Player") + 
			"# } else if (equipType == 'W') { #" + request.getAttribute("item_Was") + 
			"# } #" +
			"</span>";
	String detailsTemplate = "#:details#";
	String dateTemplate = kr.co.r2cast.utils.Util.getSmartDate();
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button type="button" class="btn btn-default k-grid-excel">${cmd_excel}</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">${cmd_delete}</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
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
		<kendo:grid-column title="이름" field="equipName" template="<%= stbTemplate %>" />
		<kendo:grid-column title="상세내용" field="details" template="<%= detailsTemplate %>" minScreenWidth="650" />
		<kendo:grid-column title="장비 유형" field="equipType" template="<%= equipTypeTemplate %>" minScreenWidth="1400" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readEquipTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
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

</style>

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


<!-- / Page body -->





<!-- Functional tags -->

<func:ecoRvmOverview />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
