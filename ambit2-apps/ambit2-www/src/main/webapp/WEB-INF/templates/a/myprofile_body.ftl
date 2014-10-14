<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >
<#include "/a/users_head.ftl" >

<script type="text/javascript">
jQuery(document).ready(function()
{
	<#if myprofile>
		setAutocompleteOrgs("${ambit_root}","#affiliation");
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/myaccount" title="My AMBIT account">My account</a></li>');
	<#else>
		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/users" title="All Users">Users</a></li>');
	    jQuery("#breadCrumb ul").append('<li><a href="${ambit_request}" id="breadCrumbUser" title="User">User</a></li>');
	</#if>   
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
		<div class="eleven columns add-bottom" >
		
		<#if myprofile>
			<#assign ro=''>
			<#assign disabled=''>
		<#else>
			<#assign ro='readonly'>
			<#assign disabled='disabled="disabled"'>
		</#if>
		<form action="${ambit_root}/myaccount/?method=put" id="form_myaccount" method="POST" autocomplete="off" >		

    	<div class="row remove-bottom ui-widget-header ui-corner-top"> <a href='#' id='useruri'></a></div>
    	<div class="half-bottom ui-widget-content ui-corner-bottom" >
    			
		<div class='row' style="margin:5px;padding:5px;"> 	
		
			<label class='three columns alpha' for="username">User name</label>
			<span class="six columns alpha remove-bottom" id='username'></span>
			<div class="seven columns omega remove-bottom">
			<#if myprofile>
					<a href="${ambit_root}/myaccount/reset">Change password</a><a href='#' class='chelp pwd'></a>&nbsp;
			<#else>	
					<a id="protocoluri" href="#">Datasets</a><a href='#' class='chelp datasets'></a>&nbsp;
			</#if>
			</div>
		</div>
		
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label for="title" class='three columns alpha'>Title</label>
			<input class="three columns alpha remove-bottom" type="text" ${ro} size='40' name='title' id='title' value=''/>
			<div class="ten columns omega">&nbsp;</div>
		</div>
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="firstname">First name</label>
			<input class="eight columns alpha remove-bottom" type="text" ${ro} size='40' name='firstname' id='firstname' value=''/>
			<div class="five columns omega">&nbsp;</div>
		</div>
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="lastname">Last name</label>
			<input class="eight columns alpha remove-bottom" type="text" ${ro} size='40' name='lastname' id='lastname' value=''/>
			<div class="five columns omega">&nbsp;</div>
		</div>
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="affiliation">Affiliation</label>
			<input class="eight columns alpha remove-bottom" type="text" ${ro} size='40' name='affiliation' id='affiliation' value=''/>
			<div class="five columns omega">&nbsp;</div>
		</div>		
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="email">e-mail</label>
			<input class="eight columns alpha remove-bottom" type="text" ${ro} size='40' name='email' id='email' value=''/>
			<div class="five columns omega">&nbsp;</div>
		</div>
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="homepage">WWW</label>
			<input class="eight columns alpha remove-bottom" type="text" ${ro} size='40' name='homepage' id='homepage' value=''/>
			<div class="five columns omega">&nbsp;</div>
		</div>		
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="keywords">Keywords</label>
			<input class="eight columns alpha remove-bottom" type="text" ${ro} size='40' name='keywords' id='keywords' value=''/>
			<div class="five columns omega">&nbsp;</div>
		</div>		
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="reviewer">I would like to be able to upload data<a href='#' class='chelp hcurator'></a></label>
			<input class="one columns alpha remove-bottom" type="checkbox" ${disabled} name='reviewer' id='reviewer' value='reviewer' />
			<div class="seven columns alpha">&nbsp;
			</div>
			<div class="five columns omega">&nbsp;</div>
		</div>		
		<div class='row' style="margin:5px;padding:5px;"> 	
			<label class='three columns alpha' for="update">&nbsp;</label>
			<#if myprofile>
				<input class="submit three columns alpha" id='update' name='update' type='submit' value='Update'>
				<a href='#' class='chelp myprofile'></a>
			<#else>
				<a href='${ambit_root}/myaccount' class="button three columns alpha" id='edit' style='display:none;' name='edit'>Edit profile</a>
			</#if>
		</div>	

		</form>
		</span>
		</div>
		<!-- twelve columns  -->

		</div> 
		
		<!-- Right column and footer
		================================================== -->
<#include "/chelp.ftl" >

<div class='row add-bottom'>&nbsp;</div>
<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>