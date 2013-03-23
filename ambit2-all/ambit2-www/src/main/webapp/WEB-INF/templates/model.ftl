<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

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
		$('.modeltable tbody td .zoomstruc').live('click',function() {
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
					$( "#selectable" ).selectable( "option", "distance", 18);
					datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);
					loadHelp("${ambit_root}","model");
					
				});
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/model' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="remove-bottom h3">
					Models
			</div>
		    <div class='h6'>Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.</div>			
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
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
		<#if modelid??>
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top">Model at <a href='${ambit_root}/model/${modelid}'>${ambit_root}/model/${modelid}</a></div>
			<div class="ui-widget-content ui-corner-bottom">
				<div style="margin:5px;padding:5px;">	
				<form action="${ambit_root}/model/${modelid}" id="runModel"  method="POST">	
					<#include "/model_one.ftl">
				</form>
				</div>
			</div>
		<#else>
		<div class="row " style="padding:0;" >
			<table id='model' class='modeltable'  cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Logo</th>
			<th><span class='ui-icon ui-icon-star' style='float: left;' title='Star rating'></span></th>
			<th>Title</th>
			<th>Algorithm</th>
			<th>Training dataset</th>
			<th>RMSE (TR)</th>
			<th>RMSE (CV)</th>
			<th>% Correct (TR)</th>
			<th>% Correct (CV)</th>
			<th>Stats</th>
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
