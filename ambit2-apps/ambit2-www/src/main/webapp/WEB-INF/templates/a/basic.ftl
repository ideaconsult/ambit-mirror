<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<style>
h1, h2, h3 {
	color: #000000;
	font-weight: bold; }
		
h4, h5, h6 {
	color: #000000;
	font-weight: normal; 
}

.h1, .h2, .h3 {
	color: #000000;
	font-weight: bold; }
		
.h4, .h5, .h6 {
	color: #000000;
	font-weight: normal; }
	
	
.enmhelp {
	font-size: 0.75em;
	font-weight: bold italic !important;
	color: #000000;
}	

.mhelp {
}

.mhelp a:link {
    text-decoration: none;
}

.mhelp a:visited {
    text-decoration: none;
}	
	
</style>	
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
		
			<div class="twelve columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h2">
					${custom_title!"Welcome to AMBIT"}

				</div>
			    <div class='enmhelp help'>${custom_description!"Chemical structures database, properties prediction & machine learning with OpenTox REST web services API"}</div>			
			</div>
			</div>
			<div class="four columns remove-bottom h6" style="margin:5px; padding: 5px; background-color: #F5F2EB; box-shadow: 3px 3px 7px #999;" >
					<ul>
					<#if username??>
							<li>You are logged in as ${username}.</li>  
							<#if method=="DELETE">
								<li>Close the browser tabs to log out.</li>
							</#if>
					<#else>						
							<li>You are not logged in.</li>
							<li>User and password will be requested only on data upload or launching models.</li>  
					</#if>
					</ul>			
			</div>
	</div>	
		
	<div class="row add-bottom">&nbsp;</div>	
	
	<div class="row add-bottom">
		<form action="${ambit_root}/ui/_search?option=auto" id="searchForm"  method="GET" >	
			<div class="fifteen columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h4">
						free text search
				</div>
			    <div class='chelp'>Enter chemical name, identifiers, SMILES, InChI</div>			
			</div>
			</div>
		<div class='row add-bottom'>
	
			<input class='eight columns omega half-bottom' type="text" id='search' value='${custom_query!"phenol"}' name='search'>
			<input class='three columns omega submit' type='submit' value='Search'>
		</div>

		</div>		
	</form>				
	</div>	

	
    
    

</div>   		



<div id='footer-out' class="sixteen columns">
	<div id='footer-in'>
		<div id='footer'>
			<a class='footerLink' href='http://www.ideaconsult.net/'>IdeaConsult Ltd.</a> 
		</div>
	</div>
</div>

</div> <!-- container -->
</body>
</html>
