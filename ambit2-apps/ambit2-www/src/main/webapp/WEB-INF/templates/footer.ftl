<div class="sixteen  columns add-bottom">&nbsp;</div>

<div class='row add-bottom' >
	<div class="five columns alpha ">&nbsp;</div>
	<div class="ten columns omega ui-state-error " id='log' style='display:none;' >&nbsp;</div> 
</div>


		<div id='footer-out' class="sixteen columns">
		<div id='footer-in'>
		<div id='footer'>
			<a class='footerLink' href='http://www.ideaconsult.net/'>IdeaConsult Ltd.</a> 
		</div>
		</div>
		</div>
		
		
<#if username??>
	    <form id='logoutForm' name='logoutForm'  action='${ambit_root}/provider/signout?targetUri=${ambit_root}&method=DELETE' method='POST'><input type='hidden' value="${username}"></input></form>
</#if>

	
  <script language="JavaScript">
  function errorHandler(service, status, jhr) {
  	try {
  		jQuery("#log").show();
    	jQuery("#log").text(" error on [" + service + "]: " + status + " " + jhr.statusText);
    } catch (e) {}
  }
  </script>
  
  
  