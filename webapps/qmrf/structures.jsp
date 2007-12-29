<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${!empty param.id}">

<c:choose>

<c:when test="${empty param.idattachment}">
	<c:choose>
		<c:when test="${empty sessionScope['username']}">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select  d.qmrf_number,d.idqmrf,idstructure,type,a.name,a.description FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) where d.idqmrf=? and status='published' order by type
				<sql:param value="${param.id}"/>
			</sql:query>
		</c:when>
		<c:when test="${sessionScope['isadmin'] eq 'true'}">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select  d.qmrf_number,d.idqmrf,idstructure,type,a.name,a.description FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) where d.idqmrf=? order by type
				<sql:param value="${param.id}"/>
			</sql:query>
		</c:when>
		<c:otherwise>
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select  d.qmrf_number,d.idqmrf,idstructure,type,a.name,a.description FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) where d.idqmrf=? and ((user_name=?) or (status='published')) order by type
				<sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>
			</sql:query>
		</c:otherwise>
	</c:choose>
</c:when>

<c:otherwise>
	<c:choose>
		<c:when test="${empty sessionScope['username']}">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select d.qmrf_number,d.idqmrf,idstructure,type,a.name,a.description FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) where d.idqmrf=? and a.idattachment=? and status='published' order by type
				<sql:param value="${param.id}"/>
				<sql:param value="${param.idattachment}"/>
			</sql:query>
		</c:when>
		<c:when test="${sessionScope['isadmin'] eq 'true'}">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select d.qmrf_number,d.idqmrf,idstructure,type,a.name,a.description FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) where d.idqmrf=? and a.idattachment=? order by type
				<sql:param value="${param.id}"/>
				<sql:param value="${param.idattachment}"/>
			</sql:query>
		</c:when>
		<c:otherwise>
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select d.qmrf_number,d.idqmrf,idstructure,type,a.name,a.description FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) where d.idqmrf=? and a.idattachment=? and ((user_name=?) or (status='published')) order by type
				<sql:param value="${param.id}"/>
				<sql:param value="${param.idattachment}"/>
				<sql:param value="${sessionScope['username']}"/>
			</sql:query>
		</c:otherwise>
	</c:choose>
</c:otherwise>
</c:choose>

<c:set var="viewmode" value="html"/>
<c:choose>
	<c:when test="${empty param.viewmode}">
			<c:set var="viewmode" value="html"/>
	</c:when>
	<c:when test="${param.viewmode eq 'applet'}">
			<c:set var="viewmode" value="applet"/>
	</c:when>
	<c:when test="${param.viewmode eq 'html'}">
			<c:set var="viewmode" value="html"/>
	</c:when>
	<c:otherwise>
			<c:set var="viewmode" value="html"/>
	</c:otherwise>
</c:choose>


<c:set var="tabletitle" value="Structures"/>
<table width="100%">

	<tr bgcolor="#D6DFF7">
		<th>#</th>
		<th><a name="#qmrf_struc">Structure diagram</a></th>
		<th>Substance identifier</th>
		<th>Relation <br>between the QMRF <br>and the substance</th>
		<th colspan="2">Found in</th>
		<th>View</th>
	</tr>
	<c:set var="record" value="1"/>
	<c:forEach var="row" items="${rs.rows}">
		<c:set var="tabletitle" value="${qmrf_number}"/>

		<c:set var="clr" value="#FFFFFF"/>
		<c:if test="${(record % 2)==0}" >
		<c:set var="clr" value="#EEEEEE"/>
		</c:if>
		<tr bgcolor="${clr}">
		<td> ${record}	</td>
		<td>
		<img src="
			<c:url value="image.jsp">
			<c:param name="idstructure" value="${row.idstructure}"/>
			<c:param name="weight" value="100"/>
			<c:param name="height" value="100"/>
			</c:url>
		" border="0">
		</td>
		<td>
		<c:catch var="exception">
			<sql:query var="subrs" dataSource="jdbc/ambit_qmrf">
				select idsubstance,casno,name,alias from structure left join cas using(idstructure) left join name using(idstructure) left join alias using(idstructure) where structure.idstructure=?;
				<sql:param value="${row.idstructure}"/>
			</sql:query>
		</c:catch>
		<c:choose>

		<c:when test="${empty exception}">
			<c:forEach var="subrow" items="${subrs.rows}">
				<c:if test="${!empty subrow.casno}">
					${subrow.casno} <br>
				</c:if>
				<c:if test="${!empty subrow.name}">
					${subrow.name} <br>
				</c:if>
				<c:if test="${!empty subrow.alias}">
					${subrow.alias}
				</c:if>

				<c:set var="idsubstance" value="${subrow.idsubstance}"/>
			</c:forEach>
		</c:when>
		<c:otherwise>
			${exception}
			<c:set var="idsubstance" value=""/>
		</c:otherwise>
		</c:choose>

		</td>
		<td> ${row.type}	</td>
		<td> ${row.description}	</td>
		<td>
					<a href="
						<c:url value="download_attachment.jsp">
					  <c:param name="name" value="${row.name}"/>
					  <c:param name="filetype" value="${row.type}"/>
					  <c:param name="format" value="${row.format}"/>
						</c:url>
					">
					<img src="images/download.gif" height="16" width="16" alt="${row.name}" border="0"/></a>
		</td>
		<td>
		<a href="
			<c:url value="proxydisplay.jsp">
			<c:param name="id" value="${idsubstance}"/>
			</c:url>
		">View substance</a>
		</td>
		  </tr>
		<c:set var="record" value="${record+1}"/>

	</c:forEach>
</table>

<!--
<c:forEach var="hname" items="${pageContext.request.headerNames}">
    <c:forEach var="hvalue" items="${headerValues[hname]}">
        ${hname}&nbsp;${hvalue}
        <p>
    </c:forEach>
</c:forEach>
-->

<!--

<c:set var="url">${pageContext.request.scheme}://${header["host"]}${pageContext.request.requestURI}</c:set>
<br>
		<a href="
			<c:url value="${url}#qmrf_doc">
			<c:param name="id" value="${param.id}"/>
			<c:param name="idstructure" value="${param.idstructure}"/>
			<c:param name="viewmode" value="${param.viewmode}"/>
			</c:url>
		">Back to the begining of QMRF document</a>

		<a href="
			<c:url value="${url}#qmrf_struc">
			<c:param name="id" value="${param.id}"/>
			<c:param name="idstructure" value="${param.idstructure}"/>
			<c:param name="viewmode" value="${param.viewmode}"/>

			</c:url>
		">Back to the begining of structures list</a>

-->
</c:if>