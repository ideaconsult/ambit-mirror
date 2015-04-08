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
			            <#include "/apidocs/authz.ftl" >
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
			     				 "message": "OK. Dataset(s) found"
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
			            <#include "/apidocs/authz.ftl" >
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
							    "description": "Feature URIs to use as columns",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": true
							},		
							{
							    "name": "sameas",
							    "description": "Ontology URI to define groups of columns",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "enum" : [
							    "",
							    "These are examples!",
							    "http://www.opentox.org/api/1.1#ChemicalName",
							    "http://www.opentox.org/api/1.1#CASRN",
							    "http://www.opentox.org/api/1.1#EINECS",
							    "http://www.opentox.org/echaEndpoints.owl#Carcinogenicity",
							    "http://www.opentox.org/echaEndpoints.owl#Mutagenicity",
							    "http://www.opentox.org/echaEndpoints.owl#Octanol-water_partition_coefficient_Kow"
							    ],
							    "allowMultiple": false
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
			    			 "message": "OK"
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
			},
			{
			    "path": "/dataset/{id}/metadata",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get metadata for a dataset",
			            "notes": "Representation of the dataset metadata in a supported MIME type <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "DatasetMetadata",
			            "nickname": "getDatasetMetadataByID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "id",
							    "description": "Dataset identifier",
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
			     			    "message": "Invalid dataset identifier"
			     			},						                                 
			                {
			                    "code": 404,
			                    "message": "Dataset not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "PUT",
			            "summary": "Update metadata for a dataset",
			            "notes": "Update metadata for a dataset <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "DatasetMetadata",
			            "nickname": "setDatasetMetadata",
	                    "consumes": [
	     	                "application/x-www-form-urlencoded",
	     	                "application/rdf+xml"
	     	            ],			            
	     	           <#include "/apidocs/authz.ftl" >
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
							    "name": "title",
							    "description": "Dataset name",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},
							{
							    "name": "licenseOptions",
							    "description": "Dataset license options",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false,
							    "enum" : [
							        "http://www.opendatacommons.org/licenses/pddl/",
							        "http://www.opendatacommons.org/licenses/by/",
							        "http://www.opendatacommons.org/licenses/odbl/",
							        "http://creativecommons.org/publicdomain/zero/1.0/",
							        "http://creativecommons.org/licenses/by-sa/3.0/",
							        "http://www.gnu.org/copyleft/fdl.html",
							        "http://opendatacommons.org/licenses/odbl/1.0/",
							        "http://opendatacommons.org/licenses/by/1.0/",
							        "Other",
							        "Unknown"
							    ]
							},
							{
							    "name": "license",
							    "description": "Dataset license (if Other option selected)",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							},
							{
							    "name": "rightsHolder",
							    "description": "Rights holder (typically an URI)",
							    "required": false,
							    "type": "string",
							    "paramType": "form",
							    "allowMultiple": false
							}
			            ],
			            "responseMessages": [
			                 <#include "/apidocs/error_task.ftl" >,	
			   	            {
			   	                "code": 404,
			   	            	"message": "Dataset not found"
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
			    "path": "/dataset/{id}/feature",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a list of all features in a dataset",
			            "notes": "List of feature uris <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "Feature",
			            "nickname": "getDatasetFeatures",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "id",
							    "description": "Dataset identifier",
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
			},
			{
			    "path": "/dataset/{id}/compound",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a list of all compounds in a dataset",
			            "notes": "List of compound uris <a href='http://opentox.org/dev/apis/api-1.2/Dataset' target='opentox'>OpenTox Dataset API</a>",
			            "type": "Dataset",
			            "nickname": "getDatasetCompounds",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "id",
							    "description": "Dataset identifier",
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
    "models" : {
      "DatasetMetadata" : <#include "/apidocs/json_schema_datasetmeta.ftl" >,   	  
      "Task" : <#include "/apidocs/json_schema_task.ftl" >
    },    
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}