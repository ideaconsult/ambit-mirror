<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:requestEncoding value="UTF-8"/>

<jsp:include page="top.jsp" flush="true">
    <jsp:param name="title" value="Search documents in QMRF Database"/>
</jsp:include>

<jsp:include page="query_settings.jsp" flush="true"/>

<c:set var="startpage" value="${sessionScope.page}"/>
<c:set var="startrecord" value="${sessionScope.page * sessionScope.pagesize}"/>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="search"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:set var="thispage" value="search_catalogs.jsp"/>

<c:set var="op" value="${param.operation}" />
<c:if test="${empty param.operation}">
	<c:set var="op" value="AND"/>
</c:if>


<c:if test="${!empty(param.qmrfno_enabled)}">

		<c:set var="p_qmrfno_enabled" value="${param.qmrfno_enabled}" scope="session"/>
</c:if>

<c:if test="${param.qmrfno != null}">
	<c:set var="p_qmrfno" value="${param.qmrfno}" scope="session"/>
</c:if>

<!-- default selection for free text -->
<c:if test="${empty(sessionScope.p_freetext_enabled)}">
	<c:set var="p_freetext_enabled" value="selected" scope="session"/>
</c:if>

<c:if test="${empty(sessionScope.p_freetext)}">
	<c:set var="p_freetext" value=" " scope="session"/>
</c:if>

<c:if test="${!empty(param.freetext_enabled)}">

		<c:set var="p_freetext_enabled" value="${param.freetext_enabled}" scope="session"/>
</c:if>

<c:if test="${param.freetext != null}">
	<c:set var="p_freetext" value="${param.freetext}" scope="session"/>
</c:if>

<c:if test="${!empty(param.endpoint_enabled) || (param.endpoint_enabled eq '')}">

	<c:set var="p_endpoint_enabled" value="${param.endpoint_enabled}" scope="session"/>
</c:if>

<c:if test="${param.endpoints_catalog != null}">
	<c:set var="p_endpoints_catalog" value="${param.endpoints_catalog}" scope="session"/>
</c:if>


<c:if test="${!empty(param.algorithm_enabled) || (param.algorithm_enabled eq '')}">
	<c:set var="p_algorithm_enabled" value="${param.algorithm_enabled}" scope="session"/>
</c:if>

<c:if test="${param.algorithms_catalog != null}">
	<c:set var="p_algorithms_catalog" value="${param.algorithms_catalog}" scope="session"/>
</c:if>

<c:if test="${!empty(param.authors_enabled) || (param.authors_enabled eq '')}">
	<c:set var="p_authors_enabled" value="${param.authors_enabled}" scope="session"/>
</c:if>

<c:if test="${param.authors_catalog != null}">
	<c:set var="p_authors_catalog" value="${param.authors_catalog}" scope="session"/>
</c:if>


<c:if test="${!empty(param.software_enabled) || (param.software_enabled eq '')}">
	<c:set var="p_software_enabled" value="${param.software_enabled}" scope="session"/>
</c:if>

<c:if test="${param.software_catalog != null}">
	<c:set var="p_software_catalog" value="${param.software_catalog}" scope="session"/>
</c:if>

<c:if test="${!empty(param.operation)}">
	<c:set var="p_operation" value="${param.operation}" scope="session"/>
</c:if>

<c:set var="op" value="${p_operation}" />
<c:if test="${empty p_operation}">
	<c:set var="op" value="AND"/>
</c:if>

<!-- end initialization -->

<form method="POST" name="searchform" action="">
<table bgcolor="${querycolor}" width="90%">
<tr bgcolor="${headercolor}">
<th>Use</th>
<th>Criteria</th>
<th>Value</th>
</tr>
<!-- qmrf number -->
<tr>
<th>
	<c:set var="selected" value=""/>
	<c:if test="${!empty p_qmrfno_enabled && (p_qmrfno_enabled eq 'selected')}">
		<c:set var="selected" value="checked"/>
	</c:if>
	<input type="checkbox" name="qmrfno_enabled" value="selected" ${selected}/>
	<input type="hidden" name="qmrfno_enabled" value="unselected">
</th>
<th>QMRF No.
</th>
<td>
		 <input type="text" name="qmrfno" size="16" value="${p_qmrfno}">
		 <a href="help.jsp?anchor=qmrf_number" target="help"><img src="images/help.png" alt="help" title="What is QMRF Number?" border="0"/></a>
</td>
</tr>

<!-- free text -->
<tr>
<th>
	<c:set var="selected" value=""/>
	<c:if test="${!empty p_freetext_enabled && (p_freetext_enabled eq 'selected')}">
		<c:set var="selected" value="checked"/>
	</c:if>
	<input type="checkbox" name="freetext_enabled" value="selected" ${selected}/>
	<input type="hidden" name="freetext_enabled" value="unselected">
</th>
<th>Free text
</th>
<td>
		 <input type="text" name="freetext" size="80" value="${p_freetext}">
</td>
</tr>

