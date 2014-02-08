{
    "columns": {
        "_": {
            "main": {
                "name": {
                    "bVisible": false
                }
            },
            "parameters": {
                "study year": {
                    "bVisible": false
                }
            },
            "conditions": {},
            "effects": {},
            "protocol": {},
            "interpretation": {}
        },
        "GI_GENERAL_INFORM_SECTION" : {
            "conditions": {
                "remark": {
                    "iOrder": -4
                }
            },        
            "effects": {
                "endpoint": {
                    "sTitle": "Physical state",
                    "iOrder" : -5
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
        "PC_VAPOUR_SECTION": {
            "conditions": {
                "temperature": {
                    "iOrder": -2
                },
                "decomposition": {
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
                "temperature": {
                    "iOrder": -3
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "pKa Value",
                    "iOrder": -4
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : 0
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
                }
            },
            "interpretation": {
                "result": {
                    "iOrder": -3,
					"sTitle" : "Interpretation of the results"                    
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : 0
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
                }
            },
            "interpretation": {
                "result": {
                    "iOrder": -3,
                    "sTitle" : "Interpretation of the results"
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : 0
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
                }
            },
            "interpretation": {
                "result": {
                    "iOrder": -3,
					"sTitle" : "Interpretation of the results"                    
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : 0
                }                
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
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
                },
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : 0
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
                }
            },
            "effects": {
                "endpoint": {
                    "bVisible": false
                },
                "result": {
                    "sTitle": "Genotoxicity",
                    "iOrder": -3
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -1
                }
            },
            "interpretation": {
                "result": {
                	"sTitle" : "Interpretation of the results",
                    "bVisible": true,
                    "iOrder": -2
                },
                "criteria": {
                    "bVisible": false
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
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
                }
            },
            "interpretation": {
                "result": {
                    "bVisible": false
                }
            }
        },
        "TO_DEVELOPMENTAL_SECTION" : {
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
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
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
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
                "type of study": {
                    "iOrder": -8,
                    "sTitle": "Study type"
                },
                "type of method": {
                    "iOrder": -9,
                    "sTitle": "Method type"
                },
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
                }
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
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
        "TO_SKIN_IRRITATION_SECTION": {
            "parameters": {
                "type of method": {
                    "iOrder": -9,
                    "sTitle": "Method type"
                },
                "species": {
                    "iOrder": -8
                },
                "study year" :{
                	"bVisible" : true,
                	"iOrder" : -1
                }                
            },
            "protocol": {
                "guideline": {
                    "iOrder": -3
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
                    "sTitle" : "Organism"
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
                    "sTitle" : "Meas. Conc."
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
                    "sTitle" : "Organism"
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
                    "sTitle" : "Meas. Conc."
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
                    "sTitle" : "Organism"
                }      
            },
            "conditions": {
                "exposure": {
                    "iOrder": -4
                },
                "effect": {
                    "iOrder": -1
                } ,
                "measured concentration": {
                    "iOrder": -5,
  					"sTitle" : "Meas. Conc."                    
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
  					"sTitle" : "Organism"                    
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
                    "sTitle" : "Meas. Conc."
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
  					"sTitle" : "Organism"                    
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
 					"sTitle" : "Meas. Conc."                    
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
  					"sTitle" : "Organism"      
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
 					"sTitle" : "Meas. Conc."                    
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
  					"sTitle" : "Organism"      
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
 					"sTitle" : "Meas. Conc."                    
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
 					"sTitle" : "Meas. Conc."                    
                },
                "test organism": {
                    "iOrder": -8,
  					"sTitle" : "Organism"      
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
  					"sTitle" : "Organism"      
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
 					"sTitle" : "Meas. Conc."                    
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
  					"sTitle" : "Organism"      
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
                    "sTitle" : "Meas. Conc."
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
  					"sTitle" : "Organism"      
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
 					"sTitle" : "Meas. Conc."                    
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
        }
    }
}