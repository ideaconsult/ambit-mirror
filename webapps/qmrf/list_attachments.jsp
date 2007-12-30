<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!-- expects ?id=qmrfid-->
<c:set var="idqmrf" value="${sessionScope['qmrf_document']}" />
<c:if test="${!empty param.id}" >
	<c:set var="idqmrf" value="${param.id}" />
</c:if>

<c:choose>
<c:when test="${sessionScope['isadmin']=='true'}" >
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select d.idqmrf,qmrf_number,name,format,description,type,a.updated,idattachment,original_name,imported,status from documents as d join attachments as a using(idqmrf) where d.idqmrf=? order by type
		<sql:param value="${idqmrf}"/>
	</sql:query>

</c:when>
<c:otherwise>
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select d.idqmrf,qmrf_number,name,format,description,type,a.updated,idattachment,original_name,imported,status from documents as d join attachments as a using(idqmrf) where d.idqmrf=? and ((user_name=?) || (status='published')) order by type
		<sql:param value="${idqmrf}"/>
		<sql:param value="${sessionScope['username']}"/>
	</sql:query>

</c:otherwise>
</c:choose>

  	    <tr  bgColor="#DDDDDD">
			<th>#</th>
    	<th>File</th>
    	<th>File format</th>
    	<th>Description</th>
    	<th>Type</th>
    	<th>Updated</th>
    	<th></th>
  	</tr>
<c:choose>
<c:when test="${rs.rowCount > 0}">

  	<c:set var="r" value="1"/>
		<c:forEach var="row" items="${rs.rows}">
			<c:set var="clr" value="#FFFFFF"/>
			<c:if test="${(r % 2)==0}" >
				<c:set var="clr" value="#EEEEEE"/>
		  </c:if>
		  <c:if test="${!empty param.idattachment && (param.idattachment eq row.idattachment)}">
			  <c:set var="clr" value="#FFFF99"/>
		  </c:if>
	    <tr bgcolor="${clr}">
	    	<td>${r}</td>
	    	<td>
	    				<c:set var="filename" value="${row.original_name}" />
              <c:set var="delim" value="\//" />
              <c:set value="${fn:split(filename,delim)}" var="paths"/>


							<c:if test="${fn:length(paths)>0}">
              <c:set var="filename" value="${paths[fn:length(paths)-1]}" />
              </c:if>

	    		${filename}

	    	</td>
	    	<td>${row.format}</td>
	    	<td>${row.description}</td>
	    	<td>${row.type}</td>
	    	<td>${row.updated}</td>
	    	<td>
					<a href="
						<c:url value="download_attachment.jsp">
					  <c:param name="name" value="${row.name}"/>
						</c:url>
					">
					<img src="images/download.gif" height="16" width="16" alt="download.gif" title="Download attachment" border="0"/></a>


	    	<c:if test="${(row.status eq 'draft' || row.status eq 'returned for revision') && (row.imported eq '0') }" >

					<a href="
						<c:url value="edit_attachmentsdb.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						<c:param name="idattachment" value="${row.idattachment}"/>
						<c:param name="action" value="delete"/>
						</c:url>
					" ><img src="images/delete.png" height="16" width="16" alt="delete.png" title="Delete attachment" border="0"/></a>


	    	</c:if>

	    		<c:if test="${(row.type ne 'document') && (row.imported eq 'false')}" >
	    			<a href="
						<c:url value="edit_attachmentsdb.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						<c:param name="idattachment" value="${row.idattachment}"/>					  
						<c:param name="name" value="${row.name}"/>
						<c:param name="action" value="properties"/>						
						</c:url>
					"><img src="images/information.png" height="16" width="16" alt="information.png" title="Configure properties to import" border="0"/></a>
				</c:if>
					
	    		<c:if test="${(row.type ne 'document') && (row.imported eq 'false') && (sessionScope['isadmin'] eq 'true')}" >
	    			<a href="
						<c:url value="admin_import.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						<c:param name="idattachment" value="${row.idattachment}"/>
						</c:url>
					"><img src="images/import.png" height="16" width="16" alt="import.png" title="Import structures from the selected attachment into structure searchable database" border="0"/></a>
					</c:if>

	    		<c:if test="${row.imported eq 'true'}" >
	    			<a href="
						<c:url value="list_structures.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="idattachment" value="${row.idattachment}"/>
						<c:param name="changedirection" value="false"/>
						</c:url>
					" target="_blank"><img src="images/benzene.gif" height="16" width="16" alt="benzene.gif" title="View structures" border="0"/></a>
					</c:if>

	    	</td>
	  	</tr>
			<c:set var="r" value="${r+1}"/>
  </c:forEach>

</c:when>
<c:otherwise>

</c:otherwise>

</c:choose>