<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>


	$(document).ready(function() {
		loadHelp("${ambit_root}","ra");
<#if menu_profile?? && menu_profile=='lri'>		
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="All assessments">Assessments</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/assessment" title="New assessment (empty template)">New</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/assessment_copy" title="New assessment (existing assessment)">Use of existing assessment</a></li>');
<#else>
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="Bundles: Datasets of substances and studies">Datasets of substances and studies</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/assessment" title="New dataset (empty template)">New</a></li>');
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/assessment_copy" title="New dataset (existing template)">Use of existing dataset</a></li>');
</#if>
		downloadForm("${ambit_request}");
  		var oTable = defineBundlesTable_lri("${ambit_root}","${ambit_root}/bundle?media=application/json",false,"${menu_profile}",true);
		jQuery("#breadCrumb").jBreadCrumb();
		jQuery("#welcome").text("Dataset");	  	
	});
</script>


</head>
<body>


<div class="container" style="margin:0;padding:0;">


<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="one column">

&nbsp;
</div>


<div class="twelve columns remove-bottom" style="padding:0;" >

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
		
			<#if menu_profile?? && menu_profile!='ot'>
				<table id='datasets' class='datasetstable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
				<thead>
				<tr>
				<th>Name</th>
				<th>Version</th>
				<th>Code</th>
				<th>Status</th>
				<th>Owner</th>
				<th>Assessment ID</th>
				<th>Action</th>
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

<div class="three columns">

	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
	<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
	<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>

</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
