<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>


<c:set var="thispage" value='admin_status.jsp'/>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>

<c:if test="${(empty sessionScope.viewmode) || (sessionScope.viewmode ne 'qmrf_manager')}" >
  <c:redirect url="/index.jsp">
  	<c:param name="highlighted" value="welcome"/>
  </c:redirect>
</c:if>

<c:if test="${empty sessionScope['ismanager']}" >
  <c:redirect url="/index.jsp">
  	<c:param name="highlighted" value="welcome"/>
  </c:redirect>
</c:if>

<c:if test="${sessionScope['ismanager']=='false'}" >
  <c:redirect url="/index.jsp">
  	<c:param name="highlighted" value="welcome"/>
  </c:redirect>
</c:if>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<c:choose>
	<c:when test="${empty param.source}">
			<c:set var="source" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="source" value="${param.source}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.status}">
			<c:set var="status" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="status" value="${param.status}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.users}">
			<c:set var="users" value=""/>
	</c:when>
	<c:otherwise>
		<c:set var="users" value="${param.users}"/>
	</c:otherwise>
</c:choose>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database statistics"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="catalog"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>        
</jsp:include>


<table width="50%" bgcolor="#FFFFFF"><tr bgcolor="#D6DFF7">
<td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="documents"/>
	</c:url>
">Documents</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="users"/>
	</c:url>
">Users</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="catalogues"/>
	</c:url>
">Catalogues</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="structures"/>
	</c:url>
">Structures</a>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="config"/>
	</c:url>
">Configuration</a>
</td>
</td><td>
<a href="
	<c:url value="${thispage}">
	<c:param name="source" value="mysql"/>
	</c:url>
">MySQL</a>
</td>
</tr>
</table>

<c:if test="${param.source eq 'documents'}">
		<h3>QMRF Documents</h3>

		<c:catch var="exception">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			SELECT count(idqmrf) as no,status  FROM documents d group by status order by status;
		</sql:query>
		</c:catch>


		<c:choose>
		<c:when test="${!empty exception}">
			<div class="error">
			${exception}
		</div>
		</c:when>
		<c:otherwise>
			<table bgcolor="${tablecolor}">
			<tr bgcolor="${rowcolor}"><th>Document status</th><th>Number of documents</th><th>Report</th></tr>

			<c:forEach var="row" items="${rs.rows}">
			<c:set var="mp">
			<fmt:formatNumber type="number" value="${(row.no / pagesize) +0.5}" pattern="###"/>
			</c:set>
				<tr bgcolor="${rowcolor}">


				<td>${row['status']}</td>
				<td>${row['no']}
					</td>
					<td>
					<a href="
			<c:url value="download_report.jsp">
				<c:param name="status" value="${row['status']}"/>
				<c:param name="source" value="documents"/>
			</c:url>
		"><img src="images/html.png"  alt="HTML file" border="0"/></a>


					</td>
			</tr>
			</c:forEach>
		</tr>
		</table>
		</c:otherwise>
		</c:choose>
</c:if>

<c:if test="${param.source eq 'users'}">
<h3>QMRF Users</h3>

<c:catch var="exception">
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	SELECT registration_status,count(user_name) as un FROM users group by registration_status order by registration_status;
</sql:query>
</c:catch>


<c:choose>
<c:when test="${!empty exception}">
	<div class="error">
	${exception}
</div>
</c:when>
<c:otherwise>
	<table bgcolor="#FFFFFF">
	<tr bgcolor="${rowcolor}"><th>Registration status</th><th>Number of users</th><th>Report</th></tr>

	<c:forEach var="row" items="${rs.rows}">
		<tr bgcolor="${rowcolor}">
			<td>
			${row['registration_status']}
			</td>

			<td>${row['un']}</td>
			<td>
			<a href="
				<c:url value="download_users.jsp">
				<c:param name="users" value="${row['registration_status']}"/>
	</c:url>
	">
	<img src="images/html.png"  alt="HTML file" border="0"/></a>
</td>


	</tr>
	</c:forEach>
</tr>
</table>
</c:otherwise>
</c:choose>

