<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  

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
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row half-bottom">
		<div class="two columns">
			<a href='${ambit_root}/ui'>
				<img class='scale-with-grid' border='0' src='${ambit_root}/images/ambit-logo-small.png' title='v${ambit_version_short}' alt='AMBIT logo'>
			</a>
		</div>
		<div class="thirteen columns remove-bottom breadCrumb module h6" id="breadCrumb">
                    <ul>
                        <li>
                            <a href="${ambit_root}" title="AMBIT Home [v ${ambit_version_long}]">Home</a>
                        </li>
                    </ul>
		</div>
</div>
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>




		
		<!-- Page Content
		================================================== -->
	<div class="sixteen columns remove-bottom" style="padding:0;" >

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