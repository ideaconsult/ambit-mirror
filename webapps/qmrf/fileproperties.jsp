<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/ambit" prefix="a" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:include page="header.jsp" flush="true"/>

<div style="text-align:left;">
<h3>File properties test</h3>
</div>

<a:fileproperties params="params" filename="/var/lib/repdose/EPAFHM_v2a_617_1Mar05.sdf"/>
	<table width="65%">
		<tr>
			<th></th>
			<th>CAS RN</th>
			<th>SMILES</th>
			<th>Chemical name</th>
			<th>INChI</th>
			<th>Descriptor</th>
			<th>Experimental data</th>
			<th>Other</th>
		</tr>
<c:forEach var="name" items="${params}">
	<tr>
		<td width="15%">
		${name.key}
		</td>
		<!--
		<td width="15%">
		${name.value}
	</td>
	-->
		<td width="5%">
		<input type="radio" name="${name.key}" value="CasRN">
	</td>
		<td width="5%">
		<input type="radio" name="${name.key}" value="SMILES">
	</td>
		<td width="5%">
		<input type="radio" name="${name.key}" value="Names">
	</td>
		<td width="5%">
		<input type="radio" name="${name.key}" value="INChI">
	</td>
		<td width="5%">
		<input type="radio" name="${name.key}" value="descriptor">
		</td>
		<td width="5%">
		<input type="radio" name="${name.key}" value="experimental">
		</td>
		<td width="15%">
		<input type="radio" name="${name.key}" value="other">
		<input type="text" name="${name.key}_text" value="other">
		</td>
	</tr>
</c:forEach>
</table>


<jsp:include page="footer.jsp" flush="true"/>
