<div class='helptitle' style='font-weight:bold;'>Help: Nanomaterials</div>
<div class='helpcontent'>
The nanomaterials <a href='#' class='chelp nanomaterial'>?</a> are considered a special case of substances <a href='#' class='chelp substance'>?</a>. 
See <a href="http://www.beilstein-journals.org/bjnano/single/articleFullText.htm?publicId=2190-4286-6-165#O9" class='qxternal' target=guide>doi:10.3762/bjnano.6.165</a>.
<br.></br/>
</div>

<div id="keys" style="display:none;">
  <ul>
  	<li><a href="#nanomaterial">Nanomaterial</a></li>
  	<li><a href="#substance">Chemical substance</a></li>
    <li><a href="#main">Main constituent</a></li>
    <li><a href="#additive">Additive</a></li>
    <li><a href="#impurity">Impurity</a></li>
    <li><a href="#i5z">.i5z file</a></li>
    <li><a href="#substancesearch">Substance search</a></li>
    <li><a href="#category">Endpoint category</a></li>
    <li><a href="#reliability">Reliability</a></li>
    <li><a href="#studypurpose">Study purpose</a></li>
    <li><a href="#robuststudy">Robust study</a></li>
    <li><a href="#resulttype">Result type</a></li>
    <li><a href="#substancesearch">Search by name</a></li>
    <li><a href="#nanomaterial2">Search by DOI</a></li>
    <li><a href="#nanomaterial3">Nanomaterial</a></li>
    <li><a href="#nanomaterial4">Search by composition</a></li>
    <li><a href="#nanomaterial5">Free text search</a></li>
    <li><a href="#nanomaterial6">Data model and upload</a></li>
    <li><a href="#nanomaterial7">Application Programming Interface (API)</a></li>
	<li><a href="#_oht">OECD HT</a></li>
	<li><a href="#_nmdataparser">Custom spreadsheet</a></li>
  </ul>
  <div id="nanomaterial2">
	Search by DOI 
  </div>
  <div id="nanomaterial3">
    Allows to search nanomaterials by endpoint data. <a href="http://www.beilstein-journals.org/bjnano/single/articleFullText.htm?publicId=2190-4286-6-165#F14" target='guide' class='qxternal'>More details</a>.
  </div>      
  <div id="nanomaterial4">
	The chemical structure and chemical similarity search in the eNanoMapper prototype database enables querying by a chemical structure of a NM component and highlighting the results as a core, coating or functionalisation component.
	<a href="http://www.beilstein-journals.org/bjnano/single/articleFullText.htm?publicId=2190-4286-6-165#F13" target='guide' class='qxternal'>More details</a>.
  </div>
  <div id="nanomaterial5">
  	Free text search and integrated view of eNanoMapper database and <a href="https://cananolab.nci.nih.gov/caNanoLab/#/" target=_blank class="qxternal">caNanoLab</a>  	 
  </div>
 <div id="nanomaterial6">
	Requires login!
	<br/>
	The data import is performed by HTTP POST to the substance resource, which translates to a regular web form for file upload.  
	 <a href="http://www.beilstein-journals.org/bjnano/single/articleFullText.htm?publicId=2190-4286-6-165#F8" target='guide' class='qxternal'>More details</a>.
  </div>
  <div id="nanomaterial7">
A way computer programs talk to one another. Can be understood in terms of how a programmer sends instructions between programs.
The API specifies how software components should interact.  A good API makes it easier to develop a program by providing all the building blocks. A programmer then puts the blocks together.
<br/>
Representational state transfer (<a href="https://en.wikipedia.org/wiki/Representational_state_transfer"  target='guide' class='qxternal'>REST</a>) is an abstraction of the architecture of the World Wide Web ;
<br/>
<a href='http://enanomapper.github.io/API/' target='guide' class='qxternal' target=_blank>Online documentation</a> using the <a href='http://swagger.io/' target='guide' class='qxternal'>Swagger</a> specification.

  </div>
 <div id="_oht">
The OECD Harmonized Templates (OHTs) are structured (XML) data formats for reporting safety-related studies on chemical substances. The <a href="http://www.oecd.org/ehs/templates/" class="qxternal" target=_blank>OHTs</a>  ) and the supporting IT tool (<a href="http://iuclid.eu" class="qxternal" target=_blank>IUCLID5</a>) are used in a regulatory context, for preparation of substance dossiers for <a href="http://ec.europa.eu/environment/chemicals/reach/reach_en.htm" class="qxternal" target=_blank>REACH</a> and for other regulatory frameworks operating in Europe; as well as by the JRC NanoHub database
  </div>
 <div id="_nmdataparser">
