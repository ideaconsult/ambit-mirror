<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="${ambit_root}/ui/_search" title="Chemical structure search">Search</a>
</li>
<li>
	<a href="${ambit_root}/dataset?page=0&pagesize=100" title="Datasets: Chemical structures and properties">Datasets</a>
	<#include "/menu/dataset_menu.ftl">
</li>
<li>
	<a href="${ambit_root}/substance?page=0&amp;pagesize=100">Substances</a>
	<#include "/menu/substance_menu.ftl">
</li>
<li>
	<a href="${ambit_root}/algorithm" title="Descriptor calculations, model building and data processing algorithms">Algorithms</a>
	<#include "/menu/algorithm_menu.ftl">
</li>
<li>
	<a href="${ambit_root}/model" title="Regression, classification, clustering, structural alerts, applicability domain, structure optimisation.">Models</a>
	<#include "/menu/model_menu.ftl">
</li>
<li>
	<a href="${ambit_root}/depict">Demo</a>
	<#include "/menu/demo_menu.ftl">
</li>		
<li>
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/help_menu.ftl">
</li>				
</ul>

