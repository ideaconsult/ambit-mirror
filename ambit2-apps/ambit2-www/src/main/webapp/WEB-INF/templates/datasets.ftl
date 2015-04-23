<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
	
	$(document).ready(function() {
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/dataset" title="Datasets">Chemical structures datasets</a></li>');

		downloadForm("${ambit_request}");
		
		<#if datasetid??>
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/dataset/${datasetid}" title="${ambit_root}/dataset/${datasetid}">/dataset/${datasetid}</a></li>');
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/dataset/${datasetid}/metadata" title="${ambit_root}/dataset/${datasetid}/metadata">Metadata</a></li>');
			loadDatasetMeta("${ambit_root}","${ambit_root}/dataset/${datasetid}/metadata",true);
		<#else>
			<#if openam_token??>
	  			var oTable = defineDatasetsTable("${ambit_root}","${ambit_request_json}",true,true);
	  		<#else>
				var oTable = defineDatasetsTable("${ambit_root}","${ambit_request_json}",true,false);
			</#if>
	  	</#if>
		jQuery("#breadCrumb").jBreadCrumb();
		jQuery("#welcome").text("Dataset");
		loadHelp("${ambit_root}","dataset");
	});
</script>


</head>
<body>


<div class="container" style="margin:0;padding:0;">


<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/dataset' style='padding:0;margin:0;'>

<div class='row remove-bottom' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Search for datasets by name'>Search for datasets by name</span> <a href='#' class='chelp hname'>?</a>
		    	<input type='text'  id='search' name='search' class='remove-bottom'' value='' tabindex='1' >
		    	<input class='ambit_search remove-bottom' id='submit' type='submit' value='Search' tabindex='2'>
</div>

<div class='row remove-bottom' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Search for datasets by endpoints'>Search for datasets</span> 
<a href='${ambit_root}/query/ndatasets_endpoint?feature_sameas=http%3A%2F%2Fwww.opentox.org%2FechaEndpoints.owl&condition=startswith' title='List datasets by endpoints'>by endpoints</a>
<a href='#' class='chelp hendpoint'>?</a>
</div>

