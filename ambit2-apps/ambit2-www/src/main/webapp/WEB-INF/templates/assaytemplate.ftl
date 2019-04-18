<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/profile/enanomapper/template.js'></script>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#tabs" ).tabs();
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/datatemplate" title="Templates">Data entry templates</a></li>');
					jQuery("#breadCrumb").jBreadCrumb();
					downloadForm("${ambit_request}");
					loadHelp("${ambit_root}","datatemplate");
					
					$('#assays').dataTable( {
					        data: templates,
							"columns": [
							            { "data": "module" },
							            { "data": "endpoint" },
							            { "data": "s_uuid" }
							        ]
					    } );					
				});
</script>
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<#include "/banner_crumbs.ftl">

<div class="one column remove-bottom" style="padding:0;">&nbsp;</div>

<div id="tabs" class="thirteen columns remove-bottom" style="padding:0;">

	<ul>
	<li><a href="#tabs_substance" id="header_model">Substances</a></li>
	<li><a href="#tabs_endpoints">Assays</a></li>
	<li><a href="#tabs_parameters">Protocol parameters</a></li>
	<li><a href="#tabs_results">Results</a></li>
	<li><a href="#tabs_download">Download template</a></li>
	</ul>

		
	<div id="tabs_substance" >
		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/model' style='padding:0;margin:0;'>
		<input type='text'  id='search' name='search' value='' tabindex='1' >
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		</form>
				
	</div>	

	<div id="tabs_endpoints">

		<table id="assays">
		
		</table>

	</div>


	<div id="tabs_parameters" >
		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/model' style='padding:0;margin:0;'>
		<input type='text'  id='search' name='search' value='' tabindex='1' >
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		</form>
				
	</div>
	
	<div id="tabs_results" >
		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/model' style='padding:0;margin:0;'>
		<input type='text'  id='search' name='search' value='' tabindex='1' >
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		</form>		
	</div>	

	<div id="tabs_download" >
		<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
		<a href='${ambit_root}/datatemplate?media=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet' id='xlsx' target=_blank><img src='${ambit_root}/images/xlsx.png' alt='XLSX' title='Download'></a>
		</div>
	</div>		

</div>

<div class="two columns" style="margin:0;padding:0;" >
<!-- help-->		
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
		</div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
		</div>		
</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
