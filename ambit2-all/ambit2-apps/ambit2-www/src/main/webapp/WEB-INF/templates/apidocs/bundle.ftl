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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
							   "name": "description",
							   "description": "Description (free text)",
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
							},
							{
								   "name": "bundle_uri",
								   "description": "URI of a bundle to create a copy of. If this field is used the rest are ignored",
								   "required": false,
								   "type": "String",
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
							   "name": "description",
							   "description": "Description (free text)",
							   "required": true,
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
							},
							{
								   "name": "status",
								   "description": "Published status",
								   "required": false,
								   "type": "String",
								   "paramType": "form",
								   "enum" : ["published","draft","archived"],
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
			     			},							            			                
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }			        
			    ]
			},

			{
			    "path": "/bundle/{idbundle}/version",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get aall versions of a bundle",
			            "notes": "Returns bundle representation (a dataset, composed of substances)",
			            "type": "DatasetMetadata",
			            "nickname": "getBundleVersionsByID",
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
			     			},							            			                
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Create bundle version",
			            "notes": "Create  bundle version",
			            "type": "Task",
			            "nickname": "createBundleVersion",
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
			     			},							            			                
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }		        
			    ]
			},
			{
			    "path": "/bundle/{idbundle}/summary",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get bundle summary",
			            "notes": "Number of structures, substances and endpoints",
			            "type": "Facet",
			            "nickname": "getBundleSummary",
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
			     			},							            			                
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }		        
			    ]
			},
			
			{
			    "path": "/bundle/{idbundle}/matrix/{matrixtype}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get substance matrix (dataset copy)",
			            "notes": "get substance dataset",
			            "type": "SubstanceDataset",
			            "nickname": "getBundleMatrix",
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
							    "name": "matrixtype",
							    "description": "Matrix type",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false,
					    		"defaultValue": "working",
							    "enum" : [
							         "working","final"
							    ]							    
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
			     			},							            			                
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "PUT",
			            "summary": "Import studies for this bundle",
			            "notes": "Import studies for this bundle. Supports <a href='https://github.com/ideaconsult/examples-ambit/blob/master/ambit-json-docs/bundle.md'>JSON format</a> Example JSON :<a href='https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-core/src/test/resources/ambit2/core/data/json/matrixupdate.json'>matrixupdate.json</a> and <a href='https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-core/src/test/resources/ambit2/core/data/json/study.json'>study.json</a>",
			            "type": "Task",
			            "nickname": "uploadBundleStudy",
		                "consumes": [
		                       "multipart/form-data",
		                       "application/json"
		                ],			            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
	           				{
							    "name": "id",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},			                           
							{
							    "name": "files[]",
							    "description": "Files to upload",
							    "required": true,
							    "type": "File",
							    "paramType": "form",
							    "allowMultiple": true
							},
		 					{
							    "name": "body",
							    "description": "JSON substances or studies to import",
							    "required": true,
							    "paramType": "body",
							    "type" : "Study",
							    "allowMultiple": false
							  },
           					{
							    "name": "matrixtype",
							    "description": "Matrix type",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false,
					    		"defaultValue": "working",
							    "enum" : [
							         "working"
							    ]							    
							}								  										
							    
			            ],
			            "responseMessages": [
			         	    <#include "/apidocs/error_task.ftl" >,	
			                {
			                  "code": 404,
			                   "message": "Model not found"
			                 },
			                {
			       	           "code": 400,
			       	           "message": "Bad request"
			       	        },	 		                    
			                <#include "/apidocs/error_aa.ftl" >,
			                <#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Create matrix from bundle",
			            "notes": "Copies studies defined by selected endpoints and substances into local bundle matrix",
			            "type": "Task",
			            "nickname": "createMatrixFromBundle",
		                "consumes": [
		                       "application/x-www-form-urlencoded"
		                ],			            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
	           				{
							    "name": "id",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
	           				{
							    "name": "matrixtype",
							    "description": "Matrix type",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false,
					    		"defaultValue": "working",
							    "enum" : [
							         "working","final"
							    ]							    
							},							
							{
							    "name": "deletematrix",
							    "description": "delete existing matrix content",						    
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue": "false",
							    "enum" : [
							         "true","false"
							    ]
							}	    							
			            ],
			            "responseMessages": [
			         	    <#include "/apidocs/error_task.ftl" >,	
			                {
			                  "code": 404,
			                   "message": "Model not found"
			                 },
			                {
			       	           "code": 400,
			       	           "message": "Bad request"
			       	        },	 		                    
			                <#include "/apidocs/error_aa.ftl" >,
			                <#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
					{
			            "method": "DELETE",
			            "summary": "Delete working matrix from bundle",
			            "notes": "Deletes study copied",
			            "type": "Task",
			            "nickname": "deleteMatrixFromBundle",
		                "consumes": [
		                       "application/x-www-form-urlencoded"
		                ],			            
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
	           				{
							    "name": "id",
							    "description": "Bundle identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
           					{
							    "name": "matrixtype",
							    "description": "Matrix type",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false,
					    		"defaultValue": "working",
							    "enum" : [
							         "working"
							    ]							    
							}								    							
			            ],
			            "responseMessages": [
			         	    <#include "/apidocs/error_task.ftl" >,	
			                {
			                  "code": 404,
			                   "message": "Model not found"
			                 },
			                {
			       	           "code": 400,
			       	           "message": "Bad request"
			       	        },	 		                    
			                <#include "/apidocs/error_aa.ftl" >,
			                <#include "/apidocs/error_500.ftl" >			                
			            ]
			        }				        		
			    ]
			},			
			{
			    "path": "/bundle/{idbundle}/matrix/deleted",
			    "operations": [
						{
				            "method": "PUT",
				            "summary": "Marks working matrix values as deleted",
				            "notes": "Marks working matrix values as deleted. Supports JSON format <a href='https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-core/src/test/resources/ambit2/core/data/json/effects_delete.json'>effects_delete.json</a>",
				            "type": "Task",
				            "nickname": "markDeletedValues",
			                "consumes": [
			                       "application/json"
			                ],			            
				            <#include "/apidocs/authz.ftl" >
				            "parameters": [
		           				{
								    "name": "id",
								    "description": "Bundle identifier",
								    "required": true,
								    "type": "string",
								    "paramType": "path",
								    "allowMultiple": false
										},
			 					{
								    "name": "body",
								    "description": "JSON effect records to mark as deleted",
								    "required": true,
								    "paramType": "body",
								    "type" : "EffectsToDelete",
								    "allowMultiple": false
								  }											
													
				            ],
				            "responseMessages": [
				         	    <#include "/apidocs/error_task.ftl" >,	
				                {
				                  "code": 404,
				                   "message": "Model not found"
				                 },
				                {
				       	           "code": 400,
				       	           "message": "Bad request"
				       	        },	 		                    
				                <#include "/apidocs/error_aa.ftl" >,
				                <#include "/apidocs/error_500.ftl" >			                
				            ]
				        }								
			               	                   
			     ]               
			},  			
			{
			    "path": "/bundle/{idbundle}/study",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get study summary per bundle",
			            "notes": "Get study summary per bundle",
			            "type": "Facet",
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
							{
							    "name": "enableFeatures",
							    "description": "Retreives tag and remarks as features",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "defaultValue" : "false",
							    "enum" : ["false","true"],
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
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
							},
							{
							    "name": "tag",
							    "description": "tag",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "defaultValue" :"target",
							    "enum" : ["target","source"]
							},		
							{
							    "name": "remarks",
							    "description": "remarks",
							    "required": false,
							    "type": "string",
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
			     			    "message": "Bad request"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Bundle not found"
			                },
			     			{
			     				"code": 415,
			     			    "message": "Media type not supported"
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
      "Facet"     : <#include "/apidocs/json_schema_facet.ftl" >,
      "StudySummaryFacet"     : <#include "/apidocs/json_schema_studysummaryfacet.ftl" >,
      "Task" : <#include "/apidocs/json_schema_task.ftl" >,
      "Feature" : <#include "/apidocs/json_schema_feature.ftl" >,
      "EffectsToDelete" : <#include "/apidocs/json_schema_effectstodelete.ftl" >
    },    
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}	