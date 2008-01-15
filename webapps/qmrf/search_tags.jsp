<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:set var="thispage" value='search.jsp'/>

<jsp:include page="query_settings.jsp" flush="true"/>


<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
  <head>
    <title>QMRF documents</title>
  </head>
  <body>


<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="structures"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:choose>
        <c:when test="${empty param.qmrf_simple}">

        </c:when>
        <c:otherwise>

        </c:otherwise>
</c:choose>

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



<!-- initializing session variables with param.x values -->


<c:if test="${param.threshold != null}">
	<c:set var="search_threshold" value="${param.threshold}" scope="session"/>
</c:if>

<c:if test="${param.similarity != null}">
	<c:set var="search_similarity" value="${param.similarity}" scope="session"/>
</c:if>

<c:if test="${param.cas != null}">
	<c:set var="search_cas" value="${param.cas}" scope="session"/>
</c:if>
<c:if test="${param.formula != null}">
	<c:set var="search_formula" value="${param.formula}" scope="session"/>
</c:if>
<c:if test="${param.name != null}">
	<c:set var="search_name" value="${param.name}" scope="session"/>
</c:if>
<c:if test="${param.identifier != null}">
	<c:set var="search_identifier" value="${param.identifier}" scope="session"/>
</c:if>
<c:if test="${param.smiles != null}">
	<c:set var="search_smiles" value="${param.smiles}" scope="session"/>
</c:if>

<!-- no structure submitted, will do exact search -->

<c:if test="${(param.similarity ne 'exact') && ((empty param.mol) && (empty sessionScope.search_smiles))}">

		<!-- will try to get those compounds and then perform similarity search -->
		<c:catch var="error">
		<a:exactsearch var="sql" params="p"  cas="${param.cas}" name="${param.name}" formula="${param.formula}" mol="${param.mol}" smiles="${sessionScope.search_smiles}" />
		</c:catch>
		<c:choose>
			<c:when test="${!empty error}">
					<blockquote>
					<div class="success">No structure submitted, will proceed with exact search</div>
					</blockquote>
					<c:set var="search_similarity" value="exact" scope="session"/>
			</c:when>
			<c:otherwise>
					<c:catch var="error">
							<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
									${sql}
									<c:forEach var="r" items="${p}">
											<sql:param value="${r}"/>
									</c:forEach>
							</sql:query>
					</c:catch>

					<c:choose>
						<c:when test="${!empty error}">
								<blockquote>
								<div class="success">No structure submitted, will proceed with exact search</div>
								</blockquote>
								<c:set var="search_similarity" value="exact" scope="session"/>
						</c:when>
						<c:otherwise>

								<c:forEach var="row" items="${rs.rows}">
										<c:set var="search_smiles" value="${row.smiles}" scope="session"/>
								</c:forEach>
						</c:otherwise>
					</c:choose>

			</c:otherwise>
		</c:choose>

</c:if>

<c:set var="sql" value=""/>
<c:set var="error" value=""/>

<c:choose>

<c:when test="${sessionScope.search_similarity eq 'fingerprint'}">
	<c:catch var="error">
	<a:similarity var="sql" params="p"  threshold="${param.threshold}" mol="${param.mol}" smiles="${sessionScope.search_smiles}"/>
	</c:catch>
			<h4>
			Similar structures
	(Tanimoto distance > ${param.threshold})

</c:when>
<c:otherwise>
	<c:catch var="error">
	<a:exactsearch var="sql" params="p"  cas="${param.cas}" name="${param.name}" formula="${param.formula}" mol="${param.mol}" smiles="${sessionScope.search_smiles}" />
	</c:catch>
	<h4>
		Exact search

</c:otherwise>
</c:choose>


		<c:if test="${!empty param.cas}">
				CAS No=${param.cas}&nbsp;
		</c:if>
		<c:if test="${!empty param.name}">
				Chemical name =${param.name}&nbsp;
		</c:if>
		<c:if test="${!empty param.formula}">
				Formula=${param.formula}&nbsp;
		</c:if>
			<c:choose>
				<c:when test="${!empty sessionScope.search_smiles}">
									<img src="
								<c:url value="image.jsp">
									<c:param name="mol" value="${sessionScope.search_smiles}"/>
									<c:param name="molType" value="smiles"/>
									<c:param name="weight" value="60"/>
									<c:param name="height" value="60"/>
								</c:url>
								" border="0"/>
				</c:when>
			</c:choose>

</h4>

<c:choose>
<c:when test="${empty sql}">
		<blockquote>
		<div class="error">
				Search criteria not specified!
		</div>
	</blockquote>
</c:when>
<c:when test="${!empty error}">
		<div class="error">
				${error}
		</div>
</c:when>
<c:otherwise>


<c:catch var="error">

		<sql:query var="rs" dataSource="jdbc/ambit_qmrf">
				${sql}
				<c:forEach var="r" items="${p}">
						<sql:param value="${r}"/>
				</c:forEach>
		</sql:query>

</c:catch>


