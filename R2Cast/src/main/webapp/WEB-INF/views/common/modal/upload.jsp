<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>





<!-- Page body -->


<!--  Forms -->

<div id="root-div" class="drop-zone">
	<input name="files" id="files" type="file" />
</div>


<style>

#root-div {
	height: 400px; display: none;
}

.k-upload-files {
	height: 300px;
	overflow-x: hidden;
	overflow-y: scroll;
}

strong.k-upload-status.k-upload-status-total { font-weight: 500; color: #2e2e2e; }

div.k-dropzone.k-dropzone-hovered em, div.k-dropzone em { color: #2e2e2e; }

.k-upload .k-upload-files ~ .k-button {
	width: 48%;
	margin: 3px 0 0.25em 1%;
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


<!--  / Forms -->


<!--  Scripts -->

<script>

$(document).ready(function() {
	$("#files").kendoUpload({
        multiple: true,
        async: {
            saveUrl: "${uploadModel.saveUrl}",
            autoUpload: false
        },
        localization: {
        	cancel: "${label_cancel}",
        	dropFilesHere: "${label_dropFilesHere}",
        	headerStatusUploaded: "${label_headerStatusUploaded}",
        	headerStatusUploading: "${label_headerStatusUploading}",
        	remove: "${label_remove}",
        	retry: "${label_retry}",
        	select: "${label_select}",
        	uploadSelectedFiles: "${label_uploadSelectedFiles}",
        	clearSelectedFiles: "${label_clearSelectedFiles}",
        	invalidFileExtension: "${label_invalidFileExtension}",
        },
        dropZone: ".drop-zone",
        upload: function(e) {
			e.data = {
   				siteId: ${uploadModel.siteId},
   				type: "${uploadModel.type}",
   			};
        },
        success: function(e) {
			if (parent && parent.goAhead) {
				parent.goAhead = "true";
			}
        },
<c:if test="${not empty uploadModel.allowedExtensions}">
        validation: {
        	allowedExtensions: ${uploadModel.allowedExtensions}
        },
</c:if>
    });
	
	$("#root-div").show();
	
<c:if test="${not empty uploadModel.message}">

	showAlertModal("info", "${uploadModel.message}");

</c:if>
	    
});

</script>


<!--  / Scripts -->


<!-- / Page body -->
