<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/myaccount" id="breadCrumbUser" title="User">My account</a></li>');
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/myaccount/apps" id="breadCrumbUser" title="API Keys">API Keys</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","apps");
    var oTable = defineAppsTable("${ambit_root}","${ambit_request_json}","#apps",false,"<Fif>rt");
})
</script>

</head>
<body>

<div class="container columns" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>
		
		<!-- Page Content
		================================================== -->
		<div class="twelve columns add-bottom" >
	
			<div class="row" style="margin:20;padding:10;" >
					<table id='apps'  class='fourteen columns ' cellpadding='0' border='0' width='90%' cellspacing='0' style="margin:20;padding:10;"  ></table>
			</div>	
			

		</div> 
		
		<!-- Right column and footer
		================================================== -->
<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>