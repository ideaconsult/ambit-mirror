function searchFormValidation(formName) {
	$(formName).validate({
		rules : {
			'search': {
				required : ($('input[name=b64search]').val() === undefined) || ($('input[name=b64search]').val() === null)
			},
			'b64search': {
				required : false
			},					
			'option': {
				required : true
			},
			'threshold': {
			},
			'funcgroups': {
			},
			'pagesize': {
				required : true,
				number	 : true,
				range: [1, 5000]
			}
		},
		messages : {
			'search'  : {
				required: "Please enter a search query (CAS, Chemical Name, SMILES, InChI or SMARTS in case of <i>Substructure</i> search), or click the 'Submit molecule' link to transfer the structure from JME."
			},
			'option' : "Please select an option",
			'threshold'  : {
				required: "Please select the Tanimoto similarity threshold."
			},
			'funcgroups'     : {
				required: "Please select a predefined functional group."
			},
			'pagesize'  : {
				required: "Please provide number of hits."
			}			
		}
	});
}

function initSearchForm() {
		$( "#search" )
			.focusout(function() {
				$('#b64search').attr("value",null);
				$('#type').attr("value","smiles");
		});
		$.each(funcgroups,function(index,val) {
			$('<option>',
					{
					    value: val["smarts"],
					    title: val["hint"]===undefined?
					    		val["family"] + " SMARTS: " + val["smarts"]:
					    		val["family"]+" " + 
					    		val["hint"]===undefined?"":val["hint"] + 
					    		" SMARTS: " + val["smarts"],
					    text: val["name"]
					}).appendTo("#funcgroups");
			});
		$('#funcgroups').change(function(){ 
			$('#search').attr("value",$(this).val());
			$("#molQuery").text("");
			$('#b64search').attr("value",null);
			document["searchform"].type.value = "smiles";
			$('input:radio[name=option][value=smarts]').click();
		});						
		$("#molQuery").text("");
		var cb64 = $.cookie('ambit2.b64search');
		var csearch;
		if (cb64===undefined || cb64=="") 
			csearch = $.cookie('ambit2.search')===undefined || $.cookie('ambit2.search')==null?'50-00-0':$.cookie('ambit2.search');
		else csearch="";
		
		try {
			$("#search").attr("value",csearch);
		} catch (err) {
			$("#search").attr("value","");
		}
		try {
			$("#b64search").attr("value",(cb64==null?"":cb64));
			$("#molQuery").text(cb64==null || cb64==""?"":"mol");
		} catch (err) {
			$("#b64search").attr("value","");
		}		
		try {
			$("#pagesize").attr("value",$.cookie('ambit2.pagesize')==null?"10":$.cookie('ambit2.pagesize'));
		} catch (err) {
			$("#pagesize").attr("value","10");
		}
		
		try {
			$('#funcgroups').val(csearch).change();
		} catch (err) {
			$('#funcgroups').val("").change();
		}							
		//option
		try {
			if ($.cookie('ambit2.option')==null) {
				$radios.filter('[value=auto]').attr('checked', true);
				clickAuto();
			} else {
				var $radios = $('input:radio[name=option]');
		       	$radios.filter('[value='+$.cookie('ambit2.option')+']').attr('checked', true);
		       	switch ($.cookie('ambit2.option')) {
		       	case 'smarts' : {clickSmarts(); break;}
		       	case 'similarity' : {clickSimilarity(); break;}
		       	default: clickAuto();
		       	}
			}
		} catch (err) {
			$radios.filter('[value=auto]').attr('checked', true);
			clickAuto();
		}
		//select
		try {
			$('#threshold').val($.cookie('ambit2.threshold'));
		} catch (err) {
			$('#threshold').val(0.9);
		}
		try {
			document["searchform"].type.value = $.cookie('ambit2.type'); 
		} catch (err) {
			document["searchform"].type.value = "smiles";
		}		
		//func groups
	
		searchFormValidation('#searchform');

}

