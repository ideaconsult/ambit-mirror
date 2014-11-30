{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list",
        "application/x-javascript"
    ],		
    "resourcePath": "/feature",
	"apis": [
			{
			    "path": "/feature",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get a list of available feature definitions",
			            "notes": "Returns all features <a href='http://opentox.org/dev/apis/api-1.2/Feature' target='opentox'>OpenTox Feature API</a>",
			            "type": "Feature",
			            "nickname": "getFeatures",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "search",
							    "description": "Name (regexp search)",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false
							},		
							{
							    "name": "sameas",
							    "description": "Endpoint URI",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
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
			                    "code": 404,
			                    "message": "Feature not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "POST",
			            "summary": "Create a new feature.",
			            "notes": "Create a new feature. Returns feature URI. <a href='http://opentox.org/dev/apis/api-1.2/Feature' target='opentox'>OpenTox Feature API</a>",
			            "type": "String",
			            "nickname": "createFeature",
	                    "consumes": [
	     	                  "application/rdf+xml"
	     	            ],
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "body",
							    "description": "RDF/XML representation of the Feature",
							    "required": true,
							    "type": "string",
							    "paramType": "body",
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
			   	            			<#include "/apidocs/error_aa.ftl" >,
			   	            			<#include "/apidocs/error_500.ftl" >				                
			            ]
			        }			        
			    ]
			},
			{
			    "path": "/feature/{id}",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Get description of a specific feature definition",
			            "notes": "Returns a feature representation <a href='http://opentox.org/dev/apis/api-1.2/Feature' target='opentox'>OpenTox Feature API</a>",
			            "type": "Feature",
			            "nickname": "getFeatureByID",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "id",
							    "description": "Feature id",
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
			                    "message": "Invalid Feature identifier"
			                },			    			
			                {
			                    "code": 404,
			                    "message": "Feature not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >			                
			            ]
			        },
			        {
			            "method": "PUT",
			            "summary": "Update feature",
			            "notes": "Updates feature <a href='http://opentox.org/dev/apis/api-1.2/Feature' target='opentox'>OpenTox Feature API</a>",
			            "type": "Task",
			            "nickname": "updateFeature",
	                    "consumes": [
	     	                  "application/rdf+xml"
	     	            ],
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "id",
							    "description": "Feature id",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							},
							{
							    "name": "body",
							    "description": "RDF/XML representation of the Feature",
							    "required": true,
							    "type": "string",
							    "paramType": "body",
							    "allowMultiple": false
							}							
			            ],
			            "responseMessages": [
			         	                    <#include "/apidocs/error_task.ftl" >,	
			   	             			 {
			   	            			    "code": 404,
			   	            			    "message": "Feature not found"
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
			            "summary": "Delete feature",
			            "notes": "Delete feature <a href='http://opentox.org/dev/apis/api-1.2/Feature' target='opentox'>OpenTox Feature API</a>",
			            "type": "Task",
			            "nickname": "deleteFeature",
	                    "consumes": [
	                                 "application/x-www-form-urlencoded"
	     	            ],
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "id",
							    "description": "Feature id",
							    "required": true,
							    "type": "string",
							    "paramType": "path",
							    "allowMultiple": false
							}							
			            ],
			            "responseMessages": [
			         	                    <#include "/apidocs/error_task.ftl" >,	
			   	             			 {
			   	            			    "code": 404,
			   	            			    "message": "Feature not found"
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
			}			
    ],
    "models" : {
    	"Task" : <#include "/apidocs/json_schema_task.ftl" >,
		"Feature" : <#include "/apidocs/json_schema_feature.ftl" >    	
    },    
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}