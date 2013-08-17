<#include "/html.ftl">
<head>
  <#include "/header.ftl">
  <script type='text/javascript'>

$(document)
		.ready(
				function() {
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
		<div class="remove-bottom h3">
				OpenTox log in
		</div>
	    <div class='h6'>via OpenAM identity service at ${openam_service}</div>			
	    </div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
</div>


		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns remove-bottom" style="padding:0;" >
		
	    <div class="row remove-bottom ui-widget-header ui-corner-top">
	    <#if openam_token??>
	    &nbsp;Welcome to OpenTox
	    <#else>
	    &nbsp;Log in to OpenTox
	    </#if>
	    </div>
    	<div class="half-bottom ui-widget-content ui-corner-bottom">
		<div style='margin:5px;padding:5px;'>
		<form method='post' action='${ambit_root}/openssouser?targetUri=${ambit_root}/login' autocomplete='off'>
		
		
		
		<div class="row">		
		<label class='three columns alpha'>OpenAM service</label> 
		<div class='thirteen columns omega'>
			${openam_service}
		</div>
		</div>
		
		<div class="row remove-bottom">		
		<label class='three columns alpha'>OpenTox User name</label> 
		<#if username??>
		<div class='five columns omega add-bottom'>${username}</div>
		<#else>
		<input class='five columns omega' type='text' size='40' name='user' value=''>
		</#if>
		<div class='eight columns omega'></div>
		</div>
		
		<#if openam_token??>
		<div class="row">		
		<label class='three columns alpha'>OpenAM token</label> 
		<div class='thirteen columns omega'>
			${openam_token}
		</div>	
		</div>
		</#if>
		
		<#if username??>
		<#else>
		<div class="row remove-bottom">		
		<label class='three columns alpha'>Password</label> 
		<input class='five columns omega' type='password' size='40' name='password' value=''>
		<!--
		<div class='eight columns omega'><a href="${ambit_root}/forgotten">Forgotten password?</a></div>
		-->
		</div>
		</#if>

		<#if username??>
		<#else>
		<div class="row half-bottom">		
		<label class='three columns alpha'>&nbsp;</label>		
		<div class='eight columns omega'>
		<input type=CHECKBOX name='subjectid_secure' CHECKED>
		Use secure cookie for the OpenSSO token
		</div>
		</div>
		
		<div class="row remove-bottom">		
		<label class='five columns alpha'>&nbsp;</label>		
		<input class='three columns omega'  type="submit" value="Log in">
		<input type='hidden' size='40' name='targetURI' value='${ambit_root}/openssouser'>
		</div>
		
		</#if>
		
		</form>
		
		</div>
		</div>				
		</div>
		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>