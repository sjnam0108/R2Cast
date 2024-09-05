<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/montask/read" var="readUrl" />
<c:url value="/eco/montask/create" var="createUrl" />
<c:url value="/eco/montask/destroy" var="destroyUrl" />

<c:url value="/eco/montask/readCommands" var="readCommandUrl" />
<c:url value="/eco/montask/readDestRvmCnt" var="readDestRvmCntUrl" />
<c:url value="/eco/montask/readStatusTypes" var="readStatusTypeUrl" />

<c:url value="/eco/common/readAccRvms" var="readCmnAccRvmUrl" />
<c:url value="/eco/common/readAccRvmGroups" var="readCmnAccRvmGroupUrl" />


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
String rvmTemplate =
	"# if (rvm) { #" + "<div class='d-flex align-items-center'><a href='javascript:void(0)' class='rvm-status-popover' tabindex='0'>#= rvm.rvmName #</a><a href='javascript:showRvm(#= rvm.id #,\"#= rvm.rvmName #\")' class='btn btn-default btn-xs icon-btn ml-1'><span class='fa-regular fa-search text-info'></span></a></div>" +
	"# } else { #" + "<span></span>" +
	"# } #";
	String statusTemplate =
	"# if (flagCode == 'C') { #" + "<span title='#= statusTip #' class='fa-regular fa-trash-can text-info'></span>" +
	"# } else if (flagCode == 'F') { #" + "<span title='#= statusTip #' class='fa-regular fa-hand-paper text-danger'></span>" +
	"# } else if (flagCode == 'P') { #" + "<span title='#= statusTip #' class='fa-regular fa-flag text-success'></span>" +
	"# } else if (flagCode == 'R') { #" + "<span title='#= statusTip #' class='fa-regular fa-asterisk text-muted'></span>" +
	"# } else if (flagCode == 'S') { #" + "<span title='#= statusTip #' class='fa-regular fa-flag text-blue'></span>" +
	"# } else if (flagCode == 'W') { #" + "<span title='#= statusTip #' class='fa-regular fa-hourglass-half'></span>" +
	"# } else { #" +  
	"# } #";
	String paramTemplate =
	"# if (params) { #" + "<span data-toggle='tooltip' title='#= params #'><span class='fa-regular fa-info-circle text-info'></span></span>" +
	"# } else { #" + "<span></span>" +
	"# } #";
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
	String destDateTemplate = kr.co.r2cast.utils.Util.getSmartDate("destDate");
	String executedDateTemplate = kr.co.r2cast.utils.Util.getSmartDate("executedDate");
	String requestedDateTemplate = kr.co.r2cast.utils.Util.getSmartDate("requestedDate");
	String cancelDateTemplate = kr.co.r2cast.utils.Util.getSmartDate("cancelDate");
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
					<div class="pr-1">
		    			<button id="add-btn" type="button" class="btn btn-outline-success">추가</button>
					</div>
	    			<button type="button" class="btn btn-default d-none d-sm-inline k-grid-excel">엑셀</button>
    			</div>
    		</div>
    		<div class="float-right">
    			<button id="delete-btn" type="button" class="btn btn-danger">삭제</button>
    		</div>
    	</div>
	</kendo:grid-toolbarTemplate>
	<kendo:grid-columns>
		<kendo:grid-column title="번호" field="id" width="100px" />
		<kendo:grid-column title="RVM명" field="rvm.rvmName" sortable="false" 
				template="<%= rvmTemplate %>" />
		<kendo:grid-column title="명령" field="command" sortable="false" template="#:univCommand#" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcTextOnly">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readCommandUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
        <kendo:grid-column title="상태" field="status" sortable="false" filterable="true" template="<%= statusTemplate %>" >
			<kendo:grid-column-filterable multi="true" itemTemplate="kfcIconText">
				<kendo:dataSource>
					<kendo:dataSource-transport>
						<kendo:dataSource-transport-read url="${readStatusTypeUrl}" dataType="json" type="POST" contentType="application/json" />
					</kendo:dataSource-transport>
				</kendo:dataSource>
			</kendo:grid-column-filterable>
		</kendo:grid-column>
		<kendo:grid-column title="실행예정" field="destDate" template="<%= destDateTemplate %>" minScreenWidth="1000"  />
		<kendo:grid-column title="실행" field="executedDate" template="<%= executedDateTemplate %>" minScreenWidth="1100" 
				filterable="false" sortable="false"/>
		<kendo:grid-column title="요청" field="requestedDate" template="<%= requestedDateTemplate %>" minScreenWidth="1200" 
				filterable="false" sortable="false" />
		<kendo:grid-column title="자동취소" field="cancelDate" template="<%= cancelDateTemplate %>" minScreenWidth="1200"  />
        <kendo:grid-column title="전달인자" field="params" sortable="false" minScreenWidth="1300" 
        		template="<%= paramTemplate %>" />
		<kendo:grid-column title="정기?" field="routineTask" sortable="false"  minScreenWidth="1400" 
				filterable="false" template="#=routineTask ? \"<span class='fa-regular fa-check'>\" : \"\"#"/>
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:grid-dataBound>
		<script>
			function grid_dataBound(e) {
				$('[data-toggle="tooltip"]').tooltip();
				
				attachRvmStatusBarPopover();
			}
		</script>
	</kendo:grid-dataBound>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="id" dir="desc"/>
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
					<kendo:dataSource-schema-model-field name="destDate" type="date"/>
					<kendo:dataSource-schema-model-field name="cancelDate" type="date"/>
					<kendo:dataSource-schema-model-field name="requestedDate" type="date"/>
					<kendo:dataSource-schema-model-field name="executedDate" type="date"/>
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
			<div class="modal-body modal-bg-color py-3">
				<div class="form-row pr-1">
					<div class="d-flex justify-content-end w-100 pr-3">
						<label class="custom-control custom-checkbox">
							<input type="checkbox" class="custom-control-input" name="all-rvms">
							<span class="custom-control-label">
								<span class="badge badge-outline-dark font-weight-normal small">모든 기기 대상</span>
							</span>
						</label>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-12">
						<div class="form-group col">
							<label class="form-label">
								명령
							</label>
							<select name="command" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
								<optgroup label="동작">
									<option value="Reboot.bbmc">STB 재시작(리부팅)</option>
									<option value="RestartAgent.bbmc">에이전트 재시작</option>
									<option value="UpdateAgent.bbmc">에이전트 업데이트</option>
								</optgroup>
								<optgroup label="업로드">
									<option value="UploadDebugFile.bbmc">디버그 로그 업로드</option>
									<option value="UploadTrxFile.bbmc">원시 트랜잭션 업로드</option>
								</optgroup>
							</select>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-12">
						<div class="form-group col">
							<label class="form-label">
								RVM 그룹
							</label>
							<select name="rvmGroups" class="form-control border-none"></select>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-12">
						<div class="form-group col">
							<label class="form-label">
								RVM
							</label>
							<select name="rvms" class="form-control border-none"></select>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-12">
						<div class="form-group col mb-1">
							<button type="button" class="btn btn-xs btn-outline-secondary collapsed" data-toggle="collapse" data-target="\\\#adv-opt">
								<span name="adv-opt-show"><span class="fas fa-caret-up"></span></span>
								<span name="adv-opt-hide"><span class="fas fa-caret-down"></span></span>
								<span class="pl-1">고급 옵션</span>
							</button>
						</div>
					</div>
				</div>
				<div class="form-row collapse" id="adv-opt">
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								실행예약
							</label>
							<input name="destDate" type="text" class="form-control border-none">
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								자동취소
							</label>
							<input name="cancelDate" type="text" class="form-control border-none">
						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary disabled" data-style="expand-left" onclick='saveForm1()'>저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!--  Scripts -->

