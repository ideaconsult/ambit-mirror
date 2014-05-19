
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/ui"><span class="ui-icon ui-icon-home" style="float: left; margin-right: .3em;"></span>Home</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/ui/query"><span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Structure search</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/substance?page=0&amp;pagesize=100">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>Substances</a>
</li> 

<li class="ui-selectee"><span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>
<a href="${ambit_root}/ui/uploadsubstance">Import substance</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/le">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>Substance owners</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/admin/stats/protocol_applications">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>Experiments</a>
</li>  

<li class="ui-selectee">
	<a href="${ambit_root}/admin/stats/experiment_endpoints">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>Endpoints</a>
</li>  
		    
<#if openam_token??>

<#if username??>
<li class="ui-selectee">
<a href="${ambit_root}/bookmark/${username}"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>My workspace</a>
</li>
</#if>
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
<li class="ui-selectee">
<a href="${ambit_root}/admin/policy"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>View/Define access rights</a>
</li>
</#if>

</ul>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by category'>Study: </span> 
<a href='${ambit_root}/substance?type=topcategory&search=P-CHEM' title='Physicochemical'>P-Chem</a>
<a href='${ambit_root}/substance?type=topcategory&search=ENV+FATE' title='Environmental fate'>ENV</a>
<a href='${ambit_root}/substance?type=topcategory&search=ECOTOX' title='Ecotoxicity'>ECO</a>
<a href='${ambit_root}/substance?type=topcategory&search=TOX' title='Toxicity'>TOX</a> 
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by Klimisch code'>Reliability: </span> 
<a href='${ambit_root}/substance?type=reliability&search=1+%28reliable+without+restriction%29' title='1 (reliable without restriction)'>1</a> 
<a href='${ambit_root}/substance?type=reliability&search=2+%28reliable+with+restrictions%29' title='2 (reliable with restrictions)'>2</a>
<a href='${ambit_root}/substance?type=reliability&search=3+%28not+reliable%29' title='3 (not reliable)'>3</a>
<a href='${ambit_root}/substance?type=reliability&search=4+%28not+assignable%29' title='4 (not assignable)'>4</a>
<a href='${ambit_root}/substance?type=reliability&search=other:' title='other:'>5</a>
<a href='${ambit_root}/substance?type=reliability&search=empty+%28not+specified%29' title='empty (not specified)'>6</a>
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by purpose flag'>Study purpose:</span> 
<a href='${ambit_root}/substance?type=purposeFlag&search=key+study' title='key study'>K</a> 
<a href='${ambit_root}/substance?type=purposeFlag&search=supporting+study' title='supporting study'>S</a>
<a href='${ambit_root}/substance?type=purposeFlag&search=weight+of+evidence' title='weight of evidence'>WoE</a>
<a href='${ambit_root}/substance?type=purposeFlag&search=disregarded+study' title='disregarded study'>D</a>
<a href='${ambit_root}/substance?type=purposeFlag&search=' title='Not specified'>N/A</a>
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by robust study flag'>Robust study:</span> 
<a href='${ambit_root}/substance?type=isRobustStudy&search=1' title='Yes'>Yes</a> 
<a href='${ambit_root}/substance?type=isRobustStudy&search=0' title='No'>No</a>
</div>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; ' >
<span title='Filter substances by study result type'>Result:</span> 
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