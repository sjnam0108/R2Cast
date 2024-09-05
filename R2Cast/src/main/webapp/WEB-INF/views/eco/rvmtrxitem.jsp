<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/rvmtrxitem/read" var="readUrl" />
<c:url value="/eco/rvmtrxitem/destroy" var="destroyUrl" />

<c:url value="/eco/rvmtrxitem/readTypes" var="readTypeUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Page scripts  -->

<!-- <script type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=AIzaSyB9-dRuduoBUGDiW28ByX3Lu4kJX2KtbU8"></script> -->


<!-- Java(optional)  -->

<%
	String timeTemplate = 
			"# if (time && time.length == 17) { #" + 
				"<span>#= time.slice(0, 8) #</span> " +
				"<small>#= time.slice(8, 14) #</small> " +
				"<small>#= time.slice(14) #</small> " +
			"# } else { #" +
				"<span>#= time #</span>" +
			"# } #";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
	<kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<div class="d-flex align-items-center">
	    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    			</div>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column title="번호" field="id" />
		<kendo:grid-column title="RVM명" field="rvm.rvmName"
				template="<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvm.rvmName #</a><a href='javascript:showRvm(#= rvm.id #,\x22#= rvm.rvmName #\x22)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-search text-info'></span></a></div>" />
		<kendo:grid-column title="Trx번호" field="rvmTrx.trxNo" minScreenWidth="600"
			template="<div><span>#= rvmTrx.trxNo #</span><a href='javascript:navToTrx(#= rvmTrx.id #)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-arrow-up-left'></span></a></div>" />
		<kendo:grid-column title="수거일" field="opDate" format="{0:yyyy-MM-dd}" />
		<kendo:grid-column title="유형번호" field="groupId" minScreenWidth="800" />
		<kendo:grid-column title="갯수" field="count" minScreenWidth="1000" />
		<kendo:grid-column title="단가" field="amount" minScreenWidth="1000" />
		<kendo:grid-column title="시간" field="time" template="<%= timeTemplate %>" minScreenWidth="1300" />
		<kendo:grid-column title="바코드" field="barcode" minScreenWidth="1200" />
		<kendo:grid-column title="리필유형" field="type" minScreenWidth="1500" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="유형설명" field="emptiesType" minScreenWidth="1600" />
	</kendo:grid-columns>
	<kendo:grid-filterable extra="false">
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

   		<c:choose>
   		<c:when test="${requestScope['initFilterApplied']}">
        	<kendo:dataSource-filter>
   	    		<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}" >
    	    		<kendo:dataSource-filterItem field="rvmTrx.id" operator="eq" logic="and" value="<%= request.getAttribute(\"trxId\") %>">
	    	    	</kendo:dataSource-filterItem>
       			</kendo:dataSource-filterItem>
       		</kendo:dataSource-filter>
   		</c:when>
   		<c:otherwise>
        	<kendo:dataSource-filter>
       			<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}" >
       			</kendo:dataSource-filterItem>
   	    	</kendo:dataSource-filter>
   		</c:otherwise>
   		</c:choose>
		
		<kendo:dataSource-transport>
			<kendo:dataSource-transport-read url="${readUrl}" dataType="json" type="POST" contentType="application/json" />
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
					<kendo:dataSource-schema-model-field name="id" type="number"/>
					<kendo:dataSource-schema-model-field name="opDate" type="date" />
					<kendo:dataSource-schema-model-field name="groupId" type="number"/>
					<kendo:dataSource-schema-model-field name="count" type="number"/>
					<kendo:dataSource-schema-model-field name="amount" type="number"/>
					<kendo:dataSource-schema-model-field name="overallAmount" type="number"/>
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


/* Kendo 그리드에서 bootstrap dropdownmenu 모두 보이게 */
.k-grid td {
	overflow: visible;
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
			});
		}
	});
	// / Delete
	
});	
</script>

<!-- / Grid button actions  -->


<!--  Root form container -->
<div id="formRoot"></div>


<!--  Scripts -->

<script>

function navToTrx(id) {
	console.log("id: " + id);
	var path = "/eco/rvmtrx?trxid=" + id;
	location.href = path;
}

</script>


<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:ecoRvmOverview />
<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
