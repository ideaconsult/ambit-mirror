<#include "/html.ftl" >

<head>
<#include "/header.ftl" >


<script type='text/javascript'>

$(document).ready(function() {
			$( "#selectable" ).selectable( "option", "distance", 18);
			loadHelp("${ambit_root}","substance");
					
			jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/substance" title="Substance">Substances</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/uploadsubstance" title="File upload">Import</a></li>');
		    jQuery("#breadCrumb ul").append('<li><a href="${ambit_root}/ui/updatesubstancei5" title="Retrieve substance(s) from IUCLID5 server">Retrieve substance(s) from IUCLID5 server</a></li>');
		    jQuery("#breadCrumb").jBreadCrumb();
		    
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
<div class="row remove-bottom" id="header">
	<#include "/toplinks.ftl">
</div>
<div class="row remove-bottom">
		<#include "/logo.ftl">
		<div class="thirteen columns remove-bottom" id="query">
			<div class="half-bottom h3">
				Substance import
			</div>
			<div id="breadCrumb" class="row breadCrumb module remove-bottom">
                    <ul>
                        <li>
                            <a href="${ambit_root}" title="AMBIT Home">Home</a>
                        </li>
                    </ul>
			</div>			    
		</div>
</div>		
<div class="row remove-bottom" >
	  <div id="header_bottom" class="remove-bottom">&nbsp;</div>
</div>

<div class="three columns" style="padding:0 2px 2px 2px 0;margin-right:0;" >
<#include "/menu_substance.ftl">
<ul >
<li class="ui-selectee">
<a href="${ambit_root}/admin"><span class="ui-icon ui-icon-wrench" style="float: left; margin-right: .3em;"></span>Admin</a>
</li>
</ul>
</div>

<div class="eleven columns remove-bottom" style="padding:0;" >

<div class='ui-widget-header ui-corner-top'>&nbsp;Retrieve substance(s) from IUCLID5 server</div>
<div class='ui-widget-content ui-corner-bottom'>	
		
	<div style="margin:5px;padding:5px;">

	<form action="${ambit_root}/substance" id="uploadForm"  method="POST" >		
	
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
		<label class='three columns alpha' title='Use predefined filtering criteria'>Import only high quality study records</label>
       	<input class='one column alpha half-bottom' type="checkbox" id="qaenabled" name="qaenabled" class="toggle" checked>
       	<span class='five columns alpha half-bottom' >(uncheck to import all records)</span>
       	<div class='seven columns omega'></div>
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
		<div class='six columns omega'></div>
	</div>
	<div class='row remove-bottom' >
		<label class='three columns alpha' for="i5user">IUCLID5 user</label>
		<input class='four columns alpha half-bottom' type="text" id='i5user' value='' name='i5user' title='IUCLID5 user (optional)' size="20">
		<div class='nine columns omega'></div>
	</div>
	<div class='row remove-bottom' >
		<label class='three columns alpha' for="i5pass">IUCLID5 password</label>
		<input class='four columns alpha half-bottom' type="password" id='i5pass' value='' name='i5pass' title='IUCLID5 password (optional)' size="20">
		<div class='nine columns omega'></div>
	</div>	
				
	<div class='row'>
		<label class='three columns alpha'>&nbsp;</label>
		<input class='four columns alpha' type='submit' class='submit' value='Submit'>
		<div class='nine columns omega'></div>
	</div>

	</form>	
	</div>

			
</div>

	<div class='row' >
		&nbsp;
	</div>		
	<div class='row half-bottom'>
		<label class='three columns alpha h6'>Substance file upload:</label>
		<div class='four columns alpha half-bottom'>
			<a href="${ambit_root}/ui/uploadsubstance" title="Multiple .i5z files upload">Multiple .i5z files upload</a>
		</div>
		<div class='four columns alpha half-bottom'>	
			<a href="${ambit_root}/ui/uploadsubstance1" title="Single .i5z file upload">Single .i5z file upload</a>
		</div>	
	</div>
	
<div class='row add-bottom' style="height:140px;">&nbsp;</div>
</div>

<#include "/chelp.ftl" >


<#include "/footer.ftl" >
</div> <!-- container -->
</body>
</html>