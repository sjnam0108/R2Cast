<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->

<c:url value="/eco/common/readRecentTasks" var="readUrl" />





<!-- Page body -->


<!-- Java(optional)  -->

<%
	String statusTemplate =
			"# if (flagCode == 'C') { #" + "<span title='#= statusTip #' class='fa-regular fa-trash-can text-info'></span>" +
			"# } else if (flagCode == 'F') { #" + "<span title='#= statusTip #' class='fa-regular fa-hand-paper text-danger'></span>" +
			"# } else if (flagCode == 'P') { #" + "<span title='#= statusTip #' class='fa-regular fa-flag text-success'></span>" +
			"# } else if (flagCode == 'R') { #" + "<span title='#= statusTip #' class='fa-regular fa-asterisk text-muted'></span>" +
			"# } else if (flagCode == 'S') { #" + "<span title='#= statusTip #' class='fa-regular fa-flag text-blue'></span>" +
			"# } else if (flagCode == 'W') { #" + "<span title='#= statusTip #' class='fa-regular fa-hourglass-half'></span>" +
			"# } else { #" +  
			"# } #";
%>


<!-- Kendo grid  -->

<kendo:grid name="recent-task-grid" filterable="false" height="350" sortable="false" reorderable="false" resizable="true" selectable="single" >
    <kendo:grid-columns>
        <kendo:grid-column title="번호" field="id" width="80" minScreenWidth="580"
        		template="#= id ? \"<small>\" + id + \"</small>\" : \"\" #" />
        <kendo:grid-column title="RVM명" field="rvm.rvmName" />
        <kendo:grid-column title="명령" field="command"
        		template="#= univCommand ? \"<small>\" + univCommand + \"</small>\" : \"\" #" />
        <kendo:grid-column title="상태" field="executed" width="60"
        		template="<%= statusTemplate %>"/>
        <kendo:grid-column title="예상" field="remainingSecs" width="60" />
        <kendo:grid-column title="유효" field="destDate" width="80" minScreenWidth="580"
        		template="#= destDate ? \"<small>\" + kendo.format('{0:HH:mm:ss}', destDate) + \"</small>\" : \"\" #" />
        <kendo:grid-column title="요청" field="requestedDate" width="80" minScreenWidth="1000"
        		template="#= requestedDate ? \"<small>\" + kendo.format('{0:HH:mm:ss}', requestedDate) + \"</small>\" : \"\" #" />
        <kendo:grid-column title="실행" field="executedDate" width="80" minScreenWidth="720"
        		template="#= executedDate ? \"<small>\" + kendo.format('{0:HH:mm:ss}', executedDate) + \"</small>\" : \"\" #" />
    </kendo:grid-columns>
    <kendo:dataSource serverPaging="true" serverSorting="true" serverFiltering="true" serverGrouping="true" error="kendoReadError">
    	<kendo:dataSource-sort>
    		<kendo:dataSource-sortItem field="id" dir="desc"/>
    	</kendo:dataSource-sort>
       	<kendo:dataSource-requestEnd>
       		<script>
       			function dataSource_requestEnd(e) {
       				if (e.type == "read") {
       					recentTaskTimer = setTimeout(function() {
   							$("#recent-task-grid").data("kendoGrid").dataSource.read();
   						}, 5000);
       				}
       			}
			</script>
       	</kendo:dataSource-requestEnd>
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
                        <kendo:dataSource-schema-model-field name="destDate" type="date"/>
                        <kendo:dataSource-schema-model-field name="cancelDate" type="date"/>
                        <kendo:dataSource-schema-model-field name="requestedDate" type="date"/>
                        <kendo:dataSource-schema-model-field name="executedDate" type="date"/>
                </kendo:dataSource-schema-model-fields>
            </kendo:dataSource-schema-model>
        </kendo:dataSource-schema>
    </kendo:dataSource>
</kendo:grid>

<!-- / Kendo grid  -->


<!-- / Page body -->
