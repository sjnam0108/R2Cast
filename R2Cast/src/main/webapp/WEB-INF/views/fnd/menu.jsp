<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/fnd/menu/create" var="createUrl" />
<c:url value="/fnd/menu/destroy" var="destroyUrl" />
<c:url value="/fnd/menu/dragdrop" var="dragDropUrl" />
<c:url value="/fnd/menu/readMenus" var="readMenuUrl" />


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->

<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 ${sessionScope['loginUser'].icon}"></span>
	${pageTitle}
</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- Main form  -->

<div class="card mb-4">
	<div class="card-body">
		<div class="btn-group">
			<button type="button" class="btn btn-success" id="add-btn"><span class="fas fa-plus-circle fa-lg"></span><span class="ml-2">${label_new}</span></button>
			<button type="button" class="btn btn-default" id="refresh-btn"><span class="fas fa-sync"></span><span class="ml-2">${label_refresh}</span></button>
		</div>

		<div id="menuTree" class="mt-2 mb-0 py-1 px-3" style="font-size: 1.15rem;"></div>
	</div>
	<hr class="m-0">
	<div class="card-body">
		<form id="propForm">
			<input type="hidden" name="id" id="id" value="">
			<input type="hidden" name="oper" id="oper" value="">
			
			<div class="form-row">
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							${label_ukid}
							<span class="text-danger">*</span>
						</label>
						<input name="ukid" type="text" class="form-control required">
					</div>
				</div>
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							${label_url}
						</label>
						<input name="url" type="text" class="form-control">
					</div>
				</div>
			</div>
			<div class="form-row">
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							<span class="pr-1">${label_icon}</span>
							<a href="javascript:void(0)" class="d-none d-xl-inline" title="${tip_iconTitle}" data-toggle="popover" data-trigger="focus" data-content="${tip_icon}" tabindex="0">
								<span class="fa-regular fa-info-circle text-info"></span>
							</a>
						</label>
						<input name="icon" type="text" class="form-control">
					</div>
				</div>
				<div class="col-sm-6">
					<div class="form-group col">
						<label class="form-label">
							${label_group}
						</label>
                		<select name="group" class="selectpicker" data-style="btn-default" data-size="5" data-none-selected-text=""></select>
					</div>
				</div>
			</div>
			<div class="form-row">
				<div class="col-12">
					<div class="form-group col">
						<label class="form-label d-block">
							${form_option}
						</label>
						<label class="custom-control custom-checkbox custom-control-inline">
							<input type="checkbox" class="custom-control-input" name="site-selector">
							<span class="custom-control-label">${label_reqSiteSwitcher}</span>
						</label>
						<label class="custom-control custom-checkbox custom-control-inline">
							<input type="checkbox" class="custom-control-input" name="customized" >
							<span class="custom-control-label">${label_customized}</span>
						</label>
						<label class="custom-control custom-checkbox custom-control-inline">
							<input type="checkbox" class="custom-control-input" name="user-friendly" >
							<span class="custom-control-label">${label_userFriendly}</span>
						</label>
					</div>
				</div>
			</div>
			
			<hr/>

			<button type="button" class="btn btn-primary mr-1" id="save-btn">${form_save}</button>
			<button type="button" class="btn btn-danger mr-1" id="del-btn">${cmd_delete}</button>
			<button type="button" class="btn btn-secondary mr-1" id="unlock-btn">${cmd_unlock}</button>
			<button type="button" class="btn btn-default" id="cancel-btn">${form_cancel}</button>
		</form>
	</div>
</div>


<style>

/* 메뉴 선택 시 선택 항목의 배경 가장자리에 약간의 곡선을 추가 */
div#menuTree span.k-in.k-state-selected {
	border-radius: 4px;
}

</style>


<!-- / Main form  -->


<!--  Scripts -->

<script>

