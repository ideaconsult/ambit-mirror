<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>


<jsp:include page="query_settings.jsp" flush="false"/>

<!-- initializing session variables with param.x values -->

<c:if test="${param.threshold != null}">
	<c:set var="search_threshold" value="${param.threshold}" scope="session"/>
</c:if>

<c:set var="search_mode" value="exact"/>
	

<!-- CAS Registry number,Formula,Chemical name,Alias -->
<c:if test="${param.search_criteria eq 'CAS Registry number'}">
	<c:set var="search_cas" value="${param.search_value}"/>
	<c:set var="search_mode" value="exact"/>
</c:if>
<c:if test="${param.search_criteria eq 'Formula'}">
	<c:set var="search_formula" value="${param.search_value}"/>
	<c:set var="search_mode" value="exact"/>	
</c:if>
<c:if test="${param.search_criteria eq 'Chemical name'}">
	<c:set var="search_name" value="${param.search_value}"/>
	<c:set var="search_mode" value="exact"/>	
</c:if>
<c:if test="${param.search_criteria eq 'Alias'}">
	<c:set var="search_identifier" value="${param.search_value}"/>
	<c:set var="search_mode" value="exact"/>	
</c:if>
<c:if test="${param.smiles != null}">
	<c:set var="search_smiles" value="${param.smiles}"/>
	<c:set var="search_mode" value="${param.search_mode}"/>	
</c:if>
<c:if test="${param.mol != null}">
	<c:set var="search_mol" value="${param.mol}"/>
	<c:set var="search_mode" value="${param.search_mode}"/>	
</c:if>

<c:set var="search_criteria" value="${param.search_criteria}" scope="session"/>
<c:set var="search_value" value="${param.search_value}" scope="session"/>
<c:set var="smiles" value="${param.smiles}" scope="session"/>
<c:set var="source" value="${param.source}" scope="session"/>
<c:set var="threshold" value="${param.threshold}" scope="session"/>
<c:set var="mol" value="${param.mol}" scope="session"/>


<c:choose>

<c:when test="${search_mode eq 'fingerprint'}">
	<c:catch var="error">
	<a:similarity var="sql" params="p" prescreen="false" threshold="${param.threshold}" 
			mol="${search_mol}" 
			smiles="${search_smiles}" 
			page="0" pagesize="1000"/>
	</c:catch>
	<c:set var="title" value="Similar structures (Tanimoto distance > ${param.threshold})"/>
</c:when>
<c:when test="${search_mode eq 'substructure'}">
	<c:catch var="error">
	<a:similarity var="sql" params="p" prescreen="true" threshold="${param.threshold}" 
			mol="${param.mol}" 
			smiles="${search_smiles}"  
			page="0" pagesize="1000"/>
	</c:catch>
	<c:choose>
		<c:when test="${!empty search_smiles}">
				<c:set var="query" value="${search_smiles}"/>
				<c:set var="queryType" value="smiles"/>
		</c:when>
		<c:otherwise>
				<c:set var="query" value="${search_mol}"/>
				<c:set var="queryType" value="mol"/>
		</c:otherwise>
		</c:choose>
	<c:set var="title" value="Substructure"/>
</c:when>
<c:when test="${search_mode eq 'structure'}">
	<c:catch var="error">
	<a:exactsearch var="sql" params="p"  
		cas="" 
		name="" 
		formula="" 
		mol="${search_mol}" 
		smiles="${search_smiles}" />
	</c:catch>
	<c:set var="title" value="${search_criteria}"/>
</c:when>	
<c:when test="${search_mode eq 'exact'}">
	<c:catch var="error">
	<a:exactsearch var="sql" params="p"  
		cas="${search_cas}" 
		name="${search_name}" 
		formula="${search_formula}" 
		mol="${search_mol}" 
		smiles="${search_smiles}" />
	</c:catch>
	<c:set var="title" value="${search_criteria}"/>
</c:when>	
<c:otherwise>	
		<c:set var="error" value="${search_mode}" scope="session"/>
		<c:redirect url="search.jsp"/>
</c:otherwise>
</c:choose>

<c:choose>
<c:when test="${empty sql}">
		<c:set var="error" value="Search criteria not specified" scope="session"/>
		<c:redirect url="search.jsp"/>
</c:when>
<c:when test="${!empty error}">
		<c:set var="error" value="${error}" scope="session"/>
		<c:redirect url="search.jsp"/>
</c:when>
<c:otherwise>
<c:catch var="error">
		<sql:query var="rs" dataSource="jdbc/repdose">
				${sql}
				<c:forEach var="r" items="${p}">
						<sql:param value="${r}"/>
				</c:forEach>
		</sql:query>

</c:catch>

<c:if test="${!empty error}">
		<c:set var="error" value="${error}" scope="session"/>
		<c:redirect url="search.jsp">
		</c:redirect>
</c:if>

<c:choose>
<c:when test="${rs.rowCount eq 0}">
		<c:set var="error" value="No records found. ${search_criteria} ${title}" scope="session"/>
		<c:redirect url="search.jsp"/>
</c:when>
<c:when test="${empty error}">

<c:set var="counter" value="0"/>

<c:catch var ="err">
	<c:set var="ambituser" value="${pageContext.request.userPrincipal.name}"/>
	<c:if test="${empty ambituser}">
		<c:set var="ambituser" value="guest"/>
	</c:if>

	<sql:transaction dataSource="jdbc/repdose">
		<sql:update var="dsname">
			insert into dsname select null,substring(?,1,32),idambituser,now() from ambituser where mysqluser=? on duplicate key update updated=now()
			<sql:param value="${search_mode}"/>
			<sql:param value="${ambituser}"/>
		</sql:update>
		<sql:query var="dsname" sql="SELECT LAST_INSERT_ID() AS idquery" />
		<c:forEach var="row" items="${dsname.rows}">
			<c:set var="idquery" value="${row.idquery}"/>
		</c:forEach>


		<c:forEach var="row" items="${rs.rows}">
			<c:set var="error" value=""/>
			<c:set var="match" value="1"/>
			<c:if test="${search_mode eq 'substructure'}">
						<c:catch var ="error">
								<a:isomorphism var="match" subgraph="true" mol="${row.smiles}" molType="smiles" query="${query}" queryType="${queryType}" />
						</c:catch>
			</c:if>
			<c:choose>
			<c:when test="${(empty error) && (!empty match) && (match gt 0)}">
				<sql:update var="datasets" >
					insert ignore into datasets (iddsname,idstructure) select ?,idstructure from structure where idsubstance=?
					<sql:param value="${idquery}"/>
					<sql:param value="${row.idsubstance}"/>
				</sql:update>

			</c:when>
			<c:otherwise>
			</c:otherwise>
			</c:choose>
		</c:forEach>
	</sql:transaction>

</c:catch>
</c:when>
<c:otherwise>
		<c:set var="error" value="${error}"/>
		<c:redirect url="search.jsp">
		</c:redirect>
</c:otherwise>
</c:choose>
<!-- empty error-->


</c:otherwise>
</c:choose>

		<c:if test="${!empty idquery}">
			<c:redirect url="query.jsp">
				<c:param name="idquery" value="${idquery}"/>
				<c:param name="title" value="${title}"/>
			</c:redirect>

		</c:if>
  </body>
</html>


<jsp:include page="footer.jsp" flush="true"/>
