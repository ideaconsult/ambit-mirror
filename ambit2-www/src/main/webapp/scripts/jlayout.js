function searchFormValidation(formName) {
	$(formName).validate({
		rules : {
			'search': {
				required : true
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
			$('input:radio[name=option][value=smarts]').click();
		});						
			
		document.searchform.search.value = $.cookie('ambit2.search')==null?"50-00-0":$.cookie('ambit2.search');
		document.searchform.pagesize.value = $.cookie('ambit2.pagesize')==null?"10":$.cookie('ambit2.pagesize');
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
		//func groups
		try {
			$('#funcgroups').val($.cookie('ambit2.search')==null?"":$.cookie('ambit2.search'));
		} catch (err) {
			$('#funcgroups').val("");
		}						
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