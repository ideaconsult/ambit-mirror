<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
	
	$(document).ready(function() {
		loadHelp("${ambit_root}","bundle");
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="Bundles">Dataset of substances and studies</a></li>');

		downloadForm("${ambit_request}");
		<#if datasetid??>
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/bundle/${datasetid}" title="${ambit_root}/bundle/${datasetid}">/bundle/${datasetid}</a></li>');
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${datasetid}/metadata" title="${ambit_root}/bundle/${datasetid}/metadata">Metadata</a></li>');
			loadDatasetMeta("${ambit_root}","${ambit_root}/bundle/${datasetid}/metadata",true);
		<#else>
	  		var oTable = defineBundlesTable("${ambit_root}","${ambit_request_json}",true,"${menu_profile}");
	  	</#if>
		jQuery("#breadCrumb").jBreadCrumb();
		jQuery("#welcome").text("Dataset");	  	
	});
</script>


</head>
<body>


<div class="container" style="margin:0;padding:0;">


<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/bundle' style='padding:0;margin:0;'>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Search for datasets by name'>Search for bundles by name</span>
		    	<input type='text'  id='search' name='search' value='' tabindex='1' >
		    	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
</div>

</form>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list '></a>
<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
</div>

</div>


<div class="thirteen columns remove-bottom" style="padding:0;" >

		<!-- Page Content
		================================================== -->
		<#if datasetid??>
		<div class="row" style="padding:0;" >			
			<div class="row remove-bottom ui-widget-header ui-corner-top">
			Substance dataset at <a href='${ambit_root}/bundle/${datasetid}'>${ambit_root}/bundle/${datasetid}</a>
			</div>
			
			<#include "/dataset_one.ftl">
			
		<#else>
 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>
		
		<div class="row " style="padding:0;" >
		
			<#if menu_profile?? && menu_profile='lri'>
				<table id='datasets' class='datasetstable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
				<thead>
				<tr>
				<th>ID</th>
				<th>Code</th>
				<th>Name</th>
				<th>Status</th>
				<th>Owner</th>
				</tr>
				</thead>
				<tbody></tbody>
				</table>
			<#else>
				<table id='datasets' class='datasetstable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
				<thead>
				<tr>
				<th><span class='ui-icon ui-icon-star' style='float: left;' title='Star rating'></span></th>
				<th>Name</th>
				<th>Download</th>
				<th title='Remove the dataset. Only datasets with star rating <= 5 can be deleted!'><span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;'></span></th>
				</tr>
				</thead>
				<tbody></tbody>
				</table>			
			</#if>

		
		</#if>
		
		</div>
		



<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
