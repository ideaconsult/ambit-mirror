{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],	
    "resourcePath": "/myaccount",
    "apis": [
			{
			    "path": "/myaccount/users",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "Users query",
			            "notes": "Local users query",
			            "type": "User",
			            "nickname": "getUserByName",
			            <#include "/apidocs/authz.ftl" >
			            "parameters": [
							{
							    "name": "q",
							    "description": "User name (starts with)",
							    "required": true,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
							    "defaultValue": "g"
							},    			            
							{
							    "name": "bundle_uri",
							    "description": "Bundle URI",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false
							},			      
							{
							    "name": "mode",
							    "description": "Read or write mode",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false,
	                            "enum": [
	                                "R",
	                                "W"
	                            ]							    
							},									      
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			                {
			                    "code": 404,
			                    "message": "Users not found"
			                },
							<#include "/apidocs/error_aa.ftl" >,
							<#include "/apidocs/error_500.ftl" >												                
			            ]
			        },
			
			        {
	                    "method": "POST",
	                    "summary": "Configure access rights",
			            "notes": "Configures access rigths for a bundle (to be extended with other resources)",
	                    "type": "void",
	                    "nickname": "assignRignts",
	                    "consumes": [
	                        "application/x-www-form-urlencoded"
	                    ],
	                    <#include "/apidocs/authz.ftl" >
	                    "parameters": [
	                        {
	                            "name": "bundle_number",
	                            "description": "Bundle identifier (UUID)",
	                            "required": true,
	                            "type": "String",
	                            "paramType": "query",
	                            "allowMultiple": false
	                        },
	                        {
	                            "name": "canRead",
	                            "description": "List of users or groups to be granted read only access to the bundle",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": true
	                        },
                  			{
	                            "name": "canWrite",
	                            "description": "List of users or groups to be granted write access (POST,PUT) to the bundle",
	                            "required": false,
	                            "type": "string",
	                            "paramType": "form",
	                            "allowMultiple": true
	                        }	                                              
	                    ],
	                    "responseMessages": [

		                    <#include "/apidocs/error_task.ftl" >,	
	             			 {
	            			    "code": 404,
	            			    "message": "Users not found"
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
      "Task" : <#include "/apidocs/json_schema_task.ftl" >
    },     
	<#include "/apidocs/profile/${menu_profile}/info.ftl" >  
}