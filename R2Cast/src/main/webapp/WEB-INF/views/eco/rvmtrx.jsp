<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/rvmtrx/read" var="readUrl" />
<c:url value="/eco/rvmtrx/destroy" var="destroyUrl" />


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
	String dateTemplate = kr.co.r2cast.utils.Util.getSmartDate();
	String opDtTemplate = kr.co.r2cast.utils.Util.getSmartDate("opDt");
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
		<kendo:grid-column title="RVM명" field="rvm.rvmName" minScreenWidth="600"
				template="<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvm.rvmName #</a><a href='javascript:showRvm(#= rvm.id #,\x22#= rvm.rvmName #\x22)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-search text-info'></span></a></div>" />
		
		<kendo:grid-column title="수거" field="opDt" template="<%= opDtTemplate %>" />
		<kendo:grid-column title="유형 수" field="groupCount" filterable="false" minScreenWidth="700" 
			template="<div><span>#= groupCount #</span><a href='javascript:navToItems(#= id #)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-arrow-down-right'></span></a></div>" />
		<kendo:grid-column title="Trx번호" field="trxNo" minScreenWidth="1000"/>
		<kendo:grid-column title="영수증번호" field="receiptNo" minScreenWidth="1000"/>
		<kendo:grid-column title="영수증금액" field="overallAmount"/>
		<kendo:grid-column title="바코드" field="barcodes" minScreenWidth="1300" />
		<kendo:grid-column title="수거일" field="opDate" format="{0:yyyy-MM-dd}" minScreenWidth="1200" />
		<kendo:grid-column title="생성" field="whoCreationDate" template="<%= dateTemplate %>" minScreenWidth="1400" />
	</kendo:grid-columns>
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
    	    		<kendo:dataSource-filterItem field="id" operator="eq" logic="and" value="<%= request.getAttribute(\"trxId\") %>">
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
					<kendo:dataSource-schema-model-field name="opDt" type="date" />
					<kendo:dataSource-schema-model-field name="opDate" type="date" />
					<kendo:dataSource-schema-model-field name="trxNo" type="number"/>
					<kendo:dataSource-schema-model-field name="receiptNo" type="number"/>
					<kendo:dataSource-schema-model-field name="overallAmount" type="number"/>
					<kendo:dataSource-schema-model-field name="whoCreationDate" type="date"/>
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

function navToItems(id) {
	var path = "/eco/rvmtrxitem?trxid=" + id;
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
