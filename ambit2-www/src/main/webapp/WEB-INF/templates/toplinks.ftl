<#escape x as x?html>
	<ul class='topLinks remove-bottom'>
	<#if username??>
		<li class='topLinks'>
		[<a class='topLinks login' title='My profile' href='${ambit_root}/myaccount'><b>${username}</b></a>]
		</li>
		<#if ambit_curator?? && ambit_curator>
		<!--
			<li class='topLinks'>
				&nbsp;|<a class='topLinks curator' href='${ambit_root}/curator' title='Click to view uncurated structures'>Curator</a>
			</li>
		-->				
		</#if>
		<#if ambit_admin?? && ambit_admin>
			<li class='topLinks'>
				&nbsp;| <a class='topLinks admin' href='${ambit_root}/admin' title='Go to admin tasks'>Admin</a> | 
			</li>
		</#if>			
		<li class='topLinks'>		
			<a class='topLinks login' title='You are currently logged in as "${username}". Click here to log out.' href='#' onClick='document.forms["logoutForm"].submit(); return false;'>Log out</a>&nbsp;
		</li>
	<#else>
		<li class='topLinks'>
			<a class='topLinks login' title='Log in' href='${ambit_root}/login'>Log in</a>
		</li>						
		</#if>			
	</ul>
</#escape>
