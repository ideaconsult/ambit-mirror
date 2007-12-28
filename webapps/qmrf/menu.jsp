<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<jsp:include page="header.html" flush="true"/>
<c:if test="${empty sessionScope.tablecolor}">
	<c:set var="tablecolor" value='#FFFFFF' scope="session"/>
</c:if>
<c:if test="${empty sessionScope.rowcolor}">
	<c:set var="rowcolor" value='#EEEEEE' scope="session"/>
</c:if>

<c:if test="${empty sessionScope.dateformat}">
	<c:set var="dateformat" value='%Y-%c-%d %H:%i' scope="session"/>
</c:if>

<!-- D6DFF7 -->
<c:if test="${empty sessionScope.querycolor}">
	<c:set var="querycolor" value='#D6DFF7' scope="session"/>
</c:if>
<c:if test="${empty sessionScope.headercolor}">
	<c:set var="headercolor" value='#C5CEE6' scope="session"/>
</c:if>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>

<table width="98%" border="0">
<tr>
<td>
<div style="float:left;text-align:left">
	<c:if test="${!empty pageContext.request.userPrincipal.name}">
		<c:if test="${empty sessionScope.viewmode}">
			<c:forEach var="a" items="${pageContext.request.userPrincipal.roles}">
				<c:set var="viewmode" value="${a}" scope="session"/>
			</c:forEach>
		</c:if>

		View:
		<c:forEach var="a" items="${pageContext.request.userPrincipal.roles}">
			<c:choose>
				<c:when test="${a eq 'qmrf_user'}">
					<c:set var="title" value="Author"/>
				</c:when>
				<c:when test="${a eq 'qmrf_admin'}">
					<c:set var="title" value="Reviewer"/>
				</c:when>
				<c:when test="${a eq 'qmrf_manager'}">
					<c:set var="title" value="Administrator"/>
				</c:when>
				<c:when test="${a eq 'qmrf_editor'}">
					<c:set var="title" value="Editor"/>
				</c:when>
				<c:otherwise>
					<c:set var="title" value=""/>
				</c:otherwise>
			</c:choose>

			<c:if test="${title ne ''}">
				<c:choose>
					<c:when test="${sessionScope.viewmode eq a}">
						<img src="images/user_edit.png" alt="${title}" title="Current view : ${title}" border="0"/>
						<b><i><font color="#FF4444">${title}</font></i></b>
					</c:when>
					<c:otherwise>
						<a href="<c:url value="">
								<c:param name="viewmode" value="${a}"/>
							</c:url>">
						<img src="images/user.png" alt="${title}" border="0" title="Click here to switch to ${title} view"/>
						<i>${title}</i>
						</a>
					</c:otherwise>
				</c:choose>
				&nbsp;
			</c:if>
		</c:forEach>
	</c:if>
</div>
<div style="float:right;text-align:right">

	<c:choose>
	<c:when test="${empty pageContext.request.userPrincipal.name}">
		<a href="
			<c:url value="protected.jsp"/>
		">Log in</a>
		&nbsp;
		<a href="
			<c:url value="register.jsp"/>
		">Register</a>
	</c:when>
	<c:otherwise>
		<c:set var="clr" value="#000000"/>
		<c:forEach var="a" items="${pageContext.request.userPrincipal.roles}">
			<c:if test="${a eq 'qmrf_admin'}">
				<c:set var="clr" value="#FF4444"/>
			</c:if>
		</c:forEach>
		Welcome <b><font color="${clr}">${pageContext.request.userPrincipal.name}</font></b>

		<a href="
			<c:url value="protected.jsp">
				<c:param name="logoff" value="true"/>
			</c:url>
		">Log out</a>
	</c:otherwise>
	</c:choose>
</div>
</td>
</tr>
</table>

<jsp:include page="menuqmrf.jsp" flush="true">
	<jsp:param name="highlighted" value="${param.highlighted}" />
</jsp:include>