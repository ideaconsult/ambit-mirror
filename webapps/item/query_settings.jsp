<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- maxpages -->	
<c:choose>
	<c:when test="${empty param.maxpages}">
		<c:if test="${empty sessionScope.maxpages}">
			<c:set var="maxpages" value="10" scope="session"  />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="maxpages" value='${param.maxpages}' scope="session"  />
	</c:otherwise>
</c:choose>

<!-- pagesize -->	
<c:choose>
	<c:when test="${empty param.pagesize}">
		<c:if test="${empty sessionScope.pagesize}">
			<c:set var="pagesize" value="10" scope="session"  />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="pagesize" value='${param.pagesize}' scope="session"  />
	</c:otherwise>
</c:choose>

<!-- page -->	
<c:choose>
	<c:when test="${empty param.page}">
		<c:if test="${empty sessionScope.page}">
			<c:set var="page" value="0" scope="session"  />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="page" value='${param.page}' scope="session"  />
	</c:otherwise>
</c:choose>


<!-- Order -->	
<c:choose>
	<c:when test="${empty param.order}">
		<c:if test="${empty sessionScope.order}">
			<c:set var="order" value="updated" scope="session"  />
	</c:if>
	</c:when>
	<c:otherwise>
		<c:set var="order" value='${param.order}' scope="session"  />
	</c:otherwise>
</c:choose>

<!-- Ascending/Descending order-->	
<c:choose>
	<c:when test="${empty param.changedirection}">
		<c:if test="${empty sessionScope.order_direction}">
			<c:set var="order_direction" value="" scope="session"  />
		</c:if>
	</c:when>
	<c:otherwise>
		<c:choose>
		<c:when test="${empty sessionScope.order_direction}">
			<c:set var="order_direction" value="desc" scope="session"  />
		</c:when>
		<c:otherwise>
			<c:set var="order_direction" value="" scope="session"  />
		</c:otherwise>
		</c:choose>
	</c:otherwise>
</c:choose>
