<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>


<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>


<c:set var="thispage" value='publish.jsp'/>

<c:if test="${!empty param.viewmode}">
	<c:set var="viewmode" value="${param.viewmode}" scope="session"/>
</c:if>
<c:if test="${sessionScope['viewmode'] ne 'qmrf_editor'}" >
  <c:redirect url="index.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['iseditor']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['iseditor'] eq 'false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<c:if test="${empty param.id}" >
  <c:redirect url="/editor.jsp"/>
</c:if>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Publish a document"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="admin"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:set var="report">
	select idqmrf,qmrf_number,qmrf_title,user_name,date_format(updated,'${sessionScope.dateformat}') as lastdate,status from documents where idqmrf = ${param.id}
</c:set>

<jsp:include page="records.jsp" flush="true">
    <jsp:param name="sql" value="${report}"/>

		<jsp:param name="qmrf_number" value="QMRF#"/>
		<jsp:param name="qmrf_title" value="Title"/>		
    <jsp:param name="user_name" value="Author"/>
    <jsp:param name="lastdate" value="Last updated"/>
		<jsp:param name="status" value="Status"/>
		<jsp:param name="actions" value="editor"/>

</jsp:include>

<div class="success">
	<c:choose>
	<c:when test="${empty param.catalog}">
	</c:when>
	<c:when test="${param.catalog eq 'endpoints'}">
		<c:if test="${!empty param.endpoint_name}">

			<c:set value="${fn:split(param.endpoint_name,'.')}" var="endpoint_name"/>
			<c:if test="${fn:length(endpoint_name)>0}">
              			<c:set var="endpoint_name" value="${endpoint_name[fn:length(endpoint_name)-1]}" />

              			<c:catch var="transactionException_archive">
				<sql:transaction dataSource="jdbc/qmrf_documents">
					<sql:update>
						SET NAMES 'utf8'
					</sql:update>
					<sql:update var="updateCount">
						delete from doc_endpoint where idqmrf=?
						<sql:param value="${param.id}"/>
					</sql:update>
					<sql:update var="updateCount">
			insert into doc_endpoint (idqmrf,id_endpoint) values (?,(SELECT id_endpoint FROM catalog_endpoints where name=?))
						<sql:param value="${param.id}"/>
					  	<sql:param value="${endpoint_name}"/>
					</sql:update>
				</sql:transaction>
				</c:catch>
				<c:choose>
				<c:when test="${!empty transactionException_archive}">
					${transactionException_archive}
				</c:when>
	  			<c:otherwise>
	  				"${param.endpoint_name}" found in endpoints catalog
	  			</c:otherwise>
	  			</c:choose>


              		</c:if>
		</c:if>
	</c:when>
	<c:when test="${param.catalog eq 'authors'}">
		<c:if test="${!empty param.author_name}">

          		<c:catch var="transactionException_archive">
          			<c:if test="${empty param.author_address}">
          				<c:set var="param.author_address" value="N/A"/>
          			</c:if>
          			<c:if test="${empty param.author_www}">
          				<c:set var="param.author_www" value="N/A"/>
          			</c:if>
          			<c:if test="${empty param.author_affiliation}">
          				<c:set var="param.author_affiliation" value="N/A"/>
          			</c:if>
				<sql:transaction dataSource="jdbc/qmrf_documents">
						<sql:update>
							SET NAMES 'utf8'
						</sql:update>
					<sql:update var="updateCount">
						delete from doc_authors where idqmrf=?
						<sql:param value="${param.id}"/>
					</sql:update>
					<sql:update var="updateCount">
						insert into catalog_authors (name,affiliation,address,webpage,email) values (?,?,?,?,?) ON DUPLICATE KEY UPDATE webpage=?,email=?
						<sql:param value="${param.author_name}"/>
						<sql:param value="${param.author_affiliation}"/>
						<sql:param value="${param.author_address}"/>
						<sql:param value="${param.author_www}"/>
						<sql:param value="${param.author_email}"/>
						<sql:param value="${param.author_www}"/>
						<sql:param value="${param.author_email}"/>
					</sql:update>
				</sql:transaction>
				<sql:transaction dataSource="jdbc/qmrf_documents">
					<sql:update var="updateCount">
			insert into doc_authors (idqmrf,id_author) values (?,(SELECT id_author FROM catalog_authors where name=? and email=?))
						<sql:param value="${param.id}"/>
					  	<sql:param value="${param.author_name}"/>
					  	<sql:param value="${param.author_email}"/>
					</sql:update>
				</sql:transaction>
			</c:catch>
			<c:choose>
				<c:when test="${!empty transactionException_archive}">
					${param.author_name}<br/>
					${param.author_email}<br/>
					${transactionException_archive}
				</c:when>
	  			<c:otherwise>
	  				"${param.author_name} , ${param.author_email}" found in authors catalog
	  			</c:otherwise>
	  		</c:choose>


		</c:if>
	</c:when>
	<c:when test="${param.catalog eq 'software'}">
		<c:if test="${!empty param.software_name}">

          		<c:catch var="transactionException_archive">
          			<c:if test="${empty param.software_name}">
          				<c:set var="param.software_name" value="N/A"/>
          			</c:if>
          			<c:if test="${empty param.software_url}">
          				<c:set var="param.software_url" value="N/A"/>
          			</c:if>
          			<c:if test="${empty param.software_decription}">
          				<c:set var="param.software_decription" value="N/A"/>
          			</c:if>
          			<c:if test="${empty param.software_contact}">
          				<c:set var="param.software_contact" value="N/A"/>
          			</c:if>
				<sql:transaction dataSource="jdbc/qmrf_documents">
					<sql:update>
						SET NAMES 'utf8'
					</sql:update>
					<sql:update var="updateCount">
						delete from doc_software where idqmrf=?
						<sql:param value="${param.id}"/>
					</sql:update>
					<sql:update var="updateCount">
						insert into catalog_software (name,description,contact,url) values (?,?,?,?) ON DUPLICATE KEY UPDATE url=?
						<sql:param value="${param.software_name}"/>
						<sql:param value="${param.software_description}"/>
						<sql:param value="${param.software_contact}"/>
						<sql:param value="${param.software_url}"/>
						<sql:param value="${param.software_url}"/>
					</sql:update>
				</sql:transaction>
				<sql:transaction dataSource="jdbc/qmrf_documents">
					<sql:update var="updateCount">
			insert into doc_software (idqmrf,id_software) values (?,(SELECT id_software FROM catalog_software where name=?))
						<sql:param value="${param.id}"/>
					  	<sql:param value="${param.software_name}"/>
					</sql:update>
				</sql:transaction>
			</c:catch>
			<c:choose>
				<c:when test="${!empty transactionException_archive}">
					${transactionException_archive}
				</c:when>
	  			<c:otherwise>
	  				"${param.software_name}" found in software catalog
	  			</c:otherwise>
	  		</c:choose>


		</c:if>
	</c:when>
	<c:when test="${param.catalog eq 'algorithms'}">

		<c:if test="${!empty param.alg_definition}">

          		<c:catch var="transactionException_archive">
          			<c:if test="${empty param.alg_definition}">
          				<c:set var="param.alg_definition" value="N/A"/>
          			</c:if>
          			<c:if test="${empty param.alg_decription}">
          				<c:set var="param.alg_decription" value="N/A"/>
          			</c:if>
				<sql:transaction dataSource="jdbc/qmrf_documents">
					<sql:update>
						SET NAMES 'utf8'
					</sql:update>
					<sql:update var="updateCount">
						delete from doc_algorithms where idqmrf=?
						<sql:param value="${param.id}"/>
					</sql:update>
					<sql:update var="updateCount">
						insert into catalog_algorithms (definition,description) values (?,?) ON DUPLICATE KEY UPDATE description=?
						<sql:param value="${param.alg_definition}"/>
						<sql:param value="${param.alg_description}"/>
						<sql:param value="${param.alg_description}"/>
					</sql:update>
				</sql:transaction>
				<sql:transaction dataSource="jdbc/qmrf_documents">
					<sql:update var="updateCount">
			insert into doc_algorithms (idqmrf,id_algorithm) values (?,(SELECT id_algorithm FROM catalog_algorithms where definition=?))
						<sql:param value="${param.id}"/>
					  	<sql:param value="${param.alg_definition}"/>
					</sql:update>
				</sql:transaction>
			</c:catch>
			<c:choose>
				<c:when test="${!empty transactionException_archive}">
					${transactionException_archive}
				</c:when>
	  			<c:otherwise>
	  				"${param.alg.definition}" found in algorithms catalog
	  			</c:otherwise>
	  		</c:choose>


		</c:if>
	</c:when>
	</c:choose>