<tr>
<th>
	<c:set var="selected" value=""/>
	<c:if test="${!empty p_endpoint_enabled && (p_endpoint_enabled eq 'selected')}">
		<c:set var="selected" value="checked"/>
	</c:if>
	<input type="checkbox" name="endpoint_enabled" value="selected" ${selected}/>
	<input type="hidden" name="endpoint_enabled" value="unselected">
</th>
<th>Endpoint
</th>
<td>
		<c:catch var="err">
		<c:import var="xsl" url="/WEB-INF/xslt/catalogs2form.xsl"/>
		<c:import var="xml" url="endpoints_xml.jsp"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
			<x:param name="selected" value="${p_endpoints_catalog}"/>
	  </x:transform>
	  	</c:catch>
		<c:if test="$!empty err">
			${err}
		</c:if>
</td>
</tr>
<!--
<tr>
	<th><input type="checkbox" name="reference_enabled" value="selected" ></th>
<th>Reference
</th>
<td>
	TODO
</td>
</tr>
-->
<tr>
	<th>
		<c:set var="selected" value=""/>

		<c:if test="${!empty p_algorithm_enabled && (p_algorithm_enabled eq 'selected')}">
			<c:set var="selected" value="checked"/>
		</c:if>
		<input type="checkbox" name="algorithm_enabled" value="selected" ${selected} />
		<input type="hidden" name="algorithm_enabled" value="unselected">
	</th>
<th>Algorithm
</th>
<td>
		<c:catch var="err">
		<c:import var="xsl" url="/WEB-INF/xslt/catalogs2form.xsl"/>
		<c:import var="xml" url="algorithm_xml.jsp"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
			<x:param name="selected" value="${p_algorithms_catalog}"/>
	  </x:transform>
	  </c:catch>
		<c:if test="$!empty err">
			${err}
		</c:if>
</td>
</tr>
<tr>
	<th>
				<c:set var="selected" value=""/>
		<c:if test="${!empty p_software_enabled && (p_software_enabled eq 'selected')}">
			<c:set var="selected" value="checked"/>
		</c:if>

		<input type="checkbox" name="software_enabled" value="selected" ${selected} />
		<input type="hidden" name="software_enabled" value="unselected">
		</th>
<th>Software
</th>
<td>
		<c:catch var="err">
		<c:import var="xsl" url="/WEB-INF/xslt/catalogs2form.xsl"/>
		<c:import var="xml" url="software_xml.jsp"/>
		<x:transform xml="${fn:trim(xml)}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
			<x:param name="selected" value="${p_software_catalog}"/>
	  </x:transform>
		</c:catch>
		<c:if test="$!empty err">
			${err}
		</c:if>
</td>
</tr>
<tr>
	<th>
		<c:set var="selected" value=""/>
		<c:if test="${!empty p_authors_enabled && (p_authors_enabled eq 'selected')}">
			<c:set var="selected" value="checked"/>
		</c:if>

		<input type="checkbox" name="authors_enabled" value="selected" ${selected} />
		<input type="hidden" name="authors_enabled" value="unselected">
		</th>
<th>Authors
</th>
<td>
		<c:catch var="err">
			<c:import var="xsl" url="/WEB-INF/xslt/catalogs2form.xsl"/>
			<c:import var="xml" url="authors_xml.jsp"/>
			<x:transform xml="${fn:trim(xml)}" xslt="${xsl}" xmlSystemId="/WEB-INF/xslt/qmrf.dtd">
				<x:param name="selected" value="${p_authors_catalog}"/>
		  </x:transform>
		</c:catch>
		<c:if test="$!empty err">
			${err}
		</c:if>
</td>
</tr>

<tr>
	<th></th>
<th>
		<SELECT NAME="operation">
				<c:choose>
				<c:when test="${op eq 'AND'}">
					<OPTION value="AND" selected="SELECTED">And</Option>
					<OPTION value="OR">Or</Option>
				</c:when>
				<c:otherwise>
					<OPTION value="AND">And</Option>
					<OPTION value="OR" selected="SELECTED">Or</Option>
				</c:otherwise>
				</c:choose>

		</SELECT>
</th>
<td>
		<input type="submit" value="Search" />
		<a href="help.jsp?anchor=search_documents" target="help"><img src="images/help.png" alt="help" title="How to search QMRF documents?" border="0"/></a>
</td>
</tr>


<c:set var="where" value=""/>

<c:if test="${(p_qmrfno_enabled eq 'selected')  and !empty p_qmrfno}">
	<c:set var="where" value=" (qmrf_number = '${p_qmrfno}')"/>
</c:if>

<c:if test="${(p_freetext_enabled eq 'selected')  and !empty p_freetext}">
	<c:set var="cond" value=" "/>
	<c:if test="${!empty where}">
			<c:set var="cond" value="${where} ${op} "/>
	</c:if>
	<c:set var="where" value="${cond} (match (xml) against ('${p_freetext}' ))"/>
</c:if>

<c:if test="${(p_endpoint_enabled eq 'selected') and !empty p_endpoints_catalog}">
	<c:set var="cond" value=" "/>
	<c:if test="${!empty where}">
			<c:set var="cond" value="${where} ${op} "/>
	</c:if>
	<c:set var="where" value="${cond} (id_endpoint = ${fn:substring(p_endpoints_catalog, 4, -1)})"/>
