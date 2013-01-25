<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<#assign w=250/>
<#assign h=250/>
<script src="${ambit_root}/jquery/jquery.imagemapster.min.js" type="text/javascript"></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox-ui-atoms.js'></script>

<#if cmpid??>
<script type='text/javascript'>
	
function atomNumber(num) {
	//console.log(num);
}	
	 
$(document)
	.ready(function() {
			var cmpURI = "${ambit_root}/compound/${cmpid}/imagejson";
			createImageMap(cmpURI, '${cmpid}', '#i${cmpid}', '#m${cmpid}');
	 });
</script>
</#if>
<script type='text/javascript'>
	//var cmp = readCompound("${ambit_root}","${ambit_request_json}");
	 
$(document)
	.ready(function() {
			$( "#selectable" ).selectable( "option", "distance", 18);
	 });
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<form method='GET' name='searchform' id='searchform' action='${ambit_root}/algorithm' style='padding:0;margin:0;'>
<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="ten columns alpha">
			<h3 class="remove-bottom">
					Chemical compound
			</h3>
		    <h6>Chemical compound</h6>			
		</div>
		<div class="four columns omega">
			<h3 class="remove-bottom">
				&nbsp;
			</h3>
		    <h6>
		    	<input type='text'  id='search' name='search' value='' tabindex='1' >
		    </h6>			
		</div>		
		<div class="two columns omega">
			<h3 class="remove-bottom">
				&nbsp;
			</h3>
		    <h6>
		    	<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
		    </h6>			
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

<div class="thirteen columns remove-bottom" style="padding:0;" >


		<!-- Page Content
		================================================== -->

		<div class="row" style="padding:0;" >			
			<div class="ui-widget-header ui-corner-top">Chemical compound at <a href='${ambit_root}/compound/${cmpid}'>${ambit_root}/compound/${cmpid}</a></div>
			<div class="ui-widget-content ui-corner-bottom">
				<div style="margin:5px;padding:5px;">	
				
				<img id="i${cmpid}" src='${ambit_root}/compound/${cmpid}/image?w=${w}&h=${h}' width='${w}' height='${h}' title='${cmpid}'  usemap="#m${cmpid}" />
				<map id="m${cmpid}" name="m${cmpid}"/>
				</div>
			</div>
		</div>	
		


<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