$(document).ready(function() {
   
	$('[data-toggle="popover"]').popover({html:true});
	
    var homogeneous = new kendo.data.HierarchicalDataSource({
    	transport: {
            read: {
                url: "${readMenuUrl}",
                dataType: "json",
                type: "POST",
                contentType: "application/json",
            },
            parameterMap: function(options, type) {
            	return JSON.stringify(options);
            },
        },
        schema: {
            model: {}
        }
    });
    
    var treeView = $("#menuTree").kendoTreeView({
    	dataSource: homogeneous,
    	dragAndDrop: true,
    	messages: {
    		retry: "${treeview_retry}",
    		requestFailed: "${treeview_requestFailed}",
    		loading: "${wait_loading}",
    	},
    	select: onSelect,
    	drop: onDrop,
    });
	
    $("#propForm select[name='group']").append($('<option>', { text: "", value: "" }));
<c:forEach var="item" items="${MenuGroupItems}">
    $("#propForm select[name='group']").append($('<option>', { text: "${item.text}", value: "${item.value}" }));
</c:forEach>


	//Tree event: select
	function onSelect(e) {
		selectNode(e.node);
	}
	
	//Tree event: drop
	function onDrop(e) {
		if (e.valid) {
			var destinationNode = treeView.data("kendoTreeView").dataItem(e.destinationNode);
			var dropPosition = e.dropPosition;

			if (dropPosition == "over" && destinationNode["expanded"] == false 
					&& destinationNode["items"] != null && destinationNode["items"].length == 0) {
				e.preventDefault();
				alert("${treeview_msg_expandFirst}");
				return;
			}

			var sourceNodeId = treeView.data("kendoTreeView").dataItem(e.sourceNode).get("id");
			var destinationNodeId = destinationNode.get("id");
			
        	var data = {
        			sourceId: sourceNodeId,
        			destId: destinationNodeId,
        			position: dropPosition,
        	};

			draggedNode = e.sourceNode;
        	
        	$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${dragDropUrl}",
					data: JSON.stringify(data),
					async: false,
					success: function (data, status, xhr) {
						showOperationSuccessMsg();
					setTimeout(function() {
						treeView.data("kendoTreeView").select(e.sourceNode);
						selectNode(e.sourceNode);
					}, 50);
					},
					error: ajaxOperationError
				});
		}
	}
	// / Tree event: drop
	
	add();

    
	// Button Group: Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		add();
	});
	// / Button Group: Add

	// Button Group: Refresh
	$("#refresh-btn").click(function(e) {
		e.preventDefault();
		
		treeView.data("kendoTreeView").dataSource.read();
		add();
	});
	// / Button Group: Refresh

	
	// Save
	$("#save-btn").click(function(e) {
		if ($("#propForm").valid()) {
        	var treeView = $("#menuTree").data("kendoTreeView");
        	var selectedNode = treeView.select();
        	
        	var targetId = "0";
        	if (selectedNode.length > 0) {
        		var dataItem = treeView.dataItem(selectedNode);
        		targetId = dataItem["id"];
        	}

        	if (targetId == "0" && $("#oper").val() == "Update") {
        		return;
        	}
        	
        	var data = {
        			id: targetId,
        			ukid: $("#propForm input[name='ukid']").val(),
        			url: $("#propForm input[name='url']").val(),
        			icon: $("#propForm input[name='icon']").val(),
        			group: $("#propForm select[name='group']").val(),
        			siteSelector: $("#propForm input[name='site-selector']").is(":checked"),
        			customized: $("#propForm input[name='customized']").is(":checked"),
        			userFriendly: $("#propForm input[name='user-friendly']").is(":checked"),
        			oper: $("#oper").val(),
        	};
        	
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${createUrl}",
				data: JSON.stringify(data),
				success: function (data, status, xhr) {
					var dataItem = treeView.dataItem(selectedNode);
						
					if ($("#oper").val() == "Update") {
						dataItem.set("text", data.text);
						dataItem.set("id", data.id);
						dataItem.set("custom1", data.custom1);
						dataItem.set("custom2", data.custom2);
						dataItem.set("custom3", data.custom3);
						dataItem.set("custom4", data.custom4);
						dataItem.set("custom5", data.custom5);
						dataItem.set("custom6", data.custom6);
						dataItem.set("custom7", data.custom7);
							
						selectNode(selectedNode);
					} else {
						if (selectedNode.length == 0) {
							selectedNode = null;
						}

						treeView.append({
							childrenCount: 0,
							custom1: data.custom1,
							custom2: data.custom2,
							custom3: data.custom3,
							custom4: data.custom4,
							expanded: false,
							hasChildren: false,
							id: data.id,
							items: null,
							siblingSeq: data.siblingSeq,
							spriteCssClass: data.spriteCssClass,
							text: data.text,
						}, selectedNode);
							
						var newNodeDataItem = treeView.dataSource.get(data.id);
						var newNode = treeView.findByUid(newNodeDataItem.uid);
							
						treeView.select(newNode);
							
						selectNode(newNode);
					}
					showSaveSuccessMsg();
				},
				error: ajaxSaveError
			});
        }
	});
	// / Save
	
	// Delete
	$("#del-btn").click(function(e) {
    	var treeView = $("#menuTree").data("kendoTreeView");
    	var selectedNode = treeView.select();

    	if (selectedNode.length == 0) {
    		return;
    	}
    	
   		var dataItem = treeView.dataItem(selectedNode);
   		
		showDelConfirmModal(function(result) {
			if (result) {
				$.ajax({
					type: "POST",
					contentType: "application/json",
					dataType: "json",
					url: "${destroyUrl}",
					data: JSON.stringify({id: dataItem["id"]}),
					success: function (form) {
						treeView.remove(selectedNode);
						showDeleteSuccessMsg();
						add();
					},
					error: ajaxDeleteError
				});
			}
		});
	});
	// / Delete
	
	// Unlock
	$("#unlock-btn").click(function(e) {
		$("#propForm input[name='ukid']").removeAttr('readonly');
		$("#propForm input[name='url']").removeAttr('readonly');
		$("#propForm input[name='icon']").removeAttr('readonly');
		
		$("#propForm input[name='site-selector']").prop('disabled', false);
		$("#propForm input[name='customized']").prop('disabled', false);
		$("#propForm input[name='user-friendly']").prop('disabled', false);

		bootstrapSelectDisabled($("#propForm select[name='group']"), false);
			
		$("#unlock-btn").hide();
		$("#save-btn").show();
		$("#del-btn").hide();
		$("#cancel-btn").show();
			
		$("#ukid").focus().select();
	});
	// / Unlock
	
	// Cancel
	$("#cancel-btn").click(function(e) {
    	$("#menuTree").data("kendoTreeView").select($());
    	add();
	});
	// / Cancel
});	


