<#include "/html.ftl" >
<head>
<#include "/header_updated.ftl" >

<style>
h1, h2, h3 {
	color: #8C0305;
	font-weight: bold; }
		
h4, h5, h6 {
	color: #8C0305;
	font-weight: normal; 
}

.h1, .h2, .h3 {
	color: #8C0305;
	font-weight: bold; }
		
.h4, .h5, .h6 {
	color: #8C0305;
	font-weight: normal; }
	
.enmhelp {
	font-size: 0.75em;
	font-weight: bold italic !important;
	color: #8C0305;
}	

#footer {
	position: fixed;
	height: 80px;
	width: 25%;
	left: 75%;
	bottom: -30px;
	padding: 10px 0 0 0;
	margin-left: auto;
	margin-right: auto;
	margin-bottom: -30px;
	-webkit-border-radius: 20px;
	-khtml-border-radius: 20px;
	-moz-border-radius: 20px;
	border-radius: 20px;
	color: white;
	background-color: #8C0305;
	text-align: center;
}
.search_options { list-style-type: none; margin: 0; padding: 0; width: 600px; }
.search_options li { margin: 3px 3px 3px 0; padding: 20px 5px 5px 5px; float: left; width: 140px; height: 120px; font-size: 1em; vertical-align: middle; text-align: center; box-shadow: 5px 6px 6px #540103; border: 1px solid #8C0305; background-color: #fafafa; }
</style>


<script type='text/javascript'>

$(document)
		.ready(
				function() {
						loadHelp("${ambit_root}","nanomaterial");
						jQuery("#breadCrumb").hide();
						$( ".search_options" ).sortable();
					    $( ".search_options" ).disableSelection();
				});
</script>
</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->

<#include "/banner_crumbs.ftl">

	
<div class="two columns" id="query">
&nbsp;
</div>	

<div class="thirteen columns " style="padding:0;" >
	<div class="row add-bottom">
	&nbsp;
	</div>
	<div class="row add-bottom">
		
			<div class="sixteen columns remove-bottom" id="query">
			<div class="alpha" >
				<div class="remove-bottom h2">
						Welcome to eNanoMapper prototype database 
				</div>
			    <div class='enmhelp'> A substance database for nanomaterial safety information</div>			
			</div>
			</div>
	</div>	
		
	<div class="row add-bottom">&nbsp;</div>	
	
	<div class="row add-bottom">
		<form action="https://search.data.enanomapper.net/" id="searchForm"  method="GET" >	
			<div class="sixteen columns remove-bottom" id="query">
			<div class="alpha">
				<div class="remove-bottom h4">
						Try the new free text search
				</div>
			    <div class='chelp'> </div>			
			</div>
			</div>
		<div class='row half-bottom'>
			<input class='eight columns omega half-bottom' type="text" id='search' value='TiO2' name='search'>
			<input class='three columns omega submit' type='submit' value='Search'>
		</div>
		<!--
		<div class='row add-bottom h4'>
		<span class='eight columns'>
		Try the new  <a  href="https://search.data.enanomapper.net/" title="Free text search">free text search application</a>
		</span>
		</div>
		-->

		<div class="sixteen columns remove-bottom" id="query">

			<div class="alpha ten columns" style="padding: 0px 5px 5px 5px"> 
				<span style='font-weight:bold;'>Search, browse, upload</span>
				<ul class='search_options'>
			    <li><a href="${ambit_root}/substance?type=&search=NM-111&page=0&pagesize=20" title="Search for nanomaterials by identifiers">Search nanomaterials by identifier</a> <a href='#' class='chelp substancesearch'>?</a>
			    <br/>
			    <a href="${ambit_root}/substance?type=citation&search=10.1073&page=0&pagesize=20" title="Search for nanomaterials by paper reference">Search nanomaterials by citation</a> <a href='#' class='chelp nanomaterial2'>?</a></li>
			    <li><a href="${ambit_root}/query/study" title="Search substances by physico-chemical parameters or biological effects">Search nanomaterials by physchem parameters or biological effects</a> <a href='#' class='chelp nanomaterial3'>?</a></li>
			    <li><a href="${ambit_root}/ui/_search?search=SiO2" title="Search nanomaterials by chemical structure of components"><br/>Search nanomaterials by composition</a> <a href='#' class='chelp nanomaterial4'>?</a></li>
			    <!--
			    <li><a href="${ambit_root}/ontobucket?search=cytotoxicity&type=protocol&qe=true" title="Free text search (experimental)"><br/>Free text search</a> <a href='#' class='chelp nanomaterial5'>?</a></li>
			    -->
			    
			    <li><a href="https://search.data.enanomapper.net" title="Free text search"><br/>Free text search</a> <a href='#' class='chelp nanomaterial5'>?</a></li>
			    
			    <li><a href='${ambit_root}/substance'>Browse nanomaterials and studies</a> <a href='#' class='chelp nanomaterial'>?</a> <br/>
			    </li>
				<li><a href='${ambit_root}/ui/uploadsubstance1'>Data import</a> <a href='#' class='chelp nanomaterial6'>?</a>
				<span class='chelp'> Supported import formats: OECD HT<a href='#' class='chelp _oht'>?</a>, Excel spreadsheets<a href='#' class='chelp _nmdataparser'>?</a></a>
				</li>
				</ul>
			</div>
			<div class="one columns omega" style='padding:0;margin:0;'>&nbsp;</div>
			<div class="four columns omega" style=" box-shadow: 3px 3px 7px #999;border: 1px solid #ccc;padding: 1em 25px 25px 25px;background-color: #fafafa;">
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
				<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>		
			</div>

		</div>
		
		</div>		
	</form>				
	</div>	

	
	<div class='row add-bottom'>&nbsp;</div>

</div>   		



<div id='footer-out' class="sixteen columns">
	<div id='footer-in'>
		<div id='footer'>
			<a class='footerLink ' href='http://www.enanomapper.net'  title='eNanoMapper' target=_blank>eNanoMapper FP7 #604134</a>.
			<span style="font-size: 0.75em;">This project has received funding from the European Union''s Seventh Framework Programme for research, technological development and demonstration under grant agreement no 604134</span>
		</div>
	</div>
</div>
		
		

<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