</div>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select idqmrf,qmrf_number,xml from documents where idqmrf=?
	<sql:param value="${param.id}"/>
</sql:query>



<form method="POST" name="publish_form" action='<%= response.encodeURL("admin_publish.jsp") %>'>

	<c:forEach var="row" items="${rs.rows}">

<table width="95%" bgcolor="#FFFFFF"  cellspacing="4" border="0">
<tr bgcolor="D6DFF7" align="center">
<td align="left" bgcolor="#FFFFFF" colspan="8">
<h3>10.1	QMRF number</h3>
</td>
<td align="left" width="25%" rowspan="3" bgcolor="#FFFFFF" valign="top">
	<div class="success">
	Assign QMRF number
	</div>
	<br/>
	<div>
	<input type="submit" value="Publish" />
	</div>
	<br/>
	<div class="help">
	In order to be published, each QMRF document have to be assigned a number of the form <b>Q [Field 4.2]-[Field 2.5]-[Field 1.3]-Version</b>.
		</div>

</td>
</tr>
<tr bgcolor="D6DFF7" align="center">

<td bgcolor="#FFFFFF">Q</td>
<td bgcolor="#D6DFF7">
	<x:parse xml="${row.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
		<c:set var="aChapter">
		<x:out  select="$doc//QMRF/QMRF_chapters/QSAR_Algorithm/algorithm_explicit/@chapter"/>
		</c:set>
		<c:set var="aName">
		<x:out  select="$doc//QMRF/QMRF_chapters/QSAR_Algorithm/algorithm_explicit/@name"/>
		</c:set>
		<a href="
		<c:url value="#algorithm_explicit"/>
			"><img src="images/chart_curve.png" border="0" alt="Algorithm picture" title="Click here to go to the algorithm form and retrieve algorithm number"/></a>
		${aChapter}<xsl:text>.</xsl:text>${aName}
	<x:forEach select="$doc//QMRF/QMRF_chapters/QSAR_Algorithm/algorithm_explicit/algorithm_ref">
		<x:out select="id(@idref)/@name"/>
	</x:forEach>

