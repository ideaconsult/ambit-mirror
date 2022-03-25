var config_bao = {
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
				$
						.each(
								full.parameters,
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

									sOut += "<li>";
									if (k.indexOf("E.") == 0) {
										sOut += ("<span style='font-weight:bold;'>"
												+ title + "</span>");
									} else
										sOut += title;

									sOut += ": ";
									try {
										if (v.loValue == undefined) {
											var uri = v.indexOf("http") >= 0;

											if (uri) {
												sOut += "<a href='"
														+ v
														+ "' target='_doi' >link</a>";
											} else
												sOut += v;
										} else {
											sOut += v.loValue;
											if (v.unit != undefined)
												sOut += " " + v.unit;
										}
									} catch (err) {
										sOut += v;

									}
									sOut += "</li>";
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

				// var quri = "?type=citation&search=";
				var uri = (data["title"] != undefined)
						&& (data["title"].indexOf("http") == 0);
				if (uri) {
					var sOut = (data["year"] == null || data["year"] == 0) ? "URL"
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
		}
	},

	"parameters" : {
		"bVisible" : false,

		"data_gathering_instruments" : {
			"sTitle" : "Instrument",
			"bVisible" : false
		},
		"distribution_type" : {
			"sTitle" : "Distribution type",
			"bVisible" : true
		},
		"npo_1961" : {
			"sTitle" : "Aids used to disperse/sonification",
			"bVisible" : false
		},
		"chmo:0002774" : {
			"sTitle" : "Aids used to disperse/stirring",
			"bVisible" : false
		},
		"npo_1952" : {
			"sTitle" : "Aids used to disperse/vortexing",
			"bVisible" : false
		},
		"obi_0001911 bao_0000114" : {
			"sTitle" : "Cell culture conditions - medium",
			"iOrder" : -17,
			"bVisible" : false
		},
		"cell culture conditions - serum" : {
			"iOrder" : -18,
			"bVisible" : false
		},
		"serum concentration" : {
			"bVisible" : false
		},

		"cell culture conditions - serum concentration in growth medium" : {
			"bVisible" : false
		},
		"cell culture conditions - serum concentration in treatment medium" : {
			"bVisible" : false
		},
		"cell line/type - full name" : {
			"bVisible" : false
		},
		"clo_0000031" : {
			"iOrder" : -19,
			"bVisible" : false,
			"sTitle" : "Cell line"
		},
		"clo_0000031 efo_0004443" : {
			"sTitle" : "Cell line/type - supplier",
			"bVisible" : false
		},
		"dispersion agent" : {
			"iOrder" : -21,
			"bVisible" : false
		},
		"npo_1969 obi_0000272" : {
			"sTitle" : "Dispersion protocol",
			"iOrder" : -22,
			"bVisible" : false
		},
		"binomial" : {
			"bVisible" : false
		},
		"exposure duration" : {
			"iOrder" : -22,
			"bVisible" : false
		},
		"temperature" : {
			"iOrder" : -22,
			"bVisible" : false
		},
		"species" : {
			"bVisible" : false
		},
		"distribution_type" : {
			"bVisible" : false
		},
		"testmat_form" : {
			"bVisible" : false
		},
		"method details" : {
			"bVisible" : false
		},
		"type of method" : {
			"bVisible" : false
		},

		"sampling" : {
			"bVisible" : false,
			"sTitle" : "Sampling"
		},
		"cell line" : {
			"bVisible" : false
		},
		"method type" : {
			"bVisible" : false
		},
		"cell pass" : {
			"bVisible" : false
		},
		"cell type ref" : {
			"bVisible" : false
		},
		"mean absorbance" : {
			"bVisible" : false
		},
		"operator" : {
			"bVisible" : false
		},
		"preincubation time" : {
			"bVisible" : false
		},
		"reference(+ve)" : {
			"bVisible" : false
		},
		"reference(-ve)" : {
			"bVisible" : false
		},
		"seeding" : {
			"bVisible" : false
		},
		"e.method" : {
			"bVisible" : false,
			"inMatrix" : true,
			"sTitle" : "Method"
		},
		"e.sop_reference" : {
			"bVisible" : false,
			"inMatrix" : true,
			"sTitle" : "SOP reference"
		},
		"e.cell_type" : {
			"bVisible" : false,
			"inMatrix" : true,
			"sTitle" : "Cell type"
		},
		"e.organ" : {
			"bVisible" : false,
			"inMatrix" : true,
			"sTitle" : "organ"
		},
		"e.animal_model" : {
			"bVisible" : false,
			"inMatrix" : true,
			"sTitle" : "Species"
		},
		"e.exposure_time" : {
			"bVisible" : false,
			"inMatrix" : true,
			"sTitle" : "Exposure time"
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
		}

	},
	"conditions" : {
		"bVisible" : false,
		"material" : {
			"iOrder" : -5,
			"bVisible" : true,
			"inMatrix" : true,
			"sTitle" : "Material"
		},
		"treatment_condition" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true,
			"sTitle" : "Treatment condition"
		},		
		"concentration" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true
		},
		"concentration_ml" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true
		},		
		"time point" : {
			"iOrder" : -6,
			"bVisible" : true,
			"inMatrix" : true
		},
		"exposure_time" : {
			"iOrder" : -6,
			"bVisible" : true,
			"inMatrix" : true
		},
		"replicate" : {
			"iOrder" : -5,
			"bVisible" : true,
			"inMatrix" : false
		},
		"ph" : {
			"bVisible" : true
		},
		"medium" : {
			"bVisible" : true,
			"sTitle" : "Medium"
		},
		"biotarget" : {
			"bVisible" : false
		},
		"phraseother_percentile" : {
			"sTitle" : "Percentile",
			"bVisible" : false
		},

		"remark" : {
			"bVisible" : false
		},
		"coating_description" : {
			"sTitle" : "Coating",
			"bVisible" : true
		},
		"cell" : {
			"sTitle" : "Cell",
			"bVisible" : true
		},
		"shape_descriptive" : {
			"bVisible" : true,
			"sTitle" : "Shape"
		},
		"dose" : {
			"bVisible" : true
		},
		"exposure" : {
			"bVisible" : true
		},
		"sampling_time" : {
			"visible" : true
		},
		"temperature" : {
			"visible" : true
		},
		"total dose" : {
			"visible" : true
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
