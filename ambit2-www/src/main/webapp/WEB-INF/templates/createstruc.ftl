<#include "/html.ftl" >
<head>
<#include "/header.ftl" >

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					$( "#selectable" ).selectable( "option", "distance", 18);
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
			<h3 class="remove-bottom">
					Add a new chemical structure
			</h3>
		    <h6>Add a new chemical structure with CAS, EC, Name, IUCLID UUID</h6>
		</div>
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu.ftl">
<ul id="selectable">
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
</ul>
</div>

<div class="thirteen columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Create a new structure</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/dataset" id="uploadForm"  method="POST"   ENCTYPE="multipart/form-data">		
	
	<div class='row half-bottom'>
		<applet class='eleven columns alpha' code="JME.class" name="JME" archive="../jme/JME.jar" width="500" height="400">
		<param name="options" value="nohydrogens,polarnitro,nocanonize">
		You have to enable Java and JavaScript on your machine ! 
		</applet>
		<div class='five columns omega'>
		<label for="cas">CAS</label>
		<input class='half-bottom' type="text" name='cas' title='CASRN' size="60">
		<label for="ec">EINECS</label>
		<input class='half-bottom' type="text" name='ec' title='EINECS' size="60">		
		<label for="name">Chemical name</label>
		<input class='half-bottom' type="text" name='name' title='Chemical name' size="60">
		<label for="i5uuid">IUCLID5 UUID</label>
		<input class='half-bottom' type="text" name='i5uuid' title='IUCLID5 UUID' size="60">	
		<input class='half-bottom' type="text" name='customidname' value='Custom identifier' title='Custom Identifier' size="30">	
		<input class='half-bottom' type="text" name='customid' title='Custom identifier' size="60">
		</div>
	</div>
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="smiles">SMILES</label>
		<input class='eight columns alpha half-bottom' type="text" id='smiles' value='' name='smiles' title='smiles' size="60">
		<div class='five columns omega'></div>
	</div>
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="inchi">InChI</label>
		<input class='eight columns alpha half-bottom' type="text" id='inchi' value='' name='inchi' title='inchi' size="60">
		<div class='five columns omega'></div>
	</div>	
	<div class='row remove-bottom'>
		<div class='eleven columns alpha half-bottom' >&nbsp;</div>
		<div class='five columns omega half-bottom'>
				<input type='submit' class='submit' value='Submit'>	
		</div>
	</div>		
	</form>	
</div>			
</div>
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
