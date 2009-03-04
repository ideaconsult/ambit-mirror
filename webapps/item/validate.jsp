<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="sid_local" value="${param.SID}" scope="page"/>
<c:if test="${empty param.SID}">
	<c:set var="sid_local" value="${sessionScope['SID']}" scope="page"/>
</c:if>
<c:if test="${!empty initParam['external_validation']}">
	<c:import url="${initParam['external_validation']}">
		<c:param name="${initParam['validation_param']}" value="${sid_local}"/>
	</c:import>
</c:if>
<c:set var="SID" value="${sid_local}" scope="session"/>