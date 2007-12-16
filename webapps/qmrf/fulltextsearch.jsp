<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>

<c:choose>
	<c:when test="${empty param.pagesize}">
  	<c:set var="pagesize" value="10"/>
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

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,user_name,updated,status,xml from documents where status = 'published' and match (xml) against (? in boolean mode) limit ${startrecord},${pagesize}
	<sql:param value="${param.fulltext}"/>
</sql:query>

<c:if test="${param.paging eq 'true'}">
				<div  style="color:#36393D;background:#FFFFFF;text-align:left" >
				Pages:
				<c:forEach var='item' begin='0' end='${param.page+1}'>
							<a href="
							<c:url value="${thispage}">
								<c:param name="page" value="${item}"/>
								<c:param name="pagesize" value="${param.pagesize}"/>
							</c:url>
							">${item+1}</a>
				</c:forEach>

			</div>
</c:if>


<table width="100%" border="0">
<tr bgcolor="#DDDDDD">
	<th>QMRF number</th>
	<th>Title</th>
	<th>Keywords</th>

	<th align="left"> View/Download</th>

		<c:choose>
		<c:when test="${empty param.actions}">
	  </c:when>
		<c:when test="${param.actions eq 'admin'}">
			<th align="left"> Actions</th>
	  </c:when>
		<c:when test="${param.actions eq 'user'}">
			<th align="left"> Actions</th>
	  </c:when>
	  <c:otherwise>
	</c:otherwise>
	</c:choose>

</tr>
<c:set var="record" value="0"/>
<c:forEach var="row" items="${rs.rows}">
	<c:set var="record" value="${record+1}"/>
	<x:parse xml="${row.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
		<c:set var="clr" value="#FFFFFF"/>
		<c:if test="${(record % 2)==0}" >
			<c:set var="clr" value="#EEEEEE"/>
		</c:if>
	<tr bgcolor="${clr}">
	<td>
		<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QMRF_Summary/QMRF_number"/>
		</td>
		<td>
			<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_title"/>
		</td>
		<td>
		<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QMRF_Summary/keywords"/>
	</td>
	<!--
	<c:forEach var="columnName" items="${rs.columnNames}">
			<c:set var="title">${param[columnName]}</c:set>
			<c:choose>
			<c:when test="${empty title}">
		  </c:when>
			<c:when test="${title eq 'ID'}">
		  </c:when>
		  <c:otherwise>
				<td align="left">
    				${row[columnName]}
		    </td>
	    </c:otherwise>
	  </c:choose>
	</c:forEach>
-->
    <td>
					<a href="
						<c:url value="view.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>

						</c:url>
					" target="_blank"><img src="images/view.png" height="18" width="18" alt="View" border="0"/></a>

					<a href="
						<c:url value="view.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						<c:param name="changedirection" value="false"/>
						<c:param name="viewmode" value="attachments"/>
						</c:url>
					" target="_blank"><img src="images/clip031.gif" height="8" width="8" alt="Attachments" border="0"/></a>


					<a href="
						<c:url value="download_xml.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						</c:url>
					" target="_blank"><img src="images/xml.gif" height="18" width="36" alt="QMRF XML" border="0"/></a>

					<a href="
						<c:url value="download_pdf.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						</c:url>
					" target="_blank"><img src="images/pdficon_small.gif" height="18" width="18" border="0" alt="Adobe PDF"/></a>


					<a href="
						<c:url value="download_xml.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
						</c:url>
					" target="_blank"><img src="images/xls.jpg" height="18" width="18" alt="MS Excel XLS file" border="0"/></a>

		 </td>
		   <td>
				<c:choose>
				<c:when test="${empty param.actions}">
			  </c:when>
				<c:when test="${param.actions eq 'admin'}">
					<!-- Admin actions -->
							<c:choose>
							  <c:when test="${row.status eq 'published'}">

							  </c:when>
							  <c:when test="${row.status eq 'archived'}">
									<i>newer revisions exist - TODO link</i>
							  </c:when>
							  <c:when test="${row.status eq 'submitted'}">
									<a href="
									<c:url value="admin_review.jsp">
									  <c:param name="status" value="under review"/>
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Review</a>
							  </c:when>
							  <c:when test="${row.status eq 'under review'}">
									<a href="
									<c:url value="admin_review.jsp">
									  <c:param name="status" value="under review"/>
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Review</a>

									<a href="
									<c:url value="admin_review.jsp">
									  <c:param name="status" value="published"/>
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Publish</a>

									<a href="
									<c:url value="admin_review.jsp">
									  <c:param name="status" value="returned for revision"/>
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Return to user</a>

							  </c:when>
							  <c:when test="${row.status eq 'returned for revision'}">
							    <i>User action required</i>
							  </c:when>
							  <c:otherwise>
							  </c:otherwise>
							</c:choose>
					<!-- Admin actions end-->
			  </c:when>
				<c:when test="${param.actions eq 'user'}">
					<!-- User actions -->
						<c:choose>
						<c:when test="${row.status eq 'submitted'}">
							<i>Waiting for administrator approval</i>
						</c:when>
					  <c:when test="${row.status eq 'draft'}">
									<a href="
									<c:url value="edit.jsp">
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Edit</a>
									<a href="
									<c:url value="update_status.jsp">
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Submit</a>
					  </c:when>
					  <c:when test="${row.status eq 'returned for revision'}">
									<a href="
									<c:url value="edit.jsp">
									  <c:param name="id" value="${row.idqmrf}"/>
									</c:url>
									">Edit</a>
					  </c:when>
					  <c:otherwise>
					  </c:otherwise>
					</c:choose>
					<!-- User actions end-->
			  </c:when>
			  <c:otherwise>
				</c:otherwise>
				</c:choose>

		</td>

  </tr>
</c:forEach>

</table>

<hr>
<c:if test="${param.paging eq 'true'}">
Current page ${param.page+1} Rows per page: ${param.pagesize}
</c:if>
<br>





