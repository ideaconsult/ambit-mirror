<ul id='smartmenu' class="sm sm-mint">
<li>
	<a href="#" title="Search nanomaterials by chemical structure of components">Search</a>
	<ul>
		<li ><a href="${ambit_root}/substance?type=name&search=Silica&page=0&pagesize=20" title="Search for nanomaterials by identifiers or reference">Search nanomaterials by name</a></li>
		<li ><a href="${ambit_root}/substance?type=&search=NM-111&page=0&pagesize=20" title="Search for nanomaterials by identifiers">Search nanomaterials by identifier</a></li>
		<li ><a href="${ambit_root}/substance?type=citation&search=10.1073&page=0&pagesize=20" title="Search for nanomaterials by paper reference">Search nanomaterials by citation</a></li>
		<li><a href="${ambit_root}/query/study" title="Search substances by physico-chemical parameters or biological effects">Search nanomaterials by physchem parameters or biological effects</a></li>
		<li><a href="${ambit_root}/ui/_search?search=SiO2" title="Search nanomaterials by chemical structure of components">Search nanomaterials by composition</a></li>
		<li><a href="${ambit_root}/ontobucket?search=cytotoxicity&type=protocol&qe=true" title="Free text search (experimental)">Free text search</a></li>
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
	<a href="${ambit_root}/help">Help</a>
	<#include "/menu/profile/enanomapper/help_menu.ftl">
</li>				
</ul>