</c:if>

<c:if test="${param.source eq 'structures'}">
	<table width="95%" frame="box" rules="groups">
		<thead>
		<tr>
		<th bgcolor="#CCCCCC" colspan="6" align="center">Structures</th>
		<th bgcolor="#DDDDDD" colspan="3" align="center">Imported from attachment</th>
		<th bgcolor="#CCCCCC" align="center" colspan="3">QMRF Document</th>
		</tr>
		<tr valign="top" align="left">
		<th bgcolor="#CCCCCC">#</th>
		<th bgcolor="#CCCCCC" width="5%">Number of structures</th>
		<th bgcolor="#CCCCCC" width="5%">CAS Numbers</th>
		<th bgcolor="#CCCCCC" width="5%">IUPAC names</th>
		<th bgcolor="#CCCCCC" width="5%">Identifiers</th>
		<th bgcolor="#CCCCCC" width="5%">Number of descriptors values</th>		
		<th colspan="2" bgcolor="#DDDDDD">Name</th>
		<th bgcolor="#DDDDDD" >Type</th>		
		<th bgcolor="#CCCCCC">Number</th>
		<th bgcolor="#CCCCCC" width="25%">Title</th>
		<th bgcolor="#CCCCCC">Download</th>
		</tr>
		</thead>
		
		<tbody>
		<c:catch var="exception">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				select idattachment,idqmrf,qmrf_number,qmrf_title,name,description,type,count(s.idstructure) as c,s.id_srcdataset as iddataset from ambit_qmrf.struc_dataset as s join ambit_qmrf.src_dataset using(id_srcdataset) right join qmrf_documents.attachments using(name) join documents using(idqmrf) group by id_srcdataset order by idqmrf
			</sql:query>
			<c:set var="r" value="1"/>
			<c:forEach var="row" items="${rs.rows}">


				<tr>
				<td>${r}</td>

				<td align="left">
	    			<a href="
						<c:url value="structures.jsp">
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="idattachment" value="${row.idattachment}"/>
						<c:param name="changedirection" value="false"/>
						</c:url>
					" target="_blank">${row.c}</a>				
				</td>

				<sql:query var="subrs" dataSource="jdbc/ambit_qmrf">
					select count(casno) as c from cas join struc_dataset using(idstructure) where id_srcdataset=?
					<sql:param value="${row.iddataset}"/>
				</sql:query>				
				
				<td>
				<c:forEach var="subrow" items="${subrs.rows}">
					${subrow.c}&nbsp;	
				</c:forEach>
				</td>	
								
				<sql:query var="subrs" dataSource="jdbc/ambit_qmrf">
					select count(name) as c from name join struc_dataset using(idstructure) where id_srcdataset=?
					<sql:param value="${row.iddataset}"/>
				</sql:query>				
				
				<td>
				<c:forEach var="subrow" items="${subrs.rows}">
					${subrow.c}&nbsp;	
				</c:forEach>
				</td>	
										
				<sql:query var="subrs" dataSource="jdbc/ambit_qmrf">
					select count(alias) as c from alias join struc_dataset using(idstructure) where id_srcdataset=?
					<sql:param value="${row.iddataset}"/>
				</sql:query>				
				
				<td>
				<c:forEach var="subrow" items="${subrs.rows}">
					${subrow.c}&nbsp;	
				</c:forEach>
				</td>	
				
				<sql:query var="subrs" dataSource="jdbc/ambit_qmrf">
					select count(iddescriptor) as descr from dvalues join ddictionary using(iddescriptor) join struc_dataset using(idstructure) where id_srcdataset=?
					<sql:param value="${row.iddataset}"/>
				</sql:query>				
				<td>
				<c:forEach var="subrow" items="${subrs.rows}">
					${subrow.descr}&nbsp;	
				</c:forEach>
				</td>				
				
				<td>
					${row.description}
				</td><td><a href="
						<c:url value="download_attachment.jsp">
					  <c:param name="name" value="${row.name}"/>
						</c:url>
					"><img src="images/download.gif" height="16" width="16" alt="download.gif" title="Download attachment" border="0"/></a>				
				</td>
				<td>${row.type}</td>
				<td>${row.qmrf_number}</td>
				<td>${row.qmrf_title}</td>				
				<td>
					<c:url value="download.jsp" var="url">
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="filetype" value="html"/>
					</c:url>
					<a href="
					<c:out value="${url}" escapeXml="true" />
					"><img src="images/html.png"  alt="html.png" title="Download QMRF document as HTML file" border="0"></a>
				
					<c:url value="download.jsp" var="url">
						<c:param name="id" value="${row.idqmrf}"/>
						<c:param name="filetype" value="pdf"/>
					</c:url>
					<a href="
						<c:out value="${url}" escapeXml="true" />
					"><img src="images/pdf.png" border="0" alt="pdf.png" title="Download QMRF document as Adobe PDF" ></a>				
					
					<c:url value="download.jsp" var="url">
					 <c:param name="id" value="${row.idqmrf}"/>
					 <c:param name="filetype" value="xls"/>
					</c:url>
					<a href="
						<c:out value="${url}" escapeXml="true" />
					" ><img src="images/xls.png" alt="xls.png" title="Download QMRF document as MS Excel XLS file" border="0"></a>

					<c:url value="download.jsp" var="url">
					  <c:param name="filetype" value="xml"/>
					  <c:param name="id" value="${row.idqmrf}"/>
					  <c:param name="action" value="dbattachments"/>
						</c:url>
					<a href="
						<c:out value="${url}" escapeXml="true" />
					"><img src="images/xml.png"  alt="xml.png" title="Download QMRF document as XML file" border="0"></a>
					
				</td>

				</tr>
				<c:set var="r" value="${r+1}"/>
			</c:forEach>		
		</c:catch>
		${exception}
		</tbody>
	</table>
