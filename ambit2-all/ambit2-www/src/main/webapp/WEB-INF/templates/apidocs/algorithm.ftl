{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list",
        "text/html"
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
			            "parameters": [],
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