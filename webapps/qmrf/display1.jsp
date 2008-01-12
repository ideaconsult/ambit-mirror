<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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


<c:if test="${!empty param.idstructure}">

	<!--
<c:choose>
	<c:when test="${empty sessionScope['username']}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select status,idqmrf,idstructure,name from ambit_qmrf.struc_dataset join ambit_qmrf.src_dataset using(id_srcdataset) join qmrf_documents.attachments using(name) join qmrf_documents.documents using(idqmrf) where idstructure=? and status='published'
			<sql:param value="${param.id}"/>
		</sql:query>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'true'}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,idstructure,name from ambit_qmrf.struc_dataset join ambit_qmrf.src_dataset using(id_srcdataset) join qmrf_documents.attachments using(name) where idstructure=?
			<sql:param value="${param.id}"/>
		</sql:query>
	</c:when>
	<c:otherwise>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select status,idqmrf,idstructure,name from ambit_qmrf.struc_dataset join ambit_qmrf.src_dataset using(id_srcdataset) join qmrf_documents.attachments using(name) join qmrf_documents.documents using(idqmrf) where idstructure=? and ((user_name=?) or (status='published'))
			<sql:param value="${param.id}"/>
			<sql:param value="${sessionScope['username']}"/>
		</sql:query>
	</c:otherwise>
</c:choose>
-->

<sql:query var="rsname" dataSource="jdbc/qmrf_documents">
			select name from ambit_qmrf.name where idstructure=?
			<sql:param value="${param.idstructure}"/>
</sql:query>
<sql:query var="rscas" dataSource="jdbc/qmrf_documents">
			select casno from ambit_qmrf.cas where idstructure=?
			<sql:param value="${param.idstructure}"/>
</sql:query>
<sql:query var="rsdesc" dataSource="jdbc/qmrf_documents">
			select name,value from ambit_qmrf.dvalues join ambit_qmrf.ddictionary using(iddescriptor) where idstructure=?
			<sql:param value="${param.idstructure}"/>
</sql:query>
<sql:query var="rsexp" dataSource="jdbc/qmrf_documents">
			select d.name,c,f.name as field,s.value from ambit_qmrf.experiment join ambit_qmrf.study_results as s using(idexperiment) join ambit_qmrf.study_fieldnames as f using(id_fieldname)join (select idstudy,value as c,name from ambit_qmrf.study_conditions join  ambit_qmrf.study_fieldnames using(id_fieldname)) as d using(idstudy) where idstructure=? and (c != "")
			<sql:param value="${param.idstructure}"/>
</sql:query>
<table>
<tr>
<td>
	<c:forEach var="row" items="${rsname.rows}">
		<h3><c:out value="${row.name}"/></h3>
	</c:forEach>

	CAS RN:
	<c:forEach var="row" items="${rscas.rows}">
		<c:out value="${row.casno}"/> <br>
	</c:forEach>

	<h4>Descriptors:</h4>
	<ul>
	<c:forEach var="row" items="${rsdesc.rows}">
		<li>
		<b><c:out value="${row.name}"/></b>=
		<c:out value="${row.value}"/>
	</c:forEach>
	</ul>

	<h4>Toxicity:</h4>
	<ul>
	<c:forEach var="row" items="${rsexp.rows}">
		<li>
		<b><c:out value="${row.name}"/></b>
		<i><c:out value="${row.c}"/></i>
		<b><c:out value="${row.field}"/></b>
		<i><c:out value="${row.value}"/><i>
	</c:forEach>
	</ul>
</td>
<td>

							<img src="
							<c:url value="image.jsp">
								<c:param name="idstructure" value="${param.idstructure}"/>
								<c:param name="weight" value="200"/>
								<c:param name="height" value="200"/>
							</c:url>
							" border="0">
</td>
</tr>
</table>
Found in following QMRFs:
<c:set var="sql" value="select idqmrf,qmrf_number,version,user_name,date_format(qmrf_documents.documents.updated,'${sessionScope.dateformat}') as lastdate,name as attachment,type from ambit_qmrf.struc_dataset join ambit_qmrf.src_dataset using(id_srcdataset) join qmrf_documents.attachments using(name) join qmrf_documents.documents using(idqmrf) where idstructure=${param.idstructure} order by qmrf_documents.documents.${sessionScope.order} ${sessionScope.order_direction} limit ${startrecord},${pagesize}"/>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${sql}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="version" value="Version"/>
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="lastdate" value="Last update"/>
		<jsp:param name="attachment" value="Attachment"/>
		<jsp:param name="attachment type" value="Attachment type"/>
		<jsp:param name="actions" value=""/>

		<jsp:param name="sqlparam" value="${sessionScope['username']}"/>

		<jsp:param name="paging" value="true"/>
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
		<jsp:param name="viewmode" value="html"/>
		<jsp:param name="history" value="false"/>
		</jsp:include>
	</c:otherwise>
</c:choose>


</c:if>
