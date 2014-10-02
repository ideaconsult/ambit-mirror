<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>
<script type='text/javascript'>


$().ready(function() {
	// validate the comment form when it is submitted
	$("#pwdForm").validate({
		rules : {
			'uri': {
				required : true,
				minlength: 6
			},
			'username': {
				required : true,
				minlength: 3
			},					
			'pwd1': {
				required : true,
				minlength: 6
			},
			'pwd2': {
				required : true,
				minlength: 6,
				equalTo: "#pwd1"
			}
			
		},
		messages : {
			'uri'   : {
				required: "Please provide user URI"
			},
			'username'   : {
				required: "Please provide user name"
			},							
			'pwd1'   : {
				required: "Please provide a new password",
				minlength: "Your password must be at least 6 characters long"
			},
			'pwd2'   : {
				required: "Please provide a new password",
				minlength: "Your password must be at least 6 characters long",
				equalTo: "Please enter the same password as above"
			}			
		}
	});
});
	
</script>
<style type="text/css">
#pwdForm label.error {
	margin-left: 10px;
	width: auto;
	display: inline;
}
</style>

<script type="text/javascript">
jQuery(document).ready(function()
{
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin" title="Admin">Admin</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/admin/user/reset" title="Change password">Change user password</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
     loadHelp("${ambit_root}","myprofile");
})
</script>
</head>
<body>

<div class="container columns" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>

		
		<!-- Page Content
		================================================== -->
		<div class="eleven columns add-bottom" style="padding:0;" >
		

		<div class='ui-widget-header ui-corner-top'>Password change</div>
	    <div class='ui-widget-content ui-corner-bottom'>					
			<form action="${ambit_root}/admin/user/reset?method=PUT" id="pwdForm"  method="POST" autocomplete="off">	
			<div class='row half-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' for="pwdold">User URI</label>
				<input class="five columns alpha half-bottom" type='text' size='40' id='uri' name='uri' value='' required/>
				<div class="eight columns omega">&nbsp;</div>
			</div>	
			<div class='row half-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' for="pwdold">User name</label>
				<input class="five columns alpha half-bottom" type='text' size='40' id='username' name='username' value='' required/>
				<div class="eight columns omega">&nbsp;</div>
			</div>				
			<div class='row half-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' for="pwd1">New password</label>
				<input class="five columns alpha half-bottom" type='password' size='40' id='pwd1' name='pwd1' value=''/>
				<div class="eight columns omega">&nbsp;</div>
			</div>		
			<div class='row half-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' for="pwd2">Confirm new password</label>
				<input class="five columns alpha half-bottom"  type='password' size='40' id='pwd2' name='pwd2' value=''/>
				<div class="eight columns omega">&nbsp;</div>
			</div>		
			<div class='row half-bottom' style="margin:5px;padding:5px;"> 	
				<label class='three columns alpha' >&nbsp;</label>
				<input class="three columns alpha half-bottom" id='updatepwd' name='updatepwd' type='submit' class='submit' value='Update'>
				<div class="eleven columns omega half-bottom">&nbsp;</div>
			</div>				
			</form>		
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