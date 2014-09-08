{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list",
        "text/html"
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
			            "parameters": [],
			            "responseMessages": [
			                {
			                    "code": 404,
			                    "message": "Feature not found"
			                }
			            ]
			        }
			    ]
			}		
    ],
	<#include "/apidocs/info.ftl" >  
}