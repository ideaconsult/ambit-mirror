<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<c:if test="${!empty param.id}">
		
		<jsp:include page="display.jsp" flush="true">
    <jsp:param name="idstructure" value="${param.id}"/>
    <jsp:param name="id" value=""/>	
		</jsp:include>

</c:if>

