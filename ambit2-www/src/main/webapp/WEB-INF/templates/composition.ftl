<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-substance.js'></script>

<#if substanceid??>
	<script type='text/javascript'>
		var algorithm = substance.readComposition("${ambit_root}","${ambit_request_json}");
	</script>
<#else>
	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = substance.defineCompositionTable("${ambit_root}","${ambit_request_json}","#composition",true,null);
	  	loadHelp("${ambit_root}","substance");
	  	$( "#selectable" ).selectable( "option", "distance", 18);
	  	downloadForm("${ambit_request}");
	  	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substances: Mono-constituent, multiconstituent, additives, impurities.">Substance</a></li>');
	    <#if substanceUUID??>
	  		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance/${substanceUUID}" title="Substance">${substanceUUID}</a></li>');
	  	</#if>	  	
	  	jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="Composition.">Composition</a></li>');
	  	jQuery("#breadCrumb").jBreadCrumb();
	});
	</script>
</#if>

</head>
<body>

<div class="container" style="margin:0;padding:0;">


<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/searchmenu/menu_substance.ftl">
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
	<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
	</div>
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
	</div>		
</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >

 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>

		<!-- Page Content
		================================================== -->
		<div class="row" style="padding:0;" >
			<table id='composition' class='compositiontable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Composition ID</th>
			<th>Type</th>
			<th>Name</th>
			<th>EC No.</th>
			<th>CAS No.</th>
			<th>Typical concentration</th>
			<th>Real concentration (lower)</th>
			<th>Real concentration (upper)</th>
			<th>Other related substances</th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
	
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
