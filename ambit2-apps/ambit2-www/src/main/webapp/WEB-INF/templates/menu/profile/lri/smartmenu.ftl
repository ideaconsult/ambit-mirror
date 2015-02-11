<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="${ambit_root}/ui/_search" title="Chemical structure search">Search</a>
	<ul>
		<li><a href="${ambit_root}/ui/_search" title="Chemical structure search, Work flow: 1. find structure. 2. select data source. 3. display data.">Search structures and associated data</a></li>
		<li ><a href="${ambit_root}/substance?page=0&pagesize=10" title="search for IUC substances with identifiers (fragment), possibility to search in the hit list.">Search IUCLID substances by identifiers</a></li>
		<li><a href="${ambit_root}/query/study" title="Search all IUC substances with defined parameters hit list shows IUC substances composition and structure">Search IUCLID substances by endpoint data</a></li>
	</ul>
</li>
<li>
	<a href="${ambit_root}/bundle">Assessments</a>
	<#include "/menu/profile/lri/assessment_menu.ftl">
</li>	

<li>
	<a href="#">Import</a>
	<ul>
		<li ><a href="${ambit_root}/ui/uploadstruc" title="Upload a dataset of chemical structures and properties. Supported formats are SDF, MOL, SMI, CSV, TXT, XLS, ToxML (.xml)">Import a dataset</a></li>
		<li ><a href="${ambit_root}/ui/uploadprops" title="Import properties for compounds already in the database">Import properties</a></li>
		<li ><a href="${ambit_root}/ui/createstruc" title="Add new chemical structure">Add structure</a></li>
	</ul>
</li>
	
<li>
	<a href="#">Enhanced functions</a>
	<ul>
	<li>
		<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
		<#include "/menu/profile/lri/algorithm_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
		<#include "/menu/profile/lri/model_menu.ftl">
	</li>
	</ul>
</li>
<li >
	<a href="${ambit_root}/ui/toxtree" title="Toxtree web release">Toxtree predictions</a>
</li>
<li>
	<a href="${ambit_root}/admin">Admin</a>
	<#include "/menu/profile/lri/admin_menu.ftl">
</li>	
<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/lri/help_menu.ftl">
</li>		
	
</ul>

