<div class='helptitle' style='font-weight:bold;'>Help: Upload substances</div>
<div class='helpcontent'>
Substance import options:
<ul>	
<li><a href="${ambit_root}/ui/uploadsubstance" title="Multiple IUCLID files upload">Multiple files upload</a><a href='#' class='chelp multiupload'>?</a></li>
<li><a href="${ambit_root}/ui/uploadsubstance1" title="Single IUCLID file upload">Single file upload</a><a href='#' class='chelp singleupload'>?</a></li>
<li><a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a><a href='#' class='chelp server5'>?</a></li>
<li><a href="${ambit_root}/ui/updatesubstancei6" title="Retrieve substance(s) from IUCLID6 server">Retrieve substance(s) from IUCLID6 server</a><a href='#' class='chelp server6'>?</a></li>
</ul>	
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#i5z">IUCLID file</a></li>
    <li><a href="#nmparser">Configurable Excel templates</a></li>
    <li><a href="#reliability">Import filter: Reliability</a></li>
    <li><a href="#studypurpose">Import filter: Study purpose</a></li>
    <li><a href="#robuststudy">Import filter: Robust study</a></li>
    <li><a href="#resulttype">Import filter: Study result type</a></li>
    <li><a href="#referencetype">Import filter: Reference type</a></li>
    <li><a href="#quality">Import filters</a></li>
    <li><a href="#clear">Import mode</a></li>
    <li><a href="#server">Web services</a></li>
    <li><a href="#server5">Import from IUCLID5 SOAP web services</a></li>
    <li><a href="#server6">Import from IUCLID6 REST services</a></li>
    <li><a href="#multiupload">File upload</a></li>
    <li><a href="#singleupload">File upload</a></li>
  </ul>
  <div id="i5z">
  	<a href='http://en.wikipedia.org/wiki/IUCLID' class='qxternal' target=_blank>IUCLID</a> imports files in the .i5z and .i6z format. 
  	.i5z stands for "IUCLID 5 Zip", as the file uses Zip file compression. .i6z stands for "IUCLID 6 Zip",
  	<br/>
  	<ul>
    <li>*.i5z : IUCLID 5.4, IUCLID 5.5, IUCLID 5.6 </li>
    <li>*.i6z : IUCLID 6 </li>
    </ul>
  </div>
  <div id="multiupload">
  	Upload of multiple files in supported formats : IUCLDI5 (.i5z), IUCLID6 (.i6z), Excel + JSON,  W3C RDF.
  </div>
  <div id="singleupload">
  	Upload of single file in  supported formats IUCLDI5 (.i5z), IUCLID6 (.i6z), Excel + JSON,  W3C RDF. 
  </div>
  <div id="server">
  	Sends a request to verify if the IUCLID(5 or 6) server is accessible. The server URL and credentials have to be <a href='http://ambit.sourceforge.net/configure.html' class='qxternal'>configured</a> at AMBIT server side.
  	<br/>
  </div>
  <div id="server5">
  	Provide substance UUID or External Identifier. The substance(s) will be retrieved from the IUCLID5 server and imported intp AMBIT.
	<br/>
	IUCLID5 is no <a href='https://iuclid6.echa.europa.eu/archive-iuclid-5' class='qxternal'>longer supported by ECHA</a>.
	<br/>
	AMBIT provides support for both IUCLID5 and IUCLID6 services. The server URL and credentials have to be <a href='http://ambit.sourceforge.net/configure.html' class='qxternal'>configured</a> at AMBIT server side.		
	<br/>
  </div>
  <div id="server6">
  	Provide substance UUID or External Identifier. The substance(s) will be retrieved from the IUCLID6 server and imported into AMBIT.
  	<br/>
  	<a href='https://iuclid6.echa.europa.eu/' class='qxternal'><b>IUCLID</b></a> (International Uniform ChemicaL Information Database) is a software application to record, store, maintain and exchange data on intrinsic and hazard properties of chemical substances.
 	IUCLID6 is the current version.
 	<br/>
 	AMBIT provides support for both IUCLID5 and IUCLID6 services. The server URL and credentials have to be <a href='http://ambit.sourceforge.net/configure.html' class='qxternal'>configured</a> at AMBIT server side.
	<br/>
	</div>  
  <div id="nmparser">
    Excel spreadsheets can be imported with the help of an additional JSON configuration file, specifying the mapping between the spreadsheet content and the database internal model. 
    <a href='https://github.com/enanomapper/nmdataparser' class='qxternal'>More details</a>
  </div>  
  <div id="quality">
  	If checked, imports only high quality study records, according to the selected criteria. This option is only valdi fro IUCLID files! 
  </div>
  <div id="clear">
  The two checkboxes control whether the composition records and study records for the substances being imported will be cleared, if already in the database. 
  Each substance entry in the database is assigned a unique identifier in the form of a UUID. 
  If the input file is IUCLID (*.i5z or *.i6z), the identifiers are the IUCLID generated UUIDs already present in these files.
  If the input file is a spreadsheet, the JSON configuration defines which field to be used as an identifier and uses the field itself or generates UUID from the specified field 
  </div>

  <div id="reliability">
  	  If "Import only high quality study records" is checked, imports 
  	  only substances, with studies assigned the selected Klimish code:
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
  	If "Import only high quality study records" is checked, imports
  	  pnly substances, with studies assigned the selected study purpose:
	  <ul>
	  <li>K: key study</li>
	  <li>S: supporting study</li>
	  <li>WoE: weight of evidence</li>
	  <li>D: disregarded study</li>
	  <li>N/A: Not specified</li>
	  </ul>  
  </div>
  <div id="robuststudy">
  If "Import only high quality study records" is checked, imports
  only substances, with studies assigned the robust study flag Yes or No.
  </div>  
  <div id="resulttype">
  If "Import only high quality study records" is checked imports
  only substances, with studies assigned the selected result type:
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