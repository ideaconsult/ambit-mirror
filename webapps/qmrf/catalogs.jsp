<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Catalogues"/>
</jsp:include>


<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="catalog"/>
</jsp:include>

<c:set var="thispage" value="catalogs.jsp"/>


<jsp:include page="menuall.jsp" flush="true">
		    <jsp:param name="highlighted" value="catalog" />
</jsp:include>

<c:choose>
	<c:when test="${empty param.source}">
			<c:set var="source" value="endpoints_xml.jsp"/>
	</c:when>
	<c:otherwise>
		<c:set var="source" value="${param.source}"/>
	</c:otherwise>
</c:choose>

<ul>
<li>
<a href="
	<c:url value="${thispage}">
		<c:param name="source" value="endpoints_xml.jsp"/>
	</c:url>
">Endpoints</a>
<li>
<a href="
	<c:url value="${thispage}">
		<c:param name="source" value="software_xml.jsp"/>
	</c:url>
">Software</a>

<li>
<a href="
	<c:url value="${thispage}">
		<c:param name="source" value="algorithm_xml.jsp"/>
	</c:url>
">Algorithms</a>

<li>
<a href="
	<c:url value="${thispage}">
		<c:param name="source" value="authors_xml.jsp"/>
	</c:url>
">Author</a>

<hr>

<div style="color:#36393D;background:#FFFFFF;text-align:left" >
		<c:import var="xsl" url="/WEB-INF/xslt/catalogs2form.xsl"/>
		<c:import var="xml" url="${source}"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${xsl}"/>
</div>

<!--
select count(idstructure),s.name,id_srcdataset from src_dataset as s join struc_dataset using (id_srcdataset) join structure using (idstructure) join substance using(idsubstance) left  join fp1024 using (idsubstance) where fp1024.status is null group by id_srcdataset
display which structures need to generate fingerprints
-->

</body>
</html>

