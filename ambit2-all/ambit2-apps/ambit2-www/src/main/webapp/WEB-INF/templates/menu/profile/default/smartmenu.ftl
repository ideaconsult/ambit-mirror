<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="${ambit_root}/ui/_search" title="Chemical structure search">Search</a>
	<ul>
		<li><a href="${ambit_root}/ui/_search" title="Chemical structure search">Search structures and associated data</a></li>
		<li ><a href="${ambit_root}/substance?page=0&pagesize=100" title="search for  substances with identifiers (fragment)">Search substances</a></li>
		<li><a href="${ambit_root}/query/study" title="Search substances by experimental data">Search substances by endpoint data</a></li>
	</ul>
</li>
<li>
	<a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets: Chemical structures and properties">Datasets</a>
	<#include "/menu/profile/default/dataset_menu.ftl">
</li>
<li>
	<a href="${ambit_root}/substance?page=0&amp;pagesize=10">Substances</a>
	<#include "/menu/profile/default/substance_menu.ftl">
</li>

<li>
	<a href="#">Enhanced functions</a>
	<ul>
	<li>
		<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
		<#include "/menu/profile/default/algorithm_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
		<#include "/menu/profile/default/model_menu.ftl">
	</li>
	</ul>
</li>
		
<li >
	<a href="${ambit_root}/ui/toxtree" title="Toxtree web release">Toxtree predictions</a>
</li>		
<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/default/help_menu.ftl">
</li>				
</ul>

