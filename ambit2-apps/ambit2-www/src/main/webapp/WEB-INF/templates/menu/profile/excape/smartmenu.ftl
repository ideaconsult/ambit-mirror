<#if custom_structurequery??>
	<#assign custom_structurequery=custom_structurequery?url('UTF-8')>
</#if>

<ul id='smartmenu' class="sm sm-mint">
<li><a href="https://sandbox.ideaconsult.net/search/excape/" title="Home">Home</a></li>
<li><a href="#" title="Search">Search</a>
<ul>
<li><a href="${ambit_root}/ui/_search?search=${custom_structurequery!'phenol'}" title="Chemical structure search">Structure search</a></li>
<li><a href='${service_search!"${ambit_root}/ui/_search?option=auto"}'>Free text search</a>	
</ul>
</li>

<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/excape/help_menu.ftl">
</li>		
							
</ul>