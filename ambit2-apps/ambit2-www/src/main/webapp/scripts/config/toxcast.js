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
		"concentration" : {
			"bVisible" : true,
			"iOrder" : -22		
		},
		"fitc" : {
			"bVisible" : false,
			"iOrder" : -19
		},
		"bmad" : {
			"bVisible" : false,
			"iOrder" : -18
		},
		"modl_rmse" : {
			"bVisible" : false,
			"sTitle" : "Model RMSE",
			"iOrder" : -17
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
		"cell_free_component_source" : {
			"iOrder" : -35,
			"bVisible" : false
		},
		"cell_format" : {
			"iOrder" : -34,
			"bVisible" : false
		},
		"cell_short_name" : {
			"sTitle" : "Cell",
			"iOrder" : -33,
			"bVisible" : true
		},
		"assay_format_type" : {
			"iOrder" : -32,
			"bVisible" : false
		},
		"assay_format_type_sub" : {
			"iOrder" : -31,
			"bVisible" : false
		},
		"dilution_solvent" : {
			"iOrder" : -30,
			"bVisible" : false
		},
		"dilution_solvent_percent_max" : {
			"iOrder" : -29,
			"bVisible" : false
		},
		"assay_design_type" : {
			"iOrder" : -28,
			"bVisible" : false
		},
		"assay_design_type_sub" : {
			"iOrder" : -27,
			"bVisible" : false
		},
		"signal_direction_type" : {
			"iOrder" : -26,
			"bVisible" : false
		},
		"key_assay_reagent" : {
			"iOrder" : -25,
			"bVisible" : false
		},
		"technological_target_type" : {
			"iOrder" : -24,
			"bVisible" : false
		},
		"technological_target_type_sub" : {
			"iOrder" : -23,
			"bVisible" : false
		},
		"timepoint" : {
			"sTitle" : "Time point",
			"iOrder" : -23,
			"bVisible" : true
		},
		"analysis_direction" : {
			"bVisible" : false,
			"iOrder" : -18
		},
		"signal_direction" : {
			"sTitle" : "Signal direction",
			"bVisible" : false,
			"iOrder" : -17
		},
		"key_positive_control" : {
			"bVisible" : false,
			"iOrder" : -16
		},
		"assay_function_type" : {
			"bVisible" : false,
			"iOrder" : -15
		},
		"intended_target_type" : {
			"sTitle" : "Target Type",
			"bVisible" : false,
			"iOrder" : -14
		},
		"intended_target_type_sub" : {
			"bVisible" : false,
			"iOrder" : -13
		},
		"intended_target_family" : {
			"sTitle" : "Target Family",
			"bVisible" : false,
			"iOrder" : -12
		},
		"intended_target_family_sub" : {
			"bVisible" : false,
			"iOrder" : -11
		},
		"intended_target_gene_symbol" : {
			"sTitle" : "Intended target (gene)",
			"bVisible" : true,
			"iOrder" : -19
		}		
	}	
}