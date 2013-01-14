<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<#if algid??>
	<script type='text/javascript'>
		var algorithm = readDataset("${ambit_root}","${ambit_request_json}");
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = defineDatasetsTable("${ambit_root}","${ambit_request_json}");
	});
	</script>
</#if>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/dataset' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<h3 class="remove-bottom">
					Datasets: Chemical structures and properties
			</h3>
		    <div class='help'>
<a href='${ambit_root}/query/ndatasets_endpoint?feature_sameas=http%3A%2F%2Fwww.opentox.org%2FechaEndpoints.owl&condition=startswith' title='List datasets by endpoints'>Datasets by endpoints</a>
 <a href='?search=' title='List all datasets'>All</a>|&nbsp<a href='?search=^A' title='Search for datasets with name staring with A'>A</a>&nbsp<a href='?search=^B' title='Search for datasets with name staring with B'>B</a>&nbsp<a href='?search=^C' title='Search for datasets with name staring with C'>C</a>&nbsp<a href='?search=^D' title='Search for datasets with name staring with D'>D</a>&nbsp<a href='?search=^E' title='Search for datasets with name staring with E'>E</a>&nbsp<a href='?search=^F' title='Search for datasets with name staring with F'>F</a>&nbsp<a href='?search=^G' title='Search for datasets with name staring with G'>G</a>&nbsp<a href='?search=^H' title='Search for datasets with name staring with H'>H</a>&nbsp<a href='?search=^I' title='Search for datasets with name staring with I'>I</a>&nbsp<a href='?search=^J' title='Search for datasets with name staring with J'>J</a>&nbsp<a href='?search=^K' title='Search for datasets with name staring with K'>K</a>&nbsp<a href='?search=^L' title='Search for datasets with name staring with L'>L</a>&nbsp<a href='?search=^M' title='Search for datasets with name staring with M'>M</a>&nbsp<a href='?search=^N' title='Search for datasets with name staring with N'>N</a>&nbsp<a href='?search=^O' title='Search for datasets with name staring with O'>O</a>&nbsp<a href='?search=^P' title='Search for datasets with name staring with P'>P</a>&nbsp<a href='?search=^Q' title='Search for datasets with name staring with Q'>Q</a>&nbsp<a href='?search=^R' title='Search for datasets with name staring with R'>R</a>&nbsp<a href='?search=^S' title='Search for datasets with name staring with S'>S</a>&nbsp<a href='?search=^T' title='Search for datasets with name staring with T'>T</a>&nbsp<a href='?search=^U' title='Search for datasets with name staring with U'>U</a>&nbsp<a href='?search=^V' title='Search for datasets with name staring with V'>V</a>&nbsp<a href='?search=^W' title='Search for datasets with name staring with W'>W</a>&nbsp<a href='?search=^X' title='Search for datasets with name staring with X'>X</a>&nbsp<a href='?search=^Y' title='Search for datasets with name staring with Y'>Y</a>&nbsp<a href='?search=^Z' title='Search for datasets with name staring with Z'>Z</a>&nbsp|&nbsp;<a href='?search=^a' title='Search for datasets with name staring with a'>a</a>&nbsp<a href='?search=^b' title='Search for datasets with name staring with b'>b</a>&nbsp<a href='?search=^c' title='Search for datasets with name staring with c'>c</a>&nbsp<a href='?search=^d' title='Search for datasets with name staring with d'>d</a>&nbsp<a href='?search=^e' title='Search for datasets with name staring with e'>e</a>&nbsp<a href='?search=^f' title='Search for datasets with name staring with f'>f</a>&nbsp<a href='?search=^g' title='Search for datasets with name staring with g'>g</a>&nbsp<a href='?search=^h' title='Search for datasets with name staring with h'>h</a>&nbsp<a href='?search=^i' title='Search for datasets with name staring with i'>i</a>&nbsp<a href='?search=^j' title='Search for datasets with name staring with j'>j</a>&nbsp<a href='?search=^k' title='Search for datasets with name staring with k'>k</a>&nbsp<a href='?search=^l' title='Search for datasets with name staring with l'>l</a>&nbsp<a href='?search=^m' title='Search for datasets with name staring with m'>m</a>&nbsp<a href='?search=^n' title='Search for datasets with name staring with n'>n</a>&nbsp<a href='?search=^o' title='Search for datasets with name staring with o'>o</a>&nbsp<a href='?search=^p' title='Search for datasets with name staring with p'>p</a>&nbsp<a href='?search=^q' title='Search for datasets with name staring with q'>q</a>&nbsp<a href='?search=^r' title='Search for datasets with name staring with r'>r</a>&nbsp<a href='?search=^s' title='Search for datasets with name staring with s'>s</a>&nbsp<a href='?search=^t' title='Search for datasets with name staring with t'>t</a>&nbsp<a href='?search=^u' title='Search for datasets with name staring with u'>u</a>&nbsp<a href='?search=^v' title='Search for datasets with name staring with v'>v</a>&nbsp<a href='?search=^w' title='Search for datasets with name staring with w'>w</a>&nbsp<a href='?search=^x' title='Search for datasets with name staring with x'>x</a>&nbsp<a href='?search=^y' title='Search for datasets with name staring with y'>y</a>&nbsp<a href='?search=^z' title='Search for datasets with name staring with z'>z</a>&nbsp|&nbsp;
<a href='?search=^0' title='Search for datasets with name staring with 0'>0</a>&nbsp<a href='?search=^1' title='Search for datasets with name staring with 1'>1</a>&nbsp<a href='?search=^2' title='Search for datasets with name staring with 2'>2</a>&nbsp<a href='?search=^3' title='Search for datasets with name staring with 3'>3</a>&nbsp<a href='?search=^4' title='Search for datasets with name staring with 4'>4</a>&nbsp<a href='?search=^5' title='Search for datasets with name staring with 5'>5</a>&nbsp<a href='?search=^6' title='Search for datasets with name staring with 6'>6</a>&nbsp<a href='?search=^7' title='Search for datasets with name staring with 7'>7</a>&nbsp<a href='?search=^8' title='Search for datasets with name staring with 8'>8</a>&nbsp<a href='?search=^9' title='Search for datasets with name staring with 9'>9</a>&nbsp|&nbsp;
<!--
<a href='' title='Refresh this page'>Refresh</a><div><span class="center">
Page:<input name='page' type='text' title='Page' size='10' value='0'>
<b>Page size:</b><input name='pagesize' type='text' title='Page size' size='10' value='25'>
-->	    
		    </div>			
		</div>
		<div class="four columns omega">
			<h3 class="remove-bottom">
				&nbsp;
			</h3>
		    <h6>
		    	<input type='text'  id='search' name='search' value='' tabindex='1' >
		    </h6>			
		</div>		
		<div class="two columns omega">
			<h3 class="remove-bottom">
				&nbsp;
			</h3>
		    <h6>
		    	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		    </h6>			
		</div>	
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

</form>
<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">

</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
		<#if datasetid??>
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top">Dataset at <a href='${ambit_root}/dataset/${datasetid}'>${ambit_root}/dataset/${datasetid}</a></div>
			<div class="ui-widget-content ui-corner-bottom">
				<div style="margin:5px;padding:5px;">	
					<#include "/dataset_one.ftl">
				</div>
			</div>
		<#else>
		<div class="row" style="padding:0;" >
			<table id='datasets' class='datasetstable' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<th></th>
			<th><span class='ui-icon ui-icon-star' style='float: left;' title='Star rating'></span></th>
			<th>Name</th>
			<th>Download</th>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
