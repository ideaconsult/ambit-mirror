<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>

<script type='text/javascript'>
$(document).ready(function() {
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/datatemplate" title="Templates">Data templates</a></li>');
	jQuery("#breadCrumb").jBreadCrumb();
	downloadForm("${ambit_request}");
	loadHelp("${ambit_root}","datatemplate");
});
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >

	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='xlsx' target=_blank><img src='${ambit_root}/images/xlsx.png' alt='xlsx' title='Download as XLSX'></a>
	</div>

</div>



		<div class="ten columns remove-bottom" style="padding:0;" >

 		<div class="row remove-bottom ui-widget-header ui-corner-top">
 		&nbsp;
 		</div>
 		TODO list or search form for different templates
 		<br/>		
		Please use the XLSX button to retrieve the template.
			
		</div> 
		
		<div class="three columns remove-bottom" style="padding:0;" >

		<!-- help-->		
		Generates XLSX templates for data entry.  Help TBD
		
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
		</div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
		</div>		
		
		</div>
		
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>