<div class='helptitle' style='font-weight:bold;'>Help: Add new structure</div>
<div class='helpcontent'>
This page allows to add a new chemical structure and associated identifiers.
To upload a file with chemical structures and properties instead, use <a href="${ambit_root}/ui/uploadstruc">Import a new dataset</a>
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#hcas">CAS</a></li>
    <li><a href="#heinecs">EC</a></li>
    <li><a href="#hiupac">IUPAC name</a></li>
    <li><a href="#hname">Chemical name</a></li>
    <li><a href="#hi5uuid">IUCLID5 UUID</a></li>
    <li><a href="#hcustomidname">Custom identifier name</a></li>
    <li><a href="#hcustomid">Custom identifier value</a></li>
    <li><a href="#hsmiles">SMILES</a></li>
    <li><a href="#hmolfile">MOL file</a></li>
    <li><a href="#hinchi">InChI</a></li>
    <li><a href="#hinchikey">InChI Key</a></li>
    <li><a href="#hdraw">Draw options</a></li>
    <li><a href="#hsubmit">Submit</a></li>
  </ul>
  <div id="hcas">
    <p>Enter <a href="http://en.wikipedia.org/wiki/CAS_registry" target=_blank>CAS registry</a> number</p>
    Click on <b>Lookup</b> link to retrieve all data available.
    <br/> 
    <a href="${ambit_root}/ui/query?option=auto&search=50-00-0" target=_blank>Search by CAS 50-00-0</a>
  </div>
  <div id="heinecs">
    <p>Enter <a href="http://en.wikipedia.org/wiki/European_Commission_number" target=_blank>EC registry number</a></p>
    The European Commission number, or EC number, also known as EC No., EINECS No., and EC#, is a unique seven-digit identifier that is assigned to chemical substances for regulatory purposes within the European Union by the regulatory authorities.
    <br/>
        Click on <b>Lookup</b> link to retrieve all data available.
    <br/> 
    <a href="${ambit_root}/ui/query?option=auto&search=200-001-8" target=_blank>Search by EC 200-001-8</a>
  </div>
  <div id="hiupac">
    <p>Enter IUPAC name</p>
    Click on <b>Lookup</b> link to retrieve all data available.
    <br/>
  </div>
  <div id="hinchi">
    <p>Enter <a href="http://en.wikipedia.org/wiki/International_Chemical_Identifier" target=_blank>InChI</a></p>
   	  The IUPAC International Chemical Identifier (InChI) is a textual identifier for chemical substances, designed to provide a standard and human-readable way to encode molecular information and to facilitate the search for such information in databases and on the web.
   	  <br/>
   	  Click on <b>Lookup</b> link to retrieve all data available.
    <br/>
  </div>
  <div id="hinchikey">
    <p>
	The InChIKey, sometimes referred to as a hashed InChI, is a fixed length (25 character) condensed digital representation of the <a href="http://en.wikipedia.org/wiki/International_Chemical_Identifier" target=_blank>InChI</a> that is not human-understandable.
    </p>
  </div>
   
  
  <div id="hname">
    <p>Enter Chemical name</p>
    Click on <b>Lookup</b> link to retrieve all data available.
    <br/>
    <a href="${ambit_root}/ui/query?option=auto&search=formaldehyde" target=_blank>Search by name "formaldehyde"</a>
  </div>
  <div id="hi5uuid">
    Enter <a href="http://iuclid.eu/" target=_blank>IUCLID5</a> <a href="http://iuclid.eu/index.php?fuseaction=home.downloadsubstances" target=_blank>Reference substance</a> unique identifier (UUID).
    For example ECB5-053aa8c4-d29b-4aa5-b457-5cc3b47f7d8b is the UUID for formaldehyde.
    <br>
    Click on <b>Lookup</b> link to retrieve all data available.
    <br/>
    <a href="${ambit_root}/ui/query?option=auto&search=ECB5-053aa8c4-d29b-4aa5-b457-5cc3b47f7d8b" target=_blank>Search by UUID</a>
  </div>
  <div id="hcustomidname">
    Enter Custom identifier title. If a custom identifier is required, please enter the identifier title (e.g. MyCompanyChemicalID)
  </div>
  <div id="hcustomid">
    Enter Custom identifier value. If a custom identifier is required, please enter the identifier value (e.g. 123456)
    <br/>
    Click on <b>Lookup</b> link to retrieve all data available.
    <br/>
  </div>
  <div id="hsearch">
    Draw
  </div>
  <div id="hsubmit">
    To register a new molecule, at least one of chemical name fields should be filled in, and at least one of SMILES or Mol file fields should be non empty.
  </div>  
    <div id="hsmiles">
    <p>Enter <a href="http://en.wikipedia.org/wiki/Simplified_molecular-input_line-entry_system" target=_blank">SMILES</a> or draw the molecule
    with JME and click "Get SMILES" link
    </p>
  </div>
    <div id="hmolfile">
    <p>Enter <a href="http://en.wikipedia.org/wiki/Chemical_table_file" target=_blank">MOL file</a> or draw the molecule
    with JME and click "Get MOL file" link
    </p>
  </div>  
  <div id="hdraw">
    This page uses <a href="http://www.molinspiration.com/jme/" target=_blank>JME Molecular Editor</a> applet to draw 2D molecular structure.
    You need web browser Java support to run the applet!
    <ul>
    <li><a href="http://java.com/en/download/help/enable_browser.xml" target=_blank>How do I enable Java in my web browser?</a></li>
    <li><a href="${ambit_root}/jme/jme_help.html" target=_blank>How to use JME</a></li>
    </ul>   
  </div>    
</div>
