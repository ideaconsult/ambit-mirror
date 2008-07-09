<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html> 

<head>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<meta name="description" content="(Q)MRF database">
<meta name="keywords" content="ambit,qsar,qmrf,structure search">
<meta name="robots"content="index,follow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2007. Ideaconsult Ltd. nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link rel="SHORTCUT ICON" href="favicon.ico">
<title>Search substances in QMRF Database</title>
<SCRIPT type="text/javascript">
function clearFields(){
   document.searchForm.cas.value = "";

   }
function getMol(){
   document.searchForm.mol.value = escape(document.JCPApplet.getMolFile());
   return document.searchForm.mol.value;
   }
function load(){
	 document.searchForm.mol.value = "";
   //document.JCPApplet.setMolFile(null);
   window.status="Page is loaded"
   }
</SCRIPT>

</head>
<body onload="load()" bgcolor="#FFFFFF">

<jsp:include page="query_settings.jsp" flush="true"/>
<c:choose>
	<c:when test="${empty param.pagesize}">
  	<c:set var="pagesize" value="2"/>
  </c:when>
  <c:otherwise>
  		<c:set var="pagesize" value="${param.pagesize}"/>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty param.page}">
  	<c:set var="startpage" value="0"/>
  	<c:set var="startrecord" value="0"/>
  </c:when>
  <c:otherwise>
  		<c:set var="startpage" value="${param.page}"/>
			<c:set var="startrecord" value="${param.page * param.pagesize}"/>
	</c:otherwise>
</c:choose>

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="structures"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:set var="thispage" value="search_substances.jsp"/>

<c:set var="op" value="${param.operation}" />
<c:if test="${empty param.operation}">
	<c:set var="op" value="AND"/>
</c:if>

<!-- end initialization -->

<form action="search_tags.jsp"  name="searchForm" method="post"  onSubmit="return getMol()" onReset="return clearFields()">

<table bgcolor="#FFFFFF" width="90%" border="0">


<tr bgcolor="${querycolor}">
<th bgcolor="${headercolor}" colspan="2">Search mode</th>
<th bgcolor="${headercolor}">Draw structure</th>
</tr>

<tr bgcolor="${querycolor}">
<td colspan="2">

<c:set	var="checked_exact" value="checked"/>
<c:set	var="checked_fp" value=""/>

<c:if test="${!empty sessionScope.search_similarity}" >
		<c:if test="${sessionScope.search_similarity eq 'fingerprint'}" >
			<c:set	var="checked_exact" value=""/>
			<c:set	var="checked_fp" value="checked"/>
		</c:if>
</c:if>


<input type="radio" name="similarity" value="exact" ${checked_exact}>Exact structure
<input type="radio" name="similarity" value="fingerprint" ${checked_fp}>Similarity
<label for="threshold">Tanimoto distance >=</label>

				<c:set var="thresholds">1.0,0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1</c:set>
				<c:if test="${empty sessionScope.search_threshold}">
					<c:set var="search_threshold" value="0.5" scope="session"/>
				</c:if>

				<select name="threshold" id="threshold">
					<c:forTokens var="t" items="${thresholds}" delims=",">
						<c:set	var="checked" value=""/>
						<c:if test="${t eq sessionScope.search_threshold}">
							<c:set	var="checked" value="selected"/>
						</c:if>

		  			<option value ="${t}" ${checked}>${t}</option>
		  		</c:forTokens>
				</select></td>
<th bgcolor="#FFFFFF" rowspan="10" width="400px">
	<applet code="org.openscience.cdk.applications.jchempaint.applet.JChemPaintEditorApplet"
  	   	  archive="applets/jchempaint-applet-2.2.1/jchempaint-applet-core.jar"	name="JCPApplet" width="400" height="300">
	  <PARAM NAME=background VALUE="15658734">  <!-- #EEEEEE -->
	  <PARAM NAME=atomNumbersVisible VALUE="false">
	  <PARAM NAME=compact VALUE="false">
	  <PARAM NAME=tooltips VALUE="text">
	  <PARAM NAME=impliciths VALUE="true">
		<!--
{ "background", "color", 	"Background color as integer" },
			{ "atomNumbersVisible", "true or false", "should atom numbers be shown"},
			{ "load", "url", "URL of the chemical data" },
			{ "compact", "true or false", "compact means elements shown as dots, no figures etc. (default false)"},
			{ "tooltops", "string like 'atomumber|test|atomnumber|text", "the texts will be used as tooltips for the respective atoms (leave out if none required"},
			{ "impliciths", "true or false", "the implicit hs will be added from start (default false)"},
			{ "spectrumRenderer", "string", "name of a spectrum applet (see subproject in NMRShiftDB) where peaks should be highlighted when hovering over atom"},
			{ "hightlightTable", "true or false", "if true peaks in a table will be highlighted when hovering over atom, ids are assumed to be tableid$atomnumber (default false)"},
		-->
		<a href="http://almost.cubic.uni-koeln.de/cdk/jcp" target=_blank>JChemPaint</a> structure diagram editor . If you don't see Java applet here, check your Java browse settings.
		</applet>
</th>
</tr>

<tr bgcolor="${headercolor}">
<th colspan="2">Identification</th>
</tr>

<tr bgcolor="${querycolor}">
<th><label for="cas">CAS Registry Number: </label></th>
<td><input type="text" id="cas" name="cas" value="${sessionScope.search_cas}" size="14" MAXLENGTH="14"></td>
</tr>

<tr bgcolor="${querycolor}">
<th><label for="formula">Formula</label></th>
<td><input type="text" id="formula" name="formula"  value="${search_formula}" size="40" MAXLENGTH="40"></td>
</tr>

<tr bgcolor="${querycolor}">
<th><label for="name">Chemical name:</label></th>
<td><input type="text" id="name" name="name"  value="${search_name}" size="40" MAXLENGTH="40">
	<br>
	<input type="checkbox" name="soundsLike" >Sounds like</td>
</tr>

<tr bgcolor="${querycolor}">
<th><label for="identifier">Alias</label></th>
<td><input type="text" id="identifier" name="identifier"  value="${search_identifier}" size="40" MAXLENGTH="40">
	<br>
	<input type="checkbox" name="soundsLikealias">Sounds like
	</td>
</tr>

<tr bgcolor="${querycolor}">
<th><label for="smiles">SMILES: </label></th>
<td><input type="text" id="smiles" name="smiles"  value="${search_smiles}" size="40" MAXLENGTH="40"></td>
</tr>


<tr bgcolor="${headercolor}">
<th colspan="2">Results appearance
	<input type="hidden" name="page" value="0" size="5" MAXLENGTH="4">
	<input type="hidden" name="mol" value="" >
	</th>
</tr>

<tr bgcolor="${querycolor}">
<td><label for="pagesize">Structures per page</label></td>
<td><select name="pagesize" id="pagesize">
		  		<option value ="5">5</option>
					<option value ="10" selected="SELECTED">10</option>
		  		<option value ="25">25</option>
					<option value ="50">50</option>
		  		<option value ="100">100</option>
				</select></td>
</tr>

<tr bgcolor="${headercolor}">
<th ></th >
<th ><input type="submit" value="Search">
<a href="help.jsp?anchor=search_structures" target="help"><img src="images/help.png" alt="help" title="How to search structures in QMRF Database?" border="0"></a>
	</th>
</tr>


</table>

</form>

<div id="hits">
		<p>
		<jsp:include page="hits.jsp" flush="true">
    <jsp:param name="id" value=""/>
		</jsp:include>
	
</div>
</body>
</html>

