<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
    
    <!-- smartmenus
	================================================== -->
    <script src="${ambit_root}/jquery/jquery.smartmenus.min.js" type="text/javascript" language="JavaScript"></script>
    <link rel="stylesheet" href="${ambit_root}/style/sm-core-css.css" type="text/css">
    <link rel="stylesheet" href="${ambit_root}/style/sm-mint.css" type="text/css">
    
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>



<#if modelid??>
	<script type='text/javascript'>
		var model = readModel("${ambit_root}","${ambit_request_json}");
		$(document)
		.ready(
				function() {
					//featureAutocomplete(".featureuri",null,"${ambit_root}/model/${modelid}/independent",100);
					//featureAutocomplete(".featureuri",null,"${ambit_root}/model/${modelid}/dependent",10);
					featureAutocomplete(".predicteduri",null,"${ambit_root}/model/${modelid}/predicted",10);
				});
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = defineModelTable("${ambit_root}","${ambit_request_json}");
	  		/* event listener  */
	    <!-- Details panel -->	
	    $('.modeltable tbody').on('click','td .zoomstruc',function() {
					var nTr = $(this).parents('tr')[0];
					if (oTable.fnIsOpen(nTr)) {
						$(this).removeClass("ui-icon-folder-open");
						$(this).addClass("ui-icon-folder-collapsed");
						this.title='Click to show model details';
						oTable.fnClose(nTr);
					} else {
						$(this).removeClass("ui-icon-folder-collapsed");
						$(this).addClass("ui-icon-folder-open");
						this.title='Click to close model details';
						var id = 'v'+getID();
						oTable.fnOpen(nTr, modelFormatDetails(oTable,nTr,"${ambit_root}"),	'details');
												       
					}
			});

	});
	</script>
</#if>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#tabs" ).tabs();
					$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/model'><b>Model search</b> <input name='search' class='search' value='' id='search' title='Model search'> <input type='submit' value='Search'></form>");
				
					datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);
					loadHelp("${ambit_root}","model");
					downloadForm("${ambit_request}");
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/model" title="Models">Models</a></li>');
					<#if modelid??>
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/model/${modelid}" title="This model">M${modelid}</a></li>');
					</#if>
					jQuery("#breadCrumb").jBreadCrumb();
				});
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div id="tabs" class="sixteen columns remove-bottom" style="padding:0;">
	<ul>
	<li><a href="#tabs_models" id="header_model">Models</a></li>
	<li><a href="#tabs_search">Advanced search</a></li>
	<li><a href="#tabs_help">Help</a></li>
	</ul>

		
	<div id="tabs_models" >
		<#if modelid??>
		<div class="row" style="padding:0;" >			
			<div style="font-weight:bold" >Model at <a href='${ambit_root}/model/${modelid}'>${ambit_root}/model/${modelid}</a></div>
			<div >
				<div style="margin:5px;padding:5px;" class="fourteen columns">	
				<form action="${ambit_root}/model/${modelid}" id="runModel"  method="POST">	
					<#include "/model_one.ftl">
				</form>
				</div>
					<div class="two columns chelp" style='padding:0;margin:0;'>
					<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
					<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
					</div>
					<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
					</div>		
					</div>
			</div>
			
		<#else>

		<div class="row " style="padding:0;" >
			<table id='model' class='modeltable jtoxkit'  cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>ID</th>
			<th><span class='ui-icon ui-icon-star' style='float: left;' title='Star rating'></span></th>
			<th>Title</th>
			<th>Training dataset</th>
			<th>Algorithm</th>
			<th>RMSE (TR)</th>
			<th>RMSE (CV)</th>
			<th>% Correct (TR)</th>
			<th>% Correct (CV)</th>
			<th>Stats</th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		
		</div>
		</#if>
	</div>	

	<div id="tabs_search">

		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/model' style='padding:0;margin:0;'>
		<input type='text'  id='search' name='search' value='' tabindex='1' >
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		</form>

	</div>


	<div id="tabs_help" >
		<#include "/searchmenu/model_menu.ftl">
	</div>
	
	
</div>


<#include "/footer.ftl" >
</div> <!-- container -->



</body>
</html>
