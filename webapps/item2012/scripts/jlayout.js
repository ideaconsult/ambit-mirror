function initLayout() {
//layout
		var myLayout = $('body').layout({
			//applyDefaultStyles		: true,
			closable				: false,
			resizable				: false,
			slidable				: true,
			livePaneResizing		: true,
			west__closable			: true,
			west__slidable			: true,
			west__togglerLength_closed: '100%',
			west__spacing_closed:		20,
			north__spacing_open		:0,
			north__spacing_closed	:0,
			south__spacing_open		:0,
			south__spacing_closed	:0,
			east__spacing_open		:0,
			east__spacing_closed	:0,
			south__size				:52,
			south__minSize			:52,
			south__maxSize			:52,
			north__size				:48,
			west__size				:200,
			east__size				:200
		});
		myLayout.close('west');
}						


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