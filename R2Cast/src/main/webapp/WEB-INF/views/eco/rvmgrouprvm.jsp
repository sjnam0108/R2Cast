<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/rvmgrouprvm/create" var="createUrl" />
<c:url value="/eco/rvmgrouprvm/read" var="readUrl" />
<c:url value="/eco/rvmgrouprvm/destroy" var="destroyUrl" />

<c:url value="/eco/common/readAccRvms" var="readCmnAccRvmUrl" />
<c:url value="/eco/common/readDSRRvmGroups" var="readRvmGroupUrl" />


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
	String rvmGroupTemplate =
			"<span class='fas fa-folder text-" + 
			"# if (category == 'R') { #" + "red" +
			"# } else if (category == 'O') { #" + "orange" +
			"# } else if (category == 'Y') { #" + "yellow" +
			"# } else if (category == 'G') { #" + "green" +
			"# } else if (category == 'B') { #" + "blue" +
			"# } else if (category == 'P') { #" + "purple" +
			"# } #" +
			"'></span><span class='pl-2'>#:data.rvmGroupName#</span>";
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" filterable="true" groupable="true" pageable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}">
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
		<kendo:grid-column title="RVM 그룹" field="rvmGroup.rvmGroupName"
				groupHeaderTemplate="RVM 그룹: #= value # (${tmpl_count}: #= count #)"/>
		<kendo:grid-column title="RVM" field="rvm.rvmName" 
				template="<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvm.rvmName #</a><a href='javascript:showRvm(#= rvm.id #,\x22#= rvm.rvmName #\x22)' class='btn btn-default btn-xs icon-btn ml-1'><span class='fas fa-search text-info'></span></a></div>" />
	</kendo:grid-columns>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				attachRvmStatusBarPopover();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" serverAggregates="true" error="kendoReadError">
		<kendo:dataSource-group>
			<kendo:dataSource-groupItem field="rvmGroup.rvmGroupName" />
		</kendo:dataSource-group>
		<kendo:dataSource-aggregate>
			<kendo:dataSource-aggregateItem aggregate="count" field="rvmGroup.rvmGroupName"/>
		</kendo:dataSource-aggregate>
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="rvm.rvmName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-filter>
			<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}">
			</kendo:dataSource-filterItem>
		</kendo:dataSource-filter>
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
		<kendo:dataSource-schema data="data" total="total" groups="data" aggregates="aggregates">
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
				<div class="form-group col">
					<label class="form-label">
						RVM 그룹
						<span class="text-danger">*</span>
					</label>
					<div name="rvmGroups-con">
						<select name="rvmGroups" class="form-control border-none"></select>
					</div>
					<label name="rvmGroups-feedback" for="rvmGroups" class="error invalid-feedback"></label>
				</div>
				<div class="form-group col">
					<label class="form-label">
						RVM
						<span class="text-danger">*</span>
					</label>
					<div name="rvms-con">
						<select name="rvms" class="form-control border-none"></select>
					</div>
					<label name="rvms-feedback" for="rvms" class="error invalid-feedback"></label>
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

// Form validator
var validator = null;

// Form error obj
var errors = {};


function validateMSValue(name) {
	
	var selector = null;
	
	if (name == "rvmGroups") {
		selector = $("#form-1 select[name='rvmGroups']");
	} else if (name == "rvms") {
		selector = $("#form-1 select[name='rvms']");
	} else {
		return;
	}
	
	var ids = selector.data("kendoMultiSelect").value();

	if (ids.length == 0) {
		errors[name] = "이 항목의 값을 선택하십시오.";
	} else {
		delete errors[name];
	}
}

function checkMSMessage(name) {
	
	var selector = null;
	var container = null;
	var feedback = null;
	
	if (name == "rvmGroups") {
		selector = $("#form-1 select[name='rvmGroups']");
		container = $("#form-1 div[name='rvmGroups-con']");
		feedback = $("#form-1 label[name='rvmGroups-feedback']");
	} else if (name == "rvms") {
		selector = $("#form-1 select[name='rvms']");
		container = $("#form-1 div[name='rvms-con']");
		feedback = $("#form-1 label[name='rvms-feedback']");
	} else {
		return;
	}
	
	var ids = selector.data("kendoMultiSelect").value();

	if (ids.length == 0) {
		container.addClass("is-invalid");
		feedback.addClass("small d-block").css("display", "block");
	} else {
		container.removeClass("is-invalid");
		feedback.removeClass("small d-block").css("display", "none");
	}
}

function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	

    $("#form-1 select[name='rvmGroups']").kendoMultiSelect({
        dataTextField: "rvmGroupName",
        dataValueField: "id",
        tagTemplate: "<%= rvmGroupTemplate %>",
        itemTemplate: "<%= rvmGroupTemplate %>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readRvmGroupUrl}",
                    type: "POST",
                    contentType: "application/json"
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
            schema: {
            	data: "data",
            	total: "total"
            },
			error: kendoReadError
        },
	    change: function(e) {
	    	checkMSMessage("rvmGroups");
	    },
        noDataTemplate: "표시할 자료가 없습니다.",
    });

    $("#form-1 select[name='rvms']").kendoMultiSelect({
        dataTextField: "rvmName",
        dataValueField: "id",
        tagTemplate: "<span class='far fa-flag text-gray'></span>" + 
        			 "<span class='pl-2'>#:data.rvmName#</span>",
        itemTemplate: "<span class='far fa-flag text-gray'></span>" +
        		      "<span class='pl-2'>#:data.rvmName#</span>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readCmnAccRvmUrl}",
                    type: "POST",
                    contentType: "application/json",
					data: JSON.stringify({}),
                },
                parameterMap: function (options) {
            		return JSON.stringify(options);	
                }
            },
			error: kendoReadError
        },
	    change: function(e) {
	    	checkMSMessage("rvms");
	    },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });
    
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "${form_add}");
	
	validator = $("#form-1").validate();
}

function saveForm1() {

	errors = {};
	
	validateMSValue("rvmGroups");
	validateMSValue("rvms");
	
	if (Object.keys(errors).length == 0) {
		var rvmGroupIds = $("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").value();
		var rvmIds = $("#form-1 select[name='rvms']").data("kendoMultiSelect").value();

		if (rvmGroupIds.length > 0 && rvmIds.length > 0) {
			var data = {
				rvmGroupIds: rvmGroupIds,
				rvmIds: rvmIds,
			};
        	
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${createUrl}",
				data: JSON.stringify(data),
				success: function (data, status, xhr) {
					showAlertModal("success", JSON.parse(xhr.responseText));
					$("#form-modal-1").modal("hide");
					$("#grid").data("kendoGrid").dataSource.read();
				},
				error: ajaxSaveError
			});
		}
	} else {
		validator.showErrors(errors);
		
		checkMSMessage("rvmGroups");
		checkMSMessage("rvms");
	}
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />
<func:ecoRvmOverview />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
