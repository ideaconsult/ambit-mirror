<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<c:set var="ip" value="${pageContext.request.remoteAddr}" />

<c:set var="this_page" value="${pageContext.request.requestURI}" />

<c:set var="calling_page">
	<%= request.getHeader("Referer") %>
</c:set>
<c:if test="${empty calling_page}" >
		<c:set var="calling_page" value="" />
</c:if>

<sql:update var="updateCount" dataSource="jdbc/qmrf_documents">
			UPDATE u_hits SET counter=counter+1, last_access=now() WHERE page = ? AND ip=? AND ref=?
			<sql:param value="${this_page}"/>
			<sql:param value="${ip}"/>
			<sql:param value="${calling_page}"/>
</sql:update>

<c:if test="${updateCount < 1}">
		<c:catch var = "err">
		<sql:update var="rs" dataSource="jdbc/qmrf_documents">
				INSERT INTO u_hits VALUES (?,?,substring(?,1,128), 1,now(),now()) ON DUPLICATE KEY UPDATE last_access=now(),counter=counter+1
				<sql:param value="${this_page}"/>
				<sql:param value="${ip}"/>
				<sql:param value="${calling_page}"/>
		</sql:update>
		</c:catch>
		${err}
</c:if>

		<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT sum(counter) as nhits,min(first_access) as fromhits from u_hits WHERE page = ? group by page
				<sql:param value="${this_page}"/>
		</sql:query>

		<c:set var="nhits" value="" />
		<c:set var="fromhits" value="" />

		<c:forEach var="row" items="${rs.rows}">
				<c:set var="nhits" value="${row.nhits}" />
				<c:set var="fromhits" value="${row.fromhits}" />
		</c:forEach>
<table border="0" width="95%">
<tr>
<td width="50%" align="left">
<h6>
	<font color='#AAAAAA'>For information about this site please contact ${initParam['support-email']}</font>
</h6>	
</td>
<td width="50%" align="right">
<h6>
	<font color='#AAAAAA'>This page has been accessed ${nhits} times since ${fromhits} </font>
</h6>	
</td>
</tr>
<tr>
<td colspan="2" align="right">
<font color='#D6DFF7'>
Developed by Ideaconsult Ltd. (2007-2008) on behalf of JRC
</font>
</td>
</tr>
<tr>
<td colspan="2" align="right">
  <A HREF="http://validator.w3.org/check?uri=referer">
    <IMG SRC="images/valid-html401-blue-small.png" ALT="Valid HTML 4.01 Transitional" TITLE="Valid HTML 4.01 Transitional" HEIGHT="16" WIDTH="45" border="0">
  </A>

  <A HREF="http://jigsaw.w3.org/css-validator/check/referer">
    <IMG SRC="images/valid-css-blue-small.png" TITLE="Valid CSS" ALT="Valid CSS" HEIGHT="16" WIDTH="45" border="0">
  </A>
</td>
</tr>
</table>


