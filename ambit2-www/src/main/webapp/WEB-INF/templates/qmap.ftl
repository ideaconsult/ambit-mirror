<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/qmap.js'></script>

	<script type='text/javascript'>
	
	$(document).ready(function() {
	  	var oTable = qmap.defineMetadataTable("${ambit_root}","${ambit_request_json}",true);
	});
	</script>

<script type='text/javascript'>

$(document)
	.ready(
		function() {
			loadHelp("${ambit_root}","qmap");
			downloadForm("${ambit_request}");
			jQuery("#breadCrumb ul").append('<li><a href="http://toxmatch.sourceforge.net/index.htm#background" title="Click for background and more information">ToxMatch: Chemical Landscape Analysis</a></li>');
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/qmap" title="Quantitative Structure-Activity-Similarity Maps (QMaps)">Browse QMaps</a></li>');
			<#if qmapid??>
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="Quantitative Structure-Activity-Similarity Maps (QMaps)">QMap${qmapid}</a></li>');
			</#if>
			jQuery("#breadCrumb").jBreadCrumb();
		});
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="column">&nbsp;</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->
		<#if qmapid??>
		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top">Dataset at <a href='${ambit_root}/dataset/${datasetid}'>${ambit_root}/dataset/${datasetid}</a></div>
			<div class="ui-widget-content ui-corner-bottom">
				<div style="margin:5px;padding:5px;">	
					<#include "/dataset_one.ftl">
				</div>
			</div>
		<#else>
 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>		
		<div class="row" style="padding:0;" >
			<table id='qmap' class='qmaptable ambit2' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Dataset <a href='#' class='chelp dataset'>?</a></th>
			<th>Property <a href='#' class='chelp feature'>?</a></th>
			<th>Activity threshold<a href='#' class='chelp athreshold'>?</a> </th>
			<th>Similarity threshold<a href='#' class='chelp sthreshold'>?</a> </th>
			<th>Activity cliffs <a href='#' class='chelp activity_cliff'>?</a></th>
			<th>All QMaps for the dataset<a href='#' class='chelp qmap'>?</a></th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
		
		</#if>
		
		</div>
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>

</div>
<#include "/chelp.ftl" >

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
