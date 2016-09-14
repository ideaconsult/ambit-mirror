<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >


<style>
.search_options { list-style-type: none; margin: 0; padding: 0; width: 600px; }
.search_options li { margin: 3px 3px 3px 0; padding: 20px 5px 5px 5px; float: left; width: 140px; height: 120px; font-size: 1em; vertical-align: middle; text-align: center; box-shadow: 5px 6px 6px #729203; border: 1px solid #729203; background-color: #fafafa; }
</style>


<script type='text/javascript'>

$(document)
		.ready(
				function() {
						loadHelp("${ambit_root}","about");
						jQuery("#breadCrumb").hide();
						$( ".search_options" ).sortable();
					    $( ".search_options" ).disableSelection();
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
						${custom_title!"Welcome to AMBIT"}
				</div>
			    <div class='help'>${custom_description!"Chemical structures database, properties prediction & machine learning with OpenTox REST web services API"}</div>		
			    	
			</div>
			</div>
	</div>	
		
	<div class="row add-bottom">&nbsp;</div>	
	
	<div class="row add-bottom">
		<form action='${service_search!"${ambit_root}/ui/_search"}' id="searchForm"  method="GET" >
			<div class="fifteen columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h4">
						Search chemical structures
				</div>
			    <div class='chelp'>Enter identifier</div>			
			</div>
			</div>
		<div class='row add-bottom'>
	
			<input class='eight columns omega half-bottom' type="text" id='search' value='formaldehyde' name='search'>
			<input class='three columns omega submit' type='submit' value='Search'>
		</div>

		<div class="sixteen columns remove-bottom" id="query">

			<div class="alpha nine columns" style="padding: 0px 5px 5px 5px"> 
				<ul class='search_options'>
			    <li><a href="${ambit_root}/ui/_search?option=similarity&search=c1ccccc1O" title="Search chemical structures "><br/>Chemical similarity search</a> <a href='#' class='chelp stra'>?</a></li>

			    <li><a href="${ambit_root}/ui/_search?option=smarts&search=%5BCX3H1%5D(%3DO)%5B%236%5D" title="Substructure search "><br/>Substructure search</a> <a href='#' class='chelp stra'>?</a></li>
			    <li><a href='${ambit_root}/substance'>Browse chemical substances and studies</a> <a href='#' class='chelp substancesearch'>?</a> <br/>
				<li><a href="${ambit_root}/substance?type=name&search=glutaraldehyde&page=0&pagesize=20" title="Search substances by name">Search substances by name</a> <a href='#' class='chelp substancesearch'>?</a>
			    <li><a href="${ambit_root}/ui/toxtree" title="Toxtree prediction">Toxtree predictions</a> <a href='#' class='chelp toxtree'>?</a>
   			    <li><a href="${ambit_root}/query/study" title="Search substances by physico-chemical parameters or biological effects">Search substances by endpoint data</a> <a href='#' class='chelp substancesearch'>?</a></li>
			    </li>
				</ul>
			</div>
			<div class="one columns omega" style='padding:0;margin:0;'>&nbsp;</div>
			<div class="five columns omega" style=" box-shadow: 3px 3px 7px #999;border: 1px solid #ccc;padding: 1em 25px 25px 25px;background-color: #fafafa;">
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
				<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>		
			</div>
			</div>
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
