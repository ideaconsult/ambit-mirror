<div class='helptitle' style='font-weight:bold;'>Help: Simple upload</div>
<div class='helpcontent'>
Simple upload using eNanoMapper Turtle file
<ul>	
<li><a href="${ambit_root}/ui/uploadenm" title="Turtle upload">upload</a><a href='#' class='chelp turtle'>?</a></li>
</ul>	
</div>

<div id="keys" style="display:none;">
  <ul>
    <li><a href="#turtle">Turtle upload</a></li>
  </ul>

  <div id="turtle">
  	 W3C RDF Turtle format, see <a href='https://github.com/egonw/enmrdf/' class='qxt'>these</a> eNanoMapper .ttl file examples	 
  </div>
 
  <div id="clear">
  The two checkboxes control whether the composition records and study records for the substances being imported will be cleared, if already in the database. 
  Each substance entry in the database is assigned a unique identifier in the form of a UUID. 
  If the input file is IUCLID (*.i5z or *.i6z), the identifiers are the IUCLID generated UUIDs already present in these files.
  If the input file is a spreadsheet, the JSON configuration defines which field to be used as an identifier and uses the field itself or generates UUID from the specified field 
  </div>

</div>      