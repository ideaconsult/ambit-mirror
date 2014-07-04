<#include "/html.ftl">
<head>
  <#include "/header_updated.ftl">
  
<script type='text/javascript' src='${ambit_root}/jquery/jquery.validate.min.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/a/myprofile.js'></script>
<script type='text/javascript' src='${ambit_root}/scripts/jopentox.js'></script>
<script type='text/javascript'>

$().ready(function() {
	// validate the comment form when it is submitted
	setAutocompleteOrgs("${ambit_root}","#affiliation");
	
	$("#registerForm").validate({
		rules : {
			'username': {
				required : true,
				minlength: 3,
				maxlength: 16
			},		
			'firstname': {
				required : true
			},
			'lastname': {
				required : true
			},
			'affiliation': {
				required : true
			},		
			'email': {
				required : true,
				email: true
			},
			'homepage': {
				url: true
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
			'username'  : {
				required: " Please enter user name"
			},
			'firstname' : " Please provide your first name",
			'lastname'  : {
				required: " Please provide your last name"
			},
			'affiliation'  : {
				required: "Please provide your affiliation"
			},					
			'email'     : {
				required: " Please provide valid e-mail",
				email: " Please provide valid e-mail"
			},
			'homepage'  : {
				url: " Please provide valid web address"
			},
			'pwd1'      : {
				required: " Please provide a password",
				minlength: " Your password must be at least 6 characters long"
			},
			'pwd2'      : {
				required: " Please confirm the password",
				minlength: " Your password must be at least 6 characters long",
				equalTo: " Please enter the same password as above"
			}			
		}
	});
});
	
</script>
<script type="text/javascript">
jQuery(document).ready(function()
{
	jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/login" title="AMBIT log in">Log in</a></li>');
    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/register" title="AMBIT registration">Register</a></li>');
    jQuery("#breadCrumb").jBreadCrumb();
    loadHelp("${ambit_root}","register");
})
</script>
</head>

<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
&nbsp;
</div>

		<!-- Page Content
		================================================== -->
		<div class="eleven columns add-bottom" style="padding:0;" >	
		
		<div class='ui-widget-header ui-corner-top'>&nbsp;AMBIT Registration</div>
	    <div class='ui-widget-content ui-corner-bottom'>	
				
			<div style="margin:5px;padding:5px;">	
			<form action="${ambit_root}/register" id="registerForm"  method="POST" autocomplete="off">		
			
			<div class='row remove-bottom'>
				<label class='three columns alpha' for="username">User name <em>*</em></label>
				<input class='three columns alpha half-bottom' type="text" size='40' name='username' id='username' value=''/>
				<div class='ten columns omega'><a href='#' class='chelp husername'></a></div>
			</div>
			<div class='row remove-bottom'>
				<label class='three columns alpha' for="title">Title</label>
				<input class='three columns alpha half-bottom'  type="text" size='40' name='title' id='title' value=''/>
				<div class='ten columns omega'></div>
			</div>
			<div class='row remove-bottom'>
				<label class='three columns alpha' for="firstname">First name <em>*</em></label>
				<input class='eight columns alpha half-bottom' type="text" size='40' name='firstname' id='firstname' value=''/>
				<div class='five columns omega'></div>
			</div>
			<div class='row remove-bottom'>
				<label class='three columns alpha' for="lastname">Last name <em>*</em></label>
				<input class='eight columns alpha half-bottom' type="text"  size='40' name='lastname' id='lastname' value=''/>
				<div class='five columns omega'></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for="affiliation">Affiliation<em>*</em></label>
				<input class='eight columns alpha half-bottom' type="text"  size='40' name='affiliation' id='affiliation' value=''/>
				<div class='five columns omega'><a href='#' class='chelp horganisation'></a></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for="email">e-mail <em>*</em></label>
				<input class='eight columns alpha half-bottom' type="text"size='40' name='email' id='email' value=''/>
				<div class='five columns omega'></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for="homepage">WWW</label>
				<input class='eight columns alpha half-bottom' type="text"  size='40' name='homepage' id='homepage' value=''/>
				<div class='five columns omega'></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for="keywords">Keywords</label>
				<input class='eight columns alpha half-bottom'  type="text"  size='40' name='keywords' id='keywords' value=' '/>
				<div class='five columns omega'><a href='#' class='chelp hkeywords'></a></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for="reviewer">I would like to be able to upload data<a href='#' class='chelp hcurator'></a></label>
				<input class='eight columns alpha half-bottom' type="checkbox" name='reviewer' id='reviewer' value='' style='width:3em;'/>
				<div class='five columns omega'><a href='#' class='chelp hcurator'></a></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for='pwd1'>Password <em>*</em></label>
				<input class='six columns alpha half-bottom'  type='password' size='40' id='pwd1' name='pwd1' value=''/>
				<div class='five columns omega'></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'  for='pwd2'>Confirm password <em>*</em></label>
				<input class='six columns alpha half-bottom' type='password' size='40' id='pwd2' name='pwd2' value=''/>
				<div class='five columns omega'></div>
			</div>				
			<div class='row remove-bottom'>
				<label class='three columns alpha'>&nbsp;</label>
				<input class='three columns alpha' id='register' name='register' type='submit' class='submit' value='Register'>
				<div class='ten columns omega'></div>
			</div>
			</form>	
		</div>			
		</div>

		<!-- twelve -->
		</div>
		
		
		<!-- Right column and footer
		================================================== -->

<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>