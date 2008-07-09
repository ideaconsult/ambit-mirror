<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="/ambit" prefix="a" %>
<fmt:requestEncoding value="UTF-8"/>

<jsp:include page="query_settings.jsp" flush="true"/>
<c:set var="thispage" value='display.jsp'/>
<c:choose>
	<c:when test="${empty param.pagesize}">
  	<c:set var="pagesize" value="5"/>
  </c:when>
  <c:otherwise>
  		<c:set var="pagesize" value="${param.pagesize}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.page}">
  	<c:set var="startpage" value="0"/>
  	<c:set var="startrecord" value="0"/>
  </c:when>
  <c:otherwise>
  		<c:set var="startpage" value="${param.page}"/>
			<c:set var="startrecord" value="${param.page * param.pagesize}"/>
	</c:otherwise>
</c:choose>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="Substance in QMRF Database"/>
</jsp:include>

<c:if test="${!empty param.idstructure}">
	<table>
	<tr>
	<td valign="top">
	<c:import var="xml" url="structure_xml.jsp?idstructure=${param.idstructure}"/>
	<c:import var="xsl" url="/WEB-INF/xslt/cml2html.xsl"/>
	<x:transform xml="${fn:trim(xml)}" xslt="${fn:trim(xsl)}"/>
	</td>
	<td valign="top">
		<c:url var="url" value="image.jsp">
		<c:param name="idstructure" value="${param.idstructure}"/>
		<c:param name="weight" value="200"/>
		<c:param name="height" value="200"/>
		</c:url>
		<img src="
			<c:out value="${url}" escapeXml="true" />
			" border="0">			
	</td>
	</tr>
	</table>

Found in following QMRFs:
<c:set var="sql" value="select qmrf_documents.documents.idqmrf,qmrf_number,qmrf_title,version,user_name,date_format(qmrf_documents.documents.updated,'${sessionScope.dateformat}') as lastdate,concat(qa.description,' (',qa.type,')') as attachment,type from ambit_qmrf.struc_dataset join ambit_qmrf.src_dataset using(id_srcdataset) join qmrf_documents.attachments as qa using(name) join qmrf_documents.documents using(idqmrf) where idstructure=${param.idstructure} order by qmrf_documents.documents.${sessionScope.order} ${sessionScope.order_direction} limit ${startrecord},${pagesize}"/>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>
		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
		<jsp:param name="version" value="Version"/>
		<jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="attachment" value="Found in"/>
		<jsp:param name="attachment type" value="Attachment type"/>
		<jsp:param name="actions" value=""/>

		<jsp:param name="sqlparam" value="${sessionScope['username']}"/>

		<jsp:param name="paging" value="false"/>
		<jsp:param name="page" value="${startpage}"/>
		<jsp:param name="pagesize" value="${pagesize}"/>
		<jsp:param name="viewpage" value="display.jsp"/>
		<jsp:param name="idstructure" value="${param.idstructure}"/>

</jsp:include>

<c:choose>
	<c:when test="${param.history eq 'true'}">
		<jsp:include page="history.jsp" flush="true">
    <jsp:param name="id" value="${param.id}"/>
		<jsp:param name="idstructure" value="${param.idstructure}"/>
		</jsp:include>
	</c:when>
	<c:otherwise>
		<jsp:include page="view.jsp" flush="true">
    <jsp:param name="id" value="${param.id}"/>
		<jsp:param name="idstructure" value="${param.idstructure}"/>
		<jsp:param name="viewmode" value="${param.viewmode}"/>
		<jsp:param name="history" value="false"/>
		</jsp:include>
	</c:otherwise>
</c:choose>


</c:if>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true"/>

	</p>
</div>
</body>
</html>
