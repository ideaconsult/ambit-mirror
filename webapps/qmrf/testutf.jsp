<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>
<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin'] eq 'false'}" >
  <c:redirect url="/user.jsp"/>
</c:if>

	<c:if test="${!empty param.text && !empty param.name}">
		Update start <i>${param.text}</i>
		<c:catch var="err">
		<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update>
				SET NAMES 'utf8'
			</sql:update>
			<sql:update var="rs">
				insert into test (name,text) values (?,?) on duplicate KEY UPDATE text=?
				<sql:param value="${param.name}"/>
				<sql:param value="${param.text}"/>
				<sql:param value="${param.text}"/>
			</sql:update>
		</sql:transaction>		Update done.
		</c:catch>
		<h6>
		${err}
		</h6>
	</c:if>

	<sql:update dataSource="jdbc/qmrf_documents">
		SET NAMES 'utf8'
	</sql:update>
	<sql:query var="rs" dataSource="jdbc/qmrf_documents">
		select name,text from test
	</sql:query>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>

<title>Test UTF8</title>
</head>
<body>
	<table border="0" cellspacing="5" border="1">
	<tr align="left">
		<th>#</th><th>Name</th><th>Text</th>
	</tr>
	<tr>
	<th></th>
	<th>${param.name}</th><th>${param.text}</th>
	</tr>
	<c:if test="${empty err}">
		<set var name="id" value="1"/>

		<c:forEach var="row" items="${rs.rows}">
			<tr align="left">
			<form method="post" name="n${id}">
			<th>
			${id}
			</th>
			<td>
			<input type="text" name="name" value = "${row.name}" size="32"/>
			</td>
			<td>
			<input type="text" name="text" value = "${row.text}" size="40"/>
			</td>
			<td>
					${row.text}
			</td>
			<td>
			<input type="submit" value="update"/>
			</td>
			</form>
			</tr>
			<set var name="id" value="${id +1}"/>
		</c:forEach>
	</c:if>
	<h6>
	${err}
	</h6>
	</tr>
	</table>

<hr>
<!--
<h4>At the start of the page</h4>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>
<h4>Between <head> tags of the page</h4>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta http-equiv="Content-Type" contentType="text/xml;charset=UTF-8">
<h4>server.xml</h4> - useBodyEncodingForURI="true"  - otherwise the statement above will have no effect
      <Connector
               port="80"
               .......
               useBodyEncodingForURI="true"
               URIEncoding="UTF-8"
               />
<h4>server.xml</h4> - JDBC connector
				  <Resource name="jdbc/yourdatabase" auth="Container" type="javax.sql.DataSource"
				               maxActive="100" maxIdle="30" maxWait="10000"
				               username="user" password="thepassword" driverClassName="com.mysql.jdbc.Driver"
				               useUnicode="true" characterEncoding="UTF-8"
				               url="jdbc:mysql://localhost:3306/yourdatabase"/>
<h4>Start mysql witth default charset utf8 !</h4>

	Try
	SHOW VARIABLES LIKE 'character_set%'
</textarea>
-->

</body>
</html>