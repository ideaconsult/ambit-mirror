<div class='helptitle' style='font-weight:bold;'>Help: Import structures</div>
<div class='helpcontent'>
This page allows to upload a file with chemical structures and properties.
Supported formats are SDF, MOL, SMI, CSV, TXT, XLS, ToxML (.xml).
In order to add a single chemical structure instead, use <a href="${ambit_root}/ui/createstruc">Add a new structure</a>
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#hfile">File</a></li>
    <li><a href="#hname">Dataset name</a></li>
    <li><a href="#hurl">URL</a></li>
    <li><a href="#hmatch">URL</a></li>
    <li><a href="#hlicense">URL</a></li>
  </ul>
  <div id="hfile">
		Select a file with chemical structures and properties.
		Supported formats are:
		<ul>
		<li>Structure Data Format <a href="http://en.wikipedia.org/wiki/Chemical_table_file" target=_blank>SDF (.sdf)</a></li>
		<li>Mol file <a href="http://en.wikipedia.org/wiki/Chemical_table_file" target=_blank>MOL (.mol)</a></li>
		<li>SMILES <a href="http://en.wikipedia.org/wiki/Simplified_molecular-input_line-entry_system" target=_blank>(.smi)</a></li>
		<li>Chemical Markup Language <a href="http://en.wikipedia.org/wiki/Chemical_Markup_Language" target=_blank>CML</a>(.cml)</li>
		<li>Comma separated text file (.csv)</li>
		<li>TAB separated text file (.txt)</li>
		<li>Excel 97-2003 XLS (.xls). The first worksheet only is imported.</li>
		<li><a href="http://www.toxml.org/" target=_blank>ToxML</a> (.xml)</li>
		<li><a href="http://iuclid.eu" target=_blank>IUCLID 5</a> Reference substances (.i5d)</li>
		<li>ZIP file, containing any of the above (.zip)</li>
		</ul>
		CSV, TXT and XLS files should have a header row. A column,containing SMILES, should have title "SMILES". The number,titles and order of the columns are arbitrary.
		Example:
		<pre>
NAME,CAS,SMILES
Acetic acid,64-19-7,CC(O)=O
Acetoin,513-86-0,CC(O)C(C)=O
		</pre>
  </div>
  <div id="hname">
		Enter the name of the dataset, as it should appear at <a href="${ambit_root}/dataset?max=100">Datasets</a> page.
  </div>
  <div id="hurl">
		A related URL (e.g. the site where the file has been downloaded).
  </div>   
  <div id="hmatch">
		Match options specify how the molecule should be matched against existing structures in the database.
		The default is match by CAS and if no CAS present or matched, then by structure.
  </div>
  <div id="hlicense">
		Dataset license. A number of Open licenses are available, as well as custom license (select Other from metadata page, once the dataset is uploaded).
  </div>       
</div>    