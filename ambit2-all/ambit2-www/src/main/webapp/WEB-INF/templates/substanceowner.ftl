<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	var oTable = defineSubstanceOwnerTable("${ambit_root}","${ambit_request_json}","#facet");
	loadHelp("${ambit_root}","substance");
	downloadForm("${ambit_request}");
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/stats" title="Statistics">Statistics</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${facet_tooltip}">${facet_title}</a></li>');
	jQuery("#breadCrumb").jBreadCrumb();
	
	downloadForm("${ambit_request}");		
});
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<#include "/searchmenu/menu_substance.ftl">

<!-- help-->		
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
 				
		<table id='facet' class='ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>	
		<tr>
		<th >Substance contributor Name</th>
		<th >Substances</th>
		<th >Table view</th>
		<th >Substance contributor UUID</th>		
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