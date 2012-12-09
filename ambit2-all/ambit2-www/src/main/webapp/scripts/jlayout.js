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
				range: [1, 100]
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