<div class='row remove-bottom' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Search for datasets with name staring with'>Search for datasets with name staring with</span>
<a href='?search=' title='List all datasets'>All</a>| <a href='?search=^A' title='Search for datasets with name staring with A'>A</a> <a href='?search=^B' title='Search for datasets with name staring with B'>B</a> <a href='?search=^C' title='Search for datasets with name staring with C'>C</a> <a href='?search=^D' title='Search for datasets with name staring with D'>D</a> <a href='?search=^E' title='Search for datasets with name staring with E'>E</a> <a href='?search=^F' title='Search for datasets with name staring with F'>F</a> <a href='?search=^G' title='Search for datasets with name staring with G'>G</a> <a href='?search=^H' title='Search for datasets with name staring with H'>H</a> <a href='?search=^I' title='Search for datasets with name staring with I'>I</a> <a href='?search=^J' title='Search for datasets with name staring with J'>J</a> <a href='?search=^K' title='Search for datasets with name staring with K'>K</a> <a href='?search=^L' title='Search for datasets with name staring with L'>L</a> <a href='?search=^M' title='Search for datasets with name staring with M'>M</a> <a href='?search=^N' title='Search for datasets with name staring with N'>N</a> <a href='?search=^O' title='Search for datasets with name staring with O'>O</a> <a href='?search=^P' title='Search for datasets with name staring with P'>P</a> <a href='?search=^Q' title='Search for datasets with name staring with Q'>Q</a> <a href='?search=^R' title='Search for datasets with name staring with R'>R</a> <a href='?search=^S' title='Search for datasets with name staring with S'>S</a> <a href='?search=^T' title='Search for datasets with name staring with T'>T</a> <a href='?search=^U' title='Search for datasets with name staring with U'>U</a> <a href='?search=^V' title='Search for datasets with name staring with V'>V</a> <a href='?search=^W' title='Search for datasets with name staring with W'>W</a> <a href='?search=^X' title='Search for datasets with name staring with X'>X</a> <a href='?search=^Y' title='Search for datasets with name staring with Y'>Y</a> <a href='?search=^Z' title='Search for datasets with name staring with Z'>Z</a> | <a href='?search=^a' title='Search for datasets with name staring with a'>a</a> <a href='?search=^b' title='Search for datasets with name staring with b'>b</a> <a href='?search=^c' title='Search for datasets with name staring with c'>c</a> <a href='?search=^d' title='Search for datasets with name staring with d'>d</a> <a href='?search=^e' title='Search for datasets with name staring with e'>e</a> <a href='?search=^f' title='Search for datasets with name staring with f'>f</a> <a href='?search=^g' title='Search for datasets with name staring with g'>g</a> <a href='?search=^h' title='Search for datasets with name staring with h'>h</a> <a href='?search=^i' title='Search for datasets with name staring with i'>i</a> <a href='?search=^j' title='Search for datasets with name staring with j'>j</a> <a href='?search=^k' title='Search for datasets with name staring with k'>k</a> <a href='?search=^l' title='Search for datasets with name staring with l'>l</a> <a href='?search=^m' title='Search for datasets with name staring with m'>m</a> <a href='?search=^n' title='Search for datasets with name staring with n'>n</a> <a href='?search=^o' title='Search for datasets with name staring with o'>o</a> <a href='?search=^p' title='Search for datasets with name staring with p'>p</a> <a href='?search=^q' title='Search for datasets with name staring with q'>q</a> <a href='?search=^r' title='Search for datasets with name staring with r'>r</a> <a href='?search=^s' title='Search for datasets with name staring with s'>s</a> <a href='?search=^t' title='Search for datasets with name staring with t'>t</a> <a href='?search=^u' title='Search for datasets with name staring with u'>u</a> <a href='?search=^v' title='Search for datasets with name staring with v'>v</a> <a href='?search=^w' title='Search for datasets with name staring with w'>w</a> <a href='?search=^x' title='Search for datasets with name staring with x'>x</a> <a href='?search=^y' title='Search for datasets with name staring with y'>y</a> <a href='?search=^z' title='Search for datasets with name staring with z'>z</a>
<a href='?search=^0' title='Search for datasets with name staring with 0'>0</a> <a href='?search=^1' title='Search for datasets with name staring with 1'>1</a> <a href='?search=^2' title='Search for datasets with name staring with 2'>2</a> <a href='?search=^3' title='Search for datasets with name staring with 3'>3</a> <a href='?search=^4' title='Search for datasets with name staring with 4'>4</a> <a href='?search=^5' title='Search for datasets with name staring with 5'>5</a> <a href='?search=^6' title='Search for datasets with name staring with 6'>6</a> <a href='?search=^7' title='Search for datasets with name staring with 7'>7</a> <a href='?search=^8' title='Search for datasets with name staring with 8'>8</a> <a href='?search=^9' title='Search for datasets with name staring with 9'>9</a>
</div>

</form>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list '></a>
<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
</div>

<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
</div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
</div>	

</div>


<div class="thirteen columns remove-bottom" style="padding:0;" >

		<!-- Page Content
		================================================== -->
		<#if datasetid??>
		<div class="row" style="padding:0;" >			
			<div class="row remove-bottom ui-widget-header ui-corner-top">
			Dataset at <a href='${ambit_root}/dataset/${datasetid}'>${ambit_root}/dataset/${datasetid}</a>
			</div>
			
			<#include "/dataset_one.ftl">
			
		<#else>
 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>
		
		<div class="row " style="padding:0;" >
			<table id='datasets' class='datasetstable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th><a href='#' class='chelp hsearch'>?</a></th>
			<th><span class='ui-icon ui-icon-star' style='float: left;' title='Star rating'></span> </th>
			<th>Name</th>
			<th>Actions <a href='#' class='chelp hactions'>?</a></th>
			<th>Download <a href='#' class='chelp download'>?</a></th>
			<th title='Remove the dataset. Only datasets with star rating <= 5 can be deleted!'> <a href='#' class='chelp hdelete'>?</a></th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		



<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
