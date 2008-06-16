<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${!empty param.status}">
	<c:set var="record_status" value="${param.status}" scope="session"/>
</c:if>
<c:set var="status_allowed" value="${param.status_allowed}"/>
<c:if test="${empty param.status_allowed}">
	<c:set var="status_allowed" value="all,draft,submitted,under review,returned for revision,review completed,published,archived"/>
</c:if>
	<table bgcolor="${tablecolor}" width="100%">
	<tr bgcolor="${tablecolor}">
	<td>
		<c:set var="status">${status_allowed}</c:set>
		<b>Filter documents by status</b>:&nbsp;
		<c:forTokens var="t" items="${status}" delims=",">

			<c:if test="${sessionScope.record_status eq t}">
				[
			</c:if>
			<a href="
				<c:url value="">

					<c:param name="status" value="${t}"/>

				</c:url>
			">${t}&nbsp;</a>

			<c:if test="${sessionScope.record_status eq t}">
				]
			</c:if>
		</c:forTokens>
	</td>
	</tr>
	</table>