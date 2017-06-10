<div class='helptitle' style='font-weight:bold;'>Help: Upload substances</div>
<div class='helpcontent'>
Substance import options:
<ul>	
<li><a href="${ambit_root}/ui/uploadsubstance" title="Multiple IUCLID files upload">Multiple files upload</a><a href='#' class='chelp multiupload'>?</a></li>
<li><a href="${ambit_root}/ui/uploadsubstance1" title="Single IUCLID file upload">Single file upload</a><a href='#' class='chelp singleupload'>?</a></li>
<li><a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a><a href='#' class='chelp server'>?</a></li>
</ul>	
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#i5z">.i5z file</a></li>
    <li><a href="#nmparser">Configurable Excel templates</a></li>
    <li><a href="#reliability">Reliability</a></li>
    <li><a href="#studypurpose">Study purpose</a></li>
    <li><a href="#robuststudy">Robust study</a></li>
    <li><a href="#resulttype">Result type</a></li>
    <li><a href="#referencetype">Reference type</a></li>
    <li><a href="#quality">quality</a></li>
    <li><a href="#clear">clear</a></li>
    <li><a href="#server">server</a></li>
    <li><a href="#multiupload">multiple</a></li>
    <li><a href="#singleupload">single</a></li>
  </ul>
  <div id="i5z">
  	<a href='http://en.wikipedia.org/wiki/IUCLID' class='qxternal' target=_blank>IUCLID</a> imports files in the .i5z and .i6z format. 
  	I5Z stands for "IUCLID 5 Zip", as the file uses Zip file compression.
  	<br/>
  	<ul>
    <li>*.i5z : IUCLID 5.4, IUCLID 5.5, IUCLID 5.6 </li>
    <li>*.i6z : IUCLID 6 </li>
    </ul>
  </div>
    
  <div id="nmparser">
    Excel templates with JSON configuration. <a href='https://github.com/enanomapper/nmdataparser'>More details</a>
  </div>  
  
  <div id="reliability">
  	  Only import substances, with studies assigned the selected Klimish code:
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
  	  Only import substances, with studies assigned the selected study purpose:
	  <ul>
	  <li>K: key study</li>
	  <li>S: supporting study</li>
	  <li>WoE: weight of evidence</li>
	  <li>D: disregarded study</li>
	  <li>N/A: Not specified</li>
	  </ul>  
  </div>
  <div id="robuststudy">
  Only import substances, with studies assigned the robust study flag Yes or No.
  </div>  
  <div id="resulttype">
  Only import substances, with studies assigned the selected result type:
	  <ul>
	  <li>experimental result</li>
	  <li>experimental study planned</li>
	  <li>estimated by calculation</li>
	  <li>read-across based on grouping of substances (category approach)</li>
	  <li>read-across from supporting substance (structural analogue or surrogate)</li>
	  <li>(Q)SAR</li>
	  <li>other</li>
	  <li>no data</li>
	  <li>not specified</li>
	  </ul>   
  </div>  
</div>      