<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %>
<?xml version='1.0' encoding='UTF-8' ?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!--
Catalogs available in QMRF Database in XML format as defined in QMRF DTD schema
<!ELEMENT Catalogs (software_catalog*,algorithms_catalog*,descriptors_catalog*,
          endpoints_catalog*,
          publications_catalog*,authors_catalog*)>
-->


<Catalogs>

<software_catalog>
<c:if test="${!empty param.all || !empty param.software_catalog}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_software,name,description,contact,url FROM catalog_software
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
				<software id="SW${row.id_software}"	name="${row.name}" description="${row.description}" number="${row.id_software}" url="${row.url}" contact="${row.contact}" catalog="software_catalog"/>
	  </c:forEach>
</c:if>
</software_catalog>

<algorithms_catalog>
<c:if test="${!empty param.all ||  !empty param.algorithms_catalog}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_algorithm,definition,description FROM catalog_algorithms
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
				<algorithm id="ALG${row.id_algorithm}"	description="${row.description}" definition="${row.definition}" catalog="algorithms_catalog"/>
	  </c:forEach>
</c:if>
</algorithms_catalog>

<descriptors_catalog>
<c:if test="${!empty param.all || !empty param.descriptors_catalog}">

</c:if>
</descriptors_catalog>

<endpoints_catalog>
<c:if test="${!empty param.all || !empty param.endpoints_catalog}">

		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_endpoint,concat(id_group,'.',endpoint_group) as g,if(subgroup="",'',concat(id_subgroup,'.',subgroup)) as sg,concat(id_group,'.',id_subgroup,if(subgroup="",'','.'),id_name,'.',name) as n FROM catalog_endpoints c;
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
				<endpoint id="endpoint${row.id_endpoint}"	group="${row.g}" subgroup="${row.sg}" name="${row.n}" />
	  </c:forEach>
</c:if>
</endpoints_catalog>

<publications_catalog>
<c:if test="${!empty param.all ||  !empty param.publications_catalog}">

</c:if>
</publications_catalog>


<authors_catalog>
<c:if test="${!empty param.all || !empty param.authors_catalog}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select id_author,name,affiliation,address,webpage,email from catalog_authors
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
				<author affiliation="" contact="" email="${row.email}" id="AUTH${row.id_author}" name="${row.name}" number="${row.id_author}" >
			 </author>
	  </c:forEach>
</c:if>
</authors_catalog>

</Catalogs>