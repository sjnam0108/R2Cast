<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/rtnschdtask/read" var="readUrl" />
<c:url value="/eco/rtnschdtask/edit" var="editUrl" />
<c:url value="/eco/rtnschdtask/destroy" var="destroyUrl" />

<c:url value="/eco/rvmmsg/readDestRvmCnt" var="readDestRvmCntUrl" />

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
	String editTemplate = 
			"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
			"<span class='fas fa-pencil-alt'></span></button>";
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
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="single" >
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
		<kendo:grid-column title="수정" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="작업명" field="taskName" />
		<kendo:grid-column title="명령" field="command" template="#:univCommand#" />
		<kendo:grid-column title="기기수" field="rvmCount" sortable="false" filterable="false" />
		<kendo:grid-column title="자동 취소" field="autoCancelMins" minScreenWidth="1350"
				template="#:univAutoCancel#" sortable="false" filterable="false" />
		<kendo:grid-column title="발행" field="published" filterable="false" minScreenWidth="1400"
				template="#=published == 'Y' ? \"<span class='far fa-check'>\" : \"\"#" />
		<kendo:grid-column title="월요일" field="monTime" sortable="false" filterable="false" minScreenWidth="800" />
		<kendo:grid-column title="화요일" field="tueTime" sortable="false" filterable="false" minScreenWidth="800" />
		<kendo:grid-column title="수요일" field="wedTime" sortable="false" filterable="false" minScreenWidth="800" />
		<kendo:grid-column title="목요일" field="thuTime" sortable="false" filterable="false" minScreenWidth="800" />
		<kendo:grid-column title="금요일" field="friTime" sortable="false" filterable="false" minScreenWidth="800" />
		<kendo:grid-column title="토요일" field="satTime" sortable="false" filterable="false" minScreenWidth="1200" />
		<kendo:grid-column title="일요일" field="sunTime" sortable="false" filterable="false" minScreenWidth="1200" />
	</kendo:grid-columns>
	<kendo:grid-filterable>
		<kendo:grid-filterable-messages selectedItemsFormat="{0} 항목 선택됨"/>
	</kendo:grid-filterable>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="taskName" dir="asc"/>
		</kendo:dataSource-sort>
		<kendo:dataSource-filter>
			<kendo:dataSource-filterItem field="site.id" operator="eq" logic="and" value="${sessionScope['currentSiteId']}" />
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
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								작업명
							</label>
							<input name="taskName" type="text" maxlength="25" class="form-control required">
						</div>
					</div>
					<div class="col-sm-6">
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
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								월요일
							</label>
							<input name="monTime" type="text" class="form-control border-none">
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								화요일
							</label>
							<input name="tueTime" type="text" class="form-control border-none">
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								수요일
							</label>
							<input name="wedTime" type="text" class="form-control border-none">
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								목요일
							</label>
							<input name="thuTime" type="text" class="form-control border-none">
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								금요일
							</label>
							<input name="friTime" type="text" class="form-control border-none">
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								토요일
							</label>
							<input name="satTime" type="text" class="form-control border-none">
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								일요일
							</label>
							<input name="sunTime" type="text" class="form-control border-none">
						</div>
					</div>
					<div class="col-sm-2"></div>
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								자동취소
							</label>
							<select name="autoCancel" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
								<option value="5">5 분후</option>
								<option value="10">10 분후</option>
								<option value="30">30 분후</option>
								<option value="60">1 시간후</option>
								<option value="120">2 시간후</option>
								<option value="180">3 시간후</option>
								<option value="360">6 시간후</option>
								<option value="720">12 시간후</option>
								<option value="0">자정</option>
							</select>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								선택 옵션
							</label>
							<label class="custom-control custom-checkbox">
								<input type="checkbox" class="custom-control-input" name="published">
								<span class="custom-control-label">발행</span>
							</label>
						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary" data-style="expand-left" onclick='saveForm1()'>저장</button>
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
	
	$("#form-1 select[name='command']").selectpicker('render');
	$("#form-1 select[name='autoCancel']").selectpicker('render');
	
	bootstrapSelectVal($("#form-1 select[name='command']"), "Reboot.bbmc");
	bootstrapSelectVal($("#form-1 select[name='autoCancel']"), "0");
	
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
        height: 400,
        filter: "contains",
        noDataTemplate: "표시할 자료가 없습니다.",
    });

	$("#form-1 input[name='all-rvms']").click(function(){
		if($(this).is(':checked')){
			$("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").enable(false);
			$("#form-1 select[name='rvms']").data("kendoMultiSelect").enable(false);
		} else {
			$("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").enable(true);
			$("#form-1 select[name='rvms']").data("kendoMultiSelect").enable(true);
		}
	});
	
	$("#form-1 input[name='monTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });
	$("#form-1 input[name='tueTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });
	$("#form-1 input[name='wedTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });
	$("#form-1 input[name='thuTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });
	$("#form-1 input[name='friTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });
	$("#form-1 input[name='satTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });
	$("#form-1 input[name='sunTime']").kendoTimePicker({ format: "HH:mm:ss", parseFormats: ["HH:mm:ss", "HHmmss", "HH mm ss", "HH:mm", "HHmm", "HH mm", "HH"], change: onKendoPickerChange, });

	$("#form-1 input[name='published']").prop("checked", true);
	
	
	$("#form-1 span[name='subtitle']").text(subtitle ? subtitle : "추가");
	
	$("#form-1").validate({
		rules: {
			taskName: {
				minlength: 2,
			},
		}
	});
}


function saveForm1() {
	
	// kendo timepicker validation
	validateKendoTimeValue($("#form-1 input[name='monTime']"));
	validateKendoTimeValue($("#form-1 input[name='tueTime']"));
	validateKendoTimeValue($("#form-1 input[name='wedTime']"));
	validateKendoTimeValue($("#form-1 input[name='thuTime']"));
	validateKendoTimeValue($("#form-1 input[name='friTime']"));
	validateKendoTimeValue($("#form-1 input[name='satTime']"));
	validateKendoTimeValue($("#form-1 input[name='sunTime']"));
	
	if ($("#form-1").valid()) {
		var monTime = kendo.toString($("#form-1 input[name='monTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		var tueTime = kendo.toString($("#form-1 input[name='tueTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		var wedTime = kendo.toString($("#form-1 input[name='wedTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		var thuTime = kendo.toString($("#form-1 input[name='thuTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		var friTime = kendo.toString($("#form-1 input[name='friTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		var satTime = kendo.toString($("#form-1 input[name='satTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		var sunTime = kendo.toString($("#form-1 input[name='sunTime']").data("kendoTimePicker").value(), "HH:mm:ss");
		
		var data = {
			id: Number($("#form-1").attr("rowid")),
    		taskName: $.trim($("#form-1 input[name='taskName']").val()),
			command: $("#form-1 select[name='command']").val(),
			monTime: monTime == null ? "" : monTime,
   			tueTime: tueTime == null ? "" : tueTime,
   			wedTime: wedTime == null ? "" : wedTime,
   			thuTime: thuTime == null ? "" : thuTime,
   			friTime: friTime == null ? "" : friTime,
   			satTime: satTime == null ? "" : satTime,
   			sunTime: sunTime == null ? "" : sunTime,
   			autoCancelMins: Number($("#form-1 select[name='autoCancel']").val()),
   			published: $("#form-1 input[name='published']").is(":checked") ? "Y" : "N",
			rvmGroupIds: $("#form-1 select[name='rvmGroups']").data("kendoMultiSelect").value(),
			rvmIds: $("#form-1 select[name='rvms']").data("kendoMultiSelect").value(),
			toAllRvm: $("#form-1 input[name='all-rvms']").is(':checked') ? "Y" : "N",
		};
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${editUrl}",
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
	
	$("#form-1 input[name='taskName']").val(dataItem.taskName);

	bootstrapSelectVal($("#form-1 select[name='command']"), dataItem.command);
	bootstrapSelectVal($("#form-1 select[name='autoCancel']"), dataItem.autoCancelMins);
	
	$("#form-1 input[name='published']").prop("checked", dataItem.published == "Y");
	
	$("#form-1 input[name='monTime']").data("kendoTimePicker").value(dataItem.monTime);
	$("#form-1 input[name='tueTime']").data("kendoTimePicker").value(dataItem.tueTime);
	$("#form-1 input[name='wedTime']").data("kendoTimePicker").value(dataItem.wedTime);
	$("#form-1 input[name='thuTime']").data("kendoTimePicker").value(dataItem.thuTime);
	$("#form-1 input[name='friTime']").data("kendoTimePicker").value(dataItem.friTime);
	$("#form-1 input[name='satTime']").data("kendoTimePicker").value(dataItem.satTime);
	$("#form-1 input[name='sunTime']").data("kendoTimePicker").value(dataItem.sunTime);

	
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
