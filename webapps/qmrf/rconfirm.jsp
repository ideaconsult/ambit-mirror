<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
 <%@ taglib uri="http://jakarta.apache.org/taglibs/taglibs-mailer" prefix="mt" %>

<fmt:requestEncoding value="UTF-8"/>

<c:if test="${empty param.user_name}" >
  <c:redirect url="/myprofile.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin'] eq 'false'}" >
  <c:redirect url="/myprofile.jsp"/>
</c:if>


<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="QMRF Inventory Registration page"/>
</jsp:include>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="register"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>    
</jsp:include>

<sql:query var="rs" dataSource="jdbc/qmrf_documents">
	select user_name,firstname,lastname,email,title from users where user_name=? and registration_status='verified' limit 1
<sql:param value="${param.user_name}"/>

</sql:query>


<c:choose>
	<c:when test="${rs.rowCount gt 0}">
		<c:forEach var="row" items="${rs.rows}">


			<sql:setDataSource dataSource="jdbc/qmrf_documents"/>

			<c:catch var='transactionException1'>
				<sql:transaction>
							<!-- if ok, update statis to confirmed and transfer the users into tomcat table , perhaps delete password from here -->
							<sql:update var="rs">
									update users set registration_status='confirmed',registration_date=now(),registration_id=null where user_name=? and registration_status='verified'
									<sql:param value="${param.user_name}"/>
							</sql:update>
				</sql:transaction>
			  </c:catch>

				<c:if test='${not empty transactionException1}'>
					<div class="error">
							Registration error ${transactionException1}
				</div>

				</c:if>

				<sql:setDataSource dataSource="jdbc/tomcat_users"/>

				<c:catch var='transactionException2'>
						<sql:transaction>

								<sql:update var="rs1" >
										insert into users (user_name,user_pass) select user_name,password from qmrf_documents.users where user_name=?
										<sql:param value="${row.user_name}"/>
								</sql:update>

								<sql:update var="rs2" >
										insert into user_roles (user_name,role_name) values (?,'qmrf_user')
										<sql:param value="${row.user_name}"/>
								</sql:update>

					</sql:transaction>
			</c:catch>

			<c:if test='${not empty transactionException2}'>

					<div class="error">
							Registration error ${transactionException2}
				</div>

			</c:if>

			<c:set var="mailserver" value="${initParam['mail-server']}" />			 
			<c:set var="mailfrom" value="${initParam['mail-from']}"  /> 			
			<mt:mail server="${mailserver}" >
					<mt:from>${mailfrom}</mt:from>
					<mt:setrecipient type="to">${row.email}</mt:setrecipient>
					<mt:subject>[QMRF Inventory] Confirmation of user registration (${param.user_name})</mt:subject>
			
			    <mt:message>
		<jsp:include page="mail.jsp" flush="true">
			    <jsp:param name="title" value="${row.title}"/>
			    <jsp:param name="firstname" value="${row.firstname}"/>
			    <jsp:param name="lastname" value="${row.lastname}"/>        
			    <jsp:param name="text" value="Your registration is now confirmed and you can log in into QMRF repository."/>
		</jsp:include>	    
			    </mt:message>
			    <mt:send>
			    	<mt:error id="err">
			         <jsp:getProperty name="err" property="error"/>
			       </mt:error>
				</mt:send>
			</mt:mail>
			 
			<c:choose>
			<c:when test='${not empty err}'>
				<div class="error">
						Registration error ${err}
				</div>
			
			</c:when>
			<c:otherwise>
			<dic class="success">
				The registration of the new user <b>${row.user_name}</b> into QMRF Inventory is confirmed. Notification email is sent to <b>${row.email}</b>.
			</div>
			</c:otherwise>
			</c:choose>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<p><p>
			No user found ${param.user_name}

	</c:otherwise>
</c:choose>

<hr>

<div id="hits">
<p>
<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
</jsp:include>
</div>

</body>
</html>
