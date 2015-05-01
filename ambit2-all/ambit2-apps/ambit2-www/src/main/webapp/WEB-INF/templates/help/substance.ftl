<div class='helptitle' style='font-weight:bold;'>Help: Substances</div>
<div class='helpcontent'>
Chemical substance, a material with a definite chemical composition.
<a href='http://echa.europa.eu/documents/10162/13643/nutshell_guidance_substance_en.pdf' class='qxternal'  target='_blank' title='Identification and naming of 
substances under REACH and CLP'>REACH guide</a> 
<br/>
Mono-constituent<a href='#' class='chelp mono'>?</a> and multi-constituent <a href='#' class='chelp multi'>?</a> substances.
Main constituent<a href='#' class='chelp main'>?</a>Additive<a href='#' class='chelp additive'>?</a>Impurity<a href='#' class='chelp impurity'>?</a>


</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#mono">Mono-constituent substance</a></li>
    <li><a href="#multi">Multi-constituent substance</a></li>
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
  </ul>
  <div id="mono">
  	A substance with one main constituent..
  </div>  
  <div id="multi">
	A substance with two or more main constituents.
  </div>
  <div id="main">
    A constituent, not being an additive or impurity, in a substance that makes up a significant part of that substance.
    Contributes to the naming of the substance. 
    Concentration of the main constituent(s) = purity of the substance.
  </div>
  <div id="additive">
    A substance that has been intentionally added to stabilise the substance. Contributes to the substance composition (but notto the naming).
  </div>
  <div id="impurity">
    An unintended constituent present in a substance, as produced. Does not contribute to the naming of the substance.
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
    <li><b>CompTox</b>: External identifier (of type CompTox), exact search</li>
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