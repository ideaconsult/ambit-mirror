<div class="sixteen columns" id="header">
		
	<ul class='topLinks'>
	<li class='topLinks'>
		<a class='topLinks' href='http://ambit.sf.net/'>Help</a>
	</li>
	<li class='topLinks'>|</li>
	<li class='topLinks'>
		<#if username??>
			<a class='topLinks login' title='You are currently logged in as "${username}". Click here to log out.' href='#' onClick='document.forms["logoutForm"].submit(); return false;'>Log out [<b>${username}</b>]</a>			   
			<form id='logoutForm' action='${ambit_root}/protected/signout?targetUri=.' method='POST'></form>
		<#else>
			<a class='topLinks login' title='Log in' href='${ambit_root}/opentoxuser'>Log in</a>
			<li class='topLinks'>|</li>
			<li class='topLinks'>
			<a class='topLinks register' title='Register' href='http://opentox.org/join_form'>Register</a>
			</li>				
		</#if>			
	</li>
	</ul>

</div>
		<div class="three columns">
			<a href='http://ambit.sf.net/'>
				<img class='scale-with-grid' border='0' src='${ambit_root}/images/ambit-logo.png' title='http://ambit.sf.net/' alt='AMBIT logo'>
			</a>
			<h5 style='color:#729203;'>v2.4.10</h5>
		</div>
		<#include "/banner_search.ftl">
		<div class="sixteen columns" >
			<div id="header_bottom" class="remove-bottom">&nbsp;</div>
		</div>
