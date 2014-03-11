<#include "/html.ftl" >

<head>
<#include "/header.ftl" >

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
					loadHelp("${ambit_root}","substance");
				});
</script>

</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
		<div class="alpha">
			<div class="remove-bottom h3">
				Substances upload	
			</div>
		    <div class='h6'>Upload substances as defined in a single IUCLID5 .i5d or .i5z file
		    <br/>
    		<a href="${ambit_root}/ui/uploadsubstance">Multiple .i5z files upload<a>
		    </div>
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu_substance.ftl">
<ul >
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
</ul>
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Import new substance(s)</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/substance" id="uploadForm"  method="POST"   ENCTYPE="multipart/form-data">		
	
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="file">File (.i5z or .i5d)<a href='#' class='chelp i5z'>?</a><em>*</em></label>
		<input class='eight columns alpha half-bottom'  type="file" name="files[]" title='Add new substance(s) (.i5d or .i5z file)' size="60">
		<div class='five columns omega'></div>
	</div>
	<div class='row'>
		<label class='three columns alpha'>&nbsp;</label>
		<input class='three columns alpha' type='submit' class='submit' value='Submit'>
		<div class='ten columns omega'></div>
	</div>
	</form>	
</div>			
</div>
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
