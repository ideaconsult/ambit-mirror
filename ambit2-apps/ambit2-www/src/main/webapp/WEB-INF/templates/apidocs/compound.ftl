{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "image/png",
        "text/csv",
        "text/plain",
        "chemical/x-mdl-sdfile",
        "chemical/x-cml",
        "chemical/x-daylight-smiles",
        "chemical/x-inchi",
        "text/x-arff",
        "text/x-arff-3col",
        "text/uri-list"
    ],		
    "resourcePath": "/compound",
	"apis": [
	         {
	        	 "path": "/compound/{id}",
	        	 "operations": [
					{
					    "method": "GET",
					    "summary": "Get the representation of a compound",
					    "notes": "Get the representation of a compound <a href='http://opentox.org/dev/apis/api-1.2/Compound' target='opentox'>OpenTox Compound API</a>",
					    "type": "Compound",
					    "nickname": "getCompoundByID",
					    <#include "/apidocs/authz.ftl" >
					    "parameters": [
							{
							    "name": "id",
							    "description": "Compound id",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_mol.ftl" >
					    ],
					    "responseMessages": [
								{
							      "code": 200,
							      "message": "OK"
							},		
					        {
					            "code": 400,
					            "message": "Invalid Compound identifier"
					        },			    			
					        {
					            "code": 404,
					            "message": "Compound not found"
					        },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
					    ]
					}
	        	 ]
	         },
	         
	         {
	        	 "path": "/compound/{id}/feature",
	        	 "operations": [
					{
					    "method": "GET",
					    "summary": "Get available Features for a compound",
					    "notes": "Get available Features for a compound <a href='http://opentox.org/dev/apis/api-1.2/Compound' target='opentox'>OpenTox Compound API</a>",
					    "type": "Feature",
					    "nickname": "getCompoundFeatures",
					    <#include "/apidocs/authz.ftl" >
					    "parameters": [
							{
							    "name": "id",
							    "description": "Compound id",
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
					            "message": "Invalid Compound identifier"
					        },			    			
					        {
					            "code": 404,
					            "message": "Compound not found"
					        },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
					    ]
					}
	        	 ]
	         },
	         {
	        	 "path": "/compound/{id}/conformer",
	        	 "operations": [
					{
					    "method": "GET",
					    "summary": "Get available structures of a compound",
					    "notes": "Get available structures of a compound <a href='http://opentox.org/dev/apis/api-1.2/Compound' target='opentox'>OpenTox Compound API</a>",
					    "type": "Compound",
					    "nickname": "getCompoundByID",
					    <#include "/apidocs/authz.ftl" >
					    "parameters": [
							{
							    "name": "id",
							    "description": "Compound id",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_mol.ftl" >
					    ],
					    "responseMessages": [
								{
							      "code": 200,
							      "message": "OK"
							},		
					        {
					            "code": 400,
					            "message": "Invalid Compound identifier"
					        },			    			
					        {
					            "code": 404,
					            "message": "Compound not found"
					        },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
					    ]
					}
	        	 ]
	         },	         
	         {
	        	 "path": "/compound/{id}/conformer/{cid}",
	        	 "operations": [
					{
					    "method": "GET",
					    "summary": "Get the representation of a structure",
					    "notes": "Get the representation of a structure <a href='http://opentox.org/dev/apis/api-1.2/Compound' target='opentox'>OpenTox Compound API</a>",
					    "type": "Compound",
					    "nickname": "getCompoundByID",
					    <#include "/apidocs/authz.ftl" >
					    "parameters": [
							{
							    "name": "id",
							    "description": "Compound id",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "cid",
							    "description": "Conformer id",
							    "required": false,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							<#include "/apidocs/parameters_mol.ftl" >
					    ],
					    "responseMessages": [
								{
							      "code": 200,
							      "message": "OK"
							},		
					        {
					            "code": 400,
					            "message": "Invalid Compound identifier"
					        },			    			
					        {
					            "code": 404,
					            "message": "Compound not found"
					        },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
					    ]
					}
	        	 ]
	         },
	         {
	        	 "path": "/compound/{id}/conformer/{cid}/feature",
	        	 "operations": [
					{
					    "method": "GET",
					    "summary": "Get available Features for a compound",
					    "notes": "Get available Features for a compound <a href='http://opentox.org/dev/apis/api-1.2/Compound' target='opentox'>OpenTox Compound API</a>",
					    "type": "Feature",
					    "nickname": "getCompoundFeatures",
					    <#include "/apidocs/authz.ftl" >
					    "parameters": [
							{
							    "name": "id",
							    "description": "Compound id",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "cid",
							    "description": "Conformer id",
							    "required": false,
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
					            "message": "Invalid Compound identifier"
					        },			    			
					        {
					            "code": 404,
					            "message": "Compound not found"
					        },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
					    ]
					}
	        	 ]
	         }	         
    ],
    "models" : {
      "Task" : <#include "/apidocs/json_schema_task.ftl" >,
      "Feature" : <#include "/apidocs/json_schema_feature.ftl" >
    },     
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}