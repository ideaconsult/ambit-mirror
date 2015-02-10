var config_study = {
    "columns": {
        "_": {
            "main": {
                "name": {
                    "bVisible": false
                }
            },
            "parameters": {
          
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
                    "iOrder": -5
                },
                "result": {
                    "bVisible": false
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Substance type",
                    "iOrder": -3
                }
            }
        },
        "PC_BOILING_SECTION": {
            "conditions": {
                "atm. pressure": {
                    "sTitle": "Pressure"
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -1
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_MELTING_SECTION": {
            "conditions": {
                "decomposition": {
                    "iOrder": -2
                },
                "sublimation": {
                    "iOrder": -1
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -3
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_PARTITION_SECTION": {
            "parameters": {
                "method type": {
                    "bVisible": false
                }
            },
            "conditions": {
                "temperature": {
                    "iOrder": -2
                },
                "ph": {
                    "sTitle": "pH",
                    "iOrder": -1
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -3
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_NON_SATURATED_PH_SECTION": {
            "parameters": {
                "method type": {
                    "bVisible": false
                }
            },
            "conditions": {
                "temperature": {
                    "iOrder": -2
                },
                "doses/concentrations": {
                    "sTitle": "Concentration",
                    "iOrder": -1
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "pH Value",
                    "iOrder": -3
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_VAPOUR_SECTION": {
            "conditions": {
                "temperature": {
                    "iOrder": -2
                },
                "decomposition": {
                    "bVisible": false
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -3
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_WATER_SOL_SECTION": {
            "parameters": {
                "method type": {
                    "bVisible": false
                }
            },
            "conditions": {
                "temperature": {
                    "iOrder": -3
                },
                "ph": {
                    "sTitle": "pH",
                    "iOrder": -2
                },
                "remark": {
                    "iOrder": -1
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -4
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_DISSOCIATION_SECTION": {
            "conditions": {
                "no": {
                    "sTitle": "pKa No.",
                    "iOrder": -3
                },
                "temperature": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "pKa Value",
                    "iOrder": -5
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "PC_SOL_ORGANIC_SECTION": {
            "conditions": {
                "solvent": {
                    "iOrder": -4
                },
                "temperature": {
                    "iOrder": -2
                },
                "remark": {
                    "iOrder": -1
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Solubility in solv",
                    "iOrder": -3
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EN_ADSORPTION_SECTION": {
            "conditions": {
                "remark": {
                    "iOrder": -1
                },
                "% org.carbon": {
                    "bVisible": false
                },
                "temperature": {
                    "bVisible": false
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Kp type",
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EN_BIOACCU_TERR_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -4
                }
            },
            "conditions": {
                "doses/concentrations": {
                    "iOrder": -5,
                    "sTitle": "Conc./Dose",
                    "bVisible" : false
                },
                "bioacc. basis": {
                    "iOrder": -3
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Bioacc. type",
                    "iOrder": -2
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -1
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EN_BIOACCUMULATION_SECTION": {
            "parameters": {
                "route": {
                    "iOrder": -5
                },
                "species": {
                    "iOrder": -3
                }
            },
            "conditions": {
                "doses/concentrations": {
                    "sTitle": "Conc. / Dose",
                    "iOrder": -4
                },
                "bioacc. basis": {
                    "iOrder": -2
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Bioacc. type",
                    "iOrder": -1
                },
                "result": {
                    "sTitle": "",
                    "iOrder": 0
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_BIODEG_WATER_SCREEN_SECTION": {
            "parameters": {
                "test type": {
                    "iOrder": -4
                }
            },
            "conditions": {
                "sampling time": {
                    "iOrder": -3
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Degrad. Parameter",
                    "iOrder": -1
                },
                "result": {
                    "sTitle": "Degradation",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "TO_HYDROLYSIS_SECTION": {
            "conditions": {
                "ph": {
                    "iOrder": -3
                },
                "temperature": {
                    "iOrder": -2
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "DT50",
                    "iOrder": -1
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_ACUTE_ORAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -7
                },
                "study year": {
                    "bVisible": false,
                    "iOrder": 0
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }                
            },
            "interpretation": {
                "result": {
                    "iOrder": -3,
                    "sTitle": "Interpretation of the results"
                },
                "criteria": {
                    "iOrder": -2
                }
            }
        },
        "TO_ACUTE_DERMAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -7
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": 0
                },
                "sex": {
                    "iOrder": -4
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }                   
            },
            "interpretation": {
                "result": {
                    "iOrder": -3,
                    "sTitle": "Interpretation of the results"
                },
                "criteria": {
                    "iOrder": -2
                }
            }
        },
        "TO_ACUTE_INHAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -8
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": 0
                },
                "sex": {
                    "iOrder": -4
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                },
                "route of administration": {
                    "iOrder": -7
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }   
            
            },
            "interpretation": {
                "result": {
                    "iOrder": -3,
                    "sTitle": "Interpretation of the results"
                },
                "criteria": {
                    "iOrder": -2
                }
            }
        },
        "TO_EYE_IRRITATION_SECTION": {
            "parameters": {
                "type of method": {
                    "iOrder": -7,
                    "sTitle": "Method type"
                },
                "species": {
                    "iOrder": -6
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": 0
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "bVisible": false
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Interpretation of the results",
                    "iOrder": -3
                },
                "criteria": {
                    "iOrder": -2
                }
            }
        },
        "TO_GENETIC_IN_VITRO_SECTION": {
            "parameters": {
                "type of genotoxicity": {
                    "iOrder": -9,
                    "sTitle": "Genotoxicity type"
                },
                "type of study": {
                    "iOrder": -8,
                    "sTitle": "Study type"
                },
                "metabolic activation system": {
                    "iOrder": -7,
                    "sVisible": true
                },
                "target gene": {
                    "iOrder": -6
                }
            },
            "conditions": {
                "metabolic activation system": {
                    "iOrder": -4,
                    "sTitle": "Metabolic activation"
                },
                "metabolic activation": {
                    "iOrder": -4
                },
                "species": {
                    "iOrder": -5,
                    "sTitle": "Species/strain"
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "bVisible": false,
                    "sTitle": "Genotoxicity",
                    "iOrder": -3
                },
                "text": {
                    "sTitle": "Genotoxicity",
                    "bVisible": true,
                    "iOrder": -3
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "sTitle": "Interpretation of the result",
                    "bVisible": true,
                    "iOrder": -2
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "TO_GENETIC_IN_VIVO_SECTION": {
            "parameters": {
                "type of genotoxicity": {
                    "iOrder": -9,
                    "sTitle": "Genotoxicity type"
                },
                "type of study": {
                    "iOrder": -8,
                    "sTitle": "Study type"
                },
                "route of administration": {
                    "iOrder": -7,
                    "sVisible": true
                },
                "species": {
                    "iOrder": -6
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": 0
                }
            },
            "conditions": {
                "toxicity": {
                    "iOrder": -4
                },
                "sex": {
                    "iOrder": -5
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "bVisible": false,
                    "sTitle": "Genotoxicity",
                    "iOrder": -3
                },
                "text": {
                    "sTitle": "Genotoxicity",
                    "bVisible": true,
                    "iOrder": -3
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "sTitle": "Interpretation of the result",
                    "bVisible": true,
                    "iOrder": -2
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "TO_CARCINOGENICITY_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -10
                },
                "route of administration": {
                    "iOrder": -9
                },
                "doses/concentrations": {
                    "iOrder": -8,
                    "sTitle": "Dose/concentrations"
                },
                "type of genotoxicity": {
                	"bVisible" : false
                },
                "type of study": {
                	"bVisible" : false
                }                
            },
            "conditions": {
                "effect type": {
                    "iOrder": -6
                },
                "sex": {
                    "bVisible" : false
                },
                "toxicity": {
                    "bVisible" : false
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -7
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                },
                "citation": {
                    "bVisible": true
                }                
            },            
            "interpretation": {
                "result": {
                    "iOrder": -3,
                    "bVisible": false,
                    "sTitle": "Interpretation of the results"
                },
                "criteria": {
                    "bVisible": false,
                    "iOrder": -2
                }
            }
        },
        "TO_REPRODUCTION_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -10
                },
                "route of administration": {
                    "iOrder": -9
                },
                "doses/concentrations": {
                    "sTitle": "Dose/concentrations",
                    "iOrder": -8
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "conditions": {
                "generation": {
                    "iOrder": -7
                },
                "sex": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_DEVELOPMENTAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -10
                },
                "route of administration": {
                    "iOrder": -9
                },
                "doses/concentrations": {
                    "sTitle": "Dose/concentrations",
                    "iOrder": -8
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                },
                "effect type": {
                    "iOrder": -7
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_REPEATED_ORAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -10
                },
                "route of administration": {
                    "iOrder": -8
                },
                "test type": {
                    "iOrder": -9
                },
                "doses/concentrations": {
                    "sTitle": "Dose/concentrations",
                    "iOrder": -7
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_REPEATED_INHAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -10
                },
                "route of administration": {
                    "iOrder": -8
                },
                "test type": {
                    "iOrder": -9
                },
                "doses/concentrations": {
                    "sTitle": "Dose/concentrations",
                    "iOrder": -7
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_REPEATED_DERMAL_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -11
                },
                "type_coverage": {
                    "iOrder": -10,
                    "sTitle": "Type of coverage"
                },
                "route of administration": {
                    "iOrder": -8
                },
                "test type": {
                    "iOrder": -9
                },
                "doses/concentrations": {
                    "sTitle": "Dose/concentrations",
                    "iOrder": -7
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "conditions": {
                "sex": {
                    "iOrder": -4
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -6
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_SENSITIZATION_SECTION": {
            "parameters": {
                "species": {
                    "iOrder": -7,
                    "bVisible": false
                },
                "type of study": {
                    "iOrder": -8,
                    "sTitle": "Study type"
                },
                "type of method": {
                    "iOrder": -9,
                    "sTitle": "Method type"
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "bVisible": false
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Interpretation of the results",
                    "iOrder": -7
                },
                "criteria": {
                    "iOrder": -6
                }
            }
        },
        "TO_SENSITIZATION_INVITRO_SECTION": {
            "parameters": {
                "type_of_study": {
                    "iOrder": -11,
                    "sTitle": "Study type",
                    "bVisible": false
                },
                "type_of_method": {
                    "iOrder": -10,
                    "sTitle": "Method type"
                },
                "data_gathering_instruments": {
                    "iOrder": -9,
                    "bVisible": false
                }                
            },
            "protocol": {
                "guideline": {
                    "iOrder": -2
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "conditions": {
                "target gene": {
                    "iOrder": -7
                },
                "cell line": {
                    "iOrder": -8
                }
            },            
            "effects": {
                "endpoint": {
                    "bVisible": true,
                    "iOrder": -6
                },
                "result": {
                    "bVisible": true,
                    "iOrder": -5
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Pos/Neg",
                    "iOrder": -4
                },
                "criteria": {
                    "iOrder": -3,
                    "bVisible": false
                }
            }
        },        
        "TO_SENSITIZATION_INCHEMICO_SECTION": {
            "parameters": {
                "type_of_study": {
                    "iOrder": -11,
                    "sTitle": "Study type",
                    "bVisible": false
                },
                "type_of_method": {
                    "iOrder": -10,
                    "sTitle": "Method type"
                },
                "data_gathering_instruments": {
                    "iOrder": -9,
                    "bVisible": false
                }                
            },
            "protocol": {
                "guideline": {
                    "iOrder": -2
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "conditions": {
                "protein": {
                    "iOrder": -7
                },
            },            
            "effects": {
                "endpoint": {
                    "bVisible": true,
                    "iOrder": -6
                },
                "result": {
                    "bVisible": true,
                    "iOrder": -5
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Pos/Neg",
                    "iOrder": -4
                },
                "criteria": {
                    "iOrder": -3,
                    "sTitle": "Potency",
                    "bVisible": true
                }
            }
        },                
        "TO_SKIN_IRRITATION_SECTION": {
            "parameters": {
                "type of method": {
                    "iOrder": -9,
                    "sTitle": "Method type"
                },
                "species": {
                    "iOrder": -8
                },
                "study year": {
                	"bVisible": false,
                    "iOrder": -1
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
                },
                "citation": {
                    "bVisible": true
                }   
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "bVisible": false
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Interpretation of the results",
                    "iOrder": -7
                },
                "criteria": {
                    "iOrder": -6
                }
            }
        },
        "EN_HENRY_LAW_SECTION": {
            "conditions": {
                "temperature": {
                    "iOrder": -4
                },
                "pressure": {
                    "iOrder": -3
                },
                "remark": {
                    "iOrder": -2
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -5
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EN_STABILITY_IN_SOIL_SECTION": {
            "parameters": {
                "test type": {
                    "iOrder": -9
                }
            },
            "conditions": {
                "soil no.": {
                    "iOrder": -8
                },
                "soil type": {
                    "iOrder": -7
                },
                "oc content": {
                    "iOrder": -6
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Half-life",
                    "iOrder": -5
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -4
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_PHOTOTRANS_AIR_SECTION": {
            "parameters": {
                "reactant": {
                    "iOrder": -9
                }
            },
            "conditions": {
                "test condition": {
                    "iOrder": -8
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "DT50",
                    "iOrder": -7
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_BIODEG_WATER_SIM_SECTION": {
            "parameters": {
                "test type": {
                    "iOrder": -9
                }
            },
            "conditions": {
                "sampling time": {
                    "iOrder": -8
                },
                "degradation": {
                    "iOrder": -7
                },
                "degradation parameter": {
                    "iOrder": -6
                },
                "compartment": {
                    "iOrder": -5
                }
            },
            "effects": {
                "endpoint": {
                    "sTitle": "Half-life",
                    "iOrder": -4
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -3
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_FISHTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_CHRONFISHTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_DAPHNIATOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_CHRONDAPHNIATOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_ALGAETOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_BACTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_SOIL_MICRO_TOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_PLANTTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -9
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -6
                },
                "effect": {
                    "iOrder": -3
                },
                "measured concentration": {
                    "iOrder": -7,
                    "sTitle": "Meas. Conc."
                },
                "test organism": {
                    "iOrder": -8,
                    "sTitle": "Organism"
                },
                "based on": {
                    "iOrder": -2
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -5
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -4
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_SEDIMENTDWELLINGTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_SOILDWELLINGTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "EC_HONEYBEESTOX_SECTION": {
            "parameters": {
                "test medium": {
                    "iOrder": -7
                },
                "test organism": {
                    "iOrder": -6,
                    "sTitle": "Organism"
                }
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                },
                "measured concentration": {
                    "iOrder": -5,
                    "sTitle": "Meas. Conc."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -3
                },
                "result": {
                    "sTitle": "",
                    "iOrder": -2
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "AGGLOMERATION_AGGREGATION_SECTION": {
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
                "percentile": {
                    "bVisible": false
                },
                "remark": {
                    "sTitle": "Remarks",
                    "iOrder": -15,
                    "bVisible": false
                },
                "medium": {
                    "sTitle": "Medium",
                    "iOrder": -16
                },
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -18
                },
                "ph": {
                    "iOrder": -17
                },
                "seq_num": {
                    "iOrder": -21,
                    "sTitle": "Seq. num."
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -20
                },
                "result": {
                    "iOrder": -19
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
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
                    "iOrder": -7,
                    "bVisible": false
                }
            }
        },
        "ASPECT_RATIO_SHAPE_SECTION": {
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
                    "iOrder": -19
                },
                "x": {
                    "iOrder": -16
                },
                "y": {
                    "iOrder": -15
                },
                "z": {
                    "iOrder": -14
                },
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -17
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -20
                },
                "result": {
                    "iOrder": -18
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
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
            "parameters": {
                "type of method": {
                    "iOrder": -22
                },
                "method details": {
                    "iOrder": -10,
                    "bVisible": false
                },
                "sampling": {
                    "iOrder": -8,
                    "bVisible": false
                },
                "data_gathering_instruments": {
                    "sTitle": "Instruments",
                    "iOrder": -7,
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -21
                }
            },
            "conditions": {
                "medium": {
                    "sTitle": "Medium",
                    "iOrder": -15,
                    "bVisible": true
                },
                "n": {
                    "iOrder": -17
                },                
                "ph": {
                    "iOrder": -16
                },
                "remark": {
                    "sTitle": "Remarks",
                    "iOrder": -19
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -21
                },
                "result": {
                    "iOrder": -20
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -9
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
                    "iOrder": -10,
                    "bVisible": false
                }
            }
        },
        "SURFACE_CHEMISTRY_SECTION": {
            "parameters": {
                "type of method": {
                    "iOrder": -25
                },
                "method details": {
                    "iOrder": -11,
                    "bVisible": false
                },
                "sampling": {
                    "sTitle": "Sampling",
                    "iOrder": -9,
                    "bVisible": false
                },
                "functionalization": {
                    "bVisible": false
                },
                "coating": {
                    "bVisible": false
                },
                "data_gathering_instruments": {
                    "sTitle": "Instruments",
                    "iOrder": -8,
                    "bVisible": false
                }
            },
            "conditions": {
                "type": {
                    "sTitle": "",
                    "iOrder": -23
                },
                "description": {
                    "sTitle": "Type",
                    "iOrder": -22
                },
                "coating_description": {
                    "sTitle": "Coating description",
                    "iOrder": -21,
                    "bVisible": false
                },
                "remark": {
                    "sTitle": "Remarks",
                    "iOrder": -15,
                    "bVisible": false
                },
                "element_or_group": {
                    "sTitle": "Element / Func. group",
                    "iOrder": -19,
                    "bVisible": false
                },
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -17
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -20,
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Fraction",
                    "iOrder": -18
                },
                "text" : {
                	"sTitle": "Element / Func. group",
                	"iOrder": -19,
                	"bVisible": true
                }
                
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
                },
                "citation": {
                    "bVisible":true,
                    "iOrder" : -11,
                    "sTitle": "Reference",
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
                },
                "criteria": {
                    "sTitle": "Coating / Functionalisation",
                    "bVisible": true,
                    "iOrder": -24
                }
            }
        },
        "PC_GRANULOMETRY_SECTION": {
            "parameters": {
                "type of method": {
                    "iOrder": -25
                },
                "distribution_type": {
                    "sTitle": "Distribution type",
                    "iOrder": -23
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -24
                }
            },
            "conditions": {
                "seq_num": {
                    "sTitle": "Passage num.",
                    "iOrder": -22
                },
                "medium": {
                    "sTitle": "Medium",
                    "iOrder": -14,
                    "bVisible": true
                },                
                "remark": {
                    "sTitle": "Remark",
                    "iOrder": -15,
                    "bVisible": false
                },
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -17
                },
                "n": {
                    "iOrder": -16
                },                
                "phraseother_percentile" : {
                	"bVisible": false
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -20,
                    "bVisible": true
                },
                "result": {
                    "sTitle": "Value",
                    "iOrder": -18
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -9
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
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "CRYSTALLITE_AND_GRAIN_SIZE_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                },
                "material_isotropic": {
                    "sTitle": "Isotropic material",
                    "iOrder": -18
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -14
                },
                "medium": {
                    "sTitle": "Medium",
                    "iOrder": -15
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17,
                    "bVisible": true
                },
                "result": {
                    "sTitle": "Result",
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
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
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "DUSTINESS_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -15
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17,
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Dustiness index",
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
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
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "POROSITY_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -15
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17,
                    "bVisible": true
                },
                "result": {
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
                },
                "citation": {
                    "bVisible": true,
                    "iOrder": -11,
                    "sTitle": "Reference",
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
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "SPECIFIC_SURFACE_AREA_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -15
                },
                "remark": {
                    "sTitle": "Remarks",
                    "iOrder": -14
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17,
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Specific Surface Area",
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
                },
                "citation": {
                    "bVisible": true,
                    "iOrder": -11,
                    "sTitle": "Reference",
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
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "POUR_DENSITY_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -15
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17,
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Pour density",
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
                },
                "citation": {
                    "bVisible": true,
                    "iOrder": -11,
                    "sTitle": "Reference",
                    "mRender" : function(data,type,full) {
                    	var sOut = (data["year"]==null || data["year"] == 0)?"DOI":data["year"];
                    	return "<a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                    }
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Conclusions",
                    "iOrder": -14,
                    "bVisible": true
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "PHOTOCATALYTIC_ACTIVITY_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -15
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17
                },
                "result": {
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
                },
                "citation": {
                    "bVisible": true,
                    "sTitle": "Reference",
                    "mRender" : function(data,type,full) {
                    	var sOut = (data["year"]==null || data["year"] == 0)?"DOI":data["year"];
                    	return "<a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                    }
                }
            },
            "interpretation": {
                "result": {
                    "sTitle": "Conclusions",
                    "iOrder": -14,
                    "bVisible": true
                },
                "criteria": {
                	"iOrder": -11,
                    "bVisible": false
                }
            }
        },
        "CATALYTIC_ACTIVITY_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "std_dev": {
                    "sTitle": "Std. dev.",
                    "iOrder": -15
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -17
                },
                "result": {
                    "iOrder": -16
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -10
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
                    "iOrder": -14,
                    "bVisible": true
                },
                "criteria": {
                    "bVisible": false
                }
            }
        },
        "CRYSTALLINE_PHASE_SECTION": {
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
                    "bVisible": false
                },
                "testmat_form": {
                    "sTitle": "Test Material Form",
                    "bVisible": true,
                    "iOrder": -19
                }
            },
            "conditions": {
                "crystal system": {
                    "sTitle": "Crystal system",
                    "iOrder": -18
                },
                "common name": {
                    "sTitle": "Common name",
                    "iOrder": -17
                },
                "bravais lattice": {
                    "sTitle": "Bravais lattice",
                    "iOrder": -16
                },
                "point group": {
                    "sTitle": "Point group",
                    "iOrder": -15
                },
                "space group": {
                    "sTitle": "Space group",
                    "iOrder": -14
                },
                "crystallographic planes": {
                    "sTitle": "Crystallographic planes",
                    "iOrder": -13
                }
            },
            "effects": {
                "endpoint": {
                    "iOrder": -12,
                    "bVisible": false
                },
                "result": {
                    "bVisible": false,
                    "iOrder": -11
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -5
                },
                "citation": {
                    "bVisible": true
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
                    "bVisible": true
                },
                "text": {
                    "bVisible": true,
                    "iOrder": -12
                },                
                "result": {
                    "bVisible": false,
                    "iOrder": -11
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -5
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
            "effects": {
                "text": {
                    "bVisible": true
                }
            }       	
        },
        "UNKNOWN_TOXICITY_SECTION_SUMMARY" : {
            "effects": {
                "text": {
                    "bVisible": true
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
                    "bVisible": true
                },
                "text": {
                    "bVisible": true,
                    "iOrder": -11
                },                
                "result": {
                    "bVisible": true,
                    "iOrder": -12
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
            "protocol": {
                "citation": {
                    "bVisible":true,
                    "iOrder" : -30,
                    "sTitle": "Reference",
                    "mRender" : function(data,type,full) {
                    	var sOut = (data["year"]==null || data["year"] == 0)?"DOI":data["year"];
                    	return "<a href='" + data["title"] + "' title='" + data["title"] + "' target='_doi' >"+sOut+"</a>";
                    }
                }
            },
            "interpretation": {
                "result": {
                	"bVisible": true,
                	"sTitle" : "Result (text)"
                },
                "criteria": {
                	"bVisible": false
                }
            }   

        },
        "CELL_DEATH" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
              	"concentration": {
              		"bVisible": true,
              		"iOrder" : -20
                  },
                	"baseline": {
               		"bVisible": false,
                      "iOrder" : -19
                  },
                  "analysis_direction": {
                  	"bVisible": false,
                  	"iOrder" : -18
                  },
                  "signal_direction": {
              	  	"sTitle" : "Signal direction",
                	"bVisible": true,
                    	"iOrder" : -17
                  },
                  "key_positive_control": {
                    	"bVisible": false,
                    	"iOrder" : -16
                  },
                  "assay_function_type": {
                  	"bVisible": false,
                  	"iOrder" : -15
                  },
                  "intended_target_type": {
                	"sTitle" : "Target Type",  
                	"bVisible": false,
                  	"iOrder" : -14
                  },
                  "intended_target_type_sub": {
                    	"bVisible": false,
                    	"iOrder" : -13
                  },
                  "intended_target_family": {
                	"sTitle" : "Target Family",
                  	"bVisible": true,
                  	"iOrder" : -12
                  },
                  "intended_target_family_sub": {
                	"bVisible": false,
                	"iOrder" : -11
                  }                
              },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "OXIDATIVE_PHOSPHORYLATION" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
              	"concentration": {
              		"bVisible": true,
              		"iOrder" : -20
                  },
                	"baseline": {
               		"bVisible": false,
                      "iOrder" : -19
                  },
                  "analysis_direction": {
                  	"bVisible": false,
                  	"iOrder" : -18
                  },
                  "signal_direction": {
              	  	"sTitle" : "Signal direction",
                	"bVisible": true,
                    	"iOrder" : -17
                  },
                  "key_positive_control": {
                    	"bVisible": false,
                    	"iOrder" : -16
                  },
                  "assay_function_type": {
                  	"bVisible": false,
                  	"iOrder" : -15
                  },
                  "intended_target_type": {
                	"sTitle" : "Target Type",  
                	"bVisible": false,
                  	"iOrder" : -14
                  },
                  "intended_target_type_sub": {
                    	"bVisible": false,
                    	"iOrder" : -13
                  },
                  "intended_target_family": {
                	"sTitle" : "Target Family",
                  	"bVisible": true,
                  	"iOrder" : -12
                  },
                  "intended_target_family_sub": {
                	"bVisible": false,
                	"iOrder" : -11
                  }                
              },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "CELL_MORPHOLOGY" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
              	"concentration": {
              		"bVisible": true,
              		"iOrder" : -20
                  },
                	"baseline": {
               		"bVisible": false,
                      "iOrder" : -19
                  },
                  "analysis_direction": {
                  	"bVisible": false,
                  	"iOrder" : -18
                  },
                  "signal_direction": {
              	  	"sTitle" : "Signal direction",
                	"bVisible": true,
                    	"iOrder" : -17
                  },
                  "key_positive_control": {
                    	"bVisible": false,
                    	"iOrder" : -16
                  },
                  "assay_function_type": {
                  	"bVisible": false,
                  	"iOrder" : -15
                  },
                  "intended_target_type": {
                	"sTitle" : "Target Type",  
                	"bVisible": false,
                  	"iOrder" : -14
                  },
                  "intended_target_type_sub": {
                    	"bVisible": false,
                    	"iOrder" : -13
                  },
                  "intended_target_family": {
                	"sTitle" : "Target Family",
                  	"bVisible": true,
                  	"iOrder" : -12
                  },
                  "intended_target_family_sub": {
                	"bVisible": false,
                	"iOrder" : -11
                  }                
              },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "CELL_CYCLE" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
              	"concentration": {
              		"bVisible": true,
              		"iOrder" : -20
                  },
                	"baseline": {
               		"bVisible": false,
                      "iOrder" : -19
                  },
                  "analysis_direction": {
                  	"bVisible": false,
                  	"iOrder" : -18
                  },
                  "signal_direction": {
              	  	"sTitle" : "Signal direction",
                	"bVisible": true,
                    	"iOrder" : -17
                  },
                  "key_positive_control": {
                    	"bVisible": false,
                    	"iOrder" : -16
                  },
                  "assay_function_type": {
                  	"bVisible": false,
                  	"iOrder" : -15
                  },
                  "intended_target_type": {
                	"sTitle" : "Target Type",  
                	"bVisible": false,
                  	"iOrder" : -14
                  },
                  "intended_target_type_sub": {
                    	"bVisible": false,
                    	"iOrder" : -13
                  },
                  "intended_target_family": {
                	"sTitle" : "Target Family",
                  	"bVisible": true,
                  	"iOrder" : -12
                  },
                  "intended_target_family_sub": {
                	"bVisible": false,
                	"iOrder" : -11
                  }                
              },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "CELL_MORPHOLOGY" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
              	"concentration": {
              		"bVisible": true,
              		"iOrder" : -20
                  },
                	"baseline": {
               		"bVisible": false,
                      "iOrder" : -19
                  },
                  "analysis_direction": {
                  	"bVisible": false,
                  	"iOrder" : -18
                  },
                  "signal_direction": {
              	  	"sTitle" : "Signal direction",
                	"bVisible": true,
                    	"iOrder" : -17
                  },
                  "key_positive_control": {
                    	"bVisible": false,
                    	"iOrder" : -16
                  },
                  "assay_function_type": {
                  	"bVisible": false,
                  	"iOrder" : -15
                  },
                  "intended_target_type": {
                	"sTitle" : "Target Type",  
                	"bVisible": false,
                  	"iOrder" : -14
                  },
                  "intended_target_type_sub": {
                    	"bVisible": false,
                    	"iOrder" : -13
                  },
                  "intended_target_family": {
                	"sTitle" : "Target Family",
                  	"bVisible": true,
                  	"iOrder" : -12
                  },
                  "intended_target_family_sub": {
                	"bVisible": false,
                	"iOrder" : -11
                  }                
              },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "CELL_PROLIFERATION" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "PROTEIN_STABILIZATION" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "RECEPTOR_BINDING" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "REGULATION_OF_GENE_EXPRESSION" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "REGULATION_OF_TRANSCRIPTION_FACTOR_ACTIVITY" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "REGULATION_OF_CATALYTIC_ACTIVITY" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "MITOCHONDRIAL_DEPOLARIZATION" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        },
        "NA" : {
            "parameters": {
                "detection_technology": {
                	"iOrder" : -38,
                	"sTitle" : "Detection Technology",
                	"bVisible": true
                },            	
                "organism": {
                	"sTitle" : "Organism",
                	"iOrder" : -37,
                	"bVisible": true
                },
                "tissue": {
                	"sTitle" : "Tissue",
                	"iOrder" : -36,
                	"bVisible": true
                },
                "cell_free_component_source": {
                	"iOrder" : -35,
                    "bVisible": false
                },
                "cell_format": {
                	"iOrder" : -34,
                	"bVisible": false
                },
                "cell_short_name": {
                	"sTitle" : "Cell",
                	"iOrder" : -33,
                	"bVisible": true
                },
                "assay_format_type": {
                	"iOrder" : -32,
                	"bVisible": false
                },                  
                "assay_format_type_sub": {
                	"iOrder" : -31,
                	"bVisible": false
                },
                "dilution_solvent": {
                	"iOrder" : -30,
                	"bVisible": false
                },
                "dilution_solvent_percent_max": {
                	"iOrder" : -29,
                	"bVisible": false
                },
                "assay_design_type": {
                	"iOrder" : -28,
                	"bVisible": false
                },       
                "assay_design_type_sub": {
                	"iOrder" : -27,
                	"bVisible": false
                },  
                "signal_direction_type": {
                	"iOrder" : -26,
                	"bVisible": false
                },                  
                "key_assay_reagent": {
                	"iOrder" : -25,
                	"bVisible": false
                },                  
                "technological_target_type": {
                	"iOrder" : -24,
                	"bVisible": false
                },        
                "technological_target_type_sub": {
                	"iOrder" : -23,
                	"bVisible": false
                },             
                "timepoint": {
                	"sTitle" : "Time point",
                	"iOrder" : -23,
                    "bVisible": true
                },                
            },
        	  "effects": {
              	"endpoint" : {
              		"iOrder" : -22
              	},
                  "result": {
                      "sTitle": "Response",
                      "iOrder" : -21
                  },
                  "text": {
                      "bVisible": false
                  }
              },
              "conditions": {
                	"concentration": {
                		"bVisible": true,
                		"iOrder" : -20
                    },
                  	"baseline": {
                 		"bVisible": false,
                        "iOrder" : -19
                    },
                    "analysis_direction": {
                    	"bVisible": false,
                    	"iOrder" : -18
                    },
                    "signal_direction": {
                	  	"sTitle" : "Signal direction",
                  	"bVisible": true,
                      	"iOrder" : -17
                    },
                    "key_positive_control": {
                      	"bVisible": false,
                      	"iOrder" : -16
                    },
                    "assay_function_type": {
                    	"bVisible": false,
                    	"iOrder" : -15
                    },
                    "intended_target_type": {
                  	"sTitle" : "Target Type",  
                  	"bVisible": false,
                    	"iOrder" : -14
                    },
                    "intended_target_type_sub": {
                      	"bVisible": false,
                      	"iOrder" : -13
                    },
                    "intended_target_family": {
                  	"sTitle" : "Target Family",
                    	"bVisible": true,
                    	"iOrder" : -12
                    },
                    "intended_target_family_sub": {
                  	"bVisible": false,
                  	"iOrder" : -11
                    }                
                },
              "protocol": {
                  "guideline": {
                	  "bVisible": false,
                      "iOrder": -1
                  },
              	 "citation": {
                       "bVisible": true,
                       "sTitle": "Reference",
                       "mRender" : function(data,type,full) {
                       	return data["title"] + " " + data["owner"];
                       },
              		 "iOrder" : -1
                   }             
              },
              "interpretation": {
                  "result": {
                	"sTitle" : "hitc",  
                  	"bVisible": true,
                  	"iOrder" : -10
                  },
                  "criteria": {
                  	"bVisible": false
                  }
              }       

        }
           
    }
}