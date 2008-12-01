<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="/ambit" prefix="a" 
%>
<c:set var="w" value="100"/>
<c:if test="${!empty param.width}">
	<c:set var="w" value="${param.width}"/>
</c:if>
<c:set var="h" value="100"/>
<c:if test="${!empty param.height}">
	<c:set var="h" value="${param.height}"/>
</c:if>
<c:set var="clr" value="#FFFFFF"/>
<c:if test="${!empty param.bgcolor}">
	<c:set var="clr" value="${param.bgcolor}"/>
</c:if>
<c:set var="sql" value="SELECT uncompress(structure) as cml FROM structure "/>
<c:choose>
<c:when test="${!empty param.idstructure}">
	<c:set var="sql" value="${sql} where idstructure=${param.idstructure} limit 1"/>
</c:when>
<c:when test="${!empty param.idsubstance}">
	<c:set var="sql" value="${sql} where idsubstance=${param.idsubstance} limit 1"/>
</c:when>
<c:otherwise>
	<c:set var="sql" value=""/>
</c:otherwise>
</c:choose>
<c:catch var="err">
	<c:if test="${!empty sql}">
		<sql:query var="rs" dataSource="jdbc/repdose">
			${sql}
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<a:image mol="${row.cml}" width="${w}" height="${h}" molType="cml" bgcolor="${clr}"/>		
		</c:forEach>
	</c:if>
</c:catch>