</c:if>

<c:if test="${param.source eq 'catalogues'}">
		<h3>QMRF Catalogues</h3>

			<table bgcolor="#FFFFFF">
			<tr bgcolor="#D6DFF7">
			<th>Catalogue</th>
			<th>Number of entries</th>
			<th>Report</th>
			</tr>
				<!-- endpoints -->
			<c:catch var="exception">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT count(*) as no FROM catalog_endpoints c;
			</sql:query>
			</c:catch>


			<c:choose>
			<c:when test="${!empty exception}">
				<div class="error">
				${exception}
			</div>
			</c:when>
			<c:otherwise>

				<tr bgcolor="${rowcolor}"><th>Endpoints</th>

				<c:forEach var="row" items="${rs.rows}">
						<td>
						${row['no']}
						</td><td>
						<a href="
					<c:url value="download_catalogs.jsp">
				<c:param name="source" value="endpoints_xml.jsp"/>
				<c:param name="name" value="endpoints.html"/>
					</c:url>
					">
					<img src="images/html.png"  alt="HTML file" border="0"/></a>
				</c:forEach>
			</tr>

			</c:otherwise>
			</c:choose>

				<!-- algorithms -->
			<c:catch var="exception">
			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT count(*) as no FROM catalog_algorithms c;
			</sql:query>
			</c:catch>


			<c:choose>
			<c:when test="${!empty exception}">
				<div class="error">
				${exception}
			</div>
			</c:when>
			<c:otherwise>

				<tr bgcolor="${rowcolor}"><th>Algorithms</th>

				<c:forEach var="row" items="${rs.rows}">

						<td>
						${row['no']}
						</td><td>
						<a href="
					<c:url value="download_catalogs.jsp">
				<c:param name="source" value="algorithm_xml.jsp"/>
				<c:param name="name" value="algorithms.html"/>
					</c:url>
					">
					<img src="images/html.png"  alt="HTML file" border="0"/></a>
				</c:forEach>
			</tr>

			</c:otherwise>
			</c:choose>

