<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript'>

$(document)
		.ready(
				function() {
						loadHelp("${ambit_root}","about");
						jQuery("#breadCrumb").hide();
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->

<#include "/banner_crumbs.ftl">

	
<div class="three columns" id="query">
&nbsp;
</div>	

<div class="twelve columns " style="padding:0;" >
	<div class="row add-bottom">
	&nbsp;
	</div>
	<div class="row add-bottom">
		
			<div class="fifteen columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h2">
						QMRF chemical structure database
				</div>
			    <div class='help'>Chemical structure database for QMRF</div>			
			</div>
			</div>
	</div>	
		
	<div class="row add-bottom">&nbsp;</div>	
	
	<div class="row add-bottom">
		<form action="${ambit_root}/ui/_search?option=auto" id="searchForm"  method="GET" >	
			<div class="fifteen columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h4">
						Simple search
				</div>
			    <div class='chelp'>Enter chemical name, identifiers, SMILES, InChI</div>			
			</div>
			</div>
		<div class='row add-bottom'>
	
			<input class='eight columns omega half-bottom' type="text" id='search' value='' name='search'>
			<input class='three columns omega submit' type='submit' value='Search'>
		</div>

		<div class="fifteen columns remove-bottom" id="query">
			<div class="alpha">
			    <div class='chelp'>Advanced: 
			    <a href='${ambit_root}/ui/_search'>Structure search</a> |
			    </div>			
		</div>
		</div>		
	</form>				
	</div>	

	
    
    

</div>   		



<div class='row add-bottom' style="height:400px;">&nbsp;</div>

    	<div  id="footer_logo">
		
		</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
