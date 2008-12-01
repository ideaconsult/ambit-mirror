<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<c:if test="${empty param.molType}">
	<c:set var="param.molType" value="smiles"/>
</c:if>
<c:if test="${!empty param.smarts}">
<c:catch var ="err">

		<c:set var="idquery" value=""/>

		<sql:query var="rscount" dataSource="jdbc/repdose">
			SELECT iddsname,count(idstructure) FROM dsname join datasets using(iddsname) where dsname.name=substring(?,1,32) group by iddsname limit 1
			<sql:param value="${param.smarts}"/>
		</sql:query>

		<c:forEach var="row" items="${rscount.rows}">
			<c:set var="idquery" value="${row.iddsname}"/>
		</c:forEach>


		<c:set var="ambituser" value="${pageContext.request.userPrincipal.name}"/>
		<c:if test="${empty ambituser}">
			<c:set var="ambituser" value="guest"/>
		</c:if>

		<c:if test="${empty idquery}">
			<c:catch var="transactionerr">
				<sql:transaction dataSource="jdbc/repdose">
					<sql:update var="rs">
						insert into dsname select null,substring(?,1,32),idambituser,now() from ambituser where mysqluser=? on duplicate key update updated=now()
						<sql:param value="${param.smarts}"/>
						<sql:param value="${ambituser}"/>
					</sql:update>
				<sql:query var="rs"  sql="SELECT LAST_INSERT_ID() AS idquery" />
				<c:forEach var="row" items="${rs.rows}">
					<c:set var="idquery" value="${row.idquery}"/>
				</c:forEach>
				<sql:query var="rsstruc" >
					select idstructure,uncompress(structure) as cml from structure
				</sql:query>
				<c:forEach var="rowstruc" items="${rsstruc.rows}">
						<c:catch var ="error">
								<a:smarts var="match" mol="${rowstruc.cml}" molType="cml" smarts="${param.smarts}" />
						</c:catch>
						<c:choose>
						<c:when test="${(empty error) && (!empty match) && (match gt 0)}"	>
							<sql:update var="rs">
								insert into datasets (iddsname,idstructure) values (?,?)
								<sql:param value="${idquery}"/>
								<sql:param value="${rowstruc.idstructure}"/>
							</sql:update>

						</c:when>
						<c:otherwise>
								<c:set var="smartserr" value="${error}"/>
						</c:otherwise>
						</c:choose>

						</c:forEach>
				</sql:transaction>
		</c:catch>

		</c:if>

		<c:if test="${!empty idquery}">
			<c:redirect url="query.jsp">
					<c:param name="idquery" value="${idquery}"/>
					<c:param name="title" value="${param.smarts}"/>
			</c:redirect>
		</c:if>
</c:catch>
</c:if>

<jsp:include page="header.jsp" flush="false"/>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">


<div style="text-align:left;">
<h3>Functional groups search</h3>
</div>

<form action=""  name="smartsForm" method="post"  border=1 bordercolor="#FF0000">
<table width="99%" border="0">

<tr>
	<th  width="20%" align="left">Predefined list of functional groups</th>
	<td width="60%">
		<c:catch var="error">
			<c:import var="xml" url="funcgroups.xml"/>
			<c:import var="xsl" url="/WEB-INF/xslt/funcgroups2form.xsl"/>
			<x:transform xml="${fn:trim(xml)}" xslt="${xsl}">
				<x:param name="selected" value="${param.smarts}"/>
			</x:transform>
		</c:catch>
	${error}
	</td>
</tr>
<tr>
	<td/>
	<td align="left">
	<input type="submit" value="Search"/>
	</td>

</tr>

</table>
</form>

<form action=""  name="smartsForm" method="post"  border=1 bordercolor="#FF0000">
<table width="99%" border="0">


<tr>
	<th width="20%" align="left">Specify functional group by SMARTS</th>
	<td width="60%">
		<input type="text" name="smarts" value="${param.smarts}" size="50"/>

	</td>

</tr>
<tr>
	<td/>
	<td align="left">
	<input type="submit" value="Search"/>
	</td>

</tr>

</table>

<blockquote>
	<c:if test="${!empty smartserr}">
	<div class="success">
	${smartserr}
	</div>
</blockquote>

</c:if>
</form>



<jsp:include page="footer.jsp" flush="false"/>