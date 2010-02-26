var smiles = "";
var jme = "0 0";

function startEditor(baseRef) {
  // use here fully qualified IP address (number!) i.e.
  // window.open('http://123.456.789.1/jme_window.html',... 
  window.open(baseRef+'/jme/jme_window.html','JME','width=500,height=450,scrollbars=no,resizable=yes');
}

function fromEditor(smiles,jme) {
  // this function is called from jme_window
  // editor fills variable smiles & jme
  if (smiles=="") {
    alert ("no molecule submitted");
    return;
  }
  document.form.type.value = "smiles"; 
  document.form.search.value = smiles; 
}

function processMolecule() {
  smiles = document.form.smiles.value;
  if (smiles == "") {alert("Nothing to process!"); return;}
  alert('submitting '+smiles+' for processing!');
  // in actual application remove line above and add something like this
  // document.form.submit();
}

function getMolFile() {
  var mol = document.JME.molFile();
  document.form.text.value = mol;
}

function getMolFile() {
  var mol = document.JME.molFile();
  document.form.text.value = mol;
}

function getSmiles() {
  var drawing = document.JME.smiles();
  document.form.search.value = drawing;
}

