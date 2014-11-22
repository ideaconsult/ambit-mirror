<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl">
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/substance/dataset-details.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/substance/config-dataset.js'></script>

<script type='text/javascript'>
		
	$(document).ready(function() {
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle" title="Bundles">Bundles</a></li>');
	    <#if datasetid??>
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/bundle/${datasetid}" title="Bundle">/bundle/${datasetid}</a></li>');
	    </#if>
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="${ambit_request}">Chemical structures</a></li>');
		jQuery("#breadCrumb").jBreadCrumb();

	  	//var ds = new jToxCompound($(".jtox-toolkit")[0],config_dataset);
        //ds.queryDataset("${ambit_request}");
	});
	</script>
   
</head>
<body>


<div class="container" style="margin:0;padding:0;">

<#include "/banner_crumbs.ftl">

		<!-- Page Content
		================================================== -->


<div class="sixteen columns " style="padding:0 2px 2px 2px 0;margin-top:5px;margin-left:25;margin-right:25;">		
	

	<div class="jtox-toolkit" data-kit="compound" 
	data-configuration="config_dataset" 
	data-cross-domain="false"	
	data-show-export="yes"
	data-on-details="onDetailedRow" data-show-diagrams="true"
	data-dataset-uri="${ambit_request}"
	data-tabs-folded="true"
	data-on-error="errorHandler"
	<#if ajaxtimeout??>
	  data-timeout="${ajaxtimeout}"
	</#if>	
	data-jsonp="false">
	</div>
</div>


<div class='row add-bottom' style="height:140px;">&nbsp;</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
