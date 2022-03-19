<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>



<script type='text/javascript'>
	
	function confirmDeleteBundle(txt) {
		return confirm("Are you sure you want to delete " + txt + "? Please confirm!");
	}
	
	$(document).ready(function() {
		$( "#tabs" ).tabs();
		
    	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="Nanomaterials">Bundles</a></li>');
    	loadHelp("${ambit_root}","bundle");
		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/bundle'><b>Name</b> <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
		$("#header_substance").text("Nanomaterial bundles");
		$("#th_assessmentid").text("Bundle ID");

		userAutocomplete(".users","${ambit_root}/myaccount/users",10);
		
		downloadForm("${ambit_request}");
		<#if datasetid??>
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/bundle/${datasetid}" title="${ambit_root}/bundle/${datasetid}">/bundle/${datasetid}</a></li>');
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${datasetid}/metadata" title="${ambit_root}/bundle/${datasetid}/metadata">Metadata</a></li>');
			loadDatasetMeta("${ambit_root}","${ambit_root}/bundle/${datasetid}/metadata",true);
		<#else>
	  		var oTable = defineBundlesTable_enm("${ambit_root}","${ambit_request_json}",false,false);
	  		 $('.datasetstable tbody').on('click','td .zoomstruc',function() {
					var nTr = $(this).parents('tr')[0];
					if (oTable.fnIsOpen(nTr)) {
						$(this).removeClass("ui-icon-folder-open");
						$(this).addClass("ui-icon-folder-collapsed");
						this.title='Click to show bundle details';
						oTable.fnClose(nTr);
					} else {
						$(this).removeClass("ui-icon-folder-collapsed");
						$(this).addClass("ui-icon-folder-open");
						this.title='Click to close bundle details';
						var id = 'v'+getID();
						oTable.fnOpen(nTr, bundleFormatDetails(oTable,nTr,"${ambit_root}",true,true,true,false,true),	'details');
						
						loadBundleSummary(oTable.fnGetData(nTr));				       	
					}
			});
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

<div id="tabs" class="sixteen columns remove-bottom" style="padding:0;">
	<ul>
	<li><a href="#tabs_substance" id="header_substance">Substances</a></li>
	<#if menu_profile?? && menu_profile =='lri'>	
	<li><a href="#tabs_search">Advanced search</a></li>
	</#if>
	<li><a href="#download">Download</a></li>
	<li><a href="#help">Help</a></li>
	</ul>

	<div class='row remove-bottom' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf64.png' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf64.png' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>
	<a href='#' id='jsonld' target=_blank><img src='${ambit_root}/images/json-ld.png' alt='json' title='Download as JSON-LD'></a>	
	</div>
	
	<div class='row remove-bottom' id='help'>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>		
	</div>	
	
	<div id="tabs_substance" >

		<!-- Page Content
		================================================== -->
		<#if datasetid??>

			<div class="row remove-bottom ui-widget-header ui-corner-top">
			Substance dataset at <a href='${ambit_root}/bundle/${datasetid}'>${ambit_root}/bundle/${datasetid}</a>
			</div>
			<#include "/dataset_one.ftl">
			
		<#else>
		
			<table id='datasets' class='datasetstable jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>#</th>
			<th id='th_assessmentid'>Bundle ID</th>
			<th>Name</th>
			<th title='Version'>Ver</th>
			<th>Description</th>
			<th>Source</th>
			<th>License & Maintainer</th>			
			</tr>
			</thead>
			<tbody></tbody>
			</table>

		
		</#if>

	</div>	<!-- tabs_substance -->
		
</div> <!-- tabs -->


<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->


<script type='application/ld+json'>
{
    "@context": "http://schema.org",
    "@type": "Dataset",
    "http://purl.org/dc/terms/conformsTo": { "@type": "CreativeWork", "@id": "https://bioschemas.org/profiles/Dataset/0.4-DRAFT" },
    "@id": "${ambit_request}",
    "url": "${ambit_request}",
    "version": "1",
    "publisher": {
        "@type": "Organization",
        "name": "${menu_profile}"  
    },
    "distribution": [
      
      {
        "@type": "DataDownload",
        "contentUrl": "${ambit_request_json}",
        "encodingFormat" : "application/json"
      }
      
      
    ]
}
  
</script>

</body>
</html>
