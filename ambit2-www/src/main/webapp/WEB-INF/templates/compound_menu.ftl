
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/ui"><span class="ui-icon ui-icon-home" style="float: left; margin-right: .3em;"></span>Home</a>
</li>

<li class="ui-selectee">
<a href="${ambit_root}/ui/query"><span class="ui-icon ui-icon-search" style="float: left; margin-right: .3em;"></span>Structure search</a>
</li>

<li class="ui-selectee">
	<a href="${ambit_root}/ui/query?option=similarity&type=url&pagesize=10&threshold=0.75&search=${ambit_request?url('UTF-8')}">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>Find similar</a>
</li> 

<li class="ui-selectee">
	<a href="${ambit_root}/ui/query?option=smarts&type=url&pagesize=10&threshold=0.75&search=${ambit_request?url('UTF-8')}">
	<span class="ui-icon ui-icon-document" style="float: left; margin-right: .3em;"></span>Find substructure</a>
</li> 

<li class="ui-selectee" >
	<span class="ui-icon ui-icon-disk" style="float: left; margin-right: .3em;" ></span>
	Download
</li>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
</div>

</ul>