<div class="sixteen  columns add-bottom">&nbsp;</div>
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
						
<script type="text/javascript">
	var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
	document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
		
