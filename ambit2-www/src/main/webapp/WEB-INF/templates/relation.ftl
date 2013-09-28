<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>


<link rel="stylesheet" href="${ambit_root}/style/skeleton/graph.css" type="text/css">

<script type='text/javascript'>
	
	$(document).ready(function() {
		 $( "#tabs" ).tabs();
	  		//help, menu
			$( "#selectable" ).selectable( "option", "distance", 18);
			loadHelp("${ambit_root}","relation");
			downloadForm("${ambit_request}");
	});
</script>


</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/relation' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="remove-bottom h3">
					Structure relations
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

</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Chemical structure relations</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">
		<h5>This request as JSON</h5>
		<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
			<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
		</div>

	 	<h5>Tautomers per dataset</h5>
		Please use the <a href='${ambit_root}/query/relation/dataset/has_tautomer?dataset_uri=&media=application/json'>${ambit_root}/query/relation/dataset/has_tautomer?dataset_uri=${ambit_root}/dataset/999</a> page to retrieve pre-generated tautomers.
		<br/>
	 	<h5>Tautomers per compound</h5>
		Please use the <a href='${ambit_root}/query/relation/compound/has_tautomer?dataset_uri=&media=application/json'>${ambit_root}/query/relation/compound/has_tautomer?dataset_uri=${ambit_root}/compound/999</a> page to retrieve pre-generated tautomers of all compounds in a dataset.
		<h5>Tautomers generation</h5>
		Please use the <a href='${ambit_root}/algorithm/tautomers'>${ambit_root}/algorithm/tautomers</a> with the OpenTox API
		<br/>
		
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