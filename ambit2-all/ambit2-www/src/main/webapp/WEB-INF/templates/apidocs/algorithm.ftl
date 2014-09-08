{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list"
    ],	
    "resourcePath": "/algorithm",
    "apis": [
			{
			    "path": "/algorithm",
			    "operations": [
			        {
			            "method": "GET",
			            "summary": "List algorithms",
			            "notes": "Returns all algorithms",
			            "type": "Algorithms",
			            "nickname": "getAllAlgorithms",
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
			                    "message": "Algorithms not found"
			                }
			            ]
			        }
			    ]
			}
    ],
	<#include "/apidocs/info.ftl" >  
}