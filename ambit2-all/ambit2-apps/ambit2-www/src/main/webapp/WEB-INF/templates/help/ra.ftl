<div class='helptitle' style='font-weight:bold;'>Help: Read Across workflow</div>
<div class='helpcontent'>
Workflow for read across and category formation. <a href="http://echa.europa.eu/support/grouping-of-substances-and-read-across" target="qxternal" class="qxternal">REACH guidance</a>  <br/>
The assessment <a href='#' class='chelp assessment'>?</a> workflow is organized in five main tabs: 
<ol>
<li>Assessment identifier<a href='#' class='chelp a_tab1'></a></li>
<li>Collect structures<a href='#' class='chelp a_tab2'></a></li>
<li>Endpoint data used<a href='#' class='chelp a_tab3'></a></li>
<li>Assessment details<a href='#' class='chelp a_tab4'></a></li>
<li>Endpoint data used<a href='#' class='chelp a_tab5'></a></li>
<li>Report<a href='#' class='chelp a_tab6'></a></li>
</ol>
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#assessment">Assessment</a></li>
    <li><a href="#a_name">Assessment name</a></li>
    <li><a href="#a_source">Assessment info</a></li>
    <li><a href="#a_description">Assessment description</a></li>
    <li><a href="#a_maintainer">Assessment owner</a></li>
    <li><a href="#a_name">Assessment name</a></li>
    <li><a href="#a_version">Assessment version</a></li>
    <li><a href="#a_license">Assessment license</a></li>
    <li><a href="#a_rightsholder">Assessment rights holder</a></li>
    <li><a href="#a_status">Assessment status</a></li>
    <li><a href="#a_started">Assessment start/complete dates</a></li>
    <li><a href="#a_flags">Assessment flags</a></li>
    <li><a href="#a_rating">Assessment rating</a></li>
    
    <li><a href="#a_tab1">Tab1. Assessment identifier</a></li>
    <li><a href="#a_tab2">Tab2. Collect structures</a></li>
    <li><a href="#a_tab3">Tab3. Endpoint data used</a></li>
    <li><a href="#a_tab4">Tab4. Assessment details</a></li>
    <li><a href="#a_tab5">Tab5. Report</a></li>
  </ul>
  <div id="assessment">
  	Assessment ID is automatically generated upon creating an Assessment.
  	<br/>
 	The assessment is available at {ambit_root}/bundle/{assessmentid}</a>. 
 	<br/>
  	To create a new Assessment, fill in the Assessment identifiers and click Start.
  	<br/>
  	To edit the Assessment identifiers, edit the relevant text fields. 
  </div>  
  <div id="a_name">
    Assessment name
  </div>  
  <div id="a_source">
    A placeholder for the assessment number (e.g. internal company number).
    The source URL field may contain an URL to relevant documents on the intranet or internet.  
  </div>  
  <div id="a_description">
    Purpose of the assessment or description. Free text.
  </div>    
  <div id="a_maintainer">
    The owner or maintainer of the assessment.
  </div>    
  <div id="a_version">
    Assessment version. Not supported yet.
  </div>    
  <div id="a_license">
    Enter URI of the  License / Waiver / Rights / Disclaimer <a href="http://dublincore.org/documents/dcmi-terms/#elements-rights" target='qxternal' class='qxternal'>?</a>
    
    <br/>
    A list of Open data licenses / waivers : 
    
    <ul>
    <li><a href='http://www.opendatacommons.org/licenses/pddl/' target='qxternal' class='qxternal'>Public Domain Dedication and License (PDDL) - 'Public Domain for data/databases'</a>
    <li><a href='http://www.opendatacommons.org/licenses/by/' target='qxternal' class='qxternal'>Open Data Commons Attribution (ODC-By) - 'Attribution for data/databases'</a>
    <li><a href='http://www.opendatacommons.org/licenses/odbl/' target='qxternal' class='qxternal'>Open Database License (ODC-ODbL) - 'Attribution Share-Alike for data/databases'</a>
    <li><a href='http://creativecommons.org/publicdomain/zero/1.0/' target='qxternal' class='qxternal'>CC0 1.0 Universal - 'Creative Commons public domain waiver'</a>
    
    <li><a href='http://creativecommons.org/licenses/by-sa/3.0/' target='qxternal' class='qxternal'>Creative Commons Attribution-ShareAlike (CC-BY-SA)</a>
    <li><a href='http://www.gnu.org/copyleft/fdl.html' target='qxternal' class='qxternal'>GNU Free Documentation License (GFDL)</a>
    <li><a href='http://opendatacommons.org/licenses/odbl/1.0/' target='qxternal' class='qxternal'>Open Database License v1.0</a>
    <li><a href='http://opendatacommons.org/licenses/by/1.0/' target='qxternal' class='qxternal'>Open Data Commons Attribution License (ODC-By) v1.0</a>
    
	</ul>
   
  </div>   
  <div id="a_rightsholder">
    <a href="http://dublincore.org/documents/dcmi-terms/#terms-rightsHolder" target='qxternal' class='qxternal'>Rights holder</a> A person or organization owning or managing rights over the resource. 
  </div>
  <div id="a_status">
    Status. Not supported yet
  </div>
  <div id="a_started">
    Start/completed date.
  </div>
  <div id="a_flags">
    Assessment version. Not supported yet.
  </div>   
  <div id="a_rating">
    Rating
  </div>         

   
      
  <div id="a_tab1">
	This tab. Allows to create new assessment or update the fields of an existing assessment.
  </div>
  <div id="a_tab2">
	<ul>
	<li>Search by identifiers: CAS, EC, Name, structure, substructure, similarity</li>
	<li>Tag the structure as initial target <b>T</b> or source <b>S</b>. Once tagged, the structure appears in the list of collected structures</li>
	<li>List collected structures</li>
	</ul>
  </div>
  <div id="a_tab3">
	For each structure of the list show the related substances and endpoint data.  
	<br/>
	By default shows only the endpoints available for the selected substances only. The <i>Show All</i> checkbox will show the full list.
	<br/>
	The assessor has to check mark those endpoints which should be displayed in the data matrix.
	<br/>
	Selecting at least one substance and endpoint will enable the <i>Assessment details</i> tab.
  </div>
  <div id="a_tab4">
  	The <b>data matrix</b> is separated into different  matrices, called <i>initial</i>, <i>working</i>, and <i>final</i>.
  	<ul>
  	<li>The <b>initial</b> matrix is generated automatically by the selection of substances and endpoints. Can not be edited.</li>
  	<li>The <b>working</b> matrix is generated upon user request from the initial matrix. Data points may be added or removed.</li>
  	<li>The <b>final</b> matrix is generated upon user request from the working matrix. Can not be edited.</li> 
  	</ul> 

  </div>      
  <div id="a_tab5">
	Not supported yet.
   </div>        
  
</div>      