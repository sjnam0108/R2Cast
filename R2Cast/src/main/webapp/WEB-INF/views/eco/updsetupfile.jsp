<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/updsetupfile/create" var="createUrl" />
<c:url value="/eco/updsetupfile/read" var="readUrl" />
<c:url value="/eco/updsetupfile/update" var="updateUrl" />
<c:url value="/eco/updsetupfile/destroy" var="destroyUrl" />


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

<link rel="stylesheet" href="/resources/vendor/lib/smartwizard/smartwizard.css">

<script src="/resources/vendor/lib/smartwizard/smartwizard.js"></script>


<!-- Java(optional)  -->

<%
	String editTemplate =
	"<div class='text-nowrap'>" +
		"<button type='button' onclick='edit(#= id #)' class='btn icon-btn btn-sm btn-outline-success borderless'>" + 
		"<span class='fas fa-pencil-alt'></span></button>" +
		"<span class='d-none d-sm-inline pl-1'>" +
	"<button type='button' onclick='download(\"/#= filename #\")' class='btn icon-btn btn-sm btn-outline-secondary borderless'>" + 
	"<span class='fas fa-download'></span></button>" +
		"</span>" +
	"</div>";
	String releaseDateTemplate = kr.co.r2cast.utils.Util.getSmartDate("releaseDate", false);
%>


<!-- Kendo grid  -->

