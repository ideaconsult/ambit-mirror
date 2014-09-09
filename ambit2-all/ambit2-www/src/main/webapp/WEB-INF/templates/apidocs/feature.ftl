{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],		
    "resourcePath": "/feature",
	"apis": [
			{
			    "path": "/feature",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List features",
			            "notes": "Returns all features",
			            "type": "Feature",
			            "nickname": "getAllFeature",
			            "authorizations": {},
			            "parameters": [
							{
							    "name": "search",
							    "description": "Name",
							    "required": false,
							    "type": "string",
							    "paramType": "query",
							    "allowMultiple": false
							},			            
							<#include "/apidocs/parameters_page.ftl" >			            
			            ],
			            "responseMessages": [
			                {
			                    "code": 404,
			                    "message": "Feature not found"
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