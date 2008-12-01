<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<sql:query var="rs" dataSource="jdbc/repdose">
	select idstructure,casno from cas limit 10
</sql:query>
<c:forEach var="row" items="${rs.rows}">
	${row.idstructure} &nbsp; ${row.casno}
           <p>
</c:forEach>
