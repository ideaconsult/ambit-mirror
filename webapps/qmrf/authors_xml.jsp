<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select id_author,name,affiliation,address,webpage,email from catalog_authors
</sql:query>				
<?xml version="1.0" encoding="UTF-8" ?>
<authors_catalog>
		<c:forEach var="row" items="${rs.rows}">
				<author affiliation="" contact="" email="${row.email}" id="AUTH${row.id_author}" name="${row.name}" number="${row.id_author}" >
			 </author>	
	  </c:forEach>	
</authors_catalog>