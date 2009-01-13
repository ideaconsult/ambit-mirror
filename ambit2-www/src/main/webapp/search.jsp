<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>


		<sql:query var="rs" dataSource="jdbc/ambit2">
			SELECT count(idstructure) as c from structure

		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			EINECS : ${row.c} records
		</c:forEach>
		<sql:query var="rs" dataSource="jdbc/ambit2">
			select name,idproperty from properties 
		</sql:query>
		<table bgcolor="#DDDDDD" border="0">
			<tr bgcolor="#DDDDDD">
				<th align="left">Field</th>
				<th colspan="2" align = "left">Structures</th>
			</tr>
		<c:forEach var="row" items="${rs.rows}">
			<tr bgcolor="#FFFFFF">
			<td align="right">${row.name}</td>
			<td colspan="2">

						<sql:query var="subq" dataSource="jdbc/ambit2">
						SELECT count(idstructure) as c,value,idproperty  FROM values_string where idproperty=? group by value
							<sql:param value="${row.idproperty}"/>
						</sql:query>

						<c:forEach var="rq" items="${subq.rows}">
												<c:url value="" var="url">
													<c:param name="page" value="${item}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${rq.idproperty}"/>
													<c:param name="fieldvalue" value="${rq.value}"/>
												</c:url>
												<a href="
													<c:out value="${url}" escapeXml="true" />
												">${rq.value}</a>
											(${rq.c})

						</c:forEach>
				</td>
			</tr>
		</c:forEach>

		<sql:query var="rs" dataSource="jdbc/ambit2">
			SELECT name,idproperty  FROM properties
		</sql:query>

<form>
	<tr bgcolor="#FFFFFF">
	<td align="right">
	<select name="fieldname">
	<c:forEach var="row" items="${rs.rows}">
		<c:set var="tag" value=""/>
			<c:if test="${row.idproperty eq param.fieldname}">
				<c:set var="tag" value="selected"/>
			</c:if>
		<option value="${row.idproperty}" ${tag} >${row.name}</option>
	</c:forEach>
</select>
</td>
<td colspan="2">
	<input type="text" name="fieldvalue" value="${param.fieldvalue}"/>
	<input type="submit" value="Search"/>
</td>
</tr>
</form>


	</table>

<c:set var="page" value="${param.page}"/>
<c:set var="pagesize" value="${param.pagesize}"/>


<c:if test="${empty param.page}">
		<c:set var="page" value="0"/>
</c:if>
<c:if test="${param.page lt 0}">
		<c:set var="page" value="0"/>
</c:if>
<c:if test="${empty param.pagesize}">
		<c:set var="pagesize" value="20"/>
</c:if>

<br>
												<c:url value="" var="prev">
													<c:param name="page" value="${page-1}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${param.fieldname}"/>
													<c:param name="fieldvalue" value="${param.fieldvalue}"/>
												</c:url>
												<c:url value="" var="next">
													<c:param name="page" value="${page+1}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${param.fieldname}"/>
													<c:param name="fieldvalue" value="${param.fieldvalue}"/>
												</c:url>
												<a href="
													<c:out value="${prev}" escapeXml="true" />
												"><<</a>
Page ${page+1}
												<a href="
													<c:out value="${next}" escapeXml="true" />
												">>></a>
