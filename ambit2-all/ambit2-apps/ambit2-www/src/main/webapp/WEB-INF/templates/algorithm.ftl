<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<style  type="text/css">
.ui-autocomplete {
  max-height: 200px;
  overflow-y: auto;
  /* prevent horizontal scrollbar */
  overflow-x: auto;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
  height: 200px;
}
.ui-autocomplete-category {
    font-weight: bold;
    padding: .2em .4em;
    margin: .8em 0 .2em;
    line-height: 1.5;
</style>

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>

<#if algid??>
	<script type='text/javascript'>
		var algorithm = readAlgorithm("${ambit_root}","${ambit_request_json}");
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = defineAlgorithmTable("${ambit_root}","${ambit_request_json}",[false,true,true,true,true,true,true]);
	});
	</script>
</#if>

<script type='text/javascript'>

$(document)
	.ready(function() {
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/algorithm" title="Algorithms">Algorithms</a></li>');
	    <#if algid??>
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/algorithm/${algid}" title="Algorithms">${algid}</a></li>');
	    </#if>
		jQuery("#breadCrumb").jBreadCrumb();
 		loadHelp("${ambit_root}","algorithm");
	
		datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);
		featureAutocomplete(".featureuri",".dataseturi","${ambit_root}/feature",10);
		algorithmAutocomplete(".alguri","${ambit_root}/algorithm","Supervised",100);
		algorithmAutocomplete(".descuri","${ambit_root}/algorithm","DescriptorCalculation",100);
		modelAutocomplete(".modeluri","${ambit_root}/model",100);
		loadHelp("${ambit_root}","algorithm");
		downloadForm("${ambit_request}");
				
		var purl = $.url();
		$('#dataset_uri').attr('value',purl.param('dataset_uri')===undefined?'':purl.param('dataset_uri'));
		$('#model_uri').attr('value',purl.param('model_uri')===undefined?'':purl.param('model_uri'));
	}
);
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="one column" style="padding:0 2px 2px 2px 0;margin-right:0;" >&nbsp;</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >

		<!-- Page Content
		================================================== -->
		<#if algid??>
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top">Algorithm at <a href='${ambit_root}/algorithm/${algid}'>${ambit_root}/algorithm/${algid}</a></div>
			<div class="ui-widget-content ui-corner-bottom">
				<div style="margin:5px;padding:5px;">	
				<form action="${ambit_root}/algorithm/${algid}" id="runAlgorithm"  method="POST">	
					<#include "/algorithm_one.ftl">
				</form>
				</div>
			</div>
		<#else>
		<div class="row remove-bottom" style="padding:0;" >
		
			<table id='algorithm'  class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th></th>
			<th>Name</th>
			<th>Endpoint <a href='#' class='chelp hendpoint'>?</a></th>
			<th>Description</th>
			<th>Type<a href='#' class='chelp halgtypes'>?</a></th>
			<th>Models <a href='#' class='chelp hmodel'>?</a></th>			
			<th>Implementation of <a href='#' class='chelp himpl'>?</a></th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>


</div>

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >


	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>
	
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
	<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>	
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