<div class="mb-4">
<kendo:grid name="grid" pageable="true" filterable="true" sortable="true" scrollable="false" reorderable="true" resizable="true" selectable="${value_gridSelectable}" >
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
		<kendo:grid-column title="" width="50" filterable="false" sortable="false" template="<%= editTemplate %>" />
		<kendo:grid-column title="에디션" field="edition" filterable="false" sortable="false" minScreenWidth="700" />
		<kendo:grid-column title="버전" field="version" filterable="false" sortable="false" minScreenWidth="550" />
		<kendo:grid-column title="유형" field="progType" filterable="false" sortable="false" minScreenWidth="450" />
		<kendo:grid-column title="파일크기" field="fileLength" filterable="false" minScreenWidth="1200"
				template="#= smartLength #" />
		<kendo:grid-column title="파일명" field="filename" />
		<kendo:grid-column title="출시일시" field="releaseDate" minScreenWidth="1100" template="<%= releaseDateTemplate %>" />
		<kendo:grid-column title="안정판?" field="fileType" filterable="false" minScreenWidth="1300"
				template="#=fileType == 'S' ? \"<span class='far fa-check'>\" : \"\"#" />
		<kendo:grid-column title="발행" field="published" filterable="false" minScreenWidth="450"
				template="#=published == 'Y' ? \"<span class='far fa-check'>\" : \"\"#" />
	</kendo:grid-columns>
	<kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
		<kendo:dataSource-sort>
			<kendo:dataSource-sortItem field="releaseDate" dir="desc"/>
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
					<kendo:dataSource-schema-model-field name="releaseDate" type="date" />
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

	// Add
	$("#add-btn").click(function(e) {
		e.preventDefault();
		
		initSmartWizard();

		
		$('#wizard-modal .modal-dialog').draggable({ handle: '.modal-header' });
		$("#wizard-modal").modal();
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

<script id="sw-template" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="wizard-modal">
	<div class="modal-dialog">
		<form class="modal-content" id="wizard-form">
      
			<!-- Modal Header -->
			<div class="modal-header move-cursor">
				<h5 class="modal-title">
					${pageTitle}
					<span class="font-weight-light pl-1"><span name="subtitle">추가</span>
				</h5>
				<button type="button" class="close" data-dismiss="modal">×</button>
			</div>
        
			<!-- Modal body -->
			<div class="modal-body modal-bg-color">

				<div name="smartWizard">
					<ul>
						<li name="tab-0">
							<a href="\\\#sw-step-1" class="mb-3">
								<span class="sw-done-icon"><span class="far fa-check"></span></span>
								<span class="sw-icon"><span class="fas fa-upload"></span></span>
								업로드
								<div class="text-muted small">설치 파일</div>
							</a>
						</li>
						<li name="tab-1">
							<a href="\\\#sw-step-2" class="mb-3">
								<span class="sw-done-icon"><span class="far fa-check"></span></span>
								<span class="sw-icon"><span class="fas fa-pencil-alt"></span></span>
								폼 입력
								<div class="text-muted small">설명 및 옵션</div>
							</a>
						</li>
					</ul>

					<div class="mb-3">
						<div id="sw-step-1" class="card animated fadeIn">
							<div class="card-body">
								<div class="drop-zone upload-root-div">
									<input name="files" type="file" />
								</div>
							</div>
						</div>
						<div id="sw-step-2" class="card animated fadeIn py-3">
						
							<div class="form-row pr-1">
								<div class="d-flex justify-content-end w-100 pr-3">
									<label class="custom-control custom-checkbox">
										<input type="checkbox" class="custom-control-input" name="published">
										<span class="custom-control-label">
											<span class="badge badge-outline-dark font-weight-normal small">발행</span>
										</span>
									</label>
								</div>
							</div>
							<div class="form-row">
								<div class="col-sm-8">
									<div class="form-group col">
										<label class="form-label">
											파일명
										</label>
										<input name="filename" type="text" class="form-control" readonly="readonly">
									</div>
								</div>
								<div class="col-sm-4">
									<div class="form-group col">
										<label class="form-label">
											안정판?
										</label>
										<select name="fileType" class="selectpicker" data-style="btn-default" data-none-selected-text="">
											<option value="S">안정판</option>
											<option value="P">시험판</option>
										</select>
									</div>
								</div>
							</div>
							<div class="form-row">
								<div class="col-sm-12">
									<div class="form-group col">
										<label class="form-label">
											설명(영어)
										</label>
										<textarea name="descEng" rows="3" class="form-control required"></textarea>
									</div>
								</div>
							</div>
							<div class="form-row">
								<div class="col-sm-12">
									<div class="form-group col">
										<label class="form-label">
											설명(로컬)
										</label>
										<textarea name="descLocal" rows="3" class="form-control"></textarea>
									</div>
								</div>
							</div>

						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button name="save-btn" type="button" class="btn btn-primary disabled" onclick='saveForm1()' name="saveBtn">저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<script id="template-2" type="text/x-kendo-template">

<div class="modal fade" data-backdrop="static" id="form-modal-2">
	<div class="modal-dialog">
		<form class="modal-content" id="form-2" rowid="-1" url="${createUrl}">
      
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
							<input type="checkbox" class="custom-control-input" name="published">
							<span class="custom-control-label">
								<span class="badge badge-outline-dark font-weight-normal small">발행</span>
							</span>
						</label>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-8">
						<div class="form-group col">
							<label class="form-label">
								파일명
							</label>
							<input name="filename" type="text" class="form-control" readonly="readonly">
						</div>
					</div>
					<div class="col-sm-4">
						<div class="form-group col">
							<label class="form-label">
								안정판?
							</label>
							<select name="fileType" class="selectpicker bg-white" data-style="btn-default" data-none-selected-text="">
								<option value="S">안정판</option>
								<option value="P">시험판</option>
							</select>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-12">
						<div class="form-group col">
							<label class="form-label">
								설명(영어)
							</label>
							<textarea name="descEng" rows="3" class="form-control required"></textarea>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-12">
						<div class="form-group col">
							<label class="form-label">
								설명(로컬)
							</label>
							<textarea name="descLocal" rows="3" class="form-control"></textarea>
						</div>
					</div>
				</div>
				<div class="form-row">
					<div class="col-sm-6">
						<div class="form-group col">
							<label class="form-label">
								출시일시
							</label>
							<input name="releaseDate" type="text" class="form-control border-none">
						</div>
					</div>
				</div>
			</div>
        
			<!-- Modal footer -->
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">취소</button>
				<button type="button" class="btn btn-primary" onclick='saveForm2()' name="saveBtn">저장</button>
			</div>
			
		</form>
	</div>
</div>

</script>

<!--  / Forms -->


<!-- Smart wizard -->

<style>

.upload-root-div {
	height: 200px;
}

.k-upload-files {
	height: 100px;
	overflow-x: hidden;
	overflow-y: hidden;
}

strong.k-upload-status.k-upload-status-total { font-weight: 500; color: #2e2e2e; }

div.k-dropzone.k-dropzone-hovered em, div.k-dropzone em { color: #2e2e2e; }

.k-upload .k-upload-files ~ .k-button {
	width: 48%;
	margin: 3px 3px 0.25em 1%;
}

.k-upload .k-button {
	height: 38px;
	border-radius: .25rem;
}

.k-upload .k-upload-button {
	border-color: transparent;
	background: #8897AA;
	color: #fff;
}

.k-upload .k-upload-button:hover {
	background: #818fa2;
}

.k-upload .k-upload-files ~ .k-upload-selected {
	border-color: transparent;
	background: #e84c64;
	color: #fff;
}

.k-upload .k-upload-files ~ .k-upload-selected:hover {
	background: #dc485f;
}

.k-upload .k-upload-files ~ .k-clear-selected {
	background: transparent;
	color: #4E5155;
	border: 1px solid rgba(24,28,33,0.1);
}

.k-upload .k-upload-files ~ .k-clear-selected:hover {
	background: rgba(24,28,33,0.06);
}

</style>

<!-- / Smart wizard -->


<!--  Scripts -->

<script>

function initSmartWizard() {
	
	$("#formRoot").html(kendo.template($("#sw-template").html()));
	
	$("#wizard-form div[name='smartWizard']").smartWizard({
		showStepURLhash: false,
		toolbarSettings: {
			showNextButton: false,
			showPreviousButton: false,
		}
	});

	$("#wizard-form input[name='files']").kendoUpload({
		multiple: false,
		async: {
			saveUrl: "${uploadModel.saveUrl}",
			autoUpload: false
		},
		localization: {
			cancel: "취소",
			dropFilesHere: "업로드 대상 파일을 여기에 끌어 놓기",
			headerStatusUploaded: "완료",
			headerStatusUploading: "업로드중...",
			remove: "삭제",
			retry: "재시도",
			select: "파일 선택...",
			uploadSelectedFiles: "업로드 시작",
			clearSelectedFiles: "목록 지우기",
			invalidFileExtension: "허용되지 않는 파일 유형입니다.",
		},
		dropZone: ".drop-zone",
		upload: function(e) {
			e.data = {
				siteId: ${uploadModel.siteId},
				type: "${uploadModel.type}",
   				code: "${uploadModel.code}",
			};
		},
		success: function(e) {
			$("#wizard-form button[name='save-btn']").removeClass("disabled");
			$("#wizard-form input[name='filename']").val(e.files[0].name);
			
			$("#wizard-form div[name='smartWizard']").smartWizard("next");
		},
		validation: {
			allowedExtensions: ${uploadModel.allowedExtensions}
		},
	});
	
	$("#wizard-form select[name='fileType']").selectpicker('render');

	bootstrapSelectVal($("#form-1 select[name='fileType']"), "S");

	$("#wizard-form textarea[name='descEng']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});

	$("#wizard-form textarea[name='descLocal']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
}


function saveForm1() {

	var filename = $("#wizard-form input[name='filename']").val();
	if (filename) {
		if ($("#wizard-form li[name='tab-0']").hasClass("active")) {
			$("#wizard-form div[name='smartWizard']").smartWizard("next");
		}
		
		if ($("#wizard-form").valid()) {
	    	var data = {
        		filename: filename,
        		fileType: $("#wizard-form select[name='fileType']").val(),
        		descEng: $.trim($("#wizard-form textarea[name='descEng']").val()),
        		descLocal: $.trim($("#wizard-form textarea[name='descLocal']").val()),
        		published: $("#wizard-form input[name='published']").is(":checked") ? "Y" : "N",
        	};
	    		
			$.ajax({
				type: "POST",
				contentType: "application/json",
				dataType: "json",
				url: "${createUrl}",
				data: JSON.stringify(data),
				success: function (form) {
					showSaveSuccessMsg();
					$("#wizard-modal").modal("hide");
					$("#grid").data("kendoGrid").dataSource.read();
				},
				error: ajaxSaveError
			});
		}
	}
}


function initForm2(subtitle) {
	
	$("#formRoot").html(kendo.template($("#template-2").html()));
	
	$("#form-2 select[name='fileType']").selectpicker('render');

	bootstrapSelectVal($("#form-2 select[name='fileType']"), "S");

	$("#form-2 textarea[name='descEng']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});

	$("#form-2 textarea[name='descLocal']").keypress(function (e) {
		if (e.keyCode != 13) {
			return;
		}
		
		$(this).text().replace(/\n/g, "");
		
		return false;
	});
	
	$("#form-2 input[name='releaseDate']").kendoDateTimePicker({
		format: "yyyy-MM-dd HH:mm", 
		parseFormats: [
			"yyyy-MM-dd HH:mm", "yyyy-MM-dd HHmm", "yyyy-MM-dd HH mm", "yyyy-MM-dd HH",
			"yyyyMMdd HH:mm", "yyyyMMdd HHmm", "yyyyMMdd HH mm", "yyyyMMdd HH",
			"yyyy-MM-dd", "yyyyMMdd", "MMdd HH mm", "MMdd HHmm", "MMdd HH", "MMdd"
		],
		change: onKendoPickerChange,
	});
	
	$("#form-2 span[name='subtitle']").text(subtitle ? subtitle : "추가");
}


function edit(id) {
	
	initForm2("변경");

	var dataItem = $("#grid").data("kendoGrid").dataSource.get(id);
	
	$("#form-2").attr("rowid", dataItem.id);
	$("#form-2").attr("url", "${updateUrl}");
	
	$("#form-2 input[name='filename']").val(dataItem.filename);

	$("#form-2 textarea[name='descEng']").val(dataItem.descEng);
	$("#form-2 textarea[name='descLocal']").val(dataItem.descLocal);
	
	bootstrapSelectVal($("#form-2 select[name='fileType']"), dataItem.fileType);
	
	$("#form-2 input[name='published']").prop("checked", dataItem.published == "Y");
	
	$("#form-2 input[name='releaseDate']").data("kendoDateTimePicker").value(dataItem.releaseDate);

	
	$('#form-modal-2 .modal-dialog').draggable({ handle: '.modal-header' });
	$("#form-modal-2").modal();
}


function saveForm2() {

	// kendo datepicker validation
	validateKendoDateTimeValue($("#form-2 input[name='releaseDate']"));

	if ($("#form-2").valid()) {
    	var data = {
    		id: Number($("#form-2").attr("rowid")),
    		fileType: $("#form-2 select[name='fileType']").val(),
    		descEng: $.trim($("#form-2 textarea[name='descEng']").val()),
    		descLocal: $.trim($("#form-2 textarea[name='descLocal']").val()),
    		published: $("#form-2 input[name='published']").is(":checked") ? "Y" : "N",
			releaseDate: $("#form-2 input[name='releaseDate']").data("kendoDateTimePicker").value(),
    	};
		
		$.ajax({
			type: "POST",
			contentType: "application/json",
			dataType: "json",
			url: "${updateUrl}",
			data: JSON.stringify(data),
			success: function (form) {
				showSaveSuccessMsg();
				$("#form-modal-2").modal("hide");
				$("#grid").data("kendoGrid").dataSource.read();
			},
			error: ajaxSaveError
		});
	}
}


function download(file) {
	
	var path = "/eco/common/download?type=SETUP&siteId=${sessionScope['currentSiteId']}&file=" + file;
	
	location.href = path;
}


function uploadModalClosed() { }

</script>

<!--  / Scripts -->


<!-- / Page body -->





<!-- Functional tags -->

<func:cmmValidate />


<!-- Closing tags -->

<common:base />
<common:pageClosing />
