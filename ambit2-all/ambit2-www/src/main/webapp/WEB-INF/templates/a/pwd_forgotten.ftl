<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/a/myprofile.js'></script>
<script type='text/javascript'>

$().ready(function() {
	// validate the reset form when it is submitted
	$("#pwdresetForm").validate({
		rules : {
			'username': {
				required : true,
				minlength: 3,
				maxlength: 16
			},		
			'email': {
				required : true,
				email: true
			}
		},
		messages : {
			'username'  : {
				required: " Please enter user name"
			},
			'email'     : {
				required: " Please provide valid e-mail",
				email: " Please provide valid e-mail"
			}
		}
	});
});
	
</script>
<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="AMBIT log in">Log in</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/register" title="AMBIT password reset">Request password reset</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","pwd_forgotten");
})
</script>
</head>

<body>


<div class="container" style="margin:0;padding:0;">

<#include "/banner_crumbs.ftl">

<div class="two columns remove-bottom" style="padding:0;" >&nbsp;
</div>

		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns add-bottom" style="padding:0;" >	
		
		<div class='ui-widget-header ui-corner-top'>&nbsp;AMBIT password reset</div>
	    <div class='ui-widget-content ui-corner-bottom'>	
				
			<div style="margin:5px;padding:5px;">	
			<form action="${ambit_root}/forgotten" id="pwdresetForm"  method="POST" >		
			
			<div class='row remove-bottom'>
				<label class='three columns alpha' for="username">User name <em>*</em></label>
				<input class='three columns alpha half-bottom' type="text" size='40' name='username' id='username' value=''/>
				<div class='ten columns omega'></div>
			</div>
		
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for="email">e-mail <em>*</em></label>
				<input class='eight columns alpha half-bottom' type="text"size='40' name='email' id='email' value=''/>
				<div class='ten columns omega'></div>
			</div>				
		
			<div class='row remove-bottom'>
				<label class='three columns alpha'>&nbsp;</label>
				<input class='three columns alpha' id='register' name='register' type='submit' class='submit' value='Reset'>
				<div class='ten columns omega'><a href='#' class='chelp reset'></a></div>
			</div>
			</form>	
		</div>			
		</div>

		<!-- twelve -->
		</div>
		
		

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>