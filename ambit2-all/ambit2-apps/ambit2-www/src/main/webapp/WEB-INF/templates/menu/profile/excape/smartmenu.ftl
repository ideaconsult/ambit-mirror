<#if custom_structurequery??>
	<#assign custom_structurequery=custom_structurequery?url('UTF-8')>
</#if>

			<ul id='smartmenu' class="sm sm-mint">
				<li>
				<a href=""${ambit_root}" title="Home">Home</a>
			</li>
			<li>
	<li><a href="${ambit_root}/ui/_search?search=${custom_structurequery!'phenol'}" title="Chemical structure search">Structure search</a></li>
	
</li>

<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/excape/help_menu.ftl">
</li>		
							
</ul>