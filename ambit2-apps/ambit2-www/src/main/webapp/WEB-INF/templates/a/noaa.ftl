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
		
			<div class="twelve columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h2">
					Welcome to AMBIT

				</div>
			    <div class='help'>Chemical structures database, properties prediction & machine learning with OpenTox REST web services API</div>			
			</div>
			</div>
			<div class="four columns remove-bottom h6" style="margin:5px; padding: 5px; background-color: #F5F2EB; box-shadow: 3px 3px 7px #999;" >
					<ul>
					<#if username??>
							<li>You are logged in as ${username}.</li>  
					<#else>						
							<li>Please configure AMBIT web application to enable log in.</li>
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
			    <a href='${ambit_root}/substance'>Search substances by identifiers</a> |
			    <a href='${ambit_root}/query/study'>Search substances by endpoint data</a> 
			    
			    </div>			
		</div>
		</div>		
	</form>				
	</div>	

	
    
    

</div>   		



<div class='row add-bottom' style="height:400px;">&nbsp;</div>

    	<div  id="footer_logo">
			<a href='http://www.cefic.be' class='qxternal' title='CEFIC' target=_blank><img src='${ambit_root}/images/logocefic.png' border='0' width='115' height='60' alt='Logo CEFIC'></a>&nbsp;
		
			<a href='http://www.cefic-lri.org' class='qxternal'  title='CEFIC LRI' target=_blank><img src='${ambit_root}/images/logolri.png' border='0' width='115' height='60' alt='Logo CEFIC-LRI'></a>&nbsp;
		
			<a href='http://www.opentox.org' class='qxternal'  title='OpenTox' target=_blank><img src='${ambit_root}/images/logo.png' border='0' width='115' height='60' alt='Logo OpenTox'></a>
		</div>


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
