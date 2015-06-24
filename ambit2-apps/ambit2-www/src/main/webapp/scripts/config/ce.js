var config_ce = {
	     "parameters": {
             "type_of_study": {
                 "iOrder": -11,
                 "sTitle": "Study type",
                 "bVisible": false
             },
             "type_of_method": {
                 "iOrder": -10,
                 "sTitle": "Method type",
                 "bVisible": false
             },
             "data_gathering_instruments": {
                 "iOrder": -9,
                 "bVisible": false
             }                
         },
         "protocol": {
             "guideline": {
             	"sTitle" : "in chemico test method",
                 "iOrder": -20,
                 "inMatrix" : true
             },
             "citation": {
                 "bVisible": true,
                 "iOrder": -16
             }   
         },
         "conditions": {
             "c_endpoint": {
                 "iOrder": -19,
                 "sTitle" : "Endpoint",
                 "inMatrix" : true
             }
         },            
         "effects": {
             "endpoint": {
             	"sTitle" : "Parameter",
                 "bVisible": true,
                 "inMatrix" : true,
                 "iOrder": -18
             },
             "result": {
                 "bVisible": true,
                 "inMatrix" : true,
                 "iOrder": -17
             }       
         },
         "interpretation": {
             "result": {
                 "sTitle": "Pos/Neg",
                 "inMatrix" : false,
                 "iOrder": -4,
                 "bVisible": false
             },
             "criteria": {
                 "iOrder": -3,
                 "sTitle": "Potency",
                 "bVisible": false
             }
         }		
}

var config_ce0 = {
	    "parameters": {
            "type_of_study": {
                "iOrder": -11,
                "sTitle": "Study type",
                "bVisible": false
            },
            "type_of_method": {
                "iOrder": -10,
                "sTitle": "Method type",
                "bVisible": false
            },
            "data_gathering_instruments": {
                "iOrder": -9,
                "bVisible": false
            }                
        },
        "protocol": {
            "guideline": {
            	"sTitle" : "In vitro test method",
                "iOrder": -20,
                "inMatrix" : true,
                "bVisible": true
            },
            "citation": {
                "bVisible": true,
                "iOrder": -14
            }   
        },
        "conditions": {
            "target gene": {
            	"sTitle" : "Endpoint",
                "iOrder": -18,
                "inMatrix" : true,
                "bVisible": true
            },
            "cell line": {
                "iOrder": -19,
                "bVisible": true
            }
        },            
        "effects": {
            "endpoint": {
            	"sTitle" : "Parameter",
                "bVisible": true,
                "iOrder": -17,
                "inMatrix" : true
                
            },
            "result": {
                "bVisible": true,
                "iOrder": -16,
                "inMatrix" : true                  
            }                
        },
        "interpretation": {
            "result": {
                "sTitle": "Hazard prediction",
                "iOrder": -4,
                "inMatrix" : false,
                "bVisible": true,
                "iOrder": -15
            },
            "criteria": {
                "iOrder": -3,
                "bVisible": false
            }
        }
}

var config_ce1 = {
     	
   	 "parameters": {
            "p_vehicle": {
                "iOrder": -4,
                "sTitle": "Vehicle",
                "bVisible": true,
                "inMatrix" : false
            },
            "p_max dose tested": {
                "iOrder": -3,
                "sTitle": "Max Dose Tested (%)",
                "bVisible": true,
                "inMatrix" : false
            },
            "type of study": {
                "iOrder": -9,
                "sTitle": "Type of study",
                "bVisible": false,
                "inMatrix" : false
            }
        },
        "protocol": {
            "guideline": {
           	 "iOrder": -9,
                "sTitle": "Type of study",
                "inMatrix" : false,
                "bVisible": false
            },
            "uuid" : {
           	 "bVisible": false
            },
            "owner" : {
           	 "bVisible": false
            },
            "citation" : {
                "bVisible": true,
                "sTitle": "Reference",
                "mRender" : function(data,type,full) {
                	return data["title"];
                },             
            	"iOrder": -1,
            	"inMatrix" : true
            }                     
        },
        "conditions": {
            "vehicle": {
                "iOrder": -4,
                "sTitle": "Vehicle",
                "inMatrix" : false,
                "bVisible": false
            },
            "max dose tested": {
                "iOrder": -3,
                "sTitle": "Max Dose Tested (%)",
                "bVisible": false,
                "inMatrix" : true
            }
        },            
        "effects": {
            "endpoint": {
            	"sTitle" : "EC3",
                "bVisible": false,
                "inMatrix" : true,
                "iOrder": -8
            },
            
            "result": {
            	"sTitle" : "EC3",
                "bVisible": true,
                "inMatrix" : true,
                "iOrder": -7
            },
            "text": {
                "bVisible": true,
                "sTitle": "",
                "iOrder": -6                    
            }
        },
        "interpretation": {
            "result": {
                "sTitle": "Potency",
                "inMatrix" : false,
                "bVisible": true,
                "iOrder": -5
            },
            "criteria": {
                "iOrder": -3,
                "bVisible": false
            }
        }        
}

var config_ce2 = {
	 	 "parameters": {
             "species": {
                 "iOrder": -9,
                 "sTitle": "Species",
                 "bVisible": false,
                 "inMatrix" : false
             },
             "type of study": {
                 "iOrder": -9,
                 "sTitle": "Type of study",
                 "bVisible": false,
                 "inMatrix" : false
             }
         },
         "protocol": {
             "guideline": {
            	 "iOrder": -9,
                 "sTitle": "Guideline",
                 "inMatrix" : false,
                 "bVisible": false
             },
             "uuid" : {
            	 "bVisible": false
             },
             "owner" : {
            	 "bVisible": false
             },
             "citation" : {
                 "bVisible": true,
                 "sTitle": "Reference",
                 "mRender" : function(data,type,full) {
                 	return data["title"];
                 },             
             	"iOrder": -1,
             	"inMatrix" : true
             }                     
         },
         "conditions": {

         },            
         "effects": {
             "endpoint": {
             	"sTitle" : "NOEL",
                 "bVisible": false,
                 "inMatrix" : true,
                 "iOrder": -7
             },
             
             "result": {
             	"sTitle" : "NOEL",
                 "bVisible": true,
                 "inMatrix" : true,
                 "iOrder": -6
             },
             "text": {
                 "bVisible": true,
                 "sTitle": "",
                 "iOrder": -5                    
             }
         },
         "interpretation": {
             "result": {
             	"sTitle": "Potency class",
                 "inMatrix" : true,
                 "bVisible": true,
                 "iOrder": -8
             },
             "criteria": {
             	"sTitle": "",
                 "iOrder": -3,
                 "bVisible": true
             }
         }      
}