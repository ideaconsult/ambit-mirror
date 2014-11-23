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


<style>
h1, h2, h3 {
	color: #8C0305;
	font-weight: bold; }
		
h4, h5, h6 {
	color: #8C0305;
	font-weight: normal; 
}

.h1, .h2, .h3 {
	color: #8C0305;
	font-weight: bold; }
		
.h4, .h5, .h6 {
	color: #8C0305;
	font-weight: normal; }
	
.enmhelp {
	font-size: 0.75em;
	font-weight: bold italic !important;
	color: #8C0305;
}	
</style>

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
			<div class="alpha" >
				<div class="remove-bottom h2">
						Welcome to eNanoMapper prototype database 
				</div>
			    <div class='enmhelp'> A substance database to support safe-by-design engineered nano materials</div>			
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
	
			<input class='eight columns omega half-bottom' type="text" id='search' value='[Au]' name='search'>
			<input class='three columns omega submit' type='submit' value='Search'>
		</div>

		<div class="fifteen columns remove-bottom" id="query">
			<div class="alpha">
			    <div class='chelp'>Advanced: 
			    <a href='${ambit_root}/ui/_search'>Structure search</a> |
			    <a href='${ambit_root}/substance'>Search nanomaterials by identifiers</a> |
			    <a href='${ambit_root}/query/study'>Search nanomaterials by endpoint data</a> 
			    
			    </div>			
		</div>
		</div>		
	</form>				
	</div>	

	
    
    

</div>   		



<div class='row add-bottom' style="height:400px;">&nbsp;</div>

    	<div  id="footer_logo">
<!--
			<a href='http://www.opentox.org' class='qxternal'  title='eNanoMapper leverages and extends OpenTox API and resources' target=_blank><img src='${ambit_root}/images/logo.png' border='0' width='115' height='60' alt='Logo OpenTox'></a>
			-->
			<a href='http://www.enanomapper.net' class='qxternal'  title='FP7 eNanoMapper web site' target=_blank><img src='${ambit_root}/images/profile/enanomapper/logo.png' border='0' alt='Logo eNanoMapper'></a>
			<p  class='chelp'>
			This project has received funding from the European Union's Seventh Framework Programme for research, technological development and demonstration under grant agreement no 604134.
			</p>
		</div>
		

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
