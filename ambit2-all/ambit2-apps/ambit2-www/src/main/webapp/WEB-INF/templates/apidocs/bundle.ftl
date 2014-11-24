{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/csv",
        "text/plain",
        "chemical/x-mdl-sdfile",
        "chemical/x-cml",
        "chemical/x-daylight-smiles",
        "chemical/x-inchi",
        "text/x-arff",
        "text/x-arff-3col"
    ],		
    "resourcePath": "/bundle",
	"apis": [
		{
			    "path": "/bundle",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get all bundles",
			            "notes": "Returns bundle representation (a dataset, composed of substances)",
			            "type": "Bundle",
			            "nickname": "getBundles",
			            <#include "/apidocs/authz.ftl" >

			            "parameters": [
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Create bundle",
			            "notes": "Create an empty bundle",
			            "type": "Task",
			            "nickname": "createBundle",
		                "consumes": [
				                       "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >

			            "parameters": [
							{
							   "name": "title",
							   "description": "Bundle name",
							   "required": true,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},	
							{
							   "name": "source",
							   "description": "Bundle source",
							   "required": true,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},			
							{
							   "name": "url",
							   "description": "Bundle source URL",
							   "required": true,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},	
							{
							   "name": "license",
							   "description": "Bundle license",
							   "required": true,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},				
							{
							   "name": "rightsHolder",
							   "description": "Bundle rightsHolder",
							   "required": true,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},		
							{
							   "name": "maintainer",
							   "description": "Bundle maintainer",
							   "required": true,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},
							{
							   "name": "stars",
							   "description": "Star ratings",
							   "required": true,
							   "type": "int",
							   "paramType": "form",
							   "allowMultiple": false
							}								
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }			        
			    ]
			},
			{
			    "path": "/bundle/{idbundle}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a bundle",
			            "notes": "Returns bundle representation (a dataset, composed of substances)",
			            "type": "DatasetMetadata",
			            "nickname": "getBundleByID",
			            <#include "/apidocs/authz.ftl" >

			            "parameters": [
							{
							    "name": "id",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							}								
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "PUT",
			            "summary": "Update bundle",
			            "notes": "Update bundle",
			            "type": "Task",
			            "nickname": "updateBundle",
		                "consumes": [
				                       "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >

			            "parameters": [
			               {
							   "name": "idbundle",
							   "description": "Bundle identifier",
							   "required": true,
							   "type": "string",
							   "paramType": "path",
							   "allowMultiple": false
							},				                           
							{
							   "name": "title",
							   "description": "Bundle name",
							   "required": false,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},	
							{
							   "name": "license",
							   "description": "Bundle license",
							   "required": false,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},				
							{
							   "name": "rightsHolder",
							   "description": "Bundle rightsHolder",
							   "required": false,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},		
							{
							   "name": "maintainer",
							   "description": "Bundle maintainer",
							   "required": false,
							   "type": "String",
							   "paramType": "form",
							   "allowMultiple": false
							},
							{
							   "name": "stars",
							   "description": "Star ratings",
							   "required": false,
							   "type": "int",
							   "paramType": "form",
							   "allowMultiple": false
							}								
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "DELETE",
			            "summary": "Delete bundle",
			            "notes": "Delete bundle",
			            "type": "Task",
			            "nickname": "deleteBundle",
			            <#include "/apidocs/authz.ftl" >

			            "parameters": [
			               {
							   "name": "idbundle",
							   "description": "Bundle identifier",
							   "required": true,
							   "type": "string",
							   "paramType": "path",
							   "allowMultiple": false
							}							
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }			        
			    ]
			},
			{
			    "path": "/bundle/{idbundle}/metadata",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get metadata for a bundle",
			            "notes": "Representation of the bundle metadata in a supported MIME type <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "DatasetMetadata",
			            "nickname": "getBundleMetadataByID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							}
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }		        
			    ]
			},
			
			{
			    "path": "/bundle/{idbundle}/substance",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a list of all substances in a dataset",
			            "notes": "List of substance uris ",
			            "type": "Substance",
			            "nickname": "getBundleSubstances",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Add a substance to the bundle",
			            "notes": "Add a substance to the bundle",
			            "type": "Task",
			            "nickname": "addSubstanceToBundle",
		                "consumes": [
				                       "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "substance_uri",
							    "description": "Substance URI",
							    "defaultValue" : "${ambit_root}/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true
							}									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "PUT",
			            "summary": "Add or delete a substance to the bundle",
			            "notes": "Add or delete a substance to the bundle",
			            "type": "Task",
			            "nickname": "updateSubstanceInBundle",
		                "consumes": [
				               "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "substance_uri",
							    "description": "Substance URI",
							    "defaultValue" : "${ambit_root}/substance/IUC4-efdb21bb-e79f-3286-a988-b6f6944d3734",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": true
							},
							{
							    "name": "command",
							    "description": "Specify operation (add or delete)",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "enum" : ["add","delete"]
							}										
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }			        
			    ]
			},
			{
			    "path": "/bundle/{idbundle}/property",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get endpoints",
			            "notes": "List of property uris ",
			            "type": "Substance",
			            "nickname": "getBundleProperties",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        
			        {
			            "method": "POST",
			            "summary": "Add a property to the bundle",
			            "notes": "Add a property to the bundle",
			            "type": "Task",
			            "nickname": "addPropertyToBundle",
		                "consumes": [
				               "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "topcategory",
							    "description": "Top endpoint category",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue" : "ECOTOX",
							    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
							},
							{
							    "name": "endpointcategory",
							    "description": "Endpoint category",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue" :"EC_FISHTOX_SECTION",
							    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl">
							}				
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "PUT",
			            "summary": "Add / delete a property to the bundle",
			            "notes": "Add a property to the bundle",
			            "type": "Task",
			            "nickname": "updatePropertyInBundle",
		                "consumes": [
				               "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "topcategory",
							    "description": "Top endpoint category",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue" : "ECOTOX",
							    "enum" : ["P-CHEM","ENV FATE","ECOTOX","TOX"]
							},
							{
							    "name": "endpointcategory",
							    "description": "Endpoint category",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue" :"EC_FISHTOX_SECTION",
							    <#include "/apidocs/parameter_endpointcategorysection_enum.ftl">
							},
							{
							    "name": "command",
							    "description": "Specify operation (add or delete)",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "enum" : ["add","delete"]
							}							
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }				        
			    ]
			},
			{
			    "path": "/bundle/{idbundle}/dataset",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance dataset",
			            "notes": "get substance dataset",
			            "type": "SubstanceDataset",
			            "nickname": "getBundleDataset",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }		        
			    ]
			},
			{
			    "path": "/bundle/{idbundle}/studysummary",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get study summary per bundle",
			            "notes": "Get study summary per bundle",
			            "type": "StudySummaryFacet",
			            "nickname": "getBundleStudySummary",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }		        
			    ]
			},			
			{
			    "path": "/bundle/{idbundle}/compound",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get chemical structures per bundle",
			            "notes": "Get chemical structures per bundle",
			            "type": "Dataset",
			            "nickname": "getBundleCompounds",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid bundle identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Add a compound to the bundle",
			            "notes": "Add a compound to the bundle",
			            "type": "Task",
			            "nickname": "addPropertyToBundle",
		                "consumes": [
				               "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "compound_uri",
							    "description": "Compound URI",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "defaultValue" : "${ambit_root}/compound/1",
							    "allowMultiple": false
							}			
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
					{
			            "method": "PUT",
			            "summary": "Add or delete a compound to the bundle",
			            "notes": "Add or delete a compound to the bundle",
			            "type": "Task",
			            "nickname": "updateCompoundInBundle",
		                "consumes": [
				               "application/x-www-form-urlencoded"
		                ],					            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "idbundle",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "compound_uri",
							    "description": "Compound URI",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "defaultValue" : "${ambit_root}/compound/1",
							    "allowMultiple": false
							},
							{
							    "name": "command",
							    "description": "Specify operation (add or delete)",
							    "required": true,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "enum" : ["add","delete"]
							}										
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "OK"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }			        			        
			    ]
			}								
    ],
    "models" : {
      "DatasetMetadata" : <#include "/apidocs/json_schema_datasetmeta.ftl" >,   	
      "Substance" : <#include "/apidocs/json_schema_substance.ftl" >,   	  
      "Study"     : <#include "/apidocs/json_schema_study.ftl" >,   	  
      "Effect"     : <#include "/apidocs/json_schema_effect.ftl" >,
      "Result"     : <#include "/apidocs/json_schema_result.ftl" >,
      "StudySummaryFacet" : {
    		
      },
      "Task" : <#include "/apidocs/json_schema_task.ftl" >
    },    
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}	