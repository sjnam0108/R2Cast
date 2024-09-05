<%@ tag pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!-- URL -->

<c:url value="/dsg/common/updateOpTag" var="updateOpTagUrl" />


<!-- Stb overview modal -->

<div class="modal fade modal-level-plus-1" id="deviceOverviewModal" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header move-cursor">
                <h5 class="modal-title" id="deviceOverviewModalTitle" rowid="-1"></h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">×</span>
                </button>
            </div>
            <div class="modal-body py-1">
				<div class='d-flex align-items-center justify-content-center py-4'>${wait_plaseWait}</div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="btn-device-overview-ok" data-dismiss="modal">${confirm_ok}</button>
                <button type="button" class="btn btn-primary hidden disabled" id="btn-device-overview-save" onclick='saveTagValues()'>${form_save}</button>
            </div>
        </div>
    </div>
</div>

<style>

.rvm-status-tree-node {
	cursor: pointer;
}

</style>

<script>

var isRvmStatusTreeFirstPhase = false;
var childDoc = null;
var rvmOverviewDate = null;

function getRvmId(row) {
	var component = $("#grid").data("kendoGrid");
	if (component) {
		var dataItem = component.dataItem(row);
		if (dataItem) {
			if (dataItem.rvm) {
				if (dataItem.rvm.id) {
					return dataItem.rvm.id;
				}
			}
			if (dataItem.rvmId) {
				return dataItem.rvmId;
			}
			if (dataItem.equipId) {
				return dataItem.equipId;
			}
			if (dataItem.id) {
				return dataItem.id;
			}
		}
	}
	
	return -1;
}

function getTreeRvmId(target) {
	var component = $("#rvmTreeView").data("kendoTreeView");
	if (component) {
		var dataItem = component.dataItem(target);
		if (dataItem) {
			return dataItem.id;
		}
	}
	
	return -1;
}

function attachRvmStatusBarPopover() {
	$('.rvm-status-popover').popover({
		html: true,
		trigger: 'focus',
	    content: function(){
	        var div_id =  "tmp-id-" + $.now();
	        return popoverContent(kendo.format("/dsg/common/rvmstatus?rvmId={0}", 
   					getRvmId($(this).closest("tr"))), div_id);
	    },
	});
}

function popoverContent(link, div_id){
    $.ajax({
        url: link,
        success: function(response){
            $('#'+div_id).html(response);
        }
    });
    return '<div id="'+ div_id +'" class="d-flex justify-content-center align-items-center" style="height: 34px; width: 170px;">${wait_loading}</div>';
}


function showRvm(id, name, date) {
	$("#deviceOverviewModalTitle").attr("rowid", id);
	$("#deviceOverviewModalTitle").text(name);
	
    $("#deviceOverviewModal .modal-body").html("<div class='d-flex align-items-center justify-content-center py-4'>${wait_plaseWait}</div>");
    
    if (date) {
    	rvmOverviewDate = date;
    }
    
	$('#deviceOverviewModal .modal-dialog').draggable({ handle: '.modal-header' });
	$("#deviceOverviewModal").modal();
}

function changeModalButton(code) {
	if (code == "save") {
		$("#btn-device-overview-ok").hide();
		$("#btn-device-overview-save").show();
	} else {
		$("#btn-device-overview-ok").show();
		$("#btn-device-overview-save").hide();
	}
}

function enableSaveButton(val) {
	if (val) {
		$("#btn-device-overview-save").removeClass("disabled");
	} else {
		$("#btn-device-overview-save").addClass("disabled");
	}
}

function saveTagValues() {
	if (childDoc != null && !$("#btn-device-overview-save").hasClass("disabled")) {
		var data = {
			rvmId: Number($("#deviceOverviewModalTitle").attr("rowid")),
			opTag: childDoc.getOpTagValues(),
		}
  		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${updateOpTagUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#deviceOverviewModal").modal("hide");
			},
			error: ajaxSaveError
		});
	}
}


$(document).ready(function() {
	$("#deviceOverviewModal").on('show.bs.modal', function (e) {

		setTimeout(function(){
			$('.modal-backdrop:last-child').addClass('modal-level-plus-1');
		});
		
		if (rvmOverviewDate) {
			var tmpDate = rvmOverviewDate;
			rvmOverviewDate = null;
			$("#deviceOverviewModal .modal-body").load("/dsg/common/rvmoverview?id=" + 
					$("#deviceOverviewModalTitle").attr("rowid") + "&date=" + tmpDate);
		} else {
			$("#deviceOverviewModal .modal-body").load("/dsg/common/rvmoverview?id=" + 
					$("#deviceOverviewModalTitle").attr("rowid"));
		}

	    changeModalButton("");
	});
});

</script>
