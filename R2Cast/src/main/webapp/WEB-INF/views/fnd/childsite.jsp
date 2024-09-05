<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/childsite/create" var="createUrl" />
<c:url value="/fnd/childsite/read" var="readUrl" />
<c:url value="/fnd/childsite/readSites" var="readSiteUrl" />
<c:url value="/fnd/childsite/destroy" var="destroyUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" filterable="true" groupable="true" pageable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}">
	<kendo:grid-excel fileName="${pageTitle}.xlsx" allPages="true" proxyURL="/proxySave"/>
	<kendo:grid-pageable refresh="true" buttonCount="5" pageSize="10" pageSizes="${pageSizesNormal}" />
    <kendo:grid-toolbarTemplate>
    	<div class="clearfix">
    		<div class="float-left">
    			<button id="add-btn" type="button" class="btn btn-outline-success">${cmd_add}</button>
    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">${cmd_excel}</button>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">${cmd_delete}</button>
    		</div>
    	</div>
   	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column title="${title_parentShortName}" field="parentSite.shortName"
				groupHeaderTemplate="${title_parentShortName}: #= value # (${tmpl_count}: #= count #)"/>
		<kendo:grid-column title="${title_parentSiteName}" field="parentSite.siteName" minScreenWidth="500" />
		<kendo:grid-column title="${title_childShortName}" field="childSite.shortName" />
		<kendo:grid-column title="${title_childSiteName}" field="childSite.siteName" minScreenWidth="700" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" serverAggregates="true" error="kendoReadError">
		<kendo:dataSource-group>
			<kendo:dataSource-groupItem field="parentSite.shortName" />
		</kendo:dataSource-group>
		<kendo:dataSource-aggregate>
			<kendo:dataSource-aggregateItem aggregate="count" field="parentSite.shortName"/>
		</kendo:dataSource-aggregate>
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="childSite.shortName" dir="asc"/>
		</kendo:dataSource-sort>
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
						${label_parent}
						<span class="text-danger">*</span>
					</label>
					<div name="parents-con">
						<select name="parents" class="form-control border-none"></select>
					</div>
					<label name="parents-feedback" for="parents" class="error invalid-feedback"></label>
				</div>
				<div class="form-group col">
					<label class="form-label">
						${label_child}
						<span class="text-danger">*</span>
					</label>
					<div name="children-con">
						<select name="children" class="form-control border-none"></select>
					</div>
					<label name="children-feedback" for="children" class="error invalid-feedback"></label>
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

<!--  / Forms -->



<!--  Scripts -->

<script>

// Form validator
var validator = null;

// Form error obj
var errors = {};


function validateMSValue(name) {
	
	var selector = null;
	
	if (name == "parents") {
		selector = $("#form-1 select[name='parents']");
	} else if (name == "children") {
		selector = $("#form-1 select[name='children']");
	} else {
		return;
	}
	
	var ids = selector.data("kendoMultiSelect").value();

	if (ids.length == 0) {
		errors[name] = "${form_selectReq}";
	} else {
		delete errors[name];
	}
}

function checkMSMessage(name) {
	
	var selector = null;
	var container = null;
	var feedback = null;
	
	if (name == "parents") {
		selector = $("#form-1 select[name='parents']");
		container = $("#form-1 div[name='parents-con']");
		feedback = $("#form-1 label[name='parents-feedback']");
	} else if (name == "children") {
		selector = $("#form-1 select[name='children']");
		container = $("#form-1 div[name='children-con']");
		feedback = $("#form-1 label[name='children-feedback']");
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
	

    $("#form-1 select[name='parents']").kendoMultiSelect({
        dataTextField: "ukid",
        dataValueField: "id",
        tagTemplate: "<span class='fas fa-globe text-gray'></span>" + 
        			 "<span class='pl-2' title='#:data.siteName#'>#:data.shortName#</span>",
        itemTemplate: "<span class='fas fa-globe text-gray'></span>" +
        		      "<span class='pl-2' title='#:data.siteName#'>#:data.shortName#</span>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readSiteUrl}",
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
	    	checkMSMessage("parents");
	    },
        noDataTemplate: "${control_noRows}",
    });

    $("#form-1 select[name='children']").kendoMultiSelect({
        dataTextField: "ukid",
        dataValueField: "id",
        tagTemplate: "<span class='fas fa-globe text-gray'></span>" + 
        			 "<span class='pl-2' title='#:data.siteName#'>#:data.shortName#</span>",
        itemTemplate: "<span class='fas fa-globe text-gray'></span>" +
        		      "<span class='pl-2' title='#:data.siteName#'>#:data.shortName#</span>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readSiteUrl}",
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
	    	checkMSMessage("children");
	    },
        noDataTemplate: "${control_noRows}",
    });
    
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "${form_add}");
	
	validator = $("#form-1").validate();
}


function saveForm1() {

	errors = {};
	
	validateMSValue("parents");
	validateMSValue("children");
	
	if (Object.keys(errors).length == 0) {
		var parentSiteIds = $("#form-1 select[name='parents']").data("kendoMultiSelect").value();
		var childSiteIds = $("#form-1 select[name='children']").data("kendoMultiSelect").value();

		if (parentSiteIds.length > 0 && childSiteIds.length > 0) {
			var data = {
				parentSiteIds: parentSiteIds,
				childSiteIds: childSiteIds,
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
		
		checkMSMessage("parents");
		checkMSMessage("children");
	}
}

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
