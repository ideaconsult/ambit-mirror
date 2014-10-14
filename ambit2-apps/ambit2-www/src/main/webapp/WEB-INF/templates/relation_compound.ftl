<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<link rel="stylesheet" href="${ambit_root}/scripts/dataset/ui-query.css"/>
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/dataset/ui-query.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/dataset/config-dataset.js'></script>


<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript'>
	var purl = $.url();
						
	$(document).ready(function() {
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets">Datasets</a></li>');
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" id='relation' title="Dataset browser">Structure relation</a></li>');
        jQuery("#breadCrumb ul").append("<li><a href='#' id='current' title='Old view mode'>Old view mode</a></li>");
	
		var dataset_uri = "${ambit_request_json}";
		var duri = dataset_uri;
		if (duri.length>60) duri = duri.substring(0,60) + "...";
		$('#relation').text(purl.segment().pop());
		$('#current').text(duri);
		$('#current').prop("href",dataset_uri);
		$('#dataseturi').val(dataset_uri);	  
	    datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);
	  	var ds = new jToxCompound($(".jtox-toolkit")[0],config_dataset);
        ds.queryDataset("${ambit_request_json}");
        
        
	});
	</script>
    
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">


		<!-- Page Content
		================================================== -->


<div class="sixteen columns " style="padding:0 2px 2px 2px 0;margin-top:5px;margin-left:25;margin-right:25;">		

	<div class="jtox-toolkit" data-kit="compound" 
	data-manual-init="yes" 
	data-show-features="yes" 
	data-cross-domain="false"	
	data-show-export="yes"
	data-on-details="onDetailedRow" data-show-diagrams="true"
	data-on-error="errorHandler" 
	data-jsonp="false"></div>
</div>


<div class='row add-bottom' style="height:140px;">&nbsp;</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
