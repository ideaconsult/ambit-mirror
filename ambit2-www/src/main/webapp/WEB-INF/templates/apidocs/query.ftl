{
	<#include "/apidocs/version.ftl" >
    "produces": [
        "application/json",
        "text/n3",
        "application/rdf+xml",
        "text/uri-list",
        "text/html"
    ],		
    "resourcePath": "/query",
	"apis": [
     	{
            "path": "/query/compound/search/all",
            "operations": [
                {
                    "method": "GET",
                    "summary": "Exact compound search",
                    "notes": "Returns all models",
                    "type": "Search compound",
                    "nickname": "searchByIdentifier",
                    "authorizations": {},
                    "parameters": [
                  
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Compounds not found"
                        }
                    ]
                }
            ]
     	}	
    ],
	<#include "/apidocs/info.ftl" >  
}