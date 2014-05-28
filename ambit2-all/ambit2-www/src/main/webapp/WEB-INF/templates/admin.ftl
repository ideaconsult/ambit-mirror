<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript'>

$(document)
		.ready(
				function() {
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="AMBIT admin">Admin</a></li>');
					loadHelp("${ambit_root}","about");
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">
	<!-- banner -->
	<#include "/banner_crumbs.ftl">
	
	<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	&nbsp;
	</div>
	

<div class="twelve columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Admin</div>
<div class='ui-widget-content ui-corner-bottom'>	
			
		<div class='row' style="margin:5px;padding:5px;"> 	
		<div class="three columns">Database admin</div>
		<div class="ten columns"><a href='${ambit_root}/admin/database'>Create or inspect the database</a></div>
		<div class="three columns help"><a href='http://ambit.sourceforge.net/install_ambitrest.html' class='qxternal' target=_blank>Help</a></div>
		</div>
	
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">System jobs</div>
		<div class="thirteen columns"><a href='${ambit_root}/task'>Browse jobs status</a></div>
		</div>		
		
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">Statistics</div>
		<div class="thirteen columns">
		<a href='${ambit_root}/admin/stats'>Number of datasets</a> | 
		<a href='${ambit_root}/admin/stats/structures'>Number of structures</a> | 
		<a href='${ambit_root}/admin/stats/properties'>Number of properties</a> |
		<a href='${ambit_root}/admin/stats/values'>Number of values</a> |
		<a href='${ambit_root}/admin/stats/models'>Number of models</a> |
		</div>
		</div>	
		<!--
		
admin/policy
admin/stats
admin/stats/structures
admin/stats/chemicals_in_dataset
admin/stats/dataset_intersection
admin/stats/properties
admin/stats/values
admin/stats/dataset
admin/stats/models
admin/fingerprint/fp1024
admin/fingerprint/sk1024
-->
	
		<div class='row' style="margin:5px;padding:5px;">
		<div class="three columns">Structure indexing</div>
		<div class="thirteen columns">
		Click <a href="#" onClick="runStructureIndexing('${ambit_root}','#status');">here</a> to enable similarity and structure search
		for newly imported datasets.
		<span class='help' id='status'></span>
		</div>
		</div>	

</div>
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
