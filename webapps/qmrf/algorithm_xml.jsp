<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_algorithm,definition,description FROM catalog_algorithms
</sql:query>
<?xml version="1.0" encoding="UTF-8" ?>

<algorithms_catalog>
		<c:forEach var="row" items="${rs.rows}">
				<algorithm id="ALGM${row.id_algorithm}"	description="${row.description}" definition="${row.definition}" catalog="algorithms_catalog"/>
	  </c:forEach>
</algorithms_catalog>