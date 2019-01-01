<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					loadHelp("${ambit_root}","model");
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/model" title="Models">Models</a></li>');
				});
</script>
</head>
<body>


<div class="container" style="margin:0;padding:0;">

<#include "/banner_crumbs.ftl">
	
<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<a href='${ambit_root}/datatemplate?media=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet' id='xlsx' target=_blank><img src='${ambit_root}/images/xlsx.png' alt='XLSX' title='Download'></a>
</div>

</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Data template</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	 
		
	</div>			
</div>
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>
<div class="two columns" style="margin:0;padding:0;" >
<#include "/help.ftl" >
</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