Excel files with custom JSON configuration. <br/>
<ul>
<li>Prepare the ENM characterisation and assay data using your own templates;</li>
<li>The Excel parser enables converting the  input templates into the eNanoMapper data model and upload into the DB;</li>
<li>The parser configuration is defined in a separate JSON file, mapping the custom spreadsheet structure into the internal eNanoMapper storage components</li> 
<li>The JSON configuration syntax includes a set of keywords, specifying different strategies for reading the data from one or several sheets, as well as allowing combination of the excel structures (sheets, rows, columns, blocks of cells and cells) into the eNanoMapper data model. The parser code, the JSON syntax, documentation and example files are <a href="https://github.com/enanomapper/nmdataparser/" target=_blank class="qxternal">available</a>. The mapping enables a uniform approach towards import, storage and searching of the ENM physicochemical measurements and biological assay results. While the parser itself is open source, the configuration files may not be, thus not revealing the organisation of confidential data templates.</li> 
<li>Maps of the confidential spreadsheet templates are available on <a href='mailto:support@ideaconsult.net'>request</a></li> 
</ul>
<a href="http://www.beilstein-journals.org/bjnano/single/articleFullText.htm?publicId=2190-4286-6-165#F7" target='guide'>More</a>
  </div>       
  <div id="nanomaterial">
  	The Nano Particle Ontology (<a href='http://bioportal.bioontology.org/ontologies/NPO' target='bioportal'>NPO</a>) defines a nanomaterial (<a href='http://bioportal.bioontology.org/ontologies/ENM?p=classes&conceptid=http%3A%2F%2Fpurl.bioontology.org%2Fontology%2Fnpo%23NPO_199' target='bioportal'>NPO_199</a>) 
  	as equivalent to a chemical substance (<a href='http://bioportal.bioontology.org/ontologies/NPO?p=classes&conceptid=http%3A%2F%2Fpurl.bioontology.org%2Fontology%2Fnpo%23NPO_1973' target='bioportal'>NPO_1973</a>) 
  	that has as constituent a nano-object, nanoparticle, engineered nanomaterial, nanostructured material, or nanoparticle formulation. 
  	Chemical substances are classified as types of chemical entity (<a href='http://bioportal.bioontology.org/ontologies/NPO?p=classes&conceptid=http%3A%2F%2Fpurl.bioontology.org%2Fontology%2Fnpo%23NPO_1972' target='bioportal'>NPO_1972</a>). 
  </div>
  <div id="substance">
	  Chemical substance, a material with a definite chemical composition.
	  <a href='http://echa.europa.eu/documents/10162/13643/nutshell_guidance_substance_en.pdf' class='qxternal'  target='_blank' title='Identification and naming of 
	  substances under REACH and CLP'>REACH guide</a> 
	  <ul>
	  <li>Mono-constituent substances have one main constituent.</li>
	  <li>Multi-constituent substances have  two or more main constituents.</li>
	  <li>Substances may contain additives and impurities.</li>
	  <li>Main constituent: a constituent, not being an additive or impurity, in a substance that makes up a significant part of that substance.
	    Contributes to the naming of the substance. 
	  </li>
	  <li>An additive is a substance that has been intentionally added to stabilise the substance. Contributes to the substance composition (but not to the naming).</li>
	  <li>An impurity is an unintended constituent present in a substance, as produced. Does not contribute to the naming of the substance.</li>
	  </ul>
  </div>    


  <div id="i5z">
  	<a href='http://en.wikipedia.org/wiki/IUCLID' class='qxternal' target=_blank>IUCLID 5</a> exports and imports files in the I5Z format. 
  	I5Z stands for "IUCLID 5 Zip", as the file uses Zip file compression.
  	<br/>
    Only Substance (IUCLID 5.4, IUCLID 5.5) and ReferenceSubstance (IUCLID 5.0, IUCLID 5.4, IUCLID 5.5.) records
    (as defined by the <a href='http://iuclid.eu/index.php?fuseaction=home.format' class='qxternal'  target=_blank>IUCLID schema</a>) can be imported in this version of Ambit.
  </div>  
    <div id="substancesearch">
    <ul>
    <li><b>by Name</b>: Substance name or public name, starting with the specified string (case insensitive)</li>
    <li><b>by Name (pattern matching)</b>: Substance name or public name. Use * to matches any number of characters, use _ to match one character, e.g <i>*ether*</i> or <i>*ether</i>. 
    </li>
    <li><b>by Name (regexp)</b>: Substance name or public name, search by <a href='http://regexone.com/' target='_blank' class='qxternal'>regular expression</a></li>
    <li><b>by UUID</b>: Substance UUID</li>
    <li><b>by External identifier</b>: Any external identifier (of all types), exact search</li>
    </ul>
  </div>    
  <div id="category">
  Retrieve all substances, having studies in the selected category : one of Phys-Chem, Ecotoxicity, Environmental Fate, Toxicity.
  </div>      
  <div id="reliability">
  Retrieve all substances, having studies assigned the selected Klimish code:
	  <ul>
	  <li>1 (reliable without restriction)</li>
	  <li>2 (reliable with restrictions)</li>
	  <li>3 (not reliable)</li>
	  <li>4 (not assignable)</li>
	  <li>5 other</li>
	  <li>6 empty (not specified)</li>
	  </ul>
  </div>        
  <div id="studypurpose">
  Retrieve all substances, having studies assigned the selected study purpose:
	  <ul>
	  <li>K: key study</li>
	  <li>S: supporting study</li>
	  <li>WoE: weight of evidence</li>
	  <li>D: disregarded study</li>
	  <li>N/A: Not specified</li>
	  </ul>  
  </div>
  <div id="robuststudy">
  Retrieve all substances, having studies assigned the robust study flag Yes or No.
  </div>  
  <div id="resulttype">
  Retrieve all substances, having studies assigned with the selected result type:
	  <ul>
	  <li>E: experimental result</li>
	  <li>EP: experimental study planned</li>
	  <li>C: estimated by calculation</li>
	  <li>RAg: read-across based on grouping of substances (category approach)</li>
	  <li>RAa: read-across from supporting substance (structural analogue or surrogate)</li>
	  <li>Q: (Q)SAR</li>
	  <li>O: other</li>
	  <li>ND: no data</li>
	  <li>NA: not specified</li>
	  </ul>   
  </div>  
</div>      