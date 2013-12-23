
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