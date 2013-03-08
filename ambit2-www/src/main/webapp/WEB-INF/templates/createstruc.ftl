<#include "/html.ftl" >
<head>
<#include "/header.ftl" >
<style>
.applet {width:500px; height:400px; margin:0 auto; text-align:center; border:1px solid #000;  position:absolute }
</style>
<script type='text/javascript'>

$(document)
	.ready(function() {
			$("#selectable" ).selectable( "option", "distance", 18);
			$("#cas").blur(function () {
			      getSmiles();
			});
});

function getSmiles() {
  var drawing = document.JME.smiles();
   $("#smiles").attr("value",drawing);
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

<div class="thirteen columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Create a new structure</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/compound" id="uploadForm"  method="POST">		
	<div class="iframe row remove-bottom"><iframe style="width:1px; height:1px; background:none; border:none"> </iframe></div>	
	<div class='row half-bottom'>
		<div class='ten columns alpha'>
			<applet code="JME.class" id="JME" name="JME" archive="../jme/JME.jar" width="500" height="400">
			<param name="options" value="nohydrogens,polarnitro,nocanonize">
			You have to enable Java and JavaScript on your machine ! 
			</applet>
		</div>
		<div class='six columns omega'>
		<label for="cas">CAS</label>
		<input class='half-bottom' type="text" id="cas" name='cas' title='CASRN' size="15">
		<label for="ec">EINECS</label>
		<input class='half-bottom' type="text" name='ec' title='EINECS' size="15">		
		<label for="name">Chemical name</label>
		<input class='half-bottom' type="text" name='name' title='Chemical name' size="80">
		<label for="i5uuid">IUCLID5 UUID</label>
		<input class='half-bottom' type="text" name='i5uuid' title='IUCLID5 UUID' size="80">	
		<input class='half-bottom' type="text" name='customidname' value='Custom identifier' title='Custom Identifier' size="30">	
		<input class='half-bottom' type="text" name='customid' title='Custom identifier' size="80">
		</div>
	</div>
	<div class='row remove-bottom'>
		<b>SMILES </b> 
		<a href='#' onClick="getSmiles()" title='Get SMILES from the editor'>Get SMILES</a>
	</div>	
	<div class='row remove-bottom'>
		<input class='eight columns alpha half-bottom' type="text" id='smiles' value='' name='smiles' title='smiles' size="60">
		<div class='eight columns omega'></div>
	</div>
	<div class='row remove-bottom'>
		<b>MOL file </b> 
		<a href='#' onClick="getMolFile()" title='Get Mol file from the editor'>Get MOL file</a> 
		<a href='#' onClick="useMol()" title='Use Mol file in the editor'>Use Mol file</a>  
	</div>
	<div class='row remove-bottom'>
		<textarea class='eight columns alpha half-bottom' id='molfile' name='molfile' title='MOL file'></textarea>
		<div class='eight columns omega'></div>
	</div>
	<label class='row remove-bottom' for="molfile">InChI</label>	
	<div class='row remove-bottom'>
		<input class='eight columns alpha half-bottom' type="text" id='inchi' value='' name='inchi' title='InChI' size="60">
		<div class='eight columns omega'></div>
	</div>	
	<div class='row remove-bottom'>
		<div class='ten columns alpha half-bottom' >&nbsp;</div>
		<div class='six columns omega half-bottom'>
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
