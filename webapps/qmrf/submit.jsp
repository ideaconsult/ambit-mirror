<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<fmt:requestEncoding value="UTF-8"/> 

<c:set var="thispage" value='submit.jsp'/>


<c:if test="${empty param.xml}" >
  <c:redirect url="/user.jsp"/>
</c:if>


<c:if test="${empty sessionScope['username']}" >
  <c:redirect url="/protected.jsp"/>
</c:if>

<c:if test="${empty sessionScope['isauthor']}" >
  <c:redirect url="/index.jsp"/>
</c:if>

<c:if test="${sessionScope['isauthor']=='false'}" >
  <c:redirect url="/index.jsp"/>
</c:if>

<!-- Not launched from create.jsp -->
<c:if test="${sessionScope.qmrf_create ne 'true'}">
	<c:redirect url="/user.jsp"/>
</c:if>
<c:set var="qmrf_create" value="" scope="session"/>


<c:set var="uname" value='${sessionScope["username"]}'/>
<c:set var="updateCount" value="0"/>

<c:set var="state" value="draft"/>
<!--
<c:choose>
	<c:when test="${empty param.submit_state}">
		<c:set var="state" value="draft"/>
	</c:when>
	<c:when test="${param.submit_state} eq 'submitted'">
		<c:set var="state" value="submitted"/>
	</c:when>
	<c:when test="${param.submit_state} eq 'draft'">
		<c:set var="state" value="draft"/>
	</c:when>
	<c:otherwise>
		<c:set var="state" value="draft"/>
	</c:otherwise>
</c:choose>
-->
<c:catch var="transactionException_update">
	<sql:transaction dataSource="jdbc/qmrf_documents">
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
		<sql:update>
				set names 'utf8'
		</sql:update>
		<c:import var="xsl" url="/WEB-INF/xslt/delete_attachments.xsl"/>
		<c:set var="newxml">
		<x:transform xml="${param.xml}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>
		</c:set>

		<sql:update var="updateCount" >
			INSERT INTO documents (qmrf_title,xml,user_name,status) VALUES (substring(?,1,128),?,?,?);
		<sql:param value="${qmrf_title}"/>			
		<sql:param value="${newxml}"/>
		<sql:param value="${sessionScope['username']}"/>
		<sql:param value="${state}"/>


		</sql:update>
		<sql:query var="rs" sql="SELECT LAST_INSERT_ID() AS id" />
	</sql:transaction>
	<c:forEach var="row" items="${rs.rows}">

		<c:redirect url="edit_attachmentsdb.jsp">
		<c:param name="id" value="${row.id}"/>
		<c:param name="status">
			New document added to QMRF Database.
		</c:param>
		</c:redirect>

	</c:forEach>
</c:catch>


<c:if test='${not empty transactionException_update}'>
<%-- prepare the page --%>
<!DOCTYPE html PUBLIC
  "-//W3C//DTD XHTML 1.0 Transitional//EN"
  "DTD/xhtml1-transitional.dtd">
<html>
	<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<head>
<title>QMRF documents</title>

</head>
<body>

<jsp:include page="menu.jsp" flush="true">
	<jsp:param name="viewmode" value="${param.viewmode}"/>
    <jsp:param name="highlighted" value="create"/>
</jsp:include>
    <div class="error">
    	${transactionException_update}
    </div>
  </body>
</html>

</c:if>


