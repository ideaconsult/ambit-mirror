
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> 
<fmt:requestEncoding value="UTF-8"/> 

<c:catch var="err">
	<sql:transaction dataSource="jdbc/qmrf_documents">
		<sql:update>
			CREATE TABLE IF NOT EXISTS `qmrf_documents`.`test`  (
  			`name` varchar(32) NOT NULL,
  			`text` text NOT NULL,
  				PRIMARY KEY  (`name`)
			) ENGINE=MyISAM DEFAULT CHARSET=utf8;
		</sql:update>
	</sql:transaction>	
</c:catch>
<c:if test="${empty err}">
	<c:if test="${!empty param.text && !empty param.name}">
		Update start ${param.text}
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
		</sql:transaction>	
		Update done.
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
</c:if>

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
	
<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	SHOW VARIABLES LIKE 'character_set%'
</sql:query>
<c:forEach var="row" items="${rs.rows}">
	${row.Variable_name}&nbsp;${row.Value}
	<br>
</c:forEach>	


<!--
At the start of the page
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> 
<fmt:requestEncoding value="UTF-8"/> 

Between <head> tags
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<meta http-equiv="Content-Type" contentType="text/xml;charset=UTF-8">

server.xml
  useBodyEncodingForURI="true"  - otherwise the statement above will have no effect
    <Connector
               port="80"  maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" redirectPort="8443" acceptCount="100"
               connectionTimeout="20000" disableUploadTimeout="true" 
               useBodyEncodingForURI="true"
               URIEncoding="UTF-8"
               />
               
JDBC connector
				  <Resource name="jdbc/qmrf_documents" auth="Container" type="javax.sql.DataSource"
				               maxActive="100" maxIdle="30" maxWait="10000"
				               username="qmrf" password="qmrf2007" driverClassName="com.mysql.jdbc.Driver"
				               useUnicode="true" characterEncoding="UTF-8"
				               url="jdbc:mysql://localhost:3306/qmrf_documents"/>

not sure if mandatory - seems not to
Start tomcat with 				               
-Dfile.encoding=UTF-8
				               
-->

</body>
</html>