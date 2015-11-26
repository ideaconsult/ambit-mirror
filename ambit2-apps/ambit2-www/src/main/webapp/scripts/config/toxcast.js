var config_toxcast = {
	"interpretation" : {
		"result" : {
			"sTitle" : "hitc",
			"bVisible" : true,
			"iOrder" : -10,
			"mRender" : function(data, type, full) {
				var c = "";
				if ("Active" == data) c = 'class="active"';
				else if ("Inactive" == data) c = 'class="inactive"';
				return "<span "+ c +">"+data+"</span></br/>" + full["interpretation"]["criteria"];
			},
			"inMatrix" : true
		},
		"criteria" : {
			"bVisible" : false,
			"iOrder" : -9
		}
	},
	"protocol" : {
		"citation" : {
			"bVisible" : true,
			"sTitle" : "Reference",
			"mRender" : function(data, type, full) {
				return "<label>"+data["owner"] + "</label><br/>" 
				+ full["protocol"]["guideline"] + "<br/>"
				+ data["title"];
			},
			"iOrder" : -1
		},		
		"guideline" : {
			"bVisible" : false,
			"iOrder" : -1		
		},
		"uuid" : {
			"bVisible" : false
		},
		"owner" : {
			"bVisible" : false,
			"iOrder" : -1
		}
	},
	"conditions" : {
		"bVisible": false,
		
		"concentration" : {
			"bVisible" : true,
			"iOrder" : -22		
		},

		"modl_rmse" : {
			"bVisible" : false,
			"sTitle" : "Model RMSE",
			"iOrder" : -17
		},
		"model" : {
	    	"sTitle" : "Model",
			"iOrder" : -2,
			"bVisible" : true
	    }
	},
	"effects" : {
		"endpoint" : {
			"iOrder" : -21
		},
		"result" : {
			"sTitle" : "",
			"iOrder" : -20
		},
		"text" : {
			"bVisible" : false
		}
	},
	"parameters" : {
		"bVisible": false,
		
		"detection_technology" : {
			"iOrder" : -38,
			"sTitle" : "Detection Technology",
			"bVisible" : true
		},
		"organism" : {
			"sTitle" : "Organism",
			"iOrder" : -37,
			"bVisible" : true
		},
		"tissue" : {
			"sTitle" : "Tissue",
			"iOrder" : -36,
			"bVisible" : true
		},		
		"cell_short_name" : {
			"sTitle" : "Cell",
			"iOrder" : -33,
			"bVisible" : true
		},
		"intended_target_gene_symbol" : {
			"sTitle" : "Intended target (gene)",
			"bVisible" : true,
			"iOrder" : -19
		}	,
		"timepoint" : {
			"sTitle" : "Time point",
			"iOrder" : -23,
			"bVisible" : true
		}
	    
	}	
}