function clickSimilarity() {
	$('#search').attr("title","Tanimoto similarity");
	$('#thresholdSpan').show();
	$('#funcgroupsSpan').hide();
	$('.help#strucSearch').text("Enter Chemical name, SMILES or InChI or draw a compound.");
}
function clickSmarts() {
	$('#search').attr("title","Substructure search");
	$('#thresholdSpan').hide();
	$('#funcgroupsSpan').show();
	$('.help#strucSearch').text("Enter SMARTS or draw a structure.");
}
function clickAuto() {
	$('#search').attr("title","Exact structure or search by an identifier. CAS, Chemical name, SMILES or InChI. The input type is guessed automatically.");
	$('#thresholdSpan').hide();
	$('#funcgroupsSpan').hide();
	$('.help#strucSearch').text("Enter CAS,EINECS, Chemical name, SMILES or InChI.");
}

function createStrucFormValidation(formName) {
	$(formName).validate({
		rules : {
			'CASRN': {
				required : false
			},		
			'EINECS': {
				required : false
			},
			'IUPACName': {
				required : function() {
					var name1 = $("#ChemicalName").attr("value");
					var name2 = $("#TradeName").attr("value");
					return ((name1==null) || ($.trim(name1)=='')) && ((name2==null) || ($.trim(name2)=='')); 
				}
			},
			'ChemicalName': {
				required : function() {
					var name1 = $("#IUPACName").attr("value");
					var name2 = $("#TradeName").attr("value");
					return ((name1==null) || ($.trim(name1)=='')) && ((name2==null) || ($.trim(name2)=='')); 
				}
			},
			'TradeName': {
				required : function() {
					var name1 = $("#ChemicalName").attr("value");
					var name2 = $("#IUPACName").attr("value");
					return ((name1==null) || ($.trim(name1)=='')) && ((name2==null) || ($.trim(name2)=='')); 
				}				
			},
			'IUCLID5_UUID': {
			},
			'customidname': {
			},
			'customid': {
			},
			'SMILES': {
				required : function() {
					var molFile = getMolFile();
					return ((molFile == null) || ("" == molFile)); //if no structures drawn, the smiles field should not be empty
				}
			},			
			'molfile': {
				required : function() {
					var smiles = getSmiles();
					return (smiles == null) || ( "" == smiles);
				}
			},			
			'InChI_std': {
			},			
			'InChIKey_std': {
			}
		},
		messages : {
			'CASRN': {
				required : "Please enter CAS Registry number (e.g. 50-00-0)"
			},		
			'EINECS': {
				required : "Please enter EC Registry number (e.g. 200-001-8)"
			},
			'IUPACName': {
				required : "Please enter a chemical name according to the IUPAC nomenclature"
			},
			'ChemicalName': {
				required : "Please enter a chemical name"
			},
			'TradeName': {
				required : "Please enter a trivial name"
			},
			'IUCLID5_UUID': {
				required : "Please enter a IUCLID5 unique identifier (UUID)"
			},
			'customidname': {
			},
			'customid': {
			},
			'SMILES': {
				required : "Please enter SMILES. Alternatively, draw the structure and click 'Get SMILES'"
			},			
			'molfile': {
				required : "MOL file expected. Draw the structure and click 'Get Mol file'"
			},			
			'InChI_std': {
				required : "Standard InChI expected"
			},			
			'InChIKey_std': {
				required : "Standard InChI key expected"
			}
		},
		submitHandler: function(form) {
			var molFile = getMolFile();
			if (molFile != null) {
				form.molfile.text = molFile;
			}
			var smiles = getSmiles();
			if (smiles != null) {
				form.smiles.value = smiles;
			}			
			form.submit();
		}		
	});
	
	$.validator.addMethod('isCASvalid', function (value) { 
        return /^[A-Z]{2}\d{1,2}\s\d{1}[A-Z]{2}$/.test(value); 
        }, 'Please enter a valid CAS RN'); 
}

function copyToClipboard (text) {
	  window.prompt ("Copy to clipboard: Ctrl+C, Enter", text);
}