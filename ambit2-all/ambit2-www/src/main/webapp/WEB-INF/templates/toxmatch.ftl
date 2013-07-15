<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/d3.v3.min.js'></script>
<script type="text/javascript" src='${ambit_root}/scripts/fisheye.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jcompound.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/qmap.js'></script>

<link rel="stylesheet" href="${ambit_root}/style/skeleton/graph.css" type="text/css">

<script type='text/javascript'>
	
	$(document).ready(function() {
		 $( "#tabs" ).tabs();
	  		//help, menu
			$( "#selectable" ).selectable( "option", "distance", 18);
			loadHelp("${ambit_root}","qmap");
			downloadForm("${ambit_request}");
			
	  	//	var oTable = qmap.defineMetadataTable("${ambit_root}","${ambit_request_json}",true);
	  		var oTable = qmap.defineNodesTable("${ambit_root}","${ambit_request_json}",function(root,result,nodesTable){
	  			//qmap.defineChart(root,result,"#qchart",640,480);
	  			qmap.defineBubbleChart(root,result,"#bchart",640,480,nodesTable);
	  			
	  			$("#networkbutton").click(function() {
	  				$.each(result.links, function(index, link) {
	  					qmap.drawGraph(root,result,index,"#chart1",640,480);
					 });
	  			});
	  		});
	});
</script>


</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/qmap' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<div class="remove-bottom h3">
					Toxmatch 2
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

<!-- Top -->

<div class="row" style="padding:0 2px 2px 2px 0;margin-right:0;" >
	<div class="eight columns">
			<table id='nodes' class='qnodestable' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
			<thead>
			<tr>
			<th>Structure <a href='#' class='chelp dataset'>?</a></th>
			<th>ID</th>
			<th>G2 <a href='#' class='chelp activity_cliff'>?</a></th>
			<th>Activity</th>
			<th>QMap</th>
			</tr>
			</thead>
			<tbody></tbody>
			</table>
	</div>
	<div class="eight columns">
	<div id="tabs">
	  <ul>
	    <li><a href="#tabs-2">Bubble chart</a></li>
	    <!--
	    <li><a href="#tabs-1">Chart: Activity vs G2</a></li>
	    -->
	    <li><a href="#tabs-3">Network</a></li>
	    <li><a href="#tabs-4">Help</a></li>
	  </ul>
	  <div id="tabs-2">
	  	<div id='bchart'></div>
	  </div>
	  <!--
	  <div id="tabs-1">
	  	<div id='qchart'></div>
	  </div>
	  -->
	  <div id="tabs-3">
	  	<div id='chart1'></div>
	  	<a href='#' id="networkbutton" >Draw graph</a>
	  </div>
	  <div id="tabs-4">
		<!-- Help -->
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>	  	
	  </div>	  
	</div>
	
	</div>	
</div>
<!-- Similarity results -->
<div class="row" style="padding:0 2px 2px 2px 0;margin-right:0;"  >

	<div class="sixteen columns ui-widget-header ui-corner-top">Similar compounds (Activity cliffs) <span id='simtitle'></span></div>
	<div class="sixteen columns ui-widget-content ui-corner-bottom">
		<ul class='structresults' id="cliffs" style='height:150px;'></ul>
	</div>
</div>

<div class="row" style="padding:0 2px 2px 2px 0;margin-right:0;"  >

<div class="sixteen columns ui-widget-header ui-corner-top">Similar compounds (Smooth landscape)<span id='simtitle'></span></div>
<div class="sixteen columns ui-widget-content ui-corner-bottom">
	<ul class='structresults' id="notcliffs" style='height:150px;'></ul>
</div>
</div>

<!-- Bottom -->
<div class="row" style="padding:0 2px 2px 2px 0;margin-right:0;"  >
	<div class="eight columns">

	<table id='qmap' class='qmaptable' cellpadding='0' border='0' width='100%' cellspacing='0' style="margin:0;padding:0;" >
	<thead>
	<tr>
	<th>Dataset <a href='#' class='chelp dataset'>?</a></th>
	<th>Property <a href='#' class='chelp feature'>?</a></th>
	<th>Activity threshold</th>
	<th>Similarity threshold</th>
	<th>Activity cliffs <a href='#' class='chelp activity_cliff'>?</a></th>
	</tr>
	</thead>
	<tbody></tbody>
	</table>
	
		<!-- Downloads -->
		<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
			<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list'></a>
			<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
		</div>
	</div>

	<div class="eight columns">

	</div>
</div>
	
<div class='row add-bottom' style="height:140px;">&nbsp;</div>	

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
