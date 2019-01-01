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
		

			<#if menu_profile?? && menu_profile=='lri'>
				jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="All assessments ">Assessments</a></li>');
	    		loadHelp("${ambit_root}","ra");
	    		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/bundle'><b>Name</b> <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
			<#else>
				jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="Bundles: Datasets of substances and studies">Datasets of substances and studies</a></li>');
	    		loadHelp("${ambit_root}","substance");
	    		$("#_searchdiv").html("<form class='remove-bottom' action='${ambit_root}/bundle'><b>Name</b> <input name='search' class='search' value='' id='search'> <input type='submit' value='Search'></form>");
			</#if>
	    

		userAutocomplete(".users","${ambit_root}/myaccount/users",10);
		
		downloadForm("${ambit_request}");
		<#if datasetid??>
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/bundle/${datasetid}" title="${ambit_root}/bundle/${datasetid}">/bundle/${datasetid}</a></li>');
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${datasetid}/metadata" title="${ambit_root}/bundle/${datasetid}/metadata">Metadata</a></li>');
			loadDatasetMeta("${ambit_root}","${ambit_root}/bundle/${datasetid}/metadata",true);
		<#else>
	  		var oTable = defineBundlesTable("${ambit_root}","${ambit_request_json}",true,"${menu_profile}");
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
						oTable.fnOpen(nTr, bundleFormatDetails(oTable,nTr,"${ambit_root}",false,true,false,true),	'details');
												       
					}
					loadBundleSummary(oTable.fnGetData(nTr));		
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
	</div>
	
	<div class='row remove-bottom' id='help'>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>		
	</div>	

	<#if menu_profile?? && menu_profile =='lri'>
	<div id="tabs_search">
		<!-- search -->
		<form method='GET' name='searchform' id='searchform' action='${ambit_root}/bundle' style='padding:0;margin:0;'>
	
		<div class='row remove-bottom' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
						<span title='Search for bundles by name'>by name</span>
				    	<input  type='text'  id='search' name='search' value='' tabindex='1' >
	
		<span title='Search by users with read access'>by Users with Read access</span>
				    	<input type='text' class='users' id='canRead' name='canRead' value='' tabindex='1' >
	
		<span title='Search by users with write access'>by Users with Write access</span>
				    	<input type='text' class='users' id='canWrite' name='canWrite' value='' tabindex='1' >
	
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		</div>
		</form>
	</div>
	</#if>
	
	<div id="tabs_substance" >

		<!-- Page Content
		================================================== -->
		<#if datasetid??>

			<div class="row remove-bottom ui-widget-header ui-corner-top">
			Substance dataset at <a href='${ambit_root}/bundle/${datasetid}'>${ambit_root}/bundle/${datasetid}</a>
			</div>
			<#include "/dataset_one.ftl">
			
		<#else>
		
			<#if menu_profile?? && menu_profile !='ot'>
				<table id='datasets' class='datasetstable jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
				<thead>
				<tr>
				<th>#</th>
				<th>Name</th>
				<th>Code</th>
				<th>Status</th>
				<th>Owner</th>
				<th id='th_assessmentid'>Assessment ID</th>
				<th id='th_action'></th>
				</tr>
				</thead>
				<tbody></tbody>
				</table>
			<#else>
				<table id='datasets' class='datasetstable jtoxkit' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
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

	</div>	<!-- tabs_substance -->
		
</div> <!-- tabs -->


<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
