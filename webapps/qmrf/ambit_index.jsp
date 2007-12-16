<%@ taglib uri="http://java.sun.com/jsp/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="thispage" value='ambit_index.jsp'/>

<jsp:include page="query_settings.jsp" flush="true"/>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN"
        "http://www.w3.org/TR/html4/strict.dtd">
<html lang="en">
<link href="styles/search.css" rel="stylesheet" type="text/css">
<head>
	<title>QMRF database (advanced query)</title>
	<meta name="description" content="">
	<meta name="copyright" content="Copyright (c) 2005-2007 Nina Jeliazkova">
	<meta name="author" content="Nina Jeliazkova">
<SCRIPT>
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
<body onload="load()">

<jsp:include page="menu.jsp" flush="true">
    <jsp:param name="highlighted" value="search"/>
</jsp:include>




<form action="search_tags.jsp"  name="searchForm" method="post"  onSubmit="return getMol()" border=1 bordercolor="#FF0000">
<div  id="search" >
		Advanced query in QMRF database
		<a href="help.html" target="_blank" nobr>Help</a>
</div>


<jsp:include page="menuall.jsp" flush="true">
    <jsp:param name="highlighted" value="search"/>
</jsp:include>

<jsp:include page="submenu.jsp" flush="true">
	<jsp:param name="highlighted" value="search_catalogs.jsp" />
</jsp:include>


<div id="wrap" style="text-align:center">

	<div id="main">
		<h4>Draw structure</h4>
		<applet code="org.openscience.cdk.applications.jchempaint.applet.JChemPaintEditorApplet"
  	   	  archive="applets/jchempaint-applet-2.2.1/jchempaint-applet-core.jar"	name="JCPApplet" width="500" height="400">
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


	</div>
	<div id="options">
		<h4>Structure search options</h4>

		<div id="suboptions">
			<input type="radio" name="similarity" value="exact">Exact structure<br>

				<input type="radio" name="similarity" value="fingerprint" checked="true">Similarity
				<label for="threshold">Tanimoto distance >=</label>
				<select name="threshold">
		  		<option value ="1.0">1.0</option>
					<option value ="0.9">0.9</option>
		  		<option value ="0.8">0.8</option>
					<option value ="0.7">0.7</option>
		  		<option value ="0.6">0.6</option>
					<option value ="0.5" selected="true">0.5</option>
					<option value ="0.4">0.4</option>
		  		<option value ="0.3">0.3</option>
					<option value ="0.2">0.2</option>
					<option value ="0.1">0.1</option>
				</select>

			<br>
	</div>
		<h4>Identification</h4>
		<div id="suboptions">
			<label for="cas">CAS Registry Number: </label>
			<input type="text" name="cas"  size="14" MAXLENGTH="14"/>
			<label for="formula">Formula</label>
			<input type="text" name="formula"  size="40" MAXLENGTH="40"/>

			<label for="name">Chemical name:</label>
			<input type="checkbox" name="soundsLike" UNCHECKED />Sounds like
			<input type="text" name="name"  size="40" MAXLENGTH="40"/>
			<label for="identifier">Alias</label>
			<input type="checkbox" name="soundsLikealias" UNCHECKED />Sounds like
			<input type="text" name="identifier"  size="40" MAXLENGTH="40"/>

			<label for="smiles">SMILES: </label>
			<input type="text" name="smiles"  size="40" MAXLENGTH="40"/>

		</div>

		<h4>Results appearance</h4>
		<div id="suboptions">

			<input type="hidden" name="page" value="0" size="5" MAXLENGTH="4">


			<label for="pagesize">Structures per page</label>
			<select name="pagesize">
		  		<option value ="5">5</option>
					<option value ="10" selected="true">10</option>
		  		<option value ="25">25</option>
					<option value ="50">50</option>
		  		<option value ="100">100</option>
				</select>

			<input type="hidden" name="mol" value="" />
			<br>
		</div>


	</div>
	<h4></h4>
	<div id="footer">
		<p><input type="submit" value="Search"/></p>
	</div>
</div>

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