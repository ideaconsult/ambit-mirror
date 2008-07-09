<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
 <%@ taglib uri="http://jakarta.apache.org/taglibs/taglibs-mailer" prefix="mt" %>
 <%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>

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

<c:set var="invalid" value=""/>
<c:if test="${empty param.username}" >
	<c:set var="status_username" value="Empty user name"/>
	<c:set var="invalid" value="true"/>	
</c:if>

<c:if test="${empty param.email}" >
	<c:set var="status_email" value="Empty e-mail"/>
	<c:set var="invalid" value="true"/>		
</c:if>

<c:if test="${!fn:contains(param.email, '@') }" >
	<c:set var="status_email" value="Invalid e-mail"/>
	<c:set var="invalid" value="true"/>		
</c:if>

<c:if test="${empty param.firstname}" >
	<c:set var="status_firstname" value="Empty first name"/>
	<c:set var="invalid" value="true"/>		
</c:if>

<c:if test="${empty param.lastname}" >
	<c:set var="status_lastname" value="Empty last name"/>
	<c:set var="invalid" value="true"/>		
</c:if>

<c:if test="${fn:length(param.password1)<6}">
	<c:set var="status_password" value="The password should consist of at least 6 characters"/>
</c:if>
<c:if test="${(empty param.password1) or (empty param.password2)}" >
	<c:set var="status_password" value="Empty password"/>
	<c:set var="invalid" value="true"/>	
</c:if>

<c:if test="${!(param.password1 eq param.password2)}" >
	<c:set var="status_password" value="Passwords don't match!"/>
	<c:set var="invalid" value="true"/>	
</c:if>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select user_name,(now()-registration_date) as seconds_since_registered, registration_status from users where user_name=?
	<sql:param value="${param.username}"/>
</sql:query>

<c:forEach var="row" items="${rs.rows}">
	<c:if test="${row.registration_status != 'commenced'}">
		<c:set var="status_username" value="This username already exists!"/>
		<c:set var="invalid" value="true"/>			  
	</c:if>
</c:forEach>		

<c:if test="${invalid eq 'true'}" >
	<jsp:forward page="register.jsp">
		<jsp:param name="status_password" value="${status_password}"/>
		<jsp:param name="status_username" value="${status_username}"/>
		<jsp:param name="status_email" value="${status_email}"/>
		<jsp:param name="status_firstname" value="${status_firstname}"/>
		<jsp:param name="status_lastname" value="${status_lastname}"/>										
			<jsp:param name="email" value="${param.email}"/>		
			<jsp:param name="username" value="${param.username}"/>
			<jsp:param name="firstname" value="${param.firstname}"/>
			<jsp:param name="lastname" value="${param.lastname}"/>
			<jsp:param name="address" value="${param.address}"/>			
			<jsp:param name="affiliation" value="${param.affiliation}"/>			
			<jsp:param name="title" value="${param.title}"/>			
			<jsp:param name="country" value="${param.country}"/>	
			<jsp:param name="reviewer" value="${param.reviewer}"/>				
			<jsp:param name="webpage" value="${param.webpage}"/>							
			<jsp:param name="keywords" value="${param.keywords}"/>					

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
				<jsp:param name="status_username" value="This username already exists!"/>
				<jsp:param name="email" value="${param.email}"/>
		  </jsp:forward>
		</c:when>
		<c:otherwise>
			<c:if test="${row.seconds_since_registered le 172800}">
				<jsp:forward page="register.jsp">
					<jsp:param name="status_username" value="This username already exists!"/>
					<jsp:param name="email" value="${param.email}"/>
				</jsp:forward>
	  		</c:if>
		</c:otherwise>

	</c:choose>
</c:forEach>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Database Registration page"/>
</jsp:include>


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
	<c:set var="reviewer" value="0"/>
	<c:if test="${param.reviewer eq 'checked'}">
		<c:set var="reviewer" value="1"/>	
	</c:if>
<sql:update var="rs" dataSource="jdbc/qmrf_documents">
	insert into users (user_name,`password`,email,registration_id,title,firstname,lastname,address,country,affiliation,webpage,keywords,reviewer) values (?,md5(?),?,?,?,?,?,?,?,?,?,?,?) ON DUPLICATE KEY UPDATE password=md5(?),registration_id=?,registration_date=now(),title=?,firstname=?,lastname=?,address=?,country=?,affiliation=?,webpage=?,keywords=?,reviewer=?
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
	<sql:param value="${param.webpage}"/>
	<sql:param value="${param.keywords}"/>	
	<sql:param value="${reviewer}"/>	

  <sql:param value="${param.password1}"/>
  <sql:param value="${pageContext.session.id}"/>
  <sql:param value="${param.title}"/>
  <sql:param value="${param.firstname}"/>
	<sql:param value="${param.lastname}"/>
	<sql:param value="${param.address}"/>
	<sql:param value="${param.country}"/>
	<sql:param value="${param.affiliation}"/>
	<sql:param value="${param.webpage}"/>
	<sql:param value="${param.keywords}"/>	
	<sql:param value="${reviewer}"/>		

</sql:update>
</c:catch>

<c:choose>
<c:when test='${not empty exception}'>

					<div class="error">
							Registration error ${exception}
				</div>

</c:when>
<c:otherwise>
	<c:set var="text">
	Thank you for applying for user registration with the QMRF Database.
	
	Please point your browser to the following URL in order to proceed with the registration of the "${param.username}" user:
	
	      <c:set var="u">
	      		${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/rverify.jsp
	    	</c:set>
	<c:url value="${u}"><c:param name="id" value="${pageContext.session.id}"/></c:url>
	
	Please note that your registration will be cancelled automatically if it is not confirmed within 48 hours. If you miss this deadline you should start over the registration procedure and get a new confirmation code.
	
	If you change your mind and decide that you do NOT want to confirm the registration, then please discard this message and let the request expire on its own.
	</c:set>
	<mt:mail server="${mailserver}" >
			<mt:from>${mailfrom}</mt:from>
			<mt:setrecipient type="to">${param.email}</mt:setrecipient>
			<mt:setrecipient type="bcc">${bccadmin}</mt:setrecipient>
			<mt:subject>[QMRF Database] verification of user e-mail (${param.username})</mt:subject>
	
	    <mt:message>
<jsp:include page="mail.jsp" flush="true">
	    <jsp:param name="title" value="${param.title}"/>
	    <jsp:param name="firstname" value="${param.firstname}"/>
	    <jsp:param name="lastname" value="${param.lastname}"/>        
	    <jsp:param name="text" value="${text}"/>
</jsp:include>	    
	    </mt:message>
	    <mt:send>
	    	<mt:error id="err">
	         <jsp:getProperty name="err" property="error"/>
	       </mt:error>
		</mt:send>
	</mt:mail>

<p>
Please follow the instructions in the confirmation mail, which has been sent to ${param.email}, in order to complete the registration procedure.
</p>
</c:otherwise>
</c:choose>

<hr>
<h6>
Please note that log in is required only for submitting new (Q)MRF documents.
</h6>

<div id="hits">
<p>
<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
</jsp:include>
</div>
</body>
</html>