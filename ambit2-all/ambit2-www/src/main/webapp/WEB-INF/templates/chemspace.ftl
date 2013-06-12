<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<link href="${ambit_root}/style/simspace.css" rel="stylesheet" type="text/css">
<script type='text/javascript' src='${ambit_root}/scripts/d3.v2.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/pairwisesimilarity.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/spacesimilarity.js'></script>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
					loadHelp("${ambit_root}","about");
					console.log("${ambit_request_json}");
					$.ajax({
				  		url: "${ambit_request_json}",
				  		dataType:"json",
			      		success: function(oSpace, status, xhr) {
			      		  console.log(oSpace);
			    		  spaceMatrix(oSpace,200,"numcliff");
			      		}
					});
					
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="alpha">
			<div class="remove-bottom h3">
					Chemical landscape
			</div>
		    <div class='h6'>Chemical landscape and quantified SAS map</div>			
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >

		
	<div style="margin:50px 5px 5px 5px;padding:5px;">	
	<!-- from java code -->
	<div class='results'>&nbsp;<a href='#' title='' id='dataset'>Dataset</a>
		Order: <select id='order'></select>&nbsp;Matrix cell color <select id='mcolor'></select>
	</div>
	<div style='margin-top:20px;margin-left:50px;margin-right:50px;'>
		<div style='float:left;width:760px'>
			<div id='bar'><svg></svg></div>
			<br>
			<div id='matrix'></div>
		</div>
		<div style='float:right;margin-top:20px;minWidth:250'>
			<div class='ui-widget-header ui-corner-top'>SAS Map</div>
			<div class='ui-widget-content ui-corner-bottom' class='chartWrap' id='chart'>	
				<svg/>
				<br/><label>X</label>&nbsp;<select id='xaxis'></select>
				<br/><label>Y</label>&nbsp;<select id='yaxis'></select>
			</div><br/>
		
			<div  style='margin-top:250px' class='ui-widget-header ui-corner-top'>Selected cell</div>
			
			<div class='ui-widget-content ui-corner-bottom' id='simheader'>Similarity&nbsp;
				<label id='sh_similarity'></label><br/>dActivity&nbsp;<label id='sh_dactivity'/>
				<br/>Ratio&nbsp;
				<label id='sh_ratio'/><br/>Ridge&nbsp;<label id='sh_edge'/>
			</div>
		</div>

	</div>
	
	<div class='row'>
		<div style='margin-left:0px;minWidth:200;margin-top:900px'>
			<ol id='cmp2list' class='spacegrid'></ol>
		</div>	
			
		<div style='margin-left:0px;minWidth:200;margin-top:1150px'>
			<ol id='cmp1list' class='spacegrid'></ol>
		</div>			
	
   </div>			
   </div>	
</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
