<#if substanceUUID??>
<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
	<a href="${ambit_root}/ui/_dataset?dataset_uri=${ambit_root}/substance/${substanceUUID}/structure" title='Chemical structures for this substance'>Show structures</a>
</div>
<!--
<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
	<a href="${ambit_root}/substance/${substanceUUID}/composition" title='Substance composition'>Show composition</a>
</div>
-->
	<#if username??>
		<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; font-style:bold;'>
			Remove substance
		</div>
		<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; font-style:bold;'>
		
			<a href='#' onClick='deleteSubstance("${ambit_root}/substance/${substanceUUID}","${substanceUUID}","#statusSelector")'>
			<span class='ui-icon ui-icon-trash' style='float: left; margin: .1em;' title=></span>&nbsp;${substanceUUID}</a>
			<br/><span class='msg' id='statusSelector'></span>		
		</div>
	</#if>
	
	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
		<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json64.png' alt='json' title='Download as JSON'></a>
<!--		
		<a href='#' id='xlsx' target=_blank><img src='${ambit_root}/images/xlsx.png' alt='XLSX' title='Download as XLSX'></a>
-->		
	</div>

<#else>
	

	<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; font-style:bold;'>
	Substance search 	<a href='#' class='chelp substancesearch'></a>	
	</div>
	<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
	<form method='GET' name='searchform' id='searchform' action='${ambit_root}/substance' style='padding:0;margin:0;'>
		<select id='selecttype' name="type">
			  	 <option value="name">Name (starting with a string)</option>
			  	 <option value="like">Name (pattern matching)</option>
			  	 <option value="regexp">Name (regexp)</option>
				  <option value="uuid">UUID</option>
				  <option value="">External identifier</option>
				  <option value="substancetype">Substance type</option>
				  <option value="CompTox">CompTox</option>
				  <option value="guideline">Protocol / Guideline</option>
				  <option value="DOI">DOI</option>
				  <option value="citation" title='Experiment reference'>Publication/report describing the experiment</option>
				  <option value="topcategory" title='One of P-CHEM, ENV FATE, ECOTOX, TOX'>One of P-CHEM, ENV FATE, ECOTOX, TOX</option>
				  <option value="endpointcategory" title='Endpoint category (e.g. EC_FISHTOX_SECTION)'>Endpoint category (e.g. EC_FISHTOX_SECTION)</option>
				  <option value="endpoint" title='Endpoint, e.g. LC50'>Endpoint</option>
				  <option value="endpointhash" title='Experiment conditions hash'>Experiment conditions</option>
				  <!--
				  <option value="params" title='Protocol parameter'>Protocol parameter</option>
				  -->
				  <option value="owner_name">Owner name</option>
				  <option value="owner_uuid">Owner UUID</option>
				  
				  <option value="reliability" title='1 (reliable without restriction)|2 (reliable with restrictions)|3 (not reliable)|4 (not assignable)|other:empty (not specified)'>Reliability</option>
				  <option value="purposeFlag" title='key study|supporting study'>Study purpose</option>
				  <option value="studyResultType" title='experimental result|estimated by calculation|read-across|(Q)SAR'>Study result type</option>
				  <option value="isRobustStudy" title='true|false'>Robust study</option>
			</select>

		<input type='text'  id='search' name='search' class='search' value='' tabindex='1' >	
		<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
	</form>				
	</div>
	
	
<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by category'>Study<a href='#' class='chelp category'></a>:</span> 
<a href='${ambit_root}/substance?type=topcategory&search=P-CHEM' title='Physicochemical'>P-Chem</a>
<a href='${ambit_root}/substance?type=topcategory&search=ENV+FATE' title='Environmental fate'>ENV</a>
<a href='${ambit_root}/substance?type=topcategory&search=ECOTOX' title='Ecotoxicity'>ECO</a>
<a href='${ambit_root}/substance?type=topcategory&search=TOX' title='Toxicity'>TOX</a> 
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by Klimisch code'>Reliability<a href='#' class='chelp reliability'></a>	: </span> 
<a href='${ambit_root}/substance?type=reliability&search=1+%28reliable+without+restriction%29' title='1 (reliable without restriction)'>1</a> 
<a href='${ambit_root}/substance?type=reliability&search=2+%28reliable+with+restrictions%29' title='2 (reliable with restrictions)'>2</a>
<a href='${ambit_root}/substance?type=reliability&search=3+%28not+reliable%29' title='3 (not reliable)'>3</a>
<a href='${ambit_root}/substance?type=reliability&search=4+%28not+assignable%29' title='4 (not assignable)'>4</a>
<a href='${ambit_root}/substance?type=reliability&search=other:' title='other:'>5</a>
<a href='${ambit_root}/substance?type=reliability&search=empty+%28not+specified%29' title='empty (not specified)'>6</a>
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by purpose flag'>Study purpose<a href='#' class='chelp studypurpose'></a>:</span> 
<a href='${ambit_root}/substance?type=purposeFlag&search=key+study' title='key study'>K</a> 
<a href='${ambit_root}/substance?type=purposeFlag&search=supporting+study' title='supporting study'>S</a>
<a href='${ambit_root}/substance?type=purposeFlag&search=weight+of+evidence' title='weight of evidence'>WoE</a>
<a href='${ambit_root}/substance?type=purposeFlag&search=disregarded+study' title='disregarded study'>D</a>
<a href='${ambit_root}/substance?type=purposeFlag&search=' title='Not specified'>N/A</a>
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by robust study flag'>Robust study<a href='#' class='chelp robuststudy'></a>:</span> 
<a href='${ambit_root}/substance?type=isRobustStudy&search=1' title='Yes'>Yes</a> 
<a href='${ambit_root}/substance?type=isRobustStudy&search=0' title='No'>No</a>
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by study result type'>Result<a href='#' class='chelp resulttype'></a>:</span> 
<a href='${ambit_root}/substance?type=studyResultType&search=experimental+result' title='experimental result'>E</a>
<a href='${ambit_root}/substance?type=studyResultType&search=experimental+study+planned' title='experimental study planned'>EP</a>
<a href='${ambit_root}/substance?type=studyResultType&search=estimated+by+calculation' title='estimated by calculation'>C</a>
<a href='${ambit_root}/substance?type=studyResultType&search=read-across+based+on+grouping+of+substances+(category approach)' title='read-across based on grouping of substances (category approach)'>RAg</a>
<a href='${ambit_root}/substance?type=studyResultType&search=read-across from supporting substance+(structural+analogue+or+surrogate)' title='read-across from supporting substance (structural analogue or surrogate)'>RAa</a>
<a href='${ambit_root}/substance?type=studyResultType&search=(Q)SAR' title='(Q)SAR'>Q</a>
<a href='${ambit_root}/substance?type=studyResultType&search=other:' title='other:'>O</a>
<a href='${ambit_root}/substance?type=studyResultType&search=no data' title='no data'>ND</a>
<a href='${ambit_root}/substance?type=studyResultType&search=' title='not specified'>NA</a>

</div>
</#if>
	
