<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

<script type='text/javascript'>
	
$(document).ready(function() {
 	var oTable = defineFeatureTable("${ambit_root}","${ambit_request_json}");
});
</script>

<script type='text/javascript'>

$(document)
	.ready(function() {
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/features" title="AMBIT features">Features</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${ambit_request}">${ambit_request}</a></li>');
		jQuery("#breadCrumb").jBreadCrumb();
		jQuery("#welcome").html("Features (identifiers, measured and calculated properties)");		
		loadHelp("${ambit_root}","feature");
		downloadForm("${ambit_request}");
	}
);
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">


<#include "/banner_crumbs.ftl">

<div class="three columns remove-bottom" style="padding:0;" >

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
Feature search
</div>
<div class='row remove-bottom' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<form method='GET' name='searchform' id='searchform' action='${ambit_root}/feature' style='padding:0;margin:0;'>
<label forName='search' >Name</label>
<input type='text'  id='search' name='search' value='' tabindex='1' >
<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
</form>
</div>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
</div>

<!-- help-->	
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>

</div>
	
<div class="thirteen columns remove-bottom" style="padding:0;" >

 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>

		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >
			<table id='feature'  class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>ID</th>
			<th>Name <a href='#' class='chelp hwhat'>?</a></th>
			<th>Units</th>
			<th>Same As <a href='#' class='chelp hsameas'>?</a></th>
			<th>Origin <a href='#' class='chelp horigin'>?</a></th>
			<th>Values Type <a href='#' class='chelp hvaluestype'>?</a></th>
			<th>Nominal values <a href='#' class='chelp hnominal'>?</a></th>
			<th>More <a href='#' class='chelp hannotation'>?</a></th>			
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		</div>
		
</div>


		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>

</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
