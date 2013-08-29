<#escape x as x?html>
	<ul class='topLinks remove-bottom'>
	<li class='topLinks'>
		<a class='topLinks' href='http://ambit.sf.net/' class='qxternal' >AMBIT @ sourceforge.net</a>
	</li>	
	<li class='topLinks'>|</li>
	<li class='topLinks'>
		<a class='topLinks' href='${ambit_root}/help'>Help</a>
	</li>		
	<li class='topLinks'>|</li>
	<#if username??>
		<li class='topLinks'>
		<a class='topLinks login' title='You are currently logged in as "${username}". Click here to log out.' href='#' onClick='document.forms["logoutForm"].submit(); return false;'>Log out</a>&nbsp;
		<a class='topLinks login' title='My profile' href='${ambit_root}/opentoxuser'>[<b>${username}</b>]</a>
		</li>
	<#else>
		<li class='topLinks'>
			<a class='topLinks login' title='OpenTox Log in' href='${ambit_root}/opentoxuser'>Log in</a>
		</li>						
		<li class='topLinks'>|</li>
		<li class='topLinks'>
		<a class='topLinks register' title='Register' target=_blank href='http://opentox.org/join_form' class='qxternal' >Register</a></li>				
		</#if>			
	</ul>
</#escape>