<!-- software -->
		<c:catch var="exception">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT count(*) as no FROM catalog_software c;
	</sql:query>
	</c:catch>


	<c:choose>
	<c:when test="${!empty exception}">
		<div class="error">
		${exception}
	</div>
	</c:when>
	<c:otherwise>

		<tr bgcolor="${rowcolor}"><th>Software packages</th>

		<c:forEach var="row" items="${rs.rows}">

			<td>
				${row['no']}
				</td><td>
				<a href="
			<c:url value="download_catalogs.jsp">
			<c:param name="source" value="software_xml.jsp"/>
			<c:param name="name" value="software.html"/>
			</c:url>
			">

			<img src="images/html.png"  alt="HTML file" border="0"/></a>
		</c:forEach>
	</tr>

	</c:otherwise>
	</c:choose>

	<!-- authors -->
		<c:catch var="exception">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT count(*) as no FROM catalog_authors c;
	</sql:query>
	</c:catch>


	<c:choose>
	<c:when test="${!empty exception}">
		<div class="error">
		${exception}
	</div>
	</c:when>
	<c:otherwise>

		<tr bgcolor="${rowcolor}"><th>Authors</th>

		<c:forEach var="row" items="${rs.rows}">

				<td>
				${row['no']}
				</td><td>
				<a href="
			<c:url value="download_catalogs.jsp">
				<c:param name="source" value="authors_xml.jsp"/>
				<c:param name="name" value="authors.html"/>
			</c:url>
			">
			<img src="images/html.png"  alt="HTML file" border="0"/></a>
	</td>
		</c:forEach>
	</tr>

	</c:otherwise>
	</c:choose>



	</table>

</c:if>

<c:if test="${source eq 'config'}">
<table bgcolor="#DDDDDD">
<tr>
<th>Property</th>
<th>Current value</th>
<th>Comment</th>
</tr>
<tr bgcolor="#FFFFFF">
<th>Mail server</th>
<td>${initParam["mail-server"]}</td>
<td><i>Required for registration to work properly. Edit web.xml in order to change</i></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>e-mail from</th>
<td>${initParam["mail-from"]}</td>
<td><i>Required in order to send confirmation mails upon registration. Edit web.xml in order to change</i></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>Folder to store attachments</th>
<td>${initParam["attachments-dir"]}</td>
<td><i>This folder is used to store attachments uploaded by users. Please ensure the web application server has write permission. Edit web.xml in order to change</i></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>Support e-mail</th>
<td>${initParam["support-email"]}</td>
<td><i>This email will appear within the footer of the first page</i><h6>"For information about this site please contact support_email_here"</h6></td>
</tr>
<tr bgcolor="#FFFFFF">
<th>MySQL UTF-8 support</th>
<td>
	<c:catch>
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SHOW VARIABLES LIKE 'character_set%'
	</sql:query>
	<c:forEach var="row" items="${rs.rows}">
		${row.Variable_name}&nbsp;${row.Value}
		<br>
	</c:forEach>
</c:catch>
</td>
<td>
	<i>
NOTE: MySQL server should be started with default charset UTF-8
The settings on the left should be:<br/>
character_set_client utf8<br/>
character_set_connection utf8<br/>
character_set_database utf8<br/>
character_set_filesystem binary<br/>
character_set_results utf8<br/>
character_set_server utf8<br/>
character_set_system utf8<br/>
</i>
</td>
</tr>
</table>
</c:if>

<c:if test="${source eq 'mysql'}">


	<c:set var="commands">
	select version() as Version,show collation like 'utf%',show status
	</c:set>

	<c:forTokens var="command" items="${commands}" delims=",">
		<c:catch var="err">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			${command}
		</sql:query>
		<table>
			<tr><th>${command}</th></tr>
			<tr>
			<c:forEach var="columnName" items="${rs.columnNames}">
					<th>${columnName}</th>
			</c:forEach>
			</tr>
			<c:forEach var="row" items="${rs.rows}">
				<tr>
				<c:forEach var="columnName" items="${rs.columnNames}">
						<td>
						${row[columnName]}
						</td>
				</c:forEach>
				</tr>
			</c:forEach>
		</table>
		</c:catch>
		${err}
	</c:forTokens>
</c:if>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
  </body>
</html>

