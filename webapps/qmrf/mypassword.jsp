<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>

	<c:choose>
	<c:when test="${empty sessionScope['username']}">
			<c:redirect url="protected.jsp"/>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'false'}">
		<c:if test="${(!empty param.user_name) and (sessionScope['username'] != param.user_name)}">
			<c:redirect url="protected.jsp"/>
		</c:if>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'true'}">
		<!-- OK, can change everything-->
	</c:when>
	<c:otherwise>
		<c:if test="${(!empty param.user_name) and (sessionScope['username'] != param.user_name)}">
			<c:redirect url="protected.jsp"/>
		</c:if>
	</c:otherwise>
</c:choose>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database: Change password"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="profile"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<c:set var="success" value=""/>

<c:if test="${!empty param.user_name}">


		<c:catch var="exception">
		<sql:setDataSource dataSource="jdbc/qmrf_documents"/>
			<sql:query var="rs">
				select firstname, lastname, user_name from users where user_name=?
				<sql:param value="${param.user_name}"/>
			</sql:query>
			<c:forEach var="row" items="${rs.rows}">
				<h3>Change password for user name: <font color="red">${row.user_name}</font></h3>
			</c:forEach>
		</c:catch>
		${exception}
<c:choose>
<c:when test="${fn:length(param.newpassword)<6}">
				<div class="error">
				The password should consist of at least 6 characters
			</div>
</c:when>


		
<c:when test="${!empty param.password && !empty param.newpassword && !empty param.confirm && (param.newpassword eq param.confirm)}">


		<sql:setDataSource dataSource="jdbc/tomcat_users"/>

		<c:catch var='transactionException2'>
			<sql:transaction>
				<sql:query var="keepalive">
					select user_name from users where user_name=?
					<sql:param value="${param.user_name}"/>
				</sql:query>
				<c:if test="${keepalive.rowCount eq 0}">
					<div class="error">
						Change password error - ${param.user_name} does not exist.
						<p>
					</div>
				</c:if>

				<sql:update var="updateCount" >
					update users set user_pass=md5(?) where user_name=? and user_pass=md5(?)
					<sql:param value="${param.newpassword}"/>
					<sql:param value="${param.user_name}"/>
					<sql:param value="${param.password}"/>
				</sql:update>

			</sql:transaction>
		</c:catch>

		<c:choose>
		<c:when test='${not empty transactionException2}'>
						<div class="error">
									error ${transactionException2}
						</div>
		</c:when>
		<c:when test="${updateCount eq 0}">
			<div class="error">
			The old password doesn't match.
			</div>
		</c:when>
		<c:otherwise>
			<div class="success">
				<c:set var="success" value="true"/>
				Password for user <b>${param.user_name}</b> changed successfully.<br/> Back to <a href="myprofile.jsp">My profile</a>

			</div>

		</c:otherwise>
		</c:choose>

</c:when>
<c:otherwise>
		<div class="error">
				Invalid passwords or passwords doesn't match.
		</div>
</c:otherwise>
</c:choose>

<c:if test="${empty success}">

	<form method="POST" name="password_form" action="mypassword.jsp">

	<table>
	<tr>
	<th>
		Old Password
	</th>
	<td>
		<input type="password" size="16" name="password"/>
	</td>
	</tr>

	<tr>
	<th>
		New Password
	</th>
	<td>
		<input type="password" size="16" name="newpassword"/>
	</td>
	</tr>

	<tr>
	<th>
		Confirm new password
	</th>
	<td>
		<input type="password" size="16" name="confirm"/>
	</td>
	</tr>

	<tr>
	<th>

	</th>
	<td>
		<input type="hidden" name="user_name" value="${param.user_name}"/>

		<input type="submit" name="Submit"/>
	</td>
	</tr>
	</table>
</form>
</c:if>
</c:if>
<!-- user_name -->





<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value="${isadmin}"/>
		</jsp:include>
	</p>
</div>
</body>
</html>
