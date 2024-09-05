<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>


<!-- Taglib -->

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/common"%>
<%@ taglib prefix="func" tagdir="/WEB-INF/tags/func"%>
<%@ taglib prefix="kendo" uri="http://www.kendoui.com/jsp/tags"%>


<!-- URL -->


<!-- Opening tags -->

<common:pageOpening />


<!-- Page title -->


<h4 class="pt-1 pb-3 mb-3">
	<span class="mr-1 fas fa-home"></span>
	${pageTitle}

<c:if test="${not empty LastLogin}">

	<div class="text-muted text-tiny mt-1">
		<small class="font-weight-normal">${LastLogin}</small>
	</div>

</c:if>

</h4>

<hr class="border-light container-m--x mt-0 mb-4">





<!-- Page body -->


<!-- / Page body -->





<!-- Closing tags -->

<common:base />
<common:pageClosing />
