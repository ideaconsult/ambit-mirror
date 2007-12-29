<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<fmt:requestEncoding value="UTF-8"/>

<c:if test="${!empty param.id}">

<c:choose>
	<c:when test="${empty sessionScope['username']}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where idqmrf=? and status='published' limit 1
			<sql:param value="${param.id}"/>
		</sql:query>
	</c:when>
	<c:when test="${sessionScope['isadmin'] eq 'true'}">
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where idqmrf=? limit 1
			<sql:param value="${param.id}"/>
		</sql:query>
	</c:when>
	<c:otherwise>
		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
			select idqmrf,qmrf_number,xml from documents where idqmrf=? and ((user_name=?) or (status='published')) limit 1
			<sql:param value="${param.id}"/>
			<sql:param value="${sessionScope['username']}"/>
		</sql:query>
	</c:otherwise>
</c:choose>


<c:set var="view" value="html"/>
<c:choose>
	<c:when test="${empty param.view}">
			<c:set var="view" value="html"/>
	</c:when>
	<c:when test="${param.view eq 'applet'}">
			<c:set var="view" value="applet"/>
	</c:when>
	<c:when test="${param.view eq 'html'}">
			<c:set var="view" value="html"/>
	</c:when>
	<c:when test="${param.view eq 'attachments'}">
			<c:set var="view" value="attachments"/>
	</c:when>
	<c:otherwise>
			<c:set var="vie" value="html"/>
	</c:otherwise>
</c:choose>

<br>

<c:forEach var="row" items="${rs.rows}">

	<c:choose>
		<c:when test="${param.view eq 'applet'}">
			<form name="qmrfform">

							<input type="hidden" NAME="xml" />
					  <c:set var="u">
      						${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/download_xml.jsp
    				</c:set>
						<c:set var="dataurl">

		      	<c:url value="${u}"> <c:param name="id" value="${row.idqmrf}"/>
						</c:url>
		    	</c:set>

					<jsp:plugin type="applet" name="QMRFApplet" code="ambit.applets.QMRFApplet" archive="applets/QMRFApplet.jar" codebase="." jreversion="1.5" width="750" height="650" >
				        <jsp:params>
				        	<jsp:param
		               name="dataurl"
		               value="${dataurl}"
					          />
					      </jsp:params>
					    <jsp:fallback>
					        Plugin tag OBJECT or EMBED not supported by browser. Redirect to a plain html page
					    </jsp:fallback>
					</jsp:plugin>
			</form>
		</c:when>
		<c:when test="${param.view eq 'attachments'}">
					<h4>Attachments ${qmrf_number}</h4>
					<table width="100%" border="0">
						<jsp:include page="list_attachments.jsp" flush="true">
    					<jsp:param name="id" value="${row.idqmrf}"/>
						</jsp:include>
					</table>
			<!--
						<jsp:include page="view_attachments.jsp" flush="true">
    					<jsp:param name="xml" value="${row.xml}"/>
						</jsp:include>
			-->
		</c:when>
		<c:otherwise>
					<div style="background:#36393D;color:#FFFFFF;text-align:center" >
					<a name="#qmrf_doc">QMRF Number: ${row.qmrf_number}</a>
				</div>
			<table width="100%">
				<tr><td>
				  <c:import var="qmrfToHtml" url="/WEB-INF/xslt/qmrf2html.xsl"/>
				  <x:transform xml="${row.xml}" xslt="${qmrfToHtml}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd"/>
				</td></tr>
				<tr><td>
				<jsp:include page="structures.jsp" flush="true">
					<jsp:param name="highlighted" value="${param.id}"/>
					<jsp:param name="view" value="${view}"/>
				</jsp:include>
				</td></tr>

			</table>

		</c:otherwise>
	</c:choose>




</c:forEach>



</c:if>