<br>
Records per page ${pagesize}
<br>
<c:if test="${!empty param.fieldname}">
	<c:if test="${!empty param.fieldvalue}">


		<sql:query var="rc" dataSource="jdbc/ambit2">
			select name from properties
		</sql:query>
		<c:set var="tokens" value=""/>
		<c:set var="delim" value=""/>
		<c:forEach var="row" items="${rc.rows}">
			<c:set var="tokens" value="${tokens}${delim}${row.name}"/>
			<c:set var="delim" value=","/>
		</c:forEach>


		<sql:query var="rc" dataSource="jdbc/ambit2">
			select name from properties where idproperty=?
			<sql:param value="${param.fieldname}"/>
		</sql:query>
		<c:forEach var="row" items="${rc.rows}">
			Show structures with ${row.name} = "${param.fieldvalue}"
		</c:forEach>

		<sql:query var="rc" dataSource="jdbc/einecs">
			select idchemical,idstructure,inchi,smiles,formula,format from chemicals join structure using(idchemical) join property_values as f using (idstructure) join properties using (idproperty) where f.idproperty=? and value=? limit ${page*pagesize},${pagesize}
			<sql:param value="${param.fieldname}"/>
			<sql:param value="${param.fieldvalue}"/>
		</sql:query>


			<table border="1">
				<tr>
					<td>#</td>
					<td>Download</td>
				<c:forTokens var="t" items="${tokens}" delims=",">

					<td>${t}</td>
					<c:if test="${'GENERATED_SMILES' eq t}">
						<td>Structure diagram</td>
				</c:if>
				</c:forTokens>
			</tr>
		<c:set var="count" value="${page*pagesize}"/>
		<c:forEach var="row" items="${rc.rows}">
				<c:set var="count" value="${count+1}"/>
			 	<tr bgcolor='#FFFFFF'>
			 		<td>${count}</td>
			 		<td>
						<c:url value="download.jsp" var="url">
							<c:param name="idstructure" value="${row.idstructure}"/>
						</c:url>
						<a href="
							<c:out value="${url}" escapeXml="true" />
						">${row.format}</a>

			 		</td>
					<c:forTokens var="t" items="${tokens}" delims=",">
						<sql:query var="rf" dataSource="jdbc/einecs">
							select value,name,idproperty from values_string as f join properties using (idproperty) where f.idstructure =? and name=?
							<sql:param value="${row.idstructure}"/>
							<sql:param value="${t}"/>
						</sql:query>
						<c:forEach var="rowf" items="${rf.rows}">

								<c:choose>
								<c:when test="${'GENERATED_SMILES' eq rowf.name}">
										<c:url value="image" var="image">
											<c:param name="smiles" value="${rowf.value}"/>
										</c:url>
									<td>
												<c:url value="" var="url">
													<c:param name="page" value="${item}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${rowf.idproperty}"/>
													<c:param name="fieldvalue" value="${rowf.value}"/>
												</c:url>
												<a href="
													<c:out value="${url}" escapeXml="true" />
												">${rowf.value}</a>
									</td>
									<td>
										<c:url value="display3d.jsp" var="display">
											<c:param name="idstructure" value="${row.idstructure}"/>
										</c:url>
										<a href="
											<c:out value="${display}" escapeXml="true" />
										" target="_blank" ><img src="${image}" alt="${rowf.value}" title="Click here for 3D image"/></a>
								</td>
								</c:when>
								<c:when test="${'Name' eq rowf.name}">
									<td>
												<c:url value="" var="url">
													<c:param name="page" value="${item}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${rowf.idproperty}"/>
													<c:param name="fieldvalue" value="${rowf.value}"/>
												</c:url>

												<a href="
													<c:out value="${url}" escapeXml="true" />
												">${rowf.value}</a>

										<c:url var="search" value="http://www.google.com/search">
											<c:param name="q" value="${rowf.value}"/>
										</c:url>
										<p>
										<a href="
											<c:out value="${search}" escapeXml="true" />
										" target="_blank">Search Google</a>


										</td>
								</c:when>
								<c:when test="${'CAS' eq rowf.name}">
									<td>
											<c:url value="" var="url">
													<c:param name="page" value="${item}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${rowf.idproperty}"/>
													<c:param name="fieldvalue" value="${rowf.value}"/>
												</c:url>

												<a href="
													<c:out value="${url}" escapeXml="true" />
												">${rowf.value}</a>

										<c:url var="search" value="http://www.google.com/search">
											<c:param name="q" value="${rowf.value}"/>
										</c:url>

										<p>
										<c:url var="search" value="http://www.google.com/search">
											<c:param name="q" value="${rowf.value}"/>
										</c:url>

										<a href="
											<c:out value="${search}" escapeXml="true" />
										" target="_blank">Search Google</a>
										</td>
								</c:when>
								<c:otherwise>
										<td>
												<c:url value="" var="url">
													<c:param name="page" value="${item}"/>
													<c:param name="pagesize" value="${param.pagesize}"/>
													<c:param name="fieldname" value="${rowf.idproperty}"/>
													<c:param name="fieldvalue" value="${rowf.value}"/>
												</c:url>
												<a href="
													<c:out value="${url}" escapeXml="true" />
												">${rowf.value}</a>
										</td>
								</c:otherwise>
							</c:choose>

					  </c:forEach>
					</c:forTokens>
			 </tr>
		</c:forEach>
	</table>
	</c:if>
</c:if>


