var config_npo = {
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
		"species" : {
			"bVisible" : false
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
			"inMatrix" : true,
		}
	},
	"conditions" : {
		"bVisible" : false,
		"treatment_condition" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true,
			"sTitle" : "Treatment condition"
		},
		"amount_of_material" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true
		},
		"concentration" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true
		},
		"concentration_surface" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true
		},		
		"concentration_ml" : {
			"iOrder" : -7,
			"bVisible" : true,
			"inMatrix" : true
		},		
		"sampling_time" : {
			"iOrder" : -6,
			"bVisible" : true,
			"inMatrix" : true
		},		
		"time point" : {
			"iOrder" : -6,
			"bVisible" : true,
			"inMatrix" : true
		},
		"replicate" : {
			"iOrder" : -5,
			"bVisible" : true,
			"inMatrix" : false
		}
	},
	"interpretation" : {
		"result" : {
			"inMatrix" : false,
			"bVisible" : false
		},
		"criteria" : {
			"bVisible" : false
		}
	}
}
