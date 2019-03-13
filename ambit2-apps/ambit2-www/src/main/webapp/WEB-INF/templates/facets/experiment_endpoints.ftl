<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/ont.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>

$(document).ready(function() {
	$( "#tabs" ).tabs();	
	var oTable = defineExperimentEndpointsTable("${ambit_root}","${ambit_request_json}","#facet");

	jQuery("#breadCrumb ul").append('<li><a href="#" title="Summary">Summary</a></li>');
	jQuery("#breadCrumb ul").append('<li><a id="pchem" href="${ambit_root}/query/experiment_endpoints?top=P-CHEM">P-CHEM</a></li>');
	jQuery("#breadCrumb ul").append('<li><a id="tox" href="${ambit_root}/query/experiment_endpoints?top=TOX">TOX</a></li>');
	jQuery("#breadCrumb ul").append('<li><a id="ecotox" href="${ambit_root}/query/experiment_endpoints?top=ECOTOX">ECOTOX </a></li>');
	jQuery("#breadCrumb ul").append('<li><a id="envfate" href="${ambit_root}/query/experiment_endpoints?top=ENV%20FATE">ENV FATE </a></li>');
	jQuery("#breadCrumb").jBreadCrumb();
	
	downloadForm("${ambit_request}");
	
});


</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="one column" style="padding:0 2px 2px 2px 0;margin-right:0;" >&nbsp;
</div>

<div id="tabs" class="fifteen columns remove-bottom"  style="padding:0;">
<ul>
<li><a href="#tabs_substance" id="header_substance">Hit list</a></li>
<li><a href="#download">Download</a></li>
<li><a href="#tabs_help">Help</a></li>
</ul>
	<div id='tabs_help'>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
		</div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
		</div>	
	</div>
	<div id='download' >  <!-- tabs_download -->
		<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>

	</div>
	
		<div id="tabs_substance" style="padding:0;" >
		
		
		<table id='facet' class='jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>	
		<tr>
		<th>Top category</th>		
		<th>Category identifier</th>
		<th>Category label</th>
		<th>Result endpoint</th>
		<th>Qualifier</th>
		<th>Units</th>
		<th>Number of entries</th>
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