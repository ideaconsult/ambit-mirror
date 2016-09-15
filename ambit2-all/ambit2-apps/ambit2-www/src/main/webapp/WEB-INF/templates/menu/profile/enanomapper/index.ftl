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

.mhelp {
}

.mhelp a:link {
    text-decoration: none;
}

.mhelp a:visited {
    text-decoration: none;
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

</style>


<script type='text/javascript'>

$(document)
		.ready(
				function() {
						$( "#smartmenu" ).smartmenus();    
						loadHelpAsTooltip("${ambit_root}","enmindex");
						jQuery("#breadCrumb").hide();
					    $( document ).tooltip({
      						track: true
					    });
					    
				});
</script>
</head>
<body>


<div class="container" style="margin:0;padding:0;">

<!-- banner -->

<div class="row half-bottom" id="header" style="padding-top:5px">
	<div class="one column">&nbsp;</div>
	<div class="fourteen columns remove-bottom">
		<#include "/menu/profile/enanomapper/smartmenu.ftl">
	</div>
	<div class="one column">&nbsp;</div>	
</div>
	
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row half-bottom">

		<div class="eight columns remove-bottom" style="padding-left:10px;">
			<div id="breadCrumb" class="breadCrumb module remove-bottom h5">
                    <ul>
                        <li>
                            <a href="${ambit_root}" title="AMBIT Home">Home</a>
                        </li>
                    </ul>
			</div>
		</div>
		
		<div class="seven columns remove-bottom"  id="_searchdiv">
		</div>

</div>	
<!-- end banner -->	
<div class="row add-bottom">&nbsp;</div>
<div class="row add-bottom">&nbsp;</div>
<div class="two columns" id="query">
&nbsp;
</div>	

<div class="thirteen columns remove-bottom" style="padding:0;" >
	<div class="row remove-bottom">
		
				<div class="three columns remove-bottom">
					<a href="http://enanomapper.net"><img class='scale-with-grid' border='0' src='${ambit_root}/images/profile/${menu_profile}/logo.png' title='eNanoMapper web site' alt='AMBIT logo'></a>
				</div>
				<div class="twelve columns remove-bottom h2">${custom_title!"eNanoMapper prototype database"}
				</div>
		
	</div>
	
	<div class="row remove-bottom">
		<div class="three columns remove-bottom">&nbsp;</div>
		<div class='twelve columns remove-bottom'><span class="mhelp enmhelp substance">${custom_description!"A substance database for nanomaterial safety information"}</span></div>
	</div>	
		
	<div class="row add-bottom">&nbsp;</div>	
	
	<div class="row remove-bottom">
		<form action='${service_search!"${ambit_root}/ui/_search?option=auto"}' id="searchForm"  method="GET" >	
			<div class="sixteen columns remove-bottom" >
			<div class="alpha">
				<div class="remove-bottom h4">
						<span class='mhelp nanomaterial5' style='"text-decoration: none;'>free text search</span>
				</div>
			    <div class='chelp'> </div>			
			</div>
			</div>
		<div class='row remove-bottom'>
			<input class='eight columns omega half-bottom' type="text" id='search' value='${custom_query!"TiO2"}' name='search'>
			<input class='three columns omega submit' type='submit' value='Search'>
		</div>
		
		<div class="sixteen columns add-bottom" >
				<span style='font-weight:bold;'>Search</span>
			    <a href="${ambit_root}/substance?type=&search=NM-111&page=0&pagesize=20" title="Search for nanomaterials by identifiers" class="mhelp substancesearch">by identifier</a> | 
			    <a href="${ambit_root}/query/study" title="Search substances by physico-chemical parameters or biological effects" class="mhelp nanomaterial3">by physchem parameters or biological effects</a> |
			    <a href="${ambit_root}/ui/_search?search=SiO2" title="Search nanomaterials by chemical structure of components" class="mhelp nanomaterial4">by composition</a> | 
			    <a href="${ambit_root}/substance?type=citation&search=10.1073&page=0&pagesize=20" title="Search for nanomaterials by paper reference" class="mhelp nanomaterial2">by citation</a> |  
			    <a href='${ambit_root}/substance' class="mhelp nanomaterial"><span style='font-weight:bold;'>Browse</span></a> |
				<a href='${ambit_root}/ui/uploadsubstance1' class="mhelp nanomaterial6"><span style='font-weight:bold;'>Upload</span></a>
		</div>
		</form>	
   <div>


	<div class='row add-bottom'>&nbsp;</div>
	<div class='row add-bottom'>&nbsp;</div>
	
	
	<div class="row add-bottom">
			<div class="ten columns" style=" box-shadow: 3px 3px 7px #999;border: 1px solid #ccc;padding: 1em 25px 25px 25px;background-color: #fafafa;">
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='pagehelp'></div>
				<div class='row remove-bottom chelp' style='padding:0;margin:0;font-weight:bold;' id='keytitle'></div>
				<div class='row half-bottom chelp' style='padding:0;margin:0;' id='keycontent'></div>		
			</div>

	
				
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
