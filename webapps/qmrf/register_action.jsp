<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 <%@ taglib uri="http://jakarta.apache.org/taglibs/taglibs-mailer" prefix="mt" %>

<c:set var="mailserver" value="${initParam['mail-server']}" />
<c:if test="${empty mailserver}">
		<div class="error">Mail server not configured. Please check web.xml</div>
		<c:set var="mailserver" value="localhost" />
</c:if>
<c:set var="bccadmin" value="nina@acad.bg" />
<c:set var="mailfrom" value="${initParam['mail-from']}"  />
<c:if test="${empty mailserver}">
		<div class="error">mail-from parameter not configured. Please check web.xml</div>
		<c:set var="mailfrom" value="qmrf@acad.bg" />
</c:if>

<c:if test="${empty param.username}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="User name empty"/>
			<jsp:param name="email" value="${param.email}"/>
			<jsp:param name="firstname" value="${param.firstname}"/>
			<jsp:param name="lastname" value="${param.lastname}"/>
  </jsp:forward>
</c:if>

<c:if test="${empty param.email}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="e-mail empty"/>
		<jsp:param name="username" value="${param.username}"/>
			<jsp:param name="firstname" value="${param.firstname}"/>
			<jsp:param name="lastname" value="${param.lastname}"/>
  </jsp:forward>
</c:if>

<c:if test="${!fn:contains(param.email, '@') }" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="Invalid e-mail"/>
		<jsp:param name="username" value="${param.username}"/>
			<jsp:param name="firstname" value="${param.firstname}"/>
			<jsp:param name="lastname" value="${param.lastname}"/>
  </jsp:forward>
</c:if>

<c:if test="${empty param.firstname}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="First name empty"/>
		<jsp:param name="username" value="${param.username}"/>
		<jsp:param name="email" value="${param.email}"/>
		<jsp:param name="lastname" value="${param.lastname}"/>
  </jsp:forward>
</c:if>

<c:if test="${empty param.lastname}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="Last name empty"/>
		<jsp:param name="username" value="${param.username}"/>
		<jsp:param name="firstname" value="${param.firstname}"/>
		<jsp:param name="email" value="${param.email}"/>
  </jsp:forward>
</c:if>

<c:if test="${(empty param.password1) or (empty param.password2)}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="Password empty"/>
		<jsp:param name="username" value="${param.username}"/>
		<jsp:param name="email" value="${param.email}"/>
  </jsp:forward>
</c:if>

<c:if test="${!(param.password1 eq param.password2)}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="registerstatus" value="Passwords don't match!"/>
		<jsp:param name="username" value="${param.username}"/>
		<jsp:param name="email" value="${param.email}"/>

  </jsp:forward>
</c:if>



<c:if test="${empty param.title}" >
		<c:set var="param.title" value = ""/>
</c:if>

<c:if test="${empty param.firstname}" >
		<c:set var="param.firstname" value = ""/>
</c:if>

<c:if test="${empty param.lastname}" >
		<c:set var="param.lastname" value = ""/>
</c:if>

<c:if test="${empty param.address}" >
		<c:set var="param.address" value = ""/>
</c:if>

<c:if test="${empty param.country}" >
		<c:set var="param.country" value = ""/>
</c:if>

<c:if test="${empty param.affiliation}" >
		<c:set var="param.affiliation" value = ""/>
</c:if>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select user_name,(now()-registration_date) as seconds_since_registered, registration_status from users where user_name=?
	<sql:param value="${param.username}"/>
</sql:query>

<!-- if user has commenced status but registration date is before 48 h, he will be considered a new user -->
<c:set var="send_email" value="true" />

<c:forEach var="row" items="${rs.rows}">
	<c:choose>
		<c:when test="${row.registration_status != 'commenced'}">
			<jsp:forward page="register.jsp">
				<jsp:param name="registerstatus" value="This username already exists! "/>
				<jsp:param name="email" value="${param.email}"/>
		  </jsp:forward>
		</c:when>
		<c:otherwise>
						<c:if test="${row.seconds_since_registered le 172800}">
											<jsp:forward page="register.jsp">
												<jsp:param name="registerstatus" value="This username already exists! "/>
												<jsp:param name="email" value="${param.email}"/>
								  		</jsp:forward>
	  				</c:if>
		</c:otherwise>

	</c:choose>
</c:forEach>

<html>
<head>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<title>QMRF Register Page</title>
<body bgcolor="white">

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="register"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>


<table>
<tr>
	<td>
	Username
	</td>
	<td>
	<b>${param.username}</b>
	</td>
</tr>
<tr><td>
E-mail </td>
<td>
<b>${param.email}</b>
</td>
</tr>
</table>

<c:catch var='exception'>
<sql:update var="rs" dataSource="jdbc/qmrf_documents">
	insert into users (user_name,`password`,email,registration_id,title,firstname,lastname,address,country,affiliation) values (?,md5(?),?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE password=md5(?),registration_id=?,registration_date=now(),title=?,firstname=?,lastname=?,address=?,country=?,affiliation=?
	<sql:param value="${param.username}"/>
  <sql:param value="${param.password1}"/>
  <sql:param value="${param.email}"/>
  <sql:param value="${pageContext.session.id}"/>
  <sql:param value="${param.title}"/>
  <sql:param value="${param.firstname}"/>
	<sql:param value="${param.lastname}"/>
	<sql:param value="${param.address}"/>
	<sql:param value="${param.country}"/>
	<sql:param value="${param.affiliation}"/>

  <sql:param value="${param.password1}"/>
  <sql:param value="${pageContext.session.id}"/>
  <sql:param value="${param.title}"/>
  <sql:param value="${param.firstname}"/>
	<sql:param value="${param.lastname}"/>
	<sql:param value="${param.address}"/>
	<sql:param value="${param.country}"/>
	<sql:param value="${param.affiliation}"/>

</sql:update>
</c:catch>

<c:choose>
<c:when test='${not empty exception}'>

					<div class="error">
							Registration error ${transactionException2}
				</div>

</c:when>
<c:otherwise>
<mt:mail server="${mailserver}" >
		<mt:from>${mailfrom}</mt:from>
		<mt:setrecipient type="to">${param.email}</mt:setrecipient>
		<mt:setrecipient type="bcc">${bccadmin}</mt:setrecipient>
		<mt:subject>QMRF Website registration : your username, ${param.username}, has been created</mt:subject>

    <mt:message>
Thank you for registering as a user with QMRF repository. To  confirm the registration, simply point your browser to the following URL:

      <c:set var="u">
      		${pageContext.request.scheme}://${header["host"]}${pageContext.request.contextPath}/rconfirm.jsp
    	</c:set>
<c:url value="${u}"><c:param name="id" value="${pageContext.session.id}"/></c:url>

Finally, your  registration will be  cancelled automatically if QMRF repository does not receive your confirmation within 48h. After that time, you must start over and resend the command to get a new confirmation code. If you change your mind and decide that you do  NOT want to confirm the command, simply discard the present message and let the request expire on its own.
    </mt:message>
    <mt:send>
    	<mt:error id="err">
         <jsp:getProperty name="err" property="error"/>
       </mt:error>
	</mt:send>
</mt:mail>

<p>

A confirmation mail has been sent to ${param.email}. Please follow the instruction in the confirmation e-mail. The registration is invalid without being confirmed.
</p>
</c:otherwise>
</c:choose>

<hr>
<h6>
Please note that log in is required only for submitting new (Q)MRF documents.
</h6>


</body>
</html>
