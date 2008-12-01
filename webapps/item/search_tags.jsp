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
					<c:set var="search_similarity" value="exact" scope="session"/>
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

					<c:choose>
						<c:when test="${!empty error}">
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
<c:set var="title" value=""/>

<c:choose>

<c:when test="${sessionScope.search_similarity eq 'fingerprint'}">
	<c:catch var="error">
	<a:similarity var="sql" params="p" prescreen="false" threshold="${param.threshold}" mol="${param.mol}" smiles="${sessionScope.search_smiles}" page="0" pagesize="1000"/>
	</c:catch>
	<c:set var="title" value="Similar structures (Tanimoto distance > ${param.threshold})"/>
</c:when>
<c:when test="${sessionScope.search_similarity eq 'substructure'}">
	<c:catch var="error">
	<a:similarity var="sql" params="p" prescreen="true" threshold="${param.threshold}" mol="${param.mol}" smiles="${sessionScope.search_smiles}"  page="0" pagesize="1000"/>
	</c:catch>
	<c:choose>
		<c:when test="${!empty sessionScope.search_smiles}">
				<c:set var="query" value="${sessionScope.search_smiles}"/>
				<c:set var="queryType" value="smiles"/>
		</c:when>
		<c:otherwise>
				<c:set var="query" value="${param.mol}"/>
				<c:set var="queryType" value="mol"/>
		</c:otherwise>
		</c:choose>
	<c:set var="title" value="Substructure"/>
</c:when>
<c:otherwise>
	<c:catch var="error">
	<a:exactsearch var="sql" params="p"  cas="${param.cas}" name="${param.name}" formula="${param.formula}" mol="${param.mol}" smiles="${sessionScope.search_smiles}" />
	</c:catch>
	<c:set var="title" value="Exact search "/>
</c:otherwise>
</c:choose>

		<c:if test="${!empty param.cas}">
			<c:set var="title" value="${title}CAS No=${param.cas}&nbsp;"/>
		</c:if>
		<c:if test="${!empty param.name}">
			<c:set var="title" value="${title}Chemical name =${param.name}&nbsp"/>
		</c:if>
		<c:if test="${!empty param.formula}">
			<c:set var="title" value="${title}Formula=${param.formula}&nbsp;"/>
		</c:if>

<c:choose>
<c:when test="${empty sql}">
		<c:redirect url="search_substances.jsp">
				<c:param name="error" value="Search criteria not specified! ${error}"/>
		</c:redirect>
</c:when>
<c:when test="${!empty error}">
		<c:redirect url="search_substances.jsp">
				<c:param name="error" value="${error}"/>
		</c:redirect>
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
		<c:redirect url="search_substances.jsp">
			<c:param name="error" value="${error}"/>
		</c:redirect>
</c:if>

<c:choose>
<c:when test="${rs.rowCount eq 0}">
		<c:redirect url="search_substances.jsp">
				<c:param name="error" value="No records found. ${sql}"/>
		</c:redirect>
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
			<sql:param value="${sessionScope.search_similarity}"/>
			<sql:param value="${ambituser}"/>
		</sql:update>
		<sql:query var="dsname" sql="SELECT LAST_INSERT_ID() AS idquery" />
		<c:forEach var="row" items="${dsname.rows}">
			<c:set var="idquery" value="${row.idquery}"/>
		</c:forEach>


		<c:forEach var="row" items="${rs.rows}">
			<c:set var="error" value=""/>
			<c:set var="match" value="1"/>
			<c:if test="${sessionScope.search_similarity eq 'substructure'}">
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
		<c:redirect url="search_substances.jsp">
				<c:param name="error" value="${error}"/>
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
