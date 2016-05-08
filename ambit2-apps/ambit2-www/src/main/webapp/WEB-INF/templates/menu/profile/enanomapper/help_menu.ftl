<ul>
	<li> <a href="#">User guide</a>
		<ul>
		<li><a href='http://ambit.sourceforge.net/enanomapper_usage.html' target='guide'>Search by composition</a></li>
		<li><a href='http://ambit.sourceforge.net/enanomapper_usage_substance.html' target='guide'>Search by phys chem or biological effects</a></li>
		<li><a href='http://ambit.sourceforge.net/enanomapper.html' target='guide'>Downloads and Installation</a></li>
		
		</ul>
	</li>
	<li><a href='https://github.com/enanomapper/data.enanomapper.net/issues' target='report' title='Found an error? Report here'>Submit an issue <img src='${ambit_root}/images/profile/enanomapper/enm_api_24.png'></a></li>	
	
	<li>
		<a href="${ambit_root}/depict">Demo</a>
		<#include "/menu/profile/enanomapper/demo_menu.ftl">
	</li>			
	<#if username??>	
	<li><a href="${ambit_root}/admin">Admin</a>

			<#if menu_profile??>
				<#include "/menu/profile/${menu_profile}/admin_menu.ftl">
			<#else>
				<#include "/menu/profile/default/admin_menu.ftl">
			</#if>
	</li>	
	</#if>
	<li><a href="#">About</a>
		<ul>
		<li><a href="#">Version</a>
			<ul class="mega-menu">
			<li>
				<div style="width:400px;max-width:100%;">
	          		<div style="padding:5px 24px;">
						<div class='h6' style='color:#729203;' title='${ambit_version_long}'>AMBIT v${ambit_version_short}</div>
					</div>
				</div>		
			</li>
			</ul>
		</li>
		<li><a href="http://www.enanomapper.net/" target=_blank title="This project has received funding from the European Union’s FP7 for research, technological development and demonstration under grant agreement no 604134.">EU FP7 #604134</a></li>
		<li><a href="http://ambit.sourceforge.net/enanomapper.html" target=_blank title="eNanoMapper database is customized version of AMBIT web services">ambit.sf.net</a>	</li>		
		</ul>
	</li>

</ul>