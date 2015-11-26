<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  <script type='text/javascript' src='${ambit_root}/jquery/purl.js'></script>
  
<script type='text/javascript'>

function loginDefault() {
	$("#user").val("guest");
	$("#pwd").val("guest");
	$("#ssoform").submit();
}
</script>  
<script type='text/javascript'>

$(document)
		.ready(
				function() {
					var purl = $.url();
					$('#targetUri').attr('value',purl.param('targetUri')===undefined?'${ambit_root}':purl.param('targetUri'));
					
						jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="OpenTox log in">Log in</a></li>');
	   					jQuery("#breadCrumb").jBreadCrumb();
	   					jQuery("#welcome").text("OpenTox log in");
    					loadHelp("${ambit_root}","openam");
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
		<div id="tabs" class="eight columns remove-bottom" style="padding:0;" >
		
		
	    <div id='login_tab' class="row remove-bottom ui-widget-content ui-corner-top" style='padding:10px 10px 10px 10px;'>
	    <#if openam_token??>
		<span style='font-size:1.2em;font-weight:bold;'>&nbsp;Welcome to OpenTox</span> 
	    <br/>(via <a href='${openam_service}' target=_blank>OpenAM ${openam_service}</a> <a href='#' class='chelp openam'></a> identity service)	    
	    <#else>
	    <span style='font-size:1.2em;font-weight:bold;'>&nbsp;Log in to OpenTox</span> 
	    <br/>(via <a href='${openam_service}' target=_blank>OpenAM ${openam_service}</a> <a href='#' class='chelp openam'></a> identity service)
	    </#if>
	    </div>
    	<div class="ui-widget-content ui-corner-bottom">
		<div style='margin:5px;padding:5px;'>
		<form method='post' action='${ambit_root}/opentoxuser' id='ssoform' autocomplete='off'>
		
		<div class="row remove-bottom">		
		<label class='four columns alpha'>OpenTox User name</label> 
		<#if username??>
			<div class='six columns omega add-bottom'>${username}</div>
		<#else>
			<input class='six columns omega' type='text' size='40' name='user' id='user' value=''>
			<div class='one column omega'>&nbsp;</div>
		
		</#if>

		</div>
		
		<#if username??>
		
		<div class="row remove-bottom">
		
		<label class='four columns alpha'>OpenTox profile</label> 
	
		<div class='twelve columns omega'>
			<a href="http://opentox.org/personalize_form" target="opentox" class="help qxternal">Personal preferences</a> 
			<a href="http://opentox.org/password_form" target="opentox" class="help qxternal">Change password</a>
		</div>
		
		</div>
		
		
		<#else>
		<div class="row remove-bottom">		
		<label class='four columns alpha'>Password</label> 
		<input class='six columns omega' type='password' size='40' name='password' id='pwd' value=''>
		<div class='one columns omega'>&nbsp;</div>
		
		</div>
		</#if>

		<#if username??>
		<#else>
		
		<div class="row remove-bottom">		
		<label class='four columns alpha'>&nbsp;</label>		
		<input class='three columns omega'  type="submit" value="Log in">
		
		<div class='three columns omega'  >&nbsp;
		<input type='hidden' size='40' name='targetUri' id='targetUri' value='${ambit_root}/ui'>
		</div>

		</div>
		
		</#if>
		
		</form>
		</div>
		</div>		
		</div>
		

		<div class="two columns" style='padding:0;margin:0;'>&nbsp;
		</div>
		<!-- Right column and footer
		================================================== -->
		<div class="four columns" style='padding:0;margin:0;'>
		
		<#if username??>
		<#else>
		<div class='row half-bottom'>
			<a href="#" onClick="loginDefault();" style='font-size:1.2em;font-weight:bold;'>Log in as guest</a>
			<br/>		
			<a href="http://opentox.org/mail_password_form?userid=" class="qxternal">Forgotten password?</a>
		</div>
		</#if>
					
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
		<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
		</div>
		<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
		</div>		
		</div>


<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>