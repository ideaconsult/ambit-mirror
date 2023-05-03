var config_study = {

	"columns" : {
		"_" : {
			"main" : {
				"name" : {
					"bVisible" : false
				}
			},
			"parameters" : {},
			"conditions" : {},
			"effects" : {
				"text" : {
					"bVisible" : false
				}
			},

			"protocol" : {
				"citation" : {
					"bVisible" : false,
					"iOrder" : -1,
					"sTitle" : "Study year",
					"oldmRender" : function(data, type, full) {
						return "<span title='"
								+ (data["title"] == null ? "N/A"
										: data["title"]) + "'>"
								+ (data["year"] == null ? "-" : data["year"])
								+ "</span>";
					},
					"mRender" : function(data, type, full) {

						// var quri = "?type=citation&search=";
						var uri = (data["title"] != undefined)
								&& (data["title"].indexOf("http") == 0);
						if (uri) {
							var sOut = (data["year"] == null || data["year"] == 0) ? "URL"
									: data["year"];
							return "<a href='" + data["title"] + "' title='"
									+ data["title"] + "' target='_doi' >"
									+ sOut + "</a>";
						} else {
							return "<span title='"
									+ (data["title"] == null ? "N/A"
											: data["title"])
									+ "'>"
									+ (data["year"] == null ? "-"
											: data["year"]) + "</span>";
						}
					}
				},

				"uuid" : {
					"bVisible" : true
				},
				"reliability" : {
					"bVisible" : true
				}
			},
			"interpretation" : {}
		},
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
		"PC_DENSITY_SECTION" : config_i5["PC_DENSITY_SECTION"],
		"PC_MELTING_SECTION" : config_i5["PC_MELTING_SECTION"],
		"PC_BOILING_SECTION" : config_i5["PC_BOILING_SECTION"],
		"PC_VAPOUR_SECTION" : config_i5["PC_VAPOUR_SECTION"],
		"PC_PARTITION_SECTION" : config_i5["PC_PARTITION_SECTION"],
		"SURFACE_TENSION_SECTION" : config_i5["SURFACE_TENSION_SECTION"],
		"PC_WATER_SOL_SECTION" : config_i5["PC_WATER_SOL_SECTION"],
		"PC_SOL_ORGANIC_SECTION" : config_i5["PC_SOL_ORGANIC_SECTION"],
		"PC_NON_SATURATED_PH_SECTION" : config_i5["PC_NON_SATURATED_PH_SECTION"],
		"PC_DISSOCIATION_SECTION" : config_i5["PC_DISSOCIATION_SECTION"],
		"TO_ACUTE_ORAL_SECTION" : config_i5["TO_ACUTE_ORAL_SECTION"],
		"TO_ACUTE_DERMAL_SECTION" : config_i5["TO_ACUTE_DERMAL_SECTION"],
		"TO_ACUTE_INHAL_SECTION" : config_i5["TO_ACUTE_INHAL_SECTION"],
		"TO_ACUTE_OTHER_SECTION" : config_i5["TO_ACUTE_OTHER_SECTION"],
		"TO_SKIN_IRRITATION_SECTION" : config_i5["TO_SKIN_IRRITATION_SECTION"],

		"TO_EYE_IRRITATION_SECTION" : config_i5["TO_EYE_IRRITATION_SECTION"],

		"TO_SENSITIZATION_SECTION" : config_i5["TO_SENSITIZATION_SECTION"],

		"TO_REPEATED_ORAL_SECTION" : config_i5["TO_REPEATED_ORAL_SECTION"],
		"TO_REPEATED_OTHER_SECTION" : config_i5["TO_REPEATED_OTHER_SECTION"],

		"TO_REPEATED_INHAL_SECTION" : config_i5["TO_REPEATED_INHAL_SECTION"],

		"TO_REPEATED_DERMAL_SECTION" : config_i5["TO_REPEATED_DERMAL_SECTION"],

		"TO_GENETIC_IN_VITRO_SECTION" : config_i5["TO_GENETIC_IN_VITRO_SECTION"],

		"TO_GENETIC_IN_VIVO_SECTION" : config_i5["TO_GENETIC_IN_VIVO_SECTION"],

		"TO_CARCINOGENICITY_SECTION" : config_i5["TO_CARCINOGENICITY_SECTION"],

		"TO_REPRODUCTION_SECTION" : config_i5["TO_REPRODUCTION_SECTION"],

		"TO_DEVELOPMENTAL_SECTION" : config_i5["TO_DEVELOPMENTAL_SECTION"],
		"PC_THERMAL_STABILITY_SECTION" : config_i5["PC_THERMAL_STABILITY_SECTION"],
		"TO_SENSITIZATION_INVITRO_SECTION" : {
			"parameters" : config_ce_invitro["parameters"],
			"effects" : config_ce_invitro["effects"],
			"conditions" : config_ce_invitro["conditions"],
			"protocol" : config_ce_invitro["protocol"],
			"interpretation" : config_ce_invitro["interpretation"]

		},
		"TO_SENSITIZATION_INCHEMICO_SECTION" : {
			"parameters" : config_ce_inchemico["parameters"],
			"effects" : config_ce_inchemico["effects"],
			"conditions" : config_ce_inchemico["conditions"],
			"protocol" : config_ce_inchemico["protocol"],
			"interpretation" : config_ce_inchemico["interpretation"]
		},
		"TO_SENSITIZATION_LLNA_SECTION" : {
			"parameters" : config_ce_llna["parameters"],
			"effects" : config_ce_llna["effects"],
			"conditions" : config_ce_llna["conditions"],
			"protocol" : config_ce_llna["protocol"],
			"interpretation" : config_ce_llna["interpretation"]
		},
		"TO_SENSITIZATION_HUMANDB_SECTION" : {
			"parameters" : config_ce_humandb["parameters"],
			"effects" : config_ce_humandb["effects"],
			"conditions" : config_ce_humandb["conditions"],
			"protocol" : config_ce_humandb["protocol"],
			"interpretation" : config_ce_humandb["interpretation"]

		},
		"TO_SENSITIZATION_INSILICO_SECTION" : {
			"parameters" : config_ce_insilico["parameters"],
			"effects" : config_ce_insilico["effects"],
			"conditions" : config_ce_insilico["conditions"],
			"protocol" : config_ce_insilico["protocol"],
			"interpretation" : config_ce_insilico["interpretation"]

		},
		"NPO_1709_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"NPO_1911_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"ENM_8000223_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"ENM_9000011_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"ENM_9000013_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"ENM_0000044_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"NPO_1773_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"NPO_1339_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]
		},
		"BAO_0002189_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"BAO_0002167_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"ENM_0000081_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"ENM_0000037_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"BAO_0002189" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"BAO_0002993_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"BAO_0002733_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"OBI_0302736_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CHMO_0000287_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CHMO_0000234_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CHMO_0000239_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CHMO_0000538_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CSEO_00001191_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"IMPURITY_SECTION" : {
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
					"sTitle" : "Element / Func. group",
					"iOrder" : -7,
					"bVisible" : true,
					"inMatrix" : true
				}

			},
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"ANALYTICAL_METHODS_SECTION" : {
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
					"sTitle" : "Element / Func. group",
					"iOrder" : -7,
					"bVisible" : true,
					"inMatrix" : true
				}

			},
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"TO_PHOTOTRANS_AIR_SECTION" : config_i5["TO_PHOTOTRANS_AIR_SECTION"],

		"TO_HYDROLYSIS_SECTION" : config_i5["TO_HYDROLYSIS_SECTION"],

		"TO_BIODEG_WATER_SCREEN_SECTION" : config_i5["TO_BIODEG_WATER_SCREEN_SECTION"],

		"TO_BIODEG_WATER_SIM_SECTION" : config_i5["TO_BIODEG_WATER_SIM_SECTION"],

		"EN_STABILITY_IN_SOIL_SECTION" : config_i5["EN_STABILITY_IN_SOIL_SECTION"],

		"EN_BIOACCUMULATION_SECTION" : config_i5["EN_BIOACCUMULATION_SECTION"],

		"EN_BIOACCU_TERR_SECTION" : config_i5["EN_BIOACCU_TERR_SECTION"],
		"EN_ADSORPTION_SECTION" : config_i5["EN_ADSORPTION_SECTION"],

		"EN_HENRY_LAW_SECTION" : config_i5["EN_HENRY_LAW_SECTION"],
		"EC_FISHTOX_SECTION" : config_i5["EC_FISHTOX_SECTION"],

		"EC_CHRONFISHTOX_SECTION" : config_i5["EC_CHRONFISHTOX_SECTION"],

		"EC_DAPHNIATOX_SECTION" : config_i5["EC_DAPHNIATOX_SECTION"],

		"EC_CHRONDAPHNIATOX_SECTION" : config_i5["EC_CHRONDAPHNIATOX_SECTION"],

		"EC_ALGAETOX_SECTION" : config_i5["EC_ALGAETOX_SECTION"],

		"EC_BACTOX_SECTION" : config_i5["EC_BACTOX_SECTION"],

		"EC_SOIL_MICRO_TOX_SECTION" : config_i5["EC_SOIL_MICRO_TOX_SECTION"],
		"EC_PLANTTOX_SECTION" : config_i5["EC_PLANTTOX_SECTION"],

		"EC_SEDIMENTDWELLINGTOX_SECTION" : config_i5["EC_SEDIMENTDWELLINGTOX_SECTION"],
		"EC_SOILDWELLINGTOX_SECTION" : config_i5["EC_SOILDWELLINGTOX_SECTION"],
		"EC_HONEYBEESTOX_SECTION" : config_i5["EC_HONEYBEESTOX_SECTION"],

		"AGGLOMERATION_AGGREGATION_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]

		},
		"ASPECT_RATIO_SHAPE_SECTION" : config_i5["ASPECT_RATIO_SHAPE_SECTION"],
		"ZETA_POTENTIAL_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"I5ZETA_POTENTIAL_SECTION" : config_i5["ZETA_POTENTIAL_SECTION"],

		"GENERIC_SURFACE_CHEMISTRY_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
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
		"SURFACE_CHEMISTRY_SECTION" : config_i5["SURFACE_CHEMISTRY_SECTION"],
		"RADICAL_FORMATION_POTENTIAL_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"PC_GRANULOMETRY_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CRYSTALLITE_AND_GRAIN_SIZE_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"DUSTINESS_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
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
		"CRYSTALLINE_PHASE_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : {
				"endpoint" : {
					"bVisible" : true,
					"inMatrix" : true,
					"iOrder" : -9
				},
				"result" : {
					"bVisible" : true,
					"inMatrix" : true,
					"iOrder" : -7
				},
				"text" : {
					"sTitle" : "Result",
					"bVisible" : true,
					"inMatrix" : true,
					"iOrder" : -8
				}
			},
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"OMICS_SECTION" : {
			"parameters" : config_bao["parameters"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : {
				"result" : {
					"bVisible" : false
				},
				"criteria" : {
					"bVisible" : false
				}
			},
			"effects" : {
				"endpoint" : {
					"iOrder" : -9,
					"bVisible" : true,
					"inMatrix" : true
				},
				"result" : {
					"bVisible" : false
				},
				"text" : {
					"iOrder" : -8,
					"bVisible" : true,
					"inMatrix" : true,
					"sTitle" : "Identifier",
					"mRender" : function(data, type, full) {
						try {
							console.log(data);
							var id = data[0].result.textValue;
							var out= "<a target='_external' href='" + full.interpretation.criteria + "'>"+id+"</a>";
							return out;
						} catch(e) {
							return data;
						}
					}		
							

				}

			}			
		},
		"METABOLOMICS_SECTION" : {
			"parameters" : config_bao["parameters"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"],
			"effects" : {
				"endpoint" : {
					"iOrder" : -9,
					"bVisible" : true,
					"inMatrix" : true,
					"sTitle" : "Result"
				},
				"text" : {
					"iOrder" : -8,
					"bVisible" : true,
					"inMatrix" : true,
					"sTitle" : ""
				},
				"result" : {
					"iOrder" : -8,
					"bVisible" : false,
					"inMatrix" : false
				}

			},
		},
		"TRANSCRIPTOMICS_SECTION" : {
			"parameters" : config_bao["parameters"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"],
			"effects" : {
				"endpoint" : {
					"iOrder" : -9,
					"bVisible" : true,
					"inMatrix" : true,
					"sTitle" : "Result"
				},
				"text" : {
					"iOrder" : -8,
					"bVisible" : true,
					"inMatrix" : true,
					"sTitle" : ""
				},
				"result" : {
					"iOrder" : -8,
					"bVisible" : false,
					"inMatrix" : false
				}

			},
		},
		"PROTEOMICS_SECTION" : {
			"parameters" : {
				"bVisible" : false,
				"type of method" : {
					"iOrder" : -23,
					"bVisible" : true,
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

					"bVisible" : true
				},
				"testmat_form" : {
					"sTitle" : "Test Material Form",
					"bVisible" : false,
					"iOrder" : -19
				}
			},
			"conditions" : {
				"bVisible" : true
			},
			"effects" : {
				"endpoint" : {
					"iOrder" : -13,
					"bVisible" : true,
					"inMatrix" : true
				},
				"text" : {
					"bVisible" : false,
					"iOrder" : -12,
					"inMatrix" : true
				},
				"result" : {
					"bVisible" : false,
					"iOrder" : -11
				}
			},
			"protocol" : {
				"bVisible" : false,
				"guideline" : {
					"iOrder" : -5,
					"inMatrix" : true
				},
				"citation" : {
					"bVisible" : false

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
		"PC_UNKNOWN_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"BIO_NANO_INTERACTION_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},		
		"RISKASSESSMENT_SECTION" : config_i5["RISKASSESSMENT_SECTION"],

		"UNKNOWN_TOXICITY_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"interpretation" : config_bao["interpretation"],
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
				"citation" : {
					"bVisible" : true,
					"iOrder" : -30,
					"sTitle" : "Reference",
					"mRender" : function(data, type, full) {
						var sOut = (data["year"] == null || data["year"] == 0) ? "DOI"
								: data["year"];
						return "<a href='" + data["title"] + "' title='"
								+ data["title"] + "' target='_doi' >" + sOut
								+ "</a>";
					}
				}
			}
		},
		"UNKNOWN_TOXICITY_SECTION_SUMMARY" : {
			"effects" : {
				"text" : {
					"bVisible" : true,
					"inMatrix" : true
				}
			}
		},
		"PUBCHEM_SUMMARY_SECTION" : {
			"parameters" : {
				"target gene" : {
					"bVisible" : false,
					"iOrder" : -14
				}
			},
			"effects" : {
				"endpoint" : {
					"iOrder" : -13,
					"bVisible" : true,
					"inMatrix" : true
				},
				"text" : {
					"bVisible" : true,
					"inMatrix" : true,
					"iOrder" : -11
				},
				"result" : {
					"bVisible" : true,
					"inMatrix" : true,
					"iOrder" : -12
				}
			},
			"conditions" : {
				"replicate" : {
					"bVisible" : true
				},
				"doses/concentrations" : {
					"bVisible" : true,
					"sTitle" : "Concentration",
					"inMatrix" : true
				},
				"emission wavelength" : {
					"bVisible" : true,
					"iOrder" : -5
				},
				"target gene" : {
					"bVisible" : false,
					"iOrder" : -6
				}
			},
			"protocol" : {
				"citation" : {
					"bVisible" : true,
					"sTitle" : "Reference",
					"iOrder" : -15,
					"mRender" : function(data, type, full) {
						var sOut = (data["year"] == null || data["year"] == 0) ? data["title"]
								: data["year"];
						return "PubChem Assay: <a href='" + data["title"]
								+ "' title='" + data["title"]
								+ "' target='_doi' >" + sOut + "</a>";
					}
				}
			},
			"interpretation" : {
				"result" : {
					"sTitle" : "PubChem Activity Outcome",
					"iOrder" : -4,
					"bVisible" : true
				},
				"criteria" : {
					"sTitle" : "Target",
					"iOrder" : -3,
					"bVisible" : true
				}
			}

		},
		"PUBCHEM_DOSERESPONSE_SECTION" : {
			"effects" : {
				"endpoint" : {
					"iOrder" : -7
				},
				"result" : {
					"sTitle" : "Response",
					"iOrder" : -5
				},
				"text" : {
					"bVisible" : false
				}
			},
			"conditions" : {
				"replicate" : {
					"bVisible" : true,
					"iOrder" : -8
				},
				"doses/concentrations" : {
					"bVisible" : true,
					"sTitle" : "Concentration",
					"iOrder" : -6
				},
				"emission wavelength" : {
					"bVisible" : true,
					"iOrder" : -9
				}

			},
			"protocol" : {
				"citation" : {
					"bVisible" : true,
					"sTitle" : "Reference",
					"mRender" : function(data, type, full) {
						var sOut = (data["year"] == null || data["year"] == 0) ? "DOI"
								: data["year"];
						return "<a href='" + data["title"] + "' title='"
								+ data["title"] + "' target='_doi' >" + sOut
								+ "</a>";
					},
					"iOrder" : -10
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
		},
		"PUBCHEM_CONFIRMATORY_SECTION" : {
			"effects" : {
				"text" : {
					"bVisible" : true
				}
			},
			"conditions" : {
				"replicate" : {
					"bVisible" : true
				},
				"doses/concentrations" : {
					"bVisible" : true,
					"sTitle" : "Concentration"
				},
				"emission wavelength" : {
					"bVisible" : true
				}

			},
			"protocol" : {
				"citation" : {
					"bVisible" : true,
					"sTitle" : "Reference",
					"mRender" : function(data, type, full) {
						return data["title"];
					}
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
		},
		"CELL_VIABILITY_ASSAY_SECTION" : {
			"effects" : {
				"text" : {
					"bVisible" : true
				}
			}
		},
		"PROTEIN_SMALLMOLECULE_INTERACTION_SECTION" : {
			"effects" : {
				"text" : {
					"bVisible" : true
				}
			}
		},
		"TRANSCRIPTION_PROFILING" : {
			"effects" : {
				"text" : {
					"bVisible" : true,
					"sTitle" : "File"
				}
			}
		},

		"BAO_0003009_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"ENM_0000068_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		
		"NPO_296_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"BAO_0002084_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]

		},
		"BAO_0000451_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"MMO_0000368_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CELL_DEATH" : {

			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]

		},
		"OXIDATIVE_PHOSPHORYLATION" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]

		},
		"CELL_MORPHOLOGY" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"CELL_CYCLE" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},

		"CELL_PROLIFERATION" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"PROTEIN_STABILIZATION" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"RECEPTOR_BINDING" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]

		},
		"REGULATION_OF_GENE_EXPRESSION" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"REGULATION_OF_TRANSCRIPTION_FACTOR_ACTIVITY" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"REGULATION_OF_CATALYTIC_ACTIVITY" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"MITOCHONDRIAL_DEPOLARIZATION" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]

		},
		"AUTOFLUORESCENCE" : {
			"parameters" : config_toxcast["parameters"],
			"effects" : config_toxcast["effects"],
			"conditions" : config_toxcast["conditions"],
			"protocol" : config_toxcast["protocol"],
			"interpretation" : config_toxcast["interpretation"]
		},
		"MESOCOSM_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},
		"CHEMINF_000513" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
		},		
		"EXPOSURE_SECTION" : {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]
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
		}		
		
	}
}
