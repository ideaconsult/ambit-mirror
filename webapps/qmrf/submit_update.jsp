<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>

<fmt:requestEncoding value="UTF-8"/> 

<c:set var="thispage" value='submit_update.jsp'/>


<c:if test="${empty param.id}" >
  <c:redirect url="/user.jsp"/>
</c:if>

<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isadmin']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${sessionScope['isadmin']=='true'}" >
  <c:redirect url="/admin.jsp"/>
</c:if>

<!-- Not launched from edit.jsp -->
<c:if test="${sessionScope.qmrf_update ne 'true'}">
	<c:redirect url="/user.jsp"/>
</c:if>
<c:set var="qmrf_update" value="" scope="session"/>

<c:set var="state" value="draft"/>


<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>
<c:choose>
	<c:when test="${empty param.xml}" >
			<!-- Only status update -->
			<c:catch var="transactionException_update">
			<sql:transaction dataSource="jdbc/qmrf_documents">
			<sql:update var="updateCount" >
			    update documents set status=? where idqmrf=? and user_name=?;
			  <sql:param value="${state}"/>
			  <sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>

			</sql:update>
		</sql:transaction>
		</c:catch>

	</c:when>
	<c:otherwise>

			<!-- data  update -->
		<c:catch var="transactionException_update">
			<c:catch var="title_err">
				<x:parse xml="${param.xml}" var="doc" systemId="/WEB-INF/xslt/qmrf.dtd"/>
				<c:set var="qmrf_tmp">
					<x:out escapeXml="false" select="$doc//QMRF/QMRF_chapters/QSAR_identifier/QSAR_title"/>			
				</c:set>
				<x:parse xml="${fn:trim(qmrf_tmp)}" var="doc"/>			
				<c:set var="qmrf_title">
					<x:out select="$doc//body"/>			
				</c:set>			

				<c:set var="qmrf_title" value="${fn:trim(qmrf_title)}"/>
			</c:catch>
			<c:if test="${!empty title_err}">
				<c:set var="qmrf_title" value=""/>
			</c:if>
			<c:import url="include_attachments.jsp" var="newxml" >
				<c:param name="id" value="${param.id}"/>
				<c:param name="xml" value="${param.xml}"/>
			</c:import>
			<sql:transaction dataSource="jdbc/qmrf_documents">

			<sql:update var="updateCount" >
				update documents set qmrf_title=substring(?,1,128),status=?,xml=?,updated=now() where idqmrf=? and user_name=?;
				<sql:param value="${qmrf_title}"/>
				<sql:param value="${state}"/>
				<sql:param value="${newxml}"/>
				<sql:param value="${param.id}"/>
				<sql:param value="${sessionScope['username']}"/>
			</sql:update>
			</sql:transaction>
		</c:catch>
	</c:otherwise>
</c:choose>


<c:choose>
<c:when test='${!empty transactionException_update}'>
	<c:redirect url="edit_attachmentsdb.jsp">
	 <c:param name="id" value="${param.id}"/>
	<c:param name="status">
		${transactionException_update}
	</c:param>
	</c:redirect>
</c:when>
<c:otherwise>
	<c:redirect url="edit_attachmentsdb.jsp">
	 <c:param name="id" value="${param.id}"/>
	<c:param name="status">
		Saving as draft successful.
	</c:param>
	</c:redirect>
</c:otherwise>
</c:choose>
