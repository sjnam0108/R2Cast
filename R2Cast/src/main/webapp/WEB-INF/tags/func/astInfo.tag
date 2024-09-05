<%@ tag pageEncoding="UTF-8"%>

<script>

function attachAssetInfoPopover(by) {
	if (by == null || by == "deviceID") {
		$('.asset-popover').popover({
			html: true,
			trigger: 'focus',
		    content: function(){
		        var div_id =  "tmp-id-" + $.now();
		        return popoverAssetContent(kendo.format("/ast/common/astinfo?deviceID={0}", 
		        		getDeviceID($(this).closest("tr"))), div_id);
		    },
		});
	} else if (by == "assetID") {
		/*
		$('.asset-popover').popover({
			html: true,
			trigger: 'focus',
		    content: function(){
		        var div_id =  "tmp-id-" + $.now();
		        return popoverAssetContent(kendo.format("/ast/common/astinfo?assetID={0}", 
		        		getDeviceID($(this).closest("tr"))), div_id);
		    },
		});
		*/
	}
}

function popoverAssetContent(link, div_id){
    $.ajax({
        url: link,
        success: function(response){
            $('#'+div_id).html(response);
        }
    });

    return '<div id="'+ div_id +'" class="d-flex justify-content-center align-items-center" style="width: 170px; min-height: 135px;">${wait_loading}</div>';
}

function getDeviceID(row) {
	var component = $("#grid").data("kendoGrid");
	if (component) {
		var dataItem = component.dataItem(row);
		if (dataItem) {
			if (dataItem.assetHistory) {
				return dataItem.assetHistory.deviceCode;
			} else if (dataItem.deviceCode) {
				return dataItem.deviceCode;
			}
		}
	}
	
	return "";
}

</script>
