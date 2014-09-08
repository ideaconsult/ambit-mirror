{
	<#include "/apidocs/version.ftl" >
    "produces": [
                 "application/json",
                 "text/n3",
                 "application/rdf+xml",
                 "text/uri-list",
                 "text/html"
             ],		
    "resourcePath": "/model",
	"apis": [
     	{
            "path": "/model",
            "operations": [
                {
                    "method": "GET",
                    "summary": "List models",
                    "notes": "Returns all models",
                    "type": "Model",
                    "nickname": "getAllModels",
                    "authorizations": {},
                    "parameters": [
                    ],
                    "responseMessages": [
                        {
                            "code": 404,
                            "message": "Models not found"
                        }
                    ]
                }
            ]
     	}
    ],
	<#include "/apidocs/info.ftl" >  
}