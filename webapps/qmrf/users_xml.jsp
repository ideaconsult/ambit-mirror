<%@ page pageEncoding="UTF-8" contentType="text/xml;charset=utf-8" %><?xml version="1.0" encoding="UTF-8"?>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%
response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-store");
response.setHeader("Expires", "0");
%>

<c:set var="sql" value =""/>

<c:set var="user_name" value="${param.user_name}"/>

<c:if test="${empty param.user_name}">
	<c:set var="user_name" value="${pageContext.request.userPrincipal.name}"/>
</c:if>

<c:set var="user_status" value="${param.user_status}"/>
<c:if test="${empty param.user_status}">
	<c:set var="user_status" value="confirmed"/>
</c:if>


<c:if test="${(!empty param.user_name) && (pageContext.request.userPrincipal.name ne param.user_name) && (!sessionScope['ismanager']) && (!sessionScope['iseditor']) && (!sessionScope['isadmin'])}">
	<c:set var="stop" value="true"/>
	<users user_name="You are not authorized to retrieve user information"/>
</c:if>

<c:if test="${!stop}">
<c:choose>
<c:when test="${empty pageContext.request.userPrincipal.name}">
  <users/>
</c:when>

<c:when test="${empty sessionScope['ismanager']}" >
  <users/>
</c:when>

<c:when test="${sessionScope['ismanager'] && (sessionScope.viewmode eq 'qmrf_manager')}" >
	<c:set var="sql" value="SELECT user_name,email,registration_status,date_format(registration_date,'${sessionScope.dateformat}') as registration_date,title,firstname,lastname,address,country,webpage,affiliation,keywords,reviewer from users where registration_status= \"${user_status}\" order by user_name"  />
</c:when>
<c:otherwise>
	<c:set var="sql" value="SELECT user_name,email,registration_status,date_format(registration_date,'${sessionScope.dateformat}') as registration_date,title,firstname,lastname,address,country,webpage,affiliation,keywords,reviewer from users where user_name= \"${user_name}\" order by user_name" />
</c:otherwise>
</c:choose>
<users status='${user_status}'>
	<c:if test="${!empty sql}">
		<c:catch var="error">
	  		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				${sql}
			</sql:query>

			<c:forEach var="row" items="${rs.rows}">
					<user
						user_name="<c:out value='${row.user_name}' />"
						email="<c:out value='${row.email}' />"
						registration_status="<c:out value='${row.registration_status}' />"
						registration_date="<c:out value='${row.registration_date}' />"
						title="<c:out value='${row.title}' />"
						firstname="<c:out value='${row.firstname}' />"
						lastname="<c:out value='${row.lastname}' />"
						address="<c:out value='${row.address}' />"
						country="<c:out value='${row.country}' />"
						affiliation="<c:out value='${row.affiliation}' />"
						reviewer="<c:out value='${row.reviewer}' />"
						webpage="<c:out value='${row.webpage}' />"
						keywords="<c:out value='${row.keywords}' />"
					>
						<c:set var="roles">qmrf_user,qmrf_admin,qmrf_manager,qmrf_editor</c:set>
						<c:forTokens var="t" items="${roles}" delims=",">
							<c:catch var="err">
								<sql:query var="rs_user" dataSource="jdbc/qmrf_documents">
										SELECT user_name,role_name from tomcat_users.user_roles where user_name=? and role_name=?;
										<sql:param value="${row.user_name}"/>
										<sql:param value="${t}"/>
								</sql:query>

								<c:if test="${rs_user.rowCount eq 0}">
									<role name='${t}' selected='false' />
								</c:if>
								<c:forEach var="rs_row" items="${rs_user.rows}">
									<role name='${t}' selected='true' />
								</c:forEach>
							</c:catch>
							<c:if test="${!empty err}">
								<error>${err}</error>
							</c:if>
						</c:forTokens>
					</user>
		  	</c:forEach>


	  	</c:catch>
	  	<c:if test="${!empty error}">
	  		${error}
	  	</c:if>
	  </c:if>

</users>
</c:if>