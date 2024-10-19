var config_i5 = {
	"GI_GENERAL_INFORM_SECTION" : {
		"conditions" : {
			"remark" : {
				"iOrder" : -4
			}
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
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"PC_BOILING_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"PC_VAPOUR_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"PC_PARTITION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"SURFACE_TENSION_SECTION" : {
		"parameters" : config_bao["parameters"],
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
				"iOrder" : -7,
				"bVisible" : true,
				"sTitle" : "Result"
			}

		},
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"PC_WATER_SOL_SECTION" : {
		"parameters" : config_bao["parameters"],
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
				"iOrder" : -7,
				"bVisible" : true,
				"sTitle" : "Result"
			}

		},
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"PC_SOL_ORGANIC_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"PC_NON_SATURATED_PH_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"PC_DISSOCIATION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"PC_UNKNOWN_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_ACUTE_ORAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_ACUTE_OTHER_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},	
	"TO_ACUTE_DERMAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_ACUTE_INHAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_SKIN_IRRITATION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_EYE_IRRITATION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_SENSITIZATION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_REPEATED_ORAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_REPEATED_OTHER_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},	
	"TO_REPEATED_INHAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_REPEATED_DERMAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},

	"TO_GENETIC_IN_VITRO_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_GENETIC_IN_VIVO_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_CARCINOGENICITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_REPRODUCTION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_DEVELOPMENTAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"TO_PHOTOTRANS_AIR_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_HYDROLYSIS_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_BIODEG_WATER_SCREEN_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"TO_BIODEG_WATER_SIM_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EN_STABILITY_IN_SOIL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EN_BIOACCUMULATION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EN_BIOACCU_TERR_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EN_ADSORPTION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EN_HENRY_LAW_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EC_FISHTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_CHRONFISHTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_DAPHNIATOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_CHRONDAPHNIATOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_ALGAETOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_BACTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_SOIL_MICRO_TOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"EC_PLANTTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EC_SEDIMENTDWELLINGTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EC_SOILDWELLINGTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"EC_HONEYBEESTOX_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"AGGLOMERATION_AGGREGATION_SECTION" : {
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
	"ASPECT_RATIO_SHAPE_SECTION" : {
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
	"ZETA_POTENTIAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : {
			"n" : {
				"iOrder" : -17
			},
			"ph" : {
				"iOrder" : -16,
				"inMatrix" : true
			}
		},
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"SURFACE_CHEMISTRY_SECTION" : {
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
	"RADICAL_FORMATION_POTENTIAL_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
	"PC_GRANULOMETRY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : {
			"result" : {
				"inMatrix" : false,
				"bVisible" : true,
				"mRender" : function(data, type, full) {
					return "<span class='jtox-toolkit shortened ' title='"
							+ data + "'>" + data + "</span>";
				}
			},
			"criteria" : {
				"bVisible" : false,
				"mRender" : function(data, type, full) {
					return "<span class='jtox-toolkit shortened ' title='"
							+ data + "'>" + data + "</span>";
				}
			}
		},
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
	"CRYSTALLITE_AND_GRAIN_SIZE_SECTION" : {
		"parameters" : config_bao["parameters"],
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
		},
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},

	"CRYSTALLINE_PHASE_SECTION" : {
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
				"sTitle" : "Result",
				"iOrder" : -8,
				"bVisible" : true,
				"inMatrix" : true
			},
			"text" : {
				"sTitle" : "Result",
				"iOrder" : -8,
				"bVisible" : true,
				"inMatrix" : true
			}
		}
	},
	"DUSTINESS_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
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
				"iOrder" : -7,
				"bVisible" : true,
				"sTitle" : "Result"
			}
		}
	},
	"POROSITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"SPECIFIC_SURFACE_AREA_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"PC_DENSITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},

	"POUR_DENSITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"PHOTOCATALYTIC_ACTIVITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

	},
	"CATALYTIC_ACTIVITY_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]

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
	"IMPURITY_SECTION" : {
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
	"OMICS_SECTION" : {
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
			"text" : {
				"iOrder" : -8,
				"bVisible" : true,
				"inMatrix" : true,
				"sTitle" : "Identifier"
			}

		}
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
	"PROCESS_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	},
		"TO_ACUTE_PULMONARY_INSTILLATION_SECTION" : {
		"parameters" : config_bao["parameters"],
		"effects" : config_bao["effects"],
		"conditions" : config_bao["conditions"],
		"protocol" : config_bao["protocol"],
		"interpretation" : config_bao["interpretation"]
	}
}