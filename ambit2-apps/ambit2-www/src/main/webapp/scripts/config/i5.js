var config_i5 = {
	"GI_GENERAL_INFORM_SECTION" : {
		"conditions" : {
			"bVisible" : false,
			"remark" : {
				"iOrder" : -4,
				"bVisible" : true
			}
		},
		"parameters" : {
			"bVisible" : false
		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Parameter",
				"iOrder" : -6,
				"inMatrix" : true
			},
			"text" : {
				"sTitle" : "Result",
				"iOrder" : -5,
				"bVisible" : true,
				"inMatrix" : true
			},
			"result" : {
				"bVisible" : false
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Substance type",
				"iOrder" : -3,
				"inMatrix" : true,
				"bVisible" : false
			}
		},
		"protocol" : {
			"owner" : {
				"sTitle" : "Provider"
			},
			"uuid" : {
				"bVisible" : true
			},
			"reliability" : {
				"bVisible" : false
			}
		}
	},
	"PC_MELTING_SECTION" : {
		"parameters" : {
			"bVisible" : false
		},
		"conditions" : {
			"bVisible" : false,
			"decomposition" : {
				"iOrder" : -2,
				"bVisible" : true
			},
			"sublimation" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -3,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_THERMAL_STABILITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"],
		"effects" : {
			"endpoint" : {
				"iOrder" : -9,
				"bVisible" : true,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -8,
				"bVisible" : true,
				"inMatrix" : true
			},
			"text" : {
				"sTitle" : "Result",
				"iOrder" : -7,
				"bVisible" : true,
				"inMatrix" : true
			}
		}
	},	
	"PC_BOILING_SECTION" : {
		"parameters" : {
			"bVisible" : false
		},
		"conditions" : {
			"bVisible" : false,
			"atm. pressure" : {
				"sTitle" : "Pressure",
				"inMatrix" : true,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -1,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_VAPOUR_SECTION" : {
		"parameters" : {
			"bVisible" : false
		},
		"conditions" : {
			"bVisible" : false,
			"temperature" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"decomposition" : {
				"bVisible" : true

			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -3,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_PARTITION_SECTION" : {
		"parameters" : {
			"bVisible" : false,

			"method type" : {
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"temperature" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"ph" : {
				"sTitle" : "pH",
				"iOrder" : -1,
				"inMatrix" : true,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -3,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"SURFACE_TENSION_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"method type" : {
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"temperature" : {
				"iOrder" : -3,
				"inMatrix" : true,
				"bVisible" : true
			},
			"ph" : {
				"sTitle" : "pH",
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"remark" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -4,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}		
	},
	"PC_WATER_SOL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"method type" : {
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"temperature" : {
				"iOrder" : -3,
				"inMatrix" : true,
				"bVisible" : true
			},
			"ph" : {
				"sTitle" : "pH",
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"remark" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -4,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_SOL_ORGANIC_SECTION" : {
		"parameters" : {
			"bVisible" : false
		},
		"conditions" : {
			"bVisible" : false,
			"solvent" : {
				"iOrder" : -4,
				"inMatrix" : true,
				"bVisible" : true
			},
			"temperature" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"remark" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Solubility in solv",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_NON_SATURATED_PH_SECTION" : {
		"parameters" : {
			"method type" : {
				"bVisible" : false
			}
		},
		"conditions" : {
			"temperature" : {
				"iOrder" : -2,
				"inMatrix" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Concentration",
				"iOrder" : -1
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "pH Value",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_DISSOCIATION_SECTION" : {
		"parameters" : {
			"bVisible" : false
		},

		"conditions" : {
			"bVisible" : false,
			"no" : {
				"sTitle" : "pKa No.",
				"iOrder" : -3,
				"bVisible" : true
			},
			"temperature" : {
				"iOrder" : -4,
				"inMatrix" : true,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "pKa Value",
				"inMatrix" : true,
				"iOrder" : -5
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"PC_UNKNOWN_SECTION" : {
		"effects" : {
			"endpoint" : {
				"inMatrix" : true,
				"bVisible" : true
			},
			"result" : {
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : true,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"bVisible" : true,
				"inMatrix" : true
			}
		}
	},
	"TO_ACUTE_ORAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "Moved to conditions",
				"iOrder" : -7,
				"inMatrix" : false,
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"sex" : {
				"bVisible" : true,
				"iOrder" : -4
			},
			"species" : {
				"bVisible" : true,
				"iOrder" : -7,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"iOrder" : -3,
				"sTitle" : "Interpretation of the results"
			},
			"criteria" : {
				"iOrder" : -2
			}
		}
	},
	"TO_ACUTE_DERMAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "Moved to conditions",
				"iOrder" : -7,
				"inMatrix" : false,
				"bVisible" : false
			},

			"sex" : {
				"bVisible" : true,
				"iOrder" : -4
			}
		},
		"conditions" : {
			"sex" : {
				"bVisible" : true,
				"iOrder" : -4
			},
			"species" : {
				"bVisible" : true,
				"iOrder" : -7,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"iOrder" : -3,
				"sTitle" : "Interpretation of the results"
			},
			"criteria" : {
				"iOrder" : -2
			}
		}
	},
	"TO_ACUTE_INHAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "Moved to conditions",
				"iOrder" : -7,
				"inMatrix" : false,
				"bVisible" : false
			},
			"sex" : {
				"bVisible" : true,
				"iOrder" : -4
			}
		},
		"conditions" : {
			"bVisible" : false,
			"species" : {
				"bVisible" : true,
				"iOrder" : -8,
				"inMatrix" : true
			},
			"sex" : {
				"bVisible" : true,
				"iOrder" : -4
			},
			"route of administration" : {
				"bVisible" : true,
				"iOrder" : -7
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"iOrder" : -3,
				"sTitle" : "Interpretation of the results"
			},
			"criteria" : {
				"iOrder" : -2
			}
		}
	},
	"TO_ACUTE_OTHER_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "Moved to conditions",
				"iOrder" : -7,
				"inMatrix" : false,
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"sex" : {
				"bVisible" : true,
				"iOrder" : -4
			},
			"species" : {
				"bVisible" : true,
				"iOrder" : -7,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"iOrder" : -3,
				"sTitle" : "Interpretation of the results"
			},
			"criteria" : {
				"iOrder" : -2
			}
		}
	},	
	"TO_SKIN_IRRITATION_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"type of method" : {
				"iOrder" : -19,
				"sTitle" : "Method type",
				"inMatrix" : true,
				"bVisible" : true
			},
			"species" : {
				"iOrder" : -18,
				"bVisible" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : true,
				"inMatrix" : true,
				"iOrder" : -10
			},

			"result" : {
				"bVisible" : true,
				"inMatrix" : true,
				"iOrder" : -9
			}
		},

		"interpretation" : {
			"result" : {
				"sTitle" : "Interpretation of the results",
				"iOrder" : -7,
				"inMatrix" : true
			},
			"criteria" : {
				"iOrder" : -6
			}
		}
	},
	"TO_EYE_IRRITATION_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"type of method" : {
				"iOrder" : -17,
				"sTitle" : "Method type",
				"inMatrix" : true,
				"bVisible" : true
			},
			"species" : {
				"iOrder" : -16,
				"bVisible" : true
			}
		},

		"protocol" : {
			"guideline" : {
				"iOrder" : -1
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false
		},
		"effects" : {

			"endpoint" : {
				"bVisible" : true,
				"inMatrix" : true,
				"iOrder" : -10
			},
			"result" : {
				"bVisible" : true,
				"inMatrix" : true,
				"iOrder" : -9
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Interpretation of the results",
				"iOrder" : -3,
				"inMatrix" : true
			},
			"criteria" : {
				"iOrder" : -2
			}
		}

	},
	"TO_SENSITIZATION_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"iOrder" : -6,
				"bVisible" : false
			},
			"type of study" : {
				"iOrder" : -15,
				"sTitle" : "Study type",
				"inMatrix" : true,
				"bVisible" : true
			},
			"type of method" : {
				"iOrder" : -14,
				"sTitle" : "Method type",
				"bVisible" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -12,
				"inMatrix" : true,
				"bVisible" : true
			},
			"result" : {
				"iOrder" : -11,
				"inMatrix" : true,
				"bVisible" : true
			},
			"text" : {
				"sTitle" : "",
				"inMatrix" : true,
				"bVisible" : false,
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Interpretation of the results",
				"iOrder" : -10,
				"inMatrix" : true
			},
			"criteria" : {
				"iOrder" : -9
			}
		},
		"conditions" : {
			"bVisible" : false,
			"doses/concentrations" : {
				"iOrder" : -8,
				"inMatrix" : true,
				"bVisible" : true
			},
			"group" : {
				"iOrder" : -7,
				"inMatrix" : true,
				"bVisible" : true
			},
			"sampling time" : {
				"iOrder" : -6,
				"inMatrix" : true,
				"bVisible" : true
			},
			"total no in group" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			}
		}
	},
	"TO_REPEATED_ORAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -10,
				"inMatrix" : false,
				"bVisible" : false
			},
			"test type" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			},
			"route of administration" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Dose/concentrations",
				"iOrder" : -7,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"test type" : {
				"iOrder" : -9,
				"inMatrix" : true,
				"bVisible" : true
			},
			"species" : {
				"iOrder" : -10,
				"inMatrix" : true,
				"bVisible" : true
			},
			"sex" : {
				"iOrder" : -4,
				"bVisible" : true
			},
			"criticaleffectsobserved" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Critical Effects Observed"
			},
			"organ" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Target organ"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_REPEATED_INHAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -10,
				"inMatrix" : false,
				"bVisible" : false
			},
			"test type" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			},
			"route of administration" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Dose/concentrations",
				"iOrder" : -7,
				"bVisible" : true

			}
		},
		"conditions" : {
			"bVisible" : false,
			"test type" : {
				"iOrder" : -9,
				"inMatrix" : true,
				"bVisible" : true

			},
			"species" : {
				"iOrder" : -10,
				"inMatrix" : true,
				"bVisible" : true
			},

			"sex" : {
				"iOrder" : -4,
				"bVisible" : true
			},
			"organ" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Target organ"
			},
			"criticaleffectsobserved" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Critical Effects Observed"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_REPEATED_DERMAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -10,
				"inMatrix" : false,
				"bVisible" : false
			},
			"type_coverage" : {
				"iOrder" : -10,
				"sTitle" : "Type of coverage",
				"bVisible" : true
			},
			"route of administration" : {
				"iOrder" : -8
			},
			"test type" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			},
			"doses/concentrations" : {
				"sTitle" : "Dose/concentrations",
				"iOrder" : -7,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"test type" : {
				"iOrder" : -9,
				"inMatrix" : true,
				"bVisible" : true

			},

			"species" : {
				"iOrder" : -11,
				"inMatrix" : true,
				"bVisible" : true
			},

			"sex" : {
				"iOrder" : -4,
				"bVisible" : true
			},
			"organ" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Target organ"
			},
			"criticaleffectsobserved" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Critical Effects Observed"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_REPEATED_OTHER_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -10,
				"inMatrix" : false,
				"bVisible" : false
			},
			"test type" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			},
			"route of administration" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Dose/concentrations",
				"iOrder" : -7,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"test type" : {
				"iOrder" : -9,
				"inMatrix" : true,
				"bVisible" : true
			},
			"species" : {
				"iOrder" : -10,
				"inMatrix" : true,
				"bVisible" : true
			},
			"sex" : {
				"iOrder" : -4,
				"bVisible" : true
			},
			"criticaleffectsobserved" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Critical Effects Observed"
			},
			"organ" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Target organ"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},	
	"TO_GENETIC_IN_VITRO_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"type of genotoxicity" : {
				"iOrder" : -9,
				"sTitle" : "Genotoxicity type",
				"bVisible" : true
			},
			"type of study" : {
				"comment" : "copied to conditions",
				"iOrder" : -8,
				"sTitle" : "Study type",
				"inMatrix" : false,
				"bVisible" : false
			},
			"metabolic activation system" : {
				"iOrder" : -7,
				"bVisible" : true
			},
			"target gene" : {
				"iOrder" : -6,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"type of study" : {
				"iOrder" : -8,
				"sTitle" : "Study type",
				"inMatrix" : true,
				"bVisible" : true
			},
			"metabolic activation system" : {
				"iOrder" : -4,
				"sTitle" : "Metabolic activation",
				"bVisible" : true

			},
			"metabolic activation" : {
				"iOrder" : -4,
				"bVisible" : true
			},
			"species" : {
				"iOrder" : -5,
				"sTitle" : "Species/strain",
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"bVisible" : false,
				"sTitle" : "Genotoxicity",
				"iOrder" : -3
			},
			"text" : {
				"sTitle" : "Genotoxicity",
				"bVisible" : true,
				"iOrder" : -3
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Interpretation of the result",
				"bVisible" : false,
				"iOrder" : -2,
				"inMatrix" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"TO_GENETIC_IN_VIVO_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"type of study" : {
				"comment" : "copied to conditions",
				"iOrder" : -8,
				"sTitle" : "Study type",
				"inMatrix" : false,
				"bVisible" : false
			},
			"type of genotoxicity" : {
				"iOrder" : -9,
				"sTitle" : "Genotoxicity type",
				"bVisible" : true
			},
			"route of administration" : {
				"iOrder" : -7,
				"bVisible" : true
			},
			"species" : {
				"iOrder" : -6,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"type of study" : {
				"iOrder" : -8,
				"sTitle" : "Study type",
				"inMatrix" : true,
				"bVisible" : true
			},
			"sex" : {
				"iOrder" : -5,
				"bVisible" : true
			},
			"toxicity" : {
				"iOrder" : -4,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : true,
				"iOrder" : -3
			},
			"result" : {
				"bVisible" : false,
				"sTitle" : "Result",
				"iOrder" : -3
			},
			"text" : {
				"sTitle" : "Result",
				"bVisible" : true,
				"iOrder" : -3
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Interpretation of the result",
				"bVisible" : false,
				"iOrder" : -2,
				"inMatrix" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"TO_CARCINOGENICITY_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -10,
				"inMatrix" : false,
				"bVisible" : false
			},
			"route of administration" : {
				"iOrder" : -9,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"iOrder" : -8,
				"sTitle" : "Dose/concentrations",
				"bVisible" : true
			},
			"type of genotoxicity" : {
				"bVisible" : false

			},
			"type of study" : {
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"species" : {
				"iOrder" : -10,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect type" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"sex" : {
				"bVisible" : false
			},
			"toxicity" : {
				"bVisible" : false
			},
			"organ" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Target organ"
			},
			"criticaleffectsobserved" : {
				"iOrder" : -4,
				"bVisible" : true,
				"sTitle" : "Critical Effects Observed"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -7,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -1
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"iOrder" : -3,
				"bVisible" : false,
				"sTitle" : "Interpretation of the results"
			},
			"criteria" : {
				"bVisible" : false,
				"iOrder" : -2
			}
		}
	},
	"TO_REPRODUCTION_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -15,
				"inMatrix" : false,
				"bVisible" : false
			},
			"route of administration" : {
				"iOrder" : -14,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Dose/concentrations",
				"iOrder" : -13,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"species" : {
				"iOrder" : -15,
				"inMatrix" : true,
				"bVisible" : true
			},
			"generation" : {
				"iOrder" : -12,
				"inMatrix" : true,
				"bVisible" : true
			},
			"sex" : {
				"iOrder" : -8,
				"bVisible" : true
			},

			"reproductiveeffectsobserved" : {
				"iOrder" : -7,
				"bVisible" : true,
				"sTitle" : "Reproductive Effects Observed"
			},
			"relationtoothertoxiceffects" : {
				"iOrder" : -6,
				"bVisible" : true,
				"sTitle" : "Relation To Other Toxic Effects"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -9,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_DEVELOPMENTAL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"species" : {
				"comment" : "copied to conditions",
				"iOrder" : -10,
				"inMatrix" : false,
				"bVisible" : false
			},

			"route of administration" : {
				"iOrder" : -9,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Dose/concentrations",
				"iOrder" : -8,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"species" : {
				"iOrder" : -10,
				"inMatrix" : true,
				"bVisible" : true
			},
			"sex" : {
				"iOrder" : -4,
				"bVisible" : false
			},
			"effect type" : {
				"iOrder" : -7,
				"inMatrix" : true,
				"bVisible" : true
			},
			"developmentaleffectsobserved" : {
				"iOrder" : -7,
				"bVisible" : true,
				"sTitle" : "Developmental Effects Observed"
			},
			"relationtoothertoxiceffects" : {
				"iOrder" : -6,
				"bVisible" : true,
				"sTitle" : "Relation To Other Toxic Effects"
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -3,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_PHOTOTRANS_AIR_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"reactant" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"reactant" : {
				"iOrder" : -9,
				"inMatrix" : true
			},
			"test condition" : {
				"iOrder" : -8,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : true,
				"iOrder" : -7
			},
			"result" : {
				"inMatrix" : true,
				"iOrder" : -6
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_PHOTOTRANS_SOIL_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"reactant" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"reactant" : {
				"iOrder" : -9,
				"inMatrix" : true
			},
			"test condition" : {
				"iOrder" : -8,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : true,
				"iOrder" : -7
			},
			"result" : {
				"inMatrix" : true,
				"iOrder" : -6
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_HYDROLYSIS_SECTION" : {
		"parameters" : {
			"bVisible" : false
		},
		"conditions" : {
			"bVisible" : false,
			"ph" : {
				"iOrder" : -3,
				"inMatrix" : true,
				"bVisible" : true
			},
			"temperature" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "DT50",
				"inMatrix" : true,
				"iOrder" : -1
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"TO_BIODEG_WATER_SCREEN_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test type" : {
				"iOrder" : -4,
				"bVisible" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"sampling time" : {
				"iOrder" : -3,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Degrad. Parameter",
				"iOrder" : -1,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Degradation",
				"iOrder" : -2,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"criteria" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Result",
				"inMatrix" : true
			}
		}
	},
	"TO_BIODEG_WATER_SIM_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test type" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"test type" : {
				"iOrder" : -9,
				"inMatrix" : true,
				"bVisible" : true
			},
			"sampling time" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"degradation" : {
				"iOrder" : -7,
				"bVisible" : true
			},
			"degradation parameter" : {
				"iOrder" : -6,
				"bVisible" : true
			},
			"test type" : {
				"iOrder" : -9,
				"bVisible" : true,
				"inMatrix" : true
			},
			"compartment" : {
				"iOrder" : -5,
				"bVisible" : true
			}
		},
		"effects" : {

			"endpoint" : {
				"sTitle" : "Half-life",
				"inMatrix" : true,
				"iOrder" : -4
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EN_STABILITY_IN_SOIL_SECTION" : {
		"parameters" : {
			"bVisible" : false,

			"test type" : {
				"comment" : "copied to conditions",
				"iOrder" : -9,
				"inMatrix" : false,
				"bVisible" : false
			}
		},
		"conditions" : {
			"bVisible" : false,

			"test type" : {
				"iOrder" : -9,
				"inMatrix" : true,
				"bVisible" : true
			},
			"soil no." : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"soil type" : {
				"iOrder" : -7,
				"bVisible" : true
			},
			"oc content" : {
				"sTitle" : "OC content",
				"iOrder" : -6,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Half-life",
				"inMatrix" : true,
				"iOrder" : -5
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -4
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EN_BIOACCUMULATION_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"route" : {
				"comment" : "copied to conditions",
				"iOrder" : -5,
				"inMatrix" : false,
				"bVisible" : false
			},
			"species" : {
				"iOrder" : -3,
				"bVisible" : true,
				"inMatrix" : false
			}
		},
		"conditions" : {
			"bVisible" : false,
			"route" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			},
			"doses/concentrations" : {
				"sTitle" : "Conc. / Dose",
				"iOrder" : -4,
				"bVisible" : true
			},
			"bioacc. basis" : {
				"bVisible" : true,
				"iOrder" : -2
			}
		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Bioacc. type",
				"inMatrix" : true,
				"iOrder" : -1
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : 0
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EN_BIOACCU_TERR_SECTION" : {
		"parameters" : {
			"species" : {
				"iOrder" : -4,
				"bVisible" : true
			}
		},
		"conditions" : {
			"doses/concentrations" : {
				"iOrder" : -5,
				"sTitle" : "Conc./Dose",
				"bVisible" : false
			},
			"bioacc. basis" : {
				"iOrder" : -3
			}
		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Bioacc. type",
				"inMatrix" : true,
				"iOrder" : -2
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -1
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EN_ADSORPTION_SECTION" : {
		"conditions" : {
			"remark" : {
				"iOrder" : -1
			},
			"% org.carbon" : {
				"bVisible" : false
			},
			"temperature" : {
				"bVisible" : false
			}
		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Kp type",
				"inMatrix" : true,
				"iOrder" : -3
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -2
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EN_HENRY_LAW_SECTION" : {
		"conditions" : {
			"temperature" : {
				"iOrder" : -4
			},
			"pressure" : {
				"iOrder" : -3
			},
			"remark" : {
				"iOrder" : -2
			}
		},
		"effects" : {
			"endpoint" : {
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Value",
				"inMatrix" : true,
				"iOrder" : -5
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_FISHTOX_SECTION" : {

		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true

			},
			"effect" : {
				"iOrder" : -2,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_CHRONFISHTOX_SECTION" : {
		"parameters" : {
			"test medium" : {
				"iOrder" : -8
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism"
			}
		},
		"conditions" : {
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true
			},
			"effect" : {
				"iOrder" : -2
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc."
			},
			"based on" : {
				"iOrder" : -1
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_DAPHNIATOX_SECTION" : {
		"protocol" : {
			"citation" : {
				"bVisible" : true
			}
		},
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -2,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_CHRONDAPHNIATOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -2,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"iOrder" : -3,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_ALGAETOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true

			},
			"effect" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_BACTOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"iOrder" : -3,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_SOIL_MICRO_TOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_PLANTTOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -9,
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"test organism" : {
				"iOrder" : -8,
				"sTitle" : "Organism",
				"bVisible" : true
			},
			"exposure" : {
				"iOrder" : -6,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -3,
				"inMatrix" : true,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -7,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -2,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -5,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -4
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_SEDIMENTDWELLINGTOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"iOrder" : -3,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_SOILDWELLINGTOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,
			"test medium" : {
				"iOrder" : -8,
				"bVisible" : true
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism",
				"bVisible" : true
			}
		},
		"conditions" : {
			"bVisible" : false,
			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true,
				"bVisible" : true
			},
			"effect" : {
				"iOrder" : -2,
				"inMatrix" : true,
				"bVisible" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc.",
				"bVisible" : true
			},
			"based on" : {
				"iOrder" : -1,
				"bVisible" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"EC_HONEYBEESTOX_SECTION" : {
		"parameters" : {
			"bVisible" : false,

			"test medium" : {
				"iOrder" : -8
			},
			"test organism" : {
				"iOrder" : -7,
				"sTitle" : "Organism"
			}
		},
		"conditions" : {
			"bVisible" : false,

			"exposure" : {
				"iOrder" : -5,
				"inMatrix" : true
			},
			"effect" : {
				"iOrder" : -2,
				"inMatrix" : true
			},
			"measured concentration" : {
				"iOrder" : -6,
				"sTitle" : "Meas. Conc."
			},
			"based on" : {
				"iOrder" : -1
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -4,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "",
				"inMatrix" : true,
				"iOrder" : -3
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			}
		}
	},
	"AGGLOMERATION_AGGREGATION_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -22
			},
			"method details" : {
				"iOrder" : -11,
				"bVisible" : false
			},
			"sampling" : {
				"iOrder" : -9,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -8,
				"bVisible" : false
			}
		},
		"conditions" : {
			"percentile" : {
				"bVisible" : false
			},
			"remark" : {
				"sTitle" : "Remarks",
				"iOrder" : -15,
				"bVisible" : false
			},
			"medium" : {
				"sTitle" : "Medium",
				"iOrder" : -16,
				"inMatrix" : true
			},
			"ph" : {
				"iOrder" : -17
			},
			"seq_num" : {
				"iOrder" : -21,
				"sTitle" : "Seq. num."
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -20,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -19,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"iOrder" : -7,
				"bVisible" : false
			}
		}
	},
	"ASPECT_RATIO_SHAPE_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -22
			},
			"method details" : {
				"iOrder" : -11,
				"bVisible" : false
			},
			"sampling" : {
				"iOrder" : -9,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -8,
				"bVisible" : false
			}
		},
		"conditions" : {
			"shape_descriptive" : {
				"sTitle" : "Shape",
				"iOrder" : -19,
				"inMatrix" : true
			},
			"x" : {
				"iOrder" : -16,
				"inMatrix" : true
			},
			"y" : {
				"iOrder" : -15,
				"inMatrix" : true
			},
			"z" : {
				"iOrder" : -14,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -20,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -18,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			}
		}
	},
	"ZETA_POTENTIAL_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -22
			},
			"method details" : {
				"iOrder" : -10,
				"bVisible" : false
			},
			"sampling" : {
				"iOrder" : -8,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -7,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -21
			}
		},
		"conditions" : {
			"medium" : {
				"sTitle" : "Medium",
				"iOrder" : -15,
				"bVisible" : true,
				"inMatrix" : true
			},
			"n" : {
				"iOrder" : -17
			},
			"ph" : {
				"iOrder" : -16,
				"inMatrix" : true
			},
			"remark" : {
				"sTitle" : "Remarks",
				"iOrder" : -19
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -21,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -20,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -9,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"iOrder" : -10,
				"bVisible" : false
			}
		}
	},
	"ANALYTICAL_METHODS_SECTION" : {
		"parameters" : config_bao["parameters"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"],
		"effects" : {
			"endpoint" : {
				"iOrder" : -9,
				"bVisible" : true,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -8,
				"bVisible" : true,
				"inMatrix" : true
			},
			"text" : {
				"sTitle" : "Element / Func. group",
				"iOrder" : -7,
				"bVisible" : true,
				"inMatrix" : true
			}

		}
	},
	"SURFACE_CHEMISTRY_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -25
			},
			"method details" : {
				"iOrder" : -11,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -9,
				"bVisible" : false
			},
			"functionalization" : {
				"bVisible" : false
			},
			"coating" : {
				"bVisible" : false
			},
			"testmat_form" : {
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -8,
				"bVisible" : false
			}
		},
		"conditions" : {
			"type" : {
				"sTitle" : "",
				"iOrder" : -23
			},
			"description" : {
				"sTitle" : "Type",
				"iOrder" : -22,
				"inMatrix" : true,
				"bVisible" : true
			},
			"coating_description" : {
				"sTitle" : "Coating description",
				"iOrder" : -21,
				"bVisible" : false,
				"inMatrix" : true
			},
			"remark" : {
				"sTitle" : "Remarks",
				"iOrder" : -15,
				"bVisible" : false
			},
			"element_or_group" : {
				"sTitle" : "Element / Func. group",
				"iOrder" : -19,
				"bVisible" : false,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -20,
				"bVisible" : false
			},
			"result" : {
				"sTitle" : "Fraction",
				"iOrder" : -18,
				"inMatrix" : true
			},
			"text" : {
				"sTitle" : "Element / Func. group",
				"iOrder" : -19,
				"bVisible" : true,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"iOrder" : -11,
				"sTitle" : "Reference"
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			},
			"criteria" : {
				"sTitle" : "Coating / Functionalisation",
				"bVisible" : true,
				"iOrder" : -24
			}
		}
	},
	"PC_GRANULOMETRY_SECTION" : {
		"bVisible" : false,
		"parameters" : {
			"type of method" : {
				"iOrder" : -25
			},
			"distribution_type" : {
				"sTitle" : "Distribution type",
				"iOrder" : -23
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : false,
				"iOrder" : -24
			}
		},
		"conditions" : {
			"bVisible" : false,
			"seq_num" : {
				"sTitle" : "Passage num.",
				"iOrder" : -22,
				"bVisible" : false
			},
			"medium" : {
				"sTitle" : "Medium",
				"iOrder" : -14,
				"bVisible" : true,
				"inMatrix" : true
			},
			"remark" : {
				"sTitle" : "Remark",
				"iOrder" : -15,
				"bVisible" : false
			},
			"n" : {
				"iOrder" : -16
			},
			"phraseother_percentile" : {
				"bVisible" : false
			},
			"std_dev" : {
				"bVisible" : false
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -20,
				"bVisible" : true,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Value",
				"iOrder" : -18,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -9,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			},
			"uuid" : {
				"bVisible" : false
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"RADICAL_FORMATION_POTENTIAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},	
	"CRYSTALLITE_AND_GRAIN_SIZE_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			},
			"material_isotropic" : {
				"sTitle" : "Isotropic material",
				"iOrder" : -18
			}
		},
		"conditions" : {
			"medium" : {
				"sTitle" : "Medium",
				"iOrder" : -15,
				"inMatrix" : true
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"bVisible" : true,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Result",
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"DUSTINESS_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"bVisible" : false,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Dustiness index",
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"POROSITY_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"bVisible" : true,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"iOrder" : -11,
				"sTitle" : "Reference"
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"SPECIFIC_SURFACE_AREA_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {
			"remark" : {
				"sTitle" : "Remarks",
				"iOrder" : -14
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"bVisible" : false,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Specific Surface Area",
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"iOrder" : -11,
				"sTitle" : "Reference"
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},

	"POUR_DENSITY_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"bVisible" : false,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Pour density",
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"iOrder" : -11,
				"sTitle" : "Reference"
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"iOrder" : -14,
				"bVisible" : true
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"PHOTOCATALYTIC_ACTIVITY_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference"
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"iOrder" : -14,
				"bVisible" : true
			},
			"criteria" : {
				"iOrder" : -11,
				"bVisible" : false
			}
		}
	},
	"CATALYTIC_ACTIVITY_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {},
		"effects" : {
			"endpoint" : {
				"iOrder" : -17,
				"inMatrix" : true
			},
			"result" : {
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -10,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true,
				"sTitle" : "Reference",
				"iOrder" : -11
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"iOrder" : -14,
				"bVisible" : true
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
	"CRYSTALLINE_PHASE_SECTION" : {
		"parameters" : {
			"type of method" : {
				"iOrder" : -23,
				"sTitle" : "Method type"
			},
			"method details" : {
				"iOrder" : -22,
				"bVisible" : false
			},
			"sampling" : {
				"sTitle" : "Sampling",
				"iOrder" : -21,
				"bVisible" : false
			},
			"data_gathering_instruments" : {
				"sTitle" : "Instruments",
				"iOrder" : -20,
				"bVisible" : false
			},
			"testmat_form" : {
				"sTitle" : "Test Material Form",
				"bVisible" : true,
				"iOrder" : -19
			}
		},
		"conditions" : {
			"crystal system" : {
				"sTitle" : "Crystal system",
				"iOrder" : -18,
				"inMatrix" : true
			},
			"common name" : {
				"sTitle" : "Common name",
				"iOrder" : -17
			},
			"bravais lattice" : {
				"sTitle" : "Bravais lattice",
				"iOrder" : -16
			},
			"point group" : {
				"sTitle" : "Point group",
				"iOrder" : -15
			},
			"space group" : {
				"sTitle" : "Space group",
				"iOrder" : -14
			},
			"crystallographic planes" : {
				"sTitle" : "Crystallographic planes",
				"iOrder" : -13
			}
		},
		"effects" : {
			"endpoint" : {
				"iOrder" : -12,
				"bVisible" : false,
				"inMatrix" : true
			},
			"result" : {
				"bVisible" : false,
				"iOrder" : -11,
				"inMatrix" : true
			},
			"text" : {
				"sTitle" : "Result",
				"iOrder" : -8,
				"bVisible" : true,
				"inMatrix" : true
			}
		},
		"protocol" : {
			"guideline" : {
				"iOrder" : -5,
				"inMatrix" : true
			},
			"citation" : {
				"bVisible" : true
			}
		},
		"interpretation" : {
			"result" : {
				"sTitle" : "Conclusions",
				"iOrder" : -10,
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	},
		"ORE_SECTION" : {
			"parameters" : config_exposure["parameters"],
			"effects" : config_exposure["effects"],
			"conditions" : config_exposure["conditions"],
			"protocol" : config_exposure["protocol"],
			"interpretation" : config_exposure["interpretation"]
		},
		"ECR_SECTION" : {
			"parameters" : config_exposure["parameters"],
			"effects" : config_exposure["effects"],
			"conditions" : config_exposure["conditions"],
			"protocol" : config_exposure["protocol"],
			"interpretation" : config_exposure["interpretation"]
		},		
	"EXPOSURE_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},
	"EXPOSURE_MANUFACTURE_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},
	"EXPOSURE_FORMULATION_REPACKAGING_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},
	"EXPOSURE_INDUSTRIAL_SITES_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},
	"EXPOSURE_PROFESSIONAL_WORKERS_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},
	"EXPOSURE_CONSUMER_USE_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},
	"EXPOSURE_SERVICE_LIFE_SECTION" : {
		"parameters" : config_exposure["parameters"],
		"effects" : config_exposure["effects"],
		"conditions" : config_exposure["conditions"],
		"protocol" : config_exposure["protocol"],
		"interpretation" : config_exposure["interpretation"]
	},		
	
	"IMPURITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"OMICS_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},	
	"RISKASSESSMENT_SECTION" : {
		"protocol" : {
			"guideline" : {
				"sTitle" : "Protocol",
				"iOrder" : -20,
				"inMatrix" : true,
				"bVisible" : true,
				"mRender" : function(data, type, full) {
					var sOut = "";
					try {
						sOut += data[0];
					} catch (err) {
					}
					sOut += "<br/>";
					sOut += "<br/><ul>";
					$.each(full.parameters,
							function(k, v) {

								var title = k;
								try {
									title = config_bao.parameters[k
											.toLowerCase()]["sTitle"];

									if (title === undefined)
										title = k;
								} catch (err) {
								}
								if ((v === undefined) || (v == null)
										|| ("-" == v))
									return "";
								sOut += "<li>" + title + ": ";
								try {
									if (v.loValue == undefined)
										sOut += v;
									else
										sOut += v.loValue + " " + v.unit;
								} catch (err) {
									sOut += v;
								}
								"</li>";
							});
					sOut += "</ul>";
					return sOut;
				}
			},
			"uuid" : {
				"bVisible" : false
			},
			"owner" : {
				"bVisible" : false
			},
			"citation" : {
				"bVisible" : true,
				"iOrder" : 30,
				"sTitle" : "Reference",
				"mRender" : function(data, type, full) {

					var uri = (data["title"] != undefined)
							&& (data["title"].indexOf("http") == 0);
					if (uri) {
						var sOut = (data["year"] == null || data["year"] == 0) ? full["citation"]["owner"]
								: data["year"];
						return "<a href='" + data["title"] + "' title='"
								+ data["title"] + "' target='_doi' >" + sOut
								+ "</a>";
					} else {
						var sOut = (data["year"] == null || data["year"] == 0) ? ""
								: ("<br/>(" + data["year"] + ")");
						return (data["title"] + sOut);
					}
				}
			},
			"reliability" : {
				"bVisible" : false
			}
		},

		"parameters" : {
			"bVisible" : false
		},
		"conditions" : {
			"bVisible" : true

		},
		"effects" : {
			"endpoint" : {
				"sTitle" : "Risk assessment",
				"iOrder" : -6,
				"inMatrix" : true
			},
			"result" : {
				"sTitle" : "Risk value",
				"iOrder" : -5,
				"inMatrix" : true
			}
		},
		"interpretation" : {
			"result" : {
				"bVisible" : false
			},
			"criteria" : {
				"bVisible" : false
			}
		}
	}

}