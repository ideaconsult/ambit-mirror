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
    "resourcePath": "/dataset",
	"apis": [
			{
			    "path": "/dataset",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List datasets",
			            "notes": "Returns a list of datasets, according to the search criteria <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "DatasetMetadata",
			            "nickname": "getDatasets",
			            "authorizations": {},
			            "parameters": [
							{
							    "name": "search",
							    "description": "Dataset name (regexp)",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "^T"
							},			            
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			     			{
			     				"code": 200,
			     				 "message": "Dataset(s) found"
			     			},				                                 
			                {
			                    "code": 404,
			                    "message": "Datasets not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        }
			    ]
			},
			{
			    "path": "/dataset/{id}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a dataset",
			            "notes": "Returns dataset representation <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "Dataset",
			            "nickname": "getDatasetByID",
			            "authorizations": {},
			            "parameters": [
							{
							    "name": "id",
							    "description": "Dataset identifier",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},	  
							{
							    "name": "feature_uris[]",
							    "description": "Feature URIs",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true
							},		
							{
							    "name": "compound_uris[]",
							    "description": "Compound URI",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true
							},
							<#include "/apidocs/parameters_page.ftl" >									
			            ],
			            "responseMessages": [
			     			{
			    			 "code": 200,
			    			 "message": "Success"
			    			},				                                 
			     			{
			     				"code": 400,
			     			    "message": "Invalid dataset identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Dataset not found"
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