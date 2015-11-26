<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	$( "#tabs" ).tabs();
	var oTable = defineSubstanceOwnerTable("${ambit_root}","${ambit_request_json}","#facet");
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/stats" title="Statistics">Statistics</a></li>');
	
	<#if menu_profile?? && menu_profile=='enanomapper'>		        
	  loadHelp("${ambit_root}","nanomaterial");
	  jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${facet_tooltip}">Nanomaterials by contributor</a></li>');
	  $("#header_contributor").text("Nanomaterial contributor name");
	  $("#header_substances").text("Nanomaterials");
	  $("#header_substance").text("Nanomaterials by contributor");
	  
	  $("#header_contributor_uuid").text("Nanomaterial contributor UUID");
	<#else>
		loadHelp("${ambit_root}","substance");
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${facet_tooltip}">${facet_title}</a></li>');		
	</#if>
  
	
	downloadForm("${ambit_request}");
	jQuery("#breadCrumb").jBreadCrumb();
		
});
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div id="tabs" class="fourteen columns remove-bottom"  style="padding:0;">
<ul>
<li><a href="#tabs_substance" id="header_substance">Substances by owner</a></li>
<li><a href="#download">Download</a></li>
</ul>

<div id='download' >  <!-- tabs_download -->
<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>
<a href='#' id='csv' target=_blank><img src='${ambit_root}/images/csv64.png' alt='CSV' title='Download as CSV'></a>
</div>
	
<div id="tabs_substance">

		<table id='facet' class='jtox-toolkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
		<thead>	
		<tr>
		<th id='header_contributor' >Substance contributor name</th>
		<th id='header_substances'>Substances</th>
		<th >Table view</th>
		<th id='header_contributor_uuid'>Substance contributor UUID</th>		
		</tr>
		</thead>
		<tbody></tbody>
		</table>
			
</div> 
</div> <!-- tabs -->

<#include "/chelp.ftl" >


<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>