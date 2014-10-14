<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  <script type='text/javascript'>

$(document)
		.ready(
				function() {
					    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="AMBIT log in">Log in</a></li>');
    					jQuery("#breadCrumb").jBreadCrumb();
    					jQuery("#welcome").text("Welcome to AMBIT");
    					loadHelp("${ambit_root}","login");
				});
</script>
</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>


		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns remove-bottom" style="padding:0;" >
		
	    <div class="row remove-bottom ui-widget-header ui-corner-top">&nbsp;Sign In</div>
    	<div class="ui-widget-content ui-corner-bottom">
		<div style='margin:5px;padding:5px;'>
		<form method='post' action='${ambit_root}/provider/signin?targetUri=${ambit_root}/login' autocomplete='off'>
		
		<div class="row half-bottom">	&nbsp;
		<label class='eight columns alpha'>&nbsp;</label>
		<div class='three columns omega'>&nbsp;</div>
		<div class='five columns omega'>
			<a title='AMBIT registration' target=_blank href='${ambit_root}/register' class='h5' >Create an AMBIT account</a>
		</div>		
		</div>
		
		
		<div class="row remove-bottom">		
		<label class='three columns alpha'>User name</label> 
		<input class='five columns omega' type='text' size='40' name='login' value=''>
		</div>
		
		<div class="row remove-bottom">		
		<label class='three columns alpha'>Password</label> 
		<input class='five columns omega' type='password' size='40' name='password' value=''>
		<div class='three columns omega'>&nbsp;</div>
		<div class='five columns omega'><a href="${ambit_root}/forgotten" title='Click to request one time password reset'>Forgotten password?</a></div>
		</div>

		<div class="row remove-bottom">		
		<label class='five columns alpha'>&nbsp;</label>		
		<input class='three columns omega'  type="submit" value="Log in"><a href='#' class='chelp loginhelp'></a>
		<input type='hidden' size='40' name='targetURI' value='${ambit_root}/login'>
		</div>
		</form>
		
		</div>
		</div>				
		</div>
		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

	<div class='row add-bottom'>&nbsp;</div>

		<div class="row half-bottom" >		
			<div class='five columns alpha'>&nbsp;</div>
			<div class='six columns omega chelp ' style="box-shadow: 3px 3px 7px #999;border: 1px solid #ccc;padding: 25px 25px 25px 25px;background-color: #fafafa;"> 
			<span style='font-weight:bold;'>The open source chemoinformatic system AMBIT provides the following functions:</span>
			<br/>
			<ul>
			<li><a href='${ambit_root}/ui/_search'>Search</a> for structure(s) and meta data</li>
			<li>Assessment tools for read-across and category formation</li>
			<li>Prediction tools: <a href='${ambit_root}/ui/toxtree'>Cramer rules</a>, 
			<a href='${ambit_root}/ui/toxtree'>Protein binding</a>,
			<a href='${ambit_root}/ui/toxtree'>Carcinogenicity and mutagenicity</a>,
			<a href='${ambit_root}/algorithm/pka'>pKa</a>,
			<a href='${ambit_root}/algorithm/org.openscience.cdk.qsar.descriptors.molecular.XLogPDescriptor'>Log Kow</a>, 
			<a href='${ambit_root}/model'>etc.</a></li>
			<li>Tools for data analysis: <a href='${ambit_root}/algorithm?type=Regression'>regression</a>,
			<a href='${ambit_root}/algorithm?type=Classification'>classification</a>, 
			<a href='${ambit_root}/algorithm?type=Clustering'>clustering</a>,
			<a href='${ambit_root}/algorithm'>etc.</a>
			</li>
			<li>Meta data management: <a href='${ambit_root}/substance'>substances data</a>, flexible creation of dataset with chemical structures and properties by 
			<a href='${ambit_root}/ui/uploadstruc'>import</a>,
			<a href='${ambit_root}/dataset?page=0&pagesize=100'>etc.</a>, 
			
			</li>
			<li>Data exchange: manual or automated (<a href='http://ambit.sourceforge.net/api.html'  class='qxternal'>OpenTox API</a> 
			<a href='http://ambit.sourceforge.net/rest.html' class='qxternal'>REST</a> Web services)</li>
			</ul>
		
			</div>
		</div>		
		
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>