</td>
<td bgcolor="#FFFFFF">-</td>
<td bgcolor="#D6DFF7" >
			<a href="#model_authors"/><img src="images/user_suit.png" border="0" alt="user icon" title="Click here to go to the model developer' form and retrieve the model developer number"/></a>
		<x:out select="$doc//QMRF/QMRF_chapters/QSAR_General_information/model_authors/@chapter"/>
		<xsl:text>.</xsl:text>
		<x:out select="$doc//QMRF/QMRF_chapters/QSAR_General_information/model_authors/@name"/>


</td>
<td bgcolor="#FFFFFF">-</td>
<td bgcolor="#D6DFF7">

		<a href="
		<c:url value="#software_form">
		</c:url>
		"><img src="images/application_form.png"  border="0" alt="software icon" title="Click here to go to the model software form and retrieve software number"/></a>
		<x:out select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_software/@chapter"/>
		<xsl:text>.</xsl:text>
		<x:out select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_software/@name"/>
</td>
<td bgcolor="#FFFFFF">-</td>
<td bgcolor="#D6DFF7">
		Version
</td>

</tr>


<tr align="center">

<td colspan="9" align="center" valign="top">
Q



		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,id_algorithm from doc_algorithms where idqmrf=? and chapter='4.2'
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<input type="text" name="A" value="${row.id_algorithm}" size="5" ALT="A"/>-
		</c:forEach>
		<c:if test="${rs.rowCount eq 0}">
				<a href="
		<c:url value="#algorithms_form"/>
			"><img src="images/warning.png" alt="warning sign" title="Algorithm not defined! User Retrieve Algorithm Number." border="0"/></a>
			<input type="text" size="5" name="A"/>-
		</c:if>



		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,id_author from doc_authors where idqmrf=? and chapter='2.5'
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<input type="text" name="B" value="${row.id_author}" size="5"/>-
		</c:forEach>
		<c:if test="${rs.rowCount eq 0}">
		<a href="
		<c:url value="#model_authors"/>
			"><img src="images/warning.png" alt="warning sign" title="Model developer not defined! Use Retrieve Author Number." border="0"/></a>
			<input type="text" size="5" name="B"/>-
		</c:if>



		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,id_software from doc_software where idqmrf=? and chapter='1.3'
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<input type="text" name="D" size="5" value="${row.id_software}"/>-
		</c:forEach>
		<c:if test="${rs.rowCount eq 0}">
				<a href="
		<c:url value="#software_form"/>
			"><img src="images/warning.png" alt="warning sign" title="Software not defined! Use Retrieve Software Number." border="0"/></a>
			<input type="text" size="5" name="D"/>-
		</c:if>


		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,version from documents where idqmrf=?
			<sql:param value="${param.id}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
			<input type="text" name="C" size="5" value="${row.idqmrf}" readonly="true"/>
		</c:forEach>
		<c:if test="${rs.rowCount eq 0}">
			<img src="images/warning.png" alt="warning sign" title="not defined!"/><input type="text" size="5" name="C" value="1"/>-
		</c:if>


</td>
</tr>
</table>

	<input type="hidden" name="status" value="${param.status}">
	<input type="hidden" name="id" value="${param.id}">
</form>

<table border="0" width="95%">
<tr align="left" bgcolor="#FFFFFF">
	<td bgcolor="#FFFFFF">
		<c:import var="xsl" url="/WEB-INF/xslt/qmrfnumber.xsl"/>
		<x:transform xml="${doc}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>


	</td>
	<td valign="top" width="25%">

	<br/>
	<div class="help">
	Click "Retrieve number from catalog" to obtain corresponding numbers. If one of the fields is missing, return the document to the author to insert the missing data.
	</div>
	</td>
	</tr>

</c:forEach>

</table>
</div>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
 </body>
</html>