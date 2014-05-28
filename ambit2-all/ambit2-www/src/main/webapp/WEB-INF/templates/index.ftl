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

<div class="eleven columns " style="padding:0;" >
	<div class="row add-bottom">
	&nbsp;
	</div>
	<div class="row add-bottom">
		
			<div class="thirteen columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h3">
						Welcome to AMBIT 
				</div>
			    <div class='h6'>Chemical structures database, properties prediction & machine learning with OpenTox REST web services API</div>			
			</div>
			</div>
	</div>	
		
	<div class="row add-bottom">	
	
	<form action="${ambit_root}/ui/_search?option=auto" id="searchForm"  method="GET" >		
	
	<div class='row add-bottom'>
		<input class='eight columns omega half-bottom' type="text" id='search' value='' name='search'>
		<input class='three columns omega submit' type='submit' value='Search'>
	</div>
	</form>	
	
	
    </div>	
    
    

</div>   		


<#include "/chelp.ftl" >

<div class='row add-bottom' style="height:400px;">&nbsp;</div>
    <div class="row ">
    	<div class="four columns" id="query">&nbsp;</div>
    	<div class='twelve columns omega'>
			<a href='http://www.cefic.be' class='qxternal' title='CEFIC' target=_blank><img src='${ambit_root}/images/logocefic.png' border='0' width='115' height='60' alt='Logo CEFIC'></a>&nbsp;
		
			<a href='http://www.cefic-lri.org' class='qxternal'  title='CEFIC LRI' target=_blank><img src='${ambit_root}/images/logolri.png' border='0' width='115' height='60' alt='Logo CEFIC-LRI'></a>&nbsp;
		
			<a href='http://www.opentox.org' class='qxternal'  title='OpenTox' target=_blank><img src='${ambit_root}/images/logo.png' border='0' width='115' height='60' alt='Logo OpenTox'></a>
		</div>
	</div>	

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
