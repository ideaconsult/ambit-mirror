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
			            "type": "Bundle",
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
			    "path": "/bundle/{id}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a bundle",
			            "notes": "Returns bundle representation (a dataset, composed of substances)",
			            "type": "Bundle",
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
			            "type": "Bundle",
			            "nickname": "updateBundle",
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
			            "type": "Bundle",
			            "nickname": "deleteBundle",
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
			        }			        
			    ]
			},
			{
			    "path": "/bundle/{id}/metadata",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get metadata for a bundle",
			            "notes": "Representation of the bundle metadata in a supported MIME type <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "BundleMetadata",
			            "nickname": "getBundleMetadataByID",
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
			        }		        
			    ]
			},
			
			{
			    "path": "/bundle/{id}/substance",
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
							    "name": "id",
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
			}				
    ],
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}	