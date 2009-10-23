<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml"  prefix="x" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<link href="styles/nstyle.css" rel="stylesheet" type="text/css">
<meta name="description" content="REPDOSE database">
<meta name="keywords" content="ambit,qsar,structure search">
<meta name="robots"content="index,follow">
<META NAME="GOOGLEBOT" CONTENT="index,FOLLOW">
<meta name="copyright" content="Copyright 2008. Ideaconsult Ltd. nina@acad.bg">
<meta name="author" content="Nina Jeliazkova">
<meta name="language" content="English">
<meta name="revisit-after" content="7">
<link rel="SHORTCUT ICON" href="favicon.ico">
<title>Search substances in REPDOSE</title>
<SCRIPT type="text/javascript">
function clearFields(){
   document.identifiersForm.search_value = "";
   
}
function getSimilarityMol(){
   document.similarityForm.mol.value = escape(document.JCPApplet.getMolFile());
   return document.similarityForm.mol.value;
   }
function getStrucMol(){
   document.strucForm.mol.value = escape(document.JCPApplet.getMolFile());
   return document.strucForm.mol.value;
   }
   
function load(){
	 document.strucForm.mol.value = "";
	 document.similarityForm.mol.value = "";
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

<jsp:include page="header.jsp" flush="true">
    <jsp:param name="highlighted" value="structures"/>
    <jsp:param name="viewmode" value="${param.viewmode}"/>
</jsp:include>

<c:set var="op" value="${param.operation}" />
<c:if test="${empty param.operation}">
	<c:set var="op" value="AND"/>
</c:if>

<!-- end initialization -->

<c:if test="${!empty sessionScope.error}">
	<div class="success">
	${sessionScope.error}
	</div>
	<c:set var="error" value="" scope="session"/>
</c:if>


<c:set var="headercolor" value="#BBBBBB"/>

<table bgcolor="#FFFFFF" width="95%" border="0">


<form action="search_tags.jsp"  name="identifiersForm" method="post" onReset="return clearFields()">

<tr bgcolor="${querycolor}">
<th bgcolor="${headercolor}" colspan="3">Identification</th>
<td colspan="1" align="right"><i>Results appearance</i>
	<label for="pagesize">Structures per page</label>
	<select name="pagesize" id="pagesize">
			  		<option value ="5">5</option>
						<option value ="10" selected="SELECTED">10</option>
			  		<option value ="25">25</option>
						<option value ="50">50</option>
			  		<option value ="100">100</option>
					</select>
	<input type="hidden" name="page" value="0" size="5" MAXLENGTH="4">

</td>

</tr>

<tr bgcolor="${querycolor}">
<td align="left">
		<c:set var="criteria">CAS Registry number,Formula,Chemical name,Alias</c:set>
		<select name="search_criteria" id="search_criteria">
			<c:forTokens var="t" items="${criteria}" delims=",">
				<c:set	var="checked" value=""/>
				<c:if test="${t eq sessionScope.search_criteria}">
					<c:set	var="checked" value="selected"/>
				</c:if>
	  			<option value ="${t}" ${checked}>${t}</option>
	  		</c:forTokens>
		</select>
</td>
<td>
	<input type="text" id="search_value" name="search_value" value="${sessionScope.search_value}" size="30" MAXLENGTH="40">
</td>
<td align="right">
	<input type="submit" value="Search">
	<a href="help.jsp?anchor=search_substructures" target="help"><img src="images/help.png" alt="help" title="How to search substructures in REPDOSE?" border="0"></a>	
</td>

</form>

<td align="right">
</td>
</tr>

<!-- Structure search -->
<tr bgcolor="${headercolor}">
<th colspan="3"><i>Structure</i></th>

<th bgcolor="#FFFFFF" rowspan="10" width="440px">
	<applet code="org.openscience.cdk.applications.jchempaint.applet.JChemPaintEditorApplet"
  	   	  archive="applets/jchempaint-applet-2.2.1/jchempaint-applet-core.jar"	name="JCPApplet" width="440" height="300">
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

<tr bgcolor="${querycolor}">
<form action="search_tags.jsp"  name="strucForm" method="post"  onSubmit="return getStrucMol()" onReset="return clearFields()">
<td align="left">
<select name="source" id="source">
	<option value ="smiles">SMILES</option>
	<option value ="mol" selected="YES">Structure diagram</option>	
</select>
</td>

<td >
<input type="text" id="smiles" name="smiles"  value="${sessionScope.smiles}" size="30" MAXLENGTH="40">
<input type="hidden" id="mol" value="" name="mol" >
<input type="hidden" id="search_mode" value="structure" name="search_mode"> 
</td>
<td align="right">
<input type="submit" value="Search">
<a href="help.jsp?anchor=search_structures" target="help"><img src="images/help.png" alt="help" title="How to search structures in REPDOSE?" border="0"></a>
</td>

</form>

</tr>
<tr bgcolor="${headercolor}">
<th colspan="1"><i>Similarity</i></th>
<td colspan="2" align="left">
</td>
</tr>

<tr bgcolor="${querycolor}">

<form action="search_tags.jsp"  name="similarityForm" method="post"  onSubmit="return getSimilarityMol()" onReset="return clearFields()">
<td align="left">
<select name="source" id="source">
	<option value ="smiles">SMILES</option>
	<option value ="mol" selected="YES">Structure diagram</option>	
</select>
</td>

<td colspan="1">
	<input type="text" id="smiles" name="smiles"  value="${sessionScope.smiles}" size="30" MAXLENGTH="40">
	<input type="hidden" name="mol" value="" >
	<input type="hidden" id="search_mode" value="fingerprint" name="search_mode">	
</td>
<td align="right">
<input type="submit" value="Search">
<a href="help.jsp?anchor=search_similarity" target="help"><img src="images/help.png" alt="help" title="How to search similar structures in REPDOSE?" border="0"></a>
</td>
</tr>
<tr>
<td>
<label for="threshold">Tanimoto distance >=</label>
</td>

<td colspan="2">
				<c:set var="thresholds">1.0,0.9,0.8,0.7,0.6,0.5,0.4,0.3,0.2,0.1</c:set>
				<c:if test="${empty sessionScope.threshold}">
					<c:set var="threshold" value="0.5" scope="session"/>
				</c:if>

				<select name="threshold" id="threshold">
					<c:forTokens var="t" items="${thresholds}" delims=",">
						<c:set	var="checked" value=""/>
						<c:if test="${t eq sessionScope.threshold}">
							<c:set	var="checked" value="selected"/>
						</c:if>

		  			<option value ="${t}" ${checked}>${t}</option>
		  		</c:forTokens>
				</select>
</td>
</form>
</tr>


<tr bgcolor="${headercolor}">
<th colspan="3"><i>Substructure</i></th>
</tr>

<form action="search_smarts.jsp"  name="smartsForm" method="post"  border=1 bordercolor="#FF0000">
<tr bgcolor="${querycolor}">
<td align="left"><input type="radio" name="struc_source" value="predefined" checked="yes">Predefined list</td>
<td colspan="2">
		<c:catch var="error">
			<c:import var="xml" url="funcgroups.xml"/>
			<c:import var="xsl" url="/WEB-INF/xslt/funcgroups2form.xsl"/>
			<x:transform xml="${fn:trim(xml)}" xslt="${xsl}">
				<x:param name="selected" value="${param.smarts}"/>
			</x:transform>
		</c:catch>
	${error}
</td>

</tr>

<tr bgcolor="${querycolor}">
<td align="left"><input type="radio" name="struc_source" value="smarts">Specify by SMARTS</td>
<td colspan="1">
<input type="text" name="smarts" value="${param.smarts}" size="30" MAXLENGTH="120"/>
</td>

<td align="right">
<input type="submit" value="Search">
<a href="help.jsp?anchor=search_substructures" target="help"><img src="images/help.png" alt="help" title="How to search substructures in REPDOSE?" border="0"></a>
</td>
</tr>
</form>
</table>
</body>
</html>


