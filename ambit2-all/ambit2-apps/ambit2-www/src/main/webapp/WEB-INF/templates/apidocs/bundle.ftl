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
	<#include "/apidocs/info.ftl" >  
}