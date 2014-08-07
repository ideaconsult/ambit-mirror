<#include "/html.ftl" >

<head>
<#include "/header_updated.ftl" >

<script type='text/javascript' src='${ambit_root}/scripts/i5criteria.js'></script>
<script type='text/javascript'>

$(document).ready(function() {
			loadHelp("${ambit_root}","substance");
					
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance" title="File upload">Import</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a></li>');
		    jQuery("#breadCrumb").jBreadCrumb();
		    jQuery("#welcome").text("Substance Import");
		    $('#option').change(function(){
   			    if ("UUID" == $(this).val()) {
   			    	$('#quuid').show();
					$('.qextid').hide();
   			    } else {
   			    	$('#quuid').hide();
					$('.qextid').show();
   			    }
			});
		    $('[name=options]').val( 'UUID' );
		    updateFormValidation("#uploadForm");
		    _i5.getQAOptions(_i5.qaSettings);
		    var purl = $.url();
			$('#uuid').attr('value',purl.param('uuid')===undefined?'':purl.param('uuid'));
});

function updateFormValidation(formName) {
	$(formName).validate({
		rules : {
			'uuid': {
				required : ($('select[name=option]').val() === 'UUID')
			}
			
		},
		messages : {
			'uuid'  : {
				required: "Please enter a Substance UUID"
			}			
		}
	});
}
</script>



</head>
<body>

<div class="container" style="margin:0;padding:0;">

<!-- banner -->
<#include "/banner_crumbs.ftl">

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/searchmenu/menu_substance.ftl">
<ul >
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
</ul>
</div>


<!-- Page Content
		================================================== -->
<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Retrieve substance(s) from IUCLID5 server</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">

	<form action="${ambit_root}/substance" id="uploadForm"  method="POST" autocomplete="off">		
	
	<div class='row remove-bottom'>
		<label class='three columns alpha' for="option">Select substance</label>
		<select  class='four columns alpha half-bottom'  name='option' id='option'>
		
			<option title='by UUID' value='UUID' selected>by UUID</option>
			<option title='by external identifier stored in IUCLID5' value='EXTID' >by External identifier</option>
		</select>
		<div class='nine columns omega'></div>
	</div>

	<div class='row remove-bottom' id='quuid'>
		<label class='three columns alpha' for="uuid">UUID<em>*</em></label>
		<input class='seven columns alpha half-bottom' type="text" id='uuid' value='' name='uuid' title='UUID' size="40">
		<div class='six columns omega'></div>
	</div>
	<div class='row remove-bottom qextid' style='display:none;'>
		<label class='three columns alpha' for="extidtype">External identifier type<em>*</em></label>
		<input class='four columns alpha half-bottom' type="text" name='extidtype' title='External identifier type' size="30" value="CompTox">
		<div class='nine columns omega'></div>
	</div>
	<div class='row remove-bottom qextid' style='display:none;'>
		<label class='three columns alpha' for="extidvalue">External identifier value<em>*</em></label>
		<input class='four columns alpha half-bottom' type="text" name='extidvalue' title='External identifier value' size="30" value="Ambit Transfer">
		<div class='nine columns omega'></div>
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
	<div class='row remove-bottom' >
		<hr/>
	</div>	
	<div class='row remove-bottom' >
		<label class='three columns alpha' title=''>&nbsp;</label>
		<div class='help'>The IUCLID5 fields are optional, if left empty, the default IUCLID5 server will be used</div>
	</div>			
	<div class='row remove-bottom' >
		<label class='three columns alpha' for="i5server">IUCLID5 server</label>
		<input class='seven columns alpha half-bottom' type="text" id='i5server' value='' name='i5server' title='IUCLID5 server URI (optional)' size="40">
		<div class='six columns omega'>
			<a href="javascript:void(0)" onclick="_i5.ping('${ambit_root}/loom/i5/default/ping','#i5server','#i5user','#i5pass')">Check connectivity</a>
		</div>
	</div>
	<div class='row remove-bottom' >
		<label class='three columns alpha' for="i5user">IUCLID5 user</label>
		<input class='four columns alpha half-bottom' type="text" id='i5user' value='' name='i5user' title='IUCLID5 user (optional)' size="20">
		<div class='three columns omega'>&nbsp;</div>
		<div class='six columns omega'>
			<img src='${ambit_root}/images/24x24_ambit.gif' id='imgping' style='display:none;'>
			<div id='task_status'></div>		
		</div>
	</div>
	<div class='row remove-bottom' >
		<label class='three columns alpha' for="i5pass">IUCLID5 password</label>
		<input class='four columns alpha half-bottom' type="password" id='i5pass' value='' name='i5pass' title='IUCLID5 password (optional)' size="20">
		<div class='three columns omega'>&nbsp;</div>
		<div class='six columns omega'>
			<div id='task_errorreport'></div>
		</div>		
	</div>	
				
	<div class='row'>
		<label class='ten columns alpha'>&nbsp;</label>
		<input class='four columns alpha' type='submit' class='submit' value='Submit'>
		<div class='two columns omega'></div>
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