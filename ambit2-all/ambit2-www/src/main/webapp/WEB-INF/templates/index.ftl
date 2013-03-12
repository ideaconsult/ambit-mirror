<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
						loadHelp("${ambit_root}","about");
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
			<h3 class="remove-bottom">
					Welcome to AMBIT 
			</h3>
		    <h6>Chemical structures database, properties prediction & machine learning with OpenTox REST web services API</h6>			
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

		
	<div style="margin:50px 5px 5px 5px;padding:5px;">	
	
	<form action="${ambit_root}/ui/query?option=auto" id="searchForm"  method="GET" >		
	
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="search">&nbsp;</label>
		<input class='eight columns omega half-bottom' type="text" id='search' value='' name='search'>
		<input class='three columns omega' type='submit' class='submit' value='Search'>
	</div>
	</form>	
	
	
   </div>			

<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >

</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
