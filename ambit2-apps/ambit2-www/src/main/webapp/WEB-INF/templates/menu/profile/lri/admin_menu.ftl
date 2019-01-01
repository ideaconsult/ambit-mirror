<ul>
	<li ><a href="${ambit_root}/substanceowner">Display substance owners</a></li>
	
	<#if (ambit_datasetmgr?? && ambit_datasetmgr) || (ambit_admin?? && ambit_admin)>
		<li ><a href="${ambit_root}/ui/uploadsubstance">Import substance</a>
			<ul>
				<li ><a href="${ambit_root}/ui/uploadsubstance">Multiple files upload</a>
				<li ><a href="${ambit_root}/ui/uploadsubstance1">Single file upload</a>
				<li ><a href="${ambit_root}/ui/updatesubstancei5">Retrieve substance(s) from IUCLID5 server</a>
			</ul>
		</li>
	</#if>		
		
	<#if username??>
		<#if openam_token??>
			<li ><a href="${ambit_root}/bookmark/${username}">My workspace</a></li>
			<li ><a href="${ambit_root}/admin/policy">View/Define access rights</a></li>
		<#else>
			<li ><a href="${ambit_root}/myaccount">My account</a></li>
			<#if ambit_admin?? && ambit_admin>
				<li ><a href="${ambit_root}/user">User management</a></li>
			</#if>		
		</#if>
	</#if>

	<li>
		<a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets: Chemical structures and properties">Datasets</a>
		<#include "/menu/profile/lri/dataset_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/substance?page=0&amp;pagesize=100">Substances</a>
		<#include "/menu/profile/lri/substance_menu.ftl">
	</li>
</ul>