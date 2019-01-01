<div class='helptitle' style='font-weight:bold;'>Help: Datasets of chemical structures</div>
<div class='helpcontent'>
A dataset is a collection of chemical structures and properties (e.g. structural, physical-chemical, biological, toxicological properties).
The datasets of chemical structures is recommended for properties calculated from a chemical structure, while the preferred way to import and store assay data is via <a href='${ambit_root}/substance?pagesize=10'>substances</a> and associated studies.
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#hdataset">Dataset</a></li>
    <li><a href="#derek">DEREK predictions help</a></li>
    <li><a href="#hendpoint">Search for datasets by endpoint</a></li>
    <li><a href="#hname">Search for datasets by name</a></li>
    <li><a href="#hapi">Dataset API</a></li>
    <li><a href="#hactions">Actions</a></li>
    <li><a href="#hdelete">Remove a dataset</a></li>
    <li><a href="#hsearch">Search within a dataset</a></li>
  </ul>
  <div id="hdataset">
    Dataset or compound URI as in <a href="${ambit_root}/dataset?max=100" target=_blank>Datasets list</a>.

  </div>
<div id="derek">
<ul>
<li>CERTAIN:  There is proof that the proposition is true </li>
<li>PROBABLE: There is at least one strong argument that the proposition is true and there are no arguments against it.</li>
<li>PLAUSIBLE: The weight of evidence supports the proposition</li>
<li>EQUIVOCAL: There is an equal weight of evidence for and against the proposition</li>
<li>DOUBTED: The weight of evidence opposes the proposition</li>
<li>IMPROBABLE: There is at least one strong argument that the proposition is false and there are no arguments that it is true.</li>
<li>IMPOSSIBLE: There is proof that the proposition is false.</li>
<li>OPEN: There is no evidence that supports or opposes the proposition.</li>
<li>CONTRADICTED: There is proof that the proposition is both true and false</li>
</ul>
</div>  

<div id="hendpoint">
	Lists datasets of chemical structures, grouped by endpoints. Note the endpoint data may be calculated or imported from a file.
	<br/>
	The dataset is a table-like structure, with structures as rows and properties as columns, and is appropriate for modelling purposes. For programmatic access consult the <a href="http://ambit.sourceforge.net/api_dataset.html" target=_blank title='Application Programming interface'>Dataset API</a>.  
</div>  

<div id="hname">
	Please enter the first few letters of a dataset name and click the <i>Search</i> button. 
</div>  

<div id="hapi">
	The dataset is a table-like structure, with structures as rows and properties as columns, and is appropriate for modelling purposes. For programmatic access consult the <a href="http://ambit.sourceforge.net/api_dataset.html" target=_blank title='Application Programming interface'>Dataset API</a>.
</div>  

<div id="hdelete">
	Click the delete icon in the last column. You have to have the appropriate rights to delete a dataset.
</div>

<div id="hsearch">
	Run similarity or substructure search within this dataset.
</div>

<div id="hactions">
<ul>
<li>Same as <i>"Search structures and associated data"</i> menu. Allows to combine columns from multiple datasets and prediction models. 
More help at <a href='http://ambit.sourceforge.net/usage.html#Sidebar'>ambit.sf.net</a>
{http://localhost:8080/ambit2/ui/_search
</li>
<li>Launches online Toxtree <a href='toxtree.sf.net' target='toxtree'>Toxtree</a> predictions for this dataset</li>
<li>List models using this dataset as a training dataset; and allows to build models from the dataset.</li>
</ul>
</div>

<div id="hdownload">
	Download in different formats.
</div>  