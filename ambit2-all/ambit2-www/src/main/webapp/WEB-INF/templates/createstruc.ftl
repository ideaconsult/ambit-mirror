<#include "/html.ftl" >
<head>
<#include "/header.ftl" >
<style type="text/css">
.applet {width:500px; height:400px; margin:0 auto; text-align:center; border:1px solid #000;  position:absolute }

#uploadForm label.error {
	margin-left: 10px;
	width: auto;
	display: inline;
	color: #FF0000;
}
</style>
<script type='text/javascript'>

$(document)
	.ready(function() {
			$("#selectable" ).selectable( "option", "distance", 18);
			$("#appletdiv").blur(function () {
			      getSmiles();
			      getMolFile();
			});
			createStrucFormValidation("#uploadForm");
			$("#uploadForm").validate();
			loadHelp("${ambit_root}","createstruc");
});

function getSmiles() {
  var drawing = document.JME.smiles();
   $("#SMILES").attr("value",drawing);
}
		
function getMolFile() {
  var mol = document.JME.molFile();
   $("#molfile").text(mol);
}

function useMol() {
  document.JME.readMolFile($("#molfile").text());
}		
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

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Create a new structure</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/compound" id="uploadForm"  method="POST">		
	<div class="iframe row remove-bottom"><iframe style="width:1px; height:1px; background:none; border:none"> </iframe></div>	
	<div class='row remove-bottom'>
		<div class='ten columns alpha' id='appletdiv'>
			<applet class='remove-bottom' code="JME.class" id="JME" name="JME" archive="../jme/JME.jar" width="500" height="400">
			<param name="options" value="nohydrogens,polarnitro,nocanonize">
			You have to enable Java and JavaScript on your machine ! 
			</applet>
			<a href='#' class='chelp hdraw'>?</a>
		</div>
		<div class='six columns omega'>
		<label for="CASRN">CAS<em></em><a href='#' class='chelp hcas'>?</a></label>
		<input class='half-bottom' type="text" id="CASRN" name='CASRN' title='CASRN' size="15"  style="width:100%;">
		<label for="EINECS">EINECS<em></em><a href='#' class='chelp heinecs'>?</a></label>
		<input class='half-bottom' type="text" name='EINECS' title='EINECS' size="15" style="width:100%;">		
		<label for="IUPACName">IUPAC name<a href='#' class='chelp hname'>?</a></label>
		<input class='half-bottom' type="text" name='IUPACName' id='IUPACName' title='IUPAC name' style="width:100%;">
		<label for="ChemicalName">Chemical name<a href='#' class='chelp hname'>?</a></label>
		<input class='half-bottom' type="text" name='ChemicalName' id='ChemicalName' title='Chemical name' style="width:100%;">
		<label for="TradeName">Trivial name<a href='#' class='chelp hname'>?</a></label>
		<input class='half-bottom' type="text" name='TradeName' id='TradeName' title='Trivial name' style="width:100%;">
		<label for="IUCLID5_UUID">IUCLID5 UUID<a href='#' class='chelp hi5uuid'>?</a></label>
		<input class='half-bottom' type="text" name='IUCLID5_UUID' title='IUCLID5 UUID' style="width:100%">
		<label for="IUCLID5_UUID">Custom Identifier Title<a href='#' class='chelp hcustomidname'>?</a></label>	
		<input type="text" name='customidname' value='ID' title='Enter custom identifier name (e.g. MyID)' style="width:50%;">
		<label for="IUCLID5_UUID">Custom Identifier Value <a href='#' class='chelp hcustomid'>?</a></label>	
		<input type="text" name='customid' title='Enter custom identifier value' size="80"  style="width:50%;">
		<br/>
		<input class='remove-bottom' type='submit' class='submit' value='Submit'>	
		<a href='#' class='chelp hsubmit'>?</a>
		</div>
	</div>
	<div class='row remove-bottom'>
		<b>SMILES<a href='#' class='chelp hsmiles'>?</a>&nbsp;</b> 
		<a href='#' onClick="getSmiles()" title='Get SMILES from the editor'>Get SMILES</a>
	</div>	
	<div class='row remove-bottom'>
		<input class='eight columns alpha half-bottom' type="text" id='SMILES' value='' name='SMILES' title='SMILES' size="60">
		<div class='eight columns omega'></div>
	</div>
	<div class='row remove-bottom'>
		<b>MOL file<a href='#' class='chelp hmolfile'>?</a>&nbsp;</b> 
		<a href='#' onClick="getMolFile()" title='Get Mol file from the editor'>Get MOL file</a> 
		<a href='#' onClick="useMol()" title='Use Mol file in the editor'>Use Mol file</a>  
	</div>
	<div class='row remove-bottom'>
		<textarea class='eight columns alpha half-bottom' id='molfile' name='molfile' title='MOL file'></textarea>
		<div class='eight columns omega'></div>
	</div>
	<label class='row remove-bottom' for="InChI_std">Standard InChI<a href='#' class='chelp hinchi'>?</a>&nbsp;</label>	
	<div class='row remove-bottom'>
		<input class='eight columns alpha half-bottom' type="text" id='InChI_std' value='' name='InChI_std' title='Standard InChI' size="60">
		<div class='eight columns omega'></div>
	</div>	
	<label class='row remove-bottom' for="InChIKey_std">Standard InChI Key <a href='#' class='chelp hinchikey'>?</a>&nbsp;</label>	
	<div class='row remove-bottom'>
		<input class='eight columns alpha half-bottom' type="text" id='InChIKey_std' value='' name='InChIKey_std' title='Standard InChI Key' size="60">
		<div class='eight columns omega'></div>
	</div>	
	</form>	
</div>			
</div>
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >


</form>

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
