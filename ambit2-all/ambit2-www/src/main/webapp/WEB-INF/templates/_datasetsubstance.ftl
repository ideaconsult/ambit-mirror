<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jquery-migrate-1.2.1.min.js.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/colResizable-1.3.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/config-dataset.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jtoxkit.js'></script>


<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css"/>

<script type='text/javascript'>
	var purl = $.url();
	var dataset_uri = "${ambit_request_json}";
			
	$(document).ready(function() {
		var duri = dataset_uri;
		if (duri.length>60) duri = duri.substring(0,60) + "...";
		$('#current').text(duri);
		$('#current').prop("href",dataset_uri);
		$('#dataseturi').val(dataset_uri);	  
	    datasetAutocomplete(".dataseturi","${ambit_root}/dataset",10);

	  	var ds = new jToxCompound($(".jtox-toolkit")[0],config_dataset);
        ds.queryDataset(dataset_uri);
	});
	</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/ui/_dataset' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="six columns alpha">
			<div class="remove-bottom h3" >
				Dataset browser
			</div>
		    <div class='help'>
		     <a href='#' id='current' title='Old view mode'>Old view mode</a>
		     
		    </div>			
		</div>
		<div class="four columns omega">
			<div class="remove-bottom h3" >
				&nbsp;
			</div>
		    <div class='h6'>
		    	Enter dataset URI<br/>
		    	<span class='help remove-bottom'>
		    	or type first dataset name letters to get a list of datasets
		    	</span>
		    </div>			
		</div>			
		<div class="four columns omega">
			<div class="remove-bottom h3">
				&nbsp;
			</div>
		    <div class='h6 remove-bottom'>
		    	<input type='text' class='dataseturi remove-bottom' id='dataseturi' name='dataset_uri' value='' tabindex='1' title='Type first letters of a dataset name in the box to get a list of datasets.'>
		    	<span class='help remove-bottom'>
		    	View <a href='${ambit_root}/dataset?max=1000' target='datasets' title='All datasets list'>All datasets</a>
		    	</span>
		    </div>			
		</div>		
		<div class="two columns omega">
			<div class="remove-bottom h3" >
				&nbsp;
			</div>
		    <div class='h6'>
		    	<input class='ambit_search ' id='submit' type='submit' value='Browse' tabindex='2'>
		    </div>			
		</div>	
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

</form>


		<!-- Page Content
		================================================== -->


<div class="sixteen columns " style="padding:0 2px 2px 2px 0;margin-top:5px;margin-left:25;margin-right:25;">		

	<div class="jtox-toolkit" data-kit="compound" 
	data-manual-init="yes" 
	data-show-features="yes" 
	data-cross-domain="false"	
	data-show-export="yes" 
	data-jsonp="false"></div>

</div>


<div class='row add-bottom' style="height:140px;">&nbsp;</div>



<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
