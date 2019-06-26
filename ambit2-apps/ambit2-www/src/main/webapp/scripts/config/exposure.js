var config_exposure = {
	"render_param" : function(key, value, filter, row_start,row_end ,key_start,key_end,value_start,value_end) {

		var title = key;
		try {
			title = config_exposure.parameters[key.toLowerCase()]["sTitle"];

			if (title === undefined)
				title = key;
		} catch (err) {
		}
		if ((value === undefined) || (value == null) || ("-" == value))
			return "";

		sOut = row_start;
		if (key.indexOf(filter) == 0) {
			sOut += key_start + title + key_end;
		} else
			return "";

		sOut += value_start;
		try {
			if (value.loValue == undefined) {
				var uri = value.indexOf("http") >= 0;

				if (uri) {
					sOut += "<a href='" + value + "' target='_doi' >link</a>";
				} else
					sOut += value;
			} else {
				sOut += value.loValue;
				if (value.unit != undefined)
					sOut += " " + value.unit;
			}
		} catch (err) {
			sOut += value;

		}
		sOut += value_end;
		sOut += row_end;

		return sOut;
	},

	"protocol" : {
		"guideline" : {
			"sTitle" : "Contributing Exposure Scenario",
			"iOrder" : -20,
			"inMatrix" : true,
			"bVisible" : true,
			"mRender" : function(data, type, full) {
				//var wrapper_start="<p><table>";
				//var wrapper_end="</table></p>";
				//var row_start = "<tr>";
				//var row_end = "</tr>";
				//var key_start = "<th>";
				//var key_end = "</th>";
				//var value_start = "<td>";
				//var value_end = "</td>";				
				var wrapper_start="<p>";
				var wrapper_end="</p>";
				var row_start = "<br>";
				var row_end = "";
				var key_start = "<span style='font-weight:bold;'>";
				var key_end = "</span>";
				var value_start = " : ";
				var value_end = "";
				
				var sOut = "";

				sOut += "<div class='tabs_params'>";
				sOut += "<ul> <li><a href='#tabs_usedescriptors'>ECHA Use descriptors</a></li>";
				sOut += "<li><a href='#tabs_scenario'>Contributing Exposure Scenario/Activity</a></li>";
				sOut += "<li><a href='#tabs_controlmeasures'>Exposure control measures</a></li>";
				sOut += "<li><a href='#tabs_premises'>Premises</a></li>";
				sOut += "</ul>";

				sOut += "<div id='tabs_usedescriptors'>";
				sOut += wrapper_start;
				$.each(full.parameters, function(k, v) {
					sOut += config_exposure.render_param(k, v, "ECHA.",row_start,row_end,key_start,key_end,value_start,value_end)
				});
				sOut += wrapper_end;
				sOut += "</div>";

				sOut += "<div id='tabs_scenario'>";
				sOut += wrapper_start;
				$.each(full.parameters, function(k, v) {
					sOut += config_exposure.render_param(k, v,
							"EXPOSURE_CONTRIBUTING_SCENARIO.",row_start,row_end,key_start,key_end,value_start,value_end)
				});
				sOut += wrapper_end;
				sOut += "</div>";

				sOut += "<div id='tabs_controlmeasures'>";
				sOut += wrapper_start;
				$.each(full.parameters, function(k, v) {
					sOut += config_exposure.render_param(k, v,
							"EXPOSURE_CONTROL_MEASURES.",row_start,row_end,key_start,key_end,value_start,value_end)
				});
				sOut += wrapper_end;
				sOut += "</div>";

				sOut += "<div id='tabs_premises'>";
				sOut += wrapper_start;
				$.each(full.parameters, function(k, v) {
					sOut += config_exposure.render_param(k, v, "PREMISES.",row_start,row_end,key_start,key_end,value_start,value_end)
				});
				sOut += wrapper_end;
				sOut += "</div>";

				sOut += "</div>";

				jQuery(".tabs_params").tabs();
				return sOut;
			}
		},

		"citation" : {
			"bVisible" : true,
			"iOrder" : -1,
			"sTitle" : "Reference",

			"mRender" : function(data, type, full) {
				var sOut = data["title"] + " (" + data["year"] + ")";
				sOut += "<br>";
				sOut += data["owner"];

				iuuid = full["investigation_uuid"];
				auuid = full["assay_uuid"];

				if (auuid === undefined || (auuid == null))
					;
				else {
					sOut += "<br/><a href='../../investigation?type=byassay&search="
							+ auuid
							+ "' class='chelp' title='This experiment in table form'>This experiment</a>"
				}
				if (iuuid === undefined || (iuuid == null))
					;
				else {
					sOut += "<br/><a href='../../investigation?type=byinvestigation&search="
							+ iuuid
							+ "' class='chelp' title='Related experiments in table form'>Related experiments</a>"
				}
				try {
					if (full["reliability"]["r_value"] != undefined)
						sOut += "<br/><span class='chelp' style='color:#FF0000;' title='curation comment'>"
								+ full["reliability"]["r_value"] + "</span>"

				} catch (err) {
				}
				return sOut;
			}

		},
		"owner" : {
			"bVisible" : false
		},
		"uuid" : {
			"bVisible" : false
		},
		"reliability" : {
			"bVisible" : false
		}
	},

	"parameters" : {
		"bVisible" : false,
		"echa.sector_uses" : {
			"sTitle" : "Sector uses",
			"bVisible" : false
		},
		"echa.product_categories" : {
			"sTitle" : "Product categories",
			"bVisible" : false
		},
		"echa.process_category_proc" : {
			"sTitle" : "Process categories",
			"bVisible" : false
		},
		"echa.environmental_release_category_erc" : {
			"sTitle" : "Environmental release category",
			"bVisible" : false
		},
		"echa.article_category_ac" : {
			"sTitle" : "Article category",
			"bVisible" : false
		},
		"echa.subproducts" : {
			"sTitle" : "Subproducts",
			"bVisible" : false
		},
		"echa.technical_functions" : {
			"sTitle" : "Technical functions",
			"bVisible" : false
		},

		"exposure_contributing_scenario.name" : {
			"sTitle" : "Contributing exposure scenario name",
			"bVisible" : false
		},
		"exposure_contributing_scenario.source_domain" : {
			"sTitle" : "Source domain",
			"bVisible" : false
		},
		"exposure_contributing_scenario.activity_technique_name" : {
			"sTitle" : "Activity name",
			"bVisible" : false
		},
		"exposure_contributing_scenario.activity_technique_description" : {
			"sTitle" : "Activity description",
			"bVisible" : false
		},
		"exposure_contributing_scenario.energy_involved" : {
			"sTitle" : "Energy involved",
			"bVisible" : false
		},
		"exposure_contributing_scenario.duration_of_activity" : {
			"sTitle" : "Duration of activity",
			"bVisible" : false
		},
		"exposure_contributing_scenario.amounts_used" : {
			"sTitle" : "Amounts material used",
			"bVisible" : false
		},
		"exposure_contributing_scenario.emission_rate_airborne_nm" : {
			"sTitle" : "Emission rate airborne material",
			"bVisible" : false
		},

		"exposure_control_measures.level_automatization" : {
			"sTitle" : "Level automatization",
			"bVisible" : false
		},
		"exposure_control_measures.containment" : {
			"sTitle" : "Containment",
			"bVisible" : false
		},
		"exposure_control_measures.local_exhaust_ventilation" : {
			"sTitle" : "Local Exhaust Ventilation (LEV)",
			"bVisible" : false
		},
		"exposure_control_measures.general_ventilation" : {
			"sTitle" : "General ventilation",
			"bVisible" : false
		},
		"exposure_control_measures.air_exchanges" : {
			"sTitle" : "Air exchanges",
			"bVisible" : false
		},
		"exposure_control_measures.rpe" : {
			"sTitle" : "RPE",
			"bVisible" : false
		},
		"exposure_control_measures.dermal_protection" : {
			"sTitle" : "Dermal protection",
			"bVisible" : false
		},
		"exposure_control_measures.eye_protection" : {
			"sTitle" : "Eye protection",
			"bVisible" : false
		},
		"exposure_factors.exposure_duration" : {
			"sTitle" : "Exposure duration",
			"bVisible" : false
		},
		"exposure_factors.exposure_frequency" : {
			"sTitle" : "Exposure frequency",
			"bVisible" : false
		},
		"exposure_factors.room_size" : {
			"sTitle" : "Room size",
			"bVisible" : false
		},
		"premises.production_scale" : {
			"sTitle" : "Production scale",
			"bVisible" : false
		},
		"premises.number_employees_working_with_nm" : {
			"sTitle" : "Number employees working with nm",
			"bVisible" : false
		},
		"premises.country" : {
			"sTitle" : "Country",
			"bVisible" : false
		},
		"premises.remarks" : {
			"sTitle" : "Remarks",
			"bVisible" : false
		},
		"e.method" : {
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
			"iOrder" : -8,
			"bVisible" : true,
			"inMatrix" : true
		},
		"text" : {
			"sTitle" : "Result",
			"inMatrix" : true,
			"bVisible" : true,
			"iOrder" : -7
		}

	},
	"conditions" : {
		"bVisible" : false,
		"sampling specification" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true,
			"sTitle" : "Sampling specification"
		},
		"time start" : {
			"iOrder" : -6,
			"bVisible" : true,
			"inMatrix" : true,
			"sTitle" : "Time start"
		},
		"time stop" : {
			"iOrder" : -5,
			"bVisible" : true,
			"inMatrix" : true,
			"sTitle" : "Time stop"
		}
	},
	"interpretation" : {
		"result" : {
			"inMatrix" : false,
			"bVisible" : false,
			"mRender" : function(data, type, full) {
				return "<span class='jtox-toolkit shortened ' title='" + data
						+ "'>" + data + "</span>";
			}
		},
		"criteria" : {
			"bVisible" : false,
			"mRender" : function(data, type, full) {
				return "<span class='jtox-toolkit shortened ' title='" + data
						+ "'>" + data + "</span>";
			}
		}
	}
}