</c:if>

<c:if test="${(param.reference_enabled eq 'selected') and !empty param.reference_catalog}">
	<c:set var="cond" value=" "/>
	<c:if test="${!empty where}">
			<c:set var="cond" value="${where} ${op} "/>
	</c:if>
	<c:set var="where" value="${cond} (id_reference = ${fn:substring(param.reference_catalog, 4, -1)})"/>
</c:if>

<c:if test="${(p_algorithm_enabled eq 'selected') and !empty p_algorithms_catalog}">
	<c:set var="cond" value=" "/>
	<c:if test="${!empty where}">
			<c:set var="cond" value="${where} ${op} "/>
	</c:if>
	<c:set var="where" value="${cond} (id_algorithm = ${fn:substring(p_algorithms_catalog, 4, -1)})"/>
</c:if>

<c:if test="${(p_software_enabled eq 'selected') and !empty p_software_catalog}">
	<c:set var="cond" value=" "/>
	<c:if test="${!empty where}">
			<c:set var="cond" value="${where} ${op} "/>
	</c:if>
	<c:set var="where" value="${cond} (id_software = ${fn:substring(p_software_catalog, 4, -1)})"/>
</c:if>

<c:if test="${(p_authors_enabled eq 'selected')  and !empty p_authors_catalog}">
	<c:set var="cond" value=" "/>
	<c:if test="${!empty where}">
			<c:set var="cond" value="${where} ${op} "/>
	</c:if>

	<c:set var="where" value="${cond} (id_author = ${fn:substring(p_authors_catalog, 4, -1)})"/>
</c:if>


<c:choose>
<c:when test="${!empty where}">
		<c:catch var="error">

			<sql:query var="rs" dataSource="jdbc/qmrf_documents">
				SELECT count(idqmrf) as c FROM documents left join doc_endpoint using (idqmrf) left join doc_algorithms using (idqmrf) left join doc_authors using (idqmrf) left join doc_software using (idqmrf) left join catalog_software as s using (id_software) left join catalog_endpoints as e using (id_endpoint) left join catalog_algorithms as a using (id_algorithm) left join catalog_authors as r using (id_author) where status = 'published' and (${where})
			</sql:query>
			<c:forEach var="row" items="${rs.rows}">
				<c:set var="maxpages" scope="session">
				<fmt:formatNumber type="number" value="${(row.c / sessionScope.pagesize) +0.5}" pattern="###"/>
				</c:set>
				<c:if test="${sessionScope.page > maxpages}">
					<c:set var="page" value="0" scope="session"  />
					<c:set var="startrecord" value="0" />
				</c:if>
			</c:forEach>
		</c:catch>
		<c:if test="${!empty error}">
			${error}
			<c:set var="maxpages" scope="session" value="10"/>
		</c:if>
		<c:set var="sql" value="SELECT idqmrf,qmrf_number,qmrf_title,date_format(updated,'${sessionScope.dateformat}') as lastdate,id_endpoint,id_algorithm,id_author,id_software,s.name as software,e.name as endpoint,a.definition as alg,r.name as author FROM documents left join doc_endpoint using (idqmrf) left join doc_algorithms using (idqmrf) left join doc_authors using (idqmrf) left join doc_software using (idqmrf) left join catalog_software as s using (id_software) left join catalog_endpoints as e using (id_endpoint) left join catalog_algorithms as a using (id_algorithm) left join catalog_authors as r using (id_author) where status = 'published' and (${where}) limit ${startrecord},${sessionScope.pagesize}"/>

		<table width="95%">
		<jsp:include page="records.jsp" flush="true">
		    <jsp:param name="sql" value="${sql}"/>

				<jsp:param name="qmrf_number" value="QMRF#"/>
				<jsp:param name="qmrf_title" value="Title"/>				
		    	<jsp:param name="lastdate" value="Last updated"/>
				<jsp:param name="endpoint" value="Endpoint"/>
				<jsp:param name="alg" value="Algorithm"/>
				<jsp:param name="software" value="Software"/>
				<jsp:param name="author" value="Author"/>
				<jsp:param name="actions" value=""/>

				<jsp:param name="paging" value="true"/>
				<jsp:param name="viewpage" value="search_catalogs.jsp"/>
				<jsp:param name="page" value="${startpage}"/>
				<jsp:param name="pagesize" value="${sessionScope.pagesize}"/>
				<jsp:param name="viewmode" value="html"/>
		</jsp:include>
		</table>

		<jsp:include page="view.jsp" flush="true">
		<jsp:param name="highlighted" value="${param.id}"/>
		</jsp:include>

</c:when>
<c:otherwise>
		<div class="error">
		No condition defined! At least one of <b>QMRF No text</b>,<b>Free text</b>,<b>Endpoint</b>,<b>Algorithm</b>, <b>Software</b> or <b>Author</b> has to be selected!
	</div>
</c:otherwise>
</c:choose>

</form>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	</p>
</div>
</body>
</html>

