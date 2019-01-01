<#include "/html.ftl" >
<head>
 <title>KETCHER Molecular Editor</title>
 <link rel="stylesheet" href="../style/skeleton/base.css">
 <link rel="stylesheet" href="${ambit_root}/style/skeleton/skeleton-fluid.css">
 <link rel="stylesheet" href="${ambit_root}/style/skeleton/layout.css">
  <link rel="stylesheet" href="${ambit_root}/style/jquery-ui-1.10.4.custom.min.css">
	<link rel="stylesheet" href="${ambit_root}/style/jtoxkit.css">
	<link rel="stylesheet" href="${ambit_root}/style/ketcher.css"/>

  <script type="text/javascript" charset='utf8' src="${ambit_root}/jquery/jquery-1.10.2.js"></script>
  <script type="text/javascript" charset='utf8' src="${ambit_root}/jquery/jquery.cookie.js"></script>
  <script type='text/javascript' charset='utf8' src='${ambit_root}/jquery/jquery.base64.min.js'></script>
  <script type="text/javascript" charset='utf8' src="${ambit_root}/scripts/common.js"></script>
  <script type="text/javascript" charset='utf8' src="${ambit_root}/scripts/jtoxkit.js"></script>
  <script type="text/javascript" charset='utf8' src="${ambit_root}/scripts/ketcher.js"></script>

  
<script language="JavaScript">

   jT.$(document).ready(function () {
      jT.insertTool('ketcher', jT.$('#ketcher-test')[0]);
      ketcher.init();
      try {
    	  if ($.cookie('ambit2.search')!=undefined && $.cookie('ambit2.search')!="")
    		  ketcher.setMolecule($.cookie('ambit2.search'));
    	  else {
    		  var mol =$.base64.decode($.cookie('ambit2.b64search'));
    		  ketcher.setMolecule(mol);
    	  }
    	  
      } catch (err) {

      }
    });
    
    
function submitSmiles() {
  var smiles = ketcher.getSmiles();
  var jme = ketcher.getMolfile();
  if (jme == "") {
    alert("Nothing to submit");
  }
  else {
    opener.fromEditor(smiles,jme);
    window.close();
  }
}


</script>
</head>

<body bgcolor="#ffffff">
<div class="container" style="margin:10;padding:0;">
<div class='row half-bottom' id="ketcher-test"></div>
<div class='row remove-bottom'>
<form name="form_editor">
	<div class='six columns'>&nbsp;</div>
	<div class='three columns'>
		<input type="button" value=" Submit Molecule " onClick = "submitSmiles()">
	</div>
	<div class='three columns'>
		<input type="button" value="  Close  " onClick = "window.close()">
	</div>
</form>
</div>
</div>

</body>
</html>
