<#escape x as x?html>
<div class='row'  style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
Model search
</div>
<div class='row'  style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<form method='GET' name='searchform' id='searchform' action='${ambit_root}/model' style='padding:0;margin:0;'>
<input type='text'  id='search' name='search' value='' tabindex='1' >
<input class='ambit_search' id='submit' type='submit' value='Search' tabindex='2'>
</form>
</div>

<div class='row' id='download' style='background: #F2F0E6;margin: 3px; padding: 0.4em; font-size: 1em; '>
<a href='#' id='uri'><img src='${ambit_root}/images/link.png' alt='text/uri-list' title='Download as URI list '></a>
<a href='#' id='rdfxml'><img src='${ambit_root}/images/rdf.gif' alt='RDF/XML' title='Download as RDF/XML (Resource Description Framework XML format)'></a>
<a href='#' id='rdfn3'><img src='${ambit_root}/images/rdf.gif' alt='RDF/N3' title='Download as RDF N3 (Resource Description Framework N3 format)'></a>
<a href='#' id='json' target=_blank><img src='${ambit_root}/images/json.png' alt='json' title='Download as JSON'></a>
</div>

<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'>		
</div>
<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'>		
</div>	
</#escape>