// Method: add
function add() {
	$("#propForm input[name='ukid']").val("");
	$("#propForm input[name='url']").val("");
	$("#propForm input[name='icon']").val("");
	$("#propForm input[name='site-selector']").prop("checked", false);
	$("#propForm input[name='customized']").prop("checked", false);
	$("#propForm input[name='user-friendly']").prop("checked", false);

	bootstrapSelectVal($("#propForm select[name='group']"), "");

	$("#oper").val("Add");
		
	$("#propForm input[name='ukid']").removeAttr('readonly');
	$("#propForm input[name='url']").removeAttr('readonly');
	$("#propForm input[name='icon']").removeAttr('readonly');
	
	$("#propForm input[name='site-selector']").prop('disabled', false);
	$("#propForm input[name='customized']").prop('disabled', false);
	$("#propForm input[name='user-friendly']").prop('disabled', false);
	
	bootstrapSelectDisabled($("#propForm select[name='group']"), false);

	$("#unlock-btn").hide();
	$("#save-btn").show();
	$("#del-btn").hide();
	$("#cancel-btn").show();
		
	var selectedNode = $("#menuTree").data("kendoTreeView").select();
	if (selectedNode.length != 0) {
		$("#menuTree").data("kendoTreeView").expand(selectedNode);
	}
}
// / Method: add

// Method: selectNode
function selectNode(node) {
	var dataItem = $("#menuTree").data("kendoTreeView").dataItem(node);

	$("#propForm input[name='ukid']").attr('readonly', 'readonly');
	$("#propForm input[name='url']").attr('readonly', 'readonly');
	$("#propForm input[name='icon']").attr('readonly', 'readonly');

	$("#propForm input[name='site-selector']").prop('disabled', true);
	$("#propForm input[name='customized']").prop('disabled', true);
	$("#propForm input[name='user-friendly']").prop('disabled', true);
	
	bootstrapSelectDisabled($("#propForm select[name='group']"), true);
	
	$("#propForm input[name='ukid']").val(dataItem["custom1"]);
	$("#propForm input[name='url']").val(dataItem["custom2"]);
	$("#propForm input[name='icon']").val(dataItem["custom3"]);
	$("#propForm input[name='site-selector']").prop("checked", dataItem["custom5"] == "Y");
	$("#propForm input[name='customized']").prop("checked", dataItem["custom6"] == "Y");
	$("#propForm input[name='user-friendly']").prop("checked", dataItem["custom7"] == "Y");
	
	bootstrapSelectVal($("#propForm select[name='group']"), dataItem["custom4"]);
	
	$("#id").val(dataItem["id"]);
	$("#oper").val("Update");

	$("#unlock-btn").show();
	$("#save-btn").hide();
	$("#del-btn").show();
	$("#cancel-btn").show();
}
// / Method: selectNode

</script>

<!--  Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- One-time page validation  -->

<script>

$(document).ready(function() {
 	// set validation
	$("#propForm").validate({
		rules: {
			ukid: {
				minlength: 2, maxlength: 20, alphanumeric: true,
			},
		}
	});
	
});	

</script>

<!-- / One-time page validation  -->


<!-- Closing tags -->

<common:base />
<common:pageClosing />
