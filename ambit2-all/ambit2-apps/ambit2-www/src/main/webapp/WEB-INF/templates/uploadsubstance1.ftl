<#include "/html.ftl" >

<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/i5criteria.js'></script>

<script type='text/javascript'>

$(document)
		.ready(
				function() {
					loadHelp("${ambit_root}","substance");
					
					jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
		    		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance" title="File upload">Import</a></li>');
		    		jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance1" title="Upload substances as defined in a single IUCLID5 .i5d or .i5z file">Single .i5z file upload</a></li>');
		    		jQuery("#breadCrumb").jBreadCrumb();
					jQuery("#welcome").text("Substance Import");
					
					_i5.getQAOptions(_i5.qaSettings);
				});
				
</script>

</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="two columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/searchmenu/menu_substance.ftl">

</div>


		<!-- Page Content
		================================================== -->

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Import new substance(s)</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">	
	<form action="${ambit_root}/substance" id="uploadForm"  method="POST"   ENCTYPE="multipart/form-data">		
	
	<div class='row remove-bottom'>
		<label class='five columns alpha' for="file">File (.i5z or .i5d or xlsx)<a href='#' class='chelp i5z'>?</a><em>*</em></label>
		<input class='eight columns alpha half-bottom'  type="file" name="files[]" title='Add new substance(s) (.i5d or .i5z file)' size="60">
		<div class='three columns omega'></div>
	</div>
	<div class='row remove-bottom'>
	<label class='five columns alpha' for="file">JSON map for XLSX file<a href='#' class='chelp i5z'>?</a></label>
	<input class='eight columns alpha half-bottom'  type="file" name="jsonconfig" title='JSON configuration' size="60">
	<div class='three columns omega'></div>
	</div>	
	<div class='row remove-bottom'>
			<label class='four columns alpha' title='Uncheck to add or replace study records, retaining existing where relevant'>Clear existing study records</label>
		   	<input class='one column omega half-bottom' type="checkbox" id="clearMeasurements" name="clearMeasurements" class="toggle" checked>
		   	<label class='four columns omega' title='Uncheck to add or replace compositions'>Clear existing composition records</label>
		   	<input class='one column omega half-bottom' type="checkbox" id="clearComposition" name="clearComposition" class="toggle" checked>
		   	<div class='six columns omega'></div>
		</div>	  	    

		<div class='row remove-bottom'>
			<label class='six columns alpha' title='Use predefined filtering criteria'>Import only high quality study records</label>
	       	<input class='one column alpha half-bottom' type="checkbox" id="qaenabled" name="qaenabled" class="toggle" checked>
	       	<span class='five columns alpha half-bottom' >(uncheck to import all records)</span>
	       	<div class='four columns omega'></div>
		</div>	    
    <div class='row remove-bottom'>
			<div class='six columns alpha remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="purposeflag">Purpose flag</label>
				<a href="javascript:void(0)" onclick="_i5.selections.purposeflag.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.purposeflag.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.purposeflag.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a> 
				<br/>	
				<select multiple id="purposeflag" name="purposeflag" size='5'></select>
	    	</div>
			<div class='six columns omega remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="studyresulttype">Study result type</label>
				<a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.studyresulttype.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>				
				<br/>	
				<select multiple id="studyresulttype" name="studyresulttype" size='5'></select>
	    	</div>
			<div class='four columns omega remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="testmaterial">Test material</label>
				<a href="javascript:void(0)" onclick="_i5.selections.testmaterial.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.testmaterial.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.testmaterial.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>				
				<br/>	
				<select multiple id="testmaterial" name="testmaterial" size='3'></select>
	    	</div>

	    </div>
		<div class='row remove-bottom'>
			<div class='six columns alpha remove-bottom'>
				<label style="float: left; margin-left: .1em;" for="reliability">Reliability</label>
				<a href="javascript:void(0)" onclick="_i5.selections.reliability.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.reliability.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.reliability.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>				
				<br/>	
				<select multiple id="reliability" name="reliability" size='5'></select>
	    	</div>
			<div class='six columns omega remove-bottom'>
				<label span style="float: left; margin-left: .1em;" for="referencetype" >Reference type</label>
				<a href="javascript:void(0)" onclick="_i5.selections.referencetype.highQuality()" ><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span></a>
				<a href="javascript:void(0)" onclick="_i5.selections.referencetype.selectall(true);"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span></a> 
				<a href="javascript:void(0)" onclick="_i5.selections.referencetype.selectall(false)"><span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span></a>			
				<br/>	
				<select multiple id="referencetype" name="referencetype"  size='5'></select>
	    	</div>
			<div class='four columns omega remove-bottom'>
				<a href="javascript:void(0)" onclick="_i5.selections.highQuality()" title='Click to select a predefined set of high quality study criteria'>
				<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-flag"></span>Select high quality study criteria</a> 
				<br/>
				<a href="javascript:void(0)" onclick="_i5.selections.selectall(true);">
				<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-plus"></span>Select All</a> 
				<br/>
				<a href="javascript:void(0)" onclick="_i5.selections.selectall(false)">
				<span style="float: left; margin-left: .1em;" class="ui-icon ui-icon-minus"></span>Unselect all</a>
	    	</div>
	    	
		</div>				
	<div class='row'>
		<label class='three columns alpha'>&nbsp;</label>
		<input class='three columns alpha' type='submit' class='submit' value='Submit'>
		<div class='ten columns omega'></div>
	</div>
	</form>	
</div>			
</div>


<div class='row' >
		&nbsp;
	</div>	
	<div class='row half-bottom'>
		<div class='four columns alpha'>&nbsp;</div>
		<div class='twelve columns omega half-bottom'>
		    Substance import options: 
			<a href="${ambit_root}/ui/uploadsubstance" title="Multiple .i5z files upload">Multiple .i5z files upload</a>
			 | 
			<a href="${ambit_root}/ui/uploadsubstance1" title="Single .i5z file upload">Single .i5z file upload</a>
			 | 
			<a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a>
		</div>	
	</div>
	
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>
