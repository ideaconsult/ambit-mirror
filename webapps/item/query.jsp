<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="header.jsp" flush="true"/>

<div style="text-align:left;">

</div>

<c:if test="${!empty param.idquery}">
	<form action="/repdose/index.php" method="post">
	<c:catch var="error">
		<sql:query var="rs" dataSource="jdbc/repdose">
			select structure.idstructure,smiles,casno from substance join structure using(idsubstance) join datasets using(idstructure) right join cas using(idstructure) where iddsname=?
			<sql:param value="${param.idquery}"/>			
		</sql:query>
		
		<table bgcolor="#DDDDDD">
			<tr bgcolor="#FFFFFF">
				<th>ID</th>
				<th>CAS No</th>
				<th>Name</th>
				<th>SMILES</th>
				<th>Structure diagram</th>
				<th>Please choose items to be retrieved from Repdose</th>
			</tr>
		<c:set var="count" value="0"/>
		<c:forEach var="row" items="${rs.rows}">
				<c:set var="count" value="${count+1}"/>

				<c:set var="name" value=""/>
				<c:set var="bcf" value=""/>

				<tr bgcolor="#FFFFFF">
					<td>${count}</td>
					<td>${row.casno}</td>
					<td>${row.name}</td>
					<td>${row.smiles}</td>
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
						<input type="checkbox" name="cas[]" value="${row.casno}">
					</td>
				</tr>
		</c:forEach>
	</table>
	</c:catch>
	<input type='submit' value=' Take selected results to Repdose '>
	</form>
	<c:if test="${!empty error}">
		<font color="#FF0000">
		${error}
		</font>
	</c:if>
</c:if>

<jsp:include page="footer.jsp" flush="true"/>
