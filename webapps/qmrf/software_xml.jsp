<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_software,name,description,contact,url FROM catalog_software
</sql:query>
<?xml version="1.0" encoding="UTF-8" ?>


<software_catalog>
		<c:forEach var="row" items="${rs.rows}">
				<software id="SFWR${row.id_software}"	name="${row.name}" description="${row.description}" number="${row.id_software}" url="${row.url}" contact="${row.contact}" catalog="software_catalog"/>
	  </c:forEach>
</software_catalog>