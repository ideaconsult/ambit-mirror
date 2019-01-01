<#if substanceUUID??>
	<#if menu_profile?? && menu_profile=='enanomapper'>		        
	<div class='row' id='download_substance' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
		<a href='${ambit_root}/substance/${substanceUUID}/study?media=application/json' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>
		<a href='${ambit_root}/substance/${substanceUUID}?media=application%2Fld%2Bjson' id='jsonld' target=_blank><img src='${ambit_root}/images/json-ld.png' alt='json' title='Download as JSON-LD'></a>
		<a href='${ambit_root}/substance/${substanceUUID}?media=text/n3' id='jsonld' target=_blank><img src='${ambit_root}/images/rdf64.png' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
		<a href='${ambit_root}/substance/${substanceUUID}?media=application%2Fvnd.openxmlformats-officedocument.spreadsheetml.sheet' id='xlsx' target=_blank><img src='${ambit_root}/images/xlsx.png' alt='XLSX' title='Download as XLSX'></a>
		<a href='${ambit_root}/substance/${substanceUUID}?media=application%2Fisa%2Bjson' id='isajson' target=_blank><img src='${ambit_root}/images/isa.png' alt='ISA-JSON' title='Download as ISA-JSON'></a>
	</div>
	</#if>
<!--
	<a href="${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/substance/${substanceUUID}/structure" title='Chemical structures for this substance'>Structures</a> |
	<a href="${ambit_root}/substance/${substanceUUID}/composition" title='Substance composition'>Composition</a> 
-->	 
	<#if username??>
		<#if (ambit_datasetmgr?? && ambit_datasetmgr) || (ambit_admin?? && ambit_admin)>
			<div class='row' i style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
			<span class='three column'>Remove substance</span>
			<span class='five columns'>
			<a href='#' onClick='deleteSubstance("${ambit_root}/substance/${substanceUUID}","${substanceUUID}","#statusSelector")'>
			<span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;' title=></span>&nbsp;${substanceUUID}</a>
			<span class='msg' id='statusSelector'></span>
			</span>
			</div>
		</#if>		
	</#if>
	

</#if>
	
