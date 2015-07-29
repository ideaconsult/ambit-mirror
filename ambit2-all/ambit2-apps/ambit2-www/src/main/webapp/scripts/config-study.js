var config_study = {
    "columns": {
        "_": {
            "main": {
                "name": {
                    "bVisible": false
                }
            },
            "parameters": {
                "data_gathering_instruments": {
                    "sTitle": "Instruments"
                },
                "agitation": {"bVisible" : false},
                "common name": {"bVisible" : false},
                "controls included": {"bVisible" : false},
                "illumination (flux)": {"bVisible" : false},
                "exposure route": {"bVisible" : false},
                "gender / life-stage": {"bVisible" : false},
                "illumination (photoperiod)": {"bVisible" : false},
                "source": {"bVisible" : false},
                "temperature": {"bVisible" : false},
                "ph": {"bVisible" : false},
                "depuration duration": {"bVisible" : false},
                "media": {"bVisible" : false},
                "maintenance and preparation": {"bVisible" : false},
    			"species" : {
    				"bVisible": false
    			}	
            },
            "conditions": {
            },
            "effects": {
                "text": {
                    "bVisible": false
                }
            },
            "protocol": {
                "citation": {
                    "bVisible": false,
                    "iOrder" : -1,
                    "sTitle": "Study year",
                    "mRender" : function(data,type,full) {
                    	return "<span title='"+
                    		(data["title"]==null?"N/A":data["title"])+
                    		"'>" + 
                    		(data["year"]==null?"-":data["year"]) + 
                    		"</span>";
                    }
                }
            },
            "interpretation": {}
        },
        "GI_GENERAL_INFORM_SECTION": {
            "conditions": {
                "remark": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Physical state",
                    "iOrder": -5,
                    "inMatrix": true
                },
                "result": {
                    "bVisible": false
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Substance type",
                    "iOrder": -3,
                    "inMatrix": true
                }
            }
        },
        "PC_MELTING_SECTION": config_i5["PC_MELTING_SECTION"],
        "PC_BOILING_SECTION":  config_i5["PC_BOILING_SECTION"],
        "PC_VAPOUR_SECTION": config_i5["PC_VAPOUR_SECTION"],
        "PC_PARTITION_SECTION": config_i5["PC_PARTITION_SECTION"],
        "PC_WATER_SOL_SECTION": config_i5["PC_WATER_SOL_SECTION"],            
        "PC_SOL_ORGANIC_SECTION": config_i5["PC_SOL_ORGANIC_SECTION"],
        "PC_NON_SATURATED_PH_SECTION": config_i5["PC_NON_SATURATED_PH_SECTION"],
        "PC_DISSOCIATION_SECTION": config_i5["PC_DISSOCIATION_SECTION"],
        "TO_ACUTE_ORAL_SECTION": config_i5["TO_ACUTE_ORAL_SECTION"],
        "TO_ACUTE_DERMAL_SECTION": config_i5["TO_ACUTE_DERMAL_SECTION"],
        "TO_ACUTE_INHAL_SECTION": config_i5["TO_ACUTE_INHAL_SECTION"],

        "TO_SKIN_IRRITATION_SECTION": config_i5["TO_SKIN_IRRITATION_SECTION"],
            
        "TO_EYE_IRRITATION_SECTION":  config_i5["TO_EYE_IRRITATION_SECTION"],
            
        "TO_SENSITIZATION_SECTION":  config_i5["TO_SENSITIZATION_SECTION"],
    
        "TO_REPEATED_ORAL_SECTION":  config_i5["TO_REPEATED_ORAL_SECTION"],

        "TO_REPEATED_INHAL_SECTION":  config_i5["TO_REPEATED_INHAL_SECTION"],
            
        "TO_REPEATED_DERMAL_SECTION":  config_i5["TO_REPEATED_DERMAL_SECTION"],
            
        "TO_GENETIC_IN_VITRO_SECTION":  config_i5["TO_GENETIC_IN_VITRO_SECTION"],
            
        "TO_GENETIC_IN_VIVO_SECTION":  config_i5["TO_GENETIC_IN_VIVO_SECTION"],
         
        "TO_CARCINOGENICITY_SECTION":  config_i5["TO_CARCINOGENICITY_SECTION"],
           
        "TO_REPRODUCTION_SECTION":  config_i5["TO_REPRODUCTION_SECTION"],
    
        "TO_DEVELOPMENTAL_SECTION":  config_i5["TO_DEVELOPMENTAL_SECTION"],
            

        "TO_SENSITIZATION_INVITRO_SECTION": {
			"parameters" : config_ce_invitro["parameters"],
			"effects" : config_ce_invitro["effects"],
			"conditions" : config_ce_invitro["conditions"],			
			"protocol" : config_ce_invitro["protocol"],
			"interpretation" : config_ce_invitro["interpretation"]	
        
        },        
        "TO_SENSITIZATION_INCHEMICO_SECTION": {
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
        "TO_SENSITIZATION_HUMANDB_SECTION": {
			"parameters" : config_ce_humandb["parameters"],
			"effects" : config_ce_humandb["effects"],
			"conditions" : config_ce_humandb["conditions"],			
			"protocol" : config_ce_humandb["protocol"],
			"interpretation" : config_ce_humandb["interpretation"]	
      
        },      
        "TO_SENSITIZATION_INSILICO_SECTION": {
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
        "NPO_1773_SECTION" : {
			"parameters" : config_npo["parameters"],
			"effects" : config_npo["effects"],
			"conditions" : config_npo["conditions"],			
			"protocol" : config_npo["protocol"],
			"interpretation" : config_npo["interpretation"]	        	
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
        "CHMO_0000234_SECTION" :{
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
        "TO_PHOTOTRANS_AIR_SECTION":  config_i5["TO_PHOTOTRANS_AIR_SECTION"],

        "TO_HYDROLYSIS_SECTION":  config_i5["TO_HYDROLYSIS_SECTION"],
            
        "TO_BIODEG_WATER_SCREEN_SECTION":  config_i5["TO_BIODEG_WATER_SCREEN_SECTION"],

        "TO_BIODEG_WATER_SIM_SECTION":  config_i5["TO_BIODEG_WATER_SIM_SECTION"],

        "EN_STABILITY_IN_SOIL_SECTION":  config_i5["EN_STABILITY_IN_SOIL_SECTION"],
            
        "EN_BIOACCUMULATION_SECTION":  config_i5["EN_BIOACCUMULATION_SECTION"],
            
        "EN_BIOACCU_TERR_SECTION":  config_i5["EN_BIOACCU_TERR_SECTION"],
        "EN_ADSORPTION_SECTION":  config_i5["EN_ADSORPTION_SECTION"],
        
        "EN_HENRY_LAW_SECTION":  config_i5["EN_HENRY_LAW_SECTION"],
        "EC_FISHTOX_SECTION":  config_i5["EC_FISHTOX_SECTION"],
            
        "EC_CHRONFISHTOX_SECTION":  config_i5["EC_CHRONFISHTOX_SECTION"],
            
        "EC_DAPHNIATOX_SECTION":  config_i5["EC_DAPHNIATOX_SECTION"],
            
        "EC_CHRONDAPHNIATOX_SECTION":  config_i5["EC_CHRONDAPHNIATOX_SECTION"],
            
        "EC_ALGAETOX_SECTION":  config_i5["EC_ALGAETOX_SECTION"],
            
        "EC_BACTOX_SECTION":  config_i5["EC_BACTOX_SECTION"],
            
        "EC_SOIL_MICRO_TOX_SECTION":  config_i5["EC_SOIL_MICRO_TOX_SECTION"],
        "EC_PLANTTOX_SECTION":  config_i5["EC_PLANTTOX_SECTION"],
            
        "EC_SEDIMENTDWELLINGTOX_SECTION":  config_i5["EC_SEDIMENTDWELLINGTOX_SECTION"],
        "EC_SOILDWELLINGTOX_SECTION":  config_i5["EC_SOILDWELLINGTOX_SECTION"],
        "EC_HONEYBEESTOX_SECTION":  config_i5["EC_HONEYBEESTOX_SECTION"],
        
        "AGGLOMERATION_AGGREGATION_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	

        },
        "ASPECT_RATIO_SHAPE_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
		       "interpretation": {
		            "result": {
		                "inMatrix" : true,
		                "bVisible": true
		            },
		            "criteria": {
		                "bVisible": true
		            }
		        }	
        },
        "I5_ASPECT_RATIO_SHAPE_SECTION": {
        	 "parameters": {
                 "type of method": {
                     "iOrder": -22
                 },
                 "method details": {
                     "iOrder": -11,
                     "bVisible": false
                 },
                 "sampling": {
                     "iOrder": -9,
                     "bVisible": false
                 },
                 "data_gathering_instruments": {
                     "sTitle": "Instruments",
                     "iOrder": -8,
                     "bVisible": false
                 }
             },
             "conditions": {
                 "shape_descriptive": {
                     "sTitle": "Shape",
                     "iOrder": -19,
                     "inMatrix" : true
                 },
                 "x": {
                     "iOrder": -16,
                     "inMatrix" : true
                 },
                 "y": {
                     "iOrder": -15,
                     "inMatrix" : true
                 },
                 "z": {
                     "iOrder": -14,
                     "inMatrix" : true                    
                 }
             },
             "effects": {
                 "endpoint": {
                     "iOrder": -20,
                     "inMatrix" : true
                 },
                 "result": {
                     "iOrder": -18,
                     "inMatrix" : true
                 }
             },
            "protocol": {
                "guideline": {
                    "iOrder": -10,
                    "inMatrix" : true
                },
                "citation": {
                    "bVisible": true,
                    "sTitle": "Reference",
                    "iOrder": -11,
                    "mRender" : function(data,type,full) {
                    	var sOut = (data["year"]==null || data["year"] == 0)?"DOI":data["year"];
                    	return "<a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                    }
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Conclusions",
                    "bVisible": false
                }
            }
        },
        "ZETA_POTENTIAL_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	 	
        },
        "I5ZETA_POTENTIAL_SECTION":  config_i5["ZETA_POTENTIAL_SECTION"],

        "GENERIC_SURFACE_CHEMISTRY_SECTION": {
	    	"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
	         "interpretation": {
	                "result": {
	                    "sTitle": "Conclusions",
	                    "bVisible": false
	                },
	                "criteria": {
	                    "sTitle": "Coating / Functionalisation",
	                    "bVisible": true,
	                    "iOrder": -24
	                }
	            }
        },
        "SURFACE_CHEMISTRY_SECTION": config_i5["SURFACE_CHEMISTRY_SECTION"],

        "PC_GRANULOMETRY_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "CRYSTALLITE_AND_GRAIN_SIZE_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "DUSTINESS_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "POROSITY_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "SPECIFIC_SURFACE_AREA_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "POUR_DENSITY_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "PHOTOCATALYTIC_ACTIVITY_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "CATALYTIC_ACTIVITY_SECTION": {
			"parameters" : config_bao["parameters"],
			"effects" : config_bao["effects"],
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "CRYSTALLINE_PHASE_SECTION": {
			"parameters" : config_bao["parameters"],
	        "effects": {
	            "endpoint": {
	                "bVisible": true,
	                "inMatrix" : true,
	            },
	            "result": {
	                "bVisible": false,
	                "inMatrix" : true,
	            },
	            "text": {
	            	"sTitle" : "Result",
	                "bVisible": true,
	                "inMatrix" : true,
	            }       
	        },
			"conditions" : config_bao["conditions"],			
			"protocol" : config_bao["protocol"],
			"interpretation" : config_bao["interpretation"]	
        },
        "PROTEOMICS_SECTION": {
            "parameters": {
                "type of method": {
                    "iOrder": -23,
                    "sTitle": "Method type"
                },
                "method details": {
                    "iOrder": -22,
                    "bVisible": false
                },
                "sampling": {
                    "sTitle": "Sampling",
                    "iOrder": -21,
                    "bVisible": false
                },
                "data_gathering_instruments": {
                    "sTitle": "Instruments",
                    "iOrder": -20,
                    "bVisible": true
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": false,
                    "iOrder": -19
                }
            },
            "conditions": {
             
            },
            "effects": {
                "endpoint": {
                    "iOrder": -13,
                    "bVisible": true,
                    "inMatrix": true
                },
                "text": {
                    "bVisible": true,
                    "iOrder": -12,
                    "inMatrix": true
                },                
                "result": {
                    "bVisible": false,
                    "iOrder": -11
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -5,
                    "inMatrix": true
                },
                "citation": {
                    "bVisible": false
                    
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Conclusions",
                    "iOrder": -10,
                    "bVisible": false
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "UNKNOWN_TOXICITY_SECTION" : {
        	"parameters": {
                "type of method": {
                    "sVisible" : false,
                    "sTitle": "Method type"
                }
        	},
            "effects": {
                "endpoint": {
                    "bVisible": true,
                    "inMatrix": true
                },            	
                "text": {
                    "bVisible": false,
                    "inMatrix": true
                },
                "result": {
                    "bVisible": true,
                    "inMatrix": true
                }                
            },
            "conditions": {
                "p": {
                    "bVisible": false
                },
                "n": {
                    "bVisible": false
                }                    
            
            },            
            "interpretation": {
                "result": {
                    "bVisible": false,
                    "inMatrix": true
                },
                "criteria": {
                    "bVisible": false
                }
            },
            "protocol": {
            	 "guideline": {
                 	"sTitle" : "Protocol",
                 	"iOrder": -20,
                     "inMatrix" : true,
                     "bVisible": true,
                     "mRender" : function(data,type,full) {
                     	var sOut = "";
                      	try {
                      		sOut += data[0];
                      	} catch (err) {}	
                      	sOut += "<br/>";
                      	sOut += "<br/><ul>";
                      	$.each(full.parameters,function(k,v){
                      		
                      		var title = k;
                      		try {
                      			title = config_bao.parameters[k.toLowerCase()]["sTitle"];
                      			if (title === undefined) title=k;
                      		} catch (err) {}
                      		if ((v === undefined) || (v == null) || ("-" == v)) return "";                 		
                      		sOut += "<li>" + title + ": ";
                      		try {
                      			if (v.loValue == undefined) sOut += v;
                      			else sOut += v.loValue + " " + v.unit;
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
                  	 "bVisible": false
                 },                
                 "citation": {
                     "bVisible":true,
                     "iOrder" : -30,
                     "sTitle": "Reference",
                     "mRender" : function(data,type,full) {
                     	var sOut = (data["year"]==null || data["year"] == 0)?"DOI":data["year"];
                     	return "<a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                     }
                 }            
            }
        },
        "UNKNOWN_TOXICITY_SECTION_SUMMARY" : {
            "effects": {
                "text": {
                    "bVisible": true,
                    "inMatrix": true
                }
            }       	
        },      
        "PUBCHEM_SUMMARY_SECTION" : {
            "parameters": {
                "target gene": {
                	"bVisible": false,
                	"iOrder": -14
                }                
            },
            "effects": {
                "endpoint": {
                    "iOrder": -13,
                    "bVisible": true,
                    "inMatrix": true
                },
                "text": {
                    "bVisible": true,
                    "inMatrix": true,
                    "iOrder": -11
                },                
                "result": {
                    "bVisible": true,
                    "inMatrix": true,
                    "iOrder": -12
                }
            },            
            "conditions": {
            	"replicate": {
            		"bVisible": true
                },
              	"doses/concentrations": {
              		"bVisible": true,
                    "sTitle": "Concentration",
                    "inMatrix": true
                },
                "emission wavelength": {
                	"bVisible": true,
                	"iOrder": -5
                },
                "target gene": {
                	"bVisible": false,
                	"iOrder": -6
                }                     
            },            
            "protocol": {
           	 "citation": {
                    "bVisible": true,
                    "sTitle": "Reference",
                    "iOrder": -15,
                    "mRender" : function(data,type,full) {
                    	var sOut = (data["year"]==null || data["year"] == 0)?data["title"]:data["year"];
                    	return "PubChem Assay: <a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                    }
                }             
           },
           "interpretation": {
        	   "result" : {
        		 "sTitle": "PubChem Activity Outcome",  
        		 "iOrder": -4,
        		 "bVisible": true
        	   },
               "criteria": {
            	   "sTitle" : "Target",
            	   "iOrder": -3,
                   "bVisible": true
               }
           }
            
        },        
        "PUBCHEM_DOSERESPONSE_SECTION" : {
            "effects": {
            	"endpoint" : {
            		"iOrder" : -7
            	},
                "result": {
                    "sTitle": "Response",
                    "iOrder" : -5
                },
                "text": {
                    "bVisible": false
                }
            },
            "conditions": {
            	"replicate": {
            		"bVisible": true,
            		"iOrder" : -8
                },
              	"doses/concentrations": {
              		"bVisible": true,
                    "sTitle": "Concentration",
                    "iOrder" : -6
                },
                "emission wavelength": {
                	"bVisible": true,
                	"iOrder" : -9
                }
                
            },
            "protocol": {
            	 "citation": {
                     "bVisible": true,
                     "sTitle": "Reference",
                     "mRender" : function(data,type,full) {
                     	var sOut = (data["year"]==null || data["year"] == 0)?"DOI":data["year"];
                     	return "<a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                     },
            		 "iOrder" : -10
                 }             
            },
            "interpretation": {
                "result": {
                	"bVisible": false
                },
                "criteria": {
                	"bVisible": false
                }
            }            	
        },
        "PUBCHEM_CONFIRMATORY_SECTION" : {
            "effects": {
                "text": {
                    "bVisible": true
                }
            },
            "conditions": {
            	"replicate": {
            		"bVisible": true
                },
              	"doses/concentrations": {
              		"bVisible": true,
                    "sTitle": "Concentration"
                },
                "emission wavelength": {
                	"bVisible": true
                }
                
            },
            "protocol": {
            	 "citation": {
                     "bVisible": true,
                     "sTitle": "Reference",
                     "mRender" : function(data,type,full) {
                     	return  data["title"];
                     }
                 }             
            },
            "interpretation": {
                "result": {
                	"bVisible": false
                },
                "criteria": {
                	"bVisible": false
                }
            }            
        },         
        "CELL_VIABILITY_ASSAY_SECTION" : {
            "effects": {
                "text": {
                    "bVisible": true
                }
            }         	
        },
        "PROTEIN_SMALLMOLECULE_INTERACTION_SECTION" : {
            "effects": {
                "text": {
                    "bVisible": true
                }
            }         	
        },
        "TRANSCRIPTION_PROFILING" : {
            "effects": {
                "text": {
                    "bVisible": true,
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
		}           
    }
}

