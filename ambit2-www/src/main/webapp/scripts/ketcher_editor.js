var smiles = "";
var jme = "0 0";
var form = "searchform";

function startEditor(baseRef) {
  form ="searchform";	
  startEditor(baseRef,form);
}

function startEditor(baseRef,formName) {
	  // use here fully qualified IP address (number!) i.e.
	  // window.open('http://123.456.789.1/jme_window.html',...
	  form = formName==undefined?'searchform':formName;
	  window.open(baseRef+'/ui/editor','KETCHER','left=5,top=20,width=940,height=640,scrollbars=no,resizable=yes,status=yes');
}

function fromEditor(smiles,mol) {
  // this function is called from jme_window
  // editor fills variable smiles & jme
  if (mol=="") {
    alert ("no molecule submitted");
    return;
  }
  document[form].type.value = "mol"; 
  document[form].b64search.value =  $.base64.encode(mol);
  document[form].search.value =  "";
  $("#molQuery").text("mol");
}

function processMolecule() {
  smiles = document[form].search.value;
  if (smiles == "") {alert("Nothing to process!"); return;}
  alert('submitting '+smiles+' for processing!');
  // in actual application remove line above and add something like this
  // document.form.submit();
}

