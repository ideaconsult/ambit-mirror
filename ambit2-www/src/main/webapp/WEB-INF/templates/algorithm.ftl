<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

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
	  	var oTable = defineAlgorithmTable("${ambit_root}","${ambit_request_json}");
	});
	</script>
</#if>

<script type='text/javascript'>

$(document)
	.ready(function() {
		$( "#selectable" ).selectable( "option", "distance", 18);
		datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);
		featureAutocomplete(".featureuri",".dataseturi","${ambit_root}/feature",10);
		algorithmAutocomplete(".alguri","${ambit_root}/algorithm","Supervised",100);
		algorithmAutocomplete(".descuri","${ambit_root}/algorithm","DescriptorCalculation",100);
		modelAutocomplete(".modeluri","${ambit_root}/model",100);
		loadHelp("${ambit_root}","algorithm");
		
				
		var purl = $.url();
		$('#dataset_uri').attr('value',purl.param('dataset_uri')===undefined?'':purl.param('dataset_uri'));
		$('#model_uri').attr('value',purl.param('model_uri')===undefined?'':purl.param('model_uri'));
	}
);
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/algorithm' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="h3 remove-bottom">
					Algorithms
			</div>
		    <div class='h6'>Descriptor calculations, model building and data processing algorithms</div>			
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
<#include "/algorithm_menu.ftl">
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >


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
		<div class="row" style="padding:0;" >
			<table id='algorithm'  cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Name</th>
			<th>Endpoint</th>
			<th>Description</th>
			<th>Type</th>
			<th>Implementation of</th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
