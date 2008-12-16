<%@ page contentType="text/plain" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<html>
<title>EINECS database</title>
<script src="jmol/Jmol.js"></script><script> jmolInitialize("jmol");
</script>
<body>

	<c:if test="${!empty param.idstructure}">

		<sql:query var="rs" dataSource="jdbc/einecs">
			select value from structure_fields join field_names using(idfieldname) where idstructure=? and name="GENERATED_SMILES"
			<sql:param value="${param.idstructure}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
				SMILES: ${row.value}
				<br>
										<c:url value="image" var="image">
											<c:param name="smiles" value="${row.value}"/>
										</c:url>

									<img src="${image}" alt="${row.value}" title="2D struicture diagram" border="1"/>
		</c:forEach>

		<sql:query var="rs" dataSource="jdbc/einecs">
			select uncompress(structure) as s from structure where idstructure=?
			<sql:param value="${param.idstructure}"/>
		</sql:query>
		<c:forEach var="row" items="${rs.rows}">
				<script>
						app = jmolApplet(200);
				</script>
				<br>
				<textarea rows=20 cols=50 name=tmol readonly=true>${row.s}</textarea>
				<script>
				var mol = tmol.value;
				jmolLoadInline(mol);
				</script>
		</c:forEach>



	</c:if>



</body>
</html>