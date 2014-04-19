<#include "/html.ftl">
<head>
  <#include "/header.ftl">
  

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>
<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/ketcher.js'></script>
  
<script type='text/javascript'>  
$(document)
		.ready(
				function() {
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/_search" title="AMBIT Structure search">Structure search</a></li>');
    					jQuery("#breadCrumb").jBreadCrumb();
    					jQuery("#welcome").text("Structure search");
    					loadHelp("${ambit_root}","search");
				});
</script>  
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
</div>


		
		<!-- Page Content
		================================================== -->
	<div class="fourteen columns remove-bottom" style="padding:0;" >

	<div class="jtox-toolkit" data-kit="query" data-initial-query="yes" data-base-url="${ambit_root}">
    	<div class="jtox-toolkit jtox-widget" data-kit="search"></div>
	  <div class="jtox-toolkit" data-kit="dataset" data-cross-domain="true" data-show-tabs="true"></div>
	</div>		

   </div>
		<!-- Right column and footer
		================================================== -->

		
<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>