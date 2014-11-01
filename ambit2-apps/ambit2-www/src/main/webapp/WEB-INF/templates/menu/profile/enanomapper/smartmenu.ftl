<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="${ambit_root}/ui/_search" title="Chemical structure search">Search</a>
	<ul>
		<li><a href="${ambit_root}/ui/_search" title="Chemical structure search">Search structures and associated ENM and data</a></li>
		<li ><a href="${ambit_root}/substance?page=0&pagesize=20" title="search for ENM with identifiers">Search nanomaterials</a></li>
		<li><a href="${ambit_root}/query/study" title="Search substances by experimental data">Search nanomaterials by endpoint data</a></li>
	</ul>
</li>
<li>
	<a href="${ambit_root}/substance?page=0&amp;pagesize=100">Nanomaterials</a>
	<#include "/menu/profile/enanomapper/substance_menu.ftl">
</li>

<li>
	<a href="#" title="OpenTox resources">OpenTox</a>
	<ul>
	<li>
		<a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets: Chemical structures and properties">Datasets</a>
		<#include "/menu/profile/enanomapper/dataset_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
		<#include "/menu/profile/enanomapper/algorithm_menu.ftl">
	</li>
	<li>
		<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
		<#include "/menu/profile/enanomapper/model_menu.ftl">
	</li>	
	</ul>
</li>



<li>
	<a href="${ambit_root}/depict">Demo</a>
	<#include "/menu/profile/enanomapper/demo_menu.ftl">
</li>		
<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/enanomapper/help_menu.ftl">
</li>				
</ul>

