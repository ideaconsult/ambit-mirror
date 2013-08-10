<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-substance.js'></script>

<#if substanceid??>
	<script type='text/javascript'>
		var algorithm = substance.readComposition("${ambit_root}","${ambit_request_json}");
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = substance.defineCompositionTable("${ambit_root}","${ambit_request_json}","#composition",true,null);
	  	loadHelp("${ambit_root}","substance");
	  	$( "#selectable" ).selectable( "option", "distance", 18);
	  	downloadForm("${ambit_request}");
	});
	</script>
</#if>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/substance' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="remove-bottom h3">
					Substance composition
			</div>
		    <div class='help'>
		     Mono-constituent, multiconstituent, additives, impurities.<br/>
 <a href='?search=' title='List all substances'>All</a>
<a href='?search=^0' title='Search for substances with name staring with 0'>0</a>&nbsp;<a href='?search=^1' title='Search for substances with name staring with 1'>1</a>&nbsp;<a href='?search=^2' title='Search for substances with name staring with 2'>2</a>&nbsp;<a href='?search=^3' title='Search for substances with name staring with 3'>3</a>&nbsp;<a href='?search=^4' title='Search for substances with name staring with 4'>4</a>&nbsp;<a href='?search=^5' title='Search for substances with name staring with 5'>5</a>&nbsp;<a href='?search=^6' title='Search for substances with name staring with 6'>6</a>&nbsp;<a href='?search=^7' title='Search for substances with name staring with 7'>7</a>&nbsp;<a href='?search=^8' title='Search for substances with name staring with 8'>8</a>&nbsp;<a href='?search=^9' title='Search for substances with name staring with 9'>9</a>&nbsp;|&nbsp;
		    </div>			
		</div>
		<div class="four columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input type='text'  id='search' name='search' value='' tabindex='1' >
		    </div>			
		</div>		
		<div class="two columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		    </div>			
		</div>	
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

</form>
<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<!-- Not supported yet
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	-->
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >
			<table id='composition' class='compositiontable' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Type</th>
			<th>Name</th>
			<th>EC No.</th>
			<th>CAS No.</th>
			<th>Typical concentration</th>
			<th>Real concentration</th>
			<th>Other related substances</th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
	
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>


<#include "/chelp.ftl" >

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
