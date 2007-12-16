<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<?xml version='1.0' encoding='UTF-8' ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_endpoint,concat(id_group,'.',endpoint_group) as g,if(subgroup="",'',concat(id_subgroup,'.',subgroup)) as sg,concat(id_group,'.',id_subgroup,if(subgroup="",'','.'),id_name,'.',name) as n FROM catalog_endpoints c;
</sql:query>
<endpoints_catalog>
		<c:forEach var="row" items="${rs.rows}">

				<endpoint id="ENDP${row.id_endpoint}"	group="${row.g}" subgroup="${row.sg}" name="${row.n}" />
	  </c:forEach>
</endpoints_catalog>