<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Author' data"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="profile"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

<c:set var="user" value="${sessionScope['username']}" />

<c:if test="${(!empty param.name) && sessionScope.ismanager}">
	<c:set var="user" value="${param.name}" />
</c:if>

<c:if test="${!empty param.name && !empty param.email}">
	<c:catch var="err">
		<sql:update var="rs" dataSource="jdbc/qmrf_documents">
				INSERT INTO catalog_authors VALUES (null,?,?,?,?,?)
				<sql:param value="${param.name}"/>	
				<sql:param value="${param.affiliation}"/>		
				<sql:param value="${param.address}"/>		
				<sql:param value="${param.webpage}"/>			
				<sql:param value="${param.email}"/>				
		</sql:update>	
		<blockquote>
		<div class="success">${param.name} added to the authors list.</div>
		</blockquote>
	</c:catch>
	<c:if test="${!empty err}">
		<div class="error">${err}</div>
	</c:if>
</c:if>

<c:catch var = "err">
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		SELECT id_author,name,affiliation,address,webpage,email FROM catalog_authors where email in (select email from users where user_name=?)
		<sql:param value="${user}"/>
	</sql:query>

	<c:choose>
	<c:when test="${rs.rowCount > 0}">	
			<blockquote>
		<div class="success">
		An author with the same e-mail address as for the user <b>${user}</b> is a member of QMRF documents and models authors list.
		<a href="help.jsp?anchor=verify_author" target="help" rev="help" ><img src="images/help.png" alt="help" title="What is QMRF and model authors list?" border="0"/></a>
		</div>
		</blockquote>
		<table width="95%" >
		<tr bgcolor="#C5CEE6">
		<th>Name</th>
		<th>Affiliation</th>
		<th>Contact</th>
		<th>WWW</th>
		<th>email</th>
		</tr>
		<c:forEach var="row" items="${rs.rows}">
			<tr bgcolor="#D6DFF7">
			<td>${row.name}</td>
			<td>${row.affiliation}</td>
			<td>${row.address}</td>
			<td>${row.webpage}</td>
			<td>${row.email}</td>
			</tr>
		</c:forEach>
		</table>
	</c:when>
	<c:otherwise>
		<blockquote>
		<div class="error">
		<b>${user}</b> is not a member of QMRF documents and models authors list.
		</div>
		</blockquote>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			SELECT concat(lastname,' ',substring(firstname,1,1),'.') as name,affiliation,address,webpage,email FROM users where user_name=?
			<sql:param value="${user}"/>
		</sql:query>
		<table>
		<form name="authordb">
			<c:forEach var="row" items="${rs.rows}">
				<tr bgcolor="#C5CEE6">
				<td>Name</td><td><input type="text" size="80" maxlength="255" name="name" value="${row.name}" /></td>
				</tr>
				<tr bgcolor="#C5CEE6">
				<td>Affiliation</td><td><input type="text" size="80" maxlength="128" name="affiliation" value="${row.affiliation}" /></td>
				</tr>
				<tr bgcolor="#C5CEE6">
				<td>Address</td><td>
				<input type="text" size="80" maxlength="128" name="address" value="${row.address}" /></td>
				</tr>
				<tr bgcolor="#C5CEE6">
				<td>WWW</td><td>
				<input type="text" size="80" maxlength="255" name="webpage" value="${row.webpage}" /></td>
				</tr>
				<tr bgcolor="#C5CEE6">
				<td>Email</td><td><input type="text" size="45" maxlength="45" name="email" readonly="true" value="${row.email}" /></td>
				</tr>
			</c:forEach>
			<tr>
			<td>
			</td>
			<td>
			<input type="submit" value="Add to the list of QMRF and model authors"/>
			</td>			
			</tr>
		</form>
		</table>	
	</c:otherwise>
	</c:choose>
</c:catch>
${err}
<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value="${isadmin}"/>
		</jsp:include>
	</p>
</div>
</body>
</html>