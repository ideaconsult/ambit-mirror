
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/ui"><span class="ui-icon ui-icon-home" style="float: left; margin-right: .3em;"></span>Home</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/ui/query"><span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Structure search</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/ui/query?option=similarity&type=url&pagesize=10&threshold=0.75&search=${ambit_request?url('UTF-8')}">
	<span class="ui-icon ui-icon-heart" style="float: left; margin-right: .3em;"></span>Find similar</a>
</li> 

<li class="ui-selectee">
	<a href="${ambit_root}/ui/query?option=smarts&type=url&pagesize=10&threshold=0.75&search=${ambit_request?url('UTF-8')}">
	<span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Find substructure</a>
</li> 

<li class="ui-selectee">
	<a href="${ambit_root}/compound/${cmpid}/conformer" title='All available structures for this chemical compound'>
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>All structures</a>
</li> 

<li class="ui-selectee" >
	<span class="ui-icon ui-icon-disk" style="float: left; margin-right: .3em;" ></span>
	Download
</li>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
</div>

<li class="ui-selectee" >
	<span class="ui-icon ui-icon-star" style="float: left; margin-right: .3em;" ></span>
	Structure QA
</li>

<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<div id='structype_${cmpid}' title='Comparison label'/>
</div>
<div class='row' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<div id='consensus_${cmpid}' title='Consensus label'/>
</div>
</ul>