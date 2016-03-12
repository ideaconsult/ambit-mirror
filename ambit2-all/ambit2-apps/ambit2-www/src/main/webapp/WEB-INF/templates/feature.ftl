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
		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/feature'><b>Feature name</b> <input name='search' class='search' value='' id='search' title='Feature search'> <input type='submit' value='Search'></form>");
		
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


	
<div class="fourteen columns remove-bottom" style="padding:0;" >

		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >
			<table id='feature'  class='jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
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


<div class="two columns remove-bottom" style="padding:0;" >

	<div class='row remove-bottom' id='download' style='background: #F2F0E6;margin: 3px; padding: 0em; font-size: 1em; '>
		<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
		<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.png' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
		<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.png' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
		<a href='#' id='jsonld'><img src='${ambit_root}/images/json-ld.png' alt='JSON-LD' title='Download as JSON-LD'></a>
		<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>



<!-- help-->	
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>

</div>

<div class='row add-bottom' style="height:140px;">&nbsp;</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
