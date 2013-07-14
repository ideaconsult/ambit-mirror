<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

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
					$( "#selectable" ).selectable( "option", "distance", 18);
						loadHelp("${ambit_root}","qmap");
						downloadForm("${ambit_request}");
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/qmap' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="remove-bottom h3">
					Chemical landscape: activity cliffs
			</div>
		    <div class='help'>
		    
 
		    </div>			
		</div>
		<div class="four columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input type='text'  id='search' name='search' value='' tabindex='1' >
		    </div>			
		</div>		
		<div class="two columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		    </div>			
		</div>	
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

</form>
<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>
</div>


<div class="eleven columns remove-bottom" style="padding:0;" >


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
		<div class="row" style="padding:0;" >
			<table id='qmap' class='qmaptable' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Dataset <a href='#' class='chelp dataset'>?</a></th>
			<th>Property <a href='#' class='chelp feature'>?</a></th>
			<th>Activity threshold</th>
			<th>Similarity threshold</th>
			<th>Activity cliffs <a href='#' class='chelp activity_cliff'>?</a></th>
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