<script>

function checkSaveValidation() {
	
	var allRvms = $("#form-1 input[name='all-rvms']").is(':checked');
	var rvmGroupIds = $("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").value();
	var rvmIds = $("#form-1 select[name='rvms']").data("kendoMultiSelect").value();
	
	var ret = allRvms || rvmGroupIds.length + rvmIds.length > 0;
	
	if (ret) {
		$("#form-1 button[name='save-btn']").removeClass("disabled");
	} else {
		$("#form-1 button[name='save-btn']").addClass("disabled");
	}
	
	return ret;
}


function initForm1(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-1").html()));
	
	$("#form-1 select[name='command']").selectpicker('render');
	
	bootstrapSelectVal($("#form-1 select[name='command']"), "Reboot.bbmc");
	
	$("#form-1 input[name='destDate']").kendoDateTimePicker({
		format: "yyyy-MM-dd HH:mm", 
		parseFormats: [
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HHmm", "yyyy-MM-dd HH mm", "yyyy-MM-dd HH",
			"yyyyMMdd HH:mm", "yyyyMMdd HHmm", "yyyyMMdd HH mm", "yyyyMMdd HH",
			"yyyy-MM-dd", "yyyyMMdd", "MMdd HH mm", "MMdd HHmm", "MMdd HH", "MMdd"
		],
		change: onKendoPickerChange, 
	});
	
    $("#form-1 select[name='rvmGroups']").kendoMultiSelect({
        dataTextField: "rvmGroupName",
        dataValueField: "id",
        tagTemplate: "<%= rvmGroupTemplate %>",
        itemTemplate: "<%= rvmGroupTemplate %>",
        dataSource: {
            transport: {
                read: {
                    dataType: "json",
                    url: "${readCmnAccRvmGroupUrl}",
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
	    	checkSaveValidation();
	    },
        noDataTemplate: "표시할 자료가 없습니다.",
    });

    $("#form-1 select[name='rvms']").kendoMultiSelect({
        dataTextField: "rvmName",
        dataValueField: "id",
        tagTemplate: "<span class='far fa-flag text-gray'></span><span class='pl-2'>#:data.rvmName#</span>",
        itemTemplate: "<span class='far fa-flag text-gray'></span><span class='pl-2'>#:data.rvmName#</span>",
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
	    	checkSaveValidation();
	    },
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });

	var cancelDateDefault = new Date();
	cancelDateDefault.setHours(24, 0, 0, 0);
	
	$("#form-1 input[name='cancelDate']").kendoDateTimePicker({
		format: "yyyy-MM-dd HH:mm", 
		parseFormats: [
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HHmm", "yyyy-MM-dd HH mm", "yyyy-MM-dd HH",
			"yyyyMMdd HH:mm", "yyyyMMdd HHmm", "yyyyMMdd HH mm", "yyyyMMdd HH",
			"yyyy-MM-dd", "yyyyMMdd", "MMdd HH mm", "MMdd HHmm", "MMdd HH", "MMdd"
		],
		change: onKendoPickerChange, 
		value: cancelDateDefault,
	});
	
	$("#form-1 input[name='all-rvms']").click(function(){
		if($(this).is(':checked')){
			$("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").enable(false);
			$("#form-1 select[name='rvms']").data("kendoMultiSelect").enable(false);
		} else {
			$("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").enable(true);
			$("#form-1 select[name='rvms']").data("kendoMultiSelect").enable(true);
		}
		
		checkSaveValidation();
	});
	
	$("#form-1 span[name='adv-opt-hide']").hide();
	
	$("#adv-opt").on('show.bs.collapse', function(){
		$("#form-1 span[name='adv-opt-show']").hide();
		$("#form-1 span[name='adv-opt-hide']").show();
	});
	
	$("#adv-opt").on('hide.bs.collapse', function(){
		$("#form-1 span[name='adv-opt-show']").show();
		$("#form-1 span[name='adv-opt-hide']").hide();
	});
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
}


function saveForm1() {
	
	if (checkSaveValidation()) {
		
		// kendo datepicker validation
		validateKendoDateTimeValue($("#form-1 input[name='destDate']"));
		validateKendoDateTimeValue($("#form-1 input[name='cancelDate']"));
		
		// 실행예약 및 자동취소 검증 및 값 설정
		var destDate = $("#form-1 input[name='destDate']").data("kendoDateTimePicker").value();
		var cancelDate = $("#form-1 input[name='cancelDate']").data("kendoDateTimePicker").value();
		
		if (cancelDate != null) {
			var current = new Date();
			
			if (cancelDate < current) {
				showAlertModal("danger", "자동취소는 현재 시간보다 이전일 수는 없습니다.");
				return;
			}
		}
		
		if (destDate != null && cancelDate != null) {
			if (destDate > cancelDate) {
				showAlertModal("danger", "자동취소가 실행예약보다 이전일 수는 없습니다.");
				return;
			}
		}
		
		if (cancelDate == null) {
			if (destDate == null) {
				destDate = new Date();
			}
			
			var tmpDate = new Date(destDate);
			tmpDate.setHours(24, 0, 0, 0);
			
			$("#form-1 input[name='cancelDate']").data("kendoDateTimePicker").value(tmpDate);
		}
		

		// 대상 기기 수 파악
		var data = {
			rvmGroupIds: $("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").value(),
			rvmIds: $("#form-1 select[name='rvms']").data("kendoMultiSelect").value(),
			toAllRvm: $("#form-1 input[name='all-rvms']").is(':checked') ? "Y" : "N",
		};
		
    	$.ajax({
    		type: "POST",
    		contentType: "application/json",
    		dataType: "json",
    		url: "${readDestRvmCntUrl}",
    		data: JSON.stringify(data),
    		success: function (data, status) {
    			if (data >= 100) {
    				var msg = "총 {0} 건에 대한 저장을 수행할 예정입니다. 계속 진행하시겠습니까?".replace("{0}", "<strong>" + data + "</strong>");
    				
    				showConfirmModal(msg, function(result) {
    					if (result) {
    						saveFormData();
    					}
    				});
    			} else if (data == 0) {
    				showAlertModal("info", "대상 기기가 확인되지 않습니다.");
    				return;
    			} else {
    				saveFormData();
    			}
    		},
    		error: function(e) {
				ajaxReadError(e);
    		}
    	});
	}
}


function saveFormData() {
	
	var data = {
		command: $("#form-1 select[name='command']").val(),
		execTime: "",
		destDate: $("#form-1 input[name='destDate']").data("kendoDateTimePicker").value(),
		cancelDate: $("#form-1 input[name='cancelDate']").data("kendoDateTimePicker").value(),
		rvmGroupIds: $("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").value(),
		rvmIds: $("#form-1 select[name='rvms']").data("kendoMultiSelect").value(),
		toAllRvm: $("#form-1 input[name='all-rvms']").is(':checked') ? "Y" : "N",
	};
	
	$.ajax({
		type: "POST",
		contentType: "application/json",
		dataType: "json",
		url: "${createUrl}",
		data: JSON.stringify(data),
		success: function (form) {
			showSaveSuccessMsg();
			$("#form-modal-1").modal("hide");
			$("#grid").data("kendoGrid").dataSource.read();
		},
		error: ajaxSaveError
	});
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
