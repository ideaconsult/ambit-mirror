<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<link rel="stylesheet" href="${ambit_root}/scripts/dataset/ui-query.css"/>
<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-datasetcomparison.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>

<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript'>
	var purl = $.url();
	var dataset_uri = purl.param('dataset_uri')===undefined?'${ambit_root}/query/similarity?search=c1ccccc1':purl.param('dataset_uri');
			
	$(document).ready(function() {
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets">Datasets</a></li>');
        jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" title="Dataset comparison">Dataset comparison</a></li>');
	
		comparison.datasetComparisonMatrix("${ambit_root}","${ambit_root}/dataset",comparison.matrix,"#matrix");
        
	});
	</script>
    
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<!-- Page Content
================================================== -->


<div class="sixteen columns remove-bottom" style="padding:0;" >
	<table id="matrix"/>
</div>
			


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