<c:choose>
<c:when test="${rs.rowCount eq 0}">
				<blockquote>
		No records found.
		</blockquote>
</c:when>
<c:when test="${empty error}">

<c:set var="counter" value="0"/>
<table bgcolor="${tablecolor}" width="99%">
<tr bgcolor="#DDDDDD">
	<th>#</th>
	<th>Structure diagram</th>
	<th>Substance identifier</th>
	<c:if test="${param.similarity eq 'fingerprint'}" >
		<th>Similarity</th>
	</c:if>
	<th>QMRF identifier</th>
	<th>Relation <br>between the QMRF <br>and the substance</th>
	<th>View</th>
</tr>

<c:forEach var="row" items="${rs.rows}">
			<c:set var="counter" value="${counter+1}"/>
			<c:set var="clr" value="${tablecolor}"/>
					<c:if test="${(counter % 2)==0}" >
						<c:set var="clr" value="${rowcolor}"/>
					</c:if>

			<sql:query var="rsdocs" dataSource="jdbc/qmrf_documents">
					select qmrf_number,d.idqmrf,idstructure,type,a.name FROM documents as d join attachments as a using(idqmrf) join ambit_qmrf.src_dataset using(name) join ambit_qmrf.struc_dataset using(id_srcdataset) join ambit_qmrf.structure using(idstructure) where idstructure=? and status='published' order by d.updated
					<sql:param value="${row.idsubstance}"/>
			</sql:query>
	<tr bgcolor="${clr}">
				<td align="left">
    				${counter}
		    </td>

				<td>
							<a href="
							<c:url value="proxydisplay.jsp">
								<c:param name="id" value="${row.idsubstance}"/>
							</c:url>
							" target="_blank">
							<img src="
							<c:url value="image.jsp">
								<c:param name="idsubstance" value="${row.idsubstance}"/>
								<c:param name="weight" value="100"/>
								<c:param name="height" value="100"/>
							</c:url>
							" border="0">
						</a>
				</td>
			<td>
			<c:out value="${row.smiles}"/>
			</td>
			<c:if test="${param.similarity eq 'fingerprint'}" >
					<td>
					${row.similarity}
				</td>
			</c:if>

			<!-- QMRF info-->
			<c:set var="qmrfid" value="" />
			<c:set var="qmrftype" value="" />
			<td colspan="2">
			<table width="95%" border="0">
			<c:if test="${rsdocs.rowCount eq 0}">
			<tr><td>No published QMRF documents</td></tr>
			</c:if>
			<c:forEach var="qdoc" items="${rsdocs.rows}">
					<tr >
					<td width="50%">

					<c:set var="qview">
						<a href="<c:url value="view.jsp"><c:param name="id" value="${qdoc.idqmrf}"/></c:url>" target='_blank'>${qdoc.qmrf_number}</a>
					</c:set>
					${qview}

					<a href="
						<c:url value="download.jsp">
					  <c:param name="id" value="${qdoc.idqmrf}"/>
					  <c:param name="filetype" value="html"/>
						</c:url>
					"><img src="images/html.png"  alt="HTML file" border="0"/></a>

					<a href="
						<c:url value="download.jsp">
					  <c:param name="id" value="${qdoc.idqmrf}"/>
					  <c:param name="filetype" value="pdf"/>
						</c:url>
					"><img src="images/pdf.png" border="0" alt="Adobe PDF"/></a>


					<a href="
						<c:url value="download.jsp">
					  <c:param name="id" value="${qdoc.idqmrf}"/>
					  <c:param name="filetype" value="xls"/>
						</c:url>
					" ><img src="images/xls.png" " alt="MS Excel XLS file" border="0"/></a>

					<a href="
						<c:url value="download.jsp">
					  <c:param name="filetype" value="xml"/>
					  <c:param name="id" value="${qdoc.idqmrf}"/>
					  <c:param name="action" value="dbattachments"/>
						</c:url>
					"><img src="images/xml.png"  alt="QMRF XML" border="0"/></a>

					</td>
					<td width="50%">
					<c:choose>
					<c:when test="${qdoc.type eq 'data_training'}">
							<c:set var="qtype" value="Substance in <b>training</b> set" />
					</c:when>
					<c:when test="${qdoc.type eq 'data_validation'}">
							<c:set var="qtype" value="Substance in <b>validation</b> set" />
					</c:when>
					<c:otherwise>
								<c:set var="qtype" value="?" />
					</c:otherwise>
				</c:choose>
					${qtype}
					</td>
					</tr>
			</c:forEach>
			</table>
			</td>
			<!-- End QMRF info -->
			<!-- View-->
			<td>
					<a href="
							<c:url value="proxydisplay.jsp">
								<c:param name="id" value="${row.idsubstance}"/>
							</c:url>
							" target="_blank">View substance
						</a>
						<br>

			</td>

	</tr>
</c:forEach>
</table>

</c:when>
<c:otherwise>
		<div class="error">
				${error}
		</div>
</c:otherwise>
</c:choose>
<!-- empty error-->


</c:otherwise>
</c:choose>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
  </body>
</html>

