 <%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/taglibs-mailer" prefix="mt" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/random-1.0" prefix="rand" %>

<c:if test="${!empty param.email}" >
<c:if test="${!fn:contains(param.email, '@') }" >
	<jsp:forward page="forgotten.jsp">
			<jsp:param name="registerstatus" value="Invalid e-mail"/>
			<jsp:param name="email" value=""/>
  </jsp:forward>
</c:if>
</c:if>


<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Inventory: Forgotten password page"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="login"/>
	<jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<h3>Forgotten password</h3>
<c:if test="${!empty param.registerstatus}" >
			<div class="error">${param.registerstatus}</div>
</c:if>


<c:choose>


<c:when test="${!empty param.email}">
	<c:set var="sql" value="SELECT user_name,title,firstname,lastname,email FROM users where email=trim(?) and user_name=trim(?)"/>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				${sql}
				<sql:param value="${param.email}"/>
				<sql:param value="${param.username}"/>
		</sql:query>
		<c:catch var ="error">
		<c:if test="${rs.rowCount eq 0}">
			<p>
			No accounts in QMRF Inventory with the user name <i>${param.username}</i> and e-mail address <i>${param.email}</i>.
			</p>
		</c:if>
		<c:forEach var="row" items="${rs.rows}">
				<rand:string id="rnd" charset="a-zA-Z0-9! @ # $" length='15'/>
				<c:set var="newpass">
					<jsp:getProperty name="rnd" property="random" />
				</c:set>
				<sql:update var="rs1" dataSource="jdbc/tomcat_users">
					update users set user_pass=md5(?) where user_name=?
					<sql:param value="${newpass}"/>
					<sql:param value="${row.user_name}"/>
				</sql:update>

					<c:set var="space" value=" "/>
						<mt:mail server="${initParam['mail-server']}" >
								<mt:from>${initParam['mail-from']}</mt:from>
								<mt:setrecipient type="to">${row.email}</mt:setrecipient>
								<mt:subject>QMRF Inventory forgotten password</mt:subject>

						    <mt:message>
Dear ${row.title}${space}${row.firstname}${space}${row.lastname},

We would like to inform you that the password for the ${row.user_name} user has been reset upon your request.

Your new login credentials are as follows:
Username: ${row.user_name}
Password: ${newpass}
You could change your password through the "My Profile" tab.

The QMRF Inventory team
<c:set var="u">${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}</c:set>
<c:url value="${u}"></c:url>
						    </mt:message>
						    <mt:send>
						    	<mt:error id="err">
						         <jsp:getProperty name="err" property="error"/>

						       </mt:error>
							</mt:send>
						</mt:mail>


						<p>
						An email with your new login credentials has been sent to <i>${row.email}</i>
						</p>
			</c:forEach>
			<p>

		</p>
	</c:catch>
	<c:if test="${!empty error}">
			<div class="error">${error}</div>
	</c:if>
</c:when>
<c:otherwise>
		<form method="POST" action="forgotten.jsp" >
  <table border="0" cellspacing="5">
  	<tr>
      <th align="right">User name:</th>
      <td align="left"><input type="text" name="username"></td>
  	</tr>
    <tr>

      <th align="right">Email:</th>
      <td align="left"><input type="text" name="email"></td>
    </tr>
    <tr>
    	<td/>
      <td align="right"><input type="submit" value="Reset password"></td>
    </tr>
  </table>
</form>
</c:otherwise>
</c:choose>
</body>
</html>
