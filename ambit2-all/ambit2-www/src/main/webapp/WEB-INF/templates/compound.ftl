<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<#assign w=250/>
<#assign h=250/>
<script src="${ambit_root}/jquery/jquery.imagemapster.min.js" type="text/javascript"></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-atoms.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jcompound.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<#if cmpid??>
<script type='text/javascript'>
	
function atomNumber(num) {
	//console.log(num);
}	
	 
$(document)
	.ready(function() {
			$( "#tabs" ).tabs();
			var cmpURI = "${ambit_root}/compound/${cmpid}";
			<#if strucid??>
				cmpURI = "${ambit_root}/compound/${cmpid}/conformer/${strucid}";
			</#if>

			$.ajax({
				  url: "${ambit_root}/query/compound/url/all?headless=true&media=text/html&search=" + encodeURIComponent(cmpURI),
				  dataType:"html",
			      success: function(data, status, xhr) {
			    	  $('#identifiers').html(data);
			      }
				});
			
			var pTable = definePropertyValuesTable("${ambit_root}","${ambit_request_json}","#properties");
			
			var cmpURI_datasets = cmpURI + "/datasets?media=application/json";
			try {
				var oTable = defineDatasetsTable("${ambit_root}",cmpURI_datasets,false);
			} catch (err) {

			}
			$('#download').html(getDownloadLinksCompound("${ambit_root}",cmpURI));
			
			$('#structype_${cmpid}').load(cmpURI + '/comparison');
			$('#consensus_${cmpid}').load(cmpURI + '/consensus');
			loadHelp("${ambit_root}","algorithm");
			modelVarsAutocomplete(".modeluri","${ambit_root}/model",100);

	 });
</script>
</#if>
<script type='text/javascript'>
	//var cmp = readCompound("${ambit_root}","${ambit_request_json}");
	 
$(document)
	.ready(function() {
			$( "#selectable" ).selectable( "option", "distance", 18);
	 });
	 
$(window).load(function() {
	//get image/json only after the images are loaded; otherwise json may not have been generated!
	var cmpURI_json = "${ambit_root}/compound/${cmpid}/imagejson";
	createImageMap(cmpURI_json, '${cmpid}', '#i${cmpid}', '#m${cmpid}');
});	 
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ui/query' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="remove-bottom h3">
					Chemical compound
			</div>
		    <div class='h6'><a href='${ambit_root}/compound/${cmpid}'>${ambit_root}/compound/${cmpid}</a></div>			
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
<#include "/compound_menu.ftl">
</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->

		<div class="ui-widget-header ui-corner-top">&nbsp;Chemical compound</div>
		<div class="ui-widget-content ui-corner-bottom" >
			<div class='row' style="margin:5px;padding:5px;">	
			
			<span class='six columns alpha'>
			<img id="i${cmpid}" src='${ambit_root}/compound/${cmpid}/image?w=${w}&h=${h}' width='${w}' height='${h}' title='${cmpid}'  usemap="#m${cmpid}" onError="this.style.display='none'">
			<map id="m${cmpid}" name="m${cmpid}"></map>
			</span>
			
			<span class='ten columns omega help' id='identifiers'>${ambit_request}</span>
			</div>
			<div id='tabs' class='row' style="margin:5px;padding:5px;font-size: 0.9em;">
			<ul>
		    <li><a href="#tabs-properties">Properties</a></li>
		    <li><a href="#tabs-datasets">Datasets</a></li>
		    <li><a href="#tabs-models">Select models to display</a></li>
		    </ul>
		    	<div id='tabs-properties'>
					<table id='properties' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
					<thead>
					<tr>
					<th>Name</th>
					<th>Units</th>
					<th>Value</th>
					<th>SameAs</th>
					<th>Source</th>
					</tr>
					</thead>
					<tbody></tbody>
					</table>
		    	</div>
		    	<div id='tabs-datasets'>
					<table id='datasets' class='datasetstable' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
					<thead>
					<tr>
					<th></th>
					<th><span class='ui-icon ui-icon-star' style='float: left;' title='Star rating'></span></th>
					<th>Name</th>
					<th>Models</th>
					<th>Download</th>
					<th style='display:none;'></th>
					</tr>
					
					</thead>
					<tbody></tbody>
					</table>
				</div>
				<div id='tabs-models'>
					<form class='remove-bottom'  id='view' name='view' action='${ambit_root}/compound/${cmpid}' method='GET'>
					<br/>
				<div class='row remove-bottom ' >
					<label class='five columns alpha' for="model1">Model 1</label>
					<input class='eight columns alpha half-bottom modeluri' type="text" id='model1' value='' name='feature_uris[]' title='Enter model uri'>
					<div class='three columns omega help'><a href='#' class='chelp hmodel'>?</a></div>
				</div>	
				<div class='row  remove-bottom ' >
					<label class='five columns alpha' for="model2">Model 2</label>
					<input class='eight columns alpha half-bottom modeluri' type="text" id='model2' value='' name='feature_uris[]' title='Enter model uri'>
					<div class='three columns omega help'><a href='#' class='chelp hmodel'>?</a></div>
				</div>	
				<div class='row  remove-bottom ' >
					<label class='five columns alpha' for="model3">Model 3</label>
					<input class='eight columns alpha half-bottom modeluri' type="text" id='model2' value='' name='feature_uris[]' title='Enter model uri'>
					<div class='three columns omega help'><a href='#' class='chelp hmodel'>?</a></div>
				</div>	
				<div class='row remove bottom'>
					<label class='five columns alpha'>&nbsp;</label>
					<input class='three columns alpha' type='submit' class='submit' value='Submit'>
					<div class='eight columns omega'></div>
				</div>
				<div class='row remove bottom'>
					<div class='sixteen columns alpha'>
						Alternatively, use the 
						<a href="${ambit_root}/algorithm/superservice?dataset_uri=${ambit_root}/compound/${cmpid}">Superservice</a>
					 <a href='#' class='chelp hsuperservice'>?</a> to run predictions.
					</div>
				</div>				
				</form>

				</div>
							
			</div>
		</div>


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
