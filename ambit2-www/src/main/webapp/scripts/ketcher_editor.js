var smiles = "";
var form = "searchform";

function startEditor(baseRef) {
  form ="searchform";	
  startEditor(baseRef,form);
}

function startEditor(baseRef,formName) {
	  form = formName==undefined?'searchform':formName;
	  window.open(baseRef+'/ui/editor','KETCHER','left=5,top=20,width=940,height=640,scrollbars=no,resizable=yes,status=yes');
}

function fromEditor(smiles,mol) {
  if (mol=="") {
    alert ("no molecule submitted");
    return;
  }
  document[form].type.value = "mol"; 
  document[form].b64search.value =  $.base64.encode(mol);
  document[form].search.value =  smiles;
  $("#molQuery").text("mol");
}

function processMolecule() {
  smiles = document[form].search.value;
  if (smiles == "") {alert("Nothing to process!"); return;}
  alert('submitting '+smiles+' for processing!');
  // in actual application remove line above and add something like this
  // document.form.submit();
}

