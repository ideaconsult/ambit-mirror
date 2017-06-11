<div class='helptitle' style='font-weight:bold;'>Help: Task status</div>
<div class='helpcontent'>
You have initiated a long running task (e.g. data import). <a href='#' class='chelp what'>?</a>
<br/>Please wait until the "Ready" message and a link to the result page appear. An error message <a href='#' class='chelp error'>?</a> will be shown in case of error.  
</div>
<div id="keys" style="display:none;">
  <ul>
    <li><a href="#what"></a></li>
    <li><a href="#error"></a></li>
  </ul>
  <div id="error">
  	<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
	<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.png' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
	<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.png' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
	<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
	</div>
  </div>	
  <div id="what">
    Asynchronous jobs (e.g. data import, a calculation) are handled via an intermediate Task service. <br/>
    Browse tasks:
    <ul> 
<li><a href="${ambit_root}/task?max=100">All tasks</a></li>
<li><a href="${ambit_root}/task?search=Running">Running</a></li>
<li><a href="${ambit_root}/task?search=Completed">Completed</a></li>
<li><a href="${ambit_root}/task?search=Queued">Queued</a></li>
<li><a href="${ambit_root}/task?search=Error">Error</a></li>
</ul>

For developers, see <a href="http://ambit.sourceforge.net/api_task.html" class='qxternal' target=_blank title='Application Programming interface'>API</a>.
    
  </div